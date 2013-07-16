package ac.technion.iem.ontobuilder.matching.algorithms.line1.misc;

import java.util.Collection;
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
	
	/**
	 * Return a similarity value based on {@link Term} it was calculated against and
	 * by the {@link TokenizedAlgorithmType} used
	 * 
	 * @param target
	 * @param algorithmType
	 * @return
	 */
	public Double getSimilarity(Term target,TokenizedAlgorithmType algorithmType){
		Double result = null;
		Map<TokenizedAlgorithmType, Double> map = similarityMap.get(target);
		if (map != null) {
			result = map.get(algorithmType);
		}
		return result;
	}
	
	/**
	 * Return all Similarities values based on {@link Term} they were calculated against.
	 * The returning value is <b>final</b> 
	 * 
	 * @param target
	 * @return
	 */
	public final Collection<Double> getAllSimilarities(Term target) {
		Collection<Double> result = null;
		Map<TokenizedAlgorithmType, Double> map = similarityMap.get(target);
		if (map != null) {
			result = map.values();
		}
		return result;
	}
	
}
