package ac.technion.iem.ontobuilder.gui.utils.files.ontology;

import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.utils.files.common.ExtensionFileFilter;

/**
 * <p>Title: OntologyXMLFileFilter</p>
 * Extends {@link ExtensionFileFilter}
 */
public class OntologyXMLFileFilter extends ExtensionFileFilter
{
    /**
     * Checks if the extension is acceptable (<code>XML</code>)
     * 
     * @return <code>true</code> if it is acceptable
     */
    public boolean isExtensionAcceptable(String extension)
    {
        return extension != null && extension.equals("xml");
    }

    /**
     * Get the description
     * 
     * @return the description
     */
    public String getDescription()
    {
        return ApplicationUtilities.getResourceString("file.ontologyXMLFilter.description");
    }
}
