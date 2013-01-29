/**
 * 
 */
package ac.technion.iem.ontobuilder.io.matchimport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;

/**
 * @author Tomer Sagi
 * Importer for simple pnml csv mapping  
 */
public class PNMLPairMatchImporter implements MatchImporter {
	
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
	 * Receives match file and returns the correspondences in a ProvenancePair array list
	 * @param file
	 * @return
	 */
	private ArrayList<ProvenancePair> readMappingFile(File file) {
		//TODO: skip 2 lines
		BufferedReader readbuffer;
		String strRead;
		String splitArray[];
		ArrayList<ProvenancePair> list = new ArrayList<ProvenancePair>();
		try {
			readbuffer = new BufferedReader(new FileReader(file.getPath()));
			while (readbuffer.ready()){
				strRead=readbuffer.readLine();
				splitArray = strRead.split(",");
				if (splitArray.length==3)
					list.add( new ProvenancePair(splitArray[0],splitArray[1],Double.parseDouble(splitArray[2])));
				}
			readbuffer.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list ;
	}

}