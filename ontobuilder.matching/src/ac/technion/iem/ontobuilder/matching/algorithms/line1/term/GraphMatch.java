package ac.technion.iem.ontobuilder.matching.algorithms.line1.term;

import java.util.ArrayList;
import java.util.Iterator;

import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.core.utils.StringUtilities;
import ac.technion.iem.ontobuilder.core.utils.graphs.Graph;
import ac.technion.iem.ontobuilder.core.utils.graphs.GraphCell;
import ac.technion.iem.ontobuilder.core.utils.graphs.GraphUtilities;
import ac.technion.iem.ontobuilder.core.utils.properties.ApplicationParameters;
import ac.technion.iem.ontobuilder.core.utils.properties.PropertiesHandler;

/**
 * <p>Title: GraphMatch</p>
 * <p>Description: Implements a Graph Match</p>
 */
public class GraphMatch
{
    protected double matchMatrix[][];
    protected double auxMatchMatrix[][];

    protected double siblingsWeight;
    protected double parentsWeight;
    protected double threshold;

    protected Graph targetGraph;
    protected Graph candidateGraph;
    protected ArrayList<Term> targetTerms;
    protected ArrayList<Term> candidateTerms;

    /**
     * Constructs a GraphMatch
     * 
     * @param matchMatrix the match matrix
     * @param targetTerms a list of the target {@link Term}
     * @param candidateTerms a list of the candidate {@link Term}
     * @param targetGraph the target {@link JGraph}
     * @param candidateGraph the candidate {@link JGraph}
     */
    public GraphMatch(double matchMatrix[][], ArrayList<Term> targetTerms,
        ArrayList<Term> candidateTerms, Graph targetGraph, Graph candidateGraph)
    {
        this.matchMatrix = new double[candidateTerms.size()][targetTerms.size()];
        for (int i = 0; i < matchMatrix.length; i++)
            for (int j = 0; j < matchMatrix[0].length; j++)
                this.matchMatrix[i][j] = matchMatrix[i][j];
        auxMatchMatrix = new double[candidateTerms.size()][targetTerms.size()];
        this.targetTerms = targetTerms;
        this.candidateTerms = candidateTerms;
        this.targetGraph = targetGraph;
        this.candidateGraph = candidateGraph;
    }

    /**
     * Set the threshold
     * 
     * @param threshold the threshold
     */
    public void setThreshold(double threshold)
    {
        this.threshold = threshold;
    }

    /**
     * Get the threshold
     * 
     * @return the threshold
     */
    public double getThreshold()
    {
        return threshold;
    }

    /**
     * Set the siblings weight
     * 
     * @param siblingsWeight the siblings weight
     */
    public void setSiblingsWeight(double siblingsWeight)
    {
        this.siblingsWeight = siblingsWeight;
    }

    /**
     * Get the siblings weight
     * 
     * @return the siblings weight
     */
    public double getSiblingsWeight()
    {
        return siblingsWeight;
    }

    /**
     * Set the parent weight
     * 
     * @param parentsWeight the parent weight
     */
    public void setParentsWeight(double parentsWeight)
    {
        this.parentsWeight = parentsWeight;
    }

    /**
     * Get the parent weight
     * 
     * @return the parent weight
     */
    public double getParentsWeight()
    {
        return parentsWeight;
    }

    /**
     * Performs a match
     * 
     * @return a match matrix
     */
    public double[][] match()
    {
        for (Iterator<?> i = candidateTerms.iterator(); i.hasNext();)
        {
            Term term = (Term) i.next();
            GraphCell cell = GraphUtilities.getCellWithObject(candidateGraph, term);
            if (cell == null)
                continue;
            matchTerm(cell);
        }

        for (int i = 0; i < matchMatrix.length; i++)
            for (int j = 0; j < matchMatrix[0].length; j++)
                matchMatrix[i][j] = auxMatchMatrix[i][j];

        if (ApplicationParameters.verbose)
        {
            String columnNames[] =
            {
                PropertiesHandler.getResourceString("ontology.match.candidate"),
                PropertiesHandler.getResourceString("ontology.match.target"),
                PropertiesHandler.getResourceString("ontology.match.effectiveness")
            };
            Object matchTable[][] = new Object[targetTerms.size() * candidateTerms.size()][3];

            for (int j = 0; j < candidateTerms.size(); j++)
            {
                Term candidateTerm = (Term) candidateTerms.get(j);
                for (int i = 0; i < targetTerms.size(); i++)
                {
                    Term targetTerm = (Term) targetTerms.get(i);
                    int index = j * targetTerms.size() + i;
                    matchTable[index][0] = candidateTerm;
                    matchTable[index][1] = targetTerm;
                    matchTable[index][2] = new Double(matchMatrix[j][i]);
                }
            }

            System.out.println(PropertiesHandler.getResourceString("algorithm.graph"));
            System.out.println();
            System.out.println(StringUtilities.getTableStringRepresentation(columnNames, 0, matchTable));
        }

        return matchMatrix;
    }

    boolean print = false;

    /**
     * Matches a term
     *
     * @param candidateCell the candidate term
     */
    protected void matchTerm(GraphCell candidateCell)
    {
        if (print)
            System.out.println("***********************************");

        Term candidateTerm = (Term) candidateCell.getUserObject();
        int candidateIndex = candidateTerms.indexOf(candidateTerm);

        for (Iterator<?> i = targetTerms.iterator(); i.hasNext();)
        {
            Term targetTerm = (Term) i.next();
            GraphCell targetCell = GraphUtilities.getCellWithObject(targetGraph, targetTerm);
            if (targetCell == null)
                continue;

            if (print)
                System.out.println("Matching '" + targetTerm + "' <--> '" + candidateTerm + "'");

            double parentsConfidence = findParentsMatch(targetCell, candidateCell);
            double siblingsConfidence = findSiblingsMatch(targetCell, candidateCell);

            double overallConfidence = (parentsConfidence * parentsWeight + siblingsConfidence *
                siblingsWeight) /
                (parentsWeight + siblingsWeight);

            int targetIndex = targetTerms.indexOf(targetTerm);
            if (targetIndex != -1 && candidateIndex != -1)
                auxMatchMatrix[candidateIndex][targetIndex] = overallConfidence;

            if (print)
            {
                System.out.println("\tParents confidence: " + parentsConfidence);
                System.out.println("\tSiblings confidence: " + siblingsConfidence);
                System.out.println("\tOverall confidence: " + overallConfidence);
            }
        }

        if (print)
            System.out.println("***********************************");
    }

    /**
     * Find a match of the parents
     *
     * @param targetCell the target terms
     * @param candidateCell the candidate terms
     * @return 0 if there are no candidates, else confidence / num_of_candidates
     */
    protected double findParentsMatch(GraphCell targetCell, GraphCell candidateCell)
    {
        ArrayList<?> targetParents = GraphUtilities.getAllParents(targetCell);
        ArrayList<?> candidateParents = GraphUtilities.getAllParents(candidateCell);

        if (print)
        {
            System.out.println("\tParents of " + targetCell);
            for (Iterator<?> i = targetParents.iterator(); i.hasNext();)
                System.out.println("\t\t" + i.next());
            System.out.println("\tParents of " + candidateCell);
            for (Iterator<?> i = candidateParents.iterator(); i.hasNext();)
                System.out.println("\t\t" + i.next());
        }

        return findMatch(targetParents, candidateParents);
    }

    /**
     * Find a match of the siblings
     *
     * @param targetCell the target terms
     * @param candidateCell the candidate terms
     * @return 0 if there are no candidates, else confidence / num_of_candidates
     */
    protected double findSiblingsMatch(GraphCell targetCell, GraphCell candidateCell)
    {
        ArrayList<?> targetSiblings = GraphUtilities.getSiblings(targetCell);
        ArrayList<?> candidateSiblings = GraphUtilities.getSiblings(candidateCell);

        if (print)
        {
            System.out.println("\tSiblings of " + targetCell);
            for (Iterator<?> i = targetSiblings.iterator(); i.hasNext();)
                System.out.println("\t\t" + i.next());
            System.out.println("\tSiblings of " + candidateCell);
            for (Iterator<?> i = candidateSiblings.iterator(); i.hasNext();)
                System.out.println("\t\t" + i.next());
        }

        return findMatch(targetSiblings, candidateSiblings);
    }

    /*
     * protected double findMatch(ArrayList targets,ArrayList candidates) { if(targets.size()==0 &&
     * candidates.size()==0) return 1; double confidence=0; int matches=0; for(Iterator
     * j=candidates.iterator();j.hasNext();) { DefaultGraphCell
     * candidate=(DefaultGraphCell)j.next(); if(!(candidate.getUserObject() instanceof Term))
     * continue; Term candidateTerm=(Term)candidate.getUserObject(); int
     * candidateIndex=candidateTerms.indexOf(candidateTerm); if(candidateIndex==-1) continue;
     * for(Iterator i=targets.iterator();i.hasNext();) { DefaultGraphCell
     * target=(DefaultGraphCell)i.next(); if(!(target.getUserObject() instanceof Term)) continue;
     * Term targetTerm=(Term)target.getUserObject(); int
     * targetIndex=targetTerms.indexOf(targetTerm); if(targetIndex==-1) continue;
     * if(matchMatrix[candidateIndex][targetIndex]>=threshold)
     * confidence+=matchMatrix[candidateIndex][targetIndex]; matches++; } } return
     * matches==0?0:confidence/(double)matches; }
     */

    /**
     * Finds a match between the target terms and the candidate terms
     * 
     * @param targets a list of target terms
     * @param candidates a list of candidate terms
     * @return 0 if there are no candidates, else confidence / num_of_candidates
     */
    protected double findMatch(ArrayList<?> targets, ArrayList<?> candidates)
    {
        if (targets.size() == 0 && candidates.size() == 0)
            return 1;

        double confidence = 0;
        int matches = 0;

        for (Iterator<?> j = candidates.iterator(); j.hasNext();)
        {
            GraphCell candidate = (GraphCell) j.next();
            if (!(candidate.getUserObject() instanceof Term))
                continue;
            Term candidateTerm = (Term) candidate.getUserObject();
            int candidateIndex = candidateTerms.indexOf(candidateTerm);
            if (candidateIndex == -1)
                continue;

            double localConfidence = -1;
            for (Iterator<?> i = targets.iterator(); i.hasNext();)
            {
                GraphCell target = (GraphCell) i.next();
                if (!(target.getUserObject() instanceof Term))
                    continue;
                Term targetTerm = (Term) target.getUserObject();
                int targetIndex = targetTerms.indexOf(targetTerm);
                if (targetIndex == -1)
                    continue;
                localConfidence = Math.max(matchMatrix[candidateIndex][targetIndex],
                    localConfidence);
            }
            if (localConfidence >= threshold)
            {
                matches++;
                confidence += localConfidence;
            }
        }

        // return matches==0?0:confidence/(double)matches;
        return candidates.size() == 0 ? 0 : confidence / candidates.size();
    }
}