package ac.technion.iem.ontobuilder.gui.tools.exactmapping;


/**
 * <p>Title: ToolsException</p>
 * Extends a {@link Exception}
 */
public class ToolsException extends Exception
{

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a ToolsException
     *
     * @param arg0 the message
     */
    public ToolsException(String message)
    {
        super(message);
    }

    /**
     * Constructs a ToolsException
     *
     * @param arg0 the message
     */
    public ToolsException(String message, Throwable e)
    {
        super(message,e);
    }

}
