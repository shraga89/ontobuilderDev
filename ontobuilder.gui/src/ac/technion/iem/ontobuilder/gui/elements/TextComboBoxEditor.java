package ac.technion.iem.ontobuilder.gui.elements;

import java.awt.Component;
import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;
import javax.swing.*;

/**
 * <p>Title: TextComboBoxEditor</p>
 * Implements {@link ComboBoxEditor} and {@link FocusListener}
 */
class TextComboBoxEditor implements ComboBoxEditor, FocusListener
{
    protected TextField editor;
    protected JLabel label;
    private JPanel component;
    private ImageIcon icon;

    /** 
     * Constructs a default TextComboBoxEditor
     */
    public TextComboBoxEditor()
    {
        editor = new TextField("");
        editor.setBorder(null);
        label = new JLabel();
        label.setBackground(editor.getBackground());
        label.setOpaque(true);
        label.getMaximumSize().height = editor.getPreferredSize().height -
            editor.getInsets().bottom - editor.getInsets().top;
        component = new JPanel(new BorderLayout());
        component.add(label, BorderLayout.WEST);
        component.add(editor, BorderLayout.CENTER);
        component.setBorder(BorderFactory.createLineBorder(Color.black));
    }

    /**
     * Enables the TextComboBoxEditor
     *
     * @param b true if enabled, else false
     */
    public void setEnabled(boolean b)
    {
        editor.setEnabled(b);
    }

    /**
     * Get the editor component
     * 
     * @return Component the component
     */
    public Component getEditorComponent()
    {
        return component;
    }

    /**
     * Set the icon for the TextComboBoxEditor
     *
     * @param icon the icon to set
     */
    public void setIcon(ImageIcon icon)
    {
        this.icon = icon;
        label.setIcon(icon);
    }

    /**
     * Set the text for the TextComboBoxEditor
     *
     * @param text the icon to set
     */
    public void setText(String text)
    {
        editor.setText(text);
    }

    /**
     * Get the text of the TextComboBoxEditor
     *
     * @return the text
     */
    public String getText()
    {
        return editor.getText();
    }

    /**
     * Set an item in the TextComboBoxEditor
     * 
     * @param anObject the item to set
     */
    public void setItem(Object anObject)
    {
        if (anObject != null && anObject.toString().length() > 0)
        {
            editor.setText(anObject.toString());
            label.setIcon(icon);
        }
        else
        {
            editor.setText("");
            label.setIcon(null);
        }
    }

    /**
     * Get the item in the TextComboBoxEditor
     * 
     * @return the item
     */
    public Object getItem()
    {
        return editor.getText();
    }

    /**
     * Select all of the items in the TextComboBoxEditor
     */
    public void selectAll()
    {
        editor.selectAll();
        editor.requestFocus();
    }

    public void focusGained(FocusEvent e)
    {
    }

    public void focusLost(FocusEvent e)
    {
    }

    public void addActionListener(ActionListener l)
    {
        editor.addActionListener(l);
    }

    public void removeActionListener(ActionListener l)
    {
        editor.removeActionListener(l);
    }

    // public static class UIResource extends TextComboBoxEditor implements UIResource
    // {
    // }
}