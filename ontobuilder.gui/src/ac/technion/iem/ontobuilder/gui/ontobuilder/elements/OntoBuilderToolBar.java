package ac.technion.iem.ontobuilder.gui.ontobuilder.elements;

import javax.swing.Box;

import ac.technion.iem.ontobuilder.gui.elements.ToolBar;
import ac.technion.iem.ontobuilder.gui.ontobuilder.main.OntoBuilder;
import ac.technion.iem.ontobuilder.gui.utils.browser.Browser;

/**
 * <p>Title: OntoBuilderToolBar</p>
 * Extends a {@link ToolBar}
 */
public class OntoBuilderToolBar extends ToolBar
{
    private static final long serialVersionUID = 7116782855466005619L;

    /**
     * Constructs a OntoBuilderToolBar
     * 
     * @param ontoBuilder the {@link OntoBuilder} application
     */
    public OntoBuilderToolBar(OntoBuilder ontoBuilder)
    {
        super(ontoBuilder);
    }

    /**
     * Initialize the OntoBuilderToolBar
     */
    protected void init()
    {
        // Open Ontology
        addButton(application.getAction("newontology"));

        // Open Ontology
        addButton(application.getAction("openontology"));

        // Save Ontology
        addButton(application.getAction("saveontology"));

        addSeparator();

        // // Print
        // addButton(application.getAction("print"));

        addSeparator();

        // Ontology
        addButton(application.getAction("ontowizard"));

        // Merge Ontology
        addButton(application.getAction("mergeontologies"));

        // Meta Top k Ontology
        addButton(application.getAction("metaTopK"));

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

        addSeparator();

        // Browser
        Browser browser = ((OntoBuilder) application).getBrowser();

        add(browser.getBackButton());
        add(browser.getForwardButton());
        addSeparator();
        add(browser.getAddressBar());
        add(browser.getGoButton());

        addSeparator();

        add(Box.createHorizontalGlue());

        // Context Sensitive Help
        cshelpButton = addButton(application.getAction("cshelp"));

        // Help
        helpButton = addButton(application.getAction("help"));
    }
}
