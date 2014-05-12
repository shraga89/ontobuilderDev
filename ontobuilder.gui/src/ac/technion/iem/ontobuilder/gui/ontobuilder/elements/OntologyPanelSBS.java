package ac.technion.iem.ontobuilder.gui.ontobuilder.elements;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.gui.ontobuilder.main.OntoBuilder;
import ac.technion.iem.ontobuilder.gui.ontology.OntologyGui;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;

/**
 * <p>Title: Ontology Panel Side By Side</p>
 * Extends a {@link JPanel} and provides side by side 
 * comparison capabilities. May be extended in the future 
 * to allow manual matching. 
 */
public class OntologyPanelSBS extends JPanel
{
    private static final long serialVersionUID = 1L;
    private OntologyPanel sourcePanel;
    private OntologyPanel targetPanel;
    private MIPanel miPanel;
    /**
     * Constructs a default OntologyPanel
     * 
     * @param ontoBuilder the {@link OntoBuilder} application
     */
    public OntologyPanelSBS(OntoBuilder ontoBuilder)
    {
        miPanel = new MIPanel();
		JPanel topPane = new JPanel();
		JSplitPane mainPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPane, miPanel);
		mainPane.setDividerLocation(0.7);
		topPane.setLayout(new GridLayout(1,2));
		setLayout(new BorderLayout());
		add(BorderLayout.CENTER, mainPane);
		sourcePanel = new OntologyPanel(ontoBuilder);
        targetPanel = new OntologyPanel(ontoBuilder);
        
        sourcePanel.add(new JLabel("Source"));
        targetPanel.add(new JLabel("Target"));
        topPane.add(sourcePanel);
        topPane.add(targetPanel);
      
        
      
    }

    /**
     * Add an ontology
     *
     * @param ontology the {@link Ontology} to add
     * @param toLeft if true, adds the ontology to source panel
     */
    public void addOntology(final OntologyGui ontology,final boolean toSource)
    {
    	boolean hasSource = sourcePanel.getOntologies().size()>0;
    	boolean hasTarget = targetPanel.getOntologies().size()>0;
    	//TODO remove all arcs
    	if (toSource){
   			sourcePanel.remove(0);
    		sourcePanel.addOntology(ontology);
    		if (hasTarget)
    			miPanel.setMi(ontology,targetPanel.getCurrentOntologyGui());
    	}
    	else 
    	{
    		targetPanel.remove(0);
    		targetPanel.addOntology(ontology);
    		if (hasSource)
    			miPanel.setMi(sourcePanel.getCurrentOntologyGui(),ontology);
    	}
    	
    }

    /**
     * Get the source ontology
     *
     * @return the {@link Ontology}
     */
    public Ontology getSourceOntology()
    {
        return sourcePanel.getCurrentOntology();
    }
    
    /**
     * Get the source ontology
     *
     * @return the {@link Ontology}
     */
    public Ontology getTargetOntology()
    {
        return targetPanel.getCurrentOntology();
    }

    /**
     * Close the source ontology
     *
     * @param ontologyGui the {@link Ontology} to close
     */
    public void closeSourceOntology()
    {
        sourcePanel.closeCurrentOntology();
    }
    
    /**
     * Close the current ontology
     *
     * @param ontologyGui the {@link Ontology} to close
     */
    public void closeTargetOntology()
    {
        targetPanel.closeCurrentOntology();
    }

	/**
	 * @return the matchInformation
	 */
	public MatchInformation getMi() {
		return miPanel.getMi();
	}
	
	public boolean lines= false; 
    public int[][] line_coordinates={{0,0,0,0}}; //{x_start,y_start,x_end,y_end}
    public String[] line_writing={""};
    //don't forget to use : frame.repaint(); after using draw_line
	public void draw_line(Term t1, Term t2, String sim_val){
		OntologyGui ontologyGui1 = sourcePanel.getCurrentOntologyGui();
		OntologyGui ontologyGui2 = targetPanel.getCurrentOntologyGui();
		JTree tree1= ontologyGui1.get_tree();
		JTree tree2= ontologyGui2.get_tree();
		final int position_x_root= 345 + sourcePanel.getX();
		final int position_y_root= 47+ targetPanel.getY() + 85;
		DefaultMutableTreeNode root1 = (DefaultMutableTreeNode) tree1.getModel().getRoot();
		DefaultMutableTreeNode node1= findNode(root1,t1.toString());
		final int intervalX_size=28, intervalY_size=16;
		line_coordinates[0][0]= (int) (position_x_root + ((node1.getPath().length)*intervalX_size)+ 1.5*(t1.toString().length()));
		line_coordinates[0][1]= position_y_root + (Find_row_for_node(node1)*intervalY_size);
		DefaultMutableTreeNode root2 = (DefaultMutableTreeNode) tree2.getModel().getRoot();
		DefaultMutableTreeNode node2= findNode(root2,t2.toString());	
		line_coordinates[0][2]= 290+ position_x_root+ ((node2.getPath().length)*intervalX_size);
		line_coordinates[0][3]= position_y_root + (Find_row_for_node(node2)*intervalY_size);
		line_writing[0]= sim_val;
		lines=true;
	}

	public DefaultMutableTreeNode findNode( DefaultMutableTreeNode root, String search ) {

	    @SuppressWarnings("rawtypes")
		Enumeration nodeEnumeration = root.breadthFirstEnumeration();
	    while( nodeEnumeration.hasMoreElements() ) {
	        DefaultMutableTreeNode node =
	            (DefaultMutableTreeNode)nodeEnumeration.nextElement();
	        String found = node.getUserObject().toString();
	        if( search.equals( found ) ) {
	            return node;
	        }
	    }
	    return null;
	}
	public int Find_row_for_node(DefaultMutableTreeNode node)
	{
		int row=0;
		while (!node.isRoot()){
			row+=1+node.getParent().getIndex(node);
			node=(DefaultMutableTreeNode) node.getParent();
		}
		return (1+row);
			
		
	}
}