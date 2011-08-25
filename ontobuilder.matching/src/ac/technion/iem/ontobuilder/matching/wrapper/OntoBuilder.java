package ac.technion.iem.ontobuilder.matching.wrapper;

import java.io.File;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import ac.technion.iem.ontobuilder.core.ontology.domain.DomainSimilarity;
import ac.technion.iem.ontobuilder.core.resources.OntoBuilderResources;
import ac.technion.iem.ontobuilder.core.thesaurus.Thesaurus;
import ac.technion.iem.ontobuilder.core.thesaurus.ThesaurusException;
import ac.technion.iem.ontobuilder.core.utils.network.NetworkUtilities;
import ac.technion.iem.ontobuilder.core.utils.network.NetworkUtilitiesPropertiesEnum;
import ac.technion.iem.ontobuilder.core.utils.properties.ApplicationOptions;
import ac.technion.iem.ontobuilder.core.utils.properties.PropertiesHandler;
import ac.technion.iem.ontobuilder.core.utils.properties.PropertyException;
import ac.technion.iem.ontobuilder.core.utils.properties.ResourceException;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.AbstractAlgorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.Algorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.AlgorithmException;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.AlgorithmUtilities;

/**
 * <p>Title: OntoBuilder</p>
 * <p>Description: Initialises application - parameters, Thesaurus, algorithms</p>
 */
class OntoBuilder
{

    protected Locale locale = Locale.getDefault();
    protected ApplicationOptions options;
    protected Thesaurus thesaurus;
    protected Vector<AbstractAlgorithm> algorithms;

    /**
     * Constructs a default OntoBuilder (regular version)
     */
    protected OntoBuilder()
    {
        init(false);
    }

    /**
     * Constructs a OntoBuilder
     * 
     * @param light the version of the Onto Builder to create (<code>true</code>-light, <code>false</code>-regular)
     */
    protected OntoBuilder(boolean light)
    {
        init(light);
    }

    /**
     * Initialises an OntoBuilder
     * 
     * @param light the version of the Onto Builder to create (<code>true</code>-light, <code>false</code>-regular)
     */
    private void init(boolean light)
    {
        initializeParameters();
        initializeOptions();
        buildThesaurus();
        initializeAlgorithms();
        DomainSimilarity.buildDomainMatrix(PropertiesHandler
            .getStringProperty("domain.domainMatrix"));
        if (!light)
        {
            NetworkUtilities.initializeHTTPSProtocol();
            setProxy();
            setConnectionTimeout();
        }
    }

    /**
     * Sets the proxy according to NetworkUtilities.USE_PROXY_PROPERTY
     */
    protected void setProxy()
    {
        boolean proxy = Boolean.valueOf(
            (String) options.getOptionValue(NetworkUtilitiesPropertiesEnum.USE_PROXY_PROPERTY.getValue())).booleanValue();
        if (proxy)
            NetworkUtilities.setProxy(
                (String) options.getOptionValue(NetworkUtilitiesPropertiesEnum.PROXY_HOST_PROPERTY.getValue()),
                (String) options.getOptionValue(NetworkUtilitiesPropertiesEnum.PROXY_PORT_PROPERTY.getValue()));
        else
            NetworkUtilities.disableProxy();
    }

    /**
     * Sets the proxy according to the input host and port
     * 
     * @param proxyHost the proxy host to set
     * @param proxyPort the proxy port to set
     */
    public void setProxy(String proxyHost, String proxyPort)
    {
        NetworkUtilities.setProxy(proxyHost, proxyPort);
    }

    /**
     * Set the connection timeout according to NetworkUtilities.CONNECTION_TIMEOUT_PROPERTY
     */
    protected void setConnectionTimeout()
    {
        try
        {
            long millis = Long.parseLong((String) options
                .getOptionValue(NetworkUtilitiesPropertiesEnum.CONNECTION_TIMEOUT_PROPERTY.getValue()));
            NetworkUtilities.setConnectionTimeout(millis > 0 ? millis * 1000 : -1);
        }
        catch (NumberFormatException e)
        {
            // NetworkUtilities.setConnectionTimeout(-1);
        }
    }

    /**
     * Builds to Thesaurus of the Onto Builder
     */
    private void buildThesaurus()
    {
        try
        {
//            File thesaurusFile = new File(PropertiesHandler.getCurrentDirectory() +
//                File.separator + PropertiesHandler.getStringProperty("thesaurus.file"));
            File thesaurusFile = new File(OntoBuilderResources.Config.THESAURUS_XML);
            thesaurus = new Thesaurus(thesaurusFile);
        }
        catch (ThesaurusException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Get the Thesaurus
     * 
     * @return the {@link Thesaurus_}
     */
    public Thesaurus getThesaurus()
    {
        return thesaurus;
    }

    /**
     * Load the application's options file
     */
    private void initializeOptions()
    {
        options = new ApplicationOptions();
        File optionFile = new File(OntoBuilderResources.Config.CONFIGURATION_XML);
        options.loadOptions(optionFile);
    }

    /**
     * Initialise the matching algorithms instances
     */
    private void initializeAlgorithms()
    {
        try
        {
//            File algorithmsFile = new File(PropertiesHandler.getCurrentDirectory() +
//                PropertiesHandler.getStringProperty("algorithms.file"));
            File algorithmsFile = new File(OntoBuilderResources.Config.Matching.ALGORITHMS_XML);
            if (algorithmsFile.exists())
                algorithms = AlgorithmUtilities.getAlgorithmsInstances(algorithmsFile);
            if (algorithms == null)
                return;
            double threshold = Double.parseDouble((String) options
                .getOptionValue(Algorithm.MATCH_THRESHOLD_PROPERTY));
            for (Iterator<AbstractAlgorithm> i = algorithms.iterator(); i.hasNext();)
            {
                Algorithm algorithm = i.next();
                algorithm.setThreshold(threshold / 100);
                if (algorithm.usesThesaurus())
                    algorithm.setThesaurus(thesaurus);
            }
        }
        catch (AlgorithmException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Initialise the parameters
     */
    private void initializeParameters()
    {
        // Initialise the properties
        try
        {
            PropertiesHandler.initializeProperties(OntoBuilderResources.Config.APPLICATION_PROPERTIES);
        }
        catch (PropertyException e)
        {
            e.printStackTrace();
            return;
        }

        // Initialise the resource bundle
        try
        {
            PropertiesHandler.initializeResources(OntoBuilderResources.Config.RESOURCES_PROPERTIES, locale);
        }
        catch (ResourceException e)
        {
            e.printStackTrace();
            return;
        }
    }
}