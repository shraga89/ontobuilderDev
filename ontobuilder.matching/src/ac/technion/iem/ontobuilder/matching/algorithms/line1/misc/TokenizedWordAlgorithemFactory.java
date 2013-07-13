package ac.technion.iem.ontobuilder.matching.algorithms.line1.misc;

import java.util.ArrayList;

/**
 * This class is a factory for all the algorithms that implement TokenizedWordAlgorithem.<br>
 * The {@link #build() } method return an ArrayList of with an implementation of all the classes that implement TokenizedWordAlgorithem interface.<br>
 * If you have created a class that implements {@link TokenizedWordAlgorithemFactory} and don't need special configuration besides these of the <i>default contractor</i>
 * than you should add their creation in  {@link #createAlgorithmsWithNoDefaultContractor()}. 
 * 
 * @author Sapir Golan
 *
 */
public class TokenizedWordAlgorithemFactory {

	/**
	 * This method return an ArrayList with all algorithms that implement {@link TokenizedWordAlgorithm}.
	 * It return only algorithms that appear in {@link TokenizedWordAlgorithemFactory}.
	 * @return
	 */
	public ArrayList<TokenizedWordAlgorithm> build() {
		ArrayList<TokenizedWordAlgorithm> result = new ArrayList<TokenizedWordAlgorithm>();
		TokenizedAlgorithmType[] tokenizedAlgorithmTypes = TokenizedAlgorithmType.values();
		for (int i = 0; i < tokenizedAlgorithmTypes.length; i++) {
			TokenizedAlgorithmType tokenizedAlgorithmType = tokenizedAlgorithmTypes[i];
			TokenizedWordAlgorithm algorithem = tokenizedAlgorithmType.getImplimentedAlgorithem();
			result.add(algorithem);
		}
		
		//add algorithms that were created with no default contractor
		ArrayList<TokenizedWordAlgorithm> algorithmsWithNoDefaultContractor = this.createAlgorithmsWithNoDefaultContractor();
		if (algorithmsWithNoDefaultContractor.size()!=0) {
			//Search if an algorithms that was created using no default contractor
			//was also created using the default contractor.
			//If so, remove the instance that was created with the default contractor
			for (TokenizedWordAlgorithm noDefaultConstractor : algorithmsWithNoDefaultContractor) {
				for (TokenizedWordAlgorithm defaultConstracotr : result) {
					//if we have created an algorithm using no default contractor but it has also a default contractor
					if ( defaultConstracotr.getClass() == noDefaultConstractor.getClass() ) {
						result.remove(defaultConstracotr);
					}
				}
				
			}
			result.addAll(algorithmsWithNoDefaultContractor);
		}
		
		return result;
	}
	
	/**
	 * Add to this method instances of class that implement {@link TokenizedWordAlgorithm} interface and need to 
	 * be created by using the a <b>special contractor</b>
	 * @return
	 */
	private ArrayList<TokenizedWordAlgorithm> createAlgorithmsWithNoDefaultContractor() {
		ArrayList<TokenizedWordAlgorithm> result = new ArrayList<TokenizedWordAlgorithm>();
		
		return result; 
		
	}

}
