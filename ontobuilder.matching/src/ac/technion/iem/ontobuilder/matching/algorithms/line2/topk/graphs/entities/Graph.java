package ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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
    protected Set<Edge> edgesSet;
    /** Vertexes of the graph */
    protected Set<Vertex> vertexesSet = new HashSet<Vertex>();
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
     * @param e set of edges of the graph
     * @param v set of vertices of the graph
     */
    public Graph(Set<Edge> e, Set<Vertex> v)
    {
        edgesSet = e;
        vertexesSet = v;
    }

    /**
     * Set the vertexes set in the graph
     * 
     * @param vertexesSet the set of vertices
     */
    public void setVertexesSet(Set<Vertex> vertexesSet)
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
            edgesSet.clear();
            vertexesSet.clear();
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
        edgesSet.add(toAdd);
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
        Iterator<Vertex> it = vertexesSet.iterator();
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
    public void setEdgesSet(Set<Edge> e)
    {
        edgesSet = e;
    }

    /**
     * Get the edge set
     * 
     * @return edges group of the graph
     */
    public Set<Edge> getEdgesSet()
    {
        return edgesSet;
    }

    /**
     * Get the vertexes set
     * 
     * @return vertexes of the graph
     */
    public Set<Vertex> getVertexesSet()
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
        Iterator<Edge> it = edgesSet.iterator();
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
    public Set<Edge> getAllAdjacentEdges(Edge e)
    {// O(V)
    	Set<Edge> result = new HashSet<Edge>();
        for (int i = 0; i < vertexesSet.size(); i++)
        {// O(V)
            if (adjMatrix[e.getSourceVertexID()][i] != Graph.INF && i != e.getTargetVertexID())
                result.add(new Edge(e.getSourceVertexID(), i,
                    adjMatrix[e.getSourceVertexID()][i], true));
            if (adjMatrix[i][e.getTargetVertexID()] != Graph.INF && i != e.getSourceVertexID())
                result.add(new Edge(i, e.getTargetVertexID(), adjMatrix[i][e
                    .getTargetVertexID()], true));
        }
        return result;
    }

    /**
     * Prints the vertexes info in the graph
     */
    public void printVertexs()
    {
        Iterator<Vertex> it = vertexesSet.iterator();
        System.out.println("Vertexes info:");
        while (it.hasNext())
        {
            Vertex v = (Vertex) it.next();
            System.out.println("Vertex id:" + (v.getVertexID() + 1) + " vertex name:" +
                v.getVertexNameID());
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
        return edgesSet.iterator();
    }

    /**
     * Get an iterator to the vertexes
     */
    public Iterator<Vertex> getVertexesIterator()
    {
        return vertexesSet.iterator();
    }

    /**
     * Clone the graph
     * 
     * @return a new graph
     */
    public Object clone()
    {
        Graph clonedGraph = new Graph(new HashSet<Edge>(edgesSet),
            new HashSet<Vertex>(vertexesSet));
        clonedGraph.buildAdjMatrix();
        return clonedGraph;
    }
}