package ac.technion.iem.ontobuilder.gui.utils.files.image;

import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;

/**
 * <p>Title: JPGImageFileFilter</p>
 * Extends {@link ImageFileFilter}
 */
public class JPGImageFileFilter extends ImageFileFilter
{
    /**
     * Checks if a file extension is acceptable (jpg)
     * 
     * @return true if it is acceptable
     */
    public boolean isExtensionAcceptable(String extension)
    {
        return extension != null && extension.equals("jpg");
    }

    /**
     * Get the description
     */
    public String getDescription()
    {
        return ApplicationUtilities.getResourceString("file.jpgFilter.description");
    }

    /**
     * Build a JPGImageFileFilter
     * 
     * @return a JPGImageFileFilter
     */
    public static ImageFileFilter buildImageFileFilter()
    {
        if (imageFileFilter == null)
            imageFileFilter = new JPGImageFileFilter();
        return imageFileFilter;
    }
}
