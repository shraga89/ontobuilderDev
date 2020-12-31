package ac.technion.iem.ontobuilder.core.utils.files;

import java.io.IOException;
import java.io.OutputStream;

/**
 * <p>
 * Title: StringOutputStream
 * </p>
 * Extends {@link OutputStream}
 */
public class StringOutputStream extends OutputStream
{
    private StringBuffer buffer;

    public StringOutputStream()
    {
        buffer = new StringBuffer();
    }

    public StringOutputStream(int initialSize)
    {
        buffer = new StringBuffer(initialSize);
    }

    public void write(int param) throws IOException
    {
        buffer.append((char) param);
    }

    public void write(String str)
    {
        buffer.append(str);
    }

    public void write(String str, int off, int len)
    {
        buffer.append(str.substring(off, off + len));
    }

    public String toString()
    {
        return buffer.toString();
    }

    public StringBuffer getBuffer()
    {
        return buffer;
    }
}
