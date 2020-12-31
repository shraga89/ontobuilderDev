package ac.technion.iem.ontobuilder.core.utils.properties;

/**
 * <p>Title: Option</p>
 */
public class Option
{
    public String optionName;
    public String optionValue;
    public String optionDefault;

    /**
     * Constructs an Option
     * 
     * @param optionName the option name
     * @param optionValue the option value
     */
    public Option(String optionName, String optionValue)
    {
        this(optionName, optionValue, null);
    }

    /**
     * Constructs an Option
     * 
     * @param optionName the option name
     * @param optionValue the option value
     * @param optionDefault the option default value
     */
    public Option(String optionName, String optionValue, String optionDefault)
    {
        this.optionName = optionName;
        this.optionDefault = optionDefault;
        this.optionValue = optionValue;
    }

    /**
     * Get the option's default value
     *
     * @return the default value
     */
    public String getOptionDefault()
    {
        return optionDefault;
    }

    /**
     * Set the option's default value
     *
     * @param optionDefault the value
     */
    public void setOptionDefault(String optionDefault)
    {
        this.optionDefault = optionDefault;
    }

    /**
     * Get the option's name
     *
     * @return the name
     */
    public String getOptionName()
    {
        return optionName;
    }

    /**
     * Set the option's name
     *
     * @param optionName the name
     */
    public void setOptionName(String optionName)
    {
        this.optionName = optionName;
    }

    /**
     * Get the option's value
     *
     * @return the value
     */
    public String getOptionValue()
    {
        return optionValue;
    }

    /**
     * Set the option's  value
     *
     * @param optionValue the value
     */
    public void setOptionValue(String optionValue)
    {
        this.optionValue = optionValue;
    }

    /**
     * Reset an option to its default value
     */
    public void reset()
    {
        optionValue = optionDefault;
    }
}
