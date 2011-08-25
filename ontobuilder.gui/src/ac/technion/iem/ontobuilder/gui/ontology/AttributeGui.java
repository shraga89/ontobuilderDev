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
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;

import ac.technion.iem.ontobuilder.core.ontology.Attribute;
import ac.technion.iem.ontobuilder.core.ontology.Domain;
import ac.technion.iem.ontobuilder.core.ontology.OntologyObject;
import ac.technion.iem.ontobuilder.core.utils.properties.PropertiesHandler;
import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.application.PropertiesCellEditor;
import ac.technion.iem.ontobuilder.gui.application.PropertiesTableModel;
import ac.technion.iem.ontobuilder.gui.elements.ComboBox;
import ac.technion.iem.ontobuilder.gui.elements.MultilineLabel;
import ac.technion.iem.ontobuilder.gui.elements.TextField;
import ac.technion.iem.ontobuilder.gui.utils.hypertree.NodeHyperTree;

/**
 * <p>
 * Title: AttributeGui
 * </p>
 * <p>
 * Description: Implements the methods of the Attribute used by the GUI
 * </p>
 * Extends {@link OntologyObject}
 */
public class AttributeGui extends OntologyObjectGui
{
    private Attribute attribute;

    public AttributeGui(Attribute attribute)
    {
        super(attribute);
        this.attribute = attribute;
    }

    public JTable getProperties()
    {
        String columnNames[] =
        {
            PropertiesHandler.getResourceString("properties.attribute"),
            PropertiesHandler.getResourceString("properties.value")
        };
        Object data[][] = new Object[4][2];
        data[0][0] = PropertiesHandler.getResourceString("ontology.attribute.name");
        data[0][1] = attribute.getName();
        data[1][0] = PropertiesHandler.getResourceString("ontology.attribute.value");
        data[1][1] = (attribute.getTerm() != null ? attribute.getTerm().getAttributeValue(
            attribute.getName()) : (attribute.getOntologyClass() != null ? attribute
            .getOntologyClass().getAttributeValue(attribute.getName()) : attribute.getValue()));
        data[2][0] = PropertiesHandler.getResourceString("ontology.domain");
        data[2][1] = attribute.getDomain().getName();
        if (attribute.getTerm() != null)
        {
            data[3][0] = PropertiesHandler.getResourceString("ontology.term");
            data[3][1] = attribute.getTerm().getName();
        }
        else
        {
            data[3][0] = PropertiesHandler.getResourceString("ontology.class");
            data[3][1] = attribute.getOntologyClass().getName();
        }
        JTable properties = new JTable(new PropertiesTableModel(columnNames, 4, data))
        {
            private static final long serialVersionUID = 1L;

            public boolean isCellEditable(int row, int col)
            {
                return col == 1 && (row == 0 || row == 1);
            }
        };
        TableColumn valueColumn = properties.getColumn(columnNames[1]);
        valueColumn.setCellEditor(new PropertiesCellEditor());
        properties.getModel().addTableModelListener(new TableModelListener()
        {
            public void tableChanged(TableModelEvent e)
            {
                int row = e.getFirstRow();
                int column = e.getColumn();
                Object data = ((TableModel) e.getSource()).getValueAt(row, column);
                String label = (String) data;
                switch (row)
                // name
                {
                case 0:
                    if (!attribute.getName().equals(label))
                        attribute.setName(label);
                    break;
                case 1:
                    if (attribute.getValue() == null ||
                        (attribute.getValue() != null && !attribute.getValue().equals(label)))
                        attribute.setValue(label);
                    break;
                }
            }
        });
        return properties;
    }

    public DefaultMutableTreeNode getTreeBranch()
    {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(this);
        DomainGui domainGui = new DomainGui(attribute.getDomain());
        root.add(domainGui.getTreeBranch());
        return root;
    }

    public NodeHyperTree getHyperTreeNode()
    {
        NodeHyperTree root = new NodeHyperTree(this, NodeHyperTree.PROPERTY);
        DomainGui domainGui = new DomainGui(attribute.getDomain());
        root.add(domainGui.getHyperTreeNode());
        return root;
    }

    protected static Attribute a;

    /**
     * Create an attribute dialog
     * 
     * @return an {@link Attribute}
     */
    public static Attribute createAttributeDialog()
    {
        final TextField txtAttributeName = new TextField(15);
        final TextField txtAttributeValue = new TextField(15);
        final ComboBox cmbAttributeDomain = new ComboBox(Domain.getPredefinedDomains());
        cmbAttributeDomain.setEditable(true);
        cmbAttributeDomain.setSelectedIndex(-1);
        cmbAttributeDomain.setRenderer(new javax.swing.plaf.basic.BasicComboBoxRenderer()
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
            PropertiesHandler.getResourceString("ontology.attribute.dialog.windowTitle"), true);
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        dialog.setSize(new Dimension(PropertiesHandler
            .getIntProperty("ontology.attribute.dialog.width"), PropertiesHandler
            .getIntProperty("ontology.attribute.dialog.height")));
        dialog.setLocationRelativeTo(null);
        dialog.setResizable(false);

        JPanel south = new JPanel();
        south.setLayout(new FlowLayout());
        final JButton okButton;
        south.add(okButton = new JButton(PropertiesHandler
            .getResourceString("ontology.attribute.dialog.button.ok")));
        dialog.getRootPane().setDefaultButton(okButton);
        okButton.setEnabled(txtAttributeName.getText().trim().length() > 0);
        okButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                a = new Attribute(txtAttributeName.getText(), txtAttributeValue.getText());
                String domain = cmbAttributeDomain.getText();
                if (domain != null && domain.trim().length() > 0)
                    a.getDomain().setName(domain);
                dialog.dispose();
            }
        });
        JButton cancelButton;
        south.add(cancelButton = new JButton(PropertiesHandler
            .getResourceString("ontology.attribute.dialog.button.cancel")));
        cancelButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                a = null;
                dialog.dispose();
            }
        });
        panel.add(BorderLayout.SOUTH, south);

        JPanel center = new JPanel(new GridBagLayout());
        panel.add(BorderLayout.CENTER, center);
        center.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        {// Title
            JLabel title = new JLabel(PropertiesHandler.getResourceString("ontology.attribute"),
                ApplicationUtilities.getImage("attribute.gif"), SwingConstants.LEFT);
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
                    .getResourceString("ontology.attribute.dialog.explanation")), gbcl);
        }

        {// Name
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = 2;
            gbcl.insets = new Insets(0, 0, 5, 5);
            gbcl.anchor = GridBagConstraints.EAST;
            JLabel name = new JLabel(
                PropertiesHandler.getResourceString("ontology.attribute.name") + ":");
            name.setFont(new Font(dialog.getFont().getName(), Font.BOLD, dialog.getFont().getSize()));
            center.add(name, gbcl);

            gbcl.gridx = 1;
            gbcl.anchor = GridBagConstraints.WEST;
            center.add(txtAttributeName, gbcl);
            txtAttributeName.addKeyListener(new KeyAdapter()
            {
                public void keyTyped(KeyEvent event)
                {
                    SwingUtilities.invokeLater(new Runnable()
                    {
                        public void run()
                        {
                            if (!txtAttributeName.getText().trim().equals(""))
                                okButton.setEnabled(true);
                            else
                                okButton.setEnabled(false);
                        }
                    });
                }
            });
        }

        {// Value
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = 3;
            gbcl.insets = new Insets(0, 0, 5, 5);
            gbcl.anchor = GridBagConstraints.EAST;
            JLabel value = new JLabel(
                PropertiesHandler.getResourceString("ontology.attribute.value") + ":");
            center.add(value, gbcl);

            gbcl.gridx = 1;
            gbcl.anchor = GridBagConstraints.WEST;
            center.add(txtAttributeValue, gbcl);
        }

        {// Domain
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = 4;
            gbcl.insets = new Insets(0, 0, 0, 5);
            gbcl.anchor = GridBagConstraints.EAST;
            JLabel domain = new JLabel(PropertiesHandler.getResourceString("ontology.domain") + ":");
            center.add(domain, gbcl);

            gbcl.gridx = 1;
            gbcl.anchor = GridBagConstraints.WEST;
            center.add(cmbAttributeDomain, gbcl);
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
                        txtAttributeName.requestFocus();
                    }
                });
            }

            public void windowClosing(WindowEvent e)
            {
                a = null;
                dialog.dispose();
            }
        });
        dialog.setContentPane(panel);

        dialog.setVisible(true);// show();
        return a;
    }
    
    @Override
    public String toString()
    {
    	return attribute == null ? "<NULL>" : attribute.toString();
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
		AttributeGui other = (AttributeGui) obj;
		if (attribute == null)
		{
			if (other.attribute != null)
				return false;
		}
		else if (!attribute.equals(other.attribute))
			return false;
		return true;
	}

}
