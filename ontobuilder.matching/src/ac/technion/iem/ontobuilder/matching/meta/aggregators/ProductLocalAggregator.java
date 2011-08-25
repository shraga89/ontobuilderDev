package ac.technion.iem.ontobuilder.matching.meta.aggregators;

import ac.technion.iem.ontobuilder.matching.meta.match.AbstractMapping;
import ac.technion.iem.ontobuilder.matching.meta.match.AbstractMatchMatrix;
import ac.technion.iem.ontobuilder.matching.meta.match.MatchedAttributePair;



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
     * @param mapping a mapping result {@link AbstractMapping}
     * @param matrix a match matrix {@link AbstractMatchMatrix}
     * 
     * @return the product
     */
    public double calcArgValue(AbstractMapping mapping, AbstractMatchMatrix matrix)
    {
        double score = 0;
        MatchedAttributePair pair;
        for (int i = 0; i < mapping.getMatchedAttributesPairsCount(); i++)
        {
            pair = mapping.getMatchedAttributePair(i);
            score *= matrix.getMatchConfidenceByAttributeNames(pair.getAttribute1(),
                pair.getAttribute2());
        }
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