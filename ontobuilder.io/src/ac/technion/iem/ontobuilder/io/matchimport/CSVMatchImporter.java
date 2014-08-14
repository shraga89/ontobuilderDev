/**
 * 
 */
package ac.technion.iem.ontobuilder.io.matchimport;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import au.com.bytecode.opencsv.CSVReader;

/**
 * @author Tomer Sagi
 * Importer for simple csv mapping  
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
//				System.err.println(p.getLeftP() + "," + p.getRightP());
				Term c = candidate.getTermByProvenance(p.getLeftP());
				if (c==null)
					{System.err.println("Attribute with provenenance:" + p.getLeftP() + "not found in " + candidate.getName());
					continue;}
				Term t = target.getTermByProvenance(p.getRightP());
				if (t==null)
				{
					System.err.println("Attribute with provenenance:" + p.getRightP() + "not found in " + target.getName());
					continue;
				}
				res.updateMatch(t,c,p.getConf());
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
	        // nextLine[] is an array of values from the line
	    	list.add( new ProvenancePair(nextLine[0],nextLine[1],Double.parseDouble(nextLine[2])));
	    reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
//		try {
//			readbuffer = new BufferedReader(new FileReader(file.getPath()));
//			while (readbuffer.ready()){
//				strRead=readbuffer.readLine();
//				splitArray = strRead.split(",");
//				if (splitArray.length==3)
//					{
//						double d = Double.parseDouble(splitArray[2].);
//						list.add( new ProvenancePair(splitArray[0],splitArray[1],d));
//					}
//				}
//			readbuffer.close();
//		} catch (FileNotFoundException e1) {
//			e1.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		return list ;
	}

}
