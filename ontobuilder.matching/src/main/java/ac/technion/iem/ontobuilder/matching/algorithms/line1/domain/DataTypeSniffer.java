package ac.technion.iem.ontobuilder.matching.algorithms.line1.domain;

import ac.technion.iem.ontobuilder.core.ontology.Domain;

public interface DataTypeSniffer {
	
	boolean sniff(Domain d);
	
	String name();

}
