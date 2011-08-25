package ac.technion.iem.ontobuilder.gui.utils.files.html;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.UnsupportedEncodingException;

import javax.swing.JButton;
import javax.swing.JTable;

import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.SubmitINPUTElement;
import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.application.PropertiesTableModel;

/**
 * <p>Title: SubmitINPUTElement</p>
 * Extends {@link INPUTElementGui}
 */
public class SubmitINPUTElementGui extends INPUTElementGui
{
    protected JButton submit;
    protected SubmitINPUTElement submitINPUTElement;
    
    public SubmitINPUTElementGui(final SubmitINPUTElement submitINPUTElement)
    {
    	super(submitINPUTElement);
    	this.submitINPUTElement = submitINPUTElement;
        submit = new JButton();
        submit.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                if (form != null)
                    form.clearPressed();
                submitINPUTElement.setPressed(true);
            }
        });
    }

    public SubmitINPUTElementGui()
    {
    	this(new SubmitINPUTElement());
    }

    public SubmitINPUTElementGui(String name, String value)
    {
    	this(new SubmitINPUTElement(name,value));
        submit.setText(value);
    }

    public void setValue(String value)
    {
    	submitINPUTElement.setValue(value);
        submit.setText(value);
    }

    public String getValue()
    {
        return submit.getText();
    }

    public void setDisabled(boolean b)
    {
        super.setDisabled(b);
        submitINPUTElement.setDisabled(b);
        submit.setEnabled(!b);
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
                ApplicationUtilities.getResourceString("html.input.submit.name"), getName()
            },
            {
                ApplicationUtilities.getResourceString("html.input.submit.value"), submit.getText()
            }
        };
        return new JTable(new PropertiesTableModel(columnNames, 3, data));
    }

    public String paramString()
    {
        return submitINPUTElement.paramString();
    }

    public String encode(String s)
    {
        try
        {
            return java.net.URLEncoder.encode(s, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            // TODO: Handle the exception better.
            throw new RuntimeException(e);
        }
    }

    public Component getComponent()
    {
        return submit;
    }

    public boolean isPressed()
    {
        return submitINPUTElement.isPressed();
    }

    public void forcePressed()
    {
    	submitINPUTElement.forcePressed();
    }

    public void clearPressed()
    {
    	submitINPUTElement.clearPressed();
    }

    public boolean canSubmit()
    {
        return submitINPUTElement.canSubmit();
    }
}