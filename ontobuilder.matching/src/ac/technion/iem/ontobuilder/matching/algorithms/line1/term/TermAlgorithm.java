package ac.technion.iem.ontobuilder.matching.algorithms.line1.term;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.OntologyUtilities;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.core.utils.StringUtilities;
import ac.technion.iem.ontobuilder.core.utils.properties.PropertiesHandler;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.AbstractAlgorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.Algorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.AlgorithmException;
import ac.technion.iem.ontobuilder.matching.match.MatchComparator;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.match.MatchOntologyHandler;
import ac.technion.iem.ontobuilder.matching.meta.match.MatchMatrix;

/**
 * <p>Title: TermAlgorithm</p>
 * Extends a {@link AbstractAlgorithm}
 * Implements a {@link MatchComparator}
 */
public class TermAlgorithm extends AbstractAlgorithm implements MatchComparator
{
    protected double wordLabelWeight;
    protected double stringLabelWeight;
    protected double wordNameWeight;
    protected double stringNameWeight;

    protected double maxCommonSubStringWeight;
    protected double nGramWeight;
    protected int nGram;

    /**
     * Make a copy of the algorithm instance
     */
    public Algorithm makeCopy()
    {
        TermAlgorithm algorithm = new TermAlgorithm();
        algorithm.pluginName = pluginName;
        algorithm.mode = mode;
        algorithm.thesaurus = thesaurus;
        algorithm.termPreprocessor = termPreprocessor;
        algorithm.threshold = threshold;
        algorithm.effectiveness = effectiveness;
        algorithm.wordLabelWeight = wordLabelWeight;
        algorithm.stringLabelWeight = stringLabelWeight;
        algorithm.wordNameWeight = wordNameWeight;
        algorithm.stringNameWeight = stringNameWeight;
        algorithm.maxCommonSubStringWeight = maxCommonSubStringWeight;
        algorithm.nGramWeight = nGramWeight;
        return algorithm;
    }

    /**
     * Constructs a default TermAlgorithm
     */
    public TermAlgorithm()
    {
        super();
        effectiveness = 0;
    }

    /**
     * Gets list of terms from both ontologies. If ontology is not "light", assumes it is from a
     * webform and then applies a filter to get only terms of class "input" and not class "hidden"
     * 
     * @param targetOntology the target {@link Ontology}
     * @param candidateOntology the candidate {@link Ontology}
     */
    protected void getTermsToMatch(Ontology targetOntology, Ontology candidateOntology)
    {
        // TODO change to a dynamic filter that you can choose in runtime the filters

        if (!targetOntology.isLight())
        {
            originalTargetTerms = OntologyUtilities.getTermsOfClass(targetOntology, "input");
            originalTargetTerms = OntologyUtilities.filterTermListRemovingTermsOfClass(
                originalTargetTerms, "hidden");
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
        try
        {
            // Get Terms
            getTermsToMatch(targetOntology, candidateOntology);

            // Preprocess
            preprocess();

            // changed haggai - 6/12/03
            MatchMatrix matchMatrix = MatchOntologyHandler.createMatchMatrix(originalTargetTerms,
                targetTerms, originalCandidateTerms, candidateTerms, this);
            matchInformation = buildMatchInformation(matchMatrix.transpose());
            matchInformation.setTargetOntologyTermsTotal(originalTargetTerms.size());
            matchInformation.setTargetOntology(targetOntology);
            matchInformation.setCandidateOntologyTermsTotal(originalCandidateTerms.size());
            matchInformation.setCandidateOntology(candidateOntology);
            matchInformation.setAlgorithm(this);
            // /*****
            // /added by Haggai 9/10/03
            matchInformation.setOriginalCandidateTerms(originalCandidateTerms);
            matchInformation.setOriginalTargetTerms(originalTargetTerms);
            matchInformation.setMatchMatrix(matchMatrix.transpose());
            matchInformation.setMatrix(matchMatrix);
            // /////
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
        return matchInformation;
    }

    /**
     * Get the name
     * 
     * @return the name
     */
    public String getName()
    {
        return PropertiesHandler.getResourceString("algorithm.term");
    }

    /**
     * Get the description
     * 
     * @return the description
     */
    public String getDescription()
    {
        return PropertiesHandler.getResourceString("algorithm.term.description");
    }
    
    /**
     * Get the wordLabelWeight
     * 
     * @return the wordLabelWeight
     */
    public double getWordLabelWeight()
    {
        return wordLabelWeight;
    }
    
    /**
     * Set the wordLabelWeight
     * 
     * @param wordLabelWeight the weight
     */
    public void setWordLabelWeight(double wordLabelWeight)
    {
        this.wordLabelWeight = wordLabelWeight;
    }
    /**
     * Get the stringLabelWeight
     * 
     * @return the stringLabelWeight
     */
    public double getStringLabelWeight()
    {
        return stringLabelWeight;
    }
    
    /**
     * Set the stringLabelWeight
     * 
     * @param stringLabelWeight the weight
     */
    public void setStringLabelWeight(double stringLabelWeight)
    {
        this.stringLabelWeight = stringLabelWeight;
    }

    /**
     * Get the wordNameWeight
     * 
     * @return the wordNameWeight
     */
    public double getWordNameWeight()
    {
        return wordNameWeight;
    }
    
    /**
     * Set the wordNameWeight
     * 
     * @param wordNameWeight the weight
     */
    public void setWordNameWeight(double wordNameWeight)
    {
        this.wordNameWeight = wordNameWeight;
    }
    
    /**
     * Get the stringNameWeight
     * 
     * @return the stringNameWeight
     */
    public double getStringNameWeight()
    {
        return stringNameWeight;
    }
    
    /**
     * Set the stringNameWeight
     * 
     * @param stringNameWeight the weight
     */
    public void setStringNameWeight(double stringNameWeight)
    {
        this.stringNameWeight = stringNameWeight;
    }

    /**
     * Get the maxCommonSubStringWeight
     * 
     * @return the maxCommonSubStringWeight
     */
    public double getMaxCommonSubStringWeight()
    {
        return maxCommonSubStringWeight;
    }
    
    /**
     * Set the maxCommonSubStringWeight
     * 
     * @param maxCommonSubStringWeight the weight
     */
    public void setMaxCommonSubStringWeight(double maxCommonSubStringWeight)
    {
        this.maxCommonSubStringWeight = maxCommonSubStringWeight;
    }
    
    /**
     * Get the nGramWeight
     * 
     * @return the nGramWeight
     */
    public double getNGramWeight()
    {
        return nGramWeight;
    }
    
    /**
     * Set the nGramWeight
     * 
     * @param nGramWeight the weight
     */
    public void setNGramWeight(double nGramWeight)
    {
        this.nGramWeight = nGramWeight;
    }

    /**
     * Get the nGram
     * 
     * @return the nGram
     */
    public int getNGram()
    {
        return nGram;
    }
    
    /**
     * Set the nGram
     * 
     * @param nGram the nGram
     */
    public void setNGram(int nGram)
    {
        this.nGram = nGram;
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
            if (name.equals("symmetric") || name.equals("useThesaurus") ||
                name.equals("useSoundex"))
            {
                boolean value = Boolean.valueOf(parameterElement.getChild("value").getText())
                    .booleanValue();
                if (name.equals("symmetric") && value)
                    mode += TermAlgorithmFlagsEnum.SYMMETRIC_FLAG.getValue();
                else if (name.equals("useThesaurus") && value)
                    mode += TermAlgorithmFlagsEnum.USE_THESAURUS_FLAG.getValue();
                else if (name.equals("useSoundex") && value)
                    mode += TermAlgorithmFlagsEnum.USE_SOUNDEX_FLAG.getValue();
            }
            else if (name.equals("wordLabelWeight") || name.equals("stringLabelWeight") ||
                name.equals("wordNameWeight") || name.equals("stringNameWeight") ||
                name.equals("maxCommonSubStringWeight") || name.equals("nGramWeight"))
            {
                double value = Double.parseDouble(parameterElement.getChild("value").getText());
                if (name.equals("wordLabelWeight"))
                    wordLabelWeight = value;
                else if (name.equals("stringLabelWeight"))
                    stringLabelWeight = value;
                else if (name.equals("wordNameWeight"))
                    wordNameWeight = value;
                else if (name.equals("stringNameWeight"))
                    stringNameWeight = value;
                else if (name.equals("maxCommonSubStringWeight"))
                    maxCommonSubStringWeight = value;
                else if (name.equals("nGramWeight"))
                    nGramWeight = value;
            }

            else if (name.equals("nGram"))
            {
                int value = Integer.parseInt(parameterElement.getChild("value").getText());
                if (name.equals("nGram"))
                    nGram = value;
            }
        }
    }

    public boolean implementsEffectiveness()
    {
        return true;
    }

    /**
     * Checks whether a Thesaurus is in use
     * 
     * @return <code>true</code> is is to use {@link Thesaurus_}
     */
    public boolean usesThesaurus()
    {
        return true;
    }

    /**
     * Match Comparator methods
     * 
     * @param targetTerm the target {@link Term}
     * @param candidateTerm the candidate {@link Term}
     */
    public boolean compare(Term targetTerm, Term candidateTerm)
    {
        boolean print = false; // ((String)targetTerm.getAttributeValue("name")).equalsIgnoreCase("po header")
                               // &&
                               // ((String)candidateTerm.getAttributeValue("name")).equalsIgnoreCase("order num");

        if (print)
            System.out.println("****************************************************");
        if (print)
            System.out.println("Comparing " + targetTerm + " vs. " + candidateTerm);

        // Get similarity for labels
        String targetLabel = targetTerm.getName();
        String candidateLabel = candidateTerm.getName();

        // Word matching
        double wordEffectiveness_label;
        double nGramEffectiveness_label = 0;
        if ((mode & TermAlgorithmFlagsEnum.SYMMETRIC_FLAG.getValue()) != 0)
            wordEffectiveness_label = StringUtilities.getSymmetricSubstringEffectivity(targetLabel,
                candidateLabel, (mode & TermAlgorithmFlagsEnum.USE_THESAURUS_FLAG.getValue()) != 0 ? thesaurus : null,
                (mode & TermAlgorithmFlagsEnum.USE_SOUNDEX_FLAG.getValue()) != 0);
        else
            wordEffectiveness_label = StringUtilities.getSubstringEffectivity(targetLabel,
                candidateLabel, (mode & TermAlgorithmFlagsEnum.USE_THESAURUS_FLAG.getValue()) != 0 ? thesaurus : null,
                (mode & TermAlgorithmFlagsEnum.USE_SOUNDEX_FLAG.getValue()) != 0);

        // n Gram matching
        nGramEffectiveness_label = StringUtilities.getNGramEffectivity(targetLabel, candidateLabel,
            nGram);

        // Max common substring matching
        double commonEffectiveness_label = StringUtilities.getMaxCommonSubstringEffectivity(
            targetLabel, candidateLabel);

        // Get similarity for names
        String targetName = (String) targetTerm.getAttributeValue("name");
        String candidateName = (String) candidateTerm.getAttributeValue("name");
        boolean isUsingNames = targetName != null && targetName.length() > 0 &&
            candidateName != null && candidateName.length() > 0;

        double wordEffectiveness_name = 0;
        double commonEffectiveness_name = 0;
        double nGramEffectiveness_name = 0;
        if (isUsingNames)
        {
            // Word matching
            if (targetName.length() > 0 && candidateName.length() > 0)
            {
                if ((mode & TermAlgorithmFlagsEnum.SYMMETRIC_FLAG.getValue()) != 0)
                    wordEffectiveness_name = StringUtilities.getSymmetricSubstringEffectivity(
                        targetName, candidateName,
                        (mode & TermAlgorithmFlagsEnum.USE_THESAURUS_FLAG.getValue()) != 0 ? thesaurus : null,
                        (mode & TermAlgorithmFlagsEnum.USE_SOUNDEX_FLAG.getValue()) != 0);
                else
                    wordEffectiveness_name = StringUtilities.getSubstringEffectivity(targetName,
                        candidateName, (mode & TermAlgorithmFlagsEnum.USE_THESAURUS_FLAG.getValue()) != 0 ? thesaurus : null,
                        (mode & TermAlgorithmFlagsEnum.USE_SOUNDEX_FLAG.getValue()) != 0);
            }

            // n Gram matching
            nGramEffectiveness_name = StringUtilities.getNGramEffectivity(targetName,
                candidateName, nGram);

            // Max common substring matching
            commonEffectiveness_name = StringUtilities.getMaxCommonSubstringEffectivity(targetName,
                candidateName);
        }

        double stringMatchingEffectiveness_label = (commonEffectiveness_label *
            maxCommonSubStringWeight + nGramEffectiveness_label * nGramWeight) /
            (maxCommonSubStringWeight + nGramWeight);
        double stringMatchingEffectiveness_name = (commonEffectiveness_name *
            maxCommonSubStringWeight + nGramEffectiveness_name * nGramWeight) /
            (maxCommonSubStringWeight + nGramWeight);

        if (isUsingNames)
            effectiveness = (wordEffectiveness_label * wordLabelWeight +
                stringMatchingEffectiveness_label * stringLabelWeight + wordEffectiveness_name *
                wordNameWeight + stringMatchingEffectiveness_name * stringNameWeight) /
                (wordLabelWeight + stringLabelWeight + wordNameWeight + stringNameWeight);
        else
            effectiveness = (wordEffectiveness_label * wordLabelWeight + stringMatchingEffectiveness_label *
                stringLabelWeight) /
                (wordLabelWeight + stringLabelWeight);

        // Determine overall with voting
        /*
         * double wordLabelWeightAux=wordLabelWeight; double stringLabelWeightAux=stringLabelWeight;
         * double wordNameWeightAux=wordLabelWeight; double stringNameWeightAux=stringLabelWeight;
         * int methods=0; if(wordEffectiveness_label>=threshold) methods++;
         * if(commonEffectiveness_label>=threshold) methods++; if(isUsingNames &&
         * wordEffectiveness_name>=threshold) methods++; if(isUsingNames &&
         * commonEffectiveness_name>=threshold) methods++; if(!isUsingNames) wordNameWeightAux=0;
         * if(!isUsingNames) stringNameWeightAux=0; if((isUsingNames && methods<2) || (!isUsingNames
         * && methods<1)) { wordLabelWeightAux=0; stringLabelWeightAux=0; wordNameWeightAux=0;
         * stringNameWeightAux=0; } else if((isUsingNames && methods>2) || (!isUsingNames &&
         * methods>1)) { if(wordEffectiveness_label<threshold) wordLabelWeightAux=0;
         * if(commonEffectiveness_label<threshold) stringLabelWeightAux=0; if(!isUsingNames ||
         * wordEffectiveness_name<threshold) wordNameWeightAux=0; if(!isUsingNames ||
         * commonEffectiveness_name<threshold) stringNameWeightAux=0; }
         * if((wordLabelWeightAux+stringLabelWeightAux+wordNameWeightAux+stringNameWeightAux)==0)
         * effectiveness=0; else effectiveness=(wordEffectiveness_label*wordLabelWeightAux +
         * commonEffectiveness_label*stringLabelWeightAux + wordEffectiveness_name*wordNameWeightAux
         * + commonEffectiveness_name*stringNameWeightAux)/(wordLabelWeightAux+stringLabelWeightAux+
         * wordNameWeightAux+stringNameWeightAux);
         */

        if (print)
        {
            System.out
                .println("\tWord matching effectivity for labels: " + wordEffectiveness_label);
            System.out.println("\tMax common substring effectivity for labels: " +
                commonEffectiveness_label);
            System.out.println("\tWord matching effectivity for names: " + wordEffectiveness_name);
            System.out.println("\tMax common substring effectivity for names: " +
                commonEffectiveness_name);
            System.out.println("\tOverall effectivity: " + effectiveness);
            System.out.println("****************************************************");
        }

        return effectiveness >= threshold;
    }

    /**
     * Set the label weights
     *
     * @param string the first value
     * @param word the second value
     * @throws AlgorithmException
     */
    public void setLabelWeights(double string, double word) throws AlgorithmException
    {
        if (string + word < 1.0)
            throw new AlgorithmException("label weights sum must be 1.0");
        stringLabelWeight = string;
        wordLabelWeight = word;
    }

    /**
     * Set the name weights
     *
     * @param string the first value
     * @param word the second value
     * @throws AlgorithmException
     */
    public void setNameWeights(double string, double word) throws AlgorithmException
    {
        if (string + word < 1.0)
            throw new AlgorithmException("name weights sum must be 1.0");
        stringNameWeight = string;
        wordNameWeight = word;
    }

}