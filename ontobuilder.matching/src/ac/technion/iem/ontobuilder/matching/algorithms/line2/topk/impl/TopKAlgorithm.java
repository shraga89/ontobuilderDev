package ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.impl;

import java.util.Vector;

import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.EdgesSet;

/**
 * <p>Title: TopKAlgorithm</p>
 * Extends {@link SchemaMatchingsAlgorithm}
 */
public interface TopKAlgorithm extends SchemaMatchingsAlgorithm
{

    public EdgesSet getNextMatching(boolean openFronter) throws Exception;

    public EdgesSet getLocalSecondBestMatching();

    public Vector<EdgesSet> getNextHeuristicMatchings(byte nonUniformVersion) throws Exception;
}
