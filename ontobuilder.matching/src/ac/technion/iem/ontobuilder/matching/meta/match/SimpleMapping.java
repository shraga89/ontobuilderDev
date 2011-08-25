package ac.technion.iem.ontobuilder.matching.meta.match;

import java.util.Iterator;

import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.Edge;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.EdgesSet;

/**
 * <p>Title: SimpleMapping</p>
 * Extends {@link AbstractMapping}
 * <br>Currently not in use
 */
public class SimpleMapping extends AbstractMapping
{

    private int[] mapping;

    /**
     * Constructs a SimpleMapping with an {@link EdgesSet}
     */
    public SimpleMapping(EdgesSet match)
    {
        Edge tmpEdge;
        mapping = new int[match.getVc() / 2];
        for (int i = 0; i < mapping.length; i++)
            mapping[i] = -1;
        for (Iterator<?> it = match.getMembers().iterator(); it.hasNext();)
        {
            tmpEdge = (Edge) it.next();
            mapping[tmpEdge.getSourceVertexID()] = tmpEdge.getTargetVertexID();
        }
        // debug
        printMapping();
    }

    /**
     * Get the total number of matched pairs
     * 
     * @return number of matched Attribute pairs
     */
    public int getTotalMatchedPairsCount()
    {
        int cnt = 0;
        for (int i = 0; i < mapping.length; i++)
            if (mapping[i] != -1)
                cnt++;
        return cnt;
    }

    /**
     * Prints the mapping
     */
    public void printMapping()
    {
        for (int i = 0; i < mapping.length; i++)
            System.out.println(i + " -> " + mapping[i]);
    }

    /**
     * Not implemented
     */
    public void nullify()
    {
    }

    /**
     * @return 0
     */
    public int hashCode()
    {
        return 0;
    }
}