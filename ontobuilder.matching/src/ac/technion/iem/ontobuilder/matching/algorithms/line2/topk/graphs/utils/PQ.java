package ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.utils;

import java.util.Comparator;
import java.util.Hashtable;

/**
 * <p>Title: PQ</p>
 * <p>Description: a generic PQ</p>
 * Extends {@link Queue}
 */
public class PQ extends Queue
{
    private static final long serialVersionUID = 7916451968595825664L;

    private Comparator<Object> pqComperator;
    private Hashtable<Object, PQItem> items = new Hashtable<Object, PQItem>();

    /**
     * Constructs a PQ
     * 
     * @param pqComperator the {@link Comparator}
     */
    public PQ(Comparator<Object> pqComperator)
    {
        this.pqComperator = pqComperator;
    }

    /**
     * Insert an object to the priority queue and places it in the correct position according to the
     * comparator
     * 
     * @param obj the object to insert
     * @param priority priority of the object
     */
    public void insert(Object obj, Object priority)
    {// O(items)
        PQItem item = new PQItem(obj, priority);
        int index = 0;
        while (index < size() &&
            pqComperator.compare(((PQItem) get(index)).getPriority(), item.getPriority()) == -1)
            index++;
        add(index, item);
        items.put(item.getObject(), item);
    }

    /**
     * Change the priority of the object to the new one
     * 
     * @param obj the object which's priority needs to be changed
     * @param newPriority the new priority
     */
    public void decreaseP(Object obj, Object newPriority) // O(items)
    {
        delete(obj);
        insert(obj, newPriority);
    }

    /**
     * Delete the minimum object in the queue
     * 
     * @return the object that was deleted
     */
    public Object deleteMin() // O(1)
    {
        return ((PQItem) pop()).getObject();
    }

    /**
     * Delete the first occurrence of the specifiedobject from the queue, if it is present
     * 
     * @param obj the object to remove
     */
    public void delete(Object obj) // O(items)
    {
        PQItem item = (PQItem) items.get(obj);
        if (item != null)
            remove(item);
    }

    /**
     * Check if the object exists
     * 
     * @param obj the object to check
     * @return <code>true</code> if it exists, else return <code>false</code>
     */
    public boolean member(Object obj) // O(1)
    {
        return items.containsKey(obj);
    }

    /**
     * Reset resources
     */
    public void nullify()
    {
        try
        {
            items = null;
            pqComperator = null;
        }
        catch (NullPointerException e)
        {
        }
    }
}