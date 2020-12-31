package ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.impl;

import java.util.HashSet;
import java.util.Set;

import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.BipartiteGraph;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.Edge;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.Vertex;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.utils.VertexArray;
import junit.framework.TestCase;
/*
 * on Graph.java, the method buildAdjMatrix() has been change (TODO) change it back!
 */
public class MaxWeightBipartiteMatchingAlgorithmTest extends TestCase {

	private BipartiteGraph g;
	private VertexArray pot;
	private MaxWeightBipartiteMatchingAlgorithm algo;
	
	public MaxWeightBipartiteMatchingAlgorithmTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		Vertex a = new Vertex(1,1,"a");
		Vertex b = new Vertex(2,2,"b");
		Vertex c = new Vertex(3,3,"c");
		Vertex d = new Vertex(4,4,"d");
		Set<Vertex> X = new HashSet<Vertex>();
		Set<Vertex> Y = new HashSet<Vertex>();
		X.add(a);
		X.add(c);
		Y.add(d);
		Y.add(b);
		Edge e1 = new Edge(1,2,1,true);
		Edge e2 = new Edge(3,2,2.2,true);
		Edge e3 = new Edge(3,4,1,true);
		Set<Edge> e = new HashSet<Edge>();
		e.add(e1);
		e.add(e2);
		e.add(e3);
		//notice that i should send it inverse
		g = new BipartiteGraph(e, Y, X);
		pot = new VertexArray(g, new Double(0));		
	}
	public void printMatch(Set<Edge> bestMatching){
		for (Edge e : bestMatching){
			System.out.print(e.getSourceVertexID()+"<-->"+e.getTargetVertexID()+" with weight "+e.getEdgeWeight());
			System.out.println();
		}
	}
	
	public void testrunAlgorithm(){
		algo = new MaxWeightBipartiteMatchingAlgorithm(g, pot);
        Set<Edge> bestMatching = algo.runAlgorithm();
        printMatch(bestMatching);
	}

}
