package ac.technion.iem.ontobuilder.gui.ontobuilder.main;

import java.net.*;
import java.io.*;
import java.text.*;
import java.util.*;
import org.jdom.*;
import org.jdom.input.*;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.utils.AgentEntityResolver;
import ac.technion.iem.ontobuilder.core.utils.properties.ApplicationOptions;
import ac.technion.iem.ontobuilder.core.utils.properties.ApplicationParameters;
import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.ontology.OntologyGui;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.Algorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.AlgorithmUtilities;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.match.MatchOntologyHandler;

/**
 * <p>Title: OntoBuilderAgentClient</p>
 * Extends a {@link Thread}
 */
public class OntoBuilderAgentClient extends Thread
{
    ApplicationOptions options;
    Socket socket;
    boolean verbose;

    /**
     * Constructs a OntoBuilderAgentClient
     * 
     * @param options the {@link ApplicationOptions}
     * @param verbose <code>true </code> if is verbose
     */
    public OntoBuilderAgentClient(ApplicationOptions options, boolean verbose)
    {
        this.options = options;
        this.verbose = verbose;
    }

    /**
     * Constructs a OntoBuilderAgentClient
     * 
     * @param options the {@link ApplicationOptions}
     * @param socket the {@link Socket}
     * @param verbose <code>true </code> if is verbose
     */
    public OntoBuilderAgentClient(ApplicationOptions options, Socket socket, boolean verbose)
    {
        this(options, verbose);
        this.socket = socket;
    }

    /**
     * Runs the thread
     */
    public void run()
    {
        try
        {
            InputStream in = socket.getInputStream();
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                socket.getOutputStream())));
            StringBuffer message = new StringBuffer();
            Protocol protocol = new Protocol();
            int c;
            while ((c = in.read()) != -1)
            {
                char character = (char) c;
                protocol.append(character);
                if (protocol.isEnd())
                    break;
                message.append(character);
            }
            // System.out.println(message.toString());
            String response = processRequest(message.toString());
            out.print(response);
            out.flush();
            out.close();
            in.close();
            socket.close();
            if (verbose)
                System.out.println(MessageFormat.format(
                    ApplicationUtilities.getResourceString("agent.clientDisconnected") + "\n\n",
                    socket.getInetAddress().toString()));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Process a request
     * 
     * @param message the request
     * @return the string the the request result
     */
    public String processRequest(String message)
    {
        try
        {
            SAXBuilder builder = new SAXBuilder(true);
            builder.setEntityResolver(new AgentEntityResolver());
            Document doc = builder.build(new StringReader(message));

            // Get the type of request
            Element ontoBuilderRequestElement = doc.getRootElement();
            List<?> list = ontoBuilderRequestElement.getChildren();
            for (Iterator<?> i = list.iterator(); i.hasNext();)
            {
                Element requestElement = (Element) i.next();
                String type = requestElement.getName();
                if (type.equals("generateOntology"))
                    return requestOntologyGeneration(requestElement);
                else if (type.equals("matchOntologies"))
                    return requestOntologiesMatch(requestElement);
            }
            return "";
        }
        catch (JDOMException e)
        {
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Request of an Ontology Generation
     * 
     * @param requestElement the document request {@link Element}
     * @return a string with the request
     */
    public String requestOntologyGeneration(Element requestElement)
    {
        try
        {
            boolean normalize = isNormalizationRequested(requestElement);
            URL url = new URL(requestElement.getChild("url").getText());
            if (verbose)
                System.out.print("\n" +
                    MessageFormat.format(
                        ApplicationUtilities.getResourceString("commandLine.ontology.generating"),
                        url.toExternalForm()));
            Ontology ontology = OntologyGui.generateOntology(url);
            if (ontology == null)
                return ("Error:" + MessageFormat.format(
                    ApplicationUtilities.getResourceString("error.ontology.generating"),
                    url.toExternalForm()));
            if (verbose)
                System.out.println(" " +
                    MessageFormat.format(
                        ApplicationUtilities.getResourceString("commandLine.ontology.generated"),
                        ontology.getName()));
            if (normalize)
            {
                if (verbose)
                    System.out.print(MessageFormat.format(
                        ApplicationUtilities.getResourceString("commandLine.ontology.normalizing"),
                        ontology.getName()));
                ontology.normalize();
                if (verbose)
                    System.out.println(" " +
                        ApplicationUtilities.getResourceString("commandLine.ontology.normalized"));
            }
            OntologyGui ontologyGui = new OntologyGui();
            ontologyGui.setOntology(ontology);
            String xmlRepresentation = ontologyGui.getXMLRepresentationAsString();
            if (verbose)
                System.out.println(ApplicationUtilities.getResourceString("commandLine.generated"));
            return xmlRepresentation;
        }
        catch (Exception e)
        {
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Request of an Ontologies Match
     * 
     * @param requestElement the document request {@link Element}
     * @return a string with the request
     */
    public String requestOntologiesMatch(Element requestElement)
    {
        try
        {
            boolean normalize = isNormalizationRequested(requestElement);
            URL targetURL = new URL(requestElement.getChild("targetURL").getText());
            URL candidateURL = new URL(requestElement.getChild("candidateURL").getText());
            String algorithmName = null;
            double threshold = -1;
            Element algorithmElement = requestElement.getChild("algorithm");
            if (algorithmElement != null)
            {
                algorithmName = algorithmElement.getText();
                String thresholdStr = algorithmElement.getAttributeValue("threshold");
                if (thresholdStr != null)
                    threshold = Double.parseDouble(thresholdStr);
            }
            if (algorithmName == null)
                algorithmName = (String) options
                    .getOptionValue(AlgorithmUtilities.DEFAULT_ALGORITHM_PROPERTY);

            if (verbose)
                System.out.print("\n" +
                    MessageFormat.format(
                        ApplicationUtilities.getResourceString("commandLine.algorithm.loading"),
                        algorithmName));
            Algorithm algorithm = AlgorithmUtilities.getAlgorithmPlugin(algorithmName);
            if (algorithm == null)
                return ("Error: " + MessageFormat.format(
                    ApplicationUtilities.getResourceString("error.algorithms.notFound"),
                    algorithmName));
            if (threshold != -1)
                algorithm.setThreshold(threshold);
            if (verbose)
                System.out.println(ApplicationUtilities
                    .getResourceString("commandLine.algorithm.loaded"));
            if (verbose)
                System.out.print(MessageFormat.format(
                    ApplicationUtilities.getResourceString("commandLine.ontology.generating"),
                    targetURL.toExternalForm()));
            Ontology targetOntology = OntologyGui.generateOntology(targetURL);
            if (targetOntology == null)
                return ("Error: " + MessageFormat.format(
                    ApplicationUtilities.getResourceString("error.ontology.generating"),
                    ApplicationParameters.targetURL.toExternalForm()));
            if (verbose)
                System.out.println(" " +
                    MessageFormat.format(
                        ApplicationUtilities.getResourceString("commandLine.ontology.generated"),
                        targetOntology.getName()));
            if (verbose)
                System.out.print(MessageFormat.format(
                    ApplicationUtilities.getResourceString("commandLine.ontology.generating"),
                    candidateURL.toExternalForm()));
            Ontology candidateOntology = OntologyGui.generateOntology(candidateURL);
            if (candidateOntology == null)
                return ("Error: " + MessageFormat.format(
                    ApplicationUtilities.getResourceString("error.ontology.generating"),
                    ApplicationParameters.candidateURL.toExternalForm()));
            if (verbose)
                System.out.println(" " +
                    MessageFormat.format(
                        ApplicationUtilities.getResourceString("commandLine.ontology.generated"),
                        candidateOntology.getName()));
            if (normalize)
            {
                if (verbose)
                    System.out.print(MessageFormat.format(
                        ApplicationUtilities.getResourceString("commandLine.ontology.normalizing"),
                        targetOntology.getName()));
                targetOntology.normalize();
                if (verbose)
                    System.out.println(" " +
                        ApplicationUtilities.getResourceString("commandLine.ontology.normalized"));
                if (verbose)
                    System.out.print(MessageFormat.format(
                        ApplicationUtilities.getResourceString("commandLine.ontology.normalizing"),
                        candidateOntology.getName()));
                candidateOntology.normalize();
                if (verbose)
                    System.out.println(" " +
                        ApplicationUtilities.getResourceString("commandLine.ontology.normalized"));
            }
            if (verbose)
                System.out.print(ApplicationUtilities.getResourceString("commandLine.matching"));
            MatchInformation matchInformation = MatchOntologyHandler.match(targetOntology, candidateOntology, algorithm);
            if (matchInformation == null)
                return ("Error: " + ApplicationUtilities
                    .getResourceString("error.matchInformation.generating"));
            if (normalize)
                matchInformation.denormalize();

            String xmlRepresentation = matchInformation.getXMLRepresentationAsString();
            if (verbose)
                System.out.println(ApplicationUtilities.getResourceString("commandLine.matched"));
            return xmlRepresentation;
        }
        catch (Exception e)
        {
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Checks if the request is normalized
     * 
     * @param requestElement the document request {@link Element}
     * @return <code>true</code> if it is normalized
     */
    protected boolean isNormalizationRequested(Element requestElement)
    {
        String att = requestElement.getAttributeValue("normalize");
        if (att != null && att.equals("yes"))
            return true;
        return false;
    }

    /**
     * <p>Title: Protocol</p>
     */
    protected class Protocol
    {
        char buffer[] =
        {
            ' ', ' ', ' ', ' '
        };

        public Protocol()
        {
        }

        public void append(char c)
        {
            buffer[0] = buffer[1];
            buffer[1] = buffer[2];
            buffer[2] = buffer[3];
            buffer[3] = c;
        }

        public boolean isEnd()
        {
            return toString().equals("\r\n\r\n");
        }

        public String toString()
        {
            return new String(buffer);
        }
    }
}
