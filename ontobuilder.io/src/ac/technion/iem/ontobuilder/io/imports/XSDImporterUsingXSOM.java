package ac.technion.iem.ontobuilder.io.imports;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import ac.technion.iem.ontobuilder.core.ontology.Domain;
import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.OntologyClass;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.io.utils.xml.XSDEntityResolver;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSSchemaSet;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSTerm;
import com.sun.xml.xsom.XSType;
import com.sun.xml.xsom.impl.ComplexTypeImpl;
import com.sun.xml.xsom.impl.ElementDecl;
import com.sun.xml.xsom.impl.ModelGroupImpl;
import com.sun.xml.xsom.impl.parser.DelayedRef.ComplexType;
import com.sun.xml.xsom.impl.util.DraconianErrorHandler;
import com.sun.xml.xsom.parser.XSOMParser;

/**
 * <p>Title: XSDImporter Using XSOM </p>
 * Implements {@link Importer}
 */
public class XSDImporterUsingXSOM implements Importer
{
    /**
     * Imports an ontology from an XML file
     * 
     * @param file the {@link File} to read the ontology from
     * @throws ImportException when the XML Schema import failed
     */
	private Ontology xsdOntology;
	private HashMap<String,Term> ctTerms = new HashMap<String,Term>(); //Terms created from declared context types, used when types are referenced inside complex elements
	private ArrayList<Term> missingCTTerms = new ArrayList<Term>(); //Terms which couldn't be created since declared complex types where not converted to terms yet, used when types are referenced inside complex elements
	
    public Ontology importFile(File file) throws ImportException
    {
        // creating an empty ontology with the name of the file
        xsdOntology = new Ontology(file.getName().substring(0, file.getName().length()-4));
        xsdOntology.setLight(true);
        try
        {
        	//Parse XSD and Recursively collect complex types from imports
        	XSOMParser parser = new XSOMParser();
        	parser.setErrorHandler(new DraconianErrorHandler());
        	parser.setEntityResolver(new XSDEntityResolver());
        	parser.parse(file);
        	XSSchemaSet result = parser.getResult();
        	
        	/*Translate parsed schema to ontology:
        	 * 1. Make an OntologyClass object for each simple type and complex type
        	 * 2. Connect between each complex type's OntologyClass and it's base type's OntologyClass
        	 * 3. Make term tree from elements by:
        	 * 		1. recursively adding complex type elements as subterms
        	 * 		2. replacing extension markers with terms from the referenced type
        	 */
        	
        	//Make OntologyClass object for each simple type
        	Iterator<XSSimpleType> it1 = result.iterateSimpleTypes();
        	OntologyClass ontologyClass;
        	while (it1.hasNext())
        	{
        		XSSimpleType newType = it1.next();
				xsdOntology.addClass(createOntologyClass(xsdOntology,newType));
        	}
        	
        	//Add ontology class for each complex type
        	HashMap<OntologyClass,ArrayList<OntologyClass>> relationships = new HashMap<OntologyClass,ArrayList<OntologyClass>>();
        	Iterator<XSComplexType> it2 = result.iterateComplexTypes();
        	while (it2.hasNext())
        	{ 
        		
        		XSComplexType newType = it2.next();
				//Make ontology class
        		ontologyClass = createOntologyClass(xsdOntology,newType);
        		//Record parent child relationships to later create them in classes
        		ArrayList<OntologyClass> superClasses;
        		XSType baseType = newType.getBaseType();
        		if (!baseType.equals(newType)) //if no base type, it will reference itself
        		{
	        		if (relationships.keySet().contains(ontologyClass))
	        		{
	        			superClasses = relationships.get(ontologyClass);
	        		}
	        		else
	        		{
	        			superClasses = new ArrayList<OntologyClass>();
	        		}
	        		superClasses.add(createOntologyClass(xsdOntology,baseType));
	        		relationships.put(ontologyClass, superClasses);
        		}
				xsdOntology.addClass(ontologyClass);
        	}
        	
        	it2 = result.iterateComplexTypes();
        	while (it2.hasNext())
        	{ 
        		
        		XSComplexType newType = it2.next();
	        	//make Term and collect, Will be used to create terms in complex elements using this complex type
				Term classT = makeTermFromComplexType(null, newType);
				this.ctTerms.put(newType.getName(), classT);
        	}
        	//Build ontology class tree
        	for (OntologyClass child : relationships.keySet())
        	{
        		for (OntologyClass superClass : relationships.get(child))
        			child.setSuperClass(superClass);
        	}
        	
        	/*3. Make term tree from elements by:
           	 * 		1. recursively adding complex type elements as subterms
           	 * 		2. replacing extension markers with terms from the referenced type
           	 * */
        	//Make all terms
        	for (XSSchema s : result.getSchemas())
        	{
        		Map<String, XSElementDecl> eMap = s.getElementDecls();
        		for (XSElementDecl e : eMap.values())
        		{
        			Term t = makeTermFromElement(null,e);
        			xsdOntology.addTerm(t);
        		}
        	}
        	return xsdOntology;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new ImportException("XML Schema Import failed");
        }

    }

	/**
	 * @param xsdOntology
	 * @param newType
	 * @return
	 */
	private OntologyClass createOntologyClass(Ontology xsdOntology,
			XSType newType) {
		OntologyClass ontologyClass = new OntologyClass(newType.getName());
		ontologyClass.setOntology(xsdOntology);
		//newType.getAnnotation() TODO
		return ontologyClass;
	}

    /**
     * 
     * @param parent
     * @param complexElement
     * @return
     */
	private Term makeTermFromElement(Term parent,XSElementDecl complexElement)
	{
		//TODO check why simple elements are not assigned the correct OntologyClass
		//Make sure this is a complex element, if not, return a simple element
		//debug: System.out.println("complex element:" + complexElement.getName());
		if (!complexElement.getType().isComplexType())
			try {
				return makeTermFromSimpleElement(parent, complexElement);
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		Term t = makeTerm(parent,complexElement);
		
		//if Complex element's complex type is named make a term from it
		XSComplexType ct = complexElement.getType().asComplexType();
		if (complexElement.getType().isGlobal())
		{
			try {
				String ctName = ct.getName();
				if (!ctTerms.containsKey(ctName))
					ctTerms.put(ctName, makeTermFromComplexType(null, ct));
				Term ctTerm = ctTerms.get(ct.getName());
				t = recCloneTerm(ctTerm);
				t.setName(complexElement.getName());
				t.setParent(parent);
				t.setAttributeValue("name", complexElement.getName());
				//t.addTerm(newT);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else//if it is anonymous skip the type and make terms from the type's elements
		{
			XSTerm xst = ct.getContentType().asParticle().getTerm();
			XSModelGroup g =  xst.asModelGroup();
			for (XSParticle p : g.getChildren())
			{
				if(p.getTerm().isModelGroup()) //extension
				{
					for (XSParticle p1 : p.getTerm().asModelGroup().getChildren())
						t.addTerm(makeTermFromElement(t,p1.getTerm().asElementDecl()));
				}
				else
				{t.addTerm(makeTermFromElement(t,p.getTerm().asElementDecl()));}
			}
//			for (XSElementDecl e : ct.getElementDecls())
//				t.addTerm(makeTermFromElement(t, e));
		}
		
		return t;
	}
	
	/**
	 * Term's clone method does not clone the subterms, this solves the issue
	 * @param term Term to be cloned
	 * @return new term, clone of supplied term with cloned attributes
	 */
	private Term recCloneTerm(Term term) {
		assert(term!=null);
		Term res = term.clone();
		for (Term child : term.getTerms())
			res.addTerm(recCloneTerm(child));
		return res;
	}

	/**
	 * Makes a Term object from the element supplied. Assumes element is a simple element. Throws exception if not. 
	 * @param parent Term to which this Term will be connceted, used to set the parent and ontology properties of new Term
	 * @param e element to be converted
	 * @return Term which is assumed to be later added to parent term
	 * @throws Exception if not a simple element
	 */
	private Term makeTermFromSimpleElement(Term parent,XSElementDecl e) throws Exception
	{
		if (!e.getType().isSimpleType()) throw new Exception("Expected simple element, recieved complex element");
		return makeTerm(parent,e);
	}
	
	private Term makeTerm(Term parent, XSElementDecl e) {
		Term term = new Term(e.getName(),e.getDefaultValue());
		term.addAttribute(new ac.technion.iem.ontobuilder.core.ontology.Attribute("name",e.getName()));
		term.addAttribute(new ac.technion.iem.ontobuilder.core.ontology.Attribute("annotation",e.getAnnotation()));
		if (e.getType().isGlobal())
		{
			term.setDomain(new Domain(e.getType().getName()));//TODO improve this by understanding the domain usage in ontobuilder matchers
			term.setSuperClass(xsdOntology.findClass(e.getType().getName()));
		}
		if (parent != null) 
		 	term.setParent(parent);
		
		term.setOntology(xsdOntology);
		return term;
		
	}

	/**
	 * Assumes it receives a named complex type and makes a term from it. If the complex type is anonymous (local) throws an exception
	 * @param parent Term to which this term will refer. Assumes the term created will be added to the parent term after this method is called
	 * @param ct
	 * @return null if term cannot be created due to a missing complex type or the created term otherwise
	 * @throws Exception if the type is a simple type
	 */
	private Term makeTermFromComplexType(Term parent,XSComplexType ct) throws Exception
	{
		//check this is indeed a named complex type, if it is anonymous, throw exception
		if (ct.isLocal()) throw new Exception("Expected anonymous (local) complex type, recieved named (global) complex type");
		//Make term from type
		Term term = new Term(ct.getName());
		term.addAttribute(new ac.technion.iem.ontobuilder.core.ontology.Attribute("name",ct.getName()));
		term.addAttribute(new ac.technion.iem.ontobuilder.core.ontology.Attribute("annotation",ct.getAnnotation()));
		term.setDomain(new Domain(ct.getName()));//TODO improve this by understanding the domain usage in ontobuilder matchers
		term.setSuperClass(xsdOntology.findClass(ct.getName())); //TODO this doesn't work since maybe the ontology class has not been created yet
		if (parent != null) term.setParent(parent);
		term.setOntology(xsdOntology);
		//Extend using base type if exists and complex
		if (ct.getDerivationMethod()==ComplexTypeImpl.EXTENSION && ct.getBaseType().isComplexType())
		{
			String ctName = ct.getBaseType().getName();
			if (!ctTerms.containsKey(ctName))
				ctTerms.put(ct.getBaseType().getName(),makeTermFromComplexType(null, ct.getBaseType().asComplexType()));
			Term baseT = ctTerms.get(ctName);
			for (Term baseSubT : baseT.getTerms())
			{
				baseT.addTerm(recCloneTerm(baseSubT));
			}
		}
		//Make subterms from elements if exist
		if (ct.getContentType().asParticle()!=null)
		{
			XSTerm xst = ct.getContentType().asParticle().getTerm();
			XSModelGroup g =  xst.asModelGroup();
			for (XSParticle p : g.getChildren())
			{
				if (p.getTerm().getClass() == ElementDecl.class) //simple sequence
					term.addTerm(makeTermFromElement(term,p.getTerm().asElementDecl()));
				else if (p.getTerm().getClass() == ModelGroupImpl.class) //sequence within a choice
				{
					for (XSParticle p1 : p.asParticle().getTerm().asModelGroup())
					{
						term.addTerm(makeTermFromElement(term,p1.getTerm().asElementDecl()));
					}
				}
			}
		}
		return term;
	}
	
}
