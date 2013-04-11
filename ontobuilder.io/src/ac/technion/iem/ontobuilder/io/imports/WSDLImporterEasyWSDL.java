package ac.technion.iem.ontobuilder.io.imports;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.ow2.easywsdl.schema.api.ComplexType;
import org.ow2.easywsdl.schema.api.Element;
import org.ow2.easywsdl.schema.api.Schema;
import org.ow2.easywsdl.schema.api.Type;
import org.ow2.easywsdl.wsdl.api.Description;
import org.ow2.easywsdl.wsdl.api.InterfaceType;
import org.ow2.easywsdl.wsdl.api.Operation;
import org.ow2.easywsdl.wsdl.api.Output;
import org.ow2.easywsdl.wsdl.api.Part;

import ac.technion.iem.ontobuilder.core.ontology.Attribute;
import ac.technion.iem.ontobuilder.core.ontology.Axiom;
import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.Relationship;
import ac.technion.iem.ontobuilder.core.ontology.Term;


/**
 * <p>Title: WSDLImporter</p>
 * <p>Description: WSDL Importer for Ontobuilder. Converts a WSDL to an ontobuilder ontology: Definition.QName ->
 * Ontology Name Definition.Documentation -> Ontology Title Term Tree:
 * Definition->PortType->Operation->Input Message->Message parts ->Output Message(Optional)
 * ->Message parts Throws import exceptions when wsdl tree doesn't conform to the above mentioned
 * tree. Original author unknown.</p>
 * Implements {@link Importer}
 * 
 * @author Tomer Sagi
 * @version 1.1
 */
public class WSDLImporterEasyWSDL implements Importer
{

	
	private static Set<String> typesToIgnore = new HashSet<String>();
	
	static {
		typesToIgnore.add("{http://www.w3.org/2001/XMLSchema}string");
		typesToIgnore.add("{http://www.w3.org/2001/XMLSchema}anyURI");
		typesToIgnore.add("{http://www.w3.org/2001/XMLSchema}token");
		typesToIgnore.add("{http://www.w3.org/2001/XMLSchema}dateTime");
		typesToIgnore.add("{http://www.w3.org/2001/XMLSchema}normalizedString");
		typesToIgnore.add("{http://www.w3.org/2001/XMLSchema}base64Binary");
		typesToIgnore.add("{http://www.w3.org/2001/XMLSchema}decimal");
	}

	private void linkTerms(Term cTerm, Term eTerm) {
		if (eTerm == null)
			return;
		
		Relationship rel = new Relationship(eTerm, "is type of", cTerm);
        eTerm.addRelationship(rel);
        
        cTerm.addTerm(eTerm);
        rel = new Relationship(cTerm, "has type", eTerm);
        cTerm.addRelationship(rel);
	}

	private Term getTerm(Type type) {
		
		if (type.getQName() != null)
			if (typesToIgnore.contains(type.getQName().toString()))
				return null;
		
		Term cTerm = (type.getQName() != null)? new Term(type.getQName().toString()) : new Term ("");        
        
		if (type instanceof ComplexType) {
			ComplexType ct = (ComplexType)type;
			if (ct.getSequence() != null) {
				// Sequence 
				for (Element e : ct.getSequence().getElements()) 
					linkTerms(cTerm,getTerm(e.getType()));
			}
			
			if ( ct.getSimpleContent() != null) {
				if (ct.getSimpleContent().getExtension().getBase() != null) 
					linkTerms(cTerm,getTerm(ct.getSimpleContent().getExtension().getBase()));
	
				if (ct.getSimpleContent().getExtension().getSequence() != null)
	    			// Sequence in Simple Content
	    			for (Element e : ct.getSimpleContent().getExtension().getSequence().getElements()) 
						linkTerms(cTerm,getTerm(e.getType()));
			}
			
			if ( ct.getComplexContent() != null) {
				if (ct.getComplexContent().getExtension().getBase() != null)
					linkTerms(cTerm,getTerm(ct.getComplexContent().getExtension().getBase()));
				
				if (ct.getComplexContent().getExtension().getSequence() != null)
	    			// Sequence in Complex Content
	    			for (Element e : ct.getComplexContent().getExtension().getSequence().getElements()) 
	    				linkTerms(cTerm,getTerm(e.getType()));
			}

		}
		return cTerm;
	}
	
	
	
    /**
     * Imports an ontology from a WSDL
     * 
     * @param file the {@link File} to read the ontology from
     * @throws ImportException when the WSDL file import failed
     */
    public Ontology importFile(File file) throws ImportException
    {
    	
        // TODO convert wsdl:types to classes or terms
        // Classes

        // //definition
        // OntologyClass definitionClass = new OntologyClass("definition");
        // wsdlOntology.addClass(definitionClass);
        // //service
        // OntologyClass serviceClass = new OntologyClass("service");
        // wsdlOntology.addClass(serviceClass);
        // //binding
        // OntologyClass bindingClass = new OntologyClass("binding");
        // wsdlOntology.addClass(bindingClass);
        // //protType
        // OntologyClass protTypeClass = new OntologyClass("protType");
        // wsdlOntology.addClass(protTypeClass);
        // //serviceType
        // OntologyClass serviceTypeClass = new OntologyClass("serviceType");
        // wsdlOntology.addClass(serviceTypeClass);
        // //operation
        // OntologyClass operationClass = new OntologyClass("operation");
        // wsdlOntology.addClass(operationClass);
        // //message
        // OntologyClass messageClass = new OntologyClass("message");
        // wsdlOntology.addClass(messageClass);
        // //port
        // OntologyClass portClass = new OntologyClass("port");
        // wsdlOntology.addClass(portClass);

        // Terms
        Term definitionTerm;
        Term portTypeTerm;
        Term operationTerm;
        Term partTerm;
        Relationship rel;

        try {
        	
        	org.ow2.easywsdl.wsdl.api.WSDLReader easyReader = org.ow2.easywsdl.wsdl.WSDLFactory.newInstance().newWSDLReader();
        	Description desc = easyReader.read(file.toURI().toURL());

//        	System.out.println("parse: " + file.getPath());

            Ontology wsdlOntology = new Ontology(desc.getQName().toString(), desc
                .getDocumentation().toString());
            wsdlOntology.setLight(true);
            definitionTerm = new Term(desc.getQName().toString());

        	
            for (InterfaceType pt : desc.getInterfaces()) {
                // Add port type term and relationships with definition element
                portTypeTerm = new Term(pt.getQName().toString());
                rel = new Relationship(portTypeTerm, "is child of", definitionTerm);
                portTypeTerm.addRelationship(rel);

                // Add terms for port type operations
                for (Operation op : pt.getOperations())
                {
                    operationTerm = new Term(op.getQName().toString());
                    rel = new Relationship(operationTerm, "is operation of", portTypeTerm);
                    operationTerm.addRelationship(rel);


                    // Add subterms for input message using message parts
                    for (Part part : op.getInput().getParts())
                    {
                        partTerm = new Term(part.getPartQName().toString());
                        rel = new Relationship(partTerm, "is input part of", operationTerm);
                        partTerm.addRelationship(rel);
                        
                        Term partTypeTerm = getTerm(part.getElement().getType());
                        rel = new Relationship(partTypeTerm, "is type of", partTerm);
                        partTypeTerm.addRelationship(rel);
                        
                        partTerm.addTerm(partTypeTerm);
                        rel = new Relationship(partTerm, "has type", partTypeTerm);
                        operationTerm.addRelationship(rel);
                        
                        operationTerm.addTerm(partTerm);
                        rel = new Relationship(operationTerm, "has input part", partTerm);
                        operationTerm.addRelationship(rel);
                        
                   }

                    // Add subterms for output message using message parts
                    Output out = op.getOutput();
                    if (out != null) // Not all web services have an output message
                    {
                        for (Part part : out.getParts())
                        {
                            partTerm = new Term(part.getPartQName().toString());
                            rel = new Relationship(partTerm, "is output part of", operationTerm);
                            partTerm.addRelationship(rel);
                            
                            Term partTypeTerm = getTerm(part.getElement().getType());
                            rel = new Relationship(partTypeTerm, "is type of", partTerm);
                            partTypeTerm.addRelationship(rel);
                            
                            partTerm.addTerm(partTypeTerm);
                            rel = new Relationship(partTerm, "has type", partTypeTerm);
                            operationTerm.addRelationship(rel);

                            operationTerm.addTerm(partTerm);
                            rel = new Relationship(operationTerm, "has output part", partTerm);
                            operationTerm.addRelationship(rel);
                        }
                    }
                    portTypeTerm.addTerm(operationTerm);
                    rel = new Relationship(portTypeTerm, "has operation", operationTerm);
                    portTypeTerm.addRelationship(rel);
                }
                definitionTerm.addTerm(portTypeTerm);
                rel = new Relationship(definitionTerm, "is parent of", portTypeTerm);
                definitionTerm.addRelationship(rel);
            }

            wsdlOntology.addTerm(definitionTerm);
            return wsdlOntology;
        }
        catch (Exception e)
        {
			e.printStackTrace();
            throw new ImportException("WSDL File Import Failed");
		}
    }

	@Override
	public Ontology importFile(File schemaFile, File instanceFile)
			throws ImportException, UnsupportedOperationException {
		throw new UnsupportedOperationException("WSDL Importer doesn't support instances");	}
}
