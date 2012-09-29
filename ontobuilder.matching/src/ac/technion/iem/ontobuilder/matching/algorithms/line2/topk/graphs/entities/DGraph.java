package ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * <p>
 * Title:DGraph
 * </p>
 * <p>
 * Description: represents the transform bipartite graph of the second best Algorithm2
 * </p>
 * Extends {@link BipartiteGraph}
 * 
 * @author Haggai Roitman
 * @version 1.1
 */

public class DGraph extends BipartiteGraph
{
    private static final long serialVersionUID = -1884529504194465221L;

    /** E1 edges group */
    private Set<Edge> e1;
    /** E2 edges group */
    private Set<Edge> e2;
    /** the best matching of the bipartite graph from where the Dgraph was created */
    private Set<Edge> matching;

    /**
     * Constructs a DGraph
     * 
     * @param bg - associated {@link BipartiteGraph}
     * @param m - the best match in that bipartite graph
     */
    public DGraph(BipartiteGraph bg, Set<Edge> m)
    {// O(E+V^2)
        super(new HashSet<Edge>(), bg.getRightVertexesSet(), bg.getLeftVertexesSet());
        e1 = new HashSet<Edge>();
        e2 = new HashSet<Edge>();
        matching = m;
        setEdgesSet(bg.getEdgesSet());
        bulidE1();
        bulidE2();
        Set<Edge> union = new HashSet<Edge>(e1);
        union.addAll(e2);
        setEdgesSet(union);
        buildAdjMatrix();
    }

    /**
     * Release resources
     */
    public void nullify()
    {
        try
        {
            e1.clear();
            e2.clear();
            matching.clear();
            super.nullify();
        }
        catch (NullPointerException e)
        {
        }
    }

    /**
     * Builds E1 group
     */
    private void bulidE1()
    {
        Iterator<Edge> itM = matching.iterator();
        while (itM.hasNext())
        {// O(V)
            Edge eTmp = (Edge) itM.next();
            eTmp.setDirected(true);
            e1.add(eTmp);
        }
    }

    /**
     * Builds E2 group
     */
    private void bulidE2()
    {
    	Set<Edge> eTmp = new HashSet<Edge>(edgesSet);
    	eTmp.removeAll(e1);
        Iterator<Edge> it = eTmp.iterator();
        while (it.hasNext())
        {// O(E)
            Edge toAdd = (Edge) it.next();
            toAdd.turnOverEdgeDirection();
            toAdd.turnOverWeight();
            toAdd.setDirected(true);
            e2.add(toAdd);
        }
    }

    /**
     * Get E1 group
     * 
     * @return E1 a set of edges
     */
    public Set<Edge> getE1()
    {
        return e1;
    }

    /**
     * Get E2 group
     * 
     * @return E2, a set of edges
     */
    public Set<Edge> getE2()
    {
        return e2;
    }

    /**
     * Print the Dgraph
     * 
     * @return a string with a printable version of the graph
     */
    public String toString()
    {
        String eg1 = e1.toString();
        String eg2 = e2.toString();
        String m = matching.toString();
        int vc = getVSize();
        return "Dgraph info:\n" + " E1 = " + eg1 + "\n E2 = " + eg2 + " \n Matching = " + m +
            "\n Vcount = " + vc + "";
    }

    /**
     * Get the best matching in the graph
     * 
     * @return best matching in the associated bipartite graph
     */
    public Set<Edge> getBestMatching()
    {
        return matching;
    }
}