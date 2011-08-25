package ac.technion.iem.ontobuilder.gui.utils.files.html;

import javax.swing.JTable;
import javax.swing.tree.DefaultMutableTreeNode;

import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.HTMLElement;
import ac.technion.iem.ontobuilder.gui.application.ObjectWithProperties;

/**
 * <p>Title: HTMLElement</p>
 * Implements {@link INPUTElementGui}
 */
public abstract class HTMLElementGui implements ObjectWithProperties
{
	protected HTMLElement htmlElement;

    public HTMLElementGui(HTMLElement htmlElement)
    {
        this.htmlElement = htmlElement;
    }

    public void setDescription(String description)
    {
        this.htmlElement.setDescription(description);
    }

    public String getDescription()
    {
        return htmlElement.getDescription();
    }

    public String getType()
    {
        return htmlElement.getType();
    }

    public abstract JTable getProperties();

    public DefaultMutableTreeNode getTreeBranch()
    {
        return new DefaultMutableTreeNode(this);
    }

    public String toString()
    {
        return htmlElement.getType();
    }
}