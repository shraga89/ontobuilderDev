package ac.technion.iem.ontobuilder.matching.algorithms.line2.misc;

import java.util.Vector;

import ac.technion.iem.ontobuilder.matching.match.MatchInformation;

/**
 * <p>Title: NonUniformMetaAlgorithm</p>
 * @author Haggai Roitman
 * @version 1.1
 */
public interface NonUniformMetaAlgorithm
{
    public void notifyNewHeuristicMappings(int tid, MatchInformation alpha,
        Vector<MatchInformation> betas);

    public int progressWith();
}
