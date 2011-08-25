package ac.technion.iem.ontobuilder.gui.tools.ontowizard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
import ac.technion.iem.ontobuilder.core.utils.files.StringOutputStream;
import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.INPUTElement;
import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.application.ObjectWithProperties;
import ac.technion.iem.ontobuilder.gui.elements.MultilineLabel;
import ac.technion.iem.ontobuilder.gui.elements.TextField;
import ac.technion.iem.ontobuilder.gui.ontobuilder.elements.PropertiesPanel;
import ac.technion.iem.ontobuilder.gui.ontobuilder.main.OntoBuilder;
import ac.technion.iem.ontobuilder.gui.utils.files.html.ButtonINPUTElementGui;
import ac.technion.iem.ontobuilder.gui.utils.files.html.CheckboxINPUTElementGui;
import ac.technion.iem.ontobuilder.gui.utils.files.html.CheckboxINPUTElementOptionGui;
import ac.technion.iem.ontobuilder.gui.utils.files.html.FORMElementGui;
import ac.technion.iem.ontobuilder.gui.utils.files.html.FileINPUTElementGui;
import ac.technion.iem.ontobuilder.gui.utils.files.html.HTMLUtilitiesGui;
import ac.technion.iem.ontobuilder.gui.utils.files.html.HiddenINPUTElementGui;
import ac.technion.iem.ontobuilder.gui.utils.files.html.INPUTElementGui;
import ac.technion.iem.ontobuilder.gui.utils.files.html.ImageINPUTElementGui;
import ac.technion.iem.ontobuilder.gui.utils.files.html.OPTIONElementGui;
import ac.technion.iem.ontobuilder.gui.utils.files.html.PasswordINPUTElementGui;
import ac.technion.iem.ontobuilder.gui.utils.files.html.RadioINPUTElementGui;
import ac.technion.iem.ontobuilder.gui.utils.files.html.RadioINPUTElementOptionGui;
import ac.technion.iem.ontobuilder.gui.utils.files.html.ResetINPUTElementGui;
import ac.technion.iem.ontobuilder.gui.utils.files.html.SELECTElementGui;
import ac.technion.iem.ontobuilder.gui.utils.files.html.SubmitINPUTElementGui;
import ac.technion.iem.ontobuilder.gui.utils.files.html.TEXTAREAElementGui;
import ac.technion.iem.ontobuilder.gui.utils.files.html.TextINPUTElementGui;

/**
 * <p>Title: OntologyWizard</p>
 */
public class OntologyWizard
{
    private JFrame parent;
    protected URL startURL;
    protected Document document;
    protected String ontologyName;
    protected String ontologyTitle;
    protected ArrayList<DialogInformation> forms;

    OntoBuilder ontoBuilder;

    /**
     * Constructs a OntologyWizard
     *
     * @param parent the parent {@link JFrame}
     * @param startURL the {@link URL} to start from
     * @param document the {@link Document} to read
     * @param ontoBuilder the {@link OntoBuilder} application
     */
    public OntologyWizard(JFrame parent, URL startURL, Document document, OntoBuilder ontoBuilder)
    {
        this.ontoBuilder = ontoBuilder;
        this.parent = parent;
        this.startURL = startURL;
        this.document = document;
        NodeList titles = document.getElementsByTagName("title");
        for (int i = 0; i < titles.getLength(); i++)
        {
            Node titleNode = (((Element) titles.item(i)).getFirstChild());
            if (titleNode != null)
            {
                ontologyTitle = titleNode.getNodeValue();
                break;
            }
        }
        ontologyName = startURL.getHost();
        forms = new ArrayList<DialogInformation>();
    }

    /**
     * Start the ontology creation
     *
     * @return an {@link Ontology}
     */
    public Ontology startOntologyCreation()
    {
        if (start().getNextAction() == WizardStatus.CANCEL_ACTION)
            return null;

        Document currentDocument = document;
        URL currentURL = startURL;
        String submitInfo = "";
        do
        {
            WizardStatus status = retrieveOntology(currentDocument, currentURL, submitInfo);
            if (status.getNextAction() == WizardStatus.CANCEL_ACTION)
                return null;
            if (status.getNextAction() == WizardStatus.BACK_ACTION)
            {
                if (forms.isEmpty())
                {
                    if (start().getNextAction() == WizardStatus.CANCEL_ACTION)
                        return null;
                    currentDocument = document;
                }
                else
                {
                    currentDocument = ((DialogInformation) forms.get(forms.size() - 1))
                        .getDocument();
                    forms.remove(forms.size() - 1);
                }
            }
            if (status.getNextAction() == WizardStatus.FINISH_ACTION)
            {
                forms.add(new DialogInformation(null, status.getForms(), currentURL));
                return createOntology();
            }
            if (status.getNextAction() == WizardStatus.NEXT_ACTION)
            {
                forms.add(new DialogInformation(currentDocument, status.getForms(), currentURL));
                FORMElementGui form = status.getForm();
                currentURL = form.getAction();
                try
                {
                    if (parent != null)
                        parent.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                    StringBuffer info = new StringBuffer();
                    InputStream stream = form.submit(info);
                    submitInfo = info.toString();
                    if (stream == null)
                        return createOntology();
                    currentDocument = DOMUtilities.getDOMfromHTML(stream, new PrintWriter(
                        new StringWriter()));
                }
                catch (IOException e)
                {
                    return createOntology();
                }
                finally
                {
                    if (parent != null)
                        parent.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            }
        }
        while (currentDocument != null);
        return createOntology();
    }

    /**
     * Create an ontology
     *
     * @return an {@link Ontology}
     */
    protected Ontology createOntology()
    {
        Ontology ontology = new Ontology(ontologyName, ontologyTitle);
        ontology.setSiteURL(startURL);

        // Predefined domains
        Domain formMethodDomain = new Domain(
            ApplicationUtilities.getResourceString("ontology.domain.choice"), "choice");
        formMethodDomain.addEntry(new DomainEntry("post"));
        formMethodDomain.addEntry(new DomainEntry("get"));

        // Classes
        OntologyClass pageClass = new OntologyClass("page");
        ontology.addClass(pageClass);
        OntologyClass formClass = new OntologyClass("form");
        formClass.addAttribute(new Attribute("method", "get", formMethodDomain));
        formClass.addAttribute(new Attribute("action", null, new Domain(ApplicationUtilities
            .getResourceString("ontology.domain.url"), "url")));
        ontology.addClass(formClass);
        OntologyClass inputClass = new OntologyClass("input");
        inputClass.addAttribute(new Attribute("name", null, new Domain(ApplicationUtilities
            .getResourceString("ontology.domain.text"), "text")));
        inputClass.addAttribute(new Attribute("disabled", Boolean.FALSE, new Domain(
            ApplicationUtilities.getResourceString("ontology.domain.boolean"), "boolean")));
        ontology.addClass(inputClass);

        // Text Class
        OntologyClass textInputClass = new OntologyClass(inputClass, "text");
        textInputClass.addAttribute(new Attribute("type", "text"));
        textInputClass.addAttribute(new Attribute("defaultValue", null, new Domain(
            ApplicationUtilities.getResourceString("ontology.domain.text"), "text")));
        textInputClass.addAttribute(new Attribute("value", null, new Domain(ApplicationUtilities
            .getResourceString("ontology.domain.text"), "text")));
        textInputClass.addAttribute(new Attribute("size", null, new Domain(ApplicationUtilities
            .getResourceString("ontology.domain.pinteger"), "pinteger")));
        textInputClass.addAttribute(new Attribute("maxLength", null, new Domain(
            ApplicationUtilities.getResourceString("ontology.domain.pinteger"), "pinteger")));
        textInputClass.addAttribute(new Attribute("readOnly", Boolean.FALSE, new Domain(
            ApplicationUtilities.getResourceString("ontology.domain.boolean"), "boolean")));

        // Password Class
        OntologyClass passwordInputClass = new OntologyClass(inputClass, "password");
        passwordInputClass.addAttribute(new Attribute("type", "password"));
        passwordInputClass.addAttribute(new Attribute("defaultValue", null, new Domain(
            ApplicationUtilities.getResourceString("ontology.domain.text"), "text")));
        passwordInputClass.addAttribute(new Attribute("size", null, new Domain(ApplicationUtilities
            .getResourceString("ontology.domain.pinteger"), "text")));
        passwordInputClass.addAttribute(new Attribute("maxLength", null, new Domain(
            ApplicationUtilities.getResourceString("ontology.domain.pinteger"), "pinteger")));
        passwordInputClass.addAttribute(new Attribute("readOnly", Boolean.FALSE, new Domain(
            ApplicationUtilities.getResourceString("ontology.domain.boolean"), "boolean")));

        // File Class
        OntologyClass fileInputClass = new OntologyClass(inputClass, "file");
        fileInputClass.addAttribute(new Attribute("type", "file"));
        fileInputClass.addAttribute(new Attribute("size", null, new Domain(ApplicationUtilities
            .getResourceString("ontology.domain.pinteger"), "pinteger")));
        fileInputClass.addAttribute(new Attribute("maxLength", null, new Domain(
            ApplicationUtilities.getResourceString("ontology.domain.pinteger"), "pinteger")));
        fileInputClass.addAttribute(new Attribute("readOnly", Boolean.FALSE, new Domain(
            ApplicationUtilities.getResourceString("ontology.domain.boolean"), "boolean")));

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
        selectInputClass.addAttribute(new Attribute("size", null, new Domain(ApplicationUtilities
            .getResourceString("ontology.domain.pinteger"), "pinteger")));
        selectInputClass.addAttribute(new Attribute("multiple", Boolean.FALSE, new Domain(
            ApplicationUtilities.getResourceString("ontology.domain.boolean"), "boolean")));

        // Textarea Class
        OntologyClass textareaInputClass = new OntologyClass(inputClass, "textarea");
        textareaInputClass.addAttribute(new Attribute("type", "textarea"));
        textareaInputClass.addAttribute(new Attribute("defaultValue", null, new Domain(
            ApplicationUtilities.getResourceString("ontology.domain.text"), "text")));
        textareaInputClass.addAttribute(new Attribute("rows", null, new Domain(ApplicationUtilities
            .getResourceString("ontology.domain.pinteger"), "pinteger")));
        textareaInputClass.addAttribute(new Attribute("cols", null, new Domain(ApplicationUtilities
            .getResourceString("ontology.domain.pinteger"), "pinteger")));
        textareaInputClass.addAttribute(new Attribute("readOnly", Boolean.FALSE, new Domain(
            ApplicationUtilities.getResourceString("ontology.domain.boolean"), "boolean")));

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
        imageInputClass.addAttribute(new Attribute("src", null, new Domain(ApplicationUtilities
            .getResourceString("ontology.domain.url"), "url")));

        Term prevPageTerm = null;
        for (Iterator<DialogInformation> i = forms.iterator(); i.hasNext();)
        {
            DialogInformation dialog = (DialogInformation) i.next();
            Term pageTerm = new Term(pageClass, dialog.getURL().toExternalForm());
            if (prevPageTerm != null)
            {
                prevPageTerm.setSucceed(pageTerm);
                pageTerm.setPrecede(prevPageTerm);
            }
            prevPageTerm = pageTerm;
            ontology.addTerm(pageTerm);
            ArrayList<?> f = dialog.getForms();
            Term prevFormTerm = null;
            for (Iterator<?> j = f.iterator(); j.hasNext();)
            {
                FORMElementGui form = (FORMElementGui) j.next();
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
                    INPUTElementGui input = form.getInput(k);
                    Term inputTerm = null;
                    if (input.getInputType().equals(INPUTElement.TEXT))
                    {
                        TextINPUTElementGui textInput = (TextINPUTElementGui) input;
                        inputTerm = new Term(textInputClass, textInput.getLabel(),
                            textInput.getValue());
                        inputTerm.setAttributeValue("name", textInput.getName());
                        inputTerm.setAttributeValue("defaultValue", textInput.getDefaultValue());
                        if (textInput.getSize() != -1)
                            inputTerm.setAttributeValue("size", new Integer(textInput.getSize()));
                        if (textInput.getMaxLength() != -1)
                            inputTerm.setAttributeValue("maxLength",
                                new Integer(textInput.getMaxLength()));
                        inputTerm
                            .setAttributeValue("readOnly", new Boolean(textInput.isReadOnly()));
                    }
                    else if (input.getInputType().equals(INPUTElement.PASSWORD))
                    {
                        PasswordINPUTElementGui passwordInput = (PasswordINPUTElementGui) input;
                        inputTerm = new Term(passwordInputClass, passwordInput.getLabel(),
                            passwordInput.getValue());
                        inputTerm.setAttributeValue("name", passwordInput.getName());
                        inputTerm
                            .setAttributeValue("defaultValue", passwordInput.getDefaultValue());
                        if (passwordInput.getSize() != -1)
                            inputTerm.setAttributeValue("size",
                                new Integer(passwordInput.getSize()));
                        if (passwordInput.getMaxLength() != -1)
                            inputTerm.setAttributeValue("maxLength",
                                new Integer(passwordInput.getMaxLength()));
                        inputTerm.setAttributeValue("readOnly",
                            new Boolean(passwordInput.isReadOnly()));
                    }
                    else if (input.getInputType().equals(INPUTElement.FILE))
                    {
                        FileINPUTElementGui fileInput = (FileINPUTElementGui) input;
                        inputTerm = new Term(fileInputClass, fileInput.getLabel(),
                            fileInput.getValue());
                        inputTerm.setAttributeValue("name", fileInput.getName());
                        if (fileInput.getSize() != -1)
                            inputTerm.setAttributeValue("size", new Integer(fileInput.getSize()));
                        if (fileInput.getMaxLength() != -1)
                            inputTerm.setAttributeValue("maxLength",
                                new Integer(fileInput.getMaxLength()));
                        inputTerm
                            .setAttributeValue("readOnly", new Boolean(fileInput.isReadOnly()));
                    }
                    else if (input.getInputType().equals(INPUTElement.HIDDEN))
                    {
                        HiddenINPUTElementGui hiddenInput = (HiddenINPUTElementGui) input;
                        inputTerm = new Term(hiddenInputClass, input.getName(),
                            hiddenInput.getValue());
                        inputTerm.setAttributeValue("name", hiddenInput.getName());
                    }
                    else if (input.getInputType().equals(INPUTElement.CHECKBOX))
                    {
                        CheckboxINPUTElementGui checkboxInput = (CheckboxINPUTElementGui) input;
                        inputTerm = new Term(checkboxInputClass, checkboxInput.getLabel(),
                            checkboxInput.getValue());
                        inputTerm.setAttributeValue("name", checkboxInput.getName());
                        Domain checkboxDomain = new Domain(
                            ApplicationUtilities.getResourceString("ontology.domain.choice"),
                            "choice");
                        for (int o = 0; o < checkboxInput.getOptionsCount(); o++)
                        {
                            CheckboxINPUTElementOptionGui option = checkboxInput.getOption(o);
                            Term optionTerm = new Term(option.getLabel(), option.getValue());
                            optionTerm.addAttribute(new Attribute("checked", new Boolean(option
                                .isChecked())));
                            optionTerm.addAttribute(new Attribute("defaultChecked", new Boolean(
                                option.isDefaultChecked())));
                            checkboxDomain.addEntry(new DomainEntry(optionTerm));
                        }
                        inputTerm.setDomain(checkboxDomain);
                    }
                    else if (input.getInputType().equals(INPUTElement.RADIO))
                    {
                        RadioINPUTElementGui radioInput = (RadioINPUTElementGui) input;
                        inputTerm = new Term(radioInputClass, radioInput.getLabel(),
                            radioInput.getValue());
                        inputTerm.setAttributeValue("name", radioInput.getName());
                        Domain radioDomain = new Domain(
                            ApplicationUtilities.getResourceString("ontology.domain.choice"),
                            "choice");
                        for (int o = 0; o < radioInput.getOptionsCount(); o++)
                        {
                            RadioINPUTElementOptionGui option = radioInput.getOption(o);
                            Term optionTerm = new Term(option.getLabel(), option.getValue());
                            optionTerm.addAttribute(new Attribute("checked", new Boolean(option
                                .isChecked())));
                            optionTerm.addAttribute(new Attribute("defaultChecked", new Boolean(
                                option.isDefaultChecked())));
                            radioDomain.addEntry(new DomainEntry(optionTerm));
                        }
                        inputTerm.setDomain(radioDomain);
                    }
                    else if (input.getInputType().equals(INPUTElement.SELECT))
                    {
                        SELECTElementGui selectInput = (SELECTElementGui) input;
                        inputTerm = new Term(selectInputClass, selectInput.getLabel(),
                            selectInput.getValue());
                        inputTerm.setAttributeValue("name", selectInput.getName());
                        Domain selectDomain = new Domain(
                            ApplicationUtilities.getResourceString("ontology.domain.choice"),
                            "choice");
                        for (int o = 0; o < selectInput.getOptionsCount(); o++)
                        {
                            OPTIONElementGui option = selectInput.getOption(o);
                            Term optionTerm = new Term(option.getLabel(), option.getValue());
                            optionTerm.addAttribute(new Attribute("selected", new Boolean(option
                                .isSelected())));
                            optionTerm.addAttribute(new Attribute("defaultSelected", new Boolean(
                                option.isDefaultSelected())));
                            selectDomain.addEntry(new DomainEntry(optionTerm));
                        }
                        inputTerm.setDomain(selectDomain);
                    }
                    else if (input.getInputType().equals(INPUTElement.TEXTAREA))
                    {
                        TEXTAREAElementGui textareaInput = (TEXTAREAElementGui) input;
                        inputTerm = new Term(textareaInputClass, textareaInput.getLabel(),
                            textareaInput.getValue());
                        inputTerm.setAttributeValue("name", textareaInput.getName());
                        inputTerm
                            .setAttributeValue("defaultValue", textareaInput.getDefaultValue());
                        if (textareaInput.getRows() != -1)
                            inputTerm.setAttributeValue("rows",
                                new Integer(textareaInput.getRows()));
                        if (textareaInput.getCols() != -1)
                            inputTerm.setAttributeValue("cols",
                                new Integer(textareaInput.getCols()));
                        inputTerm.setAttributeValue("readOnly",
                            new Boolean(textareaInput.isReadOnly()));
                    }
                    else if (input.getInputType().equals(INPUTElement.BUTTON))
                    {
                        ButtonINPUTElementGui buttonInput = (ButtonINPUTElementGui) input;
                        inputTerm = new Term(buttonInputClass, buttonInput.getValue());
                        inputTerm.setAttributeValue("name", buttonInput.getName());
                    }
                    else if (input.getInputType().equals(INPUTElement.SUBMIT))
                    {
                        SubmitINPUTElementGui submitInput = (SubmitINPUTElementGui) input;
                        inputTerm = new Term(submitInputClass, submitInput.getValue());
                        inputTerm.setAttributeValue("name", submitInput.getName());
                    }
                    else if (input.getInputType().equals(INPUTElement.RESET))
                    {
                        ResetINPUTElementGui resetInput = (ResetINPUTElementGui) input;
                        inputTerm = new Term(resetInputClass, resetInput.getValue());
                        inputTerm.setAttributeValue("name", resetInput.getName());
                    }
                    else if (input.getInputType().equals(INPUTElement.IMAGE))
                    {
                        ImageINPUTElementGui imageInput = (ImageINPUTElementGui) input;
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
        }
        return ontology;
    }

    /**
     * Start the ontology wizard
     *
     * @return a {@link WizardStatus}
     */
    public WizardStatus start()
    {
        final WizardStatus status = new WizardStatus();
        final TextField txtOntologyName = new TextField(ontologyName);
        final TextField txtOntologyTitle = new TextField(
            ontologyTitle);

        final JDialog startDialog = new JDialog(parent,
            ApplicationUtilities.getResourceString("ontowizard.windowTitle"), true);
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        startDialog.setSize(new Dimension(ApplicationUtilities
            .getIntProperty("ontowizard.start.width"), ApplicationUtilities
            .getIntProperty("ontowizard.start.height")));
        startDialog.setLocationRelativeTo(parent);
        startDialog.setResizable(false);

        JPanel south = new JPanel();
        south.setLayout(new FlowLayout(FlowLayout.RIGHT));
        south.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.black),
            BorderFactory.createEmptyBorder(0, 10, 10, 10)));
        final JButton nextButton;
        south.add(nextButton = new JButton(ApplicationUtilities
            .getResourceString("ontowizard.button.next")));
        startDialog.getRootPane().setDefaultButton(nextButton);
        nextButton.setEnabled(txtOntologyTitle.getText().trim().length() > 0 &&
            txtOntologyName.getText().trim().length() > 0);
        nextButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                ontologyName = txtOntologyName.getText();
                ontologyTitle = txtOntologyTitle.getText();
                startDialog.dispose();
                status.setNextAction(WizardStatus.NEXT_ACTION);
            }
        });
        JButton cancelButton;
        south.add(cancelButton = new JButton(ApplicationUtilities
            .getResourceString("ontowizard.button.cancel")));
        cancelButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                startDialog.dispose();
                status.setNextAction(WizardStatus.CANCEL_ACTION);
            }
        });
        panel.add(BorderLayout.SOUTH, south);

        JLabel west = new JLabel(ApplicationUtilities.getImage("ontowizardbanner.gif"));
        west.setBorder(BorderFactory.createLineBorder(Color.black));
        panel.add(BorderLayout.WEST, west);

        JPanel center = new JPanel(new GridBagLayout());
        panel.add(BorderLayout.CENTER, center);
        center.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        {// Title
            JLabel title = new JLabel(ApplicationUtilities.getResourceString("ontowizard.title"),
                JLabel.LEFT);
            if (parent != null)
                title.setFont(new Font(startDialog.getFont().getFontName(), Font.BOLD, startDialog
                    .getFont().getSize() + 6));
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.weightx = 1;
            gbcl.gridwidth = 2;
            gbcl.fill = GridBagConstraints.HORIZONTAL;
            gbcl.insets = new Insets(0, 0, 10, 0);
            gbcl.anchor = GridBagConstraints.NORTHWEST;
            center.add(title, gbcl);
        }

        {// Explanation
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = 1;
            gbcl.gridwidth = 2;
            gbcl.weightx = 1;
            gbcl.fill = GridBagConstraints.HORIZONTAL;
            gbcl.insets = new Insets(0, 0, 20, 0);
            gbcl.anchor = GridBagConstraints.WEST;
            center
                .add(
                    new MultilineLabel(ApplicationUtilities
                        .getResourceString("ontowizard.explanation")), gbcl);
        }

        {// Required fields
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = 2;
            gbcl.gridwidth = 2;
            gbcl.weightx = 1;
            gbcl.fill = GridBagConstraints.HORIZONTAL;
            gbcl.insets = new Insets(0, 0, 10, 0);
            gbcl.anchor = GridBagConstraints.WEST;
            center.add(
                new MultilineLabel(ApplicationUtilities
                    .getResourceString("ontowizard.requiredFields")), gbcl);
        }

        {// Title
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = 3;
            gbcl.insets = new Insets(0, 0, 20, 5);
            gbcl.anchor = GridBagConstraints.EAST;
            JLabel title = new JLabel(
                ApplicationUtilities.getResourceString("ontowizard.ontology.title") + ":");
            title
                .setFont(new Font(title.getFont().getName(), Font.BOLD, title.getFont().getSize()));
            center.add(title, gbcl);

            gbcl.gridx = 1;
            gbcl.weightx = 1;
            gbcl.fill = GridBagConstraints.HORIZONTAL;
            center.add(txtOntologyTitle, gbcl);
            txtOntologyTitle.addKeyListener(new KeyAdapter()
            {
                public void keyTyped(KeyEvent event)
                {
                    SwingUtilities.invokeLater(new Runnable()
                    {
                        public void run()
                        {
                            if (!txtOntologyTitle.getText().trim().equals("") &&
                                !txtOntologyName.getText().trim().equals(""))
                                nextButton.setEnabled(true);
                            else
                                nextButton.setEnabled(false);
                        }
                    });
                }
            });
        }

        {// Name
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = 4;
            gbcl.insets = new Insets(0, 0, 20, 5);
            gbcl.anchor = GridBagConstraints.EAST;
            JLabel name = new JLabel(
                ApplicationUtilities.getResourceString("ontowizard.ontology.name") + ":");
            name.setFont(new Font(name.getFont().getName(), Font.BOLD, name.getFont().getSize()));
            center.add(name, gbcl);

            gbcl.gridx = 1;
            gbcl.weightx = 1;
            gbcl.fill = GridBagConstraints.HORIZONTAL;
            center.add(txtOntologyName, gbcl);
            txtOntologyName.addKeyListener(new KeyAdapter()
            {
                public void keyPressed(KeyEvent event)
                {
                    SwingUtilities.invokeLater(new Runnable()
                    {
                        public void run()
                        {
                            if (!txtOntologyTitle.getText().trim().equals("") &&
                                !txtOntologyName.getText().trim().equals(""))
                                nextButton.setEnabled(true);
                            else
                                nextButton.setEnabled(false);
                        }
                    });
                }
            });
        }

        {// Separator
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridy = 5;
            gbc.gridwidth = 2;
            gbc.gridheight = GridBagConstraints.REMAINDER;
            gbc.fill = GridBagConstraints.VERTICAL;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            center.add(new JPanel(), gbc);
        }

        startDialog.addWindowListener(new WindowAdapter()
        {
            public void windowOpened(WindowEvent e)
            {
                SwingUtilities.invokeLater(new Runnable()
                {
                    public void run()
                    {
                        txtOntologyName.requestFocus();
                    }
                });
            }

            public void windowClosing(WindowEvent e)
            {
                startDialog.dispose();
                status.setNextAction(WizardStatus.CANCEL_ACTION);
            }
        });
        startDialog.setContentPane(panel);

        startDialog.setVisible(true);// show();
        return status;
    }

    /**
     * Retrieve an ontology
     *
     * @param document the {@link Document} to read
     * @param startURL the {@link URL} to start from
     * @param submitInfo the submit information
     * @return a {@link WizardStatus}
     */
    public WizardStatus retrieveOntology(Document doc, URL url, String submitInfo)
    {
        final PropertiesPanel propertiesPanel = new PropertiesPanel();
        final WizardStatus status = new WizardStatus();

        final JDialog formDialog = new JDialog(parent,
            ApplicationUtilities.getResourceString("ontowizard.windowTitle"), true);
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        formDialog.setSize(new Dimension(ApplicationUtilities
            .getIntProperty("ontowizard.form.width"), ApplicationUtilities
            .getIntProperty("ontowizard.form.height")));
        formDialog.setLocationRelativeTo(parent);
        formDialog.setResizable(false);

        JPanel north = new JPanel(new GridBagLayout());
        panel.add(BorderLayout.NORTH, north);
        north.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));

        {// Title
            JLabel title = new JLabel(
                ApplicationUtilities.getResourceString("ontowizard.form.title"),
                ApplicationUtilities.getImage("elements.gif"), JLabel.LEFT);
            if (parent != null)
                title.setFont(new Font(formDialog.getFont().getFontName(), Font.BOLD, formDialog
                    .getFont().getSize() + 4));
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.weightx = 1;
            gbcl.gridwidth = 2;
            gbcl.fill = GridBagConstraints.HORIZONTAL;
            gbcl.insets = new Insets(0, 0, 10, 0);
            gbcl.anchor = GridBagConstraints.NORTHWEST;
            north.add(title, gbcl);
        }

        {// Explanation
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = 1;
            gbcl.gridwidth = 2;
            gbcl.weightx = 1;
            gbcl.fill = GridBagConstraints.HORIZONTAL;
            gbcl.anchor = GridBagConstraints.WEST;
            north.add(
                new MultilineLabel(ApplicationUtilities
                    .getResourceString("ontowizard.form.explanation")), gbcl);
        }

        JPanel south = new JPanel();
        panel.add(BorderLayout.SOUTH, south);
        south.setLayout(new FlowLayout(FlowLayout.RIGHT));
        south.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.black),
            BorderFactory.createEmptyBorder(0, 10, 10, 10)));
        JButton backButton;
        south.add(backButton = new JButton(ApplicationUtilities
            .getResourceString("ontowizard.button.back")));
        backButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                formDialog.dispose();
                status.setNextAction(WizardStatus.BACK_ACTION);
            }
        });
        final JButton nextButton;
        south.add(nextButton = new JButton(ApplicationUtilities
            .getResourceString("ontowizard.button.next")));
        formDialog.getRootPane().setDefaultButton(nextButton);
        nextButton.setEnabled(false);
        nextButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                formDialog.dispose();
                status.setNextAction(WizardStatus.NEXT_ACTION);
            }
        });
        JButton cancelButton;
        south.add(cancelButton = new JButton(ApplicationUtilities
            .getResourceString("ontowizard.button.cancel")));
        cancelButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                formDialog.dispose();
                status.setNextAction(WizardStatus.CANCEL_ACTION);
            }
        });
        JButton finishButton;
        south.add(finishButton = new JButton(ApplicationUtilities
            .getResourceString("ontowizard.button.finish")));
        finishButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                formDialog.dispose();
                status.setNextAction(WizardStatus.FINISH_ACTION);
            }
        });

        JPanel center = new JPanel(new BorderLayout());
        panel.add(BorderLayout.CENTER, center);
        center.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));

        JPanel left = new JPanel(new GridLayout(2, 1));
        left.setPreferredSize(new Dimension(ApplicationUtilities
            .getIntProperty("ontowizard.form.width") / 3, 0));
        center.add(BorderLayout.WEST, left);

        final JTabbedPane tabs = new JTabbedPane();

        final JPanel previewPanel = new JPanel(new BorderLayout());
        previewPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        final JEditorPane htmlPanel = new JEditorPane();
        htmlPanel.setEditable(false);
        htmlPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        htmlPanel.setContentType("text/html");
        try
        {
            StringOutputStream source = new StringOutputStream();
            DOMUtilities.prettyPrint(doc, source);
            htmlPanel.setText(source.toString());

            BufferedWriter out = new BufferedWriter(new FileWriter(
                ApplicationUtilities.getApplicationDirectory() + "lastPageSubmitted.html"));
            out.write("<!-- " + submitInfo + " -->\n\n");
            out.write(source.toString());
            out.close();
        }
        catch (IOException e)
        {
            // ignore
        }

        // HTML Elements
        JTree elementsTree = HTMLUtilitiesGui.getFORMElementsHierarchy(doc, url);
        status.setForms(HTMLUtilitiesGui.extractFormsFromTree((DefaultMutableTreeNode) elementsTree
            .getModel().getRoot()));
        elementsTree.addTreeSelectionListener(new TreeSelectionListener()
        {
            public void valueChanged(TreeSelectionEvent e)
            {
                tabs.setSelectedIndex(0);
                previewPanel.removeAll();
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) ((JTree) e.getSource())
                    .getLastSelectedPathComponent();
                if (node == null)
                    return;
                Object object = node.getUserObject();
                if (object instanceof ObjectWithProperties)
                {
                    propertiesPanel.showProperties(((ObjectWithProperties) object).getProperties());
                    if (object instanceof FORMElementGui)
                    {
                        previewPanel.add(BorderLayout.CENTER, ((FORMElementGui) object).getComponent());
                        status.setForm((FORMElementGui) object);
                    }
                }
                else
                {
                    propertiesPanel.showProperties(null);
                }
                nextButton.setEnabled(object instanceof FORMElementGui);
                previewPanel.revalidate();
            }
        });
        JScrollPane elementsTreePane = new JScrollPane(elementsTree);
        elementsTreePane.setBorder(BorderFactory.createTitledBorder(ApplicationUtilities
            .getResourceString("ontowizard.form.panel.elements")));
        left.add(elementsTreePane);

        JScrollPane propertiesPanelPane = new JScrollPane(propertiesPanel);
        propertiesPanelPane.setBorder(BorderFactory.createTitledBorder(ApplicationUtilities
            .getResourceString("ontowizard.form.panel.properties")));
        left.add(propertiesPanelPane);

        MultilineLabel submittedPanel = new MultilineLabel("This is what was submitted:\n\n" +
            submitInfo);
        submittedPanel.setLineWrap(false);
        submittedPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        tabs.setTabPlacement(JTabbedPane.BOTTOM);
        tabs.addTab(ApplicationUtilities.getResourceString("ontowizard.form.panel.formPreview"),
            ApplicationUtilities.getImage("form.gif"), new JScrollPane(previewPanel));
        tabs.addTab(ApplicationUtilities.getResourceString("ontowizard.form.panel.html"),
            ApplicationUtilities.getImage("html.gif"), new JScrollPane(htmlPanel));
        tabs.addTab(ApplicationUtilities.getResourceString("ontowizard.form.panel.submitted"),
            ApplicationUtilities.getImage("source.gif"), new JScrollPane(submittedPanel));
        center.add(BorderLayout.CENTER, tabs);

        formDialog.addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                formDialog.dispose();
                status.setNextAction(WizardStatus.CANCEL_ACTION);
            }
        });
        formDialog.setContentPane(panel);

        formDialog.setVisible(true);// show();
        return status;
    }
}