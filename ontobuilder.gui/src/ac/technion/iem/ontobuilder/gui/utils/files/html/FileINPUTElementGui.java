package ac.technion.iem.ontobuilder.gui.utils.files.html;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;

import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.FileINPUTElement;
import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.application.PropertiesTableModel;
import ac.technion.iem.ontobuilder.gui.elements.TextField;
import ac.technion.iem.ontobuilder.gui.utils.files.common.FileUtilities;

/**
 * <p>Title: FileINPUTElement</p>
 * Extends {@link INPUTElementGui}
 */
public class FileINPUTElementGui extends INPUTElementGui
{
    protected JPanel component;
    protected TextField file;
    protected JButton browse;
    
    protected FileINPUTElement fileINPUTElement;

    public FileINPUTElementGui(final FileINPUTElement fileINPUTElement)
    {
        super(fileINPUTElement);
        this.fileINPUTElement = fileINPUTElement;
        component = new JPanel();
        component.add(file = new TextField(ApplicationUtilities
            .getIntProperty("html.input.file.size")));
        component.add(browse = new JButton(ApplicationUtilities
            .getResourceString("html.input.file.browse")));
        file.addKeyListener(new KeyAdapter()
        {
            public void keyTyped(KeyEvent e)
            {
                if (fileINPUTElement.getMaxLength() > 0 && file.getText().length() >= fileINPUTElement.getMaxLength())
                    e.consume();
            }
        });
        browse.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                if (!fileINPUTElement.isReadOnly())
                {
                    File f = FileUtilities.openFileDialog(null);
                    if (f != null)
                        file.setText(f.getAbsolutePath());
                }
            }
        });
    }
    
    public FileINPUTElementGui()
    {
    	this(new FileINPUTElement());
    }

    public FileINPUTElementGui(String name)
    {
    	this(new FileINPUTElement(name));
    }

    public void setLabel(String label)
    {
        this.fileINPUTElement.setLabel(label);
    }

    public String getLabel()
    {
        return this.fileINPUTElement.getLabel();
    }

    public void setSize(int size)
    {
    	this.fileINPUTElement.setSize(size);
        if (size >= 0)
            file.setColumns(size);
    }

    public int getSize()
    {
        return this.fileINPUTElement.getSize();
    }

    public void setMaxLength(int maxLength)
    {
    	this.fileINPUTElement.setMaxLength(maxLength);
    }

    public int getMaxLength()
    {
        return this.fileINPUTElement.getMaxLength();
    }

    public void setReadOnly(boolean b)
    {
    	this.fileINPUTElement.setReadOnly(b);
        file.setEditable(!b);
    }

    public boolean isReadOnly()
    {
        return this.fileINPUTElement.isReadOnly();
    }

    public void setDisabled(boolean b)
    {
        super.setDisabled(b);
        this.fileINPUTElement.setDisabled(b);
        file.setEnabled(!b);
        browse.setEnabled(!b);
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
                ApplicationUtilities.getResourceString("html.input.file.name"), getName()
            },
            {
                ApplicationUtilities.getResourceString("html.input.file.label"), getLabel()
            },
            {
                ApplicationUtilities.getResourceString("html.input.file.value"), file.getText()
            },
            {
                ApplicationUtilities.getResourceString("html.input.file.size"),
                getSize() != -1 ? new Integer(getSize()) : null
            },
            {
                ApplicationUtilities.getResourceString("html.input.file.maxlength"),
                getMaxLength() != -1 ? new Integer(getMaxLength()) : null
            },
            {
                ApplicationUtilities.getResourceString("html.input.file.readonly"),
                new Boolean(isReadOnly())
            },
            {
                ApplicationUtilities.getResourceString("html.input.disabled"),
                new Boolean(isDisabled())
            }
        };
        return new JTable(new PropertiesTableModel(columnNames, 8, data));
    }

    public String paramString()
    {
        return this.fileINPUTElement.paramString();
    }

    public Component getComponent()
    {
        return component;
    }

    public void reset()
    {
        file.setText("");
        this.fileINPUTElement.reset();
    }

    public void setValue(String value)
    {
        file.setText(value);
        this.fileINPUTElement.setValue(value);
    }

    public String getValue()
    {
        return file.getText();
    }
}