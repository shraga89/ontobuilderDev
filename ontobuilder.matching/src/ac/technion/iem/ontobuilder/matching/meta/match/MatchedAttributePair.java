package ac.technion.iem.ontobuilder.matching.meta.match;

import java.io.Serializable;

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

    /**
     * Constructs a MatchedAttributePair with two attributes and a weight
     */
    public MatchedAttributePair(String a1, String a2, double w)
    {
        this(a1, a2);
        weight = w;
    }

    /**
     * Constructs a MatchedAttributePair with two attributes
     */
    public MatchedAttributePair(String a1, String a2)
    {
        attribute1 = a1;
        attribute2 = a2;
        char[] a1Chars = a1.toCharArray();
        char[] a2Chars = a2.toCharArray();
        int a1Index = 0;
        int a2Index = 0;
        // creating a unique hashCode by mixing the chars of the attributes
        char[] mix = new char[a1Chars.length + a2Chars.length];
        for (int i = 0; i < mix.length; i++)
        {
            if (i % 2 == 0 && a1Index < a1Chars.length)
                mix[i] = a1Chars[a1Index++];
            else if (i % 2 == 1 && a2Index < a2Chars.length)
                mix[i] = a2Chars[a2Index++];
            else if (a1Chars.length > a2Chars.length)
                mix[i] = a1Chars[a1Index++];
            else
                mix[i] = a2Chars[a2Index++];
        }
        String mixedString = new String(mix);
        hashCode = mixedString.hashCode();
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

    /**
     * Translates an input string into one of the attributes, if there is a match
     * 
     * @param toTranslate attribute to translate
     * @return the attribute translation or "NO translation" if the translation equals "DummyVertex"
     */
    public String getAttributeTranslation(String toTranslate)
    {
        // fix bug - haggai 12/12/03 - changed equalsIgnoreCase to equals
        if (attribute1.equals(toTranslate) && !attribute2.equalsIgnoreCase("DummyVertex"))
            return attribute2;
        if (attribute2.equals(toTranslate) && !attribute1.equalsIgnoreCase("DummyVertex"))
            return attribute1;
        return "No Translation";
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
        boolean idEqual = true;
        if (p.id1 != -1 && p.id2 != -1 && this.id1 != -1 && this.id2 != -1)
        {
            idEqual = ((p.id1 == this.id1 && p.id2 == this.id2) || (p.id1 == this.id2 && p.id2 == this.id1));
        }
        if (idEqual)
            return true;
        else
            return (this.attribute1.equals(p.attribute1) && this.attribute2.equals(p.attribute2) || this.attribute2
                .equals(p.attribute1) && this.attribute1.equals(p.attribute2));
    }

}