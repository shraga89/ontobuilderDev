package ac.technion.iem.ontobuilder.matching.wrapper;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.resources.OntoBuilderResources;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.AbstractAlgorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.Algorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.AlgorithmException;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.AlgorithmUtilities;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.misc.MatchingAlgorithmsNamesEnum;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.match.MatchOntologyHandler;

/**
 * <p>Title: OntoBuilderWrapper</p>
 * <p>Description: This class provides an interface for the user to compare between a target and a candidate
 * ontologies using a matching algorithm</p>
 * Extends {@link OntoBuilder}
 */
public final class OntoBuilderWrapper extends OntoBuilder
{

    private URL candidateURL;
    private URL targetURL;
    private Ontology candidateOntology;
    private Ontology targetOntology;

    private String defaultAlgorithm = MatchingAlgorithmsNamesEnum.TERM.getName();
    private boolean newCandidate = true;

    private Algorithm algorithm;

    /**
     * Constructs a default OntoBuilderWrapper Default values: <br>
     * defaultAlgorithm: MatchingAlgorithms.TERM (the matching algorithm) <br>
     * useThesaurus: <code>true</code> - should Thesaurus be used <br>
     * newCandidate: <code>true</code>  - a flag to prevent recreating an Ontology from the same URL
     */
    public OntoBuilderWrapper()
    {
        super();
    }

    /**
     * Constructs a light OntoBuilderWrapper Creates a light OntoBuilderWrapper Default values: <br>
     * defaultAlgorithm: MatchingAlgorithms.TERM (the matching algorithm) <br>
     * useThesaurus: <code>true</code>  - should Thesaurus be used <br>
     * newCandidate: <code>true</code>  - a flag to prevent recreating an Ontology from the same URL
     * @param light <code>true</code> to create a light ontology
     */
    public OntoBuilderWrapper(boolean light)
    {
        super(light);
    }

    /**
     * Get the match algorithm that is used for matching the ontologies
     * 
     * @return the {@link AbstractAlgorithm}
     */
    public AbstractAlgorithm getUsedAlgorithm()
    {
        return (AbstractAlgorithm) algorithm;
    }

    /**
     * Loads the match algorithm to use
     * 
     * @param algorithmToUse the name of the algorithm to use
     * @return the algorithm instance
     * @throws AlgorithmException
     */
    public AbstractAlgorithm loadMatchAlgorithm(String algorithmToUse) throws AlgorithmException
    {
        return (AbstractAlgorithm) AlgorithmUtilities.getAlgorithmsInstance(
        	getAlgorithmsFile(), algorithmToUse);
    }

    /**
     * Set the URL of the candidate schema and marks internally if it's a new candidate schema or
     * has been used last
     * 
     * @param candidateURL the {@link URL} to parse a candidate ontology from
     */
    public void setCandidantURL(URL candidateURL)
    {
        if (this.candidateURL != null && this.candidateURL.equals(candidateURL))
            newCandidate = false;
        else
            newCandidate = true;
        this.candidateURL = candidateURL;
    }

    /**
     * Set the URL of the target schema
     * 
     * @param targetURL the {@link URL} to parse a target ontology from
     */
    public void setTargetURL(URL targetURL)
    {
        this.targetURL = targetURL;
    }

    /**
     * Match a candidate and target ontologies
     * 
     * @param algorithmToUse set the desired algorithm for matching the ontologies
     * @return MatchInformation - holds the results of the matches
     * @throws OntoBuilderWrapperException
     */
    public MatchInformation matchOntologies(String algorithmToUse)
        throws OntoBuilderWrapperException
    {
        try
        {
            if (candidateURL == null || targetURL == null)
                return null;
            else
            {
                algorithm = AlgorithmUtilities.getAlgorithmsInstance(getAlgorithmsFile(),
                    algorithmToUse);
                if (algorithm == null)
                {
                    return null;
                }
                System.out.println("matching process starts...uses algorithm:" +
                    algorithm.getName());
                targetOntology = Ontology.generateOntology(targetURL).getOntology();
                System.out.println("target ontology generation finished...");
                if (targetOntology == null)
                {
                    return null;
                }
                if (newCandidate)
                {
                    candidateOntology = Ontology.generateOntology(candidateURL).getOntology();
                    System.out.println("candidate ontology generation finished...");
                }
                else
                {
                    System.out.println("candidate ontology has already been generated...");
                }
                if (candidateOntology == null)
                {
                    return null;
                }
                System.out.println("normalizing ontologies...");
                targetOntology.normalize();
                candidateOntology.normalize();
                System.out.println("match process finished...");
                return MatchOntologyHandler.match(targetOntology, candidateOntology, algorithm);
            }
        }
        catch (Throwable e)
        {
            throw new OntoBuilderWrapperException(e.getMessage());
        }

    }

    /**
     * @param candidateOntology the candidate {@link Ontology}
     * @param targetOntology the target {@link Ontology}
     * @param algorithmToUse set the desired algorithm for matching the ontologies
     * @return MatchInformation - holds the results of the matches
     * @throws OntoBuilderWrapperException
     */
    public MatchInformation matchOntologies(Ontology candidateOntology, Ontology targetOntology,
        String algorithmToUse) throws OntoBuilderWrapperException
    {
        try
        {
            algorithm = AlgorithmUtilities.getAlgorithmsInstance(getAlgorithmsFile(),
                algorithmToUse);
            if (algorithm == null)
            {
                return null;
            }
            System.out.println("matching process starts...uses algorithm:" + algorithm.getName());
            System.out.println("normalizing ontologies...");
            targetOntology.normalize();
            candidateOntology.normalize();
            MatchInformation res = MatchOntologyHandler.match(targetOntology, candidateOntology, algorithm);
            System.out.println("match process finished...");
            return res;
        }
        catch (Throwable e)
        {
            throw new OntoBuilderWrapperException(e.getMessage(),e);
        }
    }

    /**
     * @param candidateURL The URL which a candidate ontology will be generated from
     * @param targetURL The URL which a target ontology will be generated from
     * @param matchAlgorithm set the desired algorithm for matching the ontologies
     * @return MatchInformation - holds the results of the matches
     * @throws OntoBuilderWrapperException
     */
    public MatchInformation matchOntologies(URL candidateURL, URL targetURL, String matchAlgorithm)
        throws OntoBuilderWrapperException
    {
        setCandidantURL(candidateURL);
        setTargetURL(targetURL);
        return matchOntologies(matchAlgorithm);
    }

    /**
     * @param candidateURL The URL which a candidate ontology will be generated from
     * @param targetURL The URL which a target ontology will be generated from
     * @return MatchInformation - holds the results of the matchess
     * @throws MalformedURLException
     * @throws OntoBuilderWrapperException
     */
    public MatchInformation matchOntologies(String candidateURL, String targetURL)
        throws MalformedURLException, OntoBuilderWrapperException
    {
        setCandidantURL(new URL(candidateURL));
        setTargetURL(new URL(targetURL));
        return matchOntologies(defaultAlgorithm);
    }

    /**
     * Set a default algorithm from the list at: MatchingAlgorithms interface
     * 
     * @param defaultAlgorithm the default algorithm name
     */
    public void setDefaultAlgorithm(String defaultAlgorithm)
    {
        this.defaultAlgorithm = defaultAlgorithm;
    }

    /**
     * Set the default algorithm
     * 
     * @return the default algorithm name
     */
    public String getDefaultAlgorithm()
    {
        return defaultAlgorithm;
    }

    /**
     * Match a candidate and target ontologies with the default algorithm
     * 
     * @param candidateURL The URL which a candidate ontology will be generated from
     * @param targetURL The URL which a target ontology will be generated from
     * @param algorithmName set the desired algorithm for matching the ontologies
     * @return MatchInformation holds the results of the matches
     * @throws MalformedURLException
     * @throws OntoBuilderWrapperException
     */
    public MatchInformation matchOntologies(String candidateURL, String targetURL,
        String algorithmName) throws MalformedURLException, OntoBuilderWrapperException
    {
        setCandidantURL(new URL(candidateURL));
        setTargetURL(new URL(targetURL));
        return matchOntologies(algorithmName);
    }

    /**
     * Extracts and ontology from an .xml
     * 
     * @param siteURL provide a URL to create an Ontology from
     * @param normalize <code>true</code> to normalize ontology
     * @return Ontology - an ontology object built from the URL provided
     * @throws OntoBuilderWrapperException
     */
    public Ontology generateOntology(String siteURL, boolean normalize)
        throws OntoBuilderWrapperException
    {
        try
        {
            Ontology toReturn = Ontology.generateOntology(new URL(siteURL)).getOntology();
            if (toReturn == null)
                return null;
            if (normalize)
                toReturn.normalize();
            return toReturn;
        }
        catch (Exception e)
        {
            throw new OntoBuilderWrapperException(e.getMessage());
        }
    }
    
    private File getAlgorithmsFile()
    {
    	return new File(OntoBuilderResources.Config.Matching.ALGORITHMS_XML);
    }
}