package ac.technion.iem.ontobuilder.matching.algorithms.line1.domain;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import ac.technion.iem.ontobuilder.core.ontology.Domain;
import ac.technion.iem.ontobuilder.core.ontology.DomainEntry;

/**
 * @author Yulia
 *
 */
public class DaysOfWeekSniffer implements DataTypeSniffer {

	
	@Override
	public boolean sniff(Domain d) {	
		
		boolean flag = false;
			

		for (DomainEntry de : d.getEntries()){
			String entryVal = de.toString();
			Pattern p = Pattern.compile("[a-zA-z0-9]*");
			if ( p.matcher(entryVal).matches()){
			
				if (isContainsOnlyDaysWeek(entryVal))
				{
					flag=true;
					break;
				}
			}
		}
		
		
				
		return flag;
	}

	
	@Override
	public String name() {
		// TODO Auto-generated method stub
		return "DayOfWeek";
	}
	
	
	public static String [] DaysOfWeek={"Monday","M","Mon","Mo",
										"Tuesday","T","Tue","Tu",
										"Wednesday","W","Wed","We",
										"Thursday","R","Thu","Th",
										"Friday","F","Fri","Fr",
										"Saturday","S","Sat","Sa",
										"Sunday","U","Sun","Su","Sn",
										"TR"};
	
	public static String buildExpression(){
		StringBuilder expression = new StringBuilder("(");
		for (int i=0;  i <= DaysOfWeek.length-2; i++){
			expression.append(DaysOfWeek[i]);
			expression.append("|");
		}

		expression.append(DaysOfWeek[DaysOfWeek.length - 1]).append(")");
		return expression.toString();

	}

	public static boolean isContainsOnlyDaysWeek(String str) {
	//boolean isValid = false;

		return bySpace(str) || byUpCase(str);


	}

	public static boolean bySpace(String str) {

		String[] splitArrayBySpace;
		
		try {
			splitArrayBySpace = str.split("\\s+");
		} catch (PatternSyntaxException ex) {
			return false;
		}
		
		for ( String word :splitArrayBySpace){

			if (  word.matches(".*\\w.*")){

				Pattern patternClearString = Pattern.compile(buildExpression(), Pattern.CASE_INSENSITIVE);
				Matcher matcherClearString = patternClearString.matcher(word);
				
				if (!matcherClearString.matches())
				{
					return false;
				}
			}
			else{
				
				return false;
			}
			
		}
		
		return true;
	}

	public static boolean byUpCase(String str) {


		String[] splitArrayBySpace;
		
		try {
			splitArrayBySpace = str.split("(?=\\p{Lu})");
		} catch (PatternSyntaxException ex) {
			return false;
		}
		
		for ( String word :splitArrayBySpace){

			if (  word.matches(".*\\w.*")){
				Pattern patternClearString = Pattern.compile(buildExpression(), Pattern.CASE_INSENSITIVE);
				Matcher matcherClearString = patternClearString.matcher(word);
				
				
				if (!matcherClearString.matches())
				{
				return false;
				}
			}
			else
			{
			 return false;
			}
			
		}
		
		return true;
	}
	
	
	
	

}
