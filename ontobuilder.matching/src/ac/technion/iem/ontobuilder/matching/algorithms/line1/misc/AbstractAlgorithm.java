package ac.technion.iem.ontobuilder.matching.algorithms.line1.misc;

import java.util.ArrayList;

import org.jdom.Element;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.core.ontology.domain.GuessedDomain;
import ac.technion.iem.ontobuilder.core.thesaurus.Thesaurus;
import ac.technion.iem.ontobuilder.core.utils.StringUtilities;
import ac.technion.iem.ontobuilder.matching.algorithms.common.MatchAlgorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.term.TermPreprocessor;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.match.Mismatch;


/**
 * <p>
 * Title: AbstractAlgorithm
 * </p>
 * Implements {@link Algorithm} and {@link MatchAlgorithm}
 */
abstract public class AbstractAlgorithm implements Algorithm, MatchAlgorithm
{
    public static final int NO_MODE = 0;

    protected String pluginName;
    protected int mode = NO_MODE;
    protected MatchInformation matchInformation;
    protected Thesaurus thesaurus;
    protected TermPreprocessor termPreprocessor;
    protected double threshold;
    protected double effectiveness = -1;

    protected ArrayList<Term> originalTargetTerms = new ArrayList<Term>();
    protected ArrayList<Term> originalCandidateTerms = new ArrayList<Term>();
    protected ArrayList<Term> targetTerms = new ArrayList<Term>();
    protected ArrayList<Term> candidateTerms = new ArrayList<Term>();

    /**
     * Constructs a default AbstractAlgorithm
     */
    public AbstractAlgorithm()
    {
    }

    abstract public String getName();

    /**
     * Set the plugin name
     * 
     * @param pluginName the plugin name
     */
    public void setPluginName(String pluginName)
    {
        this.pluginName = pluginName;
    }

    /**
     * Get the plugin name
     * 
     * @return the plugin name
     */
    public String getPluginName()
    {
        return pluginName;
    }

    /**
     * Get the description
     * 
     * @return the description
     */
    abstract public String getDescription();

    /**
     * Match two ontologies
     * 
     * @param targetOntology the target {@link Ontology}
     * @param candidateOntology the candidate {@link Ontology}
     */
    abstract public MatchInformation match(Ontology targetOntology, Ontology candidateOntology);

    /**
     * Set the mode
     * 
     * @param mode the mode
     */
    public void setMode(int mode)
    {
        this.mode = mode;
    }

    /**
     * Get the mode
     * 
     * @return the mode
     */
    public int getMode()
    {
        return mode;
    }

    /**
     * Set the threshold
     * 
     * @param threshold the threshold
     */
    public void setThreshold(double threshold)
    {
        this.threshold = threshold - StringUtilities.THESAURUS_PENALTY -
            StringUtilities.SOUNDEX_PENALTY - GuessedDomain.DOMAIN_PENALTY;
    }

    /**
     * Get the threshold
     * 
     * @return the threshold
     */
    public double getThreshold()
    {
        if (threshold < 0)
            return 0;
        return threshold + StringUtilities.THESAURUS_PENALTY + StringUtilities.SOUNDEX_PENALTY +
            GuessedDomain.DOMAIN_PENALTY;
    }

    /**
     * Get the MatchInformation
     * 
     * @return the {@link MatchInformation}
     */
    public MatchInformation getMatchInformation()
    {
        return matchInformation;
    }

    public String toString()
    {
        return getName();
    }

    abstract public void configure(Element element);

    /**
     * Gets whether to use the Thesaurus
     * 
     * @return <code>true</code> if uses the {@link Thesaurus_}
     */
    public boolean usesThesaurus()
    {
        return false;
    }

    /**
     * Sets whether to use the Thesaurus
     * 
     * @param <code>true</code> if to use the {@link Thesaurus_}
     */
    public void setThesaurus(Thesaurus thesaurus)
    {
        this.thesaurus = thesaurus;
    }

    /**
     * Gets the Thesaurus
     * 
     * @return the {@link Thesaurus_}
     */
    public Thesaurus getThesaurus()
    {
        return thesaurus;
    }

    public void setTermPreprocessor(TermPreprocessor termPreprocessor)
    {
        this.termPreprocessor = termPreprocessor;
    }

    public TermPreprocessor getTermPreprocessor()
    {
        return termPreprocessor;
    }

    /**
     * Gets whether implements effectiveness
     * 
     * @return <code>false</code>
     */
    public boolean implementsEffectiveness()
    {
        return false;
    }

    /**
     * Gets the effectiveness
     * 
     * @return <code>true</code> if is effectiveness
     */
    public double getEffectiveness()
    {
        return effectiveness;
    }

    /**
     * Builds match information
     *
     * @param matchMatrix the match matrix
     * @return {@link MatchInformation}
     */
    protected MatchInformation buildMatchInformation(double matchMatrix[][])
    {
        MatchInformation matchInformation = new MatchInformation();

        for (int j = 0; j < candidateTerms.size(); j++)
        {
            Term originalCandidateTerm = (Term) originalCandidateTerms.get(j);
            Term maxTargetTerm = null;
            double maxEffectiveness = -1;
            for (int i = 0; i < targetTerms.size(); i++)
            {
                Term originalTargetTerm = (Term) originalTargetTerms.get(i);
                if (matchMatrix[j][i] >= threshold && matchMatrix[j][i] >= maxEffectiveness)
                {
                    maxEffectiveness = matchMatrix[j][i];
                    maxTargetTerm = originalTargetTerm;
                }
            }
            if (maxTargetTerm != null)
                matchInformation.addMatch(maxTargetTerm, originalCandidateTerm, maxEffectiveness);
        }
        for (int i = 0; i < originalTargetTerms.size(); i++)
        {
            Term term = (Term) originalTargetTerms.get(i);
            if (!matchInformation.isMatched(term))
                matchInformation.addMismatchTargetOntology(new Mismatch(term));
        }
        for (int i = 0; i < originalCandidateTerms.size(); i++)
        {
            Term term = (Term) originalCandidateTerms.get(i);
            if (!matchInformation.isMatched(term))
                matchInformation.addMismatchCandidateOntology(new Mismatch(term));
        }

        return matchInformation;
    }

//    // added by haggai 13/12/03 - for meta matching use
//    /**
//     * Matched two schemas
//     * 
//     * @param s1 the first {@link Schema}
//     * @param s2 the second {@link Schema}
//     * @return {@link AbstractMatchMatrix}
//     */
//    public AbstractMatchMatrix match(Ontology s1, Ontology s2)
//    {
//        MatchInformation matchInfo = match(s1, s2);
//        return matchInfo.getMatrix();
//    }

    abstract public Algorithm makeCopy();
}