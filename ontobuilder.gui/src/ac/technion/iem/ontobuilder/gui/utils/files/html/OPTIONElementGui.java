package ac.technion.iem.ontobuilder.gui.utils.files.html;

import javax.swing.JTable;

import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.OPTIONElement;
import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.application.PropertiesTableModel;

/**
 * <p>Title: OPTIONElement</p>
 * Extends {@link INPUTElementGui}
 */
public class OPTIONElementGui extends INPUTElementGui
{
	protected OPTIONElement optionElement;
    private SELECTElementGui select;
    
    public OPTIONElementGui(OPTIONElement optionElement)
    {
    	super(optionElement);
    	this.optionElement = optionElement;
    }

    public OPTIONElementGui()
    {
        this(new OPTIONElement());
    }

    public OPTIONElementGui(String value)
    {
        this(new OPTIONElement(value));
    }

    public boolean isDefaultSelected()
    {
        return optionElement.isDefaultSelected();
    }

    public void setDefaultSelected(boolean b)
    {
    	optionElement.setDefaultSelected(b);
    }

    public boolean isSelected()
    {
        return optionElement.isSelected();
    }

    public void setSelected(boolean b)
    {
    	optionElement.setSelected(b);
        if (select != null)
            select.updateSelectionView(this);
    }

    public void setLabel(String label)
    {
    	optionElement.setLabel(label);
        if (select != null)
            select.updateLabelView(this);
    }

    public String getLabel()
    {
        return optionElement.getLabel();
    }

    public void setValue(String value)
    {
    	optionElement.setValue(value);
    }

    public String getValue()
    {
    	return optionElement.getValue();
    }

    public void setSelect(SELECTElementGui select)
    {
        this.select = select;
        optionElement.setSelect(select.getSelectElement());
    }

    public SELECTElementGui getSelect()
    {
        return select;
    }

    public String toString()
    {
        return optionElement.toString();
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
                ApplicationUtilities.getResourceString("html.option.name"), getName()
            },
            {
                ApplicationUtilities.getResourceString("html.option.value"), getValue()
            },
            {
                ApplicationUtilities.getResourceString("html.option.label"), getLabel()
            },
            {
                ApplicationUtilities.getResourceString("html.option.defaultSelected"),
                new Boolean(isDefaultSelected())
            },
            {
                ApplicationUtilities.getResourceString("html.option.selected"),
                new Boolean(isSelected())
            }
        };
        return new JTable(new PropertiesTableModel(columnNames, 6, data));
    }

    public void reset()
    {
        optionElement.reset();
    }

	public OPTIONElement getOPTIONElement()
	{
		return optionElement;
	}
}