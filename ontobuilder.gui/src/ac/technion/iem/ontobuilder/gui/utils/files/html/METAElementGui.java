package ac.technion.iem.ontobuilder.gui.utils.files.html;

import java.net.URL;

import javax.swing.JTable;

import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.METAElement;
import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.application.PropertiesTableModel;

/**
 * <p>Title: METAElement</p>
 * Extends {@link HTMLElementGui}
 */
public class METAElementGui extends HTMLElementGui
{
	protected METAElement metaElement;
	
	public METAElementGui(METAElement metaElement)
    {
        super(metaElement);
        this.metaElement = metaElement;
    }

    public METAElementGui()
    {
        this(new METAElement());
    }

    public void setHTTPEquiv(String httpEquiv)
    {
        this.metaElement.setHTTPEquiv(httpEquiv);
    }

    public String getHTTEquiv()
    {
        return this.metaElement.getHTTEquiv();
    }

    public void setName(String name)
    {
    	this.metaElement.setName(name);
    }

    public String getName()
    {
        return this.metaElement.getName();
    }

    public void setContent(String content, URL documentURL)
    {
    	this.metaElement.setContent(content, documentURL);
    }

    public String getContent()
    {
        return this.metaElement.getContent();
    }

    public void setRefreshTime(int refreshTime)
    {
    	this.metaElement.setRefreshTime(refreshTime);
    }

    public int returnRefreshTime()
    {
        return this.metaElement.returnRefreshTime();
    }

    public void setURL(URL url)
    {
    	this.metaElement.setURL(url);
    }

    public URL getURL()
    {
        return this.metaElement.getURL();
    }

    public String toString()
    {
        return this.metaElement.toString();
    }

    public JTable getProperties()
    {
        String columnNames[] =
        {
            ApplicationUtilities.getResourceString("properties.attribute"),
            ApplicationUtilities.getResourceString("properties.value")
        };
        Object data[][];
        if (getHTTEquiv() != null && getHTTEquiv().length() > 0)
        {
            if (getHTTEquiv().equals("refresh"))
            {
                data = new Object[3][2];
                data[0][0] = "http-equiv";
                data[0][1] = getHTTEquiv();
                data[1][0] = ApplicationUtilities.getResourceString("html.meta.url");
                data[1][1] = getURL().toExternalForm();
                data[2][0] = ApplicationUtilities.getResourceString("html.meta.refresh");
                data[2][1] = new Integer(returnRefreshTime());
            }
            else
            {
                data = new Object[2][2];
                data[0][0] = "http-equiv";
                data[0][1] = getHTTEquiv();
                data[1][0] = ApplicationUtilities.getResourceString("html.meta.content");
                data[1][1] = getContent();
            }
        }
        else
        {
            data = new Object[2][2];
            data[0][0] = ApplicationUtilities.getResourceString("html.meta.name");
            data[0][1] = getName();
            data[1][0] = ApplicationUtilities.getResourceString("html.meta.content");
            data[1][1] = getContent();
        }
        return new JTable(new PropertiesTableModel(columnNames, 2, data));
    }
}