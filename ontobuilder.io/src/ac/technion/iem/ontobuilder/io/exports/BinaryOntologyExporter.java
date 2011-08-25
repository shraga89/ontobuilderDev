package ac.technion.iem.ontobuilder.io.exports;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;

/**
 * <p>Title: BinaryOntologyExporter</p>
 * Implements {@link Exporter}
 * @author haggaiF
 */
public class BinaryOntologyExporter implements Exporter
{

    /**
     * Exports a binary ontology into a file
     * 
     * @param params the parameters to export
     * @param file the {@link File} to save the result to
     * @throws ExportException when cannot save the match to an Binary file
     */
    public void export(HashMap<?, ?> params, File file) throws ExportException
    {
        try
        {
            Ontology ontology = (Ontology) params.get("Ontology");
            ontology.saveToBinary(file);
        }
        catch (IOException e)
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
