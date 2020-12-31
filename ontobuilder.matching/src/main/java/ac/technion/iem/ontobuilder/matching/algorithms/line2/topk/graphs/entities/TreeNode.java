package ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.utils.VertexArray;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.impl.FastMWBGAlgorithm;



/**
 * <p>
 * Title: A node in the tree
 * </p>
 * <p>
 * Description: holds all information about a node in the built tree
 * </p>
 * Implements {@link Serializable}
 * 
 * @author Haggai Roitman
 * @version 1.1
 */
public class TreeNode implements Serializable
{
    private static final long serialVersionUID = -7554477212910176709L;

    /** hold next id for node in tree */
    public static int nodeID = 0;
    /** reference to the graph of the algorithm */
    private BipartiteGraph graph;
    /** holds the matching associated with these node */
    private Set<Edge> matching;
    /** The si */
    private Set<Edge> si;
    /** The se */
    private Set<Edge> se;
    /** The weight of the assosiated matching */
    private double matchWeight = -1;
    /** reference to nodes sons */
    private List<TreeNode> mySons = new ArrayList<TreeNode>();
    /** node id in the tree */
    private int nodeid;

    /**
     * Constructs a TreeNode
     * 
     * @param g the {@link BipartiteGraph}
     */
    public TreeNode(BipartiteGraph g)
    {
        graph = g;
        initSE();
        initSI();
        nodeid = nodeID++;
    }

    /**
     * Constructs a default TreeNode
     */
    public TreeNode()
    {
    }

    /**
     * new version added by Haggai 2/11/03 Constructs a TreeNode
     * 
     * @param g the {@link BipartiteGraph}
     * @param initialSe a set of edges
     */
    public TreeNode(BipartiteGraph g, Set<Edge> initialSe)
    {
        graph = g;
        se = initialSe;// changed here
        initSI();
        nodeid = nodeID++;
    }

    /**
     * Release resources
     * 
     * @param deeping <code>true</code> to release deeper resources as well
     */
    public void nullify(boolean deeping)
    {
        try
        {
            if (deeping)
            {// added here deeping flag...10/1/04
                graph.nullify();
                matching.clear();
                si.clear();
                se.clear();
                Iterator<TreeNode> it = mySons.iterator();
                while (it.hasNext())
                    it.next().nullify(deeping);
            }
            graph = null;
            matching = null;
            si = se = null;
            mySons = null;
        }
        catch (NullPointerException e)
        {
        }
    }

    /**
     * Se(wj) = Se(w)U{ej} O(1)
     * 
     * @param eg the si of the nodes father, a set of edges
     * @param e the ej, an {@link Edge}
     */
    public void setSet(Set<Edge> eg, Edge e) // O(E)
    {
        Vector<Edge> tmp = new Vector<Edge>();
        tmp.add(e);
        se = new HashSet<Edge>(eg);
        se.addAll(tmp);
    }

    /**
     * @param eg set of edges
     */
    public void setSi(Set<Edge> eg) // O(1)
    {
        si = eg;
    }

    /**
     * @return id of the node
     */
    public int getNodeId()
    {
        return nodeid;
    }

    /**
     * Initiates se to be empty group
     */
    public void initSE()// O(1)
    {
        se = new HashSet<Edge>();
    }

    /**
     * Initiates si to be empty group
     */
    public void initSI() // O(1)
    {
        si = new HashSet<Edge>();
    }

    /**
     * @return the match associated with the node, a set of edges
     */
    public Set<Edge> getMatching()
    {
        return matching;
    }

    /**
     * @return si, a set of edges
     */
    public Set<Edge> getSi()
    {
        return si;
    }

    /**
     * @return se, a set of edges
     */
    public Set<Edge> getSe()
    {
        return se;
    }

    /**
     * Prints all information of the node into given file
     */
    public void printNode(FileWriter fw) throws IOException
    {
        fw.write(System.getProperty("line.separator") + "\n\nNode Details:\n*************");
        fw.write(System.getProperty("line.separator") + "Node id:" + nodeid);
        fw.write(System.getProperty("line.separator") + "Matching:");
        fw.write(matching.toString());
        fw.write(System.getProperty("line.separator") + "Matching weight:" +
        		EdgeUtil.getEdgesSetWeight(matching));
        fw.write(System.getProperty("line.separator") + "Se:");
        fw.write(se.toString());
        fw.write(System.getProperty("line.separator") + "Si:");
        fw.write(si.toString());
        if (mySons.size() == 0)
            fw.write(System.getProperty("line.separator") + "Node is a leaf");
        else
        {
            fw.write(System.getProperty("line.separator") + "sons:" +
                System.getProperty("line.separator"));
            Iterator<TreeNode> it = mySons.iterator();
            while (it.hasNext())
            {
                TreeNode t = (TreeNode) it.next();
                fw.write(t.getNodeId() + "->");
            }
            printSons(fw);
        }
        fw.flush();
    }

    /**
     * Prints the suns of the node
     */
    public void printSons(FileWriter fw) throws IOException
    {
        Iterator<TreeNode> it = mySons.iterator();
        while (it.hasNext())
        {
            TreeNode tn = (TreeNode) it.next();
            tn.printNode(fw);
        }
    }

    /**
     * @return match weight of the node
     */
    public double getMatchWeight()
    {
        return matchWeight;
    }

    /**
     * @return match weight of the node
     */
    public void setMatchWeight(double matchWeight)
    {
        this.matchWeight = matchWeight;
    }

    /**
     * Computes the max weighted matching in the graph O(V^3)
     */
    public void compute1to1Matching() throws Exception
    {
        try
        {
            
        	Set<Edge> eTmp = new HashSet<Edge>(graph.getEdges());
        	eTmp.removeAll(se);// E'<-E\Se O(E)
            Iterator<Edge> it = si.iterator();
            // will hold all edges to remove from the graph
            Set<Edge> adjacentEdges = new HashSet<Edge>();
            while (it.hasNext())
            { // O(E)
                adjacentEdges.addAll(graph.getAllAdjacentEdges((Edge) it.next()));
            }
            eTmp.removeAll(adjacentEdges); // E'<-E'\{adjacentEdges} O(E)
            BipartiteGraph bg = null;
            VertexArray pot = null;
            FastMWBGAlgorithm aBest = null;
            bg = new BipartiteGraph(new HashSet<Edge>(eTmp), graph.getRightVertexesSet(),
                graph.getLeftVertexesSet());
            pot = new VertexArray(bg, new Double(0));
            aBest = new FastMWBGAlgorithm(bg, pot);
            matching = aBest.runAlgorithm();// O(V*E)
            matching.addAll(si); // M<-M U Si O(E)
            matchWeight = EdgeUtil.getEdgesSetWeight(matching); // sets the weight of this node
            aBest.nullify();// release algorithm resources
            eTmp = null;
            adjacentEdges = null;
            bg = null;
            aBest = null;
            pot = null;
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            throw new Exception(e);
        }
    }

    /**
     * Computes N:1 matching
     * 
     * @param nonUniformVersion the non-uniform version
     * @throws Exception
     */
    public void computeNto1Matching(byte nonUniformVersion) throws Exception
    {
        try
        {
        	Set<Edge> eTmp = new HashSet<Edge>(graph.getEdges());
        	eTmp.removeAll(se);
            Iterator<Edge> it = si.iterator();
            // will hold all edges to remove from the graph
            Set<Edge> adjacentEdges = new HashSet<Edge>();
            if (nonUniformVersion == 1)
            {
                while (it.hasNext())
                { // O(E)
                    adjacentEdges.addAll(graph.getAllAdjacentEdges((Edge) it.next()));
                }
                eTmp.removeAll(adjacentEdges); // E'<-E'\{adjacentEdges} O(E)
            }
            BipartiteGraph bg = null;
            bg = new BipartiteGraph(new HashSet<Edge>(eTmp), graph.getRightVertexesSet(),
                graph.getLeftVertexesSet());
            matching = new HashSet<Edge>();
            // N to 1 matching...
            Set<Vertex> leftVS = bg.getLeftVertexesSet();
            Iterator<Vertex> vit = leftVS.iterator();
            while (vit.hasNext())
            {
                Vertex v = (Vertex) vit.next();
                Edge et = EdgeUtil.getEdgeThatStartsWith(v.getVertexID(), si);
                
                if (et != null)
                {
                    matching.add(et);
                }
                else
                {
                    Edge e = EdgeUtil.getMaximalEdgeThatStartsWith(v.getVertexID(), new HashSet<Edge>(bg.getEdges()));
                    if (e != null)
                        matching.add(e);
                }
            }

        }
        catch (Throwable e)
        {
            e.printStackTrace();
            throw new Exception(e);
        }

    }

    /**
     * @param son of the {@link TreeNode} to add
     */
    public void addSon(TreeNode son)
    {
        mySons.add(son);
    }

    /**
     * @param nodeid the node id
     */
    public void setNodeid(int nodeid)
    {
        this.nodeid = nodeid;
    }

    /**
     * @return the Bipartite Graph
     */
    public BipartiteGraph getGraph()
    {
        return graph;
    }

    /**
     * @param graph a {@link BipartiteGraph}
     */
    public void setGraph(BipartiteGraph graph)
    {
        this.graph = graph;
    }

    /**
     * @param matching a set of edges
     */
    public void setMatching(Set<Edge> matching)
    {
        this.matching = matching;
    }

    /**
     * @return the node id
     */
    public static int getNodeID()
    {
        return nodeID;
    }

    /**
     * @param nId the node id
     */
    public static void setNodeID(int nId)
    {
        nodeID = nId;
    }

    /**
     * @return the sons (a list of {@link TreeNode})
     */
    public List<TreeNode> getMySons()
    {
        return mySons;
    }

    /**
     * @param mySons the {@link TreeNode} list
     */
    public void setMySons(Vector<TreeNode> mySons)
    {
        this.mySons = mySons;
    }

    /**
     * @param eg a set of edges
     */
    public void setSet(Set<Edge> eg)
    {
        se = eg;
    }

}
