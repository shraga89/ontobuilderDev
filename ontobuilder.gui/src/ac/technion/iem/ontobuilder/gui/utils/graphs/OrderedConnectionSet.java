package ac.technion.iem.ontobuilder.gui.utils.graphs;

import java.util.*;
import com.jgraph.graph.*;

/**
 * <p>Title: OrderedConnectionSet</p>
 * Extends {@link ConnectionSet}
 */
public class OrderedConnectionSet extends ConnectionSet
{
    private static final long serialVersionUID = 1L;

    /** Set of changed edges for the connection set. */
    protected ArrayList<Object> edges = new ArrayList<Object>();

    /**
     * Connect <code>edge</code> to <code>port</code>. <code>source</code> indicates if
     * <code>port</code> is the source of <code>edge</code>. The previous connections between
     * <code>edge</code> and its source or target in the set is replaced.
     */
    @SuppressWarnings("unchecked")
    public void connect(Object edge, Object port, boolean source)
    {
        Connection c = new Connection(edge, port, source);
        connections.remove(c);
        connections.add(c);
        if (!edges.contains(edge))
            edges.add(edge);
    }

    /**
     * Disconnect <code>edge</code> from <code>port</code>. <code>source</code> indicates if
     * <code>port</code> is the source of <code>edge</code>. The previous connections between
     * <code>edge</code> and its source or target in the set is replaced.
     */
    @SuppressWarnings("unchecked")
    public void disconnect(Object edge, boolean source)
    {
        connections.add(new Connection(edge, null, source));
        if (!edges.contains(edge))
            edges.add(edge);
    }

    /**
     * Returns a <code>Set</code> for the edges in this connection set.
     */
    public Set<?> getChangedEdges()
    {
        return new HashSet<Object>();
    }
}