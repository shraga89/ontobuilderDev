package ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.utils;

import java.util.Iterator;

/**
 * <p>Title: QueueIterator</p>
 * <p>Description: Class which implements an iterator over a Queue</p>
 * Implements {@link Iterator}
 */
public class QueueIterator implements Iterator<Object>
{
    int size = 0;
    int index = 0;
    Queue queue;

    public QueueIterator(Queue queue)
    {
        this.queue = queue;
        this.size = queue.size();
    }

    public void remove()
    {
        queue.remove(index);
    }

    public Object next()
    {
        return queue.get(index++);
    }

    /**
     * @return <code>true</code> if there is a next item in the queue
     */
    public boolean hasNext()
    {
        return (index < size);
    }
}