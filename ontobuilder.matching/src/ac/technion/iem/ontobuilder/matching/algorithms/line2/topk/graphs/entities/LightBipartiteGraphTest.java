package ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

public class LightBipartiteGraphTest extends TestCase {
	private BipartiteGraph g;
	private LightBipartiteGraph lg;
	public LightBipartiteGraphTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
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
	}
	public void testLightBipartiteGraph() throws Exception{
		lg = new LightBipartiteGraph(g);
		assertEquals(true, lg.isLeft((short) 1));
		assertEquals(false, lg.isLeft((short) 2));
		assertEquals(true, lg.isLeft((short) 3));
		assertEquals(false, lg.isLeft((short) 4));
	}

}
