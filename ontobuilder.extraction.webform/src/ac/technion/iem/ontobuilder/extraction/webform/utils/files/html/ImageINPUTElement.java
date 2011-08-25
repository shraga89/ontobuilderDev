package ac.technion.iem.ontobuilder.extraction.webform.utils.files.html;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * <p>Title: ImageINPUTElement</p>
 * Extends {@link INPUTElement}
 */
public class ImageINPUTElement extends INPUTElement
{
    protected URL src;
    protected String alt;
    protected int x, y;
    protected boolean pressed;

    public ImageINPUTElement()
    {
        super(INPUTElement.IMAGE);
    }

    public ImageINPUTElement(String name, URL src, String alt)
    {
        this();
        this.name = name;
        setSrc(src);
        this.alt = alt;
    }

    public void setSrc(URL src)
    {
        this.src = src;
    }

    public URL getSrc()
    {
        return src;
    }

    public void setAlt(String alt)
    {
        this.alt = alt;
    }

    public String getAlt()
    {
        return alt;
    }

    public void setDisabled(boolean b)
    {
        super.setDisabled(b);
    }

    public int getX()
    {
        return x;
    }

    public void setX(int x)
    {
        this.x = x;
    }

    public int getY()
    {
        return y;
    }

    public void setY(int y)
    {
        this.y = y;
    }

    public String paramString()
    {
        if (name == null || name.length() == 0)
            return "x=" + x + "&" + "y=" + y;
        try
        {
            return URLEncoder.encode(name, "UTF-8") + ".x=" + x + "&" +
                URLEncoder.encode(name, "UTF-8") + ".y=" + y;
        }
        catch (UnsupportedEncodingException e)
        {
            // TODO: Handle the exception better.
            throw new RuntimeException(e);
        }
    }

    public void clearPressed()
    {
        pressed = false;
    }

    public void setPressed(boolean b)
    {
        pressed = b;
    }

    public boolean canSubmit()
    {
        return super.canSubmit() && pressed;
    }
}