package ac.technion.iem.ontobuilder.gui.utils.files.html;

import javax.swing.Action;
import javax.swing.JPopupMenu;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.DefaultEditorKit;

import org.comedia.text.CXmlHighlighter;
import org.comedia.ui.CSyntaxEditor;

import ac.technion.iem.ontobuilder.gui.application.action.Actions;
import ac.technion.iem.ontobuilder.gui.elements.PopupTrigger;

/**
 * <p>Title: HTMLSyntaxHighlighterEditor</p>
 * Extends {@link CSyntaxEditor}
 */
public class HTMLSyntaxHighlighterEditor extends CSyntaxEditor
{
    private static final long serialVersionUID = 1L;

    protected Actions actions;

    public HTMLSyntaxHighlighterEditor()
    {
        super();
        setSyntaxHighlighter(new CXmlHighlighter());
        init();
    }

    protected void init()
    {
        loadActions();
        createPopupMenu();
        addCaretListener(new CaretListener()
        {
            public void caretUpdate(CaretEvent e)
            {
                if (e.getMark() != e.getDot()) // There is a selection
                {
                    actions.getAction(DefaultEditorKit.copyAction).setEnabled(true);
                    actions.getAction(DefaultEditorKit.cutAction).setEnabled(true);
                }
                else
                {
                    actions.getAction(DefaultEditorKit.copyAction).setEnabled(false);
                    actions.getAction(DefaultEditorKit.cutAction).setEnabled(false);
                }
            }
        });
    }

    protected void loadActions()
    {
        actions = new Actions();
        Action[] textActionsArray = getActions();
        for (int i = 0; i < textActionsArray.length; i++)
        {
            Action a = textActionsArray[i];
            if (a.getValue(Action.DEFAULT) != null)
                actions.addAction((String) a.getValue(Action.DEFAULT), a);
            else
                actions.addAction((String) a.getValue(Action.NAME), a);
        }
    }

    public void setText(String text)
    {
        super.setText(text);
        performHighlight();
    }

    protected void createPopupMenu()
    {
        // Sets the Popup Menu
        JPopupMenu popMenu = new JPopupMenu("Edit");
        popMenu.add(actions.getAction(DefaultEditorKit.cutAction));
        popMenu.add(actions.getAction(DefaultEditorKit.copyAction));
        popMenu.add(actions.getAction(DefaultEditorKit.pasteAction));
        addMouseListener(new PopupTrigger(popMenu));
    }
}