package ac.technion.iem.ontobuilder.gui.elements;

import java.util.ArrayList;
import java.util.Iterator;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPopupMenu;

/**
 * <p>
 * Title: PopupTrigger
 * </p>
 * Extends {@link MouseAdapter}
 */
public class PopupTrigger extends MouseAdapter
{
    protected JPopupMenu popMenu;
    protected ArrayList<PopupListener> listeners;

    /**
     * Constructs a PopupTrigger
     *
     * @param popMenu the popup menu
     */
    public PopupTrigger(JPopupMenu popMenu)
    {
        this.popMenu = popMenu;
        listeners = new ArrayList<PopupListener>();
    }

    /**
     * Constructs a default PopupTrigger 
     */
    public PopupTrigger()
    {
        this.popMenu = null;
        listeners = new ArrayList<PopupListener>();
    }

    /**
     * Process a mouse-released event
     */
    public void mouseReleased(MouseEvent e)
    {
        if (e.isPopupTrigger())
        {
            if (popMenu != null)
                popMenu.show(e.getComponent(), e.getX(), e.getY());
            firePopupEvent(e);
        }
    }

    /**
     * Process a mouse-pressed event
     */
    public void mousePressed(MouseEvent e)
    {
        if (e.isPopupTrigger())
        {
            if (popMenu != null)
                popMenu.show(e.getComponent(), e.getX(), e.getY());
            firePopupEvent(e);
        }
    }

    public void addPopupListener(PopupListener l)
    {
        listeners.add(l);
    }

    public void removePopupListener(PopupListener l)
    {
        listeners.remove(l);
    }

    protected void firePopupEvent(MouseEvent e)
    {
        for (Iterator<PopupListener> i = listeners.iterator(); i.hasNext();)
        {
            PopupListener l = (PopupListener) i.next();
            l.popup(e);
        }
    }
}