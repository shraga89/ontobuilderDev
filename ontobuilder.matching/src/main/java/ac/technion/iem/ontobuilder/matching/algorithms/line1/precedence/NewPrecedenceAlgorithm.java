package ac.technion.iem.ontobuilder.matching.algorithms.line1.precedence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jdom2.Element;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.OntologyUtilities;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.common.Algorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.pivot.PivotAlgorithm;

/**
 * <p>Title: NewPrecedenceAlgorithm</p>
 * Extends a {@link PivotAlgorithm}
 */
public class NewPrecedenceAlgorithm extends PivotAlgorithm
{

    /**
     * Constructs a default NewPrecedenceAlgorithm
     */
    public NewPrecedenceAlgorithm()
    {
        super();
        setPivotOperator(new PrecedenceOperator());
    }

    /**
     * Get the description
     * 
     * @return the description
     */
    public String getDescription()
    {
        return "New Precedence Algorithm Build on Pivot Algorithm";
    }

    /**
     * Get the terms to match
     * 
     * @param targetOntology the target {@link Ontology}
     * @param candidateOntology the candidate {@link Ontology}
     */
    protected void getTermsToMatch(Ontology targetOntology, Ontology candidateOntology)
    {
    	if (!targetOntology.isLight())
        {
            originalTargetTerms = OntologyUtilities.getTermsOfClass(targetOntology, "input");
            originalTargetTerms = OntologyUtilities.filterTermListRemovingTermsOfClass(
                originalTargetTerms, "hidden");
            originalTargetTerms.addAll(OntologyUtilities.getTermsOfClass(targetOntology,
                "decomposition"));

        }
        else
        {
            originalTargetTerms = new ArrayList<Term>(targetOntology.getTerms(true));
        }

        if (!candidateOntology.isLight())
        {
            originalCandidateTerms = OntologyUtilities.getTermsOfClass(candidateOntology, "input");
            originalCandidateTerms = OntologyUtilities.filterTermListRemovingTermsOfClass(
                originalCandidateTerms, "hidden");
            originalCandidateTerms.addAll(OntologyUtilities.getTermsOfClass(candidateOntology,
                "decomposition"));
        }
        else
        {
            originalCandidateTerms = new ArrayList<Term>(candidateOntology.getTerms(true));
        }
        
        if (!targetOntology.isLight())
        {
        originalTargetTerms
            .addAll(OntologyUtilities.getTermsOfClass(targetOntology, "composition"));
        originalCandidateTerms.addAll(OntologyUtilities.getTermsOfClass(candidateOntology,
            "composition"));
        }
    }

    /**
     * Get the name
     * 
     * @return the name
     */
    public String getName()
    {
        return "New Precedence Match";
    }
    
    /**
     * Get the weights array
     * 
     * @return the weights array
     */
    public double[] getWeights()
    {
        return weights;
    }

    /**
     * Configure the algorithm parameters
     * 
     * @param element the {@link Element} with the parameters to configure
     */
    public void configure(Element element)
    {
        super.configure(element);
        Element parametersElement = element.getChild("parameters");
        if (parametersElement == null)
            return;
        List<?> parametersList = parametersElement.getChildren("parameter");
        double[] w = new double[3];
        for (Iterator<?> i = parametersList.iterator(); i.hasNext();)
        {
            Element parameterElement = (Element) i.next();
            String name = parameterElement.getChild("name").getText();
            if (name.equals("precedenceWeight") || name.equals("precedeWeight") ||
                name.equals("succeedWeight"))
            {
                double value = Double.parseDouble(parameterElement.getChild("value").getText());
                if (name.equals("precedeWeight"))
                    w[0] = value;
                else if (name.equals("succeedWeight"))
                    w[1] = value;
                else if (name.equals("precedenceWeight"))
                    pivotWeight = value;
            }
        }
        setWeights(w);
    }

    /**
     * Not implemented
     */
    public void updateProperties(HashMap<?, ?> properties)
    {

    }

    /**
     * Make a copy of the algorithm instance
     */
    public Algorithm makeCopy()
    {
        NewPrecedenceAlgorithm algorithm = new NewPrecedenceAlgorithm();
        algorithm.pluginName = pluginName;
        algorithm.mode = mode;
        algorithm.thesaurus = thesaurus;
        algorithm.termPreprocessor = termPreprocessor;
        algorithm.threshold = threshold;
        algorithm.effectiveness = effectiveness;
        algorithm.combine = combine;
        algorithm.enhance = enhance;
        algorithm.weights = new double[weights.length];
        for (int i = 0; i < weights.length; i++)
        	algorithm.weights[i] = weights[i];
        algorithm.pivotWeight = pivotWeight;
        return algorithm;
    }
}
