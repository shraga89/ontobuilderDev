package ac.technion.iem.ontobuilder.gui.application.action;

import java.util.HashMap;
import java.util.Set;
import javax.swing.Action;

/**
 * <p>Title: Actions</p>
 * <p>Description: A collection of actions</p>
 */
public class Actions
{
    private HashMap<String, Action> actions;

    /**
     * Constructs a default Actions
     */
    public Actions()
    {
        actions = new HashMap<String, Action>();
    }

    /**
     * Adds an action
     * 
     * @param actionName the action name
     * @param action the action
     */
    public void addAction(String actionName, Action action)
    {
        actions.put(actionName, action);
    }

    /**
     * Get a specific action by its name
     * 
     * @param action the action name
     * @return Action the action
     */
    public Action getAction(String action)
    {
        return (Action) actions.get(action);
    }

    /**
     * Get all the actions names
     * 
     * @return a set of action names
     */
    public Set<String> getActions()
    {
        return actions.keySet();
    }
}
