package ac.technion.iem.ontobuilder.gui.utils.files.html;

import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JTable;

import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.ButtonINPUTElement;
import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.application.PropertiesTableModel;

/**
 * <p>Title: ButtonINPUTElement</p>
 * Extends {@link INPUTElementGui}
 */
public class ButtonINPUTElementGui extends INPUTElementGui
{
	protected ButtonINPUTElement buttonInputElement;
    protected JButton button;
    
    public ButtonINPUTElementGui(ButtonINPUTElement buttonInputElement)
    {
        super(buttonInputElement);
        this.buttonInputElement = buttonInputElement;
        button = new JButton();
    }

    public ButtonINPUTElementGui()
    {
        this(new ButtonINPUTElement());
    }

    public ButtonINPUTElementGui(String name, String value)
    {
        this(new ButtonINPUTElement(name,value));
        button.setText(value);
    }

    public void setValue(String value)
    {
    	buttonInputElement.setValue(value);
        button.setText(value);
    }

    public String getValue()
    {
        return buttonInputElement.getValue();
    }

    public void setDisabled(boolean b)
    {
        super.setDisabled(b);
        buttonInputElement.setDisabled(b);
        button.setEnabled(!b);
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
                ApplicationUtilities.getResourceString("html.input.button.name"), getName()
            },
            {
                ApplicationUtilities.getResourceString("html.input.button.value"), button.getText()
            }
        };
        return new JTable(new PropertiesTableModel(columnNames, 3, data));
    }

    public String paramString()
    {
    	return buttonInputElement.paramString();
    }

    public Component getComponent()
    {
        return button;
    }

    public boolean canSubmit()
    {
        return buttonInputElement.canSubmit();
    }
}