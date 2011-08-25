package ac.technion.iem.ontobuilder.matching.algorithms.line2.meta;

/**
 * <p>Title: interface MetaAlgorithmsException</p>
 * Extends {@link Exception}
 * @author haggai
 */
public class MetaAlgorithmsException extends Exception
{

    private static final long serialVersionUID = 1L;

    public MetaAlgorithmsException()
    {
        super();
    }

    /**
     * @param message
     */
    public MetaAlgorithmsException(String message)
    {
        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public MetaAlgorithmsException(String message, Throwable cause)
    {
        super(message, cause);
    }

    /**
     * @param cause
     */
    public MetaAlgorithmsException(Throwable cause)
    {
        super(cause);
    }
}
