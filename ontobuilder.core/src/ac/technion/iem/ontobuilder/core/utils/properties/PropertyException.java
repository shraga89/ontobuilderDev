package ac.technion.iem.ontobuilder.core.utils.properties;

/**
 * <p>Title: PropertyException</p>
 * Extends {@link RuntimeException}
 */
public class PropertyException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    public PropertyException(String msg)
    {
        super(msg);
    }

    public PropertyException()
    {
        super();
    }
}
