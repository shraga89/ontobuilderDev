package ac.technion.iem.ontobuilder.matching.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import ac.technion.iem.ontobuilder.core.ontology.OntologyUtilities;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.wrapper.SchemaMatchingsException;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.wrapper.SchemaMatchingsWrapper;
import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.meta.match.AbstractMapping;
import ac.technion.iem.ontobuilder.matching.meta.match.MatchMatrix;
import ac.technion.iem.ontobuilder.matching.meta.match.MatchedAttributePair;

/**
 * <p>
 * Title: SchemaTranslator
 * </p>
 * <p>
 * Description: Translates a given attribute to its matched one
 * </p>
 * Extends{@link AbstractMapping} 
 * 
 * @author Haggai Roitman
 * @version 1.1
 */

public class SchemaTranslator extends AbstractMapping
{
    /** used when attribute has no translation */
    public static final String NO_TRANSLATION = "No Translation";

    /** hashCode holder */
    private int hashCode;

    /**
     * Construct a default SchemaTranslator
     */
    public SchemaTranslator()
    {
        super();
    }

    /**
     * Constructs a SchemaTranslator
     * 
     * @param info the match information
     */
    public SchemaTranslator(MatchInformation info)
    {
        this(info, false);
    }

    /**
     * Constructs a SchemaTranslator
     * 
     * @param info the match information
     * @param removeIds flag to remove Ids
     */
    public SchemaTranslator(MatchInformation info, boolean removeIds)
    {
        this(info, removeIds, false);
    }

    /**
     * Constructs a SchemaTranslator
     * 
     * @param info the match information
     * @param removeIds flag to remove Ids
     * @param vs string version
     */
    public SchemaTranslator(MatchInformation info, boolean removeIds, boolean vs)
    {

        ArrayList<Match> matches = info.getMatches();
        ArrayList<MatchedAttributePair> temp = new ArrayList<MatchedAttributePair>();

        Iterator<Match> it = matches.iterator();
        int i = 0;
        String candTerm;
        String targetTerm;
        while (it.hasNext())
        {
            Match match = (Match) it.next();
            if (match.getTargetTerm() == null || match.getCandidateTerm() == null)
            {
                continue;
            }
            targetTerm = (vs ? match.getTargetTerm().toStringVs2() : match.getTargetTerm()
                .toString());
            candTerm = (vs ? match.getCandidateTerm().toStringVs2() : match.getCandidateTerm()
                .toString());
            if (removeIds)
            {
                targetTerm = OntologyUtilities.oneIdRemoval(targetTerm);
                candTerm = OntologyUtilities.oneIdRemoval(candTerm);
            }
            temp.add(new MatchedAttributePair(targetTerm, candTerm, match.getEffectiveness()));
        }

        schemaPairs = new MatchedAttributePair[temp.size()];

        Iterator<MatchedAttributePair> it2 = temp.iterator();
        while (it2.hasNext())
        {
            schemaPairs[i++] = it2.next();
        }
        removeIds();
    }

    /**
     * Get the Ids from the match info
     * 
     * @param info the match info
     * @param vs the string version
     */
    public void importIdsFromMatchInfo(MatchInformation info, boolean vs)
    {
        ArrayList<Match> matches = info.getMatches();
        ArrayList<MatchedAttributePair> temp = new ArrayList<MatchedAttributePair>();

        Iterator<Match> it = matches.iterator();
        int i = 0;
        String candTerm;
        String targetTerm;
        long id1, id2;
        while (it.hasNext())
        {
            Match match = (Match) it.next();
            if (match.getTargetTerm() == null || match.getCandidateTerm() == null)
            {
                continue;
            }
            id1 = match.getTargetTerm().getId();
            id2 = match.getCandidateTerm().getId();
            targetTerm = (vs ? match.getTargetTerm().toStringVs2() : match.getTargetTerm()
                .toString());
            candTerm = (vs ? match.getCandidateTerm().toStringVs2() : match.getCandidateTerm()
                .toString());
            MatchedAttributePair pair = new MatchedAttributePair(targetTerm, candTerm,
                match.getEffectiveness());
            pair.id1 = id1;
            pair.id2 = id2;
            temp.add(pair);
        }

        schemaPairs = new MatchedAttributePair[temp.size()];

        Iterator<MatchedAttributePair> it2 = temp.iterator();
        while (it2.hasNext())
        {
            schemaPairs[i++] = it2.next();
        }
    }

    /**
     * Removes Ids
     */
    private void removeIds()
    {
        for (int i = 0; i < schemaPairs.length; i++)
        {
            MatchedAttributePair pair = schemaPairs[i];
            pair.setAttribute1(OntologyUtilities.oneIdRemoval(pair.getAttribute1()));
            pair.setAttribute2(OntologyUtilities.oneIdRemoval(pair.getAttribute2()));
        }
    }

    /**
     * Returns the matches from the schema pairs
     * 
     * @return a list of matched attribute pairs
     */
    public ArrayList<MatchedAttributePair> getMatches()
    {
        ArrayList<MatchedAttributePair> matches = new ArrayList<MatchedAttributePair>(
            schemaPairs.length);
        for (int i = 0; i < schemaPairs.length; i++)
        {
            matches.add(schemaPairs[i]);
        }
        return matches;
    }

    /**
     * Constructs a SchemaTranslator
     * 
     * @param schemaPairs array of matched attribute pairs
     */
    public SchemaTranslator(MatchedAttributePair[] schemaPairs)
    {
        super(schemaPairs);
        removeIds();
        hashCode = calcHashCode(schemaPairs);
    }

    /**
     * Constructs a SchemaTranslator
     * 
     * @param schemaPairs array of matched attribute pairs
     * @param calcHashCode flag whether to calculate a hash code
     */
    public SchemaTranslator(MatchedAttributePair[] schemaPairs, boolean calcHashCode)
    {
        super(schemaPairs);
        removeIds();
        if (calcHashCode)
            hashCode = calcHashCode(schemaPairs);
    }

    /**
     * Translates a given attribute name
     * 
     * @param attribute to translate
     * @return translation or "NO TRANSLATION" when attribute was not match to any of the other
     * schema attributes
     */
    public String translateAttribute(String attribute)
    {
        for (int i = 0; i < schemaPairs.length; i++)
        {
            String result = schemaPairs[i].getAttributeTranslation(attribute);
            if (!(result.equals(NO_TRANSLATION)))
                return result;
        }
        return NO_TRANSLATION;
    }

    /**
     * Maps a given term
     * 
     * @param term
     * @param smw
     * @return a term
     */
    public Term translateTerm(Term term, SchemaMatchingsWrapper smw)
    {
        MatchMatrix matchMatrix = smw.getMatchMatrix();
        String translation = translateAttribute(term.toString());
        if (!translation.equals(NO_TRANSLATION))
        {
            if (matchMatrix.isCandTerm(term))
            {
                return matchMatrix.getTermByName(translation, matchMatrix.getTargetTerms());
            }
            else
            {
                return matchMatrix.getTermByName(translation, matchMatrix.getCandidateTerms());
            }
        }
        else
            return null;
    }

    /**
     * Release resources
     */
    public void nullify()
    {
        for (int i = 0; i < schemaPairs.length; i++)
            schemaPairs[i].nullify();
    }

    /**
     * Get the translation weight
     * 
     * @param attribute for the translation
     * @return the weight of the translation
     */
    public double getTranslationWeight(String attribute)
    {
        for (int i = 0; i < schemaPairs.length; i++)
        {
            String result = schemaPairs[i].getAttributeTranslation(attribute);
            if (!(result.equals(NO_TRANSLATION)))
                return schemaPairs[i].getMatchedPairWeight();
        }
        return 0;
    }

    /**
     * Add to OntoBuilder Match List
     * 
     * @param smw schema matching wrapper
     * @return list of matches
     */
    public ArrayList<Match> toOntoBuilderMatchList(SchemaMatchingsWrapper smw)
    {
        MatchMatrix matchMatrix = smw.getMatchMatrix();
        return toOntoBuilderMatchList(matchMatrix);
    }

    /**
     * Add to OntoBuilder Match List
     * 
     * @param matchMatrix a match matrix
     * @return
     */
    public ArrayList<Match> toOntoBuilderMatchList(MatchMatrix matchMatrix)
    {
        ArrayList<Match> matches = new ArrayList<Match>();
        for (int i = 0; i < schemaPairs.length; i++)
        {
            Term targetTerm = matchMatrix.getTermByName(schemaPairs[i].getAttribute2(),
                matchMatrix.getTargetTerms());
            Term candidateTerm = matchMatrix.getTermByName(schemaPairs[i].getAttribute1(),
                matchMatrix.getCandidateTerms());
            matches
                .add(new Match(targetTerm, candidateTerm, schemaPairs[i].getMatchedPairWeight()));
        }
        return matches;
    }

    /**
     * Get the match information
     * 
     * @param matrix the match matrix
     * @return match information
     */
    public MatchInformation getMatchInfromation(MatchMatrix matrix)
    {
        MatchInformation matchInfo = new MatchInformation();
        for (int i = 0; i < schemaPairs.length; i++)
        {
            Term targetTerm = matrix.getTermByName(schemaPairs[i].getAttribute2(),
                matrix.getTargetTerms());
            Term candidateTerm = matrix.getTermByName(schemaPairs[i].getAttribute1(),
                matrix.getCandidateTerms());
            matchInfo.addMatch(new Match(targetTerm, candidateTerm, schemaPairs[i]
                .getMatchedPairWeight()));
        }
        return matchInfo;
    }

    /**
     * Get the total match weight
     * 
     * @return the total match weight
     */
    public double getTotalMatchWeight()
    {
        double weight = 0;
        for (int i = 0; i < schemaPairs.length; i++)
            weight += schemaPairs[i].getMatchedPairWeight();
        return weight;
    }

    /**
     * Sets the schema pairs
     * 
     * @param pairs an array of matched attribute pairs
     */
    public void setSchemaPairs(MatchedAttributePair[] pairs)
    {
        schemaPairs = pairs;
        removeIds();
        hashCode = calcHashCode(pairs);
    }

    /**
     * Calculates HashCode for SchemaTranslator
     * 
     * @param pairs an array of matched attribute pairs
     */
    private int calcHashCode(MatchedAttributePair[] pairs)
    {
        int hashCode = 0;
        for (int i = 0; i < pairs.length; i++)
        {
            if (i % 2 == 0)
                hashCode += pairs[i].hashCode();
            else
                hashCode -= pairs[i].hashCode();
        }
        return hashCode;
    }

    /**
     * Saves a match into XML representation
     * 
     * @param matchIndex the match index
     * @param candSchemataName candidate schema name
     * @param targetSchemataName target schema name
     * @param filePath path of the file to save
     * @throws SchemaMatchingsException
     */
    public void saveMatchToXML(int matchIndex, String candSchemataName, String targetSchemataName,
        String filePath) throws SchemaMatchingsException
    {
        try
        {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setIgnoringComments(true);
            DocumentBuilder docBuilder = dbf.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            Comment comment = doc.createComment("This is the " + matchIndex + " Best matching\n" +
                "Use topkmatch.xsd to parse and validate the information (XMLSchema)");
            doc.appendChild(comment);
            Element bestMatch = doc.createElement("BestMatch");
            bestMatch.setAttribute("MatchIndex", Integer.toString(matchIndex));
            bestMatch.setAttribute("MatchWeight", Double
                .toString((getGlobalScore() != -1 ? getGlobalScore() : getTotalMatchWeight())));
            bestMatch.setAttribute("CandidateOntology", candSchemataName);
            bestMatch.setAttribute("TargetOntology", targetSchemataName);
            doc.appendChild(bestMatch);
            Element matchedPair = null;
            for (int i = 0; i < schemaPairs.length; i++)
            {
                matchedPair = doc.createElement("MatchedTerms");
                matchedPair.setAttribute("Weight",
                    Double.toString(schemaPairs[i].getMatchedPairWeight()));
                Element candidateTerm = doc.createElement("CandidateTerm");
                Text text = doc.createTextNode(schemaPairs[i].getAttribute1());
                candidateTerm.appendChild(text);
                if (schemaPairs[i].id2 != -1)
                    candidateTerm.setAttribute("id", Long.toString(schemaPairs[i].id2));
                Element targetTerm = doc.createElement("TargetTerm");
                text = doc.createTextNode(schemaPairs[i].getAttribute2());
                targetTerm.appendChild(text);
                if (schemaPairs[i].id1 != -1)
                    targetTerm.setAttribute("id", Long.toString(schemaPairs[i].id1));
                matchedPair.appendChild(candidateTerm);
                matchedPair.appendChild(targetTerm);
                bestMatch.appendChild(matchedPair);
            }
            comment = doc
                .createComment("For any questions regarding the Top K Framework please send to:ontobuilder@ie.technion.ac.il");
            doc.appendChild(comment);
            File xmlfile = new File(filePath + (filePath.indexOf(".xml") == -1 ? ".xml" : ""));
            // Use a Transformer for output
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new FileOutputStream(xmlfile));
            transformer.transform(source, result);
        }
        catch (Throwable e)
        {
            throw new SchemaMatchingsException(e.getMessage());
        }
    }

    /**
     * Prints the mappings into standard output
     */
    public void printTranslations()
    {
        System.out.println("Translation contents:\n");
        for (int i = 0; i < schemaPairs.length; i++)
        {
            System.out.println(schemaPairs[i].getAttribute1() + " -> " +
                schemaPairs[i].getAttribute2());
        }
    }

    /**
     * For testing - checks difference between pairs
     * 
     * @param pair matched attribute pair
     * @return true if the pair exists
     */
    public boolean isExist(MatchedAttributePair pair)
    {
        for (int i = 0; i < schemaPairs.length; i++)
            if (schemaPairs[i].getAttribute1().equals(pair.getAttribute1()) &&
                schemaPairs[i].getAttribute2().equals(pair.getAttribute2()))
                return true;
        return false;
    }

    /**
     * Returns the hash-code for AbstractMapping
     */
    public int hashCode()
    {
        return hashCode;
    }

    /**
     * Returns the global score if such exists, else return the total match weight
     * 
     * @return the score
     */
    public double getGlobalScore()
    {
        if (globalScore != -1)
            return globalScore;
        else
            return getTotalMatchWeight();
    }
}