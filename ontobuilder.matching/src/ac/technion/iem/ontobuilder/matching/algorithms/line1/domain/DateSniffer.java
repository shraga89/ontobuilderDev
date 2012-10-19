package ac.technion.iem.ontobuilder.matching.algorithms.line1.domain;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;

import ac.technion.iem.ontobuilder.core.ontology.Domain;
import ac.technion.iem.ontobuilder.core.ontology.DomainEntry;

public class DateSniffer implements DataTypeSniffer {

	@Override
	public boolean sniff(Domain d) {
		boolean flag = true;
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MM-yyyy");
		for (DomainEntry de : d.getEntries()){
			try {
				if (sdf1.parse(de.toString(),new ParsePosition(0)) == null){
					if (sdf2.parse(de.toString(),new ParsePosition(0)) == null){
						flag = false;
						break;
					}
					
				}
			}
			catch (NullPointerException e){
				flag = true;
			}
				
		}
		return flag;
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return "Date";
	}

}
