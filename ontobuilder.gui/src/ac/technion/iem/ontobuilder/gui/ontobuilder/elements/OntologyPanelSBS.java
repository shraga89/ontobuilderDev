package ac.technion.iem.ontobuilder.gui.ontobuilder.elements;

import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.gui.ontobuilder.main.OntoBuilder;
import ac.technion.iem.ontobuilder.gui.ontology.OntologyGui;

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
    /**
     * Constructs a default OntologyPanel
     * 
     * @param ontoBuilder the {@link OntoBuilder} application
     */
    public OntologyPanelSBS(OntoBuilder ontoBuilder)
    {
    
        sourcePanel = new OntologyPanel(ontoBuilder);
        targetPanel = new OntologyPanel(ontoBuilder);

        sourcePanel.add(new JLabel("Source"));
        targetPanel.add(new JLabel("Target"));

        setLayout(new GridLayout(1,2));
        add(sourcePanel);
        add(targetPanel);
    }

    /**
     * Add an ontology
     *
     * @param ontology the {@link Ontology} to add
     * @param toLeft if true, adds the ontology to source panel
     */
    public void addOntology(final OntologyGui ontology,final boolean toSource)
    {
    	if (toSource) sourcePanel.addOntology(ontology);
    	else targetPanel.addOntology(ontology);
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

}