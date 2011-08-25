package ac.technion.iem.ontobuilder.gui.utils.files.common;



/**
 * <p>Title: GeneralFileFilter</p>
 * Extends {@link ExtensionFileFilter}
 * @author haggai
 */
public class GeneralFileFilter extends ExtensionFileFilter
{

    /**
     * Constructs a GeneralFileFilter
     *
     * @param extension the extension
     * @param description the description
     */
    public GeneralFileFilter(String extension, String description)
    {
        setExtension(extension);
        setDescription(description);
    }

    /**
     * Check if a file's extension is acceptable
     *
     * @param extension the extension
     * @return true if it is acceptable
     */
    public boolean isExtensionAcceptable(String extension)
    {
        return extension != null && extension.equals(getExtension());
    }

    /**
     * Get the description of this filter
     * 
     * @return the description
     */
    public String getDescription()
    {
        return description;
    }
}
