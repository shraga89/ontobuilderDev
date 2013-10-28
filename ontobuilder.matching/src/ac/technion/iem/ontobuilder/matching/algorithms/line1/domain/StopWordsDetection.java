package ac.technion.iem.ontobuilder.matching.algorithms.line1.domain;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StopWordsDetection {
	
		public static String [] stopWords={"a","able","about","across","after","all","almost",
	        "also","am","among","an","and","any","are","as","at",
	        "be","because","been","but","by","can","cannot","could",
	        "dear","did","do","does","either","else","ever","every",
	        "for","from","get","got","had","has","have","he","her",
	        "hers","him","his","how","however","i","if","in","into",
	        "is","it","its","just","least","let","like","likely",
	        "may","me","might","most","must","my","neither","no",
	        "nor","not","of","off","often","on","only","or","other",
	        "our","own","rather","said","say","says","she","should",
	        "since","so","some","than","that","the","their","them",
	        "then","there","these","they","this","tis","to","too",
	        "twas","us","wants","was","we","were","what","when",
	        "where","which","while","who","whom","why","will","with",
	        "would","yet","you","your"};
	
		public static boolean isContainsStopWord(String str) {
		boolean isValid = false;
				
		String expression ="("; 
		for (int i=0;  i <= stopWords.length-2; i++){
		expression+=stopWords[i];
		expression+="|";	    	
		}
		
		expression+=stopWords[stopWords.length-1]+")";
		
		
		
		String starts ="(?i)"+expression+"\\s.*"; // starts with and_
		String ends ="(?i).*\\s"+expression; // ends with _and
		String contains=".*\\s"+expression+"\\s.*"; // contains _and_
		
		
		CharSequence inputStr = str;
		
		Pattern patternStarts = Pattern.compile(starts, Pattern.CASE_INSENSITIVE);
		Pattern patternEnds = Pattern.compile(ends, Pattern.CASE_INSENSITIVE);
		Pattern patternContains = Pattern.compile(contains, Pattern.CASE_INSENSITIVE);
		
		
		Matcher matcherStarts = patternStarts.matcher(inputStr);
		Matcher matcherEnds = patternEnds.matcher(inputStr);
		Matcher matcherContains = patternContains.matcher(inputStr);
		
		if (matcherStarts.matches() || matcherEnds.matches() || matcherContains.matches()) {
		isValid = true;
		}
		return isValid;
		}
	


}
