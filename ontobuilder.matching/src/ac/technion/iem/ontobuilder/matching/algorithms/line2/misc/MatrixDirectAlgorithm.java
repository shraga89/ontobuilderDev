package ac.technion.iem.ontobuilder.matching.algorithms.line2.misc;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.matching.algorithms.common.MatchAlgorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.meta.AbstractMetaAlgorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.meta.MetaAlgorithmInitiationException;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.meta.MetaAlgorithmRunningException;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.tkm.TKM;
import ac.technion.iem.ontobuilder.matching.meta.aggregators.AbstractGlobalAggregator;
import ac.technion.iem.ontobuilder.matching.meta.aggregators.AbstractLocalAggregator;
import ac.technion.iem.ontobuilder.matching.meta.match.AbstractMapping;
import ac.technion.iem.ontobuilder.matching.meta.match.AbstractMatchMatrix;
import ac.technion.iem.ontobuilder.matching.meta.statistics.MetaAlgorithmStatistics;

/**
 * <p>Title: MatrixDirectAlgorithm</p>
 * Extends {@link AbstractMetaAlgorithm}
 */
public class MatrixDirectAlgorithm extends AbstractMetaAlgorithm
{
    protected TKM _tkm;

    /**
     * Constructs a default MatrixDirectAlgorithm
     */
    protected MatrixDirectAlgorithm()
    {
        super(false);
        setAlgorithmName("Matrix Direct Algorithm");
    }

    // remark: need to check here if matrix double[][] is allocated
    public MatrixDirectAlgorithm(int k, AbstractGlobalAggregator globalArg,
        AbstractLocalAggregator localArg, AbstractMatchMatrix combinedMatrix)
    {
        this();
        this.k = k;
        this.globalArg = globalArg;
        this.localArg = localArg;
        this.combinedMatrix = combinedMatrix;
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
            super.runAlgorithm();// run algorithms A1,...Am
            // prepare the combined matrix
            createCombinedMatrix(globalArg);
            // Initialise TKM algorithm
            _tkm.setInitialSchema(combinedMatrix.getCandidateAttributeNames());
            _tkm.setMatchedSchema(combinedMatrix.getTargetAttributeNames(),
                combinedMatrix.getMatchMatrix());
            // do k best mapping searches with using TKM
            while (!canHalt())
            {
                AbstractMapping newMapping = _tkm.getNextBestMapping(true);
                double localScore = localArg.calcArgValue(newMapping, combinedMatrix);
                newMapping.setGlobalScore(localScore);
                mappings.put(new Integer(currentStep), newMapping);
                if (isUsingStatistics())
                {
                    statistics.increaseIterationsCount();
                    statistics.increaseTotalMappingsCount();
                    statistics.setCurrentTopKMappings(currentStep);
                }
                currentStep++;
            }
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
     * Creates the combined matrix
     * 
     * @param gArg the {@link AbstractGlobalAggregator}
     */
    protected void createCombinedMatrix(AbstractGlobalAggregator gArg)
    {
        int rowCount = matrixs[0].getRowCount();
        int colCount = matrixs[0].getColCount();
        combinedMatrix.copyWithEmptyMatrix(matrixs[0]);
        for (int i = 0; i < rowCount; i++)
            // construct new matrix M*(i,j) = F(Mij(1),...Mij(m))
            for (int j = 0; j < colCount; j++)
                combinedMatrix.setMatchConfidenceAt(i, j,
                    gArg.calcArgValue(prepareConfidenceVector(i, j)));
        if (isNormalizeMatrixes())
            combinedMatrix.normalize();
    }

    /**
     * Get the Top-K matrix
     * 
     * @return the {@link TKM} matrix
     */
    public TKM getTKM()
    {
        return _tkm;
    }

    /**
     * Calculates the confidence vector for each one of the algorithms in place i,j
     * 
     * @param i the row
     * @param j the column
     * @return the confidence vector
     */
    protected double[] prepareConfidenceVector(int i, int j)
    {
        double[] confidences = new double[numOfMatchingAlgorithms];
        for (int l = 0; l < numOfMatchingAlgorithms; l++)
            confidences[l] = matrixs[l].getMatchConfidenceAt(i, j);
        return confidences;
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
        this._tkm = tkm;
        initiated = true;
    }

    /**
     * Checks if the algorithm can stop
     * 
     * @return <code>true</code> if can stop
     */
    protected boolean canHalt()
    {
        return (currentStep > k);
    }

    /**
     * Initialise the statistics instance
     */
    public void useStatistics()
    {
        statistics = new MetaAlgorithmStatistics(this, o1.getName(), o2.getName());
    }

    /**
     * Not implemented
     */
    public void reset()
    {

    }

}