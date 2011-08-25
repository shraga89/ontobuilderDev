package ac.technion.iem.ontobuilder.gui.utils.thesaurus;

import java.util.EventObject;
import java.awt.Component;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.DefaultCellEditor;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import ac.technion.iem.ontobuilder.core.thesaurus.ThesaurusWord;
import ac.technion.iem.ontobuilder.gui.elements.TextField;

/**
 * <p>Title: ThesaurusTreeCellEditor</p>
 * Extends {@link DefaultCellEditor} 
 */
public class ThesaurusTreeCellEditor extends DefaultCellEditor
{
    private static final long serialVersionUID = 4134860475171668644L;
    
    protected JTree tree;
	protected ThesaurusWord wordBeingEdited;

	/**
	 * 
	 * Constructs a ThesaurusTreeCellEditor
	 *
	 * @param tree a {@link JTree}
	 */
	public ThesaurusTreeCellEditor(JTree tree)
	{
		super(new TextField());
		this.tree = tree;
	}

	/**
	 * Returns the Editor Componen
	 *
	 * @return a {@link JComponent}
	 */
	public JComponent getEditorComponent()
	{
		return editorComponent;
	}

	/**
	 * Get the word that's being edited
	 *
	 * @return the {@link ThesaurusWord} word
	 */
	public ThesaurusWord getWordBeingEdited()
	{
		return wordBeingEdited;
	}

	/**
	 * Get a TreeCellEditorComponent
	 * 
	 * @return a {@link Component}
	 */
	public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row)
	{
		Object object=((DefaultMutableTreeNode)value).getUserObject();
		if(object instanceof ThesaurusWord)
		{
			wordBeingEdited=(ThesaurusWord)object;
			delegate.setValue(wordBeingEdited.getWord());
		}
		else
		{
			wordBeingEdited=null;
			delegate.setValue(tree.convertValueToText(value,isSelected,expanded,leaf,row,false));
		}
		return editorComponent;
	}

	public String getChangedValue()
	{
		return (String)delegate.getCellEditorValue();
	}

	public Object getCellEditorValue()
	{
		return wordBeingEdited;
	}

	/**
	 * Check if the cell is editable
	 * 
	 * @return <code>true</code> if it is editable, else <code>false</code>
	 */
	public boolean isCellEditable(EventObject e)
	{
		if(e == null) return true;
		if(super.isCellEditable(e))
		{
			if(e instanceof MouseEvent)
			{
				MouseEvent me=(MouseEvent)e;
				TreePath path=tree.getPathForLocation(me.getX(),me.getY());
				DefaultMutableTreeNode node=(DefaultMutableTreeNode)path.getLastPathComponent();
				return (node.getUserObject() instanceof ThesaurusWord);
			}
		}
		return false;
	}
}