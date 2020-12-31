package ac.technion.iem.ontobuilder.matching.algorithms.line2.stablemarriage;

import java.util.*;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.common.MatchingAlgorithmsNamesEnum;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.common.SecondLineAlgorithm;
import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.meta.match.MatchMatrix;
import ac.technion.iem.ontobuilder.matching.wrapper.OntoBuilderWrapper;

/**
 * <p>
 * Title: StableMarriageWrapper
 * </p>
 */
public class StableMarriageWrapper implements SecondLineAlgorithm
{
    private final OntoBuilderWrapper m_OntoBuilderWrapper;

    private Ontology m_CandidateOntology;
    private Ontology m_TargetOntology;

    private MatchInformation m_MatchInformation;
    private MatchMatrix m_MatchMatrix;
    private Hashtable<Man, TreeMap<?, ?>> m_MenSet;
    private Hashtable<Woman, TreeMap<?, ?>> m_WomenSet;

    private StableMarriage m_StableMarriage;
    private static double EPSILON = 0.0000000000001;
    private MatchingAlgorithmsNamesEnum m_sAlgorithmName;

    /**
     * Constructs a default StableMarriageWrapper
     */
    public StableMarriageWrapper()
    {

        m_OntoBuilderWrapper = new OntoBuilderWrapper();
        m_CandidateOntology = null;
        m_TargetOntology = null;
        m_MatchInformation = null;
        m_MatchMatrix = null;
        m_MenSet = new Hashtable<Man, TreeMap<?, ?>>();
        m_WomenSet = new Hashtable<Woman, TreeMap<?, ?>>();
        m_StableMarriage = new StableMarriage();
        m_sAlgorithmName = MatchingAlgorithmsNamesEnum.TERM;

    }



    /**
     * Runs the algorithm
     * 
     * @param obTargetOntology the target {@link Ontology}
     * @param obCandidateOntology the candidate {@link Ontology}
     * @return a {@link SchemaTranslator}
     */
    public MatchInformation runAlgorithm(Ontology obTargetOntology, Ontology obCandidateOntology)
    {
        m_TargetOntology = obTargetOntology;
        m_CandidateOntology = obCandidateOntology;
        if ((m_TargetOntology == null) || (m_CandidateOntology == null))
        {
            return null;
        }
        matchOntologies();
        return runAlgorithm(m_MatchMatrix, m_CandidateOntology,m_TargetOntology);
    }

    /**
     * Runs the algorithm
     * 
     * @param matchMatrix a {@link MatchMatrix}
     * @param candidate Candidate ontology
     * @param target Target ontology
     * @return a {@link MatchInformation}
     */
    public MatchInformation runAlgorithm(MatchMatrix matchMatrix, Ontology candidate, Ontology target)
    {
    	if (candidate == null || target == null) return null;
    	MatchInformation res = new MatchInformation(candidate, target);
        m_MatchMatrix = matchMatrix;
        employ();
        if (m_alMatchingResult == null)
        {
            return null;
        }
        int iArraySize = m_alMatchingResult.size();

        ArrayList<Match> matches = new ArrayList<Match>();
        for (int i = 0; i < iArraySize; ++i)
        {
            Man man = (Man) m_alMatchingResult.get(i);
            if ((man == null) || (man.getPartner() == null))
            {
                continue;
            }
            else
            {
            	Term c = candidate.getTermByID(man.getPartner().getM_id());
            	Term t = target.getTermByID(man.getM_id());
            	Match m = new Match(t,c , m_MatchMatrix.getMatchConfidence(c, t));
                matches.add(m);
            }
        }
		res.setMatches(matches);
        return res;
    }

    /**
     * Match the ontologies
     * 
     * @return <code>false</code> if one of the ontologies is null or an error occured, else
     * <code>true</code>
     */
    private boolean matchOntologies()
    {
        if ((m_CandidateOntology == null) || (m_TargetOntology == null))
        {
            return false;
        }
        try
        {
            m_MatchInformation = m_OntoBuilderWrapper.loadMatchAlgorithm(m_sAlgorithmName).match(
                m_TargetOntology, m_CandidateOntology);
            m_MatchMatrix = m_MatchInformation.getMatrix();
        }
        catch (Exception e)
        {
            System.out.println(e.toString());
            return false;
        }
        return true;
    }

    /**
     * Get the players in the stable marriage
     *
     * @return <code>true</code> on success
     */
    private boolean readStableMarriagePlayers()
    {
        if (m_MatchMatrix == null)
        {
            return false;
        }
        ArrayList<Term> womenTerms = m_MatchMatrix.getCandidateTerms();
        ArrayList<Term> menTerms = m_MatchMatrix.getTargetTerms();
        int iMenSize = menTerms.size();
        int iWomenSize = womenTerms.size();

        m_StableMarriage.setSize(iMenSize, iWomenSize);
        for (Term m : menTerms)
        {
            m_MenSet.put(new Man(iWomenSize, m.getName(), m.getId()), new TreeMap<Object, Object>());
        }
        for (Term w : womenTerms)
        {
            m_WomenSet.put(new Woman(iMenSize, w.getName(), w.getId()), new TreeMap<Object, Object>());
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    /**
     * Set the preferences of the players
     * @return <code>true</code> on success
     */
    private boolean setPreferences()
    {

        for (Enumeration<Man> eMan = m_MenSet.keys(); eMan.hasMoreElements();)
        {
            Man man = (Man) (eMan.nextElement());
            for (Enumeration<Woman> eWoman = m_WomenSet.keys(); eWoman.hasMoreElements();)
            {
                Woman woman = (Woman) (eWoman.nextElement());

                TreeMap<Double, Woman> manTree = (TreeMap<Double, Woman>) m_MenSet.get(man);
                TreeMap<Double, Man> womanTree = (TreeMap<Double, Man>) m_WomenSet.get(woman);
                double d1 = m_MatchMatrix.getMatchConfidenceByID(woman.getM_id(), man.getM_id());
                Double manWomanCon = new Double(d1);
                Double womanManCon = new Double(d1);
                while (manTree.containsKey(manWomanCon))
                {
                    manWomanCon = new Double(manWomanCon.doubleValue() + EPSILON);
                }
                manTree.put(manWomanCon, woman);
                while (womanTree.containsKey(womanManCon))
                {
                    womanManCon = new Double(womanManCon.doubleValue() + EPSILON);
                }
                womanTree.put(womanManCon, man);
            }
        }

        for (Enumeration<Man> eMan = m_MenSet.keys(); eMan.hasMoreElements();)
        {
            Man man = (Man) eMan.nextElement();
            TreeMap<?, ?> manTree = (TreeMap<?, ?>) m_MenSet.get(man);
            int rank = 0;
            while ((!(manTree.isEmpty()))/* && (rank < m_iThreshold) */)
            {
                Woman woman = (Woman) manTree.remove((Double) (manTree.lastKey()));
                // size = manTree.size();
                man.addRankedPartner(woman, rank++);
            }
            m_StableMarriage.addMan(man);
        }

        for (Enumeration<Woman> eWoman = m_WomenSet.keys(); eWoman.hasMoreElements();)
        {
            Woman woman = (Woman) eWoman.nextElement();
            TreeMap<?, ?> womanTree = (TreeMap<?, ?>) m_WomenSet.get(woman);
            int rank = 0;
            while ((!(womanTree.isEmpty()))/* && (rank < m_iThreshold) */)
            {
                Man man = (Man) womanTree.remove((Double) womanTree.lastKey());
                // womanTree.remove( (Double) womanTree.lastKey());
                woman.addRankedPartner(man, rank++);
            }
            m_StableMarriage.addWoman(woman);
        }
        return true;
    }

    private void run()
    {

        HashSet<?> stableMarriageMatch = m_StableMarriage.getStableMarriage();
        m_alMatchingResult = new ArrayList<Man>();
        for (Iterator<?> iter = stableMarriageMatch.iterator(); iter.hasNext();)
        {
            Man man = (Man) (iter.next());
            m_alMatchingResult.add(man);
        }
    }

    public void employ()
    {
        readStableMarriagePlayers();
        setPreferences();
        run();
    }

    ArrayList<Man> m_alMatchingResult;
    // private static int m_iThreshold = 5;
    
	@Override
	public boolean init(Properties prop) {
		return true;
	}

	@Override
	public MatchInformation match(MatchInformation mi) {
		return this.runAlgorithm(mi.getMatrix(), mi.getCandidateOntology(), mi.getTargetOntology());
	}

	@Override
	public String getName() {
		return "StableMarriage";
	}

	@Override
	public String getDescription() {
		return "Performs Stable Marriage Matching as described in 'On the Stable Marriage of Maximum Weight Royal Couples'";
	}    
}
