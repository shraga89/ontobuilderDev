package ac.technion.iem.ontobuilder.matching.meta.statistics;

/**
 * <p>Title: MetaAlgorithmStatisticsTypeEnum</p>
 * <p>Description: Enum with the types of algorithms statistics</p>
 * Available types: <br>
 * <code>TA_STATISTICS</code>, <code>MD_STATISTICS</code>, <code>MDB_STATISTICS</code> and <code>HYBRID_STATISTICS</code> 
 */
public enum MetaAlgorithmStatisticsTypeEnum
{
    TA_STATISTICS(0),
    MD_STATISTICS(1),
    MDB_STATISTICS(2),
    HYBRID_STATISTICS(3);
    
    private int _id;
    
    private MetaAlgorithmStatisticsTypeEnum(int id)
    {
        _id = id;
    }
    
    public int getId()
    {
        return _id;
    }
}
