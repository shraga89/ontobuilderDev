package ac.technion.iem.ontobuilder.io.imports;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ow2.easywsdl.schema.api.ComplexType;
import org.ow2.easywsdl.schema.api.Element;
import org.ow2.easywsdl.schema.api.Schema;
import org.ow2.easywsdl.schema.api.SimpleType;
import org.ow2.easywsdl.schema.api.Type;
import org.ow2.easywsdl.wsdl.api.Description;
import org.ow2.easywsdl.wsdl.api.InterfaceType;
import org.ow2.easywsdl.wsdl.api.Operation;
import org.ow2.easywsdl.wsdl.api.Output;
import org.ow2.easywsdl.wsdl.api.Part;

import ac.technion.iem.ontobuilder.core.ontology.Domain;
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


	private void createTermsRec(Type root, Map<Type, Term> typeTerms) {
		if (typeTerms.containsKey(root))
			return;
		
		Term typeTerm = (root.getQName() != null)? new Term(root.getQName().toString()) : new Term ("");
		typeTerms.put(root,typeTerm);
		
		if (root instanceof ComplexType) {
			ComplexType ct = (ComplexType) root;
			if (ct.getSequence() != null) 
				for (Element e : ct.getSequence().getElements())
					createTermsRec(e.getType(),typeTerms);
			
			if (ct.getSimpleContent() != null) 
				if (ct.getSimpleContent().getExtension().getSequence() != null)
	    			for (Element e : ct.getSimpleContent().getExtension().getSequence().getElements()) 
						createTermsRec(e.getType(),typeTerms);
			
			if ( ct.getComplexContent() != null)
				if (ct.getComplexContent().getExtension().getSequence() != null)
	    			for (Element e : ct.getComplexContent().getExtension().getSequence().getElements()) 
						createTermsRec(e.getType(),typeTerms);
		}
	}

	private void linkTerms(Type from, Type to, Map<Type, Term> typeTerms) {
		Term cTerm = typeTerms.get(from);
		Term eTerm = typeTerms.get(to);
		
		Relationship rel = new Relationship(eTerm, "is type of", cTerm);
        eTerm.addRelationship(rel);
        
        cTerm.addTerm(eTerm);
        rel = new Relationship(cTerm, "has type", eTerm);
        cTerm.addRelationship(rel);
	}
	
	
	
    /**
     * Imports an ontology from a WSDL
     * 
     * @param file the {@link File} to read the ontology from
     * @throws ImportException when the WSDL file import failed
     */
    @SuppressWarnings("unchecked")
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

            Ontology wsdlOntology = new Ontology(desc.getQName().toString(), desc
                .getDocumentation().toString());
            wsdlOntology.setLight(true);
            definitionTerm = new Term(desc.getQName().toString());

        	/*
        	 * Create the terms for types
        	 */
        	Map<Type, Term> typeTerms = new HashMap<Type, Term>();
        	for (Schema s : desc.getTypes().getSchemas()) {
        		for (Type t : s.getTypes()) {
        			createTermsRec(t, typeTerms);
        		}
        	}
        	

        	for (Schema s : desc.getTypes().getSchemas()) {
        		for (Type t : s.getTypes()) {
        			if (!(t instanceof ComplexType))
        				continue;
        			ComplexType ct = (ComplexType)t;
        			if (ct.getSequence() != null) {
	        			// Sequence 
	        			for (Element e : ct.getSequence().getElements())
	        				linkTerms(ct,e.getType(),typeTerms);
        			}
        			
        			if ( ct.getSimpleContent() != null) {
        				if (ct.getSimpleContent().getExtension().getBase() != null)
	        				linkTerms(ct,ct.getSimpleContent().getExtension().getBase(),typeTerms);

        				if (ct.getSimpleContent().getExtension().getSequence() != null)
		        			// Sequence in Simple Content
		        			for (Element e : ct.getSimpleContent().getExtension().getSequence().getElements()) 
		        				linkTerms(ct,e.getType(),typeTerms);
        			}
        			
        			if ( ct.getComplexContent() != null) {
        				if (ct.getComplexContent().getExtension().getBase() != null)
	        				linkTerms(ct,ct.getComplexContent().getExtension().getBase(),typeTerms);
        				
        				if (ct.getComplexContent().getExtension().getSequence() != null)
    	        			// Sequence in Complex Content
		        			for (Element e : ct.getComplexContent().getExtension().getSequence().getElements()) 
		        				linkTerms(ct,e.getType(),typeTerms);
        			}

        		}
        	}

        	
        	/*
        	 * Link the terms for the types
        	 */
        	for (Schema s : desc.getTypes().getSchemas()) {
        		for (Type t : s.getTypes()) {
        			if (!(t instanceof ComplexType))
        				continue;
        			ComplexType ct = (ComplexType)t;
        			
        			if (ct.getSequence() != null) {
	        			// Sequence 
	        			for (Element e : ct.getSequence().getElements())
	        				linkTerms(ct,e.getType(),typeTerms);
        			}
        			
        			if ( ct.getSimpleContent() != null) {
        				if (ct.getSimpleContent().getExtension().getBase() != null)
	        				linkTerms(ct,ct.getSimpleContent().getExtension().getBase(),typeTerms);

        				if (ct.getSimpleContent().getExtension().getSequence() != null)
		        			// Sequence in Simple Content
		        			for (Element e : ct.getSimpleContent().getExtension().getSequence().getElements()) 
		        				linkTerms(ct,e.getType(),typeTerms);
        			}
        			
        			if ( ct.getComplexContent() != null) {
        				if (ct.getComplexContent().getExtension().getBase() != null)
	        				linkTerms(ct,ct.getComplexContent().getExtension().getBase(),typeTerms);
        				
        				if (ct.getComplexContent().getExtension().getSequence() != null)
    	        			// Sequence in Complex Content
		        			for (Element e : ct.getComplexContent().getExtension().getSequence().getElements()) 
		        				linkTerms(ct,e.getType(),typeTerms);
        			}
        		}
        	}
        	
        	
        	
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
                        
                        Term partTypeTerm = typeTerms.get(part.getElement().getType());
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
                            
                            Term partTypeTerm = typeTerms.get(part.getElement().getType());
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
