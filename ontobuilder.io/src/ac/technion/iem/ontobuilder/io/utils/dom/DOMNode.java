package ac.technion.iem.ontobuilder.io.utils.dom;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * <p>Title: DOMNode</p>
 * Implements {@link ObjectWithProperties}
 * <br>Available types: 
 * {@link Node#DOCUMENT_NODE}, {@link Node#ELEMENT_NODE}, {@link Node#TEXT_NODE}, {@link Node#COMMENT_NODE}
 */
public class DOMNode
{
    Node node;

    /**
     * Constructs a DOMNode
     *
     * @param node the node this DOMNode represents
     */
    public DOMNode(Node node)
    {
        this.node = node;
    }

    /**
     * Get the node this DOMNode represents
     *
     * @return the node
     */
    public Node getNode()
    {
        return node;
    }

    /**
     * Set the node this DOMNode represents
     *
     * @param the node
     */
    public void setNode(Node node)
    {
        this.node = node;
    }

    /**
     * Get the node type
     *
     * @return the node type
     */
    public short getNodeType()
    {
        return node.getNodeType();
    }

    /**
     * Return a string representing the node
     */
    public String toString()
    {
        switch (node.getNodeType())
        {
        case Node.DOCUMENT_NODE:
            return ((Document) node).getDocumentElement().getTagName();
        case Node.ELEMENT_NODE:
            return node.getNodeName();
        case Node.TEXT_NODE:
            return node.getNodeValue();
        case Node.COMMENT_NODE:
            return node.getNodeValue();
        default:
            return node.toString();
        }
    }
}