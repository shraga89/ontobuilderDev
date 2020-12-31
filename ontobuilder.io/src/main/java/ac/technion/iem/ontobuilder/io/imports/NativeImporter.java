/**
 * 
 */
package ac.technion.iem.ontobuilder.io.imports;

import java.io.File;
import java.io.IOException;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.utils.files.XmlFileHandler;

/**
 * @author Tomer Sagi
 * Importer for Ontobuilder native format. Actually a shell for xmlFileHandler
 */
public class NativeImporter implements Importer {
	
	XmlFileHandler xfh = new XmlFileHandler();
	
	/* (non-Javadoc)
	 * @see ac.technion.iem.ontobuilder.io.imports.Importer#importFile(java.io.File)
	 */
	@Override
	public Ontology importFile(File file) throws ImportException {
		Ontology res;
		try {
			res = xfh.readOntologyXMLFile(file.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
			throw new ImportException(e.getMessage());
		} 
		return res;
	}

	@Override
	public Ontology importFile(File schemaFile, File instanceFile)
			throws ImportException, UnsupportedOperationException {
		throw new UnsupportedOperationException("Native Importer doesn't support instances");
	}

}
