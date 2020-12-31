package ac.technion.iem.ontobuilder.core.ontology.event;

import java.util.EventListener;

/**
 * <p>Title: OntologyModelListener</p>
 * Extends {@link EventListener}
 */
public interface OntologyModelListener extends EventListener
{
    public void termAdded(OntologyModelEvent e);

    public void termDeleted(OntologyModelEvent e);

    public void classAdded(OntologyModelEvent e);

    public void classDeleted(OntologyModelEvent e);

    public void attributeAdded(OntologyModelEvent e);

    public void attributeDeleted(OntologyModelEvent e);

    public void axiomAdded(OntologyModelEvent e);

    public void axiomDeleted(OntologyModelEvent e);

    public void relationshipAdded(OntologyModelEvent e);

    public void relationshipDeleted(OntologyModelEvent e);

    public void domainEntryAdded(OntologyModelEvent e);

    public void domainEntryDeleted(OntologyModelEvent e);

    public void objectChanged(OntologyModelEvent e);

    public void modelChanged(OntologyModelEvent e);
}
