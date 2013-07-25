/**
 * 
 */
package ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.algorithms;

import java.util.ArrayList;
import java.util.List;

import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.core.utils.StringUtilities;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.WordNetAlgorithm;

/**
 * @author Sapir Golan
 *
 */
public class TokenizedWordGreedyAlgorithm implements TokenizedWordAlgorithm {

	/**
	 * This algorithm handle <b>multi-words</b> names<br>
	 * The method tokenized is Term by a using a <i>greedy</i> algorithm<br>
	 * looks for the biggest prefixing and suffixing dictionary words.<br>
	 * @param {@linkplain Term}
	 * @return
	 */
	@Override
	public List<String> tokenizeTerms(Term term) {
		List<String> result = new ArrayList<String>();
		if (term == null) {
			return result;
		}
		String termName = term.getName();
		//term's name is empty
		if (termName != null && !termName.equals("") ) {
			//removing all non alphabetic characters from a String
			termName = termName.replaceAll("[^a-zA-Z]", "");
			int termLength = termName.length();
			String biggestPrefix = null;
			String biggestSuffix = null;
			for (int i = 1; i <= termLength; i++) {
				//if current prefix\suffix is a word in the English dictionary set it has the biggest prefix\suffix
				String currentPrefix = termName.substring(0, i);
				String currentSuffix = termName.substring(termLength-i,termLength);
				biggestPrefix = WordNetAlgorithm.isWordInDiction(currentPrefix) ? currentPrefix : biggestPrefix;
				biggestSuffix = WordNetAlgorithm.isWordInDiction(currentSuffix) ? currentSuffix : biggestSuffix;
				//biggestPrefix = StringUtilities.isPlural(currentPrefix) ? currentPrefix : biggestPrefix ;
				//biggestSuffix = StringUtilities.isPlural(currentSuffix) ? currentSuffix : biggestSuffix ;
			}
			result = this.calcResult(biggestPrefix, biggestSuffix);
		}
		return result;
	}


	
	/**
	 * This method return an List of unique strings based on suffix and prefix<br>
	 * It has the following logic:
	 * <ol>
	 * <li>don't return null values</li>
	 * <li>don't return the same value twice</li>
	 * </ol>
	 * @param prefix
	 * @param suffix
	 * @return List<String>
	 */
	private List<String> calcResult(String prefix,String suffix) {
		List<String> result = new ArrayList<String>();
		//case Suffix and Prefix were found
		if (prefix == null && suffix == null) {
			result.add("");
		}
		//case found only Suffix
		else if (prefix == null && suffix != null) {
			result.add(suffix);
		}
		//case found only Prefix
		else if (prefix != null && suffix == null) {
			result.add(prefix);
		}
		//case prefix equals suffix (none of them can be null)
		else if ( suffix.equals(prefix) ) {
			result.add(prefix);
		}
		//case found both prefix and suffix
		else {
			result.add(suffix);
			result.add(prefix);
		}
		return result;
	}

	@Override
	public TokenizedAlgorithmType getAlgorithmType() {
		return TokenizedAlgorithmType.greedy;
	}

}
