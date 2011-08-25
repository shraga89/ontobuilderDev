package ac.technion.iem.ontobuilder.gui.tools.sitemap.event;

import java.util.EventObject;
import java.net.URL;

/**
* <p>Title: URLVisitedEvent</p>
* <p>Description: EventObject</p>
 */
public class URLVisitedEvent extends EventObject
{
    private static final long serialVersionUID = -3267336097772327393L;

    private URL url;
    private int depth;

    public URLVisitedEvent(Object source, URL url, int depth)
    {
        super(source);
        this.url = url;
        this.depth = depth;
    }

    /**
     * Returns the URL
     */
    public URL getURL()
    {
        return url;
    }

    /**
     * Returns the depth
     */
    public int getDepth()
    {
        return depth;
    }
}
