package ac.technion.iem.ontobuilder.matching.algorithms.line1.ted;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jdom2.Element;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.core.resources.OntoBuilderResources;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.common.AbstractAlgorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.common.Algorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.AlgorithmUtilities;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;

public class TED extends AbstractAlgorithm {
	
	private String stepOneAlgorithm;
	private double basicWeight;
	private double structWeight;
	private double insertWeight;
	private double deleteWeight;
	private double threshold;
		
	public TED(){
		this("TED Graph Match");
	}
	
	public TED(String stepOne){
		this(stepOne, 0.5);
	}
		
	public TED(String stepOne, double structWeight){
		this(stepOne, structWeight, 0.6);
	}
	
	public TED(String stepOne, double structWeight, double structThreshold){
		this(stepOne, structWeight, structThreshold, 0.5);
	}
	
	public TED(String stepOne, double structWeight, double structThreshold, double insertWeight){
		this.stepOneAlgorithm = stepOne; //TODO - get algorithm dynamically thru qualified name?
		this.structWeight = structWeight;
		this.basicWeight = 1 - structWeight;
		this.insertWeight = insertWeight;
		this.deleteWeight = 1 - insertWeight;
		this.threshold = structThreshold;
	}
	

	@Override
	public String getName() {
		return "TED";
	}

	@Override
	public String getDescription() {
		return "Tree Edit Distance Algorithm";
	}

	@Override
	public MatchInformation match(Ontology targetOntology,
			Ontology candidateOntology) {
		Algorithm stepOne = null;
		try{
			stepOne = AlgorithmUtilities.getAlgorithmsInstance(getClass().getResourceAsStream(OntoBuilderResources.Config.Matching.ALGORITHMS_XML), stepOneAlgorithm);
    	}catch(Exception e){
    		System.err.println(e.getMessage());
    		System.exit(0);
    	} 
		MatchInformation stepOneResults = stepOne.match(targetOntology, candidateOntology);
		MatchInformation stepTwoResults = computeStractural(targetOntology, candidateOntology);
		MatchInformation results = new MatchInformation(candidateOntology, targetOntology);
		for(Term a: targetOntology.getTerms(true)){
			for(Term b: candidateOntology.getTerms(true)){
				double structural = stepTwoResults.getMatchConfidence(b, a)*this.structWeight;
				double basic = stepOneResults.getMatchConfidence(b, a)*this.basicWeight;
				if((structural + basic) > this.threshold){
					results.updateMatch(a, b, structural + basic);
				}
			}
		}
		return results;
	}

	/**
	 * Calculates the TED similarity between all pairs of terms from the target and candidate ontologies.  
	 * 
	 * @param target the target ontology.
	 * @param candidate the candidate ontology.
	 * @return the MathcInformation object containing the results of the TED similarity.
	 */
	public MatchInformation computeStractural(Ontology target, Ontology candidate){
		MappingMatrix mi = new MappingMatrix(target.getAllTermsCount(), candidate.getAllTermsCount(), 0, 0);
		TreeTools<Term> targetTools = new TreeTools<Term>(new OntologyTreeWrapper(target));
		TreeTools<Term> candidateTools = new TreeTools<Term>(new OntologyTreeWrapper(candidate));
		Map<Term, Term> targetLeftDescendants = targetTools.leftDescendents();
		Map<Term, Term> candidateLeftDescendants = candidateTools.leftDescendents();
		Collection<Term> targetKeyRoots = targetTools.keyRoots();
		Collection<Term> candidateKeyRoots = candidateTools.keyRoots();
		List<Term> orderedTarget = targetTools.postOrder();
		List<Term> orderedCandidate = candidateTools.postOrder();
		for(Term t1: targetKeyRoots){
			for(Term t2: candidateKeyRoots){
				doMatch(t1, t2, orderedTarget, orderedCandidate, targetLeftDescendants, 
						candidateLeftDescendants, mi);
			}
		}
		MatchInformation ret = new MatchInformation(candidate, target);
		for(int i = 0; i < orderedTarget.size(); i++){
			for(int j = 0; j < orderedCandidate.size(); j++){
				Mapping m = mi.at(i,  j);
				if(m == null){
					ret.updateMatch(orderedTarget.get(i), orderedCandidate.get(j), 0); //no mapping
				}
				else{
					ret.updateMatch(orderedTarget.get(i), orderedCandidate.get(j), 1 - mi.getNormalizedWeight(m));
				}
			}
		}
		return ret;
	}
	
	/**
	 * This method calculates the structural contribution of two given terms to the structural 
	 * scores of the terms of the ontologies.  
	 * 
	 * @param a the first term.
	 * @param b the 2nd term.
	 * @param orderedTarget the target ontology terms post-ordered.
	 * @param orderedCandidate the candidate ontology terms post-ordered.
	 * @param targetLeftDescendants the mapping of the target ontology's terms to their most left leaf descendants.
	 * @param candidateLeftDescendants the mapping of the candidate ontology's terms to their most left leaf descendants.
	 * @param mi the MappingMatrix object holding the structural matching results.
	 */
	public void doMatch(Term a, Term b, List<Term> orderedTarget, List<Term> orderedCandidate,
			Map<Term, Term> targetLeftDescendants, Map<Term, Term> candidateLeftDescendants, MappingMatrix mi){
		Term mostLeftTarget = targetLeftDescendants.get(a);
		Term mostLeftCandidate = candidateLeftDescendants.get(b);
		int targetLeftIndex = orderedTarget.indexOf(mostLeftTarget); //index of the most left descendant of term a in post order
		int candidateLeftIndex = orderedCandidate.indexOf(mostLeftCandidate);  //index of the most left descendant of term b in post order
		int targetIndex = orderedTarget.indexOf(a); //index of term a in post order
		int candidateIndex = orderedCandidate.indexOf(b); //index of term b in post order
		MappingMatrix map = new MappingMatrix(
				targetIndex - targetLeftIndex + 2,
				candidateIndex - candidateLeftIndex + 2,
				targetLeftIndex - 1,
				candidateLeftIndex - 1
				); //+2: +1 for inclusion of both ends and another +1 for a "-1" column and row
					//-1: so the "-1" column and row will be the 0 row & column of the matrix and actual data ill start at 1.
		for(int i = targetLeftIndex; i <= targetIndex; i++){ //init first column = target = delete
			Mapping m = new Mapping(i, candidateLeftIndex - 1);
			m.addWeight(map.at(i - 1, candidateLeftIndex - 1).getComalutiveWeight() + this.deleteWeight);
			map.set(m);
		}
		for(int i = candidateLeftIndex; i <= candidateIndex; i++){ //init first row = candidate = insert
			Mapping m = new Mapping(targetLeftIndex - 1, i);
			m.addWeight(map.at(targetLeftIndex - 1, i - 1).getComalutiveWeight() + this.insertWeight);
			map.set(m);
		}
		for(int i = targetLeftIndex; i <= targetIndex; i++){
			for(int j = candidateLeftIndex; j <= candidateIndex; j++){
				double min = Math.min(map.at(i - 1, j).getComalutiveWeight() + this.deleteWeight, map.at(i,  j - 1).getComalutiveWeight() + this.insertWeight);
				Term currentTarget = orderedTarget.get(i);
				Term currentCandidate = orderedCandidate.get(j);
				Mapping m = new Mapping(i, j);
				Mapping global = mi.at(i, j);
				if(global == null){
					global = new Mapping(i, j);
					mi.set(global);
				}
				if(targetLeftDescendants.get(currentTarget).equals(mostLeftTarget) && candidateLeftDescendants.get(currentCandidate).equals(mostLeftCandidate)){ //both target and candidate terms share the same most left descendant as the candidate and term key roots
					m.addWeight(min);
					global.addWeight(min); //update the global mapping too
				}
				else{
					m.addWeight(Math.min(min, (global.getComalutiveWeight() + map.at(i - 1, j - 1).getComalutiveWeight())));
				}
				map.set(m);
			}
		}
	}

	@Override
	public void configure(Element element) {
        Element parametersElement = element.getChild("parameters");
        if (parametersElement == null)
            return;
        List<?> parametersList = parametersElement.getChildren("parameter");
        for (Iterator<?> i = parametersList.iterator(); i.hasNext();)
        {
            Element parameterElement = (Element) i.next();
            String name = parameterElement.getChild("name").getText();
            if (name.equals("stepOne")) //load step 1 algorithm dynamically
            {
            	stepOneAlgorithm = parameterElement.getChild("value").getText();
            }
            else if(name.equals("structWeight")){
            	double value = Double.parseDouble(parameterElement.getChild("value").getText());
            	this.structWeight = value;
            	this.basicWeight = 1 - value;
            }	
            else if(name.equals("insertWeight")){
            	double value = Double.parseDouble(parameterElement.getChild("value").getText());
            	this.insertWeight = value;
            	this.deleteWeight = 1 - value;
            }	
        }
	}

	@Override
	public Algorithm makeCopy() {
		// TODO Auto-generated method stub
		return null;
	}

/****************ADD ONS*************************************/	

	/**
	 * This class is used to store the changes in the structural score of a pair of terms throughout \
	 * the run of the TED algorithm.
	 * 
	 * @author Admin
	 *
	 */
	private class Mapping{
		private int targetIndex;
		private int candidateIndex;
		private double comulativeWeight;
		
		public Mapping(int a, int b){
			this.targetIndex = a;
			this.candidateIndex = b;
			this.comulativeWeight = 0;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + candidateIndex;
			result = prime * result + targetIndex;
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Mapping other = (Mapping) obj;
			if (candidateIndex != other.candidateIndex)
				return false;
			if (targetIndex != other.targetIndex)
				return false;
			return true;
		}		
		
		public void addWeight(double weight){
			this.comulativeWeight += weight;
		}
		
		public int getTargetIndex(){
			return this.targetIndex;
		}
		
		public int getCandidateIndex(){
			return this.candidateIndex;
		}
		
		public double getComalutiveWeight(){
			return this.comulativeWeight;
		}
	}
	
	/**
	 * A convenience class that holds the matrix of weights of a certain amount of pairs of terms with the 
	 * option for an offset for "clusters" of terms that come from the middle of the terms tree.
	 * 
	 * @author Admin
	 *
	 */
	private class MappingMatrix{
		private int offsetX; //target offset
		private int offsetY; //candidate offset
		Mapping[][] matrix;
		
		public MappingMatrix(int targetsAmount, int candidatesAmount, int targetsOffset, int candidatesOffset){
			matrix = new Mapping[targetsAmount][candidatesAmount];
			matrix[0][0] = new Mapping(0, 0); //dummy
			offsetX = targetsOffset;
			offsetY = candidatesOffset;
		}
		
		public Mapping at(int targetIndex, int candidateIndex){
			if(((targetIndex - offsetX) < 0) || ((candidateIndex - offsetY) < 0)){
				throw new RuntimeException();
			}
			else{
				return matrix[targetIndex - offsetX][candidateIndex - offsetY];
			}
		}
		
		public void set(Mapping m){
			if((m.getTargetIndex() - offsetX) < 0 || (m.getCandidateIndex() - offsetY) < 0){
				throw new RuntimeException();
			}
			matrix[m.getTargetIndex() - offsetX][m.getCandidateIndex() - offsetY] = m;
		}
		
		public double getNormalizedWeight(Mapping m){
			return (m.getComalutiveWeight() / ((m.getTargetIndex() - this.offsetX) + (m.getCandidateIndex() - this.offsetY)));
		}
	}
	
}
