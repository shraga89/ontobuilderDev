package ac.technion.iem.ontobuilder.matching.match;

/**
 * <p>Title: MatchInformationFormatTypesEnum</p>
 * <p>Description: Enum with the types of match information formats</p>
 * Available types: <br>
 * <code>TEXT_FORMAT</code> and <code>XML_FORMAT</code> 
 */
public enum MatchInformationFormatTypesEnum
{
    TEXT_FORMAT(0),
    XML_FORMAT(1);
    
    private int _id;
    
    private MatchInformationFormatTypesEnum(int id)
    {
        _id = id;
    }
    
    public int getId()
    {
        return _id;
    }
}
