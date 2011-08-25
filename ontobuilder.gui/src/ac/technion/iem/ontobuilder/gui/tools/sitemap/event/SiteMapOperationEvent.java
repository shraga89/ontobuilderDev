package ac.technion.iem.ontobuilder.gui.tools.sitemap.event;

import java.util.EventObject;

/**
* <p>Title: SiteMapOperationEvent</p>
* <p>Description: EventObject</p>
 */
public class SiteMapOperationEvent extends EventObject
{
    private static final long serialVersionUID = 5190505916307753066L;

    private short operation;

    public SiteMapOperationEvent(Object source, short operation)
    {
        super(source);
        this.operation = operation;
    }

    public short getOperation()
    {
        return operation;
    }
}
