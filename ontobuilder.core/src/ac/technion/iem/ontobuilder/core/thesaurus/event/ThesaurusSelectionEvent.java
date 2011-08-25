package ac.technion.iem.ontobuilder.core.thesaurus.event;

import java.util.EventObject;

/**
 * <p>Title: ThesaurusSelectionEvent</p>
 * Extends {@link EventObject} 
 */
public class ThesaurusSelectionEvent extends EventObject
{
    private static final long serialVersionUID = -2143058431833065538L;

    protected Object selectedWord;

    /**
     * Constructs a ThesaurusSelectionEvent
     *
     * @param source the source
     * @param selectedWord the selected word
     */
    public ThesaurusSelectionEvent(Object source, Object selectedWord)
    {
        super(source);
        this.selectedWord = selectedWord;
    }

    /**
     * Returns the selected word
     *
     * @return the selected word
     */
    public Object getSelectedWord()
    {
        return selectedWord;
    }
}
