package ac.technion.iem.ontobuilder.gui.ontology;

import java.awt.BorderLayout;
import java.awt.Component;
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
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;

import ac.technion.iem.ontobuilder.core.ontology.Attribute;
import ac.technion.iem.ontobuilder.core.ontology.Axiom;
import ac.technion.iem.ontobuilder.core.ontology.Domain;
import ac.technion.iem.ontobuilder.core.ontology.OntologyClass;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.core.utils.properties.PropertiesHandler;
import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.application.PropertiesTableModel;
import ac.technion.iem.ontobuilder.gui.elements.ComboBox;
import ac.technion.iem.ontobuilder.gui.elements.MultilineLabel;
import ac.technion.iem.ontobuilder.gui.elements.TextField;
import ac.technion.iem.ontobuilder.gui.utils.hypertree.NodeHyperTree;

/**
 * <p>
 * Title: OntologyClassGui
 * </p>
  * <p>Description: Implements the methods of the OntologyClass used by the GUI</p>
  * Extends {@link OntologyObjectGui}
 */
public class OntologyClassGui extends OntologyObjectGui
{
    private OntologyClass ontologyClass;
    protected static OntologyClass c;
    
    public OntologyClassGui(OntologyClass ontologyClass)
    {
        super(ontologyClass);
        this.ontologyClass = ontologyClass;
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
                PropertiesHandler.getResourceString("ontology.class.name"), ontologyClass.getName()
            },
            {
                PropertiesHandler.getResourceString("ontology.class.superclass"),
                ontologyClass.getSuperClass() != null ? ontologyClass.getSuperClass().getName() : null
            },
            {
                PropertiesHandler.getResourceString("ontology.domain"), ontologyClass.getDomain().getName()
            },
            {
                PropertiesHandler.getResourceString("ontology"), ontologyClass.getOntology()
            }
        };
        JTable properties = new JTable(new PropertiesTableModel(columnNames, 4, data));
        return properties;
    }

    public DefaultMutableTreeNode getTreeBranch()
    {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(this);

        DomainGui domainGui = new DomainGui(ontologyClass.getDomain());
        root.add(domainGui.getTreeBranch());

        DefaultMutableTreeNode attributesNode = new DefaultMutableTreeNode(
            PropertiesHandler.getResourceString("ontology.attributes"));
        root.add(attributesNode);
        for (Iterator<Attribute> i = ontologyClass.getAttributes().iterator(); i.hasNext();)
        {
            AttributeGui attributeGui = new AttributeGui((Attribute) i.next());
            attributesNode.add(attributeGui.getTreeBranch());
        }

        DefaultMutableTreeNode axiomsNode = new DefaultMutableTreeNode(
            PropertiesHandler.getResourceString("ontology.axioms"));
        root.add(axiomsNode);
        for (Iterator<Axiom> i = ontologyClass.getAxioms().iterator(); i.hasNext();)
        {
            AxiomGui axiomGui = new AxiomGui((Axiom) i.next());
            axiomsNode.add(axiomGui.getTreeBranch());
        }

        DefaultMutableTreeNode subClassesNode = new DefaultMutableTreeNode(
            PropertiesHandler.getResourceString("ontology.class.subclasses"));
        root.add(subClassesNode);
        for (Iterator<OntologyClass> i = ontologyClass.getInstances().iterator(); i.hasNext();)
        {
        	OntologyClass o = i.next();
            if (!(o instanceof Term))
            {
            	OntologyClassGui oGui = new OntologyClassGui(o);
                subClassesNode.add(oGui.getTreeBranch());
            }
        }
        return root;
    }

    public NodeHyperTree getHyperTreeNode(boolean showProperties)
    {
        NodeHyperTree root = new NodeHyperTree(this, NodeHyperTree.CLASS);

        if (showProperties)
        {
            DomainGui domainGui = new DomainGui(ontologyClass.getDomain());
            root.add(domainGui.getHyperTreeNode());

            NodeHyperTree attributesNode = new NodeHyperTree(
                PropertiesHandler.getResourceString("ontology.attributes"),
                NodeHyperTree.PROPERTY);
            root.add(attributesNode);
            for (Iterator<Attribute> i = ontologyClass.getAttributes().iterator(); i.hasNext();)
            {
                AttributeGui attributeGui = new AttributeGui((Attribute) i.next());
                attributesNode.add(attributeGui.getHyperTreeNode());
            }

            NodeHyperTree axiomsNode = new NodeHyperTree(
                PropertiesHandler.getResourceString("ontology.axioms"), NodeHyperTree.PROPERTY);
            root.add(axiomsNode);
            for (Iterator<Axiom> i = ontologyClass.getAxioms().iterator(); i.hasNext();)
            {
                axiomsNode.add(OntologyObjectGuiFactory.getOntologyObjectGui((Axiom) i.next()).getHyperTreeNode());
            }
        }

        NodeHyperTree subClassesNode = new NodeHyperTree(
            PropertiesHandler.getResourceString("ontology.class.subclasses"),
            NodeHyperTree.CLASS);
        root.add(subClassesNode);
        for (Iterator<OntologyClass> i = ontologyClass.getInstances().iterator(); i.hasNext();)
        {
            Object o = i.next();
            if (!(o instanceof Term))
                subClassesNode.add(getHyperTreeNode(showProperties));
        }
        return root;
    }
    
    public static OntologyClass createClassDialog(final OntologyClass parent)
    {
        final TextField txtClassName = new TextField(15);
        final ComboBox cmbClassDomain = new ComboBox(
            Domain.getPredefinedDomains());
        cmbClassDomain.setEditable(true);
        cmbClassDomain.setSelectedIndex(-1);
        cmbClassDomain.setRenderer(new javax.swing.plaf.basic.BasicComboBoxRenderer()
        {
            private static final long serialVersionUID = 1L;

            public Component getListCellRendererComponent(JList list, Object value, int index,
                boolean isSelected, boolean cellHasFocus)
            {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setIcon(ApplicationUtilities.getImage("domain.gif"));
                return this;
            }
        });

        final JDialog dialog = new JDialog((JFrame) null,
            ApplicationUtilities.getResourceString("ontology.class.dialog.windowTitle"), true);
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        dialog.setSize(new Dimension(PropertiesHandler
            .getIntProperty("ontology.class.dialog.width"), PropertiesHandler
            .getIntProperty("ontology.class.dialog.height")));
        dialog.setLocationRelativeTo(null);
        dialog.setResizable(false);

        JPanel south = new JPanel();
        south.setLayout(new FlowLayout());
        final JButton okButton;
        south.add(okButton = new JButton(PropertiesHandler
            .getResourceString("ontology.class.dialog.button.ok")));
        dialog.getRootPane().setDefaultButton(okButton);
        okButton.setEnabled(txtClassName.getText().trim().length() > 0);
        okButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                if (parent != null)
                    c = new OntologyClass(parent, txtClassName.getText());
                else
                    c = new OntologyClass(txtClassName.getText());
                String domain = cmbClassDomain.getText();
                if (domain != null && domain.trim().length() > 0)
                    c.getDomain().setName(domain);
                dialog.dispose();
            }
        });
        JButton cancelButton;
        south.add(cancelButton = new JButton(PropertiesHandler
            .getResourceString("ontology.class.dialog.button.cancel")));
        cancelButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                c = null;
                dialog.dispose();
            }
        });
        panel.add(BorderLayout.SOUTH, south);

        JPanel center = new JPanel(new GridBagLayout());
        panel.add(BorderLayout.CENTER, center);
        center.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        {// Title
            JLabel title = new JLabel(PropertiesHandler.getResourceString("ontology.class"),
                ApplicationUtilities.getImage("class.gif"), SwingConstants.LEFT);
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
                    .getResourceString("ontology.class.dialog.explanation")), gbcl);
        }

        {// Name
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = 2;
            gbcl.insets = new Insets(0, 0, 5, 5);
            gbcl.anchor = GridBagConstraints.EAST;
            JLabel name = new JLabel(PropertiesHandler.getResourceString("ontology.class.name") +
                ":");
            name.setFont(new Font(dialog.getFont().getName(), Font.BOLD, dialog.getFont().getSize()));
            center.add(name, gbcl);

            gbcl.gridx = 1;
            gbcl.anchor = GridBagConstraints.WEST;
            center.add(txtClassName, gbcl);
            txtClassName.addKeyListener(new KeyAdapter()
            {
                public void keyTyped(KeyEvent event)
                {
                    SwingUtilities.invokeLater(new Runnable()
                    {
                        public void run()
                        {
                            if (!txtClassName.getText().trim().equals(""))
                                okButton.setEnabled(true);
                            else
                                okButton.setEnabled(false);
                        }
                    });
                }
            });
        }

        {// Domain
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = 3;
            gbcl.insets = new Insets(0, 0, 5, 5);
            gbcl.anchor = GridBagConstraints.EAST;
            JLabel domain = new JLabel(PropertiesHandler.getResourceString("ontology.domain") +
                ":");
            center.add(domain, gbcl);

            gbcl.gridx = 1;
            gbcl.anchor = GridBagConstraints.WEST;
            center.add(cmbClassDomain, gbcl);
        }

        {// Separator
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridy = 4;
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
                        txtClassName.requestFocus();
                    }
                });
            }

            public void windowClosing(WindowEvent e)
            {
                c = null;
                dialog.dispose();
            }
        });
        dialog.setContentPane(panel);

        dialog.setVisible(true);// show();
        return c;
    }
    
    @Override
    public String toString()
    {
    	return ontologyClass == null ? "<NULL>" : ontologyClass.toString();
    }
    
    public OntologyClass getOntolgoyClass()
    {
        return ontologyClass;
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
		OntologyClassGui other = (OntologyClassGui) obj;
		if (ontologyClass == null)
		{
			if (other.ontologyClass != null)
				return false;
		}
		else if (!ontologyClass.equals(other.ontologyClass))
			return false;
		return true;
	}
}
