package ac.technion.iem.ontobuilder.matching.algorithms.line2.misc;

import java.util.Iterator;
import java.util.Vector;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.matching.algorithms.common.MatchAlgorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.meta.MetaAlgorithmInitiationException;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.meta.MetaAlgorithmRunningException;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.tkm.TKM;
import ac.technion.iem.ontobuilder.matching.meta.aggregators.AbstractGlobalAggregator;
import ac.technion.iem.ontobuilder.matching.meta.aggregators.AbstractLocalAggregator;
import ac.technion.iem.ontobuilder.matching.meta.match.AbstractMapping;
import ac.technion.iem.ontobuilder.matching.meta.match.AbstractMatchMatrix;
import ac.technion.iem.ontobuilder.matching.meta.statistics.TAStatistics;


/**
 * <p>Title: CrossThresholdAlgorithm</p>
 * Extends {@link MatrixDirectWithBoundingAlgorithm}
 * Implements {@link ThresholdAlgorithm} and {@link NonUniformMetaAlgorithm}
 */
public class CrossThresholdAlgorithm extends MatrixDirectWithBoundingAlgorithm implements
    ThresholdAlgorithm, NonUniformMetaAlgorithm
{

    /**
     * Constructs a CrossThresholdAlgorithm
     * 
     * @param k Top-K parameter
     * @param fGlobalArg F {@link AbstractGlobalAggregator}
     * @param fLocalArg F {@link AbstractLocalAggregator}
     * @param hGlobalArg H {@link AbstractGlobalAggregator}
     * @param hLocalArg H {@link AbstractLocalAggregator}
     * @param combinedMatrix the combined match matrix {@link AbstractMatchMatrix}
     */
    public CrossThresholdAlgorithm(int k, AbstractGlobalAggregator fGlobalArg,
        AbstractLocalAggregator fLocalArg, AbstractGlobalAggregator hGlobalArg,
        AbstractLocalAggregator hLocalArg, AbstractMatchMatrix combinedMatrix)
    {
        super(k, fGlobalArg, fLocalArg, hGlobalArg, hLocalArg, combinedMatrix);
        setAlgorithmName("CrossThreshold Algorithm");
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
            runMatchingAlgorithms(); // run algorithms A1,...Am
            // prepare the combined matrix
            createCombinedMatrix(hGlobalArg);
            if (threshold > 0 && mp != null)
                mp.applyThreshold(threshold);
            this.numOfMatchingAlgorithms++; // one more TKM that runs on M*
            addCombinedMatrix(combinedMatrix);
            if (isNormalizeMatrixes())
                matrixNormalization();
            if (nonUniform)
                initNonUniform();
            for (int i = 0; i < numOfMatchingAlgorithms; i++)
            {
                maThreads[i].setTKM(tkm);
                maThreads[i].setMatchMatrix(matrixs[i]);
                maThreads[i].start();
            }
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
     * @param o1 the first {@link Schema}
     * @param o2 the second {@link Schema}
     * @param numOfMatchingAlgorithms the number of matching algorithms
     * @param algorithms an array of {@link MatchAlgorithm}
     * @param tkm the {@link TKM} matrix
     * @throws MetaAlgorithmInitiationException
     */
    public void init(Ontology o1, Ontology o2, int numOfMatchingAlgorithms,
        MatchAlgorithm[] algorithms, TKM tkm) throws MetaAlgorithmInitiationException
    {
        if (o1 == null || o2 == null || numOfMatchingAlgorithms < 1 || algorithms == null ||
            algorithms.length != numOfMatchingAlgorithms || tkm == null)
            throw new IllegalArgumentException("Meta Algorithm initiation got illigal arguments");
        try
        {
            this.o1 = o1;
            this.o2 = o2;
            this.numOfMatchingAlgorithms = numOfMatchingAlgorithms;
            this.lastMappings = new AbstractMapping[numOfMatchingAlgorithms + 1];
            this.algorithms = algorithms;
            this.tkm = tkm;
            matrixs = new AbstractMatchMatrix[numOfMatchingAlgorithms];
            maThreads = new MetaAlgorithmThread[numOfMatchingAlgorithms + 1];
            for (int i = 0; i < numOfMatchingAlgorithms + 1; i++)
            {
                maThreads[i] = new MetaAlgorithmThread(this, i);
            }
            initiated = true;
        }
        catch (Throwable e)
        {
            throw new MetaAlgorithmInitiationException(e.getMessage());
        }
    }

    /**
     * Add the combined matrix
     * 
     * @param combinedMatrix the {@link AbstractMatchMatrix} to add
     */
    private void addCombinedMatrix(AbstractMatchMatrix combinedMatrix)
    {
        AbstractMatchMatrix[] temp = matrixs;
        matrixs = new AbstractMatchMatrix[numOfMatchingAlgorithms];
        for (int i = 0; i < temp.length; i++)
            matrixs[i] = temp[i];
        matrixs[temp.length] = combinedMatrix;
    }

    /**
     * Check if the algorithm can stop
     * 
     * @return <code>true</code> if can stop
     */
    protected boolean canHalt()
    {
        // debug
        // System.out.println("checking if can halt..");
        // ***
        double[] localMappingScores = new double[numOfMatchingAlgorithms - 1];
        for (int i = 0; i < numOfMatchingAlgorithms - 1; i++)
            localMappingScores[i] = localArg.calcArgValue(lastMappings[i], matrixs[i]);
        double thresholdTA = globalArg.calcArgValue(localMappingScores);
        double thresholdMD = hLocalArg.calcArgValue(lastMappings[numOfMatchingAlgorithms - 1],
            matrixs[numOfMatchingAlgorithms - 1]);
        double minThreshold = Math.min(thresholdTA, thresholdMD);
        boolean canHalt = isExistKMappingWiteScore(minThreshold);
        if (isUsingStatistics())
            ((TAStatistics) statistics).setCurrentTopKMappings(currentGeneratedTopK());
        return (minThreshold == 0 || canHalt || stopReached() || checkInfiniteTermination());
    }

    /**
     * Find the minimal heuristic value to progress with.
     * 
     * @return the heuristic value
     */
    public int progressWith()
    {
        int with = 0;
        double minVal = Double.MAX_VALUE;
        for (int i = 0; i < numOfMatchingAlgorithms; i++)
        {
            if (heuristicValues[i] < minVal)
            {
                with = i;
                minVal = heuristicValues[i];
            }
        }
        if (debugMode)
        {
            System.out.println("Progress with:" + with + " Heuristic value:" +
                heuristicValues[with] + "\n");
        }
        return with;
    }

    /**
     * Notifies the correct thread that a new mapping has been added, kills the thread is it has
     * reached the stopping condition
     * 
     * @param tid the thread id
     * @param mapping an {@link AbstractMapping}
     */
    public synchronized void notifyNewMapping(int tid, AbstractMapping mapping)
    {
        // //perform local and global aggerators calculation
        // //first check if not seen yet this mappings in one of the sorted lists
        newMapping(tid, mapping);
        // *****
        lastMappings[tid] = mapping;
        // debug
        // System.out.println("calling "+tid+" to wait");
        // ****
        maThreads[tid].waitForNextStep();
        synchronizer++;
        if (synchronizer == numOfMatchingAlgorithms)
        {
            synchronizer = 0;
            if (isUsingStatistics())
                statistics.increaseIterationsCount();
            currentStep++;
            // (b) check halt condition
            if (canHalt())
            {
                for (int i = 0; i < numOfMatchingAlgorithms; i++)
                {
                    maThreads[i].die();
                }
                // run halt
                finished();
            }
            else
            {
                for (int i = 0; i < numOfMatchingAlgorithms; i++)
                {
                    // System.out.println("calling "+i+" to continue");
                    maThreads[i].continueNextStep();
                }
            }
        }
    }

    /**
     * Notify the correct thread with the new heuristic value
     * 
     * @param tid the thread id
     * @param alpha an {@link AbstractMapping}
     * @param betas a vector of {@link AbstractMapping}
     */
    public synchronized void notifyNewHeuristicMappings(int tid, AbstractMapping alpha,
        Vector<AbstractMapping> betas)
    {
        double[] localMappingScores = new double[numOfMatchingAlgorithms];
        if (lastTidProgressedWith == tid)
        {
            double alphaVal, maxBetaVal = Double.MIN_VALUE, localVal;
            alphaVal = localArg.calcArgValue(alpha, matrixs[tid]);
            Iterator<AbstractMapping> it = betas.iterator();
            while (it.hasNext())
            {
                localVal = localArg.calcArgValue((AbstractMapping) it.next(), matrixs[tid]);
                maxBetaVal = maxBetaVal > localVal ? maxBetaVal : localVal;
            }
            // System.out.println("tid:"+tid+" beta val:"+maxBetaVal);

            lastTidHeuristicXi[tid] = Math.max(maxBetaVal, alphaVal); // save heuristic valuation -
                                                                      // epsilon

        }
        if (tid != numOfMatchingAlgorithms - 1)
        {// TA
            for (int i = 0; i < numOfMatchingAlgorithms - 1; i++)
                localMappingScores[i] = (i == tid) ? lastTidHeuristicXi[tid] : localArg
                    .calcArgValue(lastMappings[i], matrixs[i]);
            // added 8/2/04
            lastLocalXiScores = localMappingScores;
            // end added
            heuristicValues[tid] = globalArg.calcArgValue(lastLocalXiScores);
        }
        else
        {// MDB
            heuristicValues[tid] = lastTidHeuristicXi[tid];
        }

        // debug debugString.append(
        // if (lastTidProgressedWith != -1)
        if (debugMode)
        {
            System.out.print("tid:" + tid + " delta:" +
                (lastTidHeuristicXi[tid] - localArg.calcArgValue(lastMappings[tid], matrixs[tid])) +
                "\n");
            System.out.println("tid:" + tid + " TA value:" + heuristicValues[tid]);
        }
        // System.out.println("Winner:"+tid);
        // **
        // debug
        // System.out.println(tid+" heuristic value:"+heuristicValues[tid]);
        // ****
        maThreads[tid].waitForNextStep();
        synchronizer++;
        if (synchronizer == numOfMatchingAlgorithms)
        {
            synchronizer = 0;
            if (isUsingStatistics())
                statistics.increaseIterationsCount();
            currentStep++;
            // progress non uniformly with one tkm
            int progressWith = progressWith();
            lastTidProgressedWith = progressWith;
            try
            {
                // debug
                // System.out.println("continue with:"+progressWith);
                // ****
                lastMappings[progressWith] = maThreads[progressWith].continueInOneStep();
            }
            catch (Throwable e)
            {
                e.printStackTrace();
            }
            // (a)
            newMapping(tid, lastMappings[progressWith]);
            // (b) check halt condition
            if (canHalt())
            {
                for (int i = 0; i < numOfMatchingAlgorithms; i++)
                {
                    maThreads[i].die();
                }
                // run halt
                finished();
            }
            else
            {
                for (int i = 0; i < numOfMatchingAlgorithms; i++)
                {
                    // System.out.println("calling "+i+" to continue");
                    maThreads[i].continueNextStep();
                }
            }
        }
    }

    /**
     * Initialises statistics
     */
    protected void initStatistics()
    {
        super.initStatistics();
        ((TAStatistics) statistics).setThreadsCount(numOfMatchingAlgorithms + 1);
    }

    /**
     * Creates a new statistics instance
     */
    public void useStatistics()
    {
        statistics = new TAStatistics(this, o1.getName(), o2.getName());
    }

    /**
     * Not implemented
     */
    public void reset()
    {
    }

}
