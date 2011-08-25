package ac.technion.iem.ontobuilder.core.ontology;

import org.jdom.Element;

import ac.technion.iem.ontobuilder.core.ontology.operator.StringOperator;

/**
 * <p>Title: Relationship</p>
 * Extends {@link OntologyObject}
 */
public class Relationship extends OntologyObject
{
    private static final long serialVersionUID = 1L;

    protected Term source;
    protected Term target;

    /**
     * Constructs a default Relationship
     */
    public Relationship()
    {
        super();
    }

    /**
     * Constructs a Relationship
     * 
     * @param source the source {@link Term}
     * @param name the name
     * @param target the target {@link Term}
     */
    public Relationship(Term source, String name, Term target)
    {
        this();
        this.source = source;
        this.target = target;
        this.name = name;
    }

    /**
     * Set the name
     * 
     * @param the name
     */
    public void setName(String name)
    {
        super.setName(name);
        if (source != null && source.getOntology() != null)
            source.getOntology().fireObjectChangedEvent(this);
    }

    /**
     * Get the source term
     * 
     * @return the source
     */
    public Term getSource()
    {
        return source;
    }

    /**
     * Set the source term
     * 
     * @param the source {@link Term}
     */
    public void setSource(Term source)
    {
        this.source = source;
    }

    /**
     * Get the target term
     * 
     * @return the target {@link Term}
     */
    public Term getTarget()
    {
        return target;
    }

    /**
     * Set the target term
     * 
     * @param the target {@link Term}
     */
    public void setTarget(Term target)
    {
        this.target = target;
    }

    public String toString()
    {
        return source.getName() + " -- " + name + " --> " + target.getName();
    }

    public int compare(Object o1, Object o2)
    {
        return ((Relationship) o1).name.compareTo(((Relationship) o2).name);
    }

    public boolean equals(Object o)
    {
        if (o instanceof Relationship)
            return name.equals(((Relationship) o).name) &&
                source.equals(((Relationship) o).source) &&
                target.equals(((Relationship) o).target) && o.getClass() == getClass() &&
                id == ((Relationship) o).id;
        ;
        return false;
    }

    public Object clone()
    {
        return new Relationship(source, new String(name), target);
    }

    public Relationship applyStringOperator(StringOperator operator)
    {
        return new Relationship(source, operator.transformString(name), target);
    }

   
    /**
     * Get the XML {@link Element} representation of the relationship
     * 
     * @return an XML element
     */
    public Element getXMLRepresentation()
    {
        Element relationshipElement = new Element("relationship");
        relationshipElement.setAttribute("name", name);
        Element sourceElement = new Element("source");
        relationshipElement.addContent(sourceElement.addContent(source.getName()));
        Element targetsElement = new Element("targets");
        relationshipElement.addContent(targetsElement.addContent(target.getName()));
        return relationshipElement;
    }

    /**
     * Get the relationship from an XML element
     * 
     * @param relationshipElement the XML {@link Element}
     * @param source the source {@link Term}
     * @param model an {@link Ontology}
     * @return a {@link Relationship}
     */
    public static Relationship getRelationshipFromXML(Element relationshipElement, Term source,
        Ontology model)
    {
        Term target = model.findTerm(relationshipElement.getChild("targets").getText());
        Relationship relationship = new Relationship(source,
            relationshipElement.getAttributeValue("name"), target);
        return relationship;
    }

}