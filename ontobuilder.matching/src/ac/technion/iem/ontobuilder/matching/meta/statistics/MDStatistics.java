package ac.technion.iem.ontobuilder.matching.meta.statistics;

import ac.technion.iem.ontobuilder.matching.algorithms.line2.meta.AbstractMetaAlgorithm;


/**
 * <p>Title: MDStatistics</p>
 * <p>Description: Matrix Direct Statistics</p>
 * Implements {@link MetaAlgorithmStatistics}
 */
public class MDStatistics extends MetaAlgorithmStatistics
{

    protected MDStatistics(AbstractMetaAlgorithm algorithm, String candidateSchemaName,
        String targetSchemaName)
    {
        super(algorithm, candidateSchemaName, targetSchemaName);
    }
}