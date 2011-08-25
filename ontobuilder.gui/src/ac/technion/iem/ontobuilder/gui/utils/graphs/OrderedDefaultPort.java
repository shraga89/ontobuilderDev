package ac.technion.iem.ontobuilder.gui.utils.graphs;

import java.util.ArrayList;
import java.util.Iterator;
import com.jgraph.graph.*;

/**
 * <p>Title: OrderedConnectionSet</p>
 * Extends {@link DefaultPort}
 */
public class OrderedDefaultPort extends DefaultPort
{
    private static final long serialVersionUID = 1L;

    /** Edges that are connected to the port */
    protected ArrayList<Object> edges = new ArrayList<Object>();

    /**
     * Constructs a vertex that holds a reference to the specified user object.
     * 
     * @param userObject reference to the user object
     */
    public OrderedDefaultPort(Object userObject)
    {
        super(userObject, null);
    }

    /**
     * Adds <code>edge</code> to the list of ports.
     */
    public boolean add(Object edge)
    {
        if (edges.contains(edge))
            return false;
        return edges.add(edge);
    }

    /**
     * Removes <code>edge</code> from the list of ports.
     */
    public boolean remove(Object edge)
    {
        return edges.remove(edge);
    }

    /**
     * Returns an iterator of the edges connected to the port.
     */
    public Iterator<Object> edges()
    {
        return edges.iterator();
    }

    /**
     * Create a clone of the cell. The cloning of the user object is deferred to the
     * cloneUserObject() method.
     * 
     * @return Object a clone of this object.
     */
    public Object clone()
    {
        OrderedDefaultPort c = (OrderedDefaultPort) super.clone();
        c.edges = new ArrayList<Object>();
        return c;
    }
}