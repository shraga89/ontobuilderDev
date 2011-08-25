package ac.technion.iem.ontobuilder.gui.utils.files.xml;

import java.io.File;

import javax.swing.Icon;
import javax.swing.filechooser.FileView;

import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.utils.files.common.FileUtilities;

/**
 * <p>Title: XMLFileViewer</p>
 * Extends {@link FileView}
 */
public class XMLFileViewer extends FileView
{
    public Icon getIcon(File f)
    {
        String extension = FileUtilities.getFileExtension(f);
        if (extension != null && extension.equals("xml"))
            return ApplicationUtilities.getImage("xmlfile.gif");
        return null;
    }
}
