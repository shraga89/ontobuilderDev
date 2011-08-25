package ac.technion.iem.ontobuilder.gui.utils.files.image;

import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.utils.files.common.ExtensionFileFilter;

/**
 * <p>Title: ImageFileFilter</p>
 * Extends {@link ExtensionFileFilter}
 */
public class ImageFileFilter extends ExtensionFileFilter
{
    protected static ImageFileFilter imageFileFilter;

    /**
     * Checks if a file extension is acceptable (jpg or gif)
     * 
     * @return true if it is acceptable
     */
    public boolean isExtensionAcceptable(String extension)
    {
        return extension != null && (extension.equals("jpg") || extension.equals("gif"));
    }

    /**
     * Get the description
     */
    public String getDescription()
    {
        return ApplicationUtilities.getResourceString("file.imgFilter.description");
    }

    /**
     * Build an ImageFileFilter 
     *
     * @return an ImageFileFilter
     */
    public static ImageFileFilter buildImageFileFilter()
    {
        if (imageFileFilter == null)
            imageFileFilter = new ImageFileFilter();
        return imageFileFilter;
    }
}
