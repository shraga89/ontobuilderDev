package ac.technion.iem.ontobuilder.matching.algorithms.line1.domain;

import ac.technion.iem.ontobuilder.core.ontology.Domain;
import ac.technion.iem.ontobuilder.core.ontology.DomainEntry;

public class YearSniffer implements DataTypeSniffer {

	@Override
	public boolean sniff(Domain d) {
		boolean flag = true;
		try{
			for (DomainEntry de : d.getEntries()){
				/* try first to change to int, if Ok check the int lentgh not equal to 4 */
				Integer x = new Integer(de.toString());
				if (x.toString().length() != 4){
					flag = false;
					break;
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
		return "Year";
	}

}
