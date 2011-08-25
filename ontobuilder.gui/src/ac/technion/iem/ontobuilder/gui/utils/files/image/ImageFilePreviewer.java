package ac.technion.iem.ontobuilder.gui.utils.files.image;

import java.io.File;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;

import ac.technion.iem.ontobuilder.gui.utils.files.common.FilePreview;
import ac.technion.iem.ontobuilder.gui.utils.files.common.FileUtilities;

/**
 * <p>Title: ImageFilePreviewer</p>
 * Extends {@link FilePreview}
 */
public class ImageFilePreviewer implements FilePreview
{
    /**
     * Constructs a default ImageFilePreviewer
     */
    public ImageFilePreviewer()
    {
    }

    /**
     * Get a component made from the file
     * 
     * @return a JComponent
     */
    public JComponent getComponent(File f)
    {
        return new JLabel("", new ImageIcon(f.getAbsolutePath()), SwingConstants.LEFT);
    }

    /**
     * Check if the file is supported according to its extension (jpg or gif)
     * 
     * @return true if the file is supported
     */
    public boolean isFileSupported(File f)
    {
        String extension = FileUtilities.getFileExtension(f);
        return extension != null && (extension.equals("jpg") || extension.equals("gif"));
    }
}
