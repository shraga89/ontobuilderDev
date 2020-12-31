package ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.impl;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.BipartiteGraph;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.Edge;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.Vertex;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.utils.VertexArray;

public class FastMaxWeightBipartiteMatchingAlgorithmTest extends TestCase {
	
	private BipartiteGraph g;
	private VertexArray pot;
	private FastMWBGAlgorithm Falgo;
	private MaxWeightBipartiteMatchingAlgorithm Salgo;

	public FastMaxWeightBipartiteMatchingAlgorithmTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		Vertex a = new Vertex(1,1,"a");
		Vertex b = new Vertex(2,2,"b");
		Vertex c = new Vertex(3,3,"c");
		Vertex d = new Vertex(0,4,"d");
		
		Set<Vertex> X = new HashSet<Vertex>();
		Set<Vertex> Y = new HashSet<Vertex>();
		X.add(a);
		X.add(c);
		Y.add(d);
		Y.add(b);
		Edge e1 = new Edge(1,2,1.3,true);
		Edge e2 = new Edge(3,2,2.2,true);
		Edge e3 = new Edge(3,0,1,true);
		Set<Edge> e = new HashSet<Edge>();
		e.add(e1);
		e.add(e2);
		e.add(e3);
		//notice that i should send it inverse
		g = new BipartiteGraph(e, Y, X);
		pot = new VertexArray(g, new Double(0));		
	}
	public void setupCicle(){
		Vertex a = new Vertex(0,0,"a");
		Vertex b = new Vertex(1,1,"b");
		Vertex c = new Vertex(2,2,"c");
		Vertex d = new Vertex(3,3,"d");
		Vertex f = new Vertex(4,4,"e");
		Edge e1 = new Edge(0,3,0.2,true);
		Edge e2 = new Edge(0,4,0.1,true);
		Edge e3 = new Edge(1,3,0.2,true);
		Edge e4 = new Edge(1,4,0.1,true);
		Edge e5 = new Edge(2,4,0.1,true);
		
		Set<Vertex> X = new HashSet<Vertex>();
		Set<Vertex> Y = new HashSet<Vertex>();
		X.add(a);
		X.add(b);
		X.add(c);
		Y.add(d);
		Y.add(f);
		Set<Edge> e = new HashSet<Edge>();
		e.add(e1);
		e.add(e2);
		e.add(e3);
		e.add(e4);
		e.add(e5);
		g = new BipartiteGraph(e, Y, X);
		pot = new VertexArray(g, new Double(0));
	}
	public void setupRandom(){
		int right = 400;
		int left = 800;
		Set<Vertex> X = new HashSet<Vertex>();
		Set<Vertex> Y = new HashSet<Vertex>();
		Set<Edge> e = new HashSet<Edge>();
		for(int i=0;i<=right;i++)
			X.add(new Vertex(i,i,"left,V="+i));
		for(int i=right+1;i<=left;i++)
		Y.add(new Vertex(i,i,"right,V="+i));
		for(int i=0;i<=right;i++) {
			double d = Math.random()-1;
			for(int j=right+1;j<=left;j++) {
				double d2  = Math.random();
				//TODO change to 0.5
				if (d2<d)
					continue;
				e.add(new Edge(i,j,Math.random(),true));
			}
		}
		g = new BipartiteGraph(e, Y, X);
		pot = new VertexArray(g, new Double(0));
	}
	public static void printMatch(Set<Edge> bestMatching){
		for (Edge e : bestMatching){
			System.out.print(e.getSourceVertexID()+"<-->"+e.getTargetVertexID()+" with weight "+e.getEdgeWeight());
			System.out.println();
		}
	}
	
//	public void test0(){
//		setupCicle();
//		Falgo = new FastMaxWeightBipartiteMatchingAlgorithm(g, pot);
//        Set<Edge> bestMatching = Falgo.runAlgorithm();
//        printMatch(bestMatching);	
//	}
//	public void testrunAlgorithm(){
//		setupRandom();
//		System.out.println("starting test:");
//		double fastTime = System.currentTimeMillis();
//		Falgo = new FastMaxWeightBipartiteMatchingAlgorithm(g, pot);
//		Set<Edge> bestMatching = Falgo.runAlgorithm();
//		fastTime = System.currentTimeMillis()-fastTime;
//        printMatch(bestMatching);
//        System.out.println("the fast algorithm took "+fastTime);
//	}
	public void testrunAlgorithmComplex(){
		setupRandom();
		System.out.println("starting test with Fast Algorithm:");
		Falgo = new FastMWBGAlgorithm(g, pot);
		double fastTime = System.currentTimeMillis();
		Set<Edge> FastbestMatching = Falgo.runAlgorithm();
		fastTime = System.currentTimeMillis()-fastTime;
        //printMatch(FastbestMatching);
        System.out.println("starting test with Slow Algorithm:");
        Salgo = new MaxWeightBipartiteMatchingAlgorithm(g, pot);
        double slowTime = System.currentTimeMillis();
        Set<Edge> SlowbestMatching = Salgo.runAlgorithm();
        slowTime = System.currentTimeMillis()-slowTime;
        //printMatch(SlowbestMatching);
        double totalRound=0.0;
        for (Edge e1 : FastbestMatching){
        	boolean match=false;
        	for(Edge e2 : SlowbestMatching){
        		if((e1.getSourceVertexID() != e2.getSourceVertexID()) || e1.getTargetVertexID()!=e2.getTargetVertexID())
        			continue;
        		totalRound+=Math.abs(e1.getEdgeWeight()-e2.getEdgeWeight());
        		match = true;
        	}
        	if (!match)
        		System.out.println("not all Fast matching Edges are in Slow matching");
        }
        for (Edge e1 : SlowbestMatching){
        	boolean match=false;
        	for(Edge e2 : FastbestMatching){
        		if((e1.getSourceVertexID() != e2.getSourceVertexID()) || e1.getTargetVertexID()!=e2.getTargetVertexID())
        			continue;
        		match = true;
        	}
        	if (!match)
        		System.out.println("not all Slow matching Edges are in Fast matching");
        }
        System.out.println("the total round is "+totalRound);
        System.out.println("the fast algorithm took "+fastTime);
        System.out.println("the slow algorithm took "+slowTime);
	}
	
//	public void testPQ(){
//		PQ PQ = new PQ(new FloatComparator());
//		for (int i=1;i<=100;i++) {
//		Float f =(Float) (2/(float)i);
//		PQ.insert(i,f);
//		}
//			
//		for(int i=0;i<100;i+=2){
//			PQItem temp1 = (PQItem) PQ.deleteMinAndGetIt();
//			PQItem temp2 = (PQItem) PQ.deleteMinAndGetIt();
//			float a =(Float) temp1.getPriority();
//			float b = (Float) temp2.getPriority();
//			assertEquals("the first is "+a+" ,and the second is "+b,true, a<=b);
//		}
//		 
//	}

}
