package ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.algorithms;

import java.util.List;

import ac.technion.iem.ontobuilder.core.ontology.Term;

/**
 * @author Sapir Golan
 *
 */
public interface TokenizedWordAlgorithm {

	/**
	 * @param {@linkplain Term} to be tokinized
	 * @return List<String> - each string is tokenized from the input {@linkplain Term}  
	 */
	public List<String> tokenizeTerms(Term term);
	
	/**
	 * @return the algorithm type based on {@link TokenizedAlgorithmType}
	 */
	public TokenizedAlgorithmType getAlgorithmType();
}
