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
import ac.technion.iem.ontobuilder.core.utils.graphs.Graph;
import ac.technion.iem.ontobuilder.core.utils.properties.ApplicationParameters;
import ac.technion.iem.ontobuilder.core.utils.properties.PropertiesHandler;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.Algorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.AlgorithmException;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.AlgorithmUtilities;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.precedence.PrecedenceAlgorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.precedence.PrecedenceMatch;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.match.MatchOntologyHandler;
import ac.technion.iem.ontobuilder.matching.meta.match.MatchMatrix;

/**
 * <p>Title: CombinedAlgorithm</p>
 * Extends a {@link TermValueAlgorithm}
 */
public class CombinedAlgorithm extends TermValueAlgorithm
{
    protected double graphWeight;
    protected double precedenceWeight;

    protected GraphAlgorithm graphAlgorithm;
    protected PrecedenceAlgorithm precedenceAlgorithm;

    protected boolean enhance = true;

    /**
     * Make a copy of the algorithm instance
     */
    public Algorithm makeCopy()
    {
        CombinedAlgorithm algorithm = new CombinedAlgorithm();
        algorithm.pluginName = pluginName;
        algorithm.mode = mode;
        algorithm.thesaurus = thesaurus;
        algorithm.termPreprocessor = termPreprocessor;
        algorithm.threshold = threshold;
        algorithm.effectiveness = effectiveness;
        algorithm.enhance = enhance;
        return algorithm;
    }

    /**
     * Constructs a default CombinedAlgorithm
     */
    public CombinedAlgorithm()
    {
        super();
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
     * Get the name
     * 
     * @return the name
     */
    public String getName()
    {
        return PropertiesHandler.getResourceString("algorithm.combined");
    }

    /**
     * Get the description
     * 
     * @return the description
     */
    public String getDescription()
    {
        return PropertiesHandler.getResourceString("algorithm.combined.description");
    }
    
    /**
     * Get the graph weight
     * 
     * @return the weight
     */
    public double getGraphWeight()
    {
        return graphWeight;
    }
    
    /**
     * Set the graph weight
     * 
     * @param the weight
     */
    public void setGraphWeight(double _graphWeight)
    {
        graphWeight = _graphWeight;
    }
    
    /**
     * Get the precedence weight
     * 
     * @return the weight
     */
    public double getPrecedenceWeight()
    {
        return precedenceWeight;
    }

    /**
     * Set the precedence weight
     * 
     * @param the weight
     */
    public void setPrecedenceWeight(double _precedenceWeight)
    {
        precedenceWeight = _precedenceWeight;
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
            if (name.equals("termWeight") || name.equals("valueWeight") ||
                name.equals("graphWeight") || name.equals("precedenceWeight"))
            {
                double value = Double.parseDouble(parameterElement.getChild("value").getText());
                if (name.equals("termWeight"))
                    termWeight = value;
                else if (name.equals("valueWeight"))
                    valueWeight = value;
                else if (name.equals("graphWeight"))
                    graphWeight = value;
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
        if (graphAlgorithm == null)
        {
            graphAlgorithm = (GraphAlgorithm) AlgorithmUtilities.getAlgorithm(
                PropertiesHandler.getResourceString("algorithm.graph")).makeCopy();
            if (graphAlgorithm == null)
                return null;
        }
        if (precedenceAlgorithm == null)
        {
            precedenceAlgorithm = (PrecedenceAlgorithm) AlgorithmUtilities.getAlgorithm(
                PropertiesHandler.getResourceString("algorithm.precedence")).makeCopy();
            if (precedenceAlgorithm == null)
                return null;
        }

        // Get Terms
        getTermsToMatch(targetOntology, candidateOntology);

        // Preprocess
        preprocess();
        try
        {

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

            double termValueMatchMatrix[][] = combineMatrices(termMatchMatrix, valueMatchMatrix);

            Graph targetGraph = targetOntology.getGraph();
            Graph candidateGraph = candidateOntology.getGraph();
            GraphMatch graphMatch = new GraphMatch(termValueMatchMatrix, originalTargetTerms,
                originalCandidateTerms, targetGraph, candidateGraph);
            graphMatch.setParentsWeight(graphAlgorithm.getParentsWeight());
            graphMatch.setSiblingsWeight(graphAlgorithm.getSiblingsWeight());
            graphMatch.setThreshold(threshold);
            double graphMatchMatrix[][] = graphMatch.match();

            PrecedenceMatch precedenceMatch = new PrecedenceMatch(termValueMatchMatrix,
                originalTargetTerms, originalCandidateTerms);
            precedenceMatch.setPrecedeWeight(precedenceAlgorithm.getPrecedeWeight());
            precedenceMatch.setSucceedWeight(precedenceAlgorithm.getSucceedWeight());
            precedenceMatch.setThreshold(threshold);
            double precedenceMatchMatrix[][] = precedenceMatch.match();

            double matchMatrix[][] = combineMatrices(termMatchMatrix, valueMatchMatrix,
                termValueMatchMatrix, graphMatchMatrix, precedenceMatchMatrix);

            // added by haggai 6/12/03
            MatchMatrix combinedMM = new MatchMatrix(originalCandidateTerms.size(),
                originalTargetTerms.size(), originalCandidateTerms, originalTargetTerms);
            combinedMM.setMatchConfidenceMatrix(matchMatrix);
            matchInformation = buildMatchInformation(matchMatrix);
            matchInformation.setMatrix(combinedMM);
            // /end haggai

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

    // public MatchInformation match(Ontology targetOntology,Ontology candidateOntology)
    // {
    // if(termAlgorithm==null)
    // {
    // termAlgorithm=(TermAlgorithm)AlgorithmUtilities.getAlgorithm(PropertiesHandler.getResourceString("algorithm.term")).makeCopy();
    // if(termAlgorithm==null) return null;
    // }
    // if(valueAlgorithm==null)
    // {
    // valueAlgorithm=(ValueAlgorithm)AlgorithmUtilities.getAlgorithm(PropertiesHandler.getResourceString("algorithm.value")).makeCopy();
    // if(valueAlgorithm==null) return null;
    // }
    //
    // // Get Terms
    // getTermsToMatch(targetOntology,candidateOntology);
    //
    // // Preprocess
    // preprocess();
    //
    // try{
    //
    // //added by haggai 6/12/03
    // MatchMatrix termMatchMM =
    // OntologyUtilities.createMatchMatrix(originalTargetTerms,targetTerms,originalCandidateTerms,candidateTerms,termAlgorithm);
    // MatchMatrix valueMatchMM =
    // OntologyUtilities.createMatchMatrix(originalTargetTerms,targetTerms,originalCandidateTerms,candidateTerms,valueAlgorithm);
    // //***end haggai
    // //new version - haggai 6/12/03
    // double termMatchMatrix[][]=
    // termMatchMM.transpose();/*OntologyUtilities.getMatchMatrix(originalTargetTerms,targetTerms,originalCandidateTerms,candidateTerms,termAlgorithm);*/
    // double valueMatchMatrix[][]=
    // valueMatchMM.transpose();/*OntologyUtilities.getMatchMatrix(originalTargetTerms,targetTerms,originalCandidateTerms,candidateTerms,valueAlgorithm);*/
    // //end new version
    // double matchMatrix[][]=combineMatrices(termMatchMatrix,valueMatchMatrix);
    // String
    // columnNames[]={PropertiesHandler.getResourceString("ontology.match.candidate"),PropertiesHandler.getResourceString("ontology.match.target"),PropertiesHandler.getResourceString("ontology.match.effectiveness")};
    // /*gabi - ceating the matchTable and adding it to MatchInformation - 5/2/2003*/
    // Object matchTable[][]=new Object[targetTerms.size()*candidateTerms.size()][3];
    // //added by haggai - 6/12/03
    // MatchMatrix termAndValueMatchMatrix = new
    // MatchMatrix(originalCandidateTerms.size(),originalTargetTerms.size(),originalCandidateTerms,originalTargetTerms);
    // //end haggai
    // for(int j=0;j<candidateTerms.size();j++)
    // {
    // Term candidateTerm=(Term)originalCandidateTerms.get(j);
    // for(int i=0;i<targetTerms.size();i++)
    // {
    // Term targetTerm=(Term)originalTargetTerms.get(i);
    // int index=j*targetTerms.size()+i;
    // matchTable[index][0]=targetTerm;
    // matchTable[index][1]=candidateTerm;
    // matchTable[index][2]=new Double(matchMatrix[j][i]);
    // //added by haggai 6/12/3
    // termAndValueMatchMatrix.setMatchConfidence(candidateTerm,targetTerm,matchMatrix[j][i]);
    // //end
    // }
    // }
    // //end gabi
    //
    //
    //
    // matchInformation=buildMatchInformation(matchMatrix);
    //
    // //haggai
    // matchInformation.setMatrix(termAndValueMatchMatrix);
    // //gabi
    // matchInformation.setMatchMatrix(matchMatrix);
    // // matchInformation.setMatchTable1(matchTable);
    // matchInformation.setOriginalTargetTerms(originalTargetTerms);
    // matchInformation.setOriginalCandidateTerms(originalCandidateTerms);
    //
    // //end gabi
    //
    //
    // matchInformation.setTargetOntologyTermsTotal(originalTargetTerms.size());
    // matchInformation.setTargetOntology(targetOntology);
    // matchInformation.setCandidateOntologyTermsTotal(originalCandidateTerms.size());
    // matchInformation.setCandidateOntology(candidateOntology);
    // matchInformation.setAlgorithm(this);
    // }catch(Throwable e){
    // e.printStackTrace();
    // }
    // return matchInformation;
    // }

    /**
     * Combine the matrices
     * 
     * @param termMatchMatrix the term match matrix
     * @param valueMatchMatrix the value match matrix
     * @param termValueMatchMatrix the term value matrix
     * @param pivotMatchMatrix the pivot match matrix
     * @param precedenceMatchMatrix the precedence match matrix
     * @return a combined matrix
     */
    protected double[][] combineMatrices(double termMatchMatrix[][], double valueMatchMatrix[][],
        double termValueMatchMatrix[][], double graphMatchMatrix[][],
        double precedenceMatchMatrix[][])
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
                double graphEffectiveness = graphMatchMatrix[j][i];
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
                double graphWeightAux = graphWeight;
                double precedenceWeightAux = precedenceWeight;

                if (targetTerm.getDomain().getEntriesCount() == 0 &&
                    candidateTerm.getDomain().getEntriesCount() == 0) // this means that the value
                                                                      // for valueEffectiveness is
                                                                      // just domain similarity
                {
                    if (valueEffectiveness > 0)
                        termEffectiveness = valueEffectiveness + (1 - valueEffectiveness) *
                            termEffectiveness;
                    else
                        termEffectiveness -= GuessedDomain.DOMAIN_PENALTY;
                    valueWeightAux = 0;
                }

                if (termEffectiveness < threshold)
                    termWeightAux = 0;
                if (valueEffectiveness < threshold)
                    valueWeightAux = 0;
                if (graphEffectiveness < threshold)
                    graphWeightAux = 0;
                if (precedenceEffectiveness < threshold)
                    precedenceWeightAux = 0;

                /*
                 * if((termWeightAux+valueWeightAux+graphWeight+precedenceWeightAux)==0)
                 * matchMatrix[j][i]=0; else matchMatrix[j][i]=(termEffectiveness*termWeightAux +
                 * valueEffectiveness*valueWeightAux + graphEffectiveness*graphWeightAux +
                 * precedenceEffectiveness
                 * *precedenceWeightAux)/(termWeightAux+valueWeightAux+graphWeightAux
                 * +precedenceWeightAux);
                 */

                if ((termWeight + valueWeight + precedenceWeight) == 0)
                    matchMatrix[j][i] = 0;
                else
                    matchMatrix[j][i] = (termEffectiveness * termWeightAux + valueEffectiveness *
                        valueWeightAux + graphEffectiveness * graphWeightAux + precedenceEffectiveness *
                        precedenceWeightAux) /
                        (termWeight + valueWeight + graphWeight + precedenceWeight);

                // Enhancing
                if (enhance && matchMatrix[j][i] < threshold &&
                    matchMatrix[j][i] < termValueMatchMatrix[j][i])
                    matchMatrix[j][i] = termValueMatchMatrix[j][i];

                if (print)
                {
                    System.out.println("\tTerm effectivity: " + termEffectiveness);
                    System.out.println("\tValue effectivity: " + valueEffectiveness);
                    System.out.println("\tGraph effectivity: " + graphEffectiveness);
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
                PropertiesHandler.getResourceString("algorithm.graph") + " + " +
                PropertiesHandler.getResourceString("algorithm.precedence"));
            System.out.println();
            System.out.println(StringUtilities.getTableStringRepresentation(columnNames, 4, matchTable));
        }

        return matchMatrix;
    }

    // weights[0] := Term weight
    // weights[1] := value weight
    // weights[2] := precedence weight
    // weights[3] := graph weight
    /**
     * Configure the algorithm weights
     * 
     * @param weights the weights
     */
    public void configureAlgorithmsWeights(double[] weights) throws AlgorithmException
    {
        configureAlgorithmsWeights(weights, true);
    }

    // weights[0] := Term weight
    // weights[1] := value weight
    // weights[2] := precedence weight
    // weights[3] := graph weight
    /**
     * Configure the algorithm weights
     * 
     * @param weights the weights
     * @param checkWeights <code>true</code> if is to check the weights
     */
    protected void configureAlgorithmsWeights(double[] weights, boolean checkWeights)
        throws AlgorithmException
    {
        // if (checkWeights && weights[0] + weights[1] + weights[2] + weights[3] < 1.0)
        // throw new
        // AlgorithmException("combined algorithm weights sum must be 1.0,got: "+(weights[0] +
        // weights[1] + weights[2] + weights[3]));
        super.configureAlgorithmsWeights(weights);
        precedenceWeight = weights[2];
        graphWeight = weights[3];
    }

}