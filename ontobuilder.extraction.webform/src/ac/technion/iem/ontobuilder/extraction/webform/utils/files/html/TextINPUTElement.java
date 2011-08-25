package ac.technion.iem.ontobuilder.extraction.webform.utils.files.html;

import java.io.UnsupportedEncodingException;

/**
 * <p>Title: TextINPUTElement</p>
 * Extends {@link INPUTElement}
 */
public class TextINPUTElement extends INPUTElement
{
    protected String defaultValue;
    protected String label;
    protected String text;
    protected int size = -1;
    protected int maxLength = -1;
    protected boolean readOnly;

    public TextINPUTElement()
    {
        super(INPUTElement.TEXT);
    }

    public TextINPUTElement(String name, String defaultValue)
    {
        this();
        this.name = name;
        setDefaultValue(defaultValue);
    }

    public void setDefaultValue(String defaultValue)
    {
        this.defaultValue = defaultValue;
        this.text = defaultValue;
    }

    public String getDefaultValue()
    {
        return defaultValue;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public String getLabel()
    {
        return label;
    }

    public void setSize(int size)
    {
        this.size = size;
    }

    public int getSize()
    {
        return size;
    }

    public void setMaxLength(int maxLength)
    {
        this.maxLength = maxLength;
    }

    public int getMaxLength()
    {
        return maxLength;
    }

    public void setReadOnly(boolean b)
    {
        readOnly = b;
    }

    public boolean isReadOnly()
    {
        return readOnly;
    }

    public void setDisabled(boolean b)
    {
        super.setDisabled(b);
    }

    public String paramString()
    {
        return name + "=" + encode(text);
    }

    public String encode(String s)
    {
        try
        {
            return java.net.URLEncoder.encode(s, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            // TODO: Handle the exception better.
            throw new RuntimeException(e);
        }
    }

    public void reset()
    {
        text = defaultValue;
    }

    public void setValue(String value)
    {
        text = value;
    }

    public String getValue()
    {
        return text;
    }
}