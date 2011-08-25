package ac.technion.iem.ontobuilder.matching.algorithms.line1.term;

/**
 * <p>
 * Title: TermAlgorithmFlagsEnum
 * </p>
 * <p>
 * Description: Keeps all the available term algorithm flags types
 * </p>
 * Available types: <code>FIX_BASIC</code>, <code>FIX_A</code> and <code>MAX_ITERATIONS</code>
 */
public enum TermAlgorithmFlagsEnum
{
    SYMMETRIC_FLAG(1),
    USE_THESAURUS_FLAG(2),
    USE_SOUNDEX_FLAG(4);
    
    private int _id;
    
    private TermAlgorithmFlagsEnum(int id)
    {
        _id = id;
    }
    
    public int getValue()
    {
        return _id;
    }
}
