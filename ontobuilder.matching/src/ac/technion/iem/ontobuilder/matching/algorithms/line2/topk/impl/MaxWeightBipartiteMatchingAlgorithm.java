package ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.BipartiteGraph;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.Edge;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.EdgeUtil;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.Graph;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.Vertex;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.utils.GraphUtilities;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.utils.VertexArray;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.utils.VertexPQ;

/**
 * <p>
 * Title: MaxWeightBipartiteMatchingAlgorithm
 * </p>
 * <p>
 * Description: Represents the Max Weighted Bipartite Graph Algorithm ("A-Best")
 * </p>
 * <p>
 * Implementation Reference: This algorithm was based on LEDA C++ implementation<br>
 * taken from LEDA book: "A platform for combinatorial and geometric computing"<br>
 * authors: K.Mehlhorn,S.Naher , CAMBRIDGE UNIVERSITY PRESS<br>
 * </p>
 * <p>
 * The use of this implementation is only for ACADEMIC RESEARCH,and by that<br>
 * all other use of this implementation is on the user responsibility<br>
 * </p>
 * Implements {@link SchemaMatchingsAlgorithm}
 * 
 * @author Haggai Roitman
 * @author Omer Ben-Porat
 * @version 1.1
 */

public final class MaxWeightBipartiteMatchingAlgorithm implements SchemaMatchingsAlgorithm
{
    private BipartiteGraph g;
    private VertexArray pot;
    private MaxWeightBipartiteMatchingAlgorithmHeuristicsEnum heuristic = MaxWeightBipartiteMatchingAlgorithmHeuristicsEnum.NAIVE_HEURISTIC;
    //TODO
    //Currently, our goal is to minimize running time with naive heuristic. if done, we shall move back to Refined heuristic 
    private double oneOverS;
    private int exp;

    /**
     * Constructs a MaxWeightBipartiteMatchingAlgorithm with
     * 
     * @param g a {@link BipartiteGraph}
     * @param pot {@link VertexArray}
     */
    public MaxWeightBipartiteMatchingAlgorithm(BipartiteGraph g, VertexArray pot)
    {
        this.g = (BipartiteGraph) g.clone();
        this.pot = pot;
        scaleWeights(g,3.0);
    }
    
    /**
     * Set the heuristic
     * 
     * @param heuristic the heuristic
     */
    public void setHeuristic(MaxWeightBipartiteMatchingAlgorithmHeuristicsEnum heuristic)
    {
        this.heuristic = heuristic;
    }

    /**
     * Run the algorithm
     * 
     * @return a set of edges
     */
    public Set<Edge> runAlgorithm()
    {
    	Set<Edge> result = new HashSet<Edge>();
        // for all nodes(v,G) pot[v] = 0;
        // this will be done by giving pot:= VertexArray(g,new Double(0))
        if (g.getEdges().isEmpty())
            return result;
        // check that all edges are directed from A to B
        // this should be a precondition also
        VertexArray free = new VertexArray(g, new Boolean(true));
        VertexArray pred = new VertexArray(g, null);
        VertexArray dist = new VertexArray(g, new Double(0));
        VertexPQ PQ = new VertexPQ(g);

        switch (heuristic)
        {
        //Naive - sets the potential of every node in B equal to zero
        // and every node of A equal to the maximal cost of all edges
        case NAIVE_HEURISTIC: 
            Double C = new Double(0);
            for (Edge e : g.getEdges())
            {

                Double edgeC = e.getEdgeWeight();
                if (edgeC.compareTo(C) == 1)// bigger than...
                    C = edgeC;
            }
            for (Vertex l : g.getLeftVertexesSet())
                pot.setVertexProperty(l, C);
            break;
            
        case SIMPLE_HEURISTIC:
            for (Vertex a : g.getLeftVertexesSet())
            {
                Edge eMax = null;
                double C_max = 0;
                for (Edge e : GraphUtilities.getVertexAdjEdges(g, a))
                {
                    if (e.getEdgeWeight() > C_max)
                    {
                        eMax = e;
                        C_max = e.getEdgeWeight();
                    }
                }
                pot.setVertexProperty(a, new Double(C_max));
                Vertex b;
                if (eMax != null &&
                    ((Boolean) free.getVertexProperty((b = GraphUtilities.getEdgeTargetVertex(g,
                        eMax)))).booleanValue())
                {
                    eMax.turnOverEdgeDirection();
                    free.setVertexProperty(a, new Boolean(false));
                    free.setVertexProperty(b, new Boolean(false));
                }
            }
            break;
        default:
            // REFINED_HEURISTIC
            mwbmHeuristic(g, pot, free);
            break;
        }
        
        for (Vertex a : g.getLeftVertexesSet())
            if (((Boolean) free.getVertexProperty(a)).booleanValue())
                augment(g, a, pot, free, pred, dist, PQ);
        for (Vertex b : g.getRightVertexesSet())
        	result.addAll(GraphUtilities.getVertexOutEdges(g, b));
        
        EdgeUtil.turnOverEdges(false,result);

        return result;
    }

    /**
     * Augment of the graph
     * 
     * @param g {@link BipartiteGraph}
     * @param a {@link Vertex}
     * @param pot a {@link VertexArray}
     * @param free a {@link VertexArray}
     * @param pred a {@link VertexArray}
     * @param dist a {@link VertexArray}
     * @param PQ a vertex priority queue {@link VertexPQ}
     */
    private void augment(BipartiteGraph g, Vertex a, VertexArray pot, VertexArray free,
        VertexArray pred, VertexArray dist, VertexPQ PQ)
    {
        // augument:initialization - phase 1
        dist.setVertexProperty(a, new Double(0));
        Vertex bestVertexInA = a;
        double minA = ((Double) pot.getVertexProperty(a)).doubleValue();
        double Delta;
        Stack<Vertex> RA = new Stack<Vertex>(), RB = new Stack<Vertex>();
        RA.push(a);
        Vertex a1 = a;
        // relax all edges out of a1 - phase 1.1
        for (Edge e: GraphUtilities.getVertexAdjEdges(g, a1))
        {
            Vertex b = GraphUtilities.getEdgeTargetVertex(g, e);
            //db = dist[a1]+(pot[a1]+pot[b]-c[e])
            double db = ((Double) dist.getVertexProperty(a1)).doubleValue() +
                (((Double) pot.getVertexProperty(a1)).doubleValue() +
                    ((Double) pot.getVertexProperty(b)).doubleValue() - e.getEdgeWeight());
            if (pred.getVertexProperty(b) == null)
            {
                dist.setVertexProperty(b, new Double(db));
                pred.setVertexProperty(b, e);
                RB.push(b);
                PQ.insert(b, new Double(db));
            }
            else if (db < ((Double) dist.getVertexProperty(b)).doubleValue())
            {
                dist.setVertexProperty(b, new Double(db));
                pred.setVertexProperty(b, e);
                PQ.decreaseP(b, new Double(db));
            }
        }
        while (true) //the main loop
        {
            // select from PQ the node b with minimal distance db - phase 2
            Vertex b;
            double db = 0;
            if (PQ.isEmpty())
                b = null;
            else
            {
                b = (Vertex) PQ.deleteMin();
                db = ((Double) dist.getVertexProperty(b)).doubleValue();
            }
            // distinguish three cases - phase 3
            if (b == null || db >= minA) //case 3.1
            {
                Delta = minA;
                // augmentation by path to best node in A
                augmentPathTo(g, bestVertexInA, pred);
                free.setVertexProperty(a, new Boolean(false));
                free.setVertexProperty(bestVertexInA, new Boolean(true));
                break;
            }
            else if (((Boolean) free.getVertexProperty(b)).booleanValue()) //case 3.2
            {
                Delta = db;
                // augmentation by path to b
                augmentPathTo(g, b, pred);
                free.setVertexProperty(a, new Boolean(false));
                free.setVertexProperty(b, new Boolean(false));
                break;
            }
            else //case 3.3
            {
                // continue shortest-path computation
                Edge e = GraphUtilities.getVertexFirstAdjEdge(g, b);
                Vertex a11 = GraphUtilities.getEdgeTargetVertex(g, e);
                pred.setVertexProperty(a11, e);
                RA.push(a11);
                dist.setVertexProperty(a11, new Double(db));
                //the following condition checks if we will gain points flowing
                //on the opposite direction
                if (db + ((Double) pot.getVertexProperty(a11)).doubleValue() < minA)
                {
                    bestVertexInA = a11;
                    minA = db + ((Double) pot.getVertexProperty(a11)).doubleValue();
                }
                // relax all edges out of a11
                for (Edge e1: GraphUtilities.getVertexAdjEdges(g, a11))
                {
                    e = e1;
                    Vertex b1 = GraphUtilities.getEdgeTargetVertex(g, e);
                    double db1 = ((Double) dist.getVertexProperty(a11)).doubleValue() +
                        (((Double) pot.getVertexProperty(a11)).doubleValue() +
                            ((Double) pot.getVertexProperty(b1)).doubleValue() - e.getEdgeWeight());
                    if (pred.getVertexProperty(b1) == null)
                    {
                        dist.setVertexProperty(b1, new Double(db1));
                        pred.setVertexProperty(b1, e);
                        RB.push(b1);
                        PQ.insert(b1, new Double(db1));// PQ.insert(b1,db1)
                    }
                    else if (db1 < ((Double) dist.getVertexProperty(b1)).doubleValue())
                    {
                        dist.setVertexProperty(b1, new Double(db1));
                        pred.setVertexProperty(b1, e);
                        PQ.decreaseP(b1, new Double(db1));
                    }
                }
            }
        }
        // augment: potential update and re-initialization - phase 4
        while (!RA.isEmpty())
        {
            Vertex a12 = (Vertex) RA.pop();
            pred.setVertexProperty(a12, null);
            double potChange = Delta - ((Double) dist.getVertexProperty(a12)).doubleValue();
            if (potChange <= 0)
                continue;
            pot.setVertexProperty(a12,
                new Double(((Double) pot.getVertexProperty(a12)).doubleValue() - potChange));
        }
        while (!RB.isEmpty())
        {
            Vertex b12 = (Vertex) RB.pop();
            pred.setVertexProperty(b12, null);
            if (PQ.member(b12))
                PQ.delete(b12);
            double potChange = Delta - ((Double) dist.getVertexProperty(b12)).doubleValue();
            if (potChange <= 0)
                continue;
            pot.setVertexProperty(b12,
                new Double(((Double) pot.getVertexProperty(b12)).doubleValue() + potChange));
        }
    }

    /**
     * Augments the path the vertex
     * 
     * @param g the {@link Graph}
     * @param v the {@link Vertex} to start from
     * @param pred a {@link VertexArray}
     */
    private void augmentPathTo(Graph g, Vertex v, final VertexArray pred)
    {
        Edge e = (Edge) pred.getVertexProperty(v);
        while (e != null)
        {
            e.turnOverEdgeDirection();
            e = (Edge) pred.getVertexProperty(GraphUtilities.getEdgeTargetVertex(g, e));
        }
    }

    /**
     * The maximum weight bipartite matching heuristis
     * 
     * @param g {@link BipartiteGraph}
     * @param pot a {@link VertexArray}
     * @param free free {@link VertexArray}
     */
    private void mwbmHeuristic(BipartiteGraph g, VertexArray pot, VertexArray free)
    {
    	Vertex b;
        Edge e2, eb;
        VertexArray secEdge = new VertexArray(g, null);
        for(Vertex a : g.getLeftVertexesSet())
        {
            double max2 = 0, max = 0;
            eb = null;
            e2 = null;
            // compute edges with largest and second largest slack - and nothing else
            for(Edge e : GraphUtilities.getVertexAdjEdges(g, a))
            {
                double we = ((Double) e.getEdgeWeight() -
                    ((Double) pot.getVertexProperty(GraphUtilities.getEdgeTargetVertex(g, e)))
                        .doubleValue());
                if (we >= max2)
                {
                    if (we >= max)
                    {
                        max2 = max;
                        e2 = eb;
                        max = we;
                        eb = e;
                    }
                    else
                    {
                        max2 = we;
                        e2 = e;
                    }
                }
            }
            if (eb != null)
            {
                b = GraphUtilities.getEdgeTargetVertex(g, eb);
                if (((Boolean) free.getVertexProperty(b)).booleanValue())
                {
                    // match eb and change pot[] to make slack of e2 zero
                    secEdge.setVertexProperty(a, e2);
                    pot.setVertexProperty(a, new Double(max2));
                    pot.setVertexProperty(b, new Double(max - max2));
                    eb.turnOverEdgeDirection();
                    free.setVertexProperty(a, new Boolean(false));
                    free.setVertexProperty(b, new Boolean(false));
                }
                else
                {
                    // try to augment matching along
                    // path of length 3 given by sec_edge[]
                    pot.setVertexProperty(a, new Double(max));
                    e2 = GraphUtilities.getVertexFirstAdjEdge(g, b);
                    Edge e = (Edge) secEdge.getVertexProperty(GraphUtilities.getEdgeTargetVertex(g, e2));
                    if (e != null &&
                        GraphUtilities.getVertexOutDeg(g, GraphUtilities.getEdgeTargetVertex(g, e)) == 0)
                    {
                        free.setVertexProperty(a, new Boolean(false));
                        free.setVertexProperty(GraphUtilities.getEdgeTargetVertex(g, e),
                            new Boolean(false));
                        e.turnOverEdgeDirection();
                        e2.turnOverEdgeDirection();
                        eb.turnOverEdgeDirection();
                    }
                }
            }
            else
            {
                pot.setVertexProperty(a, new Double(0));
            }
        }
    }

    /**
     * Scale weights in the graph
     * 
     * @param g {@link BipartiteGraph}
     * @param f value
     */
    private void scaleWeights(final BipartiteGraph g, double f)
    {
    	//TODO the first for can be deleted - apparently used in older version - the call computeS does not use f nor c
        double C = 0;
        for (Edge e : g.getEdges())
            C = Math.max(C, fabs(e.getEdgeWeight()));

        double S = computeS(f, C);
        for (Edge e : g.getEdges())
            e.setWeight(scaleWeight(e.getEdgeWeight(), S));
    }

    private double computeS(double f, double C)
    {
        // double x = frexp(f * C);
        oneOverS = ldexp(1, exp - 53);
        return ldexp(1, 53 - exp);
    }

    /**
     * Scale the weight
     */
    //TODO change label back to private
    private double scaleWeight(double w, double S)
    {
        if (w == 0)
            return 0;
        int signW = +1;
        if (w < 0)
        {
            signW = -1;
            w = -w;
        }
        return (signW * Math.floor(w * S) * oneOverS);
    }

    /**
     * The frexp function breaks a floating-point number into a normalised fraction and an integral
     * power of 2
     */
    @SuppressWarnings("unused")
    private double frexp(double x)
    {
        boolean neg;
        int i;
        if (neg = (x < 0.0))
            x = -x;
        else if (x == 0.0)
        {
            exp = 0;
            return 0.0;
        }
        if (x < 0.5)
            for (i = 0; x < 0.5; i--)
                x *= 2.0;
        else
            for (i = 0; x >= 1.0; i++)
                x *= 0.5;
        exp = i;
        return (neg ? -x : x);
    }

    /**
     * ldexp is a function that multiplies a double precision floating point value by a specified
     * integral power of two, returning the result if it is a valid floating point value for the
     * representation used for double precision floating point values
     * 
     * @param x double precision floating point value
     * @param exp the exponent
     * @return the result
     */
    private double ldexp(double x, int exp)
    {
        return (x * Math.pow(2, exp));
    }

    /**
     * Returns the absolute value
     * 
     * @param arg the value
     * @return the absolute value
     */
    private double fabs(double arg)
    {
        if (arg < 0.0)
            arg = -arg;
        return (arg);
    }

    /**
     * Release resources
     */
    public void nullify()
    {
        try
        {
            g = null;
            pot.nullify();
            pot = null;
        }
        catch (NullPointerException e)
        {
        }
    }

    /**
     * Get the algorithm name
     */
    public String getAlgorithmName()
    {
        return TopKAlgorithmsNamesEnum.MWBM_ALGORITHM.getName();
    }
    
    @SuppressWarnings("unused")
	private void CompareToFastAlgorithm(Set<Edge> SlowResult){
        Set<Edge> FastResult = new HashSet<Edge>();
		pot = new VertexArray(g, new Double(0));
		FastMWBGAlgorithm FastAlg = new FastMWBGAlgorithm(g, pot);
		FastResult = FastAlg.runAlgorithm();
        //checking if the result from the slow Alg match the Fast Alg
        if (SlowResult.containsAll(FastResult) && FastResult.containsAll(SlowResult)) {
        	System.out.println("************************************************");
        	System.out.println("there is exact match");
        	System.out.println("************************************************");
        }
        System.out.println("************************************************");
        System.out.println("no match!");
        System.out.println("************************************************");
	}

}