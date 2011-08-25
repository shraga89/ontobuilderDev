package ac.technion.iem.ontobuilder.matching.meta.aggregators;

/**
 * <p>Title: SumGlobalAggregator</p>
 * Extends {@link AbstractGlobalAggregator}
 */
public class SumGlobalAggregator extends AbstractGlobalAggregator
{

    private static final long serialVersionUID = 3053141629264272387L;

    /**
     * Constructs a default SumGlobalAggregator
     */
    public SumGlobalAggregator()
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
     * Calculates a sum global value
     * 
     * @return the sum
     */
    public double calcArgValue(double[] params)
    {
        int n = params.length;
        double sum = 0;
        for (int i = 0; i < n; i++)
        {
            sum += params[i];
        }
        return sum;
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