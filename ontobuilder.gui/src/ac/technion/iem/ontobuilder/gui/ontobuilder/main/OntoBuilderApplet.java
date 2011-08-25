package ac.technion.iem.ontobuilder.gui.ontobuilder.main;

import java.util.Locale;
import java.net.URL;
import java.net.MalformedURLException;
import java.awt.BorderLayout;
import javax.swing.JOptionPane;
import javax.swing.JApplet;

import ac.technion.iem.ontobuilder.gui.application.Application;
import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;

/**
 * <p>Title: OntoBuilderApplet</p>
 * Extends a {@link JApplet}
 */
public class OntoBuilderApplet extends JApplet
{
    private static final long serialVersionUID = 1L;

    Application application;

    /**
     * Initialize the applet
     */
    public void init()
    {
        Locale locale;
        String country = getParameter("country");
        String language = getParameter("language");
        if (country != null && language != null)
            locale = new Locale(language, country);
        else
            locale = getLocale();

        getContentPane().add(application = new OntoBuilder(this, locale), BorderLayout.CENTER);
    }

    /**
     * Show a document
     * 
     * @param filename the file name
     */
    public void showDocument(String filename)
    {
        URL codeBase = getCodeBase();
        URL url = null;

        try
        {
            url = new URL(codeBase, filename);
            getAppletContext().showDocument(url);
        }
        catch (MalformedURLException e)
        {
            JOptionPane.showMessageDialog(null, ApplicationUtilities.getResourceString("error") +
                ": " + e.getMessage(), ApplicationUtilities.getResourceString("error"),
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Exists the application
     */
    public void destroy()
    {
        if (application != null)
            application.exit();
    }
}
