package ac.technion.iem.ontobuilder.gui.utils.files.html;

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.UnsupportedEncodingException;

import javax.swing.JTable;

import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.TextINPUTElement;
import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.application.PropertiesTableModel;
import ac.technion.iem.ontobuilder.gui.elements.TextField;

/**
 * <p>Title: TextINPUTElement</p>
 * Extends {@link INPUTElementGui}
 */
public class TextINPUTElementGui extends INPUTElementGui
{
    protected TextINPUTElement textINPUTElement;

    protected TextField text;

    public TextINPUTElementGui(final TextINPUTElement textINPUTElement)
    {
        super(textINPUTElement);
        this.textINPUTElement = textINPUTElement;
        text = new TextField(ApplicationUtilities.getIntProperty("html.input.text.size"));
        text.addKeyListener(new KeyAdapter()
        {
            public void keyTyped(KeyEvent e)
            {
                if (textINPUTElement.getMaxLength() > 0 && text.getText().length() >= textINPUTElement.getMaxLength())
                    e.consume();
            }
        });
    }
    
    public TextINPUTElementGui()
    {
    	this(new TextINPUTElement());
    }

    public TextINPUTElementGui(String name, String defaultValue)
    {
    	this(new TextINPUTElement(name,defaultValue));
        setDefaultValue(defaultValue);
    }

    public void setDefaultValue(String defaultValue)
    {
    	textINPUTElement.setDefaultValue(defaultValue);
        text.setText(defaultValue);
    }

    public String getDefaultValue()
    {
        return textINPUTElement.getDefaultValue();
    }

    public void setLabel(String label)
    {
    	textINPUTElement.setLabel(label);
    }

    public String getLabel()
    {
        return textINPUTElement.getLabel();
    }

    public void setSize(int size)
    {
    	textINPUTElement.setSize(size);
        if (size >= 0)
            text.setColumns(size);
    }

    public int getSize()
    {
        return textINPUTElement.getSize();
    }

    public void setMaxLength(int maxLength)
    {
    	textINPUTElement.setMaxLength(maxLength);
    }

    public int getMaxLength()
    {
        return textINPUTElement.getMaxLength();
    }

    public void setReadOnly(boolean b)
    {
    	textINPUTElement.setReadOnly(b);
        text.setEditable(!b);
    }

    public boolean isReadOnly()
    {
        return textINPUTElement.isReadOnly();
    }

    public void setDisabled(boolean b)
    {
        super.setDisabled(b);
        textINPUTElement.setDisabled(b);
        text.setEnabled(!b);
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
                ApplicationUtilities.getResourceString("html.input.text.name"), getName()
            },
            {
                ApplicationUtilities.getResourceString("html.input.text.defaultValue"),
                getDefaultValue()
            },
            {
                ApplicationUtilities.getResourceString("html.input.text.value"), text.getText()
            },
            {
                ApplicationUtilities.getResourceString("html.input.text.label"), getLabel()
            },
            {
                ApplicationUtilities.getResourceString("html.input.text.size"),
                getSize() != -1 ? new Integer(getSize()) : null
            },
            {
                ApplicationUtilities.getResourceString("html.input.text.maxlength"),
                getMaxLength() != -1 ? new Integer(getMaxLength()) : null
            },
            {
                ApplicationUtilities.getResourceString("html.input.text.readonly"),
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
        return textINPUTElement.paramString();
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
        return text;
    }

    public void reset()
    {
    	textINPUTElement.reset();
        text.setText(getDefaultValue());
    }

    public void setValue(String value)
    {
    	textINPUTElement.setValue(value);
        text.setText(value);
    }

    public String getValue()
    {
        return text.getText();
    }
}