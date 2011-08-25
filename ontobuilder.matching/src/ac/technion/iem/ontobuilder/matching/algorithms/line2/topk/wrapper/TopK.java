package ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.wrapper;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.utils.files.XmlFileHandler;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.misc.MatchingAlgorithmsNamesEnum;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.meta.match.MatchedAttributePair;
import ac.technion.iem.ontobuilder.matching.utils.SchemaTranslator;
import ac.technion.iem.ontobuilder.matching.wrapper.OntoBuilderWrapper;

/**
 * <p>
 * Title: TopK
 * </p>
 * <p>
 * Description: Use this class to get Top K mappings from command-line
 * </p>
 */
public class TopK
{

    private SchemaMatchingsWrapper smw;
    private OntoBuilderWrapper obw;
    private XmlFileHandler fileHandler;
    private boolean userWantNextBestMatch = true;
    private boolean debugMode = false;
    private boolean directErrorsToFile = false;

    SchemaTranslator stRemember;

    /**
     * Constructs a TopK
     * 
     * @param params the parameters
     * @throws TopKException
     * @throws IOException
     */
    public TopK(String[] params) throws TopKException, IOException
    {
        if (directErrorsToFile)
            System.setErr(new PrintStream(new FileOutputStream("errors.txt")));
        TopKParametersUtilities topKpUtils = new TopKParametersUtilities(params);
        if (!topKpUtils.isValidCommandLine())
        {
            System.err.println(topKpUtils.getErrorString());
            System.exit(1);
        }
        else
        {
            try
            {
                System.out.println("Top K framework version 1.0\n\n");
                obw = new OntoBuilderWrapper();
                fileHandler = new XmlFileHandler();
                Ontology candidateOntology = fileHandler.readOntologyXMLFile(topKpUtils
                    .getCandidateOntologyXMLFilePath());
                Ontology targetOntology = fileHandler.readOntologyXMLFile(topKpUtils
                    .getTargetOntologyXMLFilePath());
                MatchInformation match = obw.loadMatchAlgorithm(
                    MatchingAlgorithmsNamesEnum.getAllNames()[topKpUtils.getMatchAlgorithm()]).match(
                    candidateOntology, targetOntology);
                smw = new SchemaMatchingsWrapper(match.getMatrix());
                SchemaTranslator st = null;
                BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
                int matchIndex = 0;
                while (userWantNextBestMatch)
                {
                    matchIndex++;
                    if (debugMode)
                        stRemember = st;
                    st = smw.getNextBestMatching();
                    if (debugMode)
                        printDiff(st.getMatchedPairs());
                    st.saveMatchToXML(
                        matchIndex,
                        topKpUtils.getCandidateOntologyXMLFilePath(),
                        topKpUtils.getTargetOntologyXMLFilePath(),
                        topKpUtils.getTopKOutputXMLFilePath() +
                            matchIndex +
                            (topKpUtils.getTopKOutputXMLFilePath().indexOf(".xml") == -1 ? ".xml" : ""));
                    System.out.println(matchIndex + "-th best matching finished and save in file:" +
                        topKpUtils.getTopKOutputXMLFilePath() + matchIndex + "\n" +
                        "Do you want to get the next best matching? (y/n)\n");
                    String answer = in.readLine();
                    if (answer.equalsIgnoreCase("y"))
                        userWantNextBestMatch = true;
                    else if (answer.equalsIgnoreCase("n"))
                        userWantNextBestMatch = false;
                    else
                        throw new TopKException("error in answer for next best matching");
                }
                System.out.println("Top K Matching process finished\nThank you for using Top K...");
                System.exit(0);
            }
            catch (Throwable e)
            {
                e.getStackTrace();
                throw new TopKException(e.getMessage() + "\n" +
                    "\nYou can send an error report to haggairic@yahoo.com");
            }
        }
    }

    /**
     * Prints the different between attributes in the matched attribute pairs. Used for debugging
     * 
     * @param pairs the array of matched attribute pairs
     */
    public void printDiff(MatchedAttributePair[] pairs)
    {
        if (stRemember == null)
            return;
        else
        {
            for (int i = 0; i < pairs.length; i++)
            {
                if (!stRemember.isExist(pairs[i]))
                    System.out.println("new pair: " + pairs[i].getAttribute1() + " -> " +
                        pairs[i].getAttribute2() + "weight:" + pairs[i].getMatchedPairWeight());
            }
        }
    }

    public static void main(String[] args)
    {
        try
        {
            new TopK(args);
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }
}