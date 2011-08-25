package ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.wrapper;

import ac.technion.iem.ontobuilder.matching.algorithms.line2.tkm.TKMInitializationException;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.tkm.TKMRunningException;
import ac.technion.iem.ontobuilder.matching.meta.match.MatchMatrix;
import ac.technion.iem.ontobuilder.matching.utils.SchemaMatchingAlgorithmsRunner;
import ac.technion.iem.ontobuilder.matching.utils.SchemaTranslator;

/**
 * <p>
 * Title: SchemaMatchingsWrapper
 * </p>
 * <p>
 * Description: SchemaMatchingsWrapper - this wrapper class wraps the schema matchings<br>
 * utilities - used when mapping is between Ontologies
 * </p>
 * 
 * @author Haggai Roitman
 * @version 1.1
 */
public final class SchemaMatchingsWrapper
{

    private SchemaMatchingAlgorithmsRunner smRunner = null;
    private MatchMatrix matchMatrix = null;
    private String[] candidateTermNames;
    private String[] targetTermNames;

    /**
     * Constructs a SchemaMatchingsWrapper
     *
     * @param matchMatrix a {@link MatchMatrix}
     * @throws SchemaMatchingsException
     * @throws IllegalArgumentException
     */
    public SchemaMatchingsWrapper(MatchMatrix matchMatrix) throws SchemaMatchingsException,
        IllegalArgumentException
    {
        if (matchMatrix == null)
            throw new IllegalArgumentException("Must specify match information");
        this.matchMatrix = matchMatrix;
        candidateTermNames = matchMatrix.getCandidateTermNames();
        targetTermNames = matchMatrix.getTargetTermNames();
        try
        {
            smRunner = new SchemaMatchingAlgorithmsRunner();
            smRunner.setInitialSchema(candidateTermNames);
            smRunner.setMatchedSchema(targetTermNames, matchMatrix.getMatchMatrix());
        }
        catch (TKMInitializationException ge)
        {
            throw new SchemaMatchingsException(ge.getMessage());
        }
    }

    /**
     * Get the adjacency matrix
     * 
     * @return the matrix
     */
    public double[][] getAdjMatrix()
    {
        if (smRunner == null)
            return null;
        return smRunner.getAdjMatrix();
    }

    /**
     * Reset the wrapper with the match matrix
     * 
     * @param matchMatrix the matrix information to use for the reset
     * @throws SchemaMatchingsException
     * @throws IllegalArgumentException
     */
    public void reset(MatchMatrix matchMatrix) throws SchemaMatchingsException,
        IllegalArgumentException
    {
        if (matchMatrix == null)
            throw new IllegalArgumentException("Must specify match information");
        this.matchMatrix = matchMatrix;
        candidateTermNames = matchMatrix.getCandidateTermNames();
        targetTermNames = matchMatrix.getTargetTermNames();
        try
        {
            smRunner = new SchemaMatchingAlgorithmsRunner();
            smRunner.setInitialSchema(candidateTermNames);
            smRunner.setMatchedSchema(targetTermNames, matchMatrix.getMatchMatrix());
        }
        catch (TKMInitializationException ge)
        {
            // ge.printStackTrace();
            throw new SchemaMatchingsException(ge.getMessage());
        }
    }

    /**
     * Returns the match matrix
     * 
     * @return a {@link MatchMatrix}
     */
    public MatchMatrix getMatchMatrix()
    {
        return matchMatrix;
    }

    /**
     * Get the best matching
     * 
     * @return {@link SchemaTranslator} of the best marching
     * @throws SchemaMatchingsException
     */
    public SchemaTranslator getBestMatching() throws SchemaMatchingsException
    {
        if (matchMatrix == null || smRunner == null)
            throw new SchemaMatchingsException("Schema Matchings Wrapper hasn't been intialized");
        return smRunner.getBestMatching();
    }

    /**
     * Get the next best matching
     * 
     * @return {@link SchemaTranslator} of the best marching
     * @throws SchemaMatchingsException
     */
    public SchemaTranslator getNextBestMatching() throws SchemaMatchingsException
    {
        if (matchMatrix == null || smRunner == null)
            throw new SchemaMatchingsException("Schema Matchings Wrapper hasn't been intialized");
        try
        {
            return smRunner.getNextBestMatching(true);
        }
        catch (TKMRunningException e)
        {
            throw new SchemaMatchingsException(e.getMessage());
        }
    }

    /**
     * Get the previous best matching
     * 
     * @return {@link SchemaTranslator} of the previous best marching
     * @throws SchemaMatchingsException
     */
    public SchemaTranslator getPreviousBestMatching() throws SchemaMatchingsException
    {
        if (matchMatrix == null || smRunner == null)
            throw new SchemaMatchingsException("Schema Matchings Wrapper hasn't been intialized");
        try
        {
            return smRunner.getPreviousBestMatching(true);
        }
        catch (TKMRunningException e)
        {
            throw new SchemaMatchingsException(e.getMessage());
        }
    }

    /**
     * Get the K-th best matching
     * 
     * @return {@link SchemaTranslator} of the  K-th best marching
     * @throws SchemaMatchingsException
     */
    public SchemaTranslator getKthBestMatching(int k) throws SchemaMatchingsException
    {
        if (matchMatrix == null || smRunner == null)
            throw new SchemaMatchingsException("Schema Matchings Wrapper hasn't been intialized");
        try
        {
            return smRunner.getKthBestMatching(k, true);
        }
        catch (TKMRunningException e)
        {
            throw new SchemaMatchingsException(e.getMessage());
        }
    }

    /**
     * Get the second best matching
     * 
     * @return {@link SchemaTranslator} of the second best marching
     * @throws SchemaMatchingsException
     */
    public SchemaTranslator getSecondBestMatching() throws SchemaMatchingsException
    {
        if (matchMatrix == null || smRunner == null)
            throw new SchemaMatchingsException("Schema Matchings Wrapper hasn't been intialized");
        try
        {
            return smRunner.getSecondBestMatching(true, true);
        }
        catch (TKMRunningException e)
        {
            throw new SchemaMatchingsException(e.getMessage());
        }
    }

}
