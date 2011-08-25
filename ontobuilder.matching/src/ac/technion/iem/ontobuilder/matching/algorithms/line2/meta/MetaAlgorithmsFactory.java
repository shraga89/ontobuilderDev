package ac.technion.iem.ontobuilder.matching.algorithms.line2.meta;

import ac.technion.iem.ontobuilder.matching.algorithms.line2.misc.CrossThresholdAlgorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.misc.MatrixDirectAlgorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.misc.MatrixDirectWithBoundingAlgorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.misc.SMThersholdAlgorithm;
import ac.technion.iem.ontobuilder.matching.meta.aggregators.AbstractGlobalAggregator;
import ac.technion.iem.ontobuilder.matching.meta.aggregators.AbstractLocalAggregator;
import ac.technion.iem.ontobuilder.matching.meta.match.AbstractMatchMatrix;

// Singleton pattern
/**
 * <p>Title: MetaAlgorithmsFactory</p>
 * <p>Description: Creates the MetaAlgorithm instance according to the correct type</p>
 */
public final class MetaAlgorithmsFactory
{

    private static MetaAlgorithmsFactory theInstance = new MetaAlgorithmsFactory();

    public static MetaAlgorithmsFactory getInstance()
    {
        return theInstance;
    }

    private MetaAlgorithmsFactory()
    {
    }

    /**
     * Get the MetaAlgorithm instance
     * 
     * @param algorithmName the algorithm name
     * @param params the parameters
     * @return the {@link AbstractMetaAlgorithm} instance
     * @throws MetaAlgorithmsFactoryException if there's not such algorithm
     */
    public synchronized AbstractMetaAlgorithm buildMetaAlgorithm(MetaAlgorithmNamesEnum algorithmName, Object[] params)
        throws MetaAlgorithmsFactoryException
    {

        switch (algorithmName)
        {
        case MATRIX_DIRECT_ALGORITHM:
            return buildMatrixDirectAlgorithm(params);
        case THERSHOLD_ALGORITHM:
            return buildSMThersholdAlgorithm(params);
        case MATRIX_DIRECT_WITH_BOUNDING_ALGORITHM:
            return (MatrixDirectWithBoundingAlgorithm) buildMatrixDirectWithBoundingAlgorithm(
                params, false);
        case HYBRID_ALGORITHM:
            return (CrossThresholdAlgorithm) buildMatrixDirectWithBoundingAlgorithm(params, true);
        default:
            throw new MetaAlgorithmsFactoryException("no such meta algorithm");
        }

    }

    /**
     * Parse the parameters and create a new instance of MatrixDirectAlgorithm
     * 
     * @param params the parameters
     * @return the {@link MatrixDirectAlgorithm} instance
     * @throws MetaAlgorithmsFactoryException when there are illegal parameters for the Matrix Direct Algorithm
     */
    private MatrixDirectAlgorithm buildMatrixDirectAlgorithm(Object[] params)
        throws MetaAlgorithmsFactoryException
    {
        String errorMsg = "illegal params for Matrix Direct Algorithm\n"
            + "expected: (k:Integer,globalArg:AbstractGlobalAggregator,localArg:AbstractLocalAggregator,combinedMatrix:AbstractMatchMatrix)";
        if (params == null || params.length != 4)
            throw new MetaAlgorithmsFactoryException(errorMsg);
        for (int i = 0; i < 4; i++)
        {
            if (params[i] == null)
                throw new MetaAlgorithmsFactoryException(errorMsg);
        }
        boolean foundK = false, foundGAggr = false, foundLAggr = false, foundCombinedMatrix = false;
        int k = 0;
        AbstractGlobalAggregator globalArg = null;
        AbstractLocalAggregator localArg = null;
        AbstractMatchMatrix combinedMatrix = null;
        for (int i = 0; i < 4; i++)
        {
            if (params[i] instanceof Integer)
            {
                foundK = true;
                k = ((Integer) params[i]).intValue();
                continue;
            }
            else if (params[i] instanceof AbstractGlobalAggregator)
            {
                foundGAggr = true;
                globalArg = (AbstractGlobalAggregator) params[i];
                continue;
            }
            else if (params[i] instanceof AbstractLocalAggregator)
            {
                foundLAggr = true;
                localArg = (AbstractLocalAggregator) params[i];
                continue;
            }
            else if (params[i] instanceof AbstractMatchMatrix)
            {
                foundCombinedMatrix = true;
                combinedMatrix = (AbstractMatchMatrix) params[i];
                continue;
            }
        }
        if (!foundK || !foundGAggr || !foundLAggr || !foundCombinedMatrix)
            throw new MetaAlgorithmsFactoryException(errorMsg);
        return new MatrixDirectAlgorithm(k, globalArg, localArg, combinedMatrix);
    }

    /**
     * Parse the parameters and create a new instance of (hybrid) CrossThresholdAlgorithm or
     * MatrixDirectWithBoundingAlgorithm
     * 
     * @param params the parameters
     * @param hybrid flag to create a hybrid MetaAlgorithm
     * @return the {@link AbstractMetaAlgorithm} instance
     * @throws MetaAlgorithmsFactoryException when there are illegal parameters for the Algorithm
     */
    private AbstractMetaAlgorithm buildMatrixDirectWithBoundingAlgorithm(Object[] params,
        boolean hybrid) throws MetaAlgorithmsFactoryException
    {
        String errorMsg = "illegal params for " +
            (hybrid ? "Hybrid Algorithm" : "Matrix Direct Algorithm\n") +
            "expected: (k:Integer,fGlobalArg:AbstractGlobalAggregator,fLocalArg:AbstractLocalAggregator,hGlobalArg:AbstractGlobalAggregator,hLocalArg:AbstractLocalAggregator,combinedMatrix:AbstractMatchMatrix)";
        if (params == null || params.length != 6)
            throw new MetaAlgorithmsFactoryException(errorMsg);
        for (int i = 0; i < 6; i++)
        {
            if (params[i] == null)
                throw new MetaAlgorithmsFactoryException(errorMsg);
        }
        boolean foundK = false, foundfGAggr = false, foundfLAggr = false, foundhGAggr = false, foundhLAggr = false, foundCombinedMatrix = false;
        int k = 0;
        AbstractGlobalAggregator fGlobalArg = null;
        AbstractLocalAggregator fLocalArg = null;
        AbstractGlobalAggregator hGlobalArg = null;
        AbstractLocalAggregator hLocalArg = null;
        AbstractMatchMatrix combinedMatrix = null;
        for (int i = 0; i < 6; i++)
        {
            if (params[i] instanceof Integer)
            {
                foundK = true;
                k = ((Integer) params[i]).intValue();
                continue;
            }
            else if (params[i] instanceof AbstractGlobalAggregator)
            {
                if (!foundfGAggr)
                {
                    foundfGAggr = true;
                    fGlobalArg = (AbstractGlobalAggregator) params[i];
                }
                else
                {
                    foundhGAggr = true;
                    hGlobalArg = (AbstractGlobalAggregator) params[i];
                }
                continue;
            }
            else if (params[i] instanceof AbstractLocalAggregator)
            {
                if (!foundfLAggr)
                {
                    foundfLAggr = true;
                    fLocalArg = (AbstractLocalAggregator) params[i];
                }
                else
                {
                    foundhLAggr = true;
                    hLocalArg = (AbstractLocalAggregator) params[i];
                }
                continue;
            }
            else if (params[i] instanceof AbstractMatchMatrix)
            {
                foundCombinedMatrix = true;
                combinedMatrix = (AbstractMatchMatrix) params[i];
                continue;
            }
        }
        if (!foundK || !foundfGAggr || !foundfLAggr || !foundhGAggr || !foundhLAggr ||
            !foundCombinedMatrix)
            throw new MetaAlgorithmsFactoryException(errorMsg);
        if (!hybrid)
            return new MatrixDirectWithBoundingAlgorithm(k, fGlobalArg, fLocalArg, hGlobalArg,
                hLocalArg, combinedMatrix);
        else
            return new CrossThresholdAlgorithm(k, fGlobalArg, fLocalArg, hGlobalArg, hLocalArg,
                combinedMatrix);
    }

    /**
     * Parse the parameters and create a new instance of SMThersholdAlgorithm
     * 
     * @param params the parameters
     * @return the {@link SMThersholdAlgorithm} instance
     * @throws MetaAlgorithmsFactoryException when there are illegal parameters for the Thershold Algorithm
     */
    private SMThersholdAlgorithm buildSMThersholdAlgorithm(Object[] params)
        throws MetaAlgorithmsFactoryException
    {
        String errorMsg = "illegal params for Thershold Algorithm\n"
            + "expected: (k:Integer,globalArg:AbstractGlobalAggregator,localArg:AbstractLocalAggregator)";
        if (params == null || params.length != 3)
            throw new MetaAlgorithmsFactoryException(errorMsg);
        for (int i = 0; i < 3; i++)
        {
            if (params[i] == null)
                throw new MetaAlgorithmsFactoryException(errorMsg);
        }
        boolean foundK = false, foundGAggr = false, foundLAggr = false;
        int k = 0;
        AbstractGlobalAggregator globalArg = null;
        AbstractLocalAggregator localArg = null;
        for (int i = 0; i < 3; i++)
        {
            if (params[i] instanceof Integer)
            {
                foundK = true;
                k = ((Integer) params[i]).intValue();
                continue;
            }
            else if (params[i] instanceof AbstractGlobalAggregator)
            {
                foundGAggr = true;
                globalArg = (AbstractGlobalAggregator) params[i];
                continue;
            }
            else if (params[i] instanceof AbstractLocalAggregator)
            {
                foundLAggr = true;
                localArg = (AbstractLocalAggregator) params[i];
                continue;
            }
        }
        if (!foundK || !foundGAggr || !foundLAggr)
            throw new MetaAlgorithmsFactoryException(errorMsg);
        return new SMThersholdAlgorithm(k, globalArg, localArg);
    }
}