package ac.technion.iem.ontobuilder.gui.elements;

import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;

/**
 * <p>Title: ComboBox</p>
 * Extends {@link JComboBox}
 */
public class ComboBox extends JComboBox
{
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a default ComboBox
     */
    public ComboBox()
    {
        super();
        setEditor(new TextComboBoxEditor());
    }

    /**
     * Constructs a ComboBox with a list of items
     * 
     * @param items the list of items
     */
    public ComboBox(Vector<?> items)
    {
        super(items);
        setEditor(new TextComboBoxEditor());
    }

    /**
     * Set the text of the combobox
     * 
     * @param text the text to set
     */
    public void setText(String text)
    {
        ((TextComboBoxEditor) editor).setText(text);
    }

    /**
     * Get the text of the combobox
     * 
     * @return the text
     */
    public String getText()
    {
        return ((TextComboBoxEditor) editor).getText();
    }

    /**
     * Set the icon of the combobox
     * 
     * @param icon the {@link ImageIcon} to set
     */
    public void setIcon(ImageIcon icon)
    {
        ((TextComboBoxEditor) editor).setIcon(icon);
    }

    /**
     * Enables to combobox
     * 
     * @param b <code>true</code> to enable, <code>false</code> to disable
     */
    public void setEnabled(boolean b)
    {
        super.setEnabled(b);
        ((TextComboBoxEditor) editor).setEnabled(b);
    }
}