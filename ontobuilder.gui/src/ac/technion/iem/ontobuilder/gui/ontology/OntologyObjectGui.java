package ac.technion.iem.ontobuilder.gui.ontology;

import javax.swing.tree.DefaultMutableTreeNode;

import ac.technion.iem.ontobuilder.core.ontology.OntologyObject;
import ac.technion.iem.ontobuilder.gui.application.ObjectWithProperties;
import ac.technion.iem.ontobuilder.gui.utils.hypertree.NodeHyperTree;

/**
 * <p>
 * Title: OntologyObjectGui
 * </p>
  * <p>Description: Implements the methods of the OntologyObject used by the GUI</p>
  * Implements {@link ObjectWithProperties}
 */
public abstract class OntologyObjectGui implements ObjectWithProperties
{   
    private OntologyObject ontologyObject;
    
    public OntologyObjectGui(OntologyObject ontologyObject)
    {
        this.ontologyObject = ontologyObject;
    }
    
    public DefaultMutableTreeNode getTreeBranch()
    {
        return new DefaultMutableTreeNode(this);
    }

    public NodeHyperTree getHyperTreeNode()
    {
        return new NodeHyperTree(this, NodeHyperTree.TERM);
    }

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ontologyObject == null) ? 0 : ontologyObject.hashCode());
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
		OntologyObjectGui other = (OntologyObjectGui) obj;
		if (ontologyObject == null)
		{
			if (other.ontologyObject != null)
				return false;
		}
		else if (!ontologyObject.equals(other.ontologyObject))
			return false;
		return true;
	}
}
