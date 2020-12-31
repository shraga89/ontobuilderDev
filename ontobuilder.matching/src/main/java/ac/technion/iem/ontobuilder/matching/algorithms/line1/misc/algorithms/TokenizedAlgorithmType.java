package ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.algorithms;

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
	simple(TokenizedWordsSimpleAlgorithm.class), greedy(TokenizedWordGreedyAlgorithm.class), 
	greedystar(TokenizedWordGreedyStarAlgorithm.class);
	
	private Class<?> algorithmType;
	
//	private TokenizedAlgorithmType(TokenizedAlgorithmType algorithmType) {
//		this.algorithmType = algorithmType;
//	}
	
	private TokenizedAlgorithmType(Class<?> algorithmType) {
		this.algorithmType = algorithmType;
	}
	
	/**
	 * This method will return an instance the implements {@link TokenizedWordAlgorithm} according to its {@link #algorithmType} value.<br>
	 * Once an instance is created (by any contractor) it's returned. 
	 * @return {@link TokenizedWordAlgorithm}
	 */
	public TokenizedWordAlgorithm getImplementedAlgorithm () {
		TokenizedWordAlgorithm instance = null;
		Constructor<?>[] declaredConstructors = this.algorithmType.getDeclaredConstructors();
		List<Constructor<?>> list = Arrays.asList(declaredConstructors);
		for (Constructor<?> constructor : list) {
			if (Modifier.PUBLIC == constructor.getModifiers()) {
				try {
					instance = (TokenizedWordAlgorithm) constructor.newInstance(new Object[]{});
					break;
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
	
	public Class<?> getTokenizedAlgorithmType() {
		return this.algorithmType;
	}
}
