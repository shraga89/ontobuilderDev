package ac.technion.iem.ontobuilder.gui.utils.files.html;

import java.awt.Component;

import javax.swing.JTable;

import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.HiddenINPUTElement;
import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.application.PropertiesTableModel;
import ac.technion.iem.ontobuilder.gui.elements.TextField;

/**
 * <p>Title: HiddenINPUTElement</p>
 * Extends {@link INPUTElementGui}
 */
public class HiddenINPUTElementGui extends INPUTElementGui
{
	protected HiddenINPUTElement hiddenINPUTElement;
    protected TextField hidden;

    public HiddenINPUTElementGui(HiddenINPUTElement hiddenINPUTElement)
    {
        super(hiddenINPUTElement);
        this.hiddenINPUTElement = hiddenINPUTElement;
        hidden = new TextField(ApplicationUtilities.getIntProperty("html.input.hidden.size"));
    }
    
    public HiddenINPUTElementGui()
    {
    	this(new HiddenINPUTElement());
    }

    public HiddenINPUTElementGui(String name, String value)
    {
        this(new HiddenINPUTElement(name,value));
    }

    public void setValue(String value)
    {
        this.hiddenINPUTElement.setValue(value);
        hidden.setText(value);
    }

    public String getValue()
    {
        return this.hiddenINPUTElement.getValue();
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
                ApplicationUtilities.getResourceString("html.input.type"), getInputType()
            },
            {
                ApplicationUtilities.getResourceString("html.input.hidden.name"), getName()
            },
            {
                ApplicationUtilities.getResourceString("html.input.hidden.value"), getValue()
            },
            {
                ApplicationUtilities.getResourceString("html.input.disabled"),
                new Boolean(isDisabled())
            }
        };
        return new JTable(new PropertiesTableModel(columnNames, 4, data));
    }

    public String paramString()
    {
    	return this.hiddenINPUTElement.paramString();
    }

    public Component getComponent()
    {
        return hidden;
    }
}