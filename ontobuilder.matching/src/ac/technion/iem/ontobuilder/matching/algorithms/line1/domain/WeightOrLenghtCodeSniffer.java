package ac.technion.iem.ontobuilder.matching.algorithms.line1.domain;

import ac.technion.iem.ontobuilder.core.ontology.Domain;
import ac.technion.iem.ontobuilder.core.ontology.DomainEntry;

public class WeightOrLenghtCodeSniffer implements DataTypeSniffer {

	@Override
	public boolean sniff(Domain d) {
		String [] currencies = {"kg","g","gr","l","ml","kl","mcg","mg","mt","t","oz","lb","unit",
				"cm","mm","km","m","nm","pm","in","ft","yd","mi"};
		boolean flag = false;
		for (DomainEntry de : d.getEntries()){
			String entryVal = de.toString();
			for(String currChar :  currencies){
				if (entryVal.equals(currChar)){
					flag = true;
					break;
				}
			}
		}
	
		return flag;
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return "Weight Unit";
	}

}
