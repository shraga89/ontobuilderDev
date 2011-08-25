package ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities;

/**
 * <p>
 * Title: GraphIsNotBipartiteException
 * </p>
 * <p>
 * Description: Exception throw by Graph factory when given graph parameters don't belong to
 * bipartite graph
 * </p>
 * Extends {@link Exception}
 * 
 * @author Haggai Roitman
 * @version 1.1
 */

public class GraphIsNotBipartiteException extends GraphException
{
    private static final long serialVersionUID = 5259508132082972802L;

    /** adjacency matrix of the illegal graph */
    private double[][] adjMatrix;
    /** right vertexes group count */
    private int right;
    /** left vertexes group count */
    private int left;

    /**
     * @param a - adjacency matrix of the illegal graph
     * @param r - right vertexes group count
     * @param l - left vertexes group count
     */
    public GraphIsNotBipartiteException(double[][] a, int r, int l)
    {
        super(
            "GraphFactory\nGraphIsNotBipartiteException: The following graph is not a legal Bipartite Graph for GraphFactory:\n");
        adjMatrix = a;
        left = l;
        right = r;
    }

    /**
     * prints the adjacency matrix
     */
    public void printAdjMatrix()
    {
        for (int i = 0; i < left + right; i++)
        {
            for (int j = 0; j < right + left; j++)
                System.out.print("[" + adjMatrix[i][j] + "]");
            System.out.println();
        }
    }
}