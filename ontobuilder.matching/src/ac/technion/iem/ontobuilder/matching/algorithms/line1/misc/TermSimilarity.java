package ac.technion.iem.ontobuilder.matching.algorithms.line1.misc;

import java.util.HashMap;
import java.util.Map;

import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.algorithms.TokenizedAlgorithmType;

/**
 * @author Sapir Golan
 *
 */
public class TermSimilarity {
	private HashMap<Term, Map<TokenizedAlgorithmType, Double>> similarityMap;
	
	/**
	 *Default Contractor 
	 */
	public TermSimilarity() {
		this.similarityMap =  new HashMap<Term, Map<TokenizedAlgorithmType, Double>>();
	}

	/**
	 * Adds similarity measurement to a Term
	 * @param term, {@link Term} that the similarity is calculated against
	 * @param algorithmType, {@link TokenizedAlgorithmType} that was used to token both Terms
	 * @param similarityValue, the value of the similarity
	 */
	public void addSimilarityByAlgorithm(Term targetTerm,
			TokenizedAlgorithmType algorithmType, Double similarityValue) {
		if (targetTerm != null && algorithmType != null) {
			//similarityMap contains a given term
			if (similarityMap.containsKey(targetTerm)) {
				Map<TokenizedAlgorithmType, Double> map = similarityMap.get(targetTerm);
				map.put(algorithmType, similarityValue);
			}
			//similarityMap doesn't contain a given term
			else {
				Map<TokenizedAlgorithmType, Double> map = new HashMap<TokenizedAlgorithmType, Double>();
				map.put(algorithmType, similarityValue);
				similarityMap.put(targetTerm, map);
			}
		}
	}
	
}
