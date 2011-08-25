package ac.technion.iem.ontobuilder.io.utils.xml.schema;

/**
 * <p>Title: XSDSimpleElement</p>
 * Implements {@link XSDElement}
 */
public class XSDSimpleElement implements XSDElement
{

    private String name;
    private XSDType type;
    private Object value;

    /**
     * @return Returns the value.
     */
    public Object getValue()
    {
        return value;
    }

    /**
     * @param value The value to set.
     */
    public void setValue(Object value)
    {
        this.value = value;
    }

    /**
     * @return Returns the name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name The name to set.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * @return Returns the type.
     */
    public XSDType getType()
    {
        return type;
    }

    /**
     * @param type The type to set.
     */
    public void setType(XSDType type)
    {
        this.type = type;
    }
}
