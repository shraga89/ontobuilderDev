package ac.technion.iem.ontobuilder.extraction.webform.utils.files.html;

import java.io.UnsupportedEncodingException;

/**
 * <p>Title: PasswordINPUTElement</p>
 * Extends {@link INPUTElement}
 */
public class PasswordINPUTElement extends INPUTElement
{
    protected String defaultValue;
    protected String label;
    protected String value;
    protected int size = -1;
    protected int maxLength = -1;
    protected boolean readOnly;

    public PasswordINPUTElement()
    {
        super(INPUTElement.PASSWORD);
    }

    public PasswordINPUTElement(String name, String defaultValue)
    {
        this();
        this.name = name;
        setDefaultValue(defaultValue);
    }

    public void setDefaultValue(String defaultValue)
    {
        this.defaultValue = defaultValue;
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
        try
        {
            return name + "=" +
                java.net.URLEncoder.encode(new String(value), "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            // TODO: Handle the exception better.
            throw new RuntimeException(e);
        }
    }

    public void reset()
    {
        value = defaultValue;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}