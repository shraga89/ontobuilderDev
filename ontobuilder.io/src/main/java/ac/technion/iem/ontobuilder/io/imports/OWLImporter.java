package ac.technion.iem.ontobuilder.io.imports;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.ConsoleProgressMonitor;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.util.QNameShortFormProvider;

import uk.ac.manchester.cs.owl.owlapi.OWLDataPropertyAssertionAxiomImpl;
import ac.technion.iem.ontobuilder.core.ontology.Attribute;
import ac.technion.iem.ontobuilder.core.ontology.Domain;
import ac.technion.iem.ontobuilder.core.ontology.DomainEntry;
import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.Term;

/**
 * RDF OWL importer to be used for OAEI @link{http://oaei.ontologymatching.org/} files at first.
 * Conversion logic:
 * OWL Ontology Classes are converted into Ontology classes. Note that the 
 * created hierarchy is larger due to the fact that owl supports equivalent 
 * classes to reside in the same node in the hierarchy. In ontobuilder
 * ontologies this is not possible and thus each class in the node is expanded in parallel.
 *  
 * @author Tomer Sagi
 * @version 0.1
 *
 */
public class OWLImporter implements Importer {

	IRI ontologyIRI = null;
	Ontology o = null;
	OWLOntology owlO = null;
	OWLReasoner reasoner = null;
	QNameShortFormProvider q = null;
	
	@Override
	public Ontology importFile(File file) throws ImportException {
		
		// Get hold of an ontology manager
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        // Load ontology
        try {
        	//Assign OWL objects
			owlO = manager.loadOntologyFromOntologyDocument(file);
			ontologyIRI = owlO.getOntologyID().getOntologyIRI();
			HashSet<OWLOntology> s = new HashSet<OWLOntology>();
			s.add(owlO);
			q = new QNameShortFormProvider();
			o = new Ontology(ontologyIRI.toString()); //TODO iterate over ontology annotations to look for the ontology rdfs:label
			o.setFile(file);
			o.setLight(true);
			OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
	        ConsoleProgressMonitor progressMonitor = new ConsoleProgressMonitor();
	        OWLReasonerConfiguration config = new SimpleConfiguration(progressMonitor);
	        reasoner = reasonerFactory.createReasoner(owlO, config);
	        reasoner.precomputeInferences();
	        boolean consistent = reasoner.isConsistent();
	        System.out.println("Consistent: " + consistent);
	        System.out.println("\n");
	        Node<OWLClass> topCls = reasoner.getTopClassNode();
	        //Fill OWL classes as terms
	        recFillTerms(o,topCls,null);
	        //Fill OWL Object Properties as terms
	        fillObjectProperties();
	        //Fill OWL Data Properties as terms
	        fillDataProperties();
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
			throw new ImportException("OWL import failed");
		}
        
        return o;
		
	}

	/**
	 * Fills object properties by attaching to domain classes as sub-terms
	 */
	private void fillObjectProperties() {
		Node<OWLObjectPropertyExpression> topOP = reasoner.getTopObjectPropertyNode();
		NodeSet<OWLObjectPropertyExpression> nopSet = reasoner.getSubObjectProperties(topOP.getRepresentativeElement(),false);
		for (Node<OWLObjectPropertyExpression> nop : nopSet)
		{
			for (OWLObjectPropertyExpression op : nop.getEntities())
			{
				if (op.isAnonymous() || op.isBottomEntity()) continue;
				Set<OWLClassExpression> ocSet = op.getDomains(owlO);
				if (ocSet.isEmpty() && !op.getSuperProperties(owlO).isEmpty())
				{
					for (OWLObjectPropertyExpression p :  op.getSuperProperties(owlO))
					{
						Term subT = makeTerm(op.asOWLObjectProperty());
		    			Term t = findTermByResourceID(p.asOWLObjectProperty().toStringID());
		    			if (t== null)
		    			{
		    				System.err.println("Couldnt find " + p + " in term lookup hash");
		    				o.addTerm(subT);
		    				continue;
		    			}
		    			else
		    			{
		    				subT.setParent(t);
			    			t.addTerm(subT);
		    			}
		    			
					}
				}
		    	for (OWLClassExpression nCls : op.getDomains(owlO))
		    	{
		    		for (OWLClass c : nCls.getClassesInSignature())
		    		{
		    			Term subT = makeTerm(op.asOWLObjectProperty());
		    			if (c.isOWLThing())
		    			{
		    				o.addTerm(subT); 
		    				continue;
		    			}
		    			Term t = findTermByResourceID(c.getIRI().toString());
		    			if (t== null)
		    			{
		    				System.err.println("Couldnt find " + c.getIRI() + " in term lookup hash");
		    				continue;
		    			}
		    			
		    			subT.setParent(t);
		    			t.addTerm(subT);
		    		}
		    	}
			}
		}
	}

	/**
	 * Fills data properties by attaching to domain classes as sub-terms
	 */
	private void fillDataProperties() {
		Node<OWLDataProperty> topOP = reasoner.getTopDataPropertyNode();
		NodeSet<OWLDataProperty> nopSet = reasoner.getSubDataProperties(topOP.getRepresentativeElement(),false);
		for (Node<OWLDataProperty> nop : nopSet)
		{
			Set<OWLDataProperty> eSet = nop.getEntities();
			for (OWLDataProperty op : eSet)
			{
				if (op.isAnonymous() || op.isBottomEntity()) continue;
				Set<OWLClassExpression> ocSet = op.getDomains(owlO);
				if (ocSet.isEmpty() && !op.getReferencingAxioms(owlO).isEmpty())
				{
					System.err.println("datatype not assigned to class:" + op );//+ "the following are the axioms: \n ");
					for ( OWLAxiom x : op.getReferencingAxioms(owlO))
					{
						//System.err.println(x.toString() + "\n");
						if (x.getAxiomType().toString().equals("DataPropertyAssertion"))
						{
							OWLDataPropertyAssertionAxiomImpl d = (OWLDataPropertyAssertionAxiomImpl)x;
							ocSet.addAll(d.getSubject().getTypes(owlO));
						}
					}
				}
				
				//Create ontobuilder domain
				Domain d = new Domain("Dom" + q.getShortForm(op));
				for (OWLClassExpression oc : ocSet)
			    	//for (Node<OWLClass> nCls : reasoner.getDataPropertyDomains(op,false))
			    	{
			    		for (OWLClass c : oc.getClassesInSignature())
			    		{
			    			for (OWLIndividual i : c.getIndividuals(owlO))
			    			{
			    				if (!i.isNamed()) continue;
			    				for (OWLLiteral l : reasoner.getDataPropertyValues(i.asOWLNamedIndividual(), op))
			    				{
			    					d.addEntry(new DomainEntry(l.getLiteral()));
			    				}
			    			}
			    		}
			    	}
				for (OWLClassExpression oc : ocSet)
		    	//for (Node<OWLClass> nCls : reasoner.getDataPropertyDomains(op,false))
		    	{
		    		for (OWLClass c : oc.getClassesInSignature())
		    		{
		    			if (c.isOWLThing()) continue;
		    			Term subT = makeTerm(op);
		    			subT.setDomain(d);
		    			Term t = findTermByResourceID(c.getIRI().toString());
		    			if (t== null)
		    			{
		    				System.err.println("Couldnt find " + c.getIRI() + " in term lookup hash");
		    				o.addTerm(subT);
		    				continue;
		    			}
		    			else
		    			{
		    				subT.setParent(t);
		    				t.addTerm(subT);
		    			}
		    			
		    			
		    		}
		    	}
			}
		}
		
	}
	/**
	 * Finds term by resourceID attribute TODO: make O(1) by hashmap
	 * @param rID resourceID for term
	 * @return
	 */
	private Term findTermByResourceID(String rID) {
		for (Term t : o.getTerms(true))
		{
				if (((String)t.getAttributeValue("resourceID")).equals(rID)) return t;
		}
		return null;
	}

	/**
	 * Recursively fills Ontobuilder ontology classes 
	 * with OWLOntology classes.
	 * @param o Ontology to be filled
	 * @param parent OWl ontology class parent
	 * @param tParent term parent
	 * @param reasoner
	 */
	private void recFillTerms(Ontology o, Node<OWLClass> parent,Term tParent) {
		// We don't want to print out the bottom node (containing owl:Nothing
        // and unsatisfiable classes) because this would appear as a leaf node
        // everywhere
        if (parent.isBottomNode()) {
            return;
        }
        boolean firstLevel = (o.getTermsCount()==0 || tParent.getName().equals("owl:Thing"));
    	//for each entity add a class
        for (OWLClass cls : parent.getEntities()) 
        {
            // User a prefix manager to provide a slightly nicer shorter name
        	Term t = makeTerm(cls);
    		if (!firstLevel) //not first level
    			tParent.addTerm(t);
    		else
    			if(!cls.isOWLThing()) //skip "Thing"
    				o.addTerm(t);
    		
    		//Continue recursively to add sub classes as subterms
    		NodeSet<OWLClass> subSet = reasoner.getSubClasses(parent.getRepresentativeElement(),true);
    		for (Node<OWLClass> subCls : subSet)
    		{
    			//skip imported elements
    			//if (!subCls.getRepresentativeElement().getIRI().getNamespace().equals(ontologyIRI.toString() + "#"))
    				//	continue;
    			if(subCls.toString().equals("Node( rdf:List )"))
    			{
    				for (Node<OWLClass> listMemberCls : reasoner.getSubClasses(subCls.getRepresentativeElement(), true))
    					recFillTerms(o,listMemberCls,t);
    						
    			}
    			else
    			{
    				recFillTerms(o,subCls,t);
    			}
    		}
    		
    		
        }
	}

	/**
	 * @param cls
	 * @return
	 */
	private Term makeTerm(OWLEntity cls) {
		String label = q.getShortForm(cls);
		String name = q.getShortForm(cls);
		String annotation = "";
		for (OWLAnnotation a : cls.getAnnotations(owlO))
		{
			if (a.getProperty().toString().equals("rdfs:label"))
				label = a.getValue().toString();
			if (a.getProperty().toString().equals("rdfs:comment"))
				annotation = a.getValue().toString();
		}
		Term t = new Term(label); // pm.getShortForm(cls));
		t.addAttribute(new Attribute("name",name));
		t.addAttribute(new Attribute("resourceID",cls.getIRI().toString()));
		t.addAttribute(new Attribute("annotation",annotation));
//		String clsTypeName = cls.getEntityType().getName();
//		if (clsTypeName == "Class")
//		{
//			for (Node<OWLNamedIndividual> n : reasoner.getInstances(cls.asOWLClass(), true))
//			{
//				for (OWLNamedIndividual i : n)
//				{
//					System.err.println(q.getShortForm(i));
//				}
//			}
//		}
//		else if (clsTypeName == "ObjectProperty")
//		{
//			
//		}
//		else // clsTypeName == "DataProperty"
//		{
//			for (OWLNamedIndividual i : cls.asOWLDataProperty().getDomains(owlO))
//			for (Node<OWLNamedIndividual> n : reasoner.getDataPropertyValues(cls.asOWLDataProperty() , cls.asOWLDataProperty()))
//			{
//				
//				{
//					System.err.println(q.getShortForm(i));
//				}
//			}
//		}
		//TODO Add domains, ontology classes, instances, additional relationships
		return t;
	}

	@Override
	public Ontology importFile(File schemaFile, File instanceFile)
			throws ImportException, UnsupportedOperationException {
		throw new UnsupportedOperationException();

	}
	
}
