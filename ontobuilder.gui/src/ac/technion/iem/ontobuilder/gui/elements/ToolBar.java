package ac.technion.iem.ontobuilder.gui.elements;

import javax.help.CSH;
import javax.help.HelpBroker;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JToolBar;

import ac.technion.iem.ontobuilder.gui.application.Application;

/**
 * <p>Title: ToolBar</p>
 * Extends {@link JToolBar}
 */
public class ToolBar extends JToolBar
{
    private static final long serialVersionUID = 1L;

    protected Application application;
    protected JButton helpButton;
    protected JButton cshelpButton;

    /**
     * Constructs a ToolBar
     * 
     * @param application the application to tool bar belongs to
     */
    public ToolBar(Application application)
    {
        super();
        this.application = application;
        setFloatable(false);
        setRollover(true);
        // putClientProperty("JToolBar.isRollover",Boolean.TRUE);

        init();
    }

    /**
     * Initialise the tool bar
     */
    protected void init()
    {
        if (application == null)
            return;
        addSeparator();

        // Cut
        addButton(application.getAction("cut"));

        // Copy
        addButton(application.getAction("copy"));

        // Paste
        addButton(application.getAction("paste"));

        addSeparator();

        // Options
        addButton(application.getAction("options"));

        add(Box.createHorizontalGlue());

        // Context Sensitive Help
        cshelpButton = addButton(application.getAction("cshelp"));

        // Help
        helpButton = addButton(application.getAction("help"));
    }

    /**
     * Adds a button to the tool bar
     * 
     * @param action the action to add a button to
     * @return the new button
     */
    public JButton addButton(Action action)
    {
        JButton button = add(action);
        if (action.getValue("helpID") != null)
            CSH.setHelpIDString(button, (String) action.getValue("helpID"));
        button.setRequestFocusEnabled(false);
        return button;
    }

    public void setHelpBroker(HelpBroker helpBroker)
    {
        if (helpBroker != null)
            helpButton.addActionListener(new CSH.DisplayHelpFromSource(helpBroker));
    }

    public void setCSHelpBroker(HelpBroker helpBroker)
    {
        if (helpBroker != null)
            cshelpButton.addActionListener(new CSH.DisplayHelpAfterTracking(helpBroker));
    }
}