package ac.technion.iem.ontobuilder.extraction.webform.utils.files.html;

import java.io.UnsupportedEncodingException;

/**
 * <p>Title: ButtonINPUTElement</p>
 * Extends {@link INPUTElement}
 */
public class ButtonINPUTElement extends INPUTElement
{
	protected String value;

    public ButtonINPUTElement()
    {
        super(INPUTElement.BUTTON);
    }

    public ButtonINPUTElement(String name, String value)
    {
        this();
        this.name = name;
        this.value = value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return this.value;
    }

    public void setDisabled(boolean b)
    {
        super.setDisabled(b);
    }

    public String paramString()
    {
        try
        {
            return name + "=" + java.net.URLEncoder.encode(value, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            // TODO: Handle the exception better.
            throw new RuntimeException(e);
        }
    }

    public boolean canSubmit()
    {
        return false;
    }
}