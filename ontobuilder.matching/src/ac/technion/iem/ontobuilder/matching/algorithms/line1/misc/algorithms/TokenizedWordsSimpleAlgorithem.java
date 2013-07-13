package ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.algorithms;

import java.util.List;

import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.core.utils.StringUtilities;

/**
 * @author Sapir Golan
 *
 */
public class TokenizedWordsSimpleAlgorithem implements TokenizedWordAlgorithm {

	/**
	 * The method tokenized a {@link Term} by a using a <i>simple</i> algorithm<br>
	 * Each Term is tokenized into words based on camelCase and punctuation
	 * @param {@link Term}
	 * @return For each {@link Term} return List<String> of all the string it contains
	 */
	@Override
	public List<String> tokenizeTerms(Term term) {
		List<String> tokenizedWords = tokenizedWordsSimple(term.getName());
		return tokenizedWords;
	}
	
	/**
	 * Breaks up a given string to words by Capitalized letters
	 *  and separators (tab, space, hyphen, underscore)
	 * @param word
	 * @return List of strings representing distinct words found
	 */
	private List<String> tokenizedWordsSimple(String word)
	{
		String wordCamelCase = StringUtilities.separateCapitalizedWords(word);
		List<String> listOfWords = StringUtilities.breakTextIntoWords(wordCamelCase);
		if (listOfWords.isEmpty()) {
			System.err.println("No words were found in " + word);
		}
		return listOfWords;
	}

	@Override
	public TokenizedAlgorithmType getAlgorithmType() {
		return TokenizedAlgorithmType.simple;
	}

}
