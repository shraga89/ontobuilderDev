package ac.technion.iem.ontobuilder.matching.algorithms.line1.term;

import java.util.*;
import org.jdom.*;

import ac.technion.iem.ontobuilder.core.ontology.OntologyUtilities;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.core.ontology.operator.HyphenRemovalOperator;
import ac.technion.iem.ontobuilder.core.ontology.operator.IgnorableCharacterRemovalOperator;
import ac.technion.iem.ontobuilder.core.ontology.operator.LowerCaseOperator;
import ac.technion.iem.ontobuilder.core.ontology.operator.StopWordsRemovalOperator;
import ac.technion.iem.ontobuilder.core.ontology.operator.StringOperator;
import ac.technion.iem.ontobuilder.core.ontology.operator.WordSeparatorOperator;

/**
 * <p>Title: TermPreprocessor</p>
 */
public class TermPreprocessor
{
    public static final int CASE_SENSITIVE_FLAG = 1;
    public static final int MERGE_WORDS_FLAG = 2;

    public static final String STOP_TERM_SEPARATOR = ",";

    protected ArrayList<String> stopTerms;
    protected String stopTermsText = "";
    protected String stopTermSeparator = STOP_TERM_SEPARATOR;

    protected int mode = 0;

    /**
     * Constructs a default TermPreprocessor
     */
    public TermPreprocessor()
    {
        stopTerms = new ArrayList<String>();
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
            if (name.equals("caseSensitive") || name.equals("mergeWords"))
            {
                boolean value = Boolean.valueOf(parameterElement.getChild("value").getText())
                    .booleanValue();
                if (name.equals("caseSensitive") && value)
                    mode += CASE_SENSITIVE_FLAG;
                else if (name.equals("mergeWords") && value)
                    mode += MERGE_WORDS_FLAG;
            }
            else if (name.equals("stopTerms"))
            {
                stopTermsText = parameterElement.getChild("value").getText();
            }
            else if (name.equals("stopTermSeparator"))
            {
                stopTermSeparator = parameterElement.getChild("value").getText();
            }
            configureStopTerms();
        }
    }

    protected void configureStopTerms()
    {
        StringTokenizer st = new StringTokenizer(stopTermsText, stopTermSeparator);
        while (st.hasMoreTokens())
            stopTerms.add(st.nextToken().toLowerCase());
    }

    /**
     * Preprocess the target and candidate terms
     */
    public void preprocess(ArrayList<Term> targetTerms, ArrayList<Term> candidateTerms,
        ArrayList<?> originalTargetTerms, ArrayList<?> originalCandidateTerms)
    {
        StringOperator wsOp = new WordSeparatorOperator();
        ArrayList<Term> tTerms = OntologyUtilities.applyStringOperatorToTerms(originalTargetTerms,
            wsOp);
        ArrayList<Term> cTerms = OntologyUtilities.applyStringOperatorToTerms(
            originalCandidateTerms, wsOp);

        if ((mode & CASE_SENSITIVE_FLAG) == 0)
        {
            StringOperator lwOp = new LowerCaseOperator();
            tTerms = OntologyUtilities.applyStringOperatorToTerms(tTerms, lwOp);
            cTerms = OntologyUtilities.applyStringOperatorToTerms(cTerms, lwOp);
        }

        StringOperator icrOp = new IgnorableCharacterRemovalOperator();
        tTerms = OntologyUtilities.applyStringOperatorToTerms(tTerms, icrOp);
        cTerms = OntologyUtilities.applyStringOperatorToTerms(cTerms, icrOp);

        StringOperator hrOp = new HyphenRemovalOperator((mode & MERGE_WORDS_FLAG) > 0);
        tTerms = OntologyUtilities.applyStringOperatorToTerms(tTerms, hrOp);
        cTerms = OntologyUtilities.applyStringOperatorToTerms(cTerms, hrOp);

        StringOperator swrOp = new StopWordsRemovalOperator(stopTerms);
        tTerms = OntologyUtilities.applyStringOperatorToTerms(tTerms, swrOp);
        cTerms = OntologyUtilities.applyStringOperatorToTerms(cTerms, swrOp);

        targetTerms.addAll(tTerms);
        candidateTerms.addAll(cTerms);
    }
}
