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
    public Ontology importFile(File file) throws ImportException;
}
