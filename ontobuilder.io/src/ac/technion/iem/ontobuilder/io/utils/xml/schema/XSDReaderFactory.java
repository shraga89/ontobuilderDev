package ac.technion.iem.ontobuilder.io.utils.xml.schema;

/**
 * <p>Title: XSDReaderFactory</p>
 */
public class XSDReaderFactory
{

    private static XSDReaderFactory instance = new XSDReaderFactory();

    private XSDReaderFactory()
    {
    }

    public XSDReader createXSDReader()
    {

        return new XSDReaderImpl();
    }

    public static XSDReaderFactory instance()
    {
        return instance;
    }
}
