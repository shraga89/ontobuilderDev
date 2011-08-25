package ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Vector;

/**
 * <p>
 * Title: VertexesSet
 * </p>
 * <p>
 * Description: representation of vertex group
 * </p>
 * Implements {@link Serializable}
 * 
 * @author Haggai Roitman
 * @version 1.1
 */

public class VertexesSet implements Serializable
{
    private static final long serialVersionUID = 6207641717123401726L;

    /** group members holder */
    protected Vector<Vertex> members = new Vector<Vertex>();
    /** number of members in the group */
    protected int numOfMembers = 0;

    /**
     * Construct a default VertexesSet with an empty vertexes group
     */
    public VertexesSet()
    {
    }

    /**
     * Construct a VertexesSet
     * 
     * @param initialVertexes - initial vertexs to put on the group
     */
    public VertexesSet(Vector<Vertex> initialVertexes)
    {// O(1)
        members = initialVertexes;
        numOfMembers = initialVertexes.size();
    }

    /**
     * Release resources
     */
    public void nullify()
    {
        try
        {
            members.clear();
            members = null;
        }
        catch (NullPointerException e)
        {
        }
    }

    /**
     * Get the id of the vertex with the max id in the set
     * 
     * @return max vertex id in the group
     */
    public int maxVertexID()
    {// O(V)
        Iterator<Vertex> it = members.iterator();
        int maxID = -1;
        while (it.hasNext())
        {
            int vID = ((Vertex) it.next()).getVertexID();
            maxID = maxID >= vID ? maxID : vID;
        }
        return maxID;
    }

    /**
     * Get the id of the vertex with the min id in the set
     * 
     * @return min vertex id in the group
     */
    public int minVertexID()
    {// O(V)
        Iterator<Vertex> it = members.iterator();
        int minID = 100000000;
        while (it.hasNext())
        {
            int vID = ((Vertex) it.next()).getVertexID();
            minID = minID <= vID ? minID : vID;
        }
        return minID;
    }

    /**
     * Get the union of two VertexesSet
     * 
     * @param v1 - {@link VertexesSet} 1
     * @param v2 - {@link VertexesSet} 2
     * @return v1 U v2
     */
    public static VertexesSet union(VertexesSet v1, VertexesSet v2)
    {// O(E)
        return new VertexesSet(VertexesSet.plus(v1, v2).getMembers());// since V1<>V2
    }

    /**
     * Get the vertexes in the set
     * 
     * @return group members, a list of {@link Vertex}
     */
    public Vector<Vertex> getMembers()
    {
        return members;
    }

    /**
     * Set the vertexes in the set
     * 
     * @param members the {@link Vertex} list
     */
    public void setMembers(Vector<Vertex> members)
    {
        this.members = members;
    }

    /**
     * Get the number of vertexes in the set
     * 
     * @return the number
     */
    public int getNumOfMembers()
    {
        return numOfMembers;
    }

    /**
     * Set the number of vertexes in the set
     * 
     * @param numOfMembers the number of vertexes to set
     */
    public void setNumOfMembers(int numOfMembers)
    {
        this.numOfMembers = numOfMembers;
    }

    /**
     * Create a vertex set with all the vertexes in the first group and second group
     * 
     * @param g1 - Group1
     * @param g2 - Group2
     * @return Group1 plus Group2
     */
    public static VertexesSet plus(VertexesSet g1, VertexesSet g2)
    {// O(E)- assumes g1 intersect g2 = EMPTY GROUP
        VertexesSet resultGroup = new VertexesSet();
        Iterator<Vertex> it = g1.getMembers().iterator();
        while (it.hasNext())
            resultGroup.addMember(it.next());
        it = g2.getMembers().iterator();
        while (it.hasNext())
            resultGroup.addMember(it.next());
        return resultGroup;
    }

    /**
     * Add a vertex to the vertex set
     * 
     * @param m new member to add to the group
     */
    public void addMember(Vertex m)
    {// O(1)
        members.add(members.size(), m);
        numOfMembers++;
    }

    /**
     * Check if a vertex with a certain id is in the group
     * 
     * @param vID - vertex id to search in the group
     * @return <code>true</code> if vertex belongs to the group
     */
    public boolean isInVertexGroup(int vID)
    {// O(V)
        Iterator<Vertex> it = members.iterator();
        while (it.hasNext())
        {
            if (((Vertex) it.next()).getVertexID() == vID)
                return true;
        }
        return false;
    }

    /**
     * Get a printable string of the vertexes set
     * 
     * @return the string
     */
    public String getPrintableString()
    {
        String toReturn = "V = {";
        Iterator<Vertex> it = members.iterator();
        while (it.hasNext())
        {
            Vertex v = (Vertex) it.next();
            toReturn += "<V" + v.getVertexID() + ">,";
        }
        toReturn += "}";
        return toReturn;
    }

    /**
     * Get the number of vertexes in the set
     * 
     * @return group size
     */
    public int size()
    {
        return numOfMembers;
    }

    /**
     * Clone the vertex set
     * 
     * @return a new vertex set
     */
    public Object clone()
    {// O(V)
        VertexesSet clonedVertexSet = new VertexesSet();
        Iterator<Vertex> it = members.iterator();
        while (it.hasNext())
        {
            clonedVertexSet.addMember((it.next()).clone());
        }
        return clonedVertexSet;
    }
}