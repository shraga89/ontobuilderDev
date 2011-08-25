package ac.technion.iem.ontobuilder.core.ontology;

import org.jdom.Element;

import ac.technion.iem.ontobuilder.core.ontology.operator.StringOperator;

/**
 * <p>Title: DomainEntry</p>
 * Extends {@link OntologyObject}
 */
public class DomainEntry extends OntologyObject
{
    private static final long serialVersionUID = 1L;

    Object entry;
    Domain domain;

    /**
     * Constructs a default DomainEntry
     */
    public DomainEntry()
    {
        super();
    }

    /**
     * Constructs a DomainEntry
     *
     * @param domain the {@link Domain}
     */
    public DomainEntry(Domain domain)
    {
        this();
        this.domain = domain;
    }

    /**
     * Constructs a DomainEntry
     *
     * @param domain the {@link Domain}
     * @param entry the entry
     */
    public DomainEntry(Domain domain, Object entry)
    {
        this(domain);
        this.entry = entry;
    }

    /**
     * Constructs a DomainEntry
     *
     * @param entry the entry
     */
    public DomainEntry(Object entry)
    {
        this();
        this.entry = entry;
    }

    /**
     * Get the entry
     *
     * @return the entry
     */
    public Object getEntry()
    {
        return entry;
    }

    /**
     * Set the entry
     *
     * @param entry the entry
     */
    public void setEntry(Object entry)
    {
        this.entry = entry;
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
     * Set the domain
     *
     * @param domain the {@link Domain}
     */
    public void setDomain(Domain domain)
    {
        this.domain = domain;
    }

    public String toString()
    {
        return entry.toString();
    }

    public int compare(Object o1, Object o2)
    {
        return ((DomainEntry) o1).name.compareTo(((DomainEntry) o2).name);
    }

    public boolean equals(Object o)
    {
        return (o instanceof DomainEntry && o.getClass() == getClass() && id == ((DomainEntry) o).id) ||
            (o instanceof Term && entry instanceof Term && entry.equals(o));
    }

    public Object clone()
    {
        if (entry instanceof Term)
            return new DomainEntry(domain, ((Term) entry).clone());
        else
            return new DomainEntry(domain, entry);
    }

    public DomainEntry applyStringOperator(StringOperator operator)
    {
        if (entry instanceof Term)
            return new DomainEntry(domain, ((Term) entry).applyStringOperator(operator));
        else if (entry instanceof String)
            return new DomainEntry(domain, operator.transformString((String) entry));
        else
            return new DomainEntry(domain, entry);
    }

    /**
     * Get the XML {@link Element} representation of the domain
     */
    public Element getXMLRepresentation()
    {
        Element entryElement = new Element("entry");
        if (entry instanceof Term)
            entryElement.addContent(((Term) entry).getXMLRepresentation());
        else
            entryElement.addContent(entry.toString());
        return entryElement;
    }

    /**
     * Get the domain from an XML element
     * 
     * @param entryElement the XML {@link Element}
     * @param model the {@link Ontology}
     * @return the {@link DomainEntry}
     */
    public static DomainEntry getDomainEntryFromXML(Element entryElement, Ontology model)
    {
        Element termElement = entryElement.getChild("term");
        if (termElement != null) // the entry is a term
            return new DomainEntry(Term.getTermFromXML(termElement, model));
        else
            return new DomainEntry(entryElement.getText());
    }
}