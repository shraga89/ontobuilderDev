package ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * <p>
 * Title: Path
 * </p>
 * <p>
 * Description: represents a path in a graph
 * </p>
 * 
 * @author Haggai Roitman
 * @version 1.1
 */

public class Path
{

    /** the edges of the path */
    protected LinkedList<Edge> pathEdges = new LinkedList<Edge>();
    /** path total weight */
    protected double pathWeight;
    /** graph vertexes count */
    protected int vertexesCount = 0;

    /**
     * Construct a Path
     * 
     * @param pWeight - path weight
     * @param vc - vertexes count
     */
    public Path(double pWeight, int vc)
    {
        vertexesCount = vc;
        pathWeight = pWeight;
    }

    /**
     * Set the edges in the path
     * 
     * @param m the list of {@link Edge} to set
     */
    public void setPathMembers(LinkedList<Edge> m)
    {
        this.pathEdges = m;
    }

    /**
     * Release resources
     */
    public void nullify()
    {
        pathEdges = null;
    }

    /**
     * Add an edge to the path, done in a LIFO way in order to recover the path
     * 
     * @param next {@link Edge} to add to the path
     */
    public void addFollowingEdgeToPath(Edge next)
    {// O(1)
        pathEdges.addFirst(next);// LIFO stack- in order to recover the path
    }

    /**
     * Get the path
     * 
     * @return path - the list of {@link Edge}
     */
    public LinkedList<Edge> getPath()
    {
        return pathEdges;
    }

    /**
     * Get the edges in the path
     * 
     * @return the {@link EdgesSet} building the path
     */
    public EdgesSet getPathEdges()
    {// O(E)
        Iterator<Edge> it = pathEdges.iterator();
        EdgesSet toReturn = new EdgesSet(vertexesCount);
        while (it.hasNext())
        {
            toReturn.addMember(it.next());
        }
        return toReturn;
    }

    /**
     * Get the total weight of the path
     * 
     * @return path weight
     */
    public double getPathWeight()
    {
        return pathWeight;
    }

    /**
     * @return printable string of the path
     */
    public String printPath()
    {// O(V)
        String result = "";
        for (int i = 0; i < pathEdges.size(); i++)
        {
            Edge tmpE = (Edge) pathEdges.get(i);
            result += tmpE.printEdge();
        }
        return result;
    }

    /**
     * Clone the path
     * 
     * @return a new {@link Path}
     */
    public Path clone()
    {
        Path p = new Path(this.pathWeight, this.vertexesCount);
        p.setPathMembers(this.getPath());
        return p;
    }

}