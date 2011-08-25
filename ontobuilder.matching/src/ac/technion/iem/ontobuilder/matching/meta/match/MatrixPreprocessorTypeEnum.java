package ac.technion.iem.ontobuilder.matching.meta.match;

/**
 * <p>Title: MatrixPreprocessorTypeEnum</p>
 * <p>Description: Enum with the types of matrix pre-processing</p>
 * Available types: <br>
 * <code>UNION_PREPROCESSING</code>, <code>INTERSECT_PREPROCESSING</code> and <code>TEMPLATE_PREPROCESSING</code>
 */
public enum MatrixPreprocessorTypeEnum
{   
    UNION_PREPROCESSING(0),
    INTERSECT_PREPROCESSING(1),
    TEMPLATE_PREPROCESSING(2);
    
    private int _id;
    
    private MatrixPreprocessorTypeEnum(int id)
    {
        _id = id;
    }
    
    public int getId()
    {
        return _id;
    }
}
