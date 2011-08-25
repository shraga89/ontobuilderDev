package ac.technion.iem.ontobuilder.gui.utils.files.html;

import java.awt.Component;
import java.util.Hashtable;

import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.INPUTElement;

/**
 * <p>Title: INPUTElement</p>
 * Extends {@link HTMLElementGui}
 */
public abstract class INPUTElementGui extends HTMLElementGui
{
	protected INPUTElement inputElement;
    protected FORMElementGui form;

    public INPUTElementGui(INPUTElement inputElement)
    {
        super(inputElement);
        this.inputElement = inputElement;
    }
    
    public INPUTElement getInputElement()
    {
    	return inputElement;
    }

    public String getInputType()
    {
        return inputElement.getInputType();
    }

    public void setName(String name)
    {
        this.inputElement.setName(name);
    }

    public String getName()
    {
        return inputElement.getName();
    }

    public void setForm(FORMElementGui form)
    {
        this.form = form;
        this.inputElement.setForm(form.getFORMElement());
    }

    public FORMElementGui getForm()
    {
        return form;
    }

    public String toString()
    {
        return inputElement.toString();
    }

    public void setDisabled(boolean b)
    {
        this.inputElement.setDisabled(b);
    }

    public boolean isDisabled()
    {
        return inputElement.isDisabled();
    }

    public String paramString()
    {
        return inputElement.paramString();
    }

    public Component getComponent()
    {
        return null;
    }

    public void reset()
    {
    }

    public boolean canSubmit()
    {
        return !inputElement.isDisabled();
    }

    public Hashtable<String, String> getEvents()
    {
        return inputElement.getEvents();
    }

    public void addEvent(String eventName, String script)
    {
        if (!inputElement.getEvents().contains(eventName))
        	inputElement.getEvents().put(eventName, script);
    }

    public void removeEvent(String eventName)
    {
    	inputElement.getEvents().remove(eventName);
    }

    public String getEventScript(String eventName)
    {
        return (String) inputElement.getEvents().get(eventName);
    }
}