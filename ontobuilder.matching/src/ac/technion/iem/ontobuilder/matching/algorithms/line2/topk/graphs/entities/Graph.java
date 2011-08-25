package ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities;

import java.io.Serializable;
import java.util.Iterator;

/**
 * <p>
 * Title: Graph
 * </p>
 * <p>
 * Description: Graph representation
 * </p>
 * Implements {@link Serializable}
 * 
 * @author Haggai Roitman
 * @version 1.1
 */

public class Graph implements Serializable
{

    private static final long serialVersionUID = -4355460902972670122L;

    /** used to sign that two vertexes in the graph are not connected */
    public static final double INF = Double.POSITIVE_INFINITY;
    /** edges of the graph */
    protected EdgesSet edgesSet;
    /** Vertexes of the graph */
    protected VertexesSet vertexesSet = new VertexesSet();
    /** Adjacency matrix of the graph (hold also edges weights) */
    protected double[][] adjMatrix;

    /**
     * Constructs a default Graph
     */
    public Graph()
    {
    }

    /**
     * Constructs a Graph
     * 
     * @param e {@link EdgesSet} of the graph
     * @param v {@link VertexesSet} of the graph
     */
    public Graph(EdgesSet e, VertexesSet v)
    {
        edgesSet = e;
        vertexesSet = v;
    }

    /**
     * Set the vertexes set in the graph
     * 
     * @param vertexesSet the {@link VertexesSet}
     */
    public void setVertexesSet(VertexesSet vertexesSet)
    {
        this.vertexesSet = vertexesSet;
    }

    /**
     * Set the Adjacency matrix
     * 
     * @param adjMatrix the matrix to set
     */
    public void setAdjMatrix(double[][] adjMatrix)
    {
        this.adjMatrix = adjMatrix;
    }

    /**
     * Release resources
     */
    public void nullify()
    {
        try
        {
            edgesSet.nullify();
            vertexesSet.nullify();
            adjMatrix = null;
        }
        catch (NullPointerException e)
        {
        }
    }

    /**
     * Removes a given edge from the graph
     * 
     * @param toRemove {@link Edge} to be removed from the graph
     * @return reference to removed edge
     */
    public Edge removeEdgeFromGraph(Edge toRemove)
    {
        boolean ok = edgesSet.remove(toRemove);
        if (!ok)
            return null;
        else
        {
            adjMatrix[toRemove.getSourceVertexID()][toRemove.getTargetVertexID()] = Graph.INF;
            return toRemove;
        }
    }

    /**
     * Adds edge to graph
     * 
     * @param toAdd {@link Edge} to add graph
     */
    public void addEdgeToGraph(Edge toAdd)
    {
        edgesSet.addMember(toAdd);
        adjMatrix[toAdd.getSourceVertexID()][toAdd.getTargetVertexID()] = toAdd.getEdgeWeight();
    }

    /**
     * Get a vertex of the graph
     * 
     * @param vID - vertex id to return
     * @return the {@link Vertex} of specified id
     */
    public Vertex getVertex(int vID)
    {// O(V)
        Iterator<Vertex> it = vertexesSet.getMembers().iterator();
        while (it.hasNext())
        {
            Vertex v = it.next();
            if (v.getVertexID() == vID)
                return v;
        }
        return null;
    }

    /**
     * Set the edge set
     * 
     * @param e edges group to set
     */
    public void setEdgesSet(EdgesSet e)
    {
        edgesSet = e;
    }

    /**
     * Get the edge set
     * 
     * @return edges group of the graph
     */
    public EdgesSet getEdgesSet()
    {
        return edgesSet;
    }

    /**
     * Get the vertexes set
     * 
     * @return vertexes of the graph
     */
    public VertexesSet getVertexesSet()
    {
        return vertexesSet;
    }

    /**
     * Get the number of vertexes in the graph
     * 
     * @return vertexes count in graph
     */
    public int getVSize()
    {
        return vertexesSet.size();
    }

    /**
     * Builds the Adjacency matrix of the graph
     */
    public void buildAdjMatrix()
    {// O(V^2+E)
        adjMatrix = new double[vertexesSet.size()][vertexesSet.size()];
        for (int i = 0; i < vertexesSet.size(); i++)
            // O(V^2)
            for (int j = 0; j < vertexesSet.size(); j++)
                if (i == j)
                    adjMatrix[i][j] = 0;
                else
                    adjMatrix[i][j] = INF;
        Iterator<Edge> it = edgesSet.getMembers().iterator();
        while (it.hasNext())
        {// O(E)
            Edge eTmp = (Edge) it.next();
            int i = eTmp.getSourceVertexID();
            int j = eTmp.getTargetVertexID();
            adjMatrix[i][j] = eTmp.getEdgeWeight();
        }
    }

    /**
     * Get the Adjacency matcrix
     * 
     * @return adj matrix of the graph
     */
    public double[][] getAdjMatrix()
    {
        return adjMatrix;
    }

    /**
     * Get all the adjacent edges in the graph
     * 
     * @param e edge to search its adjacent edges on its vertexes
     * @return all adjacent edges
     */
    public EdgesSet getAllAdjacentEdges(Edge e)
    {// O(V)
        EdgesSet result = new EdgesSet(vertexesSet.size());
        for (int i = 0; i < vertexesSet.size(); i++)
        {// O(V)
            if (adjMatrix[e.getSourceVertexID()][i] != Graph.INF && i != e.getTargetVertexID())
                result.addMember(new Edge(e.getSourceVertexID(), i,
                    adjMatrix[e.getSourceVertexID()][i], true));
            if (adjMatrix[i][e.getTargetVertexID()] != Graph.INF && i != e.getSourceVertexID())
                result.addMember(new Edge(i, e.getTargetVertexID(), adjMatrix[i][e
                    .getTargetVertexID()], true));
        }
        return result;
    }

    /**
     * Prints the vertexes info in the graph
     */
    public void printVertexs()
    {
        Iterator<Vertex> it = vertexesSet.getMembers().iterator();
        System.out.println("Vertexes info:");
        while (it.hasNext())
        {
            Vertex v = (Vertex) it.next();
            System.out.println("Vertex id:" + (v.getVertexID() + 1) + " vertex name:" +
                v.getVertexName());
        }
    }

    /**
     * @return printable string of the adjacency matrix
     */
    public String printAdjMatrix()
    {// O(V^2)
        String result = "";
        for (int i = 0; i < vertexesSet.size(); i++)
        {
            for (int j = 0; j < vertexesSet.size(); j++)
                result += ("[" + i + "->" + j + " " +
                    (adjMatrix[i][j] == INF ? "I" : "" + adjMatrix[i][j]) + "]" + ",");
            result += System.getProperty("line.separator") + "\n";
        }
        return result;
    }

    /**
     * Get an iterator to the edges
     */
    public Iterator<Edge> getEdgesIterator()
    {
        return edgesSet.getMembers().iterator();
    }

    /**
     * Get an iterator to the vertexes
     */
    public Iterator<Vertex> getVertexesIterator()
    {
        return vertexesSet.getMembers().iterator();
    }

    /**
     * Clone the graph
     * 
     * @return a new graph
     */
    public Object clone()
    {
        Graph clonedGraph = new Graph((EdgesSet) edgesSet.clone(),
            (VertexesSet) vertexesSet.clone());
        clonedGraph.buildAdjMatrix();
        return clonedGraph;
    }
}