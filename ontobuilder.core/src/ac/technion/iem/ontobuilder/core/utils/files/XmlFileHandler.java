package ac.technion.iem.ontobuilder.core.utils.files;

import java.io.File;
import java.io.IOException;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;

/**
 * <p>Title: XmlFileHandler</p>
 * <p>Description: This class handles extraction and normalisation of an Ontology from an XML file</p>
 * Extends {@link OntoBuilder}
 */
public class XmlFileHandler
{
    /**
     * Constructs a default XmlFileHandler
     */
    public XmlFileHandler()
    { 
    }
    
    /**
     * Extracts and Normalized Ontology from an .xml
     * 
     * @param filepath the file path to the desired xml.file
     * @return Ontology - an ontology object which is built from the .xml file
     * @throws IOException
     */
    public Ontology readOntologyXMLFile(String filepath) throws IOException
    {
        return readOntologyXMLFile(filepath, true);
    }

    /**
     * Extracts and Normalized Ontology from an .xml
     * 
     * @param filepath the file path to the desired xml.file
     * @param normalize <code>true</code> to normalize ontology
     * @return Ontology - an ontology object which is built from the .xml file
     * @throws IOException
     */
    public Ontology readOntologyXMLFile(String filepath, boolean normalize) throws IOException
    {
        Ontology ontology = Ontology.openFromXML(new File(filepath));
        if (normalize)
            ontology.normalize();
        return ontology;
    }
}
