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

import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.meta.match.MatchMatrix;
import ac.technion.iem.ontobuilder.matching.meta.match.MatchedAttributePair;
import ac.technion.iem.ontobuilder.matching.utils.SchemaTranslator;

/**
 * @author Tomer Sagi
 * Importer for COMA++ / AMC .mapping standard 
 */
public class MappingMatchImporter implements MatchImporter {
	
	/* (non-Javadoc)
	 * @see ac.technion.iem.ontobuilder.io.matchimport.MatchImporter#importMatch(java.io.File)
	 */
	@Override
	public MatchInformation importMatch(MatchInformation mi, File file) {
		try {
			MatchInformation res = new MatchInformation(mi.getCandidateOntology(),mi.getTargetOntology());
			SchemaTranslator st = readMappingFile(file);
			res.setMatches(st.toOntoBuilderMatchList(mi,true));
			MatchMatrix newMM = res.getMatrix(); 
			for (Match m : res.getMatches())
				newMM.setMatchConfidence(m.getCandidateTerm(), m.getTargetTerm(), m.getEffectiveness());
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
	private SchemaTranslator readMappingFile(File file) {
		BufferedReader readbuffer;
		String strRead;
		String splitArray[];
		ArrayList<String[]> res = new ArrayList<String[]>();
		try {
			readbuffer = new BufferedReader(new FileReader(file.getPath()));
			for (int i=0;i<8;i++)
				readbuffer.readLine(); //skip first 8 lines
			strRead=readbuffer.readLine();
			while (!strRead.substring(0, 2).equals(" +")){
				String resArray[] = new String[3];
				splitArray = strRead.split(" <-> ");
				resArray[0] = splitArray[0].substring(3);
				resArray[1] = splitArray[1].substring(0,splitArray[1].length()-5);
				resArray[2] = splitArray[1].substring(splitArray[1].lastIndexOf(":")+1);
	    		res.add(resArray);
	    		strRead=readbuffer.readLine();
				}
		
			
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		SchemaTranslator st = new SchemaTranslator();
		
		MatchedAttributePair[] pairs = new MatchedAttributePair[res.size()];
		for (int i=0;i<res.size();i++)
		{
			String newPair[] = res.get(i);
			pairs[i]=new MatchedAttributePair(newPair[0],newPair[1],Double.parseDouble(newPair[2]),-1,-1);
		}
		st.setSchemaPairs(pairs );
		return st;
	}

}
