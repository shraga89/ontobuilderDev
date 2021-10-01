package ac.technion.iem.ontobuilder.matching.algorithms.line1.domain;

import ac.technion.iem.ontobuilder.core.ontology.Domain;
import ac.technion.iem.ontobuilder.core.ontology.DomainEntry;

public class CurrencySniffer implements DataTypeSniffer {

	@Override
	public boolean sniff(Domain d) {
		String currChar = "$";
		boolean flag = true;
		for (DomainEntry de : d.getEntries()){
			
			String entryVal = de.toString();

			if (!entryVal.contains(currChar)){
				flag = false;
				break;
			}
			else {
				try	{
					@SuppressWarnings("unused")
					Double d1 = Double.parseDouble(entryVal.replace(currChar,""));
					
				}
				catch (NumberFormatException e)	{
					flag = false;
				}
				 
			}
		}
	
		return flag;
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return "Currency";
	}

}
