package ac.technion.iem.ontobuilder.gui.ontology;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import ac.technion.iem.ontobuilder.core.ontology.Relationship;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.core.utils.properties.PropertiesHandler;
import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.application.PropertiesTableModel;
import ac.technion.iem.ontobuilder.gui.elements.MultilineLabel;
import ac.technion.iem.ontobuilder.gui.elements.TextField;
import ac.technion.iem.ontobuilder.gui.utils.hypertree.NodeHyperTree;

/**
 * <p>
 * Title: RelationshipGui
 * </p>
  * <p>Description: Implements the methods of the Relationship used by the GUI</p>
  * Extends {@link OntologyObjectGui}
 */
public class RelationshipGui extends OntologyObjectGui
{
    private Relationship relationship;
    protected static Relationship r;
    protected static Term aTarget;
    
    public RelationshipGui(Relationship relationship)
    {
        super(relationship);
        this.relationship = relationship;
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
                PropertiesHandler.getResourceString("ontology.relationship.name"), relationship.getName()
            },
            {
                PropertiesHandler.getResourceString("ontology.relationship.source"), relationship.getSource()
            },
            {
                PropertiesHandler.getResourceString("ontology.relationship.target"), relationship.getTarget()
            }
        };
        JTable properties = new JTable(new PropertiesTableModel(columnNames, 3, data));
        return properties;
    }

    public DefaultMutableTreeNode getTreeBranch()
    {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(this);
        DefaultMutableTreeNode sources = new DefaultMutableTreeNode(
            PropertiesHandler.getResourceString("ontology.relationship.source"));
        sources.add(new DefaultMutableTreeNode(relationship.getSource()));
        root.add(sources);
        DefaultMutableTreeNode targets = new DefaultMutableTreeNode(
            PropertiesHandler.getResourceString("ontology.relationship.targets"));
        targets.add(new DefaultMutableTreeNode(relationship.getTarget()));
        root.add(targets);
        return root;
    }

    public NodeHyperTree getHyperTreeNode()
    {
        NodeHyperTree root = new NodeHyperTree(this, NodeHyperTree.RELATIONSHIP);
        NodeHyperTree sources = new NodeHyperTree(
            PropertiesHandler.getResourceString("ontology.relationship.source"),
            NodeHyperTree.TERM);
        sources.add(new NodeHyperTree(relationship.getSource(), NodeHyperTree.TERM));
        root.add(sources);
        NodeHyperTree targets = new NodeHyperTree(
            PropertiesHandler.getResourceString("ontology.relationship.targets"),
            NodeHyperTree.TERM);
        targets.add(new NodeHyperTree(relationship.getTarget(), NodeHyperTree.TERM));
        root.add(targets);
        return root;
    }

    public static Relationship createRelationshipDialog(final Term source, OntologyGui model)
    {
        final TextField txtRelationshipName = new TextField(15);

        final JDialog dialog = new JDialog((JFrame) null,
            PropertiesHandler.getResourceString("ontology.relationship.dialog.windowTitle"),
            true);
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        dialog.setSize(new Dimension(PropertiesHandler
            .getIntProperty("ontology.relationship.dialog.width"), PropertiesHandler
            .getIntProperty("ontology.relationship.dialog.height")));
        dialog.setLocationRelativeTo(null);
        // dialog.setResizable(false);

        JPanel south = new JPanel();
        south.setLayout(new FlowLayout());
        final JButton okButton;
        south.add(okButton = new JButton(PropertiesHandler
            .getResourceString("ontology.relationship.dialog.button.ok")));
        dialog.getRootPane().setDefaultButton(okButton);
        okButton.setEnabled(false);
        okButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                r = new Relationship(source, txtRelationshipName.getText(), aTarget);
                dialog.dispose();
            }
        });
        JButton cancelButton;
        south.add(cancelButton = new JButton(PropertiesHandler
            .getResourceString("ontology.relationship.dialog.button.cancel")));
        cancelButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                r = null;
                aTarget = null;
                dialog.dispose();
            }
        });
        panel.add(BorderLayout.SOUTH, south);

        JPanel center = new JPanel(new GridBagLayout());
        panel.add(BorderLayout.CENTER, center);
        center.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        {// Title
            JLabel title = new JLabel(
                PropertiesHandler.getResourceString("ontology.relationship"),
                ApplicationUtilities.getImage("relationship.gif"), SwingConstants.LEFT);
            title.setFont(new Font(dialog.getFont().getFontName(), Font.BOLD, dialog.getFont()
                .getSize() + 6));
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.weightx = 1;
            gbcl.gridwidth = 2;
            gbcl.fill = GridBagConstraints.HORIZONTAL;
            gbcl.insets = new Insets(0, 0, 10, 0);
            gbcl.anchor = GridBagConstraints.NORTHWEST;
            center.add(title, gbcl);
        }

        {// Explanation
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = 1;
            gbcl.gridwidth = 2;
            gbcl.weightx = 1;
            gbcl.fill = GridBagConstraints.HORIZONTAL;
            gbcl.insets = new Insets(0, 0, 20, 0);
            gbcl.anchor = GridBagConstraints.WEST;
            center.add(
                new MultilineLabel(PropertiesHandler
                    .getResourceString("ontology.relationship.dialog.explanation")), gbcl);
        }

        {// Name
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = 2;
            gbcl.insets = new Insets(0, 0, 5, 5);
            gbcl.anchor = GridBagConstraints.EAST;
            JLabel name = new JLabel(
                PropertiesHandler.getResourceString("ontology.relationship.name") + ":");
            name.setFont(new Font(dialog.getFont().getName(), Font.BOLD, dialog.getFont().getSize()));
            center.add(name, gbcl);

            gbcl.gridx = 1;
            gbcl.anchor = GridBagConstraints.WEST;
            gbcl.fill = GridBagConstraints.HORIZONTAL;
            center.add(txtRelationshipName, gbcl);
            txtRelationshipName.addKeyListener(new KeyAdapter()
            {
                public void keyTyped(KeyEvent event)
                {
                    SwingUtilities.invokeLater(new Runnable()
                    {
                        public void run()
                        {
                            if (!txtRelationshipName.getText().trim().equals("") && aTarget != null)
                                okButton.setEnabled(true);
                            else
                                okButton.setEnabled(false);
                        }
                    });
                }
            });
        }

        {// Source
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = 3;
            gbcl.insets = new Insets(0, 0, 5, 5);
            gbcl.anchor = GridBagConstraints.EAST;
            JLabel sourceLabel = new JLabel(
                PropertiesHandler.getResourceString("ontology.relationship.source") + ":");
            sourceLabel.setFont(new Font(dialog.getFont().getName(), Font.BOLD, dialog.getFont()
                .getSize()));
            center.add(sourceLabel, gbcl);

            gbcl.gridx = 1;
            gbcl.anchor = GridBagConstraints.WEST;
            center.add(new JLabel(source.getName(), ApplicationUtilities.getImage("term.gif"),
                SwingConstants.LEFT), gbcl);
        }

        {// Target
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = 4;
            gbcl.insets = new Insets(0, 0, 0, 5);
            gbcl.anchor = GridBagConstraints.NORTHEAST;
            JLabel targetLabel = new JLabel(
                PropertiesHandler.getResourceString("ontology.relationship.target") + ":");
            targetLabel.setFont(new Font(dialog.getFont().getName(), Font.BOLD, dialog.getFont()
                .getSize()));
            center.add(targetLabel, gbcl);

            gbcl.gridx = 1;
            gbcl.weighty = 1;
            gbcl.weightx = 1;
            gbcl.anchor = GridBagConstraints.NORTH;
            gbcl.fill = GridBagConstraints.BOTH;
            JTree terms = model.getTermsHierarchy();
            center.add(new JScrollPane(terms), gbcl);
            terms.addTreeSelectionListener(new TreeSelectionListener()
            {
                public void valueChanged(TreeSelectionEvent event)
                {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) ((JTree) event
                        .getSource()).getLastSelectedPathComponent();
                    if (node != null)
                    {
                        if (node.getUserObject() instanceof Term)
                            aTarget = (Term) node.getUserObject();
                        else
                            aTarget = null;
                        if (!txtRelationshipName.getText().trim().equals("") && aTarget != null)
                            okButton.setEnabled(true);
                        else
                            okButton.setEnabled(false);
                    }
                }
            });
        }

        {// Separator
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridy = 5;
            gbc.gridwidth = 2;
            gbc.gridheight = GridBagConstraints.REMAINDER;
            gbc.fill = GridBagConstraints.VERTICAL;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            center.add(new JPanel(), gbc);
        }

        dialog.addWindowListener(new WindowAdapter()
        {
            public void windowOpened(WindowEvent e)
            {
                SwingUtilities.invokeLater(new Runnable()
                {
                    public void run()
                    {
                        txtRelationshipName.requestFocus();
                    }
                });
            }

            public void windowClosing(WindowEvent e)
            {
                r = null;
                aTarget = null;
                dialog.dispose();
            }
        });
        dialog.setContentPane(panel);

        dialog.setVisible(true);// show();
        return r;
    }
    
    @Override
    public String toString()
    {
    	return relationship == null ? "<NULL>" : relationship.toString();
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
		RelationshipGui other = (RelationshipGui) obj;
		if (relationship == null)
		{
			if (other.relationship != null)
				return false;
		}
		else if (!relationship.equals(other.relationship))
			return false;
		return true;
	}
}
