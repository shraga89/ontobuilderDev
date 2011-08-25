package ac.technion.iem.ontobuilder.extraction.webform.utils.files.html;

import java.io.UnsupportedEncodingException;

/**
 * <p>Title: FileINPUTElement</p>
 * Extends {@link INPUTElement}
 */
public class FileINPUTElement extends INPUTElement
{
    protected String label;
    protected int size = -1;
    protected int maxLength = -1;
    protected boolean readOnly;
    protected String value;

    public FileINPUTElement()
    {
        super(INPUTElement.FILE);
    }

    public FileINPUTElement(String name)
    {
        this();
        this.name = name;
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
            return name + "=" + java.net.URLEncoder.encode(value, "UTF-8");
        }
        // TODO: Handle the exception better
        catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void reset()
    {
        value = "";
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