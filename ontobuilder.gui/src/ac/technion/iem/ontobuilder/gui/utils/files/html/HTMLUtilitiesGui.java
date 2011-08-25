package ac.technion.iem.ontobuilder.gui.utils.files.html;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JTree;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileView;
import javax.swing.tree.DefaultMutableTreeNode;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ac.technion.iem.ontobuilder.core.utils.StringUtilities;
import ac.technion.iem.ontobuilder.core.utils.dom.DOMUtilities;
import ac.technion.iem.ontobuilder.core.utils.network.NetworkUtilities;
import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.INPUTElement;
import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.utils.files.common.FilePreview;

/*
 * 
 * Form submission fix
 */

/**
 * <p>Title: HTMLUtilities</p>
 */
public class HTMLUtilitiesGui
{
    public static final String HTML_META_NAVIGATION_PROPERTY = "metaNavigation";

    public static FileFilter htmlFileFilter = new HTMLFileFilter();
    public static FileView htmlFileViewer = new HTMLFileViewer();
    public static FilePreview htmlFilePreviewer = new HTMLFilePreviewer();

    private static URL base;

    /*
     * This attribute was added to fix a bug that generated an infinite loop
     */
    private static URL lastFrameUrl = null;

    public static URL getMETARefresh(Document doc) throws MalformedURLException
    {
        String content = null;
        boolean refresh = false;

        NodeList metaTags = doc.getElementsByTagName("meta");
        for(int i=0;i<metaTags.getLength();i++)
        {
            Node metaTag=metaTags.item(i);
            NamedNodeMap attributes=metaTag.getAttributes();
            if(attributes==null) return null;
            
            for(int j=0;j<attributes.getLength();j++)
            {
                if(attributes.item(j).getNodeName().equalsIgnoreCase("http-equiv") && attributes.item(j).getNodeValue().equalsIgnoreCase("refresh"))
                    refresh=true;
                else if(attributes.item(j).getNodeName().equalsIgnoreCase("content"))
                    content=attributes.item(j).getNodeValue();
            }
        }
        if (refresh && content != null)
        {
            String url = null;
            int index = content.toLowerCase().lastIndexOf("url=");
            if (index != -1)
                url = content.substring(index + 4, content.length()).trim();
            if (url != null)
                return new URL(url);
        }
        return null;
    }

    public static URL findNearestBase(Node root, Node node)
    {
        NodeList childs = root.getChildNodes();
        for (int i = 0; i < childs.getLength(); i++)
        {
            Node child = childs.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE &&
                ((Element) child).getTagName().equalsIgnoreCase("base"))
            {
                try
                {
                    base = NetworkUtilities.makeURL(((Element) child).getAttribute("href"));
                }
                catch (MalformedURLException e)
                {
                }
            }
            else if (child.equals(node))
                return base;
            else
            {
                URL retBase = findNearestBase(child, node);
                if (retBase != null)
                    return base = retBase;
            }
        }
        return null;
    }

    public static JTree getElementsHierarchy(Document doc, URL url)
    {
        DefaultMutableTreeNode rootNode = getElementsHierarchyNode(doc, url);
        JTree elementsTree = new JTree(rootNode);
        elementsTree.setCellRenderer(new ElementsTreeRenderer());
        return elementsTree;
    }

    protected static DefaultMutableTreeNode getElementsHierarchyNode(Document doc, URL url)
    {
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(
            ApplicationUtilities.getResourceString("html.elements"));

        // <FRAME> Elements
        DefaultMutableTreeNode frameTreeNode = new DefaultMutableTreeNode(new String("<frame>"));
        ArrayList<FRAMEElementGui> frameElements = getFRAMEElements(doc, url);
        for (Iterator<FRAMEElementGui> i = frameElements.iterator(); i.hasNext();)
        {
            FRAMEElementGui frameElement = (FRAMEElementGui) i.next();
            URL frameURL = frameElement.getSrc();
            DefaultMutableTreeNode frameElementNode = frameElement.getTreeBranch();
            try
            {
                Document frameDocument = DOMUtilities.getDOM(frameURL, new PrintWriter(
                    new StringWriter()));
                if (frameDocument != null)
                    frameElementNode.add(getElementsHierarchyNode(frameDocument, frameURL));
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            frameTreeNode.add(frameElementNode);
        }
        rootNode.add(frameTreeNode);

        // <META> Elements
        DefaultMutableTreeNode metaTreeNode = new DefaultMutableTreeNode("<meta>");
        ArrayList<METAElementGui> metaElements = getMETAElements(doc, url);
        for (Iterator<METAElementGui> i = metaElements.iterator(); i.hasNext();)
            metaTreeNode.add(((METAElementGui) i.next()).getTreeBranch());
        rootNode.add(metaTreeNode);

        // <A> Elements
        DefaultMutableTreeNode aTreeNode = new DefaultMutableTreeNode("<a>");
        ArrayList<AElementGui> aElements = getAElements(doc, url);
        for (Iterator<AElementGui> i = aElements.iterator(); i.hasNext();)
            aTreeNode.add(((AElementGui) i.next()).getTreeBranch());
        rootNode.add(aTreeNode);

        // <FORM> Elements
        DefaultMutableTreeNode formTreeNode = new DefaultMutableTreeNode(new String("<form>"));
        ArrayList<FORMElementGui> formElements = getFORMElements(doc, url);
        for (Iterator<FORMElementGui> i = formElements.iterator(); i.hasNext();)
            formTreeNode.add(((FORMElementGui) i.next()).getTreeBranch());
        rootNode.add(formTreeNode);

        return rootNode;
    }

    // Creates a tree of elements from the document and returns them for use in displaying the
    // form nicely.
    public static JTree getFORMElementsHierarchy(Document doc, URL url)
    {
        lastFrameUrl = null;
        DefaultMutableTreeNode rootNode = getFORMElementsHierarchyNode(doc, url);
        JTree elementsTree = new JTree(rootNode);
        elementsTree.setCellRenderer(new ElementsTreeRenderer());
        return elementsTree;
    }

    /*
     * Gets all the Form elements in the document First, the method runs getFRAMEElements, which
     * breaks the document into its frames elements. Then, for each sub-frame, it runs
     * getFRAMEElements again, etc. , and adds all the frame elements into an arrayList called
     * frameTreeNode. Then, frameTreeNode is added as a child to rootNode rootNode's second child is
     * formTreeNode. To build it, the method calls getFORMElements, which returns an arrayList of
     * FormElements, which the method then adds as child nodes to formTreeNode. finally, the method
     * returns rootNode with its two childs - frameTreeNode and formTreeNode
     */

    protected static DefaultMutableTreeNode getFORMElementsHierarchyNode(Document doc, URL url)
    {

        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(
            ApplicationUtilities.getResourceString("html.elements"));

        // <FRAME> Elements
        DefaultMutableTreeNode frameTreeNode = new DefaultMutableTreeNode(new String("<frame>"));
        ArrayList<FRAMEElementGui> frameElements = getFRAMEElements(doc, url);
        for (Iterator<FRAMEElementGui> i = frameElements.iterator(); i.hasNext();)
        {
            FRAMEElementGui frameElement = (FRAMEElementGui) i.next();
            URL frameURL = frameElement.getSrc();
            DefaultMutableTreeNode frameElementNode = frameElement.getTreeBranch();
            try
            {
                Document frameDocument = DOMUtilities.getDOM(frameURL, new PrintWriter(
                    new StringWriter()));
                if (frameDocument != null && !frameURL.equals(lastFrameUrl))
                {

                    lastFrameUrl = frameURL;
                    frameElementNode.add(getFORMElementsHierarchyNode(frameDocument, frameURL));

                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            frameTreeNode.add(frameElementNode);
        }
        rootNode.add(frameTreeNode);
        // <FORM> Elements
        DefaultMutableTreeNode formTreeNode = new DefaultMutableTreeNode(new String("<form>"));
        ArrayList<FORMElementGui> formElements = getFORMElements(doc, url);
        for (Iterator<FORMElementGui> i = formElements.iterator(); i.hasNext();)
            formTreeNode.add(((FORMElementGui) i.next()).getTreeBranch());
        rootNode.add(formTreeNode);

        return rootNode;
    }

    public static ArrayList<AElementGui> getAElements(Node node, URL url)
    {
        ArrayList<AElementGui> aElementsList = new ArrayList<AElementGui>();
        base = null;
        getAElementsRecursive(aElementsList, node, url);
        return aElementsList;
    }

    private static void getAElementsRecursive(ArrayList<AElementGui> aElementsList, Node node, URL url)
    {
        NodeList childs = node.getChildNodes();
        for (int i = 0; i < childs.getLength(); i++)
        {
            Node child = childs.item(i);
            if (child.getNodeType() != Node.ELEMENT_NODE)
                continue;
            Element element = (Element) child;
            String name = element.getTagName();
            if (name.equalsIgnoreCase("base"))
            {
                try
                {
                    base = NetworkUtilities.makeURL(element.getAttribute("href"));
                }
                catch (MalformedURLException e)
                {
                }
            }
            else if (name.equalsIgnoreCase("a"))
            {
                try
                {
                    URL absoluteURL = NetworkUtilities.getAbsoluteURL(element.getAttribute("href"),
                        url, base);
                    if (absoluteURL != null) // Get the text used for reference
                    {
                        // Get text
                        String text = DOMUtilities.getTextValue(element);
                        if (text != null && text.length() > 0)
                        {
                            AElementGui aElement = new AElementGui(text, absoluteURL);
                            aElement.setTarget(element.getAttribute("target"));
                            aElement.setName(element.getAttribute("name"));
                            aElementsList.add(aElement);
                        }
                        else
                        {
                            // Get images
                            NodeList imageElements = element.getElementsByTagName("img");
                            if (imageElements.getLength() > 0)
                            {
                                Element imageElement = (Element) imageElements.item(0);
                                String alt = imageElement.getAttribute("alt").trim();
                                AElementGui aElement = new AElementGui(alt, absoluteURL);
                                aElement.setTarget(element.getAttribute("target"));
                                aElement.setName(element.getAttribute("name"));
                                aElementsList.add(aElement);
                            }
                        }
                    }
                }
                catch (MalformedURLException e)
                {
                }
            }
            else
                getAElementsRecursive(aElementsList, child, url);
        }
    }

    public static ArrayList<METAElementGui> getMETAElements(Document document, URL documentURL)
    {
        ArrayList<METAElementGui> metaElementsList = new ArrayList<METAElementGui>();
        NodeList metaElements = document.getElementsByTagName("meta");
        for (int i = 0; i < metaElements.getLength(); i++)
        {
            Element metaElement = (Element) metaElements.item(i);
            METAElementGui meta = new METAElementGui();
            meta.setHTTPEquiv(metaElement.getAttribute("http-equiv"));
            meta.setName(metaElement.getAttribute("name"));
            meta.setContent(metaElement.getAttribute("content"), documentURL);
            metaElementsList.add(meta);
        }
        return metaElementsList;
    }

    public static ArrayList<FRAMEElementGui> getFRAMEElements(Document doc, URL url)
    {
        ArrayList<FRAMEElementGui> frameElementsList = new ArrayList<FRAMEElementGui>();
        base = null;
        getFRAMEElementsRecursive(frameElementsList, doc, url);
        return frameElementsList;
    }

    private static void getFRAMEElementsRecursive(ArrayList<FRAMEElementGui> frameElementsList,
        Node node, URL url)
    {
        NodeList childs = node.getChildNodes();
        for (int i = 0; i < childs.getLength(); i++)
        {
            Node child = childs.item(i);
            if (child.getNodeType() != Node.ELEMENT_NODE)
                continue;
            Element element = (Element) child;
            String name = element.getTagName();
            if (name.equalsIgnoreCase("base"))
            {
                try
                {
                    base = NetworkUtilities.makeURL(element.getAttribute("href"));
                }
                catch (MalformedURLException e)
                {
                }
            }
            else if (name.equalsIgnoreCase("frame"))
            {
                try
                {
                    frameElementsList.add(new FRAMEElementGui(NetworkUtilities.getAbsoluteURL(
                        element.getAttribute("src"), url, base), element.getAttribute("name")));
                }
                catch (MalformedURLException e)
                {
                }
            }
            else if (name.equalsIgnoreCase("iframe"))
            {
                try
                {
                    FRAMEElementGui frameElement = new FRAMEElementGui(NetworkUtilities.getAbsoluteURL(
                        element.getAttribute("src"), url, base), element.getAttribute("name"));
                    frameElement.setInternal(true);
                    frameElementsList.add(frameElement);
                }
                catch (MalformedURLException e)
                {
                }
            }
            else
                getFRAMEElementsRecursive(frameElementsList, child, url);
        }
    }

    public static ArrayList<FORMElementGui> getFORMElements(Document doc, URL url)
    {
        ArrayList<FORMElementGui> formElementsList = new ArrayList<FORMElementGui>();
        base = null;
        getFORMElementsRecursive(formElementsList, doc, doc, url);
        // The following is not necessary if we use a good HTML parser
        resolveOrphansINPUTElements(formElementsList, doc, url);
        return formElementsList;
    }

    /*
     * Recursively gets all the forms in the document Called by getFORMElements with an empty
     * formElementsList, doc,doc,url. in each run, the method runs over all the children of the
     * current node, looking for forms. if it finds one, it adds it to formElementsList, and
     * regardless, it calls itself again on all child nodes.
     */
    private static void getFORMElementsRecursive(ArrayList<FORMElementGui> formElementsList,
        Document doc, Node node, URL url)
    {
        NodeList childs = node.getChildNodes();
        for (int i = 0; i < childs.getLength(); i++)
        {
            Node child = childs.item(i);
            if (child.getNodeType() != Node.ELEMENT_NODE)
                continue;
            Element element = (Element) child;
            String name = element.getTagName();
            if (name.equalsIgnoreCase("base"))
            {
                try
                {
                    base = NetworkUtilities.makeURL(element.getAttribute("href"));
                }
                catch (MalformedURLException e)
                {
                }
            }
            else if (name.equalsIgnoreCase("form"))
            {
                try
                {
                    FORMElementGui form = new FORMElementGui(element.getAttribute("name"),
                        NetworkUtilities.getAbsoluteURL(element.getAttribute("action"), url, base),
                        element.getAttribute("method"));
                    form.setInputs(getINPUTElementsForForm(doc, url, element));
                    form.flat();
                    formElementsList.add(form);
                }
                catch (MalformedURLException e)
                {
                }
            }
            getFORMElementsRecursive(formElementsList, doc, child, url);
        }
    }

    /*
     * Processes all the input elements of element 'form', and returns an array list on INPUTElement
     * (s) translates DOM elements into INPUTElements by calling processINPUTElement
     */
    public static ArrayList<INPUTElementGui> getINPUTElementsForForm(Document document,
        URL documentURL, Element form)
    {
        ArrayList<INPUTElementGui> inputElementsList = new ArrayList<INPUTElementGui>();

        NodeList inputElements = form.getElementsByTagName("*");
        for (int i = 0; i < inputElements.getLength(); i++)
        {
            Element inputElement = (Element) inputElements.item(i);
            INPUTElementGui input = processINPUTElement(inputElement, document, documentURL);
            if (input != null)
                inputElementsList.add(input);
        }
        return inputElementsList;
    }

    private static INPUTElementGui processINPUTElement(Element inputElement, Document document,
        URL documentURL)
    {
        if (!inputElement.getTagName().equals("input") &&
            !inputElement.getTagName().equals("select") &&
            !inputElement.getTagName().equals("textarea"))
            return null;
        String inputType = "";
        if (inputElement.getTagName().equalsIgnoreCase("select"))
            inputType = "select";
        else if (inputElement.getTagName().equalsIgnoreCase("textarea"))
            inputType = "textarea";
        else
            inputType = inputElement.getAttribute("type").toLowerCase();
        if (inputType == null || inputType.equals(""))
            inputType = INPUTElement.TEXT;
        INPUTElementGui input = null;

        // hidden
        if (inputType.equals(INPUTElement.HIDDEN))
        {
            input = new HiddenINPUTElementGui(inputElement.getAttribute("name"),
                inputElement.getAttribute("value"));
        }
        // text
        else if (inputType.equals(INPUTElement.TEXT))
        {
            TextINPUTElementGui textInput = new TextINPUTElementGui(inputElement.getAttribute("name"),
                inputElement.getAttribute("value"));
            textInput.setLabel(cleanHTMLEntities(StringUtilities
                .normalizeSpaces(getElementLabel(inputElement))));
            try
            {
                textInput.setSize(Integer.parseInt(inputElement.getAttribute("size")));
            }
            catch (NumberFormatException e)
            {
            }
            try
            {
                textInput.setMaxLength(Integer.parseInt(inputElement.getAttribute("maxlength")));
            }
            catch (NumberFormatException e)
            {
            }
            textInput.setReadOnly(inputElement.getAttributeNode("readonly") != null);
            input = textInput;
        }
        // password
        else if (inputType.equals(INPUTElement.PASSWORD))
        {
            PasswordINPUTElementGui passwordInput = new PasswordINPUTElementGui(
                inputElement.getAttribute("name"), inputElement.getAttribute("value"));
            passwordInput.setLabel(cleanHTMLEntities(StringUtilities
                .normalizeSpaces(getElementLabel(inputElement))));
            try
            {
                passwordInput.setSize(Integer.parseInt(inputElement.getAttribute("size")));
            }
            catch (NumberFormatException e)
            {
            }
            try
            {
                passwordInput
                    .setMaxLength(Integer.parseInt(inputElement.getAttribute("maxlength")));
            }
            catch (NumberFormatException e)
            {
            }
            passwordInput.setReadOnly(inputElement.getAttributeNode("readonly") != null);
            input = passwordInput;
        }
        // file
        else if (inputType.equals(INPUTElement.FILE))
        {
            FileINPUTElementGui fileInput = new FileINPUTElementGui(inputElement.getAttribute("name"));
            fileInput.setLabel(cleanHTMLEntities(StringUtilities
                .normalizeSpaces(getElementLabel(inputElement))));
            try
            {
                fileInput.setSize(Integer.parseInt(inputElement.getAttribute("size")));
            }
            catch (NumberFormatException e)
            {
            }
            try
            {
                fileInput.setMaxLength(Integer.parseInt(inputElement.getAttribute("maxlength")));
            }
            catch (NumberFormatException e)
            {
            }
            fileInput.setReadOnly(inputElement.getAttributeNode("readonly") != null);
            input = fileInput;
        }
        // image
        else if (inputType.equals(INPUTElement.IMAGE))
        {
            try
            {
                URL base = findNearestBase(document, inputElement);
                URL src = NetworkUtilities.getAbsoluteURL(inputElement.getAttribute("src"),
                    documentURL, base);
                input = new ImageINPUTElementGui(inputElement.getAttribute("name"), src,
                    inputElement.getAttribute("alt"));
            }
            catch (MalformedURLException e)
            {
            }
        }
        // submit
        else if (inputType.equals(INPUTElement.SUBMIT))
        {
            input = new SubmitINPUTElementGui(
                inputElement.getAttribute("name"),
                inputElement.getAttributeNode("value") != null ? inputElement.getAttribute("value") : ApplicationUtilities
                    .getResourceString("html.input.submit.defaultValue"));
        }
        // button
        else if (inputType.equals(INPUTElement.BUTTON))
        {
            input = new ButtonINPUTElementGui(inputElement.getAttribute("name"),
                inputElement.getAttribute("value"));
        }
        // reset
        else if (inputType.equals(INPUTElement.RESET))
        {
            input = new ResetINPUTElementGui(
                inputElement.getAttribute("name"),
                inputElement.getAttributeNode("value") != null ? inputElement.getAttribute("value") : ApplicationUtilities
                    .getResourceString("html.input.reset.defaultValue"));
        }
        // select
        else if (inputType.equals(INPUTElement.SELECT))
        {
            SELECTElementGui selectInput = new SELECTElementGui(inputElement.getAttribute("name"));
            selectInput.setMultiple(inputElement.getAttributeNode("multiple") != null);
            selectInput.setLabel(cleanHTMLEntities(StringUtilities
                .normalizeSpaces(getElementLabel(inputElement))));
            try
            {
                selectInput.setSize(Integer.parseInt(inputElement.getAttribute("size")));
            }
            catch (NumberFormatException e)
            {
            }
            NodeList optionElements = inputElement.getElementsByTagName("option");

            // Amir
            boolean isThereDefaultSelected = false;
            String optionValue = null;
            //

            for (int j = 0; j < optionElements.getLength(); j++)
            {
                Element optionElement = (Element) optionElements.item(j);
                // Amir
                optionValue = optionElement.getAttribute("value");
                OPTIONElementGui selectOption = new OPTIONElementGui(optionValue);
                if (optionElement.getAttributeNode("selected") != null)
                {
                    selectOption.setDefaultSelected(true);
                    isThereDefaultSelected = true;
                }
                //
                // OPTIONElement selectOption=new
                // OPTIONElement(optionElement.getAttribute("value"));
                // selectOption.setDefaultSelected(optionElement.getAttributeNode("selected")!=null);

                if (optionElement.getAttribute("label").length() > 0)
                    selectOption.setLabel(optionElement.getAttribute("label"));
                else
                    selectOption.setLabel(cleanHTMLEntities(StringUtilities
                        .normalizeSpaces(DOMUtilities.getTextValue(optionElement))));

                // Amir
                if (optionValue == null || optionValue.length() == 0)
                {
                    selectOption.setValue(selectOption.getLabel());
                }
                //

                selectInput.addOption(selectOption);

                // Amir
                if (!isThereDefaultSelected && (selectInput.getOptionsCount() > 0))
                {
                    selectInput.getOption(0).setDefaultSelected(true);
                }
                //
            }
            input = selectInput;
        }
        // checkbox
        else if (inputType.equals(INPUTElement.CHECKBOX))
        {
            CheckboxINPUTElementGui checkboxInput = new CheckboxINPUTElementGui(
                inputElement.getAttribute("name"));
            // CheckboxINPUTElementOption checkboxOption=new
            // CheckboxINPUTElementOption(inputElement.getAttribute("value"));
            // Amir
            String checkBoxValue = inputElement.getAttribute("value");
            if (checkBoxValue.length() == 0)
                checkBoxValue = "on";
            CheckboxINPUTElementOptionGui checkboxOption = new CheckboxINPUTElementOptionGui(
                checkBoxValue);
            //
            checkboxInput.addOption(checkboxOption);
            checkboxOption.setDefaultChecked(inputElement.getAttributeNode("checked") != null);
            checkboxInput.setLabel(cleanHTMLEntities(StringUtilities
                .normalizeSpaces(getElementLabel(inputElement))));
            checkboxOption.setLabel(cleanHTMLEntities(StringUtilities.normalizeSpaces(DOMUtilities
                .getTextValue(inputElement.getNextSibling()))));
            input = checkboxInput;
        }
        // radio
        else if (inputType.equals(INPUTElement.RADIO))
        {
            RadioINPUTElementGui radioInput = new RadioINPUTElementGui(inputElement.getAttribute("name"));
            RadioINPUTElementOptionGui radioOption = new RadioINPUTElementOptionGui(
                inputElement.getAttribute("value"));
            radioInput.addOption(radioOption);
            radioOption.setDefaultChecked(inputElement.getAttributeNode("checked") != null);
            radioInput.setLabel(cleanHTMLEntities(StringUtilities
                .normalizeSpaces(getElementLabel(inputElement))));
            radioOption.setLabel(cleanHTMLEntities(StringUtilities.normalizeSpaces(DOMUtilities
                .getTextValue(inputElement.getNextSibling()))));
            input = radioInput;
        }
        // textarea
        else if (inputType.equals(INPUTElement.TEXTAREA))
        {
            TEXTAREAElementGui textareaInput = new TEXTAREAElementGui(inputElement.getAttribute("name"));
            textareaInput.setDefaultValue(DOMUtilities.getTextValue(inputElement));
            textareaInput.setLabel(cleanHTMLEntities(StringUtilities
                .normalizeSpaces(getElementLabel(inputElement))));
            try
            {
                textareaInput.setRows(Integer.parseInt(inputElement.getAttribute("rows")));
            }
            catch (NumberFormatException e)
            {
            }
            try
            {
                textareaInput.setCols(Integer.parseInt(inputElement.getAttribute("cols")));
            }
            catch (NumberFormatException e)
            {
            }
            textareaInput.setReadOnly(inputElement.getAttributeNode("readonly") != null);
            input = textareaInput;
        }
        if (input != null)
        {
            if (inputElement.getAttributeNode("onchange") != null)
                input.addEvent(INPUTElement.ONCHANGE, inputElement.getAttribute("onchange"));
            if (inputElement.getAttributeNode("onblur") != null)
                input.addEvent(INPUTElement.ONBLUR, inputElement.getAttribute("onblur"));
            if (inputElement.getAttributeNode("onselect") != null)
                input.addEvent(INPUTElement.ONSELECT, inputElement.getAttribute("onselect"));
            if (inputElement.getAttributeNode("onfocus") != null)
                input.addEvent(INPUTElement.ONFOCUS, inputElement.getAttribute("onfocus"));

            input.setDisabled(inputElement.getAttributeNode("disabled") != null);
        }
        return input;
    }

    public static String getElementLabel(Node inputElement)
    {
        // boolean print=((Element)inputElement).getAttribute("name").equals("rlocterms");

        String elementLabel = "";
        try
        {
            { // Try to find the label in a text defined before the input
                Node node = inputElement;
                do
                {
                    node = node.getPreviousSibling() != null ? node.getPreviousSibling() : (elementLabel
                        .trim().length() == 0 ? node.getParentNode() : null);
                    if (node == null || node.getNodeName().equals("table") ||
                        node.getNodeName().equals("tr") || node.getNodeName().equals("td") ||
                        node.getNodeName().equals("form"))
                        break;
                    elementLabel = DOMUtilities.getTextValue(node) + " " + elementLabel;
                }
                while ((elementLabel.trim().length() == 0 || node.getNodeType() == Node.TEXT_NODE ||
                    node.getNodeName().equals("p") || node.getNodeName().equals("br") ||
                    node.getNodeName().equals("input") || node.getNodeName().equals("select") || node
                    .getNodeName().equals("textarea")) && !node.getNodeName().equals("form"));
                if (elementLabel.trim().length() > 0)
                    return StringUtilities.cleanText(elementLabel);
            }

            { // Try to find the label in a table
                Node tableNode = inputElement;
                do
                {
                    tableNode = tableNode.getParentNode();
                }
                while (tableNode != null && !tableNode.getNodeName().equals("table"));
                if (tableNode != null && tableNode.getNodeType() == Node.ELEMENT_NODE &&
                    tableNode.getNodeName().equals("table")) // Input is inside a table
                {
                    HTMLTableGui table = HTMLTableGui.buildTable((Element) tableNode);
                    Node tdNode = inputElement;
                    do
                    {
                        tdNode = tdNode.getParentNode();
                    }
                    while (tdNode != null && !tdNode.getNodeName().equals("td"));
                    if (tdNode != null && tdNode.getNodeName().equals("td")) // Input is inside a
                                                                             // table column
                    {
                        { // Try to find label on the left
                            Node tdLeftNode = tdNode;
                            do
                            {
                                tdLeftNode = table.getLeftCell(tdLeftNode);
                                if (tdLeftNode == null)
                                    break;
                                elementLabel = DOMUtilities.getTextValue(tdLeftNode);
                            }
                            while (elementLabel.length() == 0);
                            if (elementLabel.trim().length() > 0)
                                return StringUtilities.cleanText(elementLabel);
                        }

                        { // Try to find label on top
                            Node tdTopNode = tdNode;
                            do
                            {
                                tdTopNode = table.getNonEmptyTopCell(tdTopNode);
                                if (tdTopNode == null)
                                    break;
                                elementLabel = DOMUtilities.getTextValue(tdTopNode);
                            }
                            while (elementLabel.length() == 0);
                            if (elementLabel.trim().length() > 0)
                                return StringUtilities.cleanText(elementLabel);
                        }

                        { // Make one last try
                            Node parent = tdNode;
                            do
                            {
                                Node prevNode = parent;
                                do
                                {
                                    prevNode = prevNode.getPreviousSibling();
                                    if (prevNode == null)
                                        break;
                                    elementLabel = DOMUtilities.getTextValue(prevNode);
                                }
                                while (prevNode != null && elementLabel.length() == 0);
                                parent = parent.getParentNode();
                            }
                            while (parent != null && !parent.getNodeName().equals("form") &&
                                elementLabel.length() == 0);
                            if (elementLabel.trim().length() > 0)
                                return StringUtilities.cleanText(elementLabel);
                        }
                    }
                }
            }

            { // Try to find the label in an image defined before the input
                Node node = inputElement;
                do
                {
                    node = node.getPreviousSibling();
                }
                while (node != null && !node.getNodeName().equals("img") &&
                    !node.getNodeName().equals("form"));
                if (node != null && node.getNodeName().equals("img"))
                    return StringUtilities.cleanText(((Element) node).getAttribute("alt"));
            }

            { // Try to find the label in an image in a table
                Node tableNode = inputElement;
                do
                {
                    tableNode = tableNode.getParentNode();
                }
                while (tableNode != null && !tableNode.getNodeName().equals("table"));
                if (tableNode == null || tableNode.getNodeType() != Node.ELEMENT_NODE ||
                    !tableNode.getNodeName().equals("table"))
                    return null; // Input is not inside a table
                HTMLTableGui table = HTMLTableGui.buildTable((Element) tableNode);

                Node tdNode = inputElement;
                do
                {
                    tdNode = tdNode.getParentNode();
                }
                while (tdNode != null && !tdNode.getNodeName().equals("td"));
                if (tdNode == null || !tdNode.getNodeName().equals("td"))
                    return null; // Input is not inside a table column

                { // Try to find label on an image on the left
                    Node tdLeftNode = tdNode;
                    do
                    {
                        tdLeftNode = table.getLeftCell(tdLeftNode);
                        if (tdLeftNode == null)
                            break;
                        if (tdLeftNode.getNodeType() != Node.ELEMENT_NODE)
                            continue;
                        NodeList imgElements = ((Element) tdLeftNode).getElementsByTagName("img");
                        if (imgElements.getLength() > 0)
                        {
                            Element imgElement = (Element) imgElements.item(0);
                            elementLabel = imgElement.getAttribute("alt");
                        }
                    }
                    while (elementLabel.length() == 0);
                    if (elementLabel.trim().length() > 0)
                        return StringUtilities.cleanText(elementLabel);
                }

                { // Try to find label on an image on top
                    Node tdTopNode = tdNode;
                    do
                    {
                        tdTopNode = table.getTopCell(tdTopNode);
                        if (tdTopNode == null)
                            break;
                        if (tdTopNode.getNodeType() != Node.ELEMENT_NODE)
                            continue;
                        NodeList imgElements = ((Element) tdTopNode).getElementsByTagName("img");
                        if (imgElements.getLength() > 0)
                        {
                            Element imgElement = (Element) imgElements.item(0);
                            elementLabel = imgElement.getAttribute("alt");
                        }
                    }
                    while (elementLabel.length() == 0);
                    if (elementLabel.trim().length() > 0)
                        return StringUtilities.cleanText(elementLabel);
                }

                { // Make one last try
                    Node parent = tdNode;
                    do
                    {
                        Node prevNode = parent;
                        do
                        {
                            prevNode = prevNode.getPreviousSibling();
                            if (prevNode == null)
                                break;
                            if (prevNode.getNodeType() != Node.ELEMENT_NODE)
                                continue;
                            NodeList imgElements = ((Element) prevNode).getElementsByTagName("img");
                            if (imgElements.getLength() > 0)
                            {
                                Element imgElement = (Element) imgElements.item(0);
                                elementLabel = imgElement.getAttribute("alt");
                            }
                        }
                        while (prevNode != null && elementLabel.length() == 0);
                        parent = parent.getParentNode();
                    }
                    while (parent != null && !parent.getNodeName().equals("form") &&
                        elementLabel.length() == 0);
                    if (elementLabel.trim().length() > 0)
                        return StringUtilities.cleanText(elementLabel);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return StringUtilities.cleanText(elementLabel);
    }

    // Finds all the orphans INPUT, SELECT and TEXTAREA elements that where left outside a form
    // due to parsing errors and puts them in the nearest form
    public static void resolveOrphansINPUTElements(ArrayList<FORMElementGui> formElementsList,
        Document doc, URL url)
    {
        // Find all the orphans INPUT elements
        ArrayList<Element> inputs = new ArrayList<Element>();
        NodeList inputElements = doc.getElementsByTagName("input");
        for (int i = 0; i < inputElements.getLength(); i++)
            inputs.add((Element) inputElements.item(i));
        NodeList selectElements = doc.getElementsByTagName("select");
        for (int i = 0; i < selectElements.getLength(); i++)
            inputs.add((Element) selectElements.item(i));
        NodeList textareaElements = doc.getElementsByTagName("textarea");
        for (int i = 0; i < textareaElements.getLength(); i++)
            inputs.add((Element) textareaElements.item(i));

        for (Iterator<Element> i = inputs.iterator(); i.hasNext();)
        {
            Element inputElement = (Element) i.next();
            if (!DOMUtilities.isNodeChildOfTag(inputElement, "form"))
            {
                Element formElement = DOMUtilities.findNearestTagToNode(inputElement, "form");
                if (formElement != null)
                {
                    FORMElementGui form = null;
                    for (Iterator<FORMElementGui> j = formElementsList.iterator(); j.hasNext();)
                    {
                        FORMElementGui aForm = (FORMElementGui) j.next();
                        if (aForm.getName().equalsIgnoreCase(formElement.getAttribute("name")))
                        {
                            form = aForm;
                            break;
                        }
                    }
                    if (form != null)
                    {
                        INPUTElementGui input = processINPUTElement(inputElement, doc, url);
                        form.addInput(input);
                        form.flat();
                    }
                }
            }
        }
    }

    public static String cleanHTMLEntities(String text)
    {
        text = StringUtilities.replace(text, "&nbsp;", "");
        text = StringUtilities.replace(text, "&amp;", "&");
        text = StringUtilities.replace(text, "&quot;", "\"");
        return text;
    }

    public static ArrayList<Object> extractFormsFromTree(DefaultMutableTreeNode root)
    {
        ArrayList<Object> forms = new ArrayList<Object>();
        if (root.getUserObject() instanceof FORMElementGui)
            forms.add(root.getUserObject());
        else
            for (int i = 0; i < root.getChildCount(); i++)
            {
                DefaultMutableTreeNode child = (DefaultMutableTreeNode) root.getChildAt(i);
                forms.addAll(extractFormsFromTree(child));
            }
        return forms;
    }
}