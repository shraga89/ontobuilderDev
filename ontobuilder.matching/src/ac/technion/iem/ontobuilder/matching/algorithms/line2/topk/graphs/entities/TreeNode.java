package ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Vector;

import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.utils.EdgeArray;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.utils.VertexArray;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.impl.MaxWeightBipartiteMatchingAlgorithm;


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
    private EdgesSet matching;
    /** The si */
    private EdgesSet si;
    /** The se */
    private EdgesSet se;
    /** The weight of the assosiated matching */
    private double matchWeight = -1;
    /** reference to nodes sons */
    private Vector<TreeNode> mySons = new Vector<TreeNode>();
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
     * @param initialSe an {@link EdgesSet}
     */
    public TreeNode(BipartiteGraph g, EdgesSet initialSe)
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
                matching.nullify();
                si.nullify();
                se.nullify();
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
     * @param eg the si of the nodes father, an {@link EdgesSet}
     * @param e the ej, an {@link Edge}
     */
    public void setSet(EdgesSet eg, Edge e) // O(E)
    {
        Vector<Edge> tmp = new Vector<Edge>();
        tmp.add(e);
        se = EdgesSet.union(new EdgesSet(eg.getMembers(), graph.getVSize()), new EdgesSet(tmp,
            graph.getVSize()));
    }

    /**
     * @param eg {@link EdgesSet}
     */
    public void setSi(EdgesSet eg) // O(1)
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
        se = new EdgesSet(graph.getVSize());
    }

    /**
     * Initiates si to be empty group
     */
    public void initSI() // O(1)
    {
        si = new EdgesSet(graph.getVSize());
    }

    /**
     * @return the match associated with the node, an {@link EdgesSet}
     */
    public EdgesSet getMatching()
    {
        return matching;
    }

    /**
     * @return si, an {@link EdgesSet}
     */
    public EdgesSet getSi()
    {
        return si;
    }

    /**
     * @return se, an {@link EdgesSet}
     */
    public EdgesSet getSe()
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
        fw.write(matching.printEdgesSet());
        fw.write(System.getProperty("line.separator") + "Matching weight:" +
            matching.getEdgesSetWeight());
        fw.write(System.getProperty("line.separator") + "Se:");
        fw.write(se.printEdgesSet());
        fw.write(System.getProperty("line.separator") + "Si:");
        fw.write(si.printEdgesSet());
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
            EdgesSet eTmp = EdgesSet.minus(graph.getEdgesSet(), se);// E'<-E\Se O(E)
            Iterator<Edge> it = si.getMembers().iterator();
            // will hold all edges to remove from the graph
            EdgesSet adjacentEdges = new EdgesSet(graph.getVSize());
            while (it.hasNext())
            { // O(E)
                adjacentEdges = EdgesSet.union(adjacentEdges,
                    graph.getAllAdjacentEdges((Edge) it.next()));
            }
            eTmp = EdgesSet.minus(eTmp, adjacentEdges); // E'<-E'\{adjacentEdges} O(E)
            /******** new version 14/11/03 ***********/
            BipartiteGraph bg = null;
            EdgeArray c = null;
            VertexArray pot = null;
            MaxWeightBipartiteMatchingAlgorithm aBest = null;
            bg = new BipartiteGraph((EdgesSet) eTmp.clone(), graph.getRightVertexesSet(),
                graph.getLeftVertexesSet());
            c = new EdgeArray(bg);
            pot = new VertexArray(bg, new Double(0));
            aBest = new MaxWeightBipartiteMatchingAlgorithm(bg, c, pot);
            /******** new version ***********/
            matching = aBest.runAlgorithm();// O(V^3)
            matching = EdgesSet.union(matching, si); // M<-M U Si O(E)
            matchWeight = matching.getEdgesSetWeight(); // sets the weight of this node
            /**** added 29/12/03 *****/
            aBest.nullify();// release algorithm resources
            /*************************/
            /**** added 10/1/04 ******/
            eTmp = null;
            adjacentEdges = null;
            bg = null;
            aBest = null;
            c = null;
            pot = null;
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            throw new Exception(e);
        }
        /*************************/
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
            EdgesSet eTmp = EdgesSet.minus(graph.getEdgesSet(), se); // E'<-E\Se O(E)
            Iterator<Edge> it = si.getMembers().iterator();
            // will hold all edges to remove from the graph
            EdgesSet adjacentEdges = new EdgesSet(graph.getVSize());
            if (nonUniformVersion == 1)
            {
                while (it.hasNext())
                { // O(E)
                    adjacentEdges = EdgesSet.union(adjacentEdges,
                        graph.getAllAdjacentEdges((Edge) it.next()));
                }
                eTmp = EdgesSet.minus(eTmp, adjacentEdges); // E'<-E'\{adjacentEdges} O(E)
            }
            BipartiteGraph bg = null;
            bg = new BipartiteGraph((EdgesSet) eTmp.clone(), graph.getRightVertexesSet(),
                graph.getLeftVertexesSet());
            matching = new EdgesSet(bg.getVSize());
            // N to 1 matching...
            VertexesSet leftVS = bg.getLeftVertexesSet();
            Iterator<Vertex> vit = leftVS.getMembers().iterator();
            while (vit.hasNext())
            {
                Vertex v = (Vertex) vit.next();
                Edge et = si.getEdgeThatStartsWith(v.getVertexID());
                if (et != null)
                {
                    matching.addMember(et);
                }
                else
                {
                    Edge e = bg.getEdgesSet().getMaximalEdgeThatStartsWith(v.getVertexID());
                    if (e != null)
                        matching.addMember(e);
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
     * @param matching an {@link EdgesSet}
     */
    public void setMatching(EdgesSet matching)
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
    public Vector<TreeNode> getMySons()
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
     * @param eg an {@link EdgesSet}
     */
    public void setSet(EdgesSet eg)
    {
        se = eg;
    }

}
