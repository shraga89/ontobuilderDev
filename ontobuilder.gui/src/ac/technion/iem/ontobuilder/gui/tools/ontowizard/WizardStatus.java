package ac.technion.iem.ontobuilder.gui.tools.ontowizard;

import java.util.ArrayList;

import ac.technion.iem.ontobuilder.gui.utils.files.html.FORMElementGui;

/**
 * <p>Title: WizardStatus</p>
 * The possible statuses:
 * <br>
 * <code>NEXT_ACTION</code>, <code>CANCEL_ACTION</code>, <code>BACK_ACTION</code>, <code>FINISH_ACTION</code>
 */
public class WizardStatus
{
    public static final short NEXT_ACTION = 0;
    public static final short CANCEL_ACTION = 1;
    public static final short BACK_ACTION = 2;
    public static final short FINISH_ACTION = 3;

    protected short nextAction;
    protected FORMElementGui form;
    protected ArrayList<?> forms;

    /**
     * Constructs a default WizardStatus
     */
    public WizardStatus()
    {
    }

    /**
     * Set the next action (next, cancel, back, finish)
     * 
     * @param nextAction the next action
     */
    public void setNextAction(short nextAction)
    {
        this.nextAction = nextAction;
    }

    /**
     * Get the next action
     * 
     * @return the next action (next, cancel, back, finish)
     */
    public short getNextAction()
    {
        return nextAction;
    }

    /**
     * Set the form
     * 
     * @param form the form
     */
    public void setForm(FORMElementGui form)
    {
        this.form = form;
    }

    /**
     * Get the form
     * 
     * @return the form
     */
    public FORMElementGui getForm()
    {
        return form;
    }

    /**
     * Set the forms
     * 
     * @param forms the list of forms
     */
    public void setForms(ArrayList<?> forms)
    {
        this.forms = forms;
    }

    /**
     * Get the forms
     * 
     * @return the forms list
     */
    public ArrayList<?> getForms()
    {
        return forms;
    }
}
