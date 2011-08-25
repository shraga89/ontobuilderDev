package ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.impl;

public enum MaxWeightBipartiteMatchingAlgorithmHeuristicsEnum
{
    NAIVE_HEURISTIC(0),
    SIMPLE_HEURISTIC(1),
    REFINED_HEURISTIC(2);
    
    private int _id;
    
    private MaxWeightBipartiteMatchingAlgorithmHeuristicsEnum(int id)
    {
        _id = id;
    }
    
    public int getId()
    {
        return _id;
    }
}
