package ac.technion.iem.ontobuilder.gui.utils.files.text;

import java.io.File;

import javax.swing.Icon;
import javax.swing.filechooser.FileView;

import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.utils.files.common.FileUtilities;

/**
 * <p>Title: TextFileViewer</p>
 * Extends {@link FileView} 
 */
public class TextFileViewer extends FileView
{
    protected static TextFileViewer textFileViewer;

    /**
     * Get the icon of the TextFileViewer
     * 
     * @return an icon
     */
    public Icon getIcon(File f)
    {
        String extension = FileUtilities.getFileExtension(f);
        if (extension != null && extension.equals("txt"))
            return ApplicationUtilities.getImage("txtfile.gif");
        return null;
    }

    /**
     * Build a TextFileViewer 
     *
     * @return a TextFileViewer
     */
    public static TextFileViewer buildTextFileViewer()
    {
        if (textFileViewer == null)
            textFileViewer = new TextFileViewer();
        return textFileViewer;
    }
}
