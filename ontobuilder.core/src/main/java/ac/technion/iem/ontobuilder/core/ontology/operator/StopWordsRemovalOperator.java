package ac.technion.iem.ontobuilder.core.ontology.operator;

import java.util.ArrayList;

import ac.technion.iem.ontobuilder.core.utils.StringUtilities;

/**
 * <p>Title: StopWordsRemovalOperator</p>
 * Implements {@link StringOperator}
 */
public class StopWordsRemovalOperator implements StringOperator
{
    protected ArrayList<String> stopTerms;

    public StopWordsRemovalOperator(ArrayList<String> stopTerms)
    {
        this.stopTerms = stopTerms;
    }

    public String transformString(String text)
    {
        return StringUtilities.removeWords(text, stopTerms);
    }
}