package ac.technion.iem.ontobuilder.matching.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.meta.match.AbstractMapping;
import ac.technion.iem.ontobuilder.matching.meta.match.MatchMatrix;
import ac.technion.iem.ontobuilder.matching.meta.match.MatchedAttributePair;

/**
 * <p>
 * Title: SchemaMatchingsUtilities
 * </p>
 * <p>
 * Description: Schema matchings utilities Class<br>
 * This utilities class offers these statistics information regarding the best matches:<br>
 * difference of two given best matches<br>
 * saving the difference into XML representation<br>
 * creating a SchemaTranslator Object for given threshold<br>
 * given a best matching and an attribute , you can get all possible mappings for the same
 * weight(confidence)
 * </p>
 * 
 * @author Haggai Roitman
 * @version 1.1
 */
public final class SchemaMatchingsUtilities
{

    private static MatchedAttributePair[] pairs;

    /**
     * Returns the difference between two best matches
     * 
     * @param stPrevious SchemaTranslator Object of previous invoked best matching
     * @param stNext SchemaTranslator Object of next invoked best matching
     * @return ArrayList with new matched pairs (difference info)
     *//*
    public static ArrayList<MatchedAttributePair> diffBestMatches(SchemaTranslator stNext,
        SchemaTranslator stPrevious)
    {
        if (stPrevious == null || stNext == null)
            throw new NullPointerException();
        ArrayList<MatchedAttributePair> diff = new ArrayList<MatchedAttributePair>();
        MatchedAttributePair[] nextPairs;
        nextPairs = stNext.getMatchedPairs();
        for (int i = 0; i < nextPairs.length; i++)
            if (!stPrevious.isExist(nextPairs[i]))
                diff.add(nextPairs[i]);
        return diff;
    }*/

    /**
     * Returns all the MatchedAttributePair that are in the first array and not in the second one
     * 
     * @param a the first array
     * @param b the second array
     * @return the array with the difference in the two arrays
     */
    private static ArrayList<MatchedAttributePair> minus(ArrayList<MatchedAttributePair> a,
        ArrayList<MatchedAttributePair> b)
    {
        ArrayList<MatchedAttributePair> minus = new ArrayList<MatchedAttributePair>();
        Iterator<MatchedAttributePair> it = a.iterator();
        while (it.hasNext())
        {
            MatchedAttributePair p = it.next();
            if (!b.contains(p))
                minus.add(p);
        }
        return minus;
    }

    /**
     * Returns the intersection between two mappings
     * 
     * @param stNext the first mapping
     * @param stPrevious the second mapping
     * @return the intersection
     */
    public static ArrayList<MatchedAttributePair> intersectMappings(AbstractMapping stNext,
        AbstractMapping stPrevious)
    {
        ArrayList<MatchedAttributePair> a = new ArrayList<MatchedAttributePair>(), b = new ArrayList<MatchedAttributePair>();
        MatchedAttributePair[] ap = stNext.getMatchedPairs();
        MatchedAttributePair[] bp = stPrevious.getMatchedPairs();

        for (int i = 0; i < ap.length; i++)
        {
            a.add(ap[i]);

        }

        for (int i = 0; i < bp.length; i++)
        {
            b.add(bp[i]);

        }
        return minus(a, minus(a, b));
    }

    /**
     * Returns the intersection between two mappings
     * 
     * @param stNext the first mapping
     * @param stPrevious the second mapping
     * @param matrix the match matrix
     * @return the intersection
     */
    public static ArrayList<MatchedAttributePair> intersectMappings(AbstractMapping stNext,
        AbstractMapping stPrevious, MatchMatrix matrix)
    {
        ArrayList<MatchedAttributePair> a = new ArrayList<MatchedAttributePair>(), b = new ArrayList<MatchedAttributePair>();
        MatchedAttributePair[] ap = stNext.getMatchedPairs();
        MatchedAttributePair[] bp = stPrevious.getMatchedPairs();
        //Term cand, target;
        for (int i = 0; i < ap.length; i++)
        {
//            cand = matrix.getTermByName(ap[i].getAttribute2());
//            target = matrix.getTermByName(ap[i].getAttribute1());
//            if (cand != null && target != null)
//                a.add(new MatchedAttributePair(OntologyUtilities.oneIdRemoval(cand.toStringVs2()),
//                    OntologyUtilities.oneIdRemoval(target.toStringVs2()), ap[i]
//                        .getMatchedPairWeight()));
        	a.add(ap[i].clone());
        }

        for (int i = 0; i < bp.length; i++)
        {
//            cand = matrix.getTermByName(bp[i].getAttribute2());
//            target = matrix.getTermByName(bp[i].getAttribute1());
//            if (cand != null && target != null)
//                b.add(new MatchedAttributePair(OntologyUtilities.oneIdRemoval(cand.toStringVs2()),
//                    OntologyUtilities.oneIdRemoval(target.toStringVs2()), bp[i]
//                        .getMatchedPairWeight()));
        	b.add(bp[i].clone());
        }
        return minus(a, minus(a, b));
    }

    /**
     * Saves the difference between best matches to an XML file
     * 
     * @param stPrevious SchemaTranslator Object of previous invoked best matching
     * @param stNext SchemaTranslator Object of next invoked best matching
     * @param previousIndex index of previous match
     * @param nextIndex index of next match
     * @param candOntologyName
     * @param targetOntologyName
     * @param filepath
     * @throws SchemaMatchingsException
     *//*
    public static void saveDiffBestMatchesToXML(SchemaTranslator stPrevious,
        SchemaTranslator stNext, int previousIndex, int nextIndex, String candOntologyName,
        String targetOntologyName, String filepath) throws SchemaMatchingsException
    {

        try
        {
            ArrayList<MatchedAttributePair> diff = diffBestMatches(stPrevious, stNext);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setIgnoringComments(true);
            DocumentBuilder docBuilder = dbf.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            Comment comment = doc.createComment("This is the diff info between the " +
                previousIndex + " BM and the " + nextIndex + " BM\n" +
                "Use diffBestMatch.xsd to parse and validate the information (XMLSchema)");
            doc.appendChild(comment);
            Element diffMatch = doc.createElement("DiffBestMatch");
            diffMatch.setAttribute("PreviousMatchIndex", Integer.toString(previousIndex));
            diffMatch.setAttribute("NextMatchIndex", Integer.toString(nextIndex));
            diffMatch.setAttribute("CandidateOntology", candOntologyName);
            diffMatch.setAttribute("TargetOntology", targetOntologyName);
            diffMatch.setAttribute("MatchWeightDiff",
                Double.toString(stPrevious.getTotalMatchWeight() - stNext.getTotalMatchWeight()));
            doc.appendChild(diffMatch);
            Element matchedPair = null;
            Object[] pairs = diff.toArray();
            for (int i = 0; i < pairs.length; i++)
            {
                matchedPair = doc.createElement("NewMatchedTerms");
                matchedPair.setAttribute("Weight",
                    Double.toString(((MatchedAttributePair) pairs[i]).getMatchedPairWeight()));
                Element candidateTerm = doc.createElement("CandidateTerm");
                Element targetTerm = doc.createElement("TargetTerm");
                Text text = doc.createTextNode(((MatchedAttributePair) pairs[i]).getAttribute1());
                candidateTerm.appendChild(text);
                text = doc.createTextNode(((MatchedAttributePair) pairs[i]).getAttribute2());
                targetTerm.appendChild(text);
                matchedPair.appendChild(candidateTerm);
                matchedPair.appendChild(targetTerm);
                diffMatch.appendChild(matchedPair);
            }
            comment = doc
                .createComment("For any questions regarding the Top K Framework please send to:ontobuilder@ie.technion.ac.il");
            doc.appendChild(comment);
            File xmlfile = new File(filepath + (filepath.indexOf(".xml") == -1 ? ".xml" : ""));
            // Use a Transformer for output
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new FileOutputStream(xmlfile));
            transformer.transform(source, result);
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            throw new SchemaMatchingsException(e.getMessage());
        }
    }*/

    /**
     * Prints the difference between to best matches to standard output
     * 
     * @param miPrevious MatchInformation Object of previous invoked best matching
     * @param miNext MatchInformation Object of next invoked best matching
     */
    public static void printDiffBestMatches(MatchInformation miPrevious, MatchInformation miNext)
    {
        if (miPrevious == null || miNext == null)
            throw new NullPointerException();
        Set<Match> nextPairs;
        Set<Match> previousPairs;
        nextPairs = new HashSet<Match>(miNext.getCopyOfMatches());
        previousPairs = new HashSet<Match>(miPrevious.getCopyOfMatches());
        nextPairs.removeAll(previousPairs);
        System.out.println("Differences:");
        for (Match m : nextPairs)
        {
        	System.out
                    .println("new pair: " + m.toString());
        }
        System.out.println("**********************************");
    }

    /**
     * Creates a new SchemaTranslator Object that obeys the threshold
     * 
     * @param st original SchemaTranslator Object
     * @param threshold for the match
     * @return new SchemaTranslator Object according to the threshold
     */
    public static SchemaTranslator getSTwithThresholdSensitivity(AbstractMapping st,
        double threshold)
    {
        if (st == null)
            throw new NullPointerException();
        else if (!(threshold >= 0 && threshold <= 1))
            throw new IllegalArgumentException("Thershold must be 0 <= th <= 1");
        MatchedAttributePair[] oldPairs = st.getMatchedPairs();
        ArrayList<MatchedAttributePair> temp = new ArrayList<MatchedAttributePair>();
        for (int i = 0; i < oldPairs.length; i++)
            if (oldPairs[i].getMatchedPairWeight() >= threshold)
                temp.add(oldPairs[i]);
        MatchedAttributePair[] newPairs = new MatchedAttributePair[temp.size()];
        for (int i = 0; i < temp.size(); i++)
            newPairs[i] = temp.get(i);
        return new SchemaTranslator(newPairs);
    }

    /**
     * Returns all possible mappings for a given attribute under a best matching
     * 
     * @param st SchemaTranslator Object
     * @param attribute attribute to translate
     * @param candidateAttribute flags if attribute belongs to candidate schemata
     * @param includeOriginalTranslation flags if need to include in all possible matchs the
     * original mapping attribute
     * @return ArrayList with all possible mappings for the attribute
     *//*
    public static ArrayList<Long> getAllPossibleTranslations(SchemaTranslator st,
        Long attribute, boolean candidateAttribute, boolean includeOriginalTranslation)
    {
        ArrayList<Long> allPossibleTranslations = new ArrayList<Long>();
        MatchedAttributePair[] pairs = st.getMatchedPairs();
        double weight = st.getTranslationWeight(attribute);
        for (int i = 0; i < pairs.length; i++)
        {
            if (candidateAttribute)
            {
                if (pairs[i].getMatchedPairWeight() >= weight)
                {
                    if (attribute.equals(pairs[i].getId1()) &&
                        includeOriginalTranslation)
                        allPossibleTranslations.add(allPossibleTranslations.size(),
                            pairs[i].getId2());
                    else if (!attribute.equals(pairs[i].getId1()))
                        allPossibleTranslations.add(allPossibleTranslations.size(),
                            pairs[i].getId2());
                }
            }
            else
            {
                if (pairs[i].getMatchedPairWeight() >= weight)
                {
                    if (attribute.equals(pairs[i].getId2()) &&
                        includeOriginalTranslation)
                        allPossibleTranslations.add(allPossibleTranslations.size(),
                            pairs[i].getId1());
                    else if (!attribute.equals(pairs[i].getId2()))
                        allPossibleTranslations.add(allPossibleTranslations.size(),
                            pairs[i].getId1());
                }
            }
        }
        return allPossibleTranslations;
    }*/

    /**
     * Read the best matching XML file
     * 
     * @param filepath path to the file to read
     * @return the schema translator
     * @throws Exception
     *//*
    public static SchemaTranslator readXMLBestMatchingFile(String filepath) throws Exception
    {
        File file = new File(filepath);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbf.newDocumentBuilder();
        docBuilder.setErrorHandler(new ParseErrorHandler(50));
        Document doc = docBuilder.parse(file);
        parseNode(doc, null);
        return new SchemaTranslator(pairs, false);
    }*/

    /**
     * Read the best matching XML file
     * 
     * @param filepath path to the file to read
     * @param matrix the match matrix
     * @return the schema translator
     * @throws Exception
     */
    public static SchemaTranslator readXMLBestMatchingFile(String filepath, MatchMatrix matrix)
        throws Exception
    {
        File file = new File(filepath);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbf.newDocumentBuilder();
        docBuilder.setErrorHandler(new ParseErrorHandler(50));
        Document doc = docBuilder.parse(file);
        parseNode(doc, matrix);
        return new SchemaTranslator(pairs, false);
    }

    /**
     * Calculates the permanent value of the matrix
     * 
     * @param matrix the matrix to calculate
     * @return the calculated value
     */
    public static double calcPermanentValue(double[][] matrix)
    {
        PermanentCalculator calc = new PermanentCalculator(matrix[0].length, matrix);
        return calc.getPermanentValue();
    }

    /**
     * Parse a node in an XML file
     * 
     * @param node the node
     * @param matrix the matrix
     * @throws Exception
     * @deprecated
     */
    private static void parseNode(Node node, MatchMatrix matrix) throws Exception
    {
        switch (node.getNodeType())
        {
        case Node.DOCUMENT_NODE:

            // recurse on each child
            NodeList nodes = node.getChildNodes();
            if (nodes != null)
            {
                for (int i = 0; i < nodes.getLength(); i++)
                {
                    parseNode(nodes.item(i), matrix);
                }
            }
            break;
        case Node.ELEMENT_NODE:
            String name = node.getNodeName();
            if (name.compareTo("BestMatch") == 0)
            {
                parseBestMatch(node, matrix);
            }
            else
            {
                throw new Exception("XML file schema error");
            }
        }
    }

    /**
     * Parse a best match from an XML file
     * 
     * @param node the node
     * @param matrix the match matrix
     */
    private static void parseBestMatch(Node node, MatchMatrix matrix)
    {
        NodeList children = node.getChildNodes();
        if (children != null)
        {
        	ArrayList<MatchedAttributePair> tmpPairs = new ArrayList<MatchedAttributePair>();
            for (int i = 0; i < children.getLength(); i++)
            {
                Node current = children.item(i);
                String candTerm = "";
                String targetTerm = "";
                if (current.getNodeName().compareTo("MatchedTerms") == 0)
                {
                    String candId = null, targetId = null;
                    NodeList currentChilds = current.getChildNodes();
                    for (int k = 0; k < currentChilds.getLength(); k++)
                    {
                        Element child = (Element) currentChilds.item(k);
                        if (child.getNodeName().compareTo("CandidateTerm") == 0)
                        {
                            NodeList childChilds = child.getChildNodes();
                            for (int l = 0; l < childChilds.getLength(); l++)
                            {
                                Node childChild = childChilds.item(l);
                                candTerm = childChild.getNodeValue();
                                if (candTerm.equals(""))
                                    break;
                                if (child.getAttribute("id") != null &&
                                    child.getAttribute("id").length() > 0)
                                {
                                    candId = child.getAttribute("id");
                                }
//                                else if (matrix != null)
//                                {
//                                    Term t = matrix.getTermByName(OntologyUtilities
//                                        .oneIdRemoval(candTerm));
//                                    if (t != null && t.getId() != -1)
//                                    {
//                                        candId = Long.toString(t.getId());
//                                    }
//                                }
// no tolerance for exact matches with no id since 27/09/11
                                else continue; //new line 27/09/11 
                            }
                        }
                        else if (child.getNodeName().compareTo("TargetTerm") == 0)
                        {
                            NodeList childChilds = child.getChildNodes();
                            for (int l = 0; l < childChilds.getLength(); l++)
                            {
                                Node childChild = childChilds.item(l);
                                targetTerm = childChild.getNodeValue();
                                if (targetTerm.equals(""))
                                    break;
                                if (child.getAttribute("id") != null &&
                                    child.getAttribute("id").length() > 0)
                                {
                                    targetId = child.getAttribute("id");
                                }
//                                else if (matrix != null)
//                                {
//                                    Term t = matrix.getTermByName(OntologyUtilities
//                                        .oneIdRemoval(targetTerm));
//                                    if (t != null && t.getId() != -1)
//                                    {
//                                        targetId = Long.toString(t.getId());
//                                    }
//                                }
                                else continue; //new line 27/09/11
// no tolerance for exact matches with no id since 27/09/11
                            }
                        }
                    }
                    if (candId == null || targetId == null) continue;
                    MatchedAttributePair newPair = new MatchedAttributePair(candTerm,targetTerm,1.0,Long.parseLong("" + candId),Long.parseLong("" + targetId));
//                    if (candId != null && candId.length() > 0 && targetId != null &&
//                        targetId.length() > 0)
//                    {
//                    	newPair.id1 = Long.parseLong("" + candId);
//                    	newPair.id2 = Long.parseLong("" + targetId);
//                    }
                    tmpPairs.add(newPair);
                }
            }
            pairs = new MatchedAttributePair[tmpPairs.size()];
            pairs = tmpPairs.toArray(pairs);
        }
    }

    /**
     * Handles an error once parse error detected
     */
    public static class ParseErrorHandler implements ErrorHandler
    {
        int errorCount = 0;
        int errorsToPrint;

        public ParseErrorHandler(int errToPrint)
        {
            this.errorsToPrint = errToPrint;
        }

        public void warning(SAXParseException spe) throws SAXException
        {
            System.err.println("Warning: " + getParseExceptionInfo(spe));
        }

        public void error(SAXParseException spe) throws SAXException
        {
            if (errorCount < errorsToPrint)
            {
                System.err.println("Error: " + getParseExceptionInfo(spe));
                ++errorCount;
            }
            throw spe; // abort parsing
        }

        public void fatalError(SAXParseException spe) throws SAXException
        {
            if (errorCount < errorsToPrint)
            {
                System.err.println("Fatal: " + getParseExceptionInfo(spe));
            }
            throw spe;
        }

        public boolean errorFound()
        {
            return errorCount > 0;
        }

        private String getParseExceptionInfo(SAXParseException spe)
        {
            return "Line = " + spe.getLineNumber() + ": " + spe.getMessage();
        }

    }

    /**
     * Calculates the precision between two mappings
     * 
     * @param em first mapping
     * @param tkm second mapping
     * @return the precision
     */
    public static double calculatePrecision(AbstractMapping em, AbstractMapping tkm)
    {
        double b = intersectMappings(em, tkm).size();
        double c = tkm.getMatchedAttributesPairsCount();
        return (c > 0 ? (b / c) : 0);
    }

    /**
     * Calculates the precision between two mappings
     * 
     * @param em first mapping
     * @param tkm second mapping
     * @param matrix the match matrix
     * @return the precision
     */
    public static double calculatePrecision(AbstractMapping em, AbstractMapping tkm,
        MatchMatrix matrix)
    {
        double b = intersectMappings(em, tkm, matrix).size();
        double c = tkm.getMatchedAttributesPairsCount();
        return (c > 0 ? (b / c) : 0);
    }

    /**
     * Calculates the recall between two mappings
     * 
     * @param em first mapping
     * @param tkm second mapping
     * @return the recall
     */
    public static double calculateRecall(AbstractMapping em, AbstractMapping tkm)
    {
        double b = intersectMappings(em, tkm).size();
        double a = em.getMatchedAttributesPairsCount();
        return (a > 0 ? (b / a) : 0);
    }

    /**
     * Calculates the recall between two mappings
     * 
     * @param em first mapping
     * @param tkm second mapping
     * @param matrix the match matrix
     * @return the recall
     */
    public static double calculateRecall(AbstractMapping em, AbstractMapping tkm, MatchMatrix matrix)
    {
        double b = intersectMappings(em, tkm, matrix).size();
        double a = em.getMatchedAttributesPairsCount();
        return (a > 0 ? (b / a) : 0);
    }

    /**
     * Checks if the total mapping weight of two schema translators is the same
     * 
     * @param st1 first MatchInformation
     * @param st2 second MatchInformation
     * @param precision a precision to trim the values according to
     * @return true if the mapping is the same, else false
     */
    public static boolean isSameTotalMappingWeight(MatchInformation st1, MatchInformation st2,
        int precision)
    {
        // System.out.println(DoublePrecision.getDoubleP(st1.getGlobalScore(),precision)+" <-> "+
        // DoublePrecision.getDoubleP(st2.getGlobalScore(),precision));
        return (DoublePrecision.getDoubleP(st1.getGlobalScore(), precision) == DoublePrecision
            .getDoubleP(st2.getGlobalScore(), precision));
    }

}