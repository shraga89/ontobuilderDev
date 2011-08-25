package ac.technion.iem.ontobuilder.gui.utils.plugins;

import java.awt.Image;

/**
 * <p>Title: interface Plugin</p>
 * @author Haggai Roitman
 */
public interface Plugin
{

    /** plugin name */
    public String getName();

    /** plugin icon */
    public Image getIcon();

    /** plugin configuration */
    public void config(PluginConfig conf);

    /** execute plugin */
    public void exec();

    /** is menuable */
    public boolean isMenuable();

    /** is toolbarable */
    public boolean isToolbarable();

}
