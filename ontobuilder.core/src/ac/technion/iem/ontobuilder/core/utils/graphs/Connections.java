package ac.technion.iem.ontobuilder.core.utils.graphs;

import java.util.HashSet;
import java.util.Set;


public class Connections
{
	
	public class Connection
	{
		private GraphEdge _edge;
		private GraphPort _port;
		private boolean _sourcePort;
		
		public Connection(GraphEdge edge, GraphPort port, boolean isSourcePort)
		{
			_edge = edge;
			_port = port;
			_sourcePort = isSourcePort;
		}
		
		public GraphEdge getEdge()
		{
			return _edge;
		}
		
		public GraphPort getPort()
		{
			return _port;
		}
		
		public boolean isSourcePort()
		{
			return _sourcePort;
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + ((_edge == null) ? 0 : _edge.hashCode());
			result = prime * result + ((_port == null) ? 0 : _port.hashCode());
			result = prime * result + (_sourcePort ? 1231 : 1237);
			return result;
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
			Connection other = (Connection) obj;
			if (_edge == null)
			{
				if (other._edge != null)
					return false;
			}
			else if (!_edge.equals(other._edge))
				return false;
			if (_port == null)
			{
				if (other._port != null)
					return false;
			}
			else if (!_port.equals(other._port))
				return false;
			if (_sourcePort != other._sourcePort)
				return false;
			return true;
		}
	}
	
	private Set<Connection> _connectionSet;
	
	public Connections()
	{
		_connectionSet = new HashSet<Connections.Connection>();
	}
	
	public void connect(GraphEdge edge, GraphPort port, boolean isSourcePort)
	{
		Connection connection = new Connection(edge, port, isSourcePort);
		if (isSourcePort)
		{
			edge.setSource(port);
		}
		else
		{
			edge.setTarget(port);
		}
		port.addEdge(edge);
		_connectionSet.add(connection);
	}
	
	public Set<Connection> getConnections()
	{
		return _connectionSet;
	}
	
	public void addAll(Connections cs)
	{
		_connectionSet.addAll(cs.getConnections());
	}
}
