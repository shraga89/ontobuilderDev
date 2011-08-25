package ac.technion.iem.ontobuilder.extraction.webform.utils.files.html;

import java.net.URL;

/**
 * <p>Title: FRAMEElement</p>
 * Extends {@link HTMLElement}
 */
public class FRAMEElement extends HTMLElement
{
    private URL src;
    private String name;
    private boolean internal;

    public FRAMEElement(URL src, String name)
    {
        super(HTMLElement.FRAME);
        this.src = src;
        this.name = name;
        internal = false;
    }

    public boolean isInternal()
    {
        return internal;
    }

    public void setInternal(boolean b)
    {
        internal = b;
    }

    public void setSrc(URL src)
    {
        this.src = src;
    }

    public URL getSrc()
    {
        return src;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public String toString()
    {
        return name + " : " + src.toExternalForm();
    }
}