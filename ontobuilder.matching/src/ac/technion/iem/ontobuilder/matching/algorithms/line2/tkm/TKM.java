package ac.technion.iem.ontobuilder.matching.algorithms.line2.tkm;

import java.util.Vector;

import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
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

    public MatchInformation getNextBestMapping(boolean openFronter) throws TKMRunningException;

    public MatchInformation getKthBestMapping(int k, boolean openFronter) throws TKMRunningException;

    public void nullify();

    public MatchInformation getLocalSecondBestMapping();

    public Vector<MatchInformation> getNextHeuristicMappings(byte nonUniformVersion)
        throws TKMRunningException;
}
