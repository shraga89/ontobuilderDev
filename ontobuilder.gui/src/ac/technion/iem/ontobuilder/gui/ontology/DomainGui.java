package ac.technion.iem.ontobuilder.gui.ontology;

import java.util.Iterator;

import javax.swing.JTable;
import javax.swing.tree.DefaultMutableTreeNode;

import ac.technion.iem.ontobuilder.core.ontology.Domain;
import ac.technion.iem.ontobuilder.core.ontology.DomainEntry;
import ac.technion.iem.ontobuilder.core.ontology.OntologyObject;
import ac.technion.iem.ontobuilder.core.utils.properties.PropertiesHandler;
import ac.technion.iem.ontobuilder.gui.application.PropertiesTableModel;
import ac.technion.iem.ontobuilder.gui.utils.hypertree.NodeHyperTree;

/**
 * <p>
 * Title: DomainGui
 * </p>
  * <p>Description: Implements the methods of the Domain used by the GUI</p>
  * Extends {@link OntologyObjectGui}
 */
public class DomainGui extends OntologyObjectGui
{
    private Domain domain;
    
    public DomainGui(Domain domain)
    {
        super(domain);
        this.domain = domain;
    }
    
    public JTable getProperties()
    {
        String columnNames[] =
        {
            PropertiesHandler.getResourceString("properties.attribute"),
            PropertiesHandler.getResourceString("properties.value")
        };
        Object data[][] =
        {
            {
                PropertiesHandler.getResourceString("ontology.domain.name"), domain.getName()
            },
            {
                PropertiesHandler.getResourceString("ontology.domain.type"), domain.getType()
            }
        };
        JTable properties = new JTable(new PropertiesTableModel(columnNames, 2, data));
        return properties;
    }

    public DefaultMutableTreeNode getTreeBranch()
    {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(this);
        for (Iterator<DomainEntry> i = domain.getEntries().iterator(); i.hasNext();)
        {;
            root.add(OntologyObjectGuiFactory.getOntologyObjectGui((OntologyObject) i.next()).getTreeBranch());
        }
        return root;
    }

    public NodeHyperTree getHyperTreeNode()
    {
        NodeHyperTree root = new NodeHyperTree(this, NodeHyperTree.PROPERTY);
        for (Iterator<DomainEntry> i = domain.getEntries().iterator(); i.hasNext();)
            root.add((OntologyObjectGuiFactory.getOntologyObjectGui((OntologyObject) i.next())).getHyperTreeNode());
        return root;
    }
    
    @Override
    public String toString()
    {
    	return domain == null ? "<NULL>" : domain.toString();
    }

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		DomainGui other = (DomainGui) obj;
		if (domain == null)
		{
			if (other.domain != null)
				return false;
		}
		else if (!domain.equals(other.domain))
			return false;
		return true;
	}
}
