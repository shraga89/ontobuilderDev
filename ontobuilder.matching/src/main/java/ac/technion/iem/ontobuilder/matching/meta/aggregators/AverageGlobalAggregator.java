package ac.technion.iem.ontobuilder.matching.meta.aggregators;

/**
 * <p>Title: AverageGlobalAggregator</p>
 * Extends {@link AbstractGlobalAggregator}
 */
public class AverageGlobalAggregator extends AbstractGlobalAggregator
{

    private static final long serialVersionUID = 3142195041777451840L;
    private double threshold = 0;

    /**
     * Constructs a default AverageGlobalAggregator
     */
    public AverageGlobalAggregator()
    {
    }

    /**
     * Constructs a AverageGlobalAggregator with a threshold
     * 
     * @param threshold
     */
    public AverageGlobalAggregator(double threshold)
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
     * Calculates an average global value.
     * 
     * @return the average if larger than the threshold, else returns 0
     */
    public double calcArgValue(double[] params)
    {
        int n = params.length;
        double sum = 0;
        for (int i = 0; i < n; i++)
        {
            sum += params[i];
        }
        return ((sum / n) >= threshold ? (sum / n) : 0);
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