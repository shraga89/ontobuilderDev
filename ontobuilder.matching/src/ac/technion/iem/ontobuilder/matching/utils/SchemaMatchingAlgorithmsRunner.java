package ac.technion.iem.ontobuilder.matching.utils;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.tkm.TKM;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.tkm.TKMInitializationException;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.tkm.TKMRunningException;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.BipartiteGraph;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.DGraph;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.Edge;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.Graph;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.GraphFactory;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.GraphIsNotBipartiteException;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.impl.KBest_Algorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.impl.SecondBestMatchingAlgorithm_Algorithm2;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.impl.TopKAlgorithm;
import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.meta.match.MatchMatrix;

/**
 * <p>
 * Title:SchemaMatchingAlgorithmsRunner
 * </p>
 * <p>
 * Description:used for the k best algorithm running (imported by the "Schema Matchings Wrapper")
 * </p>
 * Implements {@link TKM}
 * 
 * @author Haggai Roitman
 * @version 1.1
 */
public final class SchemaMatchingAlgorithmsRunner implements TKM
{
	private MatchInformation orig; //MatchInformation before any transformations
    /** the schema we want to translate */
    private long[] initialSchemataIDs;
    /** the matched to schema */
    private long[] matchToSchemataIDs;
    /** adjacency matrix - given from OntoBuilder's output */
    private double[][] adjMatrix;
    /** reference to the K-best algorithm */
    private TopKAlgorithm kba;
    /** the associated bipartite graph for the match */
    private BipartiteGraph bg;
    /** the best match in the graph */
    private Set<Edge> bestMatching;
    /** next match index */
    private int k = 0;
    /** holds the best matches */
    private LinkedList<MatchInformation> bestMatches = new LinkedList<MatchInformation>();
    /** flags if invoked second best algorithm */
    private boolean usedSecondBestAlgorithm = false;
    /** batch properties */
    private static Properties smrProperties;
    /** flags if runner needs to accumulate all best mappings */
    private static boolean accumulate = false;

    static
    {
        try
        {
            smrProperties = new Properties();
            smrProperties.load(new FileInputStream("SMR.PROPERTIES"));
            accumulate = Boolean.valueOf(smrProperties.getProperty("accumilate", "true"))
                .booleanValue();
        }
        catch (Throwable e)
        {
            // ignore - use default
        }
    }

    /**
     * Construct a SchemaMatchingAlgorithmsRunner
     * 
     * @param candidateSchemata the initial candidate schema attributes names
     * @param targetSchemata the initial target schema attributes names
     * @param weightsMatrix
     * @deprecated use MatchInformation version
     * @throws GraphIsNotBipartiteException
     */
    public SchemaMatchingAlgorithmsRunner(long[] candidateSchemata, long[] targetSchemata,
    		MatchMatrix matchMatrix) throws TKMInitializationException
    {
        try
        {
            setInitialSchema(candidateSchemata);
            setMatchedSchema(targetSchemata, matchMatrix);
        }
        catch (Throwable e)
        {
            throw new TKMInitializationException(e.getMessage());
        }
    }
    
    public SchemaMatchingAlgorithmsRunner(long[] candidateSchemata, long[] targetSchemata,
    		MatchInformation mi) throws TKMInitializationException
    {
        try
        {
            setInitialSchema(candidateSchemata);
            setMatchedSchema(targetSchemata, mi.getMatrix());
            orig = mi;
        }
        catch (Throwable e)
        {
            throw new TKMInitializationException(e.getMessage());
        }
    }

    /**
     * Construct a default SchemaMatchingAlgorithmsRunner
     */
    public SchemaMatchingAlgorithmsRunner()
    {
    }

    /**
     * Releases resources
     */
    public void nullify()
    {
        try
        {
            initialSchemataIDs = null;
            matchToSchemataIDs = null;
            adjMatrix = null;
            kba.nullify();
            bg.nullify();
            bestMatching.clear();
        }
        catch (NullPointerException e)
        {
        }
    }

    /**
     * Sets the initial schema
     * 
     * @param schemata the initial schema attributes names
     */
    public void setInitialSchema(long[] schemata)
    {
        if (schemata == null)
            throw new NullPointerException();
        if (schemata.length == 0)
            throw new IllegalArgumentException("Schemata deg should be > 0");
        initialSchemataIDs = schemata;
        k = 0;
        usedSecondBestAlgorithm = false;
        if (!bestMatches.isEmpty())
            bestMatches.clear();
    }

    /**
     * Sets the matched schema
     * 
     * @param schemata the matched to schema attributes names
     * @param weightsMatrix - the matrix from OntoBuilder
     * @throws GraphIsNotBipartiteException - the given graph of the problem is not bipartite
     */
    public void setMatchedSchema(long[] schemata, MatchMatrix matchMatrix)
        throws TKMInitializationException
    {
        try
        {
            if (schemata == null || matchMatrix == null)
                throw new NullPointerException();
            if (schemata.length == 0)
                throw new IllegalArgumentException("Schemata deg should be > 0");
            k = 0;
            usedSecondBestAlgorithm = false;
            if (!bestMatches.isEmpty())
                bestMatches.clear();
            matchToSchemataIDs = schemata;
//            createGraphAdjMatrix(matchMatrix.getMatchMatrix(), matchToSchemataIDs.length, initialSchemataIDs.length);
          /*
             * We create the adj matrix inline to make sure that we get the correct mapping between
             * nodes IDs and term IDs
             */
            adjMatrix = new double[matchToSchemataIDs.length + initialSchemataIDs.length][matchToSchemataIDs.length + initialSchemataIDs.length];
            for (int i = 0; i < initialSchemataIDs.length; i++)
                for (int j = 0; j < initialSchemataIDs.length; j++)
                    adjMatrix[i][j] = (i == j) ? 0 : Graph.INF;
            for (int i = 0; i < initialSchemataIDs.length; i++)
                for (int j = initialSchemataIDs.length; j < matchToSchemataIDs.length + initialSchemataIDs.length; j++)
                    adjMatrix[i][j] = matchMatrix.getMatchConfidenceByID(initialSchemataIDs[i], matchToSchemataIDs[j - initialSchemataIDs.length]);
                    
            for (int i = initialSchemataIDs.length; i < matchToSchemataIDs.length + initialSchemataIDs.length; i++)
                for (int j = 0; j < matchToSchemataIDs.length + initialSchemataIDs.length; j++)
                    adjMatrix[i][j] = (i == j) ? 0 : Graph.INF;

            List<Long> rn = new ArrayList<Long>();
            List<Long> ln = new ArrayList<Long>();
            String[] rnNames = new String[matchToSchemataIDs.length];
            String[] lnNames = new String[initialSchemataIDs.length];
            for (int i = 0; i < matchToSchemataIDs.length; i++) {
                rn.add(i, matchToSchemataIDs[i]);
                rnNames[i] = matchMatrix.getTermNameByID(matchToSchemataIDs[i], false);
            }
            for (int i = 0; i < initialSchemataIDs.length; i++) {
                ln.add(i, initialSchemataIDs[i]);
                lnNames[i] = matchMatrix.getTermNameByID(initialSchemataIDs[i], true);
            }
            
            bg = GraphFactory.buildBipartiteGraph(adjMatrix, rn, ln, matchToSchemataIDs.length,
                initialSchemataIDs.length, true, rnNames, lnNames);
            bg.buildAdjMatrix();
            kba = new KBest_Algorithm(bg);
            bestMatching = kba.getNextMatching(true);
        }
        catch (Throwable e)
        {
            throw new TKMInitializationException(e.getMessage());
        }
    }

    /**
     * Get the Top-K algorithm
     * 
     * @return reference to the K best algorithm
     */
    public TopKAlgorithm getAlgorithm()
    {
        return kba;
    }

//    /**
//     * Creates the graph adjacency matrix from the OntoBuilder matrix
//     * 
//     * @param m adjacency matrix
//     * @param row - row count
//     * @param col - column count
//     */
//    private void createGraphAdjMatrix(double[][] m, int row, int col)
//    {
//        adjMatrix = new double[row + col][row + col];
//        for (int i = 0; i < col; i++)
//            for (int j = 0; j < col; j++)
//                adjMatrix[i][j] = (i == j) ? 0 : Graph.INF;
//        for (int i = 0; i < col; i++)
//            for (int j = col; j < row + col; j++)
//                adjMatrix[i][j] = m[j - col][i];
//        for (int i = col; i < row + col; i++)
//            for (int j = 0; j < row + col; j++)
//                adjMatrix[i][j] = (i == j) ? 0 : Graph.INF;
//    }

    /**
     * Returns a matchinformation for the next best matching
     * 
     * @return translator
     */
    public MatchInformation getBestMatching()
    {
    	MatchInformation res = new MatchInformation(orig.getCandidateOntology(), orig.getTargetOntology());
        res.setMatches(preparePairs(bestMatching));
        if (k == 0)
            k++;
        // bestMatches.add(k-1,st);//old version...
        if (accumulate)
            bestMatches.addLast(res);
        return res;
    }

    /**
     * Prepares the matchs schema translator
     * 
     * @param matching
     * @return the matched attributes pairs list
     */
    private ArrayList<Match> preparePairs(Set<Edge> matching)
    {// O(E)
    	ArrayList<Match> m = new ArrayList<Match>();
        MatchMatrix mm =  orig.getMatrix();
        for (Edge e : matching)
        {
            Term cTerm = mm.getTermByID(bg.getVertex(e.getSourceVertexID()).getVertexNameID(), true);
            Term tTerm = mm.getTermByID(bg.getVertex(e.getTargetVertexID()).getVertexNameID(), false);
            m.add(new Match(cTerm,tTerm,e.getEdgeWeight()));
        }
        return m;
    }

    /**
     * Returns the next best matching
     * 
     * @return schema translator for the next best matching
     */
    public MatchInformation getNextBestMatching(boolean openFronter) throws TKMRunningException
    {
        try
        {
            k++;
            if (k == 1)
                return getBestMatching();
            MatchInformation res = new MatchInformation(orig.getCandidateOntology(),orig.getTargetOntology());
            if (usedSecondBestAlgorithm)
            {
                usedSecondBestAlgorithm = false;
                kba.getNextMatching(openFronter);// calculates again the second best before the
                                                 // third
            }

            res.setMatches(preparePairs(kba.getNextMatching(openFronter)));
            // bestMatches.add(k-1,st);//old version
            if (accumulate)
                bestMatches.addLast(res);
            return res;
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            throw new TKMRunningException(e.getMessage());
        }
    }

    /**
     * Get the previous best matching
     * 
     * @param openFronter
     * @return schema translator for the previous best matching
     * @throws TKMRunningException
     */
    public MatchInformation getPreviousBestMatching(boolean openFronter) throws TKMRunningException
    {
        if (k > 1)
            --k;
        return getKthBestMatching(k, openFronter);
    }

    /**
     * Get the local second best mapping
     */
    public MatchInformation getLocalSecondBestMapping()
    {
    	MatchInformation res = new MatchInformation(orig.getCandidateOntology(), orig.getTargetOntology());
        bestMatching = kba.getLocalSecondBestMatching();
        res.setMatches(preparePairs(bestMatching));
        return res;
    }

    /**
     * Get the next heuristic mapping
     * 
     * @return the mapping
     */
    public Vector<MatchInformation> getNextHeuristicMappings(byte nonUniformVersion)
        throws TKMRunningException
    {
        Vector<MatchInformation> mappings = new Vector<MatchInformation>();
        List<Set<Edge>> matchings;
        try
        {
            matchings = kba.getNextHeuristicMatchings(nonUniformVersion);
            for (Set<Edge> eSet : matchings)
            {
            	MatchInformation res = new MatchInformation(orig.getCandidateOntology(),orig.getTargetOntology());
                res.setMatches(preparePairs(eSet));
                mappings.add(res);
            }
            return mappings;
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            throw new TKMRunningException(e.getMessage());
        }
    }

    /**
     * Get the adjacency matrix
     * 
     * @return the adjacency matrix
     */
    public double[][] getAdjMatrix()
    {
        return adjMatrix;
    }

    /**
     * Returns the k-th best matching , if the k best matching is not in its matches data store, it
     * will run best matches until the given k best match is created.
     * 
     * @param k the i-th best mapping to get to
     * @return the K-th best matching
     */
    public MatchInformation getKthBestMatching(int k, boolean openFronter)
        throws TKMRunningException
    {
        if (k < 1)
            throw new IllegalArgumentException("K parameter should be > 1!");
        if (this.k < k)
        {
        	MatchInformation res = null;
            while (this.k < k)
            {
                res = getNextBestMatching(openFronter);
            }
            return res;
        }
        else
            return bestMatches.get(k - 1);
    }

    /**
     * Resets the runner
     */
    public void reset(long[] candidateSchemata, long[] targetSchemata, MatchMatrix matchMatrix)
        throws TKMInitializationException
    {
        if (candidateSchemata == null || targetSchemata == null || matchMatrix == null)
            throw new NullPointerException();
        if (candidateSchemata.length == 0 || targetSchemata.length == 0)
            throw new IllegalArgumentException("Schemata deg should be > 0");
        k = 0;
        usedSecondBestAlgorithm = false;
        if (!bestMatches.isEmpty())
            bestMatches.clear();
        setInitialSchema(candidateSchemata);
        setMatchedSchema(targetSchemata, matchMatrix);
    }

    /**
     * Returns the second best matching
     * 
     * @param useSecondBestAlgorithm flags if to use a better algorithm for second best
     * matching.O(V^3)<br>
     */
    public MatchInformation getSecondBestMatching(boolean useSecondBestAlgorithm,
        boolean openFronter) throws TKMRunningException
    {
        if (k >= 2 || !useSecondBestAlgorithm)
            return getKthBestMatching(2, openFronter);
        else
        {// k <= 1 && useSecondBestAlgorithm
            DGraph dGraph;
            if (k == 0)
            {
                dGraph = GraphFactory.buildDgraph((BipartiteGraph) bg.clone());
                Set<Edge> bestMatch = dGraph.getBestMatching();
                // bestMatches.add(0,new SchemaTranslator(matchedPairs));//old version
                if (accumulate)
                {
                	MatchInformation res = new MatchInformation(orig.getCandidateOntology(),orig.getTargetOntology()); 
                	res.setMatches(preparePairs(bestMatch));
                	bestMatches.addFirst(res);
                }
                    
            }
            else
                dGraph = GraphFactory.buildDgraph((BipartiteGraph) bg.clone(), bestMatching);
            SecondBestMatchingAlgorithm_Algorithm2 sbma = new SecondBestMatchingAlgorithm_Algorithm2(
                dGraph);
            Set<Edge> secondBestMatch = sbma.runAlgorithm();
            MatchInformation res = new MatchInformation(orig.getCandidateOntology(),orig.getTargetOntology()); 
        	res.setMatches(preparePairs(secondBestMatch));
            k = 2;
            // bestMatches.add(1,st);//old version
            if (accumulate)
                bestMatches.addLast(res);
            usedSecondBestAlgorithm = true;
            return res;
        }
        
    }

    // need to figure out the initiation should be here??
    /**
     * Initialisation
     */
    public void init(MatchMatrix matrix)
    {
        if (matrix == null)
            throw new NullPointerException("matrix = null");

    }

    /**
     * Get the next best mapping
     */
    public MatchInformation getNextBestMapping(boolean openFronter) throws TKMRunningException
    {
        return getNextBestMatching(openFronter);// will create new Mapping Object
    }

    /**
     * Get the previous best mapping
     */
    public MatchInformation getPreviousBestMapping(boolean openFronter) throws TKMRunningException
    {
        return getPreviousBestMatching(openFronter);// will create new Mapping Object
    }

    /**
     * Get the K-th best mapping
     */
    public MatchInformation getKthBestMapping(int k, boolean openFronter) throws TKMRunningException
    {
        return getKthBestMatching(k, openFronter);// will create new Mapping Object
    }

    /**
     * Set the accumulation mode
     * 
     * @param mode the mode
     */
    public static void setAccumulationMode(boolean mode)
    {
        accumulate = mode;
    }

}
