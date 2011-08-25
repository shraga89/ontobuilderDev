package ac.technion.iem.ontobuilder.gui.elements;

import java.util.Hashtable;

import javax.swing.JTabbedPane;

/**
 * <p>Title: TabbedPane</p>
 * Extends {@link JTabbedPane}
 */
public class TabbedPane extends JTabbedPane
{
    private static final long serialVersionUID = 1L;

    protected Hashtable<String, Tab> tabs;

    /**
     * Constructs a default TabbedPane
     */
    public TabbedPane()
    {
        super();
        tabs = new Hashtable<String, Tab>();
    }

    /**
     * Constructs a TabbedPane
     * 
     * @param tabPlacement the number of tabs to set
     */
    public TabbedPane(int tabPlacement)
    {
        super(tabPlacement);
        tabs = new Hashtable<String, Tab>();
    }

    /**
     * Adds a tab to the pane
     * 
     * @param tab the {@link Tab} to set
     */
    public void addTab(Tab tab)
    {
        addTab(tab.getTitle(), tab.getIcon(), tab.getComponent());
        tab.setIndex(indexOfComponent(tab.getComponent()));
        tabs.put(tab.getName(), tab);
    }

    /**
     * Sets a tab to be visible
     * 
     * @param name the name of the tab
     * @param visible <code>true</code> if visible, else <code>false</code>
     */
    public void setTabVisible(String name, boolean visible)
    {
        Tab tab = (Tab) tabs.get(name);
        if (tab == null)
            throw new UnsupportedOperationException(
                "Tab visibility can only be changed when tabs are added with the 'addTab(Tab)' method.");
        if (!visible && indexOfComponent(tab.getComponent()) != -1)
            remove(tab.getComponent());
        else if (visible && indexOfComponent(tab.getComponent()) == -1)
        {
            if (tab.getIndex() > getTabCount())
                insertTab(tab.getTitle(), tab.getIcon(), tab.getComponent(), tab.getToolTip(),
                    getTabCount());
            else
                insertTab(tab.getTitle(), tab.getIcon(), tab.getComponent(), tab.getToolTip(),
                    tab.getIndex());
        }
    }
}