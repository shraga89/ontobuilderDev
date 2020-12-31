package ac.technion.iem.ontobuilder.core.biztalk;

import ac.technion.iem.ontobuilder.core.utils.StringUtilities;

/**
 * <p>Title: BizTalkUtilities</p>
 * <p>Description: Internal BizTalk utilities</p>
 */
public class BizTalkUtilities
{
    final public static String NAME__LABEL_AND_NAME = "label_name";
    final public static String NAME__NAME_ONLY = "name";
    final public static String NAME__LABEL_ONLY = "label";

    final public static String ENUMERATION__LABEL_AND_VALUE = "label_value";
    final public static String ENUMERATION__VALUE_ONLY = "value";
    final public static String ENUMERATION__LABEL_ONLY = "label";

    public static final String NAME_NAMING_TYPE_PROPERTY = "biztalkNameNamingType";
    public static final String ENUMERATION_NAMING_TYPE_PROPERTY = "biztalkEnumerationNamingType";

    private static String nameNamingType = NAME__LABEL_AND_NAME;
    private static String enumerationNamingType = ENUMERATION__LABEL_ONLY;

    public static void setNameNamingType(String type)
    {
        nameNamingType = type;
    }

    public static String getNameNamingType()
    {
        return nameNamingType;
    }

    public static void setEnumarationNamingType(String type)
    {
        enumerationNamingType = type;
    }

    public static String getEnumerationNamingType()
    {
        return enumerationNamingType;
    }

    static public String makeValidBizTalkName(String label, String name)
    {
        return makeValidBizTalkName(label, name, nameNamingType);
    }

    static public String makeValidBizTalkName(String label, String name, String type)
    {
        String elementName = "";
        if (type.equals(NAME__LABEL_AND_NAME))
        {
            if (name == null || name.length() == 0 || name.equals(label))
                elementName = StringUtilities.makeIdentifierString(label);
            else
                elementName = StringUtilities.makeIdentifierString(label + "__" + name);
        }
        else if (type.equals(NAME__NAME_ONLY))
            elementName = StringUtilities.makeIdentifierString(name);
        else if (type.equals(NAME__LABEL_ONLY))
            elementName = StringUtilities.makeIdentifierString(label);
        return elementName == null ? "" : elementName;
    }

    static public String makeValidBizTalkEnumerationEntry(String label, String value)
    {
        return makeValidBizTalkEnumerationEntry(label, value, enumerationNamingType);
    }

    static public String makeValidBizTalkEnumerationEntry(String label, String value, String type)
    {
        String enumerationEntry = "";
        if (type.equals(ENUMERATION__LABEL_AND_VALUE))
        {
            if (value == null || value.length() == 0 || value.equals(label))
                enumerationEntry = StringUtilities.makeIdentifierString(label, false);
            else
                enumerationEntry = StringUtilities
                    .makeIdentifierString(label + "__" + value, false);
        }
        else if (type.equals(ENUMERATION__LABEL_ONLY))
            enumerationEntry = StringUtilities.makeIdentifierString(label, false);
        else if (type.equals(ENUMERATION__VALUE_ONLY))
            enumerationEntry = StringUtilities.makeIdentifierString(value, false);
        return enumerationEntry == null ? "" : enumerationEntry;
    }
}
