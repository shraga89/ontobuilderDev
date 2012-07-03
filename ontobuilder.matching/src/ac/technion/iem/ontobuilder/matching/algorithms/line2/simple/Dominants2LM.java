package ac.technion.iem.ontobuilder.matching.algorithms.line2.simple;

import java.util.Properties;

import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.common.SecondLineAlgorithm;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.meta.match.MatchMatrix;

/**
 * This 2LM returns those matches which have the max value of both their row and column 
 * @author Tomer Sagi
 *
 */
public class Dominants2LM implements SecondLineAlgorithm {

	@Override
	public MatchInformation match(MatchInformation mi) {
		MatchInformation res = new MatchInformation(mi.getCandidateOntology(),mi.getTargetOntology());
		MatchMatrix mm = mi.getMatrix();
	    for (Term t : mm.getTargetTerms()) {
	      for (Term c : mm.getCandidateTerms()) {
	        double dConfidence = mm.getMatchConfidence(c,t);
	        Double d1 = (Double) (mm.getMaxConfidence(t, false));
	        double d1value = d1.doubleValue();
	        Double d2 = (Double) (mm.getMaxConfidence(c, true));
	        double d2value = d2.doubleValue();
	        if ( (dConfidence == d1value) && (dConfidence == d2value)) res.updateMatch(t, c, dConfidence);
	      }
	    }
		return res;
	}

	@Override
	public String getName() {
		return "Dominants";
	}

	@Override
	public String getDescription() {
		return "Return all matches which have the max value of both their row and column \n";
	}

	@Override
	public boolean init(Properties prop) {	
		return true;
	}

}
