package ac.technion.iem.ontobuilder.gui.ontobuilder.elements;

import java.util.Vector;

import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.event.OntologyModelAdapter;
import ac.technion.iem.ontobuilder.core.ontology.event.OntologyModelEvent;
import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.ontobuilder.main.OntoBuilder;
import ac.technion.iem.ontobuilder.gui.ontology.OntologyGui;

/**
 * <p>Title: OntologyPanel</p>
 * Extends a {@link JTabbedPane}
 */
public class OntologyPanel extends JTabbedPane
{
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a default OntologyPanel
     * 
     * @param ontoBuilder the {@link OntoBuilder} application
     */
    public OntologyPanel(OntoBuilder ontoBuilder)
    {
    }

    /**
     * Add an ontology
     *
     * @param ontology the {@link Ontology} to add
     */
    public void addOntology(final OntologyGui ontology)
    {
        final JScrollPane scroll = new JScrollPane(ontology);
        ontology.addOntologyModelListener(new OntologyModelAdapter()
        {
            public void modelChanged(OntologyModelEvent e)
            {
                int index = indexOfComponent(scroll);
                if (index == -1)
                    return;
                setTitleAt(index, ontology.getName() + (ontology.getOntology().isDirty() ? " *" : ""));
            }
        });
        addTab(ontology.getName() + (ontology.getOntology().isDirty() ? " *" : ""),
            ApplicationUtilities.getImage("ontology.gif"), scroll);
        setSelectedComponent(scroll);
    }

    /**
     * Get the current ontology
     *
     * @return the {@link Ontology}
     */
    public Ontology getCurrentOntology()
    {
        JScrollPane scroll = (JScrollPane) getSelectedComponent();
        if (scroll == null)
            return null;
        Ontology o = ((OntologyGui) scroll.getViewport().getView()).getOntology();
        return o;
    }

    /**
     * Close the current ontology
     *
     * @param ontologyGui the {@link Ontology} to close
     */
    public void closeCurrentOntology()
    {
        remove(getSelectedIndex());
    }

    /**
     * Get the all the ontologies
     *
     * @return a list of {@link Ontology}
     */
    public Vector<Ontology> getOntologies()
    {
        Vector<Ontology> ontologies = new Vector<Ontology>();
        for (int i = 0; i < getTabCount(); i++)
        {
            JScrollPane scroll = (JScrollPane) getComponentAt(i);
            ontologies.add(((OntologyGui)(scroll.getViewport().getView())).getOntology());
        }
        return ontologies;
    }
}