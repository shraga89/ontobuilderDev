package ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.impl;

import java.util.Iterator;
import java.util.Vector;

import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.Cycle;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.DGraph;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.Edge;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.EdgesSet;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.Path;

/**
 * <p>
 * Title: SecondBestMatchingAlgorithm
 * </p>
 * <p>
 * Description: Finds the second best matching in bipartite graph in O(V^3)
 * </p>
 * <br>
 * The algorithm implementation is based on article by Avi Gal,Ateret Anaby-Tavor,Anna Moss
 * Implements {@link SchemaMatchingsAlgorithm}
 * 
 * @author Haggai Roitman
 * @version 1.1
 */
public class SecondBestMatchingAlgorithm_Algorithm2 implements SchemaMatchingsAlgorithm
{

    /** The Dgraph the algorithm operates on */
    private DGraph dgraph;

    /**
     * Constructs a SecondBestMatchingAlgorithm_Algorithm2
     * 
     * @param dg {@link DGraph} the algorithm operates on
     */
    public SecondBestMatchingAlgorithm_Algorithm2(DGraph dg)
    {
        dgraph = dg;
    }

    /**
     * Release resources
     */
    public void nullify()
    {
        try
        {
            dgraph.nullify();
        }
        catch (NullPointerException e)
        {
        }
    }

    /**
     * Runs the algorithm <br>
     * O(V^3)
     * 
     * @return the second best matching, an {@link EdgesSet}
     * @throws NegativeCycleInGraphException - the graph included negative cycle<br>
     * as part of the Floyd Warshall algorithm check
     */
    public EdgesSet runAlgorithm()
    {
        EdgesSet secondBestMatching = null;// "S"
        Floyd_Warshall_Algorithm fwa = null;
        try
        {
            // run the Floyd Warshall algorithm to find minimum paths between all pairs in Dgraph
            fwa = new Floyd_Warshall_Algorithm(dgraph.getAdjMatrix(), dgraph.getVSize());
            fwa.runAlgorithm();// O(V^3)
            EdgesSet bestMathing = dgraph.getBestMatching();// M*
            Vector<Cycle> cycles = new Vector<Cycle>();
            Iterator<Edge> it = bestMathing.getMembers().iterator();
            while (it.hasNext()) // O(E*V) reconstruct P(v,u) where (u,v) belongs to M*
            {
                Edge eTmp = it.next();
                Path p = fwa.reconstructOnePath(eTmp.getTargetVertexID(), eTmp.getSourceVertexID());
                cycles.add(new Cycle(p, new Edge(eTmp.getSourceVertexID(),
                    eTmp.getTargetVertexID(), eTmp.getEdgeWeight(), true), dgraph.getVSize()));// C(u,v)<-(u,v)
                                                                                               // U
                                                                                               // P(v,u)
            }
            Cycle minCycle = Cycle.getMinCycle(cycles);// O(cycles)//C<-argmin over (u,v) belongs to
                                                       // M* of Cycles
            // O(E) - for each (u,v) belong to (C intersect E1) do: S<-S\{(u,v)}
            EdgesSet cIntersectE1 = EdgesSet.intersect(minCycle.getPathEdges(), dgraph.getE1());
            secondBestMatching = EdgesSet.minus(bestMathing, cIntersectE1);// O(E)
            // O(E) - for each (v,u) belongs to (C intersect E2) do: S<-S U {(u,v)}
            EdgesSet cIntersectE2 = EdgesSet.intersect(minCycle.getPathEdges(), dgraph.getE2());
            cIntersectE2.turnOverEdges(true);// O(E)
            secondBestMatching = EdgesSet.union(secondBestMatching, cIntersectE2);// O(E)
            // clean up...
            try
            {
                fwa.nullify();
                bestMathing.nullify();
                Iterator<Cycle> itC = cycles.iterator();
                while (itC.hasNext())
                {
                    ((Cycle) itC.next()).nullify();
                }
                cycles = null;
                minCycle.nullify();
                cIntersectE1.nullify();
                cIntersectE2.nullify();
            }
            catch (NullPointerException e)
            {
            }
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
        return secondBestMatching;// output S
    }

    /**
     * Get algorithm name
     */
    public String getAlgorithmName()
    {
        return TopKAlgorithmsNamesEnum.SECOND_BEST_REFINED_ALGORITHM.getName();
    }
}