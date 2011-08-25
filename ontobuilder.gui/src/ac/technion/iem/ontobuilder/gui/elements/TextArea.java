package ac.technion.iem.ontobuilder.gui.elements;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.*;

import ac.technion.iem.ontobuilder.gui.application.action.Actions;

/**
 * <p>Title: TextArea</p>
 * Extends {@link JTextArea}
 */
public class TextArea extends JTextArea
{
    private static final long serialVersionUID = 1L;

    protected Actions actions;

    /**
     * Constructs a default TextArea
     */
    public TextArea()
    {
        super();
        init();
    }

    /**
     * Constructs a TextArea
     *
     * @param text the text to display
     */
    public TextArea(String text)
    {
        super(text);
        init();
    }

    /**
     * Constructs a TextArea
     *
     * @param text the text to display
     * @param rows the number of rows in the text area
     * @param cols  the columns of rows in the text area
     */
    public TextArea(String text, int rows, int cols)
    {
        super(text, rows, cols);
        init();
    }

    /**
     * Constructs a TextArea
     *
     * @param rows the number of rows in the text area
     * @param cols  the columns of rows in the text area
     */
    public TextArea(int rows, int cols)
    {
        super(rows, cols);
        init();
    }

    /**
     * Initializes the text area
     */
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

        Keymap keymap = getKeymap();
        keymap.addActionForKeyStroke(KeyStroke.getKeyStroke("shift DELETE"),
            actions.getAction(DefaultEditorKit.cutAction));
        keymap.addActionForKeyStroke(KeyStroke.getKeyStroke("control INSERT"),
            actions.getAction(DefaultEditorKit.copyAction));
        keymap.addActionForKeyStroke(KeyStroke.getKeyStroke("shift INSERT"),
            actions.getAction(DefaultEditorKit.pasteAction));
    }

    /**
     * Load the actions that are available for the text area
     */
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

    /**
     * Creates a popup menu in the text area
     */
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