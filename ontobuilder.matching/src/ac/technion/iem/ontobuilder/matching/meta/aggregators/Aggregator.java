package ac.technion.iem.ontobuilder.matching.meta.aggregators;

import java.io.Serializable;

/**
 * Interface of Aggregator classes. Each Aggregator calculates an output value from input values
 * (array of values in the Global case and mapping and match matrix in the Local case), according to
 * its type.
 * Extends {@link Serializable}
 */
public interface Aggregator extends Serializable
{
    /**
     * Get the Aggregator type
     * 
     * @return the Aggregator type
     */
    public String getAggregatorType();

    /**
     * Get the order key
     * 
     * @return the order key
     */
    public String getOrderKey();

}
