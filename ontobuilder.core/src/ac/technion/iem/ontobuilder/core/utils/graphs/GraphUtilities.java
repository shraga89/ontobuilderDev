package ac.technion.iem.ontobuilder.core.utils.graphs;

import java.util.ArrayList;
import java.util.Iterator;

import ac.technion.iem.ontobuilder.core.ontology.Term;


public class GraphUtilities
{
	
	public static GraphCell getCellWithObject(Graph graph, Object object)
	{
		for (GraphCell cell : graph.getRootCells())
        {
			if (cell.getUserObject() != null && cell.getUserObject().equals(object))
				return cell;
        }
        return null;
	}

    /**
     * Get all the parents of a specific cell
     * 
     * @param cell the default cell to start from
     * @return a list with the parent nodes
     */
    public static ArrayList<GraphCell> getParents(GraphCell cell)
    {
        ArrayList<GraphCell> targets = new ArrayList<GraphCell>();
        for (GraphCell childCell : cell.getChildren())
        {
            if (!(childCell instanceof GraphPort))
                continue;
            GraphPort port = (GraphPort) childCell;
            if (port.getUserObject() != null && port.getUserObject().equals("toParent"))
            {
                for (Iterator<?> e = port.edges(); e.hasNext();)
                {
                    GraphEdge edge = (GraphEdge) e.next();
                    GraphPort targetPort = (GraphPort) edge.getTarget();
                    if (targetPort == port)
                    {
                    	GraphPort sourcePort = (GraphPort) edge.getSource();
                        if (sourcePort != null)
                            targets.add(sourcePort.getParent());
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
    public static ArrayList<GraphCell> getAllParents(GraphCell cell)
    {
        ArrayList<GraphCell> targets = new ArrayList<GraphCell>();
        for (Object object : cell.getChildren())
        {
            if (!(object instanceof GraphPort))
                continue;
            GraphPort port = (GraphPort) object;
            if (port.getUserObject() != null && port.getUserObject().equals("toParent"))
            {
                for (GraphEdge edge : port.getEdges())
                {
                    GraphPort targetPort = edge.getTarget();
                    if (targetPort == port)
                    {
                    	GraphPort sourcePort = edge.getSource();
                        if (sourcePort != null)
                        {
                            GraphCell parent = (GraphCell) sourcePort.getParent();
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
     * Get the children nodes of a specific cell
     * 
     * @param cell the default cell to start from
     * @return a list with the children nodes
     */
    public static ArrayList<GraphCell> getChildren(GraphCell cell)
    {
        ArrayList<GraphCell> targets = new ArrayList<GraphCell>();
        for (GraphCell childCell : cell.getChildren())
        {
            if (!(childCell instanceof GraphPort))
                continue;
            GraphPort port = (GraphPort) childCell;
            if (port.getUserObject() != null && port.getUserObject().equals("toChild"))
            {
                for (Iterator<?> e = port.edges(); e.hasNext();)
                {
                	GraphEdge edge = (GraphEdge) e.next();
                    GraphPort sourcePort = (GraphPort) edge.getSource();
                    if (sourcePort == port)
                    {
                    	GraphPort targetPort = (GraphPort) edge.getTarget();
                        if (targetPort != null)
                            targets.add(targetPort.getParent());
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
    public static ArrayList<GraphCell> getSiblings(GraphCell cell)
    {
        ArrayList<GraphCell> parents = getParents(cell);
        ArrayList<GraphCell> siblings = new ArrayList<GraphCell>();
        for (GraphCell parent : parents)
        {
            ArrayList<GraphCell> children = getChildren(parent);
            for (GraphCell child : children)
            {
                if (!child.equals(cell))
                    siblings.add(child);
            }
        }
        return siblings;
    }

    /**
     * Get the cell which contains a specific object
     * 
     * @param cells the list of cells to check in
     * @param object the object to look for
     * @return the cell with the object
     */
    public static GraphCell getCellWithObject(ArrayList<GraphCell> cells, Object object)
    {
        for (Iterator<?> i = cells.iterator(); i.hasNext();)
        {
            Object o = i.next();
            if (o instanceof GraphCell)
            {
                GraphCell cell = (GraphCell) o;
                if (cell.getUserObject() != null && cell.getUserObject().equals(object))
                    return cell;
            }
        }
        return null;
    }
	
}
