package ac.technion.iem.ontobuilder.gui.ontobuilder.main;

import hypertree.HyperTree;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileView;
import javax.swing.tree.DefaultMutableTreeNode;

import org.w3c.dom.Document;

import ac.technion.iem.ontobuilder.core.biztalk.BizTalkUtilities;
import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.OntologyUtilities;
import ac.technion.iem.ontobuilder.core.ontology.domain.DomainSimilarity;
import ac.technion.iem.ontobuilder.core.ontology.event.OntologyModelAdapter;
import ac.technion.iem.ontobuilder.core.ontology.event.OntologyModelEvent;
import ac.technion.iem.ontobuilder.core.resources.OntoBuilderResources;
import ac.technion.iem.ontobuilder.core.thesaurus.ThesaurusException;
import ac.technion.iem.ontobuilder.core.thesaurus.event.ThesaurusModelAdapter;
import ac.technion.iem.ontobuilder.core.thesaurus.event.ThesaurusModelEvent;
import ac.technion.iem.ontobuilder.core.thesaurus.event.ThesaurusSelectionEvent;
import ac.technion.iem.ontobuilder.core.thesaurus.event.ThesaurusSelectionListener;
import ac.technion.iem.ontobuilder.core.utils.StringUtilities;
import ac.technion.iem.ontobuilder.core.utils.dom.DOMUtilities;
import ac.technion.iem.ontobuilder.core.utils.files.StringOutputStream;
import ac.technion.iem.ontobuilder.core.utils.network.NetworkUtilities;
import ac.technion.iem.ontobuilder.core.utils.network.NetworkUtilitiesPropertiesEnum;
import ac.technion.iem.ontobuilder.core.utils.properties.ApplicationParameters;
import ac.technion.iem.ontobuilder.gui.application.Application;
import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.application.ObjectWithProperties;
import ac.technion.iem.ontobuilder.gui.elements.MultilineLabel;
import ac.technion.iem.ontobuilder.gui.elements.Splash;
import ac.technion.iem.ontobuilder.gui.elements.StatusBar;
import ac.technion.iem.ontobuilder.gui.ontobuilder.elements.LowerPanel;
import ac.technion.iem.ontobuilder.gui.ontobuilder.elements.MainPanel;
import ac.technion.iem.ontobuilder.gui.ontobuilder.elements.OntoBuilderMenuBar;
import ac.technion.iem.ontobuilder.gui.ontobuilder.elements.OntoBuilderOptions;
import ac.technion.iem.ontobuilder.gui.ontobuilder.elements.OntoBuilderToolBar;
import ac.technion.iem.ontobuilder.gui.ontobuilder.elements.OntologyGraph;
import ac.technion.iem.ontobuilder.gui.ontobuilder.elements.OntologyHyperTree;
import ac.technion.iem.ontobuilder.gui.ontobuilder.elements.UpperPanel;
import ac.technion.iem.ontobuilder.gui.ontology.OntologyGui;
import ac.technion.iem.ontobuilder.gui.ontology.OntologySelectionEvent;
import ac.technion.iem.ontobuilder.gui.ontology.OntologySelectionListener;
import ac.technion.iem.ontobuilder.gui.tools.exactmapping.ToolMetadata;
import ac.technion.iem.ontobuilder.gui.tools.exactmapping.ToolsException;
import ac.technion.iem.ontobuilder.gui.tools.exactmapping.ToolsUtilities;
import ac.technion.iem.ontobuilder.gui.tools.mergewizard.OntologyMergeWizard;
import ac.technion.iem.ontobuilder.gui.tools.ontowizard.OntologyWizard;
import ac.technion.iem.ontobuilder.gui.tools.sitemap.SiteMap;
import ac.technion.iem.ontobuilder.gui.tools.sitemap.SitesVisited;
import ac.technion.iem.ontobuilder.gui.tools.topk.OntologyMetaTopKWizard;
import ac.technion.iem.ontobuilder.gui.utils.browser.Browser;
import ac.technion.iem.ontobuilder.gui.utils.dom.DOMUtilitiesGui;
import ac.technion.iem.ontobuilder.gui.utils.files.common.FilePreviewer;
import ac.technion.iem.ontobuilder.gui.utils.files.common.FileUtilities;
import ac.technion.iem.ontobuilder.gui.utils.files.common.GeneralFileFilter;
import ac.technion.iem.ontobuilder.gui.utils.files.common.GeneralFilePreviewer;
import ac.technion.iem.ontobuilder.gui.utils.files.common.GeneralFileView;
import ac.technion.iem.ontobuilder.gui.utils.files.html.HTMLUtilitiesGui;
import ac.technion.iem.ontobuilder.gui.utils.files.ontology.OntologyBIZFileFilter;
import ac.technion.iem.ontobuilder.gui.utils.files.ontology.OntologyFileFilter;
import ac.technion.iem.ontobuilder.gui.utils.files.ontology.OntologyFileViewer;
import ac.technion.iem.ontobuilder.gui.utils.files.ontology.OntologyONTFileFilter;
import ac.technion.iem.ontobuilder.gui.utils.files.ontology.OntologyXMLFileFilter;
import ac.technion.iem.ontobuilder.gui.utils.files.xml.XMLUtilities;
import ac.technion.iem.ontobuilder.gui.utils.graphs.GraphUtilities;
import ac.technion.iem.ontobuilder.gui.utils.thesaurus.ThesaurusGui;
import ac.technion.iem.ontobuilder.io.exports.ExportException;
import ac.technion.iem.ontobuilder.io.exports.ExportUtilities;
import ac.technion.iem.ontobuilder.io.exports.Exporter;
import ac.technion.iem.ontobuilder.io.exports.ExporterMetadata;
import ac.technion.iem.ontobuilder.io.imports.ImportException;
import ac.technion.iem.ontobuilder.io.imports.ImportUtilities;
import ac.technion.iem.ontobuilder.io.imports.Importer;
import ac.technion.iem.ontobuilder.io.imports.ImporterMetadata;
import ac.technion.iem.ontobuilder.io.utils.dom.DOMNode;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.AbstractAlgorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.Algorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.AlgorithmUtilities;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.match.MatchOntologyHandler;

import com.jgraph.JGraph;

/**
 * <p>Title: OntoBuilder</p>
 * <p>Description: Initializes the main GUI of OntoBuilder</p>
 * Extends a {@link Application}
 */
public final class OntoBuilder extends Application
{
    public static FileFilter ontologyFileFilter = new OntologyFileFilter();
    public static FileFilter ontologyONTFileFilter = new OntologyONTFileFilter();
    public static FileFilter ontologyXMLFileFilter = new OntologyXMLFileFilter();
    public static FileFilter ontologyBIZFileFilter = new OntologyBIZFileFilter();
    public static FileView ontologyFileViewer = new OntologyFileViewer();
    
    private static final long serialVersionUID = 1L;

    private static Splash splash;

    protected Browser browser;
    protected ThesaurusGui thesaurus;
    public Vector<AbstractAlgorithm> algorithms;

    protected UpperPanel upperPanel;
    protected LowerPanel lowerPanel;
    protected MainPanel mainPanel;
    protected ArrayList<Process> runningProcesses;

    public static void main(String args[])
    {
        try
        {
            ApplicationParameters.parseCommandLine(args);
        }
        catch (ParseException e)
        {
            System.out.println(e.getMessage());
            ApplicationParameters.showSyntax();
            System.exit(1);
        }

        if (ApplicationParameters.hasInterface())
        {
            splash = new Splash();
            SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {
                    // splash.show();
                    splash.setVisible(true);
                }
            });

            try
            {
                JFrame frame = new JFrame();
                final OntoBuilder application = new OntoBuilder(frame);
                frame.getContentPane().add(application, BorderLayout.CENTER);
                frame.pack();
                frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                frame.addWindowListener(new WindowAdapter()
                {
                    public void windowClosing(WindowEvent e)
                    {
                        application.exit();
                    }
                });

                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                Dimension frameSize = frame.getSize();
                frame.setLocation(screenSize.width / 2 - frameSize.width / 2, screenSize.height /
                    2 - frameSize.height / 2);
                // frame.show();
                frame.setVisible(true);
            }
            catch (RuntimeException e)
            {
                splash.dispose();
                splash = null;
                e.printStackTrace();
                System.exit(1);
            }
            splash.dispose();
            splash = null;
        }
        else
            new OntoBuilder();
    }

    /**
     * Constructs a default OntoBuilder
     */
    public OntoBuilder()
    {
        super();
        runningProcesses = new ArrayList<Process>();
        boolean verbose = true;
        if (ApplicationParameters.generate)
        {
            try
            {
                if (verbose)
                    System.out.print("\n" +
                        MessageFormat.format(ApplicationUtilities
                            .getResourceString("commandLine.ontology.generating"),
                            ApplicationParameters.url.toExternalForm()));
                Ontology ontology = OntologyGui.generateOntology(ApplicationParameters.url);
                if (ontology == null)
                {
                    System.out.println("\n" +
                        MessageFormat.format(
                            ApplicationUtilities.getResourceString("error.ontology.generating"),
                            ApplicationParameters.url.toExternalForm()));
                    System.exit(2);
                }
                if (verbose)
                    System.out.println(" " +
                        MessageFormat.format(ApplicationUtilities
                            .getResourceString("commandLine.ontology.generated"), ontology
                            .getName()));
                if (ApplicationParameters.normalize)
                {
                    if (verbose)
                        System.out.print(MessageFormat.format(ApplicationUtilities
                            .getResourceString("commandLine.ontology.normalizing"), ontology
                            .getName()));
                    ontology.normalize();
                    if (verbose)
                        System.out.println(" " +
                            ApplicationUtilities
                                .getResourceString("commandLine.ontology.normalized"));
                }
                if (verbose)
                    System.out.print(MessageFormat.format(
                        ApplicationUtilities.getResourceString("commandLine.ontology.saving"),
                        ontology.getName(), ApplicationParameters.outputFile));
                ontology.saveToXML(new File(ApplicationParameters.outputFile));
                if (verbose)
                    System.out.println(" " +
                        ApplicationUtilities.getResourceString("commandLine.ontology.saved"));
                if (verbose)
                    System.out.println(ApplicationUtilities
                        .getResourceString("commandLine.generated"));
            }
            catch (IOException e)
            {
                System.out.println();
                e.printStackTrace();
            }
        }
        else if (ApplicationParameters.match)
        {
            try
            {
                String algorithmName = (String) options
                    .getOptionValue(AlgorithmUtilities.DEFAULT_ALGORITHM_PROPERTY);
                if (verbose)
                    System.out
                        .print("\n" +
                            MessageFormat.format(ApplicationUtilities
                                .getResourceString("commandLine.algorithm.loading"), algorithmName));
                Algorithm algorithm = AlgorithmUtilities.getAlgorithmPlugin(algorithmName);
                if (algorithm == null)
                {
                    System.out.println("\n" +
                        MessageFormat.format(
                            ApplicationUtilities.getResourceString("error.algorithms.notFound"),
                            algorithmName));
                    System.exit(3);
                }
                if (verbose)
                    System.out.println(ApplicationUtilities
                        .getResourceString("commandLine.algorithm.loaded"));
                if (verbose)
                    System.out.print(MessageFormat.format(
                        ApplicationUtilities.getResourceString("commandLine.ontology.generating"),
                        ApplicationParameters.targetURL.toExternalForm()));
                Ontology targetOntology = OntologyGui
                    .generateOntology(ApplicationParameters.targetURL);
                if (targetOntology == null)
                {
                    System.out.println(MessageFormat.format(
                        ApplicationUtilities.getResourceString("error.ontology.generating"),
                        ApplicationParameters.targetURL.toExternalForm()));
                    System.exit(2);
                }
                if (verbose)
                    System.out.println(" " +
                        MessageFormat.format(ApplicationUtilities
                            .getResourceString("commandLine.ontology.generated"), targetOntology
                            .getName()));
                if (verbose)
                    System.out.print(MessageFormat.format(
                        ApplicationUtilities.getResourceString("commandLine.ontology.generating"),
                        ApplicationParameters.candidateURL.toExternalForm()));
                Ontology candidateOntology = OntologyGui
                    .generateOntology(ApplicationParameters.candidateURL);
                if (candidateOntology == null)
                {
                    System.out.println("\n" +
                        MessageFormat.format(
                            ApplicationUtilities.getResourceString("error.ontology.generating"),
                            ApplicationParameters.candidateURL.toExternalForm()));
                    System.exit(2);
                }
                if (verbose)
                    System.out.println(" " +
                        MessageFormat.format(ApplicationUtilities
                            .getResourceString("commandLine.ontology.generated"), candidateOntology
                            .getName()));
                if (ApplicationParameters.normalize)
                {
                    if (verbose)
                        System.out.print(MessageFormat.format(ApplicationUtilities
                            .getResourceString("commandLine.ontology.normalizing"), targetOntology
                            .getName()));
                    targetOntology.normalize();
                    if (verbose)
                        System.out.println(" " +
                            ApplicationUtilities
                                .getResourceString("commandLine.ontology.normalized"));
                    if (verbose)
                        System.out.print(MessageFormat.format(ApplicationUtilities
                            .getResourceString("commandLine.ontology.normalizing"),
                            candidateOntology.getName()));
                    candidateOntology.normalize();
                    if (verbose)
                        System.out.println(" " +
                            ApplicationUtilities
                                .getResourceString("commandLine.ontology.normalized"));
                }
                if (verbose)
                    System.out
                        .print(ApplicationUtilities.getResourceString("commandLine.matching"));
                MatchInformation matchInformation = MatchOntologyHandler.match(targetOntology, candidateOntology, algorithm);
                if (matchInformation == null)
                {
                    System.out
                        .println("\n" +
                            ApplicationUtilities
                                .getResourceString("error.matchInformation.generating"));
                    System.exit(4);
                }
                if (ApplicationParameters.normalize)
                    matchInformation.denormalize();
                if (verbose)
                    System.out.println(ApplicationUtilities
                        .getResourceString("commandLine.matched"));
                if (verbose)
                    System.out.print(MessageFormat.format(
                        ApplicationUtilities.getResourceString("commandLine.match.saving"),
                        ApplicationParameters.outputFile));
                matchInformation.saveToXML(new File(ApplicationParameters.outputFile));
                if (verbose)
                    System.out.println(ApplicationUtilities
                        .getResourceString("commandLine.match.saved"));
            }
            catch (IOException e)
            {
                System.out.println();
                e.printStackTrace();
            }
        }
        else if (ApplicationParameters.agent)
        {
            if (verbose)
                System.out.println(MessageFormat.format(
                    ApplicationUtilities.getResourceString("commandLine.agent.starting"),
                    (String) options.getOptionValue(OntoBuilderAgent.AGENT_PORT_PROPERTY)));
            OntoBuilderAgent agent = new OntoBuilderAgent(options, verbose);
            agent.start();
        }
    }

    /**
     * Constructs an OntoBuilder
     * 
     * @param a {@link JApplet}
     */
    public OntoBuilder(JApplet applet)
    {
        super(applet);
        runningProcesses = new ArrayList<Process>();
    }

    /**
     * Constructs an OntoBuilder
     *
     * @param applet a {@link JApplet}
     * @param locale a {@link Locale}
     */
    public OntoBuilder(JApplet applet, Locale locale)
    {
        super(applet, locale);
        runningProcesses = new ArrayList<Process>();
    }

    /**
     * Constructs an OntoBuilder
     *
     * @param frame a {@link JFrame}
     */
    public OntoBuilder(JFrame frame)
    {
        super(frame, Locale.getDefault());
        runningProcesses = new ArrayList<Process>();
    }

    /**
     * Constructs a OntoBuilder
     *
     * @param frame a {@link JFrame}
     * @param locale a {@link Locale}
     */
    public OntoBuilder(JFrame frame, Locale locale)
    {
        super(frame, locale);
        runningProcesses = new ArrayList<Process>();
    }

    /**
     * Initialize the OntoBuilder
     */
    protected void init()
    {
        if (ApplicationParameters.hasInterface())
        {

            if (!isParametersInitialized())
                initializeParameters();
            initializeImporters();
            initializeExporters();
            initializeTools();
            super.init();
        }
        else
        {
            initializeParameters();
            initializeOptions();
        }
        initializeAlgorithms();
        DomainSimilarity.buildDomainMatrix(ApplicationUtilities
            .getStringProperty("domain.domainMatrix"));
        NetworkUtilities.initializeHTTPSProtocol();
        setProxy();
        setConnectionTimeout();
    }

    /**
     * Set a proxy
     */
    protected void setProxy()
    {
        boolean proxy = Boolean.valueOf(
            (String) options.getOptionValue(NetworkUtilitiesPropertiesEnum.USE_PROXY_PROPERTY.getValue())).booleanValue();
        if (proxy)
            NetworkUtilities.setProxy(
                (String) options.getOptionValue(NetworkUtilitiesPropertiesEnum.PROXY_HOST_PROPERTY.getValue()),
                (String) options.getOptionValue(NetworkUtilitiesPropertiesEnum.PROXY_PORT_PROPERTY.getValue()));
        else
            NetworkUtilities.disableProxy();
    }

    /**
     * Set the connection timeout
     */
    protected void setConnectionTimeout()
    {
        try
        {
            long millis = Long.parseLong((String) options
                .getOptionValue(NetworkUtilitiesPropertiesEnum.CONNECTION_TIMEOUT_PROPERTY.getValue()));
            NetworkUtilities.setConnectionTimeout(millis > 0 ? millis * 1000 : -1);
        }
        catch (NumberFormatException e)
        {
            // NetworkUtilities.setConnectionTimeout(-1);
        }
    }

    /**
     * Initialize the importers
     */
    protected void initializeImporters()
    {
        try
        {

            File importersFile = new File(OntoBuilderResources.Config.IO.IMPORTERS_XML);
            if (!importersFile.exists())
            {
            	return;
            }

            ImportUtilities.initializeImporters(importersFile);

        }
        catch (ImportException e)
        {
            JOptionPane.showMessageDialog(OntoBuilder.this,
                ApplicationUtilities.getResourceString("error") + ": " + e.getMessage(),
                ApplicationUtilities.getResourceString("error"), JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Initialize the exporters
     */
    protected void initializeExporters()
    {
        try
        {

            File exportersFile = new File(OntoBuilderResources.Config.IO.EXPORTERS_XML);
            if (!exportersFile.exists())
            {
                return;
            }

            ExportUtilities.initializeExporters(exportersFile);

        }
        catch (ExportException e)
        {
            JOptionPane.showMessageDialog(OntoBuilder.this,
                ApplicationUtilities.getResourceString("error") + ": " + e.getMessage(),
                ApplicationUtilities.getResourceString("error"), JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Initialize the tools
     */
    protected void initializeTools()
    {
        try
        {
        	InputStream resource = getClass().getResourceAsStream("/config/tools.xml");
            if (resource == null)
            {
                return;
            }
            ToolsUtilities.intializeTools(resource);

        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(OntoBuilder.this,
                ApplicationUtilities.getResourceString("error") + ": " + e.getMessage(),
                ApplicationUtilities.getResourceString("error"), JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Initialize the algorithms
     */
    protected void initializeAlgorithms()
    {
        try
        {
            File algorithmsFile = new File(OntoBuilderResources.Config.Matching.ALGORITHMS_XML);
            if (algorithmsFile.exists())
                algorithms = AlgorithmUtilities.getAlgorithmsInstances(algorithmsFile);
            if (algorithms == null)
                return;
            double threshold = Double.parseDouble((String) options
                .getOptionValue(Algorithm.MATCH_THRESHOLD_PROPERTY));
            if (ApplicationParameters.hasInterface())
                statusBar.setCoordinatesText(ApplicationUtilities
                    .getResourceString("status.msg.threshold") + " " + threshold + "% ");
            for (Iterator<?> i = algorithms.iterator(); i.hasNext();)
            {
                Algorithm algorithm = (Algorithm) i.next();
                algorithm.setThreshold(threshold / (double) 100);
                if (algorithm.usesThesaurus())
                    algorithm.setThesaurus(thesaurus.getThesaurus());
            }
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(OntoBuilder.this,
                ApplicationUtilities.getResourceString("error") + ": " + e.getMessage(),
                ApplicationUtilities.getResourceString("error"), JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Initialize the GUI components
     */
    protected void initializeGUIComponents()
    {
        browser = new Browser(this);
        File browserHistory = new File(OntoBuilderResources.Config.GUI.BROWSER_HISTORY);
        browser.loadAddressHistory(browserHistory);
        browser.addPropertyChangeListener(new PropertyChangeListener()
        {
            public void propertyChange(PropertyChangeEvent evt)
            {
                if (evt.getPropertyName().equals(Browser.URL_PROPERTY))
                {
                    URL url = (URL) evt.getNewValue();
                    displayURL(url);
                }
            }
        });

        try
        {
            File thesaurusFile = new File(OntoBuilderResources.Config.THESAURUS_XML);
            thesaurus = new ThesaurusGui(thesaurusFile);
        }
        catch (ThesaurusException e)
        {
            e.printStackTrace();
            JOptionPane.showMessageDialog(OntoBuilder.this,
                ApplicationUtilities.getResourceString("error") + ": " + e.getMessage(),
                ApplicationUtilities.getResourceString("error"), JOptionPane.ERROR_MESSAGE);
        }

        toolBar = new OntoBuilderToolBar(this);
        menuBar = new OntoBuilderMenuBar(this);
        statusBar = new StatusBar();

        // File Chooser
        FileUtilities.configureFileDialogViewer(FileUtilities.fileViewer);
        FileUtilities.filePreviewer.addPreviewer(HTMLUtilitiesGui.htmlFilePreviewer);
        FileUtilities.filePreviewer.addPreviewer(XMLUtilities.xmlFilePreviewer);
        if (Boolean.valueOf(
            (String) options.getOptionValue(FileUtilities.PREVIEW_PANEL_VISIBLE_PROPERTY))
            .booleanValue())
            FileUtilities.configureFileDialogPreviewer(FileUtilities.filePreviewer);

        // Graphs
        GraphUtilities.setShowPrecedenceLinks(Boolean.valueOf(
            (String) options.getOptionValue(GraphUtilities.SHOW_PRECEDENCE_LINKS_PROPERTY))
            .booleanValue());

        // BizTalk
        BizTalkUtilities.setNameNamingType((String) options
            .getOptionValue(BizTalkUtilities.NAME_NAMING_TYPE_PROPERTY));
        BizTalkUtilities.setEnumarationNamingType((String) options
            .getOptionValue(BizTalkUtilities.ENUMERATION_NAMING_TYPE_PROPERTY));
    }

    /**
     * Initialize the interface
     */
    protected void initializeInterface()
    {
        super.initializeInterface();

        JSplitPane leftPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
            upperPanel = new UpperPanel(this), lowerPanel = new LowerPanel(this));
        leftPane.setDividerLocation((int) (getLocation().y + getPreferredSize().height / 2));
        JSplitPane pane;
        try
        {
            pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPane, mainPanel = new MainPanel(
                this));
        }
        catch (ToolsException e1)
        {
            JOptionPane.showMessageDialog(OntoBuilder.this,
                ApplicationUtilities.getResourceString("error") + ": " + e1.getMessage(),
                ApplicationUtilities.getResourceString("error"), JOptionPane.ERROR_MESSAGE);
            return;
        }
        pane.setDividerLocation((int) (getLocation().x + getPreferredSize().width / 3));
        add(pane, BorderLayout.CENTER);

        upperPanel.thesaurusPanel.showThesaurus(thesaurus);
        thesaurus.addThesaurusSelectionListener(new ThesaurusSelectionListener()
        {
            public void valueChanged(ThesaurusSelectionEvent e)
            {
                Object object = e.getSelectedWord();
                if (object == null)
                    return;
                if (object instanceof ObjectWithProperties)
                    lowerPanel.propertiesPanel.showProperties(((ObjectWithProperties) object)
                        .getProperties());
                else
                    lowerPanel.propertiesPanel.showProperties(null);
            }
        });
        thesaurus.addThesaurusModelListener(new ThesaurusModelAdapter()
        {
            public void wordChanged(ThesaurusModelEvent e)
            {
                Object object = e.getWord();
                if (object instanceof ObjectWithProperties)
                    lowerPanel.propertiesPanel.showProperties(((ObjectWithProperties) object)
                        .getProperties());
                else
                    lowerPanel.propertiesPanel.showProperties(null);
            }
        });
    }

    /**
     * Initialize the interface
     */
    protected void initializeOptions()
    {
        super.initializeOptions();
        options.addPropertyChangeListener(new PropertyChangeListener()
        {
            public void propertyChange(PropertyChangeEvent evt)
            {
                if (evt.getPropertyName().equals(Browser.ADDRESS_HISTORY_ENTRIES_PROPERTY))
                {
                    browser.setAddressHistoryEntries(Integer.parseInt((String) evt.getNewValue()));
                }
                else if (evt.getPropertyName().equals(Browser.ADDRESS_HISTORY_CLEAR_PROPERTY))
                {
                    browser.clearAddressHistory();
                }
                else if (evt.getPropertyName().equals(MainPanel.HTML_PANEL_VISIBLE_PROPERTY))
                {
                    mainPanel.setTabVisible(MainPanel.HTML_TAB,
                        Boolean.valueOf((String) evt.getNewValue()).booleanValue());
                }
                else if (evt.getPropertyName().equals(MainPanel.SOURCE_PANEL_VISIBLE_PROPERTY))
                {
                    mainPanel.setTabVisible(MainPanel.SOURCE_TAB,
                        Boolean.valueOf((String) evt.getNewValue()).booleanValue());
                }
                else if (evt.getPropertyName().equals(MainPanel.MESSAGE_PANEL_VISIBLE_PROPERTY))
                {
                    mainPanel.setTabVisible(MainPanel.MESSAGE_TAB,
                        Boolean.valueOf((String) evt.getNewValue()).booleanValue());
                }
                else if (evt.getPropertyName().equals(MainPanel.SITEMAP_PANEL_VISIBLE_PROPERTY))
                {
                    mainPanel.setTabVisible(MainPanel.SITEMAP_TAB,
                        Boolean.valueOf((String) evt.getNewValue()).booleanValue());
                }
                else if (evt.getPropertyName().equals(MainPanel.ELEMENTS_PANEL_VISIBLE_PROPERTY))
                {
                    mainPanel.setTabVisible(MainPanel.ELEMENTS_TAB,
                        Boolean.valueOf((String) evt.getNewValue()).booleanValue());
                }
                else if (evt.getPropertyName().equals(MainPanel.ONTOLOGY_PANEL_VISIBLE_PROPERTY))
                {
                    mainPanel.setTabVisible(MainPanel.ONTOLOGY_TAB,
                        Boolean.valueOf((String) evt.getNewValue()).booleanValue());
                }
                else if (evt.getPropertyName().equals(MainPanel.TOOLS_PANEL_VISIBLE_PROPERTY))
                {
                    mainPanel.setTabVisible(MainPanel.TOOLS_TAB,
                        Boolean.valueOf((String) evt.getNewValue()).booleanValue());
                }
                else if (evt.getPropertyName().equals(UpperPanel.DOM_PANEL_VISIBLE_PROPERTY))
                {
                    upperPanel.setTabVisible(UpperPanel.DOM_TAB,
                        Boolean.valueOf((String) evt.getNewValue()).booleanValue());
                }
                else if (evt.getPropertyName().equals(
                    UpperPanel.HYPERBOLICDOM_PANEL_VISIBLE_PROPERTY))
                {
                    upperPanel.setTabVisible(UpperPanel.HYPERBOLICDOM_TAB,
                        Boolean.valueOf((String) evt.getNewValue()).booleanValue());
                }
                else if (evt.getPropertyName().equals(UpperPanel.THESAURUS_PANEL_VISIBLE_PROPERTY))
                {
                    upperPanel.setTabVisible(UpperPanel.THESAURUS_TAB,
                        Boolean.valueOf((String) evt.getNewValue()).booleanValue());
                }
                else if (evt.getPropertyName().equals(FileUtilities.PREVIEW_PANEL_VISIBLE_PROPERTY))
                {
                    if (Boolean.valueOf((String) evt.getNewValue()).booleanValue()) // Add the
                                                                                    // preview panel
                        FileUtilities.configureFileDialogPreviewer(FileUtilities.filePreviewer);
                    else
                        FileUtilities.configureFileDialogPreviewer(null);
                }
                else if (evt.getPropertyName().equals(Algorithm.MATCH_THRESHOLD_PROPERTY))
                {
                    if (algorithms == null)
                        return;
                    double threshold = Double.parseDouble((String) evt.getNewValue());
                    statusBar.setCoordinatesText(ApplicationUtilities
                        .getResourceString("status.msg.threshold") + " " + threshold + "% ");
                    for (Iterator<?> i = algorithms.iterator(); i.hasNext();)
                        ((Algorithm) i.next()).setThreshold(threshold / (double) 100);
                }
                else if (evt.getPropertyName()
                    .equals(GraphUtilities.SHOW_PRECEDENCE_LINKS_PROPERTY))
                {
                    GraphUtilities.setShowPrecedenceLinks(Boolean.valueOf(
                        (String) evt.getNewValue()).booleanValue());
                }
                else if (evt.getPropertyName().equals(BizTalkUtilities.NAME_NAMING_TYPE_PROPERTY))
                {
                    BizTalkUtilities.setNameNamingType((String) evt.getNewValue());
                }
                else if (evt.getPropertyName().equals(
                    BizTalkUtilities.ENUMERATION_NAMING_TYPE_PROPERTY))
                {
                    BizTalkUtilities.setEnumarationNamingType((String) evt.getNewValue());
                }
                else if (evt.getPropertyName().equals(NetworkUtilitiesPropertiesEnum.USE_PROXY_PROPERTY) ||
                    evt.getPropertyName().equals(NetworkUtilitiesPropertiesEnum.PROXY_HOST_PROPERTY) ||
                    evt.getPropertyName().equals(NetworkUtilitiesPropertiesEnum.PROXY_PORT_PROPERTY))
                {
                    setProxy();
                }
                else if (evt.getPropertyName().equals(NetworkUtilitiesPropertiesEnum.CONNECTION_TIMEOUT_PROPERTY))
                {
                    setConnectionTimeout();
                }
            }
        });
    }

    /**
     * Initialize the actions
     */
    public void initializeActions()
    {
        super.initializeActions();

        // Open
        Action action = new AbstractAction(ApplicationUtilities.getResourceString("action.open"),
            ApplicationUtilities.getImage("open.gif"))
        {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e)
            {
                commandFileOpen();
            }
        };
        action.putValue(Action.LONG_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.open.longDescription"));
        action.putValue(Action.SHORT_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.open.shortDescription"));
        action.putValue(
            Action.MNEMONIC_KEY,
            new Integer(KeyStroke.getKeyStroke(
                ApplicationUtilities.getResourceString("action.open.mnemonic")).getKeyCode()));
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ApplicationUtilities
            .getResourceString("action.open.accelerator")));
        actions.addAction("open", action);

        // // Print (currently unfunctional)
        // action = new
        // AbstractAction(ApplicationUtilities.getResourceString("action.print"),ApplicationUtilities.getImage("print.gif"))
        // {
        // public void actionPerformed(ActionEvent e)
        // {
        // commandPrint();
        // }
        // };
        // action.putValue(Action.LONG_DESCRIPTION,ApplicationUtilities.getResourceString("action.print.longDescription"));
        // action.putValue(Action.SHORT_DESCRIPTION,ApplicationUtilities.getResourceString("action.print.shortDescription"));
        // action.putValue(Action.MNEMONIC_KEY,new
        // Integer(KeyStroke.getKeyStroke(ApplicationUtilities.getResourceString("action.print.mnemonic")).getKeyCode()));
        // action.putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke(ApplicationUtilities.getResourceString("action.print.accelerator")));
        // actions.addAction("print",action);

        // Site Map
        action = new AbstractAction(ApplicationUtilities.getResourceString("action.sitemap"),
            ApplicationUtilities.getImage("sitemap.gif"))
        {

            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    String url = browser.getCurrentURL();
                    if (url != null && url.length() > 0)
                        commandCreateSiteMap(NetworkUtilities.makeURL(url));
                    else
                        JOptionPane.showMessageDialog(OntoBuilder.this,
                            ApplicationUtilities.getResourceString("warning.sitemap.url"),
                            ApplicationUtilities.getResourceString("warning"),
                            JOptionPane.ERROR_MESSAGE);
                }
                catch (MalformedURLException ex)
                {
                    JOptionPane.showMessageDialog(OntoBuilder.this,
                        ApplicationUtilities.getResourceString("error") + ": " + ex.getMessage(),
                        ApplicationUtilities.getResourceString("error"), JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        action.putValue(Action.LONG_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.sitemap.longDescription"));
        action.putValue(Action.SHORT_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.sitemap.shortDescription"));
        action.putValue(
            Action.MNEMONIC_KEY,
            new Integer(KeyStroke.getKeyStroke(
                ApplicationUtilities.getResourceString("action.sitemap.mnemonic")).getKeyCode()));
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ApplicationUtilities
            .getResourceString("action.sitemap.accelerator")));
        actions.addAction("sitemap", action);

        // OntoParser
        action = new AbstractAction(ApplicationUtilities.getResourceString("action.ontoparser"),
            ApplicationUtilities.getImage("rdf.gif"))
        {

            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e)
            {

                boolean status = commandOntoParser();
                if (!status)
                    JOptionPane.showMessageDialog(OntoBuilder.this,
                        "Error in running OntoParser!\n"
                            + ".NET is not installed on this computer.\n" /*
                                                                           * +
                                                                           * "Please follow this link to install .NET"
                                                                           * +
                                                                           * "<html><a href=\"http://msdn.microsoft.com/netframework/downloads/updates/default.aspx\">.NET Download page</a></html>"
                                                                           */
                        , ApplicationUtilities.getResourceString("warning"),
                        JOptionPane.ERROR_MESSAGE);

            }
        };
        action.putValue(Action.LONG_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.ontoparser.longDescription"));
        action.putValue(Action.SHORT_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.ontoparser.shortDescription"));
        action
            .putValue(
                Action.MNEMONIC_KEY,
                new Integer(KeyStroke.getKeyStroke(
                    ApplicationUtilities.getResourceString("action.ontoparser.mnemonic"))
                    .getKeyCode()));
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ApplicationUtilities
            .getResourceString("action.ontoparser.accelerator")));
        actions.addAction("ontoparser", action);

        // //Exact tool
        // action = new
        // AbstractAction(ApplicationUtilities.getResourceString("action.exacttool"),ApplicationUtilities.getImage("mergewizard.gif"))
        // {
        // public void actionPerformed(ActionEvent e)
        // {
        //
        // boolean status = commandExactTool();
        // if (!status)
        // JOptionPane.showMessageDialog(OntoBuilder.this,"Error in running OntoParser!\n" +
        // ".NET is not installed on this computer.\n" /*+
        // "Please follow this link to install .NET" +
        // "<html><a href=\"http://msdn.microsoft.com/netframework/downloads/updates/default.aspx\">.NET Download page</a></html>"*/
        // ,ApplicationUtilities.getResourceString("warning"),JOptionPane.ERROR_MESSAGE);
        //
        //
        // }
        // };
        // action.putValue(Action.LONG_DESCRIPTION,ApplicationUtilities.getResourceString("action.exacttool.longDescription"));
        // action.putValue(Action.SHORT_DESCRIPTION,ApplicationUtilities.getResourceString("action.exacttool.shortDescription"));
        // action.putValue(Action.MNEMONIC_KEY,new
        // Integer(KeyStroke.getKeyStroke(ApplicationUtilities.getResourceString("action.exacttool.mnemonic")).getKeyCode()));
        // action.putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke(ApplicationUtilities.getResourceString("action.exacttool.accelerator")));
        // actions.addAction("exacttool",action);

        // Ontology Wizard
        action = new AbstractAction(ApplicationUtilities.getResourceString("action.ontowizard"),
            ApplicationUtilities.getImage("ontowizard.gif"))
        {

            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    String url = browser.getCurrentURL();
                    if (url != null && url.length() > 0)
                        commandOntologyWizard(NetworkUtilities.makeURL(url));
                    else
                        JOptionPane.showMessageDialog(OntoBuilder.this,
                            ApplicationUtilities.getResourceString("warning.ontowizard.url"),
                            ApplicationUtilities.getResourceString("warning"),
                            JOptionPane.ERROR_MESSAGE);
                }
                catch (MalformedURLException ex)
                {
                    JOptionPane.showMessageDialog(OntoBuilder.this,
                        ApplicationUtilities.getResourceString("error") + ": " + ex.getMessage(),
                        ApplicationUtilities.getResourceString("error"), JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        action.putValue(Action.LONG_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.ontowizard.longDescription"));
        action.putValue(Action.SHORT_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.ontowizard.shortDescription"));
        action
            .putValue(
                Action.MNEMONIC_KEY,
                new Integer(KeyStroke.getKeyStroke(
                    ApplicationUtilities.getResourceString("action.ontowizard.mnemonic"))
                    .getKeyCode()));
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ApplicationUtilities
            .getResourceString("action.ontowizard.accelerator")));
        actions.addAction("ontowizard", action);

        // New Ontology
        action = new AbstractAction(ApplicationUtilities.getResourceString("action.newOntology"),
            ApplicationUtilities.getImage("newontology.gif"))
        {

            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e)
            {
                commandNewOntology();
            }
        };
        action.putValue(Action.LONG_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.newOntology.longDescription"));
        action.putValue(Action.SHORT_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.newOntology.shortDescription"));
        action
            .putValue(
                Action.MNEMONIC_KEY,
                new Integer(KeyStroke.getKeyStroke(
                    ApplicationUtilities.getResourceString("action.newOntology.mnemonic"))
                    .getKeyCode()));
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ApplicationUtilities
            .getResourceString("action.newOntology.accelerator")));
        actions.addAction("newontology", action);

        // Open Ontology
        action = new AbstractAction(ApplicationUtilities.getResourceString("action.openOntology"),
            ApplicationUtilities.getImage("openontology.gif"))
        {

            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e)
            {
                commandOpenOntology();
            }
        };
        action.putValue(Action.LONG_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.openOntology.longDescription"));
        action.putValue(Action.SHORT_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.openOntology.shortDescription"));
        action.putValue(
            Action.MNEMONIC_KEY,
            new Integer(KeyStroke.getKeyStroke(
                ApplicationUtilities.getResourceString("action.openOntology.mnemonic"))
                .getKeyCode()));
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ApplicationUtilities
            .getResourceString("action.openOntology.accelerator")));
        actions.addAction("openontology", action);

        // Close Ontology
        action = new AbstractAction(ApplicationUtilities.getResourceString("action.closeOntology"),
            ApplicationUtilities.getImage("closeontology.gif"))
        {

            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e)
            {
                commandCloseOntology();
            }
        };
        action.putValue(Action.LONG_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.closeOntology.longDescription"));
        action.putValue(Action.SHORT_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.closeOntology.shortDescription"));
        action.putValue(
            Action.MNEMONIC_KEY,
            new Integer(KeyStroke.getKeyStroke(
                ApplicationUtilities.getResourceString("action.closeOntology.mnemonic"))
                .getKeyCode()));
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ApplicationUtilities
            .getResourceString("action.closeOntology.accelerator")));
        actions.addAction("closeontology", action);

        // Save Ontology
        action = new AbstractAction(ApplicationUtilities.getResourceString("action.saveOntology"),
            ApplicationUtilities.getImage("saveontology.gif"))
        {

            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e)
            {
                commandSaveOntology();
            }
        };
        action.putValue(Action.LONG_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.saveOntology.longDescription"));
        action.putValue(Action.SHORT_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.saveOntology.shortDescription"));
        action.putValue(
            Action.MNEMONIC_KEY,
            new Integer(KeyStroke.getKeyStroke(
                ApplicationUtilities.getResourceString("action.saveOntology.mnemonic"))
                .getKeyCode()));
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ApplicationUtilities
            .getResourceString("action.saveOntology.accelerator")));
        actions.addAction("saveontology", action);

        // Save Light Ontology
        action = new AbstractAction(
            ApplicationUtilities.getResourceString("action.saveLightOntology"),
            ApplicationUtilities.getImage("savelightontology.gif"))
        {

            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e)
            {
                commandSaveLightOntology();
            }
        };
        action.putValue(Action.LONG_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.saveLightOntology.longDescription"));
        action.putValue(Action.SHORT_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.saveLightOntology.shortDescription"));
        action.putValue(
            Action.MNEMONIC_KEY,
            new Integer(KeyStroke.getKeyStroke(
                ApplicationUtilities.getResourceString("action.saveLightOntology.mnemonic"))
                .getKeyCode()));
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ApplicationUtilities
            .getResourceString("action.saveLightOntology.accelerator")));
        actions.addAction("savelightontology", action);

        // Save As Ontology
        action = new AbstractAction(
            ApplicationUtilities.getResourceString("action.saveAsOntology"),
            ApplicationUtilities.getImage("saveasontology.gif"))
        {

            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e)
            {
                commandSaveAsOntology();
            }
        };
        action.putValue(Action.LONG_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.saveAsOntology.longDescription"));
        action.putValue(Action.SHORT_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.saveAsOntology.shortDescription"));
        action.putValue(
            Action.MNEMONIC_KEY,
            new Integer(KeyStroke.getKeyStroke(
                ApplicationUtilities.getResourceString("action.saveAsOntology.mnemonic"))
                .getKeyCode()));
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ApplicationUtilities
            .getResourceString("action.saveAsOntology.accelerator")));
        actions.addAction("saveasontology", action);

        // Imports
        ImporterMetadata[] importers = ImportUtilities.getAllImporterMetadata();

        if (importers.length > 0)
        {
            for (int i = 0; i < importers.length; i++)
            {
                final ImporterMetadata importer = importers[i];
                action = new AbstractAction(
                    importer.getType(),
                    ApplicationUtilities.getImage(importer.getIcon() != null ? importer.getIcon() : "import.gif"))
                {

                    private static final long serialVersionUID = 1L;

                    public void actionPerformed(ActionEvent e)
                    {
                        commandImportFile(importer.getType());
                    }
                };
                if (importer.getLongDescription() != null)
                    action.putValue(Action.LONG_DESCRIPTION,
                        ApplicationUtilities.getResourceString(importer.getLongDescription()));
                if (importer.getShortDescription() != null)
                    action.putValue(Action.SHORT_DESCRIPTION,
                        ApplicationUtilities.getResourceString(importer.getShortDescription()));
                if (importer.getMnemonic() != null)
                    action.putValue(
                        Action.MNEMONIC_KEY,
                        new Integer(KeyStroke.getKeyStroke(
                            ApplicationUtilities.getResourceString(importer.getMnemonic()))
                            .getKeyCode()));
                if (importer.getAccelerator() != null)
                    action.putValue(Action.ACCELERATOR_KEY, KeyStroke
                        .getKeyStroke(ApplicationUtilities.getResourceString(importer
                            .getAccelerator())));
                actions.addAction("import." + importer.getType(), action);
            }
        }

        // Exports
        ExporterMetadata[] exporters = ExportUtilities.getAllExporterMetadata();

        if (exporters.length > 0)
        {
            for (int i = 0; i < exporters.length; i++)
            {
                final ExporterMetadata exporter = exporters[i];
                action = new AbstractAction(
                    exporter.getType(),
                    ApplicationUtilities.getImage(exporter.getIcon() != null ? exporter.getIcon() : "export.gif"))
                {

                    private static final long serialVersionUID = 1L;

                    public void actionPerformed(ActionEvent e)
                    {
                        commandExportFile(exporter.getType());
                    }
                };
                if (exporter.getLongDescription() != null)
                    action.putValue(Action.LONG_DESCRIPTION,
                        ApplicationUtilities.getResourceString(exporter.getLongDescription()));
                if (exporter.getShortDescription() != null)
                    action.putValue(Action.SHORT_DESCRIPTION,
                        ApplicationUtilities.getResourceString(exporter.getShortDescription()));
                if (exporter.getMnemonic() != null)
                    action.putValue(
                        Action.MNEMONIC_KEY,
                        new Integer(KeyStroke.getKeyStroke(
                            ApplicationUtilities.getResourceString(exporter.getMnemonic()))
                            .getKeyCode()));
                if (exporter.getAccelerator() != null)
                    action.putValue(Action.ACCELERATOR_KEY, KeyStroke
                        .getKeyStroke(ApplicationUtilities.getResourceString(exporter
                            .getAccelerator())));
                actions.addAction("export." + exporter.getType(), action);
            }
        }

        // Tools
        ToolMetadata[] tools = ToolsUtilities.getAllToolMetadata();

        if (tools.length > 0)
        {
            for (int i = 0; i < tools.length; i++)
            {
                final ToolMetadata tool = tools[i];
                action = new AbstractAction(tool.getName(), ApplicationUtilities.getImage(tool
                    .getIcon() != null ? tool.getIcon() : "tools.gif"))
                {

                    private static final long serialVersionUID = 1L;

                    public void actionPerformed(ActionEvent e)
                    {
                        commandOpenTool(tool.getName());
                    }

                };
                if (tool.getLongDescription() != null)
                    action.putValue(Action.LONG_DESCRIPTION,
                        ApplicationUtilities.getResourceString(tool.getLongDescription()));
                if (tool.getShortDescription() != null)
                    action.putValue(Action.SHORT_DESCRIPTION,
                        ApplicationUtilities.getResourceString(tool.getShortDescription()));
                if (tool.getMnemonic() != null)
                    action.putValue(
                        Action.MNEMONIC_KEY,
                        new Integer(KeyStroke.getKeyStroke(
                            ApplicationUtilities.getResourceString(tool.getMnemonic()))
                            .getKeyCode()));
                if (tool.getAccelerator() != null)
                    action.putValue(Action.ACCELERATOR_KEY,
                        KeyStroke.getKeyStroke(ApplicationUtilities.getResourceString(tool
                            .getAccelerator())));
                actions.addAction("tool." + tool.getName(), action);
            }
        }

        // View Hyperbolic Ontology
        action = new AbstractAction(
            ApplicationUtilities.getResourceString("action.hyperbolicOntology"),
            ApplicationUtilities.getImage("hyperbolicontology.gif"))
        {

            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e)
            {
                commandViewHyperbolicOntology();
            }
        };
        action.putValue(Action.LONG_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.hyperbolicOntology.longDescription"));
        action.putValue(Action.SHORT_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.hyperbolicOntology.shortDescription"));
        action.putValue(
            Action.MNEMONIC_KEY,
            new Integer(KeyStroke.getKeyStroke(
                ApplicationUtilities.getResourceString("action.hyperbolicOntology.mnemonic"))
                .getKeyCode()));
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ApplicationUtilities
            .getResourceString("action.hyperbolicOntology.accelerator")));
        actions.addAction("hyperbolicontology", action);

        // View Graph Ontology
        action = new AbstractAction(ApplicationUtilities.getResourceString("action.graphOntology"),
            ApplicationUtilities.getImage("graph.gif"))
        {

            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e)
            {
                commandViewGraphOntology();
            }
        };
        action.putValue(Action.LONG_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.graphOntology.longDescription"));
        action.putValue(Action.SHORT_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.graphOntology.shortDescription"));
        action.putValue(
            Action.MNEMONIC_KEY,
            new Integer(KeyStroke.getKeyStroke(
                ApplicationUtilities.getResourceString("action.graphOntology.mnemonic"))
                .getKeyCode()));
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ApplicationUtilities
            .getResourceString("action.graphOntology.accelerator")));
        actions.addAction("graphontology", action);

        // Normalize Ontology
        action = new AbstractAction(
            ApplicationUtilities.getResourceString("action.normalizeOntology"),
            ApplicationUtilities.getImage("normalizeontology.gif"))
        {

            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e)
            {
                commandNormalizeOntology();
            }
        };
        action.putValue(Action.LONG_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.normalizeOntology.longDescription"));
        action.putValue(Action.SHORT_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.normalizeOntology.shortDescription"));
        action.putValue(
            Action.MNEMONIC_KEY,
            new Integer(KeyStroke.getKeyStroke(
                ApplicationUtilities.getResourceString("action.normalizeOntology.mnemonic"))
                .getKeyCode()));
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ApplicationUtilities
            .getResourceString("action.normalizeOntology.accelerator")));
        actions.addAction("normalizeontology", action);

        // BizTalk
        action = new AbstractAction(ApplicationUtilities.getResourceString("action.biztalkMapper"),
            ApplicationUtilities.getImage("biztalk.gif"))
        {

            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e)
            {
                commandBizTalkMapper();
            }
        };
        action.putValue(Action.LONG_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.biztalkMapper.longDescription"));
        action.putValue(Action.SHORT_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.biztalkMapper.shortDescription"));
        action.putValue(
            Action.MNEMONIC_KEY,
            new Integer(KeyStroke.getKeyStroke(
                ApplicationUtilities.getResourceString("action.biztalkMapper.mnemonic"))
                .getKeyCode()));
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ApplicationUtilities
            .getResourceString("action.biztalkMapper.accelerator")));
        actions.addAction("biztalkMapper", action);

        // Ontology Merging Wizard
        action = new AbstractAction(
            ApplicationUtilities.getResourceString("action.mergeOntologies"),
            ApplicationUtilities.getImage("mergewizard.gif"))
        {

            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e)
            {
                commandMergeOntologiesWizard();
            }
        };
        action.putValue(Action.LONG_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.mergeOntologies.longDescription"));
        action.putValue(Action.SHORT_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.mergeOntologies.shortDescription"));
        action.putValue(
            Action.MNEMONIC_KEY,
            new Integer(KeyStroke.getKeyStroke(
                ApplicationUtilities.getResourceString("action.mergeOntologies.mnemonic"))
                .getKeyCode()));
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ApplicationUtilities
            .getResourceString("action.mergeOntologies.accelerator")));
        actions.addAction("mergeontologies", action);

        // Meta Top K Wizard
        action = new AbstractAction(ApplicationUtilities.getResourceString("action.metaTopK"),
            ApplicationUtilities.getImage("metalg.gif"))
        {

            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e)
            {
                commandMetaTopKWizard();
            }
        };
        action.putValue(Action.LONG_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.metaTopK.longDescription"));
        action.putValue(Action.SHORT_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.metaTopK.shortDescription"));
        action.putValue(
            Action.MNEMONIC_KEY,
            new Integer(KeyStroke.getKeyStroke(
                ApplicationUtilities.getResourceString("action.metaTopK.mnemonic")).getKeyCode()));
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ApplicationUtilities
            .getResourceString("action.metaTopK.accelerator")));
        actions.addAction("metaTopK", action);
    }

    /**
     * Get the browser
     *
     * @return the {@link Browser}
     */
    public Browser getBrowser()
    {
        return browser;
    }

    /**
     * Exit the application
     */
    public void exit()
    {
        if (browser != null && isApplication())
            browser.saveAddressHistory(OntoBuilderResources.Config.GUI.BROWSER_HISTORY);
        if (thesaurus != null && isApplication())
        {
            try
            {
                thesaurus.saveThesaurus(OntoBuilderResources.Config.THESAURUS_XML);
            }
            catch (ThesaurusException e)
            {
                JOptionPane.showMessageDialog(OntoBuilder.this,
                    ApplicationUtilities.getResourceString("error") + ": " + e.getMessage(),
                    ApplicationUtilities.getResourceString("error"), JOptionPane.ERROR_MESSAGE);
            }
        }

        // terminate ontoParser
        if (runningProcesses != null && !runningProcesses.isEmpty())
        {
            try
            {
                Iterator<Process> it = runningProcesses.iterator();
                while (it.hasNext())
                {
                    Process p = (Process) it.next();
                    p.destroy();
                }
            }
            catch (Throwable e)
            {
                // ignore
            }
        }

        // Ontology o;
        while (mainPanel.ontologyPanel.getCurrentOntology() != null)
            if (!commandCloseOntology())
                return;

        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                OntoBuilder.super.exit();
            }
        });

    }

    /**
     * Execute the "OntoParser" command
     *
     * @return true on success
     */
    public boolean commandOntoParser()
    {
        try
        {
            runningProcesses.add(Runtime.getRuntime().exec(
                ApplicationUtilities.getCurrentDirectory() + "OntoParser.exe"));
            return true;
        }
        catch (Throwable e)
        {
            return false;
        }
    }

    /**
     * Execute the "ExactTool" command
     *
     * @return <code>true</code> on success
     */
    public boolean commandExactTool()
    {
        try
        {
            runningProcesses.add(Runtime.getRuntime().exec(
                ApplicationUtilities.getCurrentDirectory() + "Exact!.bat"));
            return true;
        }
        catch (Throwable e)
        {
            return false;
        }
    }

    /**
     * Execute the "OpenTool" command
     */
    private void commandOpenTool(String name)
    {

        ToolMetadata metadata = null;

        try
        {
            metadata = ToolsUtilities.getToolMetadata(name);
            metadata.setVisible(!metadata.isVisible());
            mainPanel.toolsPanel.setTabVisible(name, metadata.isVisible());
            if (metadata.isVisible())
            {
                mainPanel.setSelectedComponent(mainPanel.toolsPanel);
            }
        }
        catch (ToolsException e)
        {
            JOptionPane.showMessageDialog(null, ApplicationUtilities.getResourceString("error") +
                ": " + e.getMessage(), ApplicationUtilities.getResourceString("error"),
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Execute the "Options" command
     */
    public void commandOptions()
    {
        try
        {
            new OntoBuilderOptions(frame, this, options).setVisible(true);// show();
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Displays the url
     *
     * @param url the {@link URL} to display
     */
    public void displayURL(final URL url)
    {
        setStatus(ApplicationUtilities.getResourceString("status.msg.openingPage") + " " +
            url.toExternalForm() + "...");
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                try
                {
                    StringWriter messages = new StringWriter();
                    StringOutputStream source = new StringOutputStream();

                    // DOM
                    org.w3c.dom.Document document = DOMUtilities.getDOM(
                        url,
                        new PrintWriter(messages),
                        Boolean.valueOf(
                            (String) options.getOptionValue(DOMUtilities.XML_VALIDATION_PROPERTY))
                            .booleanValue());
                    if (document == null)
                    {
                        JOptionPane.showMessageDialog(OntoBuilder.this,
                            ApplicationUtilities.getResourceString("error") + ": " +
                                ApplicationUtilities.getResourceString("error.invalidHTML"),
                            ApplicationUtilities.getResourceString("error"),
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    try
                    {
                        final URL refreshURL = HTMLUtilitiesGui.getMETARefresh(document);
                        if (refreshURL != null &&
                            Boolean.valueOf(
                                (String) options
                                    .getOptionValue(HTMLUtilitiesGui.HTML_META_NAVIGATION_PROPERTY))
                                .booleanValue())
                        {
                            SwingUtilities.invokeLater(new Runnable()
                            {
                                public void run()
                                {
                                    browser.setCurrentURL(refreshURL.toExternalForm());
                                    displayURL(refreshURL);
                                }
                            });
                            return;
                        }
                    }
                    catch (MalformedURLException e)
                    {
                        JOptionPane.showMessageDialog(
                            OntoBuilder.this,
                            ApplicationUtilities.getResourceString("error") + ": " + e.getMessage(),
                            ApplicationUtilities.getResourceString("error"),
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Messages
                    if (Boolean.valueOf(
                        (String) options.getOptionValue(MainPanel.MESSAGE_PANEL_VISIBLE_PROPERTY))
                        .booleanValue())
                    {
                        mainPanel.messagePanel.setMessage(messages.toString());
                    }

                    // HTML Source
                    if (Boolean.valueOf(
                        (String) options.getOptionValue(MainPanel.SOURCE_PANEL_VISIBLE_PROPERTY))
                        .booleanValue())
                    {
                        DOMUtilities.prettyPrint(document, source);
                        mainPanel.sourcePanel.setSourceText(source.toString());
                    }

                    // HTML
                    if (Boolean.valueOf(
                        (String) options.getOptionValue(MainPanel.HTML_PANEL_VISIBLE_PROPERTY))
                        .booleanValue())
                    {
                        if (source.toString().length() == 0)
                            DOMUtilities.prettyPrint(document, source);
                        mainPanel.htmlPanel.setHTMLText(source.toString());
                    }

                    // DOM Tree
                    if (Boolean.valueOf(
                        (String) options.getOptionValue(UpperPanel.DOM_PANEL_VISIBLE_PROPERTY))
                        .booleanValue())
                    {
                        JTree tree = DOMUtilitiesGui.convertDOMtoJTree(document);
                        tree.addTreeSelectionListener(new TreeSelectionListener()
                        {
                            public void valueChanged(TreeSelectionEvent e)
                            {
                                DefaultMutableTreeNode node = (DefaultMutableTreeNode) ((JTree) e
                                    .getSource()).getLastSelectedPathComponent();
                                if (node == null)
                                    return;
                                Object object = node.getUserObject();
                                if (object instanceof DOMNode)
                                {
                                    org.w3c.dom.Node n = ((DOMNode) object)
                                        .getNode();
                                    if (n.getNodeType() == org.w3c.dom.Node.TEXT_NODE)
                                        System.out.println("The node value is: '" +
                                            n.getNodeValue() + "' (" +
                                            (int) n.getNodeValue().charAt(0) + ")");
                                }
                                if (object instanceof ObjectWithProperties)
                                    lowerPanel.propertiesPanel
                                        .showProperties(((ObjectWithProperties) object)
                                            .getProperties());
                                else
                                    lowerPanel.propertiesPanel.showProperties(null);
                            }
                        });
                        upperPanel.domPanel.showTree(tree);
                    }

                    // DOM Hypertree
                    if (Boolean.valueOf(
                        (String) options
                            .getOptionValue(UpperPanel.HYPERBOLICDOM_PANEL_VISIBLE_PROPERTY))
                        .booleanValue())
                    {
                        HyperTree hyperTree = DOMUtilitiesGui.convertDOMtoHyperTree(document);
                        upperPanel.hyperbolicDOMPanel.showTree(hyperTree);
                    }

                    // HTML Elements
                    if (Boolean.valueOf(
                        (String) options.getOptionValue(MainPanel.ELEMENTS_PANEL_VISIBLE_PROPERTY))
                        .booleanValue())
                    {
                        JTree elementsTree = HTMLUtilitiesGui.getElementsHierarchy(document, url);
                        elementsTree.addTreeSelectionListener(new TreeSelectionListener()
                        {
                            public void valueChanged(TreeSelectionEvent e)
                            {
                                DefaultMutableTreeNode node = (DefaultMutableTreeNode) ((JTree) e
                                    .getSource()).getLastSelectedPathComponent();
                                if (node == null)
                                    return;
                                Object object = node.getUserObject();
                                if (object instanceof ObjectWithProperties)
                                    lowerPanel.propertiesPanel
                                        .showProperties(((ObjectWithProperties) object)
                                            .getProperties());
                                else
                                    lowerPanel.propertiesPanel.showProperties(null);
                            }
                        });
                        mainPanel.elementsPanel.showElementsHierarchy(elementsTree);
                    }
                }
                catch (IOException e)
                {
                    JOptionPane.showMessageDialog(OntoBuilder.this,
                        ApplicationUtilities.getResourceString("error") + ": " + e.getMessage(),
                        ApplicationUtilities.getResourceString("error"), JOptionPane.ERROR_MESSAGE);
                }
                finally
                {
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    setTemporalStatus(ApplicationUtilities.getResourceString("status.msg.done"));
                }
            }
        });
    }

    /**
     * Execute the "OntologyWizard" command
     */
    public void commandOntologyWizard(final URL url)
    {
        setTemporalStatus(ApplicationUtilities.getResourceString("status.msg.ontowizard"));
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                Document document = null;
                try
                {
                    // DOM
                    document = DOMUtilities.getDOM(
                        url,
                        new PrintWriter(new StringWriter()),
                        Boolean.valueOf(
                            (String) options.getOptionValue(DOMUtilities.XML_VALIDATION_PROPERTY))
                            .booleanValue());
                    if (document == null)
                    {
                        JOptionPane.showMessageDialog(OntoBuilder.this,
                            ApplicationUtilities.getResourceString("error") + ": " +
                                ApplicationUtilities.getResourceString("error.invalidHTML"),
                            ApplicationUtilities.getResourceString("error"),
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                catch (IOException e)
                {
                    JOptionPane.showMessageDialog(OntoBuilder.this,
                        ApplicationUtilities.getResourceString("error") + ": " + e.getMessage(),
                        ApplicationUtilities.getResourceString("error"), JOptionPane.ERROR_MESSAGE);
                }
                finally
                {
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
                if (document == null)
                    return;
                OntologyWizard wizard = new OntologyWizard(frame, url, document, OntoBuilder.this);
                Ontology ontology = wizard.startOntologyCreation();
                if (ontology != null)
                {
                    setTemporalStatus(StringUtilities.getReplacedString(
                        ApplicationUtilities.getResourceString("status.msg.ontologyReady"),
                        new String[]
                        {
                            ontology.getName()
                        }));
                    OntologyGui ontologyGui = new OntologyGui(ontology);
                    ontologyGui.addOntologySelectionListener(new OntologySelectionListener()
                    {
                        public void valueChanged(OntologySelectionEvent e)
                        {
                            Object object = e.getSelectedObject();
                            if (object == null)
                                return;
                            if (object instanceof ObjectWithProperties)
                                lowerPanel.propertiesPanel
                                    .showProperties(((ObjectWithProperties) object).getProperties());
                            else
                                lowerPanel.propertiesPanel.showProperties(null);
                        }
                    });
                    ontology.addOntologyModelListener(new OntologyModelAdapter()
                    {
                        public void objectChanged(OntologyModelEvent e)
                        {
                            Object object = e.getObject();
                            if (object instanceof ObjectWithProperties)
                                lowerPanel.propertiesPanel
                                    .showProperties(((ObjectWithProperties) object).getProperties());
                            else
                                lowerPanel.propertiesPanel.showProperties(null);
                        }
                    });
                    mainPanel.ontologyPanel.addOntology(ontologyGui);
                    mainPanel.selectPanel(MainPanel.ONTOLOGY_TAB);
                }
            }
        });
    }

    /**
     * Execute the "CreateSiteMap" command
     */
    public void commandCreateSiteMap(final URL url)
    {
        setStatus(ApplicationUtilities.getResourceString("status.msg.creatingSiteMap") + " " +
            url.toExternalForm() + "...");
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        final SiteMap siteMap = new SiteMap(url);
        final SitesVisited sitesVisited = new SitesVisited(isApplet() ? null : frame, siteMap);
        sitesVisited.setVisible(true);// show();

        Thread t = new Thread(new Runnable()
        {
            public void run()
            {
                try
                {
                    StringWriter messages = new StringWriter();

                    siteMap.addURLVisitedListener(sitesVisited);
                    siteMap.addSiteMapOperationListener(sitesVisited);
                    siteMap.constructSiteMap(Integer.parseInt((String) options
                        .getOptionValue(SiteMap.CRAWLING_DEPTH_PROPERTY)),
                        new PrintWriter(messages));

                    mainPanel.messagePanel.setMessage(messages.toString());

                    HyperTree hyperTree = siteMap.getHyperTreeMap();
                    mainPanel.siteMapPanel.showTree(hyperTree);

                    JTree tree = siteMap.getJTreeMap();
                    tree.addTreeSelectionListener(new TreeSelectionListener()
                    {
                        public void valueChanged(TreeSelectionEvent e)
                        {
                            DefaultMutableTreeNode node = (DefaultMutableTreeNode) ((JTree) e
                                .getSource()).getLastSelectedPathComponent();
                            if (node == null)
                                return;
                            ObjectWithProperties o = (ObjectWithProperties) node.getUserObject();
                            lowerPanel.propertiesPanel.showProperties(o.getProperties());
                        }
                    });
                    mainPanel.siteMapPanel.showTree(tree);

                    mainPanel.selectPanel(MainPanel.SITEMAP_TAB);
                }
                catch (IOException e)
                {
                    JOptionPane.showMessageDialog(OntoBuilder.this,
                        ApplicationUtilities.getResourceString("error") + ": " + e.getMessage(),
                        ApplicationUtilities.getResourceString("error"), JOptionPane.ERROR_MESSAGE);
                }
                finally
                {
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    setTemporalStatus(ApplicationUtilities.getResourceString("status.msg.done"));
                }
            }
        });
        t.start();
    }

    /**
     * Execute the "FileOpen" command
     */
    public void commandFileOpen()
    {

        ImporterMetadata[] metadata = null;

        metadata = ImportUtilities.getAllImporterMetadata();

        // Filters
        ArrayList<FileFilter> filters = new ArrayList<FileFilter>();
        filters.add(ontologyFileFilter);
        FileUtilities.configureFileDialogFilters(filters);
        // Viewers
        FileUtilities.fileViewer.removeAllViewers();
        FileUtilities.fileViewer.addViewer(ontologyFileViewer);

        // ArrayList filters=new ArrayList();
        // FileUtilities.fileViewer.removeAllViewers();
        // FileUtilities.filePreviewer.removeAll();
        //
        // //XML
        // filters.add(XMLUtilities.xmlFileFilter);
        // FileUtilities.fileViewer.addViewer(XMLUtilities.xmlFileViewer);
        // FileUtilities.filePreviewer.addPreviewer(XMLUtilities.xmlFilePreviewer);

        // Other formats
        for (int i = 0; i < metadata.length; i++)
        {
            FileUtilities.fileViewer.addViewer(new GeneralFileView(metadata[i].getExtension(),
                metadata[i].getIcon()));
            FileUtilities.filePreviewer.addPreviewer(new GeneralFilePreviewer(metadata[i]
                .getExtension()));
            filters.add(new GeneralFileFilter(metadata[i].getExtension(), "Open " +
                metadata[i].getType() + " file"));
        }

        FileUtilities.configureFileDialogFilters(filters);

        final File file = FileUtilities.openFileDialog(this);
        try
        {
            if (file != null)
            {

                if (file.getName().endsWith(".xml") || file.getName().endsWith(".ont"))
                {
                    setTemporalStatus(ApplicationUtilities
                        .getResourceString("status.msg.openingOntology") + " " + file + "...");
                    setCursor(new Cursor(Cursor.WAIT_CURSOR));
                    SwingUtilities.invokeLater(new Runnable()
                    {
                        public void run()
                        {
                            try
                            {
                                Ontology ontology = OntologyGui.open(file);
                                addOntologyToPanel(ontology);
                            }
                            catch (IOException e)
                            {
                                JOptionPane.showMessageDialog(
                                    OntoBuilder.this,
                                    ApplicationUtilities.getResourceString("error") + ": " +
                                        e.getMessage(),
                                    ApplicationUtilities.getResourceString("error"),
                                    JOptionPane.ERROR_MESSAGE);
                            }
                            finally
                            {
                                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                                setTemporalStatus(ApplicationUtilities
                                    .getResourceString("status.msg.done"));
                            }
                        }
                    });
                }

                else
                {
                    setTemporalStatus(ApplicationUtilities
                        .getResourceString("status.msg.openingOntology") + " " + file + "...");
                    setCursor(new Cursor(Cursor.WAIT_CURSOR));
                    SwingUtilities.invokeLater(new Runnable()
                    {
                        public void run()
                        {
                            try
                            {

                                Importer importer = ImportUtilities.getImporterPlugin(FileUtilities
                                    .getFileExtension(file));
                                Ontology ontology = importer.importFile(file);
                                addOntologyToPanel(ontology);
                                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

                            }
                            catch (ImportException e)
                            {
                                e.printStackTrace();
                                JOptionPane.showMessageDialog(
                                    null,
                                    ApplicationUtilities.getResourceString("error") + ": " +
                                        e.getMessage(),
                                    ApplicationUtilities.getResourceString("error"),
                                    JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    });
                }
            }
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(OntoBuilder.this,
                ApplicationUtilities.getResourceString("error") + ": " + e.getMessage(),
                ApplicationUtilities.getResourceString("error"), JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Execute the "Print" command.
     * <br> Not implemented
     */
    public void commandPrint()
    {
    }

    /**
     * Add an ontology to the panel
     *
     * @param ontology the {@link Ontology} to add
     */
    public void addOntologyToPanel(Ontology ontology)
    {
        if (ontology == null)
            return;
        OntologyGui ontologyGui = new OntologyGui(ontology);
        ontologyGui.addOntologySelectionListener(new OntologySelectionListener()
        {
            public void valueChanged(OntologySelectionEvent e)
            {
                Object object = e.getSelectedObject();
                if (object == null)
                    return;
                if (object instanceof ObjectWithProperties)
                    lowerPanel.propertiesPanel.showProperties(((ObjectWithProperties) object)
                        .getProperties());
                else
                    lowerPanel.propertiesPanel.showProperties(null);
            }
        });
        ontology.addOntologyModelListener(new OntologyModelAdapter()
        {
            public void objectChanged(OntologyModelEvent e)
            {
                Object object = e.getObject();
                if (object instanceof ObjectWithProperties)
                    lowerPanel.propertiesPanel.showProperties(((ObjectWithProperties) object)
                        .getProperties());
                else
                    lowerPanel.propertiesPanel.showProperties(null);
            }
        });
        mainPanel.ontologyPanel.addOntology(ontologyGui);
        mainPanel.selectPanel(MainPanel.ONTOLOGY_TAB);
    }

    /**
     * Execute the "NewOntology" command
     */
    public void commandNewOntology()
    {       
        OntologyGui ontologyGui = OntologyGui.createOntologyDialog();        
        Ontology ontology = ontologyGui.getOntology();

        try
        {
            Ontology template = OntologyGui.open(new File("ontologies/base.xml"));
            template.setFile(null);
            template.setDirty(true);
            template.setName(ontology.getName());
            template.setSiteURL(ontology.getSiteURL());
            template.setTitle(ontology.getTitle());
            ontology = template;
        }
        catch (IOException e)
        {
        }
        addOntologyToPanel(ontology);
    }

    /**
     * Execute the "OpenOntology" command
     */
    public void commandOpenOntology()
    {
        // Filters
        ArrayList<FileFilter> filters = new ArrayList<FileFilter>();
        filters.add(ontologyFileFilter);
        FileUtilities.configureFileDialogFilters(filters);
        // Viewers
        FileUtilities.fileViewer.removeAllViewers();
        FileUtilities.fileViewer.addViewer(ontologyFileViewer);

        final File file = FileUtilities.openFileDialog(this);
        if (file != null)
        {
            setTemporalStatus(ApplicationUtilities.getResourceString("status.msg.openingOntology") +
                " " + file + "...");
            setCursor(new Cursor(Cursor.WAIT_CURSOR));
            SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {
                    try
                    {
                        Ontology ontology = OntologyGui.open(file);
                        addOntologyToPanel(ontology);
                    }
                    catch (IOException e)
                    {
                        JOptionPane.showMessageDialog(
                            OntoBuilder.this,
                            ApplicationUtilities.getResourceString("error") + ": " + e.getMessage(),
                            ApplicationUtilities.getResourceString("error"),
                            JOptionPane.ERROR_MESSAGE);
                    }
                    finally
                    {
                        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        setTemporalStatus(ApplicationUtilities.getResourceString("status.msg.done"));
                    }
                }
            });
        }
    }

    /**
     * Execute the "SaveOntology" command
     *
     * @return <code>true</code> on success
     */
    public boolean commandSaveOntology()
    {
        final Ontology ontology = mainPanel.ontologyPanel.getCurrentOntology();
        if (ontology == null)
        {
            JOptionPane.showMessageDialog(this, "There are no opened ontologies!");
            return true;
        }
        final File file = ontology.getFile();
        if (file == null)
            return commandSaveAsOntology();
        else
        {
            setTemporalStatus(ApplicationUtilities.getResourceString("status.msg.savingOntology") +
                " " + file + "...");
            setCursor(new Cursor(Cursor.WAIT_CURSOR));
            SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {
                    try
                    {
                        ontology.save(file);
                    }
                    catch (IOException e)
                    {
                        JOptionPane.showMessageDialog(
                            OntoBuilder.this,
                            ApplicationUtilities.getResourceString("error") + ": " + e.getMessage(),
                            ApplicationUtilities.getResourceString("error"),
                            JOptionPane.ERROR_MESSAGE);
                    }
                    finally
                    {
                        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        setTemporalStatus(ApplicationUtilities.getResourceString("status.msg.done"));
                    }
                }
            });
        }
        return true;
    }

    /**
     * Execute the "SaveLightOntology" command
     *
     * @return <code>true</code> on success
     */
    public boolean commandSaveLightOntology()
    {
        final Ontology ontology = mainPanel.ontologyPanel.getCurrentOntology();
        if (ontology == null)
        {
            JOptionPane.showMessageDialog(this, "There are no opened ontologies!");
            return true;
        }
        final File file = ontology.getFile();
        if (file == null)
            return commandSaveAsOntology();
        else
        {
            setTemporalStatus(ApplicationUtilities.getResourceString("status.msg.savingOntology") +
                " " + file + "...");
            setCursor(new Cursor(Cursor.WAIT_CURSOR));
            SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {
                    try
                    {
                        ontology.save(file, Ontology.LIGHT_XML_FORMAT);
                    }
                    catch (IOException e)
                    {
                        JOptionPane.showMessageDialog(
                            OntoBuilder.this,
                            ApplicationUtilities.getResourceString("error") + ": " + e.getMessage(),
                            ApplicationUtilities.getResourceString("error"),
                            JOptionPane.ERROR_MESSAGE);
                    }
                    finally
                    {
                        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        setTemporalStatus(ApplicationUtilities.getResourceString("status.msg.done"));
                    }
                }
            });
        }
        return true;
    }

    /**
     * Execute the "SaveAsOntology" command
     *
     * @return <code>true</code> on success
     */
    public boolean commandSaveAsOntology()
    {
        // Filters
        ArrayList<FileFilter> filters = new ArrayList<FileFilter>();
        filters.add(ontologyBIZFileFilter);
        filters.add(ontologyONTFileFilter);
        filters.add(ontologyXMLFileFilter);
        FileUtilities.configureFileDialogFilters(filters);
        // Viewers
        FileUtilities.fileViewer.removeAllViewers();
        FileUtilities.fileViewer.addViewer(ontologyFileViewer);

        final Ontology ontology = mainPanel.ontologyPanel.getCurrentOntology();
        if (ontology == null)
        {
            JOptionPane.showMessageDialog(this, "There are no opened ontologies!");
            return true;
        }
        final File file = FileUtilities
            .saveFileDialog(this, ontology.getFile() != null ? ontology.getFile() : new File(
                ontology.getName() + ".xml"));
        if (file != null)
        {
            setTemporalStatus(ApplicationUtilities.getResourceString("status.msg.savingOntology") +
                " " + file + "...");
            setCursor(new Cursor(Cursor.WAIT_CURSOR));
            SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {
                    File theFile;
                    try
                    {
                        String extFile = file.getAbsolutePath();
                        String ext = FileUtilities.getFileExtension(file);
                        short format = Ontology.XML_FORMAT;
                        javax.swing.filechooser.FileFilter filter = FileUtilities.fileChooser
                            .getFileFilter();
                        if (filter instanceof OntologyXMLFileFilter)
                        {
                            format = Ontology.XML_FORMAT;
                            if (ext == null || !ext.equalsIgnoreCase("xml"))
                                extFile += ".xml";
                        }
                        else if (filter instanceof OntologyONTFileFilter)
                        {
                            format = Ontology.BINARY_FORMAT;
                            if (ext == null || !ext.equalsIgnoreCase("ont"))
                                extFile += ".ont";
                        }
                        else if (filter instanceof OntologyBIZFileFilter)
                        {
                            format = Ontology.BIZTALK_FORMAT;
                            if (ext == null || !ext.equalsIgnoreCase("xml"))
                                extFile += ".xml";
                        }
                        theFile = new File(extFile);
                        ontology.save(theFile, format);
                    }
                    catch (IOException e)
                    {
                        JOptionPane.showMessageDialog(
                            OntoBuilder.this,
                            ApplicationUtilities.getResourceString("error") + ": " + e.getMessage(),
                            ApplicationUtilities.getResourceString("error"),
                            JOptionPane.ERROR_MESSAGE);
                    }
                    finally
                    {
                        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        setTemporalStatus(ApplicationUtilities.getResourceString("status.msg.done"));
                    }
                }
            });
            return true;
        }
        return false;
    }

    /**
     * Execute the "ImportFile" command
     */
    public void commandImportFile(final String type)
    {

        try
        {

            ImporterMetadata metadata = null;

            try
            {
                metadata = ImportUtilities.getImporterMetadata(type);
            }
            catch (ImportException e)
            {
                JOptionPane.showMessageDialog(null,
                    ApplicationUtilities.getResourceString("error") + ": " + e.getMessage(),
                    ApplicationUtilities.getResourceString("error"), JOptionPane.ERROR_MESSAGE);
            }

            // Filters
            ArrayList<FileFilter> filters = new ArrayList<FileFilter>();
            filters.add(new GeneralFileFilter(metadata.getExtension(), "Open " +
                metadata.getType() + " file"));
            FileUtilities.configureFileDialogFilters(filters);
            // Viewers
            FileUtilities.fileViewer.removeAllViewers();
            FileUtilities.fileViewer.addViewer(new GeneralFileView(metadata.getExtension(),
                metadata.getIcon()));
            final File file = FileUtilities.openFileDialog(this);
            // Previewers
            FilePreviewer filePrefviewer = new FilePreviewer();
            filePrefviewer.addPreviewer(new GeneralFilePreviewer(metadata.getExtension()));
            FileUtilities.configureFileDialogPreviewer(filePrefviewer);
            if (file != null)
            {
                setTemporalStatus(ApplicationUtilities
                    .getResourceString("status.msg.openingOntology") + " " + file + "...");
                setCursor(new Cursor(Cursor.WAIT_CURSOR));
                SwingUtilities.invokeLater(new Runnable()
                {
                    public void run()
                    {
                        try
                        {

                            Importer importer = ImportUtilities.getImporterPlugin(type);
                            Ontology ontology = importer.importFile(file);
                            addOntologyToPanel(ontology);
                            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

                        }
                        catch (ImportException e)
                        {
                            e.printStackTrace();
                            JOptionPane.showMessageDialog(
                                null,
                                ApplicationUtilities.getResourceString("error") + ": " +
                                    e.getMessage(),
                                ApplicationUtilities.getResourceString("error"),
                                JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });

            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            setTemporalStatus(ApplicationUtilities.getResourceString("status.msg.done"));
        }
    }

    /**
     * Execute the "ExportFile" command
     */
    public void commandExportFile(final String type)
    {

        try
        {

            ExporterMetadata metadata = null;

            try
            {
                metadata = ExportUtilities.getExporterMetadata(type);
            }
            catch (ExportException e)
            {
                JOptionPane.showMessageDialog(null,
                    ApplicationUtilities.getResourceString("error") + ": " + e.getMessage(),
                    ApplicationUtilities.getResourceString("error"), JOptionPane.ERROR_MESSAGE);
            }

            // Filters
            ArrayList<FileFilter> filters = new ArrayList<FileFilter>();
            filters.add(new GeneralFileFilter(metadata.getExtension(), "Open " +
                metadata.getType() + " file"));
            FileUtilities.configureFileDialogFilters(filters);
            // Viewers
            FileUtilities.fileViewer.removeAllViewers();
            FileUtilities.fileViewer.addViewer(new GeneralFileView(metadata.getExtension(),
                metadata.getIcon()));
            // Previewers
            FilePreviewer filePrefviewer = new FilePreviewer();
            filePrefviewer.addPreviewer(new GeneralFilePreviewer(metadata.getExtension()));
            FileUtilities.configureFileDialogPreviewer(filePrefviewer);

            final File file = FileUtilities.openFileDialog(this);

            if (file != null)
            {
                setTemporalStatus(ApplicationUtilities
                    .getResourceString("status.msg.openingOntology") + " " + file + "...");
                setCursor(new Cursor(Cursor.WAIT_CURSOR));
                SwingUtilities.invokeLater(new Runnable()
                {
                    public void run()
                    {
                        try
                        {

                            final Ontology ontology = mainPanel.ontologyPanel.getCurrentOntology();
                            if (ontology == null)
                            {
                                JOptionPane.showMessageDialog(null,
                                    "There are no opened ontologies!");
                                return;
                            }

                            Exporter exporter = ExportUtilities.getExporterPlugin(type);
                            HashMap<String, Ontology> params = new HashMap<String, Ontology>();
                            params.put("Ontology", ontology);
                            exporter.export(params, file);
                            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

                        }
                        catch (ExportException e)
                        {
                            JOptionPane.showMessageDialog(
                                null,
                                ApplicationUtilities.getResourceString("error") + ": " +
                                    e.getMessage(),
                                ApplicationUtilities.getResourceString("error"),
                                JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });

            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            setTemporalStatus(ApplicationUtilities.getResourceString("status.msg.done"));
        }
    }

    /**
     * Execute the "CloseOntology" command
     *
     * @return <code>true</code> on success
     */
    public boolean commandCloseOntology()
    {
        Ontology ontology = mainPanel.ontologyPanel.getCurrentOntology();
        if (ontology == null)
        {
            JOptionPane.showMessageDialog(this, "There are no opened ontologies!");
            return true;
        }
        if (ontology.isDirty())
        {
            int response = JOptionPane.showConfirmDialog(
                this,
                StringUtilities.getReplacedString(
                    ApplicationUtilities.getResourceString("ontology.close"), new String[]
                    {
                        ontology.getName()
                    }), ApplicationUtilities.getResourceString("ontology"),
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (response == JOptionPane.CANCEL_OPTION)
                return false;
            if (response == JOptionPane.YES_OPTION)
                commandSaveOntology();
        }
        mainPanel.ontologyPanel.closeCurrentOntology();
        return true;
    }

    /**
     * Execute the "ViewHyperbolicOntology" command
     */
    public void commandViewHyperbolicOntology()
    {
        Ontology ontology = mainPanel.ontologyPanel.getCurrentOntology();
        if (ontology == null)
            return;
        OntologyGui ontologyGui = new OntologyGui(ontology);
        new OntologyHyperTree(ontologyGui, frame).setVisible(true);// show();
    }

    /**
     * Execute the "ViewGraphOntology" command
     */
    public void commandViewGraphOntology()
    {
        Ontology ontology = mainPanel.ontologyPanel.getCurrentOntology();
        if (ontology == null)
            return;
        OntologyGui ontologyGui = new OntologyGui(ontology);
        JGraph graph = ontologyGui.getGraph();
        new OntologyGraph(frame, graph).setVisible(true);// show();
    }

    /**
     * Execute the "NormalizeOntology" command
     */
    public void commandNormalizeOntology()
    {
        Ontology ontology = mainPanel.ontologyPanel.getCurrentOntology();
        if (ontology == null)
            return;
        ontology.normalize();
        JOptionPane.showMessageDialog(this,
            ApplicationUtilities.getResourceString("message.normalizeOntology.done"),
            ApplicationUtilities.getResourceString("message"), JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Execute the "MergeOntologiesWizard" command
     */
    public void commandMergeOntologiesWizard()
    {
        if (algorithms.size() == 0)
        {
            JOptionPane.showMessageDialog(OntoBuilder.this,
                ApplicationUtilities.getResourceString("warning.mergewizard.algorithms"),
                ApplicationUtilities.getResourceString("warning"), JOptionPane.ERROR_MESSAGE);
            return;
        }
        setTemporalStatus(ApplicationUtilities.getResourceString("status.msg.mergewizard"));
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                Vector<?> ontologies = mainPanel.ontologyPanel.getOntologies();
                if (ontologies.size() == 0)
                {
                    JOptionPane.showMessageDialog(OntoBuilder.this,
                        ApplicationUtilities.getResourceString("warning.mergewizard.ontologies"),
                        ApplicationUtilities.getResourceString("warning"),
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Ontology ontology = mainPanel.ontologyPanel.getCurrentOntology();
                OntologyMergeWizard wizard = new OntologyMergeWizard(frame, ontologies, ontology,
                    algorithms);
                Ontology mergedOntology = wizard.startOntologyMerge();
                addOntologyToPanel(mergedOntology);
            }
        });
    }

    /**
     * Execute the "MetaTopKWizard" command
     */
    public void commandMetaTopKWizard()
    {
        if (algorithms.size() == 0)
        {
            JOptionPane.showMessageDialog(OntoBuilder.this,
                ApplicationUtilities.getResourceString("warning.mergewizard.algorithms"),
                ApplicationUtilities.getResourceString("warning"), JOptionPane.ERROR_MESSAGE);
            return;
        }
        setTemporalStatus(ApplicationUtilities.getResourceString("status.msg.mergewizard"));
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                Vector<Ontology> ontologies = mainPanel.ontologyPanel.getOntologies();
                if (ontologies.size() == 0)
                {
                    JOptionPane.showMessageDialog(OntoBuilder.this,
                        ApplicationUtilities.getResourceString("warning.mergewizard.ontologies"),
                        ApplicationUtilities.getResourceString("warning"),
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Ontology ontology = mainPanel.ontologyPanel.getCurrentOntology();
                OntologyMetaTopKWizard wizard = new OntologyMetaTopKWizard(frame, ontologies,
                    ontology, algorithms);
                Ontology mergedOntology = wizard.startOntologyMerge();
                addOntologyToPanel(mergedOntology);
            }
        });
    }

    // Ontologies for Biztalk
    private Ontology source = null, target = null;

    /**
     * Execute the "BizTalkMapper" command
     */
    public void commandBizTalkMapper()
    {
        source = null;
        target = null;
        Vector<?> ontologies = mainPanel.ontologyPanel.getOntologies();
        if (ontologies.size() == 0)
        {
            JOptionPane.showMessageDialog(OntoBuilder.this,
                ApplicationUtilities.getResourceString("warning.biztalk.ontologies"),
                ApplicationUtilities.getResourceString("warning"), JOptionPane.ERROR_MESSAGE);
            return;
        }
        Ontology ontology = mainPanel.ontologyPanel.getCurrentOntology();
        final JList sourceList = new JList(ontologies);
        sourceList.setSelectedValue(ontology, true);
        source = ontology;
        final JList targetList = new JList(ontologies);

        final JDialog dialog = new JDialog(frame,
            ApplicationUtilities.getResourceString("biztalk.windowTitle"), true);
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        dialog.setSize(new Dimension(ApplicationUtilities.getIntProperty("biztalk.width"),
            ApplicationUtilities.getIntProperty("biztalk.height")));
        dialog.setLocationRelativeTo(frame);
        dialog.setResizable(false);

        JPanel south = new JPanel();
        south.setLayout(new FlowLayout(FlowLayout.RIGHT));
        south.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.black),
            BorderFactory.createEmptyBorder(0, 10, 10, 10)));
        final JButton okButton;
        south.add(okButton = new JButton(ApplicationUtilities
            .getResourceString("biztalk.button.ok")));
        dialog.getRootPane().setDefaultButton(okButton);
        okButton.setEnabled(false);
        okButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                setCursor(new Cursor(Cursor.WAIT_CURSOR));
                dialog.dispose();
                SwingUtilities.invokeLater(new Runnable()
                {
                    public void run()
                    {
                        try
                        {
                            ArrayList<FileFilter> filters = new ArrayList<FileFilter>();
                            filters.add(ontologyBIZFileFilter);
                            FileUtilities.configureFileDialogFilters(filters);
                            // Viewers
                            FileUtilities.fileViewer.removeAllViewers();
                            FileUtilities.fileViewer
                                .addViewer(ontologyFileViewer);

                            final File file = FileUtilities.saveFileDialog(OntoBuilder.this, null);
                            if (file != null)
                            {
                                String extFile = file.getAbsolutePath();
                                String ext = FileUtilities.getFileExtension(file);
                                if (ext == null || !ext.equalsIgnoreCase("xml"))
                                    extFile += ".xml";
                                File theFile = new File(extFile);
                                OntologyUtilities.createBilzTalkMapperFile(source, target, theFile);
                            }
                        }
                        catch (IOException e)
                        {
                            JOptionPane.showMessageDialog(
                                OntoBuilder.this,
                                ApplicationUtilities.getResourceString("error") + ": " +
                                    e.getMessage(),
                                ApplicationUtilities.getResourceString("error"),
                                JOptionPane.ERROR_MESSAGE);
                        }
                        finally
                        {
                            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                            setTemporalStatus(ApplicationUtilities
                                .getResourceString("status.msg.done"));
                        }
                    }
                });
            }
        });
        JButton cancelButton;
        south.add(cancelButton = new JButton(ApplicationUtilities
            .getResourceString("biztalk.button.cancel")));
        cancelButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                dialog.dispose();
            }
        });
        panel.add(BorderLayout.SOUTH, south);

        JLabel west = new JLabel(ApplicationUtilities.getImage("biztalkbanner.gif"));
        west.setBorder(BorderFactory.createLineBorder(Color.black));
        panel.add(BorderLayout.WEST, west);

        sourceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sourceList.addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent e)
            {
                source = (Ontology) sourceList.getSelectedValue();
                okButton.setEnabled(target != null && source != null);
            }
        });
        sourceList.setCellRenderer(new DefaultListCellRenderer()
        {

            private static final long serialVersionUID = 1L;

            public Component getListCellRendererComponent(JList list, Object value, int index,
                boolean isSelected, boolean cellHasFocus)
            {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setIcon(ApplicationUtilities.getImage("ontology.gif"));
                return this;
            }
        });

        targetList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        targetList.addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent e)
            {
                target = (Ontology) targetList.getSelectedValue();
                okButton.setEnabled(target != null && source != null);
            }
        });
        targetList.setCellRenderer(new DefaultListCellRenderer()
        {

            private static final long serialVersionUID = 1L;

            public Component getListCellRendererComponent(JList list, Object value, int index,
                boolean isSelected, boolean cellHasFocus)
            {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setIcon(ApplicationUtilities.getImage("ontology.gif"));
                return this;
            }
        });

        JPanel center = new JPanel(new GridBagLayout());
        panel.add(BorderLayout.CENTER, center);
        center.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));

        {// Title
            JLabel title = new JLabel(ApplicationUtilities.getResourceString("biztalk.title"),
                JLabel.LEFT);
            title.setFont(new Font(title.getFont().getFontName(), Font.BOLD, title.getFont()
                .getSize() + 6));
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.weightx = 1;
            gbcl.gridwidth = 2;
            gbcl.fill = GridBagConstraints.HORIZONTAL;
            gbcl.insets = new Insets(0, 0, 10, 0);
            gbcl.anchor = GridBagConstraints.NORTHWEST;
            center.add(title, gbcl);
        }

        {// Explanation
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = 1;
            gbcl.gridwidth = 2;
            gbcl.weightx = 1;
            gbcl.fill = GridBagConstraints.HORIZONTAL;
            gbcl.insets = new Insets(0, 0, 10, 0);
            gbcl.anchor = GridBagConstraints.WEST;
            center.add(
                new MultilineLabel(ApplicationUtilities.getResourceString("biztalk.explanation")),
                gbcl);
        }

        {// Sources
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = 2;
            gbcl.gridwidth = 2;
            gbcl.insets = new Insets(0, 5, 0, 0);
            gbcl.anchor = GridBagConstraints.WEST;
            JLabel sources = new JLabel(ApplicationUtilities.getResourceString("biztalk.source") +
                ":");
            sources.setFont(new Font(sources.getFont().getName(), Font.BOLD, sources.getFont()
                .getSize()));
            center.add(sources, gbcl);

            gbcl.gridy = 3;
            gbcl.insets = new Insets(0, 5, 5, 15);
            gbcl.weightx = 1.0;
            gbcl.weighty = 1.0;
            gbcl.fill = GridBagConstraints.BOTH;
            center.add(new JScrollPane(sourceList), gbcl);
        }

        {// Targets
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = 4;
            gbcl.gridwidth = 2;
            gbcl.insets = new Insets(0, 5, 0, 0);
            gbcl.anchor = GridBagConstraints.WEST;
            JLabel targets = new JLabel(ApplicationUtilities.getResourceString("biztalk.target") +
                ":");
            targets.setFont(new Font(targets.getFont().getName(), Font.BOLD, targets.getFont()
                .getSize()));
            center.add(targets, gbcl);

            gbcl.gridy = 5;
            gbcl.insets = new Insets(0, 5, 0, 15);
            gbcl.weightx = 1.0;
            gbcl.weighty = 1.0;
            gbcl.fill = GridBagConstraints.BOTH;
            center.add(new JScrollPane(targetList), gbcl);
        }

        dialog.addWindowListener(new WindowAdapter()
        {
            public void windowOpened(WindowEvent e)
            {
                SwingUtilities.invokeLater(new Runnable()
                {
                    public void run()
                    {
                        targetList.requestFocus();
                    }
                });
            }

            public void windowClosing(WindowEvent e)
            {
                dialog.dispose();
            }
        });
        dialog.setContentPane(panel);

        dialog.setVisible(true);// show();
    }
}