package ac.technion.iem.ontobuilder.matching.algorithms.line1.ted;

import java.util.List;

/**
 * A generic base interface for wrappers of tree-based classes to be used by the TreeTools class.
 * 
 * @author sagi belder
 *
 * @param <T>
 */
public interface TreeWrapper<T> {

	/**
	 * Returns the root of the tree.
	 * 
	 * @return the root of the tree.
	 */
	public T getRoot();
	
	/**
	 * Returns the children of the given node.
	 * 
	 * @param node a tree node.
	 * @return the children of the given node.
	 */
	public List<T> getChildren(T node);
	
	/**
	 * Returns all the descendants of the given node.
	 * (all the nodes in the sub-tree rooted by the given node).
	 * 
	 * @param node a tree node.
	 * @return all the descendants of the given node.
	 */
	public List<T> getAllChildren(T node);
	
	/**
	 * Checks whether the given node is a leaf.
	 * 
	 * @param node a tree node.
	 * @return true if the given node is a leaf, false otherwise.
	 */
	public boolean isLeaf(T node);
	
	/**
	 * checks whether the given node is a left-most child.
	 * 
	 * @param node a tree node
	 * @return true if the given node is a left-most child, false otherwise.
	 */
	public boolean isFirst(T node);
	
	/**
	 * Returns the parent of the given node.
	 * 
	 * @param node a tree node.
	 * @return the parent of the given node.
	 */
	public T getParent(T node);
	
	/**
	 * Returns the right brother of the given node.
	 * 
	 * @param node a tree node.
	 * @return the right brother of the given node.
	 */
	public T getRightBrother(T node);
}
