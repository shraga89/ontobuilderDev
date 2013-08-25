package ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.tests;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.algorithms.TokenizedAlgorithmType;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.algorithms.TokenizedWordAlgorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.algorithms.TokenizedWordAlgorithmFactory;

public class TokenizedWordAlgorithemFactoryTest {
	private TokenizedWordAlgorithmFactory classUnderTest;

	@Before
	public void setUp() throws Exception {
		classUnderTest = new TokenizedWordAlgorithmFactory(); 
	}

	@After
	public void tearDown() throws Exception {
	}

	//test that all algorithms in TokenizedAlgorithmType are created using TokenizedWordAlgorithmFactory class
	@Test
	public void testBuild() {
		ArrayList<TokenizedWordAlgorithm> list = classUnderTest.build();
		assertEquals(classUnderTest.getClass().getSimpleName() + " didn't build all algorithems", TokenizedAlgorithmType.values().length, list.size());
	}

}
