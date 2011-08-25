package ac.technion.iem.ontobuilder.gui.utils.files.html;

import java.awt.Component;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.tree.DefaultMutableTreeNode;

import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.RadioINPUTElement;
import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.application.PropertiesTableModel;

/**
 * <p>Title: RadioINPUTElement</p>
 * Extends {@link INPUTElementGui}
 */
public class RadioINPUTElementGui extends INPUTElementGui
{
    protected ArrayList<RadioINPUTElementOptionGui> options;
    protected RadioINPUTElement radioINPUTElement;

    protected ButtonGroup radioGroup;
    protected JPanel component;
    
    public RadioINPUTElementGui(RadioINPUTElement radioINPUTElement)
    {
    	super(radioINPUTElement);
    	this.radioINPUTElement = radioINPUTElement;
        options = new ArrayList<RadioINPUTElementOptionGui>();
        component = new JPanel(new GridLayout(0, 1));
        radioGroup = new ButtonGroup();
    }

    public RadioINPUTElementGui()
    {
        this(new RadioINPUTElement());
    }

    public RadioINPUTElementGui(String name)
    {
        this(new RadioINPUTElement(name));
    }

	public RadioINPUTElement getRadioINPUTElement()
	{
		return radioINPUTElement;
	}

    public void setLabel(String label)
    {
    	radioINPUTElement.setLabel(label);
    }

    public String getLabel()
    {
        return radioINPUTElement.getLabel();
    }

    public int getOptionsCount()
    {
        return options.size();
    }

    public void addOption(RadioINPUTElementOptionGui option)
    {
        if (option == null)
            return;
        option.setRadio(this);
        options.add(option);
        radioINPUTElement.addOption(option.getRadioINPUTElementOption());
        JRadioButton radio = (JRadioButton) option.getComponent();
        component.add(radio);
        radioGroup.add(radio);
    }

    public void removeOption(RadioINPUTElementOptionGui option)
    {
        options.remove(option);
        radioINPUTElement.removeOption(option.getRadioINPUTElementOption());
        component.removeAll();
        for (Iterator<RadioINPUTElementOptionGui> i = options.iterator(); i.hasNext();)
        {
            RadioINPUTElementOptionGui o = (RadioINPUTElementOptionGui) i.next();
            JRadioButton radio = (JRadioButton) o.getComponent();
            component.add(radio);
            radioGroup.add(radio);
        }
    }

    public RadioINPUTElementOptionGui getOption(int index)
    {
        if (index < 0 || index >= options.size())
            return null;
        return (RadioINPUTElementOptionGui) options.get(index);
    }

    public String getCheckedValue()
    {
        for (Iterator<RadioINPUTElementOptionGui> i = options.iterator(); i.hasNext();)
        {
            RadioINPUTElementOptionGui option = (RadioINPUTElementOptionGui) i.next();
            if (!option.isDisabled() && option.isChecked())
                return option.getValue();
        }
        return "";
    }

    public String getValue()
    {
        return getCheckedValue();
    }

    public void setDisabled(boolean b)
    {
        super.setDisabled(b);
        radioINPUTElement.setDisabled(b);
        for (Iterator<RadioINPUTElementOptionGui> i = options.iterator(); i.hasNext();)
            ((RadioINPUTElementOptionGui) i.next()).setDisabled(b);
    }

    public void check(int index)
    {
        if (index < 0 || index >= options.size())
            return;
        for (Iterator<RadioINPUTElementOptionGui> i = options.iterator(); i.hasNext();)
            ((RadioINPUTElementOptionGui) i.next()).setChecked(false);
        ((RadioINPUTElementOptionGui) options.get(index)).setChecked(true);
    }

    public void uncheck(int index)
    {
        if (index < 0 || index >= options.size())
            return;
        ((RadioINPUTElementOptionGui) options.get(index)).setChecked(false);
    }

    public DefaultMutableTreeNode getTreeBranch()
    {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(this);
        for (Iterator<RadioINPUTElementOptionGui> i = options.iterator(); i.hasNext();)
            node.add(((RadioINPUTElementOptionGui) i.next()).getTreeBranch());
        return node;
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
                ApplicationUtilities.getResourceString("html.input.radio.name"), getName()
            },
            {
                ApplicationUtilities.getResourceString("html.input.radio.label"), getLabel()
            },
            {
                ApplicationUtilities.getResourceString("html.input.radio.options"),
                new Integer(options.size())
            },
            {
                ApplicationUtilities.getResourceString("html.input.disabled"),
                new Boolean(isDisabled())
            }
        };
        return new JTable(new PropertiesTableModel(columnNames, 5, data));
    }

    public String paramString()
    {
    	return radioINPUTElement.paramString();
    }

    public Component getComponent()
    {
        return component;
    }

    public void reset()
    {
    	radioINPUTElement.reset();
        for (Iterator<RadioINPUTElementOptionGui> i = options.iterator(); i.hasNext();)
            ((RadioINPUTElementOptionGui) i.next()).reset();
    }

    public boolean canSubmit()
    {
        return radioINPUTElement.canSubmit();
    }
}