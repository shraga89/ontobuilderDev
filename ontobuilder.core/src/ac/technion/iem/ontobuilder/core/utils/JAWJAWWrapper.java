package ac.technion.iem.ontobuilder.core.utils;

import java.util.LinkedHashSet;
import java.util.Set;

import edu.cmu.lti.jawjaw.JAWJAW;
import edu.cmu.lti.jawjaw.pobj.POS;

/**
 * This class is a wrapper for <code>edu.cmu.lti.ws4j.WS4J</code><
 * 
 * @author Sapir Golan
 
 */
public class JAWJAWWrapper extends JAWJAW  {
	
	/**
	 * This method check that given word is not a single letter<br>
	 * If it's a single letter it returns an empty Set;<br>
	 * otherwise it calls JAWJAW <i>findDefinitions</i>
	 * @param word
	 * @param pos
	 * @return
	 */
	public static Set<String> findDefinitions( String word, POS pos ) {
		 Set<String> results = new LinkedHashSet<String>();
		 //check given word is not a single letter
		 if (word != null && word.length() >1) {
			 results =  JAWJAW.findDefinitions(word, pos);
		 }
		 return results;
	}
}
