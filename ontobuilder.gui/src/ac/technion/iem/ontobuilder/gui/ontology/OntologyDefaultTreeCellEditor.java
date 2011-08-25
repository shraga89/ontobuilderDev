package ac.technion.iem.ontobuilder.gui.ontology;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellEditor;

/**
 * <p>Title: OntologyDefaultTreeCellEditor</p>
 * Extends {@link DefaultTreeCellEditor}
 */
public class OntologyDefaultTreeCellEditor extends DefaultTreeCellEditor
{
    /**
     * 
     * Constructs a OntologyDefaultTreeCellEditor
     *
     * @param tree the JTree
     * @param renderer the DefaultTreeCellRenderer
     * @param editor the TreeCellEditor
     */
    public OntologyDefaultTreeCellEditor(JTree tree, DefaultTreeCellRenderer renderer,
        TreeCellEditor editor)
    {
        super(tree, renderer, editor);
    }

    /**
     * Determines the offset of the OntologyDefaultTreeCellEditor
     */
    protected void determineOffset(JTree tree, Object value, boolean isSelected, boolean expanded,
        boolean leaf, int row)
    {
        super.determineOffset(tree, value, isSelected, expanded, leaf, row);
        JLabel label = (JLabel) renderer.getTreeCellRendererComponent(tree, value, isSelected,
            expanded, leaf, row, true);
        editingIcon = label.getIcon();
    }
}