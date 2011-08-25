package ac.technion.iem.ontobuilder.gui.tools.topk;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import ac.technion.iem.ontobuilder.gui.tools.utils.FileExtentionUtils;

/**
 * <p>Title: TopKFileFilter</p>
 * Extends {@link FileFilter}
 */
public class TopKFileFilter extends FileFilter
{

    /**
     * Accept all directories and all XML files.
     */
    public boolean accept(File f)
    {
        if (f.isDirectory())
        {
            return true;
        }
        String extension = FileExtentionUtils.getExtension(f);
        if (extension != null)
        {
            if (extension.equals(FileExtentionUtils.XML))
            {
                return true;
            }
            else
            {
                return false;
            }

        }
        return false;
    }

    /**
     * The description of this filter
     */
    public String getDescription()
    {
        return "XML Files";
    }
}
