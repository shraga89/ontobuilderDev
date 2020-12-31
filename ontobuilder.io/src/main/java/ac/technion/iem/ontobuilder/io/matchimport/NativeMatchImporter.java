/**
 * 
 */
package ac.technion.iem.ontobuilder.io.matchimport;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;

/**
 * @author Tomer Sagi
 * Importer for Ontobuilder native match format (xml). 
 * This class is actually a shell for SchemaMatchingsUtilities.readXMLBestMatchingFile
 * TODO: fill out with MatchInformation based methods instead of  SchemaMatchingsUtilities.readXMLBestMatchingFile
 */
public class NativeMatchImporter implements MatchImporter {

	/* (non-Javadoc)
	 * @see ac.technion.iem.ontobuilder.io.matchimport.MatchImporter#importMatch(java.io.File)
	 */
	@Override
	public MatchInformation importMatch(MatchInformation mi, File file) {
		try {
			MatchInformation res = new MatchInformation(mi.getCandidateOntology(),mi.getTargetOntology());
			
			//Old schema translator based code
//			SchemaTranslator st = SchemaMatchingsUtilities.readXMLBestMatchingFile(file.getAbsolutePath(),mi.getMatrix());
//			res.setMatches(st.toOntoBuilderMatchList(mi.getMatrix()));
//			
			//New match information based code May 2012
	        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder docBuilder = dbf.newDocumentBuilder();
	        docBuilder.setErrorHandler(new ParseErrorHandler(50));
	        Document doc = docBuilder.parse(file);
	        parseNode(doc, res);
			return res;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
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
     * Parse a node in an XML file
     * 
     * @param node the node
     * @param matrix the matrix
     * @throws Exception
     */
    private static void parseNode(Node node, MatchInformation mi) throws Exception
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
                    parseNode(nodes.item(i), mi);
                }
            }
            break;
        case Node.ELEMENT_NODE:
            String name = node.getNodeName();
            if (name.compareTo("BestMatch") == 0)
            {
                parseBestMatch(node, mi);
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
    private static void parseBestMatch(Node node, MatchInformation mi)
    {
        NodeList children = node.getChildNodes();
        if (children != null)
        {
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
                    	//if(!currentChilds.item(k).getClass().getName().equals("com.sun.org.apache.xerces.internal.dom.DeferredElementImpl")) continue;
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
                                else continue;
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
                                else continue; 

                            }
                        }
                    }
                    if (candId == null || targetId == null) continue;
                    Term c = mi.getMatrix().getTermByID(Long.parseLong(candId), true);
                    Term t = mi.getMatrix().getTermByID(Long.parseLong(targetId), false);
                    assert(c!=null);
                    assert(t!=null);
                    mi.updateMatch(t, c, 1.0);
                }
            }
        }
    }

}
