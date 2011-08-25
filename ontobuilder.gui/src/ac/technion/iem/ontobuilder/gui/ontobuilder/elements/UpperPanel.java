package ac.technion.iem.ontobuilder.gui.ontobuilder.elements;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;

import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.elements.Tab;
import ac.technion.iem.ontobuilder.gui.elements.TabbedPane;
import ac.technion.iem.ontobuilder.gui.ontobuilder.main.OntoBuilder;
import ac.technion.iem.ontobuilder.gui.utils.thesaurus.ThesaurusPanel;

/**
 * <p>Title: UpperPanel</p>
 * Extends a {@link TabbedPane}
 */
public class UpperPanel extends TabbedPane
{
    private static final long serialVersionUID = 1L;

    public final static String DOM_PANEL_VISIBLE_PROPERTY = "domPanelVisible";
    public final static String HYPERBOLICDOM_PANEL_VISIBLE_PROPERTY = "hyperbolicdomPanelVisible";
    public final static String THESAURUS_PANEL_VISIBLE_PROPERTY = "thesaurusPanelVisible";

    public final static String DOM_TAB = "dom";
    public final static String HYPERBOLICDOM_TAB = "hyperbolicdom";
    public final static String THESAURUS_TAB = "thesaurus";

    public DOMPanel domPanel;
    public HyperbolicDOMPanel hyperbolicDOMPanel;
    public ThesaurusPanel thesaurusPanel;

    /**
     * Constructs an UpperPanel
     * 
     * @param ontoBuilder the {@link OntoBuilder} application
     */
    public UpperPanel(OntoBuilder ontoBuilder)
    {
        setBorder(BorderFactory.createTitledBorder(ApplicationUtilities
            .getResourceString("panel.upper")));

        Tab tab0 = new Tab(DOM_TAB, ApplicationUtilities.getResourceString("panel.DOM"),
            new JScrollPane(domPanel = new DOMPanel(ontoBuilder)));
        tab0.setIcon(ApplicationUtilities.getImage("dom.gif"));
        addTab(tab0);

        Tab tab1 = new Tab(HYPERBOLICDOM_TAB,
            ApplicationUtilities.getResourceString("panel.hyperbolicDOM"), new JScrollPane(
                hyperbolicDOMPanel = new HyperbolicDOMPanel(ontoBuilder)));
        tab1.setIcon(ApplicationUtilities.getImage("hyperbolicdom.gif"));
        addTab(tab1);

        Tab tab2 = new Tab(THESAURUS_TAB,
            ApplicationUtilities.getResourceString("panel.thesaurus"), new JScrollPane(
                thesaurusPanel = new ThesaurusPanel(ontoBuilder)));
        tab2.setIcon(ApplicationUtilities.getImage("thesaurus.gif"));
        addTab(tab2);

        setTabVisible(DOM_TAB, Boolean.valueOf(ontoBuilder.getOption(DOM_PANEL_VISIBLE_PROPERTY))
            .booleanValue());
        setTabVisible(HYPERBOLICDOM_TAB,
            Boolean.valueOf(ontoBuilder.getOption(HYPERBOLICDOM_PANEL_VISIBLE_PROPERTY))
                .booleanValue());
        setTabVisible(THESAURUS_TAB,
            Boolean.valueOf(ontoBuilder.getOption(THESAURUS_PANEL_VISIBLE_PROPERTY)).booleanValue());
    }

    /**
     * Select a panel
     * 
     * @param panel the panel name
     */
    public void selectPanel(String panel)
    {
        setTabVisible(panel, true);
        if (panel.equals(DOM_TAB))
            setSelectedComponent(domPanel);
        else if (panel.equals(HYPERBOLICDOM_TAB))
            setSelectedComponent(hyperbolicDOMPanel);
        else if (panel.equals(THESAURUS_TAB))
            setSelectedComponent(thesaurusPanel);
    }
}