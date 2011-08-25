package ac.technion.iem.ontobuilder.core.ontology;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.jdom.Element;

import ac.technion.iem.ontobuilder.core.ontology.domain.GuessedDomain;
import ac.technion.iem.ontobuilder.core.ontology.operator.StringOperator;
import ac.technion.iem.ontobuilder.core.utils.properties.PropertiesHandler;

/**
 * <p>Title: Domain</p>
 * Extends {@link OntologyObject}
 */
public class Domain extends OntologyObject
{
    private static final long serialVersionUID = 1L;

    public static String[] predefinedDomains =
    {
        "ontology.domain.text", "ontology.domain.number", "ontology.domain.boolean",
        "ontology.domain.date", "ontology.domain.time", "ontology.domain.float",
        "ontology.domain.integer", "ontology.domain.pinteger", "ontology.domain.ninteger",
        "ontology.domain.choice", "ontology.domain.url", "ontology.domain.email"
    };

    protected ArrayList<DomainEntry> entries;
    protected Attribute attribute;
    protected OntologyClass ontologyClass;
    protected String type;

    /**
     * Constructs a default Domain
     */
    public Domain()
    {
        name = "";
        type = "";
        entries = new ArrayList<DomainEntry>();
    }

    /**
     * Constructs a Domain
     *
     * @param name the domain name
     */
    public Domain(String name)
    {
        this();
        this.name = (name == null ? "" : name);
    }

    /**
     * Constructs a Domain
     *
     * @param name the domain name
     * @param type the domain type
     */
    public Domain(String name, String type)
    {
        this(name);
        this.type = (type == null ? "" : type);
    }

    /**
     * Clears the domain
     */
    public void clear()
    {
        entries.clear();
        type = "";
        name = "";
    }

    /**
     * Sets the domain name
     * 
     * @param name the name
     */
    public void setName(String name)
    {
        super.setName(name == null ? "" : name);
        if (ontologyClass != null && ontologyClass.getOntology() != null)
            ontologyClass.getOntology().fireObjectChangedEvent(this);
        else if (attribute != null && attribute.getOntologyClass() != null &&
            attribute.getOntologyClass().getOntology() != null)
            attribute.getOntologyClass().getOntology().fireObjectChangedEvent(this);
    }

    /**
     * Sets the domain type
     * 
     * @param type the type
     */
    public void setType(String type)
    {
        this.type = (type == null ? "" : type);
    }

    /**
     * Gets the domain type
     * 
     * @return the type
     */
    public String getType()
    {
        if (type == null || type.length() == 0)
        {
            for (int i = 0; i < predefinedDomains.length; i++)
            {
                String d = predefinedDomains[i];
                if (name.equalsIgnoreCase(PropertiesHandler.getResourceString(d)))
                    return d.substring(d.lastIndexOf(".") + 1);
            }
            return type;
        }
        else
            return type;
    }

    /**
     * Gets the ontologyClass
     * 
     * @return the {@link OntologyClass}
     */
    public OntologyClass getOntologyClass()
    {
        return ontologyClass;
    }

    /**
     * Sets the ontologyClass
     * 
     * @param ontologyClass the {@link OntologyClass}
     */
    public void setOntologyClass(OntologyClass ontologyClass)
    {
        this.ontologyClass = ontologyClass;
    }

    /**
     * Gets the attribute
     * 
     * @return the {@link Attribute}
     */
    public Attribute getAttribute()
    {
        return attribute;
    }

    /**
     * Sets the attribute
     * 
     * @param attribute the {@link Attribute}
     */
    public void setAttribute(Attribute attribute)
    {
        this.attribute = attribute;
    }

    /**
     * Add an entry to the domain 
     *
     * @param entry the {@link DomainEntry} to add
     */
    public void addEntry(DomainEntry entry)
    {
        if (entry == null)
            return;
        if (!entries.contains(entry))
        {
            entries.add(entry);
            entry.setDomain(this);
            if (ontologyClass != null && ontologyClass.getOntology() != null)
                ontologyClass.getOntology().fireDomaiEntryAddedEvent(this, entry);
            else if (attribute != null && attribute.getOntologyClass() != null &&
                attribute.getOntologyClass().getOntology() != null)
                attribute.getOntologyClass().getOntology().fireDomaiEntryAddedEvent(this, entry);
        }
    }

    /**
     * Remove an entry from the domain 
     *
     * @param entry the {@link DomainEntry} to remove
     */
    public void removeEntry(DomainEntry entry)
    {
        if (entries.contains(entry))
        {
            entries.remove(entry);
            if (ontologyClass != null && ontologyClass.getOntology() != null)
                ontologyClass.getOntology().fireDomaiEntryDeletedEvent(this, entry);
            else if (attribute != null && attribute.getOntologyClass() != null &&
                attribute.getOntologyClass().getOntology() != null)
                attribute.getOntologyClass().getOntology().fireDomaiEntryDeletedEvent(this, entry);
        }
    }

    /**
     * Get the number of entries
     *
     * @return the number of entries
     */
    public int getEntriesCount()
    {
        return entries.size();
    }
    
    /**
     * Get the entries
     */
    public ArrayList<DomainEntry> getEntries()
    {
        return entries;
    }

    /**
     * Get the entry at a specific index
     *
     * @param index the index
     * @return the {@link DomainEntry}
     */
    public DomainEntry getEntryAt(int index)
    {
        if (index < 0 || index >= entries.size())
            return null;
        return (DomainEntry) entries.get(index);
    }

    /**
     * Checks if an entry is valid
     *
     * @param entry the entry
     * @return <code>true</code> if is valid
     */
    public boolean isValidEntry(Object entry)
    {
        for (Iterator<DomainEntry> i = entries.iterator(); i.hasNext();)
        {
            DomainEntry de = (DomainEntry) i.next();
            Object o = de.getEntry();
            if ((o instanceof Term) && (entry instanceof Term))
            {
                if (((Term) o).getName().equals(((Term) entry).getName()))
                    return true;
            }
            else
            {
                if (o.equals(entry))
                    return true;
            }
        }
        return false;
    }

    public String toString()
    {
        return PropertiesHandler.getResourceString("ontology.domain") +
            (name != null && name.length() > 0 ? " (" + name + ")" : "");
    }

    public int compare(Object o1, Object o2)
    {
        return ((Domain) o1).name.compareTo(((Domain) o2).name);
    }

    public boolean equals(Object o)
    {
        if (!(o instanceof Domain) || o.getClass() != getClass() || id != ((Domain) o).id)
            return false;
        Domain d = (Domain) o;
        if (entries.size() != d.entries.size())
            return false;
        for (int i = 0; i < entries.size(); i++)
            if (!entries.get(i).equals(d.entries.get(i)))
                return false;
        return true;
    }

    /**
     * Clone the domain
     */
    public Object clone()
    {
        Domain domain = new Domain(new String(name), new String(type));
        domain.entries.ensureCapacity(entries.size());
        for (Iterator<DomainEntry> i = entries.iterator(); i.hasNext();)
        {
            DomainEntry de = (DomainEntry) i.next();
            DomainEntry dec = (DomainEntry) de.clone();
            dec.setDomain(domain);
            domain.entries.add(dec);
        }
        return domain;
    }

    public Domain applyStringOperator(StringOperator operator)
    {
        Domain domain = new Domain(name == null ? "" : operator.transformString(name));
        domain.entries.ensureCapacity(entries.size());
        for (Iterator<DomainEntry> i = entries.iterator(); i.hasNext();)
        {
            DomainEntry de = ((DomainEntry) i.next()).applyStringOperator(operator);
            de.setDomain(domain);
            domain.entries.add(de);
        }
        return domain;
    }

    /**
     * Get the predefined domains
     *
     * @return a list of values
     */
    public static Vector<String> getPredefinedDomains()
    {
        Vector<String> v = new Vector<String>();
        for (int i = 0; i < predefinedDomains.length; i++)
            v.add(PropertiesHandler.getResourceString(predefinedDomains[i]));
        return v;
    }

    /**
     * Get the XML representation of the domain
     * @return {@link Element}
     */
    public Element getXMLRepresentation()
    {
        Element domainElement = new Element("domain");
        if (name != null && name.trim().length() > 0)
            domainElement.setAttribute(new org.jdom.Attribute("name", name));
        if (type != null && type.trim().length() > 0)
            domainElement.setAttribute(new org.jdom.Attribute("type", type));
        else
            domainElement.setAttribute(new org.jdom.Attribute("type", getType()));
        for (Iterator<DomainEntry> i = entries.iterator(); i.hasNext();)
            domainElement.addContent(((DomainEntry) i.next()).getXMLRepresentation());
        return domainElement;
    }

    /**
     * Get the domain from an XML element
     *
     * @param domainElement the XML {@link Element}
     * @param model the {@link Ontology}
     * @return the {@link Domain}
     */
    public static Domain getDomainFromXML(Element domainElement, Ontology model)
    {
        Domain domain = new Domain(domainElement.getAttributeValue("name"));
        domain.setType(domainElement.getAttributeValue("type"));
        java.util.List<?> entryElements = domainElement.getChildren("entry");
        for (Iterator<?> i = entryElements.iterator(); i.hasNext();)
        {
            DomainEntry de = DomainEntry.getDomainEntryFromXML((Element) i.next(), model);
            de.setDomain(domain);
            domain.entries.add(de);
        }
        return domain;
    }

    /**
     * Guess the domain
     *
     * @return a string with the domain values
     */
    public String guessDomain()
    {
        String guessedDomain = null;

        if (ontologyClass.isInstanceOf("select") || ontologyClass.isInstanceOf("radio"))
        {
            Hashtable<String, Integer> domains = new Hashtable<String, Integer>();

            for (Iterator<DomainEntry> i = entries.iterator(); i.hasNext();)
            {
                DomainEntry de = (DomainEntry) i.next();
                if (!(de.getEntry() instanceof Term))
                    continue;
                Term term = (Term) de.getEntry();
                GuessedDomain gd = GuessedDomain.guessDomain(term.getName());
                if (gd != null)
                {
                    Object o = domains.get(gd.getDomainType());
                    domains.put(gd.getDomainType(), o == null ? new Integer(1) : new Integer(
                        ((Integer) o).intValue() + 1));
                }
            }

            int count = 0;
            for (Enumeration<String> e = domains.keys(); e.hasMoreElements();)
            {
                String tempDomain = (String) e.nextElement();
                int tempCount = ((Integer) domains.get(tempDomain)).intValue();
                if (tempCount > count)
                {
                    count = tempCount;
                    guessedDomain = tempDomain;
                }
            }

            if (count < entries.size() / 2)
                guessedDomain = null;
        }
        else if (ontologyClass.isInstanceOf("text") || ontologyClass.isInstanceOf("password") ||
            ontologyClass.isInstanceOf("file") || ontologyClass.isInstanceOf("textarea"))
            guessedDomain = "text";
        else if (ontologyClass.isInstanceOf("checkbox"))
            guessedDomain = "boolean";

        if (guessedDomain != null)
        {
            name = PropertiesHandler.getResourceString("ontology.domain." + guessedDomain);
            type = guessedDomain;
            return guessedDomain;
        }
        else
            return null;
    }
}