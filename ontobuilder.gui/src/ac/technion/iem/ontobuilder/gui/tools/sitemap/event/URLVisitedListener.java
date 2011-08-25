package ac.technion.iem.ontobuilder.gui.tools.sitemap.event;

import java.util.EventListener;

/**
* <p>Title: URLVisitedListener</p>
* <p>Description: EventListener</p>
 */
public interface URLVisitedListener extends EventListener
{
    public void urlVisited(URLVisitedEvent e);
}
