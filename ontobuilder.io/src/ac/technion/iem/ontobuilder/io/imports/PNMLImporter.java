package ac.technion.iem.ontobuilder.io.imports;

import java.io.File;
import ac.technion.iem.ontobuilder.core.ontology.Ontology;

/**
 * <p>Title: PNML (Petri-Net Mark-up Language) Importer </p>
 * Implements {@link Importer}
 */
public class PNMLImporter implements Importer
{
	private Ontology PNMLOntology;
    /**
     * Imports an ontology from a PNML file
     * 
     * @param file the {@link File} to read the ontology from
     * @throws ImportException when the import failed
     */
	public Ontology importFile(File file) throws ImportException
    {
		return importFile(file,null);
    }
	
	/**
     * Imports an ontology from an PNML file and mines domain entries
     * from the instances supplied
     * 
     * @param file the {@link File} to read the ontology from
     * @throws ImportException when the import failed
     */
	public Ontology importFile(File file, File instances) throws ImportException
    {
        // create an empty ontology with the name of the file
        PNMLOntology = new Ontology(file.getName().substring(0, file.getName().length()-5));
        PNMLOntology.setFile(file);
        PNMLOntology.setLight(true);

    	Object result = null;
        //Parse PNML
        try
        {

        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new ImportException("PNML Import failed");
        }
        
        
        
		//create classes and terms from result
        createOntology(result );
        
        //mine domain from instances
        if (instances!=null) 
        	createDomainsFromInstances(file,instances);
        
        return PNMLOntology;
        

    }

	/**
	 * Parses instances into ontology domains
	 * @param parser 
	 * @param instances
	 */
	private void createDomainsFromInstances(File schema, File instances) {

	}


	/**
	 * Translate parsed process to ontology:
	 * 1. Make an OntologyClass object for each simple type and complex type
	 * 2. Connect between each complex type's OntologyClass and it's base type's OntologyClass
	 * 3. Make term tree from elements by:
	 * 		1. recursively adding complex type elements as subterms
	 * 		2. replacing extension markers with terms from the referenced type
	 * @param result parsed BP
	 */
	private void createOntology(Object result) {
		
		
	}

	
}
