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
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import ac.technion.iem.ontobuilder.core.ontology.Axiom;
import ac.technion.iem.ontobuilder.core.utils.properties.PropertiesHandler;
import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.application.PropertiesTableModel;
import ac.technion.iem.ontobuilder.gui.elements.MultilineLabel;
import ac.technion.iem.ontobuilder.gui.elements.TextArea;
import ac.technion.iem.ontobuilder.gui.elements.TextField;

/**
 * <p>
 * Title: AxiomGui
 * </p>
  * <p>Description: Implements the methods of the Axiom used by the GUI</p>
  * Extends {@link OntologyObjectGui}
 */
public class AxiomGui extends OntologyObjectGui
{
    private Axiom _axiom;
    protected static Axiom a;
    
    public AxiomGui(Axiom axiom)
    {
        super(axiom);
        this._axiom = axiom;
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
                PropertiesHandler.getResourceString("ontology.axiom.name"), _axiom.getName()
            },
            {
                PropertiesHandler.getResourceString("ontology.axiom.axiom"), _axiom.getAxiom()
            }
        };
        JTable properties = new JTable(new PropertiesTableModel(columnNames, 2, data));
        return properties;
    }
    
    public static Axiom createAxiomDialog()
    {
        final TextField txtAxiomName = new TextField(15);
        final TextArea txtAxiom = new TextArea(1, 4);

        final JDialog dialog = new JDialog((JFrame) null,
            PropertiesHandler.getResourceString("ontology.axiom.dialog.windowTitle"), true);
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        dialog.setSize(new Dimension(PropertiesHandler
            .getIntProperty("ontology.axiom.dialog.width"), PropertiesHandler
            .getIntProperty("ontology.axiom.dialog.height")));
        dialog.setLocationRelativeTo(null);
        dialog.setResizable(false);

        JPanel south = new JPanel();
        south.setLayout(new FlowLayout());
        final JButton okButton;
        south.add(okButton = new JButton(PropertiesHandler
            .getResourceString("ontology.axiom.dialog.button.ok")));
        dialog.getRootPane().setDefaultButton(okButton);
        okButton.setEnabled(false);
        okButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                a = new Axiom(txtAxiomName.getText(), txtAxiom.getText());
                dialog.dispose();
            }
        });
        JButton cancelButton;
        south.add(cancelButton = new JButton(PropertiesHandler
            .getResourceString("ontology.axiom.dialog.button.cancel")));
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
            JLabel title = new JLabel(PropertiesHandler.getResourceString("ontology.axiom"),
                ApplicationUtilities.getImage("axiom.gif"), SwingConstants.LEFT);
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
                    .getResourceString("ontology.axiom.dialog.explanation")), gbcl);
        }

        {// Name
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = 2;
            gbcl.insets = new Insets(0, 0, 5, 5);
            gbcl.anchor = GridBagConstraints.EAST;
            JLabel name = new JLabel(PropertiesHandler.getResourceString("ontology.axiom.name") +
                ":");
            name.setFont(new Font(dialog.getFont().getName(), Font.BOLD, dialog.getFont().getSize()));
            center.add(name, gbcl);

            gbcl.gridx = 1;
            gbcl.anchor = GridBagConstraints.WEST;
            center.add(txtAxiomName, gbcl);
            txtAxiomName.addKeyListener(new KeyAdapter()
            {
                public void keyTyped(KeyEvent event)
                {
                    SwingUtilities.invokeLater(new Runnable()
                    {
                        public void run()
                        {
                            if (!txtAxiomName.getText().trim().equals("") &&
                                !txtAxiom.getText().trim().equals(""))
                                okButton.setEnabled(true);
                            else
                                okButton.setEnabled(false);
                        }
                    });
                }
            });
        }

        {// Axiom
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = 3;
            gbcl.insets = new Insets(0, 0, 5, 5);
            gbcl.anchor = GridBagConstraints.NORTHEAST;
            JLabel axiom = new JLabel(PropertiesHandler.getResourceString("ontology.axiom") +
                ":");
            axiom.setFont(new Font(dialog.getFont().getName(), Font.BOLD, dialog.getFont()
                .getSize()));
            center.add(axiom, gbcl);

            gbcl.gridx = 1;
            gbcl.weighty = 1;
            gbcl.weightx = 1;
            gbcl.anchor = GridBagConstraints.NORTH;
            gbcl.fill = GridBagConstraints.BOTH;
            center.add(new JScrollPane(txtAxiom), gbcl);
            txtAxiom.addKeyListener(new KeyAdapter()
            {
                public void keyTyped(KeyEvent event)
                {
                    SwingUtilities.invokeLater(new Runnable()
                    {
                        public void run()
                        {
                            if (!txtAxiomName.getText().trim().equals("") &&
                                !txtAxiom.getText().trim().equals(""))
                                okButton.setEnabled(true);
                            else
                                okButton.setEnabled(false);
                        }
                    });
                }
            });
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
                        txtAxiomName.requestFocus();
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
    	return _axiom == null ? "<NULL>" : _axiom.toString();
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
		AxiomGui other = (AxiomGui) obj;
		if (_axiom == null)
		{
			if (other._axiom != null)
				return false;
		}
		else if (!_axiom.equals(other._axiom))
			return false;
		return true;
	}
}
