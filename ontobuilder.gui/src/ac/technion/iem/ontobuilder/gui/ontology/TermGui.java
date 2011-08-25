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
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;

import com.jgraph.graph.ConnectionSet;
import com.jgraph.graph.DefaultEdge;
import com.jgraph.graph.DefaultGraphCell;
import com.jgraph.graph.DefaultPort;
import com.jgraph.graph.GraphConstants;

import ac.technion.iem.ontobuilder.core.ontology.Attribute;
import ac.technion.iem.ontobuilder.core.ontology.Axiom;
import ac.technion.iem.ontobuilder.core.ontology.Domain;
import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.OntologyClass;
import ac.technion.iem.ontobuilder.core.ontology.Relationship;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.core.utils.properties.PropertiesHandler;
import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.application.PropertiesTableModel;
import ac.technion.iem.ontobuilder.gui.elements.ComboBox;
import ac.technion.iem.ontobuilder.gui.elements.MultilineLabel;
import ac.technion.iem.ontobuilder.gui.elements.TextField;
import ac.technion.iem.ontobuilder.gui.utils.graphs.GraphUtilities;
import ac.technion.iem.ontobuilder.gui.utils.graphs.OrderedDefaultPort;
import ac.technion.iem.ontobuilder.gui.utils.hypertree.NodeHyperTree;

/**
 * <p>
 * Title: TermGui
 * </p>
  * <p>Description: Implements the methods of the Term used by the GUI</p>
  * Extends {@link OntologyObjectGui}
 */
public class TermGui extends OntologyObjectGui
{
    private Term term;
    protected static Term t;
    
    public TermGui(Term term)
    {
        super(term);
        this.term = term;
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
                PropertiesHandler.getResourceString("ontology.term.name"), term.getName()
            },
            {
                PropertiesHandler.getResourceString("ontology.term.value"), term.getValue()
            },
            {
                PropertiesHandler.getResourceString("ontology.domain"), term.getDomain().getName()
            },
            {
                PropertiesHandler.getResourceString("ontology.class"),
                term.getSuperClass() != null ? term.getSuperClass().getName() : null
            },
            {
                PropertiesHandler.getResourceString("ontology"),
                term.getOntology() != null ? term.getOntology().getName() : null
            }
        };
        JTable properties = new JTable(new PropertiesTableModel(columnNames, 5, data));
        return properties;
    }
    
    public DefaultMutableTreeNode getTreeBranch()
    {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(this);

        DomainGui domainGui = new DomainGui(term.getDomain());
        root.add(domainGui.getTreeBranch());

        DefaultMutableTreeNode attributesNode = new DefaultMutableTreeNode(
            PropertiesHandler.getResourceString("ontology.attributes"));
        root.add(attributesNode);
        for (Iterator<?> i = term.getAttributes().iterator(); i.hasNext();)
        {
            AttributeGui attributeGui = new AttributeGui((Attribute) i.next());
            attributesNode.add(attributeGui.getTreeBranch());
        }

        DefaultMutableTreeNode axiomsNode = new DefaultMutableTreeNode(
            PropertiesHandler.getResourceString("ontology.axioms"));
        root.add(axiomsNode);
        for (Iterator<?> i = term.getAxioms().iterator(); i.hasNext();)
        {
            AxiomGui axiomGui = new AxiomGui((Axiom) i.next());
            axiomsNode.add(axiomGui.getTreeBranch());
        }
        DefaultMutableTreeNode relationshipsNode = new DefaultMutableTreeNode(
            PropertiesHandler.getResourceString("ontology.relationships"));
        root.add(relationshipsNode);
        for (Iterator<Relationship> i = term.getRelationships().iterator(); i.hasNext();)
        {
            RelationshipGui relationshipGui = new RelationshipGui((Relationship) i.next());
            relationshipsNode.add(relationshipGui.getTreeBranch());
        }

        DefaultMutableTreeNode termsNode = new DefaultMutableTreeNode(
            PropertiesHandler.getResourceString("ontology.subterms"));
        root.add(termsNode);
        for (Iterator<Term> i = term.getTerms().iterator(); i.hasNext();)
        {
            TermGui termGui = new TermGui((Term) i.next());
            termsNode.add(termGui.getTreeBranch());
        }

        return root;
    }

    public NodeHyperTree getHyperTreeNode(boolean showRelations, boolean showClasses,
        boolean showProperties)
    {
        NodeHyperTree root = new NodeHyperTree(this, NodeHyperTree.TERM);

        if (showProperties)
        {
            DomainGui domainGui = new DomainGui(term.getDomain());
            root.add(domainGui.getHyperTreeNode());
            NodeHyperTree attributesNode = new NodeHyperTree(
                PropertiesHandler.getResourceString("ontology.attributes"),
                NodeHyperTree.PROPERTY);
            root.add(attributesNode);
            for (Iterator<?> i = term.getAttributes().iterator(); i.hasNext();)
                attributesNode.add(OntologyObjectGuiFactory.getOntologyObjectGui((Attribute) i.next()).getHyperTreeNode());

            NodeHyperTree axiomsNode = new NodeHyperTree(
                PropertiesHandler.getResourceString("ontology.axioms"), NodeHyperTree.PROPERTY);
            root.add(axiomsNode);
            for (Iterator<?> i = term.getAxioms().iterator(); i.hasNext();)
                axiomsNode.add(OntologyObjectGuiFactory.getOntologyObjectGui((Axiom) i.next()).getHyperTreeNode());
        }

        if (showRelations)
        {
            NodeHyperTree relationshipsNode = new NodeHyperTree(
                PropertiesHandler.getResourceString("ontology.relationships"),
                NodeHyperTree.RELATIONSHIP);
            root.add(relationshipsNode);
            for (Iterator<Relationship> i = term.getRelationships().iterator(); i.hasNext();)
                relationshipsNode.add(OntologyObjectGuiFactory.getOntologyObjectGui((Relationship) i.next()).getHyperTreeNode());
        }

        // NodeHyperTree termsNode=new
        // NodeHyperTree(PropertiesHandler.getResourceString("ontology.subterms"));
        // root.add(termsNode);
        for (Iterator<Term> i = term.getTerms().iterator(); i.hasNext();)
        {
            TermGui termGui = new TermGui((Term) i.next());
            root.add(termGui.getHyperTreeNode(showRelations, showClasses, showProperties));
        }

        return root;
    }
    
    public static Term createTermDialog(Ontology model)
    {
        final TextField txtTermName = new TextField(15);
        final TextField txtTermValue = new TextField(15);
        final ComboBox cmbTermDomain = new ComboBox(
            Domain.getPredefinedDomains());
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

        Vector<Object> classes = model.getClasses(true);
        classes.add(0, "               ");
        final ComboBox cmbTermClass = new ComboBox(classes);
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
            PropertiesHandler.getResourceString("ontology.term.dialog.windowTitle"), true);
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        dialog.setSize(new Dimension(PropertiesHandler
            .getIntProperty("ontology.term.dialog.width"), PropertiesHandler
            .getIntProperty("ontology.term.dialog.height")));
        dialog.setLocationRelativeTo(null);
        dialog.setResizable(false);

        JPanel south = new JPanel();
        south.setLayout(new FlowLayout());
        final JButton okButton;
        south.add(okButton = new JButton(PropertiesHandler
            .getResourceString("ontology.term.dialog.button.ok")));
        dialog.getRootPane().setDefaultButton(okButton);
        okButton.setEnabled(txtTermName.getText().trim().length() > 0);
        okButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                if (cmbTermClass.getSelectedIndex() > 0)
                    t = new Term((OntologyClass) cmbTermClass.getSelectedItem());
                else
                    t = new Term();
                t.setName(txtTermName.getText());
                t.setValue(txtTermValue.getText());
                String domain = cmbTermDomain.getText();
                if (domain != null && domain.trim().length() > 0)
                    t.getDomain().setName((domain));
                dialog.dispose();
            }
        });
        JButton cancelButton;
        south.add(cancelButton = new JButton(PropertiesHandler
            .getResourceString("ontology.term.dialog.button.cancel")));
        cancelButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                t = null;
                dialog.dispose();
            }
        });
        panel.add(BorderLayout.SOUTH, south);

        JPanel center = new JPanel(new GridBagLayout());
        panel.add(BorderLayout.CENTER, center);
        center.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        {// Title
            JLabel title = new JLabel(PropertiesHandler.getResourceString("ontology.term"),
                ApplicationUtilities.getImage("term.gif"), JLabel.LEFT);
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
                    .getResourceString("ontology.term.dialog.explanation")), gbcl);
        }

        {// Name
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = 2;
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
            gbcl.gridy = 3;
            gbcl.insets = new Insets(0, 0, 5, 5);
            gbcl.anchor = GridBagConstraints.EAST;
            JLabel value = new JLabel(
                PropertiesHandler.getResourceString("ontology.term.value") + ":");
            center.add(value, gbcl);

            gbcl.gridx = 1;
            gbcl.anchor = GridBagConstraints.WEST;
            center.add(txtTermValue, gbcl);
        }

        {// Domain
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = 4;
            gbcl.insets = new Insets(0, 0, 5, 5);
            gbcl.anchor = GridBagConstraints.EAST;
            JLabel domain = new JLabel(PropertiesHandler.getResourceString("ontology.domain") +
                ":");
            center.add(domain, gbcl);

            gbcl.gridx = 1;
            gbcl.anchor = GridBagConstraints.WEST;
            center.add(cmbTermDomain, gbcl);
        }

        {// Class
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = 5;
            gbcl.insets = new Insets(0, 0, 0, 5);
            gbcl.anchor = GridBagConstraints.EAST;
            JLabel clazz = new JLabel(PropertiesHandler.getResourceString("ontology.class") +
                ":");
            center.add(clazz, gbcl);

            gbcl.gridx = 1;
            gbcl.anchor = GridBagConstraints.WEST;
            center.add(cmbTermClass, gbcl);
        }

        {// Separator
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridy = 6;
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
                        txtTermName.requestFocus();
                    }
                });
            }

            public void windowClosing(WindowEvent e)
            {
                t = null;
                dialog.dispose();
            }
        });
        dialog.setContentPane(panel);

        dialog.setVisible(true);// show();
        return t;
    }

    public DefaultGraphCell buildGraphHierarchy(DefaultPort parentPort,
        ArrayList<DefaultGraphCell> cells, Hashtable<DefaultGraphCell, Map<?, ?>> attributes,
        ConnectionSet cs)
    {
        if (term.isInstanceOf("hidden") && !GraphUtilities.isShowHiddenElements())
            return null;
        DefaultGraphCell termVertex = new DefaultGraphCell(this);
        cells.add(termVertex);
        Map<?, ?> termMap = GraphUtilities.createDefaultAttributes();
        GraphConstants.setIcon(termMap, ApplicationUtilities.getImage("term.gif"));
        attributes.put(termVertex, termMap);

        if (parentPort != null) // Connect parent with this child
        {
            DefaultPort toParentPort = new DefaultPort("toParent");
            termVertex.add(toParentPort);
            DefaultEdge edge = new DefaultEdge();
            cs.connect(edge, parentPort, true);
            cs.connect(edge, toParentPort, false);
            cells.add(edge);
        }

        if (!term.getTerms().isEmpty()) // It has children
        {
            DefaultPort toChildPort = new OrderedDefaultPort("toChild");
            termVertex.add(toChildPort);
            for (Iterator<Term> i = term.getTerms().iterator(); i.hasNext();)
            {
                Term term = (Term) i.next();
                TermGui termGui = new TermGui(term);
                termGui.buildGraphHierarchy(toChildPort, cells,
                    attributes, cs);
            }
        }

        return termVertex;
    }

    public void buildPrecedenceRelationships(ArrayList<DefaultGraphCell> cells,
        Hashtable<?, ?> attributes, ConnectionSet cs)
    {
        DefaultGraphCell vertex = GraphUtilities.getCellWithObject(cells, this);
        if (vertex == null)
            return;
        if (term.getPrecede() != null)
        {
            DefaultGraphCell prevVertex = null;
            Term auxPrecede = this.term;
            do
            {
                auxPrecede = auxPrecede.getPrecede();
                prevVertex = GraphUtilities.getCellWithObject(cells, auxPrecede);
            }
            while (prevVertex == null && auxPrecede != null);
            if (prevVertex != null)
            {
                DefaultPort prevPort = new DefaultPort("precedes");
                prevVertex.add(prevPort);
                DefaultPort nextPort = new DefaultPort("isPreceded");
                vertex.add(nextPort);
                DefaultEdge edge = new DefaultEdge();
                cs.connect(edge, prevPort, true);
                cs.connect(edge, nextPort, false);
                cells.add(edge);
            }
        }
        if (term.getSucceed() != null)
        {
            DefaultGraphCell nextVertex = null;
            Term auxSucceed = term;
            do
            {
                auxSucceed = auxSucceed.getSucceed();
                nextVertex = GraphUtilities.getCellWithObject(cells, auxSucceed);
            }
            while (nextVertex == null && auxSucceed != null);
            if (nextVertex != null)
            {
                DefaultPort prevPort = new DefaultPort("isSucceeded");
                vertex.add(prevPort);
                DefaultPort nextPort = new DefaultPort("succeeds");
                vertex.add(nextPort);
                DefaultEdge edge = new DefaultEdge();
                cs.connect(edge, prevPort, false);
                cs.connect(edge, nextPort, true);
                cells.add(edge);
            }
        }

        for (Iterator<Term> i = term.getTerms().iterator(); i.hasNext();)
        {
            Term term = (Term) i.next();
            TermGui termGui = new TermGui(term);
            termGui.buildPrecedenceRelationships(cells, attributes, cs);
        }
    }
    
    @Override
    public String toString()
    {
    	return term == null ? "<NULL>" : term.toString(); 
    }
    
    public Term getTerm()
    {
        return term;
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
		TermGui other = (TermGui) obj;
		if (term == null)
		{
			if (other.term != null)
				return false;
		}
		else if (!term.equals(other.term))
			return false;
		return true;
	}
}
