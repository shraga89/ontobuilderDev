package ac.technion.iem.ontobuilder.core.ontology.operator;

import ac.technion.iem.ontobuilder.core.utils.StringUtilities;

/**
 * <p>Title: HyphenRemovalOperator</p>
 * Implements {@link StringOperator}
 */
public class HyphenRemovalOperator implements StringOperator
{
    protected boolean mergeWords = false;

    public HyphenRemovalOperator(boolean mergeWords)
    {
        this.mergeWords = mergeWords;
    }

    public String transformString(String text)
    {
        return StringUtilities.removeHyphen(text, mergeWords);
    }
}