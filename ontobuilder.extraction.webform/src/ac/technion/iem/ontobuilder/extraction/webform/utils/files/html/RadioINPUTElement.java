package ac.technion.iem.ontobuilder.extraction.webform.utils.files.html;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;

import ac.technion.iem.ontobuilder.core.utils.graphs.GraphCell;


/**
 * <p>Title: RadioINPUTElement</p>
 * Extends {@link INPUTElement}
 */
public class RadioINPUTElement extends INPUTElement
{
    protected ArrayList<RadioINPUTElementOption> options;
    protected String label;

    public RadioINPUTElement()
    {
        super(INPUTElement.RADIO);
        options = new ArrayList<RadioINPUTElementOption>();
    }

    public RadioINPUTElement(String name)
    {
        this();
        this.name = name;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public String getLabel()
    {
        return label;
    }

    public int getOptionsCount()
    {
        return options.size();
    }

    public void addOption(RadioINPUTElementOption option)
    {
        if (option == null)
            return;
        option.setRadio(this);
        options.add(option);
        if (!option.isDisabled())
            disabled = false;
    }

    public void removeOption(RadioINPUTElementOption option)
    {
        options.remove(option);
    }

    public RadioINPUTElementOption getOption(int index)
    {
        if (index < 0 || index >= options.size())
            return null;
        return (RadioINPUTElementOption) options.get(index);
    }

    public String getCheckedValue()
    {
        for (Iterator<RadioINPUTElementOption> i = options.iterator(); i.hasNext();)
        {
            RadioINPUTElementOption option = (RadioINPUTElementOption) i.next();
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
        for (Iterator<RadioINPUTElementOption> i = options.iterator(); i.hasNext();)
            ((RadioINPUTElementOption) i.next()).setDisabled(b);
    }

    public void check(int index)
    {
        if (index < 0 || index >= options.size())
            return;
        for (Iterator<RadioINPUTElementOption> i = options.iterator(); i.hasNext();)
            ((RadioINPUTElementOption) i.next()).setChecked(false);
        ((RadioINPUTElementOption) options.get(index)).setChecked(true);
    }

    public void uncheck(int index)
    {
        if (index < 0 || index >= options.size())
            return;
        ((RadioINPUTElementOption) options.get(index)).setChecked(false);
    }

    public GraphCell getTreeBranch()
    {
    	GraphCell node = new GraphCell(this);
        for (Iterator<RadioINPUTElementOption> i = options.iterator(); i.hasNext();)
            node.addChild(((RadioINPUTElementOption) i.next()).getTreeBranch());
        return node;
    }

    public String paramString()
    {
        try
        {
            return name + "=" + java.net.URLEncoder.encode(getCheckedValue(), "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            // TODO: Handle the exception better.
            throw new RuntimeException(e);
        }
    }

    public void reset()
    {
        for (Iterator<RadioINPUTElementOption> i = options.iterator(); i.hasNext();)
            ((RadioINPUTElementOption) i.next()).reset();
    }

    public boolean canSubmit()
    {
        return super.canSubmit() && getCheckedValue().length() > 0;
    }
}