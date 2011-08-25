package ac.technion.iem.ontobuilder.core.thesaurus;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Vector;


import ac.technion.iem.ontobuilder.core.thesaurus.event.ThesaurusModelEvent;
import ac.technion.iem.ontobuilder.core.thesaurus.event.ThesaurusModelListener;

/**
 * <p>
 * Title: ThesaurusWord
 * </p>
 * Implements {@link Comparator}
 */
public class ThesaurusWord implements Comparator<Object>
{
    protected String word;
    protected Vector<ThesaurusWord> homonyms;
    protected Vector<ThesaurusWord> synonyms;
    protected ArrayList<ThesaurusModelListener> modelListeners;

    /**
     * Constructs a ThesaurusWord
     * 
     * @param word the string of the word to hold
     */
    public ThesaurusWord(String word)
    {
        this.word = word;
        homonyms = new Vector<ThesaurusWord>();
        synonyms = new Vector<ThesaurusWord>();
        modelListeners = new ArrayList<ThesaurusModelListener>();
    }

    public void addThesaurusModelListener(ThesaurusModelListener l)
    {
        modelListeners.add(l);
    }

    public void removeThesaurusModelListener(ThesaurusModelListener l)
    {
        modelListeners.remove(l);
    }

    protected void fireWordRenamedEvent(String oldName)
    {
        for (Iterator<ThesaurusModelListener> i = modelListeners.iterator(); i.hasNext();)
        {
            ThesaurusModelListener l = i.next();
            l.wordRenamed(new ThesaurusModelEvent(this, this, oldName));
        }
    }

    public void setWord(String word)
    {
        String oldName = this.word;
        this.word = word;
        fireWordRenamedEvent(oldName);
    }

    /**
     * Get the word
     * 
     * @return the word
     */
    public String getWord()
    {
        return word;
    }

    /**
     * Compares two ThesaurusWords
     * 
     * @param o1 the first {@link ThesaurusWordGui}
     * @param o2 the first {@link ThesaurusWordGui}
     */
    @Override
    public int compare(Object o1, Object o2)
    {
        return (((ThesaurusWord) o1).getWord().compareTo(((ThesaurusWord) o2).getWord()));
    }

    /**
     * Checks if two {@link ThesaurusWordGui} are equal
     * 
     * @return true if they are equal
     */
    public boolean equals(Object o)
    {
        if (o instanceof ThesaurusWord)
            return word.equals(((ThesaurusWord) o).getWord());
        return false;
    }

    /**
     * Get all the synonyms of a word
     * 
     * @return a vector with all the synonyms
     */
    public Vector<ThesaurusWord> getSynonyms()
    {
        return synonyms;
    }

    /**
     * Get all the homonyms of a word
     * 
     * @return a vector with all the homonyms
     */
    public Vector<ThesaurusWord> getHomonyms()
    {
        return homonyms;
    }

    /**
     * Adds a synonym to the ThesaurusWord
     * 
     * @param synonym the synonym to add
     */
    public void addSynonym(ThesaurusWord synonym)
    {
        if (!synonyms.contains(synonym))
            synonyms.add(synonym);
        if (!synonym.synonyms.contains(this))
            synonym.synonyms.add(this);
    }

    /**
     * Delete a synonym of the ThesaurusWord
     * 
     * @param synonym the synonym to delete
     */
    public void deleteSynonym(ThesaurusWord synonym)
    {
        int index = synonyms.indexOf(synonym);
        if (index == -1)
            return;
        synonyms.remove(index);
        index = synonym.synonyms.indexOf(this);
        if (index == -1)
            return;
        synonym.synonyms.remove(index);
    }

    /**
     * Adds a homonym to the ThesaurusWord
     * 
     * @param homonym the homonym to add
     */
    public void addHomonym(ThesaurusWord homonym)
    {
        if (!homonyms.contains(homonym))
            homonyms.add(homonym);
        if (!homonym.homonyms.contains(this))
            homonym.homonyms.add(this);
    }

    /**
     * Delete a homonym of the ThesaurusWord
     * 
     * @param homonym the homonym to delete
     */
    public void deleteHomonym(ThesaurusWord homonym)
    {
        int index = homonyms.indexOf(homonym);
        if (index == -1)
            return;
        homonyms.remove(index);
        index = homonym.homonyms.indexOf(this);
        if (index == -1)
            return;
        homonym.homonyms.remove(index);
    }

    /**
     * Check is the word had a synonym
     * 
     * @param synonym the synonym to check
     * @return true if the synonym exists
     */
    public boolean isSynonym(ThesaurusWord synonym)
    {
        return synonyms.contains(synonym);
    }

    /**
     * Check is the word had a homonym
     * 
     * @param homonym the homonym to check
     * @return true if the homonym exists
     */
    public boolean isHomonym(ThesaurusWord homonym)
    {
        return homonyms.contains(homonym);
    }

    public String toString()
    {
        return word;
    }
}