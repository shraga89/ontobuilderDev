package ac.technion.iem.ontobuilder.gui.utils.plugins;

import java.util.HashMap;

/**
 * <p>Title: PluginUtils</p>
 * <p>Description: Internal plug-in utilities</p>
 * @author Haggai Roitman
 */
public class PluginUtils
{
    /**
     * Initialize the plug-ins
     * 
     * @throws PluginException
     */
    public static void initializePlugins() throws PluginException
    {
        new HashMap<Object, Object>();
    }

    /**
     * Runs a plug-in.
     * <br>Not implemented
     * 
     * @param pluginName the plug-in name
     * @param params the plug-in parameters
     * @throws PluginException
     */
    public static void runPlugin(String pluginName, HashMap<?, ?> params) throws PluginException
    {

    }
}
