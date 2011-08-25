package ac.technion.iem.ontobuilder.extraction.webform.utils.files.html;


/**
 * <p>Title: RadioINPUTElementOption</p>
 * Extends {@link INPUTElement}
 */
public class RadioINPUTElementOption extends INPUTElement
{
    protected boolean defaultChecked;
    protected boolean selected;
    protected String value;
    protected String label;
    protected RadioINPUTElement radioInput;

    public RadioINPUTElementOption()
    {
        super(INPUTElement.RADIOOPTION);
    }

    public RadioINPUTElementOption(String value)
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

    public void setValue(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public String getLabel()
    {
        return label;
    }

    public void setRadio(RadioINPUTElement radioInput)
    {
        this.radioInput = radioInput;
        this.name = radioInput.name;
    }

    public RadioINPUTElement getRadio()
    {
        return radioInput;
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
        return (selected ? name + "=" + value : "");
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