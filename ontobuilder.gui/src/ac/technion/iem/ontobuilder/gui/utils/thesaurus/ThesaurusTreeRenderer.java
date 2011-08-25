package ac.technion.iem.ontobuilder.gui.utils.thesaurus;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import ac.technion.iem.ontobuilder.core.thesaurus.ThesaurusWord;
import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;

/**
 * <p>Title: ThesaurusTreeRenderer</p>
 * Extends {@link DefaultTreeCellRenderer} 
 */
public class ThesaurusTreeRenderer extends DefaultTreeCellRenderer
{
    private static final long serialVersionUID = -4749576262638236349L;

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel,
        boolean expanded, boolean leaf, int row, boolean hasFocus)
    {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        Object object = node.getUserObject();
        if (node.isRoot())
            setIcon(ApplicationUtilities.getImage("thesaurus.gif"));
        else if (object instanceof String)
        {
            String nodeString = (String) object;
            if (nodeString.equals(ApplicationUtilities.getResourceString("thesaurus.synonyms")))
            {
                if (expanded)
                    setIcon(ApplicationUtilities.getImage("synonymsopen.gif"));
                else
                    setIcon(ApplicationUtilities.getImage("synonymsclosed.gif"));
            }
            else
            {
                if (expanded)
                    setIcon(ApplicationUtilities.getImage("homonymsopen.gif"));
                else
                    setIcon(ApplicationUtilities.getImage("homonymsclosed.gif"));
            }
        }
        else if (object instanceof ThesaurusWord)
        {
            Object parent = ((DefaultMutableTreeNode) node.getParent()).getUserObject();
            if (parent instanceof String)
            {
                ImageIcon wordIcon = ApplicationUtilities.getImage("word.gif");
                String parentStr = (String) parent;
                if (parentStr.equals(ApplicationUtilities.getResourceString("thesaurus.synonyms")))
                    setIcon(ApplicationUtilities.getImage("synonym.gif"));
                else if (parentStr.equals(ApplicationUtilities
                    .getResourceString("thesaurus.homonyms")))
                    setIcon(ApplicationUtilities.getImage("homonym.gif"));
                else
                    setIcon(wordIcon);
                setLeafIcon(wordIcon);
                setOpenIcon(wordIcon);
                setClosedIcon(wordIcon);
            }
        }
        return this;
    }
}