package ac.technion.iem.ontobuilder.gui.tools.sitemap;

import java.net.URL;

/**
 * <p>Title: URLVisit</p>
 * <p>Description: Implements a visit to a URL</p>
 */
public class URLVisit
{
    public URL url;
    public int depth;

    public URLVisit(URL url, int depth)
    {
        this.url = url;
        this.depth = depth;
    }
}
