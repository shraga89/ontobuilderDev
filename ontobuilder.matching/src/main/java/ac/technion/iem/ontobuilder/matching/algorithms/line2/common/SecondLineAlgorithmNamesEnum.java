package ac.technion.iem.ontobuilder.matching.algorithms.line2.common;

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
 */
public enum SecondLineAlgorithmNamesEnum
{
    TH("Threshold");
    
    private String _name;

    private SecondLineAlgorithmNamesEnum(String name)
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
            TH.getName()
        };
        return allNames;
    };
}
