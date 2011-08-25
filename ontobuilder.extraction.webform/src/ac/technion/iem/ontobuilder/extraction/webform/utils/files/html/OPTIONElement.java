package ac.technion.iem.ontobuilder.extraction.webform.utils.files.html;


/**
 * <p>Title: OPTIONElement</p>
 * Extends {@link INPUTElement}
 */
public class OPTIONElement extends INPUTElement
{
    private boolean defaultSelected;
    private boolean selected;
    private String value;
    private String label;
    private SELECTElement select;

    public OPTIONElement()
    {
        super(INPUTElement.OPTION);
    }

    public OPTIONElement(String value)
    {
        this();
        this.value = value;
    }

    public boolean isDefaultSelected()
    {
        return defaultSelected;
    }

    public void setDefaultSelected(boolean b)
    {
        defaultSelected = b;
        selected = b;
    }

    public boolean isSelected()
    {
        return selected;
    }

    public void setSelected(boolean b)
    {
        selected = b;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public String getLabel()
    {
        return label;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        if (value == null)
            return label;
        else
            return value;
    }

    public void setSelect(SELECTElement select)
    {
        this.select = select;
        this.name = select.name;
    }

    public SELECTElement getSelect()
    {
        return select;
    }

    public String toString()
    {
        return value + (selected ? " * " : "");
    }

    public void reset()
    {
        setSelected(defaultSelected);
    }
}