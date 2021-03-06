package ac.technion.iem.ontobuilder.matching.meta.match;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

import ac.technion.iem.ontobuilder.core.ontology.DummyTerm;
import ac.technion.iem.ontobuilder.core.ontology.OntologyUtilities;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.utils.ArrayConversion;

/**
 * <p>
 * Title: MatchMatrix
 * </p>
 * Extends {@link AbstractMatchMatrix}
 */
public class MatchMatrix
{

    private ArrayList<Term> candidateTerms;
    private ArrayList<Term> targetTerms;

    // to support getting match confidence for given two terms in O(1)
    private Hashtable<Term, Integer> candidateHashIndex = new Hashtable<Term, Integer>();
    private Hashtable<Term, Integer> targetHashIndex = new Hashtable<Term, Integer>();
    
    //supports getting a term by ID in O(1)
    private HashMap<Long,Term> candidateTermIDs = new HashMap<Long,Term>();
    private HashMap<Long,Term> targetTermIDs = new HashMap<Long,Term>();
    
    //supports getting max confidence for a term in O(1) filled when first used (O(n^2) for first invocation of getMaxConfidence)
    private HashMap<Term,Double> candidateTermMaxConf = new HashMap<Term,Double>();
    private HashMap<Term,Double> targetTermMaxConf = new HashMap<Term,Double>();
    
    private HashMap<Term,Double> candidateTermAvgConf = new HashMap<Term,Double>();
    private HashMap<Term,Double> targetTermAvgConf = new HashMap<Term,Double>();

	protected double[][] confidenceMatrix;
    
    private static int printIndex = 0;

    /**
     * Constructs a default MatchMatrix
     */
    public MatchMatrix()
    {
        super();
    }

    /**
     * Constructs a MatchMatrix
     * 
     * @param candidateTermsCount number of terms in the candidate ontology
     * @param targetTermsCount number of terms in the target ontology
     * @param candTerms list of Terms of candidate ontology
     * @param targetTerms list of {@link Term} of target ontology
     */
    public MatchMatrix(int candidateTermsCount, int targetTermsCount, ArrayList<Term> candTerms,
        ArrayList<Term> targetTerms)
    {
        this.candidateTerms = candTerms;
        this.targetTerms = targetTerms;
        confidenceMatrix = new double[targetTermsCount][candidateTermsCount];
        prepareHashIndexes(candidateTerms, targetTerms);
    }

    /**
     * Get the number of rows in the match matrix
     * 
     * @return int - number of terms in the target ontology
     */
    public int getRowCount()
    {
        return targetTerms.size();
    }

    /**
     * Get the number of columns in the match matrix
     * 
     * @return int - number of terms in the candidate ontology
     */
    public int getColCount()
    {
        return candidateTerms.size();
    }

    /**
     * Calculate the transpose of the match matrix
     * 
     * @return double[][] - a transposed confidence matrix
     */
    public double[][] transpose()
    {
        double[][] transpose = new double[candidateTerms.size()][targetTerms.size()];
        for (int i = 0; i < candidateTerms.size(); i++)
            for (int j = 0; j < targetTerms.size(); j++)
                transpose[i][j] = confidenceMatrix[j][i];
        return transpose;
    }

    /**
     * Calculate the transpose of a given matrix
     * 
     * @param matrix a confidence matrix
     * @param row number of rows in the given matrix
     * @param col number of columns in the given matrix
     * @return double[][] a transposed confidence matrix
     */
    public double[][] transpose(double[][] matrix, int row, int col)
    {
        double[][] transpose = new double[col][row];
        for (int i = 0; i < col; i++)
            for (int j = 0; j < row; j++)
                transpose[i][j] = matrix[j][i];
        return transpose;
    }

    /**
     * Set the confidence values of MatchMatrix confidenceMatrix
     * 
     * @param matchCMatrix the confidence matrix to set
     */
    public void setMatchConfidenceMatrix(double[][] matchCMatrix)
    {
        double[][] transposedMM = transpose(matchCMatrix, candidateTerms.size(), targetTerms.size());
        for (int i = 0; i < targetTerms.size(); i++)
            for (int j = 0; j < candidateTerms.size(); j++)
            {
                confidenceMatrix[i][j] = transposedMM[i][j];
            }
    }

    /**
     * Set the match confidence for a pair of terms
     * 
     * @param candidate a {@link Term} for the candidate ontology
     * @param target a {@link Term} for the target ontology
     * @param confidence confidence value
     */
    public void setMatchConfidence(Term candidate, Term target, double confidence)
    {
        int candIndex = getTermIndex(candidateTerms, candidate, true);
        int targetIndex = getTermIndex(targetTerms, target, false);
        if (candIndex != -1 && targetIndex != -1)
        {
            confidenceMatrix[targetIndex][candIndex] = confidence;
        }
    }

    /**
     * Get the match confidence for a pair of terms
     * 
     * @param candidate a {@link Term} for the candidate ontology
     * @param target a {@link Term} for the target ontology
     * @return confidence value if terms are found -1 otherwise
     */
    public double getMatchConfidence(Term candidate, Term target)
    {
        int candIndex = getTermIndex(candidateTerms, candidate, true);
        int targetIndex = getTermIndex(targetTerms, target, false);
        if (candIndex == -1)
            System.out.println("ERROR candidate:" + candidate);
        if (targetIndex == -1)
            System.out.println("ERROR target:" + target);
        if (candIndex == -1 || targetIndex == -1) return -1;
        else return confidenceMatrix[targetIndex][candIndex];
    }

    public double getMatchConfidenceByID(long candidate, long target)
    {
//    	Term t1 = getTermByID(candidate, true);
//    	Term t2 = getTermByID(target, false);
    	
        int candIndex = getTermIndex(candidateTerms, getTermByID(candidate, true), true);
        int targetIndex = getTermIndex(targetTerms, getTermByID(target, false), false);
        if (candIndex == -1)
            System.out.println("ERROR candidate:" + candidate);
        if (targetIndex == -1)
            System.out.println("ERROR target:" + target);
//        double tmp = confidenceMatrix[targetIndex][candIndex];
        if (candIndex == -1 || targetIndex == -1) return -1;
        else return confidenceMatrix[targetIndex][candIndex];
    }

    // old version
    // public int getTermIndex(ArrayList termsList,Term term,boolean b){
    // for (int i=0;i<termsList.size();i++){
    // if (((Term)termsList.get(i)).toString().equals(term.toString())) return i;
    // }
    // return -1;
    // }

    /**
     * Returns the hash index of a specific Term, return -1 if terms doesn't exist
     * 
     * @param termsList a {@link Term} list
     * @param term the {@link Term} you wish to retrieve its hash index
     * @param candidate set to <code>true</code> if this term is in the candidate Terms list and
     * <code>false</code> if it's from the target list
     * @return int - hash index
     */
    public int getTermIndex(ArrayList<Term> termsList, Term term, boolean candidate)
    {
        Hashtable<Term, Integer> searchInHash;
        if (candidate)
            searchInHash = candidateHashIndex;
        else
            searchInHash = targetHashIndex;
        assert(term !=null);
        Integer i = (Integer) searchInHash.get(term);
        if (i == null)
            return -1;
        else
            return i.intValue();
    }

    /**
     * Creates a sub matrix by terms supplied
     * 
     * @param a list of terms from the candidate ontology
     * @param a list of terms from the ontology
     * @return {@link MatchMatrix} - which holds both lists and their relevant confidence values
     */
    public MatchMatrix createSubMatrix(ArrayList<Term> cTerms, ArrayList<Term> tTerms)
    {
        MatchMatrix subMatrix = new MatchMatrix(cTerms.size(), tTerms.size(), cTerms, tTerms);
        for (Iterator<Term> i = cTerms.iterator(); i.hasNext();)
        {
            Term candidateTerm = i.next();
            for (Iterator<Term> j = tTerms.iterator(); j.hasNext();)
            {
                Term targetTerm = (Term) j.next();
                subMatrix.setMatchConfidence(candidateTerm, targetTerm,
                    getMatchConfidence(candidateTerm, targetTerm));
            }
        }
        return subMatrix;
    }

    /**
     * Get the term from the terms list by its name
     * 
     * @param a list of Terms
     * @param name of the desired Term
     * @return {@link MatchMatrix} - which holds both lists and their relevant confidence values
     */
    public Term getTermByName(String name, ArrayList<Term> termsList)
    {
        for (int i = 0; i < termsList.size(); i++)
            if (OntologyUtilities.oneIdRemoval(termsList.get(i).toString()).equals(
                OntologyUtilities.oneIdRemoval(name)) ||
                OntologyUtilities.oneIdRemoval(termsList.get(i).toStringVs2()).equals(
                    OntologyUtilities.oneIdRemoval(name)))
                return termsList.get(i);
        // System.out.println(name+">>> was not found!\n\n");
        return null;
    }

    /**
     * Search and retrieves a term from the list of candidate/target ontologies (will be searched in
     * both) in case of failure, will return NULL
     * 
     * @param name of the desired Term
     * @return {@link Term} - which holds both lists and their relevant confidence values
     */
    public Term getTermByName(String name)
    {
        Term term = getTermByName(name, getTargetTerms());
        if (term != null)
            return term;
        else
            return getTermByName(name, getCandidateTerms());
    }
    
    /**
     * Gets a Term in O(1) 
     * @param id Term ID of the term to retrieve
     * @param isCandidate supply true if the requested term is in the candidate terms
     * @return Term with matching ID, null otherwise
     */
    public Term getTermByID(long id,boolean isCandidate)
    {
    	HashMap<Long,Term> terms = (isCandidate?candidateTermIDs:targetTermIDs);
    	if (terms.containsKey(id))
    		return terms.get(new Long(id));
    	else
    		return null;
    }
    
    public String getTermNameByID(long id,boolean isCandidate)
    {
    	HashMap<Long,Term> terms = (isCandidate?candidateTermIDs:targetTermIDs);
    	if (terms.containsKey(id))
    		return terms.get(id).getName();
    	else
    		return null;
    }

    
    /**
     * Gets all Term identifier for all target or candidate terms
     * @param isCandidate supply true if the requested ID relates to a candidate term
     * @return all IDs of Terms
     */
    public long[] getTermIDs(boolean isCandidate) {
    	if (isCandidate)
    		return ArrayConversion.toPrimitiveLong(this.candidateTermIDs.keySet());
    	else
    		return ArrayConversion.toPrimitiveLong(this.targetTermIDs.keySet());
    }
    
    /**
     * Gets all Term identifier for all target terms
     * @return all IDs of target Terms
     */
    public long[] getTargetTermIDs() {
    		return this.getTermIDs(false);
    }
    
    /**
     * Gets all Term identifier for all candidate terms
     * @return all IDs of candidate Terms
     */
    public long[] getCandidateTermIDs() {
    		return this.getTermIDs(true);
    }


    /**
     * @deprecated
     */
    public void setCandidatetAttributeNames(String[] names)
    {
        // candidateAttributeNames = names;
    }

    /**
     * @deprecated
     */
    public void setTargetAttributeNames(String[] names)
    {
        // targetAttributeNames = names;
    }

    /**
     * Checks if this term is a candidate term
     * 
     * @param term the {@link Term} to check
     * @return <code>true</code> if this is a candidate term
     */
    public boolean isCandTerm(Term term)
    {
        return (getTermIndex(candidateTerms, term, true) != -1);
    }

    /**
     * Checks if this term is a target term
     * 
     * @param term the {@link Term} to check
     * @return <code>true</code> if this is a target term
     */
    public boolean isTargetTerm(Term term)
    {
        return (getTermIndex(targetTerms, term, false) != -1);
    }

    /**
     * Get the confidence matrix
     * 
     * @return the confidence matrix
     */
    public double[][] getMatchMatrix()
    {
        return confidenceMatrix;
    }

    /**
     * Assumes the matrix provided fits to the ontologies stored at the MatchMatrix object
     * 
     * @param matrix the matrix to set
     */
    public void setMatchMatrix(double[][] matrix)
    {
        this.confidenceMatrix = matrix;
    }

    /**
     * Get the candidate terms
     * 
     * @return the candidate {@link Term} list
     */
    public ArrayList<Term> getCandidateTerms()
    {
        return candidateTerms;
    }

    /**
     * Get the target terms
     * 
     * @return the target {@link Term} list
     */
    public ArrayList<Term> getTargetTerms()
    {
        return targetTerms;
    }

    /**
     * Fills 2 hashtables for the candTerms and targetTermstargetTerms to support getting match
     * confidence for given two terms in O(1)
     * 
     * @param candTerms - list of {@link Term} of candidate ontology
     * @param targetTerms - list of {@link Term} of target ontology
     */
    private void prepareHashIndexes(ArrayList<Term> candTerms, ArrayList<Term> targetTerms)
    {
    	int i=0;
        for (Term term : candTerms)
        {
            candidateHashIndex.put(term, new Integer(i++));
            candidateTermIDs.put(term.getId(), term);
        }
        
        i = 0;
        for (Term term : targetTerms)
        {
            targetHashIndex.put(term, new Integer(i++));
            targetTermIDs.put(term.getId(),term);
        }

    }

    /**
     * Copy a match matrix (any object from a class which implements AbstractMatchMatrix) to an
     * MatchMatrix object. Note, a new empty confidence matrix is created
     * 
     * @param matrix - (any object from a class which implements {@link AbstractMatchMatrix}) holds
     * terms of candidate and
     * @param targetTerms - list of Terms of target ontology
     */
    public void copyWithEmptyMatrix(MatchMatrix matrix)
    {
        confidenceMatrix = new double[matrix.getRowCount()][matrix.getColCount()];
        candidateTerms = ((MatchMatrix) matrix).getCandidateTerms();
        targetTerms = ((MatchMatrix) matrix).getTargetTerms();
        prepareHashIndexes(candidateTerms, targetTerms);
    }

    /**
     * Get all of the target terms names
     * 
     * @return String[] - array with the names of target ontology
     */
    public String[] getTargetTermNames()
    {
        String[] names = new String[targetTerms.size()];
        for (int i = 0; i < targetTerms.size(); i++)
            names[i] = ((Term) targetTerms.get(i)).toString();
        return names;
    }

    /**
     * Get all of the candidate terms names
     * 
     * @return String[] - array with the names of candidate ontology
     */
    public String[] getCandidateTermNames()
    {
        String[] names = new String[candidateTerms.size()];
        for (int i = 0; i < candidateTerms.size(); i++)
            names[i] = ((Term) candidateTerms.get(i)).toString();
        return names;
    }

    /**
     * Get the target attribute names
     * 
     * @return the names
     */
    public String[] getTargetAttributeNames()
    {
        String[] targetAttributeNames = new String[targetTerms.size()];
        int size = targetTerms.size();
        for (int i = 0; i < size; i++)
            targetAttributeNames[i] = ((Term) targetTerms.get(i)).toString();
        return targetAttributeNames;
    }

    /**
     * Get the candidate attribute names
     * 
     * @return the names
     */
    public String[] getCandidateAttributeNames()
    {
        String[] candidateAttributeNames = new String[candidateTerms.size()];
        int size = candidateTerms.size();
        for (int i = 0; i < size; i++)
            candidateAttributeNames[i] = ((Term) candidateTerms.get(i)).toString();
        return candidateAttributeNames;
    }

    /**
     * @param candAttrName - attribute Terms name from candidate ontology
     * @param targetAttrName - attribute Terms name from target ontology
     * @return double - Confidence value of the given terms if method fails to find one of the terms
     * an error will be printed and value returned is 0
     * @deprecated use this.getMatchConfidenceByID
     */
    public double getMatchConfidenceByAttributeNames(String candAttrName, String targetAttrName)
    {
        if (!containsCandidateAttribute(candAttrName) || !containsTargetAttribute(targetAttrName))
        {
            System.err.println("no such Terms: [" + candAttrName + "," + targetAttrName + "]");
            return 0;// throw new
                     // IllegalArgumentException("no such Terms: ["+candAttrName+","+targetAttrName+"]");
        }
        int row = 0;
        int col = 0;
        String[] candNames = getCandidateTermNames();
        String[] targetNames = getTargetTermNames();
        for (int i = 0; i < candNames.length; i++)
        {
            if (candNames[i].equals(candAttrName))
            {
                col = i;
                break;
            }
        }
        for (int i = 0; i < targetNames.length; i++)
        {
            if (targetNames[i].equals(targetAttrName))
            {
                row = i;
                break;
            }
        }
        return getMatchConfidenceAt(row, col);
    }

    /**
     * Checks if the candidate attribute exists
     * 
     * @param attributeName the name of the candidate attribute to search
     * @return <code>true</code> if the attribute exists
     */
    public boolean containsCandidateAttribute(String attributeName)
    {
        // if (candidateTerms == null)
        // candidateAttributeNames = getCandidateAttributeNames();
        // for (int i=0;i<candidateAttributeNames.length;i++)
        // if (candidateAttributeNames[i].equals(attributeName))
        // return true;
        String att = OntologyUtilities.oneIdRemoval(attributeName);
        Iterator<Term> it = candidateTerms.iterator();
        while (it.hasNext())
        {
            Term term = (Term) it.next();
            if (OntologyUtilities.oneIdRemoval(term.toString()).equals(att) ||
                OntologyUtilities.oneIdRemoval(term.toStringVs2()).equals(att))
                return true;
        }

        return false;
    }

    /**
     * Checks if the target attribute exists
     * 
     * @param attributeName the name of the target attribute to search
     * @return <code>true</code> if the attribute exists
     */
    public boolean containsTargetAttribute(String attributeName)
    {
        String att = OntologyUtilities.oneIdRemoval(attributeName);
        Iterator<Term> it = targetTerms.iterator();
        while (it.hasNext())
        {
            Term term = (Term) it.next();
            if (OntologyUtilities.oneIdRemoval(term.toString()).equals(att) ||
                OntologyUtilities.oneIdRemoval(term.toStringVs2()).equals(att))
                return true;
        }
        return false;
    }

    /**
     * Prints confidence values of all matches to :"matchMatrix"+(printIndex++)+".txt (printindex=
     * number of times this matchMatrix object was printed)
     * 
     * @throw IOException if fails to open file
     */
    public void printMatchMatrix() throws IOException
    {
        FileWriter out = new FileWriter("matchMatrix" + (printIndex++) + ".txt");
        out.write("matrix dim: (" + targetTerms.size() + "," + candidateTerms.size() + ")\n\n");
        for (int i = 0; i < candidateTerms.size(); i++)
        {
            Term candidate = (Term) candidateTerms.get(i);
            for (int j = 0; j < targetTerms.size(); j++)
            {
                Term target = (Term) targetTerms.get(j);
                out.write(candidate.toString() + " <-> " + target.toString() + " " +
                    confidenceMatrix[j][i] + "\n");
            }
            out.write("-----------------------------------------------------------\n");
            out.flush();
        }
        out.flush();
        out.close();
    }

    /**
     * Adds a new candidate attribute
     * 
     * @param attribute the name of the attribute to add
     */
    public void addNewAttributeToCandidateSchema(String attribute)
    {
        candidateTerms.add(candidateTerms.size(), new DummyTerm(attribute));
    }

    /**
     * Adds a dummy term with the attribute name to the candidate terms
     * 
     * @param attribute the name of the attribute to add
     */
    public void addNewAttributeToTargetSchema(String attribute)
    {
        targetTerms.add(targetTerms.size(), new DummyTerm(attribute));
    }

    /**
     * Remove the attribute from the candidate schema
     * 
     * @param attribute the name of the attribute to remove
     */
    public void removeAttributeFromCandidateSchema(String attribute)
    {
        Iterator<Term> it = candidateTerms.iterator();
        while (it.hasNext())
        {
            Term term = (Term) it.next();
            if (term.toString().equals(attribute))
            {
                it.remove();
                break;
            }
        }
    }

    /**
     * Remove the attribute from the target schema
     * 
     * @param attribute the name of the attribute to remove
     */
    public void removeAttributeFromTargetSchema(String attribute)
    {
        Iterator<Term> it = targetTerms.iterator();
        while (it.hasNext())
        {
            Term term = (Term) it.next();
            if (term.toString().equals(attribute))
            {
                it.remove();
                break;
            }
        }
    }

    /**
     * Rearrange the list of the candidate terms according to a new permutation
     * 
     * @param newPermutation the new permutation of candidate attributes termIDs
     */
    public void rearrangeAttributesPermutation(long[] newPermutation,boolean isCandidate)
    {
        ArrayList<Term> newAttrPermutation = new ArrayList<Term>(newPermutation.length);
        for (int i = 0; i < newPermutation.length; i++)
            newAttrPermutation.add(i, getTermByID(newPermutation[i], isCandidate));
        ArrayList<Term> termList = (isCandidate?candidateTerms:targetTerms);
        termList.clear();
        termList = null;
        termList = newAttrPermutation;
    }
    
    /**
     * Gets max confidence for a term in O(1)
     * Except when first used (O(n^2) for first invocation of getMaxConfidence in a matrix)
     * @param t Term to get max confidence for
     * @param isCandidate
     * @return
     */
    public double getMaxConfidence(Term t, boolean isCandidate)
    {
    	if (t==null) return 0.0;
    	if (candidateTermMaxConf.isEmpty() || targetTermMaxConf.isEmpty())
    		this.fillMaxConfidence();
    	Double res = (isCandidate?candidateTermMaxConf.get(t):targetTermMaxConf.get(t));
    	if (res==null) res=0.0;
		return res ;
    	
    }

    /**
     * Gets avg confidence for a term in O(1)
     * Except when first used (O(n^2) for first invocation of getAvgConfidence in a matrix)
     * @param t Term to get avg confidence for
     * @param isCandidate
     * @return
     */
    public double getAvgConfidence(Term t, boolean isCandidate)
    {
    	if (t==null) return 0.0;
    	if (candidateTermAvgConf.isEmpty() || targetTermAvgConf.isEmpty())
    		this.fillAvgConfidence();
    	Double res = (isCandidate?candidateTermAvgConf.get(t):targetTermAvgConf.get(t));
    	if (res==null) res=0.0;
		return res ;
    	
    }
    
    /**
     * Fills the avg confidence for all terms
     */
    private void fillAvgConfidence() {
    	
    	for (Term candidate : this.candidateTerms)
		{
			Double sumConf = 0.0;
			int cntNZ = 0;
			int col = getTermIndex(candidateTerms, candidate, true);
			for (int i = 0; i<confidenceMatrix.length;i++)
			{
				sumConf += confidenceMatrix[i][col];
				cntNZ += (confidenceMatrix[i][col]>0.0 ? 1 : 0);
			}
			double avgConf = (cntNZ==0?0:sumConf/((double)cntNZ));
			candidateTermAvgConf.put(candidate, avgConf);
		}
		
		for (Term target : this.targetTerms)
		{
			Double sumConf = 0.0;
			int cntNZ = 0;
			
			int row = getTermIndex(targetTerms, target, false);
			for (int j = 0; j<this.getColCount();j++)
			{
				sumConf += confidenceMatrix[row][j];
				cntNZ += (confidenceMatrix[row][j]>0.0 ? 1 : 0);
			}
			double avgConf = (cntNZ==0?0:sumConf/((double)cntNZ));
			targetTermAvgConf.put(target, avgConf);
		}
	

		
		
	}

	/**
     * Fills the max confidence hash for all terms
     */
	private void fillMaxConfidence() 
	{
		for (Term candidate : this.candidateTerms)
		{
			Double maxConf = 0.0;
			int col = getTermIndex(candidateTerms, candidate, true);
			for (int i = 0; i<confidenceMatrix.length;i++)
				maxConf = Math.max(maxConf,confidenceMatrix[i][col]);
			candidateTermMaxConf.put(candidate, maxConf);
		}
		
		for (Term target : this.targetTerms)
		{
			Double maxConf = 0.0;
			int row = getTermIndex(targetTerms, target, false);
			for (int j = 0; j<this.getColCount();j++)
				maxConf = Math.max(maxConf,confidenceMatrix[row][j]);
			targetTermMaxConf.put(target, maxConf);
		}
	
		
	}

	/**
	 * Sets a confidence value in the Confidence Matrix to '0' if the original value isn't higher
	 * than the threshold
	 * 
	 * @param th - the threshold
	 */
	public void applyThreshold(double th) {
	    for (int i = 0; i < getRowCount(); i++)
	        for (int j = 0; j < getColCount(); j++)
	        {
	            confidenceMatrix[i][j] = (confidenceMatrix[i][j] >= th ? confidenceMatrix[i][j] : 0);
	        }
	}

	/**
	 * Set match confidence for a specific pair
	 * 
	 * @param i - row position
	 * @param j - column position
	 * @param confidence value
	 */
	public void setMatchConfidenceAt(int i, int j, double confidence) {
	    if (confidenceMatrix == null)
	        throw new NullPointerException("match matrix hasn't been constructed yet");
	    if (0 > i || i >= getRowCount() || 0 > j || j >= getColCount())
	        throw new IllegalArgumentException("i=" + i + ",j=" + j + ",row=" + getRowCount() +
	            ",col=" + getColCount());
	    else
	        confidenceMatrix[i][j] = confidence;
	}

	/**
	 * Get the match confidence for a specific pair
	 * 
	 * @param i - row position
	 * @param j - column position
	 * @return the confidence value
	 */
	public double getMatchConfidenceAt(int i, int j) {
	    if (0 > i || i >= getRowCount() || 0 > j || j >= getColCount())
	        throw new IllegalArgumentException("i=" + i + ",j=" + j + ",row=" + getRowCount() +
	            ",col=" + getColCount());
	    if (confidenceMatrix == null)
	        throw new NullPointerException("match matrix hasn't been constructed yet");
	    return confidenceMatrix[i][j];
	}

	/**
	 * Normalise the Confidence Matrix value by dividing each pair's value in the maximum value in
	 * the matrix
	 */
	public void normalize() {
	    double maxValue = 0;
	    for (int i = 0; i < getRowCount(); i++)
	        for (int j = 0; j < getColCount(); j++)
	            maxValue = maxValue < confidenceMatrix[i][j] ? confidenceMatrix[i][j] : maxValue;
	    if (maxValue != 0)
	    {
	        for (int i = 0; i < getRowCount(); i++)
	            for (int j = 0; j < getColCount(); j++)
	                confidenceMatrix[i][j] /= maxValue;
	    }
	
	}
}
