package ac.technion.iem.ontobuilder.matching.algorithms.line2.stablemarriage;

/**
 * <p>Title: Woman</p>
 * Extends {@link StableMarriagePlayer}
 */
public class Woman extends StableMarriagePlayer
{
    public Woman(int nPreferencesSize)
    {
        super(nPreferencesSize);
    }

    /**
     * @param nPreferencesSize the number of preferred partners
     * @param sName the name of the woman
     */
    public Woman(int nPreferencesSize, String sName)
    {
        super(/* nPreferencesSize, */sName);
    }

    /**
     * Checks whether this partner is more preferred than the current partner
     * 
     * @param candidate the {@link StableMarriagePlayer} to check
     * @return <code>true</code> if it is more preferred
     */
    public boolean morePrefer(StableMarriagePlayer candidate)
    {
        int currPartnerIndex = m_lPreferencesList.indexOf(m_uPartner);
        int candIndex = m_lPreferencesList.indexOf(candidate);

        // if the proposed partner appear before the current partner in the list
        // then 'this' prefer the proposed partner on its current partner;
        if (candIndex < currPartnerIndex)
        {
            return true;
        }
        return false;
    }

}
