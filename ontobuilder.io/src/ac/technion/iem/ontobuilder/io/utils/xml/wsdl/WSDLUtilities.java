package ac.technion.iem.ontobuilder.io.utils.xml.wsdl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.wsdl.Definition;
import javax.wsdl.Operation;
import javax.wsdl.Part;
import javax.wsdl.PortType;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import ac.technion.iem.ontobuilder.core.ontology.Domain;
import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.core.utils.network.NetworkEntityResolver;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.term.TermAlgorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.misc.MatchingAlgorithmsNamesEnum;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.wrapper.SchemaMatchingsWrapper;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.wrapper.OntoBuilderWrapper;

/**
 * <p>Title: WSDLUtilities</p>
 */
public final class WSDLUtilities
{

    public static final byte INPUT = 0;
    public static final byte OUTPUT = 1;

    private static OntoBuilderWrapper obw = new OntoBuilderWrapper(true);

    public static final String CHALLENGES_DTD = "challenges.dtd";

    @SuppressWarnings("unchecked")
    public static Ontology extractOntology(String wsdlFile, byte type) throws WSDLException
    {

        WSDLFactory factory = WSDLFactory.newInstance();
        WSDLReader reader = factory.newWSDLReader();

        reader.setFeature("javax.wsdl.verbose", true);
        reader.setFeature("javax.wsdl.importDocuments", true);

        Definition def = reader.readWSDL(null, wsdlFile);
        // Namespace ns = (Namespace)def.getNamespaces().get("");
        Map<?, ?> portTypes = def.getPortTypes();
        Iterator<?> ptItr = portTypes.values().iterator();
        Ontology ontology = new Ontology();
        ontology.setName(wsdlFile);
        Term term;
        while (ptItr.hasNext())
        {
            PortType pt = (PortType) ptItr.next();
            List<Operation> operations = pt.getOperations();
            Iterator<Operation> opItr = operations.iterator();
            Map<?, Part> parts;
            while (opItr.hasNext())
            {
                Operation op = (Operation) opItr.next();
                switch (type)
                {
                case (INPUT):
                    parts = op.getInput().getMessage().getParts();
                    break;
                case (OUTPUT):
                    parts = op.getOutput().getMessage().getParts();
                    break;
                default:
                    throw new IllegalArgumentException("Ontology Type");
                }
                Iterator<Part> partsItr = parts.values().iterator();
                while (partsItr.hasNext())
                {
                    Part part = partsItr.next();
                    term = new Term();
                    term.setName(part.getName());
                    term.setDomain(new Domain(part.getTypeName().toString()));
                    ontology.addTerm(term);
                }
            }
        }
        return ontology;
    }

    public static Vector<EEE05Challenge> extractChallenges(String challengeFile, byte type)
        throws Exception
    {
        Vector<EEE05Challenge> challenges = new Vector<EEE05Challenge>();
        BufferedReader reader = new BufferedReader(new FileReader(challengeFile));
        SAXBuilder builder = new SAXBuilder(true);
        builder.setEntityResolver(new NetworkEntityResolver());
        Document doc = builder.build(reader, CHALLENGES_DTD);
        Element EEE05ChallengeElement = doc.getRootElement();
        Iterator<?> elmItr = EEE05ChallengeElement.getChildren().iterator();
        while (elmItr.hasNext())
        {
            Element element = (Element) elmItr.next();
            Element provided, resultant;
            String content;
            StringTokenizer st;
            Vector<Object> providedParams, resultantParams;
            switch (type)
            {
            case (EEE05Challenge.DISCOVERY):
                if (!element.getName().equalsIgnoreCase("DiscoveryRoutine"))
                    continue;
                else
                {
                    provided = element.getChild("Provided");
                    resultant = element.getChild("Resultant");
                    providedParams = new Vector<Object>();
                    resultantParams = new Vector<Object>();
                    content = provided.getTextTrim();
                    st = new StringTokenizer(content + ",", ",");
                    while (st.hasMoreElements())
                    {
                        providedParams.add(st.nextElement());
                    }
                    content = resultant.getTextTrim();
                    st = new StringTokenizer(content + ",", ",");
                    while (st.hasMoreElements())
                    {
                        resultantParams.add(st.nextElement());
                    }
                }
                challenges.add(new EEE05Challenge(EEE05Challenge.DISCOVERY, providedParams,
                    resultantParams));
                break;
            case (EEE05Challenge.COMPOSITION):
                if (!element.getName().equalsIgnoreCase("CompositionRoutine"))
                    continue;
                else
                {
                    provided = element.getChild("Provided");
                    resultant = element.getChild("Resultant");
                }
                break;
            }
        }
        return challenges;
    }

    public static void discover(EEE05Challenge challenge, Vector<Ontology> inputOntologies,
        Vector<Ontology> outputOntologies) throws Exception
    {
        TermAlgorithm matcher = (TermAlgorithm) obw.loadMatchAlgorithm(MatchingAlgorithmsNamesEnum.TERM.getName());
        MatchInformation match;
        SchemaMatchingsWrapper smw;
        double score;
        challenge.setInputDiscoverScore(-1);
        challenge.setOutputDiscoverScore(-1);
        // find best inputOntology
        Iterator<Ontology> it = inputOntologies.iterator();
        while (it.hasNext())
        {
            Ontology in = (Ontology) it.next();
            match = matcher.match(in, challenge.getInputOntology());
            try
            {
                smw = new SchemaMatchingsWrapper(match.getMatrix());
                // 1:1 mapping
                score = smw.getBestMatching().getTotalMatchWeight();
                if (challenge.getInputDiscoverScore() < score)
                {
                    challenge.setInputDiscoverScore(score);
                    challenge.setDiscoveredInputWSDL(in.getName());
                }
            }
            catch (Exception e)
            {
                continue;
            }
        }
        // find best outputOntology
        it = outputOntologies.iterator();
        while (it.hasNext())
        {
            Ontology out = (Ontology) it.next();
            match = matcher.match(out, challenge.getOutputOntology());
            try
            {
                smw = new SchemaMatchingsWrapper(match.getMatrix());
                // 1:1 mapping
                score = smw.getBestMatching().getTotalMatchWeight();
                if (challenge.getOutputDiscoverScore() < score)
                {
                    challenge.setOutputDiscoverScore(score);
                    challenge.setDiscoveredOutputWSDL(out.getName());
                }
            }
            catch (Exception e)
            {
                continue;
            }
        }
    }

    public static void main(String[] args)
    {
        try
        {
            // extract input and output ontologies
            File[] wsdlFiles = new File("EEE05_repository").listFiles();
            new File("Ontologies").mkdir();
            new File("Challenges").mkdir();
            Vector<Ontology> inputOntologies = new Vector<Ontology>();
            Vector<Ontology> outputOntologies = new Vector<Ontology>();
            for (int i = 0; i < wsdlFiles.length; i++)
            {
                Ontology input = WSDLUtilities.extractOntology(
                    "EEE05_repository/" + wsdlFiles[i].getName(), WSDLUtilities.INPUT);
                Ontology output = WSDLUtilities.extractOntology(
                    "EEE05_repository/" + wsdlFiles[i].getName(), WSDLUtilities.OUTPUT);
                input.setTitle("input ontology");
                input.saveToXML(new File("Ontologies/" + wsdlFiles[i].getName() + "_INPUT.xml"));
                output.setTitle("output ontology");
                output.saveToXML(new File("Ontologies/" + wsdlFiles[i].getName() + "_OUTPUT.xml"));
                inputOntologies.add(input);
                outputOntologies.add(output);
            }
            // extract discovery challenges
            Vector<EEE05Challenge> challenges = WSDLUtilities.extractChallenges(
                "EEE05_SampleChallenges.xml", EEE05Challenge.DISCOVERY);
            Iterator<EEE05Challenge> it = challenges.iterator();
            int i = 0;
            while (it.hasNext())
            {
                EEE05Challenge challenge = (EEE05Challenge) it.next();
                challenge.getInputOntology().saveToXML(
                    new File("Challenges/challenge_" + i + "_INPUT.xml"));
                challenge.getOutputOntology().saveToXML(
                    new File("Challenges/challenge_" + i + "_OUTPUT.xml"));
                i++;
                WSDLUtilities.discover(challenge, inputOntologies, outputOntologies);
                System.out.println("Challenge Discovery Results:");
                System.out.println("Provided: " + challenge.getProvided());
                System.out.println("Input WSDL: " + challenge.getDiscoveredInputWSDL());
                System.out.println("Resultant: " + challenge.getResultant());
                System.out.println("Output WSDL: " + challenge.getDiscoveredOutputWSDL());
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
