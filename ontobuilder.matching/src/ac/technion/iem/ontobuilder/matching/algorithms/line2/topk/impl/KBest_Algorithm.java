package ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.BipartiteGraph;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.Edge;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.Tree;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.TreeNode;

/**
 * <p>
 * Title: KBest_Algorithm
 * </p>
 * <p>
 * Description: finds k best schema matching for given k invocations <br>
 * The algorithm implementation is based on article by Avi Gal,Ateret Anaby-Tavor,Anna Moss
 * </p>
 * Implements {@link TopKAlgorithm}
 * 
 * @author Haggai Roitman
 * @version 1.1
 */
public class KBest_Algorithm implements TopKAlgorithm
{
    /** The tree that the algorithm builds */
    private Tree tr = new Tree();
    /** Holds the number of successive matchings available */
    private int k;
    /** The Bipartite graph that the algorithm operates on */
    private BipartiteGraph graph;
    /** reference to the max weighted match leaf of the tree */
    private TreeNode maxLeaf;
    /** reference to second best leaf in the tree */
    private TreeNode secondBestLeaf;
    /** flags if need to save all create tree */
    private boolean treeSaved = true;// not used currently

    /**
     * Builds the root of the tree. <br>
     * O(V^3)
     * 
     * @param g The {@link BipartiteGraph} on which that algorithm works
     */
    public KBest_Algorithm(BipartiteGraph g)
    {
        tr.buildRoot(g);
        k = 1; // first matching is available
        graph = g;
    }

    /**
     * Run the algorithm. Each invocation returns the next best matching
     * 
     * @return an list of edges
     */
    public Set<Edge> runAlgorithm() throws Exception
    {
        try
        {
            return getNextMatching(true);
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            throw new Exception(e);
        }
    }

    /**
     * Release resources
     */
    public void nullify()
    {
        try
        {
            tr.nullify(true);
            graph.nullify();
            maxLeaf.nullify(false);
        }
        catch (NullPointerException e)
        {
        }
    }

    /**
     * Returns the next in order best matching <br>
     * O(V^4)
     * 
     * @return a set of edges
     */
    public Set<Edge> getNextMatching(boolean openFronter) throws Exception // O(V^4)
    {
        try
        {
            if (openFronter)
            {
                // new - 7/4/05 - if no mapping possible return the empty mapping
                if (tr.getLeaves().isEmpty())
                    return new HashSet<Edge>(0);
                // throw new Exception("Top K Tree empty");
                //
                maxLeaf = tr.removeMaxLeaf();// removes the max Leaf of the tree from the leafs list
                                             // O(1)
                List<Edge> diff = new ArrayList<Edge>(maxLeaf.getMatching());
                Set<Edge> minus = new HashSet<Edge>(maxLeaf.getMatching());
                minus.removeAll(maxLeaf.getSi()); // D(w)<-M(w)\Si(w)

                int t = diff.size(); // O(1)
                for (int j = 0; j < t; j++)
                {// maxLeaf sons developing
                    TreeNode tmpNode = new TreeNode(graph);// O(V^2)
                    tmpNode.setSet(maxLeaf.getSe(), (Edge) diff.get(j)); // Se(wj) = Se(w)U{ej}
                                                                              // O(E)
                    tmpNode.setSi(maxLeaf.getSi());// O(1)
                    for (int l = 0; l < j; l++)
                    { // Si(wj) = Si(w)UU{el} l = 1,...,j-1 O(j-1)
                        List<Edge> tmp = new LinkedList<Edge>();
                        tmp.add(diff.get(l));
                        
                        tmpNode.getSi().addAll(tmp);// O(E)
                    }
                    tmpNode.compute1to1Matching(); // O(V^3)
                    tr.addLeaf(tmpNode); // insertion sort O(leafs)
                    if (treeSaved)
                        maxLeaf.addSon(tmpNode);// O(1) marked// haggai 10/1/04
                }
                
                if (tr.getLeaves().size() >= 2)
                    secondBestLeaf = tr.getSecondBestLeaf();
            }
            else
            {
            	maxLeaf = tr.getMaxLeaf();
            }
        
            Set<Edge> match = maxLeaf.getMatching();
            maxLeaf.nullify(false);
            maxLeaf = null;
            // diff = null;
            // end of new version
            // return (EdgesSet)matchings.get((k++) - 1);//output the maxLeaf of the current leafs
            // list
            return match;// 11/1/04
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            throw new Exception(e);
        }
    }

    /**
     * Get the local second best matching
     * @return a set of edges
     */
    public Set<Edge> getLocalSecondBestMatching()
    {
        if (tr.getLeaves().size() < 2)
            return new HashSet<Edge>();
        return secondBestLeaf.getMatching();
    }

    /**
     * Get the next heuristic matching
     * @param nonUniformVersion the non uniform version (1 or 2)
     * @return a list of set of edges
     */
    public List<Set<Edge>> getNextHeuristicMatchings(byte nonUniformVersion) throws Exception
    {
        List<Set<Edge>> matchings = new ArrayList<Set<Edge>>();
        // new - 7/4/05 - if no mapping possible return the empty mapping
        if (tr.getLeaves().isEmpty())
            return matchings;
        //
        maxLeaf = tr.getMaxLeaf();
        List<Edge> diff = new ArrayList<Edge>(maxLeaf.getMatching());
        diff.removeAll(maxLeaf.getSi());// D(w)<-M(w)\Si(w)

        int t = diff.size(); // O(1)
        for (int j = 0; j < t; j++)
        { // maxLeaf sons developing
            TreeNode tmpNode = new TreeNode(graph); // O(V^2)
            tmpNode.setSet(maxLeaf.getSe(), (Edge) diff.get(j)); // Se(wj) = Se(w)U{ej} O(E)
            tmpNode.setSi(maxLeaf.getSi()); // O(1)
            for (int l = 0; l < j; l++)
            { // Si(wj) = Si(w)UU{el} l = 1,...,j-1 O(j-1)
                List<Edge> tmp = new LinkedList<Edge>();
                tmp.add(diff.get(l));
                tmpNode.getSi().addAll(tmp); // O(E)
            }
            tmpNode.computeNto1Matching(nonUniformVersion); // O(V^2) N to 1 Matching
            matchings.add(tmpNode.getMatching());
        }
        return matchings;
    }

    /**
     * Get the tree
     * 
     * @return reference to the {@link Tree}
     */
    public Tree getTree()
    {
        return tr;
    }

    /**
     * Returns <code>true</code> if tree is saved
     */
    public boolean isTreeSaved()
    {
        return treeSaved;
    }

    /**
     * Set that the tree is saved if <code>true</code>
     */
    public void setTreeSaved(boolean treeSaved)
    {
        this.treeSaved = treeSaved;
    }

    /**
     * Get the algorithm name
     */
    public String getAlgorithmName()
    {
        return TopKAlgorithmsNamesEnum.K_BEST_ALGORITHM.getName();
    }

    /**
     * Get the number of successive matchings available
     * 
     * @return the number
     */
    public int getK()
    {
        return k;
    }

}
