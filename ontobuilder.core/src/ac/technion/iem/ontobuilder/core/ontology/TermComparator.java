package ac.technion.iem.ontobuilder.core.ontology;

import java.util.*;


/**
 * <p>Title: TermComparator</p>
 * Implements {@link Comparator}
 */
public class TermComparator implements Comparator<Object>
{
    /**
     * Implements a comparator for two terms
     * @param o1 the first {@link Term}
     * @param o2 the second {@link Term} 
     */
    public int compare(Object o1, Object o2)
    {
        Term t1 = (Term) o1;
        Term t2 = (Term) o2;
        if (t1.equals(t2))
            return 0;
        if (t1.precedes(t2))
            return -1;
        else
            return 1;
    }
}