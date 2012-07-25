package ac.technion.iem.ontobuilder.matching.algorithms.line2.tkm;

import java.util.Vector;

import ac.technion.iem.ontobuilder.matching.meta.match.AbstractMapping;
import ac.technion.iem.ontobuilder.matching.meta.match.MatchMatrix;

/**
 * <p>Title: interface TKM</p>
 */
public interface TKM
{

    public void init(MatchMatrix matrix) throws TKMInitializationException;

    public void setInitialSchema(long[] schema) throws TKMInitializationException;// S1

    public void setMatchedSchema(long[] schema, MatchMatrix matchMatrix)
        throws TKMInitializationException;

    public AbstractMapping getNextBestMapping(boolean openFronter) throws TKMRunningException;

    public AbstractMapping getKthBestMapping(int k, boolean openFronter) throws TKMRunningException;

    public void nullify();

    public AbstractMapping getLocalSecondBestMapping();

    public Vector<AbstractMapping> getNextHeuristicMappings(byte nonUniformVersion)
        throws TKMRunningException;
}
