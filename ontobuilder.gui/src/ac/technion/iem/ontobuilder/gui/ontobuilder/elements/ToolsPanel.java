package ac.technion.iem.ontobuilder.gui.ontobuilder.elements;

import java.awt.Component;

import javax.swing.JScrollPane;

import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.elements.Tab;
import ac.technion.iem.ontobuilder.gui.elements.TabbedPane;
import ac.technion.iem.ontobuilder.gui.ontobuilder.main.OntoBuilder;
import ac.technion.iem.ontobuilder.gui.tools.exactmapping.ToolMetadata;
import ac.technion.iem.ontobuilder.gui.tools.exactmapping.ToolsException;
import ac.technion.iem.ontobuilder.gui.tools.exactmapping.ToolsUtilities;

/**
 * <p>Title: ToolsPanel</p>
 * Extends a {@link TabbedPane}
 */
public class ToolsPanel extends TabbedPane
{
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a ToolsPanel
     * 
     * @param ontoBuilder the {@link OntoBuilder} application
     */
    public ToolsPanel(OntoBuilder ontoBuilder)
    {
        super();
    }

    /**
     * Initialize the tools tab
     * 
     * @throws ToolsException
     */
    public void initToolTabs() throws ToolsException
    {
        try
        {
            ToolMetadata[] tools = ToolsUtilities.getAllToolMetadata();
            for (int i = 0; i < tools.length; i++)
            {
                Component toolPanel = (Component) Class.forName(tools[i].getClasspath())
                    .newInstance();
                JScrollPane sp = new JScrollPane();
                sp.setViewportView(toolPanel);
                Tab tool = new Tab(tools[i].getName(), tools[i].getShortDescription(), sp);
                tool.setIcon(ApplicationUtilities.getImage(tools[i].getIcon()));
                addToolPane(tool);
            }
        }
        catch (Exception e)
        {
            throw new ToolsException(e.getMessage());
        }
    }

    /**
     * Add a tool pane
     * 
     * @param toolPane the tool pane {@link Tab} to add
     */
    public void addToolPane(final Tab toolPane)
    {
        addTab(toolPane);
        setTabVisible(toolPane.getName(), false);
    }

}
