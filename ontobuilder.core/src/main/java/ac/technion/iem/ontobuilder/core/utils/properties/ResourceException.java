package ac.technion.iem.ontobuilder.core.utils.properties;

/**
 * <p>Title: ResourceException</p>
 * Extends {@link RuntimeException}
 */
public class ResourceException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    public ResourceException(String msg)
    {
        super(msg);
    }

    public ResourceException()
    {
        super();
    }
}
