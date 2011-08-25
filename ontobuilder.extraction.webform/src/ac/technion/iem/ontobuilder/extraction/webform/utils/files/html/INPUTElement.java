package ac.technion.iem.ontobuilder.extraction.webform.utils.files.html;

import java.util.Hashtable;
import java.awt.Component;

/**
 * <p>Title: INPUTElement</p>
 * Extends {@link HTMLElement}
 */
public abstract class INPUTElement extends HTMLElement
{
    public static final String TEXT = "text";
    public static final String PASSWORD = "password";
    public static final String CHECKBOX = "checkbox";
    public static final String CHECKBOXOPTION = "checkbox option";
    public static final String RADIO = "radio";
    public static final String RADIOOPTION = "radio option";
    public static final String SUBMIT = "submit";
    public static final String RESET = "reset";
    public static final String FILE = "file";
    public static final String IMAGE = "image";
    public static final String BUTTON = "button";
    public static final String SELECT = "select";
    public static final String OPTION = "option";
    public static final String HIDDEN = "hidden";
    public static final String TEXTAREA = "textarea";

    // Events
    public static final String ONFOCUS = "onFocus";
    public static final String ONSELECT = "onSelect";
    public static final String ONBLUR = "onBlur";
    public static final String ONCHANGE = "onChange";

    private String inputType;
    protected String name;
    protected FORMElement form;
    protected boolean disabled;
    protected Hashtable<String, String> events;

    public INPUTElement(String inputType)
    {
        super(HTMLElement.INPUT);
        this.inputType = inputType;
        events = new Hashtable<String, String>();
    }

    public String getInputType()
    {
        return inputType;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void setForm(FORMElement form)
    {
        this.form = form;
    }

    public FORMElement getForm()
    {
        return form;
    }

    public String toString()
    {
        return name + " (" + inputType + ")";
    }

    public void setDisabled(boolean b)
    {
        this.disabled = b;
    }

    public boolean isDisabled()
    {
        return disabled;
    }

    public String paramString()
    {
        return inputType + ": " + name + (disabled ? " (disabled)" : "");
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
        return !disabled;
    }

    public Hashtable<String, String> getEvents()
    {
        return events;
    }

    public void addEvent(String eventName, String script)
    {
        if (!events.contains(eventName))
            events.put(eventName, script);
    }

    public void removeEvent(String eventName)
    {
        events.remove(eventName);
    }

    public String getEventScript(String eventName)
    {
        return (String) events.get(eventName);
    }
}