package ac.technion.iem.ontobuilder.matching.meta.match;

/**
 * <p>Title: AbstractMapping</p>
 * <p>Description: Abstract class to hold a mapping between 2 schemas</p>
 * Implements {@link Comparable}
 */
public abstract class AbstractMapping implements Comparable<Object>
{

    /** local scores of the mapping */
    protected double[] localScores;
    /** global score of the mapping */
    protected double globalScore = -1;
    protected boolean newMapping = true;
    /** the associated matched pair of the translator */
    protected MatchedAttributePair[] schemaPairs;

    /**
     * Constructs a default AbstractMapping.
     */
    protected AbstractMapping()
    {
    }

    /**
     * Constructs a AbstractMapping with an array of MatchedAttributePair
     * 
     * @param schemaPairs an array of {@link MatchedAttributePair}
     */
    public AbstractMapping(MatchedAttributePair[] schemaPairs)
    {
        this.schemaPairs = schemaPairs;
    }

    /**
     * Sets a flag whether the mapping is new.
     * 
     * @param newMapping <code>true</code> if the mapping is new
     */
    public void setNewMapping(boolean newMapping)
    {
        this.newMapping = newMapping;
    }

    /**
     * Gets the flag whether this mapping is new.
     * 
     * @return <code>true</code> if the mapping is new
     */
    public boolean isNewMapping()
    {
        return newMapping;
    }

    /**
     * Set the local matching score for each pair in the mapping
     * 
     * @param localScores - the local scores
     */
    public void setLocalScores(double[] localScores)
    {
        this.localScores = localScores;
    }

    /**
     * Get the local matching score for each pair in the mapping
     * 
     * @return the local scores array
     */
    public double[] getLocalScores()
    {
        return localScores;
    }

    /**
     * Set a global score for the mapping
     * 
     * @param globalScore - the global score
     */
    public void setGlobalScore(double globalScore)
    {
        this.globalScore = globalScore;
    }

    /**
     * Get the global score for the mapping
     * 
     * @return the global score
     */
    public double getGlobalScore()
    {
        return globalScore;
    }

    /**
     * Get the number of matched attribute pairs
     * 
     * @return
     */
    public int getMatchedAttributesPairsCount()
    {
        return schemaPairs.length;
    }

    /**
     * Compares two mappings and returns a higher rank for the mapping with a higher global score
     */
    public int compareTo(Object o)
    {
        AbstractMapping m = (AbstractMapping) o;
        if (m.getGlobalScore() > this.getGlobalScore())
            return -1;
        else if (m.getGlobalScore() < this.getGlobalScore())
            return 1;
        else
            return 0;
    }

    /**
     * Get the matched attribute pairs in the mapping
     * 
     * @return the {@link MatchedAttributePair} array
     */
    public MatchedAttributePair[] getMatchedPairs()
    {
        return schemaPairs;
    }

    /**
     * Get the i-th matched attribute pair
     * 
     * @return i-th {@link MatchedAttributePair}
     */
    public MatchedAttributePair getMatchedAttributePair(int i)
    {
        if (schemaPairs == null || i < 0 || i > schemaPairs.length)
            throw new IllegalArgumentException();
        return schemaPairs[i];
    }

    /**
     * Reset the mapping
     */
    public abstract void nullify();

    public abstract int hashCode();
}