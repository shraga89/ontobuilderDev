package ac.technion.iem.ontobuilder.io.exports;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import ac.technion.iem.ontobuilder.core.utils.network.NetworkEntityResolver;


/**
 * <p>Title: ExportUtilities</p>
 * <p>Description: Internal export utilities</p>
 * @author haggai
 */
public class ExportUtilities
{

    private static Hashtable<String, ExporterMetadata> exporters;

    /**
     * Initialize all the exporters
     *
     * @param file the {@link File} with the exporters to intialize
     * @throws ExportException when cannot read from the file
     */
    public static void initializeExporters(File file) throws ExportException
    {
        BufferedReader reader = null;
        try
        {
            reader = new BufferedReader(new FileReader(file));
            // if(reader==null)
            // {
            // String params[]={file.getAbsolutePath()};
            // throw new
            // ExportException(StringUtilities.getReplacedString(PropertiesHandler.getResourceString("error.algorithm.file"),params));
            // }
            SAXBuilder builder = new SAXBuilder(true);
            builder.setEntityResolver(new NetworkEntityResolver());
            Document doc = builder.build(reader);
            loadFromDocument(doc);
        }
        catch (FileNotFoundException e)
        {
            throw new ExportException(e.getMessage());
        }
        catch (JDOMException e)
        {
            throw new ExportException(e.getMessage());
        }
        catch (Exception e)
        {
            throw new ExportException(e.getMessage());
        }
        finally
        {
            if (reader != null)
            {
                try
                {
                    reader.close();
                }
                catch (IOException ignore)
                {
                }
            }
        }
    }

    /**
     * Load the exporters parameters from the document 
     *
     * @param doc the {@link Document} to load from
     * @throws ExportException when there's an invalid Exporter descriptor
     */
    protected static void loadFromDocument(Document doc) throws ExportException
    {
        exporters = new Hashtable<String, ExporterMetadata>();
        Element algorithmsElement = doc.getRootElement();
        // Now create the list of exporters
        java.util.List<?> importersList = algorithmsElement.getChildren("exporter");
        for (Iterator<?> i = importersList.iterator(); i.hasNext();)
        {
            Element importElement = (Element) i.next();
            ExporterMetadata metadata = new ExporterMetadata();
            if (importElement.getChild("key") != null)
                metadata.setKey(importElement.getChild("key").getText());
            if (importElement.getChild("classpath") != null)
                metadata.setClasspath(importElement.getChild("classpath").getText());
            if (importElement.getChild("exClass") != null)
                metadata.setExClass(importElement.getChild("exClass").getText());
            if (importElement.getChild("icon") != null)
                metadata.setIcon(importElement.getChild("icon").getText());
            if (importElement.getChild("type") != null)
                metadata.setType(importElement.getChild("type").getText());
            if (importElement.getChild("extension") != null)
                metadata.setExtension(importElement.getChild("extension").getText());
            if (importElement.getChild("longDescription") != null)
                metadata.setLongDescription(importElement.getChild("longDescription").getText());
            if (importElement.getChild("shortDescription") != null)
                metadata.setShortDescription(importElement.getChild("shortDescription").getText());
            if (importElement.getChild("mnemonic") != null)
                metadata.setMnemonic(importElement.getChild("mnemonic").getText());
            if (importElement.getChild("accelerator") != null)
                metadata.setAccelerator(importElement.getChild("accelerator").getText());

            // validate
            if (!metadata.validate())
                throw new ExportException("Invalid Exporter Descriptor");

            exporters.put(metadata.getKey(), metadata);

        }

    }

    /**
     * Get the exporter plug-in
     *
     * @param key the key of the exporter
     * @return the exporter
     * @throws ExportException
     */
    public static Exporter getExporterPlugin(String key) throws ExportException
    {
        ExporterMetadata metadata = exporters.get(key);
        if (metadata == null)
        {

            // search according to file extension
            key = searchFileType(key);
            if (key == null)
            {
                throw new ExportException("Unsupported Exporter");
            }
            else
            {
                metadata = exporters.get(key);
                if (metadata == null)
                    throw new ExportException("Unsupported Exporter");
            }
        }
        try
        {

            Exporter exporter = (Exporter) Class.forName(metadata.getClasspath()).newInstance();

            return exporter;

        }
        catch (Exception e)
        {
            throw new ExportException("Exporter Initiation Failed");
        }
    }

    /**
     * Search for a specific file type
     *
     * @param extension the file's extension to look for
     * @return the exporter key
     */
    private static String searchFileType(String extension)
    {
        ExporterMetadata[] metadata = getAllExporterMetadata();
        for (int i = 0; i < metadata.length; i++)
        {
            if (metadata[i].getExtension().equalsIgnoreCase(extension))
                return metadata[i].getKey();
        }

        return null;
    }

    /**
     * Get an exporter metadata according to a type 
     *
     * @param type the type 
     * @return the {@link ExporterMetadata}
     * @throws ExportException when an unsupported exporter type is requested
     */
    public static ExporterMetadata getExporterMetadata(String type) throws ExportException
    {
        ExporterMetadata metadata = exporters.get(type);
        if (metadata == null)
            throw new ExportException("Unsupported Exporter Type");
        return metadata;
    }

    /**
     * Get all the exporters metadata
     *
     * @return an array of {@link ExporterMetadata}
     */
    public static ExporterMetadata[] getAllExporterMetadata()
    {
        ExporterMetadata[] _exporters = new ExporterMetadata[exporters.size()];
        Enumeration<ExporterMetadata> enumr = exporters.elements();
        for (int i = 0; enumr.hasMoreElements(); i++)
        {
            _exporters[i] = enumr.nextElement();
        }

        return _exporters;
    }

    /**
     * Get all the exporters information
     *
     * @return a vector with the information
     */
    public static Vector<?> getExportersInfo()
    {
        Vector<Object> types = new Vector<Object>();
        Enumeration<ExporterMetadata> enumr = exporters.elements();
        ExporterMetadata metadata;
        for (int i = 0; enumr.hasMoreElements(); i++)
        {
            metadata = enumr.nextElement();
            types.add(metadata.getClasspath() + "(" + metadata.getType() + ")");
        }
        return types;
    }

}
