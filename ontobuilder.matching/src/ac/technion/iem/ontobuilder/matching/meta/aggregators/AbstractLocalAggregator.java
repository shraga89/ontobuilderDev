package ac.technion.iem.ontobuilder.matching.meta.aggregators;

import ac.technion.iem.ontobuilder.matching.meta.match.AbstractMapping;
import ac.technion.iem.ontobuilder.matching.meta.match.AbstractMatchMatrix;



/**
 * <p>Title: AbstractLocalAggregator</p>
 * Extends {@link Aggregator}
 */
public abstract class AbstractLocalAggregator implements Aggregator
{

    private static final long serialVersionUID = -7952366611387457571L;

    /**
     * Constructs a default AbstractLocalAggregator
     */
    public AbstractLocalAggregator()
    {
    }

    /**
     * @return the Aggregator type
     */
    public String toString()
    {
        return getAggregatorType();
    }

    /**
     * Calculates a local value according to the Aggregator type
     * 
     * @param mapping a mapping result {@link AbstractMapping}
     * @param matrix a match matrix {@link AbstractMatchMatrix}
     * @return the value
     */
    public abstract double calcArgValue(AbstractMapping mapping, AbstractMatchMatrix matrix);

}