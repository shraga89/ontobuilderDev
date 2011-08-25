package ac.technion.iem.ontobuilder.io.exports;

/**
 * <p>Title: ExportException</p>
 * Extends {@link Exception}
 * @author haggai
 */
public class ExportException extends Exception
{

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a default ExportException
     */
    public ExportException()
    {
        super();
    }

    /**
     * Constructs an ExportException
     * 
     * @param arg0 the string
     */
    public ExportException(String arg0)
    {
        super(arg0);
    }

    /**
     * Constructs an ExportException
     * 
     * @param arg0 the string
     * @param arg1 the throwable
     */
    public ExportException(String arg0, Throwable arg1)
    {
        super(arg0, arg1);
    }

    /**
     * Constructs an ExportException
     * 
     * @param arg0 the throwable
     */
    public ExportException(Throwable arg0)
    {
        super(arg0);
    }
}
