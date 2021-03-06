package ac.technion.iem.ontobuilder.gui.utils.files.xml;

import java.io.File;

import ac.technion.iem.ontobuilder.gui.utils.files.common.FileUtilities;
import ac.technion.iem.ontobuilder.gui.utils.files.text.TextFilePreviewer;

/**
 * <p>Title: XMLFilePreviewer</p>
 * Extends {@link TextFilePreviewer}
 */
public class XMLFilePreviewer extends TextFilePreviewer
{
    public boolean isFileSupported(File f)
    {
        if (f == null)
            return false;
        String extension = FileUtilities.getFileExtension(f);
        return extension != null && extension.equals("xml");
    }
}
