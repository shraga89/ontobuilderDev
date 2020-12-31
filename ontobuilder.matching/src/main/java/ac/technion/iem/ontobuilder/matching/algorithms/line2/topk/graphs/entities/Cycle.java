package ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities;

import java.util.Collection;
import java.util.Iterator;

/**
 * <p>
 * Title: Cycle
 * </p>
 * <p>
 * Description: representation of a cycled path in graph
 * </p>
 * Extends {@link Path}
 * 
 * @author Haggai Roitman
 * @version 1.1
 */

public class Cycle extends Path
{

    /**
     * Constructs a Cycle
     * 
     * @param p - {@link Path} to close
     * @param closingCycleEdge - the cycle closing {@link Edge}
     * @param vc - vertexes count in graph
     */
    public Cycle(Path p, Edge closingCycleEdge, int vc)
    {// O(E)
        super(p.getPathWeight(), vc);
        pathEdges = p.getPath();
        addFollowingEdgeToPath(closingCycleEdge);
        pathWeight += closingCycleEdge.getEdgeWeight();
    }

    /**
     * Release resources
     */
    public void nullify()
    {
        try
        {
            super.nullify();
        }
        catch (NullPointerException e)
        {
        }
    }

    /**
     * Get a printable string of the cycle
     * 
     * @return printable string of the cycle
     */
    public String printCycle()
    {
        return printPath();
    }

    /**
     * Get the minimum cycle group in the Dgraph
     * 
     * @param cycles - minimum {@link Cycle} group in the Dgraph
     * @return the smaller cycle of the group
     */
    public static Cycle getMinCycle(Collection<Cycle> cycles)
    {// O(cycles)
        if (cycles.isEmpty())
            return null;
        Iterator<Cycle> it = cycles.iterator();
        Cycle minCyc = (Cycle) it.next();
        while (it.hasNext())
        {
            Cycle tmpC = (Cycle) it.next();
            minCyc = (minCyc.getPathWeight() <= tmpC.getPathWeight()) ? minCyc : tmpC;
        }
        return minCyc;
    }
}