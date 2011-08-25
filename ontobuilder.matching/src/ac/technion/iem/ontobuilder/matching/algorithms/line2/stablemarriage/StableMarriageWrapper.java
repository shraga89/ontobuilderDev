package ac.technion.iem.ontobuilder.matching.algorithms.line2.stablemarriage;

import java.util.*;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.misc.MatchingAlgorithmsNamesEnum;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.meta.match.MatchMatrix;
import ac.technion.iem.ontobuilder.matching.meta.match.MatchedAttributePair;
import ac.technion.iem.ontobuilder.matching.utils.SchemaTranslator;
import ac.technion.iem.ontobuilder.matching.wrapper.OntoBuilderWrapper;

/**
 * <p>
 * Title: StableMarriageWrapper
 * </p>
 */
public class StableMarriageWrapper
{
    private OntoBuilderWrapper m_OntoBuilderWrapper;

    private Ontology m_CandidateOntology;
    private Ontology m_TargetOntology;

    private MatchInformation m_MatchInformation;
    private MatchMatrix m_MatchMatrix;
    private Hashtable<Man, TreeMap<?, ?>> m_MenSet;
    private Hashtable<Woman, TreeMap<?, ?>> m_WomenSet;

    private StableMarriage m_StableMarriage;
    private static double EPSILON = 0.0000000000001;
    private String m_sAlgorithmName;

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
        m_sAlgorithmName = MatchingAlgorithmsNamesEnum.TERM.getName();

    }

    /**
     * Set the algorithm name
     * 
     * @param sAlgorithmName the name to set
     */
    public void setAlgorithmName(String sAlgorithmName)
    {
        m_sAlgorithmName = sAlgorithmName;
    }

    /**
     * Runs the algorithm
     * 
     * @param obTargetOntology the target {@link Ontology}
     * @param obCandidateOntology the candidate {@link Ontology}
     * @return a {@link SchemaTranslator}
     */
    public SchemaTranslator runAlgorithm(Ontology obTargetOntology, Ontology obCandidateOntology)
    {
        m_TargetOntology = obTargetOntology;
        m_CandidateOntology = obCandidateOntology;
        if ((m_TargetOntology == null) || (m_CandidateOntology == null))
        {
            return null;
        }
        employ();
        if (m_alMatchingResult == null)
        {
            return null;
        }
        SchemaTranslator obSchemaTranslator = new SchemaTranslator();
        int iArraySize = m_alMatchingResult.size();

        ArrayList<MatchedAttributePair> m_alFilteredMatchingResult = new ArrayList<MatchedAttributePair>();
        for (int i = 0; i < iArraySize; ++i)
        {
            Man man = (Man) m_alMatchingResult.get(i);
            if ((man == null) || (man.getPartner() == null))
            {
                continue;
            }
            else
            {
                MatchedAttributePair map = new MatchedAttributePair(man.getPartner().getName(),
                    man.getName(), 1.0);
                m_alFilteredMatchingResult.add(map);
            }
        }
        MatchedAttributePair[] obArrayMatchedPair = new MatchedAttributePair[m_alFilteredMatchingResult
            .size()];
        obArrayMatchedPair = (MatchedAttributePair[]) m_alFilteredMatchingResult
            .toArray(obArrayMatchedPair);

        obSchemaTranslator.setSchemaPairs(obArrayMatchedPair);
        return obSchemaTranslator;
    }

    /**
     * Runs the algorithm
     * 
     * @param matchMatrix a {@link MatchMatrix}
     * @return a {@link SchemaTranslator}
     */
    public SchemaTranslator runAlgorithm(MatchMatrix matchMatrix)
    {

        m_MatchMatrix = matchMatrix;
        readStableMarriagePlayers();
        setPreferences();
        run();
        if (m_alMatchingResult == null)
        {
            return null;
        }
        SchemaTranslator obSchemaTranslator = new SchemaTranslator();
        int iArraySize = m_alMatchingResult.size();

        ArrayList<MatchedAttributePair> m_alFilteredMatchingResult = new ArrayList<MatchedAttributePair>();
        for (int i = 0; i < iArraySize; ++i)
        {
            Man man = (Man) m_alMatchingResult.get(i);
            if ((man == null) || (man.getPartner() == null))
            {
                continue;
            }
            else
            {
                MatchedAttributePair map = new MatchedAttributePair(man.getPartner().getName(),
                    man.getName(), 1.0);
                m_alFilteredMatchingResult.add(map);
            }
        }
        MatchedAttributePair[] obArrayMatchedPair = new MatchedAttributePair[m_alFilteredMatchingResult
            .size()];
        obArrayMatchedPair = (MatchedAttributePair[]) m_alFilteredMatchingResult
            .toArray(obArrayMatchedPair);

        obSchemaTranslator.setSchemaPairs(obArrayMatchedPair);
        return obSchemaTranslator;
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
        String[] womenTerms = m_MatchMatrix.getCandidateTermNames();
        String[] menTerms = m_MatchMatrix.getTargetTermNames();
        int iMenSize = m_MatchMatrix.getTargetTerms().size();
        int iWomenSize = m_MatchMatrix.getCandidateTerms().size();

        m_StableMarriage.setSize(iMenSize, iWomenSize);
        for (int i = 0; i < iMenSize; ++i)
        {
            String manName = menTerms[i];
            m_MenSet.put(new Man(iWomenSize, manName), new TreeMap<Object, Object>());
        }
        for (int j = 0; j < iWomenSize; ++j)
        {
            String womanName = (String) womenTerms[j];
            m_WomenSet.put(new Woman(iMenSize, womanName), new TreeMap<Object, Object>());
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
                double d1 = m_MatchMatrix.getMatchConfidenceByAttributeNames(woman.getName(),
                    man.getName());
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
        matchOntologies();
        readStableMarriagePlayers();
        setPreferences();
        run();
    }

    ArrayList<Man> m_alMatchingResult;
    // private static int m_iThreshold = 5;
}
