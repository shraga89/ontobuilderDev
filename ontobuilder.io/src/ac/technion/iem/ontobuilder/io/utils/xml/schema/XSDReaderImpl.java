package ac.technion.iem.ontobuilder.io.utils.xml.schema;

import java.io.File;
import java.util.ArrayList;
import java.util.Stack;

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
    private static final byte Element_TAG = 5;
    private static final byte WAIT_FOR_ELEMENT = 7;

    private XMLSchema schema;
    private Stack<XSDComplexElement> complexElements = new Stack<XSDComplexElement>();
    
//    private XSDComplexElement complexElement;
//    private XSDSimpleElement simpleElement;
//    private ArrayList<XSDElement> sequence;
//    private int currentIndex;
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
    	if (qualifiedName.startsWith("xs:")) {
			qualifiedName = qualifiedName.substring(3);
		} else if (qualifiedName.startsWith("xsd:")) {
			qualifiedName = qualifiedName.substring(4);
		}
		XSDComplexElement complexElement = null;
		if (!complexElements.empty()) {
			complexElement = complexElements.peek();
		}
		
        switch (currentState)
        {
        case (Schema_TAG):
            currentState = WAIT_FOR_ELEMENT;
            return;
        case (WAIT_FOR_ELEMENT):
//            if (qualifiedName.equalsIgnoreCase("xs:complexType"))
//            {
//                currentIndex = 0;
//                currentState = ComplexType_TAG;
//                sequence = new ArrayList<XSDElement>();
//                complexElement = new XSDComplexElement();
//                complexElement.setName(attributes.getValue("name"));
//                XSDType type = new XSDType(XSDType.XSD_COMPLEX);
//                complexElement.setType(type);
//            }
//
//            else if (qualifiedName.equalsIgnoreCase("xs:element"))
//            {
//
//            }
//            return;
        	if (qualifiedName.equalsIgnoreCase("element")) {
				currentState = Element_TAG;
				addFirstElement(attributes);
			} 
			else if (qualifiedName.equalsIgnoreCase("complexType")) {
				currentState = ComplexType_TAG;
				addFirstElement(attributes);                	
			}
		return;
        case (Element_TAG)://element
			if (qualifiedName.equalsIgnoreCase("complexType")) {
				currentState = ComplexType_TAG;
			}
			else if (qualifiedName.equalsIgnoreCase("element")) {
				addNewElement(attributes, complexElement);  
			}
		return;
        case (ComplexType_TAG):// ComplexContent
//            if (qualifiedName.equalsIgnoreCase("xs:extension"))
//            {// in extension
//                complexElement.getType().setBaseTypeName(attributes.getValue("base"));
//                currentState = Extension_TAG;
//            }
//            else if (qualifiedName.equalsIgnoreCase("xs:sequence"))
//            {
//                currentState = Sequence_TAG;
//            }
//            return;
        	if (qualifiedName.equalsIgnoreCase("extension")) {//in extension
				if (complexElement.getType() != null) {
					complexElement.getType().setBaseTypeName(attributes.getValue("base"));
				}
				currentState = Extension_TAG;   
			}
			else if (qualifiedName.equalsIgnoreCase("sequence")) {
				currentState = Sequence_TAG;
			}
			else if (qualifiedName.equalsIgnoreCase("attribute")) {
				XSDSimpleElement attribute = new XSDSimpleElement();
				attribute.setName(attributes.getValue("name"));
				if (attributes.getValue("type") != null) {
					XSDType type = new XSDType(attributes.getValue("type"));
					attribute.setType(type);
				}
				complexElement.getSequence().add(attribute); 
			}
			else if (qualifiedName.equalsIgnoreCase("element")) {
				currentState = Element_TAG;
				addNewElement(attributes, complexElement);				
			} 
			return;
        case (Extension_TAG)://
//            if (qualifiedName.equalsIgnoreCase("xs:sequence"))
//            {
//                currentState = Sequence_TAG;
//            }
//            return;
        	if (qualifiedName.equalsIgnoreCase("sequence")) {
				currentState = Sequence_TAG;
			}
			if (qualifiedName.equalsIgnoreCase("element")) {
				addNewElement(attributes, complexElement);
			}              
			return;
        case (Sequence_TAG)://
//            if (qualifiedName.equalsIgnoreCase("xs:element"))
//            {
//                simpleElement = new XSDSimpleElement();
//                simpleElement.setName(attributes.getValue("name"));
//                if (attributes.getValue("type") != null)
//                {
//                    XSDType type = new XSDType(attributes.getValue("type"));
//                    simpleElement.setType(type);
//                }
//                sequence.add(currentIndex++, simpleElement);
//            }
//            return;
        	if (qualifiedName.equalsIgnoreCase("element")) {
				addNewElement(attributes, complexElement);
			}  
		if (qualifiedName.equalsIgnoreCase("complexType")) {
			currentState = ComplexType_TAG;
		}
		return;

        }
    }

    private void addFirstElement(Attributes attributes) {
		XSDComplexElement newComplexElement = new XSDComplexElement();
		if (attributes.getValue("name") != null) {
			newComplexElement.setName(attributes.getValue("name"));
		} else if (attributes.getValue("ref") != null) {
			newComplexElement.setName(attributes.getValue("ref"));
		}
		XSDType type;
		if (attributes.getValue("type") != null) {
			type = new XSDType(attributes.getValue("type"));
		} else {
			type = new XSDType(XSDType.XSD_COMPLEX);
		}
		newComplexElement.setType(type);
		ArrayList<XSDElement> sequence = new ArrayList<XSDElement>();
		newComplexElement.setSequence(sequence);
		complexElements.push(newComplexElement);
	}

	private void addNewElement(Attributes attributes, XSDComplexElement complexElement) {
		XSDComplexElement newComplexElement = new XSDComplexElement();
		if (attributes.getValue("name") != null) {
			newComplexElement.setName(attributes.getValue("name"));
		} else if (attributes.getValue("ref") != null) {
			newComplexElement.setName(attributes.getValue("ref"));
		}
		XSDType type;
		if (attributes.getValue("type") != null) {
			type = new XSDType(attributes.getValue("type"));
		} else {
			type = new XSDType(XSDType.XSD_COMPLEX);
		}
		newComplexElement.setType(type);
		ArrayList<XSDElement> sequence = new ArrayList<XSDElement>();
		newComplexElement.setSequence(sequence);

		complexElement.getSequence().add(newComplexElement);
		complexElements.push(newComplexElement);
	}

    public void endElement(String uri, String name, String qualifiedName) throws SAXException
    {

        // System.out.println("close >> currentState::"+currentState+",qualifiedName::"+qualifiedName);

//        switch (currentState)
//        {
//        case (Sequence_TAG):
//            if (qualifiedName.equalsIgnoreCase("xs:sequence"))
//            {
//                complexElement.setSequence(sequence);
//                currentState = Extension_TAG;
//            }
//            return;
//        case (Extension_TAG):
//            currentState = ComplexContent_TAG;
//            return;
//        case (ComplexContent_TAG):
//            currentState = ComplexType_TAG;
//            return;
//        case (ComplexType_TAG):
//            schema.addXSDElement(complexElement);
//            currentState = WAIT_FOR_ELEMENT;
//            return;
    	if (qualifiedName.startsWith("xs:")) {
			qualifiedName = qualifiedName.substring(3);
		} else if (qualifiedName.startsWith("xsd:")) {
			qualifiedName = qualifiedName.substring(4);
		}
		XSDComplexElement pop = null;
		if (qualifiedName.equalsIgnoreCase("element")) {
			if (!complexElements.empty()) {
				pop = complexElements.pop();
			}
		}

		switch (currentState) {
		case (Sequence_TAG):
			if (qualifiedName.equalsIgnoreCase("sequence")) {
				currentState = ComplexType_TAG;
			}
		return;
		case (Extension_TAG):  
			if (qualifiedName.equalsIgnoreCase("extension")) {
				currentState = ComplexType_TAG;
			}
		return;
		case (ComplexContent_TAG):
			currentState = ComplexType_TAG;
		return;
		case (ComplexType_TAG):
			if (complexElements.empty()) {
				schema.addXSDElement(pop);
				currentState = WAIT_FOR_ELEMENT;
			} else if (qualifiedName.equalsIgnoreCase("sequence")) {
				currentState = ComplexType_TAG;
			} else if (qualifiedName.equalsIgnoreCase("complexType")) {
				currentState = Element_TAG;
			}
			/*TODO this isn;t working in cases where the complex type is dangling from the root. 
			* consider previous code (commented out) logic should be that complex types should 
			* extend elements in the ontology so if the complex type is declared after it is used
			* the element using it should be extended with sub elements according to the complexType definition.
			* Consider adding a complex type container in the XMLSchema object and then using it as a class and 
			* to extend the term with other terms at the ontology level after main element parsing. 
			* Explanation of this notion can be found here: http://www.w3schools.com/Schema/schema_complex.asp
			* In the terms of the above-mentioned explanation, this reader can only handle anonymous complex types
			* declared within the complex element and used in it immediately. 
			*/

		return; 
		case (Element_TAG):
			if (complexElements.empty()) {
				schema.addXSDElement(pop);
				currentState = WAIT_FOR_ELEMENT;
			} else {
				currentState = ComplexType_TAG;     		
			}
		return;
		}
	
       

    }
}
