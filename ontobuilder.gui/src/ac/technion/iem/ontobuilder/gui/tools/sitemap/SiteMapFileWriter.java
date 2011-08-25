package ac.technion.iem.ontobuilder.gui.tools.sitemap;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

/**
 * <p>Title: SiteMapFileWriter</p>
 * <p>Description: Writes a site map to a text file</p>
 */
public class SiteMapFileWriter
{

    public static void write(Vector<?> urls) throws IOException
    {
        PrintStream ps = null;
        try
        {
            ps = new PrintStream(new FileOutputStream("sitemap.txt"));
            HashSet<String> filter = new HashSet<String>();
            Iterator<?> it = urls.iterator();
            while (it.hasNext())
            {
                URLVisit url = (URLVisit) it.next();
                String key = url.url.toString();
                if (!filter.contains(key))
                {
                    ps.println(url.url);
                    filter.add(key);
                }
            }
        }
        finally
        {
            if (ps != null)
            {
                ps.close();
            }
        }
    }

}
