package ac.technion.iem.ontobuilder.gui.utils.files.xml;

import ac.technion.iem.ontobuilder.core.utils.properties.PropertiesHandler;
import ac.technion.iem.ontobuilder.gui.utils.files.common.ExtensionFileFilter;

/**
 * <p>Title: XMLFileFilter</p>
 * Extends {@link ExtensionFileFilter}
 */
public class XMLFileFilter extends ExtensionFileFilter
{
    public boolean isExtensionAcceptable(String extension)
    {
        return extension != null && extension.equals("xml");
    }

    public String getDescription()
    {
        return PropertiesHandler.getResourceString("file.xmlFilter.description");
    }
}
