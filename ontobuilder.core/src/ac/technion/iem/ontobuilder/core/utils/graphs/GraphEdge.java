package ac.technion.iem.ontobuilder.core.utils.graphs;


public class GraphEdge extends GraphCell
{

	private GraphPort _source;
	private GraphPort _target;
	
	public GraphEdge()
	{
		super();
	}
	
	public GraphEdge(Object userObject)
	{
		super(userObject);
	}

	public GraphPort getSource()
	{
		return _source;
	}

	public void setSource(GraphPort source)
	{
		this._source = source;
	}

	public GraphPort getTarget()
	{
		return _target;
	}

	public void setTarget(GraphPort target)
	{
		this._target = target;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_source == null) ? 0 : _source.hashCode());
		result = prime * result + ((_target == null) ? 0 : _target.hashCode());
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
		GraphEdge other = (GraphEdge) obj;
		if (_source == null)
		{
			if (other._source != null)
				return false;
		}
		else if (!_source.equals(other._source))
			return false;
		if (_target == null)
		{
			if (other._target != null)
				return false;
		}
		else if (!_target.equals(other._target))
			return false;
		return true;
	}
	
}
