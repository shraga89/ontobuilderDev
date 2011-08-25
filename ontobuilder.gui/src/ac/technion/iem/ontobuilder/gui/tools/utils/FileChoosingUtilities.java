package ac.technion.iem.ontobuilder.gui.tools.utils;

import java.awt.Component;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;

import ac.technion.iem.ontobuilder.gui.tools.topk.TopKFileFilter;
import ac.technion.iem.ontobuilder.gui.tools.topk.TopKFileView;

/**
 * <p>Title: FileChoosingUtilities</p>
 * <p>Description: Handles choosing of files</p>
 */
public final class FileChoosingUtilities
{

    /** Holds chosen file */
    public static File chosenFile = null;

    /** File chooser */
    public static JFileChooser fc = null;

    /** Signals if a file was chosen or not */
    public static boolean fileChoosed = false;

    /**
     * Opens file chooser dialog
     * 
     * @param conponent parent component
     * @param header to print on file chooser title
     */
    public static void openFileChoser(Component conponent, String header)
    {
        openFileChoser(conponent, header, System.getProperty("user.dir"));
    }

    /**
     * Opens file chooser dialog
     * 
     * @param conponent parent component
     * @param header to print on file chooser title
     * @param path of the current directory
     */
    public static void openFileChoser(Component conponent, String header, String path)
    {
        fc = new JFileChooser();
        fc.setCurrentDirectory(new File(path));
        TopKFileView fv = new TopKFileView();
        fv.putIcon("XML", new ImageIcon("images/ontology.gif"));
        fc.setFileFilter(new TopKFileFilter());
        fc.setFileView(fv);
        fc.showDialog(conponent, header);
        chosenFile = fc.getSelectedFile(); // blocking operation
        if (chosenFile != null)
            fileChoosed = true;
        else
            fileChoosed = false;
    }

    public static File getChosenFile()
    {
        return chosenFile;
    }

    /**
     * Signals if file was chosen by user
     * 
     * @return true if file was chosen
     */
    public static boolean isFileChosed()
    {
        return fileChoosed;
    }
}
