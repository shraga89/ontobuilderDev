package ac.technion.iem.ontobuilder.gui.utils.files.common;

import java.io.File;

import javax.swing.filechooser.FileFilter;


/**
 * <p>Title: ExtensionFileFilter</p>
 * Extends {@link FileFilter}
 */
public abstract class ExtensionFileFilter extends FileFilter
{

    protected String extension;
    protected String description;

    /**
     * Set a description
     *
     * @param description the description to set
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * Get the extension
     *
     * @return the extension
     */
    public String getExtension()
    {
        return extension;
    }

    /**
     * Set an extension
     *
     * @param extension the extension to set
     */
    public void setExtension(String extension)
    {
        this.extension = extension;
    }

    /**
     * Check if a file is acceptable (is in the directory and its extension is valid)
     * 
     * @return if file is acceptable
     */
    public boolean accept(File f)
    {
        return f.isDirectory() || isExtensionAcceptable(FileUtilities.getFileExtension(f));
    }

    /**
     * Get the description of this filter
     * 
     * @return the description
     */
    abstract public String getDescription();

    /**
     * Check if a file's extension is acceptable
     *
     * @param extension the extension
     * @return true if it is acceptable
     */
    abstract public boolean isExtensionAcceptable(String extension);
}
