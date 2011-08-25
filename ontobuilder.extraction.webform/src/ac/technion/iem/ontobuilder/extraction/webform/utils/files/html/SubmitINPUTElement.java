package ac.technion.iem.ontobuilder.extraction.webform.utils.files.html;

import java.io.UnsupportedEncodingException;

/**
 * <p>Title: SubmitINPUTElement</p>
 * Extends {@link INPUTElement}
 */
public class SubmitINPUTElement extends INPUTElement
{
    protected boolean pressed;
    protected String value;

    public SubmitINPUTElement()
    {
        super(INPUTElement.SUBMIT);
    }

    public SubmitINPUTElement(String name, String value)
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
        return name + "=" + encode(this.value);
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

    public boolean isPressed()
    {
        return pressed;
    }

    public void forcePressed()
    {
        pressed = true;
    }

    public void clearPressed()
    {
        pressed = false;
    }

    public boolean canSubmit()
    {
        return super.canSubmit() && pressed;
    }

	public void setPressed(boolean b)
	{
		this.pressed = b;
	}
}