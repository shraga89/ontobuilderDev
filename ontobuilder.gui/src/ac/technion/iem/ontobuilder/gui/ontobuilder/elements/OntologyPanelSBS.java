package ac.technion.iem.ontobuilder.gui.ontobuilder.elements;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
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

}