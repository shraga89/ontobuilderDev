package ac.technion.iem.ontobuilder.matching.algorithms.line1.domain;

import java.util.regex.PatternSyntaxException;

import ac.technion.iem.ontobuilder.core.ontology.Domain;
import ac.technion.iem.ontobuilder.core.ontology.DomainEntry;

public class WeightOrLenghtFieldSniffer implements DataTypeSniffer {

	@Override
	public boolean sniff(Domain d) {
		String [] currencies = {"kg","g","gr","l","ml","kl","mcg","mg","mt","t","oz","lb","unit",
				"cm","mm","km","m","nm","pm","in","ft","yd","mi"};
		boolean flag = false;
		String[] splitArray = {};
		for (DomainEntry de : d.getEntries()){
			String GrossentryVal = de.toString();
			String entryVal = GrossentryVal.replaceAll("(?<=[A-Za-z])(?=[0-9])|(?<=[0-9])(?=[A-Za-z])", " ");
			try {
			     splitArray = entryVal.split("\\s+");
			} catch (PatternSyntaxException ex) {
			    // 
			}
			for(String currChar :  currencies){
				if (entryVal.indexOf(currChar) != -1){
					try	{
						Double d1 = Double.parseDouble(splitArray[0]);
						flag = true;
						break;
						
					}
					catch (NumberFormatException e)	{
						flag = false;
					}
				}
			}
		}
	
		return flag;
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return "Weight Field";
	}

}
