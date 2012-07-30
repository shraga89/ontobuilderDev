package ac.technion.iem.ontobuilder.matching.algorithms.line1.domain;


public enum DomainDataType {NUMERIC(new NumberSniffer());

private DomainDataType(DataTypeSniffer sniff)
{
	this.sniff=sniff;
}

	/**
 * @return the sniff
 */
public DataTypeSniffer getSniff() {
	return sniff;
}

	private DataTypeSniffer sniff;
}
