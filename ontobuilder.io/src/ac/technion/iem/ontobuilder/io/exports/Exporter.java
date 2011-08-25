package ac.technion.iem.ontobuilder.io.exports;

import java.io.File;
import java.util.HashMap;

/**
 * <p>Title: interface Exporter</p>
 */
public interface Exporter
{
    public void export(HashMap<?, ?> params, File file) throws ExportException;

    public abstract String getType();
}
