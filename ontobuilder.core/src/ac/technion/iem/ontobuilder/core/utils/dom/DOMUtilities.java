package ac.technion.iem.ontobuilder.core.utils.dom;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

import org.hccp.net.CookieManager;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.DOMOutputter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;

import ac.technion.iem.ontobuilder.core.utils.StringUtilities;
import ac.technion.iem.ontobuilder.core.utils.files.StringInputStream;
import ac.technion.iem.ontobuilder.core.utils.network.NetworkUtilities;
import ac.technion.iem.ontobuilder.core.utils.properties.PropertiesHandler;

/*
 * Changes: 1) Haggai: fix for Cookie Management
 */

/**
 * <p>Title: DOMUtilities</p>
 * <p>Description: DOM utility functions for internal use</p> 
 */
public class DOMUtilities
{
    public static final String XML_VALIDATION_PROPERTY = "xmlValidation";

    // public static Client cookieClient=new Client();
    // public static CookieJar cookieJar=new CookieJar();

    public static CookieManager cm = new CookieManager();

    /**
     * Get the DOM document
     * 
     * @param url the {@link URL} to get the DOM from
     * @return a document
     * @throws IOException when cannot get the DOM from the URL
     */
    public static Document getDOM(URL url) throws IOException
    {
        if (url == null)
            throw new NullPointerException();
        return getDOM(url, null);
    }

    /**
     * Get the DOM document
     * 
     * @param url the {@link URL} to get the DOM from
     * @param errorOutput output stream for errors
     * @return a {@link Document}
     * @throws IOException when cannot get the DOM from the URL
     */
    public static Document getDOM(URL url, PrintWriter errorOutput) throws IOException
    {
        if (url == null)
            throw new NullPointerException();
        return getDOM(url, errorOutput, false);
    }

    /**
     * Get the DOM document
     * 
     * @param url the {@link URL} to get the DOM from
     * @param errorOutput output stream for errors
     * @param validation whether to perform a validation of not
     * @return a {@link Document}
     * @throws IOException when cannot get the DOM from the HTML
     */
    public static Document getDOM(URL url, PrintWriter errorOutput, boolean validation)
        throws IOException
    {
        if (url == null)
            throw new NullPointerException();
        String contentType = NetworkUtilities.getContentType(url);
        if (contentType == null)
            contentType = "text/html";
        if (contentType.indexOf("text/html") != -1)
        {
            URLConnection conn = url.openConnection();
            InputStream stream;
            if (conn instanceof HttpURLConnection)
            {
                HttpURLConnection connection = (HttpURLConnection) conn;
                // cookieClient.setCookies(connection,cookieJar);
                try
                {
                    cm.storeCookies(connection);
                }
                catch (Exception e)
                {
                }

                stream = connection.getInputStream();
                // cookieJar.addAll(cookieClient.getCookies(connection));
                try
                {
                    cm.storeCookies(connection);
                }
                catch (Exception e)
                {
                }

            }
            else
                stream = conn.getInputStream();
            return getDOMfromHTML(stream, errorOutput);
        }
        else if (contentType.indexOf("text/xml") != -1 ||
            contentType.indexOf("application/xml") != -1)
            return getDOMfromXML(url.openStream(), errorOutput, validation);
        else
            return null;
    }

    /**
     * Get a DOM from an XML
     * 
     * @param stream the input stream with the XML
     * @param errorOutput output stream for errors
     * @param validation whether to perform a validation of not
     * @return the {@link Document}
     * @throws IOException when cannot get the DOM from the XML
     */
    public static Document getDOMfromXML(InputStream stream, PrintWriter errorOutput,
        boolean validation) throws IOException
    {
        if (stream == null)
            throw new NullPointerException();

        try
        {
            SAXBuilder builder = new SAXBuilder(validation);
            org.jdom.Document doc = builder.build(stream);

            DOMOutputter fmt = new DOMOutputter();
            return fmt.output(doc);
        }
        catch (JDOMException e)
        {
            throw new IOException(e.getMessage());
        }
    }

    /**
     * Get a DOM from an HTML
     * 
     * @param stream the input stream with the HTML
     * @param errorOutput output stream for errors
     * @return the {@link Document}
     * @throws IOException when cannot get the DOM from the HTML
     */
    public static Document getDOMfromHTML(InputStream stream, PrintWriter errorOutput)
        throws IOException
    {
        if (stream == null)
            throw new NullPointerException();

        StringBuffer input = new StringBuffer();
        int c;
        while ((c = stream.read()) != -1)
            input.append((char) c);
        String filteredInput = filterInput(input.toString());
        InputStream propertiesStream = PropertiesHandler.class.getResourceAsStream(
        	"/config/tidyconfig.PROPERTIES");
        if (propertiesStream == null)
        {
            // throw new PropertyException("The property file '" + propertiesFile +
            // "' doesn't exists.");
            propertiesStream = new FileInputStream("tidyconfig.PROPERTIES");
        }
        Properties properties = new Properties();
        properties.load(propertiesStream);
        propertiesStream.close();

        Tidy tidy = new Tidy();
        tidy.setConfigurationFromProps(properties);
        if (errorOutput != null)
            tidy.setErrout(errorOutput);
        Document doc = tidy.parseDOM(new StringInputStream(filteredInput), null);
        cleanDOMTree(doc);
        return doc;
    }

    /**
     * Filters the input string (gets everything after "!doctype" or "html")
     * 
     * @param input the input string
     * @return the filtered input
     */
    public static String filterInput(String input)
    {
        String filteredInput = "";
        int index = input.toLowerCase().indexOf("<!doctype");
        if (index != -1)
            filteredInput = input.substring(index, input.length());
        else
        {
            index = input.toLowerCase().indexOf("<html");
            if (index != -1)
                filteredInput = input.substring(index, input.length());
        }
        return filteredInput;
    }

    /**
     * Prints a DOM document
     * 
     * @param doc the document to print
     * @param out the outpur stream to print to
     * @throws IOException
     */
    public static void prettyPrint(Document doc, OutputStream out) throws IOException
    {
        if (doc == null || out == null)
            throw new NullPointerException();
        InputStream propertiesStream = PropertiesHandler.class.getResourceAsStream(
        	"/config/tidyconfig.PROPERTIES");
        if (propertiesStream == null)
        {
            // throw new PropertyException("The property file '" + propertiesFile +
            // "' doesn't exists.");
            propertiesStream = new FileInputStream("tidyconfig.PROPERTIES");
        }
        Properties properties = new Properties();
        properties.load(propertiesStream);

        Tidy tidy = new Tidy();
        tidy.setConfigurationFromProps(properties);
        tidy.pprint(doc, out);
    }

    /**
     * Get the text value of a node
     * 
     * @param node the node
     * @return a string value
     */
    public static String getTextValue(Node node)
    {
        if (node == null || !isMeaningfulNode(node))
            return "";
        if (node.getNodeType() == org.w3c.dom.Node.TEXT_NODE &&
            node.getNodeValue().trim().length() > 0)
            return StringUtilities.trimWhitespace(node.getNodeValue());
        String text = new String("");
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++)
        {
            org.w3c.dom.Node child = childNodes.item(i);
            if (child.getNodeType() == org.w3c.dom.Node.TEXT_NODE)
                text += " " + child.getNodeValue();
            else
                text += " " + getTextValue(child);
        }
        text = StringUtilities.trimWhitespace(text);
        if (StringUtilities.hasLetterOrDigit(text))
            return text;
        else
            return "";
    }

    /**
     * Check whether a node is a meaningful node
     * 
     * @param node the node to check
     * @return <code>true</code> if is is meaningful
     */
    public static boolean isMeaningfulNode(Node node)
    {
        if (node.getNodeType() == org.w3c.dom.Node.TEXT_NODE)
            return true;
        if (node.getNodeType() != org.w3c.dom.Node.ELEMENT_NODE)
            return false;
        if (node.getNodeName().equals("script") || node.getNodeName().equals("select") ||
            node.getNodeName().equals("table"))
            return false;
        return true;
    }

    /**
     * Checks if a node is a child of a specific tag
     * 
     * @param node the node to check
     * @param tag the tag to compare to
     * @return <code>true</code> if the node is the child of the tag
     */
    public static boolean isNodeChildOfTag(Node node, String tag)
    {
        Node parent = node.getParentNode();
        while (parent != null)
        {
            if (parent.getNodeType() == Node.ELEMENT_NODE &&
                parent.getNodeName().equalsIgnoreCase(tag))
                return true;
            parent = parent.getParentNode();
        }
        return false;
    }

    /**
     * Find the nearest tag to a specific node
     * 
     * @param element the node
     * @param tag the tag to check
     * @return the nearest node or <code>null</code> if such does not exist
     */
    public static Element findNearestTagToNode(Element element, String tag)
    {
        Node parent = element;
        while (parent != null)
        {
            Node prevParent = parent.getPreviousSibling();
            while (prevParent != null)
            {
                Element result = findChildElementWithTag(prevParent, tag);
                if (result != null)
                    return result;
                prevParent = prevParent.getPreviousSibling();
            }
            parent = parent.getParentNode();
        }
        return null;
    }

    /**
     * Find a child element with a specific tag
     * 
     * @param node the node to start from
     * @param tag the tag to compare to
     * @return the node, if such is found
     */
    public static Element findChildElementWithTag(Node node, String tag)
    {
        if (node == null)
            return null;
        if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals(tag))
            return (Element) node;
        NodeList childs = node.getChildNodes();
        for (int i = 0; i < childs.getLength(); i++)
        {
            Element result = findChildElementWithTag(childs.item(i), tag);
            if (result != null)
                return result;
        }
        return null;
    }

    /**
     * Find a child elements with a specific tag
     * 
     * @param node the node to start from
     * @param tag the tag to compare to
     * @return the nodes list
     */
    public static ArrayList<Element> findChildElementsWithTag(Node node, String tag)
    {
        ArrayList<Element> results = new ArrayList<Element>();
        NodeList childs = node.getChildNodes();
        for (int i = 0; i < childs.getLength(); i++)
        {
            if (childs.item(i) instanceof Element)
            {
                Element child = (Element) childs.item(i);
                if (child.getNodeName().equals(tag))
                    results.add(child);
            }
        }
        return results;
    }

    /**
     * Cleans the DOM tree
     * 
     * @param node the root node
     */
    public static void cleanDOMTree(Node node)
    {
        ArrayList<Node> nodesToRemove = new ArrayList<Node>();
        NodeList childs = node.getChildNodes();
        for (int i = 0; i < childs.getLength(); i++)
        {
            Node child = childs.item(i);
            if (child == null ||
                (child.getNodeType() == Node.TEXT_NODE && StringUtilities.trimWhitespace(
                    StringUtilities.replace(child.getNodeValue(), "&nbsp;", " ")).length() == 0))
                nodesToRemove.add(child);
            else
                cleanDOMTree(child);
        }

        for (Iterator<Node> i = nodesToRemove.iterator(); i.hasNext();)
        {
            Node child = (Node) i.next();
            node.removeChild(child);
        }
    }
}