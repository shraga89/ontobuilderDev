package ac.technion.iem.ontobuilder.gui.utils.graphs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import javax.swing.SwingConstants;
import javax.swing.tree.TreeNode;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;

import com.jgraph.JGraph;
import com.jgraph.graph.CellView;
import com.jgraph.graph.DefaultEdge;
import com.jgraph.graph.DefaultGraphCell;
import com.jgraph.graph.DefaultPort;
import com.jgraph.graph.EdgeView;
import com.jgraph.graph.GraphConstants;
import com.jgraph.graph.GraphModel;
import com.jgraph.graph.GraphView;
import com.jgraph.graph.PortView;

/**
 * <p>Title: GraphUtilities</p>
 * <p>Description: Graph utility methods for internal use</p>
 */
public class GraphUtilities
{
    public static final String SHOW_PRECEDENCE_LINKS_PROPERTY = "showPrecedenceLinks";
    public static final String SHOW_HIDDEN_ELEMENTS_PROPERTY = "showHiddenElements";

    // Used for alignment
    private static int width = ApplicationUtilities.getIntProperty("graph.cell.width");
    private static int height = ApplicationUtilities.getIntProperty("graph.cell.height");
    private static int offx;
    private static int offy;
    private static boolean showPrecedenceLinks = true;
    private static boolean showHiddenElements = false;

    /**
     * Set whether to show the precedence links
     * 
     * @param b <code>true</code> whether precedence links should be shown
     */
    public static void setShowPrecedenceLinks(boolean b)
    {
        showPrecedenceLinks = b;
    }

    /**
     * Get whether precedence links should be shown
     * 
     * @return <code>true</code> if they should be shown
     */
    public static boolean getShowPrecedenceLinks()
    {
        return showPrecedenceLinks;
    }

    /**
     * Get whether hidden elements should be shown
     * 
     * @return <code>true</code> if they should be shown
     */
    public static boolean isShowHiddenElements()
    {
        return showHiddenElements;
    }

    /**
     * Set whether to show the hidden elements
     * 
     * @param showHiddenElements <code>true</code> whether hidden elements should be shown
     */
    public static void setShowHiddenElements(boolean showHiddenElements)
    {
        GraphUtilities.showHiddenElements = showHiddenElements;
    }

    /**
     * Creates default graph attributes (e.g. BorderColor, Bounds)
     * 
     * @return a map with the default attributes
     */
    public static Map<?, ?> createDefaultAttributes()
    {
        Map<?, ?> map = GraphConstants.createMap();
        GraphConstants.setBorderColor(map, Color.black);
        // GraphConstants.setAutoSize(map, true);
        GraphConstants.setBounds(map, new Rectangle(width, height));
        GraphConstants.setHorizontalTextPosition(map, SwingConstants.RIGHT);
        GraphConstants.setHorizontalAlignment(map, SwingConstants.LEFT);
        return map;
    }

    /**
     * Aligns an hierarchy in the graph
     * 
     * @param graph the graph to align
     * @param align whether to align
     * @param x alignment parameter
     * @param y alignment parameter
     */
    public static void alignHierarchy(JGraph graph, int align, int x, int y)
    {
        GraphModel model = graph.getModel();
        DefaultGraphCell root = (DefaultGraphCell) model.getRootAt(0);
        alignHierarchy(graph, root, align, x, y);
    }

    /**
     * Aligns an hierarchy in the graph
     * 
     * @param graph the graph to align
     * @param root the default graph cell to start from
     * @param align whether to align
     * @param x alignment parameter
     * @param y alignment parameter
     */
    public static void alignHierarchy(JGraph graph, DefaultGraphCell root, int align, int x, int y)
    {
        offy = height + 15;
        if (align == SwingConstants.RIGHT)
        {
            x -= width;
            offx = -(width / 2 + 10);
        }
        else if (align == SwingConstants.LEFT)
        {
            offx = width / 2 + 10;
        }
        else
            throw new IllegalArgumentException("" + align);

        // Layout the parent
        alignHierarchyRec(graph, root, new Point(x, y), align);
        alignPrecedenceRec(graph, root, align);
    }

    /**
     * Align the precedence rectangle
     * 
     * @param graph the graph to align
     * @param root the default graph cell to start from
     * @param align whether to align
     */
    private static void alignPrecedenceRec(JGraph graph, DefaultGraphCell root, int align)
    {
        GraphView view = graph.getView();
        ArrayList<DefaultPort> precedencePorts = getPortsWithObject(root, "precedes");
        for (Iterator<DefaultPort> i = precedencePorts.iterator(); i.hasNext();)
        {
            DefaultPort precedencePort = (DefaultPort) i.next();
            // DefaultPort precedencePort=getPortWithObject(root,"precedes");

            if (precedencePort != null)
            {
                for (Iterator<?> p = precedencePort.edges(); p.hasNext();)
                {
                    DefaultEdge edge = (DefaultEdge) p.next();
                    DefaultPort precededPort = (DefaultPort) edge.getTarget();
                    PortView precededPortView = (PortView) view.getMapping(precededPort, false);
                    Map<?, ?> precededPortAttributes = precededPortView.getAttributes();
                    GraphConstants.setOffset(precededPortAttributes, new Point(
                        align == SwingConstants.LEFT ? GraphConstants.PERCENT : 0,
                        GraphConstants.PERCENT / 2));

                    DefaultGraphCell precededCell = (DefaultGraphCell) precededPort.getParent();
                    CellView precededCellView = view.getMapping(precededCell, false);
                    Map<?, ?> precededAttributes = precededCellView.getAttributes();
                    Rectangle precededBounds = GraphConstants.getBounds(precededAttributes);

                    PortView precedencePortView = (PortView) view.getMapping(precedencePort, false);
                    Map<?, ?> precedencePortAttributes = precedencePortView.getAttributes();
                    GraphConstants.setOffset(precedencePortAttributes, new Point(
                        align == SwingConstants.LEFT ? GraphConstants.PERCENT : 0,
                        GraphConstants.PERCENT / 2));

                    CellView precedenceCellView = view.getMapping(root, false);
                    Map<?, ?> precedenceAttributes = precedenceCellView.getAttributes();
                    Rectangle precedenceBounds = GraphConstants.getBounds(precedenceAttributes);

                    int precedenceX = precedenceBounds.x +
                        (align == SwingConstants.LEFT ? precedenceBounds.width : 0);
                    int precedenceY = precedenceBounds.y + precedenceBounds.height / 2;
                    int precededX = precededBounds.x +
                        (align == SwingConstants.LEFT ? precededBounds.width : 0);
                    int precededY = precededBounds.y + precededBounds.height / 2;

                    EdgeView edgeView = (EdgeView) view.getMapping(edge, false);
                    edgeView.addPoint(
                        1,
                        new Point(Math.min(precedenceX, precededX) +
                            ((align == SwingConstants.LEFT ? 1 : -1) * Math.abs(precedenceX -
                                precededX)) / 2 + (align == SwingConstants.LEFT ? 1 : -1) *
                            Math.abs(precedenceY - precededY), Math.min(precedenceY, precededY) +
                            (Math.abs(precedenceY - precededY)) / 2));
                    Map<?, ?> edgeAttributes = edgeView.getAttributes();
                    if (showPrecedenceLinks)
                    {
                        GraphConstants.setLineColor(edgeAttributes, Color.blue);
                        GraphConstants.setLineEnd(edgeAttributes, GraphConstants.TECHNICAL);
                        GraphConstants.setLineStyle(edgeAttributes, GraphConstants.QUADRATIC);
                        GraphConstants.setDashPattern(edgeAttributes, new float[]
                        {
                            3f, 3f
                        });
                    }
                    else
                    {
                        GraphConstants.setLineColor(edgeAttributes, Color.white);
                    }
                }
            }

        }

        DefaultPort port = getPortWithObject(root, "toChild");
        if (port != null)
        {
            Object children[] = getTargetCells(root);
            for (int i = 0; i < children.length; i++)
            {
                DefaultGraphCell child = (DefaultGraphCell) children[i];
                alignPrecedenceRec(graph, child, align);
            }
        }
    }

    /**
     * Align the hierarchy rectangle
     * 
     * @param graph the graph to align
     * @param root the default graph cell to start from
     * @param p the point to align to
     * @param align whether to align
     * @return the y parameter
     */
    private static int alignHierarchyRec(JGraph graph, DefaultGraphCell root, Point p, int align)
    {
        // GraphModel model=graph.getModel();
        GraphView view = graph.getView();

        // Layout the parent
        CellView rootView = view.getMapping(root, false);
        rootView.getBounds().setLocation(p.x, p.y);
        // Layout port to bottomcenter
        DefaultPort port = getPortWithObject(root, "toChild");
        if (port != null)
        {
            CellView portView = view.getMapping(port, false);
            Map<?, ?> portAttributes = portView.getAttributes();
            GraphConstants.setOffset(portAttributes, new Point(GraphConstants.PERCENT / 2,
                GraphConstants.PERCENT));
        }

        // Layout the children
        int childX = p.x + offx;
        int childY = p.y + offy;
        if (port != null)
        {
            Object children[] = getTargetCells(root);
            for (int i = 0; i < children.length; i++)
            {
                DefaultGraphCell child = (DefaultGraphCell) children[i];
                int edgeY = childY;
                childY = alignHierarchyRec(graph, child, new Point(childX, childY), align);
                // Layout port to leftcenter
                DefaultPort childPort = getPortWithObject(child, "toParent");
                CellView childPortView = view.getMapping(childPort, false);
                Map<?, ?> childPortAttributes = childPortView.getAttributes();
                GraphConstants.setOffset(childPortAttributes, new Point(
                    align == SwingConstants.RIGHT ? GraphConstants.PERCENT : 0,
                    GraphConstants.PERCENT / 2));
                // Layout the edge
                DefaultEdge edge = getEdge(port, childPort);
                EdgeView edgeView = (EdgeView) view.getMapping(edge, false);
                edgeView.addPoint(1, new Point(p.x + width / 2 - 1, edgeY + height / 2 - 1));
            }
        }
        return childY;
    }

    /**
     * Align the matches in the graph
     * 
     * @param graph the graph to align
     */
    public static void alignMatches(JGraph graph)
    {
        GraphModel model = graph.getModel();
        GraphView view = graph.getView();

        for (int i = 0; i < model.getRootCount(); i++)
        {
            Object o = model.getRootAt(i);
            if (!(o instanceof DefaultGraphCell))
                continue;
            DefaultGraphCell cell = (DefaultGraphCell) o;
            boolean isGood = true;
            DefaultPort candidatePort = getPortWithObject(cell, "toGoodTarget");
            if (candidatePort == null)
            {
                isGood = false;
                candidatePort = getPortWithObject(cell, "toBadTarget");
            }
            else
            {
                isGood = true;
            }
            if (candidatePort == null)
                continue;
            CellView candidatePortView = view.getMapping(candidatePort, false);
            Map<?, ?> candidatePortAttributes = candidatePortView.getAttributes();
            GraphConstants.setOffset(candidatePortAttributes, new Point(0,
                GraphConstants.PERCENT / 2));
            for (Iterator<?> e = candidatePort.edges(); e.hasNext();)
            {
                DefaultEdge edge = (DefaultEdge) e.next();
                DefaultPort targetPort = (DefaultPort) edge.getSource();
                if (targetPort != null)
                {
                    CellView targetPortView = view.getMapping(targetPort, false);
                    Map<?, ?> targetPortAttributes = targetPortView.getAttributes();
                    GraphConstants.setOffset(targetPortAttributes, new Point(
                        GraphConstants.PERCENT, GraphConstants.PERCENT / 2));

                    EdgeView edgeView = (EdgeView) view.getMapping(edge, false);
                    Map<?, ?> edgeAttributes = edgeView.getAttributes();
                    GraphConstants.setLineColor(edgeAttributes, (isGood ? Color.green : Color.red));
                    GraphConstants.setLineStyle(edgeAttributes, GraphConstants.QUADRATIC);
                    GraphConstants.setFontStyle(edgeAttributes, Font.BOLD);
                    GraphConstants.setVerticalTextPosition(edgeAttributes, SwingConstants.NORTH);

                    CellView targetCellView = view.getMapping(targetPort.getParent(), false);
                    Map<?, ?> targetCellAttributes = targetCellView.getAttributes();
                    Rectangle targetBounds = GraphConstants.getBounds(targetCellAttributes);
                    CellView candidateCellView = view.getMapping(cell, false);
                    Map<?, ?> candidateCellAttributes = candidateCellView.getAttributes();
                    Rectangle candidateBounds = GraphConstants.getBounds(candidateCellAttributes);
                    int x = Math.abs(targetBounds.x + targetBounds.width - candidateBounds.x) / 2;
                    int y = Math.abs(targetBounds.y + targetBounds.height / 2 -
                        (candidateBounds.y + candidateBounds.height / 2)) / 2;
                    edgeView.addPoint(1, new Point(targetBounds.x + targetBounds.width + x,
                        targetBounds.y + targetBounds.height / 2 + y - 15));
                }
            }
        }
    }

    public static DefaultPort getPortWithObject(DefaultGraphCell cell, Object object)
    {
        for (int i = 0; i < cell.getChildCount(); i++)
        {
            Object child = cell.getChildAt(i);
            if (child instanceof DefaultPort)
            {
                DefaultPort port = (DefaultPort) child;
                if (port.getUserObject() != null && port.getUserObject().equals(object))
                    return port;
            }
        }
        return null;
    }

    public static ArrayList<DefaultPort> getPortsWithObject(DefaultGraphCell cell, Object object)
    {
        ArrayList<DefaultPort> ports = new ArrayList<DefaultPort>();
        for (int i = 0; i < cell.getChildCount(); i++)
        {
            Object child = cell.getChildAt(i);
            if (child instanceof DefaultPort)
            {
                DefaultPort port = (DefaultPort) child;
                if (port.getUserObject() != null && port.getUserObject().equals(object))
                    ports.add(port);
            }
        }
        return ports;
    }

    /**
     * Get the cell with a specific object
     * 
     * @param graph the graph to get the cell from
     * @param object the object to check for
     * @return the cell with the object
     */
    public static DefaultGraphCell getCellWithObject(JGraph graph, Object object)
    {
        GraphModel model = graph.getModel();
        for (int i = 0; i < model.getRootCount(); i++)
        {
            Object o = model.getRootAt(i);
            if (o instanceof DefaultGraphCell)
            {
                DefaultGraphCell cell = (DefaultGraphCell) o;
                if (cell.getUserObject() != null && cell.getUserObject().equals(object))
                    return cell;
            }
        }
        return null;
    }

    /**
     * Get the edges of a cell in the graph
     * 
     * @param cell the cell to get the edges of
     * @return an array with the edges
     */
    public static Object[] getEdges(DefaultGraphCell cell)
    {
        ArrayList<DefaultEdge> edges = new ArrayList<DefaultEdge>();
        for (int i = 0; i < cell.getChildCount(); i++)
        {
            DefaultPort port = (DefaultPort) cell.getChildAt(i);
            for (Iterator<?> e = port.edges(); e.hasNext();)
            {
                DefaultEdge edge = (DefaultEdge) e.next();
                edges.add(edge);
            }
        }
        return edges.toArray();
    }

    /**
     * Gets a specific edge
     * 
     * @param source the source port
     * @param target the target port
     * @return the {@link DefaultEdge} that connects to source and target
     */
    public static DefaultEdge getEdge(DefaultPort source, DefaultPort target)
    {
        for (Iterator<?> e = source.edges(); e.hasNext();)
        {
            DefaultEdge edge = (DefaultEdge) e.next();
            if (edge.getTarget() == target)
                return edge;
        }
        return null;
    }

    /**
     * Get the target cells of a specific cell
     * 
     * @param cell the cell to start from
     * @return an array with the cells
     */
    public static Object[] getTargetCells(DefaultGraphCell cell)
    {
        ArrayList<TreeNode> targets = new ArrayList<TreeNode>();
        for (int i = 0; i < cell.getChildCount(); i++)
        {
            Object object = cell.getChildAt(i);
            if (!(object instanceof DefaultPort))
                continue;
            DefaultPort port = (DefaultPort) object;
            if (port.getUserObject() != null && port.getUserObject().equals("toChild"))
            {
                for (Iterator<?> e = port.edges(); e.hasNext();)
                {
                    DefaultEdge edge = (DefaultEdge) e.next();
                    DefaultPort sourcePort = (DefaultPort) edge.getSource();
                    if (sourcePort == port)
                    {
                        DefaultPort targetPort = (DefaultPort) edge.getTarget();
                        if (targetPort != null)
                            targets.add(targetPort.getParent());
                    }
                }
            }
        }

        // Sort based on precedence
        Collections.sort(targets, new CellComparator());

        return targets.toArray();
    }

    /**
     * Get the children nodes of a specific cell
     * 
     * @param cell the default cell to start from
     * @return a list with the children nodes
     */
    public static ArrayList<TreeNode> getChildren(DefaultGraphCell cell)
    {
        ArrayList<TreeNode> targets = new ArrayList<TreeNode>();
        for (int i = 0; i < cell.getChildCount(); i++)
        {
            Object object = cell.getChildAt(i);
            if (!(object instanceof DefaultPort))
                continue;
            DefaultPort port = (DefaultPort) object;
            if (port.getUserObject() != null && port.getUserObject().equals("toChild"))
            {
                for (Iterator<?> e = port.edges(); e.hasNext();)
                {
                    DefaultEdge edge = (DefaultEdge) e.next();
                    DefaultPort sourcePort = (DefaultPort) edge.getSource();
                    if (sourcePort == port)
                    {
                        DefaultPort targetPort = (DefaultPort) edge.getTarget();
                        if (targetPort != null)
                            targets.add(targetPort.getParent());
                    }
                }
            }
        }
        return targets;
    }

    /**
     * Get all the parents of a specific cell
     * 
     * @param cell the default cell to start from
     * @return a list with the parent cells
     */
    public static ArrayList<DefaultGraphCell> getAllParents(DefaultGraphCell cell)
    {
        ArrayList<DefaultGraphCell> targets = new ArrayList<DefaultGraphCell>();
        for (int i = 0; i < cell.getChildCount(); i++)
        {
            Object object = cell.getChildAt(i);
            if (!(object instanceof DefaultPort))
                continue;
            DefaultPort port = (DefaultPort) object;
            if (port.getUserObject() != null && port.getUserObject().equals("toParent"))
            {
                for (Iterator<?> e = port.edges(); e.hasNext();)
                {
                    DefaultEdge edge = (DefaultEdge) e.next();
                    DefaultPort targetPort = (DefaultPort) edge.getTarget();
                    if (targetPort == port)
                    {
                        DefaultPort sourcePort = (DefaultPort) edge.getSource();
                        if (sourcePort != null)
                        {
                            DefaultGraphCell parent = (DefaultGraphCell) sourcePort.getParent();
                            if (parent.getUserObject() instanceof Term)
                            {
                                targets.add(parent);
                                targets.addAll(getAllParents(parent));
                            }
                        }
                    }
                }
            }
        }
        return targets;
    }

    /**
     * Get all the parents of a specific cell
     * 
     * @param cell the default cell to start from
     * @return a list with the parent nodes
     */
    public static ArrayList<TreeNode> getParents(DefaultGraphCell cell)
    {
        ArrayList<TreeNode> targets = new ArrayList<TreeNode>();
        for (int i = 0; i < cell.getChildCount(); i++)
        {
            Object object = cell.getChildAt(i);
            if (!(object instanceof DefaultPort))
                continue;
            DefaultPort port = (DefaultPort) object;
            if (port.getUserObject() != null && port.getUserObject().equals("toParent"))
            {
                for (Iterator<?> e = port.edges(); e.hasNext();)
                {
                    DefaultEdge edge = (DefaultEdge) e.next();
                    DefaultPort targetPort = (DefaultPort) edge.getTarget();
                    if (targetPort == port)
                    {
                        DefaultPort sourcePort = (DefaultPort) edge.getSource();
                        if (sourcePort != null)
                            targets.add(sourcePort.getParent());
                    }
                }
            }
        }
        return targets;
    }

    /**
     * Get all the siblings of a specific cell
     * 
     * @param cell the default cell to start from
     * @return a list with the sibling cells
     */
    public static ArrayList<DefaultGraphCell> getSiblings(DefaultGraphCell cell)
    {
        ArrayList<TreeNode> parents = getParents(cell);
        ArrayList<DefaultGraphCell> siblings = new ArrayList<DefaultGraphCell>();
        for (Iterator<TreeNode> i = parents.iterator(); i.hasNext();)
        {
            DefaultGraphCell parent = (DefaultGraphCell) i.next();
            ArrayList<TreeNode> children = getChildren(parent);
            for (Iterator<TreeNode> j = children.iterator(); j.hasNext();)
            {
                DefaultGraphCell child = (DefaultGraphCell) j.next();
                if (!child.equals(cell))
                    siblings.add(child);
            }
        }
        return siblings;
    }

    /**
     * Get all the non-related cells to a specific cell
     * 
     * @param cell the default cell to start from
     * @return a list with the non-related
     */
    public static ArrayList<DefaultGraphCell> getUnrelated(JGraph graph, DefaultGraphCell cell)
    {
        ArrayList<DefaultGraphCell> targets = new ArrayList<DefaultGraphCell>();

        ArrayList<TreeNode> parents = getParents(cell);
        ArrayList<DefaultGraphCell> siblings = getSiblings(cell);

        Ontology om = ((Term) cell.getUserObject()).getOntology();

        GraphModel model = graph.getModel();
        outer: for (int i = 0; i < model.getRootCount(); i++)
        {
            Object o = model.getRootAt(i);
            if (o instanceof DefaultGraphCell)
            {
                DefaultGraphCell aCell = (DefaultGraphCell) o;
                if (aCell == null || aCell == cell || !(aCell.getUserObject() instanceof Term) ||
                    ((Term) aCell.getUserObject()).getOntology() != om)
                    continue;

                if (parents.contains(aCell))
                    continue outer;
                if (siblings.contains(aCell))
                    continue outer;
                targets.add(aCell);
            }
        }

        return targets;
    }

    /**
     * Get the depth of the graph starting from a specific node
     * 
     * @param node the cell to start from
     * @return the depth of the tree form that node
     */
    public static int getDepth(DefaultGraphCell node)
    {
        Object children[] = getTargetCells(node);
        if (children.length == 0)
            return 0;
        int depth = 0;
        for (int i = 0; i < children.length; i++)
        {
            DefaultGraphCell child = (DefaultGraphCell) children[i];
            depth = Math.max(depth, getDepth(child));
        }
        return 1 + depth;
    }

    /**
     * Create a buffered image of the specified graph
     */
    public static BufferedImage toImage(JGraph graph)
    {
        Object[] cells = graph.getRoots();
        if (cells.length > 0)
        {
            Rectangle bounds = graph.getCellBounds(cells);
            graph.toScreen(bounds);

            // Create a Buffered Image
            Dimension d = bounds.getSize();
            BufferedImage img = new BufferedImage(d.width + 10, d.height + 10,
                BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = img.createGraphics();
            graphics.translate(-bounds.x + 5, -bounds.y + 5);
            graph.paint(graphics);
            return img;
        }
        return null;
    }

    /**
     * Get the cell which contains a specific object
     * 
     * @param cells the list of cells to check in
     * @param object the object to look for
     * @return the cell with the object
     */
    public static DefaultGraphCell getCellWithObject(ArrayList<?> cells, Object object)
    {
        for (Iterator<?> i = cells.iterator(); i.hasNext();)
        {
            Object o = i.next();
            if (o instanceof DefaultGraphCell)
            {
                DefaultGraphCell cell = (DefaultGraphCell) o;
                if (cell.getUserObject() != null && cell.getUserObject().equals(object))
                    return cell;
            }
        }
        return null;
    }
}