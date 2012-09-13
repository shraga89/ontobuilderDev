package ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.utils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.Edge;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.Graph;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.Vertex;

/**
 * <p>Title: GraphUtilities</p>
 */
public final class GraphUtilities
{

    /**
     * Get the edges that come out of a vertex
     * 
     * @param g the {@link Graph}
     * @param v the {@link Vertex}
     * @return the {@link EdgesSet}
     */
    public static Set<Edge> getVertexOutEdges(Graph g, Vertex v) // O(E)
    {
    	Set<Edge> vOutEdges = new HashSet<Edge>();
        Iterator<Edge> edgesIterator = g.getEdgesIterator();
        while (edgesIterator.hasNext())
        {
            Edge edge = (Edge) edgesIterator.next();
            if (edge.getSourceVertexID() == v.getVertexID())
                vOutEdges.add(edge);
        }
        return vOutEdges;
    }

    /**
     * Remove all the edges with a weight lower than the one defined
     * 
     * @param g the {@link Graph}
     * @param w the upper threshold weight
     * @return the {@link Graph} without the removed edges
     */
    public static Graph removeLowWeightEdges(Graph g, double w)
    {
        Graph g_ = (Graph) g.clone();
        Set<Edge> toRemove = new HashSet<Edge>();
        for (Edge e : g_.getEdgesSet())
        {
            if (e.getEdgeWeight() < w)
                toRemove.add(e);
        }
        g_.getEdgesSet().removeAll(toRemove);
        return g_;
    }

    /**
     * Get all the edges with a certain weight
     * 
     * @param g the {@link Graph}
     * @param w the weight to compare to
     * @return the {@link EdgesSet}
     */
    public static Set<Edge> getEdgesWithWeight(Graph g, double w)
    {
    	Set<Edge> es = new HashSet<Edge>();
        for (Iterator<Edge> it = g.getEdgesSet().iterator(); it.hasNext();)
        {
            Edge e = (Edge) it.next();
            if (e.getEdgeWeight() == w)
                es.add(e.clone());
        }
        return es;
    }

    /**
     * Get all the edges that are adjacent to a certain vertex
     * 
     * @param g the {@link Graph}
     * @param v the vertex
     * @return the {@link EdgesSet}
     */
    public static Set<Edge> getVertexAdjEdges(Graph g, Vertex v) // O(E)
    {
    	Set<Edge> vAdjEdges = new HashSet<Edge>();
        Iterator<Edge> edgesIterator = g.getEdgesIterator();
        while (edgesIterator.hasNext())
        {
            Edge edge = (Edge) edgesIterator.next();
            if (edge.getSourceVertexID() == v.getVertexID())
                vAdjEdges.add(edge);
        }
        return vAdjEdges;
    }

    /**
     * Get the target vertex of the edge
     * 
     * @param g the {@link Graph}
     * @param e the {@link Edge}
     * @return the {@link Vertex}
     */
    public static Vertex getEdgeTargetVertex(Graph g, Edge e) // O(V)
    {
        return g.getVertex(e.getTargetVertexID());
    }

    /**
     * Get the first adjacent edge of the vertex
     * 
     * @param g the {@link Graph}
     * @param v the {@link Vertex}
     * @return the {@link Edge}
     */
    public static Edge getVertexFirstAdjEdge(Graph g, Vertex v) // O(E)
    {
        Iterator<Edge> edgesIterator = g.getEdgesIterator();
        while (edgesIterator.hasNext())
        {
            Edge edge = (Edge) edgesIterator.next();
            if (edge.getSourceVertexID() == v.getVertexID())
                return edge;
        }
        return null;
    }

    /**
     * Get the out degree of the vertex
     * 
     * @param g the {@link Graph}
     * @param v the {@link Vertex}
     * @return the degree
     */
    public static int getVertexOutDeg(Graph g, Vertex v) // O(E)
    {
        Iterator<Edge> edgesIterator = g.getEdgesIterator();
        int outDeg = 0;
        while (edgesIterator.hasNext())
        {
            Edge edge = (Edge) edgesIterator.next();
            if (edge.getSourceVertexID() == v.getVertexID())
                outDeg++;
        }
        return outDeg;
    }
}