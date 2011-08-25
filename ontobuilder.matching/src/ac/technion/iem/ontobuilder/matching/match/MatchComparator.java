package ac.technion.iem.ontobuilder.matching.match;

import ac.technion.iem.ontobuilder.core.ontology.Term;

/**
 * <p>Title: interface MatchComparator</p>
 */
public interface MatchComparator
{
    public boolean compare(Term t1, Term t2);

    public boolean implementsEffectiveness();

    public double getEffectiveness();

    public String getName();
}
