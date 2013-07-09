package ac.technion.iem.ontobuilder.matching.algorithms.line1.misc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import org.jdom2.Element;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.core.utils.StringUtilities;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.common.AbstractAlgorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.common.Algorithm;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import edu.cmu.lti.jawjaw.pobj.POS;
import edu.cmu.lti.ws4j.WS4J; 

public class WordNetAlgorithm extends AbstractAlgorithm {

	private HashMap<String,String> unknownwords = new HashMap<String,String>();
	private HashMap<String,String> knownWords = new HashMap<String,String>();

	public WordNetAlgorithm(){
		
	}
	@Override
	public String getName() {
		return "WordNet Algorithm";
	}

	@Override
	public String getDescription() {
		return "WordNet Algorithm using Jiang-Conrath distance";
	}
	
	@Override
	public MatchInformation match(Ontology target,
			Ontology candidate) {
		
	      MatchInformation matchInformation = new MatchInformation(candidate,target);
	      match(matchInformation);
	      matchInformation.setAlgorithm(this);
	      return matchInformation ;
	}

	@Override
	public void configure(Element element) {
		
	}
	
	/**
	 * Converts term names to words, looks words up in wordnet and calculates similarity
	 * @param cands
	 * @param targs
	 * @return
	 */
	 private void match(MatchInformation mi)
    {
		 ArrayList<Term> cands = getTerms(mi.getCandidateOntology());
		 ArrayList<Term> targs = getTerms(mi.getTargetOntology());
	        for (int i = 0; i < targs.size(); i++)
	        {
	            for (int j = 0; j < cands.size(); j++)
	            {
	            	
	            	String cName = cands.get(j).getName();
	            	String tName = targs.get(i).getName();
	            	//Extract words
	            	ArrayList<String> canidatesWordList = tokenizedWordsSimple(cName);
	            	ArrayList<String> targetWordList = tokenizedWordsSimple(tName);
	            	double avgSim = 0.0;
	            	for (String cWord : canidatesWordList)
	            	{
	            		double maxSim = 0.0;
	            		String cleanCandWord = cleanWord(cWord);
	            		if (checkWord(cWord,cleanCandWord,true))
	            		{
	            			for (String tWord : targetWordList)
		            		{
	            				String cleanTargWord = cleanWord(tWord);
		            			if (checkWord(tWord,cleanTargWord,true))
		            			{
		            				maxSim = Math.max(maxSim, WS4J.calcDistanceByJiangConrath(cleanCandWord,tWord));
		            				maxSim = Math.min(maxSim, 1.0);
		            			}
		            		}
	            		}
	            		avgSim+=maxSim;
	            	}
	            	if (!canidatesWordList.isEmpty()) avgSim /= canidatesWordList.size();
	            	mi.updateMatch(targs.get(i), cands.get(j), avgSim);
	            }
	        }
	        for (String word : unknownwords.keySet())
	        {
	        	String cWord = unknownwords.get(word);
				System.err.println("No definitions were found for " + word + " which was cleaned to " + cWord);
	        }
	    }

	 /**
	 * Cleans non-alphabetical symbols (street1->street, from: -> from)
	 * @TODO add option of removing stop words (of, you, your, etc.)
	 * @TODO add option to expand acronyms and shortened words (Num->Number, Dr. -> Doctor)
	 * @TODO check why plural is not recognized (quotes in spid 55)
	 * @param word to clean
	 * @return clean word
	 */
	 private String cleanWord(String word)
	 {
		 if (knownWords.containsKey(word)) return knownWords.get(word);
		 //Remove non alphabetical characters
		 StringBuffer sb = new StringBuffer();
		 for (char c : word.toCharArray())
			 if (Character.isLetter(c))
				 sb.append(c);
		 //Lower case
		 String cWord = sb.toString().toLowerCase();
		 
		 //Handle english plural
		 if (!checkWord(word,cWord,false) && cWord.endsWith("s")) {
			 if (cWord.endsWith("ies"))
			 {
				 String singularWord = cWord.substring(0, cWord.length()-3) + "y";
				 	if (checkWord(word,singularWord,false))
				 		cWord = singularWord;
			 }
			 else
			 {	String singularWord = cWord.substring(0, cWord.length()-1);
			 	if (checkWord(word,singularWord,false))
			 		cWord = singularWord;
			 }
		}
		 knownWords.put(word, cWord);
		 return cWord;
	 }
	/**
	 * Check if word exists in dictionary
	 * @param origWord original word
	 * @param cleanWord clean version of word
	 */
	private boolean checkWord(String origWord, String cleanWord,boolean recordUnkowns) {
		Set<String> defs =  WS4J.findDefinitions(cleanWord, POS.n);
		defs.addAll(WS4J.findDefinitions(cleanWord, POS.v));
		defs.addAll(WS4J.findDefinitions(cleanWord, POS.a));
		defs.addAll(WS4J.findDefinitions(cleanWord, POS.r));
		if (defs.isEmpty())
			{
				if(recordUnkowns) 
					unknownwords.put(origWord,cleanWord);
				return false;
			}
		return true;
	}

	@Override
	public Algorithm makeCopy() {
		WordNetAlgorithm algo = new WordNetAlgorithm();
		return algo ;
	}
	
	  private ArrayList<Term> getTerms(Ontology o)
	    {
	        Vector<Term> terms = o.getTerms(true);
	        ArrayList<Term> result = new ArrayList<Term>();
	        for (int i = 0; i < terms.size(); i++)
	        {
	            result.add(i, terms.get(i));
	        }
	        return result;
	    }
	  
	  /**
	   * Breaks up a given string to words by Capitalized letters
	   *  and separators (tab, space, hyphen, underscore)
	   * @param canidateName
	   * @return Arraylist of strings representing distinct words found
	   */
	 private ArrayList<String> tokenizedWordsSimple(String canidateName)
	  {
		 String canidateCamelCase = StringUtilities.separateCapitalizedWords(canidateName);
		 ArrayList<String> canidateWordList = StringUtilities.breakTextIntoWords(canidateCamelCase);
		 if (canidateWordList.isEmpty()) {
			 System.err.println("No words were found in " + canidateName);
		 }
		 return canidateWordList;
	  }
}


