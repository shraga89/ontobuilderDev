package ac.technion.iem.ontobuilder.gui.application;

import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.BorderFactory;
import javax.swing.KeyStroke;
import javax.swing.JFrame;
import javax.help.CSH;

import ac.technion.iem.ontobuilder.core.utils.properties.ApplicationOptions;

/**
 * <p>Title: Options</p>
 * <p>Description: Dialog for changing the application's options (settings)</p>
 * Extends {@link JDialog}
 * <br>
 * Available buttons: 
 * <code>BUTTON_OK</code>,   
 * <code>BUTTON_CANCEL</code>,  
 * <code>BUTTON_APPLY</code>,  
 * <code>BUTTON_DEFAULT</code>
 */
public class Options extends JDialog
{
    private static final long serialVersionUID = 1L;

    final public static short BUTTON_OK = 1;
    final public static short BUTTON_CANCEL = 2;
    final public static short BUTTON_APPLY = 4;
    final public static short BUTTON_DEFAULT = 8;

    protected ApplicationOptions options;
    protected JPanel optionsPanel;

    /**
     * Constructs an Options object
     * 
     * @param parent the Jframe the options belong to
     * @param options the application options
     */
    public Options(JFrame parent, ApplicationOptions options)
    {
        this(parent, options, BUTTON_OK | BUTTON_CANCEL);
    }

    /**
     * Constructs an Options object
     * 
     * @param parent the Jframe the options belong to
     * @param options the application options
     * @param buttons enum of the button to set (BUTTON_OK, BUTTON_CANCEL)
     */
    public Options(JFrame parent, ApplicationOptions options, int buttons)
    {
        super(parent, ApplicationUtilities.getResourceString("options.windowTitle"), true);
        this.options = options;

        setSize(new Dimension(ApplicationUtilities.getIntProperty("options.width"),
            ApplicationUtilities.getIntProperty("options.height")));
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        setContentPane(mainPanel);

        // Buttons
        JPanel buttonsPanel = new JPanel(new FlowLayout());
        ActionListener listener = new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                if (e.getActionCommand().equals("ok"))
                {
                    commandOK();
                }
                else if (e.getActionCommand().equals("cancel"))
                {
                    commandCancel();
                }
                else if (e.getActionCommand().equals("apply"))
                {
                    commandApply();
                }
                else if (e.getActionCommand().equals("default"))
                {
                    commandDefault();
                }
            }
        };

        if ((buttons & BUTTON_OK) != 0)
        {
            JButton button;
            buttonsPanel.add(button = new JButton(ApplicationUtilities
                .getResourceString("options.button.ok")));
            button.setActionCommand("ok");
            button.setToolTipText(ApplicationUtilities
                .getResourceString("options.button.ok.tooltip"));
            button.setMnemonic(KeyStroke.getKeyStroke(
                ApplicationUtilities.getResourceString("options.button.ok.mnemonic")).getKeyCode());
            String helpID = ApplicationUtilities.getResourceString("options.button.ok.helpID");
            if (helpID != null)
                CSH.setHelpIDString(button, helpID);
            button.addActionListener(listener);

            getRootPane().setDefaultButton(button);
        }

        if ((buttons & BUTTON_APPLY) != 0)
        {
            JButton button;
            buttonsPanel.add(button = new JButton(ApplicationUtilities
                .getResourceString("options.button.apply")));
            button.setActionCommand("apply");
            button.setToolTipText(ApplicationUtilities
                .getResourceString("options.button.apply.tooltip"));
            button.setMnemonic(KeyStroke.getKeyStroke(
                ApplicationUtilities.getResourceString("options.button.apply.mnemonic"))
                .getKeyCode());
            String helpID = ApplicationUtilities.getResourceString("options.button.apply.helpID");
            if (helpID != null)
                CSH.setHelpIDString(button, helpID);
            button.addActionListener(listener);
        }

        if ((buttons & BUTTON_DEFAULT) != 0)
        {
            JButton button;
            buttonsPanel.add(button = new JButton(ApplicationUtilities
                .getResourceString("options.button.default")));
            button.setActionCommand("default");
            button.setToolTipText(ApplicationUtilities
                .getResourceString("options.button.default.tooltip"));
            button.setMnemonic(KeyStroke.getKeyStroke(
                ApplicationUtilities.getResourceString("options.button.default.mnemonic"))
                .getKeyCode());
            String helpID = ApplicationUtilities.getResourceString("options.button.default.helpID");
            if (helpID != null)
                CSH.setHelpIDString(button, helpID);
            button.addActionListener(listener);
        }

        if ((buttons & BUTTON_CANCEL) != 0)
        {
            JButton button;
            buttonsPanel.add(button = new JButton(ApplicationUtilities
                .getResourceString("options.button.cancel")));
            button.setActionCommand("cancel");
            button.setToolTipText(ApplicationUtilities
                .getResourceString("options.button.cancel.tooltip"));
            button.setMnemonic(KeyStroke.getKeyStroke(
                ApplicationUtilities.getResourceString("options.button.cancel.mnemonic"))
                .getKeyCode());
            String helpID = ApplicationUtilities.getResourceString("options.button.cancel.helpID");
            if (helpID != null)
                CSH.setHelpIDString(button, helpID);
            button.addActionListener(listener);
        }

        mainPanel.add(BorderLayout.SOUTH, buttonsPanel);

        // Options
        optionsPanel = new JPanel();
        mainPanel.add(BorderLayout.CENTER, optionsPanel);
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                dispose();
            }
        });
    }

    /**
     * Applies the command
     */
    protected void commandOK()
    {
        commandApply();
        dispose();
    }

    /**
     * Cancels the command
     */
    protected void commandCancel()
    {
        dispose();
    }

    /**
     * Resets all the options to their default value
     */
    protected void commandDefault()
    {
        options.resetOptions();
        refreshOptions();
    }

    /**
     * No implemented
     */
    protected void commandApply()
    {
    }

    /**
     * No implemented
     */
    protected void refreshOptions()
    {
    }
}