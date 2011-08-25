package ac.technion.iem.ontobuilder.gui.tools.mergewizard;

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

    /**
     * Constructs a default WizardStatus
     */
    public WizardStatus()
    {
    }

    /**
     * Set the next action
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
     * @return the next action
     */
    public short getNextAction()
    {
        return nextAction;
    }
}
