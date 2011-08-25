package ac.technion.iem.ontobuilder.core.ontology.operator;

import ac.technion.iem.ontobuilder.core.utils.StringUtilities;

/**
 * <p>Title: WordSeparatorOperator</p>
 * Implements {@link StringOperator}
 */
public class WordSeparatorOperator implements StringOperator
{
    public String transformString(String text)
    {
        return StringUtilities.separateCapitalizedWords(text);
    }
}