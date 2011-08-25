package ac.technion.iem.ontobuilder.extraction.webform.utils.files.html;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * <p>Title: CheckboxINPUTElement</p>
 * Extends {@link INPUTElement}
 */
public class CheckboxINPUTElement extends INPUTElement
{
    protected ArrayList<CheckboxINPUTElementOption> options;
    protected String label;

    public CheckboxINPUTElement()
    {
        super(INPUTElement.CHECKBOX);
        options = new ArrayList<CheckboxINPUTElementOption>();
    }

    public CheckboxINPUTElement(String name)
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
        if (((label == null || label.length() == 0) && options.size() > 0) || options.size() == 1)
            return ((CheckboxINPUTElementOption) options.get(0)).getLabel();
        return label;
    }

    public int getOptionsCount()
    {
        return options.size();
    }

    public void setDisabled(boolean b)
    {
        super.setDisabled(b);
        for (Iterator<CheckboxINPUTElementOption> i = options.iterator(); i.hasNext();)
            ((CheckboxINPUTElementOption) i.next()).setDisabled(b);
    }

    public void addOption(CheckboxINPUTElementOption option)
    {
        if (option == null)
            return;
        option.setCheckbox(this);
        options.add(option);
        if (!option.isDisabled())
            disabled = false;
    }

    public void removeOption(CheckboxINPUTElementOption option)
    {
        options.remove(option);
    }

    public CheckboxINPUTElementOption getOption(int index)
    {
        if (index < 0 || index >= options.size())
            return null;
        return (CheckboxINPUTElementOption) options.get(index);
    }

    public String getCheckedValues()
    {
        String value = new String("");
        for (Iterator<CheckboxINPUTElementOption> i = options.iterator(); i.hasNext();)
        {
            CheckboxINPUTElementOption option = (CheckboxINPUTElementOption) i.next();
            if (!option.isDisabled() && option.isChecked())
                value += option.getValue() + ",";
        }
        if (!value.equals("")) // remove last comma
            value = value.substring(0, value.length() - 1);
        return value;
    }

    public String getValue()
    {
        return getCheckedValues();
    }

    public void check(int index)
    {
        if (index < 0 || index >= options.size())
            return;
        ((CheckboxINPUTElementOption) options.get(index)).setChecked(true);
    }

    public void uncheck(int index)
    {
        if (index < 0 || index >= options.size())
            return;
        ((CheckboxINPUTElementOption) options.get(index)).setChecked(false);
    }

    public void uncheckAll()
    {
        for (Iterator<CheckboxINPUTElementOption> i = options.iterator(); i.hasNext();)
            ((CheckboxINPUTElementOption) i.next()).setChecked(false);
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

    public String paramString()
    {
        StringBuffer param = new StringBuffer();
        for (Iterator<CheckboxINPUTElementOption> i = options.iterator(); i.hasNext();)
        {
            CheckboxINPUTElementOption option = (CheckboxINPUTElementOption) i.next();
            try
            {
                if (!option.isDisabled() && option.isChecked())
                    param.append(name).append("=")
                        .append(java.net.URLEncoder.encode(option.getValue(), "UTF-8")).append("&");
            }
            // TODO: Handle the exception better
            catch (UnsupportedEncodingException e)
            {
                throw new RuntimeException(e);
            }
        }
        String paramText = param.toString();
        if (paramText.length() > 0)
            paramText = paramText.substring(0, paramText.length() - 1);
        return paramText;
    }

    public void reset()
    {
        for (Iterator<CheckboxINPUTElementOption> i = options.iterator(); i.hasNext();)
            ((CheckboxINPUTElementOption) i.next()).reset();
    }

    public boolean canSubmit()
    {
        return super.canSubmit() && getCheckedValues().length() > 0;
    }
}