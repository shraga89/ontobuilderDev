package ac.technion.iem.ontobuilder.core.utils.files;

import java.io.IOException;
import java.io.InputStream;

/**
 * <p>Title: StringInputStream</p>
 * Extends {@link InputStream}
 */
public class StringInputStream extends InputStream
{
    protected String str;
    protected int mark = 0;
    protected int readlimit = -1;
    protected int currentpos = 0;

    public StringInputStream(String str)
    {
        this.str = str;
    }

    public int available() throws IOException
    {
        return str.length();
    }

    public void close() throws IOException
    {
    }

    public void mark(int readlimit)
    {
        mark = currentpos;
        this.readlimit = readlimit;
    }

    public void reset() throws IOException
    {
        if (readlimit == -1)
            currentpos = 0;
        else if (currentpos - mark > readlimit)
            throw new IOException();
        else
            currentpos = mark;
    }

    public boolean markSupported()
    {
        return true;
    }

    public int read() throws IOException
    {
        if (currentpos >= str.length())
            return -1;
        return (str.charAt(currentpos++));
    }

    public long skip(long n) throws IOException
    {
        if (n <= 0)
            return 0;
        if (currentpos + n < str.length())
        {
            currentpos += n;
            return n;
        }
        else
        {
            int remaining = str.length() - currentpos;
            currentpos = str.length() - 1;
            return remaining;
        }
    }
}