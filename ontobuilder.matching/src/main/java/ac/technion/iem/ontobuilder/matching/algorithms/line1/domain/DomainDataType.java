package ac.technion.iem.ontobuilder.matching.algorithms.line1.domain;


public enum DomainDataType {
	NUMERIC(new NumberSniffer()),
	DAY(new DaySniffer()),
	MONTH(new MonthSniffer()),
	YEAR(new YearSniffer()),
	DATE(new DateSniffer()),
	MAIL(new MailSniffer()),
    BOOLEAN(new BooleanSniffer()),
    CURRENCY(new CurrencySniffer()),
    PHONE(new PhoneSniffer()),
    DOUBLE(new DoubleSniffer());

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
