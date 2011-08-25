package ac.technion.iem.ontobuilder.matching.algorithms.line1.precedence;

import java.util.ArrayList;
import java.util.Iterator;

import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.core.utils.StringUtilities;
import ac.technion.iem.ontobuilder.core.utils.properties.ApplicationParameters;
import ac.technion.iem.ontobuilder.core.utils.properties.PropertiesHandler;

/**
 * <p>Title: PrecedenceMatch</p>
 * <p>Description: Implements a Precedence Match</p>
 */
public class PrecedenceMatch
{
    protected double matchMatrix[][];
    protected double auxMatchMatrix[][];

    protected double precedeWeight;
    protected double succeedWeight;
    protected double threshold;

    protected ArrayList<Term> targetTerms;
    protected ArrayList<Term> candidateTerms;

    private ArrayList<Term> targetPrecedeTerms;
    private ArrayList<Term> candidatePrecedeTerms;

    /**
     * Constructs a PrecedenceMatch
     *
     * @param matchMatrix the match matrix
     * @param targetTerms a list of the target {@link Term}
     * @param candidateTerms a list of the candidate {@link Term}
     */
    public PrecedenceMatch(double matchMatrix[][], ArrayList<Term> targetTerms,
        ArrayList<Term> candidateTerms)
    {
        this.matchMatrix = new double[candidateTerms.size()][targetTerms.size()];
        for (int i = 0; i < matchMatrix.length; i++)
            for (int j = 0; j < matchMatrix[0].length; j++)
                this.matchMatrix[i][j] = matchMatrix[i][j];
        auxMatchMatrix = new double[candidateTerms.size()][targetTerms.size()];
        this.targetTerms = targetTerms;
        this.candidateTerms = candidateTerms;
    }

    /**
     * Set the precede weight
     * 
     * @param precedeWeight the precede weight
     */
    public void setPrecedeWeight(double precedeWeight)
    {
        this.precedeWeight = precedeWeight;
    }

    /**
     * Get the precede weight
     * 
     * @return the precede weight
     */
    public double getPrecedeWeight()
    {
        return precedeWeight;
    }

    /**
     * Set the succeed weight
     * 
     * @param succeedWeight the succeed weight
     */
    public void setSucceedWeight(double succeedWeight)
    {
        this.succeedWeight = succeedWeight;
    }

    /**
     * Get the succeed weight
     * 
     * @return the succeed weight
     */
    public double getSucceedWeight()
    {
        return succeedWeight;
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
     * Perform a match
     *
     * @return a match matrix
     */
    public double[][] match()
    {
        for (Iterator<?> i = candidateTerms.iterator(); i.hasNext();)
        {
            Term term = (Term) i.next();
            matchTerm(term);
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

            System.out.println(PropertiesHandler.getResourceString("algorithm.precedence"));
            System.out.println();
            System.out.println(StringUtilities.getTableStringRepresentation(columnNames, 0, matchTable));
        }

        return matchMatrix;
    }

    boolean print = false;

    /**
     * Matches a term
     *
     * @param candidateTerm the candidate {@link Term}
     */
    protected void matchTerm(Term candidateTerm)
    {
        if (print)
            System.out.println("***********************************");

        int candidateIndex = candidateTerms.indexOf(candidateTerm);

        for (Iterator<?> i = targetTerms.iterator(); i.hasNext();)
        {
            Term targetTerm = (Term) i.next();

            // print=((String)targetTerm.getAttributeValue("name")).equalsIgnoreCase("po header") &&
            // ((String)candidateTerm.getAttributeValue("name")).equalsIgnoreCase("order num");
            if (print)
                System.out.println("Matching '" + targetTerm + "' <--> '" + candidateTerm + "'");

            double precedeConfidence = findPrecedeMatch(targetTerm, candidateTerm);
            double succeedConfidence = findSucceedMatch(targetTerm, candidateTerm);

            double overallConfidence = (precedeConfidence * precedeWeight + succeedConfidence *
                succeedWeight) /
                (precedeWeight + succeedWeight);

            int targetIndex = targetTerms.indexOf(targetTerm);
            if (targetIndex != -1 && candidateIndex != -1)
                auxMatchMatrix[candidateIndex][targetIndex] = overallConfidence;

            if (print)
            {
                System.out.println("\tPrecede confidence: " + precedeConfidence);
                System.out.println("\tSucceed confidence: " + succeedConfidence);
                System.out.println("\tOverall confidence: " + overallConfidence);
            }
        }

        if (print)
            System.out.println("***********************************");
    }

    /**
     * Find a match of precedes
     *
     * @param targetTerm the target {@link Term}
     * @param candidateTerm the candidate {@link Term}
     * @return 0 if there are no candidates, else confidence / num_of_candidates
     */
    protected double findPrecedeMatch(Term targetTerm, Term candidateTerm)
    {
        /*
         * ArrayList targetPrecede=new ArrayList(); int targetIndex=targetTerms.indexOf(targetTerm);
         * for(int i=0;i<targetIndex;i++) targetPrecede.add(targetTerms.get(i)); ArrayList
         * candidatePrecede=new ArrayList(); int
         * candidateIndex=candidateTerms.indexOf(candidateTerm); for(int i=0;i<candidateIndex;i++)
         * candidatePrecede.add(candidateTerms.get(i));
         */

        targetPrecedeTerms = targetTerm.getAllPrecedes();
        for (Iterator<Term> i = targetPrecedeTerms.listIterator(); i.hasNext();)
        {
            if (!targetTerms.contains(i.next()))
                i.remove();
        }

        candidatePrecedeTerms = candidateTerm.getAllPrecedes();
        for (Iterator<Term> i = candidatePrecedeTerms.listIterator(); i.hasNext();)
        {
            if (!candidateTerms.contains(i.next()))
                i.remove();
        }

        if (print)
        {
            System.out.println("\tPrecede of " + targetTerm);
            for (Iterator<Term> i = targetPrecedeTerms.iterator(); i.hasNext();)
                System.out.println("\t\t" + i.next());
            System.out.println("\tPrecede of " + candidateTerm);
            for (Iterator<Term> i = candidatePrecedeTerms.iterator(); i.hasNext();)
                System.out.println("\t\t" + i.next());
        }

        return findMatch(targetPrecedeTerms, candidatePrecedeTerms);
    }

    /**
     * Find a match of succeeds
     *
     * @param targetTerm the target {@link Term}
     * @param candidateTerm the candidate {@link Term}
     * @return 0 if there are no candidates, else confidence / num_of_candidates
     */
    protected double findSucceedMatch(Term targetTerm, Term candidateTerm)
    {
        /*
         * ArrayList targetSucceed=new ArrayList(); int targetIndex=targetTerms.indexOf(targetTerm);
         * for(int i=targetIndex+1;i<targetTerms.size();i++) targetSucceed.add(targetTerms.get(i));
         * ArrayList candidateSucceed=new ArrayList(); int
         * candidateIndex=candidateTerms.indexOf(candidateTerm); for(int
         * i=candidateIndex+1;i<candidateTerms.size();i++)
         * candidateSucceed.add(candidateTerms.get(i));
         */

        ArrayList<Term> targetSucceedTerms = new ArrayList<Term>();
        for (Iterator<Term> i = targetTerms.iterator(); i.hasNext();)
        {
            Term o = i.next();
            if (!targetPrecedeTerms.contains(o) && !o.equals(targetTerm))
                targetSucceedTerms.add(o);
        }

        ArrayList<Term> candidateSucceedTerms = new ArrayList<Term>();
        for (Iterator<Term> i = candidateTerms.iterator(); i.hasNext();)
        {
            Term o = i.next();
            if (!candidatePrecedeTerms.contains(o) && !o.equals(candidateTerm))
                candidateSucceedTerms.add(o);
        }

        if (print)
        {
            System.out.println("\tSucceed of " + targetTerm);
            for (Iterator<Term> i = targetSucceedTerms.iterator(); i.hasNext();)
                System.out.println("\t\t" + i.next());
            System.out.println("\tSucceed of " + candidateTerm);
            for (Iterator<Term> i = candidateSucceedTerms.iterator(); i.hasNext();)
                System.out.println("\t\t" + i.next());
        }

        return findMatch(targetSucceedTerms, candidateSucceedTerms);
    }

    /*
     * protected double findMatch(ArrayList targets,ArrayList candidates) { if(targets.size()==0 &&
     * candidates.size()==0) return 1; double confidence=0; int matches=0; for(Iterator
     * j=candidates.iterator();j.hasNext();) { Term candidateTerm=(Term)j.next(); int
     * candidateIndex=candidateTerms.indexOf(candidateTerm); if(candidateIndex==-1) continue;
     * for(Iterator i=targets.iterator();i.hasNext();) { Term targetTerm=(Term)i.next(); int
     * targetIndex=targetTerms.indexOf(targetTerm); if(targetIndex==-1) continue;
     * if(matchMatrix[candidateIndex][targetIndex]>threshold)
     * confidence+=matchMatrix[candidateIndex][targetIndex]; matches++; } } return
     * matches==0?0:confidence/(double)matches; }
     */

    /**
     * Finds a match between the target precede terms and the candidate precede terms
     * 
     * @param targets a list of target precede {@link Term}
     * @param candidates a list of candidate precede {@link Term}
     * @return 0 if there are no candidates, else confidence / num_of_precede_candidates
     */
    protected double findMatch(ArrayList<Term> targetPrecedeTerms2,
        ArrayList<Term> candidatePrecedeTerms2)
    {
        if (targetPrecedeTerms2.size() == 0 && candidatePrecedeTerms2.size() == 0)
            return 1;

        double confidence = 0;
        int matches = 0;

        for (Iterator<Term> j = candidatePrecedeTerms2.iterator(); j.hasNext();)
        {
            Term candidateTerm = (Term) j.next();
            int candidateIndex = candidateTerms.indexOf(candidateTerm);
            if (candidateIndex == -1)
                continue;

            double localConfidence = -1;
            for (Iterator<Term> i = targetPrecedeTerms2.iterator(); i.hasNext();)
            {
                Term targetTerm = (Term) i.next();
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
        return candidatePrecedeTerms2.size() == 0 ? 0 : confidence / candidatePrecedeTerms2.size();
    }
}