package ac.technion.iem.ontobuilder.gui.ontobuilder.elements;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
//import ilog.views.gantt.swing.IlvJTree;




import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.TreeTableModel;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.gui.ontobuilder.main.OntoBuilder;
import ac.technion.iem.ontobuilder.gui.ontology.OntologyGui;
import ac.technion.iem.ontobuilder.matching.match.Match;
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
    private OntologyPanel candidatePanel;
    private OntologyPanel targetPanel;
    private MIPanel miPanel;
    private ArrayList<Line> arcs = new ArrayList<Line>(); 
    private  static OntologyPanelSBS instance=null;
    /**
     * Constructs a default OntologyPanel
     * 
     * @param ontoBuilder the {@link OntoBuilder} application
     */
    public OntologyPanelSBS(OntoBuilder ontoBuilder)
    {
        miPanel = MIPanel.getMIPanel();
		JPanel topPane = new JPanel();
		JSplitPane mainPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPane, miPanel);
		mainPane.setDividerLocation(0.3);
		topPane.setLayout(new GridLayout(1,2));
		setLayout(new BorderLayout());
		add(BorderLayout.CENTER, mainPane);
		candidatePanel = new OntologyPanel(ontoBuilder);
        targetPanel = new OntologyPanel(ontoBuilder);
        
        candidatePanel.add(new JLabel("Candidate"));
        targetPanel.add(new JLabel("Target"));
        topPane.add(candidatePanel);
        topPane.add(targetPanel);
        instance = this;
        
      
    }

    /**
     * Add an ontology
     *
     * @param ontology the {@link Ontology} to add
     * @param toCandidate if true, adds the ontology to candidate panel
     */
    public void addOntology(final OntologyGui ontology,final boolean toCandidate)
    {
    	boolean hasSource = candidatePanel.getOntologies().size()>0;
    	boolean hasTarget = targetPanel.getOntologies().size()>0;
    	//TODO remove all arcs
    	if (toCandidate){
   			candidatePanel.remove(0);
    		candidatePanel.addOntology(ontology);
    		if (hasTarget)
    			miPanel.setMi(ontology,targetPanel.getCurrentOntologyGui());
    		candidatePanel.addTermListener(true);
    	}
    	else 
    	{
    		targetPanel.remove(0);
    		targetPanel.addOntology(ontology);
    		if (hasSource)
    			miPanel.setMi(candidatePanel.getCurrentOntologyGui(),ontology);
    		targetPanel.addTermListener(false);
    	}
    	
    }

    /**
     * Get the candidate ontology
     *
     * @return the {@link Ontology}
     */
    public Ontology getCandidateOntology()
    {
        return candidatePanel.getCurrentOntology();
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
     * Close the candidate ontology
     *
     */
    public void closeCandidateOntology()
    {
        candidatePanel.closeCurrentOntology();
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
    /**
     * Get the current Candidate ontologyGui
     *
     * @return the {@link OntologyGui}
     */
	public OntologyGui getCandidateOntologyGui(){
		if (candidatePanel.getOntologies().isEmpty()) return null;
		return candidatePanel.getCurrentOntologyGui();
	}
    /**
     * Get the current Target ontologyGui
     *
     * @return the {@link OntologyGui}
     */
	public OntologyGui getTargetOntologyGui(){
		if (targetPanel.getOntologies().isEmpty()) return null;
		return targetPanel.getCurrentOntologyGui();
	}
	
	public boolean lines= false; 
    //don't forget to use : frame.repaint(); after using draw_line
	public void draw_line(Term t1, Term t2, String sim_val){
		OntologyGui ontologyGui1 = candidatePanel.getCurrentOntologyGui();
		OntologyGui ontologyGui2 = targetPanel.getCurrentOntologyGui();
		JTree tree1= ontologyGui1.get_tree();
		JTree tree2= ontologyGui2.get_tree();
		System.out.println(tree1.getVisibleRowCount());
		System.out.println(tree1.getRowCount());
		System.out.println(tree1.getMinSelectionRow());
		DefaultMutableTreeNode root1 = (DefaultMutableTreeNode) tree1.getModel().getRoot();
		DefaultMutableTreeNode node1= findNode(root1,t1.toString());
		TreePath path1 =new TreePath(node1.getPath());
		int x_start=(int) (candidatePanel.getX()+ tree1.getRowBounds(tree1.getRowForPath(path1)).x+ 3*(t1.toString().length()));
		int y_start= candidatePanel.getY()+ tree1.getRowBounds(tree1.getRowForPath(path1)).y;
		DefaultMutableTreeNode root2 = (DefaultMutableTreeNode) tree2.getModel().getRoot();
		DefaultMutableTreeNode node2= findNode(root2,t2.toString());	
		TreePath path2 =new TreePath(node2.getPath());
		int x_end=(int) (targetPanel.getX()+ tree2.getRowBounds(tree2.getRowForPath(path2)).x);
		int y_end= targetPanel.getY()+ tree1.getRowBounds(tree2.getRowForPath(path2)).y;
		Graphics2D g= (Graphics2D) getInstance().getGraphics();
		Line line = new Line(this, g, x_start ,y_start+45, x_end, y_end+45, sim_val, true, false);
		arcs.add(line);
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
	public void paint( Graphics g ){
    	super.paint(g);
    	for (Line arc : arcs){
        	if (lines){
        		arc.graphics=(Graphics2D) g;
            	arc.repaint();
            	this.repaint();
        	}
    	}

    	}
	public static OntologyPanelSBS getInstance() {
		return instance;
		
	}

	/**
	 * Should delete all arcs
	 */
	public void clearArcs() {
		//TODO
		
	}
}