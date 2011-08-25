package ac.technion.iem.ontobuilder.extraction.webform.utils.files.html;

import java.io.UnsupportedEncodingException;

/**
 * <p>Title: TEXTAREAElement</p>
 * Extends {@link INPUTElement}
 */
public class TEXTAREAElement extends INPUTElement
{
    protected String defaultValue;
    protected String label;
    protected String text;
    protected int rows = -1;
    protected int cols = -1;
    protected boolean readOnly;

    public TEXTAREAElement()
    {
        super(INPUTElement.TEXTAREA);
    }

    public TEXTAREAElement(String name)
    {
        this();
        this.name = name;
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

    public void setRows(int rows)
    {
        this.rows = rows;
    }

    public int getRows()
    {
        return rows;
    }

    public void setCols(int cols)
    {
        this.cols = cols;
    }

    public int getCols()
    {
        return cols;
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
        return name + "=" + encode(this.text);
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
    	this.text = defaultValue;
    }

    public void setValue(String value)
    {
    	this.text = value;
    }

    public String getValue()
    {
        return this.text;
    }
}