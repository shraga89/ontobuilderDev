package ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import junit.framework.Assert;

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
	public void tokenizeTermsCompundValueTest() {
		Term term = new Term();
		term.setName("PurchaseCarBikeShipOrder");
		List<String> result = classUndetTest.tokenizeTerms(term); 
		assertEquals("Algorithm didn't find all words", 5, result.size());
		assertTrue("Result of algorithm doesn't contain 'Purchase'", result.contains("Purchase"));
		assertTrue("Result of algorithm doesn't contain 'Car'", result.contains("Car"));
		assertTrue("Result of algorithm doesn't contain 'Bike'", result.contains("Bike"));
		assertTrue("Result of algorithm doesn't contain 'Ship'", result.contains("Ship"));
		assertTrue("Result of algorithm doesn't contain 'Order'", result.contains("Order"));
	}
	
	@Test
	public void tokenizeTermsNullAndEmptyTest() {
		List<String> tokenizeTerms = classUndetTest.tokenizeTerms(null);
		Assert.assertNotNull(tokenizeTerms);
		assertTrue(tokenizeTerms.size() == 0);
		Term term = new Term();
		tokenizeTerms = classUndetTest.tokenizeTerms(term);
		assertTrue(tokenizeTerms.size() == 0);
	}
	
	@Test
	//The good result in my opinion is Order
	public void tokenizeTermsPluralTest() {
		Term term = new Term();
		term.setName("Orders");
		List<String> result = classUndetTest.tokenizeTerms(term); 
		assertEquals("Algorithm didn't find all words", 1, result.size());
		assertTrue("Result of algorithm doesn't contain 'Orders'", result.contains("Orders"));
	}
	
	@Test
	public void tokenizeTermsMultiWordTest() {
		Term term = new Term();
		term.setName("purchaseimportantorder");
		List<String> result = classUndetTest.tokenizeTerms(term); 
		assertEquals("Algorithm didn't find all words", 3, result.size());
		assertTrue("Result of algorithm doesn't contain 'purchase'", result.contains("purchase"));
		assertTrue("Result of algorithm doesn't contain 'order'", result.contains("order"));
		assertTrue("Result of algorithm doesn't contain 'important'", result.contains("important"));
	}
	
	@Test
	public void tokenizeTermsMultiWordAndNoneWordsTest() {
		Term term = new Term();
		term.setName("77dogmovie");
		List<String> result = classUndetTest.tokenizeTerms(term); 
		assertEquals("Algorithm didn't find all words", 2, result.size());
		assertTrue("Result of algorithm doesn't contain 'dog'", result.contains("dog"));
		assertTrue("Result of algorithm doesn't contain 'movie'", result.contains("movie"));
		
		term.setName("Zdogmovie");
		result = classUndetTest.tokenizeTerms(term); 
		assertEquals("Algorithm didn't find all words", 2, result.size());
		assertTrue("Result of algorithm doesn't contain 'dog'", result.contains("dog"));
		assertTrue("Result of algorithm doesn't contain 'movie'", result.contains("movie"));
		
		term.setName("dogprince88");
		result = classUndetTest.tokenizeTerms(term); 
		assertEquals("Algorithm didn't find all words", 2, result.size());
		assertTrue("Result of algorithm doesn't contain 'dog'", result.contains("dog"));
		assertTrue("Result of algorithm doesn't contain 'prince'", result.contains("prince"));
		
		term.setName("dogprinceI");
		result = classUndetTest.tokenizeTerms(term); 
		assertEquals("Algorithm didn't find all words", 2, result.size());
		assertTrue("Result of algorithm doesn't contain 'dog'", result.contains("dog"));
		assertTrue("Result of algorithm doesn't contain 'prince'", result.contains("prince"));
	}

}
