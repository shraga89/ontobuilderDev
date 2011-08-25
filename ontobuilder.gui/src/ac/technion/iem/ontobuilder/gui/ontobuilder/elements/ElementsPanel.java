package ac.technion.iem.ontobuilder.gui.ontobuilder.elements;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JTree;

import ac.technion.iem.ontobuilder.gui.ontobuilder.main.OntoBuilder;

/**
 * <p>Title: ElementsPanel</p>
 * Extends a {@link JPanel}
 */
public class ElementsPanel extends JPanel
{
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a ElementsPanel
     *
     * @param ontoBuilder the {@link OntoBuilder} application
     */
    public ElementsPanel(OntoBuilder ontoBuilder)
    {
        setLayout(new BorderLayout());
    }

    public void showElementsHierarchy(JTree tree)
    {
        removeAll();
        if (tree == null)
            return;
        add(BorderLayout.CENTER, tree);
    }
}
