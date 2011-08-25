package ac.technion.iem.ontobuilder.matching.algorithms.line2.misc;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.matching.algorithms.common.MatchAlgorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.meta.MetaAlgorithmInitiationException;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.meta.MetaAlgorithmRunningException;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.tkm.TKM;
import ac.technion.iem.ontobuilder.matching.meta.aggregators.AbstractGlobalAggregator;
import ac.technion.iem.ontobuilder.matching.meta.aggregators.AbstractLocalAggregator;
import ac.technion.iem.ontobuilder.matching.meta.match.AbstractMapping;
import ac.technion.iem.ontobuilder.matching.meta.match.AbstractMatchMatrix;

/**
 * <p>Title: MatrixDirectWithBoundingAlgorithm</p>
 * Extends {@link MatrixDirectAlgorithm}
 */
public class MatrixDirectWithBoundingAlgorithm extends MatrixDirectAlgorithm
{

    protected AbstractGlobalAggregator hGlobalArg;
    protected AbstractLocalAggregator hLocalArg;

    /**
     * 
     * Constructs a MatrixDirectWithBoundingAlgorithm with TODO: Doc.
     *
     * @param k Top-K parameter
     * @param fGlobalArg F {@link AbstractGlobalAggregator}
     * @param fLocalArg F {@link AbstractLocalAggregator}
     * @param hGlobalArg H {@link AbstractGlobalAggregator}
     * @param hLocalArg H {@link AbstractLocalAggregator}
     * @param combinedMatrix the combined match matrix {@link AbstractMatchMatrix}
     */
    public MatrixDirectWithBoundingAlgorithm(int k, AbstractGlobalAggregator fGlobalArg,
        AbstractLocalAggregator fLocalArg, AbstractGlobalAggregator hGlobalArg,
        AbstractLocalAggregator hLocalArg, AbstractMatchMatrix combinedMatrix)
    {
        super(k, fGlobalArg, fLocalArg, combinedMatrix);
        setAlgorithmName("Matrix Direct with Bounding Algorithm");
        setDominators(hGlobalArg, hLocalArg);
    }

    /**
     * Checks if the algorithm can halt
     * 
     * @return <code>true</code> if can halt
     */
    protected boolean canHalt()
    {
        double thresholdMD;
        if (!existZeroAlgorithmScore())
            thresholdMD = hLocalArg.calcArgValue(lastMappings[0], combinedMatrix);
        else
            thresholdMD = adjustMDBThreshold();
        boolean canHalt = isExistKMappingWiteScore(thresholdMD);
        if (isUsingStatistics())
            statistics.setCurrentTopKMappings(currentGeneratedTopK());
        return (thresholdMD == 0 || canHalt || stopReached() || checkInfiniteTermination());
    }

    /**
     * Run the algorithm
     */
    public void runAlgorithm() throws MetaAlgorithmRunningException
    {
        try
        {
            if (isUsingStatistics())
                initStatistics();
            runMatchingAlgorithms();// run algorithms A1,...Am
            // prepare the combined matrix
            createCombinedMatrix(hGlobalArg);// normalized
            if (threshold > 0 && mp != null)
                mp.applyThreshold(threshold);
            if (isNormalizeMatrixes())
            {
                matrixNormalization();
            }
            // initialize TKM algorithm
            tkm.setInitialSchema(combinedMatrix.getCandidateAttributeNames());
            tkm.setMatchedSchema(combinedMatrix.getTargetAttributeNames(),
                combinedMatrix.getMatchMatrix());
            // do k best mapping searches with using TKM
            do
            {
                AbstractMapping newMapping = tkm.getNextBestMapping(true);
                lastMappings[0] = newMapping;
                // calculate local aggregator score according
                double[] localMappingScores = new double[numOfMatchingAlgorithms];
                for (int i = 0; i < numOfMatchingAlgorithms; i++)
                    localMappingScores[i] = localArg.calcArgValue(newMapping, matrixs[i]);
                // added 8/2/04
                lastLocalfScores = localMappingScores;
                // end added
                // added 13/2/04
                lastMappings[0].setLocalScores(localMappingScores);
                // end added
                double globalScore = globalArg.calcArgValue(localMappingScores);
                newMapping.setGlobalScore(globalScore);
                // check if mapping is one of K highest seen so far, if thus remember it
                if (isMappingOneOfKHighestSeenSoFar(globalScore))
                    enterNewHighMapping(newMapping);
                currentStep++;
                if (isUsingStatistics())
                {
                    statistics.increaseIterationsCount();
                    statistics.increaseTotalMappingsCount();
                }
            }
            while (!canHalt());
            // run halt
            finished();
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            abnormalTermination();
            throw new MetaAlgorithmRunningException(e.getMessage());
        }
    }

    /**
     * Initialise the algorithm
     * 
     * @param s1 the first {@link Schema}
     * @param s2 the second {@link Schema}
     * @param numOfMatchingAlgorithms the number of matching algorithms
     * @param algorithms an array of {@link MatchAlgorithm}
     * @param tkm the {@link TKM} algorithm
     * @throws MetaAlgorithmInitiationException
     */
    public void init(Ontology s1, Ontology s2, int numOfMatchingAlgorithms,
        MatchAlgorithm[] algorithms, TKM tkm) throws MetaAlgorithmInitiationException
    {
        super.init(s1, s2, numOfMatchingAlgorithms, algorithms, tkm);
        initiated = true;
    }

    /**
     * Sets the Aggregators
     * 
     * @param hGlobalArg the {@link AbstractGlobalAggregator}
     * @param hLocalArg the {@link AbstractLocalAggregator}
     */
    public void setDominators(AbstractGlobalAggregator hGlobalArg, AbstractLocalAggregator hLocalArg)
    {
        this.hGlobalArg = hGlobalArg;
        this.hLocalArg = hLocalArg;
    }

    /**
     * Initialise the statistics
     */
    protected void initStatistics()
    {
        super.initStatistics();
        statistics.setHAggregatorTypes(hLocalArg.getAggregatorType(),
            hGlobalArg.getAggregatorType());
    }

    /**
     * Check is there's a least one local score in the last mapping which was 0
     * 
     * @return <code>true</code> if there was a zero mapping
     */
    private boolean existZeroAlgorithmScore()
    {
        double[] localScores = lastMappings[0].getLocalScores();
        for (int i = 0; i < localScores.length; i++)
            if (localScores[i] == 0)
                return true;
        return false;
    }

    /**
     * Get the global score of the last mapping
     */
    private double adjustMDBThreshold()
    {
        return lastMappings[0].getGlobalScore();
    }

    /**
     * Not implemented
     */
    public void reset()
    {

    }

}