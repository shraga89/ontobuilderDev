package ac.technion.iem.ontobuilder.gui.utils.files.image;

import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;

/**
 * <p>Title: GIFImageFileFilter</p>
 * Extends {@link ImageFileFilter}
 */
public class GIFImageFileFilter extends ImageFileFilter
{
	public boolean isExtensionAcceptable(String extension)
	{
		return extension!=null && extension.equals("gif");
	}

	/**
	 * Get the description
	 */
	public String getDescription()
	{
		return ApplicationUtilities.getResourceString("file.gifFilter.description");
	}
	
	/**
	 * Builds an ImageFileFilter
	 *
	 * @return an ImageFileFilter
	 */
	public static ImageFileFilter buildImageFileFilter()
	{
		if(imageFileFilter==null)
			imageFileFilter=new GIFImageFileFilter();
		return imageFileFilter;
	}
}
