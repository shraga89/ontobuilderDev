package ac.technion.iem.ontobuilder.core.utils.properties;

/**
 * <p>Title: OptionException</p>
 * Extends {@link RuntimeException}
 */
public class OptionException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    public OptionException(String msg)
    {
        super(msg);
    }

    public OptionException()
    {
        super();
    }
}