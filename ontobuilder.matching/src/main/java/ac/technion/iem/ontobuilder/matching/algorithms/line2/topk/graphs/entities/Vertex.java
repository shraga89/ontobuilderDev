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
    /** id of the entity represented by the vertex */
    private long vertexNameID;
    /* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Vertex " + vertexID + ";" + vertexName;
	}

	/** name of the vertex */
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
     * @param vNameID  id of the entity represented by the vertex 
     * @param vName vertex associated name
     */
    public Vertex(int vID, long vNameID, String vName)
    {
        this.vertexID = vID;
        this.vertexNameID = vNameID;
        this.vertexName = vName;
    }

    /**
     * Release resources
     */
    public void nullify()
    {
    	this.vertexName = null;
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
     * Get the vertex name id
     * 
     * @return vertex associated name id
     */
    public long getVertexNameID()
    {
        return vertexNameID;
    }

    /**
     * Set the vertex name id
     * 
     * @param vertexNameID the name id to set
     */
    public void setVertexNameID(long vertexNameID)
    {
        this.vertexNameID = vertexNameID;
    }

    /**
     * Clone the vertex
     * 
     * @return a new vertex
     */
    public Vertex clone()
    {
        return new Vertex(this.vertexID, this.vertexNameID, this.vertexName);
    }

    public String getVertexName() {
    	return this.vertexName;
    }
    
    /**
     * Check if two vertexes are equal
     * 
     * @return true if the vertexes are equal
     */
    public boolean equals(Object obj)
    {
        return (this.vertexID == ((Vertex) obj).getVertexID() && this.vertexNameID
            == ((Vertex) obj).getVertexNameID() && this.vertexName
                    == ((Vertex) obj).getVertexName());
    }
}