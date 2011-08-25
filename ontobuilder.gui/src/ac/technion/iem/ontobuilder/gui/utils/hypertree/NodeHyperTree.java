package ac.technion.iem.ontobuilder.gui.utils.hypertree;

import hypertree.HTNode;

import java.awt.Color;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.JTable;

import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.application.ObjectWithProperties;
import ac.technion.iem.ontobuilder.gui.application.PropertiesTableModel;

/**
 * <p>Title: NodeHyperTree</p>
 * Implements {@link ObjectWithProperties} and {@link HTNode}
 */
public class NodeHyperTree implements ObjectWithProperties, HTNode
{
    public static final byte TERM = 0;
    public static final byte CLASS = 1;
    public static final byte RELATIONSHIP = 2;
    public static final byte PROPERTY = 3;

    protected String name;
    protected Hashtable<String, Object> children;
    protected byte type;

    /**
     * Constructs a NodeHyperTree
     * 
     * @param name the name of the node
     * @param type the type of the node (term, class, relationship, property)
     */
    public NodeHyperTree(String name, byte type)
    {
        this.name = name;
        this.type = type;
        children = new Hashtable<String, Object>();
    }

    /**
     * Constructs a NodeHyperTree
     * 
     * @param o the name of the node
     * @param type the type of the node (term, class, relationship, property)
     */
    public NodeHyperTree(Object o, byte type)
    {
        this.name = o.toString();
        this.type = type;
        children = new Hashtable<String, Object>();
    }

    /**
     * Adds a child to the node
     * 
     * @param o the child to add
     */
    public void add(Object o)
    {
        if (o == null)
            return;
        children.put(o.toString(), o);
    }

    /**
     * Get the child of the node
     * 
     * @param name the name of the child to get
     * @return the child
     */
    public Object getChild(String name)
    {
        return children.get(name);
    }

    /**
     * Get the name of the node
     * 
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Set the name of the node
     * 
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Get all the children of the node
     */
    public Enumeration<Object> children()
    {
        return children.elements();
    }

    /**
     * Check if the node is a leaf
     * 
     * @return true if it is a leaf
     */
    public boolean isLeaf()
    {
        return children.isEmpty();
    }

    /**
     * Get the properties of the node
     * 
     * @return a JTable
     */
    public JTable getProperties()
    {
        String columnNames[] =
        {
            ApplicationUtilities.getResourceString("properties.attribute"),
            ApplicationUtilities.getResourceString("properties.value")
        };
        Object data[][] =
        {
            {
                ApplicationUtilities.getResourceString("hypertree.node.name"), name
            }
        };
        JTable propertiesTable = new JTable(new PropertiesTableModel(columnNames, 1, data));
        return propertiesTable;
    }

    /**
     * Get the color of the node according to its type (term - yellow, class - red, relationship -
     * green, property - orange, else - green)
     * 
     * @return the color
     */
    public Color getColor()
    {
        switch (type)
        {
        case (TERM):
            return Color.YELLOW;
        case (CLASS):
            return Color.RED;
        case (RELATIONSHIP):
            return Color.GREEN;
        case (PROPERTY):
            return Color.ORANGE;
        default:
            return Color.WHITE;
        }

    }
}