package ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities;

/**
 * <p>
 * Title: NegativeCycleInGraphException
 * </p>
 * <p>
 * Description: an exception thrown by the Floyd Warshall algorithm
 * </p>
 * Extends {@link GraphException}
 * 
 * @author Haggai Roitman
 * @version 1.1
 */

public class NegativeCycleInGraphException extends GraphException
{
    private static final long serialVersionUID = -440674136459085854L;

    /**
     * Negative cycle exception
     */
    public NegativeCycleInGraphException()
    {
        super("Floyd Warshall Algorithm run\nGraph contains negative cycle");
    }
}