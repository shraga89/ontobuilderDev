package ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.algorithms;

import java.util.ArrayList;
import java.util.List;

import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.WordNetAlgorithm;
/**
 * @author Arik Senderovic, Sapir Golan
 *
 */
public class TokenizedWordGreedyStarAlgorithm implements TokenizedWordAlgorithm {

	@Override
	public List<String> tokenizeTerms(Term term) {
		
		List<String> result = new ArrayList<String>();
		tokenizeTermsRecursive(term, result);
		
		return result;
	}
	
	
	@Override
	public TokenizedAlgorithmType getAlgorithmType() {
	
		return TokenizedAlgorithmType.greedystar;
	
		}
	
	public void tokenizeTermsRecursive(Term term, List<String> result)
	{ 
		int arg_pref = 0;
		int arg_suf = 0;
		
		
		String termName = term.getName();
		//term's name is empty
		if (termName == null || termName == "") {
			return;
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
				if (WordNetAlgorithm.isWordInDiction(currentPrefix) == true)
						{
							biggestPrefix = currentPrefix;
							arg_pref = i;
						}
				if (WordNetAlgorithm.isWordInDiction(currentSuffix) == true)
				{
					biggestSuffix = currentSuffix;
					arg_suf =termLength-i;
				}
				
			
			}
			
			this.calcResult(biggestPrefix, biggestSuffix, result);
			
			Term midTerm = new Term();
			//List<String> midResult = new ArrayList<String>();
			if (arg_suf-arg_pref>2)
			{
				//We assume that significant word is of two letters - otherwise it doesn't
				//contribue to semantics.
				midTerm.setName(termName.substring(arg_pref,arg_suf));
				tokenizeTermsRecursive(midTerm, result);		
								
			} 
		}
		return;
	}
	private void calcResult(String prefix,String suffix, List<String> result) {
		//List<String> result = new ArrayList<String>();
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
		return;
	}
}
