package ac.technion.iem.ontobuilder.core.biztalk;

/**
 * <p>Title: Link</p>
 */
public class Link
{
    protected Long id;
    protected Double similarity;
    protected String from;
    protected String to;

    public Link()
    {
    }

    public Link(Long id, String from, String to)
    {
        this.id = id;
        this.from = from;
        this.to = to;
        this.similarity = null;
    }

    public Link(String from, String to, Double similarity)
    {
        this.id = null;
        this.from = from;
        this.to = to;
        this.similarity = similarity;
    }
    
    public Long getId()
    {
        return id;
    }

    public Double getSimilarity()
    {
        return similarity;
    }

    public String getFrom()
    {
        return from;
    }

    public String getTo()
    {
        return to;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public void setSimilarity(Double similarity)
    {
        this.similarity = similarity;
    }

    public void setFrom(String from)
    {
        this.from = from;
    }

    public void setTo(String to)
    {
        this.to = to;
    }
}
