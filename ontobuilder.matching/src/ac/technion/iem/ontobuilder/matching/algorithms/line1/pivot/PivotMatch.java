package ac.technion.iem.ontobuilder.matching.algorithms.line1.pivot;

import java.util.ArrayList;
import java.util.Iterator;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.wrapper.SchemaMatchingsWrapper;
import ac.technion.iem.ontobuilder.matching.meta.match.MatchMatrix;
import ac.technion.iem.ontobuilder.matching.utils.SchemaMatchingsUtilities;
import ac.technion.iem.ontobuilder.matching.utils.SchemaTranslator;

/**
 * <p>Title: PivotMatch</p>
 * <p>Description: Implements the pivot match</p>
 */
public class PivotMatch
{

    protected double auxMatchMatrix[][];
    protected double threshold;
    protected ArrayList<Term> targetTerms;
    protected ArrayList<Term> candidateTerms;
    protected MatchMatrix matrix;
    protected PivotOperator po;
    protected Ontology candidateOntology;
    protected Ontology targetOntology;
    protected double[] weights;
    protected double[] localScores;
    protected boolean useOneToOneMatch = false;
    protected SchemaMatchingsWrapper smw = null;

    /**
     * Constructs a PivotMatch
     *
     * @param candidateOntology the candidate {@link Ontology}
     * @param targetOntology the target {@link Ontology}
     * @param matrix the {@link MatchMatrix}
     * @param po the {@link PivotOperator}
     */
    public PivotMatch(Ontology candidateOntology, Ontology targetOntology, MatchMatrix matrix,
        PivotOperator po)
    {
        this.candidateOntology = candidateOntology;
        this.targetOntology = targetOntology;
        this.matrix = matrix;
        this.po = po;
        auxMatchMatrix = new double[matrix.getColCount()][matrix.getRowCount()];
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
     * Set to use one-to-one match
     * 
     * @param useOneToOneMatch <code>true</code> if is is one-to-one match
     */
    public void setUseOneToOneMatch(boolean useOneToOneMatch)
    {
        this.useOneToOneMatch = useOneToOneMatch;
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
     * Set the weights
     * 
     * @param weights the weights
     */
    public void setWeights(double[] weights)
    {
        this.weights = weights;
        this.localScores = new double[weights.length];
    }

    /**
     * Get the weights
     * 
     * @return the weights
     */
    public double[] getWeights()
    {
        return weights;
    }

    /**
     * Performs a match between the target and candidate ontologies
     * 
     * @return the match matrix
     */
    public double[][] match()
    {
        candidateTerms = matrix.getCandidateTerms();
        for (Iterator<Term> i = candidateTerms.iterator(); i.hasNext();)
        {
            Term term = i.next();
            matchTerm(term);
        }
        return auxMatchMatrix;
    }

    /**
     * Match a term
     *
     * @param candidateTerm the candidate {@link Term}
     */
    protected void matchTerm(Term candidateTerm)
    {
        targetTerms = matrix.getTargetTerms();
        for (Iterator<Term> i = targetTerms.iterator(); i.hasNext();)
        {
            Term targetTerm = (Term) i.next();
            ArrayList<ArrayList<Term>> candidateTermsSets = po.performPivot(
                matrix.getCandidateTerms(), candidateTerm);
            ArrayList<ArrayList<Term>> targetTermsSets = po.performPivot(targetTerms, targetTerm);
            int numOfSets = candidateTermsSets.size();
            for (int k = 0; k < numOfSets; k++)
            {
                localScores[k] = findTermSetsMatch(candidateTermsSets.get(k),
                    targetTermsSets.get(k));
            }
            double overallConfidence = 0;
            for (int k = 0; k < numOfSets; k++)
            {
                overallConfidence += localScores[k] * weights[k];
            }
            auxMatchMatrix[matrix.getTermIndex(matrix.getCandidateTerms(), candidateTerm, true)][matrix
                .getTermIndex(matrix.getTargetTerms(), targetTerm, false)] = overallConfidence;
        }
    }

    /**
     * Find the term sets that match
     *
     * @param candidates a list of candidate terms
     * @param targets a list of target terms
     * @return 0 if there are no candidates, else returns the confidence/num_of_candidates
     */
    public double findTermSetsMatch(ArrayList<Term> candidates, ArrayList<Term> targets)
    {
        if (targets.size() == 0 && candidates.size() == 0)
            return 1;

        double confidence = 0;
        int matches = 0;

        if (useOneToOneMatch)
        {
            try
            {
                // perform one to one match...
                if (smw == null)
                    smw = new SchemaMatchingsWrapper(matrix.createSubMatrix(candidates, targets));
                else
                    smw.reset(matrix.createSubMatrix(candidates, targets));
                SchemaTranslator st = smw.getBestMatching();
                st = SchemaMatchingsUtilities.getSTwithThresholdSensitivity(st, threshold);
                return candidates.size() == 0 ? 0 : st.getTotalMatchWeight() / candidates.size();
            }
            catch (Exception e)
            {
                // e.printStackTrace();
                return 0;
            }
        }
        else
        {// one to many match
            for (Iterator<?> j = candidates.iterator(); j.hasNext();)
            {
                Term candidateTerm = (Term) j.next();
                double localConfidence = -1;
                for (Iterator<?> i = targets.iterator(); i.hasNext();)
                {
                    Term targetTerm = (Term) i.next();
                    localConfidence = Math.max(
                        matrix.getMatchConfidence(candidateTerm, targetTerm), localConfidence);
                }
                if (localConfidence >= threshold)
                {
                    matches++;
                    confidence += localConfidence;
                }
            }
        }
        return candidates.size() == 0 ? 0 : confidence / candidates.size();
    }

}
