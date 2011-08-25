package ac.technion.iem.ontobuilder.extraction.webform;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ac.technion.iem.ontobuilder.core.ontology.Attribute;
import ac.technion.iem.ontobuilder.core.ontology.Axiom;
import ac.technion.iem.ontobuilder.core.ontology.Domain;
import ac.technion.iem.ontobuilder.core.ontology.DomainEntry;
import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.OntologyClass;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.core.utils.dom.DOMUtilities;
import ac.technion.iem.ontobuilder.core.utils.graphs.Graph;
import ac.technion.iem.ontobuilder.core.utils.graphs.GraphCell;
import ac.technion.iem.ontobuilder.core.utils.properties.PropertiesHandler;
import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.ButtonINPUTElement;
import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.CheckboxINPUTElement;
import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.CheckboxINPUTElementOption;
import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.FORMElement;
import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.FileINPUTElement;
import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.HTMLUtilities;
import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.HiddenINPUTElement;
import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.INPUTElement;
import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.ImageINPUTElement;
import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.OPTIONElement;
import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.PasswordINPUTElement;
import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.RadioINPUTElement;
import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.RadioINPUTElementOption;
import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.ResetINPUTElement;
import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.SELECTElement;
import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.SubmitINPUTElement;
import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.TEXTAREAElement;
import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.TextINPUTElement;

/**
 * Ontology extractor from a web form
 *
 * @author Y.A
 * @version 2.2
 */
public class WebFormToOntology
{
	private URL url;
	private String ontologyName;
	private String ontologyTitle;
	private Document document;
	private ArrayList<FORMElement> formsToExtract;
	
	/**
	 * Constructs a WebFormToOntology
	 *
	 * @param url URL with the forms from which to extract the ontology
	 * @param formsToExtract specific FORM elements to extract from the URL, 
	 * all other forms will be ignored 
	 */
	public WebFormToOntology(URL url, ArrayList<FORMElement> formsToExtract)
	{
		this.url = url;
		this.formsToExtract = formsToExtract;
	}
	
	/**
     * Constructs a WebFormToOntology
     *
     * @param url URL with the forms from which to extract the ontology
     */
	public WebFormToOntology(URL url)
	{
		this(url,null);
	}
	
	private void init() throws IOException
	{
		document = DOMUtilities.getDOM(url, new PrintWriter(new StringWriter()));

        ontologyTitle = "";
        ontologyName = url.getHost();

        NodeList titles = document.getElementsByTagName("title");
        for (int i = 0; i < titles.getLength(); i++)
        {
            Node titleNode = (((org.w3c.dom.Element) titles.item(i)).getFirstChild());
            if (titleNode != null)
            {
                ontologyTitle = titleNode.getNodeValue();
                break;
            }
        }
	}

	/**
	 * Generate an ontology from the URL 
	 *
	 * @return the generated ontology
	 * @throws IOException
	 */
	public Ontology generateOntology() throws IOException
    {
		init();
        Ontology ontology = new Ontology(ontologyName, ontologyTitle);
        ontology.setSiteURL(url);

        // Predefined domains
        Domain formMethodDomain = new Domain(
            PropertiesHandler.getResourceString("ontology.domain.choice"), "choice");
        formMethodDomain.addEntry(new DomainEntry("post"));
        formMethodDomain.addEntry(new DomainEntry("get"));

        // Classes
        OntologyClass pageClass = new OntologyClass("page");
        ontology.addClass(pageClass);
        OntologyClass formClass = new OntologyClass("form");
        formClass.addAttribute(new Attribute("method", "get", formMethodDomain));
        formClass.addAttribute(new Attribute("action", null, new Domain(PropertiesHandler
            .getResourceString("ontology.domain.url"), "url")));
        ontology.addClass(formClass);
        OntologyClass inputClass = new OntologyClass("input");
        inputClass.addAttribute(new Attribute("name", null, new Domain(PropertiesHandler
            .getResourceString("ontology.domain.text"), "text")));
        inputClass.addAttribute(new Attribute("disabled", Boolean.FALSE, new Domain(
        	PropertiesHandler.getResourceString("ontology.domain.boolean"), "boolean")));
        ontology.addClass(inputClass);

        // Text Class
        OntologyClass textInputClass = new OntologyClass(inputClass, "text");
        textInputClass.addAttribute(new Attribute("type", "text"));
        textInputClass.addAttribute(new Attribute("defaultValue", null, new Domain(
        	PropertiesHandler.getResourceString("ontology.domain.text"), "text")));
        textInputClass.addAttribute(new Attribute("value", null, new Domain(PropertiesHandler
            .getResourceString("ontology.domain.text"), "text")));
        textInputClass.addAttribute(new Attribute("size", null, new Domain(PropertiesHandler
            .getResourceString("ontology.domain.pinteger"), "pinteger")));
        textInputClass.addAttribute(new Attribute("maxLength", null, new Domain(
            PropertiesHandler.getResourceString("ontology.domain.pinteger"), "pinteger")));
        textInputClass.addAttribute(new Attribute("readOnly", Boolean.FALSE, new Domain(
            PropertiesHandler.getResourceString("ontology.domain.boolean"), "boolean")));

        // Password Class
        OntologyClass passwordInputClass = new OntologyClass(inputClass, "password");
        passwordInputClass.addAttribute(new Attribute("type", "password"));
        passwordInputClass.addAttribute(new Attribute("defaultValue", null, new Domain(
            PropertiesHandler.getResourceString("ontology.domain.text"), "text")));
        passwordInputClass.addAttribute(new Attribute("size", null, new Domain(PropertiesHandler
            .getResourceString("ontology.domain.pinteger"), "text")));
        passwordInputClass.addAttribute(new Attribute("maxLength", null, new Domain(
            PropertiesHandler.getResourceString("ontology.domain.pinteger"), "pinteger")));
        passwordInputClass.addAttribute(new Attribute("readOnly", Boolean.FALSE, new Domain(
            PropertiesHandler.getResourceString("ontology.domain.boolean"), "boolean")));

        // File Class
        OntologyClass fileInputClass = new OntologyClass(inputClass, "file");
        fileInputClass.addAttribute(new Attribute("type", "file"));
        fileInputClass.addAttribute(new Attribute("size", null, new Domain(PropertiesHandler
            .getResourceString("ontology.domain.pinteger"), "pinteger")));
        fileInputClass.addAttribute(new Attribute("maxLength", null, new Domain(
            PropertiesHandler.getResourceString("ontology.domain.pinteger"), "pinteger")));
        fileInputClass.addAttribute(new Attribute("readOnly", Boolean.FALSE, new Domain(
            PropertiesHandler.getResourceString("ontology.domain.boolean"), "boolean")));

        // Hidden Class
        OntologyClass hiddenInputClass = new OntologyClass(inputClass, "hidden");
        hiddenInputClass.addAttribute(new Attribute("type", "hidden"));

        // Checkbox Class
        OntologyClass checkboxInputClass = new OntologyClass(inputClass, "checkbox");
        checkboxInputClass.addAttribute(new Attribute("type", "checkbox"));

        // Radio Class
        OntologyClass radioInputClass = new OntologyClass(inputClass, "radio");
        radioInputClass.addAttribute(new Attribute("type", "radio"));

        // Select Class
        OntologyClass selectInputClass = new OntologyClass(inputClass, "select");
        selectInputClass.addAttribute(new Attribute("type", "select"));
        selectInputClass.addAttribute(new Attribute("size", null, new Domain(PropertiesHandler
            .getResourceString("ontology.domain.pinteger"), "pinteger")));
        selectInputClass.addAttribute(new Attribute("multiple", Boolean.FALSE, new Domain(
            PropertiesHandler.getResourceString("ontology.domain.boolean"), "boolean")));

        // Textarea Class
        OntologyClass textareaInputClass = new OntologyClass(inputClass, "textarea");
        textareaInputClass.addAttribute(new Attribute("type", "textarea"));
        textareaInputClass.addAttribute(new Attribute("defaultValue", null, new Domain(
            PropertiesHandler.getResourceString("ontology.domain.text"), "text")));
        textareaInputClass.addAttribute(new Attribute("rows", null, new Domain(PropertiesHandler
            .getResourceString("ontology.domain.pinteger"), "pinteger")));
        textareaInputClass.addAttribute(new Attribute("cols", null, new Domain(PropertiesHandler
            .getResourceString("ontology.domain.pinteger"), "pinteger")));
        textareaInputClass.addAttribute(new Attribute("readOnly", Boolean.FALSE, new Domain(
            PropertiesHandler.getResourceString("ontology.domain.boolean"), "boolean")));

        // Button Class
        OntologyClass buttonInputClass = new OntologyClass(inputClass, "button");
        hiddenInputClass.addAttribute(new Attribute("type", "button"));

        // Submit Class
        OntologyClass submitInputClass = new OntologyClass(inputClass, "submit");
        submitInputClass.addAttribute(new Attribute("type", "submit"));

        // Reset Class
        OntologyClass resetInputClass = new OntologyClass(inputClass, "reset");
        resetInputClass.addAttribute(new Attribute("type", "reset"));

        // Image Class
        OntologyClass imageInputClass = new OntologyClass(inputClass, "image");
        imageInputClass.addAttribute(new Attribute("type", "image"));
        imageInputClass.addAttribute(new Attribute("src", null, new Domain(PropertiesHandler
            .getResourceString("ontology.domain.url"), "url")));
        
        ArrayList<FORMElement> forms;
        if (formsToExtract == null)
        {
	        Graph elementsTree = HTMLUtilities.getFORMElementsHierarchy(document, url);
	        GraphCell root = elementsTree.getRootCells().iterator().next();
	        forms = HTMLUtilities.extractFormsFromTree(root);
        }
        else
        {
        	forms = formsToExtract;
        }

        Term pageTerm = new Term(pageClass, url.toExternalForm());
        ontology.addTerm(pageTerm);
        
        Term prevFormTerm = null;
        for (FORMElement form : forms)
        {
            Term formTerm = new Term(formClass, form.getName());
            if (prevFormTerm != null)
            {
                prevFormTerm.setSucceed(formTerm);
                formTerm.setPrecede(prevFormTerm);
            }
            prevFormTerm = formTerm;
            formTerm.setAttributeValue("method", form.getMethod());
            formTerm.setAttributeValue("action", form.getAction());
            pageTerm.addTerm(formTerm);
            Term prevInputTerm = null;
            for (int k = 0; k < form.getInputsCount(); k++)
            {
                INPUTElement input = form.getInput(k);
                Term inputTerm = null;
                if (input.getInputType().equals(INPUTElement.TEXT))
                {
                    TextINPUTElement textInput = (TextINPUTElement) input;
                    inputTerm = new Term(textInputClass, textInput.getLabel(), textInput.getValue());
                    inputTerm.setAttributeValue("name", textInput.getName());
                    inputTerm.setAttributeValue("defaultValue", textInput.getDefaultValue());
                    if (textInput.getSize() != -1)
                        inputTerm.setAttributeValue("size", new Integer(textInput.getSize()));
                    if (textInput.getMaxLength() != -1)
                        inputTerm.setAttributeValue("maxLength",
                            new Integer(textInput.getMaxLength()));
                    inputTerm.setAttributeValue("readOnly", new Boolean(textInput.isReadOnly()));
                }
                else if (input.getInputType().equals(INPUTElement.PASSWORD))
                {
                    PasswordINPUTElement passwordInput = (PasswordINPUTElement) input;
                    inputTerm = new Term(passwordInputClass, passwordInput.getLabel(),
                        passwordInput.getValue());
                    inputTerm.setAttributeValue("name", passwordInput.getName());
                    inputTerm.setAttributeValue("defaultValue", passwordInput.getDefaultValue());
                    if (passwordInput.getSize() != -1)
                        inputTerm.setAttributeValue("size", new Integer(passwordInput.getSize()));
                    if (passwordInput.getMaxLength() != -1)
                        inputTerm.setAttributeValue("maxLength",
                            new Integer(passwordInput.getMaxLength()));
                    inputTerm
                        .setAttributeValue("readOnly", new Boolean(passwordInput.isReadOnly()));
                }
                else if (input.getInputType().equals(INPUTElement.FILE))
                {
                    FileINPUTElement fileInput = (FileINPUTElement) input;
                    inputTerm = new Term(fileInputClass, fileInput.getLabel(), fileInput.getValue());
                    inputTerm.setAttributeValue("name", fileInput.getName());
                    if (fileInput.getSize() != -1)
                        inputTerm.setAttributeValue("size", new Integer(fileInput.getSize()));
                    if (fileInput.getMaxLength() != -1)
                        inputTerm.setAttributeValue("maxLength",
                            new Integer(fileInput.getMaxLength()));
                    inputTerm.setAttributeValue("readOnly", new Boolean(fileInput.isReadOnly()));
                }
                else if (input.getInputType().equals(INPUTElement.HIDDEN))
                {
                    HiddenINPUTElement hiddenInput = (HiddenINPUTElement) input;
                    inputTerm = new Term(hiddenInputClass, input.getName(), hiddenInput.getValue());
                    inputTerm.setAttributeValue("name", hiddenInput.getName());
                }
                else if (input.getInputType().equals(INPUTElement.CHECKBOX))
                {
                    CheckboxINPUTElement checkboxInput = (CheckboxINPUTElement) input;
                    inputTerm = new Term(checkboxInputClass, checkboxInput.getLabel(),
                        checkboxInput.getValue());
                    inputTerm.setAttributeValue("name", checkboxInput.getName());
                    Domain checkboxDomain = new Domain(
                        PropertiesHandler.getResourceString("ontology.domain.choice"), "choice");
                    for (int o = 0; o < checkboxInput.getOptionsCount(); o++)
                    {
                        CheckboxINPUTElementOption option = checkboxInput.getOption(o);
                        Term optionTerm = new Term(option.getLabel(), option.getValue());
                        optionTerm.addAttribute(new Attribute("checked", new Boolean(option
                            .isChecked())));
                        optionTerm.addAttribute(new Attribute("defaultChecked", new Boolean(option
                            .isDefaultChecked())));
                        checkboxDomain.addEntry(new DomainEntry(optionTerm));
                    }
                    inputTerm.setDomain(checkboxDomain);
                }
                else if (input.getInputType().equals(INPUTElement.RADIO))
                {
                    RadioINPUTElement radioInput = (RadioINPUTElement) input;
                    inputTerm = new Term(radioInputClass, radioInput.getLabel(),
                        radioInput.getValue());
                    inputTerm.setAttributeValue("name", radioInput.getName());
                    Domain radioDomain = new Domain(
                        PropertiesHandler.getResourceString("ontology.domain.choice"), "choice");
                    for (int o = 0; o < radioInput.getOptionsCount(); o++)
                    {
                        RadioINPUTElementOption option = radioInput.getOption(o);
                        Term optionTerm = new Term(option.getLabel(), option.getValue());
                        optionTerm.addAttribute(new Attribute("checked", new Boolean(option
                            .isChecked())));
                        optionTerm.addAttribute(new Attribute("defaultChecked", new Boolean(option
                            .isDefaultChecked())));
                        radioDomain.addEntry(new DomainEntry(optionTerm));
                    }
                    inputTerm.setDomain(radioDomain);
                }
                else if (input.getInputType().equals(INPUTElement.SELECT))
                {
                    SELECTElement selectInput = (SELECTElement) input;
                    inputTerm = new Term(selectInputClass, selectInput.getLabel(),
                        selectInput.getValue());
                    inputTerm.setAttributeValue("name", selectInput.getName());
                    Domain selectDomain = new Domain(
                        PropertiesHandler.getResourceString("ontology.domain.choice"), "choice");
                    for (int o = 0; o < selectInput.getOptionsCount(); o++)
                    {
                        OPTIONElement option = selectInput.getOption(o);
                        Term optionTerm = new Term(option.getLabel(), option.getValue());
                        optionTerm.addAttribute(new Attribute("selected", new Boolean(option
                            .isSelected())));
                        optionTerm.addAttribute(new Attribute("defaultSelected", new Boolean(option
                            .isDefaultSelected())));
                        selectDomain.addEntry(new DomainEntry(optionTerm));
                    }
                    inputTerm.setDomain(selectDomain);
                }
                else if (input.getInputType().equals(INPUTElement.TEXTAREA))
                {
                    TEXTAREAElement textareaInput = (TEXTAREAElement) input;
                    inputTerm = new Term(textareaInputClass, textareaInput.getLabel(),
                        textareaInput.getValue());
                    inputTerm.setAttributeValue("name", textareaInput.getName());
                    inputTerm.setAttributeValue("defaultValue", textareaInput.getDefaultValue());
                    if (textareaInput.getRows() != -1)
                        inputTerm.setAttributeValue("rows", new Integer(textareaInput.getRows()));
                    if (textareaInput.getCols() != -1)
                        inputTerm.setAttributeValue("cols", new Integer(textareaInput.getCols()));
                    inputTerm
                        .setAttributeValue("readOnly", new Boolean(textareaInput.isReadOnly()));
                }
                else if (input.getInputType().equals(INPUTElement.BUTTON))
                {
                    ButtonINPUTElement buttonInput = (ButtonINPUTElement) input;
                    inputTerm = new Term(buttonInputClass, buttonInput.getValue());
                    inputTerm.setAttributeValue("name", buttonInput.getName());
                }
                else if (input.getInputType().equals(INPUTElement.SUBMIT))
                {
                    SubmitINPUTElement submitInput = (SubmitINPUTElement) input;
                    inputTerm = new Term(submitInputClass, submitInput.getValue());
                    inputTerm.setAttributeValue("name", submitInput.getName());
                }
                else if (input.getInputType().equals(INPUTElement.RESET))
                {
                    ResetINPUTElement resetInput = (ResetINPUTElement) input;
                    inputTerm = new Term(resetInputClass, resetInput.getValue());
                    inputTerm.setAttributeValue("name", resetInput.getName());
                }
                else if (input.getInputType().equals(INPUTElement.IMAGE))
                {
                    ImageINPUTElement imageInput = (ImageINPUTElement) input;
                    inputTerm = new Term(imageInputClass, imageInput.getAlt());
                    inputTerm.setAttributeValue("name", imageInput.getName());
                    inputTerm.setAttributeValue("src", imageInput.getSrc());
                }
                if (inputTerm != null)
                {
                    inputTerm.setAttributeValue("disabled", new Boolean(input.isDisabled()));
                    Hashtable<?, ?> events = input.getEvents();
                    for (Enumeration<?> e = events.keys(); e.hasMoreElements();)
                    {
                        String event = (String) e.nextElement();
                        String script = (String) events.get(event);
                        inputTerm.addAxiom(new Axiom(event, script));
                    }
                    if (prevInputTerm != null)
                    {
                        prevInputTerm.setSucceed(inputTerm);
                        inputTerm.setPrecede(prevInputTerm);
                    }
                    prevInputTerm = inputTerm;
                    formTerm.addTerm(inputTerm);
                }
            }
        }

        return ontology;
    }
	
}
