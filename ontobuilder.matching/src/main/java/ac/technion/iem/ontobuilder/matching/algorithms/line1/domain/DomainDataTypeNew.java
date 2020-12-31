package ac.technion.iem.ontobuilder.matching.algorithms.line1.domain;

public enum DomainDataTypeNew {

	
	NUMERIC(new NumberSniffer()),
	DAY(new DaySniffer()),
	MONTH(new MonthSniffer()),
	YEAR(new YearSniffer()),
	DATE(new DateSniffer()),
	MAIL(new MailSniffer()),
    BOOLEAN(new BooleanSniffer()),
    CURRENCY(new CurrencySniffer()),
    PHONE(new PhoneSniffer()),
    DOUBLE(new DoubleSniffer()),
	
	// new sniffers
	PersonName(new PersonNameSniffer()),
	DaysOfWeek (new DaysOfWeekSniffer()),
    CurrencyCode( new CurrencyCodeSniffer()),
	ISBN(new ISBNCodeSniffer()),
	Price ( new PriceSniffer()),
	UOMCode ( new WeightOrLenghtCodeSniffer()),
	UOMField ( new WeightOrLenghtFieldSniffer()),
	DateTime (new DateTimeSniffer()),
	Time( new TimeSniffer());
	
	

private DomainDataTypeNew(DataTypeSniffer sniff)
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
