package ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.algorithms.TokenizedWordGreedyStarAlgorithm;

public class TokenizedWordGreedyStarAlgorithmTest {
	
	private TokenizedWordGreedyStarAlgorithm classUndetTest;

	@Before
	public void setUp() throws Exception {
		classUndetTest = new TokenizedWordGreedyStarAlgorithm();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test

	public void GreedyStarTest() {

		Term t = new Term();
		t.setName("PurchaseCarBikeShipOrder");
		List<String> result = classUndetTest.tokenizeTerms(t); 
		assertEquals("Algorithm didn't find all words", 5, result.size());
		assertTrue("Result of algorithm doesn't contain 'Purchase'", result.contains("Purchase"));
		assertTrue("Result of algorithm doesn't contain 'Car'", result.contains("Car"));
		assertTrue("Result of algorithm doesn't contain 'Bike'", result.contains("Bike"));
		assertTrue("Result of algorithm doesn't contain 'Ship'", result.contains("Ship"));
		assertTrue("Result of algorithm doesn't contain 'Order'", result.contains("Order"));
	}
}
