package ac.technion.iem.ontobuilder.core.ontology;

/**
 * <p>Title: DummyTerm</p>
 * <p>Description: Implements a term, used in a match matrix</p>
 * Extends {@link Term}
 */
public class DummyTerm extends Term
{
    private static final long serialVersionUID = 6911515144973205186L;

    private String dummyTermName;

    public DummyTerm(String dummyTermName)
    {
        this.dummyTermName = dummyTermName;
    }

    public String toString()
    {
        return dummyTermName;
    }
}