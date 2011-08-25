package ac.technion.iem.ontobuilder.gui.utils.files.text;

import java.io.File;

import javax.swing.JComponent;
import javax.swing.JEditorPane;

import ac.technion.iem.ontobuilder.gui.utils.files.common.FilePreview;
import ac.technion.iem.ontobuilder.gui.utils.files.common.FileUtilities;

/**
 * <p>Title: TextFilePreviewer</p>
 * Implements {@link FilePreview}
 */
public class TextFilePreviewer implements FilePreview
{
    protected JEditorPane page;

    /**
     * Constructs a default TextFilePreviewer
     */
    public TextFilePreviewer()
    {
        page = new JEditorPane();
        page.setEditable(false);
    }

    /**
     * Get the JComponent of this TextFilePreviewer
     */
    public JComponent getComponent(File f)
    {
        try
        {
            if (!isFileSupported(f))
                return null;
            page.setContentType("text/text");
            page.setPage(f.toURI().toURL());
            return page;
        }
        catch (Exception e)
        {
            return null;
        }
    }

    /**
     * Checks if a file extension is acceptable (txt)
     * 
     * @return true if it is acceptable
     */
    public boolean isFileSupported(File f)
    {
        String extension = FileUtilities.getFileExtension(f);
        return extension != null && extension.equals("txt");
    }
}
