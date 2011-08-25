package ac.technion.iem.ontobuilder.gui.ontology;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.DefaultCellEditor;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import ac.technion.iem.ontobuilder.core.ontology.OntologyObject;
import ac.technion.iem.ontobuilder.gui.elements.TextField;

/**
 * <p>Title: OntologyTreeCellEditor</p>
 * Extends {@link DefaultCellEditor}
 */
public class OntologyTreeCellEditor extends DefaultCellEditor
{
    private static final long serialVersionUID = 1L;

    protected JTree tree;
    protected OntologyObject objectBeingEdited;

    public OntologyTreeCellEditor(JTree tree)
    {
        super(new TextField());
        this.tree = tree;
    }

    public JComponent getEditorComponent()
    {
        return editorComponent;
    }

    public OntologyObject getObjectBeingEdited()
    {
        return objectBeingEdited;
    }

    public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected,
        boolean expanded, boolean leaf, int row)
    {
        Object object = ((DefaultMutableTreeNode) value).getUserObject();
        if (object instanceof OntologyObject)
        {
            objectBeingEdited = (OntologyObject) object;
            delegate.setValue(objectBeingEdited.getName());
        }
        else
        {
            objectBeingEdited = null;
            delegate.setValue(tree
                .convertValueToText(value, isSelected, expanded, leaf, row, false));
        }
        return editorComponent;
    }

    public String getChangedValue()
    {
        return (String) delegate.getCellEditorValue();
    }

    public Object getCellEditorValue()
    {
        return objectBeingEdited;
    }

    public boolean isCellEditable(EventObject e)
    {
        if (e == null)
            return true;
        if (super.isCellEditable(e))
        {
            if (e instanceof MouseEvent)
            {
                MouseEvent me = (MouseEvent) e;
                TreePath path = tree.getPathForLocation(me.getX(), me.getY());
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                return (node.getUserObject() instanceof OntologyObject);
            }
        }
        return false;
    }
}