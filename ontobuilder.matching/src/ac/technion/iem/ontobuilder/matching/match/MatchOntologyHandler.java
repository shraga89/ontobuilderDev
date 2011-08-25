package ac.technion.iem.ontobuilder.matching.match;

import java.util.ArrayList;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.core.utils.StringUtilities;
import ac.technion.iem.ontobuilder.core.utils.properties.ApplicationParameters;
import ac.technion.iem.ontobuilder.core.utils.properties.PropertiesHandler;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.Algorithm;
import ac.technion.iem.ontobuilder.matching.meta.match.MatchMatrix;

/**
 * <p>Title: MatchOntologyHandler</p>
 * <p>Description: Handles a match between two ontologies without the GUI the GUI</p>
 */
public class MatchOntologyHandler
{
    /**
     * Match two ontologies according to an algorithm
     * 
     * @param ontology the {@link OntologyGui}
     * @param algorithm the {@link Algorithm}
     * @return MatchInformation
     */
    public static MatchInformation match(Ontology targetOntology, Ontology candOntology, Algorithm algorithm)
    {
        return algorithm.match(targetOntology, candOntology);
    }
    
    // added haggai 6/12/03
    /**
     * Create a match matrix
     * 
     * @param originalTargetTermList the original target terms list
     * @param targetTermList the target terms list
     * @param originalCandidateTermList the original candidate terms list
     * @param candidateTermList the candidate terms list
     * @param comparator the comparator to use in the match
     * @return a {@link MatchMatrix}
     */
    public static MatchMatrix createMatchMatrix(ArrayList<Term> originalTargetTermList,
        ArrayList<Term> targetTermList, ArrayList<Term> originalCandidateTermList,
        ArrayList<Term> candidateTermList, MatchComparator comparator)
    {
        // double matchMatrix[][]=new double[candidateTermList.size()][targetTermList.size()];
        // added 1/1/04
        // System.out.println("updating domain matrix...");
        // DomainSimilarity.updateDomainMatrix(originalCandidateTermList,originalTargetTermList);
        // /
        String columnNames[] =
        {
            PropertiesHandler.getResourceString("ontology.match.candidate") + "(*)",
            PropertiesHandler.getResourceString("ontology.match.candidate"),
            PropertiesHandler.getResourceString("ontology.match.target") + "(*)",
            PropertiesHandler.getResourceString("ontology.match.target"),
            PropertiesHandler.getResourceString("ontology.match.effectiveness")
        };
        Object matchTable[][] = new Object[targetTermList.size() * candidateTermList.size()][5];
        MatchMatrix matchMatrix = new MatchMatrix(originalCandidateTermList.size(),
            originalTargetTermList.size(), originalCandidateTermList, originalTargetTermList);

        for (int j = 0; j < candidateTermList.size(); j++)
        {
            Term candidateTerm = (Term) candidateTermList.get(j);
            Term originalCandidateTerm = (Term) originalCandidateTermList.get(j);
            for (int i = 0; i < targetTermList.size(); i++)
            {
                Term targetTerm = (Term) targetTermList.get(i);
                // gabi

                // System.out.println("target term: "+ i+"  "+targetTerm);
                Term originalTargetTerm = (Term) originalTargetTermList.get(i);
                // System.out.println("originalTarget term: "+ i+"  "+originalTargetTerm);
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
                matchMatrix.setMatchConfidence(originalCandidateTerm, originalTargetTerm,
                    effectiveness);
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
