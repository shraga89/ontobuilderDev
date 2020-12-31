package ac.technion.iem.ontobuilder.matching.algorithms.line2.set;

import java.util.Properties;

import ac.technion.iem.ontobuilder.matching.algorithms.line2.common.SecondLineAlgorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.simple.Dominants2LM;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.stablemarriage.StableMarriageWrapper;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;

/**
 * This 2LM returns the union of two algorithms on the same 1LM result.
 * Default algorithms run are @link{StableMarriage} and @link{Dominants2LM}
 * @author Tomer Sagi
 *
 */
public class Union2LM implements SecondLineAlgorithm {
	
	private SecondLineAlgorithm _first2LM = new StableMarriageWrapper();
	/**
	 * @return the _first2LM
	 */
	public SecondLineAlgorithm get_first2LM() {
		return _first2LM;
	}

	/**
	 * @param _first2LM the _first2LM to set
	 */
	public void set_first2LM(SecondLineAlgorithm _first2LM) {
		this._first2LM = _first2LM;
	}

	/**
	 * @return the _second2LM
	 */
	public SecondLineAlgorithm get_second2LM() {
		return _second2LM;
	}

	/**
	 * @param _second2LM the _second2LM to set
	 */
	public void set_second2LM(SecondLineAlgorithm _second2LM) {
		this._second2LM = _second2LM;
	}

	private SecondLineAlgorithm _second2LM = new Dominants2LM();

	/**
	 * Replaces default 2LM with supplied ones 
	 * @param first2LM
	 * @param second2LM
	 */
	public Union2LM(SecondLineAlgorithm first2LM,SecondLineAlgorithm second2LM)
	{
		_first2LM = first2LM;
		_second2LM = second2LM;
	}
	
	/**
	 * Default constructor uses default 2LM
	 */
	public Union2LM()
	{
		
	}
	
	@Override
	public MatchInformation match(MatchInformation mi) {
		MatchInformation r1 = _first2LM.match(mi);
		MatchInformation r2 = _second2LM.match(mi);
		return MatchInformation.combineMatches(r1,r2,0.0);
	}

	@Override
	public String getName() {
		return "Union" + _first2LM.getName() + _second2LM.getName();
	}

	@Override
	public String getDescription() {
		return "Return Union of the matches of two algorithms \n";
	}

	@Override
	public boolean init(Properties prop) {	
		return true;
	}

}
