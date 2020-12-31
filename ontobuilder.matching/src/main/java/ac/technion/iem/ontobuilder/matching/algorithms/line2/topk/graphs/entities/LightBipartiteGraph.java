package ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities;


/**
 * <p>
 * Title: LightBipartiteGraph
 * 
 * This class gets a Bipartite Graph, and makes a lighter and more efficient version of it.
 * used in FastMaxWeightBipartiteMatchingAlgorithm
 * 
 * @author Omer Ben-Porat
 */
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Set;

public class LightBipartiteGraph {
	
	protected BitSet left;
	protected ArrayList<LEdge>[] adjList;
	public ArrayList<LEdge>[] getAdjList() {
		return adjList;
	}

	protected short lgSize;
	
	@SuppressWarnings("unchecked")
	public LightBipartiteGraph(BipartiteGraph G){
		
		lgSize =(short)( G.leftVertexesSet.size()+G.rightVertexesSet.size());
						
		adjList = (ArrayList<LEdge>[]) new ArrayList[lgSize];
		left = new BitSet(lgSize);
		//adjList initialization
		for(int i = 0; i<lgSize;i++)
			adjList[i] = new ArrayList<LEdge>();
		
		//Initialize vertexTouchEdges
    	for(Vertex v : G.getRightVertexesSet()) {
    		short vertexID = (short) v.getVertexID();
    		left.set(vertexID,false);  
    	}
    	for(Vertex v : G.getLeftVertexesSet()) {
    		short vertexID = (short) v.getVertexID();
    		left.set(vertexID,true);    		
    	}
    	//Initialize Edges
    	for(Edge edge : G.getEdgesSet()){
    		short source = (short) edge.getSourceVertexID();
    		LEdge le = new LEdge(edge);
    		adjList[source].add(le);
    	}
	}

	public boolean isLeft(short v){
		return left.get(v);
	}
	public short getSize(){
		return lgSize;
	}
	public BitSet getLeft() {
		return left;
	}
	public ArrayList<LEdge> getAdjVertex(short source){
		return adjList[source];
	}
	
	public LEdge getVertexFirstAdjEdge(short source) {
		for(LEdge s :adjList[source] ){
			return s;
		}
		//TODO if you got here, that means that the source is not legal
		System.out.println("the source is"+source);
		return null;
	}
	public void turnOverEdgeDirection(LEdge le){
		short source = le.getsID();
		short target = le.gettID();
		adjList[target].add(le);
		adjList[source].remove((Object)le);
		le.turnOverLEdge();
				
	}

	public static void printMatch(Set<Edge> bestMatching){
		for (Edge e : bestMatching){
			System.out.print(e.getSourceVertexID()+"<-->"+e.getTargetVertexID()+" with weight "+e.getEdgeWeight());
			System.out.println();
		}
	}

}
