package ac.technion.iem.ontobuilder.core.utils.graphs;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class GraphPort extends GraphCell
{
	
	private Set<GraphEdge> _edges;
	
	public GraphPort()
	{
		super();
		_edges = new HashSet<GraphEdge>();
	}
	
	public GraphPort(Object userObject)
	{
		super(userObject);
		_edges = new HashSet<GraphEdge>();
	}
	
	public void addEdge(GraphEdge edge)
	{
		_edges.add(edge);
	}
	
	public void removeEdge(GraphEdge edge)
	{
		_edges.remove(edge);
	}
	
	public Set<GraphEdge> getEdges()
	{
		return _edges;
	}
	
	public void setEdges(Set<GraphEdge> edges)
	{
		_edges = edges;
	}
	
	public Iterator<GraphEdge> edges()
	{
		return _edges.iterator();
	}

	@Override
	public int hashCode()
	{
//		final int prime = 31;
//		int result = 1;
//		result = prime * result + ((_edges == null) ? 0 : _edges.hashCode());
		return super.hashCode();//result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GraphPort other = (GraphPort) obj;
		if (_edges == null)
		{
			if (other._edges != null)
				return false;
		}
		else if (!_edges.equals(other._edges))
			return false;
		return true;
	}
	
}
