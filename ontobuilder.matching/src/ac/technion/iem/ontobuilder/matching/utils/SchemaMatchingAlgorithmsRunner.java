package ac.technion.iem.ontobuilder.matching.utils;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Vector;

import ac.technion.iem.ontobuilder.matching.algorithms.line2.tkm.TKM;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.tkm.TKMInitializationException;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.tkm.TKMRunningException;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.BipartiteGraph;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.DGraph;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.Edge;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.EdgesSet;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.Graph;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.GraphFactory;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.impl.KBest_Algorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.impl.SecondBestMatchingAlgorithm_Algorithm2;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.impl.TopKAlgorithm;
import ac.technion.iem.ontobuilder.matching.meta.match.AbstractMapping;
import ac.technion.iem.ontobuilder.matching.meta.match.AbstractMatchMatrix;
import ac.technion.iem.ontobuilder.matching.meta.match.MatchedAttributePair;

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
    /** the schema we want to translate */
    private String[] initialSchemata;
    /** the matched to schema */
    private String[] matchToSchemata;
    /** adjacency matrix - given from OntoBuilder's output */
    private double[][] adjMatrix;
    /** reference to the K-best algorithm */
    private TopKAlgorithm kba;
    /** the associated bipartite graph for the match */
    private BipartiteGraph bg;
    /** the best match in the graph */
    private EdgesSet bestMatching;
    /** next match index */
    private int k = 0;
    /** holds the best matches */
    private LinkedList<SchemaTranslator> bestMatches = new LinkedList<SchemaTranslator>();
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
     * @throws GraphIsNotBipartiteException
     */
    public SchemaMatchingAlgorithmsRunner(String[] candidateSchemata, String[] targetSchemata,
        double[][] weightsMatrix) throws TKMInitializationException
    {
        try
        {
            setInitialSchema(candidateSchemata);
            setMatchedSchema(targetSchemata, weightsMatrix);
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
            initialSchemata = null;
            matchToSchemata = null;
            adjMatrix = null;
            kba.nullify();
            bg.nullify();
            bestMatching.nullify();
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
    public void setInitialSchema(String[] schemata)
    {
        if (schemata == null)
            throw new NullPointerException();
        if (schemata.length == 0)
            throw new IllegalArgumentException("Schemata deg should be > 0");
        initialSchemata = schemata;
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
    public void setMatchedSchema(String[] schemata, double[][] weightsMatrix)
        throws TKMInitializationException
    {
        try
        {
            if (schemata == null || weightsMatrix == null)
                throw new NullPointerException();
            if (schemata.length == 0)
                throw new IllegalArgumentException("Schemata deg should be > 0");
            k = 0;
            usedSecondBestAlgorithm = false;
            if (!bestMatches.isEmpty())
                bestMatches.clear();
            matchToSchemata = schemata;
            createGraphAdjMatrix(weightsMatrix, matchToSchemata.length, initialSchemata.length);
            ArrayList<String> rn = new ArrayList<String>();
            ArrayList<String> ln = new ArrayList<String>();
            for (int i = 0; i < matchToSchemata.length; i++)
                rn.add(i, matchToSchemata[i]);
            for (int i = 0; i < initialSchemata.length; i++)
                ln.add(i, initialSchemata[i]);
            bg = GraphFactory.buildBipartiteGraph(adjMatrix, rn, ln, matchToSchemata.length,
                initialSchemata.length, true);
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

    /**
     * Creates the graph adjacency matrix from the OntoBuilder matrix
     * 
     * @param m adjacency matrix
     * @param row - row count
     * @param col - column count
     */
    private void createGraphAdjMatrix(double[][] m, int row, int col)
    {
        adjMatrix = new double[row + col][row + col];
        for (int i = 0; i < col; i++)
            for (int j = 0; j < col; j++)
                adjMatrix[i][j] = (i == j) ? 0 : Graph.INF;
        for (int i = 0; i < col; i++)
            for (int j = col; j < row + col; j++)
                adjMatrix[i][j] = m[j - col][i];
        for (int i = col; i < row + col; i++)
            for (int j = 0; j < row + col; j++)
                adjMatrix[i][j] = (i == j) ? 0 : Graph.INF;
    }

    /**
     * Returns a translator for the next best matching
     * 
     * @return translator
     */
    public SchemaTranslator getBestMatching()
    {
        SchemaTranslator st = new SchemaTranslator();
        st.setSchemaPairs(preparePairs(bestMatching));
        if (k == 0)
            k++;
        // bestMatches.add(k-1,st);//old version...
        if (accumulate)
            bestMatches.addLast(st);
        return st;
    }

    /**
     * Prepares the matched attribute pairs for the schema translator
     * 
     * @param matching
     * @return the matched attributes pairs list
     */
    private MatchedAttributePair[] preparePairs(EdgesSet matching)
    {// O(E)
        MatchedAttributePair[] mp = new MatchedAttributePair[matching.getMembers().size()];
        Iterator<Edge> it = matching.getMembers().iterator();
        for (int i = 0; it.hasNext(); i++)
        {
            Edge e = (Edge) it.next();
            mp[i] = new MatchedAttributePair(bg.getVertex(e.getSourceVertexID()).getVertexName(),
                bg.getVertex(e.getTargetVertexID()).getVertexName(), e.getEdgeWeight());
        }
        return mp;
    }

    /**
     * Returns the next best matching
     * 
     * @return schema translator for the next best matching
     */
    public SchemaTranslator getNextBestMatching(boolean openFronter) throws TKMRunningException
    {
        try
        {
            k++;
            if (k == 1)
                return getBestMatching();
            SchemaTranslator st = new SchemaTranslator();
            if (usedSecondBestAlgorithm)
            {
                usedSecondBestAlgorithm = false;
                kba.getNextMatching(openFronter);// calculates again the second best before the
                                                 // third
            }

            st.setSchemaPairs(preparePairs(kba.getNextMatching(openFronter)));
            // bestMatches.add(k-1,st);//old version
            if (accumulate)
                bestMatches.addLast(st);
            return st;
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
    public SchemaTranslator getPreviousBestMatching(boolean openFronter) throws TKMRunningException
    {
        if (k > 1)
            --k;
        return getKthBestMatching(k, openFronter);
    }

    /**
     * Get the local second best mapping
     */
    public AbstractMapping getLocalSecondBestMapping()
    {
        bestMatching = kba.getLocalSecondBestMatching();
        SchemaTranslator st = new SchemaTranslator();
        st.setSchemaPairs(preparePairs(bestMatching));
        return st;
    }

    /**
     * Get the next heuristic mapping
     * 
     * @return the mapping
     */
    public Vector<AbstractMapping> getNextHeuristicMappings(byte nonUniformVersion)
        throws TKMRunningException
    {
        Vector<AbstractMapping> mappings = new Vector<AbstractMapping>();
        Vector<EdgesSet> matchings;
        try
        {
            matchings = kba.getNextHeuristicMatchings(nonUniformVersion);
            Iterator<EdgesSet> it = matchings.iterator();
            SchemaTranslator st;
            while (it.hasNext())
            {
                st = new SchemaTranslator();
                st.setSchemaPairs(preparePairs((EdgesSet) it.next()));
                mappings.add(st);
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
    public SchemaTranslator getKthBestMatching(int k, boolean openFronter)
        throws TKMRunningException
    {
        if (k < 1)
            throw new IllegalArgumentException("K parameter should be > 1!");
        if (this.k < k)
        {
            SchemaTranslator st = null;
            while (this.k < k)
            {
                st = getNextBestMatching(openFronter);
            }
            return st;
        }
        else
            return (SchemaTranslator) bestMatches.get(k - 1);
    }

    /**
     * Resets the runner
     */
    public void reset(String[] candidateSchemata, String[] targetSchemata, double[][] weightsMatrix)
        throws TKMInitializationException
    {
        if (candidateSchemata == null || targetSchemata == null || weightsMatrix == null)
            throw new NullPointerException();
        if (candidateSchemata.length == 0 || targetSchemata.length == 0)
            throw new IllegalArgumentException("Schemata deg should be > 0");
        k = 0;
        usedSecondBestAlgorithm = false;
        if (!bestMatches.isEmpty())
            bestMatches.clear();
        setInitialSchema(candidateSchemata);
        setMatchedSchema(targetSchemata, weightsMatrix);
    }

    /**
     * Returns the second best matching
     * 
     * @param useSecondBestAlgorithm flags if to use a better algorithm for second best
     * matching.O(V^3)<br>
     */
    public SchemaTranslator getSecondBestMatching(boolean useSecondBestAlgorithm,
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
                EdgesSet bestMatch = dGraph.getBestMatching();
                MatchedAttributePair[] matchedPairs = preparePairs(bestMatch);
                // bestMatches.add(0,new SchemaTranslator(matchedPairs));//old version
                if (accumulate)
                    bestMatches.addFirst(new SchemaTranslator(matchedPairs));
            }
            else
                dGraph = GraphFactory.buildDgraph((BipartiteGraph) bg.clone(), bestMatching);
            SecondBestMatchingAlgorithm_Algorithm2 sbma = new SecondBestMatchingAlgorithm_Algorithm2(
                dGraph);
            EdgesSet secondBestMatch = sbma.runAlgorithm();
            MatchedAttributePair[] matchedPairs = preparePairs(secondBestMatch);
            SchemaTranslator st = new SchemaTranslator(matchedPairs);
            k = 2;
            // bestMatches.add(1,st);//old version
            if (accumulate)
                bestMatches.addLast(st);
            usedSecondBestAlgorithm = true;
            return st;
        }
    }

    // need to figure out the initiation should be here??
    /**
     * Initialisation
     */
    public void init(AbstractMatchMatrix matrix)
    {
        if (matrix == null)
            throw new NullPointerException("matrix = null");

    }

    /**
     * Get the next best mapping
     */
    public AbstractMapping getNextBestMapping(boolean openFronter) throws TKMRunningException
    {
        return getNextBestMatching(openFronter);// will create new Mapping Object
    }

    /**
     * Get the previous best mapping
     */
    public AbstractMapping getPreviousBestMapping(boolean openFronter) throws TKMRunningException
    {
        return getPreviousBestMatching(openFronter);// will create new Mapping Object
    }

    /**
     * Get the K-th best mapping
     */
    public AbstractMapping getKthBestMapping(int k, boolean openFronter) throws TKMRunningException
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
