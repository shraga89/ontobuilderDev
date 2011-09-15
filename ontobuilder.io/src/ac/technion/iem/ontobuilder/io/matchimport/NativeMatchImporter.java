/**
 * 
 */
package ac.technion.iem.ontobuilder.io.matchimport;

import java.io.File;

import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.meta.match.MatchMatrix;
import ac.technion.iem.ontobuilder.matching.utils.SchemaMatchingsUtilities;
import ac.technion.iem.ontobuilder.matching.utils.SchemaTranslator;

/**
 * @author Tomer Sagi
 * Importer for Ontobuilder native match format (xml). 
 * This class is actually a shell for SchemaMatchingsUtilities.readXMLBestMatchingFile
 * the 
 */
public class NativeMatchImporter implements MatchImporter {

	/* (non-Javadoc)
	 * @see ac.technion.iem.ontobuilder.io.matchimport.MatchImporter#importMatch(java.io.File)
	 */
	@Override
	public MatchInformation importMatch(MatchInformation mi, File file) {
		try {
			MatchInformation res = mi.clone();
			SchemaTranslator st = SchemaMatchingsUtilities.readXMLBestMatchingFile(file.getAbsolutePath(),mi.getMatrix());
			res.setMatches(st.toOntoBuilderMatchList(mi.getMatrix()));
			MatchMatrix newMM = new MatchMatrix(); 
			newMM.copyWithEmptyMatrix(mi.getMatrix());
			for (Match m : res.getMatches())
				newMM.setMatchConfidence(m.getCandidateTerm(), m.getTargetTerm(), m.getEffectiveness());
			res.setMatrix(new MatchMatrix());
			return res;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
