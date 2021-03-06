package ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.utils.VertexArray;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.impl.FastMWBGAlgorithm;


/**
 * <p>
 * Title: GraphFactory
 * </p>
 * <p>
 * Description: uses for graphs construction
 * </p>
 * 
 * @author Haggai Roitman
 * @version 1.1
 */

public abstract class GraphFactory
{

    /**
     * Builds a general graph
     * 
     * @param adjMatrix - Adjacency matrix of the graph
     * @param vertexNameIDs - vertexes name IDs of the graph
     * @param vertexNames - vertexes names of the graph
     * @param vertexCount - vertex count in the graph
     * @param dirGraph - <code>true</code> if it is a directed graph
     * @return built graph
     */
    public final static Graph buildGraph(double[][] adjMatrix, long[] vertexNameIDs, String[] vertexNames,
        int vertexCount, boolean dirGraph)
    {
    	Set<Vertex> vg = new HashSet<Vertex>();
        Set<Edge> eg = new HashSet<Edge>();
        for (int i = 0; i < vertexCount; i++)
        {// O(V)
            vg.add(new Vertex(i, vertexNameIDs[i], vertexNames[i]));
        }
        for (int i = 0; i < vertexCount; i++)
            // O(V^2)
            for (int j = 0; j < vertexCount; j++)
                if (adjMatrix[i][j] != 0 && adjMatrix[i][j] != Graph.INF)
                    eg.add(new Edge(i, j, adjMatrix[i][j], dirGraph));
        return new Graph(eg, vg);
    }

    /**
     * Builds a bipartite graph<br>
     * left vertexes group represent the "sources" and right the "targets"
     * 
     * @param adjMatrix - Adjacency matrix of the graph
     * @param rightVertexIDs - the IDs of the right vertexes of the graph
     * @param leftVertexIDs - the IDs of the left vertexes of the graph
     * @param rightVertexCount - right vertexes count
     * @param leftVertexCount - left vertexes count
     * @param dirGraph - <code>true</code> if it is directed graph
     * @return built bipartite graph
     * @throws GraphIsNotBipartiteException - given graph to construct is not a bipartite graph
     */
    public final static BipartiteGraph buildBipartiteGraph(double[][] adjMatrix,
        List<Long> rightVertexIDs, List<Long> leftVertexIDs,
        int rightVertexCount, int leftVertexCount, boolean dirGraph, 
        String[] rightVertexNames, String[] leftVertexNames
    		)
        throws GraphIsNotBipartiteException
    {// O(V^2+E)
        
    	
    	if (!isBipartiteGraph(adjMatrix, rightVertexCount, leftVertexCount))
            throw new GraphIsNotBipartiteException(adjMatrix, rightVertexCount, leftVertexCount);
    	Set<Vertex> rvg = new HashSet<Vertex>();
    	Set<Vertex> lvg = new HashSet<Vertex>();
        if (rightVertexCount != leftVertexCount)
        {
            boolean rightVertexesSetBigger = (rightVertexCount > leftVertexCount) ? true : false;
            if (rightVertexesSetBigger)
            {
                int numOfDummyVertexesToAdd = rightVertexCount - leftVertexCount;
                adjMatrix = rightBiggerNewAdjMatrixConstruction(adjMatrix, numOfDummyVertexesToAdd,
                    leftVertexCount, rightVertexCount);
                leftVertexCount += numOfDummyVertexesToAdd;
                for (int i = 0; i < numOfDummyVertexesToAdd; i++)
                    leftVertexIDs.add(BipartiteGraph.ID_DUMMY_VERTEX);
            }
            else
            {
                int numOfDummyVertexesToAdd = leftVertexCount - rightVertexCount;
                adjMatrix = leftBiggerNewAdjMatrixConstruction(adjMatrix, numOfDummyVertexesToAdd,
                    leftVertexCount, rightVertexCount);
                rightVertexCount += numOfDummyVertexesToAdd;
                for (int i = 0; i < numOfDummyVertexesToAdd; i++)
                    rightVertexIDs.add(rightVertexCount - numOfDummyVertexesToAdd + i, BipartiteGraph.ID_DUMMY_VERTEX);
            }
        }
        Set<Edge> eg = new HashSet<Edge>();
        for (int i = 0; i < leftVertexCount; i++)
        {
            if (!(leftVertexIDs.get(i) == BipartiteGraph.ID_DUMMY_VERTEX ))
            {
                lvg.add(new Vertex(i, leftVertexIDs.get(i),leftVertexNames[i]));
            }
            else
            {
                lvg.add(new Vertex(i, BipartiteGraph.ID_DUMMY_VERTEX - i,"Dummy Vertex"));
            }
        }
        for (int i = leftVertexCount; i < (leftVertexCount + rightVertexCount); i++)
        {
            if (!(rightVertexIDs.get(i - leftVertexCount) == BipartiteGraph.ID_DUMMY_VERTEX))
                rvg.add(new Vertex(i, rightVertexIDs.get(i - leftVertexCount), rightVertexNames[i - leftVertexCount]));
            else
                rvg.add(new Vertex(i, BipartiteGraph.ID_DUMMY_VERTEX - i,"Dummy Vertex"));
        }
        for (int i = 0; i < (rightVertexCount + leftVertexCount); i++)
            // O(V^2)
            for (int j = 0; j < (rightVertexCount + leftVertexCount); j++)
                if (adjMatrix[i][j] != Graph.INF && i != j)
                    eg.add(new Edge(i, j, adjMatrix[i][j], dirGraph));
        return new BipartiteGraph(eg, rvg, lvg);
    }

    /**
     * Checks if given graph is a bipartite graph
     * 
     * @param a - Adjacency matrix
     * @param r - right vertexes count
     * @param l - left vertexes count
     * @return <code>true</code> if the given graph to check is a bipartite graph
     */
    private final static boolean isBipartiteGraph(double[][] a, int r, int l)
    {// O(V^2)
        for (int i = 0; i < l; i++)
            for (int j = 0; j < l; j++)
            {
                if (i == j && a[i][j] != 0)
                    return false;
                if (i != j && a[i][j] != Graph.INF)
                    return false;
            }
        for (int i = 0; i < l; i++)
            for (int j = l; j < l + r; j++)
                if (a[i][j] == Graph.INF || a[i][j] < 0)
                    return false;
        for (int i = l; i < l + r; i++)
            for (int j = 0; j < l + r; j++)
            {
                if (i == j && a[i][j] != 0)
                    return false;
                if (i != j && a[i][j] != Graph.INF)
                    return false;
            }
        return true;// Bipartite Graph
    }

    /**
     * Builds Dgraph for a given bipartite graph
     * 
     * @param bg - associated {@link BipartiteGraph} to deviate the Dgraph
     * @return built Dgraph
     */
    public final static DGraph buildDgraph(BipartiteGraph bg)
    {
        /****** new version 14/11/03 ******/
        VertexArray pot = new VertexArray(bg, new Double(0));
        FastMWBGAlgorithm algo = new FastMWBGAlgorithm(bg, pot);
        /****** new version 14/11/03 ******/
        Set<Edge> bestMatching = algo.runAlgorithm();
        return new DGraph(bg, bestMatching);
    }

    /**
     * Builds Dgraph for a given bipartite graph and best matching
     * 
     * @param bg - associated {@link BipartiteGraph} to deviate the Dgraph
     * @param bestMatching - best matching in the graph
     * @return built Dgraph
     */
    public final static DGraph buildDgraph(BipartiteGraph bg, Set<Edge> bestMatching)
    {
        return new DGraph(bg, bestMatching);
    }

    /**
     * builds complete adjacency matrix for a given bipartite graph when left vertexes group is
     * bigger than the right one
     * 
     * @param a - adjacency matrix
     * @param k - dummy vertexes that were added to graph
     * @param l - left vertex count
     * @param r - right vertex count
     * @return complete bipartite adjacency matrix
     */
    private final static double[][] leftBiggerNewAdjMatrixConstruction(double[][] a, int k, int l,
        int r)
    {// O(V^2)
        double[][] na = new double[l * 2][l * 2];
        for (int i = 0; i < l + r; i++)
            for (int j = 0; j < r + l; j++)
                na[i][j] = a[i][j];
        for (int i = 0; i < l; i++)
            for (int j = r + l; j < r + l + k; j++)
                na[i][j] = 0;
        for (int i = l; i < l + r; i++)
            for (int j = r + l; j < r + l + k; j++)
                na[i][j] = Graph.INF;
        for (int i = r + l; i < l + r + k; i++)
            for (int j = 0; j < r + l; j++)
                na[i][j] = Graph.INF;
        for (int i = r + l; i < l + r + k; i++)
            for (int j = r + l; j < r + l + k; j++)
                na[i][j] = i == j ? 0 : Graph.INF;
        return na;
    }

    /**
     * builds complete adjacency matrix for a given bipartite graph when right vertexes group is
     * bigger than the left one
     * 
     * @param a - adjacency matrix
     * @param k - dummy vertexes that were added to graph
     * @param l - left vertex count
     * @param r - right vertex count
     * @return complete bipartite adjacency matrix
     */
    private final static double[][] rightBiggerNewAdjMatrixConstruction(double[][] a, int k, int l,
        int r)
    {// O(V^2)
        double[][] na = new double[r * 2][r * 2];
        for (int i = 0; i < l; i++)
            for (int j = 0; j < l; j++)
                na[i][j] = a[i][j];
        for (int i = 0; i < l; i++)
            for (int j = l; j < l + k; j++)
                na[i][j] = Graph.INF;
        for (int i = 0; i < l; i++)
            for (int j = k + l; j < r + l + k; j++)
                na[i][j] = a[i][j - k];
        for (int i = l; i < l + r + k; i++)
            for (int j = 0; j < k + l; j++)
                na[i][j] = i == j ? 0 : Graph.INF;
        for (int i = l; i < l + k; i++)
            for (int j = k + l; j < r + l + k; j++)
                na[i][j] = 0;
        for (int i = l + k; i < l + k + r; i++)
            for (int j = k + l; j < r + l + k; j++)
                na[i][j] = i == j ? 0 : Graph.INF;
        return na;
    }

}