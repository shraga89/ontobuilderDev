package ac.technion.iem.ontobuilder.matching.algorithms.line1.misc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import ac.technion.iem.ontobuilder.core.utils.StringUtilities;
import ac.technion.iem.ontobuilder.core.utils.network.NetworkEntityResolver;
import ac.technion.iem.ontobuilder.core.utils.properties.PropertiesHandler;
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
            SAXBuilder builder = new SAXBuilder(true);
            builder.setEntityResolver(new NetworkEntityResolver());
            Document doc = builder.build(stream);
            return loadFromDocument(doc);
        }
        catch (JDOMException e)
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
    public static Vector<AbstractAlgorithm> getAlgorithmsInstances(File file)
        throws AlgorithmException
    {
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            SAXBuilder builder = new SAXBuilder(true);
            builder.setEntityResolver(new NetworkEntityResolver());
            Document doc = builder.build(reader);
            return loadFromDocument(doc);
        }
        catch (FileNotFoundException e)
        {
            throw new AlgorithmException(e.getMessage());
        }
        catch (JDOMException e)
        {
            throw new AlgorithmException(e.getMessage());
        }
    }

    // added haggai 7/12/03
    /**
     * Get the algorithm instances from a file
     * 
     * @param file the {@link File} to read from
     * @param algoritmPluginName algorithm plugin to match to
     * @return a vector of algorithms
     * @throws AlgorithmException
     */
    public static Algorithm getAlgorithmsInstance(File file, String algoritmPluginName)
        throws AlgorithmException
    {
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            SAXBuilder builder = new SAXBuilder(true);
            builder.setEntityResolver(new NetworkEntityResolver());
            // second parameter added for topK . amir 10/2004
            Document doc;
            doc = builder.build(reader);
            Vector<AbstractAlgorithm> algorithms = loadFromDocument(doc);
            Iterator<AbstractAlgorithm> algorithmsIterator = algorithms.iterator();
            while (algorithmsIterator.hasNext())
            {
                Algorithm algorithm = (Algorithm) algorithmsIterator.next();
                if (algorithm.getPluginName().equalsIgnoreCase(algoritmPluginName))
                    return algorithm;
            }
            return null;
        }
        catch (FileNotFoundException e)
        {
            throw new AlgorithmException(e.getMessage());
        }
        catch (JDOMException e)
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
    @SuppressWarnings("unchecked")
    protected static Vector<AbstractAlgorithm> loadFromDocument(Document doc)
    {
        algorithms = new Vector<AbstractAlgorithm>();
        Element algorithmsElement = doc.getRootElement();

        // Create the preprocessor first
        TermPreprocessor termPreprocessor = new TermPreprocessor();
        Element preprocessingElement = algorithmsElement.getChild("preprocessing");
        if (preprocessingElement != null)
            termPreprocessor.configure(preprocessingElement);

        // Now create the list of algorithms
        List<Element> algorithmsList = algorithmsElement.getChildren("algorithm");
        for (Iterator<Element> i = algorithmsList.iterator(); i.hasNext();)
        {
            Element algorithmElement = i.next();
            String algorithmClass = algorithmElement.getChild("class").getText();
            try
            {
                AbstractAlgorithm algorithm = (AbstractAlgorithm) ClassLoader
                    .getSystemClassLoader().loadClass(algorithmClass).newInstance();
                algorithm.setPluginName(algorithmElement.getAttributeValue("name"));
                algorithm.setTermPreprocessor(termPreprocessor);
                algorithm.configure(algorithmElement);
                algorithms.add(algorithm);
            }
            catch (Exception e)
            {
                continue;
            }
        }
        Collections.reverse(algorithms);
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
            if (a.getName().equalsIgnoreCase(name))
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