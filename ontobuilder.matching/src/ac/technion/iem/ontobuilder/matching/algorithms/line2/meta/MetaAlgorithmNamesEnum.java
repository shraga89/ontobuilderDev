package ac.technion.iem.ontobuilder.matching.algorithms.line2.meta;

/**
 * <p>Title: MetaAlgorithmsEnum</p>
 * <p>Description: Keeps all the 2nd Line algorithms names</p>
 * Available algorithms:
 * <code>THERSHOLD_ALGORITHM</code>, <code>MATRIX_DIRECT_ALGORITHM</code>, 
 * <code>MATRIX_DIRECT_WITH_BOUNDING_ALGORITHM</code> and <code>HYBRID_ALGORITHM</code>
 */
public enum MetaAlgorithmNamesEnum
{
    THERSHOLD_ALGORITHM(0),
    MATRIX_DIRECT_ALGORITHM(1),
    MATRIX_DIRECT_WITH_BOUNDING_ALGORITHM(2),
    HYBRID_ALGORITHM(3);
    
    private int _id;
    
    private MetaAlgorithmNamesEnum(int id)
    {
        _id = id;
    }
    
    public int getId()
    {
        return _id;
    }
    
    public String getName()
    {
        switch (this)
        {
        case THERSHOLD_ALGORITHM:
            return "Threshold Algorithm";
        case MATRIX_DIRECT_ALGORITHM:
            return "Matrix Direct Algorithm";
        case MATRIX_DIRECT_WITH_BOUNDING_ALGORITHM:
            return "Matrix Direct with Bounding Algorithm";
        case HYBRID_ALGORITHM:
            return "CrossThreshold Algorithm";
        }
        return "";
    }
    
    public String[] getAllNames()
    {
        String[] allNames = {"Threshold Algorithm", "Matrix Direct Algorithm", "Matrix Direct with Bounding Algorithm",
            "CrossThreshold Algorithm"};
        return allNames;
    };
}
