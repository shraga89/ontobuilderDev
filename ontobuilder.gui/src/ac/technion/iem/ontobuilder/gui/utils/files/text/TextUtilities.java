package ac.technion.iem.ontobuilder.gui.utils.files.text;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileView;

import ac.technion.iem.ontobuilder.gui.utils.files.common.FilePreview;

/**
 * <p>Title: TextUtilities</p>
 * <p>Description: Internal text utilities</p>
 */
public class TextUtilities
{
    public static FileFilter textFileFilter = new TextFileFilter();
    public static FileView textFileViewer = new TextFileViewer();
    public static FilePreview textFilePreviewer = new TextFilePreviewer();
}
