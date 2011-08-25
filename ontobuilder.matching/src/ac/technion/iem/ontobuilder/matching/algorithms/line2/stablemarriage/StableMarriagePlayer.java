package ac.technion.iem.ontobuilder.matching.algorithms.line2.stablemarriage;

import java.util.*;

/**
 * <p>Title: StableMarriagePlayer</p>
 */
public class StableMarriagePlayer
{
    /**
     * Constructs a StableMarriagePlayer
     *
     * @param nPreferencesSize the number of alloswed preferences for each player
     */
    public StableMarriagePlayer(int nPreferencesSize)
    {
        m_bMarried = false;
        m_uPartner = null;
        m_sName = "";
        m_lPreferencesList = new LinkedList<StableMarriagePlayer>();
        m_nPreferencesSize = nPreferencesSize;
        for (int i = 0; i < m_nPreferencesSize; ++i)
        {
            m_lPreferencesList.add(i, null);
        }
    }

    /**
     * Constructs a StableMarriagePlayer
     *
     * @param sName the name of the player
     */
    public StableMarriagePlayer(String sName)
    {
        m_bMarried = false;
        m_uPartner = null;
        m_sName = sName;
        m_lPreferencesList = new LinkedList<StableMarriagePlayer>();
        /*
         * m_nPreferencesSize = nPreferencesSize; for (int i = 0; i < m_nPreferencesSize; ++i) {
         * m_lPreferencesList.add(i, null); }
         */

    }

    /**
     * Set the name of the player
     *
     * @param sName the name of the player
     */
    public void setName(String sName)
    {
        m_sName = sName;
    }

    /**
     * Get the name of the player
     *
     * @return the name of the player
     */
    public String getName()
    {
        if (m_sName == null)
        {
            return "";
        }
        return m_sName;
    }

    /**
     * Add a ranked partner to the player
     *
     * @param rankedPartner the ranked partner
     * @param rank the rank to add the partner in
     * @return <code>true</code> on success
     */
    public boolean addRankedPartner(StableMarriagePlayer rankedPartner, int rank)
    {
        if ( /* (rank > m_nPreferencesSize) || */(rankedPartner == null))
        {
            return false;
        }

        if ((!m_lPreferencesList.contains(rankedPartner))/*
                                                          * && (m_lPreferencesList.get(rank) ==
                                                          * null)
                                                          */)
        {
            try
            {
                m_lPreferencesList.add(rank, rankedPartner);
            }
            catch (Exception e)
            {
                System.out.println(e.toString());
                return false;
            }
        }
        else
        {
            return false;
        }
        return true;
    }

    public boolean equals(StableMarriagePlayer object)
    {
        if (m_sName.equals(object.getName()))
        {
            return true;
        }
        return false;
    }

    /**
     * @return <code>null</code>
     */
    public StableMarriagePlayer popFirstPreference()
    {
        return null;
    }

    /**
     * Checks whether this partner is more preferred than the current partner
     * 
     * @param candidate the {@link StableMarriagePlayer} to check
     * @return <code>false</code> always
     */
    public boolean morePrefer(StableMarriagePlayer candidate)
    {
        return false;
    }

    /**
     * Sets a new partner the {@link StableMarriagePlayer} to set
     *
     * @param newPartner
     */
    public void setPartner(StableMarriagePlayer newPartner)
    {
        m_uPartner = newPartner;
    }

    /**
     * Get the current partner 
     *
     * @return the {@link StableMarriagePlayer} partner
     */
    public StableMarriagePlayer getPartner()
    {
        return m_uPartner;
    }

    public boolean Islegal()
    {
        /*
         * if (m_lPreferencesList.size() != m_nPreferencesSize) { return false; } for (int i = 0; i
         * < m_nPreferencesSize; ++i) { if (m_lPreferencesList.get(i) == null) { return false; } }
         */
        return true;
    }

    public boolean IsMarried()
    {
        return this.m_bMarried;
    }

    public void SetMarried(boolean bMarried)
    {
        m_bMarried = bMarried;
    }

    public String toString()
    {
        StringBuffer str = new StringBuffer(
            "                                                                  ");
        str.replace(0, m_sName.length() - 1, m_sName);

        if (m_uPartner != null)
        {
            str.append(m_uPartner.getName());
        }
        else
        {
            str.append("could not be matched");
        }
        return str.toString();
    }

    String m_sName;
    boolean m_bMarried;
    StableMarriagePlayer m_uPartner;
    LinkedList<StableMarriagePlayer> m_lPreferencesList;
    int m_nPreferencesSize;

}
