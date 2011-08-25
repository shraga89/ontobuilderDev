package ac.technion.iem.ontobuilder.gui.utils.files.common;

import java.io.File;

import ac.technion.iem.ontobuilder.gui.utils.files.text.TextFilePreviewer;

/**
 * <p>Title: GeneralFilePreviewer</p>
 * Extends {@link TextFilePreviewer}
 * @author haggai
 */
public class GeneralFilePreviewer extends TextFilePreviewer
{

    private String extension;

    /**
     * Constructs a GeneralFilePreviewer
     *
     * @param extension the extension
     */
    public GeneralFilePreviewer(String extension)
    {
        this.extension = extension;
    }

    /**
     * Checks if the file is supported according to it's extension
     * 
     * @return if the file is supported
     */
    public boolean isFileSupported(File f)
    {
        String _extension = FileUtilities.getFileExtension(f);
        return _extension != null && _extension.equals(extension);
    }
}
