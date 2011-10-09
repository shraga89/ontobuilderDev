package ac.technion.iem.ontobuilder.io.utils.xml.schema;

import java.util.ArrayList;

/**
 * <p>Title: XSDType</p>
 */
public class XSDComplexType
{

    public static final String XSD_STRING = "xs:String";
    public static final String XSD_INTEGER = "xs:Integer";
    public static final String XSD_DECIMAL = "xs:decimal";
    public static final String XSD_BOOLEAN = "xs:Boolean";
    public static final String XSD_DATE = "xs:Date";
    public static final String XSD_TIME = "xs:Time";
    public static final String XSD_COMPLEX = "xs:ComplexElement";

    private String typeName; //name of complex type
    //private Class<?> typeClass; 
    private XSDComplexType baseType; //optional, used for extension
    private ArrayList<XSDElement> elements; //used to list the contained simple elements in this complex type

    public XSDComplexType(String typeName,XSDComplexType baseType)
    {
    	if (typeName.startsWith("xs:")) {
        	typeName = "xs:" + typeName.substring(3);
		} else if (typeName.startsWith("xsd:")) {
			typeName = "xs:" + typeName.substring(4);
		}
    	
    	this.typeName = typeName;
    	this.baseType = baseType;
//        if (typeName.equals(XSD_STRING))
//            typeClass = String.class;
//        else if (typeName.equals(XSD_INTEGER))
//            typeClass = Integer.class;
//        else if (typeName.equals(XSD_DECIMAL))
//            typeClass = BigDecimal.class;
//        else if (typeName.equals(XSD_BOOLEAN))
//            typeClass = Boolean.class;
//        else if (typeName.equals(XSD_DATE))
//            typeClass = Date.class;
//        else if (typeName.equals(XSD_TIME))
//            typeClass = Time.class;
//        else if (typeName.equals(XSD_COMPLEX))
//            typeClass = XSDComplexElement.class;
    }

    /**
     * @return Returns the baseType
     */
    public XSDComplexType getBaseType()
    {
        return baseType;
    }

    /**
     * @param baseType The baseType to set.
     */
    public void setBaseType(XSDComplexType baseType)
    {
        this.baseType = baseType;
    }

    /**
     * @return Returns the typeClass.
     */
//    public Class<?> getTypeClass()
//    {
//        return typeClass;
//    }

    /**
     * @param typeClass The typeClass to set.
     */
//    public void setTypeClass(Class<?> typeClass)
//    {
//        this.typeClass = typeClass;
//    }

    /**
     * @return Returns the typeName.
     */
    public String getTypeName()
    {
        return typeName;
    }

    /**
     * @param typeName The typeName to set.
     */
    public void setTypeName(String typeName)
    {
        this.typeName = typeName;
    }
    
    public void addElement(XSDElement e)
    {
    	this.elements.add(e);
    }
    
    public ArrayList<XSDElement> getElements()
    {
    	if (this.baseType != null)
    	{
    		ArrayList<XSDElement> res = new ArrayList<XSDElement>();
    		res.addAll(baseType.getElements());
    		res.addAll(elements);
    		return res;
    	}
    	else
    	{
    		return elements;
    	}
    }
}
