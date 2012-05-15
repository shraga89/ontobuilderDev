package ac.technion.iem.ontobuilder.matching.algorithms.line2.simple;

import java.util.Properties;

import ac.technion.iem.ontobuilder.matching.algorithms.line2.common.SecondLineAlgorithm;
import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.meta.match.MatchMatrix;

/**
 * This 2LM returns only those matches which have a 
 * confidence value within delta of the max confidence of
 * the candidate or target.
 * @author Tomer Sagi
 *
 */
public class Max2LM implements SecondLineAlgorithm {

	private double delta = 0.0;
	
	/**
	 * Sets the delta value to supplied parameter
	 * @param d
	 */
	public Max2LM(double d)
	{
		this.delta = d;
	}
	@Override
	public MatchInformation match(MatchInformation mi) {
		MatchInformation res = new MatchInformation(mi.getCandidateOntology(),mi.getTargetOntology());
		MatchMatrix mm = mi.getMatrix();
		for (Match m : mi.getCopyOfMatches())
		{
			double maxC = mm.getMaxConfidence(m.getCandidateTerm(), true)-delta;
			double maxT = mm.getMaxConfidence(m.getTargetTerm(), false)-delta;
			if (m.getEffectiveness()>= Math.min(maxC, maxT))
					res.updateMatch(m.getTargetTerm(),m.getCandidateTerm(),m.getEffectiveness());
		}
		return res;
	}

	@Override
	public String getName() {
		return "Simple Threshold filter";
	}

	@Override
	public String getDescription() {
		return "Return all matches with confidence equal or over supplied threshold. \n";
	}

	@Override
	public boolean init(Properties prop) {
		this.delta = Double.parseDouble(prop.getProperty("delta", "0.1"));	
		return true;
	}

}
