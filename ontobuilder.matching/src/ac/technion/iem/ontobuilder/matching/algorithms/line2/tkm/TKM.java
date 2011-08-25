package ac.technion.iem.ontobuilder.matching.algorithms.line2.tkm;

import java.util.Vector;

import ac.technion.iem.ontobuilder.matching.meta.match.AbstractMapping;
import ac.technion.iem.ontobuilder.matching.meta.match.AbstractMatchMatrix;

/**
 * <p>Title: interface TKM</p>
 */
public interface TKM
{

    public void init(AbstractMatchMatrix matrix) throws TKMInitializationException;

    public void setInitialSchema(String[] schema) throws TKMInitializationException;// S1

    public void setMatchedSchema(String[] schema, double[][] weightsMatrix)
        throws TKMInitializationException;

    public AbstractMapping getNextBestMapping(boolean openFronter) throws TKMRunningException;

    public AbstractMapping getKthBestMapping(int k, boolean openFronter) throws TKMRunningException;

    public void nullify();

    public AbstractMapping getLocalSecondBestMapping();

    public Vector<AbstractMapping> getNextHeuristicMappings(byte nonUniformVersion)
        throws TKMRunningException;
}
