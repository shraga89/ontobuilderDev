package ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Vector;

/**
 * <p>
 * Title:EdgesSet
 * </p>
 * <p>
 * Description:represents an edge group
 * </p>
 * Implements {@link Serializable}
 * 
 * @author Haggai Roitman
 * @version 1.1
 */

public class EdgesSet implements Serializable
{
    /** matrix for deciding if an edge belongs to the set */

    private static final long serialVersionUID = -6793867241151426508L;

    /** Vertexes in associated graph */
    private int vertexesCount = 0;
    /** group members holder */
    private Vector<Edge> members = new Vector<Edge>();
    /** number of members in the group */
    protected int numOfMembers = 0;

    /**
     * Constructs a default EdgesSet
     */
    public EdgesSet()
    {
    }

    /**
     * Constructs an EdgesSet
     * 
     * @param initialMembers - initial members to put in the group, a list of {@link Edge}
     */
    public EdgesSet(Vector<Edge> initialMembers)
    {// O(1)
        members = initialMembers;
        numOfMembers = members.size();
    }

    /**
     * Constructs an EdgesSet
     * 
     * @param vc - vertexes count in the graph
     */
    public EdgesSet(int vc)
    {// O(1)
        this();
        vertexesCount = vc;
    }

    /**
     * Get the number of vertexes in the set
     * 
     * @return the number
     */
    public int getVertexesCount()
    {
        return getVc();
    }

    /**
     * Set the number of vertexes in the set
     * 
     * @param vertexesCount the number of vertexes to set
     */
    public void setVertexesCount(int vertexesCount)
    {
        this.vertexesCount = vertexesCount;
    }

    /**
     * Constructs an EdgesSet
     * 
     * @param initialEdges - list of {@link Edge} to initiate the group with
     * @param vc - vertexes count in the graph
     */
    public EdgesSet(Vector<Edge> initialEdges, int vc)
    {// O(E)
        this(initialEdges);
        vertexesCount = vc;
    }

    /**
     * Get the number of vertexes in the graph
     * 
     * @return vertexes count in the graph
     */
    public int getVc()
    {// O(1)
        return vertexesCount;
    }

    /**
     * Get all the edges that are in the first set but not in the second set
     * 
     * @param e1 - first Edges Set
     * @param e2 - second Edges Set
     * @return E1\E2,an {@link EdgesSet}
     */
    public static EdgesSet minus(EdgesSet e1, EdgesSet e2)
    {// O(E)
        EdgesSet resultSet = new EdgesSet(e1.getVc());
        Iterator<Edge> it = e1.getMembers().iterator();
        while (it.hasNext())
        {
            Edge eToCheck = (Edge) it.next();
            if (!(e2.contains(eToCheck)))
                resultSet.addMember(eToCheck);
        }
        return resultSet;
    }

    /**
     * Get the intersection between two edge sets
     * 
     * @param e1 - first Edges Set
     * @param e2 - second Edges Set
     * @return E1 intersect E2 ( = E1\(E1\E2) )
     */
    public static EdgesSet intersect(EdgesSet e1, EdgesSet e2)
    {// E1 intersect E2 = E1\(E1\E2)
        return minus(e1, minus(e1, e2));// O(E)
    }

    /**
     * Check is an edge exist in the set
     * 
     * @param e - edge to check if belongs to group
     * @return <code>true</code> if edge belongs to group
     */
    public boolean contains(Edge e)
    {// O(1)
        return members.contains(e);
    }

    /**
     * Unions two edges groups
     * 
     * @param e1 - first Edges Group
     * @param e2 - second Edges Group
     * @return E1 U E2
     */
    public static EdgesSet union(EdgesSet e1, EdgesSet e2)
    {// O(E)
        EdgesSet resultGroup = new EdgesSet(e1.getVc());
        Iterator<Edge> it = e1.getMembers().iterator();
        while (it.hasNext())
            resultGroup.addMember(it.next());
        it = e2.getMembers().iterator();
        while (it.hasNext())
        {
            Edge edgeToCheck = (Edge) it.next();
            if (!e1.contains(edgeToCheck))
                resultGroup.addMember(edgeToCheck);
        }
        return resultGroup;
    }

    /**
     * Add an edge to the edge set
     * 
     * @param e {@link Edge} to add to the group
     */
    public void addMember(Edge e)
    {// O(1)
        members.add(members.size(), e);
        numOfMembers++;
    }

    /**
     * Removes edge from the group
     * 
     * @param e {@link Edge} to remove
     * @return <code>true</code> if the edge was in the group
     */
    public boolean remove(Edge e)
    {
        return members.remove(e);
    }

    /**
     * Unions a given edge with edges group
     * 
     * @param e1 - edges group
     * @param e - edge to add
     * @return E1 U edge
     */
    public static EdgesSet union(EdgesSet e1, Edge e)
    {// O(1)
        if (!e1.contains(e))
            e1.addMember(e);
        return e1;
    }

    /**
     * Clones the edge set
     * 
     * @return a new edge set
     */
    public Object clone()
    {// O(E)
        EdgesSet cloned = new EdgesSet(this.getVc());
        Iterator<Edge> it = members.iterator();
        while (it.hasNext())
        {
            cloned.addMember((it.next()).clone());
        }
        return cloned;
    }

    /**
     * Get the edge in the set that has a certain source vertex id
     * 
     * @param i the id to check
     * @return the {@link Edge} if it is found, else null
     */
    public Edge getEdgeThatStartsWith(int i)
    {
        Edge e;
        Iterator<Edge> it = members.iterator();
        while (it.hasNext())
        {
            e = it.next();
            if (e.getSourceVertexID() == i)
                return e;
        }
        return null;
    }

    /**
     * Get the edge in the set that has a certain source vertex id and has the maximum weight
     * 
     * @param i the id to check
     * @return the {@link Edge} if it is found, else null
     */
    public Edge getMaximalEdgeThatStartsWith(int i)
    {
        Edge e;
        Edge maxEdge = null;
        Iterator<Edge> it = members.iterator();
        double maxWeight = Double.MIN_VALUE;
        while (it.hasNext())
        {
            e = it.next();
            if (e.getSourceVertexID() == i && e.getEdgeWeight() > maxWeight)
            {
                maxEdge = e;
                maxWeight = maxEdge.getEdgeWeight();
            }
        }
        return maxEdge;
    }

    /**
     * @return printable string of the edges group
     */
    public String printEdgesSet()
    {// O(E)
        String result = "";
        result += ("{");
        for (int i = 0; i < numOfMembers; i++)
        {
            Edge e = (Edge) members.get(i);
            result += (", (" + (e.getSourceVertexID() + 1) + "," + (e.getTargetVertexID() + 1) +
                " <" + e.getEdgeWeight() + ">" + ")");
        }
        result += ("}");
        return result;
    }

    /**
     * Turns over the edges in the group
     */
    public void turnOverEdges(boolean weightTurn)
    {// O(E)
        for (int i = 0; i < numOfMembers; i++)
        {
            Edge e = (Edge) members.get(i);
            e.turnOverEdgeDirection();
            if (weightTurn)
                e.turnOverWeight();
        }
    }

    /**
     * Get the sum of edges in the group
     * 
     * @return edges in group weight sum
     */
    public double getEdgesSetWeight()
    {// O(E)
        double weight = 0;
        Iterator<Edge> it = members.iterator();
        while (it.hasNext())
        {
            Edge eTmp = it.next();
            weight += eTmp.getEdgeWeight();
        }
        return weight;
    }

    /**
     * Release resources
     */
    public void nullify()
    {
        try
        {
            members.clear();
        }
        catch (NullPointerException e)
        {
        }
    }

    /**
     * Get the group of edges in the set
     * 
     * @return group members, a list of {@link Edge}
     */
    public Vector<Edge> getMembers()
    {
        return members;
    }

    /**
     * Set the edges in the set
     * 
     * @param members the edges to set
     */
    public void setMembers(Vector<Edge> members)
    {
        this.members = members;
    }

    /**
     * Get the number of edges in the set
     * 
     * @return the number
     */
    public int getNumOfMembers()
    {
        return numOfMembers;
    }

    /**
     * Set the number of edges in the set
     * 
     * @param numOfMembers the number to set
     */
    public void setNumOfMembers(int numOfMembers)
    {
        this.numOfMembers = numOfMembers;
    }

    /**
     * Get an edge according to an index
     * 
     * @param index - index of the member in the group to return
     * @return the member
     */
    public Edge getMember(int index)
    {
        return members.get(index);
    }

    /**
     * Get the group size
     * 
     * @return group size
     */
    public int size()
    {
        return numOfMembers;
    }

    /**
     * @return <code>true</code> if group is empty
     */
    public boolean isEmpty()
    {
        if (numOfMembers == 0)
            return true;
        return false;
    }
}