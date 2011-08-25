package ac.technion.iem.ontobuilder.matching.algorithms.line1.misc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.jdom.Element;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.OntologyUtilities;
import ac.technion.iem.ontobuilder.core.ontology.Relationship;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.core.thesaurus.Thesaurus;
import ac.technion.iem.ontobuilder.core.utils.graphs.LabeledEdge;
import ac.technion.iem.ontobuilder.core.utils.graphs.LabeledVertex;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.wrapper.SchemaMatchingsWrapper;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.match.Mismatch;
import ac.technion.iem.ontobuilder.matching.meta.match.MatchMatrix;
import ac.technion.iem.ontobuilder.matching.meta.match.MatchedAttributePair;

/**
 * <p>
 * Title: SimilarityFloodingAlgorithm
 * </p>
 * <p>
 * Description: This algorithm is based on "Similarity Flooding: A Versatile Graph Matching
 * Algorithm and its Application to Schema Matching" by Sergey Melnik, Hector Garcia-Molina, and
 * Erhard Rahm (2002). The idea behind it is that object which have similar edges connecting their
 * neighbours in different ontologies are similar themselves.
 * </p>
 * Extends {@link AbstractAlgorithm}
 * 
 * @author Written by Tomer Hary, and Amir Taller as part of a seminar group, 08/2005
 * @see LabeledVertex and LabeledEdge in ac.technion.iem.ontobuilder.core.utils.graphs.graph
 */

public class SimilarityFloodingAlgorithm extends AbstractAlgorithm
{

    // value of minimal residual vector length that will necessitate another algorithm iteration
    protected double epsilon = 0.05;

    // selected type of fixpoint formula
    protected double fixpointType = SimilarityFloodingAlgorithmFixpointFormulasTypes.FIX_BASIC.getValue();

    /**
     * Constructs a default SimilarityFloodingAlgorithm
     */
    public SimilarityFloodingAlgorithm()
    {
    }

    /**
     * Configure the algorithm parameters when user changes one of the values of the JTable
     * 
     * @param element the {@link Element} with the parameters to configure
     */
    public void configure(Element element)
    {
        Element parametersElement = element.getChild("parameters");
        if (parametersElement == null)
            return;
        List<?> parametersList = parametersElement.getChildren("parameter");
        for (Iterator<?> i = parametersList.iterator(); i.hasNext();)
        {
            Element parameterElement = (Element) i.next();
            String name = parameterElement.getChild("name").getText();
            if (name.equals("precedenceWeight") || name.equals("precedeWeight") ||
                name.equals("succeedWeight"))
            {
                double value = Double.parseDouble(parameterElement.getChild("value").getText());
                if (name.equals("Epsilon Value"))
                    epsilon = value;
                else if (name.equals("FixPoint Type"))
                    fixpointType = value;
            }
        }
    }

    public void updateProperties(HashMap<?, ?> properties)
    {
        epsilon = new Double(properties.get("Epsilon Value").toString()).doubleValue();
        fixpointType = new Double(properties.get("FixPoint Type").toString()).doubleValue();
    }

    /**
     * Make a copy of the algorithm instance
     * 
     * @return the copy
     */
    public Algorithm makeCopy()
    {
        SimilarityFloodingAlgorithm newAlgo = new SimilarityFloodingAlgorithm();
        newAlgo.epsilon = this.epsilon;
        newAlgo.fixpointType = this.fixpointType;
        newAlgo.pluginName = pluginName;
        newAlgo.mode = mode;
        return newAlgo;
    }

    /**
     * Get the name
     * 
     * @return the name
     */
    public String getName()
    {
        return "Similarity Flooding Algorithm";
    }

    public MatchInformation match(Ontology o1, Ontology o2, double[][] matchMatrix)
    {
        return match(o1, o2);
    }

    public MatchInformation match(Ontology o1, Ontology o2, Thesaurus parm3)
    {
        return match(o1, o2);
    }

    /**
     * Main algorithm Method. First it builds a Pairwise Connectivity Graph (PCG) based on the
     * graphs of the two ontologies. For edges it uses the model labels, which are generally all
     * (ischildof or issonof) the exclusivity of the labels, and the fact that for each edge there
     * is another edge going the opposite direction with the other type of label, is the reason why
     * this algorithm performs so badly in ontobuilder. the PCG has nodes with labels made from the
     * source node and target node for each such possible pair. Its edges follow the rule that an
     * edge from node (a1_s1) to node (a2_s2) in PCG with label l will exist iff there is an edge
     * with label l going from a1 to a2 in o1, and an edge will label l from s1 to s2 in o2. Weights
     * of edges are calculated for each edge as 1 divided by the number of similar labeled edges
     * leaving from the same source to any destination node. Initial weights on vertexes are
     * calculated using the function sigma0 . The algorithm runs by calling function fixpoint for
     * each node. Then the values of all the nodes are divided by the value of the node with the
     * maximum value, and the residual length is calculated if the residual length is greater than
     * epsilon, or maximum iterations were reached - the algorithm stops. Otherwise - continues. The
     * method uses the Ontobuilders Bipartate Graph matcher to find a single best match
     * 
     * @param o1 candidate {@link Ontology}
     * @param o2 target {@link Ontology}
     * @return MatchInformation
     */
    public MatchInformation match(Ontology o1, Ontology o2)
    {
        ArrayList<Term> candTerms = getTerms(o1);
        ArrayList<Term> targTerms = getTerms(o2);

        /** calculate edges and vertexes */
        Vector<LabeledVertex> vert1 = getVertexes(o1);
        Vector<LabeledVertex> vert2 = getVertexes(o2);
        Vector<LabeledEdge> edges1 = getEdges(o1, vert1);
        Vector<LabeledEdge> edges2 = getEdges(o2, vert2);

        HashMap<String, LabeledVertex> PCGVerts = new HashMap<String, LabeledVertex>();
        HashMap<String, LabeledEdge> PCGEdges = new HashMap<String, LabeledEdge>();
        /*
         * print("vert1 = "); for (int i = 0; i < vert1.size(); i++) print( ( (LabeledVertex)
         * vert1.get(i)).getName()); print("\n\nedges1 = "); for (int i = 0; i < edges1.size(); i++)
         * print( ( (LabeledEdge) edges1.get(i)).toString()); print("vert2 = "); for (int i = 0; i <
         * vert2.size(); i++) print( ( (LabeledVertex) vert2.get(i)).getName());
         * print("\n\nedges2 = "); for (int i = 0; i < edges2.size(); i++) print( ( (LabeledEdge)
         * edges2.get(i)).toString());
         */

        /** create PCG, with initial values on nodes */
        for (int i = 0; i < edges1.size(); i++)
        {
            LabeledEdge edge1 = (LabeledEdge) edges1.get(i);
            for (int j = 0; j < edges2.size(); j++)
            {
                try
                {
                    LabeledEdge edge2 = (LabeledEdge) edges2.get(j);
                    if (edge1.getLabel().equals(edge2.getLabel()))
                    {
                        LabeledVertex newV = new LabeledVertex(edge1.getSource().getName(), edge2
                            .getSource().getName(), 0);
                        if (PCGVerts.get(newV.getLabel()) == null)
                        {
                            newV.setWeight(sigma0(newV));
                            PCGVerts.put(newV.getLabel(), newV);
                        }
                        else
                            newV = (LabeledVertex) PCGVerts.get(newV.getLabel());
                        LabeledVertex newV2 = new LabeledVertex(edge1.getTarget().getName(), edge2
                            .getTarget().getName(), 0);
                        if (PCGVerts.get(newV2.getLabel()) == null)
                        {
                            newV2.setWeight(sigma0(newV2));
                            PCGVerts.put(newV2.getLabel(), newV2);
                        }
                        else
                            newV2 = (LabeledVertex) PCGVerts.get(newV2.getLabel());
                        LabeledEdge newE = new LabeledEdge(newV, newV2, edge1.getLabel());
                        newV.getOutgoingEdges().add(newE);
                        newV2.getIncomingEdges().add(newE);

                        LabeledEdge newE2 = new LabeledEdge(newV2, newV, edge1.getLabel());
                        newV2.getOutgoingEdges().add(newE2);
                        newV.getIncomingEdges().add(newE2);
                        PCGEdges.put(newE.getTag(), newE);
                        PCGEdges.put(newE2.getTag(), newE2);
                    }
                }
                catch (NullPointerException e)
                {
                    print("error in " + i + " " + j);
                    throw e;
                }
            }
        }

        /** calculate initial values on edges */
        for (Iterator<LabeledVertex> it = PCGVerts.values().iterator(); it.hasNext();)
        {
            LabeledVertex vert = (LabeledVertex) it.next();
            for (int j = 0; j < vert.getOutgoingEdges().size(); j++)
            {
                LabeledEdge current = (LabeledEdge) vert.getOutgoingEdges().get(j);
                float x = getNumberOfOutgoingsWithLabel(vert.getOutgoingEdges(), current.getLabel());
                current.setWeight((1) / x);
            }
        }

        float epsilon1;
        int iterations = 1;

        /** algorithm loop */
        do
        {

            epsilon1 = 0;
            float max = 0;

            /** running the fixpoint formula to calculate sigma(i+1) */
            for (Iterator<LabeledVertex> it = PCGVerts.values().iterator(); it.hasNext();)
            {
                LabeledVertex vert = (LabeledVertex) it.next();
                max = Math.max(max, fixpoint(vert));
            }

            /** normalization */
            float nextWeight;
            // print("\n\nitertion " + iterations++ +": " + ((float)epsilon1));
            for (Iterator<LabeledVertex> it = PCGVerts.values().iterator(); it.hasNext();)
            {
                LabeledVertex vert = (LabeledVertex) it.next();
                nextWeight = vert.getWeightNext() / max;
                epsilon1 += Math.pow(vert.getWeight() - nextWeight, 2);
                vert.setWeight(nextWeight);
            }
            epsilon1 = (float) Math.sqrt(epsilon1);
            // print("after norm:");
            // printResults(PCGVerts, false);
            // print("epsilon is " + ((float)epsilon1) +" continue? " +(epsilon1>epsilon)
            // +"\n\n\n");
        }
        while (epsilon1 > epsilon && iterations < SimilarityFloodingAlgorithmFixpointFormulasTypes.MAX_ITERATIONS.getValue());

        /** Create match matrix */
        MatchMatrix matrix = new MatchMatrix(candTerms.size(), targTerms.size(), candTerms,
            targTerms);
        // print("received " + candTerms.size() + ":" + targTerms.size());

        matrix.setMatchMatrix(getMatchMatrix(PCGVerts, candTerms, targTerms));
        MatchedAttributePair[] pairs;
        try
        {
            SchemaMatchingsWrapper smw = new SchemaMatchingsWrapper(matrix);
            pairs = smw.getNextBestMatching().getMatchedPairs();

        }
        catch (Exception e)
        {
            return null;
        }
        /*
         * print ("\nFiltered Pairs:"); for (int i =0 ; i<pairs.length ; i++){ print("pair " + i +
         * ": " + pairs[i].getAttribute1() + " && " + pairs[i].getAttribute2()); }
         */

        /** Create MatchInformation object */
        MatchInformation matchInformation = new MatchInformation();
        matchInformation.setMatrix(matrix);
        matchInformation.setTargetOntologyTermsTotal(targTerms.size());
        matchInformation.setTargetOntology(o2);
        matchInformation.setCandidateOntologyTermsTotal(candTerms.size());
        matchInformation.setCandidateOntology(o1);
        matchInformation.setOriginalCandidateTerms(candTerms);
        matchInformation.setOriginalTargetTerms(targTerms);
        matchInformation.setAlgorithm(this);

        Term candidateTerm, targetTerm;
        for (int i = 0; i < candTerms.size(); i++)
        {
            candidateTerm = matrix.getTermByName(((Term) candTerms.get(i)).toString(),
                matrix.getCandidateTerms());
            MatchedAttributePair map;
            for (int j = 0; j < pairs.length; j++)
            {
                map = pairs[j];
                if (OntologyUtilities.oneIdRemoval((((Term) candTerms.get(i)).toString())).equals(
                    OntologyUtilities.oneIdRemoval(map.getAttribute1())))
                {
                    targetTerm = matrix.getTermByName(map.getAttribute2(), matrix.getTargetTerms());
                    if (targetTerm != null)
                    {
                        matchInformation.addMatch(targetTerm, candidateTerm,
                            map.getMatchedPairWeight());
                    }

                }
            }
        }

        for (int i = 0; i < matrix.getTargetTerms().size(); i++)
        {
            Term term = (Term) matrix.getTargetTerms().get(i);
            if (!matchInformation.isMatched(term))
                matchInformation.addMismatchTargetOntology(new Mismatch(term));
        }
        for (int i = 0; i < matrix.getCandidateTerms().size(); i++)
        {
            Term term = (Term) matrix.getCandidateTerms().get(i);
            if (!matchInformation.isMatched(term))
                matchInformation.addMismatchCandidateOntology(new Mismatch(term));
        }
        return matchInformation;
    }

    /**
     * Creates a match matrix based on the PCG remaining at the end of the algorithm
     * 
     * @param PCGVerts HashMap
     * @param cands ArrayList
     * @param targs ArrayList
     * @return double[][] match matrix
     */
    private double[][] getMatchMatrix(HashMap<String, LabeledVertex> PCGVerts, ArrayList<?> cands,
        ArrayList<?> targs)
    {
        double[][] result = new double[targs.size()][cands.size()];
        String label;
        LabeledVertex vert;
        for (int i = 0; i < targs.size(); i++)
        {
            for (int j = 0; j < cands.size(); j++)
            {
                label = ((Term) cands.get(j)).toString() + "_" + ((Term) targs.get(i)).toString();
                vert = (LabeledVertex) PCGVerts.get(label);
                if (vert != null)
                    result[i][j] = vert.getWeight();
                else
                    result[i][j] = 0;
            }
        }
        return result;
    }

    /**
     * Get the description
     * 
     * @return the description
     */
    public String getDescription()
    {
        return "Similarity Flooding Algorithm";
    }

    private static void print(String s)
    {
        System.out.println(s);
    }

    /**
     * Get the edges of the ontology
     * 
     * @param ontology the {@link Ontology}
     * @param verts a {@link LabeledVertex} list
     * @return a list of {@link LabeledEdge}
     */
    private Vector<LabeledEdge> getEdges(Ontology ontology, Vector<LabeledVertex> verts)
    {
        Vector<LabeledEdge> result = new Vector<LabeledEdge>();
        Vector<?> relationships = ontology.getRelationships();
        for (int i = 0; i < relationships.size(); i++)
        {
            Relationship rel = (Relationship) relationships.get(i);
            result.add(new LabeledEdge(getVert(verts, rel.getSource().toString()), getVert(verts,
                rel.getTarget().toString()), rel.getName()));
        }
        return result;
    }

    /**
     * Get a vertex with a certain label
     * 
     * @param verts the list of {@link LabeledVertex}
     * @param label the label to match with
     * @return the {@link LabeledVertex}
     */
    private LabeledVertex getVert(Vector<LabeledVertex> verts, String label)
    {
        for (int i = 0; i < verts.size(); i++)
        {
            if (verts.get(i).getName().equals(label))
                return verts.get(i);
        }
        return null;
    }

    /**
     * Get all the vertices of an ontology
     * 
     * @param ontology the {@link Ontology}
     * @return a list of {@link LabeledVertex}
     */
    private Vector<LabeledVertex> getVertexes(Ontology ontology)
    {
        Vector<?> terms = ontology.getTerms(true);
        Vector<LabeledVertex> result = new Vector<LabeledVertex>();
        for (int i = 0; i < terms.size(); i++)
        {
            String termName = ((Term) terms.get(i)).toString();
            result.add(new LabeledVertex(termName));
        }
        return result;
    }

    /**
     * right now returns just 1 . supposadly can use other forms of initial values (say, based on
     * syntactical similarities)
     * 
     * @param v {@link LabeledVertex}
     * @return float initial value for vertex v
     */
    private float sigma0(LabeledVertex v)
    {
        return 1;
    }

    /**
     * Root 'flooding' algorithm. uses one of two different formulas. Either way calculates the phi
     * - sum over all incoming edges of the weight on the edge times the weight on the originator.
     * In the case of FIX_BASIC, phi is simply added to the previous weight of the vertex In the
     * case of FIX_A, phi is added to the original sigma0 of the vertex.
     * 
     * @param vert a {@link LabeledVertex}
     * @return float fixpoint value on the vertex
     */
    private float fixpoint(LabeledVertex vert)
    {
        double fixBasic = SimilarityFloodingAlgorithmFixpointFormulasTypes.FIX_BASIC.getValue();
        double fixA = SimilarityFloodingAlgorithmFixpointFormulasTypes.FIX_A.getValue();
        float phi = 0;
        for (int i = 0; i < vert.getIncomingEdges().size(); i++)
        {
            LabeledEdge incoming = (LabeledEdge) vert.getIncomingEdges().get(i);
            if (fixpointType == fixBasic || fixpointType == fixA)
                phi += (incoming.getWeight() * incoming.getSource().getWeight());
            // if adding a new fixpoint, here a new formula for phi should be added
        }
        float sigmaNext = vert.getWeight();
        if (fixpointType == fixBasic)
            sigmaNext = vert.getWeight() + phi;
        if (fixpointType == fixA)
            sigmaNext = sigma0(vert) + phi;
        vert.setWeightNext(sigmaNext);
        return sigmaNext;
        // for fixpoints B and C add new if statements here
    }

    // private void printResults(HashMap<?, ?> PCGVerts, boolean printNext) {
    // for (Iterator<?> it = PCGVerts.values().iterator(); it.hasNext(); ) {
    // LabeledVertex vert = (LabeledVertex) it.next();
    // if (printNext)
    // print(vert.getLabel() + "\t" + vert.getWeightNext());
    // else
    // print(vert.getLabel() + "\t" + vert.getWeight());
    // }
    // }

    /**
     * Get all the terms of an ontology
     * 
     * @param o the {@link Ontology}
     * @return a list of {@link Term}
     */
    private ArrayList<Term> getTerms(Ontology o)
    {
        Vector<Term> terms = o.getTerms(true);
        ArrayList<Term> result = new ArrayList<Term>();
        for (int i = 0; i < terms.size(); i++)
        {
            result.add(i, terms.get(i));
        }
        return result;
    }

    /**
     * Get the number of edges with a certain label
     * 
     * @param edges a list of edges
     * @param label the label to look for
     * @return the number of edges with the lael
     */
    private float getNumberOfOutgoingsWithLabel(Vector<?> edges, String label)
    {
        float counter = 0;
        for (int i = 0; i < edges.size(); i++)
            if (((LabeledEdge) edges.get(i)).getLabel().equals(label))
                counter++;
        return counter;
    }

}
