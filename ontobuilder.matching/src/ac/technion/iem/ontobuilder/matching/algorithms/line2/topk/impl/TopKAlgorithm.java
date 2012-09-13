package ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.impl;

import java.util.List;
import java.util.Set;

import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.Edge;

/**
 * <p>Title: TopKAlgorithm</p>
 * Extends {@link SchemaMatchingsAlgorithm}
 */
public interface TopKAlgorithm extends SchemaMatchingsAlgorithm
{

    public Set<Edge> getNextMatching(boolean openFronter) throws Exception;

    public Set<Edge> getLocalSecondBestMatching();

    public List<Set<Edge>> getNextHeuristicMatchings(byte nonUniformVersion) throws Exception;
}
