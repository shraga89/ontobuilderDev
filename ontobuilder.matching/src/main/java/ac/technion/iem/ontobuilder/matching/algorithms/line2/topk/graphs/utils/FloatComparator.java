package ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.utils;


/**
 * <p>Title: FloatComparator</p>
 * Implements {@link Comparator}
 * @author Omer Ben-Porat
 */
import java.util.Comparator;



public class FloatComparator implements Comparator<Object> {

	/**
     * Compares the two objects
     * 
     * @return 1 if the first object is smaller
     * @return 0 if equal
     * @return -1 if bigger
     */
	public int compare(Object f1, Object f2)
    {
        return ((Float) f1).compareTo((Float) f2);
    }

    /** 
     * equals just being implemented without using it 
     *@deprecated 
     */
	
    public boolean equals(Float f)
    {
        return true;
    }

}
