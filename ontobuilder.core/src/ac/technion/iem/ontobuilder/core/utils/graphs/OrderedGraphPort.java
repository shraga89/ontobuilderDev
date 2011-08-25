package ac.technion.iem.ontobuilder.core.utils.graphs;

import java.util.ArrayList;
import java.util.Iterator;

public class OrderedGraphPort extends GraphPort
{
	
	protected ArrayList<GraphEdge> edges = new ArrayList<GraphEdge>();

    /**
     * Constructs a vertex that holds a reference to the specified user object.
     * 
     * @param userObject reference to the user object
     */
    public OrderedGraphPort(Object userObject)
    {
        super(userObject);
    }

    /**
     * Adds <code>edge</code> to the list of ports.
     */
    public boolean add(GraphEdge edge)
    {
        if (edges.contains(edge))
            return false;
        return edges.add(edge);
    }

    /**
     * Removes <code>edge</code> from the list of ports.
     */
    public boolean remove(GraphEdge edge)
    {
        return edges.remove(edge);
    }

    /**
     * Returns an iterator of the edges connected to the port.
     */
    public Iterator<GraphEdge> edges()
    {
        return edges.iterator();
    }
	
}
