package ac.technion.iem.ontobuilder.gui.utils.files.ontology;

import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.utils.files.common.ExtensionFileFilter;

/**
 * <p>Title: OntologyFileFilter</p>
 * Extends {@link ExtensionFileFilter}
 */
public class OntologyFileFilter extends ExtensionFileFilter
{
    /**
     * Checks if the extension is acceptable (<code>xml</code> or <code>ont</code>)
     * 
     * @return <code>true</code> if it is acceptable
     */
    public boolean isExtensionAcceptable(String extension)
    {
        return extension != null && (extension.equals("xml") || extension.equals("ont"));
    }

    /**
     * Get the description
     * 
     * @return the description
     */
    public String getDescription()
    {
        return ApplicationUtilities.getResourceString("file.ontologyFilter.description");
    }
}
