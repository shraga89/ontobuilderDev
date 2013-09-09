package ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
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
 * @author Omer Ben-Porat
 * @version 1.1
 */
public class BipartiteGraph extends Graph implements Serializable // G(X,Y,E)
{
	private Long EdgeID= (long) 0;
	
	public static final long ID_DUMMY_VERTEX = -2;

    private static final long serialVersionUID = 7953761776911271034L;

    	
    protected HashMap<Long,Edge> edgeMap = new HashMap<Long,Edge>() ;
    /** holds for each VertexId its touching Edges, by Edge Id */
    protected HashMap<Integer,Set<Long> > vertexTouchEdges = new HashMap<Integer,Set<Long>>();
    /** holds the right vertexes group of the graph */
    protected Set<Vertex> rightVertexesSet= new HashSet<Vertex>();
    /** holds the left vertexes group of the graph */
    protected Set<Vertex> leftVertexesSet = new HashSet<Vertex>();

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
    	//Initialize Graph
    	super();
    	super.edgesSet = e;
    	Set<Vertex> union = new HashSet<Vertex>(x);
    	union.addAll(y);
    	super.vertexesSet = union;
    	
    	//Initialize Vertices
    	rightVertexesSet = x;
    	leftVertexesSet = y;
    	
    	//Initialize vertexTouchEdges
    	for(Vertex v : rightVertexesSet) {
    		vertexTouchEdges.put(v.getVertexID(),new HashSet<Long>());
    	}
    	for(Vertex v : leftVertexesSet) {
    		vertexTouchEdges.put(v.getVertexID(),new HashSet<Long>());
    	}
    	//Initialize Edges
    	for(Edge edge : e){
    		edgeMap.put(EdgeID, edge);
    		vertexTouchEdges.get(edge.getSourceVertexID()).add(EdgeID);
    		vertexTouchEdges.get(edge.getTargetVertexID()).add(EdgeID);
    		EdgeID++;
    	}
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
            edgeMap.clear();
            vertexTouchEdges.clear();
            super.nullify();
        }
        catch (NullPointerException e)
        {
        }
    }
    
    /**
     * removes Edge from the graph
     * @deprecated
     * 
     */
    public Edge removeEdgeFromGraph(Edge toRemove)
    {
        boolean ok = edgesSet.remove(toRemove);
        for (Long id : edgeMap.keySet()){
        	if (edgeMap.get(id) == toRemove){
        		edgeMap.remove(id);
        		return toRemove;
        	}
        }
        if (!ok)
            return null;
        else
        {
        	//TODO fix the adjacent Matrix
            adjMatrix[toRemove.getSourceVertexID()][toRemove.getTargetVertexID()] = Graph.INF;
            return toRemove;
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
     * Adds edge to graph
     * 
     * @param toAdd {@link Edge} to add graph
     * 
     */
    public void addEdgeToGraph(Edge toAdd)
    {
        edgesSet.add(toAdd);
        edgeMap.put(EdgeID,toAdd);
        if (vertexTouchEdges.containsKey(toAdd.getSourceVertexID())) {
        	vertexTouchEdges.get(toAdd.getSourceVertexID()).add(EdgeID);
        }
        if (vertexTouchEdges.containsKey(toAdd.getTargetVertexID())) {
        	vertexTouchEdges.get(toAdd.getTargetVertexID()).add(EdgeID);
        }
        adjMatrix[toAdd.getSourceVertexID()][toAdd.getTargetVertexID()] = toAdd.getEdgeWeight();
        EdgeID++;
    }
    /**
     * Builds the Adjacency matrix of the graph
     * //TODO should be built from the Bipartite graph's edges and vertices
     * @deprecated
     */
    public void buildAdjMatrix() {
    	super.buildAdjMatrix();
    }

    /**
     * Return vertex id according to the vertex group association and id
     * 
     * @param id
     * @param left flag if belong to the left vertex group of the graph
     * @return vertex index in the graph
     *   //TODO can be optimized
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
     * Get all the adjacent edges in the graph
     * 
     * @param e edge to search its adjacent edges on its vertexes
     * @return all adjacent edges
     */
    public Set<Edge> getAllAdjacentEdges(Edge e)
    {// O(E'), where E' is the number of Adjacent Edgs
    	int source = e.getSourceVertexID();
    	int target = e.getTargetVertexID();
    	HashSet<Edge> toReturn = new HashSet<Edge>();
    	for (Long eid : vertexTouchEdges.get(source)) {
    		toReturn.add(edgeMap.get(eid));
    	}
    	for (Long eid : vertexTouchEdges.get(target)) {
    		toReturn.add(edgeMap.get(eid));
    	}
    	toReturn.remove(e);
    	return toReturn;    	
    }

    /**
     * Clone the graph
     */
    public Object clone()
    {
        BipartiteGraph clonedBipartiteGraph =  new BipartiteGraph(new HashSet<Edge>(edgesSet),
            new HashSet<Vertex>(rightVertexesSet), new HashSet<Vertex>(leftVertexesSet));
        clonedBipartiteGraph.buildAdjMatrix();
        return clonedBipartiteGraph;
    }
    /**
     * Get an iterator to the edges
     * @deprecated
     */
    public Iterator<Edge> getEdgesIterator()
    {
        return edgesSet.iterator();
    }

    /**
     * Get an iterator to the vertexes
     * @deprecated
     */
    public Iterator<Vertex> getVertexesIterator()
    {
        return vertexesSet.iterator();
    }
    
    
    public void setVertexesSet(Set<Vertex> vertexesSet) throws UnsupportedOperationException
    {
    	throw new UnsupportedOperationException();
    }
    public void setEdgesSet(Set<Edge> e) throws UnsupportedOperationException
    {
    	throw new UnsupportedOperationException();
    }
    
    /**
     * @deprecated returns copy of the edges in the edge map, should not be used as the returned
     * set will be unsynchronized with the original set. 
     */
    public HashSet<Edge> getEdgesSet() 
    {
    	return new HashSet<Edge>(edgeMap.values());
    }
    
    public Collection<Edge> getEdges()
    {
    	return edgeMap.values();
    }
}