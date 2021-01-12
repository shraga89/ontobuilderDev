package ac.technion.iem.ontobuilder.io.matchimport;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;

import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import org.apache.jena.rdf.model.*;
import org.apache.jena.util.FileManager;

/**
 * RDF match importer to be used with OAEI standard
 * terms are looked up in ontology using a resourceID property
 * which is assumed to be present. 
 * @author Tomer Sagi
 *
 */
public class RDFMatchImporter implements MatchImporter {
public static String ALIGNns = "http://knowledgeweb.semanticweb.org/heterogeneity/alignment:";
	@Override
	public MatchInformation importMatch(MatchInformation mi, File file) {
		
		//Prepare ontology term lookup facility
		HashMap<String,Term> cTerms = new HashMap<String,Term>();
		HashMap<String,Term> tTerms = new HashMap<String,Term>();
		for (Term ct : mi.getCandidateOntology().getTerms(true))
			cTerms.put((String)ct.getAttributeValue("resourceID"),ct);
		for (Term tt : mi.getTargetOntology().getTerms(true))
			tTerms.put((String)tt.getAttributeValue("resourceID"),tt);

		// create an empty model
		 Model model = ModelFactory.createDefaultModel();
		 // use the FileManager to find the input file
		 InputStream in = FileManager.get().open( file.getAbsolutePath() );
		if (in == null) {
		    throw new IllegalArgumentException(
		                                 "File: " + file.getAbsolutePath() + " not found");
		}

		// read the RDF/XML file
		model.read(in, null);
		
		//Read alignments
		ResIterator aIt = model.listResourcesWithProperty(ALIGN.O1);
		while (aIt.hasNext())
		{
			//Determine ontology order
			Resource alignment = aIt.next();
			String onto1 = alignment.getRequiredProperty(ALIGN.O1).getString();
			String onto2 = alignment.getRequiredProperty(ALIGN.O2).getString();
			boolean swap = false; 
			if (onto1.equalsIgnoreCase(mi.getTargetOntology().getName()) 
					&& onto2.equalsIgnoreCase(mi.getCandidateOntology().getName()))
				swap = true;
			
			//Convert maps to matches
			ResIterator it = model.listResourcesWithProperty(ALIGN.E1);
			while (it.hasNext())
			{
				Resource cell = it.next();
				Resource entity1 = cell.getRequiredProperty(ALIGN.E1).getResource();
				Resource entity2 = cell.getRequiredProperty(ALIGN.E2).getResource();
				float conf = cell.getRequiredProperty(ALIGN.MEASURE).getFloat();
				Term cTerm = (swap ? cTerms.get(entity2.toString()) : cTerms.get(entity1.toString()));
				if (cTerm == null) 
				{
					System.err.println("candidate term: " + (swap ? entity2 : entity1).getLocalName() + " not found in ontology");
					continue;
				}
				Term tTerm = (swap ? tTerms.get(entity1.toString()) : tTerms.get(entity2.toString()));
				if (tTerm == null) 
				{
					System.err.println("target term: " + (swap ? entity1 : entity2).getLocalName() + " not found in ontology");
					continue;
				}
				mi.updateMatch(tTerm, cTerm, conf);
				//StmtIterator sit = cell.listProperties();
				//while (sit.hasNext())
					//System.err.println(sit.next().toString());
				
			}
		}
		return mi;
	}

}
