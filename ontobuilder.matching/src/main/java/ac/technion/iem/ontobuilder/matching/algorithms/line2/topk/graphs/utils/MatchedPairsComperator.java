package ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.utils;

import ac.technion.iem.ontobuilder.matching.meta.match.MatchedAttributePair;

/**
 * <p>Title: MatchedPairsComperator</p>
 * <p>Description: Comparator for a couple of MatchedAttributePairs</p>
 * Extends {@link DoubleComparator}
 */
public class MatchedPairsComperator extends DoubleComparator
{

    public MatchedPairsComperator()
    {
    }

    public int compare(MatchedAttributePair pair1, MatchedAttributePair pair2)
    {
        MatchedAttributePair map1 = pair1;
        MatchedAttributePair map2 = pair2;
        return super.compare(new Double(map2.getMatchedPairWeight()),
            new Double(map1.getMatchedPairWeight()));
    }
}