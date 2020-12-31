package ac.technion.iem.ontobuilder.core.utils.network;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

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
		try
		{
			entityStream = new FileInputStream(new File(new URI(systemId)));
		}
		catch (URISyntaxException e)
		{
			throw new IOException(e);
		}
        return new InputSource(entityStream);
    }
}
