package ac.technion.iem.ontobuilder.matching.meta.statistics;

import ac.technion.iem.ontobuilder.matching.algorithms.line2.meta.AbstractMetaAlgorithm;

/**
 * <p>Title: TAStatistics</p>
 * <p>Description: Threshold Algorithm Statistics</p>
 * Extends {@link MetaAlgorithmStatistics}
 */
public class TAStatistics extends MetaAlgorithmStatistics
{

    private int threadsCount = 0;
    private int[][] generatedMappingsPerThread;

    /**
     * Constructs a TAStatistics with algorithm, Candidate Schema Name and Target Schema Name
     * 
     * @param algorithm the {@link AbstractMetaAlgorithm}
     * @param candidateSchemaName the candidate schema name
     * @param targetSchemaName the target schema name
     */
    public TAStatistics(AbstractMetaAlgorithm algorithm, String candidateSchemaName,
        String targetSchemaName)
    {
        super(algorithm, candidateSchemaName, targetSchemaName);
    }

    /**
     * Get the thread count
     * 
     * @return the thread count
     */
    public int getThreadsCount()
    {
        return threadsCount;
    }

    /**
     * Set the thread count and create a place-holder for the mapping
     * 
     * @param threadsCount the thread count
     */
    public void setThreadsCount(int threadsCount)
    {
        this.threadsCount = threadsCount;
        generatedMappingsPerThread = new int[threadsCount][getKParameter()];
    }

    /**
     * Increase the thread mapping count according to the thread id and k count
     * 
     * @param tid the thread id
     * @param k
     */
    public synchronized void increaseThreadMappingCount(int tid, int k)
    {
        generatedMappingsPerThread[tid][k != 0 ? k - 1 : k]++;
    }

    /**
     * Not implemented
     */
    public void updateMappingsCount()
    {

    }

    /**
     * Prints the statistics
     */
    public void printStatistics()
    {
        super.printStatistics();
        System.out.println("> Threads Count: " + getThreadsCount());
        System.out.println("> New Mappings Generated Per Thread:");
        for (int i = 0; i < threadsCount; i++)
            System.out.println("    > Thread " + (i + 1) + " Total New Generated Mappings: " +
                generatedMappingsPerThread[i]);
        System.out.println("> Last Threshold Value: " + getLastThreshold());
        System.out.println("> Total Scored High Mappings : " + getTotalHighMappings());
    }

    /**
     * Returns a String with the statistics
     */
    public String getStatisticsString()
    {
        String stat = super.getStatisticsString();
        stat += "> Threads Count: " + getThreadsCount() + "\n";
        stat += "> New Mappings Generated Per Thread:" + "\n";
        for (int i = 0; i < threadsCount; i++)
            stat += "    > Thread " + (i + 1) + " Total New Generated Mappings: " +
                generatedMappingsPerThread[i] + "\n";
        stat += "> Last Threshold Value: " + getLastThreshold() + "\n";
        stat += "> Total Scored High Mappings : " + getTotalHighMappings() + "\n";
        return stat;
    }

}