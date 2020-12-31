package ac.technion.iem.ontobuilder.core.thesaurus.event;

import java.util.EventObject;

import ac.technion.iem.ontobuilder.core.thesaurus.ThesaurusWord;

/**
 * <p>
 * Title: ThesaurusModelEvent
 * </p>
 * Extends {@link EventObject}
 */
public class ThesaurusModelEvent extends EventObject
{
    private static final long serialVersionUID = -1283571537137419800L;

    protected ThesaurusWord word;
    protected ThesaurusWord synonym;
    protected ThesaurusWord homonym;
    protected String oldName;

    /**
     * Constructs a ThesaurusModelEvent
     * 
     * @param source the source
     * @param word a word
     * @param synonym a synonym
     * @param homonym a homonym
     */
    public ThesaurusModelEvent(Object source, ThesaurusWord word, ThesaurusWord synonym,
        ThesaurusWord homonym)
    {
        super(source);
        this.word = word;
        this.synonym = synonym;
        this.homonym = homonym;
        oldName = null;
    }

    /**
     * Constructs a ThesaurusModelEvent
     * 
     * @param source the source
     * @param word a word
     * @param oldName the old name
     */
    public ThesaurusModelEvent(Object source, ThesaurusWord word, String oldName)
    {
        this(source, word, null, null);
        this.oldName = oldName;
    }

    /**
     * Constructs a ThesaurusModelEvent
     * 
     * @param source the source
     * @param word a word
     */
    public ThesaurusModelEvent(Object source, ThesaurusWord word)
    {
        this(source, word, null);
    }

    /**
     * Get the ThesaurusWord
     * 
     * @return the word {@link ThesaurusWord}
     */
    public ThesaurusWord getWord()
    {
        return word;
    }

    /**
     * Get the new name
     * 
     * @return the name
     */
    public String getNewName()
    {
        return oldName;
    }

    /**
     * Get the old name
     * 
     * @return the name
     */
    public String getOldName()
    {
        return oldName;
    }

    /**
     * Get the new synonym
     * 
     * @return the synonym {@link ThesaurusWord}
     */
    public ThesaurusWord getSynonym()
    {
        return synonym;
    }

    /**
     * Get the new homonym
     * 
     * @return the homonym {@link ThesaurusWord}
     */
    public ThesaurusWord getHomonym()
    {
        return homonym;
    }
}