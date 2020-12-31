package ac.technion.iem.ontobuilder.matching.meta.aggregators;

/**
 * <p>Title: AbstractGlobalAggregator</p>
 * Extends {@link Aggregator}
 */
public abstract class AbstractGlobalAggregator implements Aggregator
{

    private static final long serialVersionUID = 1172774006873967969L;

    /**
     * Constructs a default AbstractGlobalAggregator
     */
    public AbstractGlobalAggregator()
    {
    }

    /**
     * Calculates a global value according to the Aggregator type
     * 
     * @param array of double values
     * @return the value
     */
    public abstract double calcArgValue(double[] params);

    /**
     * @return the Aggregator type
     */
    public String toString()
    {
        return getAggregatorType();
    }

}