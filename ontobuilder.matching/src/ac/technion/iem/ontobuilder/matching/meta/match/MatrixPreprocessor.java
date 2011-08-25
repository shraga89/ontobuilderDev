package ac.technion.iem.ontobuilder.matching.meta.match;

import java.text.Collator;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Vector;

/**
 * <p>Title: MatrixPreprocessor</p>
 * <p>Description: Class which performs different actions on matrixes, such as union and intersect between the
 * candidate and target attributes</p>
 * Available types:
 * <code>UNION_PREPROCESSING</code>, <code>INTERSECT_PREPROCESSING</code> and <code>TEMPLATE_PREPROCESSING</code>
 */
public final class MatrixPreprocessor
{
    private AbstractMatchMatrix[] matrixes;
    private int templateIndex = 0; // by default take the first
    private MatrixPreprocessorTypeEnum typeOfPreprocessing = MatrixPreprocessorTypeEnum.INTERSECT_PREPROCESSING; // default

    /**
     * Constructs a default MatrixPreprocessor
     */
    public MatrixPreprocessor()
    {
    }

    /**
     * Constructs a MatrixPreprocessor with a Match Matrix, template index and type of
     * pre-processing
     */
    public MatrixPreprocessor(AbstractMatchMatrix[] matrixes, int templateIndex,
        MatrixPreprocessorTypeEnum typeOfPreprocessing)
    {
        this.matrixes = matrixes;
        this.templateIndex = templateIndex;
        this.typeOfPreprocessing = typeOfPreprocessing;
    }

    /**
     * Apply the input threshold to all the matrixes
     * 
     * @param threshold
     */
    public void applyThreshold(double threshold)
    {
        for (int i = 0; i < matrixes.length; i++)
        {
            matrixes[i].applyThreshold(threshold);
        }
    }

    /**
     * Apply the input threshold to all the matrixes except for the one with the index to skip
     * 
     * @param threshold - the threshold
     * @param skip - the index of matrix to skip
     */
    public void applyThreshold(double threshold, int skip)
    {
        for (int i = 0; i < matrixes.length; i++)
        {
            if (skip != i)
                matrixes[i].applyThreshold(threshold);
        }
    }

    /**
     * Perform pre-process for all the matrixes according to the type of pre-processing
     * 
     * @return the {@link AbstractMatchMatrix} array
     */
    public AbstractMatchMatrix[] preprocess()
    {
        switch (typeOfPreprocessing)
        {
        case UNION_PREPROCESSING:
            // System.out.println("performing UNION preprocessing...\n");
            return union();
        case INTERSECT_PREPROCESSING:
            // System.out.println("performing INTERSECT preprocessing...\n");
            return intersect();
        default:// TEMPLATE_PREPROCESSING
            // System.out.println("performing TEMPLATE preprocessing...\n");
            return template(templateIndex);
        }
    }

    /**
     * Perform a union between the candidate and target attributes and calculate the match matrix
     * 
     * @return an {@link AbstractMatchMatrix} array
     */
    private AbstractMatchMatrix[] union()
    {// O(n*m^2)
        String[] candidateAttributes;
        String[] targetAttributes;
        double[][] tempNewMatrix;
        Vector<Vector<String>> tempNewCandAttributes = new Vector<Vector<String>>(matrixes.length);
        Vector<Vector<String>> tempNewTargetAttributes = new Vector<Vector<String>>(matrixes.length);

        // initialise tempNewCandAttributes and tempNewTargetAttributes
        for (int i = 0; i < matrixes.length; i++)
        {
            tempNewCandAttributes.add(i, new Vector<String>());// create temporary attributes
                                                               // storage for each matrix
            tempNewTargetAttributes.add(i, new Vector<String>());
        }

        // phase one - equalise matrixes by size(matrixes may grow)
        // 1.1 find all those candidate/target attributes that are missing in each matrix
        for (int i = 0; i < matrixes.length; i++)
        {
            candidateAttributes = matrixes[i].getCandidateAttributeNames();
            targetAttributes = matrixes[i].getTargetAttributeNames();
            for (int j = 0; j < candidateAttributes.length; j++)
            {
                for (int k = 0; k < matrixes.length; k++)
                    if (k != i && !matrixes[k].containsCandidateAttribute(candidateAttributes[j]))
                    {
                        if (!tempNewCandAttributes.get(k).contains(candidateAttributes[j]))
                        {
                            tempNewCandAttributes.get(k).add(candidateAttributes[j]);
                        }
                    }
            }
            for (int j = 0; j < targetAttributes.length; j++)
            {
                for (int k = 0; k < matrixes.length; k++)
                    if (k != i && !matrixes[k].containsTargetAttribute(targetAttributes[j]))
                    {
                        if (!tempNewTargetAttributes.get(k).contains(targetAttributes[j]))
                        {
                            tempNewTargetAttributes.get(k).add(targetAttributes[j]);
                        }
                    }
            }
        }
        // before moving to next step check if need to continue
        boolean foundNoneEmptyList = false;
        for (int i = 0; i < matrixes.length; i++)
        {
            if (!tempNewCandAttributes.get(i).isEmpty())
            {
                foundNoneEmptyList = true;
                break;
            }
        }
        if (!foundNoneEmptyList)
        {
            for (int i = 0; i < matrixes.length; i++)
                if (!tempNewTargetAttributes.get(i).isEmpty())
                {
                    foundNoneEmptyList = true;
                    break;
                }
        }
        if (foundNoneEmptyList)
        {
            // 1.2 resize the matrixes
            for (int i = 0; i < matrixes.length; i++)
            {
                int oldRowCount = matrixes[i].getRowCount();
                int oldColCount = matrixes[i].getColCount();

                candidateAttributes = new String[matrixes[i].getCandidateAttributeNames().length +
                    tempNewCandAttributes.get(i).size()];
                targetAttributes = new String[matrixes[i].getTargetAttributeNames().length +
                    tempNewTargetAttributes.get(i).size()];

                for (int j = 0; j < matrixes[i].getCandidateAttributeNames().length; j++)
                {
                    candidateAttributes[j] = matrixes[i].getCandidateAttributeNames()[j];
                }
                for (int j = matrixes[i].getCandidateAttributeNames().length; j < matrixes[i]
                    .getCandidateAttributeNames().length + tempNewCandAttributes.get(i).size(); j++)
                {
                    candidateAttributes[j] = tempNewCandAttributes.get(i).get(
                        j - matrixes[i].getCandidateAttributeNames().length);
                }

                for (int j = 0; j < matrixes[i].getTargetAttributeNames().length; j++)
                {
                    targetAttributes[j] = matrixes[i].getTargetAttributeNames()[j];
                }
                for (int j = matrixes[i].getTargetAttributeNames().length; j < matrixes[i]
                    .getTargetAttributeNames().length + tempNewTargetAttributes.get(i).size(); j++)
                {
                    targetAttributes[j] = tempNewTargetAttributes.get(i).get(
                        j - matrixes[i].getTargetAttributeNames().length);
                }

                tempNewMatrix = new double[targetAttributes.length][candidateAttributes.length];
                // initialise with '0'
                for (int j = 0; j < targetAttributes.length; j++)
                    for (int k = 0; k < candidateAttributes.length; k++)
                        tempNewMatrix[j][k] = 0;
                // calculate match confidence
                for (int j = 0; j < oldRowCount; j++)
                    for (int k = 0; k < oldColCount; k++)
                        tempNewMatrix[j][k] = matrixes[i].getMatchConfidenceAt(j, k);
                matrixes[i].setMatchMatrix(tempNewMatrix);

                Vector<String> toAddTarget = tempNewTargetAttributes.get(i);
                Vector<String> toAddCandidate = tempNewCandAttributes.get(i);
                for (int j = 0; j < toAddCandidate.size(); j++)
                    matrixes[i].addNewAttributeToCandidateSchema(toAddCandidate.get(j));
                for (int j = 0; j < toAddTarget.size(); j++)
                    matrixes[i].addNewAttributeToTargetSchema(toAddTarget.get(j));
            }// now all matrixes are with the same size with the same attributes
             // but attributes permutation may differ , so fix it in phase two

        }// else just equalise permutations

        // phase two - equalise matrixes permutation over candidate and target attributes
        for (int i = 0; i < matrixes.length; i++)
            permuteMatrix(matrixes[i], null);

        return matrixes;
    }

    /**
     * Perform an intersect between the candidate and target attributes and calculate the match
     * matrix
     * 
     * @return an {@link AbstractMatchMatrix} array
     */
    private AbstractMatchMatrix[] intersect()
    {// O(n*m^2)

        String[] candidateAttributes;
        String[] targetAttributes;
        double[][] tempNewMatrix;
        Vector<Vector<String>> tempToRemoveCandAttributes = new Vector<Vector<String>>(
            matrixes.length);
        Vector<Vector<String>> tempToRemoveTargetAttributes = new Vector<Vector<String>>(
            matrixes.length);

        for (int i = 0; i < matrixes.length; i++)
        {
            // create temporary attributes storage for each matrix
            tempToRemoveCandAttributes.add(i, new Vector<String>());
            tempToRemoveTargetAttributes.add(i, new Vector<String>());
        }

        // phase one - equalise matrixes by size (matrixes may shrink)
        // 1.1 find all those candidate/target attributes that are missing in each matrix
        for (int i = 0; i < matrixes.length; i++)
        {
            candidateAttributes = matrixes[i].getCandidateAttributeNames();
            targetAttributes = matrixes[i].getTargetAttributeNames();
            // find at least one matrix that doesn't contain the candidate attribute...
            for (int j = 0; j < candidateAttributes.length; j++)
            {
                for (int k = 0; k < matrixes.length; k++)
                    if (k != i && !matrixes[k].containsCandidateAttribute(candidateAttributes[j]))
                    {
                        if (!tempToRemoveCandAttributes.get(i).contains(candidateAttributes[j]))
                        {
                            tempToRemoveCandAttributes.get(i).add(candidateAttributes[j]);
                            break;
                        }
                    }
            }
            // find at least one matrix that doesn't contain the target attribute...
            for (int j = 0; j < targetAttributes.length; j++)
            {
                for (int k = 0; k < matrixes.length; k++)
                    if (k != i && !matrixes[k].containsTargetAttribute(targetAttributes[j]))
                    {
                        if (!tempToRemoveTargetAttributes.get(i).contains(targetAttributes[j]))
                        {
                            tempToRemoveTargetAttributes.get(i).add(targetAttributes[j]);
                            break;
                        }
                    }
            }
        }

        // before moving to next step check if need to contiune
        boolean foundNoneEmptyList = false;
        for (int i = 0; i < matrixes.length; i++)
        {
            if (!(tempToRemoveCandAttributes.get(i)).isEmpty())
            {
                foundNoneEmptyList = true;
                break;
            }
        }
        if (!foundNoneEmptyList)
        {
            for (int i = 0; i < matrixes.length; i++)
                if (!(tempToRemoveTargetAttributes.get(i)).isEmpty())
                {
                    foundNoneEmptyList = true;
                    break;
                }

        }

        if (foundNoneEmptyList)
        {
            // 1.2 resize the matrixes
            Iterator<String> it;
            for (int i = 0; i < matrixes.length; i++)
            {
                String[] diffCandAttributes = differentiateAttributes(
                    matrixes[i].getCandidateAttributeNames(), tempToRemoveCandAttributes.get(i));
                String[] diffTargetAttributes = differentiateAttributes(
                    matrixes[i].getTargetAttributeNames(), tempToRemoveTargetAttributes.get(i));

                // fix the matrix
                tempNewMatrix = new double[diffTargetAttributes.length][diffCandAttributes.length];
                for (int j = 0; j < diffTargetAttributes.length; j++)
                    for (int k = 0; k < diffCandAttributes.length; k++)
                        tempNewMatrix[j][k] = matrixes[i].getMatchConfidenceByAttributeNames(
                            diffCandAttributes[k], diffTargetAttributes[j]);
                matrixes[i].setMatchMatrix(tempNewMatrix);

                // now remove attributes
                it = tempToRemoveCandAttributes.get(i).iterator();
                while (it.hasNext())
                {
                    matrixes[i].removeAttributeFromCandidateSchema(it.next());
                }
                it = tempToRemoveTargetAttributes.get(i).iterator();
                while (it.hasNext())
                {
                    matrixes[i].removeAttributeFromTargetSchema((String) it.next());
                }
            }// now all matrixes are with the same size with the same attributes
             // but attributes permutation may differ , so fix it in phase two
        }// else just equalise permutations

        // phase two - equalise matrixes permutation over candidate and target attributes
        for (int i = 0; i < matrixes.length; i++)
            permuteMatrix(matrixes[i], null);
        return matrixes;
    }

    /**
     * Sorts the match matrix to the same order of candidate and target attribute names
     * 
     * @param matrixToPermute a {@link AbstractMatchMatrix}
     * @param templateMatrix a {@link AbstractMatchMatrix}
     * @return a sorted {@link AbstractMatchMatrix}
     */
    private AbstractMatchMatrix permuteMatrix(AbstractMatchMatrix matrixToPermute,
        AbstractMatchMatrix templateMatrix)
    {
        String[] candidateAttributes;
        String[] targetAttributes;
        double[][] tempNewMatrix;
        LinkedList<String> sortedCandidatePermutation = new LinkedList<String>();
        LinkedList<String> sortedTargetPermutation = new LinkedList<String>();

        for (int i = 0; i < matrixes.length; i++)
        {
            if (!sortedCandidatePermutation.isEmpty())
                sortedCandidatePermutation.clear();
            if (!sortedTargetPermutation.isEmpty())
                sortedTargetPermutation.clear();

            // initialise the linked lists with the candidate and target attribute names
            candidateAttributes = matrixToPermute.getCandidateAttributeNames();
            targetAttributes = matrixToPermute.getTargetAttributeNames();
            for (int j = 0; j < candidateAttributes.length; j++)
                sortedCandidatePermutation.add(j, candidateAttributes[j]);
            for (int j = 0; j < targetAttributes.length; j++)
                sortedTargetPermutation.add(j, targetAttributes[j]);

            // sort the two lists
            Collections.sort(sortedCandidatePermutation, Collator.getInstance(Locale.getDefault()));
            Collections.sort(sortedTargetPermutation, Collator.getInstance(Locale.getDefault()));

            // initialise the original candidate and target attribute with the right order
            for (int j = 0; j < candidateAttributes.length; j++)
                candidateAttributes[j] = sortedCandidatePermutation.get(j);
            for (int j = 0; j < targetAttributes.length; j++)
                targetAttributes[j] = sortedTargetPermutation.get(j);

            // create a new match matrix with the originally calculated match values
            tempNewMatrix = new double[targetAttributes.length][candidateAttributes.length];
            for (int k = 0; k < candidateAttributes.length; k++)
                for (int j = 0; j < targetAttributes.length; j++)
                    tempNewMatrix[j][k] = matrixToPermute.getMatchConfidenceByAttributeNames(
                        candidateAttributes[k], targetAttributes[j]);

            // set the new matrix
            matrixToPermute.rearrangeCandidateAttributesPermutation(candidateAttributes);
            matrixToPermute.rearrangeTargetAttributesPermutation(targetAttributes);
            matrixToPermute.setMatchMatrix(tempNewMatrix);
        }
        return matrixToPermute;
    }

    /**
     * Find the attributes that do not exist in toDiff but exist in attributes.
     * 
     * @param attributes
     * @param toDiff
     * @return the different attributes
     */
    private String[] differentiateAttributes(String[] attributes, Vector<String> toDiff)
    {
        String[] diffResult = new String[attributes.length - toDiff.size()];
        int size = attributes.length;
        int index = 0;
        for (int i = 0; i < size; i++)
            if (!toDiff.contains(attributes[i]))
                diffResult[index++] = attributes[i];
        return diffResult;
    }

    /**
     * Perform template processing between the candidate and target attributes and calculate the
     * match matrix
     * 
     * @param templateIndex
     * @return a {@link AbstractMatchMatrix} array
     */
    private AbstractMatchMatrix[] template(int templateIndex)
    {
        String[] candidateAttributes;
        String[] targetAttributes;
        double[][] tempNewMatrix;
        AbstractMatchMatrix templateMatrix = matrixes[templateIndex];
        Vector<Vector<String>> tempNewCandAttributes = new Vector<Vector<String>>(matrixes.length);
        Vector<Vector<String>> tempNewTargetAttributes = new Vector<Vector<String>>(matrixes.length);
        Vector<Vector<String>> tempToRemoveCandAttributes = new Vector<Vector<String>>(
            matrixes.length);
        Vector<Vector<String>> tempToRemoveTargetAttributes = new Vector<Vector<String>>(
            matrixes.length);

        for (int i = 0; i < matrixes.length; i++)
        {
            // create temporary attributes storage for each matrix
            tempNewCandAttributes.add(i, new Vector<String>());
            tempNewTargetAttributes.add(i, new Vector<String>());
            tempToRemoveCandAttributes.add(i, new Vector<String>());
            tempToRemoveTargetAttributes.add(i, new Vector<String>());
        }

        // phase one - equalise matrixes by size(matrixes may grow)
        // 1.1.1 find all those candidate/target attributes that are missing in each matrix
        candidateAttributes = templateMatrix.getCandidateAttributeNames();
        targetAttributes = templateMatrix.getTargetAttributeNames();
        for (int j = 0; j < candidateAttributes.length; j++)
        {
            for (int k = 0; k < matrixes.length; k++)
                if (k != templateIndex &&
                    !matrixes[k].containsCandidateAttribute(candidateAttributes[j]))
                {
                    if (!tempNewCandAttributes.get(k).contains(candidateAttributes[j]))
                    {
                        tempNewCandAttributes.get(k).add(candidateAttributes[j]);
                    }
                }
        }
        for (int j = 0; j < targetAttributes.length; j++)
        {
            for (int k = 0; k < matrixes.length; k++)
                if (k != templateIndex && !matrixes[k].containsTargetAttribute(targetAttributes[j]))
                {
                    if (!tempNewTargetAttributes.get(k).contains(targetAttributes[j]))
                    {
                        tempNewTargetAttributes.get(k).add(targetAttributes[j]);
                    }
                }
        }

        // 1.1.2 find all those candidate/target attributes that are missing in each matrix
        for (int i = 0; i < matrixes.length; i++)
        {
            candidateAttributes = matrixes[i].getCandidateAttributeNames();
            targetAttributes = matrixes[i].getTargetAttributeNames();
            // find at least one matrix that doesn't contain the attribute...
            for (int j = 0; j < candidateAttributes.length; j++)
            {
                if (templateIndex != i &&
                    !templateMatrix.containsCandidateAttribute(candidateAttributes[j]))
                {
                    if (!tempToRemoveCandAttributes.get(i).contains(candidateAttributes[j]))
                    {
                        tempToRemoveCandAttributes.get(i).add(candidateAttributes[j]);
                    }
                }
            }
            // find at least one matrix that doesn't contain the attribute...
            for (int j = 0; j < targetAttributes.length; j++)
            {
                if (templateIndex != i &&
                    !templateMatrix.containsTargetAttribute(targetAttributes[j]))
                {
                    if (!tempToRemoveTargetAttributes.get(i).contains(targetAttributes[j]))
                    {
                        tempToRemoveTargetAttributes.get(i).add(targetAttributes[j]);
                    }
                }
            }
        }

        // before moving to next step check if need to continue
        boolean foundNoneEmptyNewList = false;
        for (int i = 0; i < matrixes.length; i++)
        {
            if (!tempNewCandAttributes.get(i).isEmpty())
            {
                foundNoneEmptyNewList = true;
                break;
            }
        }
        if (!foundNoneEmptyNewList)
        {
            for (int i = 0; i < matrixes.length; i++)
                if (!tempNewTargetAttributes.get(i).isEmpty())
                {
                    foundNoneEmptyNewList = true;
                    break;
                }
        }
        if (foundNoneEmptyNewList)
        {
            // 1.2 resize the matrixes
            for (int i = 0; i < matrixes.length; i++)
            {
                int oldRowCount = matrixes[i].getRowCount();
                int oldColCount = matrixes[i].getColCount();
                candidateAttributes = new String[matrixes[i].getCandidateAttributeNames().length +
                    tempNewCandAttributes.get(i).size()];
                targetAttributes = new String[matrixes[i].getTargetAttributeNames().length +
                    tempNewTargetAttributes.get(i).size()];

                for (int j = 0; j < matrixes[i].getCandidateAttributeNames().length; j++)
                {
                    candidateAttributes[j] = matrixes[i].getCandidateAttributeNames()[j];
                }
                for (int j = matrixes[i].getCandidateAttributeNames().length; j < matrixes[i]
                    .getCandidateAttributeNames().length + tempNewCandAttributes.get(i).size(); j++)
                {
                    candidateAttributes[j] = tempNewCandAttributes.get(i).get(
                        j - matrixes[i].getCandidateAttributeNames().length);
                }

                for (int j = 0; j < matrixes[i].getTargetAttributeNames().length; j++)
                {
                    targetAttributes[j] = matrixes[i].getTargetAttributeNames()[j];
                }
                for (int j = matrixes[i].getTargetAttributeNames().length; j < matrixes[i]
                    .getTargetAttributeNames().length + tempNewTargetAttributes.get(i).size(); j++)
                {
                    targetAttributes[j] = tempNewTargetAttributes.get(i).get(
                        j - matrixes[i].getTargetAttributeNames().length);
                }

                tempNewMatrix = new double[targetAttributes.length][candidateAttributes.length];
                for (int j = 0; j < targetAttributes.length; j++)
                    for (int k = 0; k < candidateAttributes.length; k++)
                        tempNewMatrix[j][k] = 0;
                for (int j = 0; j < oldRowCount; j++)
                    for (int k = 0; k < oldColCount; k++)
                        tempNewMatrix[j][k] = matrixes[i].getMatchConfidenceAt(j, k);
                matrixes[i].setMatchMatrix(tempNewMatrix);

                Vector<String> toAddTarget = tempNewTargetAttributes.get(i);
                Vector<String> toAddCandidate = tempNewCandAttributes.get(i);
                // now add new attributes...
                for (int j = 0; j < toAddCandidate.size(); j++)
                    matrixes[i].addNewAttributeToCandidateSchema(toAddCandidate.get(j));
                for (int j = 0; j < toAddTarget.size(); j++)
                    matrixes[i].addNewAttributeToTargetSchema(toAddTarget.get(j));

            }
            // phase two - equalise matrixes permutation over candidate and target attributes
            for (int i = 0; i < matrixes.length; i++)
                permuteMatrix(matrixes[i], null);
        }

        // before moving to next step check if need to continue
        boolean foundNoneEmptyRemoveList = false;
        for (int i = 0; i < matrixes.length; i++)
        {
            if (!tempToRemoveCandAttributes.get(i).isEmpty())
            {
                foundNoneEmptyRemoveList = true;
                break;
            }
        }
        if (!foundNoneEmptyRemoveList)
        {
            for (int i = 0; i < matrixes.length; i++)
                if (!tempToRemoveTargetAttributes.get(i).isEmpty())
                {
                    foundNoneEmptyRemoveList = true;
                    break;
                }
        }
        if (foundNoneEmptyRemoveList)
        {
            // return new matrixes
            Iterator<String> it;
            for (int i = 0; i < matrixes.length; i++)
            {
                String[] diffCandAttributes = differentiateAttributes(
                    matrixes[i].getCandidateAttributeNames(), tempToRemoveCandAttributes.get(i));
                String[] diffTargetAttributes = differentiateAttributes(
                    matrixes[i].getTargetAttributeNames(), tempToRemoveTargetAttributes.get(i));

                // fix the matrix
                tempNewMatrix = new double[diffTargetAttributes.length][diffCandAttributes.length];
                for (int j = 0; j < diffTargetAttributes.length; j++)
                    for (int k = 0; k < diffCandAttributes.length; k++)
                        tempNewMatrix[j][k] = matrixes[i].getMatchConfidenceByAttributeNames(
                            diffCandAttributes[k], diffTargetAttributes[j]);

                matrixes[i].setMatchMatrix(tempNewMatrix);

                // now remove attributes
                it = tempToRemoveCandAttributes.get(i).iterator();
                while (it.hasNext())
                {
                    matrixes[i].removeAttributeFromCandidateSchema(it.next());
                }
                it = tempToRemoveTargetAttributes.get(i).iterator();
                while (it.hasNext())
                {
                    matrixes[i].removeAttributeFromTargetSchema(it.next());
                }
            }
        }// if no new/remove just equalise permutations
         // phase two - equalise matrixes permutation over candidate and target attributes
        for (int i = 0; i < matrixes.length; i++)
            permuteMatrix(matrixes[i], null);

        return matrixes;
    }
}