package ac.technion.iem.ontobuilder.gui.tools.exactmapping;

import ac.technion.iem.ontobuilder.core.ontology.Term;

/**
 * <p>Title: ExactTableRowModel</p>
 */
public class ExactTableRowModel
{
    Term candTerm;
    Term targetTerm;
    Boolean selected;

    /**
     * Constructs a ExactTableRowModel
     *
     * @param candTerm the candidate {@link Term}
     * @param targetTerm the target {@link Term}
     * @param selected <code>true</code> if it is selected
     */
    public ExactTableRowModel(Term candTerm, Term targetTerm, Boolean selected)
    {
        this.candTerm = candTerm;
        this.targetTerm = targetTerm;
        this.selected = selected;
    }

    /**
     * Get the candidate term
     *
     * @return the candidate {@link Term}
     */
    public Term getCandTerm()
    {
        return candTerm;
    }

    /**
     * Get the target term
     *
     * @return the target {@link Term}
     */
    public Term getTargetTerm()
    {
        return targetTerm;
    }

    /**
     * Is the ExactTableRowModel selected
     *
     * @return true if is is selected
     */
    public Boolean getSelected()
    {
        return selected;
    }

    /**
     * Set if the <code>ExactTableRowModel</code> is selected
     *
     * @param selected is selected
     */
    public void setSelected(Boolean selected)
    {
        this.selected = selected;
    }
}
