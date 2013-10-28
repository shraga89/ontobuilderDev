package ac.technion.iem.ontobuilder.matching.algorithms.line1.domain;

import java.util.regex.PatternSyntaxException;

import ac.technion.iem.ontobuilder.core.ontology.Domain;
import ac.technion.iem.ontobuilder.core.ontology.DomainEntry;

public class ISBNCodeSniffer implements DataTypeSniffer {

	@Override
	public boolean sniff(Domain d) {
		String currChar = "ISBN";
		boolean flag = false;
		String[] splitArray = {};
		for (DomainEntry de : d.getEntries()){
			String entryVal = de.toString();
			if (entryVal !=""){
				try {
				     splitArray = entryVal.split("\\s+");
				} catch (PatternSyntaxException ex) {
					
				}
				
				if(splitArray.length==0){
                    break;
				}
				
				if (splitArray[0].equals(currChar)){
						int count = 0;
						for (int i = 0, len = entryVal.length(); i < len; i++) {
						    if (Character.isDigit(entryVal.charAt(i))) {
						        count++;
						    }
						}
						if(count == 10 || count == 13){
							flag = true;
							
						}
					}
			}
			
		}
	
		return flag;
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return "ISBN";
	}

}
