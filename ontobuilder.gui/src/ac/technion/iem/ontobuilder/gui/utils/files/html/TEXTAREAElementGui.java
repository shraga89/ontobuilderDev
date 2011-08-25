package ac.technion.iem.ontobuilder.gui.utils.files.html;

import java.awt.Component;
import java.awt.Font;
import java.io.UnsupportedEncodingException;

import javax.swing.JScrollPane;
import javax.swing.JTable;

import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.TEXTAREAElement;
import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.application.PropertiesTableModel;
import ac.technion.iem.ontobuilder.gui.elements.TextArea;

/**
 * <p>Title: TEXTAREAElement</p>
 * Extends {@link INPUTElementGui}
 */
public class TEXTAREAElementGui extends INPUTElementGui
{
    protected TEXTAREAElement textareaElement;

    protected TextArea textarea;
    
    public TEXTAREAElementGui(TEXTAREAElement textareaElement)
    {
    	super(textareaElement);
    	this.textareaElement = textareaElement;
        textarea = new TextArea(ApplicationUtilities.getIntProperty("html.textarea.rows"),
            ApplicationUtilities.getIntProperty("html.textarea.cols"));
        textarea.setLineWrap(true);
        textarea.setWrapStyleWord(true);
        textarea.setFont(new Font("Monospaced", Font.PLAIN, textarea.getFont().getSize()));
    }

    public TEXTAREAElementGui()
    {
        this(new TEXTAREAElement());
    }

    public TEXTAREAElementGui(String name)
    {
        this(new TEXTAREAElement(name));
    }

    public void setDefaultValue(String defaultValue)
    {
    	textareaElement.setDefaultValue(defaultValue);
        textarea.setText(defaultValue);
    }

    public String getDefaultValue()
    {
        return textareaElement.getDefaultValue();
    }

    public void setLabel(String label)
    {
    	textareaElement.setLabel(label);
    }

    public String getLabel()
    {
        return textareaElement.getLabel();
    }

    public void setRows(int rows)
    {
    	textareaElement.setRows(rows);
        if (rows > 0)
            textarea.setRows(rows);
    }

    public int getRows()
    {
        return textareaElement.getRows();
    }

    public void setCols(int cols)
    {
    	textareaElement.setCols(cols);
        if (cols > 0)
            textarea.setColumns(cols);
    }

    public int getCols()
    {
        return textareaElement.getCols();
    }

    public void setReadOnly(boolean b)
    {
    	textareaElement.setReadOnly(b);
        textarea.setEditable(!b);
    }

    public boolean isReadOnly()
    {
        return textareaElement.isReadOnly();
    }

    public void setDisabled(boolean b)
    {
        super.setDisabled(b);
        textareaElement.setDisabled(b);
        textarea.setEnabled(!b);
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
                ApplicationUtilities.getResourceString("html.textarea.name"), getName()
            },
            {
                ApplicationUtilities.getResourceString("html.textarea.defaultValue"), getDefaultValue()
            },
            {
                ApplicationUtilities.getResourceString("html.textarea.value"), textarea.getText()
            },
            {
                ApplicationUtilities.getResourceString("html.textarea.label"), getLabel()
            },
            {
                ApplicationUtilities.getResourceString("html.textarea.rows"),
                getRows() != -1 ? new Integer(getRows()) : null
            },
            {
                ApplicationUtilities.getResourceString("html.textarea.cols"),
                getCols() != -1 ? new Integer(getCols()) : null
            },
            {
                ApplicationUtilities.getResourceString("html.textarea.readonly"),
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
        return textareaElement.paramString();
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
        return new JScrollPane(textarea);
    }

    public void reset()
    {
    	textareaElement.reset();
        textarea.setText(getDefaultValue());
    }

    public void setValue(String value)
    {
    	textareaElement.setValue(value);
        textarea.setText(value);
    }

    public String getValue()
    {
        return textarea.getText();
    }
}