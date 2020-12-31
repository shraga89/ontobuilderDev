package ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities;

import java.util.Iterator;
import java.util.Set;

public class EdgeUtil {

    public static double getEdgesSetWeight(Set<Edge> e)
    {// O(E)
        double weight = 0;
        Iterator<Edge> it = e.iterator();
        while (it.hasNext())
        {
            Edge eTmp = it.next();
            weight += eTmp.getEdgeWeight();
        }
        return weight;
    }

    public static Edge getEdgeThatStartsWith(int i, Set<Edge> members)
    {
        Edge e;
        Iterator<Edge> it = members.iterator();
        while (it.hasNext())
        {
            e = it.next();
            if (e.getSourceVertexID() == i)
                return e;
        }
        return null;
    }
    
    public static Edge getMaximalEdgeThatStartsWith(int i, Set<Edge> members)
    {
        Edge e;
        Edge maxEdge = null;
        Iterator<Edge> it = members.iterator();
        double maxWeight = Double.MIN_VALUE;
        while (it.hasNext())
        {
            e = it.next();
            if (e.getSourceVertexID() == i && e.getEdgeWeight() > maxWeight)
            {
                maxEdge = e;
                maxWeight = maxEdge.getEdgeWeight();
            }
        }
        return maxEdge;
    }
    
    public static void turnOverEdges(boolean weightTurn, Set<Edge> members)
    {// O(E)
        for (Edge e : members)
        {
            e.turnOverEdgeDirection();
            if (weightTurn)
                e.turnOverWeight();
        }
    }


}
