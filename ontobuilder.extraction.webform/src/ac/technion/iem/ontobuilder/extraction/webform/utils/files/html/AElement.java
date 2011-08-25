package ac.technion.iem.ontobuilder.extraction.webform.utils.files.html;

import java.net.URL;

/**
 * <p>Title: AElement</p>
 * Extends {@link HTMLElement}
 */
public class AElement extends HTMLElement
{
    private String name;
    private String target;
    private String description;
    private URL url;

    public AElement(String description, URL url)
    {
        super(HTMLElement.A);
        this.description = description;
        this.url = url;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public URL getURL()
    {
        return url;
    }

    public void setURL(URL url)
    {
        this.url = url;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void setTarget(String target)
    {
        this.target = target;
    }

    public String getTarget()
    {
        return target;
    }

    public String toString()
    {
        return description + " : " + (url != null ? url.toExternalForm() : "");
    }
}