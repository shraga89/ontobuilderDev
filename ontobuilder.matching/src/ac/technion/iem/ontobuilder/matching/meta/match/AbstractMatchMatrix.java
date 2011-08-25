package ac.technion.iem.ontobuilder.matching.meta.match;

import java.io.IOException;

/**
 * <p>Title: AbstractMatchMatrix</p>
 * <p>Description: Abstract class to hold a match matrix and perform actions</p>
 */
public abstract class AbstractMatchMatrix
{

    protected double[][] confidenceMatrix;

    /**
     * Constructs a default AbstractMatchMatrix
     */
    public AbstractMatchMatrix()
    {
    }

    public abstract double[][] getMatchMatrix();

    public abstract String[] getTargetAttributeNames();

    public abstract void setTargetAttributeNames(String[] names);

    public abstract String[] getCandidateAttributeNames();

    public abstract void setCandidatetAttributeNames(String[] names);

    public abstract void setMatchMatrix(double[][] matchCMatrix);

    public abstract int getRowCount();

    public abstract int getColCount();

    public abstract void copyWithEmptyMatrix(AbstractMatchMatrix matrix);

    public abstract void printMatchMatrix() throws IOException;

    public abstract void addNewAttributeToCandidateSchema(String attribute);

    public abstract void addNewAttributeToTargetSchema(String attribute);

    public abstract void removeAttributeFromCandidateSchema(String attribute);

    public abstract void removeAttributeFromTargetSchema(String attribute);

    public abstract void rearrangeCandidateAttributesPermutation(String[] newPermutation);

    public abstract void rearrangeTargetAttributesPermutation(String[] newPermutation);

    public abstract boolean containsCandidateAttribute(String attributeName);

    public abstract boolean containsTargetAttribute(String attributeName);

    public abstract double getMatchConfidenceByAttributeNames(String candAttrName,
        String targetAttrName);

    /**
     * Sets a confidence value in the Confidence Matrix to '0' if the original value isn't higher
     * than the threshold
     * 
     * @param th - the threshold
     */
    public void applyThreshold(double th)
    {
        for (int i = 0; i < getRowCount(); i++)
            for (int j = 0; j < getColCount(); j++)
            {
                confidenceMatrix[i][j] = (confidenceMatrix[i][j] >= th ? confidenceMatrix[i][j] : 0);
            }
    }

    /**
     * Set match confidence for a specific pair
     * 
     * @param i - row position
     * @param j - column position
     * @param confidence value
     */
    public void setMatchConfidenceAt(int i, int j, double confidence)
    {
        if (confidenceMatrix == null)
            throw new NullPointerException("match matrix hasn't been constructed yet");
        if (0 > i || i >= getRowCount() || 0 > j || j >= getColCount())
            throw new IllegalArgumentException("i=" + i + ",j=" + j + ",row=" + getRowCount() +
                ",col=" + getColCount());
        else
            confidenceMatrix[i][j] = confidence;
    }

    /**
     * Get the match confidence for a specific pair
     * 
     * @param i - row position
     * @param j - column position
     * @return the confidence value
     */
    public double getMatchConfidenceAt(int i, int j)
    {
        if (0 > i || i >= getRowCount() || 0 > j || j >= getColCount())
            throw new IllegalArgumentException("i=" + i + ",j=" + j + ",row=" + getRowCount() +
                ",col=" + getColCount());
        if (confidenceMatrix == null)
            throw new NullPointerException("match matrix hasn't been constructed yet");
        return confidenceMatrix[i][j];
    }

    /**
     * Normalise the Confidence Matrix value by dividing each pair's value in the maximum value in
     * the matrix
     */
    public void normalize()
    {
        double maxValue = 0;
        for (int i = 0; i < getRowCount(); i++)
            for (int j = 0; j < getColCount(); j++)
                maxValue = maxValue < confidenceMatrix[i][j] ? confidenceMatrix[i][j] : maxValue;
        if (maxValue != 0)
        {
            for (int i = 0; i < getRowCount(); i++)
                for (int j = 0; j < getColCount(); j++)
                    confidenceMatrix[i][j] /= maxValue;
        }

    }

}