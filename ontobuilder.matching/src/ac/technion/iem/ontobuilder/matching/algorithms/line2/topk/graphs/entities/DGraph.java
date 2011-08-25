package ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities;

import java.util.Iterator;

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
    private EdgesSet e1;
    /** E2 edges group */
    private EdgesSet e2;
    /** the best matching of the bipartite graph from where the Dgraph was created */
    private EdgesSet matching;

    /**
     * Constructs a DGraph
     * 
     * @param bg - associated {@link BipartiteGraph}
     * @param m - the best match in that bipartite graph
     */
    public DGraph(BipartiteGraph bg, EdgesSet m)
    {// O(E+V^2)
        super(new EdgesSet(bg.getVSize()), bg.getRightVertexesSet(), bg.getLeftVertexesSet());
        e1 = new EdgesSet(bg.getVSize());
        e2 = new EdgesSet(bg.getVSize());
        matching = m;
        setEdgesSet(bg.getEdgesSet());
        bulidE1();
        bulidE2();
        setEdgesSet(EdgesSet.union(e1, e2));
        buildAdjMatrix();
    }

    /**
     * Release resources
     */
    public void nullify()
    {
        try
        {
            e1.nullify();
            e2.nullify();
            matching.nullify();
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
        Iterator<Edge> itM = matching.getMembers().iterator();
        while (itM.hasNext())
        {// O(V)
            Edge eTmp = (Edge) itM.next();
            eTmp.setDirected(true);
            e1.addMember(eTmp);
        }
    }

    /**
     * Builds E2 group
     */
    private void bulidE2()
    {
        EdgesSet eTmp = EdgesSet.minus(edgesSet, e1);
        Iterator<Edge> it = eTmp.getMembers().iterator();
        while (it.hasNext())
        {// O(E)
            Edge toAdd = (Edge) it.next();
            toAdd.turnOverEdgeDirection();
            toAdd.turnOverWeight();
            toAdd.setDirected(true);
            e2.addMember(toAdd);
        }
    }

    /**
     * Get E1 group
     * 
     * @return E1 an {@link EdgesSet}
     */
    public EdgesSet getE1()
    {
        return e1;
    }

    /**
     * Get E2 group
     * 
     * @return E2, an {@link EdgesSet}
     */
    public EdgesSet getE2()
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
        String eg1 = e1.printEdgesSet();
        String eg2 = e2.printEdgesSet();
        String m = matching.printEdgesSet();
        int vc = getVSize();
        return "Dgraph info:\n" + " E1 = " + eg1 + "\n E2 = " + eg2 + " \n Matching = " + m +
            "\n Vcount = " + vc + "";
    }

    /**
     * Get the best matching in the graph
     * 
     * @return best matching in the associated bipartite graph
     */
    public EdgesSet getBestMatching()
    {
        return matching;
    }
}