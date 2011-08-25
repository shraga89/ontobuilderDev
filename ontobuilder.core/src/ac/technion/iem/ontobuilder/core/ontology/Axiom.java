package ac.technion.iem.ontobuilder.core.ontology;

import org.jdom.Element;

import ac.technion.iem.ontobuilder.core.ontology.operator.StringOperator;


/**
 * <p>Title: Axiom</p>
 * Extends {@link OntologyObject}
 */
public class Axiom extends OntologyObject
{
    private static final long serialVersionUID = 1L;

    protected String axiom;
    protected OntologyClass ontologyClass;

    /**
     * Constructs a default Axiom
     */
    public Axiom()
    {
        super();
    }

    /**
     * Constructs a Axiom
     * 
     * @param name the axiom name
     * @param axiom the axiom value
     */
    public Axiom(String name, String axiom)
    {
        this();
        this.name = name;
        this.axiom = axiom;
    }

    /**
     * Set the name
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
     * Get the axiom value
     * 
     * @return the axiom value
     */
    public String getAxiom()
    {
        return axiom;
    }

    /**
     * Set the axiom value
     * 
     * @param axiom the axiom value
     */
    public void setAxiom(String axiom)
    {
        this.axiom = axiom;
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
     * Compare two axioms
     * 
     * @param o1 the first axiom
     * @param o2 the second axiom
     * @return 0 if the axioms are equal
     */
    public int compare(Object o1, Object o2)
    {
        return ((Axiom) o1).axiom.compareTo(((Axiom) o2).axiom);
    }

    /**
     * Checks if an axiom is equal to another
     * 
     * @param o the axiom to compare
     */
    public boolean equals(Object o)
    {
        if (o instanceof Axiom)
            return axiom.equals(((Axiom) o).axiom) && o.getClass() == getClass() &&
                id == ((Axiom) o).id;
        return false;
    }

    /**
     * Clone the axiom
     * 
     * @return the cloned axiom
     */
    public Object clone()
    {
        return new Axiom(new String(name), new String(axiom));
    }

    public Axiom applyStringOperator(StringOperator operator)
    {
        return new Axiom(operator.transformString(name), operator.transformString(axiom));
    }

    /**
     * Get the XML representation of an axiom
     * 
     * @return an XML element
     */
    public Element getXMLRepresentation()
    {
        Element axiomElement = new Element("axiom");
        axiomElement.setAttribute(new org.jdom.Attribute("name", name));
        axiomElement.addContent(axiom);
        return axiomElement;
    }

    /**
     * Create an axiom from an XML element
     * 
     * @param axiomElement the XML {@link Element}
     * @param model the {@link Ontology}
     * @return an {@link Axiom}
     */
    public static Axiom getAxiomFromXML(Element axiomElement, Ontology model)
    {
        return new Axiom(axiomElement.getAttributeValue("name"), axiomElement.getText());
    }
}