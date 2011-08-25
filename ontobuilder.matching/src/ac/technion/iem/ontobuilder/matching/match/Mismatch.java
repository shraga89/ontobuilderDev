package ac.technion.iem.ontobuilder.matching.match;

import ac.technion.iem.ontobuilder.core.ontology.Term;

/**
 * <p>Title: Mismatch</p>
 */
public class Mismatch
{
    protected Term term;
    protected boolean selected;

    /**
     * Constructs a Mismatch
     *
     * @param term the {@link Term}
     */
    public Mismatch(Term term)
    {
        this.term = term;
    }

    /**
     * Set the term
     * 
     * @param term the {@link Term}
     */
    public void setTerm(Term term)
    {
        this.term = term;
    }

    /**
     * Get the term
     * 
     * @return the {@link Term}
     */
    public Term getTerm()
    {
        return term;
    }

    /**
     * Set to selected
     *
     * @param b <code>true</code> if is selected
     */
    public void setSelected(boolean b)
    {
        selected = b;
    }

    /**
     * Check if selected
     *
     * @return <code>true</code> if is selected
     */
    public boolean isSelected()
    {
        return selected;
    }

    public String toString()
    {
        return term.getName() + " (" + term.getAttributeValue("name") + ")";
    }

    public boolean equals(Object o)
    {
        if (!(o instanceof Mismatch))
            return false;

        Mismatch m = (Mismatch) o;
        return term.equals(m.term);
    }
}