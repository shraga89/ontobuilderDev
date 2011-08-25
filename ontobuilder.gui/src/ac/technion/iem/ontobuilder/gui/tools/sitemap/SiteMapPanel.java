package ac.technion.iem.ontobuilder.gui.tools.sitemap;

import java.awt.GridLayout;
import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.JPanel;

import ac.technion.iem.ontobuilder.gui.ontobuilder.main.OntoBuilder;
import hypertree.HyperTree;

/**
 * <p>Title: SiteMapPanel</p>
 * Extends a {@link JPanel}
 */
public class SiteMapPanel extends JPanel
{
    private static final long serialVersionUID = 1L;

    private JPanel lowerPanel;
    private JPanel upperPanel;
    private JPanel panel;

    /**
     * 
     * Constructs a SiteMapPanel
     *
     * @param ontoBuilder the {@link OntoBuilder} application
     */
    public SiteMapPanel(OntoBuilder ontoBuilder)
    {
        setLayout(new BorderLayout());
        upperPanel = new JPanel(new BorderLayout());
        lowerPanel = new JPanel(new BorderLayout());
        panel = new JPanel(new GridLayout(2, 1, 5, 5));
        panel.add(new JScrollPane(upperPanel));
        panel.add(new JScrollPane(lowerPanel));
        add(panel, BorderLayout.CENTER);
    }

    public void showTree(HyperTree tree)
    {
        upperPanel.removeAll();
        if (tree == null)
            return;
        upperPanel.add(BorderLayout.CENTER, tree.getView());
    }

    public void showTree(JTree tree)
    {
        lowerPanel.removeAll();
        if (tree == null)
            return;
        lowerPanel.add(BorderLayout.CENTER, tree);
    }
}
