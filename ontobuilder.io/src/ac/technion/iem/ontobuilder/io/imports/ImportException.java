package ac.technion.iem.ontobuilder.io.imports;

/**
 * <p>Title: ImportException</p>
 * Extends {@link Exception}
 */
public class ImportException extends Exception
{

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a ImportException
     *
     * @param message the message
     */
    public ImportException(String message)
    {
        super(message);
    }
}
