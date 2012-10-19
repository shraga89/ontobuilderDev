package ac.technion.iem.ontobuilder.gui.tools.mergewizard;

import java.util.LinkedList;

import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.wrapper.SchemaMatchingsException;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.wrapper.SchemaMatchingsWrapper;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.utils.SchemaMatchingsUtilities;

import com.jgraph.JGraph;

/**
 * <p>Title: MatchGraphStatus</p>
 * <p>Description: Implementation of the status of a match graph</p>
 */
public class MatchGraphStatus
{

    private JGraph one2ManyBestGraph = null;
    private MatchInformation st = null;
    private JGraph displayedGraph = null;
    private double scale = 1;
    private int topkIndex = 0;
    private int currentK = 0;
    private LinkedList<MatchInformation> bestMatches = null;
    private SchemaMatchingsWrapper topk = null;
    private MatchInformation matchInformation;

    // debug
    private MatchInformation temp;
    private boolean debug = false;

    // debug

    /**
     * Constructs a MatchGraphStatus
     * 
     * @param matchInformation the {@link MatchInformation}
     * @throws SchemaMatchingsException
     */
    public MatchGraphStatus(MatchInformation matchInformation) throws SchemaMatchingsException
    {
        bestMatches = new LinkedList<MatchInformation>();
        topk = new SchemaMatchingsWrapper(matchInformation);
        this.matchInformation = matchInformation;
    }

    /**
     * Set the current K for the TopK algorithm
     *
     * @param k the k to set
     */
    public void setCurrentK(int k)
    {
        currentK = k;
    }

    /**
     * Get the current k 
     *
     * @return the current k
     */
    public int getCurrentK()
    {
        return currentK;
    }

    /**
     * Get the match information
     *
     * @return the {@link MatchInformation}
     */
    public MatchInformation getMatchInformation()
    {
        return matchInformation;
    }

    /**
     * Get the best match using the TopK algorithm
     *
     * @return a {@link SchemaTranslator}
     * @throws SchemaMatchingsException
     */
    public MatchInformation best() throws SchemaMatchingsException
    {
        if (currentK == 0)
        {
            bestMatches.add(currentK, topk.getBestMatching());
            currentK = 1;
        }
        topkIndex = 1;
        MatchInformation best = bestMatches.getFirst();
        if (debug)
        {
            debug(best);
            temp = best;
        }

        st = best;

        return best;
    }

    /**
     * Get the next best match
     *
     * @return a {@link SchemaTranslator}
     * @throws SchemaMatchingsException
     */
    public MatchInformation next() throws SchemaMatchingsException
    {
        ++topkIndex;
        MatchInformation next;
        if (topkIndex == 1)
            return best();
        if (topkIndex < currentK)
            next = bestMatches.get(topkIndex - 1);
        else
        {
            bestMatches.add(currentK++, topk.getNextBestMatching());
            next = bestMatches.get(topkIndex - 1);
        }

        if (debug)
        {
            debug(next);
            temp = next;
        }

        st = next;

        return next;
    }

    /**
     * Get the previous best match
     *
     * @return a {@link SchemaTranslator}
     * @throws SchemaMatchingsException
     */
    public MatchInformation previous() throws SchemaMatchingsException
    {

        topkIndex = (topkIndex > 1 ? topkIndex - 1 : 1);
        MatchInformation previous;
        if (topkIndex == 1)
            return best();
        else
        {
            previous = bestMatches.get(topkIndex - 1);
        }

        if (debug)
        {
            debug(previous);
            temp = previous;
        }

        st = previous;

        return previous;

    }

    private void debug(MatchInformation newMatching)
    {
        if (debug)
        {
            if (temp != null)
            {
                SchemaMatchingsUtilities.printDiffBestMatches(temp, newMatching);
            }
        }
    }

    public SchemaMatchingsWrapper getTopk()
    {
        return topk;
    }

    /**
     * Get the schema translator
     *
     * @return a {@link SchemaTranslator}
     */
    public MatchInformation getSt()
    {
        return st;
    }

    /**
     * Set the Match Information
     *
     * @param mi a {@link SchemaTranslator}
     */
    public void setSt(MatchInformation mi)
    {
        this.st = mi;
    }

    /**
     * Get the TopK index
     *
     * @return the index
     */
    public int getTopkIndex()
    {
        return topkIndex;
    }

    /**
     * Get the One-To-Many best graph
     *
     * @return the {@link JGraph}
     */
    public JGraph getOne2ManyBestGraph()
    {
        return one2ManyBestGraph;
    }

    /**
     * Set the One-To-Many best graph
     *
     * @param one2ManyBestGraph a {@link JGraph}
     */
    public void setOne2ManyBestGraph(JGraph one2ManyBestGraph)
    {
        this.one2ManyBestGraph = one2ManyBestGraph;
    }

    /**
     * Get the scale
     *
     * @return the scale
     */
    public double getScale()
    {
        return scale;
    }

    /**
     * Set the scale
     *
     * @param scale a scale
     */
    public void setScale(double scale)
    {
        this.scale = scale;
    }

    /**
     * Get the display graph
     *
     * @return the {@link JGraph}
     */
    public JGraph getDisplayedGraph()
    {
        return displayedGraph;
    }

    /**
     * Set the display graph
     *
     * @param displayedGraph a displayed graph
     */
    public void setDisplayedGraph(JGraph displayedGraph)
    {
        this.displayedGraph = displayedGraph;
    }
}
