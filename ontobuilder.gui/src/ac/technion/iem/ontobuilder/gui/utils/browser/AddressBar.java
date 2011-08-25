package ac.technion.iem.ontobuilder.gui.utils.browser;

import java.util.ArrayList;
import java.io.*;
import java.awt.Component;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.*;

import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.elements.ComboBox;

/**
 * <p>Title: AddressBar</p>
 * <p>Description: Address bar component for the browser</p>
 * Extends {@link JComponent}
 */
public class AddressBar extends JComponent
{
    private static final long serialVersionUID = 1L;

    protected ArrayList<?> history;
    protected ComboBox addressField;
    protected int historyEntries;
    protected String historyFile;

    /**
     * Constructs an AddressBar
     * 
     * @param historyEntries the number of history entries
     */
    public AddressBar(int historyEntries)
    {
        this.historyEntries = historyEntries;

        setLayout(new BorderLayout());
        add(new JLabel(ApplicationUtilities.getResourceString("addressBar.label") + " "),
            BorderLayout.WEST);

        addressField = new ComboBox();
        addressField.setIcon(ApplicationUtilities.getImage("htmlfile.gif"));
        addressField.setEditable(true);
        addressField.setPrototypeDisplayValue("wwwwwwwwwwwwwwwwwww");
        add(addressField, BorderLayout.CENTER);

        history = new ArrayList<Object>();

        addressField.setRenderer(new ListCellRenderer()
        {
            public Component getListCellRendererComponent(JList list, Object value, int index,
                boolean isSelected, boolean cellHasFocus)
            {
                return new JLabel((String) value, ApplicationUtilities.getImage("htmlfile.gif"),
                    SwingConstants.LEFT);
            }
        });

        addressField.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
            }
        });
    }

    /**
     * Set the current URL
     *
     * @param text the URL
     */
    public void setCurrentURL(String text)
    {
        addressField.setText(text);
    }

    /**
     * Get the current URL
     *
     * @return the URL
     */
    public String getCurrentURL()
    {
        return addressField.getText();
    }

    /**
     * Load the history
     *
     * @param historyFile name of the history file to load from
     */
    protected void loadHistory(String historyFile)
    {
        try
        {
            InputStream stream = getClass().getResourceAsStream(historyFile);
            if (stream == null)
                return;
            BufferedReader in = new BufferedReader(new InputStreamReader(stream));
            String line;
            int entries = 0;
            while ((line = in.readLine()) != null && entries < historyEntries)
            {
                addressField.addItem(line);
                entries++;
            }
            in.close();
            addressField.setSelectedIndex(-1);
            this.historyFile = historyFile;
        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog(null, ApplicationUtilities.getResourceString("error") +
                ": " + e.getMessage(), ApplicationUtilities.getResourceString("error"),
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Load the history
     *
     * @param historyFile history file to load from
     */
    protected void loadHistory(File historyFile)
    {
        try
        {
            if (historyFile == null)
                return;
            BufferedReader in = new BufferedReader(new FileReader(historyFile));
            String line;
            int entries = 0;
            while ((line = in.readLine()) != null && entries < historyEntries)
            {
                addressField.addItem(line);
                entries++;
            }
            in.close();
            addressField.setSelectedIndex(-1);
            this.historyFile = historyFile.getAbsolutePath();
        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog(null, ApplicationUtilities.getResourceString("error") +
                ": " + e.getMessage(), ApplicationUtilities.getResourceString("error"),
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Save the history into a file
     *
     * @param historyFile name of the history file to save to
     */
    protected void saveHistory(String historyFile)
    {
        try
        {
            BufferedWriter out = new BufferedWriter(new FileWriter(historyFile));
            for (int i = 0; i < addressField.getItemCount() - 1; i++)
            {
                out.write((String) addressField.getItemAt(i));
                out.newLine();
            }
            if (addressField.getItemCount() > 0)
                out.write((String) addressField.getItemAt(addressField.getItemCount() - 1));
            out.close();
        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog(null, ApplicationUtilities.getResourceString("error") +
                ": " + e.getMessage(), ApplicationUtilities.getResourceString("error"),
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Set the number of history entries
     *
     * @param entries the number of entries
     */
    public void setHistoryEntries(int entries)
    {
        this.historyEntries = entries;
        addressField.removeAllItems();
        loadHistory(historyFile);
    }

    /**
     * Add the current address into the history
     */
    protected void addToHistory()
    {
        String url = (String) addressField.getSelectedItem();
        addToHistory(url);
    }

    /**
     * Add a specific URL into the history
     *
     * @param url the URL to add
     */
    protected void addToHistory(String url)
    {
        if (url == null || url.length() == 0)
            return;

        // Rearrange History List
        boolean change = true;
        int selectedID = -1;

        ArrayList<Object> items = new ArrayList<Object>();
        for (int i = 0; i < addressField.getItemCount(); i++)
            items.add(addressField.getItemAt(i));

        for (int i = 0; i < items.size(); i++)
        {
            if (((String) items.get(i)).equals(url))
            {
                change = false;
                selectedID = i;
                break;
            }
        }
        if (change)
        {
            if (addressField.getItemCount() < historyEntries)
                addressField.insertItemAt(url, 0);
            else
            {
                items.add(0, url);
                items.remove(items.size() - 1);
                addressField.removeAllItems();
                for (int i = 0; i < items.size(); i++)
                    addressField.addItem(items.get(i));
            }
        }
        else
        {
            items.remove(selectedID);
            addressField.removeAllItems();
            addressField.addItem(url);
            for (int i = 0; i < items.size(); i++)
                addressField.addItem(items.get(i));
        }
        addressField.setSelectedIndex(0);
    }

    /**
     * Clears the history
     */
    public void clearHistory()
    {
        addressField.removeAllItems();
    }

    /**
     * Get the selected address
     *
     * @return the address
     */
    public String getSelectedItem()
    {
        return (String) addressField.getSelectedItem();
    }

    /**
     * Get the index of the selected address
     *
     * @return the index
     */
    public int getSelectedIndex()
    {
        return addressField.getSelectedIndex();
    }

    public void addActionListener(ActionListener l)
    {
        addressField.getEditor().addActionListener(l);
    }

    public void removeActionListener(ActionListener l)
    {
        addressField.getEditor().removeActionListener(l);
    }
}