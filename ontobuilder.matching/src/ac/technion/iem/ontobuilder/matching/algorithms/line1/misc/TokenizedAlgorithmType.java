package ac.technion.iem.ontobuilder.matching.algorithms.line1.misc;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

/**
 * Add to this Enum the names of all algorithms that implement {@link TokenizedWordAlgorithm}
 * 
 * @author Sapir Golan
 * 
 *
 */
public enum TokenizedAlgorithmType {
	simple(TokenizedWordsSimpleAlgorithem.class), greedy(TokenizedWordGreedyAlgorithem.class);
	
	private Class<?> algorithmType;
	
//	private TokenizedAlgorithmType(TokenizedAlgorithmType algorithmType) {
//		this.algorithmType = algorithmType;
//	}
	
	private TokenizedAlgorithmType(Class<?> algorithmType) {
		this.algorithmType = algorithmType;
	}
	
	/**
	 * This method will return an instance the implements {@link TokenizedWordAlgorithm} according to its {@link #algorithmType} value
	 * @return {@link TokenizedWordAlgorithm}
	 */
	public TokenizedWordAlgorithm getImplimentedAlgorithem () {
		TokenizedWordAlgorithm instance = null;
		Constructor[] declaredConstructors = this.algorithmType.getDeclaredConstructors();
		List<Constructor> list = Arrays.asList(declaredConstructors);
		for (Constructor<?> constructor : list) {
			if (Modifier.PUBLIC == constructor.getModifiers()) {
				try {
					instance = (TokenizedWordAlgorithm) constructor.newInstance(new Object[]{});
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
		return instance;
	}
	
//	public TokenizedAlgorithmType getTokenizedAlgorithmType() {
//		return algorithmType;
//	}
}
