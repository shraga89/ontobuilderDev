package ac.technion.iem.ontobuilder.io.utils.xml.schema;

import java.math.BigDecimal;
import java.sql.Time;
import java.util.Date;

/**
 * <p>Title: XSDType</p>
 */
public class XSDType
{

    public static final String XSD_STRING = "xs:String";
    public static final String XSD_INTEGER = "xs:Integer";
    public static final String XSD_DECIMAL = "xs:decimal";
    public static final String XSD_BOOLEAN = "xs:Boolean";
    public static final String XSD_DATE = "xs:Date";
    public static final String XSD_TIME = "xs:Time";
    public static final String XSD_COMPLEX = "xs:ComplexElement";

    private String typeName;
    private Class<?> typeClass;
    private String baseTypeName;

    public XSDType(String typeName)
    {
        this.typeName = typeName;
        if (typeName.equals(XSD_STRING))
            typeClass = String.class;
        else if (typeName.equals(XSD_INTEGER))
            typeClass = Integer.class;
        else if (typeName.equals(XSD_DECIMAL))
            typeClass = BigDecimal.class;
        else if (typeName.equals(XSD_BOOLEAN))
            typeClass = Boolean.class;
        else if (typeName.equals(XSD_DATE))
            typeClass = Date.class;
        else if (typeName.equals(XSD_TIME))
            typeClass = Time.class;
        else if (typeName.equals(XSD_COMPLEX))
            typeClass = XSDComplexElement.class;
    }

    /**
     * @return Returns the baseTypeName.
     */
    public String getBaseTypeName()
    {
        return baseTypeName;
    }

    /**
     * @param baseTypeName The baseTypeName to set.
     */
    public void setBaseTypeName(String baseTypeName)
    {
        this.baseTypeName = baseTypeName;
    }

    /**
     * @return Returns the typeClass.
     */
    public Class<?> getTypeClass()
    {
        return typeClass;
    }

    /**
     * @param typeClass The typeClass to set.
     */
    public void setTypeClass(Class<?> typeClass)
    {
        this.typeClass = typeClass;
    }

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
}
