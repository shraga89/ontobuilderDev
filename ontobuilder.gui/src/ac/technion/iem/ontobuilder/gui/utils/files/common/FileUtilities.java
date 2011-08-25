package ac.technion.iem.ontobuilder.gui.utils.files.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.io.File;
import java.awt.Component;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;


/**
 * <p>Title: FileUtilities</p>
 * <p>Description: Internal file utilities methods</p>
 */
public class FileUtilities
{
    public static final String PREVIEW_PANEL_VISIBLE_PROPERTY = "filepreviewPanelVisible";

    public static JFileChooser fileChooser = new JFileChooser();
    public static FileViewer fileViewer = new FileViewer();
    public static FilePreviewer filePreviewer = new FilePreviewer();

    private static PropertyChangeListener previewListener;

    /**
     * Returns the file's extension
     *
     * @param file the file's name
     * @return the extension
     */
    public static String getFileExtension(String file)
    {
        int index = file.lastIndexOf(".");
        if (index != -1)
            return file.substring(index + 1, file.length()).toLowerCase();
        return null;
    }

    /**
     * Returns the file's extension
     *
     * @param file the file
     * @return the extension
     */
    public static String getFileExtension(File file)
    {
        return getFileExtension(file.getName());
    }

    /**
     * OPens a file dialog
     *
     * @param parent the parent component of the file
     * @return the file
     */
    public static File openFileDialog(Component parent)
    {
        fileChooser.setCurrentDirectory(new File(ApplicationUtilities.getCurrentDirectory()));
        int result = fileChooser.showOpenDialog(parent);
        if (fileChooser.getSelectedFile() != null && result == JFileChooser.APPROVE_OPTION)
        {
            ApplicationUtilities.setCurrentDirectory(fileChooser.getCurrentDirectory()
                .getAbsolutePath());
            return fileChooser.getSelectedFile();
        }
        else
            return null;
    }

    /**
     * Saves a file dialog
     *
     * @param parent the parent component of the file
     * @param file the file to save a dialog to
     * @return the file
     */
    public static File saveFileDialog(Component parent, File file)
    {
        fileChooser.setCurrentDirectory(new File(ApplicationUtilities.getCurrentDirectory()));
        if (file != null)
            fileChooser.setSelectedFile(file);
        int result = fileChooser.showSaveDialog(parent);
        if (fileChooser.getSelectedFile() != null && result == JFileChooser.APPROVE_OPTION)
        {
            ApplicationUtilities.setCurrentDirectory(fileChooser.getCurrentDirectory()
                .getAbsolutePath());
            return fileChooser.getSelectedFile();
        }
        else
            return null;
    }

    public static void configureFileDialogFilters(ArrayList<FileFilter> filters)
    {
        fileChooser.resetChoosableFileFilters();
        for (Iterator<FileFilter> i = filters.iterator(); i.hasNext();)
            fileChooser.addChoosableFileFilter( i.next());
    }

    public static void configureFileDialogViewer(FileViewer viewer)
    {
        fileChooser.setFileView(viewer);
    }

    public static void configureFileDialogPreviewer(final FilePreviewer filePreviewer)
    {
        if (previewListener != null)
            fileChooser.removePropertyChangeListener(previewListener);
        previewListener = null;
        fileChooser.setAccessory(filePreviewer);
        if (filePreviewer == null)
            return;
        fileChooser.addPropertyChangeListener(previewListener = new PropertyChangeListener()
        {
            public void propertyChange(PropertyChangeEvent e)
            {
                if (e.getPropertyName().equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY))
                {
                    filePreviewer.configure((File) e.getNewValue());
                }
            }
        });
    }
}