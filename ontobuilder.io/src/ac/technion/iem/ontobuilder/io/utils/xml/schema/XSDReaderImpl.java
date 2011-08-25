package ac.technion.iem.ontobuilder.io.utils.xml.schema;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * <p>Title: XSDReaderImpl</p>
 * Extends {@link DefaultHandler}
 * <br>Implements {@link XSDReader}
 */
class XSDReaderImpl extends DefaultHandler implements XSDReader
{

    private static final byte Schema_TAG = 0;
    private static final byte ComplexType_TAG = 1;
    private static final byte ComplexContent_TAG = 2;
    private static final byte Extension_TAG = 3;
    private static final byte Sequence_TAG = 4;
    private static final byte WAIT_FOR_ELEMENT = 6;

    private XMLSchema schema;
    private XSDComplexElement complexElement;
    private XSDSimpleElement simpleElement;
    private ArrayList<XSDElement> sequence;
    private int currentIndex;
    private byte currentState;

    protected XSDReaderImpl()
    {

    }

    public XMLSchema read(File file) throws Exception
    {

        /* Create a new factory that will create the parser. */
        SAXParserFactory spf = SAXParserFactory.newInstance();
        /* Create new SAX parser */
        SAXParser saxParser = spf.newSAXParser();
        /* parse the XML file */
        currentState = Schema_TAG;
        schema = new XMLSchema();
        saxParser.parse(file, this);
        return schema;
    }

    /*
     * ----------------------- ERROR HANDLER METHODS -----------------------
     */
    public final void error(SAXParseException e) throws SAXParseException
    {
        throw e;
    }

    public final void fatalError(SAXParseException e) throws SAXParseException
    {
        error(e);
    }

    public final void warning(SAXParseException e) throws SAXParseException
    {
        error(e);
    }

    public void startElement(String uri, String name, String qualifiedName, Attributes attributes)
        throws SAXException
    {

        // System.out.println("open>> currentState::"+currentState+",qualifiedName::"+qualifiedName);

        switch (currentState)
        {
        case (Schema_TAG):
            currentState = WAIT_FOR_ELEMENT;
            return;
        case (WAIT_FOR_ELEMENT):
            if (qualifiedName.equalsIgnoreCase("xs:complexType"))
            {
                currentIndex = 0;
                currentState = ComplexType_TAG;
                sequence = new ArrayList<XSDElement>();
                complexElement = new XSDComplexElement();
                complexElement.setName(attributes.getValue("name"));
                XSDType type = new XSDType(XSDType.XSD_COMPLEX);
                complexElement.setType(type);
            }

            else if (qualifiedName.equalsIgnoreCase("xs:element"))
            {

            }
            return;
        case (ComplexType_TAG):// ComplexContent
            if (qualifiedName.equalsIgnoreCase("xs:extension"))
            {// in extension
                complexElement.getType().setBaseTypeName(attributes.getValue("base"));
                currentState = Extension_TAG;
            }
            else if (qualifiedName.equalsIgnoreCase("xs:sequence"))
            {
                currentState = Sequence_TAG;
            }
            return;
        case (Extension_TAG)://
            if (qualifiedName.equalsIgnoreCase("xs:sequence"))
            {
                currentState = Sequence_TAG;
            }
            return;
        case (Sequence_TAG)://
            if (qualifiedName.equalsIgnoreCase("xs:element"))
            {
                simpleElement = new XSDSimpleElement();
                simpleElement.setName(attributes.getValue("name"));
                if (attributes.getValue("type") != null)
                {
                    XSDType type = new XSDType(attributes.getValue("type"));
                    simpleElement.setType(type);
                }
                sequence.add(currentIndex++, simpleElement);
            }
            return;

        }
    }

    public void endElement(String uri, String name, String qualifiedName) throws SAXException
    {

        // System.out.println("close >> currentState::"+currentState+",qualifiedName::"+qualifiedName);

        switch (currentState)
        {
        case (Sequence_TAG):
            if (qualifiedName.equalsIgnoreCase("xs:sequence"))
            {
                complexElement.setSequence(sequence);
                currentState = Extension_TAG;
            }
            return;
        case (Extension_TAG):
            currentState = ComplexContent_TAG;
            return;
        case (ComplexContent_TAG):
            currentState = ComplexType_TAG;
            return;
        case (ComplexType_TAG):
            schema.addXSDElement(complexElement);
            currentState = WAIT_FOR_ELEMENT;
            return;

        }

    }
}
