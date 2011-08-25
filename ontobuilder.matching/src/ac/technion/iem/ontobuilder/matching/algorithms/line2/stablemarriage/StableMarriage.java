package ac.technion.iem.ontobuilder.matching.algorithms.line2.stablemarriage;

import java.util.*;

/**
 * <p>Title: StableMarriage</p>
 */
public class StableMarriage
{
    /**
     * Constructs a default StableMarriage
     */
    public StableMarriage()
    {
        m_sMenList = new LinkedList<Man>();
        m_sWomenList = new LinkedList<Woman>();
    }

    /**
     * Constructs a StableMarriage
     *
     * @param iMenSize the number of men
     * @param iWomenSize the number of women
     */
    public StableMarriage(int iMenSize, int iWomenSize)
    {
        m_sMenList = new LinkedList<Man>();
        m_sWomenList = new LinkedList<Woman>();
        m_iMenSize = iMenSize;
        m_iWomenSize = iWomenSize;
    }

    /**
     * Set the size of men and women in the algortihm
     *
     * @param iMenSize the number of men
     * @param iWomenSize the number of women
     */
    public void setSize(int iMenSize, int iWomenSize)
    {
        m_iMenSize = iMenSize;
        m_iWomenSize = iWomenSize;
    }

    /**
     * Adds a man
     *
     * @param aMan the {@link Man}
     * @return <code>true</code> if the man was added successfully
     */
    public boolean addMan(Man aMan)
    {
        if (m_sMenList.size() >= m_iMenSize)
        {
            return false;
        }
        if ((aMan.Islegal()) && (!aMan.IsMarried()))
        {
            return m_sMenList.add(aMan);
        }
        return false;

    }

    /**
     * Adds a woman
     *
     * @param aMan the {@link Woman}
     * @return <code>true</code> if the woman was added successfully
     */
    public boolean addWoman(Woman aWoman)
    {
        if (m_sWomenList.size() >= m_iWomenSize)
        {
            return false;
        }
        if ((aWoman.Islegal()) && (!aWoman.IsMarried()))
        {
            return m_sWomenList.add(aWoman);
        }
        return false;

    }

    /**
     * Set that a man and woman are married
     *
     * @param aMan the {@link Man}
     * @param aWoman the {@link Woman}
     */
    protected void justMarried(Man aMan, Woman aWoman)
    {
        aMan.SetMarried(true);
        aWoman.SetMarried(true);
        aMan.setPartner(aWoman);
        aWoman.setPartner(aMan);
    }

    /**
     * Set that a man and woman are divorced
     *
     * @param aMan the {@link Man}
     * @param aWoman the {@link Woman}
     */
    protected void divorce(Man aMan, Woman aWoman)
    {
        aMan.SetMarried(false);
        aWoman.SetMarried(false);
        aMan.setPartner(null);
        aWoman.setPartner(null);
    }

    /**
     * Get the man in the stable marriage
     *
     * @return a set of {@link Man}
     */
    public HashSet<Man> getStableMarriage()
    {
        if ((m_sMenList.size() != m_iMenSize) || (m_sWomenList.size() != m_iWomenSize))
        {
            return null;
        }
        HashSet<Man> MarriedMen = new HashSet<Man>();
        while (MarriedMen.size() < m_iMenSize)
        {
            Man man = (Man) m_sMenList.getFirst();
            Woman woman = (Woman) (man.popFirstPreference());
            if (woman == null)
            {
                m_sMenList.removeFirst();
                man.
                SetMarried(true);
                man.setPartner(null);
                MarriedMen.add(man);
            }
            else
            {
                if (woman.IsMarried())
                {
                    if (woman.morePrefer(man))
                    {
                        // handle X husband
                        Man womanXHusband = (Man) woman.getPartner();
                        divorce(womanXHusband, woman);
                        if (!(MarriedMen.remove(womanXHusband)))
                        {
                            System.out.println("Error in algorithm");
                        }
                        m_sMenList.removeFirst();
                        m_sMenList.addFirst(womanXHusband);
                        // Handling X hasband end
                        justMarried(man, woman);

                        MarriedMen.add(man);
                    }
                }
                else
                {
                    justMarried(man, woman);
                    m_sMenList.removeFirst();
                    MarriedMen.add(man);
                }
            }
        }
        return MarriedMen;
    }

    private LinkedList<Man> m_sMenList;
    private LinkedList<Woman> m_sWomenList;
    private int m_iMenSize;
    private int m_iWomenSize;

}
