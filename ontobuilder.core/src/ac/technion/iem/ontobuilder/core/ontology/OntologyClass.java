package ac.technion.iem.ontobuilder.core.ontology;

import java.util.ArrayList;
import java.util.Iterator;

import org.jdom.Element;

import ac.technion.iem.ontobuilder.core.ontology.operator.StringOperator;

/**
 * <p>Title: OntologyClass</p>
 * Extends {@link OntologyObject}
 */
public class OntologyClass extends OntologyObject
{
    private static final long serialVersionUID = 1L;

    protected Ontology ontology;
    protected Domain domain;
    protected ArrayList<Attribute> attributes;
    protected ArrayList<Axiom> axioms;
    protected OntologyClass superClass;
    protected ArrayList<OntologyClass> instances;

    /**
     * Constructs a default OntologyClass
     */
    public OntologyClass()
    {
        super();
        instances = new ArrayList<OntologyClass>();
        domain = new Domain();
        domain.setOntologyClass(this);
        attributes = new ArrayList<Attribute>();
        axioms = new ArrayList<Axiom>();
    }

    /**
     * Constructs a OntologyClass
     *
     * @param name the class name
     */
    public OntologyClass(String name)
    {
        this();
        this.name = name;
    }

    /**
     * Constructs a OntologyClass
     *
     * @param superClass the {@link OntologyClass}
     * @param name the class name
     */
    public OntologyClass(OntologyClass superClass, String name)
    {
        this(name);
        setSuperClass(superClass);
        if (ontology != null)
            ontology.fireClassAddedEvent(superClass, this);
    }

    /**
     * Set the superClass
     *
     * @param superClass the {@link OntologyClass}
     */
    public void setSuperClass(OntologyClass superClass)
    {
        if (superClass == null)
            return;
        superClass.instances.add(this);
        this.superClass = superClass;
        ontology = superClass.ontology;
        domain = (Domain) superClass.domain.clone();
        domain.setOntologyClass(this);
        for (Iterator<Attribute> i = superClass.attributes.iterator(); i.hasNext();)
        {
            Attribute a = (Attribute) ((Attribute) i.next()).clone();
            a.setValue(null);
            a.setOntologyClass(this);
            attributes.add(a);
        }
        for (Iterator<Axiom> i = superClass.axioms.iterator(); i.hasNext();)
        {
            Axiom a = (Axiom) ((Axiom) i.next()).clone();
            a.setOntologyClass(this);
            axioms.add(a);
        }
        ontology.fireObjectChangedEvent(this);
    }

    /**
     * Get the superClass 
     *
     * @return the {@link OntologyClass} 
     */
    public OntologyClass getSuperClass()
    {
        return superClass;
    }

    /**
     * Check is a class is an instance of the Ontology class by its name
     *
     * @param className the class name
     * @return <code>true</code> if it is an instance
     */
    public boolean isInstanceOf(String className)
    {
        OntologyClass superClassAux = superClass;
        while (superClassAux != null)
        {
            if (superClassAux.getName().equals(className))
                return true;
            else
                superClassAux = superClassAux.superClass;
        }
        return false;
    }

    /**
     * Set the name
     * 
     * @param name the name
     */
    public void setName(String name)
    {
        super.setName(name);
        if (ontology != null)
            ontology.fireObjectChangedEvent(this);
    }

    /**
     * Set the ontology
     * 
     * @param ontology the {@link Ontology}
     */
    public void setOntology(Ontology ontology)
    {
        this.ontology = ontology;
        for (Iterator<OntologyClass> i = instances.iterator(); i.hasNext();)
        {
            OntologyClass oc = (OntologyClass) i.next();
            oc.setOntology(ontology);
        }
    }

    /**
     * Get the ontology 
     *
     * @return the {@link Ontology} 
     */
    public Ontology getOntology()
    {
        return ontology;
    }

    /**
     * Set the domain
     * 
     * @param domain the {@link Domain}
     */
    public void setDomain(Domain domain)
    {
        this.domain = domain;
        domain.setOntologyClass(this);
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
     * Add an axion
     *
     * @param axiom the {@link Axiom} to add
     */
    public void addAxiom(Axiom axiom)
    {
        if (axiom == null)
            return;
        if (!axioms.contains(axiom))
        {
            axioms.add(axiom);
            axiom.setOntologyClass(this);
            for (Iterator<OntologyClass> i = instances.iterator(); i.hasNext();)
                ((OntologyClass) i.next()).addAxiom((Axiom) axiom.clone());
            if (ontology != null)
                ontology.fireAxiomAddedEvent(this, axiom);
        }
    }

    /**
     * Remove an axion
     *
     * @param axiom the {@link Axiom} to remove
     */
    public void removeAxiom(Axiom axiom)
    {
        if (axioms.contains(axiom))
        {
            axioms.remove(axiom);
            if (ontology != null)
                ontology.fireAxiomDeletedEvent(this, axiom);
        }
    }

    /**
     * Get the number of axioms
     */
    public int getAxiomsCount()
    {
        return axioms.size();
    }
    
    /**
     * Get the axioms
     */
    public ArrayList<Axiom> getAxioms()
    {
        return axioms;
    }

    /**
     * Get an axiom according to its index
     *
     * @param index the index
     * @return the {@link Axiom}
     */
    public Axiom getAxiom(int index)
    {
        if (index < 0 || index >= axioms.size())
            return null;
        return (Axiom) axioms.get(index);
    }
    
    public ArrayList<OntologyClass> getInstances()
    {
        return instances;
    }

    /**
     * Remove a certain sub-class 
     *
     * @param subClass the subClass {@link OntologyClass} to remove
     */
    public void removeSubClass(OntologyClass subClass)
    {
        if (instances.contains(subClass))
        {
            subClass.superClass = null;
            instances.remove(subClass);
            if (ontology != null)
                ontology.fireClassDeletedEvent(this, subClass);
        }
    }

    /**
     * Get the number of sub classes
     */
    public int getSubClassesCount()
    {
        return instances.size();
    }

    /**
     * Get a sub-class according to its index
     *
     * @param index the index
     * @return the sub-class {@link OntologyClass}
     */
    public OntologyClass getSubClass(int index)
    {
        if (index < 0 || index >= instances.size())
            return null;
        return (OntologyClass) instances.get(index);
    }

    /**
     * Add an attribute
     *
     * @param attribute the {@link Attribute} to add
     */
    public void addAttribute(Attribute attribute)
    {
        if (attribute == null)
            return;
        if (!attributes.contains(attribute))
        {
            attributes.add(attribute);
            attribute.setOntologyClass(this);
            for (Iterator<OntologyClass> i = instances.iterator(); i.hasNext();)
            {
                Attribute a = (Attribute) attribute.clone();
                a.setOntologyClass(this);
                ((OntologyClass) i.next()).addAttribute(a);
            }
            if (ontology != null)
                ontology.fireAttributeAddedEvent(this, attribute);
        }
    }

    /**
     * Remove an attribute
     *
     * @param attribute the {@link Attribute} to remove
     */
    public void removeAttribute(Attribute attribute)
    {
        if (attributes.contains(attribute))
        {
            attribute.setOntologyClass(null);
            attributes.remove(attribute);
            if (ontology != null)
                ontology.fireAttributeDeletedEvent(this, attribute);
        }
    }

    /**
     * Get the number of attributes
     */
    public int getAttributesCount()
    {
        return attributes.size();
    }
    
    /**
     * Get the attributes
     */
    public ArrayList<Attribute> getAttributes()
    {
        return attributes;
    }

    /**
     * Get a attribute according to its index
     *
     * @param index the index
     * @return the {@link Attribute}
     */
    public Attribute getAttribute(int index)
    {
        if (index < 0 || index >= attributes.size())
            return null;
        return (Attribute) attributes.get(index);
    }

    public void setAttributeValue(String name, Object value)
    {
        for (Iterator<Attribute> i = attributes.iterator(); i.hasNext();)
        {
            Attribute a = (Attribute) i.next();
            if (a.getName().equals(name))
                a.setValue(value);
        }
    }

    public void setAttributeValue(String name, Object value, Domain domain)
    {
        for (Iterator<Attribute> i = attributes.iterator(); i.hasNext();)
        {
            Attribute a = (Attribute) i.next();
            if (a.getName().equals(name))
            {
                a.setDomain(domain);
                a.setValue(value);
            }
        }
    }

    public Object getAttributeValue(String name)
    {
        for (Iterator<Attribute> i = attributes.iterator(); i.hasNext();)
        {
            Attribute a = (Attribute) i.next();
            if (a.getName().equals(name))
            {
                if (a.getValue() != null)
                    return a.getValue();
                else if (superClass != null)
                    return superClass.getAttributeValue(name);
                else
                    return null;
            }
        }
        return null;
    }

    public int compare(Object o1, Object o2)
    {
        return ((OntologyClass) o1).name.compareTo(((OntologyClass) o2).name);
    }

    public boolean equals(Object o)
    {
        if (o instanceof OntologyClass)
            return name.equals(((OntologyClass) o).name) && o.getClass() == getClass() &&
                id == ((OntologyClass) o).id;
        return false;
    }

    public Object clone()
    {
        OntologyClass ontologyClass = new OntologyClass(new String(name));
        ontologyClass.setDomain((Domain) domain.clone());
        ontologyClass.superClass = superClass;
        for (Iterator<Axiom> i = axioms.iterator(); i.hasNext();)
            ontologyClass.addAxiom((Axiom) ((Axiom) i.next()).clone());
        for (Iterator<Attribute> i = attributes.iterator(); i.hasNext();)
            ontologyClass.addAttribute((Attribute) ((Attribute) i.next()).clone());
        ontologyClass.ontology = ontology;
        return ontologyClass;
    }

    public OntologyClass applyStringOperator(StringOperator operator)
    {
        OntologyClass ontologyClass = new OntologyClass(operator.transformString(name));
        ontologyClass.setDomain(domain.applyStringOperator(operator));
        ontologyClass.superClass = superClass;
        for (Iterator<Axiom> i = axioms.iterator(); i.hasNext();)
            ontologyClass.addAxiom(((Axiom) i.next()).applyStringOperator(operator));
        for (Iterator<Attribute> i = attributes.iterator(); i.hasNext();)
            ontologyClass.addAttribute(((Attribute) i.next()).applyStringOperator(operator));
        ontologyClass.ontology = ontology;
        return ontologyClass;
    }

    /**
     * Get the XML {@link Element} representation of the ontology class
     */
    public Element getXMLRepresentation()
    {
        Element classElement = new Element("class");
        classElement.setAttribute("name", name);

        // Domain
        classElement.addContent(domain.getXMLRepresentation());

        // Attributes
        Element attributesElement = new Element("attributes");
        classElement.addContent(attributesElement);
        for (Iterator<Attribute> i = attributes.iterator(); i.hasNext();)
            attributesElement.addContent(((Attribute) i.next()).getXMLRepresentation());

        // Axioms
        Element axiomsElement = new Element("axioms");
        classElement.addContent(axiomsElement);
        for (Iterator<Axiom> i = axioms.iterator(); i.hasNext();)
            axiomsElement.addContent(((Axiom) i.next()).getXMLRepresentation());

        // Subclasses
        Element subclassesElement = new Element("subclasses");
        classElement.addContent(subclassesElement);
        for (Iterator<OntologyClass> i = instances.iterator(); i.hasNext();)
        {
            OntologyClass instance = (OntologyClass) i.next();
            if (!(instance instanceof Term))
                subclassesElement.addContent(instance.getXMLRepresentation());
        }

        return classElement;
    }

    /**
     * Get the classElement from an XML element
     *
     * @param domainElement the XML {@link Element}
     * @param model the {@link Ontology}
     * @return the {@link OntologyClass}
     */
    public static OntologyClass getClassFromXML(Element classElement, Ontology model)
    {
        OntologyClass ontologyClass = new OntologyClass(classElement.getAttributeValue("name"));
        ontologyClass.setDomain(Domain.getDomainFromXML(classElement.getChild("domain"), model));
        ontologyClass.ontology = model;

        java.util.List<?> attributeElements = classElement.getChild("attributes").getChildren();
        for (Iterator<?> i = attributeElements.iterator(); i.hasNext();)
            ontologyClass.addAttribute(Attribute.getAttributeFromXML((Element) i.next(), model));

        java.util.List<?> axiomElements = classElement.getChild("axioms").getChildren();
        for (Iterator<?> i = axiomElements.iterator(); i.hasNext();)
            ontologyClass.addAxiom(Axiom.getAxiomFromXML((Element) i.next(), model));

        java.util.List<?> subclassesElements = classElement.getChild("subclasses").getChildren();
        for (Iterator<?> i = subclassesElements.iterator(); i.hasNext();)
        {
            OntologyClass subClass = OntologyClass.getClassFromXML((Element) i.next(), model);
            subClass.superClass = ontologyClass;
            subClass.ontology = model;
            ontologyClass.instances.add(subClass);
        }

        return ontologyClass;
    }
}