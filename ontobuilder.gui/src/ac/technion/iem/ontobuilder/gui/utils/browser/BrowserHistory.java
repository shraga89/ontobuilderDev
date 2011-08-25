package ac.technion.iem.ontobuilder.gui.utils.browser;

import java.util.ArrayList;
import java.util.Iterator;
import java.net.URL;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

/**
 * <p>Title: BrowserHistory</p>
 * <p>Description: Manages the navigation and the browsing history</p>
 */
public class BrowserHistory
{
    Node head;
    Node end;
    Node current;
    ArrayList<ChangeListener> listeners;

    /**
     * Constructs a default BrowserHistory
     */
    public BrowserHistory()
    {
        listeners = new ArrayList<ChangeListener>();
        head = end = current = null;
    }

    public void addChangeListener(ChangeListener l)
    {
        listeners.add(l);
    }

    public void removeChangeListener(ChangeListener l)
    {
        listeners.remove(l);
    }

    /**
     * Trigger a state change in the browser history
     */
    protected void fireStateChanged()
    {
        for (Iterator<ChangeListener> i = listeners.iterator(); i.hasNext();)
        {
            ChangeListener l = (ChangeListener) i.next();
            l.stateChanged(new ChangeEvent(this));
        }
    }

    /**
     * Check if the "Back" button can be enabled
     * 
     * @return <code>true</code> if it can be enabled
     */
    public boolean isBackAvailable()
    {
        return (current != null && current.prev != null);
    }

    /**
     * Check if the "Forward" button can be enabled
     * 
     * @return <code>true</code> if it can be enabled
     */
    public boolean isForwardAvailable()
    {
        return (current != null && current.next != null);
    }

    /**
     * Get the URL to go to when clicking "Back"
     * 
     * @return the URL
     */
    public URL back()
    {
        if (current != null && current.prev != null)
        {
            URL url = current.prev.url;
            current = current.prev;
            fireStateChanged();
            return url;
        }
        else
            return null;
    }

    /**
     * Get the URL to go to when clicking "Forward"
     * 
     * @return the URL
     */
    public URL forward()
    {
        if (current != null && current.next != null)
        {
            URL url = current.next.url;
            current = current.next;
            fireStateChanged();
            return url;
        }
        else
            return null;
    }

    /**
     * Navigates to the URL
     * 
     * @param url the URL to navigate to
     */
    public void navigate(URL url)
    {
        Node n = new Node(url);
        if (head == null)
        {
            head = n;
            end = n;
            current = n;
        }
        else
        {
            n.prev = current;
            current.next = n;
            current = n;
            end = current;
        }
        fireStateChanged();
    }
}