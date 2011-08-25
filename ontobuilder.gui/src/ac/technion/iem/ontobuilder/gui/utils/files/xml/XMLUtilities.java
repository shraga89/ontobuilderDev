package ac.technion.iem.ontobuilder.gui.utils.files.xml;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileView;

import ac.technion.iem.ontobuilder.gui.utils.files.common.FilePreview;

/**
 * <p>Title: XMLUtilities</p>
 */
public class XMLUtilities
{
    public static FileFilter xmlFileFilter = new XMLFileFilter();
    public static FileView xmlFileViewer = new XMLFileViewer();
    public static FilePreview xmlFilePreviewer = new XMLFilePreviewer();
}
