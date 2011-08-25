package ac.technion.iem.ontobuilder.gui.utils.files.html;

import java.net.URL;

import javax.swing.JTable;

import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.AElement;
import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.application.PropertiesTableModel;

/**
 * <p>Title: AElement</p>
 * Extends {@link HTMLElementGui}
 */
public class AElementGui extends HTMLElementGui
{
	private AElement aElement;

    public AElementGui(String description, URL url)
    {
    	this(new AElement(description, url));
    }
    
    public AElementGui(AElement aElement)
    {
    	super(aElement);
    	this.aElement = aElement;
    }

    public String getDescription()
    {
        return aElement.getDescription();
    }

    public void setDescription(String description)
    {
        this.aElement.setDescription(description);
    }

    public URL getURL()
    {
        return aElement.getURL();
    }

    public void setURL(URL url)
    {
        this.aElement.setURL(url);
    }

    public void setName(String name)
    {
        this.aElement.setName(name);
    }

    public String getName()
    {
        return aElement.getName();
    }

    public void setTarget(String target)
    {
        this.aElement.setTarget(target);
    }

    public String getTarget()
    {
        return aElement.getTarget();
    }

    public String toString()
    {
        return aElement.getDescription() + " : " + (aElement.getURL() != null ? aElement.getURL().toExternalForm() : "");
    }

    public JTable getProperties()
    {
        String columnNames[] =
        {
            ApplicationUtilities.getResourceString("properties.attribute"),
            ApplicationUtilities.getResourceString("properties.value")
        };
        Object data[][] =
        {
            {
                ApplicationUtilities.getResourceString("html.a.description"), aElement.getDescription()
            },
            {
                ApplicationUtilities.getResourceString("html.a.url"), aElement.getURL()
            },
            {
                ApplicationUtilities.getResourceString("html.a.name"), aElement.getName()
            },
            {
                ApplicationUtilities.getResourceString("html.a.target"), aElement.getTarget()
            }
        };
        return new JTable(new PropertiesTableModel(columnNames, 4, data));
    }
}