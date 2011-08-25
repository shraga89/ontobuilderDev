package ac.technion.iem.ontobuilder.matching.algorithms.line2.stablemarriage;


/**
 * <p>Title: Man</p>
 * Extends {@link StableMarriagePlayer}
 */
public class Man extends StableMarriagePlayer
{
    public Man(int nPreferencesSize)
    {
        super(nPreferencesSize);
    }

    /**
     * @param nPreferencesSize the number of preferred partners
     * @param sName the name of the man
     */
    public Man(int nPreferencesSize, String sName)
    {
        super(/* nPreferencesSize, */sName);
    }

    /**
     * Get the first preference of the man
     * 
     * @return a {@link StableMarriagePlayer}
     */
    public StableMarriagePlayer popFirstPreference()
    {
        try
        {
            return (StableMarriagePlayer) (m_lPreferencesList.removeFirst());
        }
        catch (Exception e)
        {
            // System.out.println(e.toString());
            return null;
        }
    }

}
