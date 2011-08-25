package ac.technion.iem.ontobuilder.io.exports;

import java.io.File;
import java.util.HashMap;

import ac.technion.iem.ontobuilder.matching.utils.SchemaTranslator;

/**
 * <p>Title: BestMatchingExporter</p>
 * Implements {@link Exporter}
 */
public class BestMatchingExporter implements Exporter
{
    /**
     * Exports a best match into a file
     * 
     * @param params the parameters to export
     * @param file the {@link File} to save the result to
     * @throws ExportException when cannot save the match to an XML
     */
    public void export(HashMap<?, ?> params, File file) throws ExportException
    {
        SchemaTranslator st = (SchemaTranslator) params.get("Matching");
        int index = ((Integer) params.get("index")).intValue();
        String candName = (String) params.get("candName");
        String targetName = (String) params.get("targetName");
        String filepath = (String) params.get("filepath");
        try
        {
            // using SchemaTranslator capabilities to convert to XML format
            st.saveMatchToXML(index, candName, targetName, filepath);
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
        return ExportersTypeEnum.MATCHING.getName();
    }
    
    

}
