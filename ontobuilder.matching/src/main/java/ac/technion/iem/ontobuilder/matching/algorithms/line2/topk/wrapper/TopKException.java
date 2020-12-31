package ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.wrapper;


/**
 * <p>
 * Title: TopKException
 * </p>
 * <p>
 * Description: TopKException is thrown when any exception occuers during Top K runtime
 * </p>
 * Extends {@link Exception}
 */
public class TopKException extends Exception
{
    private static final long serialVersionUID = 8166651807434442095L;

    public TopKException(String msg)
    {
        super(msg);
    }
}