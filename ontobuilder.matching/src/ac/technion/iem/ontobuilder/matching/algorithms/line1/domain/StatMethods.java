package ac.technion.iem.ontobuilder.matching.algorithms.line1.domain;

import java.util.List;

public class StatMethods {
    public static int sum (List<Integer> a){
    	if (a.size() > 0) {
    		int sum = 0;
    	    for (Integer i : a) {
    	    	sum += i;
    	
    	    }
            return sum;
 
        }
        return 0;
    }
    
	public static double mean (List<Integer> a){
		int sum = sum(a);
		double mean = 0;
		if (sum > 0) {
			mean = sum / (a.size() * 1.0);
		}
		return mean;
	}
	
	 public static double sd (List<Integer> a){
		   int sum = 0;
		   double mean = mean(a);
		   for (Integer i : a){
			   sum += Math.pow((i - mean), 2);
		   }
		   return Math.sqrt( sum / ( a.size() - 1 ) );
	  }
		

}
