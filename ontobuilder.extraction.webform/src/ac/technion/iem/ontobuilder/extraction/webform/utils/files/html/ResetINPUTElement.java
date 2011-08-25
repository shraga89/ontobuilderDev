package ac.technion.iem.ontobuilder.extraction.webform.utils.files.html;

import java.io.UnsupportedEncodingException;

/**
 * <p>Title: ResetINPUTElement</p>
 * Extends {@link INPUTElement}
 */
public class ResetINPUTElement extends INPUTElement
{
	protected String value;

    public ResetINPUTElement()
    {
        super(INPUTElement.RESET);
    }

    public ResetINPUTElement(String name, String value)
    {
        this();
        this.name = name;
        this.value = value;
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

    public void setValue(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }

    public boolean canSubmit()
    {
        return false;
    }
}