package ac.technion.iem.ontobuilder.gui.utils.files.html;

import java.net.URL;

import javax.swing.JTable;

import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.FRAMEElement;
import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.application.PropertiesTableModel;

/**
 * <p>Title: FRAMEElement</p>
 * Extends {@link HTMLElementGui}
 */
public class FRAMEElementGui extends HTMLElementGui
{
	protected FRAMEElement frameElement;
	
	public FRAMEElementGui(FRAMEElement frameElement)
	{
		super(frameElement);
		this.frameElement = frameElement;
	}

    public FRAMEElementGui(URL src, String name)
    {
		this(new FRAMEElement(src,name));
    }

    public boolean isInternal()
    {
        return this.frameElement.isInternal();
    }

    public void setInternal(boolean b)
    {
    	this.frameElement.setInternal(b);
    }

    public void setSrc(URL src)
    {
    	this.frameElement.setSrc(src);
    }

    public URL getSrc()
    {
        return this.frameElement.getSrc();
    }

    public void setName(String name)
    {
    	this.frameElement.setName(name);
    }

    public String getName()
    {
        return this.frameElement.getName();
    }

    public String toString()
    {
        return this.frameElement.toString();
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
                ApplicationUtilities.getResourceString("html.frame.name"), getName()
            },
            {
                ApplicationUtilities.getResourceString("html.frame.source"), getSrc()
            },
            {
                ApplicationUtilities.getResourceString("html.frame.internal"),
                new Boolean(isInternal())
            }
        };
        return new JTable(new PropertiesTableModel(columnNames, 3, data));
    }
}