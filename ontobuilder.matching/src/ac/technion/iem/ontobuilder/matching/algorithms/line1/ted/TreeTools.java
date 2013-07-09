package ac.technion.iem.ontobuilder.matching.algorithms.line1.ted;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A generic class for operations on trees.
 * Any tree based class that wants to use it needs to supply a wrapper that implements <code>TreeWrapper</code>
 * 
 * @author user
 *
 * @param <T>
 * 
 * @see TreeWrapper
 */
public class TreeTools<T> {

	private TreeWrapper<T> base;
	
	/**
	 * 
	 * @param base The wrapper for the tree based class that this TreeTools object is to operate on.
	 */
	public TreeTools(TreeWrapper<T> base){
		this.base = base;
	}
	
	/**
	 * Maps each tree node to the leaf that is the most left descendant of that node.
	 * 
	 * @return the mapping of nodes to their most left leaf descendants.
	 */
	public Map<T, T> leftDescendents(){
		Map<T, T> leftDescendents = new HashMap<T, T>();
		for(T t: base.getAllChildren(base.getRoot())){
			if(base.isLeaf(t)){
				T parent = t;
				while(parent != null){
					leftDescendents.put(parent, t);
					if(!base.isFirst(parent)){
						break; //the current node is not a left most child so leaf t won't be the most left descendant of ancestors from now on
					}
					parent = base.getParent(parent);
				}
			}
		}
		return leftDescendents;	
	}
	
	/**
	 * Returns the collection of all key roots in the tree.
	 * (all nodes that aren't most left descendants of other nodes than themselves).
	 * 
	 * @return the collection of all key roots in the tree.
	 */
	public Set<T> keyRoots(){
		Collection<T> left = leftDescendents().values();
		Set<T> keys = new HashSet<T>();
		for(T t: leftDescendents().keySet()){
			if(Collections.frequency(left, t) < 2){
				keys.add(t);
			}
		}
		return keys;
	}
	
	/**
	 * Returns the nodes of the tree in post order ordering.
	 * 
	 * @return the nodes of the tree in post order ordering.
	 */
	public List<T> postOrder(){
		List<T> ordered = new ArrayList<T>();
		postOrderRec(base.getRoot(), ordered);
		return ordered;
	}
	
	private void postOrderRec(T root, List<T> list){
		T left = null;
		for(T t: base.getChildren(root)){
			if(base.isFirst(t)){
				left = t;
			}
		}
		while(left != null){
			postOrderRec(left, list);
			left = base.getRightBrother(left);
		}
		list.add(root);
	}
}
