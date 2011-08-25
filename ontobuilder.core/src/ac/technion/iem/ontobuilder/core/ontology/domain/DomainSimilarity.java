package ac.technion.iem.ontobuilder.core.ontology.domain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * <p>Title: DomainSimilarity</p>
 */
public class DomainSimilarity
{
    protected static ArrayList<DomainSimilarityEntry> domainMatrix = new ArrayList<DomainSimilarityEntry>();

    /**
     * Build a domain matrix
     *
     * @param line string including the domain's values
     */
    public static void buildDomainMatrix(String line)
    {
        domainMatrix.clear();

        for (StringTokenizer st = new StringTokenizer(line, ";"); st.hasMoreTokens();)
        {
            try
            {
                String domain = st.nextToken();
                StringTokenizer dst = new StringTokenizer(domain.substring(1, domain.length() - 1),
                    ",");
                String d1 = dst.nextToken();
                String d2 = dst.nextToken();
                double v = 0;
                try
                {
                    v = Double.parseDouble(dst.nextToken());
                }
                catch (NumberFormatException e)
                {
                    continue;
                }

                DomainSimilarityEntry dse = new DomainSimilarityEntry(d1, d2, v);
                for (Iterator<DomainSimilarityEntry> i = domainMatrix.iterator(); i.hasNext();)
                {
                    DomainSimilarityEntry entry = (DomainSimilarityEntry) i.next();
                    if (entry.equals(dse))
                    {
                        entry.setValue(v);
                        dse = null;
                        break;
                    }
                }
                if (dse != null)
                    domainMatrix.add(dse);
            }
            catch (Exception e)
            {
                continue;
            }
        }
    }

    /**
     * Get the domain's similarity
     *
     * @param d1 the first domain
     * @param d2 the second domain
     * @return a DomainSimilarityEntry
     */
    public static double getDomainSimilarity(String d1, String d2)
    {
        DomainSimilarityEntry dse = new DomainSimilarityEntry(d1, d2, 0);
        int index = domainMatrix.indexOf(dse);
        if (index == -1)
            return 0;
        else
            return ((DomainSimilarityEntry) domainMatrix.get(index)).getValue();
    }


    /**
     * Get the list of domain similarity entries
     *
     * @return the domain matrix
     */
    public ArrayList<DomainSimilarityEntry> getDomainMatrix()
    {
        return domainMatrix;
    }
}