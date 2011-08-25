package ac.technion.iem.ontobuilder.core.thesaurus;

import java.util.List;
import java.util.Vector;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.*;
import org.jdom.JDOMException;
import org.jdom.Element;
import org.jdom.Document;
import org.jdom.DocType;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import ac.technion.iem.ontobuilder.core.thesaurus.event.ThesaurusModelAdapter;
import ac.technion.iem.ontobuilder.core.thesaurus.event.ThesaurusModelEvent;
import ac.technion.iem.ontobuilder.core.thesaurus.event.ThesaurusModelListener;
import ac.technion.iem.ontobuilder.core.utils.StringUtilities;
import ac.technion.iem.ontobuilder.core.utils.network.NetworkEntityResolver;
import ac.technion.iem.ontobuilder.core.utils.properties.PropertiesHandler;

/**
 * <p>Title: ThesaurusModel</p>
 * Extends {@link ThesaurusModelAdapter} 
 */
public class Thesaurus extends ThesaurusModelAdapter
{
    protected Vector<ThesaurusWord> words;
    protected ArrayList<ThesaurusModelListener> listeners;

    /**
     * Constructs a default ThesaurusModel
     */
    public Thesaurus()
    {
        listeners = new ArrayList<ThesaurusModelListener>();
    }

    /**
     * Constructs a ThesaurusModel
     * 
     * @param file the name of the file to load the Thesaurus from
     * @throws ThesaurusException
     */
    public Thesaurus(String file) throws ThesaurusException
    {
        this();
        loadThesaurus(file);
    }

    /**
     * Constructs a ThesaurusModel
     * 
     * @param file the file to load the Thesaurus from
     * @throws ThesaurusException
     */
    public Thesaurus(File file) throws ThesaurusException
    {
        this();
        loadThesaurus(file);
    }

    /**
     * Executes a WordRenamedEvent
     * 
     * @param e ThesaurusModelEvent
     */
    public void wordRenamed(ThesaurusModelEvent e)
    {
        fireWordRenamedEvent(e.getWord(), e.getOldName());
    }

    public void addThesaurusModelListener(ThesaurusModelListener l)
    {
        listeners.add(l);
    }

    public void removeThesaurusModelListener(ThesaurusModelListener l)
    {
        listeners.remove(l);
    }

    protected void fireWordChangedEvent(ThesaurusWord word)
    {
        for (Iterator<ThesaurusModelListener> i = listeners.iterator(); i.hasNext();)
        {
            ThesaurusModelListener l = i.next();
            l.wordChanged(new ThesaurusModelEvent(this, word));
        }
    }

    protected void fireWordRenamedEvent(ThesaurusWord word, String oldName)
    {
        for (Iterator<ThesaurusModelListener> i = listeners.iterator(); i.hasNext();)
        {
            ThesaurusModelListener l = i.next();
            l.wordRenamed(new ThesaurusModelEvent(this, word, oldName));
        }
    }

    protected void fireWordAddedEvent(ThesaurusWord word)
    {
        for (Iterator<ThesaurusModelListener> i = listeners.iterator(); i.hasNext();)
        {
            ThesaurusModelListener l = i.next();
            l.wordAdded(new ThesaurusModelEvent(this, word, null, null));
        }
    }

    protected void fireWordDeletedEvent(ThesaurusWord word)
    {
        for (Iterator<ThesaurusModelListener> i = listeners.iterator(); i.hasNext();)
        {
            ThesaurusModelListener l = i.next();
            l.wordDeleted(new ThesaurusModelEvent(this, word, null, null));
        }
    }

    protected void fireSynonymAddedEvent(ThesaurusWord word, ThesaurusWord synonym)
    {
        for (Iterator<ThesaurusModelListener> i = listeners.iterator(); i.hasNext();)
        {
            ThesaurusModelListener l = i.next();
            l.synonymAdded(new ThesaurusModelEvent(this, word, synonym, null));
        }
    }

    protected void fireSynonymDeletedEvent(ThesaurusWord word, ThesaurusWord synonym)
    {
        for (Iterator<ThesaurusModelListener> i = listeners.iterator(); i.hasNext();)
        {
            ThesaurusModelListener l = i.next();
            l.synonymDeleted(new ThesaurusModelEvent(this, word, synonym, null));
        }
    }

    protected void fireHomonymAddedEvent(ThesaurusWord word, ThesaurusWord homonym)
    {
        for (Iterator<ThesaurusModelListener> i = listeners.iterator(); i.hasNext();)
        {
            ThesaurusModelListener l = i.next();
            l.homonymAdded(new ThesaurusModelEvent(this, word, null, homonym));
        }
    }

    protected void fireUpdateEvent()
    {
        for (Iterator<ThesaurusModelListener> i = listeners.iterator(); i.hasNext();)
        {
            ThesaurusModelListener l = i.next();
            l.update(new ThesaurusModelEvent(this, null, null, null));
        }
    }

    protected void fireHomonymDeletedEvent(ThesaurusWord word, ThesaurusWord homonym)
    {
        for (Iterator<ThesaurusModelListener> i = listeners.iterator(); i.hasNext();)
        {
            ThesaurusModelListener l = i.next();
            l.homonymDeleted(new ThesaurusModelEvent(this, word, null, homonym));
        }
    }

    /**
     * Load the Thesaurus from a file
     * 
     * @param file the name of the file to load from
     * @throws ThesaurusException
     */
    public void loadThesaurus(String file) throws ThesaurusException
    {
        InputStream stream = getClass().getResourceAsStream(file);
        if (stream == null)
        {
            String params[] =
            {
                file
            };
            throw new ThesaurusException(StringUtilities.getReplacedString(
                PropertiesHandler.getResourceString("error.thesaurus.file"), params));
        }
        try
        {
            SAXBuilder builder = new SAXBuilder(true);
            builder.setEntityResolver(new NetworkEntityResolver());
            Document doc = builder.build(stream);
            loadFromDocument(doc);
        }
        catch (JDOMException e)
        {
            throw new ThesaurusException(e.getMessage());
        }
    }

    /**
     * Load the Thesaurus from a file
     * 
     * @param file the file to load from
     * @throws ThesaurusException
     */
    public void loadThesaurus(File file) throws ThesaurusException
    {
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            SAXBuilder builder = new SAXBuilder(true);
            builder.setEntityResolver(new NetworkEntityResolver());
            Document doc = builder.build(reader);
            loadFromDocument(doc);
        }
        catch (FileNotFoundException e)
        {
            throw new ThesaurusException(e.getMessage());
        }
        catch (JDOMException e)
        {
            throw new ThesaurusException(e.getMessage());
        }
    }

    /**
     * Load the Thesaurus from a document
     * 
     * @param doc the document to load from
     */
    protected void loadFromDocument(Document doc)
    {
        words = new Vector<ThesaurusWord>();
        Element thesaurus = doc.getRootElement();
        List<?> entries = thesaurus.getChildren("entry");
        for (Iterator<?> i = entries.iterator(); i.hasNext();)
        {
            Element entry = (Element) i.next();
            String word = entry.getChild("word").getText();
            addWord(word);
            List<?> synonyms = entry.getChild("synonyms").getChildren("synonym");
            for (Iterator<?> s = synonyms.iterator(); s.hasNext();)
                addSynonym(word, ((Element) s.next()).getText());
            List<?> homonyms = entry.getChild("homonyms").getChildren("homonym");
            for (Iterator<?> h = homonyms.iterator(); h.hasNext();)
                addHomonym(word, ((Element) h.next()).getText());
        }
        fireUpdateEvent();
    }

    /**
     * Saves the Thesaurus into a file
     * 
     * @param file the name of the file to save to
     * @throws ThesaurusException
     */
    public void saveThesaurus(String file) throws ThesaurusException
    {
        try
        {
            BufferedWriter out = new BufferedWriter(new FileWriter(file));

            Element root = new Element("thesaurus");
            Document doc = new Document(root);

            DocType thDocType = new DocType("thesaurus", "dtds/thesaurus.dtd");
            doc.setDocType(thDocType);

            for (Iterator<ThesaurusWord> i = words.iterator(); i.hasNext();)
            {
                ThesaurusWord word = i.next();
                Element eEntry = new Element("entry");
                root.addContent(eEntry);

                // Word
                Element eWord = new Element("word").setText(word.getWord());
                eEntry.addContent(eWord);

                // Synonyms
                Element eSynonyms = new Element("synonyms");
                eEntry.addContent(eSynonyms);
                Vector<?> synonyms = word.getSynonyms();
                for (Iterator<?> j = synonyms.iterator(); j.hasNext();)
                    eSynonyms.addContent(new Element("synonym").setText(((ThesaurusWord) j.next())
                        .getWord()));

                // Homonyms
                Element eHomonyms = new Element("homonyms");
                eEntry.addContent(eHomonyms);
                Vector<?> homonyms = word.getHomonyms();
                for (Iterator<?> j = homonyms.iterator(); j.hasNext();)
                    eHomonyms.addContent(new Element("homonym").setText(((ThesaurusWord) j.next())
                        .getWord()));
            }
            XMLOutputter fmt = new XMLOutputter("    ", true);
            fmt.output(doc, out);

            out.close();
        }
        catch (IOException e)
        {
            throw new ThesaurusException(e.getMessage());
        }
    }

    /**
     * Rename a word
     * 
     * @param word the original word
     * @param newName the new word
     */
    public void renameWord(String word, String newName)
    {
        ThesaurusWord thWord = getWord(word);
        if (thWord == null)
            return;
        thWord.setWord(newName);
        fireWordRenamedEvent(thWord, word);
        fireWordChangedEvent(thWord);
    }

    /**
     * Add a word
     * 
     * @param word the word to add
     * @return a ThesaurusWord
     */
    public ThesaurusWord addWord(String word)
    {
        ThesaurusWord thWord = new ThesaurusWord(word);
        if (!words.contains(thWord))
        {
            thWord.addThesaurusModelListener(this);
            words.add(thWord);
            fireWordAddedEvent(thWord);
            return thWord;
        }
        return null;
    }

    /**
     * Delete a word
     * 
     * @param word the word to delete
     */
    public void deleteWord(String word)
    {
        ThesaurusWord thWord = getWord(word);
        if (thWord == null)
            return;
        Vector<?> synonyms = thWord.getSynonyms();
        for (int i = 0; i < synonyms.size(); i++)
            deleteSynonym(thWord.getWord(), ((ThesaurusWord) synonyms.get(i)).getWord());
        Vector<?> homonyms = thWord.getHomonyms();
        for (int i = 0; i < homonyms.size(); i++)
            deleteHomonym(thWord.getWord(), ((ThesaurusWord) homonyms.get(i)).getWord());
        words.remove(thWord);
        fireWordDeletedEvent(thWord);
    }

    /**
     * Get the number of words in the Thesaurus
     * 
     * @return the word count
     */
    public int getWordCount()
    {
        return words.size();
    }

    /**
     * Get the word in a certain index
     * 
     * @param i the index of the word to get
     * @return the ThesaurusWord
     */
    public ThesaurusWord getWord(int i)
    {
        return words.get(i);
    }

    /**
     * Get a ThesaurusWord that equals to a certain string
     * 
     * @param word the word to find in the Thesaurus
     * @return a Thesaurus
     */
    protected ThesaurusWord getWord(String word)
    {
        ThesaurusWord thWord = new ThesaurusWord(word);
        int index = words.indexOf(thWord);
        if (index < 0)
            return null;
        else
            return words.get(index);
    }

    /**
     * Add a synonym to the Thesaurus
     * 
     * @param theWord the word to add a synonym to
     * @param theSynonym the synonym to add
     */
    public void addSynonym(String theWord, String theSynonym)
    {
        if (theWord.equals(theSynonym))
            return;
        String word = theWord.toLowerCase();
        String synonym = theSynonym.toLowerCase();
        ThesaurusWord thWord = getWord(word);
        if (thWord == null)
            thWord = addWord(word);
        ThesaurusWord thSynonym = getWord(synonym);
        if (thSynonym == null)
            thSynonym = addWord(synonym);
        thWord.addSynonym(thSynonym);
        fireSynonymAddedEvent(thWord, thSynonym);
    }

    /**
     * Delete a synonym from the Thesaurus
     * 
     * @param theWord the word to delete the synonym from
     * @param theSynonym the synonym to delete
     */
    public void deleteSynonym(String theWord, String theSynonym)
    {
        String word = theWord.toLowerCase();
        ThesaurusWord thWord = getWord(word);
        if (thWord == null)
            return;
        ThesaurusWord thSynonym = getWord(theSynonym);
        if (thSynonym == null)
            return;
        thWord.deleteSynonym(thSynonym);
        // if(thSynonym.getSynonyms().isEmpty() && thSynonym.getHomonyms().isEmpty())
        // removeWord(thSynonym);
        fireSynonymDeletedEvent(thWord, thSynonym);
    }

    /**
     * Add a homonym to the Thesaurus
     * 
     * @param theWord the word to add a homonym to
     * @param theHomonym the homonym to add
     */
    public void addHomonym(String theWord, String theHomonym)
    {
        if (theWord.equals(theHomonym))
            return;
        String word = theWord.toLowerCase();
        String homonym = theHomonym.toLowerCase();
        ThesaurusWord thWord = getWord(word);
        if (thWord == null)
            thWord = addWord(word);
        ThesaurusWord thHomonym = getWord(homonym);
        if (thHomonym == null)
            thHomonym = addWord(homonym);
        thWord.addHomonym(thHomonym);
        fireHomonymAddedEvent(thWord, thHomonym);
    }

    /**
     * Delete a homonym from the Thesaurus
     * 
     * @param theWord the word to delete the homonym from
     * @param theHomonym the homonym to delete
     */
    public void deleteHomonym(String theWord, String theHomonym)
    {
        String word = theWord.toLowerCase();
        ThesaurusWord thWord = getWord(word);
        if (thWord == null)
            return;
        ThesaurusWord thHomonym = getWord(theHomonym);
        if (thHomonym == null)
            return;
        thWord.deleteHomonym(thHomonym);
        // if(thHomonym.getSynonyms().isEmpty() && thHomonym.getHomonyms().isEmpty())
        // removeWord(thHomonym);
        fireHomonymDeletedEvent(thWord, thHomonym);
    }

    /**
     * Get all the synonyms of a word
     * 
     * @param word the word to get the synonyms of
     * @return a vector of the word's synonyms
     */
    public Vector<String> getSynonyms(String word)
    {
        ThesaurusWord thWord = getWord(word);
        if (thWord == null)
            return null;
        Vector<?> synonymsWords = thWord.getSynonyms();
        Vector<String> synonyms = new Vector<String>();
        for (int i = 0; i < synonymsWords.size(); i++)
            synonyms.add(((ThesaurusWord) synonymsWords.get(i)).getWord());
        return synonyms;
    }

    /**
     * Get all the homonyms of a word
     * 
     * @param word the word to get the homonyms of
     * @return a vector of the word's homonyms
     */
    public Vector<String> getHomonyms(String word)
    {
        ThesaurusWord thWord = getWord(word);
        if (thWord == null)
            return null;
        Vector<?> homonymsWords = thWord.getHomonyms();
        Vector<String> homonyms = new Vector<String>();
        for (int i = 0; i < homonymsWords.size(); i++)
            homonyms.add(((ThesaurusWord) homonymsWords.get(i)).getWord());
        return homonyms;
    }

    /**
     * Check is a string is a synonym of a word
     * 
     * @param word the word to check
     * @param synonym the synonym to check
     * @return true of the string is a synonym of the word
     */
    public boolean isSynonym(String word, String synonym)
    {
        ThesaurusWord thWord = getWord(word);
        if (thWord == null)
            return false;
        return thWord.isSynonym(new ThesaurusWord(synonym));
    }

    /**
     * Check is a string is a homonym of a word
     * 
     * @param word the word to check
     * @param homonym the homonym to check
     * @return true of the string is a homonym of the word
     */
    public boolean isHomonym(String word, String homonym)
    {
        ThesaurusWord thWord = getWord(word);
        if (thWord == null)
            return false;
        return thWord.isHomonym(new ThesaurusWord(homonym));
    }
}