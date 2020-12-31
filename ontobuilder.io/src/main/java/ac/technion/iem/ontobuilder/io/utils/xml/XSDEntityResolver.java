/**
 * 
 */
package ac.technion.iem.ontobuilder.io.utils.xml;

import java.io.IOException;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Tomer Sagi
 *
 */
public class XSDEntityResolver implements EntityResolver {

	/* (non-Javadoc)
	 * @see org.xml.sax.EntityResolver#resolveEntity(java.lang.String, java.lang.String)
	 */
	@Override
	public InputSource resolveEntity(String publicId, String systemId)
			throws SAXException, IOException {
		//TODO check what type of resolver is needed for xsd files
		if (systemId.equals("http://www.ibm.com/developerWorks/copyright.xml")) {
            // Return local copy of the copyright.xml file
            return new InputSource("/usr/local/content/localCopyright.xml");
        }
        // If no match, returning null makes process continue normally
		return null;
	}

}
