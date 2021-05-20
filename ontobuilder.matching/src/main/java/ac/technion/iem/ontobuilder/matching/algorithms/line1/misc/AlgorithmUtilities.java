package ac.technion.iem.ontobuilder.matching.algorithms.line1.misc;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaders;

import ac.technion.iem.ontobuilder.core.utils.StringUtilities;
import ac.technion.iem.ontobuilder.core.utils.network.NetworkEntityResolver;
import ac.technion.iem.ontobuilder.core.utils.properties.PropertiesHandler;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.common.AbstractAlgorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.common.Algorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.term.TermPreprocessor;

/**
 * <p>Title: AlgorithmUtilities</p>
 * <p>Description: Internal algorithm utilities</p>
 */
public class AlgorithmUtilities
{
    public static final String DEFAULT_ALGORITHM_PROPERTY = "defaultAlgorithm";

    public static Vector<AbstractAlgorithm> algorithms;

    /**
     * Get the algorithm instances from a file
     * 
     * @param file the name of the file to read from
     * @return a vector of {@link AbstractAlgorithm}
     * @throws AlgorithmException
     */
    public static Vector<AbstractAlgorithm> getAlgorithmsInstances(String file)
        throws AlgorithmException
    {
        InputStream stream = AlgorithmUtilities.class.getResourceAsStream(file);
        if (stream == null)
        {
            String params[] =
            {
                file
            };
            throw new AlgorithmException(StringUtilities.getReplacedString(
                PropertiesHandler.getResourceString("error.algorithms.file"), params));
        }
        try
        {
            SAXBuilder builder = new SAXBuilder(XMLReaders.DTDVALIDATING);
            builder.setEntityResolver(new NetworkEntityResolver());
            Document doc = builder.build(stream);
            return loadFromDocument(doc);
        }
        catch (JDOMException | IOException e)
        {
            throw new AlgorithmException(e.getMessage());
        }
    }

    /**
     * Get the algorithm instances from a file
     * 
     * @param file the {@link File} to read from
     * @return a vector of {@link AbstractAlgorithm}
     * @throws AlgorithmException when cannot load from document
     */
    public static Vector<AbstractAlgorithm> getAlgorithmsInstances(InputStream file)
        throws AlgorithmException
    {
        try
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(file, StandardCharsets.UTF_8));
            SAXBuilder builder = new SAXBuilder(XMLReaders.NONVALIDATING);
//            builder.setEntityResolver(new NetworkEntityResolver());
            Document doc = builder.build(reader);
            return loadFromDocument(doc);
        } catch (JDOMException | IOException e)
        {
            throw new AlgorithmException(e.getMessage());
        }
    }

    // added haggai 7/12/03
    /**
     * Get the algorithm instances from a file
     * 
     * @param file the algorithm file as an input stream
     * @param algoritmPluginName algorithm plugin to match to
     * @return a vector of algorithms
     * @throws AlgorithmException if cannot parse algorithm file
     */
    public static Algorithm getAlgorithmsInstance(InputStream file, String algoritmPluginName)
        throws AlgorithmException
    {
        try
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(file, StandardCharsets.UTF_8));
            SAXBuilder builder = new SAXBuilder(XMLReaders.NONVALIDATING);
//            builder.setEntityResolver(new NetworkEntityResolver());
            // second parameter added for topK . amir 10/2004
            Document doc;
            doc = builder.build(reader);
            System.out.println("built algo doc");
            Vector<AbstractAlgorithm> algorithms = loadFromDocument(doc);
            for (AbstractAlgorithm abstractAlgorithm : algorithms) {
                if (abstractAlgorithm.getPluginName().equalsIgnoreCase(algoritmPluginName))
                    return abstractAlgorithm;
            }
            return null;
        } catch (JDOMException | IOException e)
        {
            throw new AlgorithmException(e.getMessage());
        }
    }

    /**
     * Load the algorithms from a document
     * 
     * @param doc the {@link Document} to read from
     * @return a vector of {@link AbstractAlgorithm}
     */
    protected static Vector<AbstractAlgorithm> loadFromDocument(Document doc)
    {
        System.out.printf("Loading algorithms from %s", doc.toString());
        algorithms = new Vector<>();
        Element algorithmsElement = doc.getRootElement();

        // Create the preprocessor first
        TermPreprocessor termPreprocessor = new TermPreprocessor();
        Element preprocessingElement = algorithmsElement.getChild("preprocessing");
        if (preprocessingElement != null)
            termPreprocessor.configure(preprocessingElement);

        // Now create the list of algorithms
        List<Element> algorithmsList = algorithmsElement.getChildren("algorithm");
        for (Element algorithmElement : algorithmsList) {
            String algorithmClass = algorithmElement.getChild("class").getText();
            try {
                AbstractAlgorithm algorithm = (AbstractAlgorithm) ClassLoader
                        .getSystemClassLoader().loadClass(algorithmClass).newInstance();
                algorithm.setPluginName(algorithmElement.getAttributeValue("name"));
                algorithm.setTermPreprocessor(termPreprocessor);
                algorithm.configure(algorithmElement);
                algorithms.add(algorithm);
            } catch (Exception e) {
                System.err.printf("Exception in algorithm loading process: %s", e.getMessage());
            }
        }
        Collections.reverse(algorithms);
        System.out.printf("Scanning algorthim file, found %d algorithms \n", algorithms.size());
        return algorithms;
    }

    /**
     * Get an algorithm according to its name
     * 
     * @param name the algorithm name
     * @return an {@link AbstractAlgorithm}
     */
    public static AbstractAlgorithm getAlgorithm(String name)
    {
        for (Iterator<AbstractAlgorithm> i = algorithms.iterator(); i.hasNext();)
        {
            Algorithm a = i.next();
            if (a.getName().equalsIgnoreCase(name)||a.getPluginName().equalsIgnoreCase(name))
                return (AbstractAlgorithm) a.makeCopy();
        }
        return null;
    }

    /**
     * Get an algorithm plugin according to its name
     * 
     * @param name the algorithm name
     * @return an {@link Algorithm}
     */
    public static Algorithm getAlgorithmPlugin(String name)
    {
        for (Iterator<AbstractAlgorithm> i = algorithms.iterator(); i.hasNext();)
        {
            Algorithm a = (Algorithm) i.next();
            if (a.getPluginName().equalsIgnoreCase(name))
                return a.makeCopy();
        }
        return null;
    }
}