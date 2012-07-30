package ac.technion.iem.ontobuilder.matching.algorithms.line1.domain;

import ac.technion.iem.ontobuilder.core.ontology.Domain;
import ac.technion.iem.ontobuilder.core.ontology.DomainEntry;

public class NumberSniffer implements DataTypeSniffer {

	@Override
	public boolean sniff(Domain d) {
		boolean flag = true;
		try
		{
		for (DomainEntry de : d.getEntries())
		{
			@SuppressWarnings("unused")
			Double d1 = Double.parseDouble(de.toString());
		}
		}
		catch (NumberFormatException e)
		{
			flag = false;
		}
		return flag;
	}

}
