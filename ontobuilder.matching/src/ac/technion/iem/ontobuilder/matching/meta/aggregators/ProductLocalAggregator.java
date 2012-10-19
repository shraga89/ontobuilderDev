package ac.technion.iem.ontobuilder.matching.meta.aggregators;

import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.meta.match.MatchMatrix;



/**
 * <p>Title: ProductLocalAggregator</p>
 * Extends {@link AbstractLocalAggregator}
 */
public class ProductLocalAggregator extends AbstractLocalAggregator
{

    private static final long serialVersionUID = 1777306889945048131L;

    /**
     * Constructs a default ProductLocalAggregator
     */
    public ProductLocalAggregator()
    {
    }

    /**
     * Get the Aggregator type
     */
    public String getAggregatorType()
    {
        return "Product";
    }

    /**
     * Calculates the product of the mapping's pairs values according to the match matrix provided
     * @param mapping a mapping result {@link MatchInformation}
     * @param matrix a match matrix {@link MatchMatrix}
     * 
     * @return the product
     */
    public double calcArgValue(MatchInformation mapping, MatchMatrix matrix)
    {
        double score = 0;
        for (Match m : mapping.getCopyOfMatches())
            score *= m.getEffectiveness();
        return score;
    }

    /**
     * Get the order key
     * 
     * @return the order key
     */
    public String getOrderKey()
    {
        return "PROD";
    }
}