package ac.technion.iem.ontobuilder.core.ontology.event;

import java.util.EventObject;

import ac.technion.iem.ontobuilder.core.ontology.Attribute;
import ac.technion.iem.ontobuilder.core.ontology.Axiom;
import ac.technion.iem.ontobuilder.core.ontology.Domain;
import ac.technion.iem.ontobuilder.core.ontology.DomainEntry;
import ac.technion.iem.ontobuilder.core.ontology.OntologyClass;
import ac.technion.iem.ontobuilder.core.ontology.OntologyObject;
import ac.technion.iem.ontobuilder.core.ontology.Relationship;
import ac.technion.iem.ontobuilder.core.ontology.Term;

/**
 * <p>Title: OntologyModelEvent</p>
 * Extends {@link EventObject}
 */
public class OntologyModelEvent extends EventObject
{
    private static final long serialVersionUID = 1L;

    protected Term term;
    protected Term parent;
    protected OntologyClass ontologyClass;
    protected OntologyClass superClass;
    protected Attribute attribute;
    protected Relationship relationship;
    protected OntologyObject object;
    protected Axiom axiom;
    protected Domain domain;
    protected DomainEntry entry;
    protected int position = -1;

    /**
     * Constructs an OntologyModelEvent
     * 
     * @param source the source
     * @param object the {@link OntologyObject}
     */
    public OntologyModelEvent(Object source, OntologyObject object)
    {
        super(source);
        this.object = object;
    }

    /**
     * Constructs an OntologyModelEvent
     * 
     * @param source the source
     * @param parent the parent {@link Term}
     * @param term the {@link Term}
     */
    public OntologyModelEvent(Object source, Term parent, Term term)
    {
        super(source);
        this.term = term;
        this.parent = parent;
    }

    /**
     * Constructs an OntologyModelEvent
     * 
     * @param source the source
     * @param superClass an {@link OntologyClass}
     * @param ontologyClass the {@link OntologyClass}
     */
    public OntologyModelEvent(Object source, OntologyClass superClass, OntologyClass ontologyClass)
    {
        super(source);
        this.superClass = superClass;
        this.ontologyClass = ontologyClass;
    }

    /**
     * Constructs an OntologyModelEvent
     * 
     * @param source the source
     * @param ontologyClass the {@link OntologyClass}
     * @param attribute the {@link Attribute}
     */
    public OntologyModelEvent(Object source, OntologyClass ontologyClass, Attribute attribute)
    {
        super(source);
        this.ontologyClass = ontologyClass;
        this.attribute = attribute;
    }

    /**
     * Constructs an OntologyModelEvent
     * 
     * @param source the source
     * @param ontologyClass the {@link OntologyClass}
     * @param axiom an {@link Axiom}
     */
    public OntologyModelEvent(Object source, OntologyClass ontologyClass, Axiom axiom)
    {
        super(source);
        this.ontologyClass = ontologyClass;
        this.axiom = axiom;
    }

    /**
     * Constructs an OntologyModelEvent
     * 
     * @param source the source
     * @param term the {@link Term}
     * @param relationship a {@link Relationship}
     */
    public OntologyModelEvent(Object source, Term term, Relationship relationship)
    {
        super(source);
        this.term = term;
        this.relationship = relationship;
    }

    /**
     * Constructs an OntologyModelEvent
     * 
     * @param source the source
     * @param domain a {@link Domain}
     * @param entry a {@link DomainEntry}
     */
    public OntologyModelEvent(Object source, Domain domain, DomainEntry entry)
    {
        super(source);
        this.domain = domain;
        this.entry = entry;
    }

    /**
     * Set a domain entry
     * 
     * @param entry the {@link DomainEntry}
     */
    public void setEntry(DomainEntry entry)
    {
        this.entry = entry;
    }

    /**
     * Get a domain entry
     * 
     * @return a {@link DomainEntry}
     */
    public DomainEntry getEntry()
    {
        return entry;
    }

    /**
     * Set a domain
     * 
     * @param domain the {@link Domain}
     */
    public void setDomain(Domain domain)
    {
        this.domain = domain;
    }

    /**
     * Get the domain
     * 
     * @return a {@link Domain}
     */
    public Domain getDomain()
    {
        return domain;
    }

    /**
     * Set an OntologyObject
     * 
     * @param object the {@link OntologyObject}
     */
    public void setObject(OntologyObject object)
    {
        this.object = object;
    }

    /**
     * Get the object
     * 
     * @return a {@link OntologyObject}
     */
    public OntologyObject getObject()
    {
        return object;
    }

    /**
     * Set an OntologyClass
     * 
     * @param ontologyClass the {@link OntologyClass}
     */
    public void setOntologyClass(OntologyClass ontologyClass)
    {
        this.ontologyClass = ontologyClass;
    }

    /**
     * Get the OntologyClass
     * 
     * @return an {@link OntologyClass}
     */
    public OntologyClass getOntologyClass()
    {
        return ontologyClass;
    }

    /**
     * Set the attribute
     * 
     * @param attribute the {@link Attribute}
     */
    public void setAttribute(Attribute attribute)
    {
        this.attribute = attribute;
    }

    /**
     * Get the attribute
     * 
     * @return an attribute
     */
    public Attribute getAttribute()
    {
        return attribute;
    }

    /**
     * Set the axiom
     * 
     * @param axiom the axiom
     */
    public void setAxiom(Axiom axiom)
    {
        this.axiom = axiom;
    }

    /**
     * Get the axiom
     * 
     * @return an {@link Axiom}
     */
    public Axiom getAxiom()
    {
        return axiom;
    }

    /**
     * Get the SuperClass
     * 
     * @return an {@link OntologyClass}
     */
    public void setSuperClass(OntologyClass ontologyClass)
    {
        this.superClass = ontologyClass;
    }

    /**
     * Get the SuperClass
     * 
     * @return the {@link OntologyClass}
     */
    public OntologyClass getSuperClass()
    {
        return superClass;
    }

    /**
     * Set the term
     * 
     * @param term the {@link Term}
     */
    public void setTerm(Term term)
    {
        this.term = term;
    }

    /**
     * Get the term
     * 
     * @return the {@link Term}
     */
    public Term getTerm()
    {
        return term;
    }

    /**
     * Set the parent
     * 
     * @param parent the parent {@link Term}
     */
    public void setParent(Term parent)
    {
        this.parent = parent;
    }

    /**
     * Get the parent
     * 
     * @return the parent {@link Term}
     */
    public Term getParent()
    {
        return parent;
    }

    /**
     * Set the relationship
     * 
     * @param relationship the {@link Relationship}
     */
    public void setRelationship(Relationship relationship)
    {
        this.relationship = relationship;
    }

    /**
     * Get the relationship
     * 
     * @return the {@link Relationship}
     */
    public Relationship getRelationship()
    {
        return relationship;
    }

    /**
     * Set the position
     * 
     * @param position the position
     */
    public void setPosition(int position)
    {
        this.position = position;
    }

    /**
     * Get the position
     * 
     * @return the position
     */
    public int getPosition()
    {
        return position;
    }
}