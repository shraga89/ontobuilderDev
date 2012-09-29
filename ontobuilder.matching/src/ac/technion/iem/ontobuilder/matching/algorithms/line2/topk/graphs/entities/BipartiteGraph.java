package ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * <p>
 * Title: BipartiteGraph
 * </p>
 * <p>
 * Description: represents bipartite graph
 * </p>
 * Extends {@link Graph} <br>
 * Implements {@link Serializable}
 * 
 * @author Haggai Roitman
 * @version 1.1
 */
public class BipartiteGraph extends Graph implements Serializable // G(X,Y,E)
{
	public static final long ID_DUMMY_VERTEX = -2;

    private static final long serialVersionUID = 7953761776911271034L;

    /** holds the right vertexes group of the graph */
    protected Set<Vertex> rightVertexesSet;
    /** holds the left vertexes group of the graph */
    protected Set<Vertex> leftVertexesSet;

    /**
     * Constructs a BipartiteGraph
     */
    public BipartiteGraph()
    {
        super();
    }

    /**
     * Constructs a BipartiteGraph
     * 
     * @param e  
     * @param x - right vertices 
     * @param y - left vertices
     */
    public BipartiteGraph(Set<Edge> e, Set<Vertex> x, Set<Vertex> y)
    {
    	super();
    	super.edgesSet = e;
    	Set<Vertex> union = new HashSet<Vertex>(x);
    	union.addAll(y);
    	super.vertexesSet = union;
        rightVertexesSet = x;
        leftVertexesSet = y;
    }

    /**
     * Sets the right vertexes set
     * 
     * @param rightVertexesSet the right group
     */
    public void setRightVertexesSet(Set<Vertex> rightVertexesSet)
    {
        this.rightVertexesSet = rightVertexesSet;
    }

    /**
     * Sets the left vertexes set
     * 
     * @param leftVertexesSet the left group
     */
    public void setLeftVertexesSet(Set<Vertex> leftVertexesSet)
    {
        this.leftVertexesSet = leftVertexesSet;
    }

    /**
     * Release resources
     */
    public void nullify()
    {
        try
        {
            rightVertexesSet.clear();
            leftVertexesSet.clear();
            super.nullify();
        }
        catch (NullPointerException e)
        {
        }
    }

    /**
     * @return right vertexes group
     */
    public Set<Vertex> getRightVertexesSet()
    {
        return rightVertexesSet;
    }

    /**
     * @return left 
     */
    public Set<Vertex> getLeftVertexesSet()
    {
        return leftVertexesSet;
    }

    public Iterator<Vertex> getLeftVertexSetIterator()
    {
        return leftVertexesSet.iterator();
    }

    public Iterator<Vertex> getRightVertexSetIterator()
    {
        return rightVertexesSet.iterator();
    }

    /**
     * Return vertex id according to the vertex group association and id
     * 
     * @param id
     * @param left flag if belong to the left vertex group of the graph
     * @return vertex index in the graph
     */
    public int getVertexIndexByNameID(long id, boolean left)
    {
        if (left)
        {
            Iterator<Vertex> it = leftVertexesSet.iterator();
            while (it.hasNext())
            {
                Vertex v = (Vertex) it.next();
                if (v.getVertexNameID() == id)
                    return v.getVertexID();
            }
            return -1;
        }
        else
        {// right
            Iterator<Vertex> it = rightVertexesSet.iterator();
            while (it.hasNext())
            {
                Vertex v = (Vertex) it.next();
                if (v.getVertexNameID() == id)
                    return v.getVertexID();
            }
            return -1;
        }
    }

    /**
     * Clone the graph
     */
    public Object clone()
    {
        BipartiteGraph clonedBipartiteGraph = new BipartiteGraph(new HashSet<Edge>(edgesSet),
            new HashSet<Vertex>(rightVertexesSet), new HashSet<Vertex>(leftVertexesSet));
        clonedBipartiteGraph.buildAdjMatrix();
        return clonedBipartiteGraph;
    }
}