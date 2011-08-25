package ac.technion.iem.ontobuilder.gui.utils.files.common;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.Icon;
import javax.swing.filechooser.FileView;

/**
 * <p>Title: FileViewer</p>
 * Extends {@link FileView}
 */
public class FileViewer extends FileView
{
    protected ArrayList<FileView> viewers;

    /**
     * Constructs a default FileViewer
     */
    public FileViewer()
    {
        viewers = new ArrayList<FileView>();
    }

    /**
     * Add a FileView
     * 
     * @param viewer the viewer to add
     */
    public void addViewer(FileView viewer)
    {
        viewers.add(viewer);
    }

    /**
     * Remove a FileView
     * 
     * @param viewer the viewer to remove
     */
    public void removeViewer(FileView viewer)
    {
        viewers.remove(viewer);
    }

    /**
     * Remove all of the FileViews
     */
    public void removeAllViewers()
    {
        viewers.clear();
    }

    /**
     * Get the icon of the FileViewer
     * 
     * @param f the file
     */
    public Icon getIcon(File f)
    {
        for (Iterator<FileView> i = viewers.iterator(); i.hasNext();)
        {
            FileView viewer = i.next();
            Icon icon = viewer.getIcon(f);
            if (icon != null)
                return icon;
        }
        return null;
    }
}
