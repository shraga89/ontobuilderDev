package ac.technion.iem.ontobuilder.matching.algorithms.line1.misc;

/**
 * <p>
 * Title: SimilarityFloodingAlgorithmFixpointFormulasTypes
 * </p>
 * <p>
 * Description: Keeps all the available types of fixpoint formulas
 * </p>
 * Available types: <code>FIX_BASIC</code>, <code>FIX_A</code> and <code>MAX_ITERATIONS</code>
 */
public enum SimilarityFloodingAlgorithmFixpointFormulasTypes
{
    // available types of fixpoint formulas
    FIX_BASIC(1),
    FIX_A(2),
    MAX_ITERATIONS(10);
    
    private double _id;
    
    private SimilarityFloodingAlgorithmFixpointFormulasTypes(double id)
    {
        _id = id;
    }
    
    public double getValue()
    {
        return _id;
    }
}
