package ac.technion.iem.ontobuilder.matching.algorithms.line1.pivot;

import java.util.ArrayList;
import java.util.HashSet;

import ac.technion.iem.ontobuilder.core.ontology.Term;

/**
 * <p>Title: interface PivotOperator</p>
 */
public interface PivotOperator
{
    public ArrayList<HashSet<Term>> performPivot(ArrayList<Term> ontologyTerms, Term t);
}
