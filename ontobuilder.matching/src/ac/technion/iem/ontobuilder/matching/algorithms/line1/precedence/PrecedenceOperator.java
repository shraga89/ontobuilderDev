package ac.technion.iem.ontobuilder.matching.algorithms.line1.precedence;

import java.util.ArrayList;
import java.util.Iterator;

import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.pivot.PivotOperator;

/**
 * <p>Title: PrecedenceOperator</p>
 * Implements a {@link PivotOperator}
 */
public class PrecedenceOperator implements PivotOperator
{
    /**
     * Constructs a default PrecedenceOperator
     */
    public PrecedenceOperator()
    {
    }

    /**
     * Perform a pivot on an ontology terms list according to a specific term
     * 
     * @param ontologyTerms the ontology {@link Term} list
     * @param t the {@link Term}
     * @return a list of list of terms
     */
    public ArrayList<ArrayList<Term>> performPivot(ArrayList<Term> ontologyTerms, Term t)
    {// O(Terms)
        ArrayList<ArrayList<Term>> termSets = new ArrayList<ArrayList<Term>>();
        ArrayList<Term> precedeTerms = t.getAllPrecedes();
        precedeTerms = filter(precedeTerms);
        ArrayList<Term> succeedTerms = new ArrayList<Term>();
        for (Iterator<?> it = ontologyTerms.iterator(); it.hasNext();)
        {
            Term termToCheck = (Term) it.next();
            if (!precedeTerms.contains(termToCheck) && !termToCheck.equals(t))
            {
                succeedTerms.add(termToCheck);
            }
        }
        termSets.add(0, precedeTerms);
        termSets.add(1, succeedTerms);
        return termSets;
    }

    /**
     * Filter a terms list according to certain parameters (is instance of input, composition or
     * hidden)
     * 
     * @param terms the list of {@link Term}
     * @return a filtered list
     */
    public ArrayList<Term> filter(ArrayList<Term> terms)
    {
        ArrayList<Term> filteredTerms = new ArrayList<Term>();
        for (Iterator<Term> it = terms.iterator(); it.hasNext();)
        {
            Term termToCheck = (Term) it.next();
            if ((termToCheck.isInstanceOf("input") || termToCheck.isInstanceOf("composition")) &&
                !termToCheck.isInstanceOf("hidden"))
            {
                filteredTerms.add(termToCheck);
            }
        }
        return filteredTerms;
    }

}
