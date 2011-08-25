package ac.technion.iem.ontobuilder.io.imports;

/**
 * <p>Title: ImporterMetadata</p>
 * <p>Description: The importer class metadata</p>
 */
public class ImporterMetadata
{
    private String icon;
    private String classpath;
    private String type;
    private String extension;
    private String longDescription;
    private String shortDescription;
    private String mnemonic;
    private String accelerator;

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
     * Get the extension
     * 
     * @return the extension
     */
    public String getExtension()
    {
        return extension;
    }

    /**
     * Set the extension
     * 
     * @param extension the extension
     */
    public void setExtension(String extension)
    {
        this.extension = extension;
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
     * Get the exporter type
     * 
     * @return the exporter type
     */
    public String getType()
    {
        return type;
    }

    /**
     * Set the exporter type
     * 
     * @param type the exporter type
     */
    public void setType(String type)
    {
        this.type = type;
    }

    /**
     * Validate the metadata (that the classpath, type and extension exist)
     * 
     * @return <code>true</code> if is valid
     */
    public boolean validate()
    {
        return classpath != null && type != null && extension != null;
    }
}
