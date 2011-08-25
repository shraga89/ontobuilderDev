package ac.technion.iem.ontobuilder.matching.algorithms.line2.misc;

import java.util.Vector;

import ac.technion.iem.ontobuilder.matching.meta.match.AbstractMapping;

/**
 * <p>Title: NonUniformMetaAlgorithm</p>
 * @author Haggai Roitman
 * @version 1.1
 */
public interface NonUniformMetaAlgorithm
{
    public void notifyNewHeuristicMappings(int tid, AbstractMapping alpha,
        Vector<AbstractMapping> betas);

    public int progressWith();
}
