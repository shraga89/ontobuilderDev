package ac.technion.iem.ontobuilder.matching.utils;

import java.util.Collection;

public class ArrayConversion {
	
	public static int[] toPrimitiveInt(Collection<Integer> collection) {
		Integer[] oArray = collection.toArray(new Integer[0]);
		int[] pArray = new int[oArray.length];
		for (int i = 0; i < oArray.length; i++)
			pArray[i] = oArray[i];
		return pArray;
	}

	public static long[] toPrimitiveLong(Collection<Long> collection) {
		Long[] oArray = collection.toArray(new Long[0]);
		long[] pArray = new long[oArray.length];
		for (int i = 0; i < oArray.length; i++)
			pArray[i] = oArray[i];
		return pArray;
	}
}
