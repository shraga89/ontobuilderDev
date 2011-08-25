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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;

import ac.technion.iem.ontobuilder.core.ontology.Domain;
import ac.technion.iem.ontobuilder.core.ontology.DomainEntry;
import ac.technion.iem.ontobuilder.core.ontology.Ontology;
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
 * Title: DomainEntryGui
 * </p>
  * <p>Description: Implements the methods of the DomainEntry used by the GUI</p>
  * Extends {@link OntologyObjectGui}
 */
public class DomainEntryGui extends OntologyObjectGui
{
    private DomainEntry domainEntry;
    protected static DomainEntry e;
    
    public DomainEntryGui(DomainEntry domainEntry)
    {
        super(domainEntry);
        this.domainEntry = domainEntry;
    }
    
    public JTable getProperties()
    {
        if (domainEntry.getEntry() instanceof Term)
        {
            TermGui termGui = new TermGui((Term)domainEntry.getEntry());
            return termGui.getProperties();
        }
        String columnNames[] =
        {
            PropertiesHandler.getResourceString("properties.attribute"),
            PropertiesHandler.getResourceString("properties.value")
        };
        Object data[][] =
        {
            {
                PropertiesHandler.getResourceString("ontology.domain.entry"), domainEntry.getEntry()
            }
        };
        JTable properties = new JTable(new PropertiesTableModel(columnNames, 1, data));
        return properties;
    }
    
    public DefaultMutableTreeNode getTreeBranch()
    {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(this);
        if (domainEntry.getEntry() instanceof Term)
        {
            TermGui termGui = new TermGui((Term) domainEntry.getEntry());
            DefaultMutableTreeNode termNode = (termGui.getTreeBranch());
            root.add((DefaultMutableTreeNode) termNode.getChildAt(1)); // The attributes node is the
                                                                       // second child;
        }
        return root;
    }
    
    public NodeHyperTree getHyperTreeNode()
    {
        NodeHyperTree root = new NodeHyperTree(this, NodeHyperTree.CLASS);
        if (domainEntry.getEntry() instanceof Term)
        {
            TermGui termGui = new TermGui((Term) domainEntry.getEntry());
            NodeHyperTree termNode = termGui.getHyperTreeNode();
            root.add(termNode.getChild(ApplicationUtilities
                .getResourceString("ontology.attributes")));
        }
        return root;
    }
    
    public static DomainEntry createEntryDialog(final Ontology model)
    {
        final TextField txtEntry = new TextField(15);
        txtEntry.setEnabled(false);
        final TextField txtTermName = new TextField(15);
        final TextField txtTermValue = new TextField(15);
        final ComboBox cmbTermDomain = new ComboBox(
            Domain.getPredefinedDomains());
        cmbTermDomain.setEnabled(false);
        cmbTermDomain.setEditable(true);
        cmbTermDomain.setSelectedIndex(-1);
        cmbTermDomain.setRenderer(new javax.swing.plaf.basic.BasicComboBoxRenderer()
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

        Vector<Object> classes = (Vector<Object>) model.getClasses(true);
        classes.add(0, "               ");
        final ComboBox cmbTermClass = new ComboBox(classes);
        cmbTermClass.setEnabled(false);
        cmbTermClass.setSelectedIndex(0);
        cmbTermClass.setRenderer(new javax.swing.plaf.basic.BasicComboBoxRenderer()
        {
            private static final long serialVersionUID = 1L;

            public Component getListCellRendererComponent(JList list, Object value, int index,
                boolean isSelected, boolean cellHasFocus)
            {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof OntologyClass)
                    setIcon(ApplicationUtilities.getImage("class.gif"));
                return this;
            }
        });

        final JDialog dialog = new JDialog((JFrame) null,
            PropertiesHandler.getResourceString("ontology.domainEntry.dialog.windowTitle"), true);
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        dialog.setSize(new Dimension(PropertiesHandler
            .getIntProperty("ontology.domainEntry.dialog.width"), PropertiesHandler
            .getIntProperty("ontology.domainEntry.dialog.height")));
        dialog.setLocationRelativeTo(null);
        dialog.setResizable(false);

        final JRadioButton entryRadio = new JRadioButton(
            PropertiesHandler.getResourceString("ontology.domainEntry.dialog.radio.entry"));
        final JRadioButton termRadio = new JRadioButton(
            PropertiesHandler.getResourceString("ontology.domainEntry.dialog.radio.term"));

        JPanel south = new JPanel();
        south.setLayout(new FlowLayout());
        final JButton okButton;
        south.add(okButton = new JButton(PropertiesHandler
            .getResourceString("ontology.domainEntry.dialog.button.ok")));
        dialog.getRootPane().setDefaultButton(okButton);
        okButton.setEnabled(txtTermName.getText().trim().length() > 0);
        okButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ae)
            {
                if (termRadio.isSelected())
                {
                    Term t;
                    if (cmbTermClass.getSelectedIndex() > 0)
                        t = new Term((OntologyClass) cmbTermClass.getSelectedItem());
                    else
                        t = new Term();
                    t.setName(txtTermName.getText());
                    t.setValue(txtTermValue.getText());
                    t.setOntology(model);
                    String domain = cmbTermDomain.getText();
                    if (domain != null && domain.trim().length() > 0)
                        t.getDomain().setName(domain);
                    e = new DomainEntry(t);
                }
                else
                {
                    e = new DomainEntry(txtEntry.getText());
                }
                dialog.dispose();
            }
        });
        JButton cancelButton;
        south.add(cancelButton = new JButton(PropertiesHandler
            .getResourceString("ontology.domainEntry.dialog.button.cancel")));
        cancelButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                e = null;
                dialog.dispose();
            }
        });
        panel.add(BorderLayout.SOUTH, south);

        ButtonGroup group = new ButtonGroup();
        termRadio.setSelected(true);
        group.add(entryRadio);
        entryRadio.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent e)
            {
                boolean selected = e.getStateChange() == ItemEvent.SELECTED;
                txtEntry.setEnabled(selected);
                txtTermName.setEnabled(!selected);
                txtTermValue.setEnabled(!selected);
                cmbTermDomain.setEnabled(!selected);
                cmbTermClass.setEnabled(!selected);
                okButton.setEnabled(selected && txtEntry.getText().trim().length() > 0);
                if (selected)
                    txtEntry.requestFocus();
            }
        });
        group.add(termRadio);
        termRadio.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent e)
            {
                boolean selected = e.getStateChange() == ItemEvent.SELECTED;
                txtEntry.setEnabled(!selected);
                txtTermName.setEnabled(selected);
                txtTermValue.setEnabled(selected);
                cmbTermDomain.setEnabled(selected);
                cmbTermClass.setEnabled(selected);
                okButton.setEnabled(selected && txtTermName.getText().trim().length() > 0);
                if (selected)
                    txtTermName.requestFocus();
            }
        });

        JPanel center = new JPanel(new GridBagLayout());
        panel.add(BorderLayout.CENTER, center);
        center.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        {// Title
            JLabel title = new JLabel(
                PropertiesHandler.getResourceString("ontology.domain.entry"),
                ApplicationUtilities.getImage("domainentry.gif"), SwingConstants.LEFT);
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
                    .getResourceString("ontology.domainEntry.dialog.explanation")), gbcl);
        }

        {// Radio
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = 2;
            gbcl.gridwidth = 2;
            gbcl.weightx = 1;
            gbcl.fill = GridBagConstraints.HORIZONTAL;
            gbcl.insets = new Insets(0, 0, 5, 0);
            gbcl.anchor = GridBagConstraints.WEST;
            center.add(entryRadio, gbcl);
        }

        {// Entry
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = 3;
            gbcl.insets = new Insets(0, 0, 5, 5);
            gbcl.anchor = GridBagConstraints.EAST;
            JLabel entry = new JLabel(
                PropertiesHandler.getResourceString("ontology.domain.entry") + ":");
            entry.setFont(new Font(dialog.getFont().getName(), Font.BOLD, dialog.getFont()
                .getSize()));
            center.add(entry, gbcl);

            gbcl.gridx = 1;
            gbcl.anchor = GridBagConstraints.WEST;
            center.add(txtEntry, gbcl);
            txtEntry.addKeyListener(new KeyAdapter()
            {
                public void keyTyped(KeyEvent event)
                {
                    SwingUtilities.invokeLater(new Runnable()
                    {
                        public void run()
                        {
                            if (!txtEntry.getText().trim().equals(""))
                                okButton.setEnabled(true);
                            else
                                okButton.setEnabled(false);
                        }
                    });
                }
            });
        }

        {// Radio
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = 4;
            gbcl.gridwidth = 2;
            gbcl.weightx = 1;
            gbcl.fill = GridBagConstraints.HORIZONTAL;
            gbcl.insets = new Insets(0, 0, 5, 0);
            gbcl.anchor = GridBagConstraints.WEST;
            center.add(termRadio, gbcl);
        }

        {// Name
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = 5;
            gbcl.insets = new Insets(0, 0, 5, 5);
            gbcl.anchor = GridBagConstraints.EAST;
            JLabel name = new JLabel(PropertiesHandler.getResourceString("ontology.term.name") +
                ":");
            name.setFont(new Font(dialog.getFont().getName(), Font.BOLD, dialog.getFont().getSize()));
            center.add(name, gbcl);

            gbcl.gridx = 1;
            gbcl.anchor = GridBagConstraints.WEST;
            center.add(txtTermName, gbcl);
            txtTermName.addKeyListener(new KeyAdapter()
            {
                public void keyTyped(KeyEvent event)
                {
                    SwingUtilities.invokeLater(new Runnable()
                    {
                        public void run()
                        {
                            if (!txtTermName.getText().trim().equals(""))
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
            gbcl.gridy = 6;
            gbcl.insets = new Insets(0, 0, 5, 5);
            gbcl.anchor = GridBagConstraints.EAST;
            JLabel value = new JLabel(
                PropertiesHandler.getResourceString("ontology.term.value") + ":");
            center.add(value, gbcl);

            gbcl.gridx = 1;
            gbcl.anchor = GridBagConstraints.WEST;
            center.add(txtTermValue, gbcl);
        }

        /*
         * {// Domain GridBagConstraints gbcl=new GridBagConstraints(); gbcl.gridy=7;
         * gbcl.insets=new Insets(0,0,5,5); gbcl.anchor=GridBagConstraints.EAST; JLabel domain=new
         * JLabel(PropertiesHandler.getResourceString("ontology.domain") + ":");
         * center.add(domain,gbcl); gbcl.gridx=1; gbcl.anchor=GridBagConstraints.WEST;
         * center.add(cmbTermDomain,gbcl); } {// Class GridBagConstraints gbcl=new
         * GridBagConstraints(); gbcl.gridy=8; gbcl.insets=new Insets(0,0,0,5);
         * gbcl.anchor=GridBagConstraints.EAST; JLabel clazz=new
         * JLabel(PropertiesHandler.getResourceString("ontology.class") + ":");
         * center.add(clazz,gbcl); gbcl.gridx=1; gbcl.anchor=GridBagConstraints.WEST;
         * center.add(cmbTermClass,gbcl); }
         */

        {// Separator
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridy = 7;
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
                        txtEntry.requestFocus();
                    }
                });
            }

            public void windowClosing(WindowEvent e)
            {
                e = null;
                dialog.dispose();
            }
        });
        dialog.setContentPane(panel);

        dialog.setVisible(true);// show();
        return e;
    }
    
    @Override
    public String toString()
    {
    	return domainEntry == null ? "<NULL>" : domainEntry.toString();
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
		DomainEntryGui other = (DomainEntryGui) obj;
		if (domainEntry == null)
		{
			if (other.domainEntry != null)
				return false;
		}
		else if (!domainEntry.equals(other.domainEntry))
			return false;
		return true;
	}
}
