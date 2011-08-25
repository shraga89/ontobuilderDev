package ac.technion.iem.ontobuilder.gui.utils.dom;

import hypertree.HyperTree;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;

import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ac.technion.iem.ontobuilder.core.utils.dom.DOMUtilities;
import ac.technion.iem.ontobuilder.gui.utils.hypertree.NodeHyperTree;
import ac.technion.iem.ontobuilder.io.utils.dom.DOMNode;


/**
 * <p>
 * Title: DOMUtilitiesGui
 * </p>
  * <p>Description: Implements the methods of the DOMUtilities used by the GUI</p>
 */
public class DOMUtilitiesGui
{    
    /**
     * Converts a URL to a JTree
     * 
     * @param url the {@link URL} to convert
     * @return a {@link JTree}
     * @throws IOException when cannot convert DOM to {@link JTree}
     */
    public static JTree convertURLtoJTree(URL url) throws IOException
    {
        if (url == null)
            throw new NullPointerException();
        return convertDOMtoJTree(DOMUtilities.getDOM(url));
    }
    
    /**
     * Converts a URL to a JTree
     * 
     * @param url the {@link URL} to convert
     * @param errorOutput output stream for errors
     * @return a {@link JTree}
     * @throws IOException when cannot convert DOM to {@link JTree}
     */
    public static JTree convertURLtoJTree(URL url, PrintWriter errorOutput) throws IOException
    {
        if (url == null)
            throw new NullPointerException();
        return convertDOMtoJTree(DOMUtilities.getDOM(url, errorOutput));
    }
    
    /**
     * Converts a DOM to a JTRee
     * 
     * @param rootNode the node to start from
     * @return a {@link JTree}
     */
    public static JTree convertDOMtoJTree(Node rootNode)
    {
        if (rootNode == null)
            throw new NullPointerException();
        DefaultMutableTreeNode document = new DefaultMutableTreeNode(new DOMNode(rootNode));
        convertDOMtoJTreeRecursive(rootNode, document);
        JTree tree = new JTree(document);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        ToolTipManager.sharedInstance().registerComponent(tree);
        tree.setCellRenderer(new DOMTreeRenderer());
        return (tree);
    }
    
    /**
     * Converts a DOM to a JTRee recursively
     */
    private static void convertDOMtoJTreeRecursive(Node node, DefaultMutableTreeNode tree)
    {
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++)
        {
            DefaultMutableTreeNode subtree = new DefaultMutableTreeNode(new DOMNode(
                childNodes.item(i)));
            tree.add(subtree);
            convertDOMtoJTreeRecursive(childNodes.item(i), subtree);
        }
    }
    
    /**
     * Converts a DOM to a hyper tree
     * 
     * @param rootNode the root node to start from
     * @return a {@link HyperTree}
     */
    public static HyperTree convertDOMtoHyperTree(Node rootNode)
    {
        if (rootNode == null)
            throw new NullPointerException();
        NodeHyperTree root = new NodeHyperTree(new DOMNode(rootNode), NodeHyperTree.CLASS);
        convertDOMtoHyperTreeRecursive(rootNode, root);
        HyperTree tree = new HyperTree(root);
        return (tree);
    }
    
    /**
     * Converts a DOM to a hyper tree recursively
     */
    private static void convertDOMtoHyperTreeRecursive(Node node, NodeHyperTree tree)
    {
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++)
        {
            NodeHyperTree subtree = new NodeHyperTree(new DOMNode(childNodes.item(i)),
                NodeHyperTree.CLASS);
            tree.add(subtree);
            convertDOMtoHyperTreeRecursive(childNodes.item(i), subtree);
        }
    }
}
