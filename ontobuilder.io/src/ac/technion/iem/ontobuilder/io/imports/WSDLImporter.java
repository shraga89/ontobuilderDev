package ac.technion.iem.ontobuilder.io.imports;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.wsdl.Definition;
import javax.wsdl.Input;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.Output;
import javax.wsdl.Part;
import javax.wsdl.PortType;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;

import org.ow2.easywsdl.schema.api.ComplexType;
import org.ow2.easywsdl.schema.api.Element;
import org.ow2.easywsdl.schema.api.Schema;
import org.ow2.easywsdl.schema.api.SimpleType;
import org.ow2.easywsdl.schema.api.Type;
import org.ow2.easywsdl.wsdl.api.Description;

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
public class WSDLImporter implements Importer
{

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
        Term messageTerm;
        Term operationTerm;
        Term partTerm;
        Relationship rel;

        try {
        	
//        	/*
//        	 * Trying to parse the schemas
//        	 */
//        	SAXBuilder builder = new SAXBuilder();
//       		Document document = (Document) builder.build(file);
//       		List<Element> typeList = (List<Element>) document.getRootElement().getChildren("types", Namespace.getNamespace("http://schemas.xmlsoap.org/wsdl/"));
//       		if (!typeList.isEmpty()) {
//       			List<Element> schemaList = (List<Element>) typeList.get(0).getChildren("schema", Namespace.getNamespace("http://www.w3.org/2001/XMLSchema"));
//
//	       		XSOMParser parser = new XSOMParser();
//	        	SAXOutputter outputter = new SAXOutputter(parser.getParserHandler());
//	        	for (Element e : schemaList) {
//	            	outputter.output(e);
//	        	}
//	        	XSSchemaSet schemas = parser.getResult(); 
//        		System.out.println(schemas);
//	        	
//	        	
//       		}
        	
        	WSDLFactory factory = WSDLFactory.newInstance();
            WSDLReader reader = factory.newWSDLReader();

            reader.setFeature("javax.wsdl.verbose", true);
            reader.setFeature("javax.wsdl.importDocuments", true);

            Definition definition = reader.readWSDL(null, file.getAbsolutePath());
            Ontology wsdlOntology = new Ontology(definition.getQName().toString(), definition
                .getDocumentationElement().toString());
            wsdlOntology.setLight(true);
            definitionTerm = new Term(definition.getQName().toString());

        	org.ow2.easywsdl.wsdl.api.WSDLReader easyReader = org.ow2.easywsdl.wsdl.WSDLFactory.newInstance().newWSDLReader();
        	Description desc = easyReader.read(file.toURI().toURL());
       		
        	Map<Type, Term> typeTerms = new HashMap<Type, Term>();
        	for (Schema s : desc.getTypes().getSchemas()) {
        		for (Type t : s.getTypes()) {
    				Term typeTerm = new Term(t.getQName().getLocalPart());
    				wsdlOntology.addTerm(typeTerm);
    				typeTerms.put(t,typeTerm);
        		}
        	}
        	
            Collection<PortType> portTypes = definition.getPortTypes().values();
            for (PortType pt : portTypes) {
                // Add port type term and relationships with definition element
                portTypeTerm = new Term(pt.getQName().toString());
                rel = new Relationship(portTypeTerm, "is child of", definitionTerm);
                portTypeTerm.addRelationship(rel);

                // Add terms for port type operations
                List<Operation> operations = pt.getOperations();
                for (Operation op : operations)
                {
                    operationTerm = new Term(op.getName());
                    rel = new Relationship(operationTerm, "is operation of", portTypeTerm);
                    operationTerm.addRelationship(rel);

                    // Add subterm for operation using inputMessage
                    Input in = op.getInput();
                    if (in == null)
                        throw new ImportException(
                            "WSDL File Import Failed for, no input for operation" + op.getName());
                    Message inputMessage = in.getMessage();
                    messageTerm = new Term(inputMessage.getQName().toString());
                    rel = new Relationship(messageTerm, "is input message of", operationTerm);
                    messageTerm.addRelationship(rel);

                    // Add subterms for input message using message parts
                    Collection<Part> inputParts = inputMessage.getParts().values();
                    for (Part part : inputParts)
                    {
                        partTerm = new Term(part.getName());
                        rel = new Relationship(partTerm, "is part of", messageTerm);
                        partTerm.addRelationship(rel);
                        String domainName = (part.getTypeName() == null ? "xsd:string" : part
                            .getTypeName().toString());
                        partTerm.setDomain(new Domain(domainName));
                        messageTerm.addTerm(partTerm);
                        rel = new Relationship(messageTerm, "has part", partTerm);
                        messageTerm.addRelationship(rel);
                        
                   }
                    operationTerm.addTerm(messageTerm);
                    rel = new Relationship(operationTerm, "has input message", messageTerm);
                    operationTerm.addRelationship(rel);

                    // Add subterms for output message using message parts
                    Output out = op.getOutput();
                    if (out != null) // Not all web services have an output message
                    {
                        Message outputMessage = out.getMessage();
                        messageTerm = new Term(outputMessage.getQName().toString());
                        rel = new Relationship(messageTerm, "is output message of", operationTerm);
                        messageTerm.addRelationship(rel);
                        Collection<Part> outputParts = outputMessage.getParts().values();
                        for (Part part : outputParts)
                        {
                            partTerm = new Term(part.getName());
                            rel = new Relationship(partTerm, "is part of", messageTerm);
                            partTerm.addRelationship(rel);
                            String domainName = (part.getTypeName() == null ? "xsd:string" : part
                                .getTypeName().toString());
                            partTerm.setDomain(new Domain(domainName));
                            messageTerm.addTerm(partTerm);
                            rel = new Relationship(messageTerm, "has part", partTerm);
                            messageTerm.addRelationship(rel);
                        }
                        operationTerm.addTerm(messageTerm);
                        rel = new Relationship(operationTerm, "has output message", messageTerm);
                        operationTerm.addRelationship(rel);
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
