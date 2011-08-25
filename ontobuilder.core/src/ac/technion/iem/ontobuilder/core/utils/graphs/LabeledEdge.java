package ac.technion.iem.ontobuilder.core.utils.graphs;

/**
 * <p>
 * Title: LabeledEdge
 * </p>
 * <p>
 * Description: Object used for SimilarityFlodingAlgorithm . Basically, a
 * directed edge with a label connecting two LabeledVertex objects
 * </p>
 * 
 * @author Tomer Hary and Amir Taller 08/2005
 */

public class LabeledEdge
{
    private String label;
    private LabeledVertex source;
    private LabeledVertex target;
    private float weight;

    /**
     * 
     * Constructs a default LabeledEdge
     *
     */
    public LabeledEdge()
    {
    }

    /**
     * Constructs a LabeledEdge
     *
     * @param source source vertex
     * @param target target vertex
     * @param label the label to attach to the edge
     * @param weight the weight to attach to the edge
     */
    public LabeledEdge(LabeledVertex source, LabeledVertex target, String label, float weight)
    {
        this(source, target, label);
        setWeight(weight);
    }

    /**
     * Constructs a LabeledEdge
     *
     * @param source source vertex
     * @param target target vertex
     * @param label the label to attach to the edge
     */
    public LabeledEdge(LabeledVertex source, LabeledVertex target, String label)
    {
        setSource(source);
        setTarget(target);
        setLabel(label);
    }

    /**
     * Get the label of the edge
     *
     * @return the label
     */
    public String getLabel()
    {
        return label;
    }

    /**
     * Set the label of the edge
     *
     * @param label the label to set
     */
    public void setLabel(String label)
    {
        this.label = label;
    }

    /**
     * Get the source vertex of the edge
     *
     * @param source the source vertex
     */
    public void setSource(LabeledVertex source)
    {
        this.source = source;
    }

    /**
     * Get the target vertex of the edge
     *
     * @param target the target vertex
     */
    public void setTarget(LabeledVertex target)
    {
        this.target = target;
    }

    /**
     * Set the weight of the edge
     *
     * @param _weight the weight to set
     */
    public void setWeight(float _weight)
    {
        weight = _weight;
    }

    /**
     * Get the weight of the edge
     *
     * @return the weight
     */
    public float getWeight()
    {
        return weight;
    }

    /**
     * Get the source vertex of the edge
     *
     * @return the source vertex
     */
    public LabeledVertex getSource()
    {
        return source;
    }

    /**
     * Get the source target of the edge
     *
     * @return the target vertex
     */
    public LabeledVertex getTarget()
    {
        return target;
    }

    /**
     * Get the tag of the edge
     *
     * @return the string with the tag
     */
    public String getTag()
    {
        return source.getLabel() + "_" + label + "_" + target.getLabel();
    }

    public String toString()
    {
        return getSource().getName() + "--" + getLabel() + "--" + getTarget().getName();
    }

}
