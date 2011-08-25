package ac.technion.iem.ontobuilder.core.ontology;

import org.jdom.Element;

import ac.technion.iem.ontobuilder.core.ontology.operator.StringOperator;


/**
 * <p>Title: Attribute</p>
 * Extends {@link OntologyObject}
 */
public class Attribute extends OntologyObject
{
    private static final long serialVersionUID = 1L;

    protected Object value;
    protected Domain domain;
    protected Term term;
    protected OntologyClass ontologyClass;

    /**
     * Constructs a default Attribute
     */
    public Attribute()
    {
        super();
        domain = new Domain();
        domain.setAttribute(this);
    }

    /**
     * Constructs a Attribute
     * 
     * @param name the name of the attribute
     * @param value the value of the attribute
     */
    public Attribute(String name, Object value)
    {
        this();
        this.name = name;
        this.value = value;
    }

    /**
     * Constructs a Attribute
     * 
     * @param name the name of the attribute
     * @param value the value of the attribute
     * @param domainName the domain name of the attribute
     */
    public Attribute(String name, Object value, String domainName)
    {
        this(name, value);
        domain.setName(domainName);
    }

    /**
     * Constructs a Attribute
     * 
     * @param name the name of the attribute
     * @param value the value of the attribute
     * @param domain the {@link Domain} of the attribute
     */
    public Attribute(String name, Object value, Domain domain)
    {
        this(name, value);
        this.domain = domain;
    }

    /**
     * Set the name of the attribute
     * 
     * @param name the name
     */
    public void setName(String name)
    {
        super.setName(name);
        if (ontologyClass != null && ontologyClass.getOntology() != null)
            ontologyClass.getOntology().fireObjectChangedEvent(this);
    }

    /**
     * Get the value
     * 
     * @return the value
     */
    public Object getValue()
    {
        return value;
    }

    /**
     * Set the value of the attribute
     * 
     * @param value the value
     */
    public void setValue(Object value)
    {
        this.value = value;
        if (ontologyClass != null && ontologyClass.getOntology() != null)
            ontologyClass.getOntology().fireObjectChangedEvent(this);
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
     * Set the term
     * 
     * @param term the {@link Term}
     */
    public void setTerm(Term term)
    {
        this.term = term;
    }

    /**
     * Get the ontology class
     * 
     * @return the {@link OntologyClass}
     */
    public OntologyClass getOntologyClass()
    {
        return ontologyClass;
    }

    /**
     * Set the ontology class
     * 
     * @param ontologyClass the {@link OntologyClass}
     */
    public void setOntologyClass(OntologyClass ontologyClass)
    {
        this.ontologyClass = ontologyClass;
    }

    /**
     * Set the domain
     * 
     * @param domain the {@link Domain}
     */
    public void setDomain(Domain domain)
    {
        this.domain = domain;
        domain.setAttribute(this);
    }

    /**
     * Get the domain
     * 
     * @return the {@link Domain}
     */
    public Domain getDomain()
    {
        return domain;
    }

    /**
     * Compare two attributes
     * 
     * @param o1 the first attribute
     * @param o2 the second attribute
     * @return 0 if the attributes are equal
     */
    public int compare(Object o1, Object o2)
    {
        return ((Attribute) o1).name.compareTo(((Attribute) o2).name);
    }

    /**
     * Checks if an attribute is equal to another
     * 
     * @param o the attibute to compare
     */
    public boolean equals(Object o)
    {
        if (o instanceof Attribute)
            return name.equals(((Attribute) o).name) && o.getClass() == getClass() &&
                id == ((Attribute) o).id;
        return false;
    }

 

    /**
     * Clone the attribute
     * 
     * @return the cloned attribute
     */
    public Object clone()
    {
        Attribute a = new Attribute(new String(name), value, (Domain) domain.clone());
        a.term = term;
        a.ontologyClass = ontologyClass;
        return a;
    }

    public Attribute applyStringOperator(StringOperator operator)
    {
        Attribute a = new Attribute(operator.transformString(name),
            ((value instanceof String) ? operator.transformString((String) value) : value),
            domain.applyStringOperator(operator));
        a.term = term;
        a.ontologyClass = ontologyClass;
        return a;
    }

    /**
     * Get the XML representation of an attribute
     * 
     * @return an XML {@link Element}
     */
    public Element getXMLRepresentation()
    {
        Element attributeElement = new Element("attribute");
        attributeElement.setAttribute(new org.jdom.Attribute("name", name));
        if (value != null)
            attributeElement.setAttribute(new org.jdom.Attribute("value", value.toString()));
        attributeElement.addContent(domain.getXMLRepresentation());
        return attributeElement;
    }

    /**
     * Create an attribute from an XML element
     * 
     * @param attributeElement the XML {@link Element}
     * @param model the {@link Ontology}
     * @return an {@link Attribute}
     */
    public static Attribute getAttributeFromXML(Element attributeElement, Ontology model)
    {
        Attribute attribute = new Attribute(attributeElement.getAttributeValue("name"),
            attributeElement.getAttributeValue("value"));
        attribute.setDomain(Domain.getDomainFromXML(attributeElement.getChild("domain"), model));
        return attribute;
    }
}