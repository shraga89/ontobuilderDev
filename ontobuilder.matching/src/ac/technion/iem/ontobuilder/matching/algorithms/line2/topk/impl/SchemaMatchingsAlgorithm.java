package ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.impl;

import java.util.Set;

import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.Edge;


/**
 * <p>
 * Title: SchemaMatchingsAlgorithm
 * </p>
 * <p>
 * Description: General interface for schema matching algorithm
 * </p>
 * Extends {@link AlgorithmsNames}
 * 
 * @author Haggai Roitman
 * @version 1.1
 */
public interface SchemaMatchingsAlgorithm
{
    public void nullify();

    public Set<Edge> runAlgorithm() throws Exception;

    public String getAlgorithmName();
}