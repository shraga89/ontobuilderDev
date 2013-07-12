package ac.technion.iem.ontobuilder.matching.algorithms.line1.misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.jdom2.Element;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.core.utils.StringUtilities;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.common.AbstractAlgorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.common.Algorithm;
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

	/**
	 * Converts term names to words, looks words up in wordnet and calculates similarity
	 * @param cands
	 * @param targs
	 * @return
	 */
	private void match(MatchInformation mi)
	{
		ArrayList<Term> cands = getTerms(mi.getCandidateOntology());
		ArrayList<Term> targs = getTerms(mi.getTargetOntology());

		HashMap<Term, List<String>> candsTokenizedSimple = tokenizedWordsSimpleAlgorithem(cands);
		HashMap<Term, List<String>> candsTokenizedGreedy = tokenizedWordsGreedyAlgorithem(cands);
		for (int i = 0; i < targs.size(); i++)
		{
			for (int j = 0; j < cands.size(); j++)
			{

				String candidateName = cands.get(j).getName();
				String targetName = targs.get(i).getName();
				//Extract words
				ArrayList<String> candidatesWordList = tokenizedWordsSimple(candidateName);
				ArrayList<String> targetWordList = tokenizedWordsSimple(targetName);
				double avgSim = 0.0;
				for (String candidateWord : candidatesWordList)
				{
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
				if (!candidatesWordList.isEmpty()) avgSim /= candidatesWordList.size();
				mi.updateMatch(targs.get(i), cands.get(j), avgSim);
			}
		}
		for (String word : unknownwords.keySet())
		{
			String cWord = unknownwords.get(word);
			System.err.println("No definitions were found for " + word + " which was cleaned to " + cWord);
		}
	}

	/**
	 * This algorithm handle <b>multi-words</b> names<br>
	 * The method tokenized is Term by a using a <i>greedy</i> algorithm<br>
	 * looks for the biggest prefixing and suffixing dictionary words.<br>
	 * @param cands
	 * @return
	 */
	private HashMap<Term, List<String>> tokenizedWordsGreedyAlgorithem(List<Term> cands) {
		
		HashMap<Term, List<String>> result = new HashMap<Term, List<String>>();
		for (Term term : cands) {
			String termName = term.getName();
			//term's name is empty
			if (termName == null || termName == "") {
				result.put(term, Arrays.asList(""));
			} else {
				//removing all non alphabetic characters from a String
				termName = termName.replaceAll("[^a-zA-Z]", "");
				int termLength = termName.length();
				String biggestPrefix = null;
				String biggestSuffix = null;
				for (int i = 1; i <= termLength; i++) {
					//if current prefix\suffix is a word in the English dictionary set it has the biggest prefix\suffix
					String currentPrefix = termName.substring(0, i);
					String currentSuffix = termName.substring(termLength-i,termLength);
					biggestPrefix = isWordInDiction(currentPrefix) ? currentPrefix : biggestPrefix;
					biggestSuffix = isWordInDiction(currentSuffix) ? currentSuffix : biggestSuffix;
				}
				//case not Suffix and Prefix were found
				if (biggestPrefix == null && biggestSuffix == null) {
					result.put(term, Arrays.asList(""));
				}
				//case found only Suffix
				else if (biggestPrefix == null && biggestSuffix != null) {
					result.put(term, Arrays.asList(biggestSuffix));
				}
				//case found only Prefix
				else if (biggestPrefix != null && biggestSuffix == null) {
					result.put(term, Arrays.asList(biggestPrefix));
				}
				//case prefix equals suffix (none of them can be null)
				else if ( biggestSuffix.equals(biggestPrefix) ) {
					result.put(term, Arrays.asList(biggestSuffix));
				}
			}
		}
		return result;
	}
	/**
	 * The method tokenized is Term by a using a <i>simple</i> algorithm<br>
	 *  Each Term is tokenized into words based on camelCase and punctuation
	 * @param cands - an ArrayList of terms
	 * @return HashMap<Term, ArrayList<String>>, for each {@link Term} an ArrayList of all the string it contains
	 */
	private HashMap<Term, List<String>> tokenizedWordsSimpleAlgorithem( List<Term> cands) {

		HashMap<Term, List<String>> result = new HashMap<Term, List<String>>();
		for (Term term : cands) {
			ArrayList<String> tokenizedWords = tokenizedWordsSimple(term.getName());
			result.put(term, tokenizedWords);
		}
		return result;
	}

	/**
	 * Cleans non-alphabetical symbols (street1->street, from: -> from)
	 * @TODO add option of removing stop words (of, you, your, etc.)
	 * @TODO add option to expand acronyms and shortened words (Num->Number, Dr. -> Doctor)
	 * @TODO check why plural is not recognized (quotes in spid 55)
	 * @param word to clean
	 * @return clean word
	 */
	private String cleanWord(String word)
	{
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
	private boolean isWordInDiction(String word) {
		Set<String> defs =  WS4J.findDefinitions(word, POS.n);
		defs.addAll(WS4J.findDefinitions(word, POS.v));
		defs.addAll(WS4J.findDefinitions(word, POS.a));
		defs.addAll(WS4J.findDefinitions(word, POS.r));
		if ( defs.isEmpty() ) {
			return false;
		}
		return true;
	}
	
	private void addToUnknownwords(String origWord, String cleanWord) {
		unknownwords.put(origWord,cleanWord);
	}

	@Override
	public Algorithm makeCopy() {
		WordNetAlgorithm algo = new WordNetAlgorithm();
		return algo ;
	}

	private ArrayList<Term> getTerms(Ontology o)
	{
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


