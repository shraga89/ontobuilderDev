package ac.technion.iem.ontobuilder.gui.tools.topk;

import java.util.HashMap;

/**
 * <p>Title: DominantsMap</p>
 * @author haggai
 */
public class DominantsMap
{
    // sum>=average(threshold)>=average>=min>=product (the last iequation holds only for the range
    // [0,1])
    static HashMap<String, Integer> map = new HashMap<String, Integer>();

    static
    {
        int order = 0;
        map.put("SUM", new Integer(order++));
        map.put("AVG_THRESHOLD", new Integer(order++));
        map.put("AVG", new Integer(order++));
        map.put("MIN", new Integer(order++));
        map.put("PROD", new Integer(order));
    }

    /**
     * Check is a first integer in the map dominates the other
     * 
     * @param order1 the key of the first integer
     * @param order2 the key of the second integer
     * @return <code>true</code> of order1 dominates order2
     */
    public static boolean isDominated(String order1, String order2)
    {
        if (map.containsKey(order1) && map.containsKey(order2))
        {
            int o1 = ((Integer) map.get(order1)).intValue();
            int o2 = ((Integer) map.get(order2)).intValue();
            return (o1 <= o2);
        }
        else
        {
            return false;
        }
    }
}
