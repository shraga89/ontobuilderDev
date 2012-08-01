package ac.technion.iem.ontobuilder.io.imports;

import java.io.File;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;

/**
 * <p>Title: interface Importer</p>
 * <p>Description:  The Importer interface</p>
 * In order to implement an importer, you should implement this interface.
 * XSD importer is a simple example.
 */
public interface Importer
{
	/**
	 * Regular Importer
	 * @param file containing schema information
	 * @return Ontology with schema information included
	 * @throws ImportException if import fails
	 */
    public Ontology importFile(File file) throws ImportException;

    /**
     * Instance enabled importer
     * @param schemaFile file containing schema information
     * @param instanceFile file containing instances corresponding to schema
     * @return Ontology with schema information included and instances mined
     * to produce domains and domain entries for each ontology class
     * @throws ImportException if import fails
     * @throws UnsupportedOperationException if importer doesn't support instances 
     */
	public Ontology importFile(File schemaFile, File instanceFile) throws ImportException, UnsupportedOperationException;
}
