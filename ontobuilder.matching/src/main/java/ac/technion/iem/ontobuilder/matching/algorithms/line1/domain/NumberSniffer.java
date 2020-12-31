package ac.technion.iem.ontobuilder.matching.algorithms.line1.domain;

import ac.technion.iem.ontobuilder.core.ontology.Domain;
import ac.technion.iem.ontobuilder.core.ontology.DomainEntry;

public class NumberSniffer implements DataTypeSniffer {

	@Override
	public boolean sniff(Domain d) {
		
		boolean flag = true;

			for (DomainEntry de : d.getEntries()){
				try	{					
					Integer.parseInt(de.toString());
				}
				catch (NumberFormatException e)	{
					flag = false;
					break;
				}
			}
		
		return flag;
	
	}

	@Override
	public String name() {
		return "Number";
	}
	
	

}
