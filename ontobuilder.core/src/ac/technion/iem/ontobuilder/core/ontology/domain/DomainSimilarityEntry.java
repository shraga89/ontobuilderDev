package ac.technion.iem.ontobuilder.core.ontology.domain;


/**
 * <p>Title: DomainSimilarityEntry</p>
 */
public class DomainSimilarityEntry
{
    protected String domain1;
    protected String domain2;
    protected double value;

    /**
     * Constructs a DomainSimilarityEntry
     *
     * @param domain1 the first domain
     * @param domain2 the second domain
     * @param value the similarity value
     */
    public DomainSimilarityEntry(String domain1, String domain2, double value)
    {
        this.domain1 = domain1;
        this.domain2 = domain2;
        this.value = value;
    }

    /**
     * Set the first domain
     *
     * @param domain1 the domain
     */
    public void setDomain1(String domain1)
    {
        this.domain1 = domain1;
    }

    /**
     * Get the first domain
     *
     * @return the first domain
     */
    public String getDomain1()
    {
        return domain1;
    }

    /**
     * Set the second domain
     *
     * @param domain2 the domain
     */
    public void setDomain2(String domain2)
    {
        this.domain2 = domain2;
    }

    /**
     * Get the second domain
     *
     * @return the second domain
     */
    public String getDomain2()
    {
        return domain2;
    }

    /**
     * Set the value
     *
     * @param value the value
     */
    public void setValue(double value)
    {
        this.value = value;
    }

    /**
     * Get the value
     *
     * @return the value
     */
    public double getValue()
    {
        return value;
    }

    public String toString()
    {
        return domain1 + " <-> " + domain2 + " = " + value;
    }

    public boolean equals(Object o)
    {
        if (!(o instanceof DomainSimilarityEntry))
            return false;

        DomainSimilarityEntry dse = (DomainSimilarityEntry) o;
        return (domain1.equals(dse.domain1) && domain2.equals(dse.domain2)) ||
            (domain1.equals(dse.domain2) && domain2.equals(dse.domain1));
    }
}