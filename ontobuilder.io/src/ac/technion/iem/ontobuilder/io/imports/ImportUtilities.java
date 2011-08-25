package ac.technion.iem.ontobuilder.io.imports;

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
 * <p>Title: ImportUtilities</p>
 * <p>Description: Internal import utilities</p>
 */
public class ImportUtilities
{

    private static Hashtable<String, ImporterMetadata> importers;

    /**
     * Initialize all the importers
     * 
     * @param file the {@link File} with the importers to intialize
     * @throws ImportException when cannot load from the document
     */
    public static void initializeImporters(File file) throws ImportException
    {
        BufferedReader reader = null;
        try
        {
            reader = new BufferedReader(new FileReader(file));
            // if(reader==null)
            // {
            // String params[]={file.getAbsolutePath()};
            // throw new
            // ImportException(StringUtilities.getReplacedString(PropertiesHandler.getResourceString("error.algorithm.file"),params));
            // }
            SAXBuilder builder = new SAXBuilder(true);
            builder.setEntityResolver(new NetworkEntityResolver());
            Document doc = builder.build(reader);
            loadFromDocument(doc);
        }
        catch (FileNotFoundException e)
        {
            throw new ImportException(e.getMessage());
        }
        catch (JDOMException e)
        {
            throw new ImportException(e.getMessage());
        }
        catch (Exception e)
        {
            throw new ImportException(e.getMessage());
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
     * Load the importers parameters from the document
     * 
     * @param doc the {@link Document} to load from
     * @throws ImportException when there's an invalid Importer descriptor
     */
    protected static void loadFromDocument(Document doc) throws ImportException
    {
        importers = new Hashtable<String, ImporterMetadata>();
        Element algorithmsElement = doc.getRootElement();
        // Now create the list of importers
        java.util.List<?> importersList = algorithmsElement.getChildren("importer");
        for (Iterator<?> i = importersList.iterator(); i.hasNext();)
        {
            Element importElement = (Element) i.next();
            ImporterMetadata metadata = new ImporterMetadata();
            if (importElement.getChild("classpath") != null)
                metadata.setClasspath(importElement.getChild("classpath").getText());
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
                throw new ImportException("Invalid Importer Descriptor");

            importers.put(metadata.getType(), metadata);

        }

    }

    /**
     * Get the imported plug-in
     * 
     * @param type the type of the importer
     * @return the importer
     * @throws ImportException when there's an unsupported Importer type
     */
    public static Importer getImporterPlugin(String type) throws ImportException
    {
        ImporterMetadata metadata = (ImporterMetadata) importers.get(type);
        if (metadata == null)
        {

            // search according to file extension
            type = searchFileType(type);
            if (type == null)
            {
                throw new ImportException("Unsupported Importer Type");
            }
            else
            {
                metadata = (ImporterMetadata) importers.get(type);
                if (metadata == null)
                    throw new ImportException("Unsupported Importer Type");
            }
        }
        try
        {

            Importer importer = (Importer) Class.forName(metadata.getClasspath()).newInstance();

            return importer;

        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new ImportException("Importer Initiation Failed");
        }
    }

    /**
     * Search for a specific file type
     * 
     * @param extension the file's extension to look for
     * @return the importer key
     */
    private static String searchFileType(String extension)
    {
        ImporterMetadata[] metadata = getAllImporterMetadata();
        for (int i = 0; i < metadata.length; i++)
        {
            if (metadata[i].getExtension().equalsIgnoreCase(extension))
                return metadata[i].getType();
        }

        return null;
    }

    /**
     * Get an importer metadata according to a type
     * 
     * @param type the type
     * @return the {@link ImporterMetadata}
     * @throws ImportException when there's an unsupported Importer type
     */
    public static ImporterMetadata getImporterMetadata(String type) throws ImportException
    {
        ImporterMetadata metadata = (ImporterMetadata) importers.get(type);
        if (metadata == null)
            throw new ImportException("Unsupported Importer Type");
        return metadata;
    }

    /**
     * Get all the importers metadata
     * 
     * @return an array of {@link ImporterMetadata}
     */
    public static ImporterMetadata[] getAllImporterMetadata()
    {
        ImporterMetadata[] _importers = new ImporterMetadata[importers.size()];
        Enumeration<ImporterMetadata> enumr = importers.elements();
        for (int i = 0; enumr.hasMoreElements(); i++)
        {
            _importers[i] = (ImporterMetadata) enumr.nextElement();
        }

        return _importers;
    }

    /**
     * Get all the importers information
     * 
     * @return a vector with the information
     */
    public static Vector<?> getImportersInfo()
    {
        Vector<Object> types = new Vector<Object>();
        Enumeration<ImporterMetadata> enumr = importers.elements();
        ImporterMetadata metadata;
        for (int i = 0; enumr.hasMoreElements(); i++)
        {
            metadata = (ImporterMetadata) enumr.nextElement();
            types.add(metadata.getClasspath() + "(" + metadata.getType() + ")");
        }
        return types;
    }

}
