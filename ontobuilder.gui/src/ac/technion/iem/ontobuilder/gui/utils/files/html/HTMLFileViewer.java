package ac.technion.iem.ontobuilder.gui.utils.files.html;

import java.io.File;
import javax.swing.Icon;
import javax.swing.filechooser.FileView;

import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.utils.files.common.FileUtilities;

/**
 * <p>Title: HTMLFileViewer</p>
 * Extends {@link FileView}
 */
public class HTMLFileViewer extends FileView
{
    public Icon getIcon(File f)
    {
        String extension = FileUtilities.getFileExtension(f);
        if (extension != null && (extension.equals("html") || extension.equals("htm")))
            return ApplicationUtilities.getImage("htmlfile.gif");
        return null;
    }
}