package ac.technion.iem.ontobuilder.matching.algorithms.line1.misc;

import java.util.List;

import ac.technion.iem.ontobuilder.core.ontology.Term;

public interface TokenizedWordAlgorithem {

	/**
	 * @param {@linkplain Term} to be tokinized
	 * @return List<String> - each string is tokenized from the input {@linkplain Term}  
	 */
	public List<String> tokenizeTerms(Term term);
}
