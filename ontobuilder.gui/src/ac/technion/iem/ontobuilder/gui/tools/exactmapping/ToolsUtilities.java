package ac.technion.iem.ontobuilder.gui.tools.exactmapping;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import ac.technion.iem.ontobuilder.core.utils.network.NetworkEntityResolver;
import ac.technion.iem.ontobuilder.gui.elements.Tab;
import ac.technion.iem.ontobuilder.io.imports.ImportException;


/**
* <p>Title: ToolsUtilities</p>
* <p>Description: Internal Tools Utilities</p>
 */
public class ToolsUtilities
{

    private static Hashtable<String, ToolMetadata> tools;

    /**
     * Initialize all the tools
     * 
     * @param file the {@link File} with the tools to intialize
     * @throws ToolsException when failed to loaf from the document
     */
    public static void intializeTools(InputStream resourceStream) throws ToolsException
    {
        try
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(resourceStream));
            SAXBuilder builder = new SAXBuilder(true);
            builder.setEntityResolver(new NetworkEntityResolver());
            Document doc = builder.build(reader);
            loadFromDocument(doc);
        }
        catch (Exception e)
        {
            throw new ToolsException(e.getMessage(),e);
        }
    }

    /**
     * Load the tools parameters from the document
     * 
     * @param doc the document to load from
     * @throws ImportException when there's an invalid Importer descriptor
     */
    protected static void loadFromDocument(Document doc) throws ImportException
    {
        tools = new Hashtable<String, ToolMetadata>();
        Element toolsElement = doc.getRootElement();
        // Now create the list of importers
        java.util.List<?> toolsList = toolsElement.getChildren("tool");
        for (Iterator<?> i = toolsList.iterator(); i.hasNext();)
        {
            Element toolElement = (Element) i.next();
            ToolMetadata metadata = new ToolMetadata();
            if (toolElement.getChild("name") != null)
                metadata.setName(toolElement.getChild("name").getText());
            if (toolElement.getChild("classpath") != null)
                metadata.setClasspath(toolElement.getChild("classpath").getText());
            if (toolElement.getChild("icon") != null)
                metadata.setIcon(toolElement.getChild("icon").getText());
            if (toolElement.getChild("longDescription") != null)
                metadata.setLongDescription(toolElement.getChild("longDescription").getText());
            if (toolElement.getChild("shortDescription") != null)
                metadata.setShortDescription(toolElement.getChild("shortDescription").getText());
            if (toolElement.getChild("mnemonic") != null)
                metadata.setMnemonic(toolElement.getChild("mnemonic").getText());
            if (toolElement.getChild("accelerator") != null)
                metadata.setAccelerator(toolElement.getChild("accelerator").getText());

            // validate
            if (!metadata.validate())
                throw new ImportException("Invalid Importer Descriptor");

            tools.put(metadata.getName(), metadata);

        }
    }

    /**
     * Get the tool plug-in
     * 
     * @param name the name of the tool
     * @return the tool tab
     * @throws ToolsException when it's an unsupported tool
     */
    public static Tab getToolPlugin(String name) throws ToolsException
    {
        ToolMetadata metadata = (ToolMetadata) tools.get(name);
        if (metadata == null)
        {
            throw new ToolsException("Unsupported Tool");

        }
        try
        {

            Tab tool = (Tab) Class.forName(metadata.getClasspath()).newInstance();

            return tool;

        }
        catch (Exception e)
        {
            throw new ToolsException("Tool Initiation Failed");
        }
    }

    /**
     * Get the tool metadata
     * 
     * @param name the name of the tool
     * @return the {@link ToolMetadata}
     * @throws ToolsException when it's an unsupported tool
     */
    public static ToolMetadata getToolMetadata(String name) throws ToolsException
    {
        ToolMetadata metadata = (ToolMetadata) tools.get(name);
        if (metadata == null)
            throw new ToolsException("Unsupported Tool");
        return metadata;
    }

    /**
     * Get all the tools metadata
     * 
     * @return an array of {@link ToolMetadata}
     */
    public static ToolMetadata[] getAllToolMetadata()
    {
        ToolMetadata[] _tools = new ToolMetadata[tools.size()];
        Enumeration<ToolMetadata> enumr = tools.elements();
        for (int i = 0; enumr.hasMoreElements(); i++)
        {
            _tools[i] = (ToolMetadata) enumr.nextElement();
        }

        return _tools;
    }
}
