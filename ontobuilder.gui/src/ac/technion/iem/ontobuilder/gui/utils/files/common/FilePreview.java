package ac.technion.iem.ontobuilder.gui.utils.files.common;

import java.io.File;

import javax.swing.JComponent;

/**
 * <p>Title: interface FilePreview</p>
 */
public interface FilePreview
{
    public JComponent getComponent(File f);

    public boolean isFileSupported(File f);
}
