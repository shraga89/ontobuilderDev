package ac.technion.iem.ontobuilder.io.exports;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;

/**
 * <p>Title: XmlOntologyExporter</p>
 * Implements {@link Exporter}
 * @author haggai
 */
public class XmlOntologyExporter implements Exporter
{
    /**
     * Exports an ontology into an XML
     * 
     * @param params the parameters to export
     * @param file the file to save the result to
     * @throws ExportException when cannot save the match to an XML
     */
    public void export(HashMap<?, ?> params, File file) throws ExportException
    {
        try
        {
            Ontology ontology = (Ontology) params.get("Ontology");
            ontology.saveToXML(file);
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
