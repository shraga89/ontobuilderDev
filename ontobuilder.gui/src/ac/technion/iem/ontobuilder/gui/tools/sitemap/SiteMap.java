package ac.technion.iem.ontobuilder.gui.tools.sitemap;

import hypertree.HyperTree;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import ac.technion.iem.ontobuilder.core.utils.dom.DOMUtilities;
import ac.technion.iem.ontobuilder.core.utils.network.NetworkUtilities;
import ac.technion.iem.ontobuilder.gui.tools.sitemap.event.SiteMapOperationEvent;
import ac.technion.iem.ontobuilder.gui.tools.sitemap.event.SiteMapOperationListener;
import ac.technion.iem.ontobuilder.gui.tools.sitemap.event.URLVisitedEvent;
import ac.technion.iem.ontobuilder.gui.tools.sitemap.event.URLVisitedListener;
import ac.technion.iem.ontobuilder.gui.utils.files.html.AElementGui;
import ac.technion.iem.ontobuilder.gui.utils.files.html.HTMLUtilitiesGui;
import ac.technion.iem.ontobuilder.gui.utils.hypertree.NodeHyperTree;

/**
* <p>Title: SiteMap</p>
 */
public class SiteMap
{
    public static final String CRAWLING_DEPTH_PROPERTY = "crawlingDepth";

    public static final short OPERATION_FINALIZE = 0;
    public static final short OPERATION_BEGIN = 1;

    private SiteMapNode root;
    private String domain;
    private Vector<URLVisit> visitedList;
    private ArrayList<URLVisitedListener> urlListeners;
    private ArrayList<SiteMapOperationListener> operationListeners;

    private static boolean stop;

    /**
     * Constructs a SiteMap
     * 
     * @param url the URL of the site
     */
    public SiteMap(URL url)
    {
        this.root = new SiteMapNode(url, "");
        domain = NetworkUtilities.getURLDomain(url);
        urlListeners = new ArrayList<URLVisitedListener>();
        operationListeners = new ArrayList<SiteMapOperationListener>();
    }

    /**
     * Get the root of the site map
     * 
     * @return SiteMapNode a root node
     */
    public SiteMapNode getRoot()
    {
        return root;
    }

    public void addSiteMapOperationListener(SiteMapOperationListener l)
    {
        operationListeners.add(l);
    }

    public void removeSiteMapOperationListener(SiteMapOperationListener l)
    {
        operationListeners.remove(l);
    }

    protected void fireSiteMapOperationEvent(short operation)
    {
        for (Iterator<URLVisitedListener> i = urlListeners.iterator(); i.hasNext();)
        {
            SiteMapOperationListener l = (SiteMapOperationListener) i.next();
            l.operationPerformed(new SiteMapOperationEvent(this, operation));
        }
    }

    public void addURLVisitedListener(URLVisitedListener l)
    {
        urlListeners.add(l);
    }

    public void removeURLVisitedListener(URLVisitedListener l)
    {
        urlListeners.remove(l);
    }

    protected void fireURLVisited(URL url, int depth)
    {
        for (Iterator<URLVisitedListener> i = urlListeners.iterator(); i.hasNext();)
        {
            URLVisitedListener l = (URLVisitedListener) i.next();
            l.urlVisited(new URLVisitedEvent(this, url, depth));
        }
    }

    /**
     * Visit a URL with a certain depth
     * 
     * @param url the URL to visit
     * @param currentDepth the depth to visit
     */
    private void visitURL(URL url, int currentDepth)
    {
        URLVisit visitedURL;
        for (int i = 0; i < visitedList.size(); i++)
        {
            visitedURL = (URLVisit) visitedList.get(i);
            if (visitedURL.url.sameFile(url) && visitedURL.depth >= currentDepth)
                return;
        }
        visitedList.add(new URLVisit(url, currentDepth));
    }

    /**
     * Check if a URL can be expended to a certain depth
     * 
     * @param url the URL to visit
     * @param currentDepth the depth to visit
     * @return true if the depth can be visited in the URL
     */
    private boolean canExpandURL(URL url, int currentDepth)
    {
        URLVisit visitedURL;
        for (int i = 0; i < visitedList.size(); i++)
        {
            visitedURL = (URLVisit) visitedList.get(i);
            if (visitedURL.url.sameFile(url) && visitedURL.depth > currentDepth)
                return false;
        }
        return true;
    }

    /**
     * Constructs a site-map recursively
     * 
     * @param node the node to start from
     * @param depth the depth to reach
     * @param out the location to print to
     * @throws IOException
     */
    private void constructSiteMapRecursive(SiteMapNode node, int depth, PrintWriter out)
        throws IOException
    {
        if (hasToStop())
            return;
        fireURLVisited(node.getURL(), depth);
        if (depth <= 0)
            return;
        depth--;
        org.w3c.dom.Document document;
        try
        {
            document = DOMUtilities.getDOM(node.getURL(), out);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return;
        }
        if (document == null)
            return;

        // Get the document title
        NodeList titles = document.getElementsByTagName("title");
        org.w3c.dom.Element title = null;
        if (titles.getLength() > 0)
            title = (Element) titles.item(0);
        node.setTitle(DOMUtilities.getTextValue(title));

        ArrayList<?> _A_Elements = HTMLUtilitiesGui.getAElements(document, node.getURL());
        for (int i = 0; i < _A_Elements.size(); i++)
        {
            AElementGui aElement = (AElementGui) _A_Elements.get(i);
            visitURL(aElement.getURL(), depth);
        }
        for (int i = 0; i < _A_Elements.size(); i++)
        {
            AElementGui aElement = (AElementGui) _A_Elements.get(i);
            SiteMapNode childNode = new SiteMapNode(aElement.getURL(), aElement.getDescription());
            node.addChild(childNode);
            if (NetworkUtilities.isSameDomain(aElement.getURL(), domain) &&
                canExpandURL(aElement.getURL(), depth))
                constructSiteMapRecursive(childNode, depth, out);
        }
    }

    /**
     * Constructs a site-map recursively
     * 
     * @param depth the depth to reach
     * @param out the location to print to
     * @throws IOException
     */
    public void constructSiteMap(int depth, PrintWriter out) throws IOException
    {
        fireSiteMapOperationEvent(OPERATION_BEGIN);
        visitedList = new Vector<URLVisit>();
        visitURL(root.getURL(), depth);
        stop = false;
        constructSiteMapRecursive(root, depth, out);
        visitedList.clear();
        fireSiteMapOperationEvent(OPERATION_FINALIZE);
    }

    /**
     * Stops the visit to the site
     */
    public synchronized void stop()
    {
        stop = true;
        try
        {
            SiteMapFileWriter.write(visitedList);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Checks if the visiting of a site needs to stop
     * 
     * @return true if it should stop
     */
    private synchronized boolean hasToStop()
    {
        return stop;
    }

    /**
     * Get the map of the site
     * 
     * @return a JTree
     */
    public JTree getJTreeMap()
    {
        DefaultMutableTreeNode siteRoot = new DefaultMutableTreeNode(root);
        getJTreeMapRecursive(siteRoot, root);
        JTree tree = new JTree(siteRoot);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        ToolTipManager.sharedInstance().registerComponent(tree);
        tree.setCellRenderer(new SiteMapTreeRenderer());
        return tree;
    }

    /**
     * Get a JTree of the map site recursively
     * 
     * @param treeNode the tree node
     * @param siteMapNode the site map node
     */
    private void getJTreeMapRecursive(DefaultMutableTreeNode treeNode, SiteMapNode siteMapNode)
    {
        for (int i = 0; i < siteMapNode.getChildCount(); i++)
        {
            DefaultMutableTreeNode childTreeNode = new DefaultMutableTreeNode(
                siteMapNode.getChild(i));
            treeNode.add(childTreeNode);
            getJTreeMapRecursive(childTreeNode, siteMapNode.getChild(i));
        }
    }

    /**
     * Get a HyperTree
     * 
     * @return the HyperTree
     */
    public HyperTree getHyperTreeMap()
    {
        NodeHyperTree siteRoot = new NodeHyperTree(root, NodeHyperTree.CLASS);
        getHyperTreeMapRecursive(siteRoot, root);
        HyperTree tree = new HyperTree(siteRoot);
        return tree;
    }

    /**
     * Get a HyperTree recursively
     * 
     * @param treeNode the tree node
     * @param siteMapNode the site map node
     */
    private void getHyperTreeMapRecursive(NodeHyperTree treeNode, SiteMapNode siteMapNode)
    {
        for (int i = 0; i < siteMapNode.getChildCount(); i++)
        {
            NodeHyperTree childTreeNode = new NodeHyperTree(siteMapNode.getChild(i),
                NodeHyperTree.CLASS);
            treeNode.add(childTreeNode);
            getHyperTreeMapRecursive(childTreeNode, siteMapNode.getChild(i));
        }
    }
}