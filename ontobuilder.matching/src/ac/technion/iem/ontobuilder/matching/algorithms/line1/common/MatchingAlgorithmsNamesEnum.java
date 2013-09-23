package ac.technion.iem.ontobuilder.matching.algorithms.line1.common;

/**
 * <p>
 * Title: MatchingAlgorithmsNamesEnum
 * </p>
 * <p>
 * Description: Keeps all matching algorithms names
 * </p>
 * Available algorithms: <code>Term Match</code>, <code>Value Match</code>,
 * <code>Term and Value Match</code>, <code>Combined Match</code>, <code>Precedence Match</code> and
 * <code>Graph Match</code>
 * <code>Domain Match</code>
 */
public enum MatchingAlgorithmsNamesEnum
{
    TERM("Term Match"), 
    VALUE("Value Match"), 
    TERM_VALUE_COMBINED("Term and Value Match"), 
    TERM_VALUE_PRECEDENCE_COMPOSITION_COMBINED("Combined Match"), 
    PRECEDENCE("Precedence Match"), 
    COMPOSITION("Graph Match"),
    NEW_PRECEDENCE("New Precedence Match"),
    SIMILARITY_FLOODING("SimilarityFlooding Match"),
    DOMAIN("Domain Match"),
    WordNet("WordNet Match"),
    CONTEND_BASED("ContentBasedAlgorithm");

    private String _name;

    private MatchingAlgorithmsNamesEnum(String name)
    {
        _name = name;
    }

    public String getName()
    {
        return _name;
    }

    public static String[] getAllNames()
    {
        String[] allNames =
        {
            TERM.getName(), VALUE.getName(), TERM_VALUE_COMBINED.getName(),
            TERM_VALUE_PRECEDENCE_COMPOSITION_COMBINED.getName(), PRECEDENCE.getName(),
            COMPOSITION.getName(), SIMILARITY_FLOODING.getName(),DOMAIN.getName(),WordNet.getName()
            
        };
        return allNames;
    };
}
