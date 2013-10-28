/**
 * 
 */
package ac.technion.iem.ontobuilder.matching.algorithms.line1.domain;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import ac.technion.iem.ontobuilder.core.ontology.Domain;
import ac.technion.iem.ontobuilder.core.ontology.DomainEntry;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.WordNetAlgorithm;

/**
 * @author Yulia and Viki 
 *
 */
public class PersonNameSniffer implements DataTypeSniffer {
	
	// Logic for PersonName Detection
	
	/*  1. Check if string contains only letters or spaces, dots
		2. Check if string contains stop words
		3. Check if string contains Prefix ( Ms, Mrs..)
		4. Check if sting contains less than 10 words
		5. Check if words in string are in dictionary
		*/

	
	@Override
	public boolean sniff(Domain d) {
		boolean flag = false;
				
		for (DomainEntry de : d.getEntries()){
			String entryVal = de.toString();
			if(isContainsOnlyLetters(entryVal)==false){
				//return false;
				continue;
			}
			
			else if(StopWordsDetection.isContainsStopWord(entryVal)==true){
				//return false;
				break;
			}
			else if(isNameWithPrefix(entryVal)==true){
				//return true;
				flag=true;
				break;
				
			}
			else if(wordCounter(entryVal)>10){
				//return false;
				
				break;
			}
			else if( entryVal.length()<=3)
			{
				break;
			}
			else if( entryVal.length()>3)
			{
				
				//  Check if words in string are in dictionary
				
				String[] splitArray = {};
				Boolean[] dictResults = {};
				try {
				     splitArray = entryVal.split("\\s+");
				     dictResults = new Boolean[splitArray.length];
				     
				     
				} catch (PatternSyntaxException ex) {
					  ex.printStackTrace();
					
				}
				
				for ( int i=0; i<splitArray.length; i++ )
				{
					
				
					 String word = splitArray[i];
						
					 dictResults[i] =WordNetAlgorithm.isWordInDiction(word);

				
				}
				
				int temp=0;
				for ( Boolean entry: dictResults){
					
					if(entry){ temp=1;
					//flag=true;
					//break;
					}
					
				}
				
				if (temp==0) { 
					flag=true;
					} 
					
			}	
				
		}
			
		

	
		return flag;
	}

	
	@Override
	public String name() {
		// TODO Auto-generated method stub
		return "PersonNameSniffer";
	}
	
	public static boolean isContainsOnlyLetters(String str) { 
		
		// Returns true if the string only contains alphabets and spaces
		
	    boolean isValid = false;
	    String expression =  "^[a-zA-Z\\s\\.\\'\\-\\,]*$";
	    CharSequence inputStr = str;
	    Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
	    Matcher matcher = pattern.matcher(inputStr);
	    if (matcher.matches() & str.matches(".*\\w.*"))  {
	        isValid = true;
	    }
	    return isValid;
	}
	
	
	public static boolean isNameWithPrefix(String str) {
	    boolean isValid = false;
	    
	    // returns true if string with Name Prefix
	    
	    String starts =  "(?i)(Dr|Prof|Ms|Miss|Mrs|Mr|Jr|Rev|Fr|Hon|Pres|Gov|Ofc)(\\s|\\.).*";
	    String contains=".*\\s(Dr|Prof|Ms|Miss|Mrs|Mr|Jr|Rev|Fr|Hon|Pres|Gov|Ofc)(\\s|\\.).*"; // contains _and_
	    
	     
	    //String ends ="(?i).*\\s"+expression; // ends with _and
	    
	    CharSequence inputStr = str;
	    Pattern patternStarts = Pattern.compile(starts, Pattern.CASE_INSENSITIVE);
	    Pattern patternContains = Pattern.compile(contains, Pattern.CASE_INSENSITIVE);

	    Matcher matcherStarts = patternStarts.matcher(inputStr);
	    Matcher matcherContains = patternContains.matcher(inputStr);

	    if (matcherStarts.matches() || matcherContains.matches()) {
	        isValid = true;
	    }
	    return isValid;
	}
	
	public static int wordCounter(String s){
	    if (s == null)
	       return 0;
	    return s.trim().split("\\s+").length;
	}
	
	

}
