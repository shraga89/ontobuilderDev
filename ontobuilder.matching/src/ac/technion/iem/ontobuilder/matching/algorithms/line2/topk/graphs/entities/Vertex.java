package ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities;

import java.io.Serializable;

/**
 * <p>
 * Title: Vertex
 * </p>
 * <p>
 * Description: representation of a vertex in a graph
 * </p>
 * Implements {@link Serializable}
 * 
 * @author Haggai Roitman
 * @version 1.1
 */

public class Vertex implements Serializable
{
    private static final long serialVersionUID = 5050907309954049599L;

    /** vertex id */
    private int vertexID;
    /** vertex associated name */
    private String vertexName;

    /**
     * Constructs a default Vertex
     */
    public Vertex()
    {
    }

    /**
     * Constructs a Vertex
     * 
     * @param vID vertex id
     * @param vName vertex assosiated name
     */
    public Vertex(int vID, String vName)
    {
        vertexID = vID;
        vertexName = vName;
    }

    /**
     * Release resources
     */
    public void nullify()
    {
        vertexName = null;
    }

    /**
     * Get the vertex id
     * 
     * @return vertex id
     */
    public int getVertexID()
    {
        return vertexID;
    }

    /**
     * Set the vertex id
     * 
     * @param vertexID the id to set
     */
    public void setVertexID(int vertexID)
    {
        this.vertexID = vertexID;
    }

    /**
     * Get the vertex name
     * 
     * @return vertex associated name
     */
    public String getVertexName()
    {
        return vertexName;
    }

    /**
     * Set the vertex name
     * 
     * @param vertexName the name to set
     */
    public void setVertexName(String vertexName)
    {
        this.vertexName = vertexName;
    }

    /**
     * Clone the vertex
     * 
     * @return a new vertex
     */
    public Vertex clone()
    {
        return new Vertex(this.vertexID, this.vertexName);
    }

    /**
     * Check if two vertexes are equal
     * 
     * @return true if the vertexes are equal
     */
    public boolean equals(Object obj)
    {
        return (this.vertexID == ((Vertex) obj).getVertexID() && this.vertexName
            .equals(((Vertex) obj).getVertexName()));
    }
}