package ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities;

import java.io.Serializable;

/**
 * <p>
 * Title: Edge
 * </p>
 * <p>
 * Description: represents edge in graph
 * </p>
 * Implements {@link Serializable} and {@link Comparable}
 * 
 * @author Haggai Roitman
 * @version 1.1
 */

public class Edge implements Serializable, Comparable<Object>
{
    private static final long serialVersionUID = 1L;

    /** edge source */
    private int sourceVertexID;
    /** edge target */
    private int targetVertexID;
    /** edge weight */
    private double weight;
    /** flag if the edge is undirected */
    private boolean directed;

    /**
     * Constructs a default Edge
     */
    public Edge()
    {
    }

    /**
     * Constructs a Edge
     * 
     * @param v1 - source vertex
     * @param v2 - target vertex
     * @param w - edge weight
     * @param dir - true if the edge is directed
     */
    public Edge(int v1, int v2, double w, boolean dir)
    {
        sourceVertexID = v1;
        targetVertexID = v2;
        weight = w;
        directed = dir;
    }

    /**
     * Clone the edge
     * 
     * @return a new {@link Edge}
     */
    public Edge clone()
    {
        return new Edge(sourceVertexID, targetVertexID, weight, directed);
    }

    /**
     * Release resources
     */
    public void nullify()
    {
        return;
    }

    /**
     * Checks if two edges are equal
     */
    public boolean equals(Object e)
    {
        return (this.sourceVertexID == ((Edge) e).getSourceVertexID() && this.targetVertexID == ((Edge) e)
            .getTargetVertexID());
    }

    /**
     * Get the source vertex id
     * 
     * @return edge source
     */
    public int getSourceVertexID()
    {
        return sourceVertexID;
    }

    /**
     * Get the target vertex id
     * 
     * @return edge target
     */
    public int getTargetVertexID()
    {
        return targetVertexID;
    }

    /**
     * Set the source vertex id
     * 
     * @param vID - source vertex
     */
    public void setSourceVertexID(int vID)
    {
        sourceVertexID = vID;
    }

    /**
     * Set the target vertex id
     * 
     * @param vID - target vertex
     */
    public void setTargetVertexID(int vID)
    {
        targetVertexID = vID;
    }

    /**
     * Turn over the edge direction
     */
    public void turnOverEdgeDirection()
    {
        int rememberSource = sourceVertexID;
        sourceVertexID = targetVertexID;
        targetVertexID = rememberSource;
    }

    /**
     * Set the edge to be directed
     * 
     * @param dir <code>true</code> if is set to directed
     */
    public void setDirected(boolean dir)
    {
        directed = dir;
    }

    /**
     * Turns over the weight: new weight<-(-weight)
     */
    public void turnOverWeight()
    {
        if (weight != 0)
            weight = weight * (-1);
    }

    /**
     * Get the edge weight
     * 
     * @return edge weight
     */
    public double getEdgeWeight()
    {
        return weight;
    }

    /**
     * @return <code>true</code> if edge is directed
     */
    public boolean isDirectedEdge()
    {
        return directed;
    }

    /**
     * @return printable string of the edge data
     */
    public String printEdge()
    {
        return ("<V" + (sourceVertexID + 1) + ",V" + (targetVertexID + 1) + "," + weight + ">");
    }

    public int compareTo(Object o)
    {
        Edge e = (Edge) o;
        if (this.getEdgeWeight() > e.getEdgeWeight())
            return 1;
        else if (this.getEdgeWeight() > e.getEdgeWeight())
            return -1;
        else
            return 0;
    }

}