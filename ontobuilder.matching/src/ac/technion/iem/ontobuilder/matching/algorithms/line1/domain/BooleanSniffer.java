package ac.technion.iem.ontobuilder.matching.algorithms.line1.domain;

import ac.technion.iem.ontobuilder.core.ontology.Domain;
import ac.technion.iem.ontobuilder.core.ontology.DomainEntry;

/* Check if the Value equals to a Boolean Type*/ 
public class BooleanSniffer implements DataTypeSniffer {

	@Override
	public boolean sniff(Domain d) {
		String[] boolArray = {"TRUE","FALSE","YES","NO","1","0"};
		boolean flag = true;
			for (DomainEntry de : d.getEntries()){
				boolean inList = false;
				for (String str : boolArray){
					if (de.toString().equals(str)){
						inList = true;
					}
				}
				if (!inList){
					flag = false;
					break;			
				}

			}

		return flag;
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return "Boolean";
	}

}
