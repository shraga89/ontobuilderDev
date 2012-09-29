package ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.BipartiteGraph;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.Edge;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.Graph;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.Path;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.utils.GraphUtilities;

/**
 * <p>Title: MinTopKAlgorithm</p>
 * Implements {@link TopKAlgorithm}
 */
public abstract class MinTopKAlgorithm implements TopKAlgorithm
{

    /** Holds the number of successive matchings available */
    // private int k = 0;
    /** The Bipartite graph that the algorithm operates on */
    private BipartiteGraph graph;

    private LinkedList<Edge> edges;

    private int usedEdgeIndex = 0;

    private Set<Edge> out = null;// 2.3

    private Edge t, e;
    private Graph g;
    private int state = 1;
    private List<Edge> es;
    private Set<Edge> toReturn = null;
    private Set<Edge> temp;
    private boolean treeBuilt = false;
    private TreeNode tree = null;
    private LinkedList<Path> paths = new LinkedList<Path>();

    int i = 0;

    /**
     * Get the local second best matching
     * 
     * @return a set of edges, currently <code>null</code> always
     */
    public Set<Edge> getLocalSecondBestMatching()
    {
        return null;
    }

    /**
     * Get the next heuristic matching
     * 
     * @return a list of sets of edges, currently <code>null</code> always
     * @throws Exception
     */
    public List<Set<Edge>> getNextHeuristicMatchings() throws Exception
    {
        return null;
    }

    /**
     * Builds the root of the tree. <br>
     * O(V^3)
     * 
     * @param g The {@link BipartiteGraph} on which that algorithm works
     */
    public MinTopKAlgorithm(BipartiteGraph g)
    {
        graph = g;
        edges = new LinkedList<Edge>(graph.getEdgesSet());
        Collections.sort(edges);// 1.
        out = new HashSet<Edge>();// 2.3
    }

    /**
     * Each invocation returns the next best matching
     * 
     * @return a set of edges
     */
    public Set<Edge> runAlgorithm() throws Exception
    {
        try
        {
            return getNextMatching();
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
            graph.nullify();
        }
        catch (NullPointerException e)
        {
        }
    }

    /**
     * Returns the next in order best matching
     * 
     * @return set of edges with the next best matching
     */
    public Set<Edge> getNextMatching() throws Exception
    {
        try
        {
            switch (state)
            {
            case (1):
                int i = state1();
                if (i == 0)
                    return null;
                else if (i == 1)
                    return getNextMatching();
                else
                {
                    state = 2;
                    return getNextMatching();
                }
            case (2):
                if (state2())
                {
                    state = 1;
                    return getNextMatching();
                }
                else
                {
                    state = 3;
                    return getNextMatching();
                }
            default:
                if (state3())
                {
                    state = 2;
                    return getNextMatching();
                }
                else
                {
                    // debug
                    System.out.print(toReturn.toString());
                    //
                    return toReturn;
                }
            }
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            throw new Exception(e);
        }
    }

    public int state1()
    {
        // System.out.println("State (1)");
        state = 1;
        t = (Edge) Collections.max(edges);// 2.1
        edges.remove(t);
        if (edges.isEmpty())
            return 0;
        g = GraphUtilities.removeLowWeightEdges(graph, t.getEdgeWeight());// 2.2
        if (out == null)
            out = new HashSet<Edge>();// 2.3
        usedEdgeIndex = 0;// for state 2
        es = new ArrayList<Edge>(GraphUtilities.getEdgesWithWeight(g, t.getEdgeWeight()));// 2.4/
        // debug
        System.out.println("es" + (i++) + ": " + es.toString());
        //
        treeBuilt = false;
        if (es.isEmpty())
            return 1;
        return 2;
    }

    public boolean state2()
    {
        // System.out.println(">>>State (2)");
        state = 2;
        if (usedEdgeIndex == es.size())
            return true;
        e = (Edge) es.get(usedEdgeIndex++);
        Set<Edge> union = new HashSet<Edge>(out);
        union.add(e);
        temp = new HashSet<Edge>(g.getEdgesSet());
        temp.removeAll(union);
        return false;
    }

    public boolean state3()
    {
        // System.out.println(">>>>>>>State (3)");
        state = 3;
        toReturn = findNextCombination(g, temp, e);// 2.4.1+2.4.2
        if (toReturn == null || toReturn.isEmpty())
            return true;
        out.add(e);// 2.4.3
        return false;
    }

    /**
     * Find the next path combination
     * 
     * @param g the {@link Graph}
     * @param es the set of edges
     * @param e the {@link Edge}
     * @return a new set of edges
     */
    public Set<Edge> findNextCombination(Graph g, Set<Edge> es, Edge e)
    {
        if (treeBuilt)
        {
            if (!paths.isEmpty())
                return paths.removeFirst().getPathEdges();
            else
                return null;
        }
        else
        {
            paths = new LinkedList<Path>();
            tree = buildTreeRec(es, null, new TreeNode(e));
            constructPaths(tree, new Path(0, es.size()));
            treeBuilt = true;
            if (!paths.isEmpty())
                return paths.removeFirst().getPathEdges();
            else
                return null;
        }

    }

    /**
     * Constructs path from tree node to the existing path
     * 
     * @param current the {@link TreeNode}
     * @param currentPath the current {@link Path}
     */
    private void constructPaths(TreeNode current, Path currentPath)
    {
        if (!current.hasChilds())
            return;
        Path p = (Path) currentPath.clone();
        p.addFollowingEdgeToPath(current.getEdge());
        // debug
        System.out.println("path:  " + p.printPath());
        //
        paths.addLast(p);
        int childs = current.getChilds().size();
        for (int i = 0; i < childs; i++)
            constructPaths((TreeNode) current.getChilds().get(i), p);
    }

    /**
     * Builds a tree recursively
     *
     * @param es a set of edges
     * @param parent the parent {@link TreeNode}
     * @param current the current {@link TreeNode}
     * @return a the {@link TreeNode}
     */
    private TreeNode buildTreeRec(Set<Edge> es, TreeNode parent, TreeNode current)
    {
    	Set<Edge> tempES = new HashSet<Edge>(es);
        Iterator<Edge> it = tempES.iterator();
        Vector<Edge> toRemove = new Vector<Edge>();
        while (it.hasNext())
        {
            Edge e = it.next();
            if (e.getSourceVertexID() == current.getEdge().getSourceVertexID() ||
                e.getTargetVertexID() == current.getEdge().getTargetVertexID())
            {
                toRemove.add(e);
            }
        }
        it = toRemove.iterator();
        while (it.hasNext())
        {
            tempES.remove(it.next());
        }
        current.setParent(parent);
        it = tempES.iterator();
        while (it.hasNext())
        {
            current.addChild(new TreeNode(((Edge) it.next())));
        }
        Set<Edge> toExclude = new HashSet<Edge>();
        if (current.hasChilds())
        {
            Iterator<TreeNode> it2 = current.getChilds().iterator();
            while (it2.hasNext())
            {
                TreeNode node = it2.next();
                Set<Edge> tmp = new HashSet<Edge>(tempES);
                tmp.removeAll(toExclude);
                buildTreeRec(tmp, current, node);
                toExclude.add(node.getEdge());
            }
        }
        return current;
    }

    /**
     * Get the algorithm name
     */
    public String getAlgorithmName()
    {
        return TopKAlgorithmsNamesEnum.MIN_TOP_K_ALGORITHM.getName();
    }

    /**
     * <p>
     * Title: TreeNode
     * </p>
     * <p>
     * Description: Class to implement a tree node
     * </p>
     */
    class TreeNode
    {
        private TreeNode parent;
        private LinkedList<TreeNode> childs = new LinkedList<TreeNode>();
        private Edge e;

        public TreeNode(Edge e)
        {
            this.e = e;
        }

        public Edge getEdge()
        {
            return e;
        }

        public TreeNode getParent()
        {
            return parent;
        }

        public void addChild(TreeNode child)
        {
            childs.add(child);
        }

        public void setParent(TreeNode parent)
        {
            this.parent = parent;
        }

        public LinkedList<TreeNode> getChilds()
        {
            return childs;
        }

        public boolean hasChilds()
        {
            return childs.isEmpty();
        }
    }

}
