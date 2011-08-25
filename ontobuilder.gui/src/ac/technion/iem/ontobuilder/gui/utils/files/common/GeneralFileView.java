package ac.technion.iem.ontobuilder.gui.utils.files.common;

import java.io.File;

import javax.swing.Icon;
import javax.swing.filechooser.FileView;

import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;

/**
 * <p>Title: GeneralFileView</p>
 * Extends {@link FileView}
 * @author haggai
 */
public class GeneralFileView extends FileView
{

    private String extension;
    private String icon;

    /**
     * Constructs a GeneralFileView
     *
     * @param extension the extension
     * @param icon the name of the icon
     */
    public GeneralFileView(String extension, String icon)
    {
        this.extension = extension;
        this.icon = icon;
    }

    /**
     * Returns the icon of the GeneralFileView
     * 
     * @param f the file to get the icon from
     */
    public Icon getIcon(File f)
    {
        String _extension = FileUtilities.getFileExtension(f);
        if (_extension != null && _extension.equals(extension))
            return ApplicationUtilities.getImage(icon);
        return null;
    }
}
