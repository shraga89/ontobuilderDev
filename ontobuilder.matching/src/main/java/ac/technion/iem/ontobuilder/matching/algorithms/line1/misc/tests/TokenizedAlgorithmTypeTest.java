package ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.tests;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.algorithms.TokenizedAlgorithmType;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.algorithms.TokenizedWordAlgorithm;

public class TokenizedAlgorithmTypeTest {

	private TokenizedAlgorithmType classUnderTest;
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	//Test GetImplementedAlgorithm
	//Trying to create an instance of each algorithm, by using all of its contractors
	@Test
	public void testGetImplementedAlgorithm() {
		TokenizedAlgorithmType[] tokenizedAlgorithmTypes = TokenizedAlgorithmType.values();
		for (int i = 0; i < tokenizedAlgorithmTypes.length; i++) {
			classUnderTest = tokenizedAlgorithmTypes[i];
			TokenizedWordAlgorithm algorithem = classUnderTest.getImplementedAlgorithm();
			assertNotNull("Couldn't create constractor for" + classUnderTest, algorithem);
		}
	}

}
