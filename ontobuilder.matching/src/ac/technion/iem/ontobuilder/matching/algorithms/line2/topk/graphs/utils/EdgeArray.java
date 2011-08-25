package ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.utils;

import java.util.HashMap;

import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.Edge;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.Graph;

/**
 * <p>Title: EdgeArray</p>
 */
public class EdgeArray
{

    private Graph g;
    private Object initVal;
    private HashMap<Edge, Object> edges;

    /**
     * Constructs a EdgeArray
     * 
     * @param g the {@link Graph}
     * @param initVal initial value
     */
    public EdgeArray(Graph g, Object initVal)
    {
        this.g = g;
        this.initVal = initVal;
        edges = new HashMap<Edge, Object>(g.getVSize());
        Edge[] edgesArray = (Edge[]) g.getEdgesSet().getMembers().toArray();
        for (int i = 0; i < edgesArray.length; i++) // O(V)
        {
            edges.put(edgesArray[i], initVal);
        }
    }

    /**
     * Constructs a EdgeArray
     * 
     * @param g the {@link Graph}
     */
    public EdgeArray(Graph g)
    {
        this.g = g;
        edges = new HashMap<Edge, Object>(g.getVSize());
        Edge[] edgesArray = (Edge[]) g.getEdgesSet().getMembers().toArray(new Edge[0]);
        for (int i = 0; i < edgesArray.length; i++) // O(V)
        {
            edges.put(edgesArray[i], new Double(((Edge) edgesArray[i]).getEdgeWeight()));
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
     * @return the inital value
     */
    public Object getInitVal()
    {
        return initVal;
    }

    /**
     * Set the edge property
     * 
     * @param e the {@link Edge}
     * @param property the property
     */
    public void setEdgeProperty(Edge e, Object property)
    {
        edges.put(e, property);
    }

    /**
     * Get the edge property
     * 
     * @param e the {@link Edge}
     * @return the property
     */
    public Object getEdgeProperty(Edge e)
    {
        return edges.get(e);
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
            edges = null;
        }
        catch (NullPointerException e)
        {

        }
    }
}