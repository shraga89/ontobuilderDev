package ac.technion.iem.ontobuilder.matching.meta.statistics;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import ac.technion.iem.ontobuilder.matching.algorithms.line2.meta.AbstractMetaAlgorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.meta.MetaAlgorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.utils.Point;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.utils.TopKPlot;

/**
 * <p>Title: AbstractMapping</p>
 * <p>Description: Calculates statistics of the algorithms</p>
 * Available types: <br>
 * <code>TA_STATISTICS</code>, <code>MD_STATISTICS</code>, <code>MDB_STATISTICS</code> and <code>HYBRID_STATISTICS</code> 
 */
public class MetaAlgorithmStatistics
{
    private int iterations = 0;
    private int mappings = 0;
    private int k = 0;
    private int topKIndexs = 0;
    private long startTimeMilisec = 0;
    private long stopTimeMilisec = 0;
    private double lastThreshold = 0;
    private int totalHighMappings = 0;
    private AbstractMetaAlgorithm algorithm;
    private TopKPlot iterationsPlot;
    private TopKPlot precisionPlot;
    protected TopKPlot mappingsPlot;
    private String candidateSchemaName;
    private String targetSchemaName;
    private String localFAggragatorType;
    private String localHAggragatorType;
    private String globalFAggragatorType;
    private String globalHAggragatorType;
    private int numOfUsefullMappings;
    private PrintWriter out;
    private int lastMappingsCount = 0;
    private int kOf100Recall = -1;
    private int recallIterations = -1;

    /**
     * Constructs a MetaAlgorithmStatistics with algorithm, Candidate Schema Name and Target Schema
     * Name and creates a file to write the statistics to.
     * 
     * @param algorithm the {@link AbstractMetaAlgorithm}
     * @param candidateSchemaName the candidate schema name
     * @param targetSchemaName the target schema name
     */
    public MetaAlgorithmStatistics(AbstractMetaAlgorithm algorithm, String candidateSchemaName,
        String targetSchemaName)
    {
        this.algorithm = algorithm;
        this.candidateSchemaName = candidateSchemaName;
        this.targetSchemaName = targetSchemaName;
        iterationsPlot = new TopKPlot(algorithm.getAlgorithmName(), "k", "iterations");
        mappingsPlot = new TopKPlot(algorithm.getAlgorithmName(), "k", "mappingss");
        try
        {
            out = new PrintWriter(new FileOutputStream(algorithm.getAlgorithmName() + ".txt"));
        }
        catch (IOException io)
        {
            System.err.println("An IOException occured when opening the file " + io.getMessage());
        }
    }

    /**
     * Get the Writer
     * 
     * @return the {@link PrintWriter}
     */
    public PrintWriter getWriter()
    {
        return out;
    }

    /**
     * Set the top k parameter for 100 Recall and the number of Recall iterations
     * 
     * @param k the top k parameter
     * @param i the number of recall iterations
     */
    public void setkOf100Recall(int k, int i)
    {
        kOf100Recall = k;
        recallIterations = i;
    }

    /**
     * Set the number of useful mappings
     * 
     * @param numOfUsefullMappings the number of useful mappings
     */
    public void setNumOfUsefullMappings(int numOfUsefullMappings)
    {
        this.numOfUsefullMappings = numOfUsefullMappings;
    }

    /**
     * Set the F (do not commute on M(1)... M(m)) local and global Aggregator types
     * 
     * @param lAggr the local Aggregator
     * @param gAggr the global Aggregator
     */
    public void setFAggregatorTypes(String lAggr, String gAggr)
    {
        localFAggragatorType = lAggr;
        globalFAggragatorType = gAggr;
    }

    /**
     * Set the H (commute on M(1)... M(m) and dominate F) local and global Aggregator types
     * 
     * @param lAggr the local Aggregator
     * @param gAggr the global Aggregator
     */
    public void setHAggregatorTypes(String lAggr, String gAggr)
    {
        localHAggragatorType = lAggr;
        globalHAggragatorType = gAggr;
    }

    /**
     * Get the Meta Algorithm
     * 
     * @return the {@link MetaAlgorithm}
     */
    public MetaAlgorithm getMetaAlgorithm()
    {
        return algorithm;
    }

    /**
     * Set the Precision Plot
     * 
     * @param precisionPlot the {@link TopKPlot}
     */
    public void setPrecisionPlot(TopKPlot precisionPlot)
    {
        this.precisionPlot = precisionPlot;
    }

    /**
     * Get the statistics instance according to the type
     * 
     * @param type the statistics type
     * @param algorithm the {@link AbstractMetaAlgorithm}
     * @param candidateSchemaName the candidate schema name
     * @param targetSchemaName the target schema name
     * @return a {@link MetaAlgorithmStatistics}
     */
    public static MetaAlgorithmStatistics getStatisticsInstance(MetaAlgorithmStatisticsTypeEnum type,
        AbstractMetaAlgorithm algorithm, String candidateSchemaName, String targetSchemaName)
    {
        switch (type)
        {
        case TA_STATISTICS:
            return new TAStatistics(algorithm, candidateSchemaName, targetSchemaName);
        case MD_STATISTICS:
            return new MetaAlgorithmStatistics(algorithm, candidateSchemaName, targetSchemaName);
        default:
            return null;
        }
    }

    /**
     * Set the current TopK mappings. If there are missing mappings to the counter, add the number
     * of missing mappings.
     * 
     * @param cnt the number of mappings that should exist
     */
    public void setCurrentTopKMappings(int cnt)
    {
        if (cnt > topKIndexs)
        {
            int diff = cnt - topKIndexs;
            if (diff != 0)
            {
                for (int i = 0; i < diff; i++)
                {
                    iterationsPlot.addPlot(new Point(++topKIndexs, iterations));
                    mappingsPlot.addPlot(new Point(topKIndexs, mappings/* - lastMappingsCount */));
                }
                lastMappingsCount = mappings;
            }
        }
    }

    /**
     * Prints the Non Uniform Mappings per generated Top K
     */
    public void printNonUniformMappingsPlot()
    {
        int points = iterationsPlot.size();
        Point p;
        System.out.println("Non Uniform Mappings per generated Top K");
        if (algorithm.currentGeneratedTopK() != algorithm.getK())
            System.out.println("Experiment Terminated without finishing top k mappings" +
                ",Iterations when stop:" + iterations);
        for (int i = 0; i < points; i++)
        {
            p = iterationsPlot.getPlot(i);
            if (p.y == 1)
                System.out.println(p.x + "\t" + algorithm.getNumOfSchemaMatchers());
            else
                System.out.println(p.x + "\t" + (algorithm.getNumOfSchemaMatchers() + (p.y - 1)));
        }
    }

    /**
     * Prints the total iterations needed per generated Top K
     */
    public void printIterationsPlot()
    {
        int points = iterationsPlot.size();
        Point p;
        System.out.println("Total iterations needed per generated Top K");
        for (int i = 0; i < points; i++)
        {
            p = iterationsPlot.getPlot(i);
            System.out.println(p.x + "\t" + p.y);
        }
    }

    /**
     * Prints the total unique mappings needed per generated Top K
     */
    public void printUniqueMappingsPlot()
    {
        int points = mappingsPlot.size();
        Point p;
        System.out.println("Total unique mappings needed per generated Top K");
        for (int i = 0; i < points; i++)
        {
            p = mappingsPlot.getPlot(i);
            System.out.println(p.x + "\t" + p.y);
        }
    }

    /**
     * Get the Mappings Plot
     * 
     * @return mappingsPlot the {@link TopKPlot}
     */
    public TopKPlot getMappingsPlot()
    {
        return mappingsPlot;
    }

    /**
     * Get the Iterations Plot
     * 
     * @return iterationsPlot the {@link TopKPlot}
     */
    public TopKPlot getIterationsPlot()
    {
        return iterationsPlot;
    }

    /**
     * Set the total high mappings
     * 
     * @param totalHighMappings the total high mappings
     */
    public void setTotalHighMappings(int totalHighMappings)
    {
        this.totalHighMappings = totalHighMappings;
    }

    /**
     * Get the total high mappings
     * 
     * @return totalHighMappings the total high mappings
     */
    public int getTotalHighMappings()
    {
        return totalHighMappings;
    }

    /**
     * Set the TopK parameter
     * 
     * @param k the parameter
     */
    public void setKParameter(int k)
    {
        this.k = k;
    }

    /**
     * Get the TopK parameter
     * 
     * @return k the parameter
     */
    public int getKParameter()
    {
        return k;
    }

    /**
     * Set the last threshold
     * 
     * @param lastThreshold the last threshold
     */
    public void setLastThreshold(double lastThreshold)
    {
        this.lastThreshold = lastThreshold;
    }

    /**
     * Get the last threshold
     * 
     * @return lastThreshold the last threshold
     */
    public double getLastThreshold()
    {
        return lastThreshold;
    }

    /**
     * Get the iterations count
     * 
     * @return iterations the iterations count
     */
    public int getIterationsCount()
    {
        return iterations;
    }

    /**
     * Get the last mappings count
     * 
     * @return last mappings count
     */
    public int getLastMappingsCount()
    {
        return lastMappingsCount;
    }

    /**
     * Increase the iterations count by 1
     */
    public synchronized void increaseIterationsCount()
    {
        iterations++;
    }

    /**
     * Prints the Recall information: if 100% was reached, prints the matching k
     */
    public void printRecallInformation()
    {
        if (kOf100Recall != -1)
            System.out.println("Recall reached 100% at k=" + kOf100Recall + " iterations=" +
                recallIterations);
        else
            System.out.println("Recall not reached 100% within k=" + k);
    }

    /**
     * Starts the timer
     */
    public void startTimer()
    {
        startTimeMilisec = System.currentTimeMillis();
    }

    /**
     * Stops the timer
     */
    public void stopTimer()
    {
        stopTimeMilisec = System.currentTimeMillis();
    }

    /**
     * Returns the elapsed execution time
     */
    public long getExecutionTime()
    {
        return (stopTimeMilisec - startTimeMilisec);
    }

    /**
     * Get the total mappings count
     * 
     * @return mappings the total mappings count
     */
    public int getTotalMappingsCount()
    {
        return mappings;
    }

    /**
     * Increase the total mappings count by 1
     */
    public synchronized void increaseTotalMappingsCount()
    {
        mappings++;
        // System.out.println(mappings);
    }

    /**
     * Prints the statistics
     */
    public void printStatistics()
    {
        System.out.println(getStatisticsString());
    }

    /**
     * Returns a string of the execution time in the following format:
     * <#hours>h:<#minutes>m:<#seconds>sec:<#milisecs>msec
     * 
     * @param executionTime the total exectuion time
     * @return the time presentation
     */
    private String getTimePresentation(long executionTime)
    {
        long hours = executionTime / (1000 * 60 * 60);
        long minutes = (executionTime - (hours * 60 * 60 * 1000)) / (60 * 1000);
        long seconds = (executionTime - (hours * 60 * 60 * 1000) - (minutes * 60 * 1000)) / 1000;
        long milisecs = (executionTime - (hours * 60 * 60 * 1000) - (minutes * 60 * 1000) - (seconds * 1000)) % 1000;
        return hours + "h:" + minutes + "m:" + seconds + "sec:" + milisecs + "msec";
    }

    /**
     * Gets the statistics in a string
     * 
     * @return the statistics
     */
    protected String getStatisticsString()
    {
        String stat = "";
        stat += "Meta Algorithm: " + algorithm.getAlgorithmName() + " Statistics:\n\n";
        stat += "Candidate Schema: " + candidateSchemaName + " Target Schema: " + targetSchemaName +
            "\n";
        stat += "f local:" + localFAggragatorType + " F global:" + globalFAggragatorType + "\n";
        if (localHAggragatorType != null)
            stat += "h local:" + localHAggragatorType + " H global:" + globalHAggragatorType + "\n";
        stat += "> K Best Mappings Requested: " + getKParameter() + "\n";
        stat += "> Iterations Count: " + getIterationsCount() + "\n";
        stat += "> Total Mappings Count: " + getTotalMappingsCount() + "\n";
        stat += "> Total Usefull Mappings: " + numOfUsefullMappings + "\n";
        stat += "> Execution Time: " + getTimePresentation(getExecutionTime()) + "\n";
        return stat;
    }

    /**
     * Gets the Plots string
     * 
     * @return the plots string
     */
    protected String getPlotsString()
    {
        String stat = "";
        stat += "\nPrecision vs. Top K generated mappings:\n\n";
        stat += precisionPlot.getGraphString();
        stat += "\nIterations vs. Top K generated mappings:\n\n";
        stat += iterationsPlot.getGraphString();
        stat += "\nMappingss vs. Top K generated mappings:\n\n";
        stat += mappingsPlot.getGraphString();
        return stat;
    }

    /**
     * Saves the statistics to a text file
     * 
     * @param out the {@link PrintWriter}
     * @throws IOException
     */
    public void saveToTXTFile(PrintWriter out) throws IOException
    {
        StringBuffer toSave = new StringBuffer(getStatisticsString());
        toSave.append(getPlotsString());
        out.println(toSave.toString());
        out.flush();
    }

}