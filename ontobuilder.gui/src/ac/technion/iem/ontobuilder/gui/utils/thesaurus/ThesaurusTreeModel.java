package ac.technion.iem.ontobuilder.gui.utils.thesaurus;

import java.util.Vector;
import java.util.Enumeration;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;

import ac.technion.iem.ontobuilder.core.thesaurus.Thesaurus;
import ac.technion.iem.ontobuilder.core.thesaurus.ThesaurusWord;
import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;

/**
 * <p>Title: ThesaurusTreeModel</p>
 * Extends {@link DefaultTreeModel} 
 */
public class ThesaurusTreeModel extends DefaultTreeModel
{
    private static final long serialVersionUID = -6555949336134891167L;

    protected Thesaurus model;

    /**
     * Constructs a ThesaurusTreeModel
     * 
     * @param model a ThesaurusModel
     */
    public ThesaurusTreeModel(Thesaurus model)
    {
        super(new DefaultMutableTreeNode(ApplicationUtilities.getResourceString("thesaurus")));
        this.model = model;
        updateTree();
    }

    /**
     * Updates the tree from the ThesaurusModel
     */
    public void updateTree()
    {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) getRoot();
        root.removeAllChildren();
        for (int i = 0; i < model.getWordCount(); i++)
        {
            ThesaurusWord word = model.getWord(i);
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(word);
            DefaultMutableTreeNode synonymsNode = new DefaultMutableTreeNode(
                ApplicationUtilities.getResourceString("thesaurus.synonyms"));
            DefaultMutableTreeNode homonymsNode = new DefaultMutableTreeNode(
                ApplicationUtilities.getResourceString("thesaurus.homonyms"));
            node.add(synonymsNode);
            node.add(homonymsNode);
            Vector<ThesaurusWord> synonyms = word.getSynonyms();
            for (int j = 0; j < synonyms.size(); j++)
                synonymsNode.add(new DefaultMutableTreeNode(synonyms.get(j)));
            Vector<ThesaurusWord> homonyms = word.getHomonyms();
            for (int j = 0; j < homonyms.size(); j++)
                homonymsNode.add(new DefaultMutableTreeNode(homonyms.get(j)));
            root.add(node);
        }
    }

    /**
     * Finds a child node that equals to a specific object
     * 
     * @param root the node to start from
     * @param object the object to look for
     * @return a {@link DefaultMutableTreeNode}
     */
    public DefaultMutableTreeNode findChildNodeWithUserObject(DefaultMutableTreeNode root,
        Object object)
    {
        for (Enumeration<?> children = root.children(); children.hasMoreElements();)
        {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) children.nextElement();
            if (child.getUserObject().equals(object))
                return child;
        }
        return null;
    }

    /**
     * Finds nodes that equals to a specific object
     * 
     * @param object the object to look for
     * @return a DefaultMutableTreeNode
     */
    public Vector<?> findNodesWithUserObject(Object object)
    {
        return findNodesWithUserObject((DefaultMutableTreeNode) root, object);
    }

    /**
     * Breadth first search for nodes with a given user object
     * 
     * @param root the node to start from
     * @param object the object to look for
     * @return a vertor of {@link DefaultMutableTreeNode}
     */
    public Vector<DefaultMutableTreeNode> findNodesWithUserObject(DefaultMutableTreeNode root,
        Object object)
    {
        Vector<DefaultMutableTreeNode> nodes = new Vector<DefaultMutableTreeNode>();
        for (Enumeration<?> allNodes = root.breadthFirstEnumeration(); allNodes.hasMoreElements();)
        {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) allNodes.nextElement();
            if (node.getUserObject().equals(object))
                nodes.add(node);
        }
        return nodes;
    }
}