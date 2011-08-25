package ac.technion.iem.ontobuilder.io.exports;

import java.io.File;
import java.util.HashMap;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;

/**
 * <p>Title: BizTalkOntologyExporter</p>
 * Implements {@link Exporter}
 * @author haggai
 */
public class BizTalkOntologyExporter implements Exporter
{

    /**
     * Exports a BizTalk ontology into a file
     * 
     * @param params the parameters to export
     * @param file the {@link File} to save the result to
     * @throws ExportException when cannot save the match to a BizTalk file
     */
    public void export(HashMap<?, ?> params, File file) throws ExportException
    {
        try
        {
            Ontology ontology = (Ontology) params.get("Ontology");
            ontology.saveToBizTalk(file);
        }
        catch (Exception e)
        {
            throw new ExportException(e);
        }

    }

    /**
     * Get the export type
     * 
     * @return the export type
     */
    public String getType()
    {
        return ExportersTypeEnum.ONTOLOGY.getName();
    }

}
