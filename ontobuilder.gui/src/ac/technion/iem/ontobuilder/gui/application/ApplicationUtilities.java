package ac.technion.iem.ontobuilder.gui.application;

import java.io.File;
import java.net.URL;
import java.util.Date;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import ac.technion.iem.ontobuilder.core.utils.properties.PropertiesHandler;
import ac.technion.iem.ontobuilder.core.utils.properties.PropertyException;
import ac.technion.iem.ontobuilder.core.utils.properties.ResourceException;

/**
 * <p>Title: ApplicationUtilities</p>
 * <p>Description: Various internal utility functions for the application</p> 
 */
public class ApplicationUtilities
{
    public static void initializeProperties(String propertiesFile) throws PropertyException
    {
        PropertiesHandler.initializeProperties(propertiesFile);
    }
    
    public static void initializeResources(String resourceFile, Locale locale)
    {
        PropertiesHandler.initializeResources(resourceFile, locale);
    }

    // System Properties Methods
    
    /**
     * Get a string property
     * 
     * @param key the key of the property
     * @return the property string
     * @throws PropertyException when the property was not found in the current property file
     */
    public static String getStringProperty(String key) throws PropertyException
    {
        return PropertiesHandler.getStringProperty(key);
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
        return PropertiesHandler.getIntProperty(key);
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
        return PropertiesHandler.getFloatProperty(key);
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
        return PropertiesHandler.getDateProperty(key);
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
        return PropertiesHandler.getResourceString(key);

    }
    
    /**
     * Get the application directory
     * 
     * @return the directory
     */
    public static String getApplicationDirectory()
    {
        return PropertiesHandler.getApplicationDirectory();
    }

    /**
     * Set the current directory
     * 
     * @param dir the directory to set
     */
    public static void setCurrentDirectory(String dir)
    {
        PropertiesHandler.setCurrentDirectory(dir);
    }
    
    /**
     * Get the current directory
     * 
     * @return the current directory
     */
    public static String getCurrentDirectory()
    {
        return PropertiesHandler.getCurrentDirectory();
    }
    
    // Look & Feel
    /**
     * Initialise the look and feel
     */
    public static void initLookAndFeel(String look)
    {
        String lookAndFeel = null;

        if (look != null)
        {
            if (look.equals("Metal"))
                lookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();
            else if (look.equals("System"))
                lookAndFeel = UIManager.getSystemLookAndFeelClassName();
            else if (look.equals("Mac"))
                lookAndFeel = "com.sun.java.swing.plaf.mac.MacLookAndFeel";
            else if (look.equals("Windows"))
                lookAndFeel = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
            else if (look.equals("Motif"))
                lookAndFeel = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
            try
            {
                UIManager.setLookAndFeel(lookAndFeel);
                UIManager.put("Label.foreground", java.awt.Color.black);
                UIManager.put("Label.font", new java.awt.Font(UIManager.getFont("Label.font")
                    .getName(), java.awt.Font.PLAIN, UIManager.getFont("Label.font").getSize()));
                UIManager.put("TitledBorder.titleColor", java.awt.Color.black);
                UIManager.put("TitledBorder.font",
                    new java.awt.Font(UIManager.getFont("TitledBorder.font").getName(),
                        java.awt.Font.PLAIN, UIManager.getFont("TitledBorder.font").getSize()));
            }
            catch (ClassNotFoundException e)
            {
            }
            catch (UnsupportedLookAndFeelException e)
            {
            }
            catch (Exception e)
            {
            }
        }
    }

    // Images
    /**
     * Get an image icon
     * 
     * @param icon the name of the icon
     * @return ImageIcon
     * @throws ResourceException when cannot load the image
     */
    public static ImageIcon getImage(String icon) throws ResourceException
    {   
        URL url = ApplicationUtilities.class.getResource("/images/" + icon);
        if (url == null)
        {
            try
            {
                url = new File("images", icon).toURI().toURL();
            }
            catch (Exception e)
            {
                throw new ResourceException("Can't load image");
            }
        }
        return new ImageIcon(url);
    }


}