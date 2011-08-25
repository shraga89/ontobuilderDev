package ac.technion.iem.ontobuilder.extraction.webform.utils.files.html;

import java.io.UnsupportedEncodingException;

/**
 * <p>Title: HiddenINPUTElement</p>
 * Extends {@link INPUTElement}
 */
public class HiddenINPUTElement extends INPUTElement
{
    protected String value;
    
    public HiddenINPUTElement()
    {
        super(INPUTElement.HIDDEN);
    }

    public HiddenINPUTElement(String name, String value)
    {
        this();
        this.name = name;
        setValue(value);
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
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
}