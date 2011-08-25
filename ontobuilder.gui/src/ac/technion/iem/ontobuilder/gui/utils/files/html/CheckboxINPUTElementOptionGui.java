package ac.technion.iem.ontobuilder.gui.utils.files.html;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JTable;

import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.CheckboxINPUTElementOption;
import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.application.PropertiesTableModel;

/**
 * <p>Title: CheckboxINPUTElementOption</p>
 * Extends {@link INPUTElementGui}
 */
public class CheckboxINPUTElementOptionGui extends INPUTElementGui
{
	protected CheckboxINPUTElementOption checkboxINPUTElementOption;
    protected CheckboxINPUTElementGui checkboxInput;

    protected JCheckBox checkbox;

    public CheckboxINPUTElementOptionGui(CheckboxINPUTElementOption checkboxINPUTElementOption)
    {
        super(checkboxINPUTElementOption);
        this.checkboxINPUTElementOption = checkboxINPUTElementOption;
        checkbox = new JCheckBox();
    }
    
    public CheckboxINPUTElementOptionGui()
    {
    	this(new CheckboxINPUTElementOption());
    }

    public CheckboxINPUTElementOptionGui(String value)
    {
        this(new CheckboxINPUTElementOption(value));
    }
    
    public CheckboxINPUTElementOption getCheckboxINPUTElementOption()
    {
    	return checkboxINPUTElementOption;
    }

    public boolean isDefaultChecked()
    {
        return checkboxINPUTElementOption.isDefaultChecked();
    }

    public void setDefaultChecked(boolean b)
    {
    	checkboxINPUTElementOption.setDefaultChecked(b);
        checkbox.setSelected(b);
    }

    public void setLabel(String label)
    {
    	checkboxINPUTElementOption.setLabel(label);
        checkbox.setText(label);
    }

    public String getLabel()
    {
        return checkboxINPUTElementOption.getLabel();
    }

    public void setValue(String value)
    {
    	checkboxINPUTElementOption.setValue(value);
    }

    public String getValue()
    {
        return checkboxINPUTElementOption.getValue();
    }

    public void setCheckbox(CheckboxINPUTElementGui checkboxInput)
    {
        this.checkboxInput = checkboxInput;
        checkboxINPUTElementOption.setCheckbox(checkboxInput.getCheckboxINPUTElement());
        checkboxINPUTElementOption.setName(checkboxInput.getName());
    }

    public CheckboxINPUTElementGui getCheckbox()
    {
        return checkboxInput;
    }

    public String toString()
    {
        return checkboxINPUTElementOption.toString();
    }

    public void setDisabled(boolean b)
    {
        super.setDisabled(b);
        checkboxINPUTElementOption.setDisabled(b);
        checkbox.setEnabled(!b);
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
                ApplicationUtilities.getResourceString("html.input.checkbox.option.name"), getName()
            },
            {
                ApplicationUtilities.getResourceString("html.input.checkbox.option.value"), getValue()
            },
            {
                ApplicationUtilities.getResourceString("html.input.checkbox.option.label"), getLabel()
            },
            {
                ApplicationUtilities.getResourceString("html.input.checkbox.option.defaultChecked"),
                new Boolean(isDefaultChecked())
            },
            {
                ApplicationUtilities.getResourceString("html.input.checkbox.option.checked"),
                new Boolean(checkbox.isSelected())
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
        return checkboxINPUTElementOption.paramString();
    }

    public Component getComponent()
    {
        return checkbox;
    }

    public void reset()
    {
        checkbox.setSelected(isDefaultChecked());
        checkboxINPUTElementOption.reset();
    }

    public void setChecked(boolean b)
    {
        checkbox.setSelected(b);
        checkboxINPUTElementOption.setChecked(b);
    }

    public boolean isChecked()
    {
        return checkbox.isSelected();
    }
}