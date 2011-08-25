package ac.technion.iem.ontobuilder.gui.utils.files.html;

import java.awt.Component;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.tree.DefaultMutableTreeNode;

import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.CheckboxINPUTElement;
import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.application.PropertiesTableModel;

/**
 * <p>Title: CheckboxINPUTElement</p>
 * Extends {@link INPUTElementGui}
 */
public class CheckboxINPUTElementGui extends INPUTElementGui
{
    protected ArrayList<CheckboxINPUTElementOptionGui> options;
    protected CheckboxINPUTElement checkboxINPUTElement;

    protected JPanel component;

    public CheckboxINPUTElementGui(CheckboxINPUTElement checkboxINPUTElement)
    {
        super(checkboxINPUTElement);
        this.checkboxINPUTElement = checkboxINPUTElement;
        options = new ArrayList<CheckboxINPUTElementOptionGui>();
        component = new JPanel(new GridLayout(0, 1));
    }
    
    public CheckboxINPUTElementGui()
    {
    	this(new CheckboxINPUTElement());
    }

    public CheckboxINPUTElementGui(String name)
    {
        this(new CheckboxINPUTElement(name));
    }

    public void setLabel(String label)
    {
        this.checkboxINPUTElement.setLabel(label);
    }

    public String getLabel()
    {
        return this.checkboxINPUTElement.getLabel();
    }

    public int getOptionsCount()
    {
        return options.size();
    }

    public void setDisabled(boolean b)
    {
        super.setDisabled(b);
        this.checkboxINPUTElement.setDisabled(b);
        for (Iterator<CheckboxINPUTElementOptionGui> i = options.iterator(); i.hasNext();)
            ((CheckboxINPUTElementOptionGui) i.next()).setDisabled(b);
    }

    public void addOption(CheckboxINPUTElementOptionGui option)
    {
        if (option == null)
            return;
        option.setCheckbox(this);
        option.getCheckboxINPUTElementOption().setCheckbox(checkboxINPUTElement);
        options.add(option);
        checkboxINPUTElement.addOption(option.getCheckboxINPUTElementOption());
        if (!option.isDisabled())
            checkboxINPUTElement.setDisabled(false);
        component.add(option.getComponent());
    }

    public void removeOption(CheckboxINPUTElementOptionGui option)
    {
        options.remove(option);
        checkboxINPUTElement.removeOption(option.getCheckboxINPUTElementOption());
        component.removeAll();
        for (Iterator<CheckboxINPUTElementOptionGui> i = options.iterator(); i.hasNext();)
            component.add(((CheckboxINPUTElementOptionGui) i.next()).getComponent());
    }

    public CheckboxINPUTElementOptionGui getOption(int index)
    {
        if (index < 0 || index >= options.size())
            return null;
        return (CheckboxINPUTElementOptionGui) options.get(index);
    }

    public String getCheckedValues()
    {
        return checkboxINPUTElement.getCheckedValues();
    }

    public String getValue()
    {
        return getCheckedValues();
    }

    public void check(int index)
    {
        if (index < 0 || index >= options.size())
            return;
        ((CheckboxINPUTElementOptionGui) options.get(index)).setChecked(true);
    }

    public void uncheck(int index)
    {
        if (index < 0 || index >= options.size())
            return;
        ((CheckboxINPUTElementOptionGui) options.get(index)).setChecked(false);
    }

    public void uncheckAll()
    {
        for (Iterator<CheckboxINPUTElementOptionGui> i = options.iterator(); i.hasNext();)
            ((CheckboxINPUTElementOptionGui) i.next()).setChecked(false);
    }

    public void check(int indexes[])
    {
        for (int i = 0; i < indexes.length; i++)
            check(indexes[i]);
    }

    public void uncheck(int indexes[])
    {
        for (int i = 0; i < indexes.length; i++)
            uncheck(indexes[i]);
    }

    public DefaultMutableTreeNode getTreeBranch()
    {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(this);
        for (Iterator<CheckboxINPUTElementOptionGui> i = options.iterator(); i.hasNext();)
            node.add(((CheckboxINPUTElementOptionGui) i.next()).getTreeBranch());
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
                ApplicationUtilities.getResourceString("html.input.checkbox.name"), getName()
            },
            {
                ApplicationUtilities.getResourceString("html.input.checkbox.label"), getLabel()
            },
            {
                ApplicationUtilities.getResourceString("html.input.checkbox.options"),
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
        return checkboxINPUTElement.paramString();
    }

    public Component getComponent()
    {
        return component;
    }

    public void reset()
    {
        for (Iterator<CheckboxINPUTElementOptionGui> i = options.iterator(); i.hasNext();)
            ((CheckboxINPUTElementOptionGui) i.next()).reset();
    }

    public boolean canSubmit()
    {
        return checkboxINPUTElement.canSubmit();
    }

	public CheckboxINPUTElement getCheckboxINPUTElement()
	{
		return checkboxINPUTElement;
	}
}