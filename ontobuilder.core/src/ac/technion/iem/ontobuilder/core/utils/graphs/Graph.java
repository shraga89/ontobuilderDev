package ac.technion.iem.ontobuilder.core.utils.graphs;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Graph
{
	
	private Set<GraphCell> _roots;
	private Connections _connections;
	
	public Graph()
	{
		_roots = new HashSet<GraphCell>();
		_connections = new Connections();
	}

	public Collection<? extends GraphCell> getRootCells()
	{
		return _roots;
	}
	
	public void insert(Collection<? extends GraphCell> roots, Connections cs)
	{
		_roots.addAll(roots);
		_connections.addAll(cs);
	}
	
	public Connections getConnections()
	{
		return _connections;
	}

}
