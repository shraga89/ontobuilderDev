package ac.technion.iem.ontobuilder.core.ontology;

import org.w3c.dom.Document;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.OntologyClass;
import ac.technion.iem.ontobuilder.core.ontology.Term;

/**
 * <p>Title: OntologyGenerateHelper</p>
 * <p>Description: A bean to hold all of the 'generateOngology' parameters to be used both by
 * Ontology and OntologyGui</p>
 */
public class OntologyGenerateHelper
{
    private Ontology ontology;
    private Document document;
    private OntologyClass formClass;
    private OntologyClass textInputClass;
    private OntologyClass passwordInputClass;
    private OntologyClass fileInputClass;
    private OntologyClass hiddenInputClass;
    private OntologyClass checkboxInputClass;
    private OntologyClass radioInputClass;
    private OntologyClass selectInputClass;
    private OntologyClass textareaInputClass;
    private OntologyClass buttonInputClass;
    private OntologyClass submitInputClass;
    private OntologyClass resetInputClass;
    private OntologyClass imageInputClass;
    private Term pageTerm;
    
    public Ontology getOntology()
    {
        return ontology;
    }
    public void setOntology(Ontology ontology)
    {
        this.ontology = ontology;
    }
    public Document getDocument()
    {
        return document;
    }
    public void setDocument(Document document)
    {
        this.document = document;
    }
    public OntologyClass getFormClass()
    {
        return formClass;
    }
    public void setFormClass(OntologyClass formClass)
    {
        this.formClass = formClass;
    }
    public OntologyClass getTextInputClass()
    {
        return textInputClass;
    }
    public void setTextInputClass(OntologyClass textInputClass)
    {
        this.textInputClass = textInputClass;
    }
    public OntologyClass getPasswordInputClass()
    {
        return passwordInputClass;
    }
    public void setPasswordInputClass(OntologyClass passwordInputClass)
    {
        this.passwordInputClass = passwordInputClass;
    }
    public OntologyClass getFileInputClass()
    {
        return fileInputClass;
    }
    public void setFileInputClass(OntologyClass fileInputClass)
    {
        this.fileInputClass = fileInputClass;
    }
    public OntologyClass getHiddenInputClass()
    {
        return hiddenInputClass;
    }
    public void setHiddenInputClass(OntologyClass hiddenInputClass)
    {
        this.hiddenInputClass = hiddenInputClass;
    }
    public OntologyClass getCheckboxInputClass()
    {
        return checkboxInputClass;
    }
    public void setCheckboxInputClass(OntologyClass checkboxInputClass)
    {
        this.checkboxInputClass = checkboxInputClass;
    }
    public OntologyClass getRadioInputClass()
    {
        return radioInputClass;
    }
    public void setRadioInputClass(OntologyClass radioInputClass)
    {
        this.radioInputClass = radioInputClass;
    }
    public OntologyClass getSelectInputClass()
    {
        return selectInputClass;
    }
    public void setSelectInputClass(OntologyClass selectInputClass)
    {
        this.selectInputClass = selectInputClass;
    }
    public OntologyClass getTextareaInputClass()
    {
        return textareaInputClass;
    }
    public void setTextareaInputClass(OntologyClass textareaInputClass)
    {
        this.textareaInputClass = textareaInputClass;
    }
    public OntologyClass getButtonInputClass()
    {
        return buttonInputClass;
    }
    public void setButtonInputClass(OntologyClass buttonInputClass)
    {
        this.buttonInputClass = buttonInputClass;
    }
    public OntologyClass getSubmitInputClass()
    {
        return submitInputClass;
    }
    public void setSubmitInputClass(OntologyClass submitInputClass)
    {
        this.submitInputClass = submitInputClass;
    }
    public OntologyClass getResetInputClass()
    {
        return resetInputClass;
    }
    public void setResetInputClass(OntologyClass resetInputClass)
    {
        this.resetInputClass = resetInputClass;
    }
    public OntologyClass getImageInputClass()
    {
        return imageInputClass;
    }
    public void setImageInputClass(OntologyClass imageInputClass)
    {
        this.imageInputClass = imageInputClass;
    }
    public Term getPageTerm()
    {
        return pageTerm;
    }
    public void setPageTerm(Term pageTerm)
    {
        this.pageTerm = pageTerm;
    }
}


