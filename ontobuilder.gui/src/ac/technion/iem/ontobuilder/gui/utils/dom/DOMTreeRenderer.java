package ac.technion.iem.ontobuilder.gui.utils.dom;

import java.awt.Component;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;

import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.io.utils.dom.DOMNode;

/**
 * <p>Title: DOMTreeRenderer</p>
 * <p>Description: Renders a DOM tree</p>
 * <br>Extends {@link DefaultTreeCellRenderer}
 */
public class DOMTreeRenderer extends DefaultTreeCellRenderer
{
    private static final long serialVersionUID = 1L;

    /**
     * Returns a tree cell component
     */
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel,
        boolean expanded, boolean leaf, int row, boolean hasFocus)
    {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        DOMNode node = (DOMNode) (((DefaultMutableTreeNode) value).getUserObject());
        switch (node.getNodeType())
        {
        case org.w3c.dom.Node.DOCUMENT_NODE:
            setIcon(ApplicationUtilities.getImage("document.gif"));
            break;
        case org.w3c.dom.Node.ELEMENT_NODE:
            setIcon(ApplicationUtilities.getImage("element.gif"));
            break;
        case org.w3c.dom.Node.TEXT_NODE:
            setIcon(ApplicationUtilities.getImage("text.gif"));
            break;
        case org.w3c.dom.Node.COMMENT_NODE:
            setIcon(ApplicationUtilities.getImage("comment.gif"));
        }
        setToolTipText(node.toString());
        return this;
    }
}
