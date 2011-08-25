package ac.technion.iem.ontobuilder.gui.ontology;

import java.util.EventListener;

/**
 * <p>Title: interface OntologySelectionListener</p>
 * Extends {@link EventListener}
 */
public interface OntologySelectionListener extends EventListener
{
    public void valueChanged(OntologySelectionEvent e);
}
