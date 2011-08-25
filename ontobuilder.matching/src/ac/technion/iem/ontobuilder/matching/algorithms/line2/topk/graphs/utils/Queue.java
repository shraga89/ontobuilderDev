package ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.utils;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * <p>Title: Queue</p>
 * <p>Description: Class which implements a Queue</p>
 * Extends {@link LinkedList}
 */
public class Queue extends LinkedList<Object>
{
    private static final long serialVersionUID = 9070292949141679663L;

    public Queue()
    {
    }

    public void push(Object obj)
    {
        addLast(obj);
    }

    public Object pop()
    {
        return removeFirst();
    }

    public Iterator<Object> iterator()
    {
        return new QueueIterator(this);
    }
}