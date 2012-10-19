package ac.technion.iem.ontobuilder.matching.algorithms.line1.domain;

import java.util.regex.Pattern;

import ac.technion.iem.ontobuilder.core.ontology.Domain;
import ac.technion.iem.ontobuilder.core.ontology.DomainEntry;

public class DoubleSniffer implements DataTypeSniffer {
	@Override
	public boolean sniff(Domain d) {
		//Pattern doublePattern = Pattern.compile("-?\\d+(\\.\\d*)?");
		Pattern doublePattern = Pattern.compile("-?\\d+\\.+(\\d*)?");
		boolean flag = true;
		
			for (DomainEntry de : d.getEntries()){
				//System.out.println(de.toString());
				if (!doublePattern.matcher(de.toString()).matches()){
					flag = false;
					return flag;
				}
			}
		return flag;
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return "Double";
	}	
}

