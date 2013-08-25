package ac.technion.iem.ontobuilder.matching.algorithms.line1.misc;

import ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.algorithms.TokenizedAlgorithmType;

/**
 * This Enum is used is the process that decides which similarity value should be taken 
 * from a match between a candidate and a target.
 * If any Algorithm is selected, then the similarity value should be taken based on his match.
 * 
 * See {@link WordNetAlgorithm}
 * 
 * @author Sapir Golan
 *
 */
public enum WordNetStrategy {
	SMIPLE(TokenizedAlgorithmType.simple),
	GREEDY(TokenizedAlgorithmType.greedy),
	GREED_STAR(TokenizedAlgorithmType.greedystar),
	BEST(null);
	
	private TokenizedAlgorithmType algorithmType;
	
	private WordNetStrategy(TokenizedAlgorithmType algorithmType) {
		this.algorithmType = algorithmType;
	}

	public TokenizedAlgorithmType getAlgorithmType() {
		return algorithmType;
	}

}
