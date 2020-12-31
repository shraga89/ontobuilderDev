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
 * Importer for COMA++ / AMC .mapping standard 
 */
public class MappingMatchImporter implements MatchImporter {
	
	private class ProvenancePair{
		/**
		 * @param leftP
		 * @param rightP
		 * @param conf
		 */
		public ProvenancePair(String leftP, String rightP, double conf) {
			this.setLeftP(leftP);
			this.setRightP(rightP);
			this.setConf(conf);
		}
		/**
		 * @return the leftP
		 */
		public String getLeftP() {
			return leftP;
		}
		/**
		 * @param leftP the leftP to set
		 */
		public void setLeftP(String leftP) {
			this.leftP = leftP;
		}
		/**
		 * @return the rightP
		 */
		public String getRightP() {
			return rightP;
		}
		/**
		 * @param rightP the rightP to set
		 */
		public void setRightP(String rightP) {
			this.rightP = rightP;
		}
		/**
		 * @return the conf
		 */
		public double getConf() {
			return conf;
		}
		/**
		 * @param conf the conf to set
		 */
		public void setConf(double conf) {
			this.conf = conf;
		}
		private String leftP;
		private String rightP;
		private double conf;
		
		
		
	}
	/* (non-Javadoc)
	 * @see ac.technion.iem.ontobuilder.io.matchimport.MatchImporter#importMatch(java.io.File)
	 */
	@Override
	public MatchInformation importMatch(MatchInformation mi, File file) {
		try {
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
	 * Receives .mapping file and returns the correspondences in a schema translator object
	 * @param file
	 * @return
	 */
	private ArrayList<ProvenancePair> readMappingFile(File file) {
		BufferedReader readbuffer;
		String strRead;
		String splitArray[];
		ArrayList<ProvenancePair> list = new ArrayList<ProvenancePair>();
		try {
			readbuffer = new BufferedReader(new FileReader(file.getPath()));
			for (int i=0;i<8;i++)
				readbuffer.readLine(); //skip first 8 lines
			strRead=readbuffer.readLine();
			while (!strRead.substring(0, 2).equals(" +")){
				//System.err.println(strRead);
				String resArray[] = new String[3];
				splitArray = strRead.split(" <-> ");
				resArray[0] = splitArray[0].substring(3).trim();
				resArray[1] = splitArray[1].substring(0,splitArray[1].lastIndexOf(":")).trim();
				resArray[2] = splitArray[1].substring(splitArray[1].lastIndexOf(":")+1).trim();
				list.add( new ProvenancePair(resArray[0],resArray[1],Double.parseDouble(resArray[2])));
	    		strRead=readbuffer.readLine();
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
