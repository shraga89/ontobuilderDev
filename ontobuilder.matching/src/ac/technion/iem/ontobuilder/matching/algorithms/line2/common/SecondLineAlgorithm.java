package ac.technion.iem.ontobuilder.matching.algorithms.line2.common;

import ac.technion.iem.ontobuilder.matching.match.MatchInformation;

/**
 * <p>Title: interface SecondLineAlgorithm is intended for Matching algorithms 
 * which operate on the results of a first line @link{Algorithm}</p>
 */
public interface SecondLineAlgorithm
{
	
    public MatchInformation match(MatchInformation mi);

    public String getName();

    public String getDescription();

}
