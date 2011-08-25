package ac.technion.iem.ontobuilder.gui.elements;

import java.awt.Component;

import javax.swing.ImageIcon;

/**
 * <p>Title: Tab</p>
 * <p>Description: Implements a Tab in the GUI</p>
 */
public class Tab
{
    protected String name;
    protected ImageIcon icon;
    protected String title;
    protected int index;
    protected Component component;
    protected String toolTip;

    /**
     * Constructs a default Tab
     */
    public Tab()
    {
    }

    /**
     * Constructs a Tab
     * 
     * @param name the name of the tab
     * @param title the title of the tab
     * @param component the component the tab will display
     */
    public Tab(String name, String title, Component component)
    {
        this.name = name;
        this.index = 0;
        this.title = title;
        this.component = component;
    }

    /**
     * Get the icon of the tab
     * 
     * @return the icon
     */
    public ImageIcon getIcon()
    {
        return icon;
    }

    /**
     * Set the icon of the tab
     * 
     * @param icon the icon to set
     */
    public void setIcon(ImageIcon icon)
    {
        this.icon = icon;
    }

    /**
     * Get the tab's index
     * 
     * @return the index
     */
    public int getIndex()
    {
        return index;
    }

    /**
     * Set the tab's index
     * 
     * @param index the index to set
     */
    public void setIndex(int index)
    {
        this.index = index;
    }

    /**
     * Get the title of the tab
     * 
     * @return the title
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * Set the title
     * 
     * @param title the title to set
     */
    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * Get the component the tab displays
     * 
     * @return the component
     */
    public Component getComponent()
    {
        return component;
    }

    /**
     * Set the component the tab displays
     * 
     * @param component the component to set
     */
    public void setComponent(Component component)
    {
        this.component = component;
    }

    /**
     * Get the name of the tab
     * 
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Set the name of the tab
     * 
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Get the tool-tip string of the tab
     * 
     * @return the tool-tip
     */
    public String getToolTip()
    {
        return toolTip;
    }

    /**
     * Set the tool-tip string of the tab
     * 
     * @param toolTip the tool-tip sting
     */
    public void setToolTip(String toolTip)
    {
        this.toolTip = toolTip;
    }
}