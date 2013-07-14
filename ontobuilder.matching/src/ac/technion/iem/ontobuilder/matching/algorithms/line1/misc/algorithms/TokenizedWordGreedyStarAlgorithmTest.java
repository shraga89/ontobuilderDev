package ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.algorithms;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ac.technion.iem.ontobuilder.core.ontology.Term;

public class TokenizedWordGreedyStarAlgorithmTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test

	public void GreedyStarTest(){
	
	TokenizedWordGreedyStarAlgorithm alg = new TokenizedWordGreedyStarAlgorithm();
	
	Term t = new Term();
	t.setName("PurchaseCarOrder");
	List<String> result;// = new ArrayList<String>();
	result = alg.tokenizeTerms(t);

	}
}
