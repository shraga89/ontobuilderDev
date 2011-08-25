package ac.technion.iem.ontobuilder.gui.elements;

import javax.help.CSH;
import javax.help.HelpBroker;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import ac.technion.iem.ontobuilder.gui.application.Application;
import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;

/**
 * <p>Title: MenuBar</p>
 * Extends {@link JMenuBar}
 */
public class MenuBar extends JMenuBar
{
    private static final long serialVersionUID = 1L;

    protected Application application;
    protected JMenuItem menuHelpHelp;
    protected JMenuItem menuHelpCSHelp;

    /**
     * Constructs a MenuBar
     * 
     * @param application the application the menubar belongs to
     */
    public MenuBar(Application application)
    {
        this.application = application;
        init();
    }

    /**
     * Initialiize the MenuBar
     */
    protected void init()
    {
        // File menu
        JMenu menuFile = new JMenu(ApplicationUtilities.getResourceString("menu.file"));
        menuFile
            .setMnemonic(ApplicationUtilities.getResourceString("menu.file.mnemonic").charAt(0));
        add(menuFile);

        if (application.isApplication())
        {
            // _______
            menuFile.addSeparator();

            // Exit
            addMenuItem(menuFile, application.getAction("exit"));
        }

        // Edit menu
        JMenu menuEdit = new JMenu(ApplicationUtilities.getResourceString("menu.edit"));
        menuEdit
            .setMnemonic(ApplicationUtilities.getResourceString("menu.edit.mnemonic").charAt(0));
        add(menuEdit);

        // Cut
        addMenuItem(menuEdit, application.getAction("cut"));

        // Copy
        addMenuItem(menuEdit, application.getAction("copy"));

        // Paste
        addMenuItem(menuEdit, application.getAction("paste"));

        // Tools menu
        JMenu menuTools = new JMenu(ApplicationUtilities.getResourceString("menu.tools"));
        menuTools.setMnemonic(ApplicationUtilities.getResourceString("menu.tools.mnemonic").charAt(
            0));
        add(menuTools);

        // Options
        addMenuItem(menuTools, application.getAction("options"));

        add(Box.createHorizontalGlue());

        // Help menu
        JMenu menuHelp = new JMenu(ApplicationUtilities.getResourceString("menu.help"));
        menuHelp
            .setMnemonic(ApplicationUtilities.getResourceString("menu.help.mnemonic").charAt(0));
        add(menuHelp);

        // Help
        menuHelpHelp = addMenuItem(menuHelp, application.getAction("help"));

        // Context Sensitive Help
        menuHelpCSHelp = addMenuItem(menuHelp, application.getAction("cshelp"));

        menuHelp.addSeparator();

        // About
        addMenuItem(menuHelp, application.getAction("about"));
    }

    /**
     * Adds a menu item
     * 
     * @param menu the {@link JMenu} to add the item to
     * @param action the action to add
     * @return the {@link JMenuItem}
     */
    protected JMenuItem addMenuItem(JMenu menu, Action action)
    {
        JMenuItem menuItem = menu.add(action);
        menuItem.setAccelerator((KeyStroke) action.getValue(Action.ACCELERATOR_KEY));
        if (action.getValue("helpID") != null)
            CSH.setHelpIDString(menuItem, (String) action.getValue("helpID"));
        return menuItem;
    }

    /**
     * Adds a menu item and make it enabled/disabled
     * 
     * @param menu the menu to add the item to
     * @param action the action to add
     * @param enabled <code>true</code> if enabled, <code>false</code> is disabled
     */
    protected void addMenuItem(JMenu menu, Action action, boolean enabled)
    {
        JMenuItem menuItem = addMenuItem(menu, action);
        menuItem.setEnabled(enabled);
    }

    /**
     * Create a checkbox menu menu item
     */
    protected JMenuItem addCheckBoxMenuItem(JMenu menu, String label, Action action, String icon)
    {
        JCheckBoxMenuItem mi = (JCheckBoxMenuItem) menu.add(new JCheckBoxMenuItem(label));
        if (icon != null)
            mi.setIcon(ApplicationUtilities.getImage(icon));
        mi.addActionListener(action);
        return mi;
    }

    /**
     * Adds a sub menu to the menu
     * 
     * @param menu the {@link JMenu} to add to
     * @param subMenu the sub {@link JMenu} to add
     */
    protected void addMenuItem(JMenu menu, JMenu subMenu)
    {
        menu.add(subMenu);
        // menuItem.setAccelerator((KeyStroke)action.getValue(Action.ACCELERATOR_KEY));
        // if(action.getValue("helpID")!=null)
        // CSH.setHelpIDString(menuItem,(String)action.getValue("helpID"));
        // return menuItem;
    }

    public void setHelpBroker(HelpBroker helpBroker)
    {
        if (helpBroker == null || menuHelpHelp == null)
            throw new NullPointerException();
        menuHelpHelp.addActionListener(new CSH.DisplayHelpFromSource(helpBroker));
    }

    public void setCSHelpBroker(HelpBroker helpBroker)
    {
        if (helpBroker == null || menuHelpCSHelp == null)
            throw new NullPointerException();
        menuHelpCSHelp.addActionListener(new CSH.DisplayHelpAfterTracking(helpBroker));
    }
}