package ac.technion.iem.ontobuilder.matching.algorithms.line1.misc;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom2.Element;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.core.utils.JAWJAWWrapper;
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
	Map<String,Map<String,Double>> simCache = new HashMap<String,Map<String,Double>>();

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
	 * <p>Calcs similarity between two terms</p>
	 * <p>This is done by tokenizing (extracting) the term into words by using different algorithms,
	 *  and then calculating the similarity of those extracted words. Similarity value of two terms is 
	 *  chosen by {@link WordNetStrategy} </p> 
	 * @param mi MatchInformation of terms
	 * @return a MatchInformation with updated similarity for each two terms
	 */
	private void match(MatchInformation mi) {
		long start = System.currentTimeMillis();
		long tokenTime = 0;
		long abbrExpandTime = 0;
		long calcSimTime = 0;
		long calcSimStoreTime = 0;
		long updateMatchTime = 0;
		
		HashMap<Term, TermTokenized> candidateTermsTokenizedMap = new HashMap<Term, TermTokenized>();
		HashMap<Term, TermTokenized> targetTermsTokenizedMap = new HashMap<Term, TermTokenized>();
		HashMap<Term, TermSimilarity> termsSimilarityMap = new HashMap<Term, TermSimilarity>();

		ArrayList<Term> cands = getTerms(mi.getCandidateOntology());
		ArrayList<Term> targs = getTerms(mi.getTargetOntology());
		TokenizedWordAlgorithmFactory factory = new TokenizedWordAlgorithmFactory();

		ArrayList<TokenizedWordAlgorithm> tokenizedWordAlgorithms = factory.build();
		AbbrExpand ae = new AbbrExpand();
		
		long endSU = System.currentTimeMillis();
		Long endSUtime = endSU-start;
		System.err.println("Wordnet setup time:" + endSUtime);
		
		
		for (int i = 0; i < targs.size(); i++) {
			for (int j = 0; j < cands.size(); j++) {
				Term currentCandidate = cands.get(j);
				Term currentTarget = targs.get(i);
				//Extract words by all algorithms
				long startTK = System.currentTimeMillis();
				for (TokenizedWordAlgorithm tokenizedAlgorithm : tokenizedWordAlgorithms) {
					this.tokenizeTermByAlgorithm( candidateTermsTokenizedMap, currentCandidate, tokenizedAlgorithm );
					this.tokenizeTermByAlgorithm( targetTermsTokenizedMap, currentTarget, tokenizedAlgorithm );
				}
				TermTokenized candidateTokenized = candidateTermsTokenizedMap.get(currentCandidate);
				TermTokenized targetTokenized = targetTermsTokenizedMap.get(currentTarget);
				tokenTime+=(System.currentTimeMillis()-startTK);
				//calc similarity between candidate and target terms 
				List<TokenizedAlgorithmType> algorithms = candidateTokenized.getDoneAlgorithms();
				
				for (TokenizedAlgorithmType algorithmType : algorithms) {
					long startABR = System.currentTimeMillis();
					List<String> candidateTokens = candidateTokenized.getTokenizedWordsByAlgorithmAndToken(algorithmType);
					List<String> targetTokens = targetTokenized.getTokenizedWordsByAlgorithmAndToken(algorithmType);

					List<String> candTokenAbbred = new ArrayList<String>();
					List<String> targTokenAbbred = new ArrayList<String>();
					for (String candStr: candidateTokens) {
						candTokenAbbred.add(ae.expandAbbreviation(candStr));
					}
					for (String targStr: targetTokens) {
						targTokenAbbred.add(ae.expandAbbreviation(targStr));
					}
					abbrExpandTime+=(System.currentTimeMillis()-startABR);
					long startSim = System.currentTimeMillis();
					Double similarity = calcSimilarity(candTokenAbbred, targTokenAbbred);
					calcSimTime+=(System.currentTimeMillis()-startSim);
					long startSimStore = System.currentTimeMillis();
					//current candidate term already has an entry in termsSimilarityMap
					if (termsSimilarityMap.containsKey(currentCandidate)) {
						termsSimilarityMap.get(currentCandidate).addSimilarityByAlgorithm(currentTarget, algorithmType, similarity);
					}
					else {
						TermSimilarity termSimilarity = new TermSimilarity();
						termSimilarity.addSimilarityByAlgorithm(currentTarget, algorithmType, similarity);
						termsSimilarityMap.put(currentCandidate, termSimilarity);
					}
					calcSimStoreTime+=(System.currentTimeMillis()-startSimStore);
				}
				/*	ResultsWriter resultsWriter = new ResultsWriter();
				resultsWriter.writeResults(termsSimilarityMap);*/
				long startUpdate = System.currentTimeMillis();
				this.updateMatchSimilarity( termsSimilarityMap, currentCandidate, currentTarget, WordNetStrategy.BEST, mi);
				updateMatchTime+=(System.currentTimeMillis()-startUpdate);
			}
		}
		Long end = System.currentTimeMillis()-start;
		System.err.println("Wordnet tokenization:" + tokenTime);
		System.err.println("Wordnet abbreveation expansion:" + abbrExpandTime);
		System.err.println("Wordnet similarity calculation:" + calcSimTime);
		System.err.println("Wordnet similarity storage:" + calcSimStoreTime);
		System.err.println("Wordnet match update time:" + updateMatchTime);
		System.err.println("Wordnet total runtime:" + end);
		
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
		//choose maximum similarity
		else {
			final Collection<Double> similarities = termsSimilarityMap.get(candidate).getAllSimilaritiesByTerm(target);
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
			if (similarity>0.0)
				mi.updateMatch(target, candidate, similarity);
		}
	}

	/**<p>Calcs the similarity of two Lists of words based on Distance By JiangConrath. Only words that are in the
	 * English Diction are being handled, if a word isn't its similarity to other is 0.0; the total similarity
	 * is an average of all candidate words (just words that are in the diction)</p>
	 * 
	 * <p>Each word is transformed to its Singular form and then it's checked if in the diction</p>
	 * 
	 * <p> JiangConrath distance is calculated in {@link WS4J#calcDistanceByJiangConrath(String, String)}, by using
	 * the following formula: 
	 * <blockquote><code>dist<sub>JS</sub>(c<sub>1</sub>, c<sub>2</sub>) = 2 * log( p(lso(c<sub>1</sub>, c<sub>2</sub>)) ) - ( log(p(c<sub>1</sub>))+log(p(c<sub>2</sub>) ) ).</blockquote>
	 * Jiang-Conrath similarity: <i>Negative reciprocal distance</i>,
	 * <blockquote><code>sim<sub>JS</sub>(c<sub>1</sub>, c<sub>2</sub>) = -1/<code>dist<sub>JS</sub>(c<sub>1</sub>, c<sub>2</sub>)</blockquote>
	 * </p>
	 * 
	 * @param candidateWords List of words that represents the candidate term
	 * @param targetWords List of words that represents the target term
	 * @return a similarity measurement in the range [0.0,1.0]
	 * @see {@link WS4J#calcDistanceByJiangConrath(String, String)}
	 */
	private Double calcSimilarity(List<String> candidateWords, List<String> targetWords) {
		double avgSim = 0.0;
		int validCandidateWords = 0;
		for (String candidateWord : candidateWords) {
			Map<String,Double> candSimCache = (simCache.containsKey(candidateWord) ? simCache.get(candidateWord) : new HashMap<String,Double>());
			//get the Singularize form of a word so it will be found in the Diction
			String singCandidateWord = StringUtilities.getSingularize(candidateWord);
			if ( StringUtilities.isWordInDiction(singCandidateWord) ) {
				validCandidateWords++;
				double maxSim = 0.0;
				//for each word in the candidate list of words check similarity
				//against all words in target.
				//choose the biggest similarity
				for (String targetWord : targetWords) {
					double sim=0.0d;
					if (candSimCache.containsKey(targetWord))
						sim = candSimCache.get(targetWord);
					else
					{
						
						String singTargetWord = StringUtilities.getSingularize(targetWord);
						if ( StringUtilities.isWordInDiction(singTargetWord) ) {
							sim = WS4J.runJCN(singCandidateWord,singTargetWord);
						}
						candSimCache.put(targetWord, sim);
					}
					maxSim = Math.max(maxSim, sim);
					maxSim = Math.min(maxSim, 1.0);
				}
				simCache.put(candidateWord, candSimCache);
				avgSim += maxSim;
			}
		}
		return (validCandidateWords ==0 ? 0.0d : avgSim / validCandidateWords);
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

	/**
	 * Check if word exists in dictionary
	 * @param word to be checked
	 */
	public static boolean isWordInDiction(String word) {
		Set<String> defs = new HashSet<String>();
		POS[] partsOfSpeech = POS.values();
		for (int i = 0; i < partsOfSpeech.length; i++) {
			POS pos = partsOfSpeech[i];
			defs.addAll(JAWJAWWrapper.findDefinitions(word, pos));
		}
		if ( defs.isEmpty() ) {
			return false;
		}
		return true;
	}

	private ArrayList<Term> getTerms(Ontology o) {
		Vector<Term> terms = o.getTerms(true);
		ArrayList<Term> result = new ArrayList<Term>();
		for (int i = 0; i < terms.size(); i++)
		{
			result.add(i, terms.get(i).copy());
		}
		return result;
	}

	/* 
	 * Abbreviation expansion algorithm.
	 * based on the following paper: Abbreviation Expansion in Schema Matching and Web Integration, L. Ratinov, E. Gudes.
	 */
	private class AbbrExpand {

		// CONFIGURATION
		public String WORD_LIST_PATH = "data/alist.txt";
		private String[] wordList;
		private HashMap<String, String> abbrCache;

		public AbbrExpand() {
			this.wordList = loadFile(WORD_LIST_PATH);
			this.abbrCache = new HashMap<String, String>();
		}

		/* 
		 * Create Abbreviation rule 1 regular expression
		 */
		public Pattern createSubsetAbbr(String abbr) {
			String ptrn = "\\b";
			for (int i=0, n = abbr.length(); i < n; i++) {
				char c1 = Character.toLowerCase(abbr.charAt(i));
				if (!Character.isLetterOrDigit(c1)) continue;
				ptrn = ptrn.concat(c1 +"\\w*");
			}
			ptrn = ptrn.concat("\\b");
			try {
			Pattern pattern = Pattern.compile(ptrn);
			return pattern;
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			return null;
		}

		/* 
		 * Create Abbreviation rule 2 regular expression
		 */
		public Pattern createFirstPartLettersAbbr(String abbr) {
			String ptrn = "^";
			for (int i=0, n = abbr.length(); i < n; i++) {
				char c1 = Character.toLowerCase(abbr.charAt(i));
				if (!Character.isLetterOrDigit(c1)) continue;
				ptrn = ptrn.concat("[" + c1 +"]\\w*");
				if (i+1 < n) {
					ptrn = ptrn.concat("[\\s]+");
					ptrn = ptrn.concat("(\\w*\\s+)*");
				}
				else {
					ptrn = ptrn.concat("[\\s]*");
				}
			}
			ptrn = ptrn.concat("$");
			Pattern pattern = Pattern.compile(ptrn);
			return pattern;
		}

		/*
		 * Get all matching phrases from the corpus given a regex patten (could be rule 1 or 2)
		 * return all phrases that match the rule.
		 */
		public String[] getMatchingPhrases(Pattern pattern, boolean returnPhrase) {
			Map<String, String> resultArr = new HashMap<String,String>();
			Matcher matcher;
			for (String phrase: this.wordList) {
				matcher = pattern.matcher(phrase);
				while (matcher.find()) {
					if (!resultArr.containsKey(matcher.group())) {
						if (returnPhrase)
							resultArr.put(phrase, null);
						else
							resultArr.put(matcher.group(), null);
					}
				}
			}
			String[] result = new String[resultArr.keySet().size()];
			result = resultArr.keySet().toArray(result);
			return result;
		}

		/*
		 * Construct the pattern of a word
		 */
		public String[] getWordPattern(String abbr, String phrase) {
			ArrayList<String> patterns = new ArrayList<String>();
			abbr = abbr.toLowerCase();
			phrase = phrase.toLowerCase();
			getAllPatterns(patterns, abbr, phrase, "");

			String[] result = new String[patterns.size()];
			result = patterns.toArray(result);
			return result;
		}

		/*
		 * Construct all possible pattern of a word/phrase
		 */
		private void getAllPatterns(ArrayList<String> patterns, String abbr,
				String phrase, String pat) {
			if (abbr.length() == 0) {
				// found of abbrs, fill in the xxxes and add to array
				for (int i=0, n=phrase.length(); i<n; i++) {
					pat = pat.concat("x");
				}
				patterns.add(pat);
				return;
			}
			else if (abbr.length() !=0 && phrase.length() == 0) {
				// fail this try
				return;
			}

			if (abbr.charAt(0) == phrase.charAt(0)) {
				char c = abbr.charAt(0);
				String add_c; 
				if (c=='a' || c=='e' || c=='u' || c=='i' || c=='o') {
					add_c = "V";
				} else {
					add_c = "C";
				}
				getAllPatterns(patterns, abbr.substring(1), phrase.substring(1), pat.concat(add_c));
				getAllPatterns(patterns, abbr, phrase.substring(1), pat.concat("x"));
			} 
			else {
				if (phrase.charAt(0) == ' ')
					getAllPatterns(patterns, abbr, phrase.substring(1), pat.concat("_"));
				else 
					getAllPatterns(patterns, abbr, phrase.substring(1), pat.concat("x"));
			}
		}

		/* 
		 * Return the score of a word (as defined in Algorithm 1)
		 */
		private double scoreWord(String word) {
			double score = 0.0;
			for (int i=0, n = word.length(); i < n; i++) {
				char c = word.charAt(i);
				if (c == 'x') {
					score += -1.0 * Math.pow(0.5, i);
				}
				else if (c == 'V' || c == 'C') {
					score += Math.pow(0.5, i);
				}
			}
			return score;
		}

		/* 
		 * return the score of a given pattern
		 */
		public double scorePattern(String pattern) {
			double score = 0.0;
			String[] words = pattern.split("_");
			for (String word: words) {
				score += scoreWord(word);
			}
			return score;
		}

		/*
		 * Expand abbreviation (according to the algorithm in the paper)
		 */
		public String expandAbbreviation(String abbr) {

			// check if phrase exists for the given abbreviation
			if (this.abbrCache.containsKey(abbr))
				return abbrCache.get(abbr);

			Pattern p1;
			String[] phrases = {};
			p1 = this.createSubsetAbbr(abbr);
			phrases = concat(phrases, this.getMatchingPhrases(p1, false));
			p1 = this.createFirstPartLettersAbbr(abbr);
			phrases = concat(phrases, this.getMatchingPhrases(p1, true));

			double max_score = -9999.0;
			String best_guess = "";

			for (String phrase: phrases) {
				String[] patterns = this.getWordPattern(abbr, phrase);
				boolean valid = false;
				double c_score = -9999.0;
				for (String ptrn: patterns) {
					double score = scorePattern(ptrn);
					c_score = Math.max(c_score, score);
					if (score > 0.2) {
						valid = true;
					}
				}
				if (valid) {
					if (c_score > max_score) {
						max_score = c_score;
						best_guess = phrase;
					}
				}
			}
			String returned_guess;
			if (best_guess.length() < 1)
				returned_guess = abbr;
			else
				returned_guess = best_guess;
			this.abbrCache.put(abbr, returned_guess);
			return returned_guess;
		}

		/* 
		 * Concatenate two collections
		 */
		private <T> T[] concat(T[] first, T[] second) {
			T[] result = Arrays.copyOf(first, first.length + second.length);
			System.arraycopy(second, 0, result, first.length, second.length);
			return result;
		}

		/*
		 * load a file
		 */
		private String[] loadFile(String path) {
			InputStream stream = this.getClass().getClassLoader().getResourceAsStream(path);
			BufferedReader br = null;
			ArrayList<String> lines_arr = new ArrayList<>();
			assert stream != null;
			br = new BufferedReader(new InputStreamReader(stream));
			try {
				String line = br.readLine();
				while (line != null) {
					lines_arr.add(line.replace("\n", ""));
					line = br.readLine();
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			String[] result = new String[lines_arr.size()];
			result = lines_arr.toArray(result);
			return result;
		}

	}
}