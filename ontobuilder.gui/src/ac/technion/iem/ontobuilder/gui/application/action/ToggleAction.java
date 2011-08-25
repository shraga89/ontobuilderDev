package ac.technion.iem.ontobuilder.gui.application.action;

import java.util.ArrayList;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Icon;

/**
 * <p>Title: ToggleAction</p>
 * <p>Description: Class which extends an {@link AbstractAction}</p>
 */
public class ToggleAction extends AbstractAction
{
    private static final long serialVersionUID = 1L;

    private ArrayList<AbstractButton> buttons;

    public ToggleAction(String name, Icon icon)
    {
        super(name, icon);
        buttons = new ArrayList<AbstractButton>();
    }

    public void actionPerformed(ActionEvent e)
    {
        for (int i = 0; i < buttons.size(); i++)
        {
            AbstractButton button = (AbstractButton) buttons.get(i);
            button.setSelected(true);
        }
    }

    public void addToggleButton(AbstractButton button)
    {
        buttons.add(button);
    }
}
