package ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.utils;

import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.Graph;


/**
 * <p>Title: VertexPQ</p>
 * <p>Description: Implements a Vertex priority queue</p>
 * Extends {@link PQ}
 */
public class VertexPQ extends PQ
{

    private static final long serialVersionUID = -2729747096766522864L;

    private Graph g;

    /**
     * Constructs a VertexPQ
     * 
     * @param g a {@link Graph}
     */
    public VertexPQ(Graph g)
    {
        super(new DoubleComparator());
        this.g = g;
    }

    /**
     * Get the graph
     * 
     * @return the {@link Graph}
     */
    public Graph getGraph()
    {
        return g;
    }

    /**
     * Reset resources
     */
    public void nullify()
    {
        try
        {
            g = null;
            super.nullify();
        }
        catch (NullPointerException e)
        {
        }
    }
}