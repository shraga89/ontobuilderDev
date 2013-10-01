package ac.technion.iem.ontobuilder.matching.algorithms.line1.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jdom2.Element;

import ac.technion.iem.ontobuilder.core.ontology.Domain;
import ac.technion.iem.ontobuilder.core.ontology.DomainEntry;
import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.OntologyUtilities;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.core.utils.StringUtilities;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.common.Algorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.term.TermAlgorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.term.TermAlgorithmFlagsEnum;

public class ContentBasedAlgorithm extends TermAlgorithm{

	
	 /**
     * Make a copy of the algorithm instance
     */
    public Algorithm makeCopy()
    {
    	ContentBasedAlgorithm algorithm = new ContentBasedAlgorithm();
        algorithm.pluginName = pluginName;
        algorithm.mode = mode;
        algorithm.thesaurus = thesaurus;
        algorithm.termPreprocessor = termPreprocessor;
        algorithm.threshold = threshold;
        algorithm.effectiveness = effectiveness;
        return algorithm;
    }

    /**
     * Constructs a default DomainAlgorithm
     */
  
    public ContentBasedAlgorithm()
    {
        super();
        this.threshold = 0.5;
        this.jaroWinklerWeight = 0.9;
        this.nGramWeight = 0.1;
        this.nGram = 3;

    }
  /**
     * Get the name
     * 
     * @return the name
     */
    public String getName()
    {
    	return "Content-Based Similarity Algorithm";
    }

    /**
     * Get the description
     * 
     * @return the description
     */
    public String getDescription()
    {
    	return "Match based on content-based of schemas";
    }

    /**
     * Configure the algorithm parameters when user changes one of the values of the JTable
     * 
     * Didn't make any change - don't think that it's needed 
     * 
     * @param element the {@link Element} with the parameters to configure
     */
    public void configure(Element element)
    {    
    	return;
    }


	/**
     * Get the terms/domains to match
     * 
     * @param targetOntology the target {@link Ontology}
     * @param candidateOntology the candidate {@link Ontology}
     */
    protected void getTermsToMatch(Ontology targetOntology, Ontology candidateOntology)
    {
        super.getTermsToMatch(targetOntology, candidateOntology);
        if (!targetOntology.isLight())
        {
            originalTargetTerms.addAll(OntologyUtilities.getTermsOfClass(targetOntology,
                "decomposition"));
        }
        else
        {
            originalTargetTerms = new ArrayList<Term>(targetOntology.getTerms(true));
        }

        if (!candidateOntology.isLight())
        {
            originalCandidateTerms.addAll(OntologyUtilities.getTermsOfClass(candidateOntology,
                "decomposition"));
        }
        else
        {
            originalCandidateTerms = new ArrayList<Term>(candidateOntology.getTerms(true));
        }

    }

    /**
     * Match Comparator methods
     * 
     * @param targetTerm the target {@link Term}
     * @param candidateTerm the candidate {@link Term}
     */
    public boolean compare(Term targetTerm, Term candidateTerm)
    {
        effectiveness = 0;
        double total = 0.0;
        Domain tDom = targetTerm.getDomain();
        Domain cDom = candidateTerm.getDomain();    
        for (int k = 0; k < tDom.getEntries().size() && !tDom.getEntries().isEmpty() ; k++){
        	ArrayList<Double> maxBetweenInstancesTarget2Candidate = new  ArrayList<Double>();
    		for (int m = 0; m < cDom.getEntries().size() && !cDom.getEntries().isEmpty() ; m++){
    			Double result = getSimilarity(tDom.getEntries().get(k), cDom.getEntries().get(m));
    			maxBetweenInstancesTarget2Candidate.add(result);
    		}
    		if(!maxBetweenInstancesTarget2Candidate.isEmpty()){
    			Double maxTargertList = Collections.max(maxBetweenInstancesTarget2Candidate);
    			total += maxTargertList;
    		}
    	}
    	for (int k = 0; k < cDom.getEntries().size() && !cDom.getEntries().isEmpty() ; k++){
    		ArrayList<Double> maxBetweenInstancesCandidate2Target = new  ArrayList<Double>();
    		for (int m = 0; m < tDom.getEntries().size() && !tDom.getEntries().isEmpty() ; m++){
    			Double result = getSimilarity(cDom.getEntries().get(k), tDom.getEntries().get(m));
    			maxBetweenInstancesCandidate2Target.add(result);
    		}
    		if(!maxBetweenInstancesCandidate2Target.isEmpty()){
    			Double maxCandidateList = Collections.max(maxBetweenInstancesCandidate2Target);
    			total += maxCandidateList;
    		}
    	}
    	double similarityBetweenElements = total/(cDom.getEntries().size()+tDom.getEntries().size());
    	effectiveness = similarityBetweenElements;
        return effectiveness > threshold;

    }
    
    /* Find the Distance between 2 Domains, using bitwise similarity.
     * Checks distance between the average size of the Domain entries 
     * 
     * @val - returns the similarity between 0 to 1 
     */
    public Double getSimilarity(DomainEntry tDomainEntry, DomainEntry cDomainEntry){

        String targetName = tDomainEntry.getEntry().toString();
        String candidateName = cDomainEntry.getEntry().toString();

        // 3 Gram matching
        double nGramEffectiveness = StringUtilities.getNGramEffectivity(targetName, candidateName, nGram);

        // JaroWinkler matching
        double jaroWinklerEffectiveness = StringUtilities.getHybridJaroWinklerDistance(targetName, candidateName);

		double res = (nGramEffectiveness * nGramWeight + jaroWinklerEffectiveness * jaroWinklerWeight) /(nGramWeight + jaroWinklerWeight);
		
		return res;
    }
    
    
/* returns a list with length of the Domain Entries*/
	public List<Integer> getDomainLength(Domain d){
		List<Integer> entriesLength = new ArrayList<Integer>();
		for (DomainEntry de : d.getEntries()){
			entriesLength.add(de.toString().length());
		}
		return entriesLength;
	}

}
