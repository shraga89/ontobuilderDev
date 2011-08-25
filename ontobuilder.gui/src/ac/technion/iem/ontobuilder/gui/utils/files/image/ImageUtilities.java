package ac.technion.iem.ontobuilder.gui.utils.files.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * <p>Title: ImageUtilities</p>
 * <p>Description: Internal image utilities</p>
 */
public class ImageUtilities
{
    public static void saveImageToFile(BufferedImage image, String format, File file)
        throws IOException
    {
        ImageIO.write(image, format, file);
    }
}
