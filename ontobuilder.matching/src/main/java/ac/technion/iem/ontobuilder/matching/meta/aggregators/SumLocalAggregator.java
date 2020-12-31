package ac.technion.iem.ontobuilder.matching.meta.aggregators;

import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.meta.match.MatchMatrix;


/**
 * <p>Title: SumLocalAggregator</p>
 * Extends {@link AbstractLocalAggregator}
 */
public class SumLocalAggregator extends AbstractLocalAggregator
{

    private static final long serialVersionUID = -1026088837581028840L;

    /**
     * Constructs a default SumLocalAggregator
     */
    public SumLocalAggregator()
    {
    }

    /**
     * Get the Aggregator type
     */
    public String getAggregatorType()
    {
        return "Sum";
    }

    /**
     * Calculates the sum of the mapping's pairs values according to the match matrix provided
     * 
     * @param mapping a mapping result {@link AbstractMapping}
     * @param matrix a match matrix {@link MatchMatrix}
     * @return the sum
     */
    public double calcArgValue(MatchInformation mapping, MatchMatrix matrix)
    {// O(E)
        double score = 0;
        for (Match m : mapping.getCopyOfMatches())
            score += matrix.getMatchConfidence(m.getCandidateTerm(),m.getTargetTerm());
        return score;
    }

    /**
     * Get the order key
     * 
     * @return the order key
     */
    public String getOrderKey()
    {
        return "SUM";
    }
}