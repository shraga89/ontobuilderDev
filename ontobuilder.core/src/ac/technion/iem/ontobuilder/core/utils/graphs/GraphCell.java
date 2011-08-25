package ac.technion.iem.ontobuilder.core.utils.graphs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GraphCell
{
	
	private GraphCell _parent = null;
	private List<GraphCell> _children;
	private Object _userObject = null;
	
	public GraphCell()
	{
		this(null);
	}
	
	public GraphCell(Object userObject)
	{
		_userObject = userObject;
		_children = new ArrayList<GraphCell>();
	}
	
	public void setUserObject(Object userObject)
	{
		_userObject = userObject;
	}
	
	public Object getUserObject()
	{
		return _userObject;
	}
	
	public void setParent(GraphCell parent)
	{
		_parent = parent;
	}
	
	public GraphCell getParent()
	{
		return _parent;
	}
	
	public void addChild(GraphCell cell)
	{
		cell.setParent(this);
		_children.add(cell);
	}
	
	public void addAllChildren(Collection<? extends GraphCell> cells)
	{
		for (GraphCell cell : cells)
			addChild(cell);
	}
	
	public void removeChild(GraphCell cell)
	{
		cell.setParent(null);
		_children.remove(cell);
	}
	
	public int getChildCount()
	{
		return _children.size();
	}
	
	public List<? extends GraphCell> getChildren()
	{
		return _children;
	}
	
}
