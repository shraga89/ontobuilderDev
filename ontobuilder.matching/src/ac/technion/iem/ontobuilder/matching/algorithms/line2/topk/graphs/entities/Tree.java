package ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

/**
 * <p>
 * Title: Tree
 * </p>
 * <p>
 * Description: The tree built by the k best matching algorithm
 * </p>
 * 
 * @author Haggai Roitman
 * @version 1.1
 */
public class Tree
{
    /** The root of the tree */
    private TreeNode root;
    /** Holds the leaves of the tree */
    private LinkedList<TreeNode> treeLeaves = new LinkedList<TreeNode>();
    private TreeNode lastMaxLeaf;

    /**
     * @return a {@link TreeNode}, root of the tree
     */
    public TreeNode getRoot()
    {
        return root;
    }

    /**
     * Get the leaves of the tree
     * 
     * @return a list of {@link TreeNode}
     */
    public LinkedList<TreeNode> getLeaves()
    {
        return treeLeaves;
    }

    /**
     * Release resources
     */
    public void nullify(boolean deeping)
    {// added new flag deeping
        try
        {
            // if (treeFile != null) treeFile.close();
            // bestLeafs.clear();
            // bestLeafs = null;
            root.nullify(deeping);
            Iterator<TreeNode> it = treeLeaves.iterator();
            while (it.hasNext())
            {
                it.next().nullify(deeping);
            }
            treeLeaves = null;
        }
        catch (NullPointerException e)
        {
        }
    }

    /**
     * builds the root of the tree and initializes its fields including the matching calculation <br>
     * O(V^3)
     * 
     * @param g the {@link BipartiteGraph}
     */
    public void buildRoot(BipartiteGraph g)
    {
        // new version add by Haggai 2/11/03
        // root = new TreeNode(g); old version
        Iterator<Edge> it = g.getEdgesSet().getMembers().iterator();
        Vector<Edge> zeroWeightEdges = new Vector<Edge>();
        while (it.hasNext()) // O(E)
        {
            Edge edge = it.next();
            if (edge.getEdgeWeight() == 0)
                zeroWeightEdges.add(edge);
        }
        EdgesSet initialSe = new EdgesSet(zeroWeightEdges, g.getEdgesSet().getVc());
        root = new TreeNode(g, initialSe);
        try
        {
            root.compute1to1Matching();
        }
        catch (Throwable e)
        {
        }
        addLeaf(root);
    }

    /**
     * @return the maxLeaf of the tree, a {@link TreeNode}
     */
    public TreeNode getMaxLeaf() // O(1)
    {
        return (TreeNode) treeLeaves.get(0);
    }

    /**
     * @return the second best leaf of the tree, a {@link TreeNode}
     */
    public TreeNode getSecondBestLeaf()
    {
        return (TreeNode) treeLeaves.get(1);
    }

    /**
     * @return the maxLeaf removed from the tree leafs list, a  {@link TreeNode}
     */
    public TreeNode removeMaxLeaf() // O(1)
    {
        lastMaxLeaf = (TreeNode) treeLeaves.removeFirst();
        return lastMaxLeaf;
    }

    /**
     * Adds a new leaf to the tree leafs list by insertion sort The first leaf is the has the max
     * match weight <br>
     * O(|leafs|^2) over |leafs| insertion where |leafs| = k|V|
     * 
     * @param t the leaf to add to the tree leafs list
     */
    public void addLeaf(TreeNode t)// O(leaves)
    {
        Iterator<TreeNode> it = treeLeaves.iterator();
        int possition = 0;
        while (it.hasNext() && ((TreeNode) it.next()).getMatchWeight() > t.getMatchWeight())
            possition++;
        treeLeaves.add(possition, t);
    }
}
