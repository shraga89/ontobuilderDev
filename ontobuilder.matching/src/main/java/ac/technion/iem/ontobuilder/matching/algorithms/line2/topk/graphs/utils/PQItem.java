package ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.utils;

/**
 * <p>Title: PQItem</p>
 * <p>Description: Implementation of an item stored in a priority queue</p>
 */
public class PQItem
{

    private Object obj;
    private Object priority;

    /**
     * Constructs a PQItem with TODO: Doc.
     * 
     * @param obj the object of the item
     * @param priority the priority of the item
     */
    public PQItem(Object obj, Object priority)
    {
        this.obj = obj;
        this.priority = priority;
    }

    /**
     * Get the object
     * 
     * @return the object
     */
    public Object getObject()
    {
        return obj;
    }

    /**
     * Get the priority
     * 
     * @return the priority
     */
    public Object getPriority()
    {
        return priority;
    }
}