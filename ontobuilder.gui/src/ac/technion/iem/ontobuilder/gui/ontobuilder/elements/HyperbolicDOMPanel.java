package ac.technion.iem.ontobuilder.gui.ontobuilder.elements;

import java.awt.BorderLayout;
import javax.swing.JPanel;

import ac.technion.iem.ontobuilder.gui.ontobuilder.main.OntoBuilder;
import hypertree.HyperTree;

/**
 * <p>Title: HyperbolicDOMPanel</p>
 * Extends a {@link JPanel}
 */
public class HyperbolicDOMPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
    
    /**
     * Constructs a HyperbolicDOMPanel
     *
     * @param ontoBuilder the ontoBuilder application
     */
    public HyperbolicDOMPanel(OntoBuilder ontoBuilder)
	{
		setLayout(new BorderLayout());
	}
	
	public void showTree(HyperTree tree)
	{
		removeAll();
		if(tree==null) return;
		add(BorderLayout.CENTER,tree.getView());
	}
}
