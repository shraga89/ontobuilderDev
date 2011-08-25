package ac.technion.iem.ontobuilder.core.ontology;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Vector;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import ac.technion.iem.ontobuilder.core.ontology.domain.GuessedDomain;
import ac.technion.iem.ontobuilder.core.ontology.operator.StringOperator;
import ac.technion.iem.ontobuilder.core.thesaurus.Thesaurus;
import ac.technion.iem.ontobuilder.core.utils.Email;
import ac.technion.iem.ontobuilder.core.utils.StringUtilities;

/**
 * <p>Title: OntologyUtilities</p>
 * <p>Description: Internal Ontology Utilities</p>
 */
public class OntologyUtilities
{
    /**
     * Add a term to the ontology
     * 
     * @param term the {@link Term} to add
     * @param ontology the {@link OntologyModel} to add to
     * @param termsList a list of {@link Term}
     */
    public static void addTermToOntology(Term term, Ontology ontology,
        ArrayList<Term> termsList)
    {
        term.setOntology(ontology);

        // Set classes
        OntologyClass ontologyClass = term.getSuperClass();
        if (ontologyClass != null)
        {
            OntologyClass newClass = ontology.findClass(ontologyClass.getName());
            term.superClass = newClass;
            if (term.superClass != null && !term.superClass.instances.contains(term))
                term.superClass.instances.add(term);
        }

        // Set relationships
        ArrayList<Relationship> relationshipsToRemove = new ArrayList<Relationship>();
        for (int j = 0; j < term.getRelationshipsCount(); j++)
        {
            Relationship r = term.getRelationship(j);
            Term rTarget = r.getTarget();
            if (!termsList.contains(rTarget))
                relationshipsToRemove.add(r);
        }
        for (Iterator<Relationship> j = relationshipsToRemove.iterator(); j.hasNext();)
            term.removeRelationship((Relationship) j.next());

        ontology.addTerm(term);
    }

    public static void buildClassHierarchy(OntologyClass newClass, OntologyClass oldClass)
    {
        for (Iterator<?> i = oldClass.instances.iterator(); i.hasNext();)
        {
            OntologyClass oldInstanceClass = (OntologyClass) i.next();
            if (oldInstanceClass instanceof Term)
                continue;
            OntologyClass newInstanceClass = (OntologyClass) oldInstanceClass.clone();
            newClass.instances.add(newInstanceClass);
            buildClassHierarchy(newInstanceClass, oldInstanceClass);
        }
    }

    public static String oneIdRemoval(String attr)
    {
        String tempStr = String.copyValueOf(attr.toCharArray());
        int i = tempStr.lastIndexOf(",");
        if (i != -1)
        {
            return tempStr.substring(0, i);
        }
        else
        {
            return tempStr;
        }
    }

    public static String toTermStringV2(String str)
    {
        int index = str.indexOf(":");
        if (index == -1)
            return str;
        else
        {
            return (str.substring(index + 1) + ":" + str.substring(0, index));

        }
    }

    /**
     * Checks if two terms are equal
     * 
     * @param t1 the first {@link Term}
     * @param t2 the second {@link Term}
     * @return <code>true</code> if they are equal
     */
    public static boolean isEqualTerms(Term t1, Term t2)
    {
        if (t1 == null || t2 == null)
            return false;
        String t1V1 = oneIdRemoval(t1.toString());
        String t1V2 = oneIdRemoval(t1.toStringVs2());
        String t2V1 = oneIdRemoval(t2.toString());
        String t2V2 = oneIdRemoval(t2.toStringVs2());
        return (t1V1.equals(t2V1) || t1V2.equals(t2V2));
    }

    public static ArrayList<Term> getTermsOfClass(Ontology ontology, String className)
    {
        Vector<?> terms = ontology.getTerms(true);
        ArrayList<Term> result = new ArrayList<Term>();
        for (Iterator<?> i = terms.iterator(); i.hasNext();)
        {
            Term term = (Term) i.next();
            if (term.isInstanceOf(className))
                result.add(term);
        }
        return result;
    }

    public static ArrayList<Term> filterTermListRemovingTermsOfClass(ArrayList<Term> terms,
        String className)
    {
        ArrayList<Term> result = new ArrayList<Term>();
        for (Iterator<Term> i = terms.iterator(); i.hasNext();)
        {
            Term term = (Term) i.next();
            if (!term.isInstanceOf(className))
                result.add(term);
        }
        return result;
    }

    public static ArrayList<Term> filterTermListRemovingTermsOfClass(ArrayList<Term> terms,
        ArrayList<?> classNames)
    {
        ArrayList<Term> result = terms;
        for (Iterator<?> i = classNames.iterator(); i.hasNext();)
        {
            String className = (String) i.next();
            result = filterTermListRemovingTermsOfClass(result, className);
        }
        return result;
    }

    public static ArrayList<Term> applyStringOperatorToTerms(ArrayList<?> terms,
        StringOperator operator)
    {
        ArrayList<Term> result = new ArrayList<Term>();
        for (Iterator<?> i = terms.iterator(); i.hasNext();)
        {
            Term term = (Term) ((Term) i.next()).applyStringOperator(operator);
            result.add(term);
        }
        return result;
    }

    public static double compareTermContents(Term targetTerm, Term candidateTerm)
    {
        return compareTermContents(targetTerm, candidateTerm, null, false);
    }

    public static double compareTermContents(Term targetTerm, Term candidateTerm,
        Thesaurus thesaurus)
    {
        return compareTermContents(targetTerm, candidateTerm, thesaurus, false);
    }

    public static double compareTermContents(Term targetTerm, Term candidateTerm,
        Thesaurus thesaurus, boolean useSoundex)
    {
        boolean print = false; // ((String)targetTerm.getAttributeValue("name")).equalsIgnoreCase("putime")
                               // &&
                               // ((String)candidateTerm.getAttributeValue("name")).equalsIgnoreCase("pktm");

        // double
        // domainSimilarity=DomainSimilarity.getDomainSimilarity(targetTerm.getDomain().getType(),candidateTerm.getDomain().getType());

        double runningEffectiveness = 0;
        double localEffectiveness = 0;

        if (targetTerm.getDomain().getEntriesCount() > 0 ||
            candidateTerm.getDomain().getEntriesCount() > 0)
        {
            for (int i = 0; i < candidateTerm.getDomain().getEntriesCount(); i++)
            {
                DomainEntry cDE = candidateTerm.getDomain().getEntryAt(i);
                Object cEntry = cDE.getEntry();
                if (cEntry instanceof Term)
                {
                    Term cTerm = (Term) cEntry;
                    localEffectiveness = 0;
                    for (int j = 0; j < targetTerm.getDomain().getEntriesCount(); j++)
                    {
                        DomainEntry tDE = targetTerm.getDomain().getEntryAt(j);
                        Object tEntry = tDE.getEntry();
                        if (tEntry instanceof Term)
                        {
                            Term tTerm = (Term) tEntry;
                            double tempEffectiveness = StringUtilities.getSubstringEffectivity(
                                tTerm.getName(), cTerm.getName(), thesaurus, useSoundex);
                            if (print)
                                System.out.println("\t\tContent Comparing " + tTerm.getName() +
                                    " --- " + cTerm.getName() + " with effectiveness: " +
                                    tempEffectiveness);
                            if (tempEffectiveness > localEffectiveness)
                                localEffectiveness = tempEffectiveness;
                        }
                    }
                    runningEffectiveness += localEffectiveness;
                }
            }
        }

        /*
         * if(domainSimilarity>0) return domainSimilarity +
         * (candidateTerm.getDomain().getEntriesCount
         * ()==0?0:runningEffectiveness/candidateTerm.getDomain
         * ().getEntriesCount())*(1-domainSimilarity); else
         */
        return candidateTerm.getDomain().getEntriesCount() == 0 ? 0 : runningEffectiveness /
            candidateTerm.getDomain().getEntriesCount();
    }

    public static double compareSymmetricTermContents(Term targetTerm, Term candidateTerm,
        double threshold)
    {
        return compareSymmetricTermContents(targetTerm, candidateTerm, threshold, null, false);
    }

    public static double compareSymmetricTermContents(Term targetTerm, Term candidateTerm,
        double threshold, Thesaurus thesaurus)
    {
        return compareSymmetricTermContents(targetTerm, candidateTerm, threshold, thesaurus, false);
    }

    public static double compareSymmetricTermContents(Term targetTerm, Term candidateTerm,
        double threshold, Thesaurus thesaurus, boolean useSoundex)
    {
        boolean print = false; // ((String)targetTerm.getAttributeValue("name")).equalsIgnoreCase("putime")
                               // &&
                               // ((String)candidateTerm.getAttributeValue("name")).equalsIgnoreCase("pktm");

        if (targetTerm.getDomain() == null || candidateTerm.getDomain() == null)
            return 0;

        threshold = 1;
        // double
        // domainSimilarity=DomainSimilarity.getDomainSimilarity(targetTerm.getDomain().getType(),candidateTerm.getDomain().getType());

        ArrayList<String> intersection = new ArrayList<String>();
        ArrayList<String> union = new ArrayList<String>();

        if (targetTerm.getDomain().getEntriesCount() > 0 &&
            candidateTerm.getDomain().getEntriesCount() > 0)
        {
            for (int i = 0; i < targetTerm.getDomain().getEntriesCount(); i++)
            {
                DomainEntry e = targetTerm.getDomain().getEntryAt(i);
                Object o = e.getEntry();
                if (o instanceof Term)
                {
                    String name = ((Term) o).getName();
                    StringUtilities.addWordToSet(union, thesaurus, useSoundex, name);
                }
            }

            int unionSize = union.size();
            for (int i = 0; i < candidateTerm.getDomain().getEntriesCount(); i++)
            {
                DomainEntry e = candidateTerm.getDomain().getEntryAt(i);
                Object o = e.getEntry();
                if (o instanceof Term)
                {
                    String name = ((Term) o).getName();
                    boolean addToUnion = true;
                    for (int j = 0; j < unionSize; j++)
                    {
                        String unionName = (String) union.get(j);
                        if (StringUtilities.getSymmetricSubstringEffectivity(name, unionName,
                            thesaurus) >= threshold)
                        {
                            StringUtilities.addWordToSet(intersection, thesaurus, useSoundex, name);
                            addToUnion = false;
                            break;
                        }
                    }
                    if (addToUnion)
                        StringUtilities.addWordToSet(union, thesaurus, useSoundex, name);
                }
            }
        }

        if (print)
        {
            System.out.println("\t\tUnion is: " + union);
            System.out.println("\t\tIntersection is: " + intersection);
        }

        /*
         * if(domainSimilarity>0) return domainSimilarity +
         * (union.size()==0?0:(double)intersection.size
         * ()/(double)union.size())*(1-domainSimilarity); else
         */
        return union.size() == 0 ? 0 : (double) intersection.size() / (double) union.size();
    }

  

 

    public final static short DOMAIN_TIME_HOUR_PART = 0;
    public final static short DOMAIN_TIME_MINUTE_PART = 1;
    public final static short DOMAIN_TIME_SECOND_PART = 2;
    public final static short DOMAIN_TIME_AMPM_PART = 3;
    public final static short DOMAIN_DATE_YEAR_PART = 4;
    public final static short DOMAIN_DATE_MONTH_PART = 5;
    public final static short DOMAIN_DATE_DAY_PART = 6;
    public final static short DOMAIN_URL_HOST_PART = 7;
    public final static short DOMAIN_URL_PROTOCOL_PART = 8;
    public final static short DOMAIN_URL_FILE_PART = 9;
    public final static short DOMAIN_URL_PORT_PART = 10;
    public final static short DOMAIN_EMAIL_USER_PART = 11;
    public final static short DOMAIN_EMAIL_DOMAIN_PART = 12;

    public static void fillDomain(Domain src, Domain dst, short type)
    {
        if (src.getEntriesCount() == 0)
            return;

        Calendar calendar = new GregorianCalendar();

        for (int i = 0; i < src.getEntriesCount(); i++)
        {
            Term entry = (Term) src.getEntryAt(i).getEntry();
            GuessedDomain gd = GuessedDomain.guessDomain(entry.getName());
            if (gd == null)
                continue;
            Term newEntry = null;
            Object object = gd.getObject();
            if (object instanceof Date)
            {
                calendar.setTime((Date) object);
                switch (type)
                {
                case DOMAIN_TIME_HOUR_PART:
                    newEntry = new Term(calendar.get(Calendar.HOUR) + "");
                    break;
                case DOMAIN_TIME_MINUTE_PART:
                    newEntry = new Term(calendar.get(Calendar.MINUTE) + "");
                    break;
                case DOMAIN_TIME_SECOND_PART:
                    newEntry = new Term(calendar.get(Calendar.SECOND) + "");
                    break;
                case DOMAIN_TIME_AMPM_PART:
                    newEntry = new Term(calendar.get(Calendar.AM_PM) == Calendar.PM ? "PM" : "AM");
                    break;
                case DOMAIN_DATE_YEAR_PART:
                    newEntry = new Term(calendar.get(Calendar.YEAR) + "");
                    break;
                case DOMAIN_DATE_MONTH_PART:
                    newEntry = new Term(GuessedDomain.months[calendar.get(Calendar.MONTH)]);
                    break;
                case DOMAIN_DATE_DAY_PART:
                    newEntry = new Term(calendar.get(Calendar.DAY_OF_MONTH) + "");
                    break;
                }
            }
            else if (object instanceof URL)
            {
                URL url = (URL) object;
                switch (type)
                {
                case DOMAIN_URL_HOST_PART:
                    newEntry = new Term(url.getHost());
                    break;
                case DOMAIN_URL_PROTOCOL_PART:
                    newEntry = new Term(url.getProtocol());
                    break;
                case DOMAIN_URL_PORT_PART:
                    newEntry = new Term(url.getPort() + "");
                    break;
                case DOMAIN_URL_FILE_PART:
                    newEntry = new Term(url.getFile());
                    break;
                }
            }
            else if (object instanceof Email)
            {
                Email email = (Email) object;
                switch (type)
                {
                case DOMAIN_EMAIL_USER_PART:
                    newEntry = new Term(email.getUser());
                    break;
                case DOMAIN_EMAIL_DOMAIN_PART:
                    newEntry = new Term(email.getDomain());
                    break;
                }
            }
            if (newEntry != null && !dst.isValidEntry(newEntry))
                dst.addEntry(new DomainEntry(dst, newEntry));
        }
    }

    public static void createBilzTalkMapperFile(Ontology source, Ontology target, File file)
        throws IOException
    {
        Element mapSource = new Element("mapsource");
        mapSource.setAttribute(new org.jdom.Attribute("name", file.getName()));
        mapSource.setAttribute(new org.jdom.Attribute("BizTalkServerMapperTool_Version", "1.0"));
        mapSource.setAttribute(new org.jdom.Attribute("version", "1"));
        mapSource.setAttribute(new org.jdom.Attribute("xrange", "100"));
        mapSource.setAttribute(new org.jdom.Attribute("yrange", "420"));

        Element sourceTree = new Element("srctree");
        sourceTree.addContent(source.getBizTalkRepresentation());

        Element targetTree = new Element("sinktree");
        targetTree.addContent(target.getBizTalkRepresentation());

        mapSource.addContent(sourceTree);
        mapSource.addContent(targetTree);
        mapSource.addContent(new Element("links"));
        mapSource.addContent(new Element("functions"));
        mapSource.addContent(new Element("CompiledXSL"));
        Document ontologyDocument = new Document(mapSource);

        BufferedWriter out = new BufferedWriter(new FileWriter(file));
        XMLOutputter fmt = new XMLOutputter("    ", true);
        fmt.output(ontologyDocument, out);
        out.close();
    }

    /**
     * Denormalize the terms
     * 
     * @param terms a list of terms
     * @return a list of denormalized terms
     */
    public static ArrayList<Term> denormalizeTerms(ArrayList<?> terms)
    {
        ArrayList<Term> list = new ArrayList<Term>();
        for (Iterator<?> i = terms.iterator(); i.hasNext();)
        {
            Term t = (Term) i.next();
            if (t.isDecomposedTerm())
                t = t.getParent();
            if (!list.contains(t))
                list.add(t);
        }
        return list;
    }
}