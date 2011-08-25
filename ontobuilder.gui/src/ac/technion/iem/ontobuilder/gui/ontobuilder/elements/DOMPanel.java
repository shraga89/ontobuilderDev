package ac.technion.iem.ontobuilder.gui.ontobuilder.elements;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JTree;

import ac.technion.iem.ontobuilder.gui.ontobuilder.main.OntoBuilder;

/**
 * <p>Title: DOMPanel</p>
 * Extends a {@link JPanel}
 */
public class DOMPanel extends JPanel
{
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a DOMPanel
     *
     * @param ontoBuilder the ontoBuilder application
     */
    public DOMPanel(OntoBuilder ontoBuilder)
    {
        setLayout(new BorderLayout());
    }

    public void showTree(JTree tree)
    {
        removeAll();
        if (tree == null)
            return;
        add(BorderLayout.CENTER, tree);
    }
}