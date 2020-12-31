package ac.technion.iem.ontobuilder.matching.algorithms.line2.simple;

import java.util.Properties;

import ac.technion.iem.ontobuilder.matching.algorithms.line2.common.SecondLineAlgorithm;
import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;

/**
 * Simple 2LM returns all matches with confidence over threshold
 * @author Tomer Sagi
 *
 */
public class Threshold2LM implements SecondLineAlgorithm {

	private double threshold;
	
	public Threshold2LM(double t)
	{
		this.threshold = t;
	}
	@Override
	public MatchInformation match(MatchInformation mi) {
		MatchInformation res = new MatchInformation(mi.getCandidateOntology(),mi.getTargetOntology());
		for (Match m : mi.getCopyOfMatches())
			if (m.getEffectiveness() >= threshold)
				res.updateMatch(m.getTargetTerm(), m.getCandidateTerm(), m.getEffectiveness());
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
		this.threshold = Double.parseDouble(prop.getProperty("t", "0.1"));	
		return true;
	}

}
