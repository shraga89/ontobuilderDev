/**
 * @authors: Haggai Roitman , Giovanni Modica, Tomer Sagi 
 * This class was modified by haggai 6/12/03
 * changes: using MatchMatrix to hold full match matrix information
 * Information stored in this class: <br>
 * terms of both ontologies, matches, mismatches of Target/Candidate Ontology,
 * target/candidate Ontology, matchMatrix, algorithm and MetaAlgorithm.
 * for later use for 1:1 best mappings
 * Class refactored May 2012 by Tomer Sagi
 * Use the updateMatch method as a single interface for matching changes.
 * absorb SchemaTranslator into this class 
 */

package ac.technion.iem.ontobuilder.matching.match;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.jdom.DocType;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.OntologyUtilities;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.core.utils.files.StringOutputStream;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.common.Algorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.meta.MetaAlgorithm;
import ac.technion.iem.ontobuilder.matching.meta.match.MatchMatrix;

/**
 * <p>Title: MatchInformation</p>
 * TODO: move the hashcode mechanism from schema translator to here
 */
public class MatchInformation
{
	
    protected Algorithm algorithm; /*Records the algorithm that created this match information object, 
    								may become irrelevant as second line matchers are applied. Consider adding a list of 
    								second line matchers applied to be later updated */  
    
    protected Ontology candidateOntology; //Records the original ontologies matched
    protected Ontology targetOntology;
    
    protected MatchMatrix match_Matrix; /*Records the full results of the match operation and provides 
    									  methods for efficiently querying these results. */
    protected ArrayList<Match> matches; /* List of selected matches after applying some selection rule over the result */ 
    protected MetaAlgorithm metaAlgorithm; /* records which meta algorithm generated this mi object */
    protected ArrayList<Mismatch> mismatchesCandidateOntology; /* Provides efficient access to unmatched terms. */
    protected ArrayList<Mismatch> mismatchesTargetOntology;

    private HashMap<Term,ArrayList<Match>> matchedCandidateTerms = new HashMap<Term,ArrayList<Match>>(); //provides efficient lookup for term matches
    private HashMap<Term,ArrayList<Match>> matchedTargetTerms = new HashMap<Term,ArrayList<Match>>();
    
    protected ArrayList<Term> originalCandidateTerms; /*Provides efficient access to ontology terms, 
    													since in the ontology it is kept in a tree. Consider changing to a
    													Hashset to make single term lookup more efficient. Consider using the match_matrix term lists instead*/
    protected ArrayList<Term> originalTargetTerms;

    //The following three fields are used by the meta match framework
    /** local scores of the mapping */
    protected double[] localScores;
    /** global score of the mapping */
    protected double globalScore = -1;
    protected boolean newMapping = true;
    //End fields for meta match
    
    /**
     *@deprecated Impossible constructor, retained for backwards compatability
     *Cannot allocate a match matrix without knowing the candidate and target ontologies sizes. 
     */
    public MatchInformation()
    {
        matches = new ArrayList<Match>();
        mismatchesTargetOntology = new ArrayList<Mismatch>();
        mismatchesCandidateOntology = new ArrayList<Mismatch>();
        // gabi - 5/3 adding to MatchInformation the MatchMatrix
      //matchMatrix = new double[totalCandidateTerms][totalTargetTerms];
        match_Matrix = new MatchMatrix();
    }
   
    /**
     * Reasonable constructor , allocates matches, target mismatches 
     * and candidate mismatches arrays lists. also creates an empty matchMatrix
     */
    public MatchInformation(Ontology cand, Ontology targ)
    {
    	matches = new ArrayList<Match>();
        mismatchesTargetOntology = new ArrayList<Mismatch>();
        for (Term t : targ.getTerms(true))
        	mismatchesTargetOntology.add(new Mismatch(t));
        	
        mismatchesCandidateOntology = new ArrayList<Mismatch>();
        for (Term c : cand.getTerms(true))
        	mismatchesCandidateOntology.add(new Mismatch(c));
    	this.setCandidateOntology(cand);
    	this.setTargetOntology(targ);
    	match_Matrix = new MatchMatrix(originalCandidateTerms.size(),originalTargetTerms.size(),originalCandidateTerms,originalTargetTerms); 
    }


    /**
     * If effectiveness is 0, removes the match from the list, updates 
     *  the matrix and checks if the mismatch list needs updating. 
     * Otherwise (supplied effectiveness value >0)
     *  If supplied term pair is matched, updates the effectiveness. 
     *    Otherwise adds the match to the matches list, sets the confidence 
     *    in the match matrix and removes match from mismatch list(if exists there)
     * @param match a {@link Match}
     */
    private void updateMatch(Match match)
    {
    	Term c = match.candidateTerm;
    	Term t = match.targetTerm;
    	
    	match_Matrix.setMatchConfidence(c, t, match.effectiveness);
    	
    	if (matches.contains(match)) //todo fix Match class hash function to be based on term ids and replace araylists with hashsets 
    	{
    		matches.remove(match);
    		if (matchedCandidateTerms.containsKey(c)) matchedCandidateTerms.get(c).remove(match);
    		if (matchedTargetTerms.containsKey(t)) matchedTargetTerms.get(t).remove(match);
	    	if (match.effectiveness==0)
	    	{
	    		//if last match for candidate or target term, add to mismatch list
	    		if ((!matchedCandidateTerms.containsKey(c) || matchedCandidateTerms.get(c).isEmpty()) && !mismatchesCandidateOntology.contains(c))
	    			mismatchesCandidateOntology.add(new Mismatch(match.candidateTerm));
	    		if ((!matchedTargetTerms.containsKey(t) || matchedTargetTerms.get(t).isEmpty()) && !mismatchesTargetOntology.contains(t))
	    			mismatchesTargetOntology.add(new Mismatch(t));
	    	}
	    	else //match.effectiveness >0
	    	{
	    		matches.add(match); //Since the equalsTo method of match only compares terms, the new match value will be used
	    		addToMatchList(match,true);
	    		addToMatchList(match,false);
	    	}
    	}
    	else //new match
        {
    		//add to match lists
    		matches.add(match);
    		addToMatchList(match,true);
    		addToMatchList(match,false);
    		
    		//update mismatch lists
    		if (mismatchesTargetOntology.contains(t))
    			mismatchesTargetOntology.remove(t);
    		if (mismatchesTargetOntology.contains(c))
    			mismatchesTargetOntology.remove(c);
        }
    }

	/**
	 * Updates the internal match lists
	 * @param match
	 * @param isCandidate 
	 */
	private void addToMatchList(Match match, boolean isCandidate) {
		ArrayList<Match> termMatches;
		HashMap<Term,ArrayList<Match>> matchedTermMap = ( isCandidate ? matchedCandidateTerms : matchedTargetTerms);
		Term t = (isCandidate ? match.candidateTerm : match.targetTerm);
		if (matchedTermMap.containsKey(t))
			termMatches = matchedTermMap.get(t);
		else
			termMatches = new ArrayList<Match>(); 
		termMatches.add(match);
		if (!matchedTermMap.containsKey(t))
			matchedTermMap.put(t,termMatches);
	}

    /**
     * If effectiveness is 0, removes the match from the list, updates 
     *  the matrix and checks if the mismatch list needs updating. 
     * Otherwise (supplied effectiveness value >0)
     *  If supplied term pair is matched, updates the effectiveness. 
     *    Otherwise adds the match to the matches list, sets the confidence 
     *    in the match matrix and removes match from mismatch list(if exists there)
     * 
     * @param targetTerm the target {@link Term}
     * @param candidateTerm the candidate  {@link Term}
     * @param effectiveness
     */
    public void updateMatch(Term targetTerm, Term candidateTerm, double effectiveness)
    {
        updateMatch(new Match(targetTerm, candidateTerm, effectiveness));
    }


    /**
     * returns a clone of this match information object
     */
    public MatchInformation clone()
    {
    	MatchInformation cloned = new MatchInformation(candidateOntology,targetOntology);
    	cloned.updateFromMatch(this);
    	return cloned;
    	
    }

    /**
     * Removes all matches/mismatches that are instance of "group", and replaces decomposed terms
     * with their parents
     */
    public void denormalize()
    {
        for (Match match : getCopyOfMatches())
        {
            Term targetTerm = match.getTargetTerm();
            Term candidateTerm = match.getCandidateTerm();
            if (targetTerm.isInstanceOf("group") || candidateTerm.isInstanceOf("group"))
            {
                updateMatch(targetTerm, candidateTerm, 0.0);
            }
            else
            {
                if (targetTerm.isDecomposedTerm())
                    match.setTargetTerm(targetTerm.getParent());
                if (candidateTerm.isDecomposedTerm())
                    match.setCandidateTerm(candidateTerm.getParent());
            }
        }

        ArrayList<Match> matchesAux = new ArrayList<Match>();
        for (Iterator<Match> i = matches.listIterator(); i.hasNext();)
        {
            Match match = (Match) i.next();
            int index = matchesAux.indexOf(match);
            if (index != -1)
            {
                Match matchAux = (Match) matchesAux.get(index);
                if (matchAux.getEffectiveness() >= match.getEffectiveness())
                    i.remove();
                else
                    matchesAux.set(index, match);
            }
            else
                matchesAux.add(match);
        }
        matches = matchesAux;

        for (Iterator<Mismatch> i = mismatchesTargetOntology.listIterator(); i.hasNext();)
            if (((Mismatch) i.next()).getTerm().isInstanceOf("group"))
                i.remove();

        for (Iterator<Mismatch> i = mismatchesCandidateOntology.listIterator(); i.hasNext();)
            if (((Mismatch) i.next()).getTerm().isInstanceOf("group"))
                i.remove();
    }

    /**
     * @return {@link Algorithm} the algorithm for the match
     */
    public Algorithm getAlgorithm()
    {
        return algorithm;
    }

    /**
     * @return {@link Ontology} candidateOntology
     */
    public Ontology getCandidateOntology()
    {
        return candidateOntology;
    }

    /**
     * @return int - number of candidate terms
     */
    public int getCandidateOntologyTermsTotal()
    {
        return match_Matrix.getCandidateTerms().size();
    }

    // gabi


    /**
     * Returns the match effectiveness of 2 terms, return 0 can't find a match
     * 
     * @param candidate {@link Term}
     * @param target {@link Term}
     * @return double - match effectiveness 
     */
    public double getMatchConfidence(Term candidate, Term target)
    {
        return match_Matrix.getMatchConfidence(candidate, target);
    }

    /**
     * @return ArrayList with all matches currently in the MatchInformation object
     * Note that this list is a copy of the internal match list and is therefore unsychronized. 
     */
    public ArrayList<Match> getCopyOfMatches()
    {
    	ArrayList<Match> res = new ArrayList<Match>();
    	for (Match m : matches)
    		res.add(new Match(m.targetTerm, m.candidateTerm, m.effectiveness));
        return res;
    }
    
    /**
     * This method is O(1) and therefore preferred to using getCopyOfMatches().size() which is O(n) 
     * @return number of matches in the matchInformation obhect
     */
    public int getNumMatches()
    {
    	return matches.size();
    }

    /**
     * @param t a {@link Term}
     * @param isCandidate is supplied term a candidate term?
     * @return {@link Match} - the list of matches for the target term, returns null if none is found
     */
    public ArrayList<Match> getMatchesForTerm(Term t,boolean isCandidate)
    {
    	HashMap<Term,ArrayList<Match>> termMatchList = (isCandidate ? matchedCandidateTerms: matchedTargetTerms);
    	if (termMatchList.containsKey(t)) 
    		return (termMatchList.get(t).isEmpty()? null : termMatchList.get(t));
        return null;
    }
    // end gabi

    /**
     * @return double[][] - a confidence match matrix
     */
    public double[][] getMatchMatrix()
    {
        return match_Matrix.getMatchMatrix();
    }

    /**
     * @returns {@link MatchMatrix} - holds the confidence match matrix and terms of 2 ontologies
     * (candidate,target)
     */
    // added haggai 6/12/03
    public MatchMatrix getMatrix()
    {
        return match_Matrix;
    }

    /**
     * @return {@link MetaAlgorithm}
     */
    public MetaAlgorithm getMetaAlgorithm()
    {
        return metaAlgorithm;
    }

    /**
     * @return ArrayList - of candidate ontology {@link Mismatch} 
     */
    public ArrayList<Mismatch> getMismatchesCandidateOntology()
    {
        return mismatchesCandidateOntology;
    }

    /**
     * @return ArrayList of target ontology {@link Mismatch}
     */
    public ArrayList<Mismatch> getMismatchesTargetOntology()
    {
        return mismatchesTargetOntology;
    }

    /**
     * @return ArrayList - original Candidate Terms
     */
    public ArrayList<Term> getOriginalCandidateTerms()
    {
        return originalCandidateTerms;
    }

    /**
     * @return ArrayList - original Target Terms
     */
    public ArrayList<Term> getOriginalTargetTerms()
    {
        return originalTargetTerms;
    }

    /**
     * Calculates the average effectiveness of the matches
     * 
     * @return double - average effectiveness
     */
    public double getOverallMatchConfidence()
    {
        if (matches.size() == 0)
            return 0;
        double overall = 0;
        for (Iterator<Match> i = matches.iterator(); i.hasNext();)
        {
            Match match = (Match) i.next();
            overall += match.getEffectiveness();
        }
        return overall / (double) matches.size();
    }


    
    /**
     * @param exact matchInformation containing exact match
     * @return double Precision of this match w.r.t supplied exact match
     */
    public double getPrecision(MatchInformation exact)
    {
    	if (this.matches.size()==0 || exact.matches.size()==0) return 0;
    	ArrayList<Match> truePositives = intersectMatchLists(this.matches,exact.matches);
        return (double)truePositives.size() / (double) this.matches.size();
    }
    
    /**
     * @param exact matchInformation containing exact match
     * @return double recall of this match w.r.t supplied exact match
     */
    public double getRecall(MatchInformation exact)
    {
    	if (this.matches.size()==0 || exact.matches.size()==0) return 0;
    	ArrayList<Match> truePositives = intersectMatchLists(this.matches,exact.matches);
        return (double)truePositives.size() / (double) exact.getNumMatches();
    }
    
    /**
     * Intersects two match lists and returns their intersection as a match list. 
     * Note that the equalsTo comparator is used to ascertain whether two matches are the same. 
     * Match confidence is taken from the first list arbitrarily. 
     * @param matchList1
     * @param matchList2
     * @return ArrayList<Match> representing the intersection between the lists
     */
    public static ArrayList<Match> intersectMatchLists(ArrayList<Match> matchList1 , ArrayList<Match> matchList2)
    {
    	ArrayList<Match> intersection = new ArrayList<Match>(); 
    	for (Match m : matchList1)
    		if (matchList2.contains(m)) intersection.add(m);
    	return intersection;
    }


    /**
     * @return {@link Ontology} the targetOntology 
     */
    public Ontology getTargetOntology()
    {
        return targetOntology;
    }

    /**
     * @return int - number of target terms
     */
    public int getTargetOntologyTermsTotal()
    {
        return match_Matrix.getTargetTerms().size();
    }

    // gabi 24/3/2002
    /**
     * @param termName target term name
     * @return Term - target term (return null if fails to find)
     * @deprecated use match matrix methods
     */
    public Term getTargetTerm(String termName)
    {
        if (originalTargetTerms == null)
        {
            System.out.println("getTargetTe got NULL");
            System.exit(1);
        }
        for (int i = 0; i < originalTargetTerms.size(); i++)
        {
            Term term = (Term) (originalTargetTerms.get(i));
            if ((term.getName()).equals(termName))
                return term;
        }
        return null;
    }

    /**
     * @param termName target term name
     * @return int - target Index (return -1 if fails to find)
     * @deprecated use match matrix methods
     */
    // gabi17/2/03
    public int getTargetTermIndex(String termName)
    {
        if (originalCandidateTerms == null)
        {
            System.out.println("getTargetTerms got NULL");
            System.exit(1);
        }
        for (int i = 0; i < originalTargetTerms.size(); i++)
        {
            Term term = (Term) (originalTargetTerms.get(i));
            if ((term.getName()).equals(termName))
                return i;
        }
        return -1;
    }

    /**
     * @param termName target term name
     * @return Term - reference to the target term in the matches
     * @deprecated should use the list of matches
     */
    public Term getTargetTermInMatches(String termName)
    {
        for (int i = 0; i < matches.size(); i++)
        {
            Match match = (Match) (matches.get(i));
            Term term = match.getTargetTerm();
            if ((OntologyUtilities.oneIdRemoval(term.toString())).equals(termName) ||
                (OntologyUtilities.oneIdRemoval(term.toStringVs2())).equals(termName))
                return term;
        }
        return null;
    }

    /**
     * @return int - number of matches terms
     */
    public int getTotalMatches()
    {
        return matches.size();
    }

    /**
     * Creates a match Information Element from the matchInformation object
     * 
     * @return {@link Element}
     */
    public Element getXMLRepresentation()
    {
        NumberFormat nf = NumberFormat.getInstance();

        Element matchInformationElement = new Element("matchInformation");

        Element targetOntologyElement = new Element("targetOntology");
        matchInformationElement.addContent(targetOntologyElement);
        targetOntologyElement.setAttribute("name", targetOntology.getName());
        Element targetTermsElement = new Element("terms");
        targetOntologyElement.addContent(targetTermsElement);
        ArrayList<?> targetTerms = OntologyUtilities.denormalizeTerms(OntologyUtilities
            .filterTermListRemovingTermsOfClass(
                OntologyUtilities.getTermsOfClass(targetOntology, "input"), "hidden"));
        targetOntologyElement.setAttribute("totalTerms", targetTerms.size() + "");
        for (Iterator<?> i = targetTerms.iterator(); i.hasNext();)
            targetTermsElement.addContent(((Term) i.next()).getInputFullNameAsXML());

        Element candidateOntologyElement = new Element("candidateOntology");
        matchInformationElement.addContent(candidateOntologyElement);
        candidateOntologyElement.setAttribute("name", candidateOntology.getName());
        Element candidateTermsElement = new Element("terms");
        candidateOntologyElement.addContent(candidateTermsElement);
        ArrayList<?> candidateTerms = OntologyUtilities.denormalizeTerms(OntologyUtilities
            .filterTermListRemovingTermsOfClass(
                OntologyUtilities.getTermsOfClass(candidateOntology, "input"), "hidden"));
        candidateOntologyElement.setAttribute("totalTerms", candidateTerms.size() + "");
        for (Iterator<?> i = candidateTerms.iterator(); i.hasNext();)
            candidateTermsElement.addContent(((Term) i.next()).getInputFullNameAsXML());

        Element algorithmElement = new Element("algorithm").setText(algorithm.getName());
        matchInformationElement.addContent(algorithmElement);
        algorithmElement.setAttribute("threshold", nf.format(algorithm.getThreshold()));

        Element statisticsElement = new Element("statistics").addContent(new Element("recall")
            .setText(nf.format((candidateTerms.size() - mismatchesCandidateOntology.size()) /
                (double) candidateTerms.size() * 100)));
        matchInformationElement.addContent(statisticsElement);

        Element matchesElement = new Element("matches");
        matchInformationElement.addContent(matchesElement);
        matchesElement.setAttribute("total", matches.size() + "");
        for (Iterator<Match> i = matches.iterator(); i.hasNext();)
        {
            Match match = (Match) i.next();
            Element matchElement = new Element("match");
            matchesElement.addContent(matchElement);

            Element targetElement = new Element("target");
            matchElement.addContent(targetElement);
            targetElement.addContent(match.getTargetTerm().getInputFullNameAsXML());

            Element candidateElement = new Element("candidate");
            matchElement.addContent(candidateElement);
            candidateElement.addContent(match.getCandidateTerm().getInputFullNameAsXML());

            if (match.getEffectiveness() >= 0)
                matchElement.setAttribute("confidence", nf.format(match.getEffectiveness()));
        }

        Element mismatchesElement = new Element("mismatches");
        matchInformationElement.addContent(mismatchesElement);

        Element targetMismatchesElement = new Element("targetMismatches");
        mismatchesElement.addContent(targetMismatchesElement);
        for (Iterator<Mismatch> i = mismatchesTargetOntology.iterator(); i.hasNext();)
            targetMismatchesElement.addContent(((Mismatch) i.next()).getTerm()
                .getInputFullNameAsXML());

        Element candidateMismatchesElement = new Element("candidateMismatches");
        mismatchesElement.addContent(candidateMismatchesElement);
        for (Iterator<Mismatch> i = mismatchesCandidateOntology.iterator(); i.hasNext();)
            candidateMismatchesElement.addContent(((Mismatch) i.next()).getTerm()
                .getInputFullNameAsXML());

        return matchInformationElement;
    }

    /**
     * @return String - XML representation in "matchInformation" docType
     * @throws IOException
     */
    public String getXMLRepresentationAsString() throws IOException
    {
        org.jdom.Element ontologyElement = getXMLRepresentation();
        DocType ontologyDocType = new DocType("matchInformation");
        org.jdom.Document ontologyDocument = new org.jdom.Document(ontologyElement, ontologyDocType);

        StringOutputStream xmlRepresentation = new StringOutputStream();
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(xmlRepresentation));
        XMLOutputter fmt = new XMLOutputter();// new XMLOutputter("    ",true);
        fmt.output(ontologyDocument, out);
        out.close();
        return xmlRepresentation.toString();
    }

    
    /**
     * @param target term name
     * @param candidate term name
     * @param score Effectiveness of match
     * @return true if exist in the match list
     */
    public boolean isExistSameMatch(Term target, Term candidate, double score)
    {
    	return matches.contains(new Match(target,candidate,score));
    }

    /**
     * Checks if term has a match
     * 
     * @param t {@link Term} from either ontologies
     * @return boolean - <code>true</code> if have at list one match
     * @throws NullPointerException - in case term is empty
     */
    public boolean isMatched(Term t)
    {
    	ArrayList<Match> matchList = null;
    	if (matchedCandidateTerms.containsKey(t))
    		matchList = matchedCandidateTerms.get(t);
    	
    	if (matchedTargetTerms.containsKey(t))
    		matchList = matchedTargetTerms.get(t);
    	
    	if (matchList == null) return false;
    	return !matchList.isEmpty();
    }
     

    /**
     * Removes a match from the matches list, adds it as a mismatch list(if exist their) if any of
     * terms in the match don't have any other matchs, it will be added to the mismatched list
     * 
     * @param index index of the match to remove
     */
    public void removeMatch(int index)
    {
        Match match = (Match) matches.get(index);
        match.effectiveness = 0;
        updateMatch(match);
    }

    /**
     * Prints according to the .dtds files (matchInformation.dtd)
     * 
     * @param file - a {@link File} to write to
     * @throws IOException
     */
    public void saveToXML(File file) throws IOException
    {
        org.jdom.Element ontologyElement = getXMLRepresentation();
        DocType ontologyDocType = new DocType("matchInformation", "matchInformation.dtd");
        org.jdom.Document ontologyDocument = new org.jdom.Document(ontologyElement, ontologyDocType);

        BufferedWriter out = new BufferedWriter(new FileWriter(file));
        XMLOutputter fmt = new XMLOutputter();// new XMLOutputter("    ",true);
        fmt.output(ontologyDocument, out);
        out.close();
    }

    /**
     * @param algorithm - an {@link Algorithm} for the match
     */
    public void setAlgorithm(Algorithm algorithm)
    {
        this.algorithm = algorithm;
    }

    /**
     * @param candidateOntology the candidate {@link Ontology}
     */
    private void setCandidateOntology(Ontology candidateOntology)
    {
        this.candidateOntology = candidateOntology;
        originalCandidateTerms = new ArrayList<Term>(candidateOntology.getTerms(true));
        for (Term t : originalCandidateTerms)
        	mismatchesCandidateOntology.add(new Mismatch(t));
    }


    /**
     * Updates internal match list and match matrix with provided list of matches
     * @param matches a list of {@link Match}
     * 
     */
    public void setMatches(ArrayList<Match> matches)
    {
        for (Match m : matches)
			updateMatch(m);
    }

    /**
     * @param matrix holds the {@link MatchMatrix} confidence match matrix and terms of 2 ontologies (candidate,target)
     */
    public void setMatrix(MatchMatrix matrix)
    {
        match_Matrix = matrix;
        for (Term c : match_Matrix.getCandidateTerms())
        	for (Term t : match_Matrix.getTargetTerms())
        		this.updateMatch(t, c, matrix.getMatchConfidence(c, t));
    }

    /**
     * @param metaAlgorithm - a {@link MetaAlgorithm} object to use for the match
     */
    public void setMetaAlgorithm(MetaAlgorithm metaAlgorithm)
    {
        this.metaAlgorithm = metaAlgorithm;
    }



    /**
     * @param targetOntology the target {@link Ontology}
     */
    private void setTargetOntology(Ontology targetOntology)
    {
        this.targetOntology = targetOntology;
        originalTargetTerms = new ArrayList<Term>(targetOntology.getTerms(true));
        for (Term t : originalTargetTerms)
        	mismatchesTargetOntology.add(new Mismatch(t));
    }


    
    public String toString()
    {
    	String res =  candidateOntology.getName() + "<->" + targetOntology + " NumMatches:" + this.matches.size();
    	return res;
    }
    
    /**
     * Updates match information to be as the supplied one
     * 
     * @param newMatch - a {@link MatchInformation} object containing matched,
     * mismatchesTargetOntology/mismatchescandidateOntology, and a matchMatrix.
     */
    public void updateFromMatch(MatchInformation newMatch)
    {
        matches.clear();
        mismatchesTargetOntology.clear();
        mismatchesCandidateOntology.clear();
        matchedCandidateTerms.clear();
        matchedTargetTerms.clear();
        match_Matrix.copyWithEmptyMatrix(newMatch.getMatrix());
        for (Match m : newMatch.matches)
        {
        	updateMatch(m);
        }

    }
    
    /**
     * Creates a new MatchInformation object from 2 existing one. uses a new provided threshold to
     * decided on matches. (note if both matchInformations contain the same match, the method will
     * average between their values and decides if it's still a match (effectiveness>threshold).
     * 
     * @param matchInfo1 the first {@link MatchInformation}
     * @param matchInfo2 the second {@link MatchInformation}
     * @param threshold a value from which to decide if the effectiveness is high enough for a
     * match
     * @return {@link MatchInformation}
     * TODO: this needs to be taken out of here and made into 2LM / schema matching utility
     */
    public static MatchInformation combineMatches(MatchInformation matchInfo1,
        MatchInformation matchInfo2, double threshold)
    {
    	if (matchInfo1 == null || matchInfo2 == null) return null;
        MatchInformation combinedMI = new MatchInformation(matchInfo1.candidateOntology,matchInfo2.targetOntology);

        ArrayList<Match> matches1 = matchInfo1.getCopyOfMatches();
        ArrayList<Match> matches2 = matchInfo2.getCopyOfMatches();

        // Average common matches
        for (Iterator<?> i = matches1.iterator(); i.hasNext();)
        {
            Match m1 = (Match) i.next();
            for (Iterator<?> j = matches2.iterator(); j.hasNext();)
            {
                Match m2 = (Match) j.next();
                if (m1.equals(m2))
                {
                    double avg = (m1.getEffectiveness() + m2.getEffectiveness()) / (double) 2;
                    if (avg >= threshold)
                    {
                    	ArrayList<Match> prevMatches = combinedMI.getMatchesForTerm(m1.getCandidateTerm(),true);
                        Match prevMatch = (prevMatches == null?null:prevMatches.get(0));
                        if (prevMatch != null && avg > prevMatch.getEffectiveness())
                        {
                            combinedMI.updateMatch(m1.getTargetTerm(), m1.getCandidateTerm(), avg);
                        }
                        else if (prevMatch == null)
                            combinedMI.updateMatch(m1.getTargetTerm(), m1.getCandidateTerm(), avg);
                    }
                }
            }
        }

        // Average matches only on m1
        for (Iterator<?> i = matches1.iterator(); i.hasNext();)
        {
            Match m = (Match) i.next();
            if (!combinedMI.matches.contains(m))
            {
                double avg = m.getEffectiveness() / (double) 2;
                if (avg >= threshold)
                {
                	ArrayList<Match> prevMatches = combinedMI.getMatchesForTerm(m.getCandidateTerm(),true);
                    Match prevMatch = (prevMatches == null?null:prevMatches.get(0));
                    if (prevMatch != null && avg > prevMatch.getEffectiveness())
                    {
                        combinedMI.matches.remove(prevMatch);
                        combinedMI.updateMatch(m.getTargetTerm(), m.getCandidateTerm(), avg);
                    }
                    else if (prevMatch == null)
                        combinedMI.updateMatch(m.getTargetTerm(), m.getCandidateTerm(), avg);
                }
            }
        }

        // Average matches only on m2
        for (Iterator<?> i = matches2.iterator(); i.hasNext();)
        {
            Match m = (Match) i.next();
            if (!combinedMI.matches.contains(m))
            {
                double avg = m.getEffectiveness() / (double) 2;
                if (avg >= threshold)
                {
                	ArrayList<Match> prevMatches = combinedMI.getMatchesForTerm(m.getCandidateTerm(),true);
                    Match prevMatch = (prevMatches == null?null:prevMatches.get(0));
                    if (prevMatch != null && avg > prevMatch.getEffectiveness())
                    {
                        combinedMI.matches.remove(prevMatch);
                        combinedMI.updateMatch(m.getTargetTerm(), m.getCandidateTerm(), avg);
                    }
                    else if (prevMatch == null)
                        combinedMI.updateMatch(m.getTargetTerm(), m.getCandidateTerm(), avg);
                }
            }
        }
/*
        // Add mismatches
        ArrayList<Mismatch> mismatchesTarget1 = matchInfo1.getMismatchesTargetOntology();
        ArrayList<Mismatch> mismatchesCandidate1 = matchInfo1.getMismatchesCandidateOntology();
        for (Iterator<Mismatch> i = mismatchesTarget1.iterator(); i.hasNext();)
        {
            Mismatch m = (Mismatch) i.next();
            if (!combinedMI.isMatched(m.getTerm()))
                combinedMI.addMismatchTargetOntology(new Mismatch(m.getTerm()));
        }
        for (Iterator<Mismatch> i = mismatchesCandidate1.iterator(); i.hasNext();)
        {
            Mismatch m = (Mismatch) i.next();
            if (!combinedMI.isMatched(m.getTerm()))
                combinedMI.addMismatchCandidateOntology(new Mismatch(m.getTerm()));
        }

        // Add mismatches
        ArrayList<Mismatch> mismatchesTarget2 = matchInfo2.getMismatchesTargetOntology();
        ArrayList<Mismatch> mismatchesCandidate2 = matchInfo2.getMismatchesCandidateOntology();
        for (Iterator<Mismatch> i = mismatchesTarget2.iterator(); i.hasNext();)
        {
            Mismatch m = (Mismatch) i.next();
            if (!combinedMI.isMatched(m.getTerm()))
                combinedMI.addMismatchTargetOntology(new Mismatch(m.getTerm()));
        }
        for (Iterator<Mismatch> i = mismatchesCandidate2.iterator(); i.hasNext();)
        {
            Mismatch m = (Mismatch) i.next();
            if (!combinedMI.isMatched(m.getTerm()))
                combinedMI.addMismatchCandidateOntology(new Mismatch(m.getTerm()));
        }
This section deprecated since updateMatch now updates mismatch lists as well*/
        combinedMI.setTargetOntology(matchInfo1.getTargetOntology());
        combinedMI.setCandidateOntology(matchInfo1.getCandidateOntology());
        combinedMI.setAlgorithm(matchInfo1.getAlgorithm());

        return combinedMI;
    }
    
    /**
     * Check whether this mapping is new.
     * Used by the meta match framework.
     * @return <code>true</code> if the mapping is new
     */
    public boolean isNewMapping()
    {
        return newMapping;
    }
    
    /**
     * Sets a flag whether the mapping is new.
     * Used by the meta match framework.
     * @param newMapping <code>true</code> if the mapping is new
     */
    public void setNewMapping(boolean newMapping)
    {
        this.newMapping = newMapping;
    }

	/**
	 * @return the localScores
	 */
	public double[] getLocalScores() {
		return localScores;
	}

	/**
	 * @param localScores the localScores to set
	 */
	public void setLocalScores(double[] localScores) {
		this.localScores = localScores;
	}

	/**
	 * @return the globalScore
	 */
	public double getGlobalScore() {
		return globalScore;
	}

	/**
	 * @param globalScore the globalScore to set
	 */
	public void setGlobalScore(double globalScore) {
		this.globalScore = globalScore;
	}
    
    
}
