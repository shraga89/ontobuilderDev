package ac.technion.iem.ontobuilder.matching.algorithms.line1.pivot;

import java.util.Iterator;
import java.util.List;

import org.jdom.Element;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.core.ontology.domain.GuessedDomain;
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
 * <p>Title: PivotAlgorithm</p>
 * Extends a {@link TermValueAlgorithm}
 */
public abstract class PivotAlgorithm extends TermValueAlgorithm
{

    protected PivotOperator po;
    protected double[] weights;
    protected boolean combine = true;
    protected boolean enhance = true;
    protected double pivotWeight;
    protected boolean oneToOneMatch = false;

    /**
     * Constructs a default PivotAlgorithm
     */
    public PivotAlgorithm()
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
        return "Pivot Algorithm";
    }

    /**
     * Get the description
     * 
     * @return the description
     */
    public abstract String getDescription();
    
    /**
     * Get the pivot weight
     * 
     * @return the weight
     */
    public double getPivotWeight()
    {
        return pivotWeight;
    }
    
    /**
     * Checks if is one-to-one match
     * 
     * @return <code>true</code> if is one-to-one match
     */
    public boolean isOneToOneMatch()
    {
        return oneToOneMatch;
    }

    /**
     * Performs a match between the target and candidate ontologies
     * 
     * @param targetOntology the target {@link Ontology}
     * @param candidateOntology the candidate {@link Ontology}
     * @return {@link MatchInformation}
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

        MatchMatrix termMatchMM = MatchOntologyHandler.createMatchMatrix(originalTargetTerms,
            targetTerms, originalCandidateTerms, candidateTerms, termAlgorithm);
        MatchMatrix valueMatchMM = MatchOntologyHandler.createMatchMatrix(originalTargetTerms,
            targetTerms, originalCandidateTerms, candidateTerms, valueAlgorithm);
        double termMatchMatrix[][] = termMatchMM.transpose();
        double valueMatchMatrix[][] = valueMatchMM.transpose();
        double termValueMatchMatrix[][] = combineMatrices(termMatchMatrix, valueMatchMatrix);

        // combine matrixes...
        MatchMatrix combinedMatrix = combineMatrices(termMatchMM, valueMatchMM);
        PivotMatch pivotMatch = new PivotMatch(candidateOntology, targetOntology, combinedMatrix,
            po);
        pivotMatch.setWeights(weights);
        pivotMatch.setThreshold(threshold);
        pivotMatch.setUseOneToOneMatch(oneToOneMatch);
        double[][] pivotMatchMatrix = pivotMatch.match();

        double matchMatrix[][];
        if (combine)
            matchMatrix = combineMatrices(termMatchMatrix, valueMatchMatrix, termValueMatchMatrix,
                pivotMatchMatrix);
        else
            matchMatrix = pivotMatchMatrix;

        // added by haggai 6/12/03
        MatchMatrix pivotMM = new MatchMatrix(originalCandidateTerms.size(),
            originalTargetTerms.size(), originalCandidateTerms, originalTargetTerms);
        pivotMM.setMatchConfidenceMatrix(matchMatrix);
        matchInformation = buildMatchInformation(matchMatrix);
        matchInformation.setMatrix(pivotMM);
        // /end haggai
        matchInformation.setTargetOntologyTermsTotal(originalTargetTerms.size());
        matchInformation.setTargetOntology(targetOntology);
        matchInformation.setCandidateOntologyTermsTotal(originalCandidateTerms.size());
        matchInformation.setCandidateOntology(candidateOntology);
        matchInformation.setAlgorithm(this);
        return matchInformation;
    }

    /**
     * Set the pivot operator
     * 
     * @param po the {@link PivotOperator}
     */
    public void setPivotOperator(PivotOperator po)
    {
        this.po = po;
    }

    /**
     * Get the pivot operator
     * 
     * @return the {@link PivotOperator}
     */
    public PivotOperator getPivotOperator()
    {
        return po;
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
        // double[] w = new double[3];
        for (Iterator<?> i = parametersList.iterator(); i.hasNext();)
        {
            Element parameterElement = (Element) i.next();
            String name = parameterElement.getChild("name").getText();
            if (name.equals("useOneToOneMatch"))
            {
                oneToOneMatch = Boolean.valueOf(parameterElement.getChild("value").getText())
                    .booleanValue();
            }
        }
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
     * Set to enhance
     * 
     * @param <code>true</code> if is enhanced
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
     * Set the weights
     * 
     * @param weights the weights
     */
    public void setWeights(double[] weights)
    {
        this.weights = weights;
    }

    /**
     * Get the weights
     * 
     * @return the weights
     */
    public double[] getWeights()
    {
        return weights;
    }

    public abstract Algorithm makeCopy();

    /**
     * Combine the matrices
     * 
     * @param termMatchMatrix the term match matrix
     * @param valueMatchMatrix the value match matrix
     * @param termValueMatchMatrix the term value matrix
     * @param pivotMatchMatrix the pivot match matrix
     * @return a combined matrix
     */
    protected double[][] combineMatrices(double termMatchMatrix[][], double valueMatchMatrix[][],
        double termValueMatchMatrix[][], double pivotMatchMatrix[][])
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
                double pivotEffectiveness = pivotMatchMatrix[j][i];

                boolean print = false;// ((String)targetTerm.getAttributeValue("name")).equalsIgnoreCase("putime")
                                      // &&
                                      // ((String)candidateTerm.getAttributeValue("name")).equalsIgnoreCase("pkday");

                if (print)
                    System.out.println("****************************************************");
                if (print)
                    System.out.println("Comparing " + targetTerm + " vs. " + candidateTerm);

                double termWeightAux = termWeight;
                double valueWeightAux = valueWeight;
                double pivotWeightAux = pivotWeight;

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
                if (pivotEffectiveness < threshold)
                    pivotWeightAux = 0;

                /*
                 * if((termWeightAux+valueWeightAux+graphWeightAux)==0) matchMatrix[j][i]=0; else
                 * matchMatrix[j][i]=(termEffectiveness*termWeightAux +
                 * valueEffectiveness*valueWeightAux +
                 * graphEffectiveness*graphWeightAux)/(termWeightAux+valueWeightAux+graphWeightAux);
                 */

                if ((termWeight + valueWeightAux + pivotWeight) == 0)
                    matchMatrix[j][i] = 0;
                else
                    matchMatrix[j][i] = (termEffectiveness * termWeightAux + valueEffectiveness *
                        valueWeightAux + pivotEffectiveness * pivotWeightAux) /
                        (termWeight + valueWeightAux + pivotWeight);

                // Enhancing
                if (enhance && matchMatrix[j][i] < threshold &&
                    matchMatrix[j][i] < termValueMatchMatrix[j][i])
                    matchMatrix[j][i] = termValueMatchMatrix[j][i];

                if (print)
                {
                    System.out.println("\tTerm effectivity: " + termEffectiveness);
                    System.out.println("\tValue effectivity: " + valueEffectiveness);
                    System.out.println("\tGraph effectivity: " + pivotEffectiveness);
                    System.out.println("\tOverall effectivity: " + matchMatrix[j][i]);
                    System.out.println("****************************************************");
                }
            }
        }

        // if(ApplicationParameters.verbose)
        // {
        // String
        // columnNames[]={PropertiesHandler.getResourceString("ontology.match.candidate"),PropertiesHandler.getResourceString("ontology.match.target"),PropertiesHandler.getResourceString("ontology.match.effectiveness")};
        // Object matchTable[][]=new Object[targetTerms.size()*candidateTerms.size()][3];
        //
        // for(int j=0;j<candidateTerms.size();j++)
        // {
        // Term candidateTerm=(Term)candidateTerms.get(j);
        // for(int i=0;i<targetTerms.size();i++)
        // {
        // Term targetTerm=(Term)targetTerms.get(i);
        // int index=j*targetTerms.size()+i;
        // matchTable[index][0]=candidateTerm;
        // matchTable[index][1]=targetTerm;
        // matchTable[index][2]=new Double(matchMatrix[j][i]);
        // }
        // }
        //
        // System.out.println(PropertiesHandler.getResourceString("algorithm.term") + " + " +
        // PropertiesHandler.getResourceString("algorithm.value") + " + " +
        // PropertiesHandler.getResourceString("algorithm.graph"));
        // System.out.println();
        // System.out.println(StringUtilities.getJTableStringRepresentation(new JTable(new
        // PropertiesTableModel(columnNames,matchTable))));
        // }

        return matchMatrix;
    }

}
