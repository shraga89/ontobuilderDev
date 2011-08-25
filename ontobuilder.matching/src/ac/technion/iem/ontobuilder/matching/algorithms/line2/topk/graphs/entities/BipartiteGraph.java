package ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities;

import java.io.Serializable;
import java.util.Iterator;

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
    private static final long serialVersionUID = 7953761776911271034L;

    /** holds the right vertexes group of the graph */
    protected VertexesSet rightVertexesSet;
    /** holds the left vertexes group of the graph */
    protected VertexesSet leftVertexesSet;

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
     * @param e - {@link EdgesSet}
     * @param x - right {@link VertexesSet}
     * @param y - left {@link VertexesSet}
     */
    public BipartiteGraph(EdgesSet e, VertexesSet x, VertexesSet y)
    {
        super(e, VertexesSet.union(x, y));
        rightVertexesSet = x;
        leftVertexesSet = y;
    }

    /**
     * Sets the right vertexes set
     * 
     * @param rightVertexesSet the right {@link VertexesSet}
     */
    public void setRightVertexesSet(VertexesSet rightVertexesSet)
    {
        this.rightVertexesSet = rightVertexesSet;
    }

    /**
     * Sets the left vertexes set
     * 
     * @param leftVertexesSet the left {@link VertexesSet}
     */
    public void setLeftVertexesSet(VertexesSet leftVertexesSet)
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
            rightVertexesSet.nullify();
            leftVertexesSet.nullify();
            super.nullify();
        }
        catch (NullPointerException e)
        {
        }
    }

    /**
     * @return right vertexes group
     */
    public VertexesSet getRightVertexesSet()
    {
        return rightVertexesSet;
    }

    /**
     * @return left {@link VertexesSet}
     */
    public VertexesSet getLeftVertexesSet()
    {
        return leftVertexesSet;
    }

    public Iterator<Vertex> getLeftVertexSetIterator()
    {
        return leftVertexesSet.getMembers().iterator();
    }

    public Iterator<Vertex> getRightVertexSetIterator()
    {
        return rightVertexesSet.getMembers().iterator();
    }

    /**
     * Return vertex id according to the vertex group association and name
     * 
     * @param vName vertex name
     * @param left flag if belong to the left vertex group of the graph
     * @return vertex index in the graph
     */
    public int getVertexIndexByName(String vName, boolean left)
    {
        if (left)
        {
            Iterator<Vertex> it = leftVertexesSet.getMembers().iterator();
            while (it.hasNext())
            {
                Vertex v = (Vertex) it.next();
                if (v.getVertexName().equals(vName))
                    return v.getVertexID();
            }
            return -1;
        }
        else
        {// right
            Iterator<Vertex> it = rightVertexesSet.getMembers().iterator();
            while (it.hasNext())
            {
                Vertex v = (Vertex) it.next();
                if (v.getVertexName().equals(vName))
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
        BipartiteGraph clonedBipartiteGraph = new BipartiteGraph((EdgesSet) edgesSet.clone(),
            (VertexesSet) rightVertexesSet.clone(), (VertexesSet) leftVertexesSet.clone());
        clonedBipartiteGraph.buildAdjMatrix();
        return clonedBipartiteGraph;
    }
}