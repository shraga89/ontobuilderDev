package ac.technion.iem.ontobuilder.extraction.webform.utils.files.html;

/**
 * <p>Title: CheckboxINPUTElementOption</p>
 * Extends {@link INPUTElement}
 */
public class CheckboxINPUTElementOption extends INPUTElement
{
    protected boolean defaultChecked;
    protected String label;
    protected String value;
    protected boolean selected;
    protected CheckboxINPUTElement checkboxInput;

    public CheckboxINPUTElementOption()
    {
        super(INPUTElement.CHECKBOXOPTION);
    }

    public CheckboxINPUTElementOption(String value)
    {
        this();
        this.value = value;
    }

    public boolean isDefaultChecked()
    {
        return defaultChecked;
    }

    public void setDefaultChecked(boolean b)
    {
        defaultChecked = b;
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
        return value;
    }

    public void setCheckbox(CheckboxINPUTElement checkboxInput)
    {
        this.checkboxInput = checkboxInput;
        this.name = checkboxInput.name;
    }

    public CheckboxINPUTElement getCheckbox()
    {
        return checkboxInput;
    }

    public String toString()
    {
        return value + (selected ? " * " : "");
    }

    public void setDisabled(boolean b)
    {
        super.setDisabled(b);
    }

    public String paramString()
    {
        return (selected ? name + " = " + value : "");
    }

    public void reset()
    {
        selected = defaultChecked;
    }

    public void setChecked(boolean b)
    {
        selected = b;
    }

    public boolean isChecked()
    {
        return selected;
    }
}