package ac.technion.iem.ontobuilder.core.utils;

import java.io.IOException;
import java.io.InputStream;
import org.xml.sax.InputSource;
import org.xml.sax.EntityResolver;

/**
 * <p>Title: AgentEntityResolver</p>
 * Implements {@link EntityResolver}
 */
public class AgentEntityResolver implements EntityResolver
{
    /**
     * @deprecated
     */
    public InputSource resolveEntity(String publicId, String systemId) throws IOException
    {
        InputStream entityStream = getClass().getResourceAsStream("/dtds/" + systemId);
        return new InputSource(entityStream);
    }
}
