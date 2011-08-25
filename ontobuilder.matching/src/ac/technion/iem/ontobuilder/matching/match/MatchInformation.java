/**
 * @authors: Haggai Roitman , Giovanni Modica 
 * This class was modified by haggai 6/12/03
 * changes: using MatchMatrix to hold full match matrix information
 * Information stored in this class: <br>
 * terms of both ontologies, matches, mismatches of Target/Candidate Ontology,
 * target/candidate Ontology, matchMatrix, algorithm and MetaAlgorithm.
 * for later use for 1:1 best mappings
 */

package ac.technion.iem.ontobuilder.matching.match;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;

import org.jdom.DocType;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.OntologyUtilities;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.core.utils.files.StringOutputStream;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.Algorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.meta.MetaAlgorithm;
import ac.technion.iem.ontobuilder.matching.meta.match.MatchMatrix;
import ac.technion.iem.ontobuilder.matching.utils.SchemaMatchingsUtilities;
import ac.technion.iem.ontobuilder.matching.utils.SchemaTranslator;

/**
 * <p>Title: MatchInformation</p>
 */
public class MatchInformation
{
    protected ArrayList<Match> matches;
    protected ArrayList<Mismatch> mismatchesTargetOntology;
    protected ArrayList<Mismatch> mismatchesCandidateOntology;
    protected Ontology targetOntology;
    protected int totalTargetTerms;
    protected Ontology candidateOntology;
    protected int totalCandidateTerms;
    protected Algorithm algorithm;
    protected MetaAlgorithm metaAlgorithm;
    protected double[][] matchMatrix;
    protected ArrayList<?> originalTargetTerms;
    protected ArrayList<?> originalCandidateTerms;

    protected MatchMatrix match_Matrix;

    /**
     * Default constructor allocates matches, target mismatches and candidate mismatches arrays
     * lists. also creates an empty matchMatrix
     */
    public MatchInformation()
    {
        matches = new ArrayList<Match>();
        mismatchesTargetOntology = new ArrayList<Mismatch>();
        mismatchesCandidateOntology = new ArrayList<Mismatch>();
        // gabi - 5/3 adding to MatchInformation the MatchMatrix
        matchMatrix = new double[totalCandidateTerms][totalTargetTerms];
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
     * @param matrix holds the {@link MatchMatrix} confidence match matrix and terms of 2 ontologies (candidate,target)
     */
    public void setMatrix(MatchMatrix matrix)
    {
        match_Matrix = matrix;
    }

    // end haggai

    /**
     * Updates match information to be as the supplied one
     * 
     * @param newMatch - a {@link MatchInformation} object containing matched,
     * mismatchesTargetOntology/mismatchescandidateOntology, and a matchMatrix.
     */
    public void updateFromMatch(MatchInformation newMatch)
    {
        matches.clear();
        matches.addAll(newMatch.getMatches());
        mismatchesTargetOntology.clear();
        mismatchesTargetOntology.addAll(newMatch.getMismatchesTargetOntology());
        mismatchesCandidateOntology.clear();
        mismatchesCandidateOntology.addAll(newMatch.getMismatchesCandidateOntology());
        for (int i = 0; i < matchMatrix.length; i++)
        {
            for (int j = 0; j < matchMatrix[i].length; j++)
            {
                matchMatrix[i][j] = newMatch.getMatchMatrix()[i][j];
            }
        }

    }

    // end gabi

    /**
     * Assumes the matrix provided fits to the ontologies stored at the MatchMatrix object
     * 
     * @param matrix - a confidence match matrix
     */
    public void setMatchMatrix(double matrix[][])
    {
        matchMatrix = matrix;
    }

    /**
     * @return double[][] - a confidence match matrix
     */
    public double[][] getMatchMatrix()
    {
        return matchMatrix;
    }

    /**
     * @param targetTerms - a list of terms of the target ontology
     */
    public void setOriginalTargetTerms(ArrayList<?> targetTerms)
    {
        originalTargetTerms = new ArrayList<Object>(targetTerms);
    }

    /**
     * @param CandidateTerms - a list of terms of the candidate ontology
     */
    public void setOriginalCandidateTerms(ArrayList<?> CandidateTerms)
    {
        originalCandidateTerms = new ArrayList<Object>(CandidateTerms);
    }

    /**
     * @param total - number of target terms
     */
    public void setTargetOntologyTermsTotal(int total)
    {
        totalTargetTerms = total;
    }

    /**
     * @return int - number of target terms
     */
    public int getTargetOntologyTermsTotal()
    {
        return totalTargetTerms;
    }

    /**
     * @param total - number of candidate terms
     */
    public void setCandidateOntologyTermsTotal(int total)
    {
        totalCandidateTerms = total;
    }

    /**
     * @return int - number of candidate terms
     */
    public int getCandidateOntologyTermsTotal()
    {
        return totalCandidateTerms;
    }

    /**
     * @return int - number of matches terms
     */
    public int getTotalMatches()
    {
        return matches.size();
    }

    /**
     * @param targetOntology the target {@link Ontology}
     */
    public void setTargetOntology(Ontology targetOntology)
    {
        this.targetOntology = targetOntology;
    }

    /**
     * @return {@link Ontology} the targetOntology 
     */
    public Ontology getTargetOntology()
    {
        return targetOntology;
    }

    /**
     * @param candidateOntology the candidate {@link Ontology}
     */
    public void setCandidateOntology(Ontology candidateOntology)
    {
        this.candidateOntology = candidateOntology;
    }

    /**
     * @return {@link Ontology} candidateOntology
     */
    public Ontology getCandidateOntology()
    {
        return candidateOntology;
    }

    /**
     * @param algorithm - an {@link Algorithm} for the match
     */
    public void setAlgorithm(Algorithm algorithm)
    {
        this.algorithm = algorithm;
    }

    /**
     * @return {@link Algorithm} the algorithm for the match
     */
    public Algorithm getAlgorithm()
    {
        return algorithm;
    }

    /**
     * @return ArrayList with all matches
     */
    public ArrayList<Match> getMatches()
    {
        return matches;
    }

    /**
     * Adds a match to the matches list, removes match from mismatch list(if exist their)
     * 
     * @param targetTerm the target {@link Term}
     * @param candidateTerm the candidate  {@link Term}
     * @param effectiveness
     */
    public void addMatch(Term targetTerm, Term candidateTerm, double effectiveness)
    {
        addMatch(new Match(targetTerm, candidateTerm, effectiveness));
    }

    /**
     * Adds a match to the matches list, removes match from mismatch list(if exist their)
     * 
     * @param match a  {@link Match}
     */
    public void addMatch(Match match)
    {
        matches.add(match);
        for (Iterator<Mismatch> i = mismatchesTargetOntology.iterator(); i.hasNext();)
        {
            Mismatch mismatch = (Mismatch) i.next();
            if (match.getTargetTerm().equals(mismatch.getTerm()))
                i.remove();
        }
        for (Iterator<Mismatch> i = mismatchesCandidateOntology.iterator(); i.hasNext();)
        {
            Mismatch mismatch = (Mismatch) i.next();
            if (match.getCandidateTerm().equals(mismatch.getTerm()))
                i.remove();
        }
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
        matches.remove(index);
        Term targetTerm = match.getTargetTerm();
        Term candidateTerm = match.getCandidateTerm();
        boolean addTargetTermToMismatches = true, addCandidateTermToMismatches = true;
        for (Iterator<Match> i = matches.iterator(); i.hasNext();)
        {
            Match auxMatch = (Match) i.next();
            if (auxMatch.getTargetTerm().equals(targetTerm))
                addTargetTermToMismatches = false;
            if (auxMatch.getCandidateTerm().equals(candidateTerm))
                addCandidateTermToMismatches = false;
        }
        if (addTargetTermToMismatches)
            mismatchesTargetOntology.add(new Mismatch(targetTerm));
        if (addCandidateTermToMismatches)
            mismatchesCandidateOntology.add(new Mismatch(candidateTerm));
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
        if (t == null)
        {
            throw new NullPointerException("Term is null");
        }
        for (Iterator<Match> i = matches.iterator(); i.hasNext();)
        {
            Match match = (Match) i.next();
            if (match.getTargetTerm() == null || match.getCandidateTerm() == null)
            {
                throw new NullPointerException("Match has null terms!");
            }
            if (match.getTargetTerm().equals(t) || match.getCandidateTerm().equals(t))
                return true;
        }
        return false;
    }

    /**
     * @return ArrayList of target ontology {@link Mismatch}
     */
    public ArrayList<Mismatch> getMismatchesTargetOntology()
    {
        return mismatchesTargetOntology;
    }

    /**
     * @return {@link MetaAlgorithm}
     */
    public MetaAlgorithm getMetaAlgorithm()
    {
        return metaAlgorithm;
    }

    /**
     * @param metaAlgorithm - a {@link MetaAlgorithm} object to use for the match
     */
    public void setMetaAlgorithm(MetaAlgorithm metaAlgorithm)
    {
        this.metaAlgorithm = metaAlgorithm;
    }

    /**
     * Assumes legal term (exist in the target ontology) is entered
     * 
     * @param {@link Mismatch} 
     */
    public void addMismatchTargetOntology(Mismatch mismatch)
    {
        if (!mismatchesTargetOntology.contains(mismatch))
            mismatchesTargetOntology.add(mismatch);
    }

    /**
     * @return ArrayList - of candidate ontology {@link Mismatch} 
     */
    public ArrayList<Mismatch> getMismatchesCandidateOntology()
    {
        return mismatchesCandidateOntology;
    }

    /**
     * Assumes legal term (exist in the candidate ontology) is entered
     * 
     * @param {@link Mismatch} 
     */
    public void addMismatchCandidateOntology(Mismatch mismatch)
    {
        if (!mismatchesCandidateOntology.contains(mismatch))
            mismatchesCandidateOntology.add(mismatch);
    }

    /**
     * Returns the match effectiveness of 2 terms, return 0 can't find a match
     * 
     * @param t1 target {@link Term}
     * @param t2 candidate {@link Term}
     * @return double - match effectiveness
     */
    public double getMatchConfidence(Term t1, Term t2)
    {
        for (Iterator<Match> i = matches.iterator(); i.hasNext();)
        {
            Match match = (Match) i.next();
            Term targetTerm = match.getTargetTerm();
            Term candidateTerm = match.getCandidateTerm();
            if ((OntologyUtilities.isEqualTerms(targetTerm, t1) && OntologyUtilities.isEqualTerms(
                candidateTerm, t2)) ||
                (OntologyUtilities.isEqualTerms(targetTerm, t2) && OntologyUtilities.isEqualTerms(
                    candidateTerm, t1)))
                return match.getEffectiveness();
            // if((OntologyUtilities.oneIdRemoval(targetTerm.toString()).equals(OntologyUtilities.oneIdRemoval(t1.toString())))
            // ||
            // (OntologyUtilities.oneIdRemoval(targetTerm.toStringVs2()).equals(OntologyUtilities.oneIdRemoval(t1.toStringVs2()))
            // ||
            // (OntologyUtilities.oneIdRemoval(targetTerm.toString()).equals(OntologyUtilities.oneIdRemoval(t1.toString())))
            // ||
            // (OntologyUtilities.oneIdRemoval(targetTerm.toStringVs2()).equals(OntologyUtilities.oneIdRemoval(t2.toString()))
            // ||
            // (OntologyUtilities.oneIdRemoval(targetTerm.toStringVs2()).equals(OntologyUtilities.oneIdRemoval(t2.toString())))
            // || candidateTerm.equals(t1))
            // return match.getEffectiveness();
        }
        return 0;
    }

    /**
     * Retrieves a match, returns null if none is found
     * 
     * @param t candidate {@link Term}
     * @return {@link Match}
     */
    public Match getMatchForCandidate(Term t)
    {
        for (Iterator<Match> i = matches.iterator(); i.hasNext();)
        {
            Match match = (Match) i.next();
            Term candidateTerm = match.getCandidateTerm();
            if (candidateTerm.equals(t))
            {
                System.out.println("found a match for " + t.getName());
                return match;
            }
        }
        System.out.println("no match was found for " + t.getName());
        return null;
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
     * Creates a new MatchInformation object from 2 existing one. uses a new provided trashhold to
     * decided on matches. (note if both matchInformations contain the same match, the method will
     * average between their values and decides if it's still a match (effectiveness>trashhold).
     * 
     * @param matchInfo1 the first {@link MatchInformation}
     * @param matchInfo2 the second {@link MatchInformation}
     * @param threshold a value from which to decide if the effectiveness is high enought for a
     * match
     * @return {@link MatchInformation}
     */
    public static MatchInformation combineMatches(MatchInformation matchInfo1,
        MatchInformation matchInfo2, double threshold)
    {
        MatchInformation match = new MatchInformation();

        ArrayList<?> matches1 = matchInfo1.getMatches();
        ArrayList<?> matches2 = matchInfo2.getMatches();

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
                        Match prevMatch = match.getMatchForCandidate(m1.getCandidateTerm());
                        if (prevMatch != null && avg > prevMatch.getEffectiveness())
                        {
                            match.matches.remove(prevMatch);
                            match.addMatch(m1.getTargetTerm(), m1.getCandidateTerm(), avg);
                        }
                        else if (prevMatch == null)
                            match.addMatch(m1.getTargetTerm(), m1.getCandidateTerm(), avg);
                    }
                }
            }
        }

        // Average matches only on m1
        for (Iterator<?> i = matches1.iterator(); i.hasNext();)
        {
            Match m = (Match) i.next();
            if (!match.matches.contains(m))
            {
                double avg = m.getEffectiveness() / (double) 2;
                if (avg >= threshold)
                {
                    Match prevMatch = match.getMatchForCandidate(m.getCandidateTerm());
                    if (prevMatch != null && avg > prevMatch.getEffectiveness())
                    {
                        match.matches.remove(prevMatch);
                        match.addMatch(m.getTargetTerm(), m.getCandidateTerm(), avg);
                    }
                    else if (prevMatch == null)
                        match.addMatch(m.getTargetTerm(), m.getCandidateTerm(), avg);
                }
            }
        }

        // Average matches only on m2
        for (Iterator<?> i = matches2.iterator(); i.hasNext();)
        {
            Match m = (Match) i.next();
            if (!match.matches.contains(m))
            {
                double avg = m.getEffectiveness() / (double) 2;
                if (avg >= threshold)
                {
                    Match prevMatch = match.getMatchForCandidate(m.getCandidateTerm());
                    if (prevMatch != null && avg > prevMatch.getEffectiveness())
                    {
                        match.matches.remove(prevMatch);
                        match.addMatch(m.getTargetTerm(), m.getCandidateTerm(), avg);
                    }
                    else if (prevMatch == null)
                        match.addMatch(m.getTargetTerm(), m.getCandidateTerm(), avg);
                }
            }
        }

        // Add mismatches
        ArrayList<Mismatch> mismatchesTarget1 = matchInfo1.getMismatchesTargetOntology();
        ArrayList<Mismatch> mismatchesCandidate1 = matchInfo1.getMismatchesCandidateOntology();
        for (Iterator<Mismatch> i = mismatchesTarget1.iterator(); i.hasNext();)
        {
            Mismatch m = (Mismatch) i.next();
            if (!match.isMatched(m.getTerm()))
                match.addMismatchTargetOntology(new Mismatch(m.getTerm()));
        }
        for (Iterator<Mismatch> i = mismatchesCandidate1.iterator(); i.hasNext();)
        {
            Mismatch m = (Mismatch) i.next();
            if (!match.isMatched(m.getTerm()))
                match.addMismatchCandidateOntology(new Mismatch(m.getTerm()));
        }

        // Add mismatches
        ArrayList<Mismatch> mismatchesTarget2 = matchInfo2.getMismatchesTargetOntology();
        ArrayList<Mismatch> mismatchesCandidate2 = matchInfo2.getMismatchesCandidateOntology();
        for (Iterator<Mismatch> i = mismatchesTarget2.iterator(); i.hasNext();)
        {
            Mismatch m = (Mismatch) i.next();
            if (!match.isMatched(m.getTerm()))
                match.addMismatchTargetOntology(new Mismatch(m.getTerm()));
        }
        for (Iterator<Mismatch> i = mismatchesCandidate2.iterator(); i.hasNext();)
        {
            Mismatch m = (Mismatch) i.next();
            if (!match.isMatched(m.getTerm()))
                match.addMismatchCandidateOntology(new Mismatch(m.getTerm()));
        }

        match.setTargetOntology(matchInfo1.getTargetOntology());
        match.setCandidateOntology(matchInfo1.getCandidateOntology());
        match.setAlgorithm(matchInfo1.getAlgorithm());
        match.setTargetOntologyTermsTotal(matchInfo1.getTargetOntologyTermsTotal());
        match.setCandidateOntologyTermsTotal(matchInfo1.getCandidateOntologyTermsTotal());

        return match;
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
     * Removes all matches/mismatches that are instance of "group", and replaces decomposed terms
     * with their parents
     */
    public void denormalize()
    {
        for (Iterator<Match> i = matches.listIterator(); i.hasNext();)
        {
            Match match = (Match) i.next();
            Term targetTerm = match.getTargetTerm();
            Term candidateTerm = match.getCandidateTerm();
            if (targetTerm.isInstanceOf("group") || candidateTerm.isInstanceOf("group"))
            {
                i.remove();
                addMismatchTargetOntology(new Mismatch(targetTerm));
                addMismatchCandidateOntology(new Mismatch(candidateTerm));
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
     * @param termName target term name
     * @return int - target Index (return -1 if fails to find)
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

    // gabi 24/3/2002
    /**
     * @param termName target term name
     * @return Term - target term (return null if fails to find)
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

    // gabi
    /**
     * @param termName candidate term name
     * @return Term - reference to the candidate term in the matches
     */
    public Term getCandidateTermInMatches(String termName)
    {
        for (int i = 0; i < matches.size(); i++)
        {
            Match match = (Match) (matches.get(i));
            Term term = match.getCandidateTerm();
            if ((term.getName()).equals(termName))
                return term;
        }
        return null;
    }

    /**
     * @param termName target term name
     * @return Term - reference to the target term in the matches
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
     * @param termName candidate term name
     * @return int - candidate Index (return -1 if fails to find)
     */
    public int getCandidateTermIndex(String termName)
    {
        if (originalCandidateTerms == null)
        {
            System.out.println("getCandidateTermIndex got NULL");
            System.exit(1);
        }
        for (int i = 0; i < originalCandidateTerms.size(); i++)
        {
            Term term = (Term) (originalCandidateTerms.get(i));
            if ((term.getName()).equals(termName))
                return i;
        }
        return -1;
    }

    /**
     * @return ArrayList - original Candidate Terms
     */
    public ArrayList<?> getOriginalCandidateTerms()
    {
        return originalCandidateTerms;
    }

    /**
     * @return ArrayList - original Target Terms
     */
    public ArrayList<?> getOriginalTargetTerms()
    {
        return originalTargetTerms;
    }

    /**
     * @param target term name
     * @param candidate term name
     * @param score Effectiveness of match
     * @return boolean true if exist in the match list
     */
    public boolean isExistSameMatch(String target, String candidate, double score)
    {
        Iterator<Match> it = matches.iterator();
        while (it.hasNext())
        {
            Match match = (Match) it.next();
            if (match.getTargetTerm().toString().equals(target) &&
                match.getCandidateTerm().toString().equals(candidate) &&
                match.getEffectiveness() == score)
            {
                return true;
            }
        }

        return false;
    }

    /**
     * @param st
     * @return double - the maximal Recall between the 2 parsings of the terms
     */
    public double getRecall(SchemaTranslator st)
    {
        SchemaTranslator st1_ = new SchemaTranslator(this, true, false);
        SchemaTranslator st2_ = new SchemaTranslator(this, true, true);
        return Math.max(SchemaMatchingsUtilities.calculateRecall(st, st1_),
            SchemaMatchingsUtilities.calculateRecall(st, st2_));
    }

    /**
     * @param st
     * @return double - the maximal Precision between the 2 parsings of the terms
     */
    public double getPrecision(SchemaTranslator st)
    {
        SchemaTranslator st1_ = new SchemaTranslator(this, true, false);
        SchemaTranslator st2_ = new SchemaTranslator(this, true, true);
        return Math.max(SchemaMatchingsUtilities.calculatePrecision(st, st1_),
            SchemaMatchingsUtilities.calculatePrecision(st, st2_));
    }

    /**
     * @param matches a list of {@link Match}
     */
    public void setMatches(ArrayList<Match> matches)
    {
        this.matches = matches;
    }

    /**
     * @param t a target {@link Term}
     * @return {@link Match} - the match for the target term, returns null if none is found
     */
    public Match getMatchForTarget(Term t)
    {
        for (Iterator<Match> i = matches.iterator(); i.hasNext();)
        {
            Match match = (Match) i.next();
            Term targetTerm = match.getTargetTerm();
            if (targetTerm.equals(t))
                return match;
        }
        return null;
    }
    // end gabi
}
