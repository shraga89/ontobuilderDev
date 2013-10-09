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

public class ContentBasedAlgorithm extends TermAlgorithm{

	protected double soundexWeight;
	
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
        this.soundexWeight = 0.5;
        this.nGramWeight = 0.5;
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
    	//return PropertiesHandler.getResourceString("algorithm.Domain ");
    }

    /**
     * Get the description
     * 
     * @return the description
     */
    public String getDescription()
    {
    //    return PropertiesHandler.getResourceString("algorithm.value.description");
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
	 * @return the soundexWeight
	 */
	public double getSoundexWeight() {
		return soundexWeight;
	}

	/**
	 * @param soundexWeight the soundexWeight to set
	 */
	public void setSoundexWeight(double soundexWeight) {
		this.soundexWeight = soundexWeight;
	}

	/**
     * Get the terms/domains to match
     * 
     * @param targetOntology the target {@link Ontology}
     * @param candidateOntology the candidate {@link Ontology}
     */
    protected void getTermsToMatch(Ontology targetOntology, Ontology candidateOntology)
    {
   //	System.out.println("getTermsToMatch of DomainAlgorithm");
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
        ArrayList<Double> maxBetweenInstancesTarget2Candidate = new  ArrayList<Double>();
		ArrayList<Double> maxBetweenInstancesCandidate2Target = new  ArrayList<Double>();
       // System.out.print("threshold:" + threshold);
        Domain tDom = targetTerm.getDomain();
        Domain cDom = candidateTerm.getDomain();    
//////////////////////////////////////////////////////////
        for (int k = 0; k < tDom.getEntries().size() ; k++){
    		for (int m = 0; m < cDom.getEntries().size() ; m++){
    			Double result = getSimilarity(tDom.getEntries().get(k), cDom.getEntries().get(m));
    			maxBetweenInstancesTarget2Candidate.add(result);
    		}
    		Double maxTargertList = Collections.max(maxBetweenInstancesTarget2Candidate);
    		total += maxTargertList;
    	}
    	for (int k = 0; k < cDom.getEntries().size() ; k++){
    		for (int m = 0; m < tDom.getEntries().size() ; m++){
    			Double result = getSimilarity(cDom.getEntries().get(k), tDom.getEntries().get(m));
    			maxBetweenInstancesCandidate2Target.add(result);
    		}
    		Double maxCandidateList = Collections.max(maxBetweenInstancesCandidate2Target);
    		total += maxCandidateList;
    	}
    	double similarityBetweenElements = total/(cDom.getEntries().size()+tDom.getEntries().size());
    	effectiveness = similarityBetweenElements;
 /////////////////////////////////////////////////////       
//        effectiveness = getDistance(cDom,tDom);
//        return effectiveness >= threshold;
        return effectiveness > threshold;

    }
    
    /* Find the Distance between 2 Domains, using bitwise similarity.
     * Checks distance between the average size of the Domain entries 
     * 
     * @val - returns the similarity between 0 to 1 
     */
    public Double getSimilarity(DomainEntry tDomainEntry, DomainEntry cDomainEntry){

        String targetName = tDomainEntry.getName();
        String candidateName = cDomainEntry.getName();

        // n Gram matching
        double nGramEffectiveness = StringUtilities.getNGramEffectivity(targetName, candidateName, nGram);

        // soundex matching
        double soundexEffectiveness = StringUtilities.getSoundexEffectivity(targetName, candidateName);

		double res = (nGramEffectiveness * nGramWeight + soundexEffectiveness * soundexWeight) /(nGramWeight + soundexWeight);
		
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
