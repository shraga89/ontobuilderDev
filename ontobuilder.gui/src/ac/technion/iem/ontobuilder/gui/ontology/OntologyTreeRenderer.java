package ac.technion.iem.ontobuilder.gui.ontology;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;

/**
 * <p>Title: OntologyTreeRenderer</p>
 * Extends {@link DefaultTreeCellRenderer}
 */
public class OntologyTreeRenderer extends DefaultTreeCellRenderer
{
    private static final long serialVersionUID = 1L;

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel,
        boolean expanded, boolean leaf, int row, boolean hasFocus)
    {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        Object object = node.getUserObject();
        if (node.isRoot())
            setIcon(ApplicationUtilities.getImage("ontology.gif"));
        else if (object instanceof String)
        {
            String nodeString = (String) object;
            if (nodeString.equals(ApplicationUtilities.getResourceString("ontology.terms")) ||
                nodeString.equals(ApplicationUtilities.getResourceString("ontology.subterms")))
            {
                if (expanded)
                    setIcon(ApplicationUtilities.getImage("termsopen.gif"));
                else
                    setIcon(ApplicationUtilities.getImage("termsclosed.gif"));
            }
            else if (nodeString.equals(ApplicationUtilities.getResourceString("ontology.classes")) ||
                nodeString.equals(ApplicationUtilities
                    .getResourceString("ontology.class.subclasses")))
            {
                if (expanded)
                    setIcon(ApplicationUtilities.getImage("classesopen.gif"));
                else
                    setIcon(ApplicationUtilities.getImage("classesclosed.gif"));
            }
            else if (nodeString.equals(ApplicationUtilities
                .getResourceString("ontology.relationships")))
            {
                if (expanded)
                    setIcon(ApplicationUtilities.getImage("relationshipsopen.gif"));
                else
                    setIcon(ApplicationUtilities.getImage("relationshipsclosed.gif"));
            }
            else if (nodeString.equals(ApplicationUtilities.getResourceString("ontology.axioms")))
            {
                if (expanded)
                    setIcon(ApplicationUtilities.getImage("axiomsopen.gif"));
                else
                    setIcon(ApplicationUtilities.getImage("axiomsclosed.gif"));
            }
            else if (nodeString.equals(ApplicationUtilities
                .getResourceString("ontology.attributes")))
            {
                if (expanded)
                    setIcon(ApplicationUtilities.getImage("attributesopen.gif"));
                else
                    setIcon(ApplicationUtilities.getImage("attributesclosed.gif"));
            }
            else if (nodeString.equals(ApplicationUtilities
                .getResourceString("ontology.relationship.source")))
                setIcon(ApplicationUtilities.getImage("rsource.gif"));
            else if (nodeString.equals(ApplicationUtilities
                .getResourceString("ontology.relationship.targets")))
                setIcon(ApplicationUtilities.getImage("rtargets.gif"));
            else if (nodeString.equals(ApplicationUtilities.getResourceString("ontology.domain")))
                setIcon(ApplicationUtilities.getImage("domain.gif"));
        }
        else if (object instanceof TermGui)
            setIcon(ApplicationUtilities.getImage("term.gif"));
        else if (object instanceof OntologyClassGui)
            setIcon(ApplicationUtilities.getImage("class.gif"));
        else if (object instanceof RelationshipGui)
            setIcon(ApplicationUtilities.getImage("relationship.gif"));
        else if (object instanceof AxiomGui)
            setIcon(ApplicationUtilities.getImage("axiom.gif"));
        else if (object instanceof AttributeGui)
            setIcon(ApplicationUtilities.getImage("attribute.gif"));
        else if (object instanceof DomainGui)
            setIcon(ApplicationUtilities.getImage("domain.gif"));
        else if (object instanceof DomainEntryGui)
            setIcon(ApplicationUtilities.getImage("domainentry.gif"));
        return this;
    }
}