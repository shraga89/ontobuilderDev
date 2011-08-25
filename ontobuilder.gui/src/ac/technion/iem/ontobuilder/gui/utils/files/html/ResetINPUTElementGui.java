package ac.technion.iem.ontobuilder.gui.utils.files.html;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JTable;

import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.ResetINPUTElement;
import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.application.PropertiesTableModel;

/**
 * <p>Title: ResetINPUTElement</p>
 * Extends {@link INPUTElementGui}
 */
public class ResetINPUTElementGui extends INPUTElementGui
{
    protected JButton reset;
    protected ResetINPUTElement resetINPUTElement;
    
    public ResetINPUTElementGui(ResetINPUTElement resetINPUTElement)
    {
    	super(resetINPUTElement);
    	this.resetINPUTElement = resetINPUTElement;
        reset = new JButton();
        reset.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                if (form != null)
                    form.reset();
            }
        });
    }

    public ResetINPUTElementGui()
    {
        this(new ResetINPUTElement());
    }

    public ResetINPUTElementGui(String name, String value)
    {
        this(new ResetINPUTElement(name, value));
        reset.setText(value);
    }

    public void setDisabled(boolean b)
    {
        super.setDisabled(b);
        resetINPUTElement.setDisabled(b);
        reset.setEnabled(!b);
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
                ApplicationUtilities.getResourceString("html.input.reset.name"), getName()
            },
            {
                ApplicationUtilities.getResourceString("html.input.reset.value"), reset.getText()
            }
        };
        return new JTable(new PropertiesTableModel(columnNames, 3, data));
    }

    public String paramString()
    {
    	return resetINPUTElement.paramString();
    }

    public Component getComponent()
    {
        return reset;
    }

    public void setValue(String value)
    {
    	resetINPUTElement.setValue(value);
        reset.setText(value);
    }

    public String getValue()
    {
        return reset.getText();
    }

    public boolean canSubmit()
    {
        return resetINPUTElement.canSubmit();
    }
}