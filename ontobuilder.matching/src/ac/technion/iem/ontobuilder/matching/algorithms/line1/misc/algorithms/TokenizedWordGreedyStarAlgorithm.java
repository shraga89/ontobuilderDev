package ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.algorithms;

import java.util.ArrayList;
import java.util.List;

import ac.technion.iem.ontobuilder.core.ontology.Term;
/**
 * @author Arik Senderovic, Sapir Golan
 *
 */
public class TokenizedWordGreedyStarAlgorithm implements TokenizedWordAlgorithm {

	@Override
	public List<String> tokenizeTerms(Term term) {
		
		List<String> result = new ArrayList<String>();
		if (term != null) {
			
			String fullTermName = term.getName();
			if (fullTermName != null) {
				fullTermName = fullTermName.replaceAll("[^a-zA-Z]", "");
			}
			boolean continueTokenizing = true;
			
			TokenizedWordGreedyAlgorithm greedyAlgorithm = new TokenizedWordGreedyAlgorithm();
			List<String> greedyResult = greedyAlgorithm.tokenizeTerms(term);
			while (continueTokenizing){
				
				result.addAll(greedyResult);
				for (String name : greedyResult) {
					if (fullTermName.startsWith(name)) {
						fullTermName = fullTermName.substring(name.length());
											}
					if (fullTermName.endsWith(name)) {
						fullTermName = fullTermName.substring(0,
								fullTermName.length() - name.length());
											}
				}
				term.setName(fullTermName);
				greedyResult = greedyAlgorithm.tokenizeTerms(term);
				if (greedyResult.isEmpty() == true){
					continueTokenizing=false;
				}
			}
		}
		return result;
	}
	
	
	@Override
	public TokenizedAlgorithmType getAlgorithmType() {
	
		return TokenizedAlgorithmType.greedystar;
	
	}
}
