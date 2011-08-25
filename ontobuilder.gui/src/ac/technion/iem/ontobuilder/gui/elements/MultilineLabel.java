package ac.technion.iem.ontobuilder.gui.elements;

import javax.swing.UIManager;

/**
 * <p>
 * Title: MultilineLabel
 * </p>
 * Extends {@link TextArea}
 */
public class MultilineLabel extends TextArea
{
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a MultilineLabel
     * 
     * @param text the text to set
     */
    public MultilineLabel(String text)
    {
        super(text);
        setEditable(false);
        setLineWrap(true);
        setWrapStyleWord(true);
        setBackground(UIManager.getColor("Label.background"));
    }
}
