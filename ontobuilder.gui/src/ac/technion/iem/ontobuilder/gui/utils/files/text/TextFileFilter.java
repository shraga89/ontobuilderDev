package ac.technion.iem.ontobuilder.gui.utils.files.text;

import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.utils.files.common.ExtensionFileFilter;

/**
 * <p>Title: TextFileFilter</p>
 * Extends {@link ExtensionFileFilter}
 */
public class TextFileFilter extends ExtensionFileFilter
{
    protected static TextFileFilter textFileFilter;

    /**
     * Checks if a file extension is acceptable (txt)
     * 
     * @return true if it is acceptable
     */
    public boolean isExtensionAcceptable(String extension)
    {
        return extension != null && extension.equals("txt");
    }

    /**
     * Get the description
     */
    public String getDescription()
    {
        return ApplicationUtilities.getResourceString("file.txtFilter.description");
    }

    /**
     * Build a TextFileFilter 
     *
     * @return a TextFileFilter
     */
    public static TextFileFilter buildTextFileFilter()
    {
        if (textFileFilter == null)
            textFileFilter = new TextFileFilter();
        return textFileFilter;
    }
}
