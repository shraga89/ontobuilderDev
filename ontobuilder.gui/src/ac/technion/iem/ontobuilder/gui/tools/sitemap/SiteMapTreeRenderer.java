package ac.technion.iem.ontobuilder.gui.tools.sitemap;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;

/**
 * <p>Title: SiteMapTreeRenderer</p>
 * Extends{@link DefaultTreeCellRenderer} 
 */
public class SiteMapTreeRenderer extends DefaultTreeCellRenderer
{
    private static final long serialVersionUID = 1L;

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel,
        boolean expanded, boolean leaf, int row, boolean hasFocus)
    {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        SiteMapNode node = (SiteMapNode) (((DefaultMutableTreeNode) value).getUserObject());
        if (node.isRoot())
            setIcon(ApplicationUtilities.getImage("htmlfile.gif"));
        else
            setIcon(ApplicationUtilities.getImage("link.gif"));
        setToolTipText(node.toString());
        return this;
    }
}
