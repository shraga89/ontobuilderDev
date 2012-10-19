package ac.technion.iem.ontobuilder.matching.algorithms.line1.domain;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ac.technion.iem.ontobuilder.core.ontology.Domain;
import ac.technion.iem.ontobuilder.core.ontology.DomainEntry;

public class MailSniffer implements DataTypeSniffer {

	@Override
	public boolean sniff(Domain d) {
		boolean flag = true;
		Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
		for (DomainEntry de : d.getEntries()){
			Matcher m = p.matcher(de.toString());
			boolean matchFound = m.matches();
			if(!matchFound){
				flag = false;
				break;
			}
		}
	
		return flag;
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return "Mail";
	}
	
	

}
