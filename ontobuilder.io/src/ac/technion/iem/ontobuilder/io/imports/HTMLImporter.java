package ac.technion.iem.ontobuilder.io.imports;

import java.io.File;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;

/**
 * <p>Title: HTMLImporter</p>
 * Implements {@link Importer}
 */
public class HTMLImporter implements Importer
{

    /**
     * Imports an ontology from an HTML file
     * 
     * @param file the {@linkFile} to read the ontology from
     * @throws ImportException when the HTML import failed
     */
    public Ontology importFile(File file) throws ImportException
    {
        try
        {
            Ontology htmlOntology = Ontology.generateOntology(file.toURI().toURL()).getOntology();
            htmlOntology.setName(file.getName());
            return htmlOntology;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new ImportException("HTML Import failed");
        }

    }
}
