package ac.technion.iem.ontobuilder.io.exports;

/**
 * <p>
 * Title: ExportersTypeEnum
 * </p>
 * <p>
 * Description: Keeps all exporters type
 * </p>
 * Available exporters: <code>Ontology</code> and <code>Matching</code>
 */
public enum ExportersTypeEnum
{   
    ONTOLOGY("Ontology"), 
    MATCHING("Matching");
    
    private final String _name;

    ExportersTypeEnum(String name)
    {
        _name = name;
    }

    public String getName()
    {
        return _name;
    }
    
    
}
