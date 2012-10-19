package ac.technion.iem.ontobuilder.matching.algorithms.line1.domain;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ac.technion.iem.ontobuilder.core.ontology.Domain;
import ac.technion.iem.ontobuilder.core.ontology.DomainEntry;

public class PhoneSniffer implements DataTypeSniffer {

	@Override
	public boolean sniff(Domain d) {
		boolean flag = true;
			for (DomainEntry de : d.getEntries()){				
				if (!isPhoneNumberValid(de.toString())){
					flag = false ;
					break;
				}
			}
		return flag;
	}
	
	
	
	/* check if a string is a valid phone number */ 
	public static boolean isPhoneNumberValid(String phoneNumber){  
		boolean isValid = false;  
		/* Phone Number formats: (nnn)nnn-nnnn; nnnnnnnnnn; nnn-nnn-nnnn 
		    ^\\(? : May start with an option "(" . 
		    (\\d{3}): Followed by 3 digits. 
		    \\)? : May have an optional ")" 
		    [- ]? : May have an optional "-" after the first 3 digits or after optional ) character. 
		    (\\d{3}) : Followed by 3 digits. 
		     [- ]? : May have another optional "-" after numeric digits. 
		     (\\d{4})$ : ends with four digits. 
		 
		         Examples: Matches following phone numbers: 
		         (123)456-7890, 123-456-7890, 1234567890, (123)-456-7890 
		 
		*/  
		//Initialize reg ex for phone number.   
		//String expression = "^\\(?(\\d{3})\\)?\\s?[- ]?(\\d{3})[- ]?(\\d{4})$";
		String expression = "^\\(?(\\d{2,3})?\\)?[- ]?(\\d{3})[- ]?(\\d{4})$";
		CharSequence inputStr = phoneNumber.replace(" ", "");  
		Pattern pattern = Pattern.compile(expression);  
		Matcher matcher = pattern.matcher(inputStr);  
		if(matcher.matches()){  
			isValid = true;  
		}  
		else { 
			inputStr = phoneNumber;
			matcher = pattern.matcher(inputStr);
			if(matcher.matches()){  
				isValid = true;  
			}  
		}
		return isValid;  
	}



	@Override
	public String name() {
		// TODO Auto-generated method stub
		return "Phone";
	}  

}
