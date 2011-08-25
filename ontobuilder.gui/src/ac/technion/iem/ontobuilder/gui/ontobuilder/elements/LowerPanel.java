package ac.technion.iem.ontobuilder.gui.ontobuilder.elements;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.ontobuilder.main.OntoBuilder;

/**
 * <p>Title: LowerPanel</p>
 * Extends a {@link JTabbedPane}
 */
public class LowerPanel extends JTabbedPane
{
    private static final long serialVersionUID = 1L;

    public PropertiesPanel propertiesPanel;

    /**
     * Constructs a LowerPanel
     * 
     * @param ontoBuilder the {@link OntoBuilder} application
     */
    public LowerPanel(OntoBuilder ontoBuilder)
    {
        setBorder(BorderFactory.createTitledBorder(ApplicationUtilities
            .getResourceString("panel.lower")));

        addTab(ApplicationUtilities.getResourceString("panel.properties"),
            ApplicationUtilities.getImage("properties.gif"), new JScrollPane(
                propertiesPanel = new PropertiesPanel()));
    }
}
