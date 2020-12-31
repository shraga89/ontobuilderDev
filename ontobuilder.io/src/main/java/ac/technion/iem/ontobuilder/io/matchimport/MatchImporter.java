package ac.technion.iem.ontobuilder.io.matchimport;

import java.io.File;

import ac.technion.iem.ontobuilder.matching.match.MatchInformation;

/**
 * <p>Title: interface Match Importer</p>
 * <p>Description:  The Match Importer interface</p>
 * In order to implement a Match importer, you should implement this interface.
 */
public interface MatchImporter
{
	/**
	 * Imports match information from file according to ontology structure in mi
	 * @param empty MatchInformation object to be used for Ontology information
	 * @param file
	 * @return
	 */
    public MatchInformation importMatch(MatchInformation mi, File file);
}
