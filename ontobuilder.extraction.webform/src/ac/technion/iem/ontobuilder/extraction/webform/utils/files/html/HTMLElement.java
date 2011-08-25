package ac.technion.iem.ontobuilder.extraction.webform.utils.files.html;

import ac.technion.iem.ontobuilder.core.utils.graphs.GraphCell;

/**
 * <p>Title: HTMLElement</p>
 * Implements {@link INPUTElement}
 */
public abstract class HTMLElement
{
    public static final String FORM = "form";
    public static final String INPUT = "input";
    public static final String FRAME = "frame";
    public static final String META = "meta";
    public static final String A = "a";

    private String type;
    private String description;

    public HTMLElement(String type)
    {
        this.type = type;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getDescription()
    {
        return description;
    }

    public String getType()
    {
        return type;
    }

    public String toString()
    {
        return type;
    }
    
    public GraphCell getTreeBranch()
    {
    	return new GraphCell(this);
    }
}