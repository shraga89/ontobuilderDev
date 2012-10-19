package ac.technion.iem.ontobuilder.matching.algorithms.line1.domain;

import java.util.ArrayList;
import java.util.List;
import java.lang.Math;

import org.jdom.Element;

import ac.technion.iem.ontobuilder.core.ontology.Domain;
import ac.technion.iem.ontobuilder.core.ontology.DomainEntry;
import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.OntologyUtilities;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.common.Algorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.term.TermAlgorithm;

/**
 * <p>
 * Title: DomainAlgorithm
 * </p>
 * Extends a {@link TermAlgorithm}
 */
public class DomainAlgorithm extends TermAlgorithm
{
    /**
     * Make a copy of the algorithm instance
     */
    public Algorithm makeCopy()
    {
        DomainAlgorithm algorithm = new DomainAlgorithm();
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
  
    public DomainAlgorithm()
    {
        super();
        this.threshold = 0.5;
    }
  /**
     * Get the name
     * 
     * @return the name
     */
    public String getName()
    {
    	return "Domain Algorithm";
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
    	return "Match based on domain of schemas";
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
       // System.out.print("threshold:" + threshold);
        Domain tDom = targetTerm.getDomain();
        Domain cDom = candidateTerm.getDomain();    
        effectiveness = getDistance(cDom,tDom);
//        return effectiveness >= threshold;
        return effectiveness > threshold;

    }
    
    /* Find the Distance between 2 Domains, using bitwise similarity.
     * Checks distance between the average size of the Domain entries 
     * 
     * @val - returns the similarity between 0 to 1 
     */
    public Double getDistance(Domain cDomain,Domain tDomain){
    	Double val = 0.0;

    	DomainDataType[] ddts = DomainDataType.values();
     	Integer orInt = 0;
     	Integer andInt = 0;
     	Boolean c;
     	Boolean t;
     	
     	
     	// Get values for Similarity //
    	for (DomainDataType d : ddts)
    	{
    		DataTypeSniffer s = d.getSniff();
    		c = s.sniff(cDomain);
    		t = s.sniff(tDomain);
    		
    		if (t && c){
    			andInt++;
    			orInt++;
    	   			
    		}
    		else {
    			if ( t || c ){
        			orInt++;        	
    			}
    		}

    		
    	}
     	
        
     	if (orInt == 0 && andInt == 0){ 
     	// Should give result according to String length Comparison //
     		List<Integer> cAverageDomainLength = getDomainLength(cDomain);
     		List<Integer> tAverageDomainLength = getDomainLength(tDomain);
     		Double cMean = StatMethods.mean(cAverageDomainLength);
     		Double tMean = StatMethods.mean(tAverageDomainLength);
     		val = 1 - (Math.abs(cMean-tMean)/(Math.max(cMean,tMean)*1.0));
     	}
     	else {
     		val = andInt/(orInt*1.0);
     	}
    	return val;
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
