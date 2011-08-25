package ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.impl;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.BipartiteGraph;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.Edge;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.EdgesSet;
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

    private EdgesSet out = null;// 2.3

    private Edge t, e;
    private Graph g;
    private int state = 1;
    private EdgesSet es;
    private EdgesSet toReturn = null;
    private EdgesSet temp;
    private boolean treeBuilt = false;
    private TreeNode tree = null;
    private LinkedList<Path> paths = new LinkedList<Path>();

    int i = 0;

    /**
     * Get the local second best matching
     * 
     * @return an {@link EdgesSet}, currently <code>null</code> always
     */
    public EdgesSet getLocalSecondBestMatching()
    {
        return null;
    }

    /**
     * Get the next heuristic matching
     * 
     * @return a list of {@link EdgesSet}, currently <code>null</code> always
     * @throws Exception
     */
    public Vector<EdgesSet> getNextHeuristicMatchings() throws Exception
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
        edges = new LinkedList<Edge>(graph.getEdgesSet().getMembers());
        Collections.sort(edges);// 1.
        out = new EdgesSet(graph.getVSize());// 2.3
    }

    /**
     * Each invocation returns the next best matching
     * 
     * @return an {@link EdgesSet}
     */
    public EdgesSet runAlgorithm() throws Exception
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
     * @return {@link EdgesSet} with the next best matching
     */
    public EdgesSet getNextMatching() throws Exception
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
                    System.out.print(toReturn.printEdgesSet());
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
            out = new EdgesSet(g.getVSize());// 2.3
        usedEdgeIndex = 0;// for state 2
        es = GraphUtilities.getEdgesWithWeight(g, t.getEdgeWeight());// 2.4/
        // debug
        System.out.println("es" + (i++) + ": " + es.printEdgesSet());
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
        if (usedEdgeIndex == es.getMembers().size())
            return true;
        e = (Edge) es.getMember(usedEdgeIndex++);
        temp = EdgesSet.minus(g.getEdgesSet(), EdgesSet.union(out, e));
        return false;
    }

    public boolean state3()
    {
        // System.out.println(">>>>>>>State (3)");
        state = 3;
        toReturn = findNextCombination(g, temp, e);// 2.4.1+2.4.2
        if (toReturn == null || toReturn.isEmpty())
            return true;
        out.addMember(e);// 2.4.3
        return false;
    }

    /**
     * Find the next path combination
     * 
     * @param g the {@link Graph}
     * @param es the {@link EdgesSet}
     * @param e the {@link Edge}
     * @return a new {@link EdgesSet}
     */
    public EdgesSet findNextCombination(Graph g, EdgesSet es, Edge e)
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
            constructPaths(tree, new Path(0, es.getVc()));
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
     * @param es an {@link EdgesSet}
     * @param parent the parent {@link TreeNode}
     * @param current the current {@link TreeNode}
     * @return a the {@link TreeNode}
     */
    private TreeNode buildTreeRec(EdgesSet es, TreeNode parent, TreeNode current)
    {
        EdgesSet tempES = (EdgesSet) es.clone();
        Iterator<Edge> it = tempES.getMembers().iterator();
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
        it = tempES.getMembers().iterator();
        while (it.hasNext())
        {
            current.addChild(new TreeNode(((Edge) it.next())));
        }
        EdgesSet toExclude = new EdgesSet(tempES.getVc());
        if (current.hasChilds())
        {
            Iterator<TreeNode> it2 = current.getChilds().iterator();
            while (it2.hasNext())
            {
                TreeNode node = it2.next();
                buildTreeRec(EdgesSet.minus(tempES, toExclude), current, node);
                toExclude.addMember(node.getEdge());
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
