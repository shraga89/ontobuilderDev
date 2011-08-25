package ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities;


/**
 * <p>
 * Title: GraphException
 * </p>
 * <p>
 * Description: GraphException in graphs package
 * </p>
 * Extends {@link Exception}
 * 
 * @author Haggai Roitman
 * @version 1.1
 */

public class GraphException extends Exception
{

    private static final long serialVersionUID = 815734764103820829L;

    /**
     * @param msg to print
     */
    public GraphException(String msg)
    {
        super("\nGraph Exception Occured at:" + msg);
    }
}