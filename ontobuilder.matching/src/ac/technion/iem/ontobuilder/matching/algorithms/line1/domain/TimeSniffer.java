package ac.technion.iem.ontobuilder.matching.algorithms.line1.domain;

import static java.lang.System.out;
import java.lang.Object;
import ac.technion.iem.ontobuilder.core.ontology.Domain;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ac.technion.iem.ontobuilder.core.ontology.DomainEntry;

public class TimeSniffer implements DataTypeSniffer {

	@Override
	public boolean sniff(Domain d) {
		String [] datesarray = {"HH:mm:ss","HH:mm:ss a","HH:mm a","HH:mm"};
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
		
		
		
		//special cases
		if(flag == false){
			String[] spc = {"([1-9]|1[0-9])[-]([1-9]|1[0-9])am","([1-9]|1[0-9])[-]([1-9]|1[0-9])pm",
					"([1-9]|1[0-9])[-]([1-9]|1[0-9])AM","([1-9]|1[0-9])[-]([1-9]|1[0-9])PM",
					"([1-9]|1[0-9])[-]([1-9]|1[0-9])[:]([0-9]|1[0-9]|2[0-9]|3[0-9]|4[0-9]|5[0-9])PM",
					"([1-9]|1[0-9])[-]([1-9]|1[0-9])[:]([0-9]|1[0-9]|2[0-9]|3[0-9]|4[0-9]|5[0-9])AM",
					"([1-9]|1[0-9])[-]([1-9]|1[0-9])[:]([0-9]|1[0-9]|2[0-9]|3[0-9]|4[0-9]|5[0-9])am",
					"([1-9]|1[0-9])[-]([1-9]|1[0-9])[:]([0-9]|1[0-9]|2[0-9]|3[0-9]|4[0-9]|5[0-9])pm",
					"\\w([1-9]|1[0-9])[:]([0-9]|1[0-9]|2[0-9]|3[0-9]|4[0-9]|5[0-9])[-]([1-9]|1[0-9])[:]([0-9]|1[0-9]|2[0-9]|3[0-9]|4[0-9]|5[0-9])",
					"([0-9][0-9][0-9][0-9])-([0-9][0-9][0-9][0-9])pm",
                    "([0-9][0-9][0-9][0-9])-([0-9][0-9][0-9][0-9])am",
                    "([0-9][0-9][0-9][0-9])-([0-9][0-9][0-9][0-9])PM",
                    "([0-9][0-9][0-9][0-9])-([0-9][0-9][0-9][0-9])AM"};
			for (DomainEntry de : d.getEntries()){
				String entryVal = de.toString();
				for(String s :  spc){
					Pattern p = Pattern.compile(s);
					Matcher m = p.matcher(entryVal);
					if (m.matches()){
						flag = true;
						break;
					}
				}
				if(flag){
					break;
				}
			}
		}
		return flag;
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return "time";
	}

}
