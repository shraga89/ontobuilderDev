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
    public MatchInformation importMatch(File file);
}
