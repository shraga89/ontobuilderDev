package ac.technion.iem.ontobuilder.gui.tools.sitemap.event;

import java.util.EventListener;

/**
* <p>Title: SiteMapOperationListener</p>
* <p>Description: EventListener</p>
 */
public interface SiteMapOperationListener extends EventListener
{
    public void operationPerformed(SiteMapOperationEvent e);
}
