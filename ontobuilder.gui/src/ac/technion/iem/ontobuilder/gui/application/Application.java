package ac.technion.iem.ontobuilder.gui.application;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URL;
import java.util.Locale;

import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.help.HelpSetException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.text.DefaultEditorKit;

import ac.technion.iem.ontobuilder.core.resources.OntoBuilderResources;
import ac.technion.iem.ontobuilder.core.utils.properties.ApplicationOptions;
import ac.technion.iem.ontobuilder.core.utils.properties.PropertyException;
import ac.technion.iem.ontobuilder.core.utils.properties.ResourceException;
import ac.technion.iem.ontobuilder.gui.application.action.Actions;
import ac.technion.iem.ontobuilder.gui.elements.About;
import ac.technion.iem.ontobuilder.gui.elements.MenuBar;
import ac.technion.iem.ontobuilder.gui.elements.StatusBar;
import ac.technion.iem.ontobuilder.gui.elements.ToolBar;

/**
 * <p>Title: Application</p>
 * Extends {@link JPanel}
 */
public abstract class Application extends JPanel
{
    private static final long serialVersionUID = 1L;

    private boolean parametersInitialized = false;

    // Containers
    protected JApplet applet;
    protected JFrame frame;

    protected Locale locale;

    // Layout
    private JPanel topPanel;

    // GUI
    protected MenuBar menuBar;
    protected ToolBar toolBar;
    protected StatusBar statusBar;

    // Actions
    protected Actions actions;

    // Options
    protected ApplicationOptions options;

    // Main HelpSet & Broker
    protected HelpSet helpSet;
    protected HelpBroker helpBroker;

    /**
     * Constructs a default Application with the default locale
     */
    public Application()
    {
        this(Locale.getDefault());
    }

    /**
     * Constructs an Application
     * 
     * @param locale the locale
     */
    public Application(Locale locale)
    {
        this.locale = locale;
        init();
    }

    /**
     * Constructs an Application
     * 
     * @param applet the applet
     */
    public Application(JApplet applet)
    {
        this(applet, Locale.getDefault());
    }

    /**
     * Constructs an Application
     * 
     * @param frame the frame
     */
    public Application(JFrame frame)
    {
        this(frame, Locale.getDefault());
    }

    /**
     * Constructs an Application
     * 
     * @param applet the applet
     * @param Locale the locale
     */
    public Application(JApplet applet, Locale locale)
    {
        this.applet = applet;
        this.locale = locale;
        init();
    }

    /**
     * Constructs an Application
     * 
     * @param frame the frame
     * @param Locale the locale
     */
    public Application(JFrame frame, Locale locale)
    {
        this.frame = frame;
        this.locale = locale;
        init();
    }

    /**
     * Returns whether this is an application
     * 
     * @return <code>true</code> if this is an application (a frame)
     */
    public boolean isApplication()
    {
        return frame != null;
    }

    /**
     * Returns whether this is an applet
     * 
     * @return <code>true</code> if this is an applet
     */
    public boolean isApplet()
    {
        return applet != null;
    }

    /**
     * Initialise the application (parameters, option, actions, GUI components etc)
     */
    protected void init()
    {
        initializeParameters();
        initializeOptions();
        initializeActions();
        initializeGUIComponents();
        // initializeHelp();
        initializeInterface();
    }

    /**
     * Initialise the parameters (from properties file and resource bundle)
     */
    protected void initializeParameters()
    {
        if (!parametersInitialized)
            // Initialize the properties
            try
            {

                ApplicationUtilities.initializeProperties(OntoBuilderResources.Config.APPLICATION_PROPERTIES);
            }
            catch (PropertyException e)
            {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

        // Initialize the resource bundle
        try
        {
            ApplicationUtilities.initializeResources(OntoBuilderResources.Config.RESOURCES_PROPERTIES, locale);
        }
        catch (ResourceException e)
        {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        parametersInitialized = true;
    }

    /**
     * Initialise the options from the configuration file
     */
    protected void initializeOptions()
    {
        options = new ApplicationOptions();
        File optionFile = new File(OntoBuilderResources.Config.CONFIGURATION_XML);
        options.loadOptions(optionFile);
    }

    /**
     * Initialise the GUI components (tool bar, menu bar and status bar)
     */
    protected void initializeGUIComponents()
    {
        toolBar = new ToolBar(this);
        menuBar = new MenuBar(this);
        statusBar = new StatusBar();
    }

    /**
     * Initialise the help component of the application
     */
    protected void initializeHelp()
    {
        try
        {
            ClassLoader loader = getClass().getClassLoader();
            URL hsURL = HelpSet.findHelpSet(loader,
                ApplicationUtilities.getStringProperty("application.helpSet"), locale);
            helpSet = new HelpSet(loader, hsURL);
            helpBroker = helpSet.createHelpBroker();

            if (toolBar != null)
            {
                toolBar.setHelpBroker(helpBroker);
                toolBar.setCSHelpBroker(helpBroker);
            }
            if (menuBar != null)
            {
                menuBar.setHelpBroker(helpBroker);
                menuBar.setCSHelpBroker(helpBroker);
            }
        }
        catch (HelpSetException e)
        {
            JOptionPane.showMessageDialog(null, ApplicationUtilities.getResourceString("error") +
                ": " + e.getMessage(), ApplicationUtilities.getResourceString("error"),
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Initialise the interface (layout etc)
     */
    protected void initializeInterface()
    {
        String look = ApplicationUtilities.getStringProperty("application.look");
        ApplicationUtilities.initLookAndFeel(look);

        if (isApplication())
        {
            frame.setTitle(ApplicationUtilities.getResourceString("application.title"));
            frame.setIconImage(ApplicationUtilities.getImage("application.gif").getImage());
        }

        int width = ApplicationUtilities.getIntProperty("application.width");
        int height = ApplicationUtilities.getIntProperty("application.height");
        setPreferredSize(new Dimension(width, height));

        setLayout(new BorderLayout());

        setLayout(new BorderLayout());
        topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);

        setMenuBar(menuBar);
        setToolBar(toolBar);
        setStatusBar(statusBar);
    }

    /**
     * Set the menu bar
     * 
     * @param menuBar the menuBar
     */
    public void setMenuBar(MenuBar menuBar)
    {
        this.menuBar = menuBar;
        topPanel.add(menuBar, BorderLayout.NORTH);
    }

    /**
     * Set the tool bar
     * 
     * @param toolBar the tool bar
     */
    public void setToolBar(ToolBar toolBar)
    {
        this.toolBar = toolBar;
        topPanel.add(toolBar, BorderLayout.SOUTH);
    }

    /**
     * Set the status bar
     * 
     * @param statusBar the status bar
     */
    public void setStatusBar(StatusBar statusBar)
    {
        this.statusBar = statusBar;
        add(statusBar, BorderLayout.SOUTH);
    }

    /**
     * Get the action according to its name
     * 
     * @param action the action name
     * @return Action the action
     */
    public Action getAction(String action)
    {
        return actions.getAction(action);
    }

    /**
     * Set the status of the application in the status bar
     * 
     * @param status the status string
     */
    public void setStatus(String status)
    {
        statusBar.setStatus(status);
    }

    /**
     * Set the temporal status of the application
     * 
     * @param status the status string
     */
    public void setTemporalStatus(String status)
    {
        statusBar.setTemporalStatus(status);
    }

    /**
     * Clear the status from the status bar
     */
    public void clearStatus()
    {
        statusBar.clearStatus();
    }

    /**
     * Set the status icon in the status bar
     * 
     * @param icon the icon to set
     */
    public void setStatusIcon(ImageIcon icon)
    {
        statusBar.setStatusIcon(icon);
    }

    /**
     * Clear the status icon from the status bar
     */
    public void clearStatusIcon()
    {
        statusBar.clearStatusIcon();
    }

    /**
     * Initialise the actions (LONG_DESCRIPTION, SHORT_DESCRIPTION etc)
     */
    protected void initializeActions()
    {
        actions = new Actions();
        Action action = null;

        // Exit
        action = new AbstractAction(ApplicationUtilities.getResourceString("action.exit"),
            ApplicationUtilities.getImage("blank.gif"))
        {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e)
            {
                exit();
            }
        };
        action.putValue(Action.LONG_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.exit.longDescription"));
        action.putValue(Action.SHORT_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.exit.shortDescription"));
        action.putValue(
            Action.MNEMONIC_KEY,
            new Integer(KeyStroke.getKeyStroke(
                ApplicationUtilities.getResourceString("action.exit.mnemonic")).getKeyCode()));
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ApplicationUtilities
            .getResourceString("action.exit.accelerator")));
        actions.addAction("exit", action);

        // Help
        action = new AbstractAction(ApplicationUtilities.getResourceString("action.help"),
            ApplicationUtilities.getImage("help.gif"))
        {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e)
            {
                commandHelp();
            }
        };
        action.putValue(Action.LONG_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.help.longDescription"));
        action.putValue(Action.SHORT_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.help.shortDescription"));
        action.putValue(
            Action.MNEMONIC_KEY,
            new Integer(KeyStroke.getKeyStroke(
                ApplicationUtilities.getResourceString("action.help.mnemonic")).getKeyCode()));
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ApplicationUtilities
            .getResourceString("action.help.accelerator")));
        actions.addAction("help", action);

        // Context Sensitive Help
        action = new AbstractAction(ApplicationUtilities.getResourceString("action.cshelp"),
            ApplicationUtilities.getImage("cshelp.gif"))
        {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e)
            {
                commandCSHelp();
            }
        };
        action.putValue(Action.LONG_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.cshelp.longDescription"));
        action.putValue(Action.SHORT_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.cshelp.shortDescription"));
        action.putValue(
            Action.MNEMONIC_KEY,
            new Integer(KeyStroke.getKeyStroke(
                ApplicationUtilities.getResourceString("action.cshelp.mnemonic")).getKeyCode()));
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ApplicationUtilities
            .getResourceString("action.cshelp.accelerator")));
        actions.addAction("cshelp", action);

        // About
        action = new AbstractAction(ApplicationUtilities.getResourceString("action.about"),
            ApplicationUtilities.getImage("blank.gif"))
        {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e)
            {
                commandAbout();
            }
        };
        action.putValue(Action.LONG_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.about.longDescription"));
        action.putValue(Action.SHORT_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.about.shortDescription"));
        action.putValue(
            Action.MNEMONIC_KEY,
            new Integer(KeyStroke.getKeyStroke(
                ApplicationUtilities.getResourceString("action.about.mnemonic")).getKeyCode()));
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ApplicationUtilities
            .getResourceString("action.about.accelerator")));
        actions.addAction("about", action);

        Actions textActions = new Actions();
        JTextField tf = new JTextField();
        Action[] textActionsArray = tf.getActions();
        for (int i = 0; i < textActionsArray.length; i++)
        {
            Action a = textActionsArray[i];
            textActions.addAction((String) a.getValue(Action.NAME), a);
        }

        // Cut
        action = textActions.getAction(DefaultEditorKit.cutAction);
        action.putValue(Action.DEFAULT, DefaultEditorKit.cutAction);
        action.putValue(Action.NAME, ApplicationUtilities.getResourceString("action.cut"));
        action.putValue(Action.SMALL_ICON, ApplicationUtilities.getImage("cut.gif"));
        action.putValue(Action.LONG_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.cut.longDescription"));
        action.putValue(Action.SHORT_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.cut.shortDescription"));
        action.putValue(
            Action.MNEMONIC_KEY,
            new Integer(KeyStroke.getKeyStroke(
                ApplicationUtilities.getResourceString("action.cut.mnemonic")).getKeyCode()));
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ApplicationUtilities
            .getResourceString("action.cut.accelerator")));
        actions.addAction("cut", action);

        // Copy
        action = textActions.getAction(DefaultEditorKit.copyAction);
        action.putValue(Action.DEFAULT, DefaultEditorKit.copyAction);
        action.putValue(Action.NAME, ApplicationUtilities.getResourceString("action.copy"));
        action.putValue(Action.SMALL_ICON, ApplicationUtilities.getImage("copy.gif"));
        action.putValue(Action.LONG_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.copy.longDescription"));
        action.putValue(Action.SHORT_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.copy.shortDescription"));
        action.putValue(
            Action.MNEMONIC_KEY,
            new Integer(KeyStroke.getKeyStroke(
                ApplicationUtilities.getResourceString("action.copy.mnemonic")).getKeyCode()));
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ApplicationUtilities
            .getResourceString("action.copy.accelerator")));
        actions.addAction("copy", action);

        // Paste
        action = textActions.getAction(DefaultEditorKit.pasteAction);
        action.putValue(Action.DEFAULT, DefaultEditorKit.pasteAction);
        action.putValue(Action.NAME, ApplicationUtilities.getResourceString("action.paste"));
        action.putValue(Action.SMALL_ICON, ApplicationUtilities.getImage("paste.gif"));
        action.putValue(Action.LONG_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.paste.longDescription"));
        action.putValue(Action.SHORT_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.paste.shortDescription"));
        action.putValue(
            Action.MNEMONIC_KEY,
            new Integer(KeyStroke.getKeyStroke(
                ApplicationUtilities.getResourceString("action.paste.mnemonic")).getKeyCode()));
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ApplicationUtilities
            .getResourceString("action.paste.accelerator")));
        actions.addAction("paste", action);
        // action.setEnabled(false);

        // Options
        action = new AbstractAction(ApplicationUtilities.getResourceString("action.options"),
            ApplicationUtilities.getImage("options.gif"))
        {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e)
            {
                commandOptions();
            }
        };
        action.putValue(Action.LONG_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.options.longDescription"));
        action.putValue(Action.SHORT_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.options.shortDescription"));
        action.putValue(
            Action.MNEMONIC_KEY,
            new Integer(KeyStroke.getKeyStroke(
                ApplicationUtilities.getResourceString("action.options.mnemonic")).getKeyCode()));
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ApplicationUtilities
            .getResourceString("action.options.accelerator")));
        actions.addAction("options", action);
    }

    /**
     * Get an option by its name
     * 
     * @param optionName the option name
     * @return the option string
     */
    public String getOption(String optionName)
    {
        return options.getOptionValue(optionName);
    }

    public void commandHelp()
    {
    }

    public void commandCSHelp()
    {
    }

    /**
     * Make the 'About' of a frame visible
     */
    public void commandAbout()
    {
        // new About(frame).show();
        new About(frame).setVisible(true);
    }

    /**
     * Make the option of a frame visible
     */
    public void commandOptions()
    {
        // new Options(frame,options).show();
        new Options(frame, options).setVisible(true);
    }

    /**
     * Check if the parameters of the application are initialised
     * 
     * @return <code>true</code> if the parameters are initialized
     */
    public boolean isParametersInitialized()
    {
        return parametersInitialized;
    }

    /**
     * Exit the application
     */
    public void exit()
    {
        if (isApplication())
        {
            if (options != null)
                options.saveOptions(OntoBuilderResources.Config.CONFIGURATION_XML);
            frame.dispose();
            System.exit(0);
        }
    }
}