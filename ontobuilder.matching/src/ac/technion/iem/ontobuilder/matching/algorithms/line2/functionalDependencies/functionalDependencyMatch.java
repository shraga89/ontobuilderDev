package ac.technion.iem.ontobuilder.matching.algorithms.line2.functionalDependencies;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import ac.technion.iem.ontobuilder.core.ontology.Attribute;
import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.Relationship;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.common.SecondLineAlgorithm;
import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.meta.match.MatchMatrix;


public class functionalDependencyMatch implements SecondLineAlgorithm{

	private double t_pk = 0.77;
	private double t_l = 0.9;
	private double t_r = 0.8;
	private double threshold = 0;
	
	// array list which saves the terms' IDs which are the part of primary key
	private ArrayList <ArrayList<Long>> pkArrList_target;
	private ArrayList <ArrayList<Long>> pkArrList_candidate;
	
	// functional dependencies' maps
	private HashMap <ArrayList<Long>,ArrayList<Long>> FD_Map_target;
	private HashMap <ArrayList<Long>,ArrayList<Long>> FD_Map_candidate;
	
	
	/**
	 * function returns the key's set which having common value
	 * @param map
	 * @param value
	 * @return
	 */
	public static <T, E> Set<T> getKeysByValue(HashMap<T, E> map, E value) {
	    Set<T> keys = new HashSet<T>();
	    for (Entry<T, E> entry : map.entrySet()) {
	        if (value.equals(entry.getValue())) {
	            keys.add(entry.getKey());
	        }
	    }
	    return keys;
	}

	
	/**
	 * functions builds the list of pk for given ontology
	 * @param arL_Terms - list of terms in the ontology
	 */
	public ArrayList<ArrayList<Long>> pkMapping (ArrayList<Term> arL_Terms){
		ArrayList<ArrayList<Long>> pkList = new ArrayList<ArrayList<Long>>();
		//temp HashMap - maps all composed_pk's members
		HashMap<Long,String> primaryKeyMap = new HashMap<Long,String>();
		//list of term's attributes
		ArrayList<Attribute> attrList;  
		//for each term
		for (int i = 0; i< arL_Terms.size(); i++){
			attrList = arL_Terms.get(i).getAttributes();
			//for each term's attributes
			for (int j = 0; j< attrList.size(); j++){
				//if attribute is primaryKeyComposed, then map attribute name (which is contains tableName and constraint name) to the term ID.
				//all members of the composed PK will be mapped to the same place
				if (attrList.get(j).getDomain().getName().contains("primaryKeyComposed")){
					primaryKeyMap.put(arL_Terms.get(i).getId(),attrList.get(j).getName()); 
				}
				//else check if attribute is simple pk
				else if (attrList.get(j).getDomain().getName().contains("primaryKey")){
					ArrayList<Long> temp = new ArrayList<Long>();
					temp.add(arL_Terms.get(i).getId());
					if (!pkList.contains(temp)){
						pkList.add(temp);	
					}
				}
			}//for each attribute
		}//for each term
		
		//translate primaryKeyMap to pkList
		Set<Long> ID_set;
		String pkName;
		ArrayList <String> savedPK = new ArrayList <String>();
		//get list of all pkNames
		Collection<String> valuesColection = primaryKeyMap.values();
		//while there is pkName to check
		int sizeValues = valuesColection.size();
		int i = 0;
		while (valuesColection.iterator().hasNext() && i<sizeValues){			
			pkName = valuesColection.iterator().next();
			//if pk's id's wasn't added 
			if (!savedPK.contains(pkName)){
				//add pkName to the list
				savedPK.add(pkName);
				//get list of pk's IDs
				ID_set = getKeysByValue(primaryKeyMap,pkName);
				//add ids' set to the list
				ArrayList<Long> idsInPK = new ArrayList<Long>(ID_set);
				pkList.add(idsInPK);	
			}
			i++;
		}
		return pkList;
	}

	
	/**
	 * functions builds the list of functional dependencies from given ontology based on Foreign Key.
	 * Foreign key references to the primary key (or part of it) of the referenced table.
	 * The function check if foreign key references to the whole primary key
	 *  and in that case functional dependency will be build between fk and all fields of the referenced,
	 *  otherwise fd will be build between fk and referenced terms.
	 *  
	 * @param arL_Terms - list of terms in the ontology
	 */
	public HashMap<ArrayList<Long>,ArrayList<Long>> fd_FK_Mapping (ArrayList<Term> arL_Terms){
		//fdFkMap - saves map of functional dependencies created from fk-referencedPK
		HashMap<ArrayList<Long>,ArrayList<Long>> fdFkMap = new HashMap<ArrayList<Long>,ArrayList<Long>>();
		//list of foreign keys' names
		ArrayList<Relationship> relationships = new ArrayList<Relationship>();
		//mapFK_Term maps sourceTerm,referncedTerm to fk's name
		HashMap<ArrayList<Term>,String> mapFK = new HashMap<ArrayList<Term>,String>(); 
		
	
		//for each term
		for (int i = 0; i< arL_Terms.size(); i++){
			relationships = arL_Terms.get(i).getRelationships();
			//map relationship's index_sourceID_targetID to foreign key
			for (int j = 0; j< relationships.size(); j++){
				if (relationships.get(j).getName().contains("_referencesTo")){
					//map source term's id,referenced term's id to the fk Name ( name based on referenced table and referenced fields names)
					//DELETE:  mapFK.put(j+"_"+relationships.get(j).getSource().getId()+"_"+relationships.get(j).getTarget().getId(),relationships.get(j).getName());
					ArrayList<Term> tempArr = new ArrayList<Term>();
					tempArr.add(relationships.get(j).getSource());
					tempArr.add(relationships.get(j).getTarget());
					mapFK.put(tempArr,relationships.get(j).getName());
				}
			}
		}
		//get list of all fkNames which were mapped
		Collection<String> valuesColection = mapFK.values();
		//while there is pkName to check
		//TODO: test if the function returns unique list of values: if size map could be smaller than collection's size
		String fkName;
		Set<ArrayList<Term>> ID_set;
		ArrayList<Term> arrL_IDs = new ArrayList<Term>();
		Iterator<String> iterVal = valuesColection.iterator();
		while (iterVal.hasNext()){			
			fkName = (String) iterVal.next();
			ID_set = getKeysByValue(mapFK, fkName);
			ArrayList<Long> fkTermsIds = new ArrayList<Long>();
			ArrayList<Long> refTermsIds = new ArrayList<Long>();
			Iterator<ArrayList<Term>> iterIdSet = ID_set.iterator();
			//if Foreign Key hasn't been mapped
			while (iterIdSet.hasNext()){
				arrL_IDs = iterIdSet.next();
				fkTermsIds.add(arrL_IDs.get(0).getId());
				refTermsIds.add(arrL_IDs.get(1).getId());
			}
			//check if fk references to the PK
			Term refTerm = arrL_IDs.get(1);
			if (fkTermsIds.size()>1){
				//check that referenced term is part of composed primary key
				if (refTerm.getParent().getDomain().getName().contains("ontology.domain.primaryKeyComposed")){
					//get all terms in composed PK
					ArrayList<Term> refPK_Terms = refTerm.getParent().getAllChildren();
					//check that each term in composed pk also in list of referenced terms
					boolean termsInTheList = true;
					if (fkTermsIds.size()>=refPK_Terms.size()){
						for(int jj = 0; jj<refPK_Terms.size(); jj++){
							if(!refTermsIds.contains(refPK_Terms.get(jj).getId())){
								termsInTheList = false;
							}
						}
					}
					else{
						termsInTheList = false;
					}
					//if referenced terms is PPK, then set FD between fk and the referenced table, otherwise set fd between fk and referenced terms only
					if (termsInTheList){
						//get all terms in the table
						ArrayList<Term> refTableTerms = refTerm.getParent().getParent().getAllChildren();
						Long tID;
						for(int jj = 0; jj<refTableTerms.size(); jj++){
							tID = refTableTerms.get(jj).getId();
							if (!refTermsIds.contains(tID)){
								refTermsIds.add(tID);
							}
						}
					}
					fdFkMap.put(fkTermsIds,refTermsIds);
				}
				else{
					fdFkMap.put(fkTermsIds,refTermsIds);
				}
			}
			else{ // fk has 1 term
				//check that referenced term is part of simple primary key
				if (!refTerm.getParent().getDomain().getName().contains("ontology.domain.primaryKeyComposed")){
					//get term's attribute
					ArrayList<Attribute> attrList = refTerm.getAttributes();
					boolean found = false;
					int jj = 0;
					while (!found && jj < attrList.size()){
						if(attrList.get(jj).getDomain().getName().contains("ontology.domain.primaryKey")){
							found = true;
						}
						jj++;
					}
					if (found){
						//get all table's terms and set FD between fk and referenced table's terms
						ArrayList<Term> refTableTerms = refTerm.getParent().getAllChildren();
						Long tID;
						for(jj = 0; jj<refTableTerms.size(); jj++){
							tID = refTableTerms.get(jj).getId();
							if (!refTermsIds.contains(tID)){
								refTermsIds.add(tID);
							}
						}
						fdFkMap.put(fkTermsIds,refTermsIds);
					}
				}
				else{ // single term is referenced to the composed PK
					fdFkMap.put(fkTermsIds,refTermsIds);
				}
			}
		}
				return fdFkMap;
	} // end of fd_FK_MAPPING


	/**
	 * functions builds the list of functional dependencies from given ontology based on UNIQUE CONSTRAINT
	 * @param arL_Terms - list of terms in the ontology
	 */
	public HashMap<ArrayList<Long>,ArrayList<Long>> fd_UNIQUE_Mapping (ArrayList<Term> arL_Terms){
		//fdUniqueMap - saves map of functional dependencies created Unique dependencies
		HashMap<ArrayList<Long>,ArrayList<Long>> fdUniqueMap = new HashMap<ArrayList<Long>,ArrayList<Long>>();
		ArrayList<String> mapedUniqueConstraints = new ArrayList<String>();
		//for each term
		for (int i = 0; i< arL_Terms.size(); i++){
			//list of term's attributes
			ArrayList<Attribute> attrList;
			attrList = arL_Terms.get(i).getAttributes();
			//for each term's attribute
			for (int j = 0; j< attrList.size(); j++){
				//if term is a part of unique or terms are part of uniqueComposed
				if (attrList.get(j).getDomain().getName().contains("unique")){
					if (!mapedUniqueConstraints.contains(attrList.get(j).getName())){
						mapedUniqueConstraints.add(attrList.get(j).getName());
						//find the members of unique constraint
						ArrayList <Long> uniqueTermsIDs = new ArrayList <Long>();
						ArrayList <Long> tablesTermsIDs = new ArrayList <Long>();
						uniqueTermsIDs.add(arL_Terms.get(i).getId());
						ArrayList<Relationship> relations= new ArrayList<Relationship>();
						//get all relationships of the term
						relations = arL_Terms.get(i).getRelationships();
						for (int ii = 0; i<relations.size();ii++){
							//check relation contains "partOfComposedUNIQUE"
							if ( relations.get(ii).getName().contains("partOfComposedUNIQUE")){
								uniqueTermsIDs.add(relations.get(ii).getTarget().getId());	
							}
						}
						//get all table's terms
						ArrayList <Term> tableTerms = new ArrayList <Term>();
						//if the unique term is a part of pkComposed, then get term's parent.parent
						if(arL_Terms.get(i).getParent().getDomain().getName().contains("ontology.domain.primaryKeyComposed")){
							tableTerms = arL_Terms.get(i).getParent().getParent().getAllChildren();
							}
						else{
							tableTerms = arL_Terms.get(i).getParent().getAllChildren();
						}
						for (int ii=0; ii<tableTerms.size(); ii++){
							if(!uniqueTermsIDs.contains(tableTerms.get(ii).getId())){
								tablesTermsIDs.add(tableTerms.get(ii).getId());
							}
						}
						fdUniqueMap.put(uniqueTermsIDs, tablesTermsIDs);		
					}
				}//if contains unique attribute
			}//for term's attribute
		}//for each term				
		return fdUniqueMap;
	} // end of fd_UNIQUE_MAPPING

	
	/**
	 * functions builds the list of functional dependencies from given ontology based on FOREIGN KEY and UNIQUE CONSTRAINTs
	 * @param arL_Terms - list of terms in the ontology
	 */
	public HashMap<ArrayList<Long>,ArrayList<Long>> createFunctionalDependencies (ArrayList<Term> arL_Terms){
		//create functional dependencies based on foreign key constraint		
		HashMap<ArrayList<Long>, ArrayList<Long>> FD = fd_FK_Mapping(arL_Terms);
		//create functional dependencies based on unique constraint
		HashMap<ArrayList<Long>, ArrayList<Long>> unique_FD = fd_UNIQUE_Mapping(arL_Terms);
		//add unique_FD to 
		for(Map.Entry<ArrayList<Long>,ArrayList<Long>> entry : unique_FD.entrySet())
		  {
			ArrayList<Long> key=entry.getKey();
			ArrayList<Long> val=entry.getValue();
			FD.put(key,val);

		  }
		return FD;
	}

	
	/**
	 * @param cOnt 
	 * @param tOnt 
	 * @param L_target - list of candidate terms' ID's
	 * @param L_candidate - list of candidate terms' ID's
	 * @param th - threshold
	 * @return position of target and candidate terms with maximal similarity (which is grater than th)
	 * If maximal similarity is lower than threshold,then -1 will be returned instead of position
	 */
	public ArrayList<Integer> findMaxSimilarityPosition(Ontology tOnt, Ontology cOnt, ArrayList<Long> L_target,ArrayList<Long> L_candidate, MatchMatrix copyMatchM, double th){
		Term tTerm;
		Term cTerm;
		// position saves position of target and candidate terms to be removed
		ArrayList<Integer> position = new ArrayList<Integer>();
		//set position of target term to remove to be 0
		position.add(-1);
		//set position of candidate term to remove to be 0
		position.add(-1);
		
		double  maxSimilarity = 0;
		//for each term find similarity to each candidate term
		for (int i = 0; i<L_target.size(); i++){
			for (int j = 0; j<L_candidate.size(); j++){
				//if the similarity is grater than maxSimilarity, then update maxSimilarity and save position of terms to remove
				tTerm = tOnt.getTermByID(L_target.get(i));
				cTerm = cOnt.getTermByID(L_candidate.get(j));
				double sim = copyMatchM.getMatchConfidence(cTerm, tTerm);
				//double sim = copyMatchM.getMatchConfidenceByID(L_target.get(i), L_candidate.get(j));
				if ( (sim > maxSimilarity) && (sim >= th) ){
					maxSimilarity = copyMatchM.getMatchConfidence(cTerm, tTerm);
					position.set(0, i);
					position.set(1, j);
				}
			}
		}
		return position;
	}
	
	/**
	 * 
	 * @param L_target - list of target terms
	 * @param L_candidate - list of candidate terms
	 * @param matchInf - matchInformation
	 * @param th - threshold
	 * @return???
	 */
	public boolean IsMatch(Ontology tOnt, Ontology cOnt,ArrayList<Long> L_target, ArrayList<Long> L_candidate, MatchMatrix matchM){
		
		if(L_target.size() == L_candidate.size()){
			//make a copy of elements
			ArrayList<Long> L_targetCopy = new ArrayList<Long>(L_target);
			ArrayList<Long> L_candidateCopy = new ArrayList<Long> (L_candidate); 
			MatchMatrix copyMatchM = new MatchMatrix();
			copyMatchM = matchM;
			//find term with maximal similarity
				while(L_targetCopy!= null && !L_targetCopy.isEmpty()){
				ArrayList<Integer> removeAtPosition = new ArrayList<Integer>();
				//for each term find similarity to each candidate term
				removeAtPosition = findMaxSimilarityPosition(tOnt,cOnt,L_targetCopy, L_candidateCopy, copyMatchM, t_l);
				//if position of terms with maximal similarity (which is >= t_l) is not -1,then remove terms form the list
				if (removeAtPosition.get(0)!=-1){
					L_targetCopy.remove(L_targetCopy.get(removeAtPosition.get(0)));
					L_candidateCopy.remove(L_candidateCopy.get(removeAtPosition.get(1)));
				}
				else{// confidence<t_l
					return false;
				}
			}// all termsId's were removed from the list 
			return true;
		}
		else { // the size is not equal
			return false;
		}
	}
	
	
	/**
	 * 
	 * @param tID - target term's ID
	 * @param cID - candidate term's ID
	 * @param fDTarget - list of functional dependencies of target ontology
	 * @param fDCandidate - list of functional dependencies of candidate ontology
	 * @return: true if the left side of fDTarget contains only tID and the left side of fDCandidate contains only cID,
	 *  also the function returns the FD's indx in target and candidate FD
	 */
	private int[] isInLeftSideFD(Long tID, Long cID, HashMap<ArrayList<Long>, ArrayList<Long>> fDTarget, HashMap<ArrayList<Long>, ArrayList<Long>> fDCandidate) {
		Set<ArrayList<Long>> lFdTarget = fDTarget.keySet();
		Set<ArrayList<Long>> lFdCandidate = fDCandidate.keySet();
		boolean targetFound = false;
		boolean candidateFound = false;
		int indxT = 0;
		int indxC = 0;
		
		//check each targetFD's left side if it's contains tID and it is the only one term
		while (lFdTarget.iterator().hasNext() && !targetFound){
			indxT++;
			ArrayList<Long> fd = lFdTarget.iterator().next();
			if (fd.contains(tID) && fd.size()==1){
					targetFound = true;
			}
		}	

		//check each candidateFD's left side if it's contains cID and it is the only one term
		while (lFdCandidate.iterator().hasNext() && !candidateFound){
			indxC++;
			ArrayList<Long> fd = lFdCandidate.iterator().next();
			if (fd.contains(cID) && fd.size()==1){
				candidateFound = true;
			}
		}	
	
		if(targetFound && candidateFound){
			int [] returnedValue = new int[]{1,indxT,indxC};
			return returnedValue;	
		}
		else {
			int [] returnedValue = new int[]{0,0,0};
			return returnedValue;	
		}
	}
	
	
	/**
	 * 
	 * @param tID - target term's ID
	 * @param cID - candidate term's ID
	 * @param fDTarget - list of functional dependencies of target ontology
	 * @param fDCandidate - list of functional dependencies of candidate ontology
	 * @return: true if the left side of fDTarget contains only 1 key and  it is tID and the left side of fDCandidate contains only cID
	 */
	
	private ArrayList<Long> findIDInRightSideFD(int tIndx, int cIndx, HashMap<ArrayList<Long>, ArrayList<Long>> fDTarget, HashMap<ArrayList<Long>, ArrayList<Long>> fDCandidate) {
		Set<ArrayList<Long>> lFdTarget = fDTarget.keySet();
		Set<ArrayList<Long>> lFdCandidate = fDCandidate.keySet();
		boolean targetFound = false;
		boolean candidateFound = false;
		
		ArrayList<Long> ids = new ArrayList<Long>();
		
		//check each targetFD's left side if it's contains tID and it is the only one term
		int indxL =0;
		while (lFdTarget.iterator().hasNext() && !targetFound){
			ArrayList<Long> fd = lFdTarget.iterator().next();
			indxL++;
			if (indxL ==tIndx && fd.size()==1){
				//if also the right side has to contain only 1 term
				Collection<ArrayList<Long>> rFdTarget = fDTarget.values();
				int indxR = 0;
				while (rFdTarget.iterator().hasNext() && !targetFound){
					indxR++;
					if (indxR == indxL){
						ArrayList<Long> rFD = rFdTarget.iterator().next();
						if (rFD.size()==1){
							targetFound = true;
							//TODO: test if it position 0 or 1
							ids.add(rFD.get(0));
						}	
					}	
				}
			}
		}	

		//check each candidateFD's left side if it's contains cID and it is the only one term
		indxL =0;
		while (lFdCandidate.iterator().hasNext() && !candidateFound){
			ArrayList<Long> fd = lFdCandidate.iterator().next();
			if (indxL==cIndx && fd.size()==1){
				//if also the right side has to contain only 1 term
				Collection<ArrayList<Long>> rFdCandidate = fDCandidate.keySet();
				int indxR = 0;
				while (rFdCandidate.iterator().hasNext() && !candidateFound){
					indxR++;
					if (indxR == indxL){
						ArrayList<Long> rFD = rFdCandidate.iterator().next();
						if (rFD.size()==1){
							candidateFound = true;
							ids.add(rFD.get(0));
						}	
					}	
				}
			}
		}	
		
		if(targetFound && candidateFound){
			return ids;
		}
		else {
			return null;	
		}
	}
/*PREVIOS VERSION	private ArrayList<Long> findIDInRightSideFD(int tIndx, int cIndx, HashMap<ArrayList<Long>, ArrayList<Long>> fDTarget, HashMap<ArrayList<Long>, ArrayList<Long>> fDCandidate) {
		Set<ArrayList<Long>> lFdTarget = fDTarget.keySet();
		Set<ArrayList<Long>> lFdCandidate = fDCandidate.keySet();
		boolean targetFound = false;
		boolean candidateFound = false;
		
		ArrayList<Long> ids = new ArrayList<Long>();
		
		//check each targetFD's left side if it's contains tID and it is the only one term
		int indxL =0;
		while (lFdTarget.iterator().hasNext() && !targetFound){
			ArrayList<Long> fd = lFdTarget.iterator().next();
			indxL++;
			if (fd.contains(tID) && fd.size()==1){
				//if also the right side has to contain only 1 term
				Collection<ArrayList<Long>> rFdTarget = fDTarget.values();
				int indxR = 0;
				while (rFdTarget.iterator().hasNext() && !targetFound){
					indxR++;
					if (indxR == indxL){
						ArrayList<Long> rFD = rFdTarget.iterator().next();
						if (rFD.size()==1){
							targetFound = true;
							//TODO: test if it position 0 or 1
							ids.add(rFD.get(0));
						}	
					}	
				}
			}
		}	

		//check each candidateFD's left side if it's contains cID and it is the only one term
		indxL =0;
		while (lFdCandidate.iterator().hasNext() && !candidateFound){
			ArrayList<Long> fd = lFdCandidate.iterator().next();
			if (fd.contains(cID) && fd.size()==1){
				//if also the right side has to contain only 1 term
				Collection<ArrayList<Long>> rFdCandidate = fDCandidate.keySet();
				int indxR = 0;
				while (rFdCandidate.iterator().hasNext() && !candidateFound){
					indxR++;
					if (indxR == indxL){
						ArrayList<Long> rFD = rFdCandidate.iterator().next();
						if (rFD.size()==1){
							candidateFound = true;
							ids.add(rFD.get(0));
						}	
					}	
				}
			}
		}	
		
		if(targetFound && candidateFound){
			return ids;
		}
		else {
			return null;	
		}
	}
	*/
	
	//---------------------------------------------------------------------------------------------
	 
	@Override
	public boolean init(Properties prop) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	public functionalDependencyMatch()
	{
		//this.threshold = t;
	}
	
	
	public MatchInformation fd_Match (MatchInformation copyMInf, Term target, Term candidate){
		//set match between 2 terms
		copyMInf.updateMatch(target, candidate, 1);
		//unmatch those terms to other terms
		int totTerms = copyMInf.getCandidateOntology().getTermsCount();
		Term termToUnmatch;
		for (int i = 0; i<totTerms; i++){
			termToUnmatch = copyMInf.getCandidateOntology().getTerm(i);
			if (termToUnmatch.getId()!=candidate.getId()){
				copyMInf.updateMatch(target, termToUnmatch, 0);
			}
		}
		totTerms = copyMInf.getTargetOntology().getTermsCount();
		for (int i = 0; i<totTerms; i++){
			termToUnmatch = copyMInf.getTargetOntology().getTerm(i);
			if (termToUnmatch.getId()!= target.getId()){
				copyMInf.updateMatch(termToUnmatch,candidate, 0);
			}
		}
		return copyMInf;
	}
	


	@Override
	public MatchInformation match(MatchInformation mi) {
		//DELETE: MatchInformation res = new MatchInformation(mi.getCandidateOntology(),mi.getTargetOntology());
		//MatchInformation copyMatchInf = new MatchInformation(mi.getCandidateOntology(),mi.getTargetOntology());
		MatchInformation copyMatchInf = mi;
		
		//get ontologies
		Ontology tOnt = copyMatchInf.getTargetOntology();
		Ontology cOnt = copyMatchInf.getCandidateOntology();
	
		//create functional dependencies and map primary keys
		FD_Map_target = createFunctionalDependencies(mi.getOriginalTargetTerms());
		FD_Map_candidate = createFunctionalDependencies(mi.getOriginalCandidateTerms());
		pkArrList_target = pkMapping(mi.getOriginalTargetTerms());
		pkArrList_candidate = pkMapping(mi.getOriginalCandidateTerms());
		
		//for each FD get left and right side parts of the dependencies
		Set<ArrayList<Long>> lsTarget = FD_Map_target.keySet();
		Set<ArrayList<Long>> lsCandidate = FD_Map_candidate.keySet();
		Collection<ArrayList<Long>> rsTarget = FD_Map_target.values();
		Collection<ArrayList<Long>> rsCandidate = FD_Map_candidate.values();
		//DELETE ArrayList<Long> matchedTermsIDs = new ArrayList<Long>();

		Iterator<ArrayList<Long>> iterLT = lsTarget.iterator();
		Iterator<ArrayList<Long>> iterRT = rsTarget.iterator();
		Iterator<ArrayList<Long>> iterLC;
		Iterator<ArrayList<Long>> iterRC;
		for (int i = 0; i<FD_Map_target.size(); i++){
			//get left and right side part of the dependency i
			ArrayList<Long> lT= iterLT.next();
			ArrayList<Long> rT= iterRT.next();
			iterLC = lsCandidate.iterator();
			iterRC = rsCandidate.iterator();
			for (int j = 0; j<FD_Map_candidate.size(); j++){
				//check if left FD's side isMatch
				ArrayList<Long> lC = iterLC.next();
				ArrayList<Long> rC = iterRC.next();
				if(IsMatch(tOnt, cOnt, lT, lC, copyMatchInf.getMatrix())){
					//Match LT and LC
					for(int ii = 0; ii< lT.size(); ii++){
						 Term tTerm = tOnt.getTermByID(lT.get(ii));
						 Term cTerm = cOnt.getTermByID(lC.get(ii));
						 //check that terms hasn't been matched 
						 //if (!matchedTermsIDs.contains(tTerm.getId()) && !matchedTermsIDs.contains(cTerm.getId())){
							// matchedTermsIDs.add(tTerm.getId());
							 //matchedTermsIDs.add(cTerm.getId());
							 copyMatchInf = fd_Match(copyMatchInf, tTerm,cTerm);	 
						 //}
						//match similar terms
						 //copyMatchInf.updateMatch(tTerm, cTerm, 1);
						 
						 
					}
					boolean flag = false;
					while(rT.size()>0 && rC.size()>0 && !flag){
						// find pair of right-side terms with maximal similarity
						 ArrayList<Integer> placeIndx = findMaxSimilarityPosition(tOnt, cOnt, rT, rC, copyMatchInf.getMatrix(), t_r);
						 if (placeIndx.get(0)!=-1){
							 Long tID = rT.get(placeIndx.get(0));
							 Long cID = rC.get(placeIndx.get(1));
							 //get terms by ID	
							 Term tTerm = tOnt.getTermByID(tID);
							 Term cTerm = cOnt.getTermByID(cID);
							//match similar terms
							 //copyMatchInf.updateMatch(tTerm, cTerm, 1);
							 copyMatchInf = fd_Match(copyMatchInf, tTerm,cTerm);
							 //remove terms from the right-side list
							 rT.remove(rT.get(placeIndx.get(0)));
							 rC.remove(rC.get(placeIndx.get(1)));
						}
						 else{
							 flag  = true;
						 }
					}
				}//if isMatch
			}//for FD - candidate
		}//for each FD-taregt

		//find pair of terms which are in the PK and those terms are the only terms at left-side of FD
		for (int i = 0; i<pkArrList_target.size();i++){
			for(int j = 0; j> pkArrList_candidate.size();j++){
				//make a copy of target and candidate PK
				ArrayList<Long> tPK_copy = pkArrList_target.get(i); 
				ArrayList<Long> cPK_copy = pkArrList_candidate.get(j) ;
				for (int ii = 0; ii<tPK_copy.size();ii++){
					for(int jj = 0; jj> cPK_copy.size();jj++){
						//TEST??
						//tPK_copy.get(ii);
						//cPK_copy.get(jj);
						//TEST_end??
						//check if tPK(ii) and cPk(jj) at the left Side of some FD and it's the only term in left-FD
						int [] leftFdData = isInLeftSideFD(tPK_copy.get(ii),cPK_copy.get(jj),FD_Map_target,FD_Map_candidate);
						if (leftFdData[0]==1){
							//get terms by ID	
							 tOnt = copyMatchInf.getTargetOntology();
							 cOnt = copyMatchInf.getCandidateOntology();
							 Term tTerm = tOnt.getTermByID(tPK_copy.get(ii));
							 Term cTerm = cOnt.getTermByID(cPK_copy.get(jj));
							//match similar terms
							// copyMatchInf.updateMatch(tTerm, cTerm, 1);
							 copyMatchInf = fd_Match(copyMatchInf, tTerm,cTerm);
							 //remove terms from the PK_target(i), PK_candidate(j)
							 tPK_copy.remove(tPK_copy.get(ii));
							 cPK_copy.remove(cPK_copy.get(jj));
							 //if the right FDs's sides contains only 1 term' then match them
							 //TODO: test that it founds left and right at the same palce
							 ArrayList<Long> rsID = findIDInRightSideFD(leftFdData[1],leftFdData[2],FD_Map_target,FD_Map_candidate);
							 if (rsID!=null){
								 //get right-side terms id's
								 tTerm = tOnt.getTermByID(rsID.get(0));
								 cTerm = cOnt.getTermByID(rsID.get(1));
								 //copyMatchInf.updateMatch(tTerm, cTerm, 1);
								 copyMatchInf = fd_Match(copyMatchInf, tTerm,cTerm);
							 }
						}
					}//for each term in pk_candidate(j)
				} //for each term in pk_target(i)
				
				//for the rest of terms in PK find pair with maximal similarity, which is grater than t_pk 
				 ArrayList<Integer> placeIndx = findMaxSimilarityPosition(tOnt, cOnt, tPK_copy, cPK_copy, copyMatchInf.getMatrix(), t_pk);
				 if (placeIndx.get(0)!=-1){
					 Long tID = tPK_copy.get(placeIndx.get(0));
					 Long cID = cPK_copy.get(placeIndx.get(1));
					 
					 //get terms by ID	
					 Term tTerm = tOnt.getTermByID(tID);
					 Term cTerm = cOnt.getTermByID(cID);
				 	 
					 //match similar terms
					 //copyMatchInf.updateMatch(tTerm, cTerm, 1);
					 copyMatchInf = fd_Match(copyMatchInf, tTerm,cTerm);
					 
					 //remove terms from the PK_copy
					 tPK_copy.remove(tPK_copy.get(placeIndx.get(0)));
					 cPK_copy.remove(cPK_copy.get(placeIndx.get(1)));
				}
			}//for each pk_candidate
		}//for each pk_target
		
		
		 MatchInformation res = new MatchInformation(copyMatchInf.getCandidateOntology(),copyMatchInf.getTargetOntology());

		 for (Match m : mi.getCopyOfMatches())
			if (m.getEffectiveness() > threshold)
				res.updateMatch(m.getTargetTerm(), m.getCandidateTerm(), m.getEffectiveness());
				
		 return res;
				
				/*return copyMatchInf;*/
	}
	
	

}
