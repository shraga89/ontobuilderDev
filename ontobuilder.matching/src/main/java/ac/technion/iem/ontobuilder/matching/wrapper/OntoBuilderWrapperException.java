package ac.technion.iem.ontobuilder.matching.wrapper;

/**
 * Extends Exception
 */
/**
 * <p>Title: OntoBuilderWrapperException</p>
 * Extends {@link Exception}
 */
public class OntoBuilderWrapperException extends Exception
{
    private static final long serialVersionUID = 847971930616441151L;

    public OntoBuilderWrapperException(String msg)
    {
        super(msg);
    }

    public OntoBuilderWrapperException(String msg, Throwable e)
    {
        super(msg,e);
    }
}