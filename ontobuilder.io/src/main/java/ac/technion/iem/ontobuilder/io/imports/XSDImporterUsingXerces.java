package ac.technion.iem.ontobuilder.io.imports;

import ac.technion.iem.ontobuilder.core.ontology.*;
import org.xml.sax.helpers.DefaultHandler;
import org.apache.xerces.impl.dv.XSSimpleType;
import org.apache.xerces.impl.xs.XSComplexTypeDecl;
import org.apache.xerces.impl.xs.XSElementDecl;
import org.apache.xerces.impl.xs.XSModelGroupImpl;
import org.apache.xerces.xs.*;
import org.w3c.dom.*;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.apache.xerces.xs.XSConstants.DERIVATION_EXTENSION;
import static org.apache.xerces.xs.XSConstants.ELEMENT_DECLARATION;
import static org.apache.xerces.xs.XSTypeDefinition.COMPLEX_TYPE;
import static org.apache.xerces.xs.XSTypeDefinition.SIMPLE_TYPE;


/**
 * <p>Title: XSDImporter Using XSOM </p>
 * Implements {@link Importer}
 * @author Tomer Sagi
 * Elements are mapped to terms. Element attributes are mapped to terms as well. Tree structure retained through 
 * Term-Subterm construct. 
 */

public class XSDImporterUsingXerces implements Importer
{
	public static final HashMap<String,String> TypeDomMAP;
	
	static
    {
		TypeDomMAP = new HashMap<>();
		TypeDomMAP.put("decimal", "Float");
		TypeDomMAP.put("byte", "Integer");
		TypeDomMAP.put("int", "Integer");
		TypeDomMAP.put("integer", "Integer");
		TypeDomMAP.put("long", "Integer");
		TypeDomMAP.put("short", "Integer");
		TypeDomMAP.put("negativeInteger", "Negative Integer");
		TypeDomMAP.put("nonNegativeInteger", "Positive Integer");
		TypeDomMAP.put("nonPositiveInteger", "Negative Integer");
		TypeDomMAP.put("positiveInteger", "Positive Integer");
		TypeDomMAP.put("unsignedLong", "Positive Integer");
		TypeDomMAP.put("unsignedInt", "Positive Integer");
		TypeDomMAP.put("unsignedshort", "Positive Integer");
		TypeDomMAP.put("unsignedByte", "Positive Integer");
		TypeDomMAP.put("date", "Date");
		TypeDomMAP.put("time", "Time");
		TypeDomMAP.put("ENTITIES", "Text");
		TypeDomMAP.put("ENTITY", "Text");
		TypeDomMAP.put("ID", "Text");
		TypeDomMAP.put("IDREF", "Text");
		TypeDomMAP.put("IDREFS", "Text");
		TypeDomMAP.put("language", "Text");
		TypeDomMAP.put("Name", "Text");
		TypeDomMAP.put("NCName", "Text");
		TypeDomMAP.put("NMTOKEN", "Text");
		TypeDomMAP.put("normalizedString", "Text");
		TypeDomMAP.put("QName", "Text");
		TypeDomMAP.put("string", "Text");
		TypeDomMAP.put("token", "Text");
		TypeDomMAP.put("boolean", "Boolean");
		TypeDomMAP.put("anyURI", "URL");
    }
	
	private Ontology xsdOntology;
	private final HashMap<String,Term> ctTerms = new HashMap<>(); //Terms created from declared context types, used when types are referenced inside complex elements
    /**
     * Imports an ontology from an XSD file. Searches for an instance file within the xsd path
     * 
     * @param file the {@link File} to read the ontology from
     * @throws ImportException when the XML Schema import failed
     */
	public Ontology importFile(File file) throws ImportException
    {
		String path = file.getParent() + File.separatorChar + file.getName().substring(0, file.getName().length()-4);
		File instance = new File(path + ".xml");
		if (!instance.exists())
			instance = new File(path);
		
		if (instance.exists())
			return importFile(file,instance);
		else
			return importFile(file,null);
    }
	
	/**
     * Imports an ontology from an XSD file and mines domain entries
     * from the instances supplied in the xml file
     * 
     * @param file the {@link File} to read the ontology from
     * @throws ImportException when the XML Schema import failed
     */
	public Ontology importFile(File file, File instances) throws ImportException
    {
        // create an empty ontology with the name of the file
        xsdOntology = new Ontology(file.getName().substring(0, file.getName().length()-4));
        xsdOntology.setFile(file);
        xsdOntology.setLight(true);
		System.setProperty(
				DOMImplementationRegistry.PROPERTY,
				"org.apache.xerces.dom.DOMXSImplementationSourceImpl"
		);
		DOMImplementationRegistry registry;
		try {
			registry = DOMImplementationRegistry.newInstance();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			throw new ImportException("XML Schema Import failed");
		}
		XSImplementation impl = (XSImplementation) registry.getDOMImplementation("XS-Loader");
		XSLoader schemaLoader = impl.createXSLoader(null);
		DOMConfiguration config = schemaLoader.getConfig();
		// create Error Handler
		DOMErrorHandler errorHandler = domError -> {
			System.err.println("DOMError: " + domError.getMessage());
			Object relatedException = domError.getRelatedException();
			if (relatedException != null) {
				System.err.println("DOMError: " + relatedException);
				if (relatedException instanceof Throwable) {
					((Throwable) relatedException).printStackTrace(System.out);
				}
			}
			return false;
		};
		// set error handler
		config.setParameter("error-handler", errorHandler);
		// set validation feature
		config.setParameter("validate", Boolean.TRUE);
		// parse document
//		config.setParameter("http://apache.org/xml/properties/internal/entity-resolver", new CatalogResolver());
		XSModel model = schemaLoader.loadURI(file.getAbsolutePath());
		if (model == null) {
			return null;
		}
		//From https://plugins.jetbrains.com/plugin/7525-hybris-integration
		//		XSNamedMap components = model.getComponents(XSConstants.ELEMENT_DECLARATION);
		//		for (int i = 0; i < components.getLength(); i++) {
		//			XSObject obj = components.item(i);
		//			QName qname = new QName(obj.getNamespace(), obj.getName());
		//			String file = this.model.qname2FileMap.get(qname);
		//			this.model.qname2FileMap.put(qname, (file == null ? "" : file + ";") + schemaFile.getName());
		//		}
		//		components = model.getComponents(XSConstants.TYPE_DEFINITION);
		//		for (int i = 0; i < components.getLength(); i++) {
		//			XSObject obj = components.item(i);
		//			QName qname = new QName(obj.getNamespace(), obj.getName());
		//			String file = this.model.qname2FileMap.get(qname);
		//			this.model.qname2FileMap.put(qname, (file == null ? "" : file) + ":" + schemaFile.getName() + ":");
		//		}
		//		return model;



        //create classes and terms from result
        createOntology(model);
        //mine domain from instances
        if (instances!=null) 
        {
        	createDomainsFromInstances(file,instances);
        }
        	
        
        return xsdOntology;
    }

	/**
	 * Parses instances into ontology domains
	 * @param schema File object containing the XSD schema
	 * @param instances File object containing the instances
	 */
	private void createDomainsFromInstances(File schema, File instances) {

		try {		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setNamespaceAware(true);
		SchemaFactory schemaFactory = 
			    SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
			factory.setSchema(schemaFactory.newSchema(
				    new Source[] {new StreamSource(schema)}));
		DocumentBuilder builder = factory.newDocumentBuilder();
		builder.setErrorHandler(new DefaultHandler());
		HashMap<String,ArrayList<String>> provMap = new HashMap<>();
		if (instances.isDirectory())
		{
			for (File i : Objects.requireNonNull(instances.listFiles()))
			{
				try {
					Document document = builder.parse(i);
					getNodesRecursive("",document.getFirstChild(),provMap);
				} catch (Exception e) {
				
					System.err.println("Failed import of " + i.getName() + "\n");
					System.err.println(e.getMessage());
				
			} 
				
			}
		} else
		{
			Document document;
			try {
				document = builder.parse(instances);
				getNodesRecursive("",document.getFirstChild(),provMap);
			} catch (SAXException e) {
				System.err.println("Instance file parse error: \n" );
				e.printStackTrace();
			} catch (IOException e) {
				System.err.println("Instance file load error: \n" );
				e.printStackTrace();
			}
		}
		//Create domains
		for (Term t : xsdOntology.getTerms(true))
		{
			if (provMap.containsKey(t.getProvenance()))
			{
				Domain d = t.getDomain();
				HashSet<String> instanceSet = new HashSet<>(provMap.get(t.getProvenance()));
				for(String i : instanceSet)
					d.addEntry(new DomainEntry(d,i));
				t.setDomain(d);
			}
			else
			{
				System.err.println("No instances found in provMap for " + t.getProvenance());
			}
				
		}
		} catch (SAXException | ParserConfigurationException e1) {
			e1.printStackTrace();
		}

	}

	private void getNodesRecursive(String prov ,Node node,
			HashMap<String,ArrayList<String>> provMap) {
		
		String nodeName = node.getNodeName().replaceAll("ns:", "");
		String newProv = prov + (prov.length()==0?"":".") + nodeName;
		for (int i=0;i<node.getChildNodes().getLength();i++)
		{
			Node child = node.getChildNodes().item(i);
			switch (child.getNodeType())
			{
			case Node.TEXT_NODE:
				ArrayList<String> instanceList = 
				(provMap.containsKey(newProv)?provMap.get(newProv):new ArrayList<>());
				instanceList.add(child.getNodeValue());
				provMap.put(newProv,instanceList);
				break;
			case Node.ATTRIBUTE_NODE:
				getNodesRecursive(newProv,child,provMap);
				break;
			case Node.ELEMENT_NODE:
				getNodesRecursive(newProv,child,provMap);
				break;
				
			}
			
			NamedNodeMap atts = child.getAttributes();
			if (atts !=null)
			{
				for (int j=0 ; j< atts.getLength();j++)
				{
					Node n = atts.item(j);
					getNodesRecursive(newProv + "." + child.getNodeName(),n,provMap);
				}
				
			}
		}
	
	}

	/**
	 * Translate parsed schema to ontology:
	 * 1. Make an OntologyClass object for each simple type and complex type
	 * 2. Connect between each complex type's OntologyClass and it's base type's OntologyClass
	 * 3. Make term tree from elements by:
	 * 		1. recursively adding complex type elements as subterms
	 * 		2. replacing extension markers with terms from the referenced type
	 * @param result parsed XSD
	 */
	private void createOntology(XSModel result) {
		
		
		//Make OntologyClass object for each simple type
		XSNamedMap simpleTypes = result.getComponents(SIMPLE_TYPE);
		OntologyClass ontologyClass;
		for (int i=0; i<simpleTypes.getLength() ;i++) {
			XSSimpleType newType = (XSSimpleType)simpleTypes.item(i);
			xsdOntology.addClass(createOntologyClass(xsdOntology,newType));
		}
		
		//Add ontology class for each complex type
		HashMap<OntologyClass,ArrayList<OntologyClass>> relationships = new HashMap<>();
		XSNamedMap complexTypes = result.getComponents(COMPLEX_TYPE);
		for (int i=0; i<complexTypes.getLength();i++)
		{
			XSComplexTypeDefinition newType = (XSComplexTypeDefinition)complexTypes.item(i);
			//Make ontology class
			ontologyClass = createOntologyClass(xsdOntology,newType);
			//Record parent child relationships to later create them in classes
			ArrayList<OntologyClass> superClasses;
			XSTypeDefinition baseType = newType.getBaseType();
			if (!baseType.equals(newType)) //if no base type, it will reference itself
			{
				if (relationships.containsKey(ontologyClass))
				{
					superClasses = relationships.get(ontologyClass);
				}
				else
				{
					superClasses = new ArrayList<>();
				}
				superClasses.add(createOntologyClass(xsdOntology,baseType));
				relationships.put(ontologyClass, superClasses);
			}
			xsdOntology.addClass(ontologyClass);
		}


		for (int i=0; i<complexTypes.getLength();i++)
		{ 
			
			XSTypeDefinition newType = (XSTypeDefinition)complexTypes.item(i);
			//make Term and collect, Will be used to create terms in complex elements using this complex type
			Term classT;
			try {
				classT = makeTermFromComplexType(null, newType);
				this.ctTerms.put(newType.getName(), classT);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
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
		XSNamedMap elementDecls = result.getComponents(ELEMENT_DECLARATION);
		for (int i=0; i<elementDecls.getLength();i++)
		{
			Term t = makeTermFromElement(null,(XSElementDecl)elementDecls.item(i));
			xsdOntology.addTerm(t);
		}
	}

	/**
	 * @param xsdOntology to add the class to
	 * @param newType type definition to use to create a class from
	 * @return a new OntologyClass
	 */
	private OntologyClass createOntologyClass(Ontology xsdOntology,
			XSTypeDefinition newType) {
		OntologyClass ontologyClass = new OntologyClass(newType.getName());
		ontologyClass.setOntology(xsdOntology);
		//newType.getAnnotation() TODO
		return ontologyClass;
	}

    /**
     * 
     * @param parent term to associate with
     * @param complexElement to make a term from
     * @return new Term
     */
	private Term makeTermFromElement(Term parent, XSElementDecl complexElement)
	{
		//TODO check why simple elements are not assigned the correct OntologyClass
		//Make sure this is a complex element, if not, return a simple element
		//debug: System.out.println("complex element:" + complexElement.getName());
		boolean causedCycle = causesCycle(parent,complexElement);
		if (complexElement.getTypeDefinition().getTypeCategory()!=COMPLEX_TYPE || causedCycle)
			try {
				return makeTermFromSimpleElement(parent, complexElement, causedCycle);
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		Term t = makeTerm(parent,complexElement);
		
		//if Complex element's complex type is named make a term from it
		XSComplexTypeDecl ct = (XSComplexTypeDecl)complexElement.getTypeDefinition();
		if (!ct.getAnonymous())
		{
			try {
				String ctName = ct.getName();
				if (!ctTerms.containsKey(ctName))
					ctTerms.put(ctName, makeTermFromComplexType(t, ct));
				Term ctTerm = ctTerms.get(ct.getName());
				t = recCloneTerm(ctTerm);
				//If names have changed, remove the subterms' relationships to this term since they will be reset when the complexType is instantiated
				if (!t.getName().equals(complexElement.getName()) && t.getTermsCount()>0)
					for (Term subT : t.getTerms())
					{
						subT.removeRelationship(subT,"is child of",ctTerm);
						subT.addRelationship(new Relationship(subT,"is child of",t));
					}
				t.setName(complexElement.getName());
				if (parent != null)
				{
					t.setParent(parent);
					t.addRelationship(new Relationship(t,"is child of",parent));
				}
				t.setAttributeValue("name", complexElement.getName());
				
				//t.addTerm(newT);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else
		if(!(ct.getContentType()==XSComplexTypeDefinition.CONTENTTYPE_EMPTY))//if it is anonymous skip the type and make terms from the type's elements
		{
			if (!(ct.getContentType()==XSComplexTypeDefinition.CONTENTTYPE_SIMPLE)) //simple content extension
			{
				XSTerm xst = ct.getParticle().getTerm();
				XSModelGroup g =  (XSModelGroup)xst;
				for (int i=0; i<g.getParticles().getLength(); i++)
				{
					XSParticle p = (XSParticle)g.getParticles().item(i);
					if(p.getTerm().getClass().equals(XSModelGroupImpl.class)) //extension
					{
						XSObjectList particles = ((XSModelGroupImpl)p.getTerm()).getParticles();
						for (int j=0;j<particles.getLength();j++) {
							XSParticle p1 = (XSParticle)particles.item(j);
							t.addTerm(makeTermFromElement(t,(XSElementDecl)p1.getTerm()));
						}

					}
					else
					{t.addTerm(makeTermFromElement(t,(XSElementDecl)p.getTerm()));}
				}
			}
//			for (XSElementDecl e : ct.getElementDecls())
//				t.addTerm(makeTermFromElement(t, e));
		}
		//if term has attributes, make subterms from them:
		Collection<? extends XSAttributeUse> atts = getComplexAttributes(ct);
		for (XSAttributeUse attU : atts)
		{
			XSAttributeDeclaration attributeDecl = attU.getAttrDeclaration();
			String n = attributeDecl.getName();
			Term aT = new Term(n);
			aT.addAttribute(new ac.technion.iem.ontobuilder.core.ontology.Attribute("name",n));
			aT.setDomain(new Domain(attributeDecl.getTypeDefinition().getName()));//TODO improve this by understanding the domain usage in ontobuilder matchers
		 	aT.setParent(t);
			aT.setOntology(xsdOntology);
			t.addTerm(aT);
		}
		
		return t;
	}
	
	/**
	 * Checks if the addition of the complexElement as a complex type to the 
	 * supplied parent will cause a cycle. 
	 * @param parent : suggested parent of complex type
	 * @param complexElement to add as a complex type
	 * @return true if the addition will cause a cycle
	 */
	private boolean causesCycle(Term parent, XSElementDecl complexElement) {
		Term currentp = parent;
		while (currentp != null)
		{
			assert(currentp.getDomain() != null);
			assert(complexElement != null);
			assert(complexElement.getTypeDefinition() != null);
			if (currentp.getDomain().getName().equals(complexElement.getTypeDefinition().getName()))
				return true;
			currentp = currentp.getParent();
		}
		return false;
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
	 * @param parent Term to which this Term will be connected, used to set the parent and ontology properties of new Term
	 * @param e element to be converted
	 * @param forceComplex forces conversion of a complex element to a simple element. 
	 * @return Term which is assumed to be later added to parent term
	 * @throws Exception if not a simple element
	 */
	private Term makeTermFromSimpleElement(Term parent,XSElementDecl e, boolean forceComplex) throws Exception
	{
		if (!forceComplex)
			if (e.getTypeDefinition().getTypeCategory()!=SIMPLE_TYPE) throw new Exception("Expected simple element, received complex element");
		return makeTerm(parent,e);
	}
	
	private Term makeTerm(Term parent, XSElementDecl e) {
		Term term = new Term(e.getName());
		term.addAttribute(new ac.technion.iem.ontobuilder.core.ontology.Attribute("name",e.getName()));
		term.addAttribute(new ac.technion.iem.ontobuilder.core.ontology.Attribute("annotation",e.getAnnotation()));
		if (!e.getTypeDefinition().getAnonymous())
		{
			String t = e.getTypeDefinition().getName();
			String domName = (TypeDomMAP.getOrDefault(t, t));
								
			term.setDomain(new Domain(domName));
			term.setSuperClass(xsdOntology.findClass(e.getTypeDefinition().getName()));
		}
		if (parent != null) 
		 	term.setParent(parent);
		
		term.setOntology(xsdOntology);
		return term;
		
	}

	/**
	 * Assumes it receives a named complex type and makes a term from it. If the complex type is anonymous (local) throws an exception
	 * @param parent Term to which this term will refer. Assumes the term created will be added to the parent term after this method is called
	 * @param ct named complex type to work on
	 * @return null if term cannot be created due to a missing complex type or the created term otherwise
	 * @throws Exception if the type is a simple type
	 */
	private Term makeTermFromComplexType(Term parent, XSTypeDefinition ct) throws Exception
	{
		//check this is indeed a named complex type, if it is anonymous, throw exception
		if (ct.getAnonymous()) throw new Exception("Expected anonymous (local) complex type, received named (global) complex type");
		//Make term from type
		Term term = new Term(ct.getName());
		term.addAttribute(new ac.technion.iem.ontobuilder.core.ontology.Attribute("name",ct.getName()));
		XSObjectList annotations = ((XSComplexTypeDecl)ct).getAnnotations();
		if (annotations!=null && !(annotations.getLength()==0))
		{
			StringBuilder annotation_str = new StringBuilder();
			for (int i=0;i<annotations.getLength();i++)
				annotation_str.append(((XSAnnotation) annotations.item(i)).getAnnotationString());
			term.addAttribute(new ac.technion.iem.ontobuilder.core.ontology.Attribute("annotation", annotation_str.toString()));
		}

		term.setDomain(new Domain(ct.getName()));//TODO improve this by understanding the domain usage in ontobuilder matchers
		term.setSuperClass(xsdOntology.findClass(ct.getName())); //TODO this doesn't work since maybe the ontology class has not been created yet
		if (parent != null) term.setParent(parent);
		term.setOntology(xsdOntology);
		//Extend using base type if exists and complex
		if (ct.getFinal()==DERIVATION_EXTENSION && ct.getBaseType().getTypeCategory()== COMPLEX_TYPE)
		{
			String ctName = ct.getBaseType().getName();
			if (!ctTerms.containsKey(ctName))
				ctTerms.put(ct.getBaseType().getName(),makeTermFromComplexType(null, ct.getBaseType()));
			Term baseT = ctTerms.get(ctName);
			for (Term baseSubT : baseT.getTerms())
			{
				baseT.addTerm(recCloneTerm(baseSubT));
			}
		}
		//Make subterms from elements if exist
		XSComplexTypeDefinition ctd = (XSComplexTypeDefinition) ct;
		if (ctd.getContentType()==XSComplexTypeDefinition.CONTENTTYPE_ELEMENT ||
				ctd.getContentType()==XSComplexTypeDefinition.CONTENTTYPE_MIXED)
		{
			XSTerm xst = ctd.getParticle().getTerm();
			XSModelGroup g =  (XSModelGroup) xst;
			XSObjectList particles =g.getParticles();
			for (int i=0;i<particles.getLength();i++)
			{
				XSParticle p = (XSParticle)particles.item(i);
				if (p.getTerm().getClass().equals(XSElementDecl.class)) //simple sequence
					term.addTerm(makeTermFromElement(term,(XSElementDecl)p.getTerm()));
				else if (p.getTerm().getClass().equals(XSModelGroupImpl.class)) //sequence within a choice
				{
					XSObjectList particles1 = ((XSModelGroupImpl)p.getTerm()).getParticles();
					for (int j=0;j<particles1.getLength();j++)
					{
						XSParticle p1 = (XSParticle)particles1.item(j);
						if (p1.getTerm().getClass().equals(XSElementDecl.class)) //simple sequence
							term.addTerm(makeTermFromElement(term,(XSElementDecl)p1.getTerm()));
						else if (p1.getTerm().getClass().equals(XSModelGroupImpl.class)) {
							XSObjectList particles2 = ((XSModelGroupImpl) p1.getTerm()).getParticles();
							for (int k = 0; k < particles2.getLength(); k++) {
									XSParticle p2 = (XSParticle) particles2.item(k);
									term.addTerm(makeTermFromElement(term, (XSElementDecl) p2.getTerm()));
								}
						}
					}
				}
			}
		}
		return term;
	}
	
	private static Collection<? extends XSAttributeUse> getComplexAttributes(XSComplexTypeDecl xsComplexType) {
	    List<XSAttributeUse> c = new ArrayList<>();
		XSObjectList l = xsComplexType.getAttributeUses();
	    for (int i=0;i< l.getLength();i++) {
	       // i.next is attributeUse
	       XSAttributeUse attUse = (XSAttributeUse)l.item(i);
	       //System.out.println("Attributes for ComplexType:"+ xsComplexType.getName());

	       //parseAttribute(attUse); //for debug
	       c.add(attUse);
	    }
	return c;
	}

	// To Get attribute info - for debug usage

	@SuppressWarnings("unused")
	private static void parseAttribute(XSAttributeUse attUse) { 
	    XSAttributeDeclaration attributeDecl = attUse.getAttrDeclaration();
	    System.out.println("Attribute Name:"+attributeDecl.getName());
	    XSSimpleTypeDefinition xsAttributeType = attributeDecl.getTypeDefinition();
	    System.out.println("Attribute Type: " + xsAttributeType.getName());
	    if (attUse.getRequired())
	        System.out.println("Use: Required");
	    else
	        System.out.println("Use: Optional");
	    System.out.println("Actual value: " + attributeDecl.getValueConstraintValue());
	}
}
