package ac.technion.iem.ontobuilder.gui.ontobuilder.elements;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.help.CSH;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;

import ac.technion.iem.ontobuilder.core.biztalk.BizTalkUtilities;
import ac.technion.iem.ontobuilder.core.utils.dom.DOMUtilities;
import ac.technion.iem.ontobuilder.core.utils.network.NetworkUtilitiesPropertiesEnum;
import ac.technion.iem.ontobuilder.core.utils.properties.ApplicationOptions;
import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.application.Options;
import ac.technion.iem.ontobuilder.gui.elements.MultilineLabel;
import ac.technion.iem.ontobuilder.gui.elements.TextField;
import ac.technion.iem.ontobuilder.gui.ontobuilder.main.OntoBuilder;
import ac.technion.iem.ontobuilder.gui.tools.sitemap.SiteMap;
import ac.technion.iem.ontobuilder.gui.utils.browser.Browser;
import ac.technion.iem.ontobuilder.gui.utils.files.common.FileUtilities;
import ac.technion.iem.ontobuilder.gui.utils.files.html.HTMLUtilitiesGui;
import ac.technion.iem.ontobuilder.gui.utils.graphs.GraphUtilities;
import ac.technion.iem.ontobuilder.io.exports.ExportUtilities;
import ac.technion.iem.ontobuilder.io.imports.ImportUtilities;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.Algorithm;

/**
 * <p>Title: OntoBuilderOptions</p>
 * Extends a {@link Options}
 */
public class OntoBuilderOptions extends Options
{
    private static final long serialVersionUID = 1L;

    // Options
    private JSpinner spnHistoryEntries;
    private JSpinner spnCrawlingDepth;
    private JSpinner spnMatchThreshold;
    private JCheckBox chkHTMLPanel;
    private JCheckBox chkSourcePanel;
    private JCheckBox chkMessagePanel;
    private JCheckBox chkSitemapPanel;
    private JCheckBox chkToolsPanel;
    private JCheckBox chkDOMPanel;
    private JCheckBox chkHyperbolicDOMPanel;
    private JCheckBox chkThesaurusPanel;
    private JCheckBox chkElementsPanel;
    private JCheckBox chkOntologyPanel;
    private JCheckBox chkXMLValidation;
    private JCheckBox chkMETANavigation;
    private JCheckBox chkFilePreview;
    private JCheckBox chkGraphPrecedence;
    private JCheckBox chkGraphHidden;
    private JRadioButton rdoNameLabelName;
    private JRadioButton rdoNameLabel;
    private JRadioButton rdoNameName;
    private JRadioButton rdoEnumerationLabelValue;
    private JRadioButton rdoEnumerationLabel;
    private JRadioButton rdoEnumerationValue;
    private TextField txtProxyHost;
    private TextField txtProxyPort;
    private JCheckBox chkUseProxy;
    private JSpinner spnConnectionTimeout;

    /**
     * Constructs a OntoBuilderOptions
     * 
     * @param parent the parent {@link JFrame}
     * @param ontoBuilder the {@link OntoBuilder} application
     * @param options the {@link ApplicationOptions}
     * 
     * <br> The options are: <br>
     * <code>BUTTON_OK</code>, <code>BUTTON_CANCEL</code>, <code>BUTTON_APPLY</code>, <code>BUTTON_DEFAULT</code>
     */
    public OntoBuilderOptions(JFrame parent, OntoBuilder ontoBuilder, ApplicationOptions options)
    {
        this(parent, ontoBuilder, options, BUTTON_OK | BUTTON_CANCEL | BUTTON_APPLY |
            BUTTON_DEFAULT);
    }

    /**
     * Constructs a OntoBuilderOptions
     * 
     * @param parent the parent {@link JFrame}
     * @param ontoBuilder the {@link OntoBuilder} application
     * @param options the {@link ApplicationOptions}
     * @param buttons the number of buttons
     */
    public OntoBuilderOptions(JFrame parent, OntoBuilder ontoBuilder,
        final ApplicationOptions options, int buttons)
    {
        super(parent, options, buttons);
        optionsPanel.setLayout(new BorderLayout());
        JTabbedPane panels = new JTabbedPane();
        optionsPanel.add(panels, BorderLayout.CENTER);

        // General
        {
            JPanel general = new JPanel(new GridBagLayout());
            panels.add(ApplicationUtilities.getResourceString("options.panel.general"), general);

            {// Parsing
                JPanel parsing = new JPanel(new GridBagLayout());
                parsing.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(Color.black),
                    ApplicationUtilities.getResourceString("options.general.parsing")));
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.insets = new Insets(5, 5, 5, 5);
                general.add(parsing, gbc);

                {// Icon
                    GridBagConstraints gbcl = new GridBagConstraints();
                    gbcl.gridheight = 2;
                    gbcl.insets = new Insets(5, 5, 5, 10);
                    gbcl.anchor = GridBagConstraints.NORTHWEST;
                    parsing.add(new JLabel(ApplicationUtilities.getImage("parsing.gif")), gbcl);
                }

                {// Explanation
                    GridBagConstraints gbcl = new GridBagConstraints();
                    gbcl.weightx = 1;
                    gbcl.insets = new Insets(0, 0, 5, 0);
                    gbcl.fill = GridBagConstraints.HORIZONTAL;
                    gbcl.anchor = GridBagConstraints.WEST;
                    parsing.add(
                        new MultilineLabel(ApplicationUtilities
                            .getResourceString("options.general.parsing.explanation")), gbcl);
                }

                {// Selections
                    JPanel selections = new JPanel(new GridLayout(0, 1, 5, 5));

                    chkXMLValidation = new JCheckBox(
                        ApplicationUtilities
                            .getResourceString("options.general.parsing.xmlValidation"),
                        Boolean.valueOf(
                            (String) options.getOptionValue(DOMUtilities.XML_VALIDATION_PROPERTY))
                            .booleanValue());
                    chkXMLValidation.setToolTipText(ApplicationUtilities
                        .getResourceString("options.general.parsing.xmlValidation.tooltip"));
                    chkXMLValidation.setMnemonic(KeyStroke.getKeyStroke(
                        ApplicationUtilities
                            .getResourceString("options.general.parsing.xmlValidation.mnemonic"))
                        .getKeyCode());
                    CSH.setHelpIDString(chkXMLValidation, ApplicationUtilities
                        .getResourceString("options.general.parsing.xmlValidation.helpID"));
                    selections.add(chkXMLValidation);

                    GridBagConstraints gbcl = new GridBagConstraints();
                    gbcl.gridy = 1;
                    gbcl.gridx = 1;
                    gbcl.insets = new Insets(5, 5, 5, 5);
                    gbcl.anchor = GridBagConstraints.WEST;
                    parsing.add(selections, gbcl);
                }
            }

            {// Site Map
                JPanel sitemap = new JPanel(new GridBagLayout());
                sitemap.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(Color.black),
                    ApplicationUtilities.getResourceString("options.general.sitemap")));
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridy = 1;
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.insets = new Insets(5, 5, 5, 5);
                general.add(sitemap, gbc);

                {// Icon
                    GridBagConstraints gbcl = new GridBagConstraints();
                    gbcl.gridheight = 2;
                    gbcl.insets = new Insets(5, 5, 5, 10);
                    gbcl.anchor = GridBagConstraints.NORTHWEST;
                    sitemap.add(new JLabel(ApplicationUtilities.getImage("sitemap.gif")), gbcl);
                }

                {// Explanation
                    GridBagConstraints gbcl = new GridBagConstraints();
                    gbcl.weightx = 1;
                    gbcl.gridwidth = 2;
                    gbcl.insets = new Insets(0, 0, 5, 0);
                    gbcl.fill = GridBagConstraints.HORIZONTAL;
                    gbcl.anchor = GridBagConstraints.WEST;
                    sitemap.add(
                        new MultilineLabel(ApplicationUtilities
                            .getResourceString("options.general.sitemap.explanation")), gbcl);
                }

                JLabel depthLabel = new JLabel(
                    ApplicationUtilities.getResourceString("options.general.sitemap.depth"));
                depthLabel.setDisplayedMnemonic(KeyStroke.getKeyStroke(
                    ApplicationUtilities
                        .getResourceString("options.general.sitemap.depth.mnemonic")).getKeyCode());
                CSH.setHelpIDString(depthLabel,
                    ApplicationUtilities.getResourceString("options.general.sitemap.depth.helpID"));
                {// Depth label
                    GridBagConstraints gbcl = new GridBagConstraints();
                    gbcl.gridx = 1;
                    gbcl.gridy = 1;
                    gbcl.anchor = GridBagConstraints.WEST;
                    sitemap.add(depthLabel, gbcl);
                }

                {// Depth
                    GridBagConstraints gbcl = new GridBagConstraints();
                    gbcl.gridx = 2;
                    gbcl.gridy = 1;
                    gbcl.insets = new Insets(0, 5, 0, 5);
                    gbcl.anchor = GridBagConstraints.WEST;

                    int depth = 1;
                    try
                    {
                        depth = Integer.parseInt((String) options
                            .getOptionValue(SiteMap.CRAWLING_DEPTH_PROPERTY));
                    }
                    catch (NumberFormatException e)
                    {
                    }
                    spnCrawlingDepth = new JSpinner(new SpinnerNumberModel(new Integer(depth),
                        new Integer(1), new Integer(10), new Integer(1)));
                    spnCrawlingDepth.setToolTipText(ApplicationUtilities
                        .getResourceString("options.general.sitemap.depth.tooltip"));
                    depthLabel.setLabelFor(spnCrawlingDepth);
                    CSH.setHelpIDString(spnCrawlingDepth, ApplicationUtilities
                        .getResourceString("options.general.sitemap.depth.helpID"));
                    sitemap.add(spnCrawlingDepth, gbcl);
                }
            }

            {// Separator
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridy = 2;
                gbc.gridheight = GridBagConstraints.REMAINDER;
                gbc.fill = GridBagConstraints.VERTICAL;
                gbc.weightx = 1.0;
                gbc.weighty = 1.0;
                general.add(new JPanel(), gbc);
            }
        }

        // View
        {
            JPanel view = new JPanel(new GridBagLayout());
            panels.add(ApplicationUtilities.getResourceString("options.panel.view"), view);

            {// Main Panel
                JPanel mainPanel = new JPanel(new GridBagLayout());
                mainPanel.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(Color.black),
                    ApplicationUtilities.getResourceString("options.view.mainPanel")));
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.insets = new Insets(5, 5, 5, 5);
                view.add(mainPanel, gbc);

                {// Icon
                    GridBagConstraints gbcl = new GridBagConstraints();
                    gbcl.gridheight = 2;
                    gbcl.insets = new Insets(5, 5, 5, 10);
                    gbcl.anchor = GridBagConstraints.NORTHWEST;
                    mainPanel.add(new JLabel(ApplicationUtilities.getImage("mainpanel.gif")), gbcl);
                }

                {// Explanation
                    GridBagConstraints gbcl = new GridBagConstraints();
                    gbcl.weightx = 1;
                    gbcl.insets = new Insets(0, 0, 5, 0);
                    gbcl.fill = GridBagConstraints.HORIZONTAL;
                    gbcl.anchor = GridBagConstraints.WEST;
                    mainPanel.add(
                        new MultilineLabel(ApplicationUtilities
                            .getResourceString("options.view.mainPanel.explanation")), gbcl);
                }

                {// Selections
                    JPanel selections = new JPanel(new GridLayout(0, 2, 5, 5));

                    chkHTMLPanel = new JCheckBox(
                        ApplicationUtilities.getResourceString("options.view.mainPanel.htmlPanel"),
                        Boolean.valueOf(
                            (String) options.getOptionValue(MainPanel.HTML_PANEL_VISIBLE_PROPERTY))
                            .booleanValue());
                    chkHTMLPanel.setToolTipText(ApplicationUtilities
                        .getResourceString("options.view.mainPanel.htmlPanel.tooltip"));
                    chkHTMLPanel.setMnemonic(KeyStroke.getKeyStroke(
                        ApplicationUtilities
                            .getResourceString("options.view.mainPanel.htmlPanel.mnemonic"))
                        .getKeyCode());
                    CSH.setHelpIDString(chkHTMLPanel, ApplicationUtilities
                        .getResourceString("options.view.mainPanel.htmlPanel.helpID"));
                    selections.add(chkHTMLPanel);

                    chkSourcePanel = new JCheckBox(
                        ApplicationUtilities
                            .getResourceString("options.view.mainPanel.sourcePanel"),
                        Boolean.valueOf(
                            (String) options
                                .getOptionValue(MainPanel.SOURCE_PANEL_VISIBLE_PROPERTY))
                            .booleanValue());
                    chkSourcePanel.setToolTipText(ApplicationUtilities
                        .getResourceString("options.view.mainPanel.sourcePanel.tooltip"));
                    chkSourcePanel.setMnemonic(KeyStroke.getKeyStroke(
                        ApplicationUtilities
                            .getResourceString("options.view.mainPanel.sourcePanel.mnemonic"))
                        .getKeyCode());
                    CSH.setHelpIDString(chkSourcePanel, ApplicationUtilities
                        .getResourceString("options.view.mainPanel.sourcePanel.helpID"));
                    selections.add(chkSourcePanel);

                    chkMessagePanel = new JCheckBox(
                        ApplicationUtilities
                            .getResourceString("options.view.mainPanel.messagePanel"),
                        Boolean.valueOf(
                            (String) options
                                .getOptionValue(MainPanel.MESSAGE_PANEL_VISIBLE_PROPERTY))
                            .booleanValue());
                    chkMessagePanel.setToolTipText(ApplicationUtilities
                        .getResourceString("options.view.mainPanel.messagePanel.tooltip"));
                    chkMessagePanel.setMnemonic(KeyStroke.getKeyStroke(
                        ApplicationUtilities
                            .getResourceString("options.view.mainPanel.messagePanel.mnemonic"))
                        .getKeyCode());
                    CSH.setHelpIDString(chkMessagePanel, ApplicationUtilities
                        .getResourceString("options.view.mainPanel.messagePanel.helpID"));
                    selections.add(chkMessagePanel);

                    chkSitemapPanel = new JCheckBox(
                        ApplicationUtilities
                            .getResourceString("options.view.mainPanel.sitemapPanel"),
                        Boolean.valueOf(
                            (String) options
                                .getOptionValue(MainPanel.SITEMAP_PANEL_VISIBLE_PROPERTY))
                            .booleanValue());
                    chkSitemapPanel.setToolTipText(ApplicationUtilities
                        .getResourceString("options.view.mainPanel.sitemapPanel.tooltip"));
                    chkSitemapPanel.setMnemonic(KeyStroke.getKeyStroke(
                        ApplicationUtilities
                            .getResourceString("options.view.mainPanel.sitemapPanel.mnemonic"))
                        .getKeyCode());
                    CSH.setHelpIDString(chkSitemapPanel, ApplicationUtilities
                        .getResourceString("options.view.mainPanel.sitemapPanel.helpID"));
                    selections.add(chkSitemapPanel);

                    chkElementsPanel = new JCheckBox(
                        ApplicationUtilities
                            .getResourceString("options.view.mainPanel.elementsPanel"),
                        Boolean.valueOf(
                            (String) options
                                .getOptionValue(MainPanel.ELEMENTS_PANEL_VISIBLE_PROPERTY))
                            .booleanValue());
                    chkElementsPanel.setToolTipText(ApplicationUtilities
                        .getResourceString("options.view.mainPanel.elementsPanel.tooltip"));
                    chkElementsPanel.setMnemonic(KeyStroke.getKeyStroke(
                        ApplicationUtilities
                            .getResourceString("options.view.mainPanel.elementsPanel.mnemonic"))
                        .getKeyCode());
                    CSH.setHelpIDString(chkElementsPanel, ApplicationUtilities
                        .getResourceString("options.view.mainPanel.elementsPanel.helpID"));
                    selections.add(chkElementsPanel);

                    chkOntologyPanel = new JCheckBox(
                        ApplicationUtilities
                            .getResourceString("options.view.mainPanel.ontologyPanel"),
                        Boolean.valueOf(
                            (String) options
                                .getOptionValue(MainPanel.ONTOLOGY_PANEL_VISIBLE_PROPERTY))
                            .booleanValue());
                    chkOntologyPanel.setToolTipText(ApplicationUtilities
                        .getResourceString("options.view.mainPanel.ontologyPanel.tooltip"));
                    chkOntologyPanel.setMnemonic(KeyStroke.getKeyStroke(
                        ApplicationUtilities
                            .getResourceString("options.view.mainPanel.ontologyPanel.mnemonic"))
                        .getKeyCode());
                    CSH.setHelpIDString(chkOntologyPanel, ApplicationUtilities
                        .getResourceString("options.view.mainPanel.ontologyPanel.helpID"));
                    selections.add(chkOntologyPanel);

                    chkToolsPanel = new JCheckBox(
                        ApplicationUtilities.getResourceString("options.view.mainPanel.toolsPanel"),
                        Boolean
                            .valueOf(
                                (String) options
                                    .getOptionValue(MainPanel.TOOLS_PANEL_VISIBLE_PROPERTY))
                            .booleanValue());
                    chkToolsPanel.setToolTipText(ApplicationUtilities
                        .getResourceString("options.view.mainPanel.toolsPanel.tooltip"));
                    chkToolsPanel.setMnemonic(KeyStroke.getKeyStroke(
                        ApplicationUtilities
                            .getResourceString("options.view.mainPanel.toolsPanel.mnemonic"))
                        .getKeyCode());
                    CSH.setHelpIDString(chkToolsPanel, ApplicationUtilities
                        .getResourceString("options.view.mainPanel.toolsPanel.helpID"));
                    selections.add(chkToolsPanel);

                    GridBagConstraints gbcl = new GridBagConstraints();
                    gbcl.gridy = 1;
                    gbcl.gridx = 1;
                    gbcl.insets = new Insets(5, 5, 5, 5);
                    gbcl.anchor = GridBagConstraints.WEST;
                    mainPanel.add(selections, gbcl);
                }
            }

            {// Upper Panel
                JPanel upperPanel = new JPanel(new GridBagLayout());
                upperPanel.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(Color.black),
                    ApplicationUtilities.getResourceString("options.view.upperPanel")));
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridy = 1;
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.insets = new Insets(5, 5, 5, 5);
                view.add(upperPanel, gbc);

                {// Icon
                    GridBagConstraints gbcl = new GridBagConstraints();
                    gbcl.gridheight = 2;
                    gbcl.insets = new Insets(5, 5, 5, 10);
                    gbcl.anchor = GridBagConstraints.NORTHWEST;
                    upperPanel.add(new JLabel(ApplicationUtilities.getImage("upperpanel.gif")),
                        gbcl);
                }

                {// Explanation
                    GridBagConstraints gbcl = new GridBagConstraints();
                    gbcl.weightx = 1;
                    gbcl.insets = new Insets(0, 0, 5, 0);
                    gbcl.fill = GridBagConstraints.HORIZONTAL;
                    gbcl.anchor = GridBagConstraints.WEST;
                    upperPanel.add(
                        new MultilineLabel(ApplicationUtilities
                            .getResourceString("options.view.upperPanel.explanation")), gbcl);
                }

                {// Selections
                    JPanel selections = new JPanel(new GridLayout(0, 2, 5, 5));

                    chkDOMPanel = new JCheckBox(
                        ApplicationUtilities.getResourceString("options.view.upperPanel.domPanel"),
                        Boolean.valueOf(
                            (String) options.getOptionValue(UpperPanel.DOM_PANEL_VISIBLE_PROPERTY))
                            .booleanValue());
                    chkDOMPanel.setToolTipText(ApplicationUtilities
                        .getResourceString("options.view.upperPanel.domPanel.tooltip"));
                    chkDOMPanel.setMnemonic(KeyStroke.getKeyStroke(
                        ApplicationUtilities
                            .getResourceString("options.view.upperPanel.domPanel.mnemonic"))
                        .getKeyCode());
                    CSH.setHelpIDString(chkDOMPanel, ApplicationUtilities
                        .getResourceString("options.view.upperPanel.domPanel.helpID"));
                    selections.add(chkDOMPanel);

                    chkHyperbolicDOMPanel = new JCheckBox(
                        ApplicationUtilities
                            .getResourceString("options.view.upperPanel.hyperbolicDOMPanel"),
                        Boolean.valueOf(
                            (String) options
                                .getOptionValue(UpperPanel.HYPERBOLICDOM_PANEL_VISIBLE_PROPERTY))
                            .booleanValue());
                    chkHyperbolicDOMPanel.setToolTipText(ApplicationUtilities
                        .getResourceString("options.view.upperPanel.hyperbolicDOMPanel.tooltip"));
                    chkHyperbolicDOMPanel
                        .setMnemonic(KeyStroke
                            .getKeyStroke(
                                ApplicationUtilities
                                    .getResourceString("options.view.upperPanel.hyperbolicDOMPanel.mnemonic"))
                            .getKeyCode());
                    CSH.setHelpIDString(chkHyperbolicDOMPanel, ApplicationUtilities
                        .getResourceString("options.view.upperPanel.hyperbolicDOMPanel.helpID"));
                    selections.add(chkHyperbolicDOMPanel);

                    chkThesaurusPanel = new JCheckBox(
                        ApplicationUtilities
                            .getResourceString("options.view.upperPanel.thesaurusPanel"),
                        Boolean.valueOf(
                            (String) options
                                .getOptionValue(UpperPanel.THESAURUS_PANEL_VISIBLE_PROPERTY))
                            .booleanValue());
                    chkThesaurusPanel.setToolTipText(ApplicationUtilities
                        .getResourceString("options.view.upperPanel.thesaurusPanel.tooltip"));
                    chkThesaurusPanel.setMnemonic(KeyStroke.getKeyStroke(
                        ApplicationUtilities
                            .getResourceString("options.view.upperPanel.thesaurusPanel.mnemonic"))
                        .getKeyCode());
                    CSH.setHelpIDString(chkThesaurusPanel, ApplicationUtilities
                        .getResourceString("options.view.upperPanel.thesaurusPanel.helpID"));
                    selections.add(chkThesaurusPanel);

                    GridBagConstraints gbcl = new GridBagConstraints();
                    gbcl.gridy = 1;
                    gbcl.gridx = 1;
                    gbcl.insets = new Insets(5, 5, 5, 5);
                    gbcl.anchor = GridBagConstraints.WEST;
                    upperPanel.add(selections, gbcl);
                }
            }

            {// Separator
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridy = 2;
                gbc.gridheight = GridBagConstraints.REMAINDER;
                gbc.fill = GridBagConstraints.VERTICAL;
                gbc.weightx = 1.0;
                gbc.weighty = 1.0;
                view.add(new JPanel(), gbc);
            }
        }

        // Browser
        {
            JPanel browser = new JPanel(new GridBagLayout());
            panels.add(ApplicationUtilities.getResourceString("options.panel.browser"), browser);

            {// History
                JPanel history = new JPanel(new GridBagLayout());
                history.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(Color.black),
                    ApplicationUtilities.getResourceString("options.browser.history")));
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.insets = new Insets(5, 5, 5, 5);
                browser.add(history, gbc);

                {// Icon
                    GridBagConstraints gbcl = new GridBagConstraints();
                    gbcl.gridheight = 3;
                    gbcl.insets = new Insets(5, 5, 5, 10);
                    gbcl.anchor = GridBagConstraints.NORTHWEST;
                    history.add(new JLabel(ApplicationUtilities.getImage("history.gif")), gbcl);
                }

                {// Explanation
                    GridBagConstraints gbcl = new GridBagConstraints();
                    gbcl.weightx = 1;
                    gbcl.gridwidth = 3;
                    gbcl.insets = new Insets(0, 0, 5, 0);
                    gbcl.fill = GridBagConstraints.HORIZONTAL;
                    gbcl.anchor = GridBagConstraints.WEST;
                    history.add(
                        new MultilineLabel(ApplicationUtilities
                            .getResourceString("options.browser.history.explanation")), gbcl);
                }

                JLabel entriesLabel = new JLabel(
                    ApplicationUtilities.getResourceString("options.browser.history.entries"));
                entriesLabel.setDisplayedMnemonic(KeyStroke.getKeyStroke(
                    ApplicationUtilities
                        .getResourceString("options.browser.history.entries.mnemonic"))
                    .getKeyCode());
                CSH.setHelpIDString(entriesLabel, ApplicationUtilities
                    .getResourceString("options.browser.history.entries.helpID"));
                {// Entries label
                    GridBagConstraints gbcl = new GridBagConstraints();
                    gbcl.gridx = 1;
                    gbcl.gridy = 1;
                    gbcl.anchor = GridBagConstraints.WEST;
                    history.add(entriesLabel, gbcl);
                }

                {// Entries
                    GridBagConstraints gbcl = new GridBagConstraints();
                    gbcl.gridx = 2;
                    gbcl.gridy = 1;
                    gbcl.insets = new Insets(0, 5, 0, 5);
                    int entries = 1;
                    try
                    {
                        entries = Integer.parseInt((String) options
                            .getOptionValue(Browser.ADDRESS_HISTORY_ENTRIES_PROPERTY));
                    }
                    catch (NumberFormatException e)
                    {
                    }
                    spnHistoryEntries = new JSpinner(new SpinnerNumberModel(new Integer(entries),
                        new Integer(0), new Integer(100), new Integer(1)));
                    spnHistoryEntries.setToolTipText(ApplicationUtilities
                        .getResourceString("options.browser.history.entries.tooltip"));
                    entriesLabel.setLabelFor(spnHistoryEntries);
                    CSH.setHelpIDString(spnHistoryEntries, ApplicationUtilities
                        .getResourceString("options.browser.history.entries.helpID"));
                    history.add(spnHistoryEntries, gbcl);
                }

                {// Button
                    GridBagConstraints gbcl = new GridBagConstraints();
                    gbcl.gridx = 3;
                    gbcl.gridy = 1;
                    JButton clearHistory = new JButton(
                        ApplicationUtilities.getResourceString("options.browser.history.clear"));
                    clearHistory.setToolTipText(ApplicationUtilities
                        .getResourceString("options.browser.history.clear.tooltip"));
                    clearHistory.setMnemonic(KeyStroke.getKeyStroke(
                        ApplicationUtilities
                            .getResourceString("options.browser.history.clear.mnemonic"))
                        .getKeyCode());
                    CSH.setHelpIDString(clearHistory, ApplicationUtilities
                        .getResourceString("options.browser.history.clear.helpID"));
                    history.add(clearHistory, gbcl);
                    clearHistory.addActionListener(new ActionListener()
                    {
                        public void actionPerformed(ActionEvent e)
                        {
                            options.tickleOption(Browser.ADDRESS_HISTORY_CLEAR_PROPERTY);
                        }
                    });
                }
            }

            {// Navigation
                JPanel navigation = new JPanel(new GridBagLayout());
                navigation.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(Color.black),
                    ApplicationUtilities.getResourceString("options.browser.navigation")));
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridy = 1;
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.insets = new Insets(5, 5, 5, 5);
                browser.add(navigation, gbc);

                {// Icon
                    GridBagConstraints gbcl = new GridBagConstraints();
                    gbcl.gridheight = 2;
                    gbcl.insets = new Insets(5, 5, 5, 10);
                    gbcl.anchor = GridBagConstraints.NORTHWEST;
                    navigation.add(new JLabel(ApplicationUtilities.getImage("navigation.gif")),
                        gbcl);
                }

                {// Explanation
                    GridBagConstraints gbcl = new GridBagConstraints();
                    gbcl.weightx = 1;
                    gbcl.insets = new Insets(0, 0, 5, 0);
                    gbcl.fill = GridBagConstraints.HORIZONTAL;
                    gbcl.anchor = GridBagConstraints.WEST;
                    navigation.add(
                        new MultilineLabel(ApplicationUtilities
                            .getResourceString("options.browser.navigation.explanation")), gbcl);
                }

                {// Selections
                    JPanel selections = new JPanel(new GridLayout(0, 1, 5, 5));

                    chkMETANavigation = new JCheckBox(
                        ApplicationUtilities
                            .getResourceString("options.browser.navigation.metaNavigation"),
                        Boolean.valueOf(
                            (String) options
                                .getOptionValue(HTMLUtilitiesGui.HTML_META_NAVIGATION_PROPERTY))
                            .booleanValue());
                    chkMETANavigation.setToolTipText(ApplicationUtilities
                        .getResourceString("options.browser.navigation.metaNavigation"));
                    chkMETANavigation
                        .setMnemonic(KeyStroke
                            .getKeyStroke(
                                ApplicationUtilities
                                    .getResourceString("options.browser.navigation.metaNavigation.mnemonic"))
                            .getKeyCode());
                    CSH.setHelpIDString(chkMETANavigation, ApplicationUtilities
                        .getResourceString("options.browser.navigation.metaNavigation.helpID"));
                    selections.add(chkMETANavigation);

                    JLabel lblConnectionTimeout = new JLabel(
                        ApplicationUtilities
                            .getResourceString("options.browser.navigation.connectionTimeout"),
                        JLabel.LEFT);
                    lblConnectionTimeout
                        .setDisplayedMnemonic(KeyStroke
                            .getKeyStroke(
                                ApplicationUtilities
                                    .getResourceString("options.browser.navigation.connectionTimeout.mnemonic"))
                            .getKeyCode());
                    CSH.setHelpIDString(lblConnectionTimeout, ApplicationUtilities
                        .getResourceString("options.browser.navigation.connectionTimeout.helpID"));
                    long millis = -1;
                    try
                    {
                        millis = Long.parseLong((String) options
                            .getOptionValue(NetworkUtilitiesPropertiesEnum.CONNECTION_TIMEOUT_PROPERTY.getValue()));
                    }
                    catch (NumberFormatException e)
                    {
                    }
                    spnConnectionTimeout = new JSpinner(new SpinnerNumberModel(new Long(millis),
                        new Long(-1), new Long(500), new Long(1)));
                    spnConnectionTimeout.setToolTipText(ApplicationUtilities
                        .getResourceString("options.browser.navigation.connectionTimeout.tooltip"));
                    lblConnectionTimeout.setLabelFor(spnConnectionTimeout);
                    CSH.setHelpIDString(spnConnectionTimeout, ApplicationUtilities
                        .getResourceString("options.browser.navigation.connectionTimeout.helpID"));
                    JPanel connectionTimeout = new JPanel();
                    connectionTimeout.add(lblConnectionTimeout);
                    connectionTimeout.add(spnConnectionTimeout);
                    selections.add(connectionTimeout);

                    GridBagConstraints gbcl = new GridBagConstraints();
                    gbcl.gridy = 1;
                    gbcl.gridx = 1;
                    gbcl.insets = new Insets(5, 5, 5, 5);
                    gbcl.anchor = GridBagConstraints.WEST;
                    navigation.add(selections, gbcl);
                }
            }

            {// Proxy
                JPanel proxy = new JPanel(new GridBagLayout());
                proxy.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(Color.black),
                    ApplicationUtilities.getResourceString("options.browser.proxy")));
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridy = 2;
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.insets = new Insets(5, 5, 5, 5);
                browser.add(proxy, gbc);

                {// Icon
                    GridBagConstraints gbcl = new GridBagConstraints();
                    gbcl.gridheight = 2;
                    gbcl.insets = new Insets(5, 5, 5, 10);
                    gbcl.anchor = GridBagConstraints.NORTHWEST;
                    proxy.add(new JLabel(ApplicationUtilities.getImage("proxy.gif")), gbcl);
                }

                {// Explanation
                    GridBagConstraints gbcl = new GridBagConstraints();
                    gbcl.weightx = 1;
                    gbcl.insets = new Insets(0, 0, 5, 0);
                    gbcl.fill = GridBagConstraints.HORIZONTAL;
                    gbcl.anchor = GridBagConstraints.WEST;
                    proxy.add(
                        new MultilineLabel(ApplicationUtilities
                            .getResourceString("options.browser.proxy.explanation")), gbcl);
                }

                {// Settings
                    JPanel settings = new JPanel(new GridBagLayout());

                    final JLabel lblProxyHost = new JLabel(
                        ApplicationUtilities.getResourceString("options.browser.proxy.proxyHost"),
                        JLabel.LEFT);
                    final JLabel lblProxyPort = new JLabel(
                        ApplicationUtilities.getResourceString("options.browser.proxy.proxyPort"),
                        JLabel.LEFT);

                    { // User proxy
                        GridBagConstraints gbcl = new GridBagConstraints();
                        gbcl.gridwidth = 2;
                        gbcl.weightx = 1;
                        gbcl.fill = GridBagConstraints.HORIZONTAL;
                        gbcl.anchor = GridBagConstraints.NORTHWEST;
                        chkUseProxy = new JCheckBox(
                            ApplicationUtilities
                                .getResourceString("options.browser.proxy.useProxy"),
                            Boolean.valueOf(
                                (String) options
                                    .getOptionValue(NetworkUtilitiesPropertiesEnum.USE_PROXY_PROPERTY.getValue()))
                                .booleanValue());
                        chkUseProxy.setToolTipText(ApplicationUtilities
                            .getResourceString("options.browser.proxy.useProxy"));
                        chkUseProxy.setMnemonic(KeyStroke.getKeyStroke(
                            ApplicationUtilities
                                .getResourceString("options.browser.proxy.useProxy.mnemonic"))
                            .getKeyCode());
                        CSH.setHelpIDString(chkUseProxy, ApplicationUtilities
                            .getResourceString("options.browser.proxy.useProxy.helpID"));
                        chkUseProxy.addActionListener(new ActionListener()
                        {
                            public void actionPerformed(ActionEvent e)
                            {
                                boolean enabled = ((JCheckBox) e.getSource()).isSelected();
                                lblProxyHost.setEnabled(enabled);
                                lblProxyPort.setEnabled(enabled);
                                txtProxyHost.setEnabled(enabled);
                                txtProxyPort.setEnabled(enabled);
                            }
                        });
                        settings.add(chkUseProxy, gbcl);
                    }

                    { // Host label
                        GridBagConstraints gbcl = new GridBagConstraints();
                        gbcl.gridy = 1;
                        gbcl.anchor = GridBagConstraints.EAST;
                        lblProxyHost.setDisplayedMnemonic(KeyStroke.getKeyStroke(
                            ApplicationUtilities
                                .getResourceString("options.browser.proxy.proxyHost.mnemonic"))
                            .getKeyCode());
                        lblProxyHost.setEnabled(chkUseProxy.isSelected());
                        lblProxyHost.setLabelFor(txtProxyHost);
                        settings.add(lblProxyHost, gbcl);
                    }

                    { // Host
                        GridBagConstraints gbcl = new GridBagConstraints();
                        gbcl.gridx = 1;
                        gbcl.gridy = 1;
                        gbcl.weightx = 1;
                        gbcl.anchor = GridBagConstraints.WEST;
                        gbcl.insets = new Insets(0, 5, 0, 5);
                        CSH.setHelpIDString(lblProxyHost, ApplicationUtilities
                            .getResourceString("options.browser.proxy.proxyHost.helpID"));
                        txtProxyHost = new TextField(
                            (String) options.getOptionValue(NetworkUtilitiesPropertiesEnum.PROXY_HOST_PROPERTY.getValue()),
                            25);
                        txtProxyHost.setEnabled(chkUseProxy.isSelected());
                        txtProxyHost.setToolTipText(ApplicationUtilities
                            .getResourceString("options.browser.proxy.proxyHost.tooltip"));
                        CSH.setHelpIDString(txtProxyHost, ApplicationUtilities
                            .getResourceString("options.browser.proxy.proxyHost.helpID"));
                        settings.add(txtProxyHost, gbcl);
                    }

                    { // Port label
                        GridBagConstraints gbcl = new GridBagConstraints();
                        gbcl.gridy = 2;
                        gbcl.anchor = GridBagConstraints.EAST;
                        lblProxyPort.setDisplayedMnemonic(KeyStroke.getKeyStroke(
                            ApplicationUtilities
                                .getResourceString("options.browser.proxy.proxyPort.mnemonic"))
                            .getKeyCode());
                        lblProxyPort.setEnabled(chkUseProxy.isSelected());
                        lblProxyPort.setLabelFor(txtProxyPort);
                        settings.add(lblProxyPort, gbcl);
                    }

                    { // Port
                        GridBagConstraints gbcl = new GridBagConstraints();
                        gbcl.gridx = 1;
                        gbcl.gridy = 2;
                        gbcl.weightx = 1;
                        gbcl.anchor = GridBagConstraints.WEST;
                        gbcl.insets = new Insets(0, 5, 0, 5);
                        CSH.setHelpIDString(lblProxyPort, ApplicationUtilities
                            .getResourceString("options.browser.proxy.proxyPort.helpID"));
                        txtProxyPort = new TextField(
                            (String) options.getOptionValue(NetworkUtilitiesPropertiesEnum.PROXY_PORT_PROPERTY.getValue()),
                            10);
                        txtProxyPort.setEnabled(chkUseProxy.isSelected());
                        txtProxyPort.setToolTipText(ApplicationUtilities
                            .getResourceString("options.browser.proxy.proxyPort.tooltip"));
                        CSH.setHelpIDString(txtProxyPort, ApplicationUtilities
                            .getResourceString("options.browser.proxy.proxyPort.helpID"));
                        settings.add(txtProxyPort, gbcl);
                    }

                    GridBagConstraints gbcl = new GridBagConstraints();
                    gbcl.gridy = 1;
                    gbcl.gridx = 1;
                    gbcl.insets = new Insets(5, 5, 5, 5);
                    gbcl.anchor = GridBagConstraints.WEST;
                    proxy.add(settings, gbcl);
                }
            }

            {// Separator
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridy = 3;
                gbc.gridheight = GridBagConstraints.REMAINDER;
                gbc.fill = GridBagConstraints.VERTICAL;
                gbc.weightx = 1.0;
                gbc.weighty = 1.0;
                browser.add(new JPanel(), gbc);
            }
        }

        // File
        {
            JPanel file = new JPanel(new GridBagLayout());
            panels.add(ApplicationUtilities.getResourceString("options.panel.file"), file);

            {// File Dialog
                JPanel filePreview = new JPanel(new GridBagLayout());
                filePreview.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(Color.black),
                    ApplicationUtilities.getResourceString("options.file.dialog")));
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridy = 0;
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.insets = new Insets(5, 5, 5, 5);
                file.add(filePreview, gbc);

                {// Icon
                    GridBagConstraints gbcl = new GridBagConstraints();
                    gbcl.gridheight = 2;
                    gbcl.insets = new Insets(5, 5, 5, 10);
                    gbcl.anchor = GridBagConstraints.NORTHWEST;
                    filePreview.add(new JLabel(ApplicationUtilities.getImage("filepreview.gif")),
                        gbcl);
                }

                { // Explanation
                    GridBagConstraints gbcl = new GridBagConstraints();
                    gbcl.weightx = 1;
                    gbcl.insets = new Insets(0, 0, 5, 0);
                    gbcl.fill = GridBagConstraints.HORIZONTAL;
                    gbcl.anchor = GridBagConstraints.WEST;
                    filePreview.add(
                        new MultilineLabel(ApplicationUtilities
                            .getResourceString("options.file.dialog.explanation")), gbcl);
                }

                {// Selections
                    JPanel selections = new JPanel(new GridLayout(0, 2, 5, 5));

                    chkFilePreview = new JCheckBox(
                        ApplicationUtilities.getResourceString("options.file.dialog.preview"),
                        Boolean.valueOf(
                            (String) options
                                .getOptionValue(FileUtilities.PREVIEW_PANEL_VISIBLE_PROPERTY))
                            .booleanValue());
                    chkFilePreview.setToolTipText(ApplicationUtilities
                        .getResourceString("options.file.dialog.preview.tooltip"));
                    chkFilePreview.setMnemonic(KeyStroke.getKeyStroke(
                        ApplicationUtilities
                            .getResourceString("options.file.dialog.preview.mnemonic"))
                        .getKeyCode());
                    CSH.setHelpIDString(chkFilePreview, ApplicationUtilities
                        .getResourceString("options.file.dialog.preview.helpID"));
                    selections.add(chkFilePreview);

                    GridBagConstraints gbcl = new GridBagConstraints();
                    gbcl.gridy = 1;
                    gbcl.gridx = 1;
                    gbcl.insets = new Insets(5, 5, 5, 5);
                    gbcl.anchor = GridBagConstraints.WEST;
                    filePreview.add(selections, gbcl);
                }
            }

            {// Separator
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridy = 1;
                gbc.gridheight = GridBagConstraints.REMAINDER;
                gbc.fill = GridBagConstraints.VERTICAL;
                gbc.weightx = 1.0;
                gbc.weighty = 1.0;
                file.add(new JPanel(), gbc);
            }
        }

        // Import/Export
        {
            JPanel impexp = new JPanel(new GridBagLayout());
            panels.add(ApplicationUtilities.getResourceString("options.panel.impexp"), impexp);

            {// Importers
                JPanel importers = new JPanel(new GridBagLayout());
                importers.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(Color.black),
                    ApplicationUtilities.getResourceString("options.importers")));
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridy = 0;
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.insets = new Insets(5, 5, 5, 5);
                impexp.add(importers, gbc);

                {// Icon
                    GridBagConstraints gbcl = new GridBagConstraints();
                    gbcl.gridheight = 2;
                    gbcl.insets = new Insets(5, 5, 5, 10);
                    gbcl.anchor = GridBagConstraints.NORTHWEST;
                    importers.add(new JLabel(ApplicationUtilities.getImage("import.gif")), gbcl);
                }

                { // Explanation
                    GridBagConstraints gbcl = new GridBagConstraints();
                    gbcl.weightx = 1;
                    gbcl.insets = new Insets(0, 0, 5, 0);
                    gbcl.fill = GridBagConstraints.HORIZONTAL;
                    gbcl.anchor = GridBagConstraints.WEST;
                    importers.add(
                        new MultilineLabel(ApplicationUtilities
                            .getResourceString("options.importers.explanation")), gbcl);
                }

                {// Algorithms
                    JList importersList = new JList(ImportUtilities.getImportersInfo());

                    GridBagConstraints gbcl = new GridBagConstraints();
                    gbcl.gridy = 1;
                    gbcl.gridx = 1;
                    gbcl.weightx = 1;
                    gbcl.weighty = 1;
                    gbcl.fill = GridBagConstraints.BOTH;
                    gbcl.insets = new Insets(5, 5, 5, 5);
                    gbcl.anchor = GridBagConstraints.WEST;
                    importers.add(new JScrollPane(importersList), gbcl);
                }
            }

            {// Exporters
                JPanel exporters = new JPanel(new GridBagLayout());
                exporters.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(Color.black),
                    ApplicationUtilities.getResourceString("options.exporters")));
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridy = 1;
                //gbc.gridx = 0;
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.insets = new Insets(5, 5, 5, 5);
                impexp.add(exporters, gbc);

                {// Icon
                    GridBagConstraints gbcl = new GridBagConstraints();
                    gbcl.gridheight = 2;
                    gbcl.insets = new Insets(5, 5, 5, 10);
                    gbcl.anchor = GridBagConstraints.NORTHWEST;
                    exporters.add(new JLabel(ApplicationUtilities.getImage("export.gif")), gbcl);
                }

                { // Explanation
                    GridBagConstraints gbcl = new GridBagConstraints();
                    gbcl.weightx = 1;
                    gbcl.gridwidth = 2;
                    gbcl.insets = new Insets(0, 0, 5, 0);
                    gbcl.fill = GridBagConstraints.HORIZONTAL;
                    gbcl.anchor = GridBagConstraints.WEST;
                    exporters.add(
                        new MultilineLabel(ApplicationUtilities
                            .getResourceString("options.exporters.explanation")), gbcl);
                }

                {// Algorithms
                    JList exportersList = new JList(ExportUtilities.getExportersInfo());

                    GridBagConstraints gbcl = new GridBagConstraints();
                    gbcl.gridy = 1;
                    gbcl.gridx = 1;
                    //gbcl.gridx = 2;
                    gbcl.weightx = 1;
                    gbcl.weighty = 1;
                    gbcl.fill = GridBagConstraints.BOTH;
                    gbcl.insets = new Insets(5, 5, 5, 5);
                    gbcl.anchor = GridBagConstraints.WEST;
                    exporters.add(new JScrollPane(exportersList), gbcl);
                }
            }

            {// Separator
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridy = 2;
                // gbc.gridheight=GridBagConstraints.REMAINDER;
                gbc.fill = GridBagConstraints.VERTICAL;
                gbc.weightx = 1;
                gbc.weighty = 1;
                impexp.add(new JPanel(), gbc);
            }
        }

        // Algorithms
        {
            JPanel algorithms = new JPanel(new GridBagLayout());
            panels.add(ApplicationUtilities.getResourceString("options.panel.algorithms"),
                algorithms);

            {// Match Algorithms
                JPanel matchAlgorithms = new JPanel(new GridBagLayout());
                matchAlgorithms.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(Color.black),
                    ApplicationUtilities.getResourceString("options.algorithm.matchAlgorithms")));
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridy = 0;
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.insets = new Insets(5, 5, 5, 5);
                algorithms.add(matchAlgorithms, gbc);

                {// Icon
                    GridBagConstraints gbcl = new GridBagConstraints();
                    gbcl.gridheight = 2;
                    gbcl.insets = new Insets(5, 5, 5, 10);
                    gbcl.anchor = GridBagConstraints.NORTHWEST;
                    matchAlgorithms.add(new JLabel(ApplicationUtilities.getImage("algorithm.gif")),
                        gbcl);
                }

                { // Explanation
                    GridBagConstraints gbcl = new GridBagConstraints();
                    gbcl.weightx = 1;
                    gbcl.insets = new Insets(0, 0, 5, 0);
                    gbcl.fill = GridBagConstraints.HORIZONTAL;
                    gbcl.anchor = GridBagConstraints.WEST;
                    matchAlgorithms.add(
                        new MultilineLabel(ApplicationUtilities
                            .getResourceString("options.algorithm.matchAlgorithms.explanation")),
                        gbcl);
                }

                {// Algorithms
                    JList algorithmsList = new JList(ontoBuilder.algorithms);

                    GridBagConstraints gbcl = new GridBagConstraints();
                    gbcl.gridy = 1;
                    gbcl.gridx = 1;
                    gbcl.weightx = 1;
                    gbcl.weighty = 1;
                    gbcl.fill = GridBagConstraints.BOTH;
                    gbcl.insets = new Insets(5, 5, 5, 5);
                    gbcl.anchor = GridBagConstraints.WEST;
                    matchAlgorithms.add(new JScrollPane(algorithmsList), gbcl);
                }
            }

            {// Match Threshold
                JPanel matchThreshold = new JPanel(new GridBagLayout());
                matchThreshold.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(Color.black),
                    ApplicationUtilities.getResourceString("options.algorithm.matchThreshold")));
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridy = 1;
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.insets = new Insets(5, 5, 5, 5);
                algorithms.add(matchThreshold, gbc);

                {// Icon
                    GridBagConstraints gbcl = new GridBagConstraints();
                    gbcl.gridheight = 2;
                    gbcl.insets = new Insets(5, 5, 5, 10);
                    gbcl.anchor = GridBagConstraints.NORTHWEST;
                    matchThreshold.add(new JLabel(ApplicationUtilities.getImage("threshold.gif")),
                        gbcl);
                }

                { // Explanation
                    GridBagConstraints gbcl = new GridBagConstraints();
                    gbcl.weightx = 1;
                    gbcl.gridwidth = 2;
                    gbcl.insets = new Insets(0, 0, 5, 0);
                    gbcl.fill = GridBagConstraints.HORIZONTAL;
                    gbcl.anchor = GridBagConstraints.WEST;
                    matchThreshold.add(
                        new MultilineLabel(ApplicationUtilities
                            .getResourceString("options.algorithm.matchThreshold.explanation")),
                        gbcl);
                }

                JLabel thresholdLabel = new JLabel(
                    ApplicationUtilities
                        .getResourceString("options.algorithm.matchThreshold.threshold"));
                thresholdLabel.setDisplayedMnemonic(KeyStroke.getKeyStroke(
                    ApplicationUtilities
                        .getResourceString("options.algorithm.matchThreshold.threshold.mnemonic"))
                    .getKeyCode());
                CSH.setHelpIDString(thresholdLabel, ApplicationUtilities
                    .getResourceString("options.algorithm.matchThreshold.threshold.helpID"));
                {// Threshold label
                    GridBagConstraints gbcl = new GridBagConstraints();
                    gbcl.gridx = 1;
                    gbcl.gridy = 1;
                    gbcl.anchor = GridBagConstraints.WEST;
                    matchThreshold.add(thresholdLabel, gbcl);
                }

                {// Threshold
                    GridBagConstraints gbcl = new GridBagConstraints();
                    gbcl.gridx = 2;
                    gbcl.gridy = 1;
                    gbcl.insets = new Insets(0, 5, 0, 5);
                    gbcl.anchor = GridBagConstraints.WEST;
                    int threshold = 0;
                    try
                    {
                        threshold = Integer.parseInt((String) options
                            .getOptionValue(Algorithm.MATCH_THRESHOLD_PROPERTY));
                    }
                    catch (NumberFormatException e)
                    {
                    }
                    spnMatchThreshold = new JSpinner(new SpinnerNumberModel(new Integer(threshold),
                        new Integer(0), new Integer(100), new Integer(1)));
                    spnMatchThreshold.setToolTipText(ApplicationUtilities
                        .getResourceString("options.algorithm.matchThreshold.threshold.tooltip"));
                    thresholdLabel.setLabelFor(spnMatchThreshold);
                    CSH.setHelpIDString(spnMatchThreshold, ApplicationUtilities
                        .getResourceString("options.algorithm.matchThreshold.threshold.helpID"));
                    matchThreshold.add(spnMatchThreshold, gbcl);
                }
            }

            {// Separator
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridy = 2;
                gbc.gridheight = GridBagConstraints.REMAINDER;
                gbc.fill = GridBagConstraints.VERTICAL;
                gbc.weightx = 1.0;
                gbc.weighty = 1.0;
                algorithms.add(new JPanel(), gbc);
            }
        }

        // Graph
        {
            JPanel graphs = new JPanel(new GridBagLayout());
            panels.add(ApplicationUtilities.getResourceString("options.panel.graphs"), graphs);

            {// Visualization Options
                JPanel visualization = new JPanel(new GridBagLayout());
                visualization.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(Color.black),
                    ApplicationUtilities.getResourceString("options.graphs.visualization")));
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridy = 0;
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.insets = new Insets(5, 5, 5, 5);
                graphs.add(visualization, gbc);

                {// Icon
                    GridBagConstraints gbcl = new GridBagConstraints();
                    gbcl.gridheight = 2;
                    gbcl.insets = new Insets(5, 5, 5, 10);
                    gbcl.anchor = GridBagConstraints.NORTHWEST;
                    visualization.add(new JLabel(ApplicationUtilities.getImage("graph.gif")), gbcl);
                }

                { // Explanation
                    GridBagConstraints gbcl = new GridBagConstraints();
                    gbcl.weightx = 1;
                    gbcl.insets = new Insets(0, 0, 5, 0);
                    gbcl.fill = GridBagConstraints.HORIZONTAL;
                    gbcl.anchor = GridBagConstraints.WEST;
                    visualization.add(
                        new MultilineLabel(ApplicationUtilities
                            .getResourceString("options.graphs.visualization.explanation")), gbcl);
                }

                {// Selections
                    JPanel selections = new JPanel(new GridLayout(0, 2, 5, 5));
                    // View precedence links
                    chkGraphPrecedence = new JCheckBox(
                        ApplicationUtilities
                            .getResourceString("options.graphs.visualization.precedence"),
                        Boolean.valueOf(
                            (String) options
                                .getOptionValue(GraphUtilities.SHOW_PRECEDENCE_LINKS_PROPERTY))
                            .booleanValue());
                    chkGraphPrecedence.setToolTipText(ApplicationUtilities
                        .getResourceString("options.graphs.visualization.precedence.tooltip"));
                    chkGraphPrecedence.setMnemonic(KeyStroke.getKeyStroke(
                        ApplicationUtilities
                            .getResourceString("options.graphs.visualization.precedence.mnemonic"))
                        .getKeyCode());
                    CSH.setHelpIDString(chkGraphPrecedence, ApplicationUtilities
                        .getResourceString("options.graphs.visualization.precedence.helpID"));
                    selections.add(chkGraphPrecedence);

                    GridBagConstraints gbcl = new GridBagConstraints();
                    gbcl.gridy = 1;
                    gbcl.gridx = 1;
                    gbcl.insets = new Insets(5, 5, 5, 5);
                    gbcl.anchor = GridBagConstraints.WEST;
                    visualization.add(selections, gbcl);

                    // View hidden elements
                    chkGraphHidden = new JCheckBox(
                        ApplicationUtilities
                            .getResourceString("options.graphs.visualization.hidden"),
                        Boolean.valueOf(
                            (String) options
                                .getOptionValue(GraphUtilities.SHOW_HIDDEN_ELEMENTS_PROPERTY))
                            .booleanValue());
                    chkGraphHidden.setToolTipText(ApplicationUtilities
                        .getResourceString("options.graphs.visualization.hidden.tooltip"));
                    chkGraphHidden.setMnemonic(KeyStroke.getKeyStroke(
                        ApplicationUtilities
                            .getResourceString("options.graphs.visualization.hidden.mnemonic"))
                        .getKeyCode());
                    CSH.setHelpIDString(chkGraphHidden, ApplicationUtilities
                        .getResourceString("options.graphs.visualization.hidden.helpID"));
                    selections.add(chkGraphHidden);

                    gbcl = new GridBagConstraints();
                    gbcl.gridy = 1;
                    gbcl.gridx = 1;
                    gbcl.insets = new Insets(5, 10, 5, 5);
                    gbcl.anchor = GridBagConstraints.WEST;
                    visualization.add(selections, gbcl);
                }

            }

            {// Separator
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridy = 1;
                gbc.gridheight = GridBagConstraints.REMAINDER;
                gbc.fill = GridBagConstraints.VERTICAL;
                gbc.weightx = 1.0;
                gbc.weighty = 1.0;
                graphs.add(new JPanel(), gbc);
            }
        }

        // BizTalk
        {
            JPanel biztalk = new JPanel(new GridBagLayout());
            panels.add(ApplicationUtilities.getResourceString("options.panel.biztalk"), biztalk);

            {// Naming Options
                JPanel naming = new JPanel(new GridBagLayout());
                naming.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(Color.black),
                    ApplicationUtilities.getResourceString("options.biztalk.naming")));
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridy = 0;
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.insets = new Insets(5, 5, 5, 5);
                biztalk.add(naming, gbc);

                {// Icon
                    GridBagConstraints gbcl = new GridBagConstraints();
                    gbcl.gridheight = 2;
                    gbcl.insets = new Insets(5, 5, 5, 10);
                    gbcl.anchor = GridBagConstraints.NORTHWEST;
                    naming.add(new JLabel(ApplicationUtilities.getImage("biztalk.gif")), gbcl);
                }

                { // Explanation
                    GridBagConstraints gbcl = new GridBagConstraints();
                    gbcl.weightx = 1;
                    gbcl.insets = new Insets(0, 0, 5, 0);
                    gbcl.fill = GridBagConstraints.HORIZONTAL;
                    gbcl.anchor = GridBagConstraints.WEST;
                    naming.add(
                        new MultilineLabel(ApplicationUtilities
                            .getResourceString("options.biztalk.naming.explanation")), gbcl);
                }

                {// Selections
                    JPanel selections = new JPanel(new GridLayout(0, 1, 0, 0));

                    ButtonGroup elementGroup = new ButtonGroup();
                    selections.add(new JLabel(ApplicationUtilities
                        .getResourceString("options.biztalk.naming.elementName")));

                    rdoNameLabelName = new JRadioButton(
                        ApplicationUtilities
                            .getResourceString("options.biztalk.naming.element.labelName"),
                        ((String) options
                            .getOptionValue(BizTalkUtilities.NAME_NAMING_TYPE_PROPERTY))
                            .equals("label_name"));
                    rdoNameLabelName.setToolTipText(ApplicationUtilities
                        .getResourceString("options.biztalk.naming.element.labelName.tooltip"));
                    rdoNameLabelName
                        .setMnemonic(KeyStroke
                            .getKeyStroke(
                                ApplicationUtilities
                                    .getResourceString("options.biztalk.naming.element.labelName.mnemonic"))
                            .getKeyCode());
                    CSH.setHelpIDString(rdoNameLabelName, ApplicationUtilities
                        .getResourceString("options.biztalk.naming.element.labelName.helpID"));
                    elementGroup.add(rdoNameLabelName);
                    selections.add(rdoNameLabelName);

                    rdoNameLabel = new JRadioButton(
                        ApplicationUtilities
                            .getResourceString("options.biztalk.naming.element.label"),
                        ((String) options
                            .getOptionValue(BizTalkUtilities.NAME_NAMING_TYPE_PROPERTY))
                            .equals("label"));
                    rdoNameLabel.setToolTipText(ApplicationUtilities
                        .getResourceString("options.biztalk.naming.element.label.tooltip"));
                    rdoNameLabel.setMnemonic(KeyStroke.getKeyStroke(
                        ApplicationUtilities
                            .getResourceString("options.biztalk.naming.element.label.mnemonic"))
                        .getKeyCode());
                    CSH.setHelpIDString(rdoNameLabel, ApplicationUtilities
                        .getResourceString("options.biztalk.naming.element.label.helpID"));
                    elementGroup.add(rdoNameLabel);
                    selections.add(rdoNameLabel);

                    rdoNameName = new JRadioButton(
                        ApplicationUtilities
                            .getResourceString("options.biztalk.naming.element.name"),
                        ((String) options
                            .getOptionValue(BizTalkUtilities.NAME_NAMING_TYPE_PROPERTY))
                            .equals("name"));
                    rdoNameName.setToolTipText(ApplicationUtilities
                        .getResourceString("options.biztalk.naming.element.name.tooltip"));
                    rdoNameName.setMnemonic(KeyStroke.getKeyStroke(
                        ApplicationUtilities
                            .getResourceString("options.biztalk.naming.element.name.mnemonic"))
                        .getKeyCode());
                    CSH.setHelpIDString(rdoNameName, ApplicationUtilities
                        .getResourceString("options.biztalk.naming.element.name.helpID"));
                    elementGroup.add(rdoNameName);
                    selections.add(rdoNameName);

                    ButtonGroup enumerationGroup = new ButtonGroup();
                    selections.add(new JLabel(ApplicationUtilities
                        .getResourceString("options.biztalk.naming.enumerationEntry")));

                    rdoEnumerationLabelValue = new JRadioButton(
                        ApplicationUtilities
                            .getResourceString("options.biztalk.naming.enumeration.labelValue"),
                        ((String) options
                            .getOptionValue(BizTalkUtilities.ENUMERATION_NAMING_TYPE_PROPERTY))
                            .equals("label_value"));
                    rdoEnumerationLabelValue
                        .setToolTipText(ApplicationUtilities
                            .getResourceString("options.biztalk.naming.enumeration.labelValue.tooltip"));
                    rdoEnumerationLabelValue
                        .setMnemonic(KeyStroke
                            .getKeyStroke(
                                ApplicationUtilities
                                    .getResourceString("options.biztalk.naming.enumeration.labelValue.mnemonic"))
                            .getKeyCode());
                    CSH.setHelpIDString(rdoEnumerationLabelValue, ApplicationUtilities
                        .getResourceString("options.biztalk.naming.enumeration.labelValue.helpID"));
                    enumerationGroup.add(rdoEnumerationLabelValue);
                    selections.add(rdoEnumerationLabelValue);

                    rdoEnumerationLabel = new JRadioButton(
                        ApplicationUtilities
                            .getResourceString("options.biztalk.naming.enumeration.label"),
                        ((String) options
                            .getOptionValue(BizTalkUtilities.ENUMERATION_NAMING_TYPE_PROPERTY))
                            .equals("label"));
                    rdoEnumerationLabel.setToolTipText(ApplicationUtilities
                        .getResourceString("options.biztalk.naming.enumeration.label.tooltip"));
                    rdoEnumerationLabel
                        .setMnemonic(KeyStroke
                            .getKeyStroke(
                                ApplicationUtilities
                                    .getResourceString("options.biztalk.naming.enumeration.label.mnemonic"))
                            .getKeyCode());
                    CSH.setHelpIDString(rdoEnumerationLabel, ApplicationUtilities
                        .getResourceString("options.biztalk.naming.enumeration.label.helpID"));
                    enumerationGroup.add(rdoEnumerationLabel);
                    selections.add(rdoEnumerationLabel);

                    rdoEnumerationValue = new JRadioButton(
                        ApplicationUtilities
                            .getResourceString("options.biztalk.naming.enumeration.value"),
                        ((String) options
                            .getOptionValue(BizTalkUtilities.ENUMERATION_NAMING_TYPE_PROPERTY))
                            .equals("value"));
                    rdoEnumerationValue.setToolTipText(ApplicationUtilities
                        .getResourceString("options.biztalk.naming.enumeration.value.tooltip"));
                    rdoEnumerationValue
                        .setMnemonic(KeyStroke
                            .getKeyStroke(
                                ApplicationUtilities
                                    .getResourceString("options.biztalk.naming.enumeration.value.mnemonic"))
                            .getKeyCode());
                    CSH.setHelpIDString(rdoEnumerationValue, ApplicationUtilities
                        .getResourceString("options.biztalk.naming.enumeration.value.helpID"));
                    enumerationGroup.add(rdoEnumerationValue);
                    selections.add(rdoEnumerationValue);

                    GridBagConstraints gbcl = new GridBagConstraints();
                    gbcl.gridy = 1;
                    gbcl.gridx = 1;
                    gbcl.insets = new Insets(5, 5, 5, 5);
                    gbcl.anchor = GridBagConstraints.WEST;
                    naming.add(selections, gbcl);
                }
            }

            {// Separator
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridy = 1;
                gbc.gridheight = GridBagConstraints.REMAINDER;
                gbc.fill = GridBagConstraints.VERTICAL;
                gbc.weightx = 1.0;
                gbc.weighty = 1.0;
                biztalk.add(new JPanel(), gbc);
            }
        }
    }

    /**
     * Apply a command
     */
    protected void commandApply()
    {
        super.commandApply();

        options.setOptionValue(Browser.ADDRESS_HISTORY_ENTRIES_PROPERTY, spnHistoryEntries
            .getValue().toString());
        options.setOptionValue(SiteMap.CRAWLING_DEPTH_PROPERTY, spnCrawlingDepth.getValue()
            .toString());

        options.setOptionValue(Algorithm.MATCH_THRESHOLD_PROPERTY, spnMatchThreshold.getValue()
            .toString());

        options.setOptionValue(MainPanel.HTML_PANEL_VISIBLE_PROPERTY,
            (new Boolean(chkHTMLPanel.isSelected())).toString());
        options.setOptionValue(MainPanel.SOURCE_PANEL_VISIBLE_PROPERTY,
            (new Boolean(chkSourcePanel.isSelected())).toString());
        options.setOptionValue(MainPanel.MESSAGE_PANEL_VISIBLE_PROPERTY, (new Boolean(
            chkMessagePanel.isSelected())).toString());
        options.setOptionValue(MainPanel.SITEMAP_PANEL_VISIBLE_PROPERTY, (new Boolean(
            chkSitemapPanel.isSelected())).toString());
        options.setOptionValue(MainPanel.ELEMENTS_PANEL_VISIBLE_PROPERTY, (new Boolean(
            chkElementsPanel.isSelected())).toString());
        options.setOptionValue(MainPanel.ONTOLOGY_PANEL_VISIBLE_PROPERTY, (new Boolean(
            chkOntologyPanel.isSelected())).toString());
        options.setOptionValue(MainPanel.TOOLS_PANEL_VISIBLE_PROPERTY,
            (new Boolean(chkToolsPanel.isSelected())).toString());

        options.setOptionValue(UpperPanel.DOM_PANEL_VISIBLE_PROPERTY,
            (new Boolean(chkDOMPanel.isSelected())).toString());
        options.setOptionValue(UpperPanel.HYPERBOLICDOM_PANEL_VISIBLE_PROPERTY, (new Boolean(
            chkHyperbolicDOMPanel.isSelected())).toString());
        options.setOptionValue(UpperPanel.THESAURUS_PANEL_VISIBLE_PROPERTY, (new Boolean(
            chkThesaurusPanel.isSelected())).toString());

        options.setOptionValue(DOMUtilities.XML_VALIDATION_PROPERTY,
            (new Boolean(chkXMLValidation.isSelected())).toString());
        options.setOptionValue(HTMLUtilitiesGui.HTML_META_NAVIGATION_PROPERTY, (new Boolean(
            chkMETANavigation.isSelected())).toString());

        options.setOptionValue(FileUtilities.PREVIEW_PANEL_VISIBLE_PROPERTY, (new Boolean(
            chkFilePreview.isSelected())).toString());

        GraphUtilities.setShowPrecedenceLinks(chkGraphPrecedence.isSelected());
        options.setOptionValue(GraphUtilities.SHOW_PRECEDENCE_LINKS_PROPERTY, (new Boolean(
            chkGraphPrecedence.isSelected())).toString());
        GraphUtilities.setShowHiddenElements(chkGraphHidden.isSelected());
        options.setOptionValue(GraphUtilities.SHOW_HIDDEN_ELEMENTS_PROPERTY, (new Boolean(
            chkGraphHidden.isSelected())).toString());

        if (rdoNameLabelName.isSelected())
            options.setOptionValue(BizTalkUtilities.NAME_NAMING_TYPE_PROPERTY, "label_name");
        else if (rdoNameLabel.isSelected())
            options.setOptionValue(BizTalkUtilities.NAME_NAMING_TYPE_PROPERTY, "label");
        else if (rdoNameName.isSelected())
            options.setOptionValue(BizTalkUtilities.NAME_NAMING_TYPE_PROPERTY, "name");

        if (rdoEnumerationLabelValue.isSelected())
            options
                .setOptionValue(BizTalkUtilities.ENUMERATION_NAMING_TYPE_PROPERTY, "label_value");
        else if (rdoEnumerationLabel.isSelected())
            options.setOptionValue(BizTalkUtilities.ENUMERATION_NAMING_TYPE_PROPERTY, "label");
        else if (rdoEnumerationValue.isSelected())
            options.setOptionValue(BizTalkUtilities.ENUMERATION_NAMING_TYPE_PROPERTY, "value");

        options.setOptionValue(NetworkUtilitiesPropertiesEnum.USE_PROXY_PROPERTY.getValue(),
            (new Boolean(chkUseProxy.isSelected())).toString());
        options.setOptionValue(NetworkUtilitiesPropertiesEnum.PROXY_HOST_PROPERTY.getValue(), txtProxyHost.getText());
        options.setOptionValue(NetworkUtilitiesPropertiesEnum.PROXY_PORT_PROPERTY.getValue(), txtProxyPort.getText());

        options.setOptionValue(NetworkUtilitiesPropertiesEnum.CONNECTION_TIMEOUT_PROPERTY.getValue(), spnConnectionTimeout
            .getValue().toString());
    }

    /**
     * Refresh the options
     */
    protected void refreshOptions()
    {
        try
        {
            int entries = Integer.parseInt((String) options
                .getOptionValue(Browser.ADDRESS_HISTORY_ENTRIES_PROPERTY));
            spnHistoryEntries.setValue(new Integer(entries));
        }
        catch (NumberFormatException e)
        {
        }

        try
        {
            int depth = Integer.parseInt((String) options
                .getOptionValue(SiteMap.CRAWLING_DEPTH_PROPERTY));
            spnCrawlingDepth.setValue(new Integer(depth));
        }
        catch (NumberFormatException e)
        {
        }

        try
        {
            int threshold = Integer.parseInt((String) options
                .getOptionValue(Algorithm.MATCH_THRESHOLD_PROPERTY));
            spnMatchThreshold.setValue(new Integer(threshold));
        }
        catch (NumberFormatException e)
        {
        }

        chkHTMLPanel.setSelected(Boolean.valueOf(
            (String) options.getOptionValue(MainPanel.HTML_PANEL_VISIBLE_PROPERTY)).booleanValue());
        chkSourcePanel.setSelected(Boolean.valueOf(
            (String) options.getOptionValue(MainPanel.SOURCE_PANEL_VISIBLE_PROPERTY))
            .booleanValue());
        chkMessagePanel.setSelected(Boolean.valueOf(
            (String) options.getOptionValue(MainPanel.MESSAGE_PANEL_VISIBLE_PROPERTY))
            .booleanValue());
        chkSitemapPanel.setSelected(Boolean.valueOf(
            (String) options.getOptionValue(MainPanel.SITEMAP_PANEL_VISIBLE_PROPERTY))
            .booleanValue());
        chkElementsPanel.setSelected(Boolean.valueOf(
            (String) options.getOptionValue(MainPanel.ELEMENTS_PANEL_VISIBLE_PROPERTY))
            .booleanValue());
        chkOntologyPanel.setSelected(Boolean.valueOf(
            (String) options.getOptionValue(MainPanel.ONTOLOGY_PANEL_VISIBLE_PROPERTY))
            .booleanValue());

        chkDOMPanel.setSelected(Boolean.valueOf(
            (String) options.getOptionValue(UpperPanel.DOM_PANEL_VISIBLE_PROPERTY)).booleanValue());
        chkHyperbolicDOMPanel.setSelected(Boolean.valueOf(
            (String) options.getOptionValue(UpperPanel.HYPERBOLICDOM_PANEL_VISIBLE_PROPERTY))
            .booleanValue());
        chkThesaurusPanel.setSelected(Boolean.valueOf(
            (String) options.getOptionValue(UpperPanel.THESAURUS_PANEL_VISIBLE_PROPERTY))
            .booleanValue());

        chkXMLValidation.setSelected(Boolean.valueOf(
            (String) options.getOptionValue(DOMUtilities.XML_VALIDATION_PROPERTY)).booleanValue());
        chkMETANavigation.setSelected(Boolean.valueOf(
            (String) options.getOptionValue(HTMLUtilitiesGui.HTML_META_NAVIGATION_PROPERTY))
            .booleanValue());

        chkFilePreview.setSelected(Boolean.valueOf(
            (String) options.getOptionValue(FileUtilities.PREVIEW_PANEL_VISIBLE_PROPERTY))
            .booleanValue());

        chkGraphPrecedence.setSelected(Boolean.valueOf(
            (String) options.getOptionValue(GraphUtilities.SHOW_PRECEDENCE_LINKS_PROPERTY))
            .booleanValue());

        String name = (String) options.getOptionValue(BizTalkUtilities.NAME_NAMING_TYPE_PROPERTY);
        rdoNameLabelName.setSelected(name.equals("label_name"));
        rdoNameLabel.setSelected(name.equals("label"));
        rdoNameName.setSelected(name.equals("name"));
        String enumeration = (String) options
            .getOptionValue(BizTalkUtilities.ENUMERATION_NAMING_TYPE_PROPERTY);
        rdoEnumerationLabelValue.setSelected(enumeration.equals("label_value"));
        rdoEnumerationLabel.setSelected(enumeration.equals("label"));
        rdoEnumerationValue.setSelected(enumeration.equals("value"));

        chkUseProxy.setSelected(Boolean.valueOf(
            (String) options.getOptionValue(NetworkUtilitiesPropertiesEnum.USE_PROXY_PROPERTY.getValue())).booleanValue());
        txtProxyHost.setText((String) options.getOptionValue(NetworkUtilitiesPropertiesEnum.PROXY_HOST_PROPERTY.getValue()));
        txtProxyPort.setText((String) options.getOptionValue(NetworkUtilitiesPropertiesEnum.PROXY_PORT_PROPERTY.getValue()));

        try
        {
            long millis = Long.parseLong((String) options
                .getOptionValue(NetworkUtilitiesPropertiesEnum.CONNECTION_TIMEOUT_PROPERTY.getValue()));
            spnConnectionTimeout.setValue(new Long(millis));
        }
        catch (NumberFormatException e)
        {
        }
    }
}