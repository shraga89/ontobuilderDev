package ac.technion.iem.ontobuilder.matching.match;

import ac.technion.iem.ontobuilder.core.ontology.Term;

/**
 * <p>Title: Match</p>
 */
public class Match
{
    protected Term targetTerm;
    protected Term candidateTerm;
    protected double effectiveness;

    /**
     * Constructs a Match
     * 
     * @param targetTerm the target {@link Term}
     * @param candidateTerm the candidate {@link Term}
     * @param effectiveness the effectiveness
     */
    public Match(Term targetTerm, Term candidateTerm, double effectiveness)
    {
        this.targetTerm = targetTerm;
        this.candidateTerm = candidateTerm;
        this.effectiveness = effectiveness;
    }

    /**
     * Set the target term
     * 
     * @param targetTerm the target {@link Term}
     */
    public void setTargetTerm(Term targetTerm)
    {
        this.targetTerm = targetTerm;
    }

    /**
     * Get the target term
     * 
     * @return the target {@link Term}
     */
    public Term getTargetTerm()
    {
        return targetTerm;
    }

    /**
     * Set the candidate term
     * 
     * @param candidateTerm the candidate {@link Term}
     */
    public void setCandidateTerm(Term candidateTerm)
    {
        this.candidateTerm = candidateTerm;
    }

    /**
     * Get the candidate term
     * 
     * @return the candidate {@link Term}
     */
    public Term getCandidateTerm()
    {
        return candidateTerm;
    }

    /**
     * Set the effectiveness
     * 
     * @param effectiveness the effectiveness
     */
    public void setEffectiveness(double effectiveness)
    {
        this.effectiveness = effectiveness;
    }

    /**
     * Get the effectiveness
     * 
     * @return the effectiveness
     */
    public double getEffectiveness()
    {
        return effectiveness;
    }

    public String toString()
    {
        return targetTerm.getName() + " (" + targetTerm.getAttributeValue("name") + ")" +
            " <---> " + candidateTerm.getName() + " (" + candidateTerm.getAttributeValue("name") +
            ")" + "  (" + effectiveness + ")";
    }

    public boolean equals(Object o)
    {
        if (!(o instanceof Match))
            return false;

        Match m = (Match) o;
        return targetTerm.equals(m.targetTerm) && candidateTerm.equals(m.candidateTerm);
    }
}
