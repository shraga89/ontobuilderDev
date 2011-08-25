package ac.technion.iem.ontobuilder.gui.utils.browser;

import java.util.ArrayList;
import java.util.Iterator;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.File;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.Action;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import ac.technion.iem.ontobuilder.core.utils.network.NetworkUtilities;
import ac.technion.iem.ontobuilder.gui.application.Application;
import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * <p>Title: Browser</p>
 * <p>Description: Web browser for viewing web pages. Uses the address bar and the browser history to navigate
 * between addresses</p>
 */
public class Browser
{
    public static final String ADDRESS_HISTORY_ENTRIES_PROPERTY = "addressHistoryEntries";
    public static final String ADDRESS_HISTORY_CLEAR_PROPERTY = "addressHistoryClear";
    public static final String URL_PROPERTY = "url";

    protected AddressBar addressBar;
    protected BrowserHistory history;

    protected JButton backButton;
    protected JButton forwardButton;
    protected JButton goButton;

    protected ArrayList<PropertyChangeListener> listeners;

    /**
     * Constructs a Browser
     *
     * @param application the {@link Application} to create the browser in
     */
    public Browser(Application application)
    {
        listeners = new ArrayList<PropertyChangeListener>();

        addressBar = new AddressBar(Integer.parseInt(application
            .getOption(Browser.ADDRESS_HISTORY_ENTRIES_PROPERTY)));
        addressBar.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                submitURL(addressBar.getCurrentURL());
                addressBar.addToHistory(addressBar.getCurrentURL());
            }
        });

        history = new BrowserHistory();

        // Back
        Action action = new AbstractAction(ApplicationUtilities.getResourceString("action.back"),
            ApplicationUtilities.getImage("back.gif"))
        {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e)
            {
                commandBack();
            }
        };
        action.putValue(Action.LONG_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.back.longDescription"));
        action.putValue(Action.SHORT_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.back.shortDescription"));
        action.putValue(
            Action.MNEMONIC_KEY,
            new Integer(KeyStroke.getKeyStroke(
                ApplicationUtilities.getResourceString("action.back.mnemonic")).getKeyCode()));
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ApplicationUtilities
            .getResourceString("action.back.accelerator")));
        action.setEnabled(false);
        backButton = new JButton(action);
        backButton.setText(null);

        // Forward
        action = new AbstractAction(ApplicationUtilities.getResourceString("action.forward"),
            ApplicationUtilities.getImage("forward.gif"))
        {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e)
            {
                commandForward();
            }
        };
        action.putValue(Action.LONG_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.forward.longDescription"));
        action.putValue(Action.SHORT_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.forward.shortDescription"));
        action.putValue(
            Action.MNEMONIC_KEY,
            new Integer(KeyStroke.getKeyStroke(
                ApplicationUtilities.getResourceString("action.forward.mnemonic")).getKeyCode()));
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ApplicationUtilities
            .getResourceString("action.forward.accelerator")));
        action.setEnabled(false);
        forwardButton = new JButton(action);
        forwardButton.setText(null);

        // Go
        action = new AbstractAction(ApplicationUtilities.getResourceString("action.go"),
            ApplicationUtilities.getImage("go.gif"))
        {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e)
            {
                commandGo();
            }
        };
        action.putValue(Action.LONG_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.go.longDescription"));
        action.putValue(Action.SHORT_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.go.shortDescription"));
        action.putValue(
            Action.MNEMONIC_KEY,
            new Integer(KeyStroke.getKeyStroke(
                ApplicationUtilities.getResourceString("action.go.mnemonic")).getKeyCode()));
        action
            .putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ApplicationUtilities
                .getResourceString("action.go.accelerator")));
        goButton = new JButton(action);
        goButton.setText(null);

        history.addChangeListener(new ChangeListener()
        {
            public void stateChanged(ChangeEvent e)
            {
                backButton.setEnabled(history.isBackAvailable());
                forwardButton.setEnabled(history.isForwardAvailable());
            }
        });
    }

    /**
     * Implementation of the "Back" button
     */
    protected void commandBack()
    {
        URL url = history.back();
        addressBar.setCurrentURL(url.toExternalForm());
        firePropertyChange(URL_PROPERTY, null, url);
    }

    /**
     * Implementation of the "Forward" button
     */
    protected void commandForward()
    {
        URL url = history.forward();
        addressBar.setCurrentURL(url.toExternalForm());
        firePropertyChange(URL_PROPERTY, null, url);
    }

    /**
     * Implementation of the "Go" button
     */
    protected void commandGo()
    {
        String text = null;
        text = addressBar.getCurrentURL();
        if (text == null || text.length() == 0)
            return;
        try
        {
            URL url = NetworkUtilities.makeURL(text);
            history.navigate(url);
            addressBar.addToHistory(url.toExternalForm());
            firePropertyChange(URL_PROPERTY, null, url);
        }
        catch (MalformedURLException e)
        {
            JOptionPane.showMessageDialog(null, ApplicationUtilities.getResourceString("error") +
                ": " + e.getMessage(), ApplicationUtilities.getResourceString("error"),
                JOptionPane.ERROR_MESSAGE);
            return;
        }
    }

    /**
     * Submits a URL to go to
     *
     * @param url the URL string
     */
    public void submitURL(String url)
    {
        try
        {
            submitURL(NetworkUtilities.makeURL(url));
        }
        catch (MalformedURLException e)
        {
            JOptionPane.showMessageDialog(null, ApplicationUtilities.getResourceString("error") +
                ": " + e.getMessage(), ApplicationUtilities.getResourceString("error"),
                JOptionPane.ERROR_MESSAGE);
            return;
        }
    }

    /**
     * Submits a URL to go to
     *
     * @param url the URL
     */
    public void submitURL(URL url)
    {
        if (url == null)
            return;
        history.navigate(url);
        addressBar.setCurrentURL(url.toExternalForm());
        firePropertyChange(URL_PROPERTY, null, url);
    }

    /**
     * Get the current URL
     *
     * @return the URL
     */
    public String getCurrentURL()
    {
        return addressBar.getCurrentURL();
    }

    /**
     * Set the current URL
     *
     * @param url the URL string
     */
    public void setCurrentURL(String url)
    {
        addressBar.setCurrentURL(url);
    }

    /**
     * Returns the address bar object
     *
     * @return the address bar
     */
    public AddressBar getAddressBar()
    {
        return addressBar;
    }

    /**
     * Clears the address history
     */
    public void clearAddressHistory()
    {
        addressBar.clearHistory();
    }

    /**
     * Loads the address history into an history file
     *
     * @param historyFile the name of the history file
     */
    public void loadAddressHistory(String historyFile)
    {
        addressBar.loadHistory(historyFile);
    }

    /**
     * Loads the address history into an history file
     *
     * @param historyFile the history file
     */
    public void loadAddressHistory(File historyFile)
    {
        addressBar.loadHistory(historyFile);
    }

    /**
     * Saves the address history into an history file 
     *
     * @param historyFile the history file name to save to
     */
    public void saveAddressHistory(String historyFile)
    {
        addressBar.saveHistory(historyFile);
    }

    public void setAddressHistoryEntries(int entries)
    {
        addressBar.setHistoryEntries(entries);
    }

    public JButton getGoButton()
    {
        return goButton;
    }

    public JButton getForwardButton()
    {
        return forwardButton;
    }

    public JButton getBackButton()
    {
        return backButton;
    }

    public void addPropertyChangeListener(PropertyChangeListener l)
    {
        listeners.add(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l)
    {
        listeners.remove(l);
    }

    protected void firePropertyChange(String property, Object oldValue, Object newValue)
    {
        for (Iterator<PropertyChangeListener> i = listeners.iterator(); i.hasNext();)
        {
            PropertyChangeListener listener = (PropertyChangeListener) i.next();
            listener.propertyChange(new PropertyChangeEvent(this, property, oldValue, newValue));
        }
    }
}