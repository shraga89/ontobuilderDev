package ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.algorithms.TokenizedAlgorithmType;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.algorithms.TokenizedWordGreedyAlgorithm;

public class TokenizedWordGreedyAlgorithmTest {
	
	private TokenizedWordGreedyAlgorithm classUnderTest;

	@Before
	public void setUp() throws Exception {
		classUnderTest = new TokenizedWordGreedyAlgorithm();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testTokenizeTermsNullAndEmpty() {
		List<String> tokenizeTerms = classUnderTest.tokenizeTerms(null);
		assertTrue("Didn't recieve an empty list", tokenizeTerms.isEmpty() );
		Term term = new Term();
		tokenizeTerms = classUnderTest.tokenizeTerms(term);
		assertTrue("Didn't recieve an empty list", tokenizeTerms.isEmpty() );
	}
	
	@Test
	public void testTokenizeTermsSingleWord() {
		Term term = new Term("Cat");
		List<String> tokenizeTerms = classUnderTest.tokenizeTerms(term);
		assertTrue("Recieved more than a single word", tokenizeTerms.size() == 1);
		assertTrue("Didn't recieve 'Cat'", tokenizeTerms.contains("Cat") );
	}
	
	@Test
	public void testTokenizeTermsPlural() {
		Term term = new Term("Cats");
		List<String> tokenizeTerms = classUnderTest.tokenizeTerms(term);
		assertTrue("Recieved more than a single word", tokenizeTerms.size() == 1);
		assertTrue("Didn't recieve 'Cats'", tokenizeTerms.get(0).equalsIgnoreCase("Cats") );
		
		term.setName("Orders");
		tokenizeTerms = classUnderTest.tokenizeTerms(term); 
		assertTrue("Recieved more than a single word", tokenizeTerms.size() == 1);
		assertTrue("Didn't recieve 'Orders'", tokenizeTerms.contains("Orders")|| tokenizeTerms.contains("orders"));
		
		
		term.setName("DogEatsCat");
		term = new Term("CarBikeShip");
		tokenizeTerms = classUnderTest.tokenizeTerms(term);
		assertTrue("Recieved more or less than two words", tokenizeTerms.size() == 2);
		assertTrue("Didn't recieve 'Car'", tokenizeTerms.contains("Car") || tokenizeTerms.contains("car"));
		assertTrue("Didn't recieve 'Ship'", tokenizeTerms.contains("Ship") || tokenizeTerms.contains("ship") );
		
	}
	
	@Test
	public void testTokenizeTermsPrefixOnly() {
		Term term = new Term("Catxd");
		List<String> tokenizeTerms = classUnderTest.tokenizeTerms(term);
		assertTrue("Recieved more than a single word", tokenizeTerms.size() == 1);
		assertTrue("Didn't recieve 'Cat'", tokenizeTerms.get(0).equalsIgnoreCase("Cat") );
	}
	
	@Test
	public void testTokenizeTermsSuffixOnly() {
		Term term = new Term("xdCat");
		List<String> tokenizeTerms = classUnderTest.tokenizeTerms(term);
		assertTrue("Recieved more than a single word", tokenizeTerms.size() == 1);
		assertTrue("Didn't recieve 'Cat'", tokenizeTerms.get(0).equalsIgnoreCase("Cat") );
	}

	@Test
	public void testGetAlgorithmType() {
		assertEquals("Algorithm Type is not as expected", TokenizedAlgorithmType.greedy, classUnderTest.getAlgorithmType());
	}

}
