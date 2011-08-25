package ac.technion.iem.ontobuilder.gui.tools.utils;

import java.io.File;

/**
 * <p>
 * Title: FileExtentionUtils
 * </p>
 * <p>
 * Description: Used in Filefilter to filter specific file extensions
 * </p>
 * 
 * @author <font face="Brush Script MT"><b>Haggai Roitman</b></font>
 * @version 1.0
 */
public final class FileExtentionUtils
{

    /** Measurements file extension */
    public final static String XML = "xml";
    /** Lab test file extension */
    public final static String LTF = "ltf";

    /**
     * Extracts the file extension from its filename
     * 
     * @param f file
     * @return file extension
     */
    public static String getExtension(File f)
    {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');
        if (i > 0 && i < s.length() - 1)
        {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }
}