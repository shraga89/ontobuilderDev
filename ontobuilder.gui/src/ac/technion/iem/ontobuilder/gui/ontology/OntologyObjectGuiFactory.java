package ac.technion.iem.ontobuilder.gui.ontology;

import ac.technion.iem.ontobuilder.core.ontology.Attribute;
import ac.technion.iem.ontobuilder.core.ontology.Axiom;
import ac.technion.iem.ontobuilder.core.ontology.Domain;
import ac.technion.iem.ontobuilder.core.ontology.DomainEntry;
import ac.technion.iem.ontobuilder.core.ontology.OntologyClass;
import ac.technion.iem.ontobuilder.core.ontology.OntologyObject;
import ac.technion.iem.ontobuilder.core.ontology.Relationship;
import ac.technion.iem.ontobuilder.core.ontology.Term;

/**
 * <p>Title: OntologyObjectGuiFactory</p>
 * <p>Description: Creates an ontology Gui object according to the input ontology object</p>
 */
public class OntologyObjectGuiFactory
{
    public static OntologyObjectGui getOntologyObjectGui(OntologyObject ontologyObject)
    {
        if (ontologyObject instanceof Attribute)
            return new AttributeGui((Attribute)ontologyObject);

        if (ontologyObject instanceof Term)
            return new TermGui((Term)ontologyObject);
        
        if (ontologyObject instanceof Axiom)
            return new AxiomGui((Axiom)ontologyObject);
        
        if (ontologyObject instanceof Domain)
            return new DomainGui((Domain)ontologyObject);
        
        if (ontologyObject instanceof DomainEntry)
            return new DomainEntryGui((DomainEntry)ontologyObject);
        
        if (ontologyObject instanceof OntologyClass)
            return new OntologyClassGui((OntologyClass)ontologyObject);
        
        if (ontologyObject instanceof Relationship)
            return new RelationshipGui((Relationship)ontologyObject);
        
        else return null;
    }
}
