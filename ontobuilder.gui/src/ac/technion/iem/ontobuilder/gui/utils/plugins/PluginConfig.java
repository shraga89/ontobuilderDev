package ac.technion.iem.ontobuilder.gui.utils.plugins;

import java.util.HashMap;

/**
 * <p>Title: PluginConfig</p>
 * <p>Description: The plug-in configurations</p>
 * Extends {@linkHashMap }
 * @author Haggai Roitman
 */
public class PluginConfig extends HashMap<Object, Object>
{

    private static final long serialVersionUID = 1L;

    /**
     * Add a parameter
     *
     * @param name the parameter name
     * @param value the parameter value
     */
    public void addParameter(String name, Object value)
    {
        put(name, value);
    }

    /**
     * Get the parameter by its name
     *
     * @param name the parameter name
     * @return the parameter value
     */
    public Object getParameter(String name)
    {
        return get(name);
    }
}
