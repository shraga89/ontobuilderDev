package ac.technion.iem.ontobuilder.matching.algorithms.line1.ted;

import java.util.List;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.Term;

/**
 * An implementation of the TreeWrapper class with <code>Term</code>s as the tree leaves (the generic variable).
 * 
 * @author Admin
 *
 */
public class OntologyTreeWrapper implements TreeWrapper<Term>{

	private Ontology o;
	
	public OntologyTreeWrapper(Ontology o){
		this.o = o;
	}
	
	@Override
	public Term getRoot() {
		List<Term> children = o.getTerms(false);
		if(children.size() != 1){
			throw new RuntimeException("Ontology not a tree");
		}
		else{
			return children.get(0);
		}
	}

	@Override
	public List<Term> getChildren(Term node) {
		return node.getTerms();
	}

	@Override
	public List<Term> getAllChildren(Term node) {
		return node.getAllChildren();
	}

	@Override
	public boolean isLeaf(Term node) {
		return (node.getTerms().isEmpty());
	}

	@Override
	public boolean isFirst(Term node) {
		return (node.getPrecede() == null);
	}

	@Override
	public Term getParent(Term node) {
		return node.getParent();
	}

	@Override
	public Term getRightBrother(Term node) {
		return node.getSucceed();
	}

}
