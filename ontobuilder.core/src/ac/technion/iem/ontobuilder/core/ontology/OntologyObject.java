package ac.technion.iem.ontobuilder.core.ontology;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Random;

/**
 * <p>Title: OntologyFileFilter</p>
 * Implements {@link Comparator}, {@link Cloneable} and {@link Serializable}
 */
public abstract class OntologyObject implements Comparator<Object>,
    Cloneable, Serializable
{
    private static final long serialVersionUID = 8450155672378626997L;

    private static Random idGenerator = new Random();

    protected String name;
    protected long id = -1;

    /**
     * Constructs a default OntologyObject
     */
    public OntologyObject()
    {
        if (id == -1)
            id = Math.abs(idGenerator.nextLong());
    }

    /**
     * Constructs a OntologyObject
     * 
     * @param name the OntologyObject name
     */
    public OntologyObject(String name)
    {
        this();
        this.name = name;
    }

    /**
     * Get the Id
     * 
     * @return the Id
     */
    public long getId()
    {
        return id;
    }

    /**
     * Set the Id
     * 
     * @param id the Id
     */
    public void setId(long id)
    {
        this.id = id;
    }

    /**
     * Get the name
     * 
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Set the name
     * 
     * @param name the name
     */
    public void setName(String name)
    {
        this.name = name;
    }

    public String toString()
    {
        return name;
    }

    /**
     * Clone the object
     * 
     * @return null always
     */
    public Object clone()
    {
        return null;
    }
}