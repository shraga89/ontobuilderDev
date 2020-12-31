/**
 * 
 */
package ac.technion.iem.ontobuilder.io.matchimport;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Properties;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import com.opencsv.CSVReader;

/**
 * @author Tomer Sagi
 * Importer for simple csv mapping
 * Minimal format: 
 */
public class CSVMatchImporter implements MatchImporter {
	
	/* (non-Javadoc)
	 * @see ac.technion.iem.ontobuilder.io.matchimport.MatchImporter#importMatch(java.io.File)
	 */
	@Override
	public MatchInformation importMatch(MatchInformation mi, File file) {
		try {
			if (file == null || !file.exists()) {
				System.err.println("Mapping file is null or doesn't exist");
				return null;
			}
			Ontology candidate = mi.getCandidateOntology();
			Ontology target = mi.getTargetOntology();
			MatchInformation res = new MatchInformation(candidate,target);
			ArrayList<ProvenancePair> list = readMappingFile(file);
			for (ProvenancePair p : list)
			{
				Term c = candidate.getTermByProvenance(p.getLeftP());
				if (c==null)
				{
					System.err.println("Attribute with provenenance:" + p.getLeftP() + "not found in " + candidate.getName());
					continue;
				}
				Term t = target.getTermByProvenance(p.getRightP());
				if (t==null)
				{
					System.err.println("Attribute with provenenance:" + p.getRightP() + "not found in " + target.getName());
					continue;
				}
				if (p.getElapsed()==-1.0)
					res.updateMatch(t,c,p.getConf());
				else
				{
					Properties props = new Properties();
					props.put("elapsed", p.getElapsed());
					props.put("diff", p.getDiff());
					res.updateMatch(t, c, p.getConf(), props);
				}
					
			}
			
			return res;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Receives .csv file and returns the correspondences in a ProvenancePair array list
	 * @param file
	 * @return
	 */
	private ArrayList<ProvenancePair> readMappingFile(File file) {
		ArrayList<ProvenancePair> list = new ArrayList<ProvenancePair>();
		CSVReader reader = null;
		try {
			reader = new CSVReader(new FileReader(file));
		
	    String [] nextLine;
	    while ((nextLine = reader.readNext()) != null)
	    {    // nextLine[] is an array of values from the line
	    	ProvenancePair p;
	    	p = new ProvenancePair(nextLine[0],nextLine[1],Double.parseDouble(nextLine[2]));
	    	if (nextLine.length==5) //extended match information
	    	{
	    		p.setElapsed(Double.parseDouble(nextLine[3]));
	    		p.setDiff(Double.parseDouble(nextLine[4]));
	    	}
	    
	    	list.add( p);
	    }
	    
	    reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list ;
	}

}
