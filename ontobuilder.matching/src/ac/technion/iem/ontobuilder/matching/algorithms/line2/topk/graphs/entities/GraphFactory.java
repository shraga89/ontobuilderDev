package ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities;

import java.util.ArrayList;

import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.utils.EdgeArray;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.utils.VertexArray;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.impl.MaxWeightBipartiteMatchingAlgorithm;


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
     * @param vertexNames - vertexes names of the graph
     * @param vertexCount - vertex count in the graph
     * @param dirGraph - <code>true</code> if it is a directed graph
     * @return built graph
     */
    public final static Graph buildGraph(double[][] adjMatrix, String[] vertexNames,
        int vertexCount, boolean dirGraph)
    {
        VertexesSet vg = new VertexesSet();
        EdgesSet eg = new EdgesSet(vertexCount);
        for (int i = 0; i < vertexCount; i++)
        {// O(V)
            vg.addMember(new Vertex(i, vertexNames[i]));
        }
        for (int i = 0; i < vertexCount; i++)
            // O(V^2)
            for (int j = 0; j < vertexCount; j++)
                if (adjMatrix[i][j] != 0 && adjMatrix[i][j] != Graph.INF)
                    eg.addMember(new Edge(i, j, adjMatrix[i][j], dirGraph));
        return new Graph(eg, vg);
    }

    /**
     * Builds a bipartite graph<br>
     * left vertexes group represent the "sources" and right the "targets"
     * 
     * @param adjMatrix - Adjacency matrix of the graph
     * @param rightVertexNames - the names of the right vertexes of the graph
     * @param leftVertexNames - the names of the left vertexes of the graph
     * @param rightVertexCount - right vertexes count
     * @param leftVertexCount - left vertexes count
     * @param dirGraph - <code>true</code> if it is directed graph
     * @return built bipartite graph
     * @throws GraphIsNotBipartiteException - given graph to construct is not a bipartite graph
     */
    public final static BipartiteGraph buildBipartiteGraph(double[][] adjMatrix,
        ArrayList<String> rightVertexNames, ArrayList<String> leftVertexNames,
        int rightVertexCount, int leftVertexCount, boolean dirGraph)
        throws GraphIsNotBipartiteException
    {// O(V^2+E)
        if (!isBipartiteGraph(adjMatrix, rightVertexCount, leftVertexCount))
            throw new GraphIsNotBipartiteException(adjMatrix, rightVertexCount, leftVertexCount);
        VertexesSet rvg = new VertexesSet();
        VertexesSet lvg = new VertexesSet();
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
                    leftVertexNames.add("DummyVertex");
            }
            else
            {
                int numOfDummyVertexesToAdd = leftVertexCount - rightVertexCount;
                adjMatrix = leftBiggerNewAdjMatrixConstruction(adjMatrix, numOfDummyVertexesToAdd,
                    leftVertexCount, rightVertexCount);
                rightVertexCount += numOfDummyVertexesToAdd;
                for (int i = 0; i < numOfDummyVertexesToAdd; i++)
                    rightVertexNames.add(rightVertexCount - numOfDummyVertexesToAdd + i,
                        "DummyVertex");
            }
        }
        EdgesSet eg = new EdgesSet(rightVertexCount + leftVertexCount);
        for (int i = 0; i < leftVertexCount; i++)
        {
            if (!((String) leftVertexNames.get(i)).equals("DummyVertex"))
            {
                lvg.addMember(new Vertex(i, (String) leftVertexNames.get(i)));
            }
            else
            {
                lvg.addMember(new Vertex(i, "DummyVertex " + i));
            }
        }
        for (int i = leftVertexCount; i < (leftVertexCount + rightVertexCount); i++)
        {
            if (!((String) rightVertexNames.get(i - leftVertexCount)).equals("DummyVertex"))
                rvg.addMember(new Vertex(i, (String) rightVertexNames.get(i - leftVertexCount)));
            else
                rvg.addMember(new Vertex(i, "DummyVertex " + i));
        }
        for (int i = 0; i < (rightVertexCount + leftVertexCount); i++)
            // O(V^2)
            for (int j = 0; j < (rightVertexCount + leftVertexCount); j++)
                if (adjMatrix[i][j] != Graph.INF && i != j)
                    eg.addMember(new Edge(i, j, adjMatrix[i][j], dirGraph));
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
        EdgeArray c = new EdgeArray(bg);
        VertexArray pot = new VertexArray(bg, new Double(0));
        MaxWeightBipartiteMatchingAlgorithm algo = new MaxWeightBipartiteMatchingAlgorithm(bg, c,
            pot);
        /****** new version 14/11/03 ******/
        EdgesSet bestMatching = algo.runAlgorithm();
        return new DGraph(bg, bestMatching);
    }

    /**
     * Builds Dgraph for a given bipartite graph and best matching
     * 
     * @param bg - associated {@link BipartiteGraph} to deviate the Dgraph
     * @param bestMatching - best matching in the graph
     * @return built Dgraph
     */
    public final static DGraph buildDgraph(BipartiteGraph bg, EdgesSet bestMatching)
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