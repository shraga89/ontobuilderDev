package ac.technion.iem.ontobuilder.matching.algorithms.line2.common;

import java.util.Properties;

import ac.technion.iem.ontobuilder.matching.match.MatchInformation;

/**
 * <p>Title: interface SecondLineAlgorithm is intended for Matching algorithms 
 * which operate on the results of a first line @link{Algorithm}</p>
 */
public interface SecondLineAlgorithm
{
	/**
	 * Implementation specific initializer.
	 * @param prop Initialization Properties
	 * @return true if initialization properties match expected.
	 */
	public boolean init(Properties prop);
	
	/**
	 * Major method of this interface. Performs some algorithm over supplied match
	 * result and records result in MatchInformation object.
	 * @param mi result of a previous matching algorithm.
	 * @return result of this matching algorithm. 
	 */
    public MatchInformation match(MatchInformation mi);

    public String getName();

    public String getDescription();

}
