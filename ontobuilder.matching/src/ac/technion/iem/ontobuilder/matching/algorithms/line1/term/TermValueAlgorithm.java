package ac.technion.iem.ontobuilder.matching.algorithms.line1.term;

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
import ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.AbstractAlgorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.Algorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.AlgorithmException;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.AlgorithmUtilities;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.match.MatchOntologyHandler;
import ac.technion.iem.ontobuilder.matching.meta.match.MatchMatrix;

/**
 * <p>Title: TermValueAlgorithm</p>
 * Extends a {@link AbstractAlgorithm}
 */
public class TermValueAlgorithm extends AbstractAlgorithm
{
    protected TermAlgorithm termAlgorithm;
    protected ValueAlgorithm valueAlgorithm;

    protected double termWeight;
    protected double valueWeight;

    /**
     * Make a copy of the algorithm instance
     */
    public Algorithm makeCopy()
    {
        TermValueAlgorithm algorithm = new TermValueAlgorithm();
        algorithm.pluginName = pluginName;
        algorithm.mode = mode;
        algorithm.thesaurus = thesaurus;
        algorithm.termPreprocessor = termPreprocessor;
        algorithm.threshold = threshold;
        algorithm.effectiveness = effectiveness;
        algorithm.termWeight = termWeight;
        algorithm.valueWeight = valueWeight;
        return algorithm;
    }

    /**
     * Constructs a default TermValueAlgorithm
     */
    public TermValueAlgorithm()
    {
        super();
    }

    /**
     * Get the name
     * 
     * @return the name
     */
    public String getName()
    {
        return PropertiesHandler.getResourceString("algorithm.termValue");
    }

    /**
     * Get the description
     * 
     * @return the description
     */
    public String getDescription()
    {
        return PropertiesHandler.getResourceString("algorithm.termValue.description");
    }

    /**
     * Get the term weight
     * 
     * @return the weight
     */
    public double getTermWeight()
    {
        return termWeight;
    }

    /**
     * Set the term weight
     * 
     * @param the weight
     */
    public void setTermWeight(double _termWeight)
    {
        termWeight = _termWeight;
    }
    
    /**
     * Get the value weight
     * 
     * @return the weight
     */
    public double getValueWeight()
    {
        return valueWeight;
    }

    /**
     * Set the term weight
     * 
     * @param the weight
     */
    public void setValueWeight(double _valueWeight)
    {
        valueWeight = _valueWeight;
    }
    
    /**
     * Configure the algorithm parameters when user changes one of the values of the JTable
     * 
     * @param element the {@link Element} with the parameters to configure
     */
    public void configure(Element element)
    {
        Element parametersElement = element.getChild("parameters");
        if (parametersElement == null)
            return;
        List<?> parametersList = parametersElement.getChildren("parameter");
        for (Iterator<?> i = parametersList.iterator(); i.hasNext();)
        {
            Element parameterElement = (Element) i.next();
            String name = parameterElement.getChild("name").getText();
            if (name.equals("termWeight") || name.equals("valueWeight"))
            {
                double value = Double.parseDouble(parameterElement.getChild("value").getText());
                if (name.equals("termWeight"))
                    termWeight = value;
                else if (name.equals("valueWeight"))
                    valueWeight = value;
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

    }

    /**
     * Preprocess the target and candidate terms
     */
    protected void preprocess()
    {
        targetTerms.clear();
        candidateTerms.clear();
        termPreprocessor.preprocess(targetTerms, candidateTerms, originalTargetTerms,
            originalCandidateTerms);
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

        try
        {

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
                                                                  * ,targetTerms,
                                                                  * originalCandidateTerms
                                                                  * ,candidateTerms,termAlgorithm);
                                                                  */
            double valueMatchMatrix[][] = valueMatchMM.transpose();/*
                                                                    * OntologyUtilities.getMatchMatrix
                                                                    * (
                                                                    * originalTargetTerms,targetTerms
                                                                    * ,originalCandidateTerms,
                                                                    * candidateTerms
                                                                    * ,valueAlgorithm);
                                                                    */
            // end new version
            double matchMatrix[][] = combineMatrices(termMatchMatrix, valueMatchMatrix);
            /* gabi - ceating the matchTable and adding it to MatchInformation - 5/2/2003 */
            Object matchTable[][] = new Object[targetTerms.size() * candidateTerms.size()][3];
            // added by haggai - 6/12/03
            MatchMatrix termAndValueMatchMatrix = new MatchMatrix(originalCandidateTerms.size(),
                originalTargetTerms.size(), originalCandidateTerms, originalTargetTerms);
            // end haggai
            for (int j = 0; j < candidateTerms.size(); j++)
            {
                Term candidateTerm = (Term) originalCandidateTerms.get(j);
                for (int i = 0; i < targetTerms.size(); i++)
                {
                    Term targetTerm = (Term) originalTargetTerms.get(i);
                    int index = j * targetTerms.size() + i;
                    matchTable[index][0] = targetTerm;
                    matchTable[index][1] = candidateTerm;
                    matchTable[index][2] = new Double(matchMatrix[j][i]);
                    // added by haggai 6/12/3
                    termAndValueMatchMatrix.setMatchConfidence(candidateTerm, targetTerm,
                        matchMatrix[j][i]);
                    // end
                }
            }
            // end gabi

            matchInformation = buildMatchInformation(matchMatrix);

            // haggai
            matchInformation.setMatrix(termAndValueMatchMatrix);
            // gabi
            matchInformation.setMatchMatrix(matchMatrix);
            // matchInformation.setMatchTable1(matchTable);
            matchInformation.setOriginalTargetTerms(originalTargetTerms);
            matchInformation.setOriginalCandidateTerms(originalCandidateTerms);

            // end gabi

            matchInformation.setTargetOntologyTermsTotal(originalTargetTerms.size());
            matchInformation.setTargetOntology(targetOntology);
            matchInformation.setCandidateOntologyTermsTotal(originalCandidateTerms.size());
            matchInformation.setCandidateOntology(candidateOntology);
            matchInformation.setAlgorithm(this);
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
        return matchInformation;
    }

    /**
     * Combine the matrices
     * 
     * @param termMatchMatrix the term match matrix
     * @param valueMatchMatrix the value match matrix
     * @return a combined matrix
     */
    protected double[][] combineMatrices(double termMatchMatrix[][], double valueMatchMatrix[][])
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

                boolean print = false;// ((String)targetTerm.getAttributeValue("name")).equalsIgnoreCase("putime")
                                      // &&
                                      // ((String)candidateTerm.getAttributeValue("name")).equalsIgnoreCase("pkday");

                if (print)
                    System.out.println("****************************************************");
                if (print)
                    System.out.println("Comparing " + targetTerm + " vs. " + candidateTerm);

                double termWeightAux = termWeight;
                double valueWeightAux = valueWeight;

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

                if ((termWeightAux + valueWeightAux) == 0)
                    matchMatrix[j][i] = 0;
                else
                    matchMatrix[j][i] = (termEffectiveness * termWeightAux + valueEffectiveness *
                        valueWeightAux) /
                        (termWeightAux + valueWeightAux);

                if (print)
                {
                    System.out.println("\tTerm effectivity: " + termEffectiveness);
                    System.out.println("\tValue effectivity: " + valueEffectiveness);
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
                PropertiesHandler.getResourceString("algorithm.value"));
            System.out.println();
            System.out.println(StringUtilities.getTableStringRepresentation(columnNames, 2, matchTable));
        }

        return matchMatrix;
    }

    /**
     * Combine the matrices
     * 
     * @param termMatchMatrix the term {@link MatchMatrix}
     * @param valueMatchMatrix the value {@link MatchMatrix}
     * @return a combined matrix 
     */
    protected MatchMatrix combineMatrices(MatchMatrix termMatchMatrix, MatchMatrix valueMatchMatrix)
    {
        MatchMatrix matchMatrix = new MatchMatrix();
        matchMatrix.copyWithEmptyMatrix(termMatchMatrix);

        for (int j = 0; j < originalCandidateTerms.size(); j++)
        {
            Term candidateTerm = (Term) originalCandidateTerms.get(j);
            for (int i = 0; i < originalTargetTerms.size(); i++)
            {
                Term targetTerm = (Term) originalTargetTerms.get(i);

                double termEffectiveness = termMatchMatrix.getMatchConfidence(candidateTerm,
                    targetTerm);
                double valueEffectiveness = valueMatchMatrix.getMatchConfidence(candidateTerm,
                    targetTerm);

                boolean print = false;// ((String)targetTerm.getAttributeValue("name")).equalsIgnoreCase("putime")
                                      // &&
                                      // ((String)candidateTerm.getAttributeValue("name")).equalsIgnoreCase("pkday");

                if (print)
                    System.out.println("****************************************************");
                if (print)
                    System.out.println("Comparing " + targetTerm + " vs. " + candidateTerm);

                double termWeightAux = termWeight;
                double valueWeightAux = valueWeight;

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

                if ((termWeightAux + valueWeightAux) == 0)
                    matchMatrix.setMatchConfidence(candidateTerm, targetTerm, 0);
                else
                    matchMatrix.setMatchConfidence(candidateTerm, targetTerm, (termEffectiveness *
                        termWeightAux + valueEffectiveness * valueWeightAux) /
                        (termWeightAux + valueWeightAux));

                if (print)
                {
                    System.out.println("\tTerm effectivity: " + termEffectiveness);
                    System.out.println("\tValue effectivity: " + valueEffectiveness);
                    System.out.println("\tOverall effectivity: " +
                        matchMatrix.getMatchConfidence(candidateTerm, targetTerm));
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

            for (int j = 0; j < originalCandidateTerms.size(); j++)
            {
                Term candidateTerm = (Term) originalCandidateTerms.get(j);
                for (int i = 0; i < originalTargetTerms.size(); i++)
                {
                    Term targetTerm = (Term) originalTargetTerms.get(i);
                    int index = j * targetTerms.size() + i;
                    matchTable[index][0] = candidateTerm;
                    matchTable[index][1] = targetTerm;
                    matchTable[index][2] = new Double(matchMatrix.getMatchConfidence(candidateTerm,
                        targetTerm));
                }
            }

            System.out.println(PropertiesHandler.getResourceString("algorithm.term") + " + " +
                PropertiesHandler.getResourceString("algorithm.value"));
            System.out.println();
            System.out.println(StringUtilities.getTableStringRepresentation(columnNames, 2, matchTable));
        }

        return matchMatrix;
    }

    /**
     * Configure the algorithm weights
     * 
     * @param weights the weights
     * @throws AlgorithmException 
     */
    public void configureAlgorithmsWeights(double[] weights) throws AlgorithmException
    {
        termWeight = weights[0];
        valueWeight = weights[3];
    }
}
