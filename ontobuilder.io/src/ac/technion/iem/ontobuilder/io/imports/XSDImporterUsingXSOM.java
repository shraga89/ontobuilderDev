package ac.technion.iem.ontobuilder.io.imports;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import ac.technion.iem.ontobuilder.core.ontology.Domain;
import ac.technion.iem.ontobuilder.core.ontology.DomainEntry;
import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.OntologyClass;
import ac.technion.iem.ontobuilder.core.ontology.Relationship;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.io.utils.xml.XSDEntityResolver;

import com.sun.xml.xsom.XSAttributeDecl;
import com.sun.xml.xsom.XSAttributeUse;
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
import com.sun.xml.xsom.impl.util.DraconianErrorHandler;
import com.sun.xml.xsom.parser.XSOMParser;

/**
 * <p>Title: XSDImporter Using XSOM </p>
 * Implements {@link Importer}
 * @author Tomer Sagi
 * Elements are mapped to terms. Element attributes are mapped to terms as well. Tree structure retained through 
 * Term-Subterm construct. 
 */

public class XSDImporterUsingXSOM implements Importer
{
	public static final HashMap<String,String> TypeDomMAP;
	
	static
    {
		TypeDomMAP = new HashMap<String, String>();
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
	private HashMap<String,Term> ctTerms = new HashMap<String,Term>(); //Terms created from declared context types, used when types are referenced inside complex elements
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
        XSSchemaSet result;
        XSOMParser parser;
        
        //Parse XSD
        try
        {
        	parser = new XSOMParser();
        	parser.setErrorHandler(new DraconianErrorHandler());
        	parser.setEntityResolver(new XSDEntityResolver());
        	parser.parse(file);
        	result = parser.getResult();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new ImportException("XML Schema Import failed");
        }
        
        //create classes and terms from result
        createOntology(result);
        //mine domain from instances
        if (instances!=null) 
        {
        	createDomainsFromInstances(file,instances);
        }
        	
        
        return xsdOntology;
    }

	/**
	 * Parses instances into ontology domains
	 * @param parser 
	 * @param instances
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
		builder.setErrorHandler(new DraconianErrorHandler());
		HashMap<String,ArrayList<String>> provMap = new HashMap<String,ArrayList<String>>();
		if (instances.isDirectory())
		{
			for (File i : instances.listFiles())
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
				HashSet<String> instanceSet = new HashSet<String>(provMap.get(t.getProvenance()));
				for(String i : instanceSet)
					d.addEntry(new DomainEntry(d,i));
				t.setDomain(d);
			}
			else
			{
				System.err.println("No instances found in provMap for " + t.getProvenance());
			}
				
		}
		} catch (SAXException e1) {
			e1.printStackTrace();
		} catch (ParserConfigurationException e1) {
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
				(provMap.containsKey(newProv)?provMap.get(newProv):new ArrayList<String>());
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
	private void createOntology(XSSchemaSet result) {
		
		
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
		for (XSSchema s : result.getSchemas())
		{
			Map<String, XSElementDecl> eMap = s.getElementDecls();
			for (XSElementDecl e : eMap.values())
			{
				Term t = makeTermFromElement(null,e);
				xsdOntology.addTerm(t);
			}
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
		boolean causedCycle = causesCycle(parent,complexElement);
		if (!complexElement.getType().isComplexType() || causedCycle)
			try {
				return makeTermFromSimpleElement(parent, complexElement, causedCycle);
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		Term t = makeTerm(parent,complexElement);
		
		//if Complex element's complex type is named make a term from it
		XSComplexType ct = complexElement.getType().asComplexType();
		if (ct.isGlobal())
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
		if(!(ct.getContentType().asEmpty()==ct.getContentType()))//if it is anonymous skip the type and make terms from the type's elements
		{
			if (ct.getContentType().asParticle() != null ) //simple content extension
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
			}
//			for (XSElementDecl e : ct.getElementDecls())
//				t.addTerm(makeTermFromElement(t, e));
		}
		//if term has attributes, make subterms from them:
		Collection<? extends XSAttributeUse> atts = getComplexAttributes(ct);
		for (XSAttributeUse attU : atts)
		{
			XSAttributeDecl attributeDecl = attU.getDecl();
			String n = attributeDecl.getName();
			Term aT = new Term(n);
			aT.addAttribute(new ac.technion.iem.ontobuilder.core.ontology.Attribute("name",n));
			aT.setDomain(new Domain(attributeDecl.getType().getName()));//TODO improve this by understanding the domain usage in ontobuilder matchers
		 	aT.setParent(t);
			aT.setOntology(xsdOntology);
			t.addTerm(aT);
		}
		
		return t;
	}
	
	/**
	 * Checks if the addition of the complexElement as a complex type to the 
	 * supplied parent will cause a cycle. 
	 * @param parent
	 * @param complexElement
	 * @return
	 */
	private boolean causesCycle(Term parent, XSElementDecl complexElement) {
		Term currentp = parent;
		while (currentp != null)
		{
			assert(currentp.getDomain() != null);
			assert(complexElement != null);
			assert(complexElement.getType() != null);
			if (currentp.getDomain().getName().equals(complexElement.getType().getName()))
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
			if (!e.getType().isSimpleType()) throw new Exception("Expected simple element, recieved complex element");
		return makeTerm(parent,e);
	}
	
	private Term makeTerm(Term parent, XSElementDecl e) {
		Term term = new Term(e.getName(),e.getDefaultValue());
		term.addAttribute(new ac.technion.iem.ontobuilder.core.ontology.Attribute("name",e.getName()));
		term.addAttribute(new ac.technion.iem.ontobuilder.core.ontology.Attribute("annotation",e.getAnnotation()));
		if (e.getType().isGlobal())
		{
			String t = e.getType().getName();
			String domName = (TypeDomMAP.containsKey(t)?TypeDomMAP.get(t):t);
								
			term.setDomain(new Domain(domName));
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
						if (p1.getTerm().getClass() == ElementDecl.class) //simple sequence
							term.addTerm(makeTermFromElement(term,p1.getTerm().asElementDecl()));
						else if (p1.getTerm().getClass() == ModelGroupImpl.class)
							for (XSParticle p2 : p1.asParticle().getTerm().asModelGroup())
							{term.addTerm(makeTermFromElement(term,p2.getTerm().asElementDecl()));}
					}
				}
			}
		}
		return term;
	}
	
	private static Collection<? extends XSAttributeUse> getComplexAttributes(XSComplexType xsComplexType) {
	    Collection<? extends XSAttributeUse> c = xsComplexType.getAttributeUses();
	    //Iterator<? extends XSAttributeUse> i = c.iterator();
	    //while(i.hasNext()) {
	       // i.next is attributeUse
	       //XSAttributeUse attUse = i.next();
	       //System.out.println("Attributes for ComplexType:"+ xsComplexType.getName());

	       //parseAttribute(attUse); //for debug
	       
	    //}
	return c;
	}

	// To Get attribute info - for debug usage

	@SuppressWarnings("unused")
	private static void parseAttribute(XSAttributeUse attUse) { 
	    XSAttributeDecl attributeDecl = attUse.getDecl();
	    System.out.println("Attribute Name:"+attributeDecl.getName());
	    XSSimpleType xsAttributeType = attributeDecl.getType();
	    System.out.println("Attribute Type: " + xsAttributeType.getName());
	    if (attUse.isRequired())
	        System.out.println("Use: Required");
	    else
	        System.out.println("Use: Optional");
	    System.out.println("Fixed: " + attributeDecl.getFixedValue());
	    System.out.println("Default: " + attributeDecl.getDefaultValue());
	}
}
