package ac.technion.iem.ontobuilder.gui.application.action;

import java.awt.event.ItemListener;
import javax.swing.AbstractAction;
import javax.swing.Icon;

/**
 * <p>Title: ItemSelectionAction</p>
 * Extends {@link AbstractAction}<br>
 * Implements {@link ItemListener}
 */
public abstract class ItemSelectionAction extends AbstractAction implements ItemListener
{
    private static final long serialVersionUID = 1L;

    public ItemSelectionAction(String name, Icon icon)
    {
        super(name, icon);
    }
}
