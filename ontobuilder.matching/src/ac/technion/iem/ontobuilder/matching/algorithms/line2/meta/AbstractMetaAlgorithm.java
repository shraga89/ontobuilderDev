package ac.technion.iem.ontobuilder.matching.algorithms.line2.meta;

import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Vector;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.matching.algorithms.common.MatchAlgorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.tkm.TKM;
import ac.technion.iem.ontobuilder.matching.meta.aggregators.AbstractGlobalAggregator;
import ac.technion.iem.ontobuilder.matching.meta.aggregators.AbstractLocalAggregator;
import ac.technion.iem.ontobuilder.matching.meta.match.AbstractMapping;
import ac.technion.iem.ontobuilder.matching.meta.match.AbstractMatchMatrix;
import ac.technion.iem.ontobuilder.matching.meta.match.MatrixPreprocessor;
import ac.technion.iem.ontobuilder.matching.meta.match.MatrixPreprocessorTypeEnum;
import ac.technion.iem.ontobuilder.matching.meta.statistics.MetaAlgorithmStatistics;
import ac.technion.iem.ontobuilder.matching.meta.statistics.TAStatistics;
import ac.technion.iem.ontobuilder.matching.utils.DoublePrecision;
import ac.technion.iem.ontobuilder.matching.utils.SchemaMatchingsUtilities;

/**
 * <p>
 * Title: AbstractMetaAlgorithm
 * </p>
 * Extends {@link MetaAlgorithm}
 */
public abstract class AbstractMetaAlgorithm implements MetaAlgorithm
{

    protected String algorithmName;
    protected Ontology o1, o2;
    protected AbstractLocalAggregator localArg;
    protected AbstractGlobalAggregator globalArg;
    protected int synchronizer = 0;
    protected int numOfMatchingAlgorithms = 0;
    protected MatchAlgorithm[] algorithms;
    protected AbstractMatchMatrix[] matrixs;
    protected AbstractMatchMatrix combinedMatrix;
    protected MetaAlgorithmThread[] maThreads;
    protected int currentStep = 1;
    protected boolean initiated = false;
    protected Hashtable<Integer, AbstractMapping> mappings = new Hashtable<Integer, AbstractMapping>();
    private boolean useParallel;
    protected LinkedList<AbstractMapping> highestMappings = new LinkedList<AbstractMapping>();
    protected AbstractMapping[] lastMappings;
    protected int k;
    protected TKM tkm;
    protected boolean preprocessMatrixes = true;
    protected boolean algorithmRunFinished = false;
    protected boolean normalizeMatrixes = false;
    protected MatrixPreprocessorTypeEnum preprocessingType = MatrixPreprocessorTypeEnum.INTERSECT_PREPROCESSING;
    protected int useTemplateIndex = 0;
    protected MetaAlgorithmStatistics statistics = null;
    protected int countMappings = 0;
    protected double[] lastLocalfScores;
    protected double[] lastLocalXiScores;
    protected boolean abnormalTerminationOccur = false;
    protected boolean debugMode = false;
    protected double[] heuristicValues;
    protected boolean nonUniform = false;
    protected byte nonUniformVersion = 1;
    protected StringBuffer debugString = new StringBuffer();
    // protected boolean usingStopCheck = false;
    protected boolean firstTimeHeuristic = true;
    protected int lastTidProgressedWith = -1;
    protected double[] lastTidHeuristicXi;
    protected double threshold = 0;
    protected MatrixPreprocessor mp;
    protected int lastTotalGeneratedTopKNum = 0;
    protected int lastCurrentStep = 0;
    protected boolean recallRun = false;
    protected AbstractMapping exactMapping;
    protected boolean recallReport = false;

    /**
     * Constructs a default AbstractMetaAlgorithm
     */
    protected AbstractMetaAlgorithm()
    {
    }

    /**
     * Constructs a AbstractMetaAlgorithm with a boolean to indicate usage of parallel calculations.
     * 
     * @param <code>true</code> to useParallel
     */
    protected AbstractMetaAlgorithm(boolean useParallel)
    {
        this.useParallel = useParallel;
    }

    /**
     * Get the combined matrix
     * 
     * @return combinedMatrix {@link AbstractMatchMatrix}
     */
    public AbstractMatchMatrix getCombinedMatrix()
    {
        return combinedMatrix;
    }

    /**
     * Return a match matrix by an index
     * 
     * @param i the index
     * @return {@link AbstractMatchMatrix}
     */
    public AbstractMatchMatrix getMatchMatrix(int i)
    {
        if (matrixs == null || matrixs.length == 0)
            return null;
        return matrixs[i];
    }

    /**
     * Set to a run with recall
     * 
     * @param r <code>true</code> to set recall
     */
    public void setRecallRun(boolean r)
    {
        recallRun = r;
    }

    /**
     * Sets an exact mapping using a SchemaTranslator Object according to the threshold, if such
     * exists
     * 
     * @param e an {@link AbstractMapping}
     */
    public void setExactMapping(AbstractMapping e)
    {
        if (threshold > 0)
            e = SchemaMatchingsUtilities.getSTwithThresholdSensitivity(e, threshold);
        this.exactMapping = e;
    }

    /**
     * Run the algorithm with all pre-set parameters (threshold, normalisation, parallel...)
     */
    public void runAlgorithm() throws MetaAlgorithmRunningException
    {
        if (!initiated)
            throw new MetaAlgorithmRunningException("Meta algorithm " + getAlgorithmName() +
                " was not initiated!");
        try
        {
            runMatchingAlgorithms();
            if (threshold > 0 && mp != null)
                mp.applyThreshold(threshold);
            if (isNormalizeMatrixes())
                matrixNormalization();
            if (nonUniform)
                initNonUniform();
            if (useParallel)
            {
                for (int i = 0; i < numOfMatchingAlgorithms; i++)
                {
                    // load the TKM algorithm and set it to executing thread
                    maThreads[i].setTKM(this.tkm);
                    maThreads[i].setMatchMatrix(matrixs[i]);
                    maThreads[i].start();
                }
            }
        }
        catch (ExceptionInInitializerError e)
        {
            throw new MetaAlgorithmRunningException(e.getMessage());
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            throw new MetaAlgorithmRunningException(e.getMessage());
        }
    }

    /**
     * Runs each one of the matching algorithms on a different matrix
     */
    protected void runMatchingAlgorithms()
    {
        for (int i = 0; i < numOfMatchingAlgorithms; i++)
        {
            matrixs[i] = algorithms[i].match(o1, o2).getMatrix();
        }

        if (needToPreprocess())
            preprocess();
    }

    /**
     * Sets the threshold (between 0 and 1)
     * 
     * @param t the threshold
     * @throws MetaAlgorithmInitiationException when the threshold is not in [0,1]
     */
    public void setThreshold(double t) throws MetaAlgorithmInitiationException
    {
        if (t < 0 || t > 1)
            throw new MetaAlgorithmInitiationException("Expected Threshold in [0,1]");
        threshold = t;
    }

    /**
     * Runs the requested pre-processing type on the matrixes (union, intersect or template)
     */
    private void preprocess()
    {
        mp = new MatrixPreprocessor(matrixs, useTemplateIndex, preprocessingType);
        matrixs = mp.preprocess();
    }

    /**
     * Sets the non-uniform version (1 or 2)
     * 
     * @param nu <code>true</code> to set non-uniform
     * @param nuv the non uniform version
     * @throws MetaAlgorithmInitiationException when the non uniform version is not 1 or 2
     */
    public void setNonUniform(boolean nu, byte nuv) throws MetaAlgorithmInitiationException
    {
        if (nuv != 1 && nuv != 2)
            throw new MetaAlgorithmInitiationException("Expected Non Uniform Version 1 OR 2");
        nonUniformVersion = nuv;
        nonUniform = nu;
    }

    /**
     * Get the k value
     * 
     * @return the value
     */
    public int getK()
    {
        return k;
    }

    /**
     * Set the non-uniform version for each one of the threads
     */
    public void initNonUniform()
    {
        heuristicValues = new double[numOfMatchingAlgorithms];
        if (nonUniform)
        {
            for (int i = 0; i < numOfMatchingAlgorithms; i++)
            {
                maThreads[i].setNonUniform(true, nonUniformVersion);
            }
        }
        lastTidHeuristicXi = new double[numOfMatchingAlgorithms];
    }

    /**
     * Initialise the algorithm with the schemas, the matching algorithms and the top-k matrix
     * 
     * @param o1 the first {@link Schema}
     * @param o2 the second {@link Schema}
     * @param numOfMatchingAlgorithms the number of matching algorithms
     * @param algorithms an array of {@link MatchAlgorithm}
     * @param tkm the {@link TKM} algorithm
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
            this.lastMappings = new AbstractMapping[numOfMatchingAlgorithms];
            this.algorithms = algorithms;
            this.tkm = tkm;
            matrixs = new AbstractMatchMatrix[numOfMatchingAlgorithms];
            if (useParallel)
            {
                maThreads = new MetaAlgorithmThread[numOfMatchingAlgorithms];
                for (int i = 0; i < numOfMatchingAlgorithms; i++)
                {
                    maThreads[i] = new MetaAlgorithmThread(this, i);
                }
            }
            initiated = true;
        }
        catch (Throwable e)
        {
            throw new MetaAlgorithmInitiationException(e.getMessage());
        }
    }

    /**
     * Reset all the parameters
     */
    public void nullify()
    {
        mappings.clear();
        mappings = null;
        highestMappings.clear();
        highestMappings = null;
        o1 = null;
        o2 = null;
        localArg = null;
        globalArg = null;
        algorithms = null;
        matrixs = null;
        maThreads = null;
        tkm = null;
    }

    /**
     * Set the algorithm name
     * 
     * @param algorithmName the name
     */
    public void setAlgorithmName(String algorithmName)
    {
        this.algorithmName = algorithmName;
    }

    /**
     * Get the algorithm name
     */
    public String getAlgorithmName()
    {
        return algorithmName;
    }

    /**
     * Set the Local Aggregator
     * 
     * @param localArg {@link AbstractLocalAggregator}
     */
    public void setLocalAggregator(AbstractLocalAggregator localArg)
    {
        this.localArg = localArg;
    }

    /**
     * Get the Local Aggregator
     * 
     * @return the {@link AbstractLocalAggregator}
     */
    public AbstractLocalAggregator getAbstractLocalAggregator()
    {
        return localArg;
    }

    /**
     * Set the Global Aggregator
     * 
     * @param globalArg the {@link AbstractGlobalAggregator}
     */
    public void setGlobalAggregator(AbstractGlobalAggregator globalArg)
    {
        this.globalArg = globalArg;
    }

    /**
     * Get the Global Aggregator
     * 
     * @return the {@link AbstractGlobalAggregator}
     */
    public AbstractGlobalAggregator getAbstractGlobalAggregator()
    {
        return globalArg;
    }

    /**
     * Check if the current mapping score is one of k highest seen so far.
     * 
     * @param score the score to compare
     * @return If the current k is largest that the number of highest mappings, returns true. Else,
     * if the highest mappings is better than the worst-highest-mappings score, return true.
     */
    protected boolean isMappingOneOfKHighestSeenSoFar(double score)
    {
        if (k > highestMappings.size())
            return true;
        else
        {
            AbstractMapping worst = (AbstractMapping) highestMappings.get(0);
            return worst.getGlobalScore() < score;
        }
    }

    /**
     * Initiate the statistics instance
     */
    protected void initStatistics()
    {
        statistics.setFAggregatorTypes(localArg.getAggregatorType(), globalArg.getAggregatorType());
        statistics.setKParameter(k);
        statistics.startTimer();
    }

    /**
     * Checks there exist k mappings with a score
     * 
     * @param score set as the last threshold
     * @return <code>true</code> if there are more (or equal) number of mappings than k
     */
    protected boolean isExistKMappingWiteScore(double score)
    {
        countMappings = 0;
        double recall = -1;
        int numOfCurrentMappings = highestMappings.size();
        for (int i = 0; i < numOfCurrentMappings; i++)
        {

            if (recallRun)
                recall = SchemaMatchingsUtilities.calculateRecall(exactMapping,
                    (AbstractMapping) highestMappings.get(i));
            // DEBUG
            if (debugMode)
            {
                System.out.println("current:" +
                    i +
                    " score:" +
                    DoublePrecision.getDoubleP(
                        ((AbstractMapping) highestMappings.get(i)).getGlobalScore(), 10) +
                    " need:" + DoublePrecision.getDoubleP(score, 10) + " recall:" + recall);
            }
            // DEBUG
            if (DoublePrecision.getDoubleP(
                ((AbstractMapping) highestMappings.get(i)).getGlobalScore(), 10) >= DoublePrecision
                .getDoubleP(score, 10))
                countMappings++;
            if (recallRun)
            {
                if (recall == 1.0 && countMappings > 1 && !recallReport)
                {
                    statistics.setkOf100Recall(countMappings, currentStep);
                    recallReport = true;
                    k = 20;
                }
            }
        }
        // DEBUG
        if (debugMode)
            System.out.println("current mappings:" + countMappings + " current iterations:" +
                currentStep);

        if (lastTotalGeneratedTopKNum != countMappings)
        {
            lastTotalGeneratedTopKNum = countMappings;
            lastCurrentStep = currentStep;
        }

        if (countMappings >= k && isUsingStatistics())
        {
            statistics.setLastThreshold(score);
            statistics.setTotalHighMappings(countMappings);
        }
        return (countMappings >= k);
    }

    /**
     * Return the current number of generated Top K mappings
     * 
     * @return the number of mappings
     */
    public int currentGeneratedTopK()
    {
        return countMappings;
    }

    /**
     * Enter a new high-mapping and sort (in an ascending order). If there are too many mappings,
     * remove the one with the lowest score.
     * 
     * @param an {@link AbstractMapping}
     */
    protected void enterNewHighMapping(AbstractMapping mapping)
    {
        // find index to insert and insert
        // TODO: Haggai - make it sorted every time
        if (highestMappings.size() >= k)
            highestMappings.remove(0);
        highestMappings.add(mapping);
        Collections.sort(highestMappings);
    }

    /**
     * Get the K best mappings
     * 
     * @return a list of {@link AbstractMapping}
     */
    public Vector<AbstractMapping> getAllKBestMappings()
    {
        Collections.sort(highestMappings);
        Vector<AbstractMapping> kBestMappings = new Vector<AbstractMapping>();
        for (int i = 0; i < k; i++)
        {
            AbstractMapping aMapping = highestMappings.isEmpty() ? (mappings.get(i + 1)) : highestMappings
                .get(i);
            kBestMappings.add(aMapping);
        }
        return kBestMappings;
    }

    /**
     * Get the k-th best mapping
     * 
     * @param k the index of mapping to return
     */
    public AbstractMapping getKthBestMapping(int k)
    {
        if (k < 1 || k > this.k)
            throw new IllegalArgumentException("k is illigal best mapping index,only have:" +
                this.k + "best mappings");
        return (AbstractMapping) (highestMappings.isEmpty() ? mappings.get(new Integer(k)) : highestMappings
            .get(k - 1));
    }

    /**
     * Check infinite termination
     * 
     * @return true if the current step is over 500 than the last steo and the current number of
     * generated Top-K mappings has not changed.
     */
    public boolean checkInfiniteTermination()
    {
        return (currentStep - lastCurrentStep >= 500 && currentGeneratedTopK() == lastTotalGeneratedTopKNum);
    }

    /**
     * Resets the parameters
     */
    public void reset()
    {
        synchronizer = 0;
        numOfMatchingAlgorithms = 0;
        currentStep = 1;
        useTemplateIndex = 0;
        mappings.clear();
        highestMappings.clear();
        initiated = false;
        preprocessMatrixes = true;
        algorithmRunFinished = false;
        o1 = null;
        o2 = null;
        localArg = null;
        globalArg = null;
        algorithms = null;
        matrixs = null;
        maThreads = null;
        k = 0;
        tkm = null;
        preprocessingType = MatrixPreprocessorTypeEnum.INTERSECT_PREPROCESSING;
    }

    protected abstract boolean canHalt();

    /**
     * Sets the pre-processing parameters
     * 
     * @param preprocessingType the pre-processing type (
     * <code>UNION_PREPROCESSING</code>,
     * <code>INTERSECT_PREPROCESSING</code> and
     * <code>TEMPLATE_PREPROCESSING</code>)
     * @param useTemplateIndex the template index
     * @throws MetaAlgorithmRunningException if the meta-algorithm was not initiated
     */
    public void setPreprocessMatrixes(MatrixPreprocessorTypeEnum preprocessingType, int useTemplateIndex)
        throws MetaAlgorithmRunningException
    {
        if (!initiated)
            throw new MetaAlgorithmRunningException("Meta algorithm " + getAlgorithmName() +
                " was not initiated!");
        if (preprocessingType == MatrixPreprocessorTypeEnum.TEMPLATE_PREPROCESSING &&
            (useTemplateIndex < 0 || useTemplateIndex >= matrixs.length))
            throw new IllegalArgumentException(
                "Template Preprocessing requested for illigal template index!");
        this.preprocessingType = preprocessingType;
        this.useTemplateIndex = useTemplateIndex;
    }

    /**
     * Sets skipping the matrixes pre-processing
     */
    public void skipMatrixesPreprocessing()
    {
        preprocessMatrixes = false;
    }

    /**
     * Checks if there's a need to pre-process
     * 
     * @return <code>true</code> if pre-processing is required
     */
    private boolean needToPreprocess()
    {
        return (preprocessMatrixes);
    }

    /**
     * Checks if to pre-process of the matrixes is used
     * 
     * @return <code>true</code> if pre-processing is used
     */
    public boolean isUsingMatrixPreprocessing()
    {
        return preprocessMatrixes;
    }

    /**
     * Not implemented
     */
    public void notifyNewMapping(int tid, AbstractMapping mapping)
    {
    }

    /**
     * Not implemented
     */
    public void notifyNewHeuristicMappings(int tid, AbstractMapping alpha,
        Vector<AbstractMapping> betas)
    {
    }

    /**
     * Checks if the algorithms is finished
     * 
     * @return <code>true</code> if the algorithm is finished
     */
    public boolean isAlgorithmRunFinished()
    {
        return algorithmRunFinished;
    }

    /**
     * Processes all required information when the algorithm is finished
     */
    protected void finished()
    {
        algorithmRunFinished = true;
        if (isUsingStatistics())
        {
            statistics.stopTimer();
            int cnt = 0;
            System.out.println("::: " + highestMappings.size());
            for (int i = 0; i < highestMappings.size(); i++)
                if (((AbstractMapping) highestMappings.get(i)).getGlobalScore() != 0)
                    cnt++;
            statistics.setNumOfUsefullMappings(cnt);
        }
    }

    /**
     * Set to debug mode
     * 
     * @param debugMode <code>true</code> if is set to debug
     */
    public void setDebugMode(boolean debugMode)
    {
        this.debugMode = debugMode;
    }

    public abstract void useStatistics();

    /**
     * Get the statistics
     * 
     * @return the statistics {@link MetaAlgorithmStatistics}
     */
    public MetaAlgorithmStatistics getStatistics()
    {
        return statistics;
    }

    /**
     * Return whether statistics are used of not
     * 
     * @return <code>true</code> if statistics are used
     */
    protected boolean isUsingStatistics()
    {
        return (statistics != null);
    }

    /**
     * Return whether the matrixes are normalised or not
     * 
     * @return <code>true</code> if the matrixes are normalised
     */
    protected boolean isNormalizeMatrixes()
    {
        return normalizeMatrixes;
    }

    /**
     * Set the matrixes to be normalised
     */
    public void normalizeMatrixes()
    {
        normalizeMatrixes = true;
    }

    /**
     * Normalise the matrixes
     */
    protected void matrixNormalization()
    {
        if (normalizeMatrixes)
        {
            for (int i = 0; i < numOfMatchingAlgorithms; i++)
                matrixs[i].normalize();
        }
    }

    /**
     * Processes abnormal termination of the algorithm
     */
    public synchronized void abnormalTermination()
    {
        if (abnormalTerminationOccur)
            return;
        abnormalTerminationOccur = true;
        if (useParallel)
        {
            for (int i = 0; i < numOfMatchingAlgorithms; i++)
            {
                maThreads[i].die();
            }
        }
        finished();
    }

    /**
     * Get the number of schema matchers (matching algorithms)
     */
    public int getNumOfSchemaMatchers()
    {
        return numOfMatchingAlgorithms;
    }

    /**
     * Print the debug string
     */
    public void printDebugString()
    {
        System.out.println(debugString.toString());
    }

    /**
     * Check if stop was reached - there are k mappings with highest F (do not commute over
     * M(1)...M(m)) scores and the last local scores which are 0 are the same as the number of
     * matching algorithms
     * 
     * @return <code>true</code> if conditions are met
     */
    protected boolean stopReached()
    {
        int zeroLFCnt = 0;
        for (int i = 0; i < lastLocalfScores.length; i++)
            if (lastLocalfScores[i] == 0)
                zeroLFCnt++;
            else
                break;
        return ((zeroLFCnt == numOfMatchingAlgorithms) && highestMappings.size() == k);
    }

    /**
     * Add a new mapping if a similar one does not exist
     * 
     * @param tid thread id
     * @param mapping the {@link AbstractMapping} to add
     */
    protected void newMapping(int tid, AbstractMapping mapping)
    {
        // perform local and global aggerators calculation
        // first check if not seen yet this mappings in one of the sorted lists
        if (!mappings.containsKey(new Integer(mapping.hashCode())))
        {
            if (isUsingStatistics())
            {
                ((TAStatistics) statistics).increaseThreadMappingCount(tid, currentGeneratedTopK());
                statistics.increaseTotalMappingsCount(); // count only new mappings
            }
            mappings.put(new Integer(mapping.hashCode()), mapping);
            // calculate local aggregator score according
            double[] localMappingScores = new double[numOfMatchingAlgorithms];
            for (int i = 0; i < numOfMatchingAlgorithms; i++)
                localMappingScores[i] = localArg.calcArgValue(mapping, matrixs[i]);
            // added 8/2/04
            lastLocalfScores = localMappingScores;
            // end added
            double globalScore = globalArg.calcArgValue(localMappingScores);

            mapping.setGlobalScore(globalScore);
            // check if mapping is one of K highest seen so far, if so remember it
            if (isMappingOneOfKHighestSeenSoFar(globalScore))
                enterNewHighMapping(mapping);
        }
        // /debug
        else
        {
            // System.out.println(" **seem already this mapping...");
        }
    }

    /**
     * <p>Title: MetaAlgorithmThread</p>
     * Extends {@link Thread}
     */
    protected class MetaAlgorithmThread extends Thread
    {
        private long TIME_TO_SLEEP = 100;
        private AbstractMetaAlgorithm metaAlgorithm;
        private Boolean canContinueToNextStep = Boolean.FALSE;
        private boolean keepRunning = true;
        private int tid;
        private long currentStep = 1;
        private TKM tkm;
        private boolean nonUniform = false;
        private byte nonUniformVersion;
        private boolean tempFlag = true;
        private boolean lastTidThatProgressed = true;
        private AbstractMapping lastSecondMapping;
        private Vector<AbstractMapping> lastPesduMappings;

        /**
         * Constructs a MetaAlgorithmThread with the meta algorithm and a thread id
         * 
         * @param metaAlgorithm a {@link AbstractMetaAlgorithm}
         * @param tid the thread id
         */
        public MetaAlgorithmThread(AbstractMetaAlgorithm metaAlgorithm, int tid)
        {
            this.metaAlgorithm = metaAlgorithm;
            this.tid = tid;
        }

        /**
         * Set non-uniform processing
         * 
         * @param nu <code>true</code> if is non-uniform
         * @param nuv non-uniform version
         */
        public void setNonUniform(boolean nu, byte nuv)
        {
            this.nonUniformVersion = nuv;
            this.nonUniform = nu;
        }

        /**
         * The thread's processing - creates a new best mapping
         */
        public void run()
        {
            // System.out.println("MetaAlgorithmThread "+tid+" starts running");
            while (keepRunning)
            {
                // debug
                // System.out.println("MetaAlgorithmThread "+tid+" in currentStep:"+currentStep);
                // ***

                // create new best Mapping
                try
                {
                    if (!nonUniform || (nonUniform && tempFlag))
                    {
                        metaAlgorithm.notifyNewMapping(tid, tkm.getNextBestMapping(true));
                        tempFlag = false;
                        // System.out.println("Thread:"+tid+" produced real mapping");
                    }
                    else
                    {

                        if (lastTidThatProgressed)
                        {
                            // System.out.println("Thread:"+tid+" produced real heuristic value");
                            lastTidThatProgressed = false;
                            lastSecondMapping = tkm.getLocalSecondBestMapping();
                            lastPesduMappings = tkm.getNextHeuristicMappings(nonUniformVersion);
                            metaAlgorithm.notifyNewHeuristicMappings(tid, lastSecondMapping,
                                lastPesduMappings);

                        }
                        else
                        {
                            // System.out.println("Thread:"+tid+" produced history heuristic value");
                            metaAlgorithm.notifyNewHeuristicMappings(tid, lastSecondMapping,
                                lastPesduMappings);
                        }
                    }
                }
                catch (Throwable e)
                {
                    e.printStackTrace();
                    metaAlgorithm.abnormalTermination();
                }
                while (!canContinueToNextStep.booleanValue() && keepRunning)
                {

                    try
                    {
                        sleep(TIME_TO_SLEEP);
                    }
                    catch (InterruptedException e)
                    {
                    }

                }
                // DEBUG
                // if (!keepRunning)
                // System.out.println("MetaAlgorithmThread "+tid+ " died");
                //

                currentStep++;
            }
        }

        /**
         * Sets the match matrix
         * 
         * @param matrix the {@link AbstractMatchMatrix} to set
         * @throws MetaAlgorithmRunningException when cannot initialize of match the schema
         */
        public void setMatchMatrix(AbstractMatchMatrix matrix) throws MetaAlgorithmRunningException
        {
            try
            {
                tkm.setInitialSchema(matrix.getCandidateAttributeNames());
                tkm.setMatchedSchema(matrix.getTargetAttributeNames(), matrix.getMatchMatrix());
            }
            catch (Throwable e)
            {
                e.printStackTrace();
                throw new MetaAlgorithmRunningException("in set match matrix");
            }
        }

        /**
         * Sets if can continue to the next step to false
         */
        public void waitForNextStep()
        {
            canContinueToNextStep = Boolean.FALSE;
        }

        /**
         * Continues one step
         * 
         * @return the next best mapping
         * @throws MetaAlgorithmRunningException
         */
        public AbstractMapping continueInOneStep() throws MetaAlgorithmRunningException
        {
            try
            {
                lastTidThatProgressed = true;
                return tkm.getNextBestMapping(true);
            }
            catch (Throwable e)
            {
                e.printStackTrace();
                throw new MetaAlgorithmRunningException("in continueInOneStep");
            }
        }

        /**
         * Sets if can continue to the next step to <code>true</code>
         */
        public void continueNextStep()
        {
            canContinueToNextStep = Boolean.TRUE;
        }

        /**
         * Stops the algorithm's run
         */
        public void die()
        {
            tkm.nullify();
            keepRunning = false;
        }

        /**
         * Sets the Top-K matrix
         * 
         * @param tkm the matrix to set
         */
        public void setTKM(TKM tkm)
        {
            this.tkm = tkm;
        }

    }

}
