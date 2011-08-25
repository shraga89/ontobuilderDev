package ac.technion.iem.ontobuilder.matching.algorithms.line2.meta;

import java.util.ArrayList;
import java.util.Vector;

import ac.technion.iem.ontobuilder.matching.meta.aggregators.AbstractGlobalAggregator;
import ac.technion.iem.ontobuilder.matching.meta.aggregators.AbstractLocalAggregator;
import ac.technion.iem.ontobuilder.matching.meta.aggregators.Aggregator;
import ac.technion.iem.ontobuilder.matching.meta.aggregators.AverageGlobalAggregator;
import ac.technion.iem.ontobuilder.matching.meta.aggregators.AverageLocalAggregator;
import ac.technion.iem.ontobuilder.matching.meta.aggregators.GlobalAggregatorTypesEnum;
import ac.technion.iem.ontobuilder.matching.meta.aggregators.LocalAggregatorTypesEnum;
import ac.technion.iem.ontobuilder.matching.meta.aggregators.MinimumGlobalAggregator;
import ac.technion.iem.ontobuilder.matching.meta.aggregators.ProductLocalAggregator;
import ac.technion.iem.ontobuilder.matching.meta.aggregators.SumGlobalAggregator;
import ac.technion.iem.ontobuilder.matching.meta.aggregators.SumLocalAggregator;

/**
 * <p>Title: MetaAlgorithmUtilities</p>
 * <p>Description: Internal Meta Algorithm Utilities</p>
 */
public class MetaAlgorithmUtilities
{

    /**
     * Load aggregators
     * 
     * @param isLocal <code>true</code> if local aggregators need to be loaded
     * @param name the name of the aggregator
     * @return an {@link Aggregator}
     * @throws MetaAlgorithmsException if there's no such local aggregator
     */
    public static Aggregator loadAggregator(boolean isLocal, String name)
        throws MetaAlgorithmsException
    {
        if (isLocal)
        {
            if (name.equals(LocalAggregatorTypesEnum.SUM.getName()))
            {
                return new SumLocalAggregator();
            }
            else if (name.equals(LocalAggregatorTypesEnum.AVERAGE.getName()))
            {
                return new AverageLocalAggregator();
            }
            else if (name.equals(LocalAggregatorTypesEnum.PRODUCT.getName()))
            {
                return new ProductLocalAggregator();
            }
            else
            {
                throw new MetaAlgorithmsException("No such local aggregator!");
            }
        }// global
        else
        {
            if (name.equals(GlobalAggregatorTypesEnum.SUM.getName()))
            {
                return new SumGlobalAggregator();
            }
            else if (name.equals(GlobalAggregatorTypesEnum.AVERAGE.getName()))
            {
                return new AverageGlobalAggregator();
            }
            else if (name.equals(GlobalAggregatorTypesEnum.MIN.getName()))
            {
                return new MinimumGlobalAggregator();
            }
            else
            {
                throw new MetaAlgorithmsException("No such global aggregator!");
            }
        }
    }

    /**
     * Load the local aggregators
     * 
     * @return list of {@link AbstractLocalAggregator}
     */
    public static Vector<AbstractLocalAggregator> loadAllLocalAggregators()
    {
        Vector<AbstractLocalAggregator> aggs = new Vector<AbstractLocalAggregator>();
        aggs.add(new SumLocalAggregator());
        aggs.add(new AverageLocalAggregator());
        aggs.add(new ProductLocalAggregator());
        return aggs;
    }

    /**
     * Load the global aggregators
     * 
     * @return list of {@link AbstractGlobalAggregator}
     */
    public static ArrayList<AbstractGlobalAggregator> loadAllGlobalAggregators()
    {
        ArrayList<AbstractGlobalAggregator> aggs = new ArrayList<AbstractGlobalAggregator>();
        aggs.add(new SumGlobalAggregator());
        aggs.add(new AverageGlobalAggregator());
        aggs.add(new MinimumGlobalAggregator());
        return aggs;
    }

    /**
     * Get the meta algorithm names
     * 
     * @return a list with the names
     */
    public static ArrayList<String> getMetaAlgorithmNames()
    {
        ArrayList<String> algs = new ArrayList<String>();
        algs.add(MetaAlgorithmNamesEnum.THERSHOLD_ALGORITHM.getName());
        algs.add(MetaAlgorithmNamesEnum.MATRIX_DIRECT_ALGORITHM.getName());
        algs.add(MetaAlgorithmNamesEnum.MATRIX_DIRECT_WITH_BOUNDING_ALGORITHM.getName());
        algs.add(MetaAlgorithmNamesEnum.HYBRID_ALGORITHM.getName());
        return algs;
    }

}
