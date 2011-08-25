package ac.technion.iem.ontobuilder.matching.meta.aggregators;

import ac.technion.iem.ontobuilder.matching.meta.match.AbstractMapping;
import ac.technion.iem.ontobuilder.matching.meta.match.AbstractMatchMatrix;
import ac.technion.iem.ontobuilder.matching.meta.match.MatchedAttributePair;



/**
 * <p>Title: AverageLocalAggregator</p>
 * Extends {@link AbstractLocalAggregator}
 */
public class AverageLocalAggregator extends AbstractLocalAggregator
{

    private static final long serialVersionUID = 2677730176465448187L;
    private double threshold = 0;

    /**
     * Constructs a default AverageLocalAggregator
     */
    public AverageLocalAggregator()
    {
    }

    /**
     * Constructs a AverageLocalAggregator with a threshold
     * 
     * @param threshold
     */
    public AverageLocalAggregator(double threshold)
    {
        this.threshold = threshold;
    }

    /**
     * Get the Aggregator type, including the threshold
     */
    public String getAggregatorType()
    {
        return "Average<threshold:" + threshold + ">";
    }

    /**
     * Calculates the average of the mapping's pairs values according to the match matrix provided
     * 
     * @param mapping a mapping result {@link AbstractMapping}
     * @param matrix a match matrix {@link AbstractMatchMatrix}
     * @return the average if larger than the threshold, else returns 0
     */
    public double calcArgValue(AbstractMapping mapping, AbstractMatchMatrix matrix)
    {// O(E)
        double score = 0;
        MatchedAttributePair pair;
        for (int i = 0; i < mapping.getMatchedAttributesPairsCount(); i++)
        {
            pair = mapping.getMatchedAttributePair(i);
            score += matrix.getMatchConfidenceByAttributeNames(pair.getAttribute1(),
                pair.getAttribute2());
        }

        int candMatrixNumAttributes = matrix.getCandidateAttributeNames().length;
        return ((score / candMatrixNumAttributes) >= threshold ? (score / candMatrixNumAttributes) : 0);
    }

    /**
     * Set the threshold
     * 
     * @param threshold - threshold to set
     */
    public void setThreshold(double threshold)
    {
        this.threshold = threshold;
    }

    /**
     * Get the threshold
     * 
     * @return the threshold
     */
    public double getThreshold()
    {
        return threshold;
    }

    /**
     * Get the order key
     * 
     * @return the order key
     */
    public String getOrderKey()
    {
        return "AVG";// +(threshold > 0 ? "_THRESHOLD" : "");
    }
}