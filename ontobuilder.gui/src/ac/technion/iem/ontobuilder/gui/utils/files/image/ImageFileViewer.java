package ac.technion.iem.ontobuilder.gui.utils.files.image;

import java.io.File;
import javax.swing.Icon;
import javax.swing.filechooser.FileView;

import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.utils.files.common.FileUtilities;

/**
 * <p>Title: ImageFileViewer</p>
 * Extends {@link FileView}
 */
public class ImageFileViewer extends FileView
{
    protected static ImageFileViewer imageFileViewer;

    /**
     * Get the icon of the ImageFileViewer, if the file's extension is valid (jpg or gif)
     * 
     * @param f the file
     */
    public Icon getIcon(File f)
    {
        String extension = FileUtilities.getFileExtension(f);
        if (extension != null && (extension.equals("jpg") || extension.equals("gif")))
            return ApplicationUtilities.getImage("imagefile.gif");
        return null;
    }

    /**
     * Build an ImageFileViewer 
     *
     * @return the ImageFileViewer
     */
    public static ImageFileViewer buildImageFileViewer()
    {
        if (imageFileViewer == null)
            imageFileViewer = new ImageFileViewer();
        return imageFileViewer;
    }
}
