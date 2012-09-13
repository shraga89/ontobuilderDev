package ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.BipartiteGraph;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.Edge;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.EdgeUtil;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.utils.EdgeArray;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.utils.VertexArray;


/**
 * <p>
 * Title: SecondBestMatchingAlgorithm_Algorithm1
 * </p>
 * <p>
 * Description: Finds the second best matching in bipartite graph in O(V^4) <br>
 * The algorithm implementation is based on article by Avi Gal,Ateret Anaby-Tavor,Anna Moss
 * </p>
 * Implements {@link SchemaMatchingsAlgorithm}
 * 
 * @author Haggai Roitman
 * @version 1.1
 */

public class SecondBestMatchingAlgorithm_Algorithm1 implements SchemaMatchingsAlgorithm
{

    /**
     * The bipartite graph the algorithm runs on
     */
    private BipartiteGraph bGraph;

    /**
     * The best matching in the graph
     */
    private Set<Edge> bestMatching;

    /**
     * Constructs a SecondBestMatchingAlgorithm_Algorithm1
     * 
     * @param bg - {@link BipartiteGraph}
     * @param bm - an {@link EdgesSet}, the best matching in the bipartite graph
     */
    public SecondBestMatchingAlgorithm_Algorithm1(BipartiteGraph bg, Set<Edge> bm)
    {
        bGraph = bg;
        bestMatching = bm;
    }

    /**
     * Release resources
     */
    public void nullify()
    {
        try
        {
            bGraph.nullify();
            bestMatching.clear();
        }
        catch (NullPointerException e)
        {
        }
    }

    /**
     * runs the algorithm<br>
     * O(V^4)
     * 
     * @return an {@link EdgesSet} with the best matching
     */
    public Set<Edge> runAlgorithm()
    {
        Vector<Set<Edge>> matches = new Vector<Set<Edge>>();
        Vector<MaxWeightBipartiteMatchingAlgorithm> maxAlgorithms = new Vector<MaxWeightBipartiteMatchingAlgorithm>();
        MaxWeightBipartiteMatchingAlgorithm mwbma;
        Iterator<Edge> it = bestMatching.iterator();
        while (it.hasNext())
        {
            Edge eToRemove = it.next();
            bGraph.removeEdgeFromGraph(eToRemove);
            /****** new version 14/11/03 *****/
            EdgeArray c = new EdgeArray(bGraph);
            VertexArray pot = new VertexArray(bGraph, new Double(0));
            mwbma = new MaxWeightBipartiteMatchingAlgorithm(bGraph, c, pot);
            /**** new version 14/11/03 *****/
            matches.add(mwbma.runAlgorithm());
            bGraph.addEdgeToGraph(eToRemove);
            maxAlgorithms.add(mwbma);
        }
        Iterator<Set<Edge>> it2 = matches.iterator();
        Set<Edge> secondBest = new HashSet<Edge>();
        while (it2.hasNext())
        {
        	Set<Edge> candidate = it2.next();
            secondBest = EdgeUtil.getEdgesSetWeight(secondBest) >= EdgeUtil.getEdgesSetWeight(candidate) ? secondBest : candidate;
        }
        Iterator<MaxWeightBipartiteMatchingAlgorithm> it3 = maxAlgorithms.iterator();
        while (it3.hasNext()) // clean up...
        {
            mwbma = it3.next();
            mwbma.nullify();
        }
        return secondBest;
    }

    /**
     * Get the algorithm name
     */
    public String getAlgorithmName()
    {
        return TopKAlgorithmsNamesEnum.SECOND_BEST_NAIVE_ALGORITHM.getName();
    }
}