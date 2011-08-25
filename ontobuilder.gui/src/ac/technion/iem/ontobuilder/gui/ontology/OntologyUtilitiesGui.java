package ac.technion.iem.ontobuilder.gui.ontology;

import java.util.ArrayList;
import java.util.Iterator;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.OntologyClass;
import ac.technion.iem.ontobuilder.core.ontology.OntologyUtilities;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.core.utils.StringUtilities;
import ac.technion.iem.ontobuilder.core.utils.properties.ApplicationParameters;
import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchComparator;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.match.Mismatch;


/**
 * <p>
 * Title: OntologyUtilitiesGui
 * </p>
  * <p>Description: Implements the methods of the OntologyUtilities used by the GUI</p>
 */
public class OntologyUtilitiesGui
{
    private OntologyUtilities ontologyUtilities;
    
    public OntologyUtilitiesGui(OntologyUtilities ontologyUtilities)
    {
        this.ontologyUtilities = ontologyUtilities;
    }
    
    public OntologyUtilities getonOntologyUtilities()
    {
        return this.ontologyUtilities;
    }
    
    /**
     * Create an ontology from MatchInformation
     * 
     * @param name the ontology name
     * @param matchInformation the {@link MatchInformation} to use
     * @return an {@link Ontology}
     */
    public static Ontology createOntology(String name, MatchInformation matchInformation)
    {
        OntologyGui ontologyGui = new OntologyGui(name);
        Ontology ontology = ontologyGui.getOntology();

        // Create all the classes
        Ontology targetOntology = matchInformation.getTargetOntology();
        Ontology targetModel = targetOntology;
        for (int i = 0; i < targetModel.getClassesCount(); i++)
        {
            OntologyClass targetOntologyClass = targetModel.getClass(i);
            OntologyClass ontologyClass = (OntologyClass) targetOntologyClass.clone();
            OntologyUtilities.buildClassHierarchy(ontologyClass, targetOntologyClass);
            ontologyClass.setOntology(ontology);
            ontology.addClass(ontologyClass);
        }

        ArrayList<?> matches = matchInformation.getMatches();
        ArrayList<Term> matchesToInclude = new ArrayList<Term>();
        for (Iterator<?> i = matches.iterator(); i.hasNext();)
        {
            Match match = (Match) i.next();
            matchesToInclude.add(match.getTargetTerm());
        }
        for (Iterator<Term> i = matchesToInclude.iterator(); i.hasNext();)
        {
            Term term = (Term) i.next();
            OntologyUtilities.addTermToOntology(term, ontology, matchesToInclude);
        }

        ArrayList<?> mismatchesTarget = matchInformation.getMismatchesTargetOntology();
        ArrayList<Term> mismatchesTargetToInclude = new ArrayList<Term>();
        for (Iterator<?> i = mismatchesTarget.iterator(); i.hasNext();)
        {
            Mismatch mismatch = (Mismatch) i.next();
            if (mismatch.isSelected())
                mismatchesTargetToInclude.add(mismatch.getTerm());
        }
        for (Iterator<Term> i = mismatchesTargetToInclude.iterator(); i.hasNext();)
        {
            Term term = (Term) i.next();
            OntologyUtilities.addTermToOntology(term, ontology, mismatchesTargetToInclude);
        }

        ArrayList<?> mismatchesCandidate = matchInformation.getMismatchesCandidateOntology();
        ArrayList<Term> mismatchesCandidateToInclude = new ArrayList<Term>();
        for (Iterator<?> i = mismatchesCandidate.iterator(); i.hasNext();)
        {
            Mismatch mismatch = (Mismatch) i.next();
            if (mismatch.isSelected())
                mismatchesCandidateToInclude.add(mismatch.getTerm());
        }
        for (Iterator<Term> i = mismatchesCandidateToInclude.iterator(); i.hasNext();)
        {
            Term term = (Term) i.next();
            OntologyUtilities.addTermToOntology(term, ontology, mismatchesCandidateToInclude);
        }

        return ontology;
    }
    
    /**
     * Perform a math
     * 
     * @param originalTargetTermList the original target terms list
     * @param targetTermList the target terms list
     * @param originalCandidateTermList the original candidate terms list
     * @param candidateTermList the candidate terms list
     * @param comparator the comparator to use in the match
     * @return {@link MatchInformation}
     */
    public static MatchInformation match(ArrayList<?> originalTargetTermList,
        ArrayList<?> targetTermList, ArrayList<?> originalCandidateTermList,
        ArrayList<?> candidateTermList, MatchComparator comparator)
    {
        MatchInformation matchInformation = new MatchInformation();

        String columnNames[] =
        {
            ApplicationUtilities.getResourceString("ontology.match.candidate") + "(*)",
            ApplicationUtilities.getResourceString("ontology.match.candidate"),
            ApplicationUtilities.getResourceString("ontology.match.target") + "(*)",
            ApplicationUtilities.getResourceString("ontology.match.target"),
            ApplicationUtilities.getResourceString("ontology.match.effectiveness")
        };
        Object matchTable[][] = new Object[targetTermList.size() * candidateTermList.size()][5];

        for (int j = 0; j < candidateTermList.size(); j++)
        {
            Term candidateTerm = (Term) candidateTermList.get(j);
            Term originalCandidateTerm = (Term) originalCandidateTermList.get(j);
            Term maxTargetTerm = null;
            double maxEffectiveness = -1;
            for (int i = 0; i < targetTermList.size(); i++)
            {
                Term targetTerm = (Term) targetTermList.get(i);
                Term originalTargetTerm = (Term) originalTargetTermList.get(i);

                if (comparator.compare(targetTerm, candidateTerm))
                {
                    if (comparator.implementsEffectiveness())
                    {
                        if (comparator.getEffectiveness() >= maxEffectiveness)
                        {
                            maxEffectiveness = comparator.getEffectiveness();
                            maxTargetTerm = originalTargetTerm;
                        }
                    }
                    else
                        maxTargetTerm = originalTargetTerm;
                }

                int index = j * targetTermList.size() + i;
                matchTable[index][0] = candidateTerm;
                matchTable[index][1] = originalCandidateTerm;
                matchTable[index][2] = targetTerm;
                matchTable[index][3] = originalTargetTerm;
                if (comparator.implementsEffectiveness())
                    matchTable[index][4] = new Double(comparator.getEffectiveness());
                else
                    matchTable[index][4] = "[none]";
            }
            if (maxTargetTerm != null)
                matchInformation.addMatch(maxTargetTerm, originalCandidateTerm, maxEffectiveness);
        }
        for (int i = 0; i < originalTargetTermList.size(); i++)
        {
            Term term = (Term) originalTargetTermList.get(i);
            if (!matchInformation.isMatched(term))
                matchInformation.addMismatchTargetOntology(new Mismatch(term));
        }
        for (int i = 0; i < originalCandidateTermList.size(); i++)
        {
            Term term = (Term) originalCandidateTermList.get(i);
            if (!matchInformation.isMatched(term))
                matchInformation.addMismatchCandidateOntology(new Mismatch(term));
        }

        if (ApplicationParameters.verbose)
        {
            System.out.println(ApplicationUtilities.getResourceString("verbose.match.syntactic"));
            System.out.println();
            System.out.println(StringUtilities.getTableStringRepresentation(columnNames, 0, matchTable));
        }

        return matchInformation;
    }

    public static MatchInformation matchFull(ArrayList<?> originalTargetTermList,
        ArrayList<?> targetTermList, ArrayList<?> originalCandidateTermList,
        ArrayList<?> candidateTermList, MatchComparator comparator)
    {
        MatchInformation matchInformation = new MatchInformation();

        String columnNames[] =
        {
            ApplicationUtilities.getResourceString("ontology.match.candidate") + "(*)",
            ApplicationUtilities.getResourceString("ontology.match.candidate"),
            ApplicationUtilities.getResourceString("ontology.match.target") + "(*)",
            ApplicationUtilities.getResourceString("ontology.match.target"),
            ApplicationUtilities.getResourceString("ontology.match.effectiveness")
        };
        Object matchTable[][] = new Object[targetTermList.size() * candidateTermList.size()][5];

        for (int j = 0; j < candidateTermList.size(); j++)
        {
            Term candidateTerm = (Term) candidateTermList.get(j);
            Term originalCandidateTerm = (Term) originalCandidateTermList.get(j);
            for (int i = 0; i < targetTermList.size(); i++)
            {
                Term targetTerm = (Term) targetTermList.get(i);
                Term originalTargetTerm = (Term) originalTargetTermList.get(i);
                comparator.compare(targetTerm, candidateTerm);
                int index = j * targetTermList.size() + i;
                matchTable[index][0] = candidateTerm;
                matchTable[index][1] = originalCandidateTerm;
                matchTable[index][2] = targetTerm;
                matchTable[index][3] = originalTargetTerm;
                double effectiveness = -1;
                if (comparator.implementsEffectiveness())
                {
                    effectiveness = comparator.getEffectiveness();
                    matchTable[index][4] = new Double(effectiveness);
                }
                else
                    matchTable[index][4] = "[none]";
                matchInformation.addMatch(originalTargetTerm, originalCandidateTerm, effectiveness);
            }
        }

        if (ApplicationParameters.verbose)
        {
            System.out.println(ApplicationUtilities.getResourceString("verbose.match.syntactic"));
            System.out.println();
            System.out.println(StringUtilities.getTableStringRepresentation(columnNames, 0, matchTable));
        }

        return matchInformation;
    }

    /**
     * Get a match matrix
     * 
     * @param originalTargetTermList the original target terms list
     * @param targetTermList the target terms list
     * @param originalCandidateTermList the original candidate terms list
     * @param candidateTermList the candidate terms list
     * @param comparator the comparator to use in the match
     * @return a match matrix
     */
    public static double[][] getMatchMatrix(ArrayList<?> originalTargetTermList,
        ArrayList<?> targetTermList, ArrayList<?> originalCandidateTermList,
        ArrayList<?> candidateTermList, MatchComparator comparator)
    {
        double matchMatrix[][] = new double[candidateTermList.size()][targetTermList.size()];

        String columnNames[] =
        {
            ApplicationUtilities.getResourceString("ontology.match.candidate") + "(*)",
            ApplicationUtilities.getResourceString("ontology.match.candidate"),
            ApplicationUtilities.getResourceString("ontology.match.target") + "(*)",
            ApplicationUtilities.getResourceString("ontology.match.target"),
            ApplicationUtilities.getResourceString("ontology.match.effectiveness")
        };
        Object matchTable[][] = new Object[targetTermList.size() * candidateTermList.size()][5];

        for (int j = 0; j < candidateTermList.size(); j++)
        {
            Term candidateTerm = (Term) candidateTermList.get(j);
            Term originalCandidateTerm = (Term) originalCandidateTermList.get(j);
            for (int i = 0; i < targetTermList.size(); i++)
            {
                Term targetTerm = (Term) targetTermList.get(i);
                Term originalTargetTerm = (Term) originalTargetTermList.get(i);
                comparator.compare(targetTerm, candidateTerm);
                int index = j * targetTermList.size() + i;
                matchTable[index][0] = candidateTerm;
                matchTable[index][1] = originalCandidateTerm;
                matchTable[index][2] = targetTerm;
                matchTable[index][3] = originalTargetTerm;
                double effectiveness = 0;
                if (comparator.implementsEffectiveness())
                {
                    effectiveness = comparator.getEffectiveness();
                    matchTable[index][4] = new Double(effectiveness);
                }
                else
                    matchTable[index][4] = "[none]";
                matchMatrix[j][i] = effectiveness;
            }
        }

        if (ApplicationParameters.verbose)
        {
            System.out.println(comparator.getName());
            System.out.println();
            System.out.println(StringUtilities.getTableStringRepresentation(columnNames, 0, matchTable));
        }

        return matchMatrix;
    }
}
