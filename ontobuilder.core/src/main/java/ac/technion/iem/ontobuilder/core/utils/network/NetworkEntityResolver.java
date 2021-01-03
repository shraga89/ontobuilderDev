package ac.technion.iem.ontobuilder.core.utils.network;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * <p>Title: NetworkEntityResolver</p>
 * Implements {@link EntityResolver}
 */
public class NetworkEntityResolver implements EntityResolver
{
    /**
     * @deprecated
     */
    public InputSource resolveEntity(String publicId, String systemId) throws IOException
    {
        // InputStream entityStream=getClass().getResourceAsStream("/" + systemId);
        // return new InputSource(entityStream);
//        String filtered;
//        if (systemId.indexOf("file:/") != -1)
//        {
//            filtered = systemId.substring(6, systemId.length());
//
//        }
//        else
//        {
//            filtered = systemId;
//        }
//        InputStream entityStream = getClass().getResourceAsStream(filtered);
//        InputStream entityStream = new FileInputStream(filtered);
        InputStream entityStream;
//        String[] arr = systemId.split(File.separator);
//		String file = arr[arr.length-1];
		String file = systemId.substring(systemId.lastIndexOf('/')+1);
		entityStream = getClass().getClassLoader().getResourceAsStream(file);
		return new InputSource(entityStream);
    }
}
