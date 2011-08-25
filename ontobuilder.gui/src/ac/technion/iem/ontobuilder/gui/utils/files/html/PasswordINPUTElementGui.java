package ac.technion.iem.ontobuilder.gui.utils.files.html;

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JPasswordField;
import javax.swing.JTable;

import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.PasswordINPUTElement;
import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.application.PropertiesTableModel;

/**
 * <p>Title: PasswordINPUTElement</p>
 * Extends {@link INPUTElementGui}
 */
public class PasswordINPUTElementGui extends INPUTElementGui
{
	protected PasswordINPUTElement passwordINPUTElement;

    protected JPasswordField password;
    
    public PasswordINPUTElementGui(final PasswordINPUTElement passwordINPUTElement)
    {
    	super(passwordINPUTElement);
    	this.passwordINPUTElement = passwordINPUTElement;
        password = new JPasswordField(
            ApplicationUtilities.getIntProperty("html.input.password.size"));
        password.addKeyListener(new KeyAdapter()
        {
            public void keyTyped(KeyEvent e)
            {
                if (passwordINPUTElement.getMaxLength() > 0 && password.getPassword().length >= passwordINPUTElement.getMaxLength())
                    e.consume();
            }
        });
    }

    public PasswordINPUTElementGui()
    {
        this(new PasswordINPUTElement());
    }

    public PasswordINPUTElementGui(String name, String defaultValue)
    {
    	this(new PasswordINPUTElement(name,defaultValue));
    }

    public void setDefaultValue(String defaultValue)
    {
    	passwordINPUTElement.setDefaultValue(defaultValue);
        password.setText(defaultValue);
    }

    public String getDefaultValue()
    {
        return passwordINPUTElement.getDefaultValue();
    }

    public void setLabel(String label)
    {
    	passwordINPUTElement.setLabel(label);
    }

    public String getLabel()
    {
        return passwordINPUTElement.getLabel();
    }

    public void setSize(int size)
    {
    	passwordINPUTElement.setSize(size);
        if (size >= 0)
            password.setColumns(size);
    }

    public int getSize()
    {
        return passwordINPUTElement.getSize();
    }

    public void setMaxLength(int maxLength)
    {
    	passwordINPUTElement.setMaxLength(maxLength);
    }

    public int getMaxLength()
    {
        return passwordINPUTElement.getMaxLength();
    }

    public void setReadOnly(boolean b)
    {
    	passwordINPUTElement.setReadOnly(b);
        password.setEditable(!b);
    }

    public boolean isReadOnly()
    {
        return passwordINPUTElement.isReadOnly();
    }

    public void setDisabled(boolean b)
    {
        super.setDisabled(b);
        passwordINPUTElement.setDisabled(b);
        password.setEnabled(!b);
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
                ApplicationUtilities.getResourceString("html.input.password.name"), getName()
            },
            {
                ApplicationUtilities.getResourceString("html.input.password.defaultValue"),
                getDefaultValue()
            },
            {
                ApplicationUtilities.getResourceString("html.input.password.value"),
                new String(password.getPassword())
            },
            {
                ApplicationUtilities.getResourceString("html.input.password.label"), getLabel()
            },
            {
                ApplicationUtilities.getResourceString("html.input.password.size"),
                getSize() != -1 ? new Integer(getSize()) : null
            },
            {
                ApplicationUtilities.getResourceString("html.input.password.maxlength"),
                getMaxLength() != -1 ? new Integer(getMaxLength()) : null
            },
            {
                ApplicationUtilities.getResourceString("html.input.password.readonly"),
                new Boolean(isReadOnly())
            },
            {
                ApplicationUtilities.getResourceString("html.input.disabled"),
                new Boolean(isDisabled())
            }
        };
        return new JTable(new PropertiesTableModel(columnNames, 9, data));
    }

    public String paramString()
    {
    	return passwordINPUTElement.paramString();
    }

    public Component getComponent()
    {
        return password;
    }

    public void reset()
    {
    	passwordINPUTElement.reset();
        password.setText(getDefaultValue());
    }

    public void setValue(String value)
    {
    	passwordINPUTElement.setValue(value);
        password.setText(value);
    }

    public String getValue()
    {
        return new String(password.getPassword());
    }
}