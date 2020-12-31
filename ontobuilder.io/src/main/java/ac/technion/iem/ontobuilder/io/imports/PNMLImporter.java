package ac.technion.iem.ontobuilder.io.imports;

import java.io.File;

import org.jbpt.petri.NetSystem;
import org.jbpt.petri.Transition;
import org.jbpt.petri.io.PNMLSerializer;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.OntologyClass;
import ac.technion.iem.ontobuilder.core.ontology.Term;

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

    	PNMLSerializer parser = new PNMLSerializer();
    	NetSystem netSystem = parser.parse(file.getPath());
        
    	//create classes and terms from result
        createOntology(netSystem);
        
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
		//TODO: at some point we may load traces 
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
	private void createOntology(NetSystem netSystem) {

		OntologyClass actClass = new OntologyClass("activity");
		PNMLOntology.addClass(actClass);

		for (Transition t : netSystem.getTransitions()) {
			if (t.isSilent()) 
				continue;
			
			String cleanedName = cleanNameString(t.getName());
			Term term = new Term(cleanedName, cleanedName);
			term.setSuperClass(actClass);
			PNMLOntology.addTerm(term);
			
		}
	}
	
	/**
	 * Some PNML files have transition names that start with '.\n' to 
	 * force old ProM versions to display the name. Since that is not 
	 * included in the respective match file, we delete this prefix. 
	 * Also, we replace '.' with ' ' to avoid provenance problems.
	 * 
	 * @param name the name of a transition
	 * @return the cleaned name of a transition
	 */
	private String cleanNameString(String name) {
		return name.replace(".\\n", "").replace('.', ' ').trim();
	}
	
}
