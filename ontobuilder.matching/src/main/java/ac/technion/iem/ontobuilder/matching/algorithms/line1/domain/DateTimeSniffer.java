package ac.technion.iem.ontobuilder.matching.algorithms.line1.domain;

import ac.technion.iem.ontobuilder.core.ontology.Domain;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ac.technion.iem.ontobuilder.core.ontology.DomainEntry;

public class DateTimeSniffer implements DataTypeSniffer {

	@Override
	public boolean sniff(Domain d) {
		String [] datesarray = {"dd/mm/yyyy","dd.mm.yyyy","yyyy.mm.dd","DD/MM/YYYY","MMMM D, YYYY","MMMM-DD-YY",
								"YYYY-MM-DD","D MMMM YYYY","yyyy-mm-dd","d. m. yyyy","yyyymmdd",
								"DD-MM-YYYY","YYYYMMDD","yy/mm/dd","YYYY-MM"," d MMM yyyy","yyyy.MM.dd G 'at' HH:mm:ss z",
								"EEE, MMM d, ''yy","d MMM yyyy HH:mm:ss","yyyy-MM-dd E HH:mm:ss",
								"yyyy-MM-ddEHH:mm:ss"};
		
		boolean flag = false;
		for(String currDate :  datesarray){
			Date date = new Date();
			
			for (DomainEntry de : d.getEntries()){
				String entryVal = de.toString();
				SimpleDateFormat sdf = new SimpleDateFormat(currDate);
				try {
					date = sdf.parse(entryVal);
					if(date != null){
						flag= true ;
						break;
					}
					} catch (ParseException e) {
					// TODO Auto-generated catch block
					flag = false ;
					//e.printStackTrace();
				}
			}
			if(flag == true){
				break;
			}
			
		}
		
		return flag;
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return "date";
	}

}
