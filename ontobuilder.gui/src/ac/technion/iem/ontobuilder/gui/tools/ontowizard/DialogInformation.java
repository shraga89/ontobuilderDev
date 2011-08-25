package ac.technion.iem.ontobuilder.gui.tools.ontowizard;

import java.util.ArrayList;
import java.net.URL;
import org.w3c.dom.Document;

/**
 * <p>Title: DialogInformation</p>
 */
public class DialogInformation
{
    protected ArrayList<?> forms;
    protected Document document;
    protected URL url;

    /**
     * Constructs a DialogInformation
     * 
     * @param document a {@link Document}
     * @param forms a list of forms
     * @param url the {@link URL}
     */
    public DialogInformation(Document document, ArrayList<?> forms, URL url)
    {
        this.forms = forms;
        this.document = document;
        this.url = url;
    }

    /**
     * Set the document
     * 
     * @param document the {@link Document}
     */
    public void setDocument(Document document)
    {
        this.document = document;
    }

    /**
     * Get the document
     * 
     * @return the {@link Document}
     */
    public Document getDocument()
    {
        return document;
    }

    /**
     * Set the forms
     * 
     * @param forms the forms
     */
    public void setForms(ArrayList<?> forms)
    {
        this.forms = forms;
    }

    /**
     * Get the forms
     * 
     * @return the forms list
     */
    public ArrayList<?> getForms()
    {
        return forms;
    }

    /**
     * Set the url
     * 
     * @param url the {@link URL}
     */
    public void setURL(URL url)
    {
        this.url = url;
    }

    /**
     * Get the url
     * 
     * @return the {@link URL}
     */
    public URL getURL()
    {
        return url;
    }
}
