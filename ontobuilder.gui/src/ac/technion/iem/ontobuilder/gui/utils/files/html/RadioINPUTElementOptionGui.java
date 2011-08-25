package ac.technion.iem.ontobuilder.gui.utils.files.html;

import java.awt.Component;

import javax.swing.JRadioButton;
import javax.swing.JTable;

import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.RadioINPUTElementOption;
import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.application.PropertiesTableModel;

/**
 * <p>Title: RadioINPUTElementOption</p>
 * Extends {@link INPUTElementGui}
 */
public class RadioINPUTElementOptionGui extends INPUTElementGui
{
    protected RadioINPUTElementOption radioINPUTElementOption;
    protected RadioINPUTElementGui radioInput;

    protected JRadioButton radio;

    public RadioINPUTElementOptionGui(RadioINPUTElementOption radioINPUTElementOption)
    {
    	super(radioINPUTElementOption);
    	this.radioINPUTElementOption = radioINPUTElementOption;
        radio = new JRadioButton();
    }
    
    public RadioINPUTElementOptionGui()
    {
        this(new RadioINPUTElementOption());
    }

    public RadioINPUTElementOptionGui(String value)
    {
        this(new RadioINPUTElementOption(value));
    }

	public RadioINPUTElementOption getRadioINPUTElementOption()
	{
		return radioINPUTElementOption;
	}

    public boolean isDefaultChecked()
    {
        return radioINPUTElementOption.isDefaultChecked();
    }

    public void setDefaultChecked(boolean b)
    {
    	radioINPUTElementOption.setDefaultChecked(b);
        radio.setSelected(b);
    }

    public void setValue(String value)
    {
    	radioINPUTElementOption.setValue(value);
    }

    public String getValue()
    {
        return radioINPUTElementOption.getValue();
    }

    public void setLabel(String label)
    {
    	radioINPUTElementOption.setLabel(label);
        radio.setText(label);
    }

    public String getLabel()
    {
        return radioINPUTElementOption.getLabel();
    }

    public void setRadio(RadioINPUTElementGui radioInput)
    {
        this.radioInput = radioInput;
        radioINPUTElementOption.setRadio(radioInput.getRadioINPUTElement());
    }

    public RadioINPUTElementGui getRadio()
    {
        return radioInput;
    }

    public String toString()
    {
        return radioINPUTElementOption.toString();
    }

    public void setDisabled(boolean b)
    {
        super.setDisabled(b);
        radioINPUTElementOption.setDisabled(b);
        radio.setEnabled(!b);
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
                ApplicationUtilities.getResourceString("html.input.radio.option.name"), getName()
            },
            {
                ApplicationUtilities.getResourceString("html.input.radio.option.value"), getValue()
            },
            {
                ApplicationUtilities.getResourceString("html.input.radio.option.label"), getLabel()
            },
            {
                ApplicationUtilities.getResourceString("html.input.radio.option.defaultChecked"),
                new Boolean(isDefaultChecked())
            },
            {
                ApplicationUtilities.getResourceString("html.input.radio.option.checked"),
                new Boolean(radio.isSelected())
            },
            {
                ApplicationUtilities.getResourceString("html.input.disabled"),
                new Boolean(isDisabled())
            }
        };
        return new JTable(new PropertiesTableModel(columnNames, 7, data));
    }

    public String paramString()
    {
        return radioINPUTElementOption.paramString();
    }

    public Component getComponent()
    {
        return radio;
    }

    public void reset()
    {
        radio.setSelected(isDefaultChecked());
        radioINPUTElementOption.reset();
    }

    public void setChecked(boolean b)
    {
    	radioINPUTElementOption.setChecked(b);
        radio.setSelected(b);
    }

    public boolean isChecked()
    {
        return radio.isSelected();
    }
}