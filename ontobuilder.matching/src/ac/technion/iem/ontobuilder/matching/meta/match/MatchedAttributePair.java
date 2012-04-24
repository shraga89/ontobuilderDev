package ac.technion.iem.ontobuilder.matching.meta.match;

import java.io.Serializable;

import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.BipartiteGraph;
import ac.technion.iem.ontobuilder.matching.utils.SchemaTranslator;

/**
 * <p>Title: MatchedAttributePair</p>
 * <p>Description: represents a two matched attributes pair</p>
 * Implements {@link Serializable}
 * 
 * @author Haggai Roitman
 * @version 1.1
 */
public class MatchedAttributePair implements Serializable
{

    private static final long serialVersionUID = 7000910839402395550L;

    public long id1 = -1;
    public long id2 = -1;
    /** attribute one */
    private String attribute1;
    /** attribute two */
    private String attribute2;
    /** the weight of the match between the attributes */
    private double weight = 0;
    /** holds matched pair hashCode */
    private int hashCode;

    public MatchedAttributePair(String candTerm, String targTerm, double w,long id1, long id2)
    {
        this.attribute1 = candTerm;
        this.attribute2 = targTerm;
        this.weight = w;
    	this.id1 = id1;
    	this.id2 = id2;
    	this.hashCode = new Long(this.id1 ^ this.id2).intValue();
    }

    @Override
	public String toString() {
		return " [Candidate:" + id1 + "," + attribute1 + " <-> " +
			   "  Target: " + id2 + "," + attribute2 + "];Weight:" + weight;
	}

	/**
     * return hashCode for attribute pairs
     */
    public int hashCode()
    {
        return hashCode;
    }

    /**
     * release resources
     */
    public void nullify()
    {
        attribute1 = null;
        attribute2 = null;
    }

    public long getId1() {
		return id1;
	}

	public void setId1(long id1) {
		this.id1 = id1;
	}

	public long getId2() {
		return id2;
	}

	public void setId2(long id2) {
		this.id2 = id2;
	}

	/**
     * Translates an input id into one of the attributes, if there is a match
     * 
     * @param toTranslate attribute id to translate
     * @return the attribute translation id or SchemaTranslator.NO_TRANSLATION if the translation is to a dummy vertex
     */
    public long getAttributeTranslation(long toTranslate)
    {
        // fix bug - haggai 12/12/03 - changed equalsIgnoreCase to equals
        if (this.id1 == toTranslate && !(this.id2 <= BipartiteGraph.ID_DUMMY_VERTEX))
            return this.id2;
        if (this.id2 == toTranslate && !(this.id1 <= BipartiteGraph.ID_DUMMY_VERTEX))
            return this.id1;
        return SchemaTranslator.NO_TRANSLATION;
    }

    /**
     * Get the matched pair weight
     * 
     * @return weight of the matched pair
     */
    public double getMatchedPairWeight()
    {
        return weight;
    }

    /**
     * Get Attribute1
     * 
     * @return Attribute1
     */
    public String getAttribute1()
    {
        return attribute1;
    }

    /**
     * Get Attribute2
     * 
     * @return Attribute2
     */
    public String getAttribute2()
    {
        return attribute2;
    }

    /**
     * Set Attribute1
     * 
     * @param attribute1
     */
    public void setAttribute1(String attribute1)
    {
        this.attribute1 = attribute1;
    }

    /**
     * Set Attribute2
     * 
     * @param attribute2
     */
    public void setAttribute2(String attribute2)
    {
        this.attribute2 = attribute2;
    }

    /**
     * Check if two matched attribute pairs exist
     */
    public boolean equals(Object o)
    {
        MatchedAttributePair p = (MatchedAttributePair) o;
        assert(p.id1 != -1);
        assert(p.id2 != -1);
        assert(this.id1 != -1);
        assert(this.id1 != -1);
        
        return ((p.id1 == this.id1 && p.id2 == this.id2) || (p.id1 == this.id2 && p.id2 == this.id1));
    }

}