package ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.impl;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.BipartiteGraph;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.Edge;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.Graph;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.LEdge;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.LightBipartiteGraph;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.Vertex;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.utils.FloatComparator;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.utils.PQ;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.utils.PQItem;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.utils.VertexArray;

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
 * This is a second lighter version of the algorithm.
 * 
 * This is the second edition of this algorithm. 2 major improvements:
 * 1. The algorithm runs on a light version of bipartite graph (to increase locality)
 * 2. Floats instead of Double for Edge's weights, Short instead of Int for the Vertex number
 * 3. At this point, only the Naive heuristic is available. 
 * 
 * 
 * @author Omer Ben-Porat
 * @version 1.1
 */

public class FastMWBGAlgorithm implements SchemaMatchingsAlgorithm {

	
    private MaxWeightBipartiteMatchingAlgorithmHeuristicsEnum heuristic = MaxWeightBipartiteMatchingAlgorithmHeuristicsEnum.NAIVE_HEURISTIC;
    
    /**new variables */
    private LightBipartiteGraph lg;
    private short lgSize;
    private float[] pot;
    BitSet free;
    
    private double oneOverS;
    private int exp;

    /**
     * Constructs a MaxWeightBipartiteMatchingAlgorithm with
     * 
     * @param g a {@link BipartiteGraph}
     * @param pot {@link VertexArray}
     * 
     * 

    
     */
    public FastMWBGAlgorithm(BipartiteGraph g, VertexArray pot)
    {
    	//System.out.println("Fast MWBG is now initialized");
    	lg = new LightBipartiteGraph(g);
        lgSize = lg.getSize();
        scaleWeights();
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
    	//System.out.println("Fast MWBG is Running");
        Set<Edge> result = new HashSet<Edge>();
        if (lg.getSize()==0)
            return result;
        BitSet free = new BitSet(lgSize);
        free.set(0, lgSize);
        LEdge[] pred = new LEdge[lgSize];
        for(int i=0;i<lgSize;i++)
        	pred[i]=null;
        float[] dist = new float[lgSize];
        PQ PQ = new PQ(new FloatComparator());
        pot = new float[lgSize] ;
        
        switch (heuristic)
        {
        //Naive - sets the potential of every node in B equal to zero
        // and every node of A equal to the maximal cost of all edges
        case NAIVE_HEURISTIC: 
            float C = (float) 0.0;
            for (short source=0; source<lgSize;source++){
            	for(LEdge le: lg.getAdjVertex((short) source)){
            		
            		float edgeC = le.getW();
            		if (edgeC > C)
            			C=edgeC;
            	}
            }
            for (short v=0; v<lgSize;v++){
            	if(lg.isLeft(v))
            		pot[v] = C;
            	else
            		pot[v]=(float) 0.0;
            }
              
            break;
            
		default:
			break;
        }
                
        for (short a=0; a <=lgSize; a++) 
        	if(lg.getLeft().get(a)==true &&free.get(a)) {
        		//System.out.println("the Source is "+a);
        		augment(a,pot,free,pred,dist,PQ);
        	}
        		
        
        
        for(short b=0; b <lgSize; b++) {
        	if (lg.getLeft().get(b))
        		continue;
        	for (LEdge le : lg.getAdjVertex(b)){
        		result.add(new Edge(le.gettID(),b,le.getW(),true));
        	}
        }
        //System.out.println("Fast MWBG has finished");
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
    private void augment(short a, float[] pot, BitSet free, LEdge[] pred,
			float[] dist, PQ PQ) {
    	//augument:initialization 
        dist[a]=0;
        short bestVertexInA = a;
        float minA = pot[a];
        float Delta;
        Stack<Short> RA = new Stack<Short>(), RB = new Stack<Short>();
        RA.push(a);
        short a1 = a;
        // relax all edges out of a1
        for (LEdge e : lg.getAdjVertex(a1))
        {
        	short b = e.gettID();
            //db = dist[a1]+(pot[a1]+pot[b]-c[e])
            float db = dist[a1]+(pot[a1]+pot[b]-e.getW());
            if (pred[b]==null)
            {
                dist[b]=db;
                pred[b]=e;
                RB.push(b);
                PQ.insert(b,db);
            }
            else
            	if (db < dist[b])
            {
                dist[b]=db;
                pred[b]=e;
                PQ.decreaseP(b, db);
            }
        }
       
        while (true) //the main loop
        {
            // select from PQ the node b with minimal distance db
            short b;
            float db = (float) 0.0;
            if (PQ.isEmpty())
                b = -1;
            else
            {
            	PQItem temp;
                temp =  (PQItem) PQ.deleteMinAndGetIt();
                b = (Short) temp.getObject();
                db = (Float) temp.getPriority();
                if (db != dist[b])
                	System.out.println("problem at stage 2");
            }
            // distinguish three cases 
            if (b == -1 || db >= minA) //case 1
            {
                Delta = minA;
                // augmentation by path to best node in A
                augmentPathTo(bestVertexInA, pred);
                free.set(a, false);
                free.set(bestVertexInA, true);
                break;
            }
            else if (free.get(b)==true ) //case 2
            {
        
                Delta = db;
                // augmentation by path to b
                augmentPathTo(b, pred);
                free.set(a, false);
                free.set(b, false);
                break;
            }
            else //case 3
            {
 
                // continue shortest-path computation
                LEdge e = lg.getVertexFirstAdjEdge(b);
                short a11 = e.gettID();
                pred[a11]=e;
                RA.push(a11);
                dist[a11] =  db;
                //the following condition checks if we will gain points flowing
                //on the opposite direction 
                if (db + pot[a11] < minA)
                {
                    bestVertexInA = a11;
                    minA = db + pot[a11];
                }
                // relax all edges out of a11
                for (LEdge le: lg.getAdjVertex(a11))
                {
                	short b1 = le.gettID();
                    //db1 = dist[a11]+(pot[a11]+pot[b1]-c[e])
                    float db1 = dist[a11]+(pot[a11]+pot[b1]-le.getW());
                    
                    if (pred[b1]==null)
                    {
                        dist[b1]=db1;
                        pred[b1]=le;
                        RB.push(b1);
                        PQ.insert(b1,db1);
                    }
                    else if (db1 < dist[b1])
                    {
                        dist[b1]=db1;
                        pred[b1]=le;
                        PQ.decreaseP(b1, db1);
                    }
                }
            }
        }
        // augment: potential update and re-initialization
        while (!RA.isEmpty())
        {
            short a12 = RA.pop();
            pred[a12]=null;
            float potChange = Delta - dist[a12];
            if (potChange <= 0)
                continue;
            pot[a12] = pot[a12] - potChange;
        }
        while (!RB.isEmpty())
        {
            short b12 = RB.pop();
            pred[b12]=null;
            if (PQ.member(b12))
                PQ.delete(b12);
            float potChange = Delta - dist[b12];
            if (potChange <= 0)
                continue;
            pot[b12] =  pot[b12]+ potChange;
        }
	}

    /**
     * Augments the path the vertex
     * 
     * @param g the {@link Graph}
     * @param v the {@link Vertex} to start from
     * @param pred a {@link VertexArray}
     */
	private void augmentPathTo(short v, LEdge[] pred) {

        LEdge e = pred[v];
        while (e!=null){
        	lg.turnOverEdgeDirection(e);
        	e = pred[e.gettID()];        	
        }
	}
	
    /**
     * Scale weights in the graph
     * 
     * @param g {@link BipartiteGraph}
     * @param f value
     */
    private void scaleWeights()
    {
       
        double S = computeS();
        ArrayList<LEdge>[] adjList = lg.getAdjList();
        for (short source=0; source<lgSize;source++){
        	for (LEdge le : adjList[source]) {
        		float edgeC = le.getW();
        		float newWeight = (float) scaleWeight(edgeC,S);
        		le.setW(newWeight);
        	}
        }
    }

    private double computeS()
    {
        oneOverS = ldexp(1, exp - 22);
        return ldexp(1, 22 - exp);
    }

    /**
     * Scale the weight
     */
  
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
     * Release resources
     */
    public void nullify()
    {
        try
        {
        	lg = null;
        	free = null;
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

}