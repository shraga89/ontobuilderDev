package ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.utils;

import java.util.HashMap;

import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.Graph;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.Vertex;

/**
 * <p>Title: VertexArray</p>
 */
public class VertexArray
{

    private Graph g;
    private Object initVal;
    private HashMap<Vertex, Object> vertexes;

    /**
     * Constructs a VertexArray
     * 
     * @param g the {@link Graph}
     * @param initVal the initial value
     */
    public VertexArray(Graph g, Object initVal)
    {
        this.g = g;
        this.initVal = initVal;
        vertexes = new HashMap<Vertex, Object>(g.getVSize());
        Vertex[] vertexesArray = (Vertex[]) g.getVertexesSet().getMembers().toArray(new Vertex[0]);
        for (int i = 0; i < vertexesArray.length; i++)
        {// O(V)
            vertexes.put(vertexesArray[i], initVal);
        }
    }

    /**
     * Get the graph
     * 
     * @return the {@link Graph}
     */
    public Graph getGraph()
    {
        return g;
    }

    /**
     * Get the initial value
     * 
     * @return the initial value
     */
    public Object getInitVal()
    {
        return initVal;
    }

    /**
     * Get the vertex property
     * 
     * @param vertex the {@link Vertex}
     * @return the property
     */
    public Object getVertexProperty(Vertex vertex)
    {
        return vertexes.get(vertex);
    }

    /**
     * Set the property
     * 
     * @param vertex the {@link Vertex}
     * @param property the property to set
     */
    public void setVertexProperty(Vertex vertex, Object property)
    {
        vertexes.put(vertex, property);
    }

    /**
     * Reset resources
     */
    public void nullify()
    {
        try
        {
            g = null;
            initVal = null;
            vertexes = null;
        }
        catch (NullPointerException e)
        {

        }
    }
}