package ac.technion.iem.ontobuilder.core.utils;

import edu.uniba.di.lacam.kdde.lexical_db.ILexicalDatabase;
import edu.uniba.di.lacam.kdde.lexical_db.MITWordNet;
import edu.uniba.di.lacam.kdde.lexical_db.data.Concept;
import edu.uniba.di.lacam.kdde.lexical_db.item.POS;
import edu.uniba.di.lacam.kdde.ws4j.util.WS4JConfiguration;

import java.util.ArrayList;
import java.util.List;


/**
 * This class is a wrapper for WS4J dictionary
 * 
 * @author Tomer Sagi
 
 */
public class WordNetDict  {

	private static WordNetDict wrd;
	private final ILexicalDatabase db;

	private WordNetDict()
	{
		WS4JConfiguration.getInstance().setMemoryDB(true);
		WS4JConfiguration.getInstance().setMFS(true);
		db = new MITWordNet();
	}

	public static WordNetDict getMe() {
		if (wrd==null) {
			wrd = new WordNetDict();
		}
		return wrd;
	}

	/**
	 * This method check that given word is not a single letter<br>
	 * If it's a single letter it returns an empty Set;<br>
	 * otherwise it calls JAWJAW <i>findDefinitions</i>
	 * @param word to check
	 * @param pos word part of speech
	 * @return list of concepts the word matches
	 */
	public List<Concept> findDefinitions(String word, POS pos ) {
		 List<Concept> results = new ArrayList<>();
		 //check given word is not a single letter
		 if (word != null && word.length() >1) {
			 results =  db.getAllConcepts(word, pos);
		 }
		 return results;
	}
}
