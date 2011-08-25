package ac.technion.iem.ontobuilder.gui.ontobuilder.elements;

import hypertree.HyperTree;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.ontology.OntologyGui;

/**
 * <p>Title: OntologyHyperTree</p>
 * Extends a {@link JDialog}
 * Implements an {@link ActionListener}
 */
public class OntologyHyperTree extends JDialog implements ActionListener
{
    private static final long serialVersionUID = 1L;

    protected HyperTree tree;
    protected OntologyGui ontologyGui;
    protected JScrollPane hypPane;
    protected JCheckBox showClasses;
    protected JCheckBox showRelations;
    protected JCheckBox showProperties;
    protected JButton closeButton;
    protected JPanel mainPanel;

    /**
     * Constructs a OntologyHyperTree
     * 
     * @param ontology the {@link Ontology}
     * @param parent the parent {@link JFrame}
     */
    public OntologyHyperTree(OntologyGui ontology, JFrame parent)
    {
        super(parent, false);
        this.ontologyGui = ontology;
        this.tree = ontology.getHyperTree(false, false, false);

        setSize(new Dimension(ApplicationUtilities.getIntProperty("ontology.hypertree.width"),
            ApplicationUtilities.getIntProperty("ontology.hypertree.height")));
        setLocationRelativeTo(parent);

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setContentPane(mainPanel);

        // Buttons
        JPanel buttonsPanel = new JPanel(new FlowLayout());
        mainPanel.add(BorderLayout.SOUTH, buttonsPanel);

        buttonsPanel.add(showClasses = new JCheckBox("Show Classes", false));
        showClasses.setEnabled(!ontology.getOntology().isLight());

        buttonsPanel.add(showRelations = new JCheckBox("Show Relationships", false));
        buttonsPanel.add(showProperties = new JCheckBox("Show Properties", false));
        buttonsPanel.add(closeButton = new JButton(ApplicationUtilities
            .getResourceString("ontology.hypertree.button.close")));
        getRootPane().setDefaultButton(closeButton);

        closeButton.setActionCommand("close");
        closeButton.addActionListener(this);

        hypPane = new JScrollPane(tree.getView());

        mainPanel.add(BorderLayout.CENTER, hypPane);

        showClasses.setActionCommand("show classes");
        showClasses.addActionListener(this);

        showRelations.setActionCommand("show relations");
        showRelations.addActionListener(this);

        showProperties.setActionCommand("show properties");
        showProperties.addActionListener(this);

        // Title
        JLabel title = new JLabel(
            ApplicationUtilities.getResourceString("ontology.hypertree.title"),
            ApplicationUtilities.getImage("hyperbolicontology.gif"), SwingConstants.LEFT);
        title.setFont(new Font(getFont().getName(), Font.BOLD, getFont().getSize() + 4));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        mainPanel.add(BorderLayout.NORTH, title);

        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                dispose();
            }
        });
    }

    /**
     * Executes a performed action
     * 
     * @param e an {@link ActionEvent}
     */
    public void actionPerformed(ActionEvent e)
    {
        if (e.getActionCommand().equals("close"))
        {
            dispose();
        }
        else if (e.getActionCommand().equals("show classes") ||
            e.getActionCommand().equals("show relations") ||
            e.getActionCommand().equals("show properties"))
        {
            tree = ontologyGui.getHyperTree(showClasses.isSelected(), showRelations.isSelected(),
                showProperties.isSelected());
            hypPane = (JScrollPane) mainPanel.getComponent(1);
            hypPane.setViewportView(tree.getView());
        }
    }
}