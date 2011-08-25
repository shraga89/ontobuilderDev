package ac.technion.iem.ontobuilder.gui.tools.sitemap;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.help.CSH;

import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.tools.sitemap.event.SiteMapOperationEvent;
import ac.technion.iem.ontobuilder.gui.tools.sitemap.event.SiteMapOperationListener;
import ac.technion.iem.ontobuilder.gui.tools.sitemap.event.URLVisitedEvent;
import ac.technion.iem.ontobuilder.gui.tools.sitemap.event.URLVisitedListener;

/**
 * <p>Title: SitesVisited</p>
 * Extends a {@link JDialog}
 * <br> Implements {@link URLVisitedListener} and {@link SiteMapOperationListener
 */
public class SitesVisited extends JDialog implements URLVisitedListener, SiteMapOperationListener
{
    private static final long serialVersionUID = 1L;

    protected JTextArea text;
    protected JButton cancelButton;
    protected JButton okButton;
    protected SiteMap siteMap;
    protected long counter;
    protected JLabel counterLabel;

    /**
     * Constructs a SitesVisited
     * 
     * @param parent the parent {@link JFrame}
     * @param siteMap a {@link SiteMap}
     */
    public SitesVisited(JFrame parent, SiteMap siteMap)
    {
        super(parent, false);
        this.siteMap = siteMap;
        counter = 0;

        setSize(new Dimension(ApplicationUtilities.getIntProperty("sitemap.visited.width"),
            ApplicationUtilities.getIntProperty("sitemap.visited.height")));
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
            }
        };

        // OK
        buttonsPanel.add(okButton = new JButton(ApplicationUtilities
            .getResourceString("sitemap.button.ok")));
        okButton.setEnabled(false);
        okButton.setActionCommand("ok");
        okButton
            .setToolTipText(ApplicationUtilities.getResourceString("sitemap.button.ok.tooltip"));
        okButton.setMnemonic(KeyStroke.getKeyStroke(
            ApplicationUtilities.getResourceString("sitemap.button.ok.mnemonic")).getKeyCode());
        String helpID = ApplicationUtilities.getResourceString("sitemap.button.ok.helpID");
        if (helpID != null)
            CSH.setHelpIDString(okButton, helpID);
        okButton.addActionListener(listener);
        getRootPane().setDefaultButton(okButton);

        // Cancel
        buttonsPanel.add(cancelButton = new JButton(ApplicationUtilities
            .getResourceString("sitemap.button.cancel")));
        cancelButton.setActionCommand("cancel");
        cancelButton.setToolTipText(ApplicationUtilities
            .getResourceString("sitemap.button.cancel.tooltip"));
        cancelButton.setMnemonic(KeyStroke.getKeyStroke(
            ApplicationUtilities.getResourceString("sitemap.button.cancel.mnemonic")).getKeyCode());
        helpID = ApplicationUtilities.getResourceString("sitemap.button.cancel.helpID");
        if (helpID != null)
            CSH.setHelpIDString(cancelButton, helpID);
        cancelButton.addActionListener(listener);

        mainPanel.add(BorderLayout.SOUTH, buttonsPanel);

        JPanel centerPanel = new JPanel(new BorderLayout());

        // Counter
        counterLabel = new JLabel();
        counterLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 5));
        centerPanel.add(BorderLayout.SOUTH, counterLabel);

        // Text
        text = new JTextArea();
        text.setEditable(false);
        JScrollPane scroll = new JScrollPane(text);
        scroll.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(5, 5, 5, 5),
            BorderFactory.createLineBorder(Color.black)));
        centerPanel.add(BorderLayout.CENTER, scroll);

        mainPanel.add(BorderLayout.CENTER, centerPanel);

        // Title
        JLabel title = new JLabel(ApplicationUtilities.getResourceString("sitemap.title"),
            ApplicationUtilities.getImage("sitemap.gif"), SwingConstants.LEFT);
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 5));
        mainPanel.add(BorderLayout.NORTH, title);

        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                dispose();
            }
        });
    }

    /**
     * Adds a site to the sites visited
     * 
     * @param site the site to add
     */
    public void addSite(String site)
    {
        text.append(site + "\n");
        counter++;
        counterLabel.setText(ApplicationUtilities.getResourceString("sitemap.counter") + " " +
            counter);
    }

    /**
     * Execute a "OK" command
     */
    protected void commandOK()
    {
        dispose();
    }

    /**
     * Execute a "Cancel" command
     */
    protected void commandCancel()
    {
        siteMap.stop();
        dispose();
    }

    /**
     * Adds a URL when visited to the sites visited
     * @param e a {@link URLVisitedEvent}
     */
    public void urlVisited(URLVisitedEvent e)
    {
        addSite("Visiting " + e.getURL().toExternalForm() + "(" + e.getDepth() + ")");
    }

    public void operationPerformed(SiteMapOperationEvent e)
    {
        if (e.getOperation() == SiteMap.OPERATION_FINALIZE)
        {
            cancelButton.setEnabled(false);
            okButton.setEnabled(true);
            counterLabel.setText(ApplicationUtilities.getResourceString("sitemap.counter") + " " +
                counter);
        }
    }
}