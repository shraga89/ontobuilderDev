package ac.technion.iem.ontobuilder.matching.algorithms.line1.misc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.jdom2.Element;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.core.utils.StringUtilities;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.common.AbstractAlgorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.common.Algorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.algorithms.TokenizedAlgorithmType;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.algorithms.TokenizedWordAlgorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.algorithms.TokenizedWordAlgorithmFactory;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import edu.cmu.lti.jawjaw.pobj.POS;
import edu.cmu.lti.ws4j.WS4J;

public class WordNetAlgorithm extends AbstractAlgorithm {

	private HashMap<String,String> unknownwords = new HashMap<String,String>();
	private HashMap<String,String> knownWords = new HashMap<String,String>();

	public WordNetAlgorithm(){

	}
	@Override
	public String getName() {
		return "WordNet Algorithm";
	}

	@Override
	public String getDescription() {
		return "WordNet Algorithm using Jiang-Conrath distance";
	}

	@Override
	public MatchInformation match(Ontology target,
			Ontology candidate) {

		MatchInformation matchInformation = new MatchInformation(candidate,target);
		this.match(matchInformation);
		matchInformation.setAlgorithm(this);
		return matchInformation ;
	}

	@Override
	public void configure(Element element) {

	}
	
	@Override
	public Algorithm makeCopy() {
		WordNetAlgorithm algo = new WordNetAlgorithm();
		return algo ;
	}

	/**
	 * Converts term names to words, looks words up in wordnet and calculates similarity
	 * @param cands
	 * @param targs
	 * @return
	 */
	private void match(MatchInformation mi) {
		HashMap<Term, TermTokenized> candidateTermsTokenizedMap = new HashMap<Term, TermTokenized>();
		HashMap<Term, TermTokenized> targetTermsTokenizedMap = new HashMap<Term, TermTokenized>();
		HashMap<Term, TermSimilarity> termsSimilarityMap = new HashMap<Term, TermSimilarity>();
		
		ArrayList<Term> cands = getTerms(mi.getCandidateOntology());
		ArrayList<Term> targs = getTerms(mi.getTargetOntology());
		TokenizedWordAlgorithmFactory factory = new TokenizedWordAlgorithmFactory();
		
		ArrayList<TokenizedWordAlgorithm> tokenizedWordAlgorithms = factory.build();
		
		for (int i = 0; i < targs.size(); i++) {
			for (int j = 0; j < cands.size(); j++) {

				Term currentCandidate = cands.get(j);
				Term currentTarget = targs.get(i);
				//Extract words by all algorithms
				for (TokenizedWordAlgorithm tokenizedAlgorithm : tokenizedWordAlgorithms) {
					this.tokenizeTermByAlgorithm( candidateTermsTokenizedMap, currentCandidate, tokenizedAlgorithm );
					this.tokenizeTermByAlgorithm( targetTermsTokenizedMap, currentTarget, tokenizedAlgorithm );
				}
				TermTokenized candidateTokenized = candidateTermsTokenizedMap.get(currentCandidate);
				TermTokenized targetTokenized = targetTermsTokenizedMap.get(currentTarget);
				
				List<TokenizedAlgorithmType> algorithms = candidateTokenized.getDoneAlgorithms();
				for (TokenizedAlgorithmType algorithmType : algorithms) {
					List<String> candidateTokens = candidateTokenized.getTokenizedWordsByAlgorithmAndToken(algorithmType);
					List<String> targetTokens = targetTokenized.getTokenizedWordsByAlgorithmAndToken(algorithmType);
					Double similarity = calcSimilarity(candidateTokens, targetTokens);
					//current candidate term already has an entry in termsSimilarityMap
					if (termsSimilarityMap.containsKey(currentCandidate)) {
						termsSimilarityMap.get(currentCandidate).addSimilarityByAlgorithm(currentTarget, algorithmType, similarity);
					}
					else {
						TermSimilarity termSimilarity = new TermSimilarity();
						termSimilarity.addSimilarityByAlgorithm(currentTarget, algorithmType, similarity);
						termsSimilarityMap.put(currentCandidate, termSimilarity);
					}
					
				}
				this.updateMatchSimilarity( termsSimilarityMap, currentCandidate, currentTarget, WordNetStrategy.BEST, mi);
			}
		}
	}
	
	/**
	 * Update the match similarity based on a specific algorithm matching {@link WordNetStrategy} or by
	 * finding the biggest similarity 
	 * 
	 * @param termsSimilarityMap
	 * @param candidate
	 * @param target
	 * @param strategy
	 * @param mi
	 */
	private void updateMatchSimilarity( HashMap<Term, TermSimilarity> termsSimilarityMap,
			Term candidate, Term target, WordNetStrategy strategy, MatchInformation mi) {
		//choose similarity that was achieved by a specific Algorithm
		TokenizedAlgorithmType algorithmType = strategy.getAlgorithmType();
		if (algorithmType instanceof TokenizedAlgorithmType) {
			TermSimilarity termSimilarity = termsSimilarityMap.get(candidate);
			Double similarity = termSimilarity.getSimilarity(target, algorithmType);
			mi.updateMatch(target, candidate, similarity);
		}
		//choose maximum similarrity
		else {
			final Collection<Double> similarities = termsSimilarityMap.get(candidate).getAllSimilarities(target);
			Double similarity = (double) 0;
			Iterator<Double> iteratorOneTime = similarities.iterator();
			//set maximum to be the value of the first value in similarities Collection
			if (iteratorOneTime.hasNext()) {
				similarity = (Double)iteratorOneTime.next();
			}
			//find the maximum value
			for (Iterator<Double> iterator = similarities.iterator(); iterator
					.hasNext();) {
				Double someSimilarity = (Double) iterator.next();
				similarity = Math.max(similarity, someSimilarity);
			}
			mi.updateMatch(target, candidate, similarity);
		}
	}
	
	private Double calcSimilarity(List<String> candidateWords, List<String> targetWords) {
		double avgSim = 0.0;
		int validCandidateWords = 0;
		for (String candidateWord : candidateWords) {
			if ( isWordInDiction(candidateWord) ) {
				validCandidateWords++;
				double maxSim = 0.0;
				//for each word in the candidate list of words check similarity
				//against all works in target.
				//choose the biggest similarity
				for (String targetWord : targetWords) {
					if ( isWordInDiction(targetWord) ) {
						maxSim = Math.max(maxSim, WS4J.calcDistanceByJiangConrath(candidateWord,targetWord));
						maxSim = Math.min(maxSim, 1.0);
					}
				}
			avgSim += maxSim;
			}
		}
		if (validCandidateWords != 0) {
			avgSim /= validCandidateWords;
		}
		return avgSim;
	}
	/**
	 * The method add words that were tokenized from some {@link Term} by some {@link TokenizedWordAlgorithm} 
	 * to a map to of terms Tokenized
	 * @param termsTokenized, Map<{@link Term}, {@link TermTokenized}> that for each term contains its TermTokenized
	 * @param currentTerm, {@link Term} to be tokenized
	 * @param tokenizedAlgorithm, {@link TokenizedWordAlgorithm} to tokenize the term
	 */
	private void tokenizeTermByAlgorithm( Map<Term, TermTokenized> termsTokenized,
			Term currentTerm, TokenizedWordAlgorithm tokenizedAlgorithm) {
		//currentTerm already exists in map
		//get his TermTokenized and update it 
		if ( termsTokenized.containsKey(currentTerm) ) {
			TermTokenized termTokenized = termsTokenized.get(currentTerm);
			boolean wasAlgorithmExecuted = termTokenized.getDoneAlgorithms().contains(tokenizedAlgorithm.getAlgorithmType());
			if (!wasAlgorithmExecuted) {
				termTokenized.addTokenizedWords( tokenizedAlgorithm.getAlgorithmType(), tokenizedAlgorithm.tokenizeTerms(currentTerm) );
			}
		} //currentTerm is new
		//create a new TermTokenized for it
		else {
			TermTokenized termTokenized = new TermTokenized();
			termTokenized.addTokenizedWords( tokenizedAlgorithm.getAlgorithmType(), tokenizedAlgorithm.tokenizeTerms(currentTerm) );
			termsTokenized.put(currentTerm, termTokenized);
		}
	}
	
	//This is the original method
	//
	//
	/*private void match(MatchInformation mi)
	{
		ArrayList<Term> cands = getTerms(mi.getCandidateOntology());
		ArrayList<Term> targs = getTerms(mi.getTargetOntology());

		TokenizedWordsSimpleAlgorithm simpleAlgorithm = new TokenizedWordsSimpleAlgorithm();
		TokenizedWordGreedyAlgorithm greedyAlgorithm = new TokenizedWordGreedyAlgorithm();

		for (int i = 0; i < targs.size(); i++) {
			for (int j = 0; j < cands.size(); j++) {

				String candidateName = cands.get(j).getName();
				String targetName = targs.get(i).getName();
				//Extract words
				ArrayList<String> candidatesWordList = tokenizedWordsSimple(candidateName);
				ArrayList<String> targetWordList = tokenizedWordsSimple(targetName);
				double avgSim = 0.0;
				for (String candidateWord : candidatesWordList) {
					double maxSim = 0.0;
					String cleanCandWord = cleanWord(candidateWord);
					if ( isWordInDiction(cleanCandWord) ) {
						for (String tWord : targetWordList) {
							String cleanTargWord = cleanWord(tWord);
							if ( isWordInDiction(cleanTargWord) ) {
								maxSim = Math.max(maxSim, WS4J.calcDistanceByJiangConrath(cleanCandWord,tWord));
								maxSim = Math.min(maxSim, 1.0);
							}
							else {
								addToUnknownwords(tWord, cleanTargWord);
							}
						}
					}
					else {
						addToUnknownwords(candidateWord, cleanCandWord);
					}
					avgSim+=maxSim;
				}
				if (!candidatesWordList.isEmpty()) {
					avgSim /= candidatesWordList.size();
				}
				mi.updateMatch(targs.get(i), cands.get(j), avgSim);
			}
		}
		for (String word : unknownwords.keySet())
		{
			String cWord = unknownwords.get(word);
			System.err.println("No definitions were found for " + word + " which was cleaned to " + cWord);
		}
	}*/


	/**
	 * Cleans non-alphabetical symbols (street1->street, from: -> from)
	 * @TODO add option of removing stop words (of, you, your, etc.)
	 * @TODO add option to expand acronyms and shortened words (Num->Number, Dr. -> Doctor)
	 * @TODO check why plural is not recognized (quotes in spid 55)
	 * @param word to clean
	 * @return clean word
	 */
	private String cleanWord(String word) {
		if (knownWords.containsKey(word)) {
			return knownWords.get(word);
		}
		//Remove non alphabetical characters
		StringBuffer sb = new StringBuffer();
		for (char c : word.toCharArray())
			if (Character.isLetter(c)) {
				sb.append(c);
			}
		//Lower case
		String cWord = sb.toString().toLowerCase();

		//Handle english plural
		if ( !isWordInDiction(cWord) && cWord.endsWith("s") ) {
			if (cWord.endsWith("ies")) {
				String singularWord = cWord.substring(0, cWord.length()-3) + "y";
				if ( isWordInDiction(singularWord) ) {
					cWord = singularWord;
				}
			} else {	
				String singularWord = cWord.substring(0, cWord.length()-1);
				if ( isWordInDiction(singularWord) ) {
					cWord = singularWord;
				}
			}
		}
		knownWords.put(word, cWord);
		return cWord;
	}
	/**
	 * Check if word exists in dictionary
	 * @param word to be checked
	 */
	public static boolean isWordInDiction(String word) {
		Set<String> defs = new HashSet<String>();
		POS[] partsOfSpeech = POS.values();
		for (int i = 0; i < partsOfSpeech.length; i++) {
			POS pos = partsOfSpeech[i];
			defs.addAll(WS4J.findDefinitions(word, pos));
		}
		if ( defs.isEmpty() ) {
			return false;
		}
		return true;
	}
	
	private void addToUnknownwords(String origWord, String cleanWord) {
		unknownwords.put(origWord,cleanWord);
	}

	private ArrayList<Term> getTerms(Ontology o) {
		Vector<Term> terms = o.getTerms(true);
		ArrayList<Term> result = new ArrayList<Term>();
		for (int i = 0; i < terms.size(); i++)
		{
			result.add(i, terms.get(i));
		}
		return result;
	}

	/**
	 * Breaks up a given string to words by Capitalized letters
	 *  and separators (tab, space, hyphen, underscore)
	 * @param canidateName
	 * @return Arraylist of strings representing distinct words found
	 */
	private ArrayList<String> tokenizedWordsSimple(String canidateName)
	{
		String canidateCamelCase = StringUtilities.separateCapitalizedWords(canidateName);
		ArrayList<String> canidateWordList = StringUtilities.breakTextIntoWords(canidateCamelCase);
		if (canidateWordList.isEmpty()) {
			System.err.println("No words were found in " + canidateName);
		}
		return canidateWordList;
	}
}