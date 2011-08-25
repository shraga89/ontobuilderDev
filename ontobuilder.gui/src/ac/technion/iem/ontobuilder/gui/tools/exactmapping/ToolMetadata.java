package ac.technion.iem.ontobuilder.gui.tools.exactmapping;

/**
 * <p>Title: ToolMetadata</p>
 * <p>Description: The Tool class metadata</p>
 */
public class ToolMetadata
{

    private String name;
    private String icon;
    private String classpath;
    private String longDescription;
    private String shortDescription;
    private String mnemonic;
    private String accelerator;
    private boolean visible = false;

    /**
     * Check if visible
     * 
     * @return <code>true</code> if is visible
     */
    public boolean isVisible()
    {
        return visible;
    }

    /**
     * Set if visible
     * 
     * @param visible <code>true</code> if visible
     */
    public void setVisible(boolean visible)
    {
        this.visible = visible;
    }

    /**
     * Get the name
     * 
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Set the name
     * 
     * @param name the name
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Get the accelerator
     * 
     * @return the accelerator
     */
    public String getAccelerator()
    {
        return accelerator;
    }

    /**
     * Set the accelerator
     * 
     * @param accelerator the accelerator
     */
    public void setAccelerator(String accelerator)
    {
        this.accelerator = accelerator;
    }

    /**
     * Get the long description
     * 
     * @return the long description
     */
    public String getLongDescription()
    {
        return longDescription;
    }

    /**
     * Set the long description
     * 
     * @param longDescription the long description
     */
    public void setLongDescription(String longDescription)
    {
        this.longDescription = longDescription;
    }

    /**
     * Get the mnemonic
     * 
     * @return the mnemonic
     */
    public String getMnemonic()
    {
        return mnemonic;
    }

    /**
     * Set the mnemonic
     * 
     * @param mnemonic the mnemonic
     */
    public void setMnemonic(String mnemonic)
    {
        this.mnemonic = mnemonic;
    }

    /**
     * Get the short description
     * 
     * @return the short description
     */
    public String getShortDescription()
    {
        return shortDescription;
    }

    /**
     * Set the short description
     * 
     * @param shortDescription the short description
     */
    public void setShortDescription(String shortDescription)
    {
        this.shortDescription = shortDescription;
    }

    /**
     * Get the classpath
     * 
     * @return the classpath
     */
    public String getClasspath()
    {
        return classpath;
    }

    /**
     * Set the classpath
     * 
     * @param classpath the classpath
     */
    public void setClasspath(String classpath)
    {
        this.classpath = classpath;
    }

    /**
     * Get the icon
     * 
     * @return the icon
     */
    public String getIcon()
    {
        return icon;
    }

    /**
     * Set the icon
     * 
     * @param icon the icon
     */
    public void setIcon(String icon)
    {
        this.icon = icon;
    }

    /**
     * Validate the metadata (that the classpath and name exist)
     * 
     * @return <code>true</code> if is valid
     */
    public boolean validate()
    {
        return classpath != null && name != null;
    }

}
