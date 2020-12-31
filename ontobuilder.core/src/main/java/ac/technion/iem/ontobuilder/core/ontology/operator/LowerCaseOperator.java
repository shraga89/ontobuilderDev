package ac.technion.iem.ontobuilder.core.ontology.operator;

/**
 * <p>Title: LowerCaseOperator</p>
 * Implements {@link StringOperator}
 */
public class LowerCaseOperator implements StringOperator
{
    public String transformString(String text)
    {
        return text.toLowerCase();
    }
}
