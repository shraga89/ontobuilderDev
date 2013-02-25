package ac.technion.iem.ontobuilder.matching.algorithms.line1.misc;

import java.util.ArrayList;
import java.util.Vector;

import org.jdom2.Element;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.common.AbstractAlgorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.common.Algorithm;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.meta.match.MatchMatrix;
import edu.cmu.lti.ws4j.WS4J; 

public class WordNetAlgorithm extends AbstractAlgorithm {

	public WordNetAlgorithm(){
		
	}
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "WordNet Algorithm";
	}

	@Override
	public String getDescription() {
		return "WordNet Algorithm";
	}
	
	@Override
	public MatchInformation match(Ontology o1,
			Ontology o2) {
		
		  ArrayList<Term> candTerms = getTerms(o1);
	      ArrayList<Term> targTerms = getTerms(o2);
	      MatchMatrix matrix = new MatchMatrix(candTerms.size(), targTerms.size(), candTerms,
	              targTerms);
	      matrix.setMatchMatrix(getMatchMatrix(candTerms, targTerms));
	      MatchInformation matchInformation = new MatchInformation(o1,o2);
	      matchInformation.setMatrix(matrix);
	      matchInformation.setAlgorithm(this);
	      
	      return matchInformation ;
	}

	@Override
	public void configure(Element element) {
		
	}
	
	 private double[][] getMatchMatrix( ArrayList<Term> cands,
		        ArrayList<Term> targs)
		    {
		        double[][] result = new double[targs.size()][cands.size()];

		        for (int i = 0; i < targs.size(); i++)
		        {
		            for (int j = 0; j < cands.size(); j++)
		            {
		               result[i][j] = WS4J.calcDistanceByJiangConrath(cands.get(j).getName(),targs.get(i).getName());
		            }
		        }
		        return result;
		    }

	@Override
	public Algorithm makeCopy() {
		WordNetAlgorithm algo = new WordNetAlgorithm();
		return algo ;
	}
	
	  private ArrayList<Term> getTerms(Ontology o)
	    {
	        Vector<Term> terms = o.getTerms(true);
	        ArrayList<Term> result = new ArrayList<Term>();
	        for (int i = 0; i < terms.size(); i++)
	        {
	            result.add(i, terms.get(i));
	        }
	        return result;
	    }


}


