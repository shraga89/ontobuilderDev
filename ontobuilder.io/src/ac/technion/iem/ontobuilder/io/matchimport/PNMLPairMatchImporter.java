/**
 * 
 */
package ac.technion.iem.ontobuilder.io.matchimport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
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
		BufferedReader readbuffer;
		String strRead;
		String splitArray[];
		ArrayList<ProvenancePair> list = new ArrayList<ProvenancePair>();
		try {
			
			FileInputStream fis = new FileInputStream(file);
			String encoding = determineEncoding(fis);
			InputStreamReader in = new InputStreamReader(fis, encoding);
			readbuffer = new BufferedReader(in);
			
			//skip first two lines that contain the link to the model files
			readbuffer.readLine();
			readbuffer.readLine();
			
			while (readbuffer.ready()){
				strRead=readbuffer.readLine();
				splitArray = strRead.split(",");
				if (splitArray.length==2)
					list.add( new ProvenancePair(splitArray[0].replace('.', ' ').trim(),splitArray[1].replace('.', ' ').trim(), 1.0));
				}
			readbuffer.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list ;
	}

	
	private String determineEncoding(FileInputStream in) throws IOException {
		// check for BOM
		PushbackInputStream pb = new PushbackInputStream(in);
		
		final byte  bom[] = new byte[4];
	    final int   read  = pb.read(bom);

	    switch(read) {
	      case 4:
	        if ((bom[0] == (byte)0xFF) &&
	            (bom[1] == (byte)0xFE) &&
	            (bom[2] == (byte)0x00) &&
	            (bom[3] == (byte)0x00)) {
	          return "UTF-32LE";
	        }
	        else
	        if ((bom[0] == (byte)0x00) &&
	            (bom[1] == (byte)0x00) &&
	            (bom[2] == (byte)0xFE) &&
	            (bom[3] == (byte)0xFF)) {
	          return "UTF-32BE";
	        }

	      case 3:
	        if ((bom[0] == (byte)0xEF) &&
	            (bom[1] == (byte)0xBB) &&
	            (bom[2] == (byte)0xBF)) {
	          return "UTF-8";
	        }

	      case 2:
	        if ((bom[0] == (byte)0xFF) &&
	            (bom[1] == (byte)0xFE)) {
		      return "UTF-16LE";
	        }
	        else
	        if ((bom[0] == (byte)0xFE) &&
	            (bom[1] == (byte)0xFF)) {
	        	return "UTF-16BE";
	        }
	    }
  	  return "ISO-8859-1";
	}
}
