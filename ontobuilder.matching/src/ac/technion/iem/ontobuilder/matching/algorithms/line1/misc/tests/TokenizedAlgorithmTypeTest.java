package ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.tests;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.algorithms.TokenizedAlgorithmType;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.algorithms.TokenizedWordAlgorithm;

public class TokenizedAlgorithmTypeTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetTokenizedWordsAlgorithem() {
		TokenizedAlgorithmType[] tokenizedAlgorithmTypes = TokenizedAlgorithmType.values();
		for (int i = 0; i < tokenizedAlgorithmTypes.length; i++) {
			TokenizedAlgorithmType tokenizedAlgorithmType = tokenizedAlgorithmTypes[i];
			TokenizedWordAlgorithm algorithem = tokenizedAlgorithmType.getImplementedAlgorithm();
			assertNotNull("Couldn't create constractor for" + tokenizedAlgorithmType, algorithem);
		}
	}

}
