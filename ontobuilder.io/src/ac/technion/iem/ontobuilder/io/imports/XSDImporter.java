package ac.technion.iem.ontobuilder.io.imports;

import java.io.File;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.io.utils.xml.schema.XMLSchema;
import ac.technion.iem.ontobuilder.io.utils.xml.schema.XSDComplexElement;
import ac.technion.iem.ontobuilder.io.utils.xml.schema.XSDElement;
import ac.technion.iem.ontobuilder.io.utils.xml.schema.XSDElementsIterator;
import ac.technion.iem.ontobuilder.io.utils.xml.schema.XSDReader;
import ac.technion.iem.ontobuilder.io.utils.xml.schema.XSDReaderFactory;


//Code documented by Nimrod Busany 10/04/2011
/**
 * <p>Title: WSDLImporter</p>
 * Implements {@link Importer}
 */
public class XSDImporter implements Importer
{
    /**
     * Imports an ontology from an XML file
     * 
     * @param file the {@link File} to read the ontology from
     * @throws ImportException when the XML Schema import failed
     */
    public Ontology importFile(File file) throws ImportException
    {
        // creating an empty ontology with the name of the file
        Ontology xsdOntology = new Ontology(file.getName());
        xsdOntology.setLight(true);
        try
        {
            // using xsd handlers to break all terms to simple terms (Term class) in order to
            // create an ontology that ontobuilder can handle
            XSDReaderFactory factory = XSDReaderFactory.instance();
            XSDReader xsdReader = factory.createXSDReader();
            // reading the file and parsing it into an xmlSchema.
            XMLSchema schema = xsdReader.read(file);
            XSDElementsIterator elements = schema.iterator();
            // copying and adjusting the xmlScehma to an on XSD ontology that the ontobuilder can
            // handle
            while (elements.hasNext())
            {
                XSDElement element = elements.nextElement();
                if (element instanceof XSDComplexElement)
                {
                    // breaking the element to the right a basic type
                    extractTermsRec((XSDComplexElement) element, xsdOntology);
                }
                else
                {// XSDSimpleElement
                    Term simpleTerm = new Term(element.getName());
                    simpleTerm.setValue(element.getValue());
                    xsdOntology.addTerm(simpleTerm);
                }
            }
            return xsdOntology;

        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new ImportException("XML Schema Import failed");
        }

    }

    /**
     * Extracts the terms from the XSD element into an ontology
     *
     * @param parent {@link XSDComplexElement} the parent element
     * @param xsdOntology the target {@link Ontology}
     */
    private void extractTermsRec(XSDComplexElement parent, Ontology xsdOntology)
    {
        XSDElementsIterator sequence = parent.iterator();
        Term complexTerm = new Term(parent.getName());
        while (sequence.hasNext())
        {
            XSDElement subelement = sequence.nextElement();
            if (subelement instanceof XSDComplexElement)
            {
                extractTermsRec((XSDComplexElement) subelement, xsdOntology);
            }
            else
            {// XSDSimpleElement
                Term simpleTerm = new Term(subelement.getName());
                simpleTerm.setValue(subelement.getValue());
                simpleTerm.setParent(complexTerm);
                // Relationship rel = new Relationship(complexTerm,"is parent of" ,simpleTerm);
                // complexTerm.addRelationship(rel);
                // rel = new Relationship(simpleTerm,"is child of" ,complexTerm);
                // simpleTerm.addRelationship(rel);
                complexTerm.addTerm(simpleTerm);
                // xsdOntology.addTerm(simpleTerm);
            }
        }
        xsdOntology.addTerm(complexTerm);
    }
}
