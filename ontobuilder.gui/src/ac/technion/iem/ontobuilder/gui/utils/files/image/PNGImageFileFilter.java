package ac.technion.iem.ontobuilder.gui.utils.files.image;

import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;

/**
 * <p>Title: PNGImageFileFilter</p>
 * Extends {@link ImageFileFilter}
 */
public class PNGImageFileFilter extends ImageFileFilter
{
    /**
     * Checks if a file extension is acceptable (png)
     * 
     * @return true if it is acceptable
     */
	public boolean isExtensionAcceptable(String extension)
	{
		return extension!=null && extension.equals("png");
	}

    /**
     * Get the description
     */
	public String getDescription()
	{
		return ApplicationUtilities.getResourceString("file.pngFilter.description");
	}
	
    /**
     * Build a PNGImageFileFilter 
     *
     * @return a PNGImageFileFilter
     */
	public static ImageFileFilter buildImageFileFilter()
	{
		if(imageFileFilter==null)
			imageFileFilter=new PNGImageFileFilter();
		return imageFileFilter;
	}
}
