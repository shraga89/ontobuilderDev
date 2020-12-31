package ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.utils;

import java.util.Comparator;

/**
 * <p>Title: DoubleComparator</p>
 * Implements {@link Comparator}
 */
 public class DoubleComparator implements Comparator<Object>
{
    /**
     * Compares the two objects
     * 
     * @return 1 if the first object is smaller
     * @return 0 if equal
     * @return -1 if bigger
     */
    public int compare(Object d1, Object d2)
    {
        return ((Double) d1).compareTo((Double) d2);
    }

    /** equals just being implemented with using it */
    public boolean equals(Double d)
    {
        return true;
    }
}