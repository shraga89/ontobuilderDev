package ac.technion.iem.ontobuilder.core.utils.properties;

import ac.technion.iem.ontobuilder.core.resources.OntoBuilderResources;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;

/**
 * <p>Title: PropertiesHandler</p>
 * <p>Description: Handles reading resources from the application.properties file</p> 
 */
public class PropertiesHandler
{
    private static Properties properties = null;

    // Resource bundle for internationalised and accessible text
    // private static ResourceBundle bundle = null;
    private static Properties resources = null;

    private static String currentDirectory = getApplicationDirectory();
    
    // System Properties Methods

    /**
     * Initialises the properties according to a properties file
     * 
     * @param propertiesFileName the name of the properties file
     */
    public static void initializeProperties(String propertiesFileName) throws PropertyException
    {
        try
        {
            ClassLoader classLoader = PropertiesHandler.class.getClassLoader();
            InputStream propertiesStream = classLoader.getResourceAsStream(propertiesFileName);
            if (propertiesStream == null) {
                throw new IllegalArgumentException("file not found! " + propertiesFileName);
            } else {
                properties = new java.util.Properties();
                properties.load(propertiesStream);
            }
        }
        catch (IOException e)
        {
            throw new PropertyException("There was an error trying to read properties from '" +
                    propertiesFileName + "'. cause: " + e.getMessage());
        }
    }

    /**
     * Get a string property
     * 
     * @param key the key of the property
     * @return the property string
     * @throws PropertyException when the property was not found in the current property file
     */
    public static String getStringProperty(String key) throws PropertyException
    {
        if (properties == null)
            throw new PropertyException("The property file has not been specified");
        String property = properties.getProperty(key);
        if (property == null)
            throw new PropertyException("The property '" + key +
                "' was not found in the current property file.");
        return property;
    }

    /**
     * Get the integer value of a property
     * 
     * @param key the key of the property
     * @return the integer value
     * @throws PropertyException when the property was not found in the current property file
     */
    public static int getIntProperty(String key) throws PropertyException
    {
        String property = getStringProperty(key);
        try
        {
            int value = Integer.parseInt(property);
            return value;
        }
        catch (NumberFormatException e)
        {
            throw new PropertyException("The property '" + key + "' is not an int value (" + key +
                "=" + property + ")");
        }
    }

    /**
     * Get the float value of a property
     * 
     * @param key the key of the property
     * @return the float value
     * @throws PropertyException when the property was not found in the current property file
     */
    public static float getFloatProperty(String key) throws PropertyException
    {
        String property = getStringProperty(key);
        try
        {
            float value = Float.parseFloat(property);
            return value;
        }
        catch (NumberFormatException e)
        {
            throw new PropertyException("The property '" + key + "' is not a float value (" + key +
                "=" + property + ")");
        }
    }

    /**
     * Get the date value of a property
     * 
     * @param key the key of the property
     * @return the date value
     * @throws PropertyException when the property was not found in the current property file
     */
    public static Date getDateProperty(String key) throws PropertyException
    {
        String property = getStringProperty(key);
        try
        {
            Date value = new SimpleDateFormat().parse(property);
            return value;
        }
        catch (ParseException e)
        {
            throw new PropertyException("The property '" + key + "' is not a date value (" + key +
                "=" + property + ")");
        }
    }

    // Resource Bundle Methods

    /**
     * Initialises the resources according to the resource file
     * 
     * @param resourceFileName the resource file
     */
    public static void initializeResources(String resourceFileName, Locale locale)
        throws ResourceException
    {

            ClassLoader classLoader = PropertiesHandler.class.getClassLoader();
            InputStream propertiesStream = classLoader.getResourceAsStream(resourceFileName);
            if (propertiesStream == null) {
                throw new IllegalArgumentException("file not found! " + resourceFileName);
            } else {
                try {
                    resources = new java.util.Properties();
                    resources.load(propertiesStream);
                } catch (IOException e) {
                    throw new ResourceException("The resource '" + resourceFileName + "' cannot be found.");
                }
            }
    }

    /**
     * Get the resource string according to a property key
     * 
     * @param key the property key
     * @return the string value
     * @throws ResourceException when the the resource bundle file has not been specified or the key
     * was not found
     */
    public static String getResourceString(String key) throws ResourceException
    {
        if (resources == null)
            initializeResources(OntoBuilderResources.Config.RESOURCES_PROPERTIES,  Locale.getDefault());
        try
        {
            String value = resources.getProperty(key);
            return value;
        }
        catch (MissingResourceException e)
        {
            throw new ResourceException("The key '" + key +
                "' was not found in the current resource bundle.");
        }

    }

    /**
     * Get the application directory
     * 
     * @return the directory
     */
    public static String getApplicationDirectory()
    {
        return System.getProperty("user.dir", ".") + System.getProperty("file.separator", "/");
    }

    /**
     * Set the current directory
     * 
     * @param dir the directory to set
     */
    public static void setCurrentDirectory(String dir)
    {
        currentDirectory = dir;
    }

    /**
     * Get the current directory
     * 
     * @return the current directory
     */
    public static String getCurrentDirectory()
    {
        return currentDirectory;
    }
}
