package ac.technion.iem.ontobuilder.core.utils;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * <p>Title: ArrayListUtilities</p>
 * <p>Description: Internal OntoBuilder utilities</p>
 */
public class ArrayListUtilities
{
    /**
     * Get the intersection of two ArrayLists
     * 
     * @param list1 the first list
     * @param list2 the second list
     * @return an ArrayList
     */
    public static ArrayList<Object> intersection(ArrayList<?> list1, ArrayList<?> list2)
    {
        ArrayList<Object> intersection = new ArrayList<Object>();
        for (Iterator<?> i = list1.iterator(); i.hasNext();)
        {
            Object o = i.next();
            if (list2.contains(o))
                intersection.add(o);
        }
        return intersection;
    }

    /**
     * Get the union of two ArrayLists
     * 
     * @param list1 the first list
     * @param list2 the second list
     * @return an ArrayList
     */
    public static ArrayList<Object> union(ArrayList<?> list1, ArrayList<?> list2)
    {
        ArrayList<Object> union = new ArrayList<Object>();
        for (Iterator<?> i = list1.iterator(); i.hasNext();)
        {
            Object o = i.next();
            if (!union.contains(o))
                union.add(o);
        }
        for (Iterator<?> i = list2.iterator(); i.hasNext();)
        {
            Object o = i.next();
            if (!union.contains(o))
                union.add(o);
        }
        return union;
    }

    /**
     * Get the difference between two ArrayLists
     * 
     * @param list1 the first list
     * @param list2 the second list
     * @return an ArrayList
     */
    public static ArrayList<?> difference(ArrayList<?> list1, ArrayList<?> list2)
    {
        ArrayList<Object> difference = new ArrayList<Object>();
        difference.addAll(list1);
        for (Iterator<?> i = list2.iterator(); i.hasNext();)
        {
            Object o = i.next();
            if (difference.contains(o))
                difference.remove(o);
        }
        return difference;
    }
}