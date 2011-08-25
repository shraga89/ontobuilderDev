package ac.technion.iem.ontobuilder.gui.ontobuilder.elements;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;

import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.elements.Tab;
import ac.technion.iem.ontobuilder.gui.elements.TabbedPane;
import ac.technion.iem.ontobuilder.gui.ontobuilder.main.OntoBuilder;
import ac.technion.iem.ontobuilder.gui.tools.exactmapping.ToolsException;
import ac.technion.iem.ontobuilder.gui.tools.sitemap.SiteMapPanel;

/**
 * <p>
 * Title: MainPanel
 * </p>
 * Extends a {@link TabbedPane}
 */
public class MainPanel extends TabbedPane
{
    private static final long serialVersionUID = 1L;

    public final static String HTML_PANEL_VISIBLE_PROPERTY = "htmlPanelVisible";
    public final static String SOURCE_PANEL_VISIBLE_PROPERTY = "sourcePanelVisible";
    public final static String MESSAGE_PANEL_VISIBLE_PROPERTY = "messagePanelVisible";
    public final static String SITEMAP_PANEL_VISIBLE_PROPERTY = "sitemapPanelVisible";
    public final static String ELEMENTS_PANEL_VISIBLE_PROPERTY = "elementsPanelVisible";
    public final static String ONTOLOGY_PANEL_VISIBLE_PROPERTY = "ontologyPanelVisible";
    public final static String TOOLS_PANEL_VISIBLE_PROPERTY = "toolsPanelVisible";

    public final static String HTML_TAB = "html";
    public final static String SOURCE_TAB = "source";
    public final static String MESSAGE_TAB = "message";
    public final static String SITEMAP_TAB = "sitemap";
    public final static String ELEMENTS_TAB = "elements";
    public final static String ONTOLOGY_TAB = "ontology";
    public final static String TOOLS_TAB = "tools";

    // public CobraHTMLPanel htmlPanel;
    public HTMLPanel htmlPanel;
    public SourcePanel sourcePanel;
    public MessagePanel messagePanel;
    public SiteMapPanel siteMapPanel;
    public ElementsPanel elementsPanel;
    public OntologyPanel ontologyPanel;
    public ToolsPanel toolsPanel;

    protected JScrollPane htmlPanelScroll;
    protected JScrollPane sourcePanelScroll;
    protected JScrollPane messagePanelScroll;
    protected JScrollPane elementsPanelScroll;

    /**
     * Constructs a MainPanel
     * 
     * @param ontoBuilder the ontoBuilder application
     */
    public MainPanel(OntoBuilder ontoBuilder) throws ToolsException
    {
        super();
        setBorder(BorderFactory.createTitledBorder(ApplicationUtilities
            .getResourceString("panel.main")));

        Tab tab0 = new Tab(HTML_TAB, ApplicationUtilities.getResourceString("panel.htmlView"),
            htmlPanelScroll = new JScrollPane(htmlPanel = new HTMLPanel(ontoBuilder)/*
                                                                                     * new
                                                                                     * CobraHTMLPanel
                                                                                     * (ontoBuilder)
                                                                                     */));
        tab0.setIcon(ApplicationUtilities.getImage("html.gif"));
        addTab(tab0);

        Tab tab1 = new Tab(SOURCE_TAB, ApplicationUtilities.getResourceString("panel.htmlSource"),
            sourcePanelScroll = new JScrollPane(sourcePanel = new SourcePanel(ontoBuilder)));
        tab1.setIcon(ApplicationUtilities.getImage("source.gif"));
        addTab(tab1);

        Tab tab2 = new Tab(MESSAGE_TAB, ApplicationUtilities.getResourceString("panel.messages"),
            messagePanelScroll = new JScrollPane(messagePanel = new MessagePanel(ontoBuilder)));
        tab2.setIcon(ApplicationUtilities.getImage("message.gif"));
        addTab(tab2);

        Tab tab3 = new Tab(SITEMAP_TAB, ApplicationUtilities.getResourceString("panel.sitemap"),
            siteMapPanel = new SiteMapPanel(ontoBuilder));
        tab3.setIcon(ApplicationUtilities.getImage("sitemap.gif"));
        addTab(tab3);

        Tab tab4 = new Tab(ELEMENTS_TAB, ApplicationUtilities.getResourceString("panel.elements"),
            elementsPanelScroll = new JScrollPane(elementsPanel = new ElementsPanel(ontoBuilder)));
        tab4.setIcon(ApplicationUtilities.getImage("elements.gif"));
        addTab(tab4);

        Tab tab5 = new Tab(ONTOLOGY_TAB, ApplicationUtilities.getResourceString("panel.ontology"),
            ontologyPanel = new OntologyPanel(ontoBuilder));
        tab5.setIcon(ApplicationUtilities.getImage("ontology.gif"));
        addTab(tab5);

        Tab tab6 = new Tab(TOOLS_TAB, ApplicationUtilities.getResourceString("panel.tools"),
            toolsPanel = new ToolsPanel(ontoBuilder));
        tab6.setIcon(ApplicationUtilities.getImage("tools.gif"));
        addTab(tab6);

        toolsPanel.initToolTabs();

        setTabVisible(HTML_TAB, Boolean.valueOf(ontoBuilder.getOption(HTML_PANEL_VISIBLE_PROPERTY))
            .booleanValue());
        setTabVisible(SOURCE_TAB,
            Boolean.valueOf(ontoBuilder.getOption(SOURCE_PANEL_VISIBLE_PROPERTY)).booleanValue());
        setTabVisible(MESSAGE_TAB,
            Boolean.valueOf(ontoBuilder.getOption(MESSAGE_PANEL_VISIBLE_PROPERTY)).booleanValue());
        setTabVisible(SITEMAP_TAB,
            Boolean.valueOf(ontoBuilder.getOption(SITEMAP_PANEL_VISIBLE_PROPERTY)).booleanValue());
        setTabVisible(ELEMENTS_TAB,
            Boolean.valueOf(ontoBuilder.getOption(ELEMENTS_PANEL_VISIBLE_PROPERTY)).booleanValue());
        setTabVisible(ONTOLOGY_TAB,
            Boolean.valueOf(ontoBuilder.getOption(ONTOLOGY_PANEL_VISIBLE_PROPERTY)).booleanValue());
        setTabVisible(TOOLS_TAB,
            Boolean.valueOf(ontoBuilder.getOption(TOOLS_PANEL_VISIBLE_PROPERTY)).booleanValue());
    }

    public void selectPanel(String panel)
    {
        setTabVisible(panel, true);
        if (panel.equals(HTML_TAB))
            setSelectedComponent(htmlPanelScroll);
        else if (panel.equals(SOURCE_TAB))
            setSelectedComponent(sourcePanelScroll);
        else if (panel.equals(MESSAGE_TAB))
            setSelectedComponent(messagePanelScroll);
        else if (panel.equals(SITEMAP_TAB))
            setSelectedComponent(siteMapPanel);
        else if (panel.equals(ELEMENTS_TAB))
            setSelectedComponent(elementsPanelScroll);
        else if (panel.equals(ONTOLOGY_TAB))
            setSelectedComponent(ontologyPanel);
        else if (panel.equals(TOOLS_TAB))
            setSelectedComponent(toolsPanel);
    }
}