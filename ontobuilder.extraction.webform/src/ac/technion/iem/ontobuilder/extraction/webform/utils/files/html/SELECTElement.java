package ac.technion.iem.ontobuilder.extraction.webform.utils.files.html;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;

import ac.technion.iem.ontobuilder.core.utils.graphs.GraphCell;

/**
 * <p>Title: SELECTElement</p>
 * Extends {@link INPUTElement}
 */
public class SELECTElement extends INPUTElement
{
    protected ArrayList<OPTIONElement> options;
    protected String label;
    protected boolean multiple;
    protected int size = -1;

    public SELECTElement()
    {
        super(INPUTElement.SELECT);
        options = new ArrayList<OPTIONElement>();
    }

    public SELECTElement(String name)
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

    public boolean isMultiple()
    {
        return multiple;
    }

    public void setMultiple(boolean b)
    {
        multiple = b;
    }

    public void setSize(int size)
    {
        this.size = size;
    }

    public int getSize()
    {
        return size;
    }

    public int getOptionsCount()
    {
        return options.size();
    }

    public void addOption(OPTIONElement option)
    {
        if (option == null)
            return;
        option.setSelect(this);
        options.add(option);
    }

    public void removeOption(OPTIONElement option)
    {
        if (option == null)
            return;
        options.remove(option);
    }

    public OPTIONElement getOption(int index)
    {
        if (index < 0 || index >= options.size())
            return null;
        return (OPTIONElement) options.get(index);
    }

    public String getSelectedValues()
    {
        if (multiple)
        {
            String value = "";
            for (Iterator<OPTIONElement> i = options.iterator(); i.hasNext();)
            {
                OPTIONElement option = (OPTIONElement) i.next();
                if (option.isSelected())
                    value += option.getValue() + ",";
            }
            if (!value.equals("")) // remove last comma
                value = value.substring(0, value.length() - 1);
            return value;
        }
        else
        {
            for (Iterator<OPTIONElement> i = options.iterator(); i.hasNext();)
            {
                OPTIONElement option = (OPTIONElement) i.next();
                if (option.isSelected())
                    return option.getValue();
            }
            return "";
        }
    }

    public int[] getSelectedIndexes()
    {
        if (multiple)
        {
            ArrayList<Integer> indexes = new ArrayList<Integer>();
            int j = 0;
            for (Iterator<OPTIONElement> i = options.iterator(); i.hasNext(); j++)
            {
                if (((OPTIONElement) i.next()).isSelected())
                    indexes.add(new Integer(j));
            }
            int selectedIndexes[] = new int[indexes.size()];
            for (int i = 0; i < indexes.size(); i++)
                selectedIndexes[i] = ((Integer) indexes.get(i)).intValue();
            return selectedIndexes;
        }
        else
        {
            int j = 0;
            for (Iterator<OPTIONElement> i = options.iterator(); i.hasNext(); j++)
            {
                if (((OPTIONElement) i.next()).isSelected())
                    return new int[]
                    {
                        j
                    };
            }
        }
        return new int[0];
    }

    public String getValue()
    {
        return getSelectedValues();
    }

    public void select(int index)
    {
        if (index < 0 || index >= options.size())
            return;
        if (!multiple)
            deselectAll();
        ((OPTIONElement) options.get(index)).setSelected(true);
    }

    public void select(int indexes[])
    {
        if (multiple)
            for (int i = 0; i < indexes.length; i++)
                select(indexes[i]);
        else
            select(indexes[0]);
    }

    public void deselect(int index)
    {
        if (index < 0 || index >= options.size())
            return;
        ((OPTIONElement) options.get(index)).setSelected(false);
    }

    public void deselect(int indexes[])
    {
        for (int i = 0; i < indexes.length; i++)
            deselect(indexes[i]);
    }

    public void deselectAll()
    {
        for (Iterator<OPTIONElement> i = options.iterator(); i.hasNext();)
            ((OPTIONElement) i.next()).setSelected(false);
    }

    public GraphCell getTreeBranch()
    {
    	GraphCell node = new GraphCell(this);
        for (Iterator<OPTIONElement> i = options.iterator(); i.hasNext();)
            node.addChild(((OPTIONElement) i.next()).getTreeBranch());
        return node;
    }

    public void setDisabled(boolean b)
    {
        super.setDisabled(b);
    }

    public String paramString()
    {
        StringBuffer param = new StringBuffer();
        if (multiple)
        {
            for (Iterator<OPTIONElement> i = options.iterator(); i.hasNext();)
            {
                OPTIONElement option = (OPTIONElement) i.next();
                if (option.isSelected())
                    param.append(name).append("=").append(encode(option.getValue())).append("&");
            }
            String paramText = param.toString();
            if (paramText.length() > 0)
                paramText = paramText.substring(0, paramText.length() - 1);
            return paramText;
        }
        else
        {
            for (Iterator<OPTIONElement> i = options.iterator(); i.hasNext();)
            {
                OPTIONElement option = (OPTIONElement) i.next();
                if (option.isSelected())
                    return name + "=" + encode(option.getValue());
            }
            return "";
        }
    }

    private String encode(String s)
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

    public void reset()
    {
        for (Iterator<OPTIONElement> i = options.iterator(); i.hasNext();)
            ((OPTIONElement) i.next()).reset();
    }

    public boolean canSubmit()
    {
        return super.canSubmit() && getSelectedIndexes().length > 0;
    }
}