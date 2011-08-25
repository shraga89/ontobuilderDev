package ac.technion.iem.ontobuilder.gui.utils.files.html;

import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.utils.files.common.ExtensionFileFilter;

/**
 * <p>Title: HTMLFileFilter</p>
 * Extends {@link ExtensionFileFilter}
 */
public class HTMLFileFilter extends ExtensionFileFilter
{
    public boolean isExtensionAcceptable(String extension)
    {
        return extension != null && (extension.equals("html") || extension.equals("htm"));
    }

    public String getDescription()
    {
        return ApplicationUtilities.getResourceString("file.htmlFilter.description");
    }
}
