package ac.technion.iem.ontobuilder.matching.algorithms.line1.domain;

import ac.technion.iem.ontobuilder.core.ontology.Domain;
import ac.technion.iem.ontobuilder.core.ontology.DomainEntry;

public class DaySniffer implements DataTypeSniffer {

	@Override
	public boolean sniff(Domain d) {
		boolean flag = true;
		try{
			for (DomainEntry de : d.getEntries()){
				Integer x = new Integer(de.toString());
				
				if (x<1 || x>31){
					flag = false;
					return flag;
				}
			}
		}
		catch (NumberFormatException e){
			flag = false;
		}
			
		return flag;
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return "Day";
	}

}
