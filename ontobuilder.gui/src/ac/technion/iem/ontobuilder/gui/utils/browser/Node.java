package ac.technion.iem.ontobuilder.gui.utils.browser;

import java.net.URL;

/**
 * <p>Title: Node</p>
 * <p>Description: A node in the linked list of the browsing history. Contains the URL associated to the node and
 * references to the nodes before and after</p>
 */
public class Node
{
    Node prev;
    Node next;
    URL url;

    public Node(URL url)
    {
        this.url = url;
        prev = null;
        next = null;
    }
}
