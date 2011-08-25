package ac.technion.iem.ontobuilder.matching.algorithms.line1.precedence;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.OntologyUtilities;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.core.ontology.domain.GuessedDomain;
import ac.technion.iem.ontobuilder.core.utils.StringUtilities;
import ac.technion.iem.ontobuilder.core.utils.properties.ApplicationParameters;
import ac.technion.iem.ontobuilder.core.utils.properties.PropertiesHandler;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.Algorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.AlgorithmUtilities;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.term.TermAlgorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.term.TermValueAlgorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.term.ValueAlgorithm;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.match.MatchOntologyHandler;
import ac.technion.iem.ontobuilder.matching.meta.match.MatchMatrix;

/**
 * <p>Title: PrecedenceAlgorithm</p>
 * Extends a {@link TermValueAlgorithm}
 */
public class PrecedenceAlgorithm extends TermValueAlgorithm
{
    protected double precedeWeight;
    protected double succeedWeight;
    protected double precedenceWeight;

    protected boolean combine = true;
    protected boolean enhance = true;

    /**
     * Make a copy of the algorithm instance
     */
    public Algorithm makeCopy()
    {
        PrecedenceAlgorithm algorithm = new PrecedenceAlgorithm();
        algorithm.pluginName = pluginName;
        algorithm.mode = mode;
        algorithm.thesaurus = thesaurus;
        algorithm.termPreprocessor = termPreprocessor;
        algorithm.threshold = threshold;
        algorithm.effectiveness = effectiveness;
        algorithm.combine = combine;
        algorithm.enhance = enhance;
        algorithm.precedeWeight = precedeWeight;
        algorithm.succeedWeight = succeedWeight;
        algorithm.precedenceWeight = precedenceWeight;
        return algorithm;
    }

    /**
     * Constructs a default PrecedenceAlgorithm
     */
    public PrecedenceAlgorithm()
    {
        super();
    }

    /**
     * Set the precede weight
     * 
     * @param precedeWeight the precede weight
     */
    public void setPrecedenceWeight(double precedenceWeight)
    {
        this.precedenceWeight = precedenceWeight;
    }
    
    /**
     * Set the precede weight
     * 
     * @param precedeWeight the precede weight
     */
    public void setPrecedeWeight(double precedeWeight)
    {
        this.precedeWeight = precedeWeight;
    }

    /**
     * Get the precede weight
     * 
     * @return the precede weight
     */
    public double getPrecedeWeight()
    {
        return precedeWeight;
    }
    
    /**
     * Get the precedence Weight
     * 
     * @return the precedence Weight
     */
    public double getPrecedenceWeight()
    {
        return precedenceWeight;
    }

    /**
     * Set the succeed weight
     * 
     * @param succeedWeight the succeed weight
     */
    public void setSucceedWeight(double succeedWeight)
    {
        this.succeedWeight = succeedWeight;
    }

    /**
     * Get the succeed weight
     * 
     * @return the succeed weight
     */
    public double getSucceedWeight()
    {
        return succeedWeight;
    }

    /**
     * Set to combine the matrices
     * 
     * @param b <code>true</code> if is combined
     */
    public void setCombine(boolean b)
    {
        combine = b;
    }

    /**
     * Check whether is combined
     * 
     * @return <code>true</code> if is combined
     */
    public boolean isCombine()
    {
        return combine;
    }
    
    /**
     * Get the name
     * 
     * @return the name
     */
    public String getName()
    {
        return PropertiesHandler.getResourceString("algorithm.precedence");
    }

    /**
     * Get the description
     * 
     * @return the description
     */
    public String getDescription()
    {
        return PropertiesHandler.getResourceString("algorithm.precedence.description");
    }

    /**
     * Set to enhance
     * 
     * @param b <code>true</code> if is enhanced
     */
    public void setEnhance(boolean b)
    {
        enhance = b;
    }

    /**
     * Check whether is enhanced
     * 
     * @return <code>true</code> if is enhanced
     */
    public boolean isEnhance()
    {
        return enhance;
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
        for (Iterator<?> i = parametersList.iterator(); i.hasNext();)
        {
            Element parameterElement = (Element) i.next();
            String name = parameterElement.getChild("name").getText();
            if (name.equals("precedenceWeight") || name.equals("precedeWeight") ||
                name.equals("succeedWeight"))
            {
                double value = Double.parseDouble(parameterElement.getChild("value").getText());
                if (name.equals("precedeWeight"))
                    precedeWeight = value;
                else if (name.equals("succeedWeight"))
                    succeedWeight = value;
                else if (name.equals("precedenceWeight"))
                    precedenceWeight = value;
            }
        }
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
                "composition"));

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
                "composition"));
        }
        else
        {
            originalCandidateTerms = new ArrayList<Term>(candidateOntology.getTerms(true));
        }
    }

    /**
     * Performs a match between the target and candidate ontologies
     * 
     * @param targetOntology the target {@link Ontology}
     * @param candidateOntology the candidate {@link Ontology}
     */
    public MatchInformation match(Ontology targetOntology, Ontology candidateOntology)
    {
        if (termAlgorithm == null)
        {
            termAlgorithm = (TermAlgorithm) AlgorithmUtilities.getAlgorithm(
                PropertiesHandler.getResourceString("algorithm.term")).makeCopy();
            if (termAlgorithm == null)
                return null;
        }
        if (valueAlgorithm == null)
        {
            valueAlgorithm = (ValueAlgorithm) AlgorithmUtilities.getAlgorithm(
                PropertiesHandler.getResourceString("algorithm.value")).makeCopy();
            if (valueAlgorithm == null)
                return null;
        }

        // Get Terms
        getTermsToMatch(targetOntology, candidateOntology);

        // Preprocess
        preprocess();

        // old version
        // double
        // termMatchMatrix[][]=OntologyUtilities.getMatchMatrix(originalTargetTerms,targetTerms,originalCandidateTerms,candidateTerms,termAlgorithm);
        // double
        // valueMatchMatrix[][]=OntologyUtilities.getMatchMatrix(originalTargetTerms,targetTerms,originalCandidateTerms,candidateTerms,valueAlgorithm);
        // double termValueMatchMatrix[][]=combineMatrices(termMatchMatrix,valueMatchMatrix);
        // added by haggai 6/12/03
        MatchMatrix termMatchMM = MatchOntologyHandler.createMatchMatrix(originalTargetTerms,
            targetTerms, originalCandidateTerms, candidateTerms, termAlgorithm);
        MatchMatrix valueMatchMM = MatchOntologyHandler.createMatchMatrix(originalTargetTerms,
            targetTerms, originalCandidateTerms, candidateTerms, valueAlgorithm);
        // ***end haggai
        // new version - haggai 6/12/03
        double termMatchMatrix[][] = termMatchMM.transpose();/*
                                                              * OntologyUtilities.getMatchMatrix(
                                                              * originalTargetTerms
                                                              * ,targetTerms,originalCandidateTerms
                                                              * ,candidateTerms,termAlgorithm);
                                                              */
        double valueMatchMatrix[][] = valueMatchMM.transpose();/*
                                                                * OntologyUtilities.getMatchMatrix(
                                                                * originalTargetTerms
                                                                * ,targetTerms,originalCandidateTerms
                                                                * ,candidateTerms,valueAlgorithm);
                                                                */
        // end new version
        double termValueMatchMatrix[][] = combineMatrices(termMatchMatrix, valueMatchMatrix);

        PrecedenceMatch precedenceMatch = new PrecedenceMatch(termValueMatchMatrix,
            originalTargetTerms, originalCandidateTerms);
        precedenceMatch.setPrecedeWeight(precedeWeight);
        precedenceMatch.setSucceedWeight(succeedWeight);
        precedenceMatch.setThreshold(threshold);
        double precedenceMatchMatrix[][] = precedenceMatch.match();

        double matchMatrix[][];

        if (combine)
            matchMatrix = combineMatrices(termMatchMatrix, valueMatchMatrix, termValueMatchMatrix,
                precedenceMatchMatrix);
        else
            matchMatrix = precedenceMatchMatrix;

        // added by haggai 6/12/03
        MatchMatrix precrdenceMM = new MatchMatrix(originalCandidateTerms.size(),
            originalTargetTerms.size(), originalCandidateTerms, originalTargetTerms);
        precrdenceMM.setMatchConfidenceMatrix(matchMatrix);
        matchInformation = buildMatchInformation(matchMatrix);
        matchInformation.setMatrix(precrdenceMM);
        // /end haggai

        matchInformation.setTargetOntologyTermsTotal(originalTargetTerms.size());
        matchInformation.setTargetOntology(targetOntology);
        matchInformation.setCandidateOntologyTermsTotal(originalCandidateTerms.size());
        matchInformation.setCandidateOntology(candidateOntology);
        matchInformation.setAlgorithm(this);
        return matchInformation;
    }

    /**
     * Combine the matrices
     * 
     * @param termMatchMatrix the term match matrix
     * @param valueMatchMatrix the value match matrix
     * @param termValueMatchMatrix the term value matrix
     * @param precedenceMatchMatrix the precedence match matrix
     * @return a combined matrix
     */
    protected double[][] combineMatrices(double termMatchMatrix[][], double valueMatchMatrix[][],
        double termValueMatchMatrix[][], double precedenceMatchMatrix[][])
    {
        double matchMatrix[][] = new double[candidateTerms.size()][targetTerms.size()];

        for (int j = 0; j < candidateTerms.size(); j++)
        {
            Term candidateTerm = (Term) candidateTerms.get(j);
            for (int i = 0; i < targetTerms.size(); i++)
            {
                Term targetTerm = (Term) targetTerms.get(i);

                double termEffectiveness = termMatchMatrix[j][i];
                double valueEffectiveness = valueMatchMatrix[j][i];
                double precedenceEffectiveness = precedenceMatchMatrix[j][i];

                boolean print = false;// ((String)targetTerm.getAttributeValue("name")).equalsIgnoreCase("putime")
                                      // &&
                                      // ((String)candidateTerm.getAttributeValue("name")).equalsIgnoreCase("pkday");

                if (print)
                    System.out.println("****************************************************");
                if (print)
                    System.out.println("Comparing " + targetTerm + " vs. " + candidateTerm);

                double termWeightAux = termWeight;
                double valueWeightAux = valueWeight;
                double precedenceWeightAux = precedenceWeight;

                if (targetTerm.getDomain().getEntriesCount() == 0 &&
                    candidateTerm.getDomain().getEntriesCount() == 0) // this means that the value
                                                                      // for valueEffectiveness is
                                                                      // just domain similarity
                {
                    if (valueEffectiveness > 0)
                        termEffectiveness = valueEffectiveness + (1 - valueEffectiveness) *
                            termEffectiveness;
                    else if (termEffectiveness > 0)
                        termEffectiveness -= GuessedDomain.DOMAIN_PENALTY;
                    valueWeightAux = 0;
                }

                if (termEffectiveness < threshold)
                    termWeightAux = 0;
                if (valueEffectiveness < threshold)
                    valueWeightAux = 0;
                if (precedenceEffectiveness < threshold)
                    precedenceWeightAux = 0;

                /*
                 * if((termWeightAux+valueWeightAux+precedenceWeightAux)==0) matchMatrix[j][i]=0;
                 * else matchMatrix[j][i]=(termEffectiveness*termWeightAux +
                 * valueEffectiveness*valueWeightAux +
                 * precedenceEffectiveness*precedenceWeightAux)/(
                 * termWeightAux+valueWeightAux+precedenceWeightAux);
                 */

                if ((termWeight + valueWeightAux + precedenceWeight) == 0)
                    matchMatrix[j][i] = 0;
                else
                    matchMatrix[j][i] = (termEffectiveness * termWeightAux + valueEffectiveness *
                        valueWeightAux + precedenceEffectiveness * precedenceWeightAux) /
                        (termWeight + valueWeightAux + precedenceWeight);

                // Enhancing
                if (enhance && matchMatrix[j][i] < threshold &&
                    matchMatrix[j][i] < termValueMatchMatrix[j][i])
                    matchMatrix[j][i] = termValueMatchMatrix[j][i];

                if (print)
                {
                    System.out.println("\tTerm effectivity: " + termEffectiveness);
                    System.out.println("\tValue effectivity: " + valueEffectiveness);
                    System.out.println("\tPrecedence effectivity: " + precedenceEffectiveness);
                    System.out.println("\tOverall effectivity: " + matchMatrix[j][i]);
                    System.out.println("****************************************************");
                }
            }
        }

        if (ApplicationParameters.verbose)
        {
            String columnNames[] =
            {
                PropertiesHandler.getResourceString("ontology.match.candidate"),
                PropertiesHandler.getResourceString("ontology.match.target"),
                PropertiesHandler.getResourceString("ontology.match.effectiveness")
            };
            Object matchTable[][] = new Object[targetTerms.size() * candidateTerms.size()][3];

            for (int j = 0; j < candidateTerms.size(); j++)
            {
                Term candidateTerm = (Term) candidateTerms.get(j);
                for (int i = 0; i < targetTerms.size(); i++)
                {
                    Term targetTerm = (Term) targetTerms.get(i);
                    int index = j * targetTerms.size() + i;
                    matchTable[index][0] = candidateTerm;
                    matchTable[index][1] = targetTerm;
                    matchTable[index][2] = new Double(matchMatrix[j][i]);
                }
            }

            System.out.println(PropertiesHandler.getResourceString("algorithm.term") + " + " +
                PropertiesHandler.getResourceString("algorithm.value") + " + " +
                PropertiesHandler.getResourceString("algorithm.precedence"));
            System.out.println();
            System.out.println(StringUtilities.getTableStringRepresentation(columnNames, 5, matchTable));
        }

        return matchMatrix;
    }
}