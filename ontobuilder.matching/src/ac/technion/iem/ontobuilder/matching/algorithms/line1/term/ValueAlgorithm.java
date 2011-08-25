package ac.technion.iem.ontobuilder.matching.algorithms.line1.term;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.OntologyUtilities;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.core.ontology.domain.DomainSimilarity;
import ac.technion.iem.ontobuilder.core.utils.properties.PropertiesHandler;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.Algorithm;

/**
 * <p>
 * Title: ValueAlgorithm
 * </p>
 * Extends a {@link TermAlgorithm}
 */
public class ValueAlgorithm extends TermAlgorithm
{
    /**
     * Make a copy of the algorithm instance
     */
    public Algorithm makeCopy()
    {
        ValueAlgorithm algorithm = new ValueAlgorithm();
        algorithm.pluginName = pluginName;
        algorithm.mode = mode;
        algorithm.thesaurus = thesaurus;
        algorithm.termPreprocessor = termPreprocessor;
        algorithm.threshold = threshold;
        algorithm.effectiveness = effectiveness;
        return algorithm;
    }

    /**
     * Constructs a default ValueAlgorithm
     */
    public ValueAlgorithm()
    {
        super();
    }

    /**
     * Get the name
     * 
     * @return the name
     */
    public String getName()
    {
        return PropertiesHandler.getResourceString("algorithm.value");
    }

    /**
     * Get the description
     * 
     * @return the description
     */
    public String getDescription()
    {
        return PropertiesHandler.getResourceString("algorithm.value.description");
    }

    /**
     * Configure the algorithm parameters when user changes one of the values of the JTable
     * 
     * @param element the {@link Element} with the parameters to configure
     */
    public void configure(Element element)
    {
        Element parametersElement = element.getChild("parameters");
        if (parametersElement == null)
            return;
        List<?> parametersList = parametersElement.getChildren("parameter");
        for (Iterator<?> i = parametersList.iterator(); i.hasNext();)
        {
            Element parameterElement = (Element) i.next();
            String name = parameterElement.getChild("name").getText();
            if (name.equals("symmetric") || name.equals("useThesaurus") ||
                name.equals("useSoundex"))
            {
                boolean value = Boolean.valueOf(parameterElement.getChild("value").getText())
                    .booleanValue();
                if (name.equals("symmetric") && value)
                    mode += TermAlgorithmFlagsEnum.SYMMETRIC_FLAG.getValue();
                else if (name.equals("useThesaurus") && value)
                    mode += TermAlgorithmFlagsEnum.USE_THESAURUS_FLAG.getValue();
                else if (name.equals("useSoundex") && value)
                    mode += TermAlgorithmFlagsEnum.USE_SOUNDEX_FLAG.getValue();
            }
        }

        super.configure(element);
    }

    /**
     * Get the terms to match
     * 
     * @param targetOntology the target {@link Ontology}
     * @param candidateOntology the candidate {@link Ontology}
     */
    protected void getTermsToMatch(Ontology targetOntology, Ontology candidateOntology)
    {
        super.getTermsToMatch(targetOntology, candidateOntology);
        if (!targetOntology.isLight())
        {
            originalTargetTerms.addAll(OntologyUtilities.getTermsOfClass(targetOntology,
                "decomposition"));
        }
        else
        {
            originalTargetTerms = new ArrayList<Term>(targetOntology.getTerms(true));
        }

        if (!candidateOntology.isLight())
        {
            originalCandidateTerms.addAll(OntologyUtilities.getTermsOfClass(candidateOntology,
                "decomposition"));
        }
        else
        {
            originalCandidateTerms = new ArrayList<Term>(candidateOntology.getTerms(true));
        }

    }

    /**
     * Match Comparator methods
     * 
     * @param targetTerm the target {@link Term}
     * @param candidateTerm the candidate {@link Term}
     */
    public boolean compare(Term targetTerm, Term candidateTerm)
    {
        effectiveness = 0;

        boolean print = false;// ((String)targetTerm.getAttributeValue("name")).equalsIgnoreCase("putime")
                              // &&
                              // ((String)candidateTerm.getAttributeValue("name")).equalsIgnoreCase("pkday");

        if (print)
            System.out.println("****************************************************");
        if (print)
            System.out.println("Comparing " + targetTerm + " (" + targetTerm.getDomain().getName() +
                ") vs. " + candidateTerm + " (" + candidateTerm.getDomain().getName() + ")");

        double domainSimilarity = DomainSimilarity.getDomainSimilarity(targetTerm.getDomain()
            .getType(), candidateTerm.getDomain().getType());

        if (targetTerm.getDomain().getType().equals("date") &&
            candidateTerm.getDomain().getType().equals("date"))
        {
            Term tday = targetTerm.getTerm(0);
            Term cday = candidateTerm.getTerm(0);
            double dayEffectiveness = 0;
            if (tday != null && cday != null)
            {
                if ((mode & TermAlgorithmFlagsEnum.SYMMETRIC_FLAG.getValue()) != 0)
                    dayEffectiveness = OntologyUtilities.compareSymmetricTermContents(tday, cday,
                        threshold, null, false);
                else
                    dayEffectiveness = OntologyUtilities.compareTermContents(tday, cday, null,
                        false);
            }

            Term tmonth = targetTerm.getTerm(1);
            Term cmonth = candidateTerm.getTerm(1);
            double monthEffectiveness = 0;
            if (tmonth != null && cmonth != null)
            {
                if ((mode & TermAlgorithmFlagsEnum.SYMMETRIC_FLAG.getValue()) != 0)
                    monthEffectiveness = OntologyUtilities.compareSymmetricTermContents(tmonth,
                        cmonth, threshold, null, false);
                else
                    monthEffectiveness = OntologyUtilities.compareTermContents(tmonth, cmonth,
                        null, false);
            }

            Term tyear = targetTerm.getTerm(2);
            Term cyear = candidateTerm.getTerm(2);
            double yearEffectiveness = 0;
            if (tyear != null && cyear != null)
            {
                if ((mode & TermAlgorithmFlagsEnum.SYMMETRIC_FLAG.getValue()) != 0)
                    yearEffectiveness = OntologyUtilities.compareSymmetricTermContents(tyear,
                        cyear, threshold, null, false);
                else
                    yearEffectiveness = OntologyUtilities.compareTermContents(tyear, cyear, null,
                        false);
            }

            if (print)
            {
                System.out.println("\tEffectivity for day: " + dayEffectiveness);
                System.out.println("\tEffectivity for month: " + monthEffectiveness);
                System.out.println("\tEffectivity for year: " + yearEffectiveness);
            }

            effectiveness = (dayEffectiveness + monthEffectiveness + yearEffectiveness) / 3;
        }
        else if (targetTerm.getDomain().getType().equals("time") &&
            candidateTerm.getDomain().getType().equals("time"))
        {
            Term thour = targetTerm.getTerm(0);
            Term chour = candidateTerm.getTerm(0);
            double hourEffectiveness = 0;
            if (thour != null && chour != null)
            {
                if ((mode & TermAlgorithmFlagsEnum.SYMMETRIC_FLAG.getValue()) != 0)
                    hourEffectiveness = OntologyUtilities.compareSymmetricTermContents(thour,
                        chour, threshold, null, false);
                else
                    hourEffectiveness = OntologyUtilities.compareTermContents(thour, chour, null,
                        false);
            }

            Term tminute = targetTerm.getTerm(1);
            Term cminute = candidateTerm.getTerm(1);
            double minuteEffectiveness = 0;
            if (tminute != null && cminute != null)
            {
                if ((mode & TermAlgorithmFlagsEnum.SYMMETRIC_FLAG.getValue()) != 0)
                    minuteEffectiveness = OntologyUtilities.compareSymmetricTermContents(tminute,
                        cminute, threshold, null, false);
                else
                    minuteEffectiveness = OntologyUtilities.compareTermContents(tminute, cminute,
                        null, false);
            }

            Term tsecond = targetTerm.getTerm(2);
            Term csecond = candidateTerm.getTerm(2);
            double secondEffectiveness = 0;
            if (tsecond != null && csecond != null)
            {
                if ((mode & TermAlgorithmFlagsEnum.SYMMETRIC_FLAG.getValue()) != 0)
                    secondEffectiveness = OntologyUtilities.compareSymmetricTermContents(tsecond,
                        csecond, threshold, null, false);
                else
                    secondEffectiveness = OntologyUtilities.compareTermContents(tsecond, csecond,
                        null, false);
            }

            Term tampm = targetTerm.getTerm(3);
            Term campm = candidateTerm.getTerm(3);
            double ampmEffectiveness = 0;
            if (tampm != null && campm != null)
            {
                if ((mode & TermAlgorithmFlagsEnum.SYMMETRIC_FLAG.getValue()) != 0)
                    ampmEffectiveness = OntologyUtilities.compareSymmetricTermContents(tampm,
                        campm, threshold, null, false);
                else
                    ampmEffectiveness = OntologyUtilities.compareTermContents(tampm, campm, null,
                        false);
            }

            if (print)
            {
                System.out.println("\tEffectivity for hour: " + hourEffectiveness);
                System.out.println("\tEffectivity for minute: " + minuteEffectiveness);
                System.out.println("\tEffectivity for second: " + secondEffectiveness);
                System.out.println("\tEffectivity for am/pm: " + ampmEffectiveness);
            }

            effectiveness = (hourEffectiveness + minuteEffectiveness + secondEffectiveness + ampmEffectiveness) / 4;
        }
        else if (targetTerm.getDomain().getType().equals("url") &&
            candidateTerm.getDomain().getType().equals("url"))
        {
            Term tprotocol = targetTerm.getTerm(0);
            Term cprotocol = candidateTerm.getTerm(0);
            double protocolEffectiveness = 0;
            if (tprotocol != null && cprotocol != null)
            {
                if ((mode & TermAlgorithmFlagsEnum.SYMMETRIC_FLAG.getValue()) != 0)
                    protocolEffectiveness = OntologyUtilities.compareSymmetricTermContents(
                        tprotocol, cprotocol, threshold, null, false);
                else
                    protocolEffectiveness = OntologyUtilities.compareTermContents(tprotocol,
                        cprotocol, null, false);
            }

            Term tport = targetTerm.getTerm(1);
            Term cport = candidateTerm.getTerm(1);
            double portEffectiveness = 0;
            if (tport != null && cport != null)
            {
                if ((mode & TermAlgorithmFlagsEnum.SYMMETRIC_FLAG.getValue()) != 0)
                    portEffectiveness = OntologyUtilities.compareSymmetricTermContents(tport,
                        cport, threshold, null, false);
                else
                    portEffectiveness = OntologyUtilities.compareTermContents(tport, cport, null,
                        false);
            }

            Term thost = targetTerm.getTerm(2);
            Term chost = candidateTerm.getTerm(2);
            double hostEffectiveness = 0;
            if (thost != null && chost != null)
            {
                if ((mode & TermAlgorithmFlagsEnum.SYMMETRIC_FLAG.getValue()) != 0)
                    hostEffectiveness = OntologyUtilities.compareSymmetricTermContents(thost,
                        chost, threshold, null, false);
                else
                    hostEffectiveness = OntologyUtilities.compareTermContents(thost, chost, null,
                        false);
            }

            Term tfile = targetTerm.getTerm(3);
            Term cfile = candidateTerm.getTerm(3);
            double fileEffectiveness = 0;
            if (tfile != null && cfile != null)
            {
                if ((mode & TermAlgorithmFlagsEnum.SYMMETRIC_FLAG.getValue()) != 0)
                    fileEffectiveness = OntologyUtilities.compareSymmetricTermContents(tfile,
                        cfile, threshold, null, false);
                else
                    fileEffectiveness = OntologyUtilities.compareTermContents(tfile, cfile, null,
                        false);
            }

            if (print)
            {
                System.out.println("\tEffectivity for protocol: " + protocolEffectiveness);
                System.out.println("\tEffectivity for port: " + portEffectiveness);
                System.out.println("\tEffectivity for host: " + hostEffectiveness);
                System.out.println("\tEffectivity for file: " + fileEffectiveness);
            }

            effectiveness = (protocolEffectiveness + portEffectiveness + hostEffectiveness + fileEffectiveness) / 4;
        }
        else if (targetTerm.getDomain().getType().equals("url") &&
            candidateTerm.getDomain().getType().equals("url"))
        {
            Term tuser = targetTerm.getTerm(0);
            Term cuser = candidateTerm.getTerm(0);
            double userEffectiveness = 0;
            if (tuser != null && cuser != null)
            {
                if ((mode & TermAlgorithmFlagsEnum.SYMMETRIC_FLAG.getValue()) != 0)
                    userEffectiveness = OntologyUtilities.compareSymmetricTermContents(tuser,
                        cuser, threshold, null, false);
                else
                    userEffectiveness = OntologyUtilities.compareTermContents(tuser, cuser, null,
                        false);
            }

            Term tdomain = targetTerm.getTerm(1);
            Term cdomain = candidateTerm.getTerm(1);
            double domainEffectiveness = 0;
            if (tdomain != null && cdomain != null)
            {
                if ((mode & TermAlgorithmFlagsEnum.SYMMETRIC_FLAG.getValue()) != 0)
                    domainEffectiveness = OntologyUtilities.compareSymmetricTermContents(tdomain,
                        cdomain, threshold, null, false);
                else
                    domainEffectiveness = OntologyUtilities.compareTermContents(tdomain, cdomain,
                        null, false);
            }

            if (print)
            {
                System.out.println("\tEffectivity for user: " + userEffectiveness);
                System.out.println("\tEffectivity for domain: " + domainEffectiveness);
            }

            effectiveness = (userEffectiveness + domainEffectiveness) / 2;
        }
        else
        {
            if ((mode & TermAlgorithmFlagsEnum.SYMMETRIC_FLAG.getValue()) != 0)
                effectiveness = OntologyUtilities.compareSymmetricTermContents(targetTerm,
                    candidateTerm, threshold, (mode & TermAlgorithmFlagsEnum.USE_THESAURUS_FLAG.getValue()) != 0 ? thesaurus : null,
                    (mode & TermAlgorithmFlagsEnum.USE_SOUNDEX_FLAG.getValue()) != 0);
            else
                effectiveness = OntologyUtilities.compareTermContents(targetTerm, candidateTerm,
                    (mode & TermAlgorithmFlagsEnum.USE_THESAURUS_FLAG.getValue()) != 0 ? thesaurus : null,
                    (mode & TermAlgorithmFlagsEnum.USE_SOUNDEX_FLAG.getValue()) != 0);
        }

        if (domainSimilarity > 0)
            effectiveness = domainSimilarity + effectiveness * (1 - domainSimilarity);

        if (print)
        {
            System.out.println("\tOverall effectivity: " + effectiveness);
            System.out.println("****************************************************");
        }

        return effectiveness >= threshold;
    }
}
