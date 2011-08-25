package ac.technion.iem.ontobuilder.core.utils.graphs;

import java.util.Vector;

/**
 * <p>
 * Title: Labeled Vertex
 * </p>
 * <p>
 * Description: Object used for SimilarityFlodingAlgorithm. Used both to
 * represent a single named node (and then only the field 'name' is used), or a double named node
 * (and the also 'name2' is used).
 * </p>
 * 
 * @author Tomer Hary and Amir Taller 08/2005
 */

public class LabeledVertex
{
    private String name;
    private float weight;
    private String name2;

    /**
     * Constructs a single named LabeledVertex
     *
     * @param _name the first name of the vertex
     */
    public LabeledVertex(String _name)
    {
        name = _name;
    }

    private Vector<Object> edges = new Vector<Object>();
    private Vector<Object> incomingEdges = new Vector<Object>();
    private float weightNext;

    /**
     * 
     * Constructs a double named LabeledVertex
     *
     * @param _name1 the first name
     * @param _name2 the second name
     * @param _weight the weight
     */
    public LabeledVertex(String _name1, String _name2, float _weight)
    {
        this(_name1, _weight);
        setName2(_name2);
    }

    /**
     * 
     * Constructs a singled named LabeledVertex
     *
     * @param _name the name of the vertex
     * @param _weight the weight of the vertex
     */
    public LabeledVertex(String _name, float _weight)
    {
        this(_name);
        weight = _weight;
    }

    /**
     * Get the first name of the vertex
     *
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Set the first name of the vertex
     *
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Get the weight of the vertex
     *
     * @return the weight
     */
    public float getWeight()
    {
        return weight;
    }

    /**
     * Set the weight of the vertex
     *
     * @param weight the weight to set
     */
    public void setWeight(float weight)
    {
        this.weight = weight;
    }

    /**
     * Get the second name of a double named vertex
     *
     * @return the name
     */
    public String getName2()
    {
        return name2;
    }

    /**
     * Set the second name of a double named vertex 
     *
     * @param name2 the name
     */
    public void setName2(String name2)
    {
        this.name2 = name2;
    }

    /**
     * Get the label of the vertex
     *
     * @return the string with the label
     */
    public String getLabel()
    {
        return name + "_" + name2;
    }

    /**
     * Get all the outgoing edges from the vertex
     *
     * @return a vector with all the edges
     */
    public Vector<Object> getOutgoingEdges()
    {
        return edges;
    }

    /**
     * Get all the incoming edges from the vertex
     *
     * @return a vector with all the edges
     */
    public Vector<Object> getIncomingEdges()
    {
        return incomingEdges;
    }

    /**
     * Get the next weight of the vertex
     *
     * @return the weight
     */
    public float getWeightNext()
    {
        return weightNext;
    }

    /**
     * Set the next weight of the vertex
     *
     * @param the weight
     */
    public void setWeightNext(float weightNext)
    {
        this.weightNext = weightNext;
    }

    /**
     * Print all the outgoing edges from the vertex
     */
    public void printOutgoing()
    {
        print("Outgoing edges:");
        for (int i = 0; i < edges.size(); i++)
        {
            print("edge " + i + ": " + ((LabeledEdge) edges.get(i)).getLabel());
        }
    }

    /**
     * Print all the incoming edges from the vertex
     */
    public void printIncoming()
    {
        print("Incoming edges:");
        for (int i = 0; i < incomingEdges.size(); i++)
        {
            print("edge " + i + ": " + ((LabeledEdge) incomingEdges.get(i)).getLabel() +
                " weight = " + ((LabeledEdge) incomingEdges.get(i)).getWeight());
        }
    }

    private void print(String s)
    {
        System.out.println(s);
    }

}
