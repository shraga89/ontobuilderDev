package ac.technion.iem.ontobuilder.core.utils.properties;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import ac.technion.iem.ontobuilder.core.utils.StringUtilities;
import ac.technion.iem.ontobuilder.core.utils.network.NetworkEntityResolver;

/**
 * <p>Title: ApplicationOptions</p>
 * <p>Description: A handler for the application's options (settings). Enables to save, load, reset, etc.</p>
 */
public class ApplicationOptions
{
    protected Hashtable<String, Option> options;
    protected ArrayList<PropertyChangeListener> listeners;

    /**
     * Constructs a default ApplicationOptions
     */
    public ApplicationOptions()
    {
        options = new Hashtable<String, Option>();
        listeners = new ArrayList<PropertyChangeListener>();
    }

    /**
     * Load the options according to an options file

     * @param optionFile the options file
     * @throws OptionException
     */
    public void loadOptions(File optionFile) throws OptionException
    {
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(optionFile));
            // if(reader==null)
            // {
            // String params[]={optionFile.getAbsolutePath()};
            // throw new
            // OptionException(StringUtilities.getReplacedString(ApplicationUtilities.getResourceString("error.options.file"),params));
            // }
            SAXBuilder builder = new SAXBuilder(true);
            builder.setEntityResolver(new NetworkEntityResolver());
            Document doc = builder.build(reader);
            loadOptionsFromDocument(doc);
        }
        catch (FileNotFoundException e)
        {
            throw new OptionException(e.getMessage());
        }
        catch (JDOMException e)
        {
            //e.printStackTrace();
            //
            throw new OptionException(e.getMessage());
        }
    }

    /**
     * Load the options according to an options file
     *
     * @param optionFile the name of the options file
     * @throws OptionException
     */
    public void loadOptions(String optionFile) throws OptionException
    {
        InputStream configStream = getClass().getResourceAsStream(optionFile);
        if (configStream == null)
        {
            String params[] =
            {
                optionFile
            };
            throw new OptionException(StringUtilities.getReplacedString(
                PropertiesHandler.getResourceString("error.options.file"), params));
        }

        try
        {
            SAXBuilder builder = new SAXBuilder(true);
            builder.setEntityResolver(new NetworkEntityResolver());
            Document doc = builder.build(configStream);
            loadOptionsFromDocument(doc);
        }
        catch (JDOMException e)
        {
            throw new OptionException(e.getMessage());
        }
    }

    /**
     * Load options from a document
     *
     * @param doc the document (possibly an XML)
     */
    protected void loadOptionsFromDocument(Document doc)
    {
        Element configuration = doc.getRootElement();
        java.util.List<?> parameters = configuration.getChildren("parameter");
        for (Iterator<?> i = parameters.iterator(); i.hasNext();)
        {
            Element parameter = (Element) i.next();
            String optionName = parameter.getChild("name").getText();
            String optionValue = parameter.getChild("value").getText();
            String optionDefault = null;
            if (parameter.getChild("default") != null)
                optionDefault = parameter.getChild("default").getText();
            options.put(optionName, new Option(optionName, optionValue, optionDefault));
        }
    }

    /**
     * Save the current options in an options file 
     *
     * @param optionFile the name of the options file to save in
     * @throws OptionException when the option name was not found
     */
    public void saveOptions(String optionFile) throws OptionException
    {
        try
        {
            BufferedWriter out = new BufferedWriter(new FileWriter(optionFile));

            Element root = new Element("configuration");
            Document doc = new Document(root);

            DocType configDocType = new DocType("configuration", "dtds/configuration.dtd");
            doc.setDocType(configDocType);

            for (Enumeration<String> e = options.keys(); e.hasMoreElements();)
            {
                Option o = (Option) options.get(e.nextElement());
                Element parameter = new Element("parameter");
                root.addContent(parameter);
                Element name = new Element("name").setText(o.getOptionName());
                parameter.addContent(name);
                Element value = new Element("value");
                if (o.getOptionValue() != null)
                    value.setText((String) o.getOptionValue());
                parameter.addContent(value);
                if (o.getOptionDefault() != null)
                {
                    Element def = new Element("default").setText((String) o.getOptionDefault());
                    parameter.addContent(def);
                }
            }

            XMLOutputter fmt = new XMLOutputter("    ", true);
            fmt.output(doc, out);

            out.close();
        }
        catch (IOException e)
        {
            throw new OptionException(e.getMessage());
        }
    }

    /**
     * Get an option value
     *
     * @param optionName the name of the option
     * @return the string value of the option
     * @throws OptionException when the option name was not found
     */
    public String getOptionValue(String optionName) throws OptionException
    {
        Option option = options.get(optionName);
        if (option == null)
        {
            String params[] =
            {
                optionName
            };
            throw new OptionException(StringUtilities.getReplacedString(
                PropertiesHandler.getResourceString("error.option.notFound"), params));
        }
        return option.getOptionValue();
    }

    /**
     * Reset an option value
     *
     * @param optionName the name of the option to reset
     * @throws OptionException when the option name was not found
     */
    public void resetOption(String optionName) throws OptionException
    {
        Option option = options.get(optionName);
        if (option == null)
        {
            String params[] =
            {
                optionName
            };
            throw new OptionException(StringUtilities.getReplacedString(
                PropertiesHandler.getResourceString("error.options.notFound"), params));
        }
        String oldValue = option.getOptionValue();
        option.reset();
        String newValue = option.getOptionValue();
        if (newValue != null && !newValue.equals(oldValue))
            firePropertyChange(option.getOptionName(), oldValue, newValue);
    }

    /**
     * Set an option's value
     *
     * @param optionName the option's name
     * @param optionValue the options's value
     * @throws OptionException when the option name was not found
     */
    public void setOptionValue(String optionName, String optionValue) throws OptionException
    {
        Option option = options.get(optionName);
        if (option == null)
        {
            String params[] =
            {
                optionName
            };
            throw new OptionException(StringUtilities.getReplacedString(
                PropertiesHandler.getResourceString("error.options.notFound"), params));
        }
        String oldValue = (String) option.getOptionValue();
        option.setOptionValue(optionValue);
        if (!optionValue.equals(oldValue))
            firePropertyChange(optionName, oldValue, optionValue);
    }

    /**
     * 
     * Tickles and option - trigger a property change
     *
     * @param optionName the name of the option to tickle
     * @throws OptionException when the option name was not found
     */
    public void tickleOption(String optionName) throws OptionException
    {
        Option option = options.get(optionName);
        if (option == null)
        {
            String params[] =
            {
                optionName
            };
            throw new OptionException(StringUtilities.getReplacedString(
                PropertiesHandler.getResourceString("error.options.notFound"), params));
        }
        firePropertyChange(optionName, null, null);
    }

    /**
     * Adds an option
     *
     * @param optionName the option name
     * @param value the option value
     */
    public void addOption(String optionName, String value)
    {
        options.put(optionName, new Option(optionName, value));
    }

    /**
     * Get the default value of an option
     *
     * @param optionName the option name
     * @return the option
     * @throws OptionException when the option name was not found
     */
    public String getOptionDefault(String optionName) throws OptionException
    {
        Option option = options.get(optionName);
        if (option == null)
        {
            String params[] =
            {
                optionName
            };
            throw new OptionException(StringUtilities.getReplacedString(
                PropertiesHandler.getResourceString("error.options.notFound"), params));
        }
        return option.getOptionDefault();
    }

    /**
     * Resets all the options
     */
    public void resetOptions()
    {
        for (Enumeration<String> keys = options.keys(); keys.hasMoreElements();)
            resetOption(keys.nextElement());
    }

    public void addPropertyChangeListener(PropertyChangeListener l)
    {
        listeners.add(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l)
    {
        listeners.remove(l);
    }

    /**
     * Trigger a property change
     *
     * @param property the property
     * @param oldValue the old value of the property
     * @param newValue the new value of the property
     */
    public void firePropertyChange(String property, Object oldValue, Object newValue)
    {
        for (Iterator<PropertyChangeListener> i = listeners.iterator(); i.hasNext();)
        {
            PropertyChangeListener listener = (PropertyChangeListener) i.next();
            listener.propertyChange(new PropertyChangeEvent(this, property, oldValue, newValue));
        }
    }
}