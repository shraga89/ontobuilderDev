package ac.technion.iem.ontobuilder.matching.algorithms.line1.misc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.algorithms.TokenizedAlgorithmType;

/**
 * @author Sapir Golan
 *
 */
public class TermTokenized {
	private List<TokenizedAlgorithmType> executedAlgorithms;
	private Map<TokenizedAlgorithmType, List<String>> tokenizedWords;
	
	/**
	 *Default Contractor 
	 */
	TermTokenized() {
		this.executedAlgorithms = new ArrayList<TokenizedAlgorithmType>();
		this.tokenizedWords = new HashMap<TokenizedAlgorithmType, List<String>>();
	}
	
	/**
	 * @return List containing all {@link TokenizedAlgorithmType} that have already been executed on this instance
	 */
	public List<TokenizedAlgorithmType> getDoneAlgorithms() {
		return executedAlgorithms;
	}
	
	/**
	 * Return a Map that for each TokenizedAlgorithm that has been already executed can
	 * Retrieves the tokens that were created by it. 
	 * @return Map<{@link TokenizedAlgorithmType}, List<{@link String}>><br>
	 * 
	 */
	public Map<TokenizedAlgorithmType, List<String>> getTokenizedWords() {
		return tokenizedWords;
	}
	
	/**
	 * Add a words that were generated by some {@link TokenizedAlgorithmType}
	 * @param algorithmType - The algorithm that was executed
	 * @param list - The words that algorithm have generated
	 */
	public void addTokenizedWords(TokenizedAlgorithmType algorithmType, List<String> list) {
		if ( algorithmType!= null && list!=null ) {
			this.addExecutedAlgorithm(algorithmType);
			tokenizedWords.put(algorithmType, list);
		}
	}
	
	private void addExecutedAlgorithm (TokenizedAlgorithmType algorithmType) {
		if ( algorithmType!=null ) {
			if (!executedAlgorithms.contains(algorithmType)) {
				executedAlgorithms.add(algorithmType);
			}
		}
	}
	
}
