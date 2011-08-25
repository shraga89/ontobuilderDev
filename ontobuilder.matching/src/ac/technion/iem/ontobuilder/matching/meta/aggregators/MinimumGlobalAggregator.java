package ac.technion.iem.ontobuilder.matching.meta.aggregators;

/**
 * <p>Title: MinimumGlobalAggregator</p>
 * Extends {@link AbstractGlobalAggregator}
 */
public class MinimumGlobalAggregator extends AbstractGlobalAggregator
{

    private static final long serialVersionUID = 6038610164492190396L;

    /**
     * Constructs a default MinimumGlobalAggregator
     */
    public MinimumGlobalAggregator()
    {
    }

    /**
     * Get the Aggregator type
     */
    public String getAggregatorType()
    {
        return "Min";
    }

    /**
     * Calculates a minimum global value.
     * 
     * @return the minimum
     */
    public double calcArgValue(double[] params)
    {
        if (params.length == 0) // precondition params > 0
            throw new IllegalArgumentException("params should be > 0");
        int n = params.length;
        double min = params[0];
        for (int i = 1; i < n; i++)
        {
            min = min < params[i] ? min : params[i];
        }
        return min;
    }

    /**
     * Get the order key
     * 
     * @return the order key
     */
    public String getOrderKey()
    {
        return "MIN";
    }
}