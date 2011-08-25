package ac.technion.iem.ontobuilder.io.utils.xml.schema;

import java.io.File;

/**
 * <p>Title: interface XSDReader</p>
 */
public interface XSDReader
{

    public XMLSchema read(File file) throws Exception;

}
