package ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.impl;

/**
 * <p>
 * Title: AlgorithmsNames
 * </p>
 * <p>
 * Description: Enum of the Top K Algorithms Names
 * </p>
 * Available values:
 * <code>Floyd Warshall Algorithm</code>, <code>Top K Mappings Algorithm</code>, <codeMax Weighted Bipartite Matching Algorithm></code>, 
 * <code>Second Best Mapping Naive Algorithm</code>, <code>Second Best Mapping Refined Algorithm</code> and <code>Min Top K Algorithm</code>
 * @author Haggai Roitman
 * @version 1.1
 */
public enum TopKAlgorithmsNamesEnum
{
    FLOYD_WARSHALL_ALGORITHM("Floyd Warshall Algorithm"), 
    K_BEST_ALGORITHM("Top K Mappings Algorithm"), 
    MWBM_ALGORITHM("Max Weighted Bipartite Matching Algorithm"), 
    SECOND_BEST_NAIVE_ALGORITHM("Second Best Mapping Naive Algorithm"), 
    SECOND_BEST_REFINED_ALGORITHM("Second Best Mapping Refined Algorithm"),
    MIN_TOP_K_ALGORITHM("Min Top K Algorithm");

    private String _name;

    private TopKAlgorithmsNamesEnum(String name)
    {
        _name = name;
    }

    public String getName()
    {
        return _name;
    }
}
