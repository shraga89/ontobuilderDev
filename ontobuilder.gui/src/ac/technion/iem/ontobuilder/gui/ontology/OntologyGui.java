package ac.technion.iem.ontobuilder.gui.ontology;

import hypertree.HyperTree;

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
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.help.CSH;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.jdom.DocType;
import org.jdom.output.XMLOutputter;
import org.w3c.dom.Document;

import ac.technion.iem.ontobuilder.core.ontology.Attribute;
import ac.technion.iem.ontobuilder.core.ontology.Axiom;
import ac.technion.iem.ontobuilder.core.ontology.Domain;
import ac.technion.iem.ontobuilder.core.ontology.DomainEntry;
import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.OntologyClass;
import ac.technion.iem.ontobuilder.core.ontology.OntologyGenerateHelper;
import ac.technion.iem.ontobuilder.core.ontology.OntologyObject;
import ac.technion.iem.ontobuilder.core.ontology.Relationship;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.core.ontology.event.OntologyModelEvent;
import ac.technion.iem.ontobuilder.core.ontology.event.OntologyModelListener;
import ac.technion.iem.ontobuilder.core.utils.StringUtilities;
import ac.technion.iem.ontobuilder.core.utils.files.StringOutputStream;
import ac.technion.iem.ontobuilder.core.utils.network.NetworkUtilities;
import ac.technion.iem.ontobuilder.core.utils.properties.PropertiesHandler;
import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.INPUTElement;
import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.application.PropertiesCellEditor;
import ac.technion.iem.ontobuilder.gui.application.PropertiesTableModel;
import ac.technion.iem.ontobuilder.gui.application.action.Actions;
import ac.technion.iem.ontobuilder.gui.elements.MultilineLabel;
import ac.technion.iem.ontobuilder.gui.elements.PopupListener;
import ac.technion.iem.ontobuilder.gui.elements.PopupTrigger;
import ac.technion.iem.ontobuilder.gui.elements.TextField;
import ac.technion.iem.ontobuilder.gui.utils.files.common.FileUtilities;
import ac.technion.iem.ontobuilder.gui.utils.files.html.ButtonINPUTElementGui;
import ac.technion.iem.ontobuilder.gui.utils.files.html.CheckboxINPUTElementGui;
import ac.technion.iem.ontobuilder.gui.utils.files.html.CheckboxINPUTElementOptionGui;
import ac.technion.iem.ontobuilder.gui.utils.files.html.FORMElementGui;
import ac.technion.iem.ontobuilder.gui.utils.files.html.FileINPUTElementGui;
import ac.technion.iem.ontobuilder.gui.utils.files.html.HTMLUtilitiesGui;
import ac.technion.iem.ontobuilder.gui.utils.files.html.HiddenINPUTElementGui;
import ac.technion.iem.ontobuilder.gui.utils.files.html.INPUTElementGui;
import ac.technion.iem.ontobuilder.gui.utils.files.html.ImageINPUTElementGui;
import ac.technion.iem.ontobuilder.gui.utils.files.html.OPTIONElementGui;
import ac.technion.iem.ontobuilder.gui.utils.files.html.PasswordINPUTElementGui;
import ac.technion.iem.ontobuilder.gui.utils.files.html.RadioINPUTElementGui;
import ac.technion.iem.ontobuilder.gui.utils.files.html.RadioINPUTElementOptionGui;
import ac.technion.iem.ontobuilder.gui.utils.files.html.ResetINPUTElementGui;
import ac.technion.iem.ontobuilder.gui.utils.files.html.SELECTElementGui;
import ac.technion.iem.ontobuilder.gui.utils.files.html.SubmitINPUTElementGui;
import ac.technion.iem.ontobuilder.gui.utils.files.html.TEXTAREAElementGui;
import ac.technion.iem.ontobuilder.gui.utils.files.html.TextINPUTElementGui;
import ac.technion.iem.ontobuilder.gui.utils.graphs.GraphUtilities;
import ac.technion.iem.ontobuilder.gui.utils.graphs.OrderedDefaultPort;
import ac.technion.iem.ontobuilder.gui.utils.hypertree.NodeHyperTree;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.Algorithm;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;

import com.jgraph.JGraph;
import com.jgraph.graph.ConnectionSet;
import com.jgraph.graph.DefaultGraphCell;
import com.jgraph.graph.DefaultGraphModel;
import com.jgraph.graph.DefaultPort;
import com.jgraph.graph.GraphConstants;

/**
 * <p>Title: OntologyGui</p>
 * <p>Description: Implements the methods of the Ontology used by the GUI</p>
 * Extends {@link JPanel}
 */
public class OntologyGui extends JPanel
{
    private static final long serialVersionUID = -7583434763421818827L;

    protected JPopupMenu popMenu;
    protected Actions actions;
    protected ArrayList<OntologyModelListener> modelListeners;
    protected ArrayList<OntologySelectionListener> selectionListeners;

    protected Ontology ontologyCore;
    protected JTree ontologyTree;
    protected OntologyTreeCellEditor ontologyCellEditor;

    protected Object actionObject;

    /**
     * Constructs a default Ontology
     */
    public OntologyGui()
    {
        super(new BorderLayout());
        ontologyCore = new Ontology();
        init();
    }

    /**
     * Constructs an Ontology
     * 
     * @param model an {@link OntologyModel}
     */
    public OntologyGui(Ontology model)
    {
        super(new BorderLayout());
        this.ontologyCore = model;
        init();
    }

    /**
     * Constructs an Ontology
     * 
     * @param name the ontology name
     */
    public OntologyGui(String name)
    {
        this(name, "");
    }

    /**
     * Constructs an Ontology
     * 
     * @param name the ontology name
     * @param title the ontology title
     */
    public OntologyGui(String name, String title)
    {
        super(new BorderLayout());
        ontologyCore = new Ontology(name, title);
        init();
    }

    /**
     * Initialize the ontology
     */
    protected void init()
    {
        modelListeners = new ArrayList<OntologyModelListener>();
        selectionListeners = new ArrayList<OntologySelectionListener>();
        initializeActions();
        createPopupMenu();
        ontologyCore.setDirty(true);

        // Initialize the model
        ontologyCore.addOntologyModelListener(new OntologyModelListener()
        {
            public void modelChanged(OntologyModelEvent e)
            {
                fireModelChangedEvent(e);
            }

            public void objectChanged(OntologyModelEvent e)
            {
                updateTree(e.getObject());
                fireObjectChangedEvent(e);
                ontologyCore.setDirty(true);
            }

            public void classAdded(OntologyModelEvent e)
            {
                addClassToTree(e.getSuperClass(), e.getOntologyClass());
                fireClassAddedEvent(e);
                ontologyCore.setDirty(true);
            }

            public void classDeleted(OntologyModelEvent e)
            {
                deleteClassFromTree(e.getSuperClass(), e.getOntologyClass());
                fireClassDeletedEvent(e);
                ontologyCore.setDirty(true);
            }

            public void termAdded(OntologyModelEvent e)
            {
                addTermToTree(e.getParent(), e.getTerm(), e.getPosition());
                fireTermAddedEvent(e);
                ontologyCore.setDirty(true);
            }

            public void termDeleted(OntologyModelEvent e)
            {
                deleteTermFromTree(e.getParent(), e.getTerm());
                fireTermDeletedEvent(e);
                ontologyCore.setDirty(true);
            }

            public void attributeAdded(OntologyModelEvent e)
            {
                addAttributeToTree(e.getOntologyClass(), e.getAttribute());
                fireAttributeAddedEvent(e);
                ontologyCore.setDirty(true);
            }

            public void attributeDeleted(OntologyModelEvent e)
            {
                deleteAttributeFromTree(e.getOntologyClass(), e.getAttribute());
                fireAttributeDeletedEvent(e);
                ontologyCore.setDirty(true);
            }

            public void axiomAdded(OntologyModelEvent e)
            {
                addAxiomToTree(e.getOntologyClass(), e.getAxiom());
                fireAxiomAddedEvent(e);
                ontologyCore.setDirty(true);
            }

            public void axiomDeleted(OntologyModelEvent e)
            {
                deleteAxiomFromTree(e.getOntologyClass(), e.getAxiom());
                fireAxiomDeletedEvent(e);
                ontologyCore.setDirty(true);
            }

            public void relationshipAdded(OntologyModelEvent e)
            {
                addRelationshipToTree(e.getTerm(), e.getRelationship());
                fireRelationshipAddedEvent(e);
                ontologyCore.setDirty(true);
            }

            public void relationshipDeleted(OntologyModelEvent e)
            {
                deleteRelationshipFromTree(e.getTerm(), e.getRelationship());
                fireRelationshipDeletedEvent(e);
                ontologyCore.setDirty(true);
            }

            public void domainEntryAdded(OntologyModelEvent e)
            {
                addDomainEntryToTree(e.getDomain(), e.getEntry());
                fireDomainEntryAddedEvent(e);
                ontologyCore.setDirty(true);
            }

            public void domainEntryDeleted(OntologyModelEvent e)
            {
                deleteDomainEntryFromTree(e.getDomain(), e.getEntry());
                fireDomainEntryDeletedEvent(e);
                ontologyCore.setDirty(true);
            }
        });

        // Initialize the view
        ontologyTree = new JTree(new OntologyTreeModel(this));
        ontologyTree.setCellRenderer(new OntologyTreeRenderer());
        ontologyTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        ToolTipManager.sharedInstance().registerComponent(ontologyTree);
        ontologyTree.putClientProperty("JTree.lineStyle", "Angled");
        PopupTrigger pt = new PopupTrigger();
        pt.addPopupListener(new PopupListener()
        {
            public void popup(MouseEvent e)
            {
                JTree ontologyTree = (JTree) e.getSource();
                TreePath path = ontologyTree.getPathForLocation(e.getX(), e.getY());
                if (path == null)
                    return;
                ontologyTree.setSelectionPath(path);
                showPopupForObject((DefaultMutableTreeNode) path.getLastPathComponent(), e);
            }
        });
        ontologyTree.addMouseListener(pt);
        ontologyTree.addTreeSelectionListener(new TreeSelectionListener()
        {
            public void valueChanged(TreeSelectionEvent e)
            {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) ((JTree) e.getSource())
                    .getLastSelectedPathComponent();
                if (node != null)
                {
                    Object object = node.getUserObject();
                    fireOntologySelectionEvent(object);
                }
            }
        });
        ontologyTree.setEditable(true);
        ontologyCellEditor = new OntologyTreeCellEditor(ontologyTree);
        ontologyTree.setCellEditor(new OntologyDefaultTreeCellEditor(ontologyTree,
            (DefaultTreeCellRenderer) ontologyTree.getCellRenderer(), ontologyCellEditor));
        ontologyTree.getCellEditor().addCellEditorListener(new CellEditorListener()
        {
            public void editingStopped(ChangeEvent e)
            {
                OntologyTreeCellEditor editor = (OntologyTreeCellEditor) e.getSource();
                OntologyObject o = editor.getObjectBeingEdited();
                if (o != null)
                    o.setName(editor.getChangedValue());
            }

            public void editingCanceled(ChangeEvent e)
            {
            }
        });
        add(BorderLayout.CENTER, ontologyTree);
    }

    public String toString()
    {
        return ontologyCore.toString();
    }

    /**
     * Get the OntologyModel
     * 
     * @return the {@link OntologyModel}
     */
    public Ontology getOntology()
    {
        return ontologyCore;
    }

    /**
     * Get the ontology name
     * 
     * @return the ontology name
     */
    public String getName()
    {
        return ontologyCore.getName();
    }

    /**
     * Set the ontology name
     * 
     * @param name the ontology name
     */
    public void setName(String name)
    {
        ontologyCore.setName(name);
    }

    /**
     * Get the ontology title
     * 
     * @return the ontology title
     */
    public String getTitle()
    {
        return ontologyCore.getTitle();
    }

    /**
     * Set the ontology title
     * 
     * @param title the ontology title
     */
    public void setTitle(String title)
    {
        ontologyCore.setTitle(title);
    }

    /**
     * Set the site URL
     * 
     * @param siteURL the site {@link URL}
     */
    public void setSiteURL(URL siteURL)
    {
        ontologyCore.setSiteURL(siteURL);
    }

    /**
     * Get the site URL
     * 
     * @return the site {@link URL}
     */
    public URL getSiteURL()
    {
        return ontologyCore.getSiteURL();
    }

    /**
     * Add a term
     * 
     * @param term the {@link Term} to add
     */
    public void addTerm(Term term)
    {
        ontologyCore.addTerm(term);
    }

    /**
     * Remove a term
     * 
     * @param term the {@link Term} to remove
     */
    public void removeTerm(Term term)
    {
        ontologyCore.removeTerm(term);
    }

    /**
     * Get the number of terms
     */
    public int getTermsCount()
    {
        return ontologyCore.getTermsCount();
    }

    /**
     * Gets term according to ordinal index of term in term list. Not very useful unless you know
     * what you are looking for. Consider using getTermByID(id) if you know the Term ID
     * 
     * @param index
     * @return matching {@link Term} or Null if not found
     */
    public Term getTerm(int index)
    {
        return ontologyCore.getTerm(index);
    }

    /**
     * Gets term according to term ID. Term ID is randomly generated when ontology is created and
     * saved to the ontology file Subsequent matching of this ontology with others will use this id
     * as well inside the MatchInformation object
     * 
     * @param id termID to find
     * @return {@link Term} if found, Null otherwise
     */
    public Term getTermByID(long id)
    {
        return ontologyCore.getTermByID(id);
    }

    public void addClass(OntologyClass ontologyClass)
    {
        ontologyCore.addClass(ontologyClass);
    }

    public void removeClass(OntologyClass ontologyClass)
    {
        ontologyCore.removeClass(ontologyClass);
    }

    public int getClassesCount()
    {
        return ontologyCore.getClassesCount();
    }

    public OntologyClass getClass(int index)
    {
        return ontologyCore.getClass(index);
    }

    public void addOntologySelectionListener(OntologySelectionListener l)
    {
        selectionListeners.add(l);
    }

    public void removeOntologySelectionListener(OntologySelectionListener l)
    {
        selectionListeners.remove(l);
    }

    protected void fireOntologySelectionEvent(Object object)
    {
        OntologySelectionEvent event = new OntologySelectionEvent(this, object);
        for (Iterator<OntologySelectionListener> i = selectionListeners.iterator(); i.hasNext();)
        {
            OntologySelectionListener l = (OntologySelectionListener) i.next();
            l.valueChanged(event);
        }
    }

    public void addOntologyModelListener(OntologyModelListener l)
    {
        modelListeners.add(l);
    }

    public void removeOntologyModelListener(OntologyModelListener l)
    {
        modelListeners.remove(l);
    }

    protected void fireModelChangedEvent(OntologyModelEvent e)
    {
        OntologyModelEvent event = new OntologyModelEvent(this, e.getObject());
        for (Iterator<OntologyModelListener> i = modelListeners.iterator(); i.hasNext();)
        {
            OntologyModelListener l = (OntologyModelListener) i.next();
            l.modelChanged(event);
        }
    }

    protected void fireObjectChangedEvent(OntologyModelEvent e)
    {
        OntologyModelEvent event = new OntologyModelEvent(this, e.getObject());
        for (Iterator<OntologyModelListener> i = modelListeners.iterator(); i.hasNext();)
        {
            OntologyModelListener l = (OntologyModelListener) i.next();
            l.objectChanged(event);
        }
    }

    protected void fireTermAddedEvent(OntologyModelEvent e)
    {
        OntologyModelEvent event = new OntologyModelEvent(this, e.getParent(), e.getTerm());
        for (Iterator<OntologyModelListener> i = modelListeners.iterator(); i.hasNext();)
        {
            OntologyModelListener l = (OntologyModelListener) i.next();
            l.termAdded(event);
        }
    }

    protected void fireTermDeletedEvent(OntologyModelEvent e)
    {
        OntologyModelEvent event = new OntologyModelEvent(this, e.getParent(), e.getTerm());
        for (Iterator<OntologyModelListener> i = modelListeners.iterator(); i.hasNext();)
        {
            OntologyModelListener l = (OntologyModelListener) i.next();
            l.termDeleted(event);
        }
    }

    protected void fireClassAddedEvent(OntologyModelEvent e)
    {
        OntologyModelEvent event = new OntologyModelEvent(this, e.getSuperClass(),
            e.getOntologyClass());
        for (Iterator<OntologyModelListener> i = modelListeners.iterator(); i.hasNext();)
        {
            OntologyModelListener l = (OntologyModelListener) i.next();
            l.classAdded(event);
        }
    }

    protected void fireClassDeletedEvent(OntologyModelEvent e)
    {
        OntologyModelEvent event = new OntologyModelEvent(this, e.getSuperClass(),
            e.getOntologyClass());
        for (Iterator<OntologyModelListener> i = modelListeners.iterator(); i.hasNext();)
        {
            OntologyModelListener l = (OntologyModelListener) i.next();
            l.classDeleted(event);
        }
    }

    protected void fireAttributeAddedEvent(OntologyModelEvent e)
    {
        OntologyModelEvent event = new OntologyModelEvent(this, e.getOntologyClass(),
            e.getAttribute());
        for (Iterator<OntologyModelListener> i = modelListeners.iterator(); i.hasNext();)
        {
            OntologyModelListener l = (OntologyModelListener) i.next();
            l.attributeAdded(event);
        }
    }

    protected void fireAttributeDeletedEvent(OntologyModelEvent e)
    {
        OntologyModelEvent event = new OntologyModelEvent(this, e.getOntologyClass(),
            e.getAttribute());
        for (Iterator<OntologyModelListener> i = modelListeners.iterator(); i.hasNext();)
        {
            OntologyModelListener l = (OntologyModelListener) i.next();
            l.attributeDeleted(event);
        }
    }

    protected void fireAxiomAddedEvent(OntologyModelEvent e)
    {
        OntologyModelEvent event = new OntologyModelEvent(this, e.getOntologyClass(), e.getAxiom());
        for (Iterator<OntologyModelListener> i = modelListeners.iterator(); i.hasNext();)
        {
            OntologyModelListener l = (OntologyModelListener) i.next();
            l.axiomAdded(event);
        }
    }

    protected void fireAxiomDeletedEvent(OntologyModelEvent e)
    {
        OntologyModelEvent event = new OntologyModelEvent(this, e.getOntologyClass(), e.getAxiom());
        for (Iterator<OntologyModelListener> i = modelListeners.iterator(); i.hasNext();)
        {
            OntologyModelListener l = (OntologyModelListener) i.next();
            l.axiomDeleted(event);
        }
    }

    protected void fireRelationshipAddedEvent(OntologyModelEvent e)
    {
        OntologyModelEvent event = new OntologyModelEvent(this, e.getTerm(), e.getRelationship());
        for (Iterator<OntologyModelListener> i = modelListeners.iterator(); i.hasNext();)
        {
            OntologyModelListener l = (OntologyModelListener) i.next();
            l.relationshipAdded(event);
        }
    }

    protected void fireRelationshipDeletedEvent(OntologyModelEvent e)
    {
        OntologyModelEvent event = new OntologyModelEvent(this, e.getTerm(), e.getRelationship());
        for (Iterator<OntologyModelListener> i = modelListeners.iterator(); i.hasNext();)
        {
            OntologyModelListener l = (OntologyModelListener) i.next();
            l.relationshipDeleted(event);
        }
    }

    protected void fireDomainEntryAddedEvent(OntologyModelEvent e)
    {
        OntologyModelEvent event = new OntologyModelEvent(this, e.getDomain(), e.getEntry());
        for (Iterator<OntologyModelListener> i = modelListeners.iterator(); i.hasNext();)
        {
            OntologyModelListener l = (OntologyModelListener) i.next();
            l.domainEntryAdded(event);
        }
    }

    protected void fireDomainEntryDeletedEvent(OntologyModelEvent e)
    {
        OntologyModelEvent event = new OntologyModelEvent(this, e.getDomain(), e.getEntry());
        for (Iterator<OntologyModelListener> i = modelListeners.iterator(); i.hasNext();)
        {
            OntologyModelListener l = (OntologyModelListener) i.next();
            l.domainEntryDeleted(event);
        }
    }

    /**
     * Initialize all the relevant actions
     */
    protected void initializeActions()
    {
        actions = new Actions();

        // Add class
        Action action = new AbstractAction(
            ApplicationUtilities.getResourceString("ontology.action.addClass"),
            ApplicationUtilities.getImage("addclass.gif"))
        {
            /**
			 * 
			 */
            private static final long serialVersionUID = -365323134917080608L;

            public void actionPerformed(ActionEvent e)
            {
                commandAddClass();
            }
        };
        action.putValue(Action.LONG_DESCRIPTION,
            ApplicationUtilities.getResourceString("ontology.action.addClass.longDescription"));
        action.putValue(Action.SHORT_DESCRIPTION,
            ApplicationUtilities.getResourceString("ontology.action.addClass.shortDescription"));
        action.putValue(
            Action.MNEMONIC_KEY,
            new Integer(KeyStroke.getKeyStroke(
                ApplicationUtilities.getResourceString("ontology.action.addClass.mnemonic"))
                .getKeyCode()));
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ApplicationUtilities
            .getResourceString("ontology.action.addClass.accelerator")));
        actions.addAction("addClass", action);

        // Add term
        action = new AbstractAction(
            ApplicationUtilities.getResourceString("ontology.action.addTerm"),
            ApplicationUtilities.getImage("addterm.gif"))
        {
            /**
			 * 
			 */
            private static final long serialVersionUID = -6798374443298269596L;

            public void actionPerformed(ActionEvent e)
            {
                commandAddTerm();
            }
        };
        action.putValue(Action.LONG_DESCRIPTION,
            ApplicationUtilities.getResourceString("ontology.action.addTerm.longDescription"));
        action.putValue(Action.SHORT_DESCRIPTION,
            ApplicationUtilities.getResourceString("ontology.action.addTerm.shortDescription"));
        action.putValue(
            Action.MNEMONIC_KEY,
            new Integer(KeyStroke.getKeyStroke(
                ApplicationUtilities.getResourceString("ontology.action.addTerm.mnemonic"))
                .getKeyCode()));
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ApplicationUtilities
            .getResourceString("ontology.action.addTerm.accelerator")));
        actions.addAction("addTerm", action);

        // Add Attribute
        action = new AbstractAction(
            ApplicationUtilities.getResourceString("ontology.action.addAttribute"),
            ApplicationUtilities.getImage("addattribute.gif"))
        {
            /**
			 * 
			 */
            private static final long serialVersionUID = -5782247577102612427L;

            public void actionPerformed(ActionEvent e)
            {
                commandAddAttribute();
            }
        };
        action.putValue(Action.LONG_DESCRIPTION,
            ApplicationUtilities.getResourceString("ontology.action.addAttribute.longDescription"));
        action
            .putValue(Action.SHORT_DESCRIPTION, ApplicationUtilities
                .getResourceString("ontology.action.addAttribute.shortDescription"));
        action.putValue(
            Action.MNEMONIC_KEY,
            new Integer(KeyStroke.getKeyStroke(
                ApplicationUtilities.getResourceString("ontology.action.addAttribute.mnemonic"))
                .getKeyCode()));
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ApplicationUtilities
            .getResourceString("ontology.action.addAttribute.accelerator")));
        actions.addAction("addAttribute", action);

        // Add Axiom
        action = new AbstractAction(
            ApplicationUtilities.getResourceString("ontology.action.addAxiom"),
            ApplicationUtilities.getImage("addaxiom.gif"))
        {
            /**
			 * 
			 */
            private static final long serialVersionUID = -1429484172124654728L;

            public void actionPerformed(ActionEvent e)
            {
                commandAddAxiom();
            }
        };
        action.putValue(Action.LONG_DESCRIPTION,
            ApplicationUtilities.getResourceString("ontology.action.addAxiom.longDescription"));
        action.putValue(Action.SHORT_DESCRIPTION,
            ApplicationUtilities.getResourceString("ontology.action.addAxiom.shortDescription"));
        action.putValue(
            Action.MNEMONIC_KEY,
            new Integer(KeyStroke.getKeyStroke(
                ApplicationUtilities.getResourceString("ontology.action.addAxiom.mnemonic"))
                .getKeyCode()));
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ApplicationUtilities
            .getResourceString("ontology.action.addAxiom.accelerator")));
        actions.addAction("addAxiom", action);

        // Add Relationship
        action = new AbstractAction(
            ApplicationUtilities.getResourceString("ontology.action.addRelationship"),
            ApplicationUtilities.getImage("addrelationship.gif"))
        {
            /**
			 * 
			 */
            private static final long serialVersionUID = 6838465638619976106L;

            public void actionPerformed(ActionEvent e)
            {
                commandAddRelationship();
            }
        };
        action.putValue(Action.LONG_DESCRIPTION, ApplicationUtilities
            .getResourceString("ontology.action.addRelationship.longDescription"));
        action.putValue(Action.SHORT_DESCRIPTION, ApplicationUtilities
            .getResourceString("ontology.action.addRelationship.shortDescription"));
        action.putValue(
            Action.MNEMONIC_KEY,
            new Integer(KeyStroke.getKeyStroke(
                ApplicationUtilities.getResourceString("ontology.action.addRelationship.mnemonic"))
                .getKeyCode()));
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ApplicationUtilities
            .getResourceString("ontology.action.addRelationship.accelerator")));
        actions.addAction("addRelationship", action);

        // Add Domain Entry
        action = new AbstractAction(
            ApplicationUtilities.getResourceString("ontology.action.addDomainEntry"),
            ApplicationUtilities.getImage("adddomainentry.gif"))
        {
            /**
			 * 
			 */
            private static final long serialVersionUID = 2742783500475014344L;

            public void actionPerformed(ActionEvent e)
            {
                commandAddDomainEntry();
            }
        };
        action.putValue(Action.LONG_DESCRIPTION, ApplicationUtilities
            .getResourceString("ontology.action.addDomainEntry.longDescription"));
        action.putValue(Action.SHORT_DESCRIPTION, ApplicationUtilities
            .getResourceString("ontology.action.addDomainEntry.shortDescription"));
        action.putValue(
            Action.MNEMONIC_KEY,
            new Integer(KeyStroke.getKeyStroke(
                ApplicationUtilities.getResourceString("ontology.action.addDomainEntry.mnemonic"))
                .getKeyCode()));
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ApplicationUtilities
            .getResourceString("ontology.action.addDomainEntry.accelerator")));
        actions.addAction("addDomainEntry", action);

        // Delete
        action = new AbstractAction(
            ApplicationUtilities.getResourceString("ontology.action.delete"),
            ApplicationUtilities.getImage("delete.gif"))
        {
            /**
			 * 
			 */
            private static final long serialVersionUID = 4629467236841994609L;

            public void actionPerformed(ActionEvent e)
            {
                commandDelete();
            }
        };
        action.putValue(Action.LONG_DESCRIPTION,
            ApplicationUtilities.getResourceString("ontology.action.delete.longDescription"));
        action.putValue(Action.SHORT_DESCRIPTION,
            ApplicationUtilities.getResourceString("ontology.action.delete.shortDescription"));
        action.putValue(
            Action.MNEMONIC_KEY,
            new Integer(KeyStroke.getKeyStroke(
                ApplicationUtilities.getResourceString("ontology.action.delete.mnemonic"))
                .getKeyCode()));
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ApplicationUtilities
            .getResourceString("ontology.action.delete.accelerator")));
        actions.addAction("delete", action);

        // Rename
        action = new AbstractAction(
            ApplicationUtilities.getResourceString("ontology.action.rename"),
            ApplicationUtilities.getImage("rename.gif"))
        {
            /**
			 * 
			 */
            private static final long serialVersionUID = 3193573320053147756L;

            public void actionPerformed(ActionEvent e)
            {
                commandRenameObject();
            }
        };
        action.putValue(Action.LONG_DESCRIPTION,
            ApplicationUtilities.getResourceString("ontology.action.rename.longDescription"));
        action.putValue(Action.SHORT_DESCRIPTION,
            ApplicationUtilities.getResourceString("ontology.action.rename.shortDescription"));
        action.putValue(
            Action.MNEMONIC_KEY,
            new Integer(KeyStroke.getKeyStroke(
                ApplicationUtilities.getResourceString("ontology.action.rename.mnemonic"))
                .getKeyCode()));
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ApplicationUtilities
            .getResourceString("ontology.action.rename.accelerator")));
        actions.addAction("rename", action);
    }

    /**
     * Execute a "Rename" command
     */
    protected void commandRenameObject()
    {
        ontologyTree.startEditingAtPath(ontologyTree.getSelectionModel().getSelectionPath());
        ontologyCellEditor.getEditorComponent().requestFocus();
    }

    /**
     * Execute a "AddClass" command
     */
    protected void commandAddClass()
    {
        OntologyClass parent = null;
        if (actionObject instanceof OntologyClass)
            parent = (OntologyClass) actionObject;
        if (actionObject instanceof OntologyClassGui)
            parent = ((OntologyClassGui) actionObject).getOntolgoyClass();
        OntologyClass c = OntologyClassGui.createClassDialog(parent);
        if (c == null)
            return;
        if (parent == null)
            addClass(c);
    }

    /**
     * Execute a "AddTerm" command
     */
    protected void commandAddTerm()
    {
        Term t = TermGui.createTermDialog(ontologyCore);
        if (t == null)
            return;
        TermGui parent = (TermGui) actionObject;
        if (parent != null)
            parent.getTerm().addTerm(t);
        else
            addTerm(t);
    }

    /**
     * Execute a "AddAttribute" command
     */
    protected void commandAddAttribute()
    {
        Attribute a = AttributeGui.createAttributeDialog();
        if (a == null)
            return;
        if (actionObject instanceof DomainEntry)
            actionObject = ((DomainEntry) actionObject).getEntry();
        
        OntologyClass parent = null;
        if (actionObject instanceof OntologyClassGui)
            parent = ((OntologyClassGui) actionObject).getOntolgoyClass();
        if (actionObject instanceof TermGui)
            parent = ((TermGui) actionObject).getTerm().getSuperClass();
        if (parent != null)
            parent.addAttribute(a);
    }

    /**
     * Execute a "AddAxiom" command
     */
    public void commandAddAxiom()
    {
        Axiom a = AxiomGui.createAxiomDialog();
        if (a == null)
            return;
        
        OntologyClass parent = null;
        if (actionObject instanceof OntologyClassGui)
            parent = ((OntologyClassGui) actionObject).getOntolgoyClass();
        if (actionObject instanceof TermGui)
            parent = ((TermGui) actionObject).getTerm().getSuperClass();
        
        if (parent != null)
            parent.addAxiom(a);
    }

    /**
     * Execute a "AddRelationship" command
     */
    public void commandAddRelationship()
    {
        TermGui source = (TermGui) actionObject;
        Relationship r = RelationshipGui.createRelationshipDialog(source.getTerm(), this);
        if (r != null)
            source.getTerm().addRelationship(r);
    }

    /**
     * Execute a "AddDomainEntr" command
     */
    public void commandAddDomainEntry()
    {
        Domain domain = (Domain) actionObject;
        DomainEntry entry = DomainEntryGui.createEntryDialog(ontologyCore);
        if (entry != null)
            domain.addEntry(entry);
    }

    /**
     * Execute a "Delete" command
     */
    protected void commandDelete()
    {
        if (actionObject instanceof Term)
        {
            Term t = (Term) actionObject;
            Vector<?> relationships = ontologyCore.getRelationships();
            for (Iterator<?> i = relationships.iterator(); i.hasNext();)
            {
                Relationship r = (Relationship) i.next();
                if (t.equals(r.getSource()) || t.equals(r.getTarget()))
                {
                    if (JOptionPane.showConfirmDialog(this, StringUtilities.getReplacedString(
                        ApplicationUtilities.getResourceString("warning.ontology.deleteTerm"),
                        new String[]
                        {
                            t.getName()
                        }), ApplicationUtilities.getResourceString("ontology"),
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION)
                        return;
                }
            }
            Term pt = t.getParent();
            if (pt != null)
                pt.removeTerm(t);
            else
                removeTerm(t);
        }
        if (actionObject instanceof OntologyClass)
        {
            OntologyClass c = (OntologyClass) actionObject;
            Vector<?> terms = ontologyCore.getTerms(false);
            for (Iterator<?> i = terms.iterator(); i.hasNext();)
                if (c.equals(((Term) i.next()).getSuperClass()))
                {
                    JOptionPane.showMessageDialog(null, StringUtilities.getReplacedString(
                        ApplicationUtilities.getResourceString("warning.ontology.deleteClass"),
                        new String[]
                        {
                            c.getName()
                        }), ApplicationUtilities.getResourceString("warning"),
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            OntologyClass sc = c.getSuperClass();
            if (sc != null)
                sc.removeSubClass(c);
            else
                removeClass(c);
        }
        else if (actionObject instanceof Attribute)
        {
            Attribute a = (Attribute) actionObject;
            OntologyClass c = a.getOntologyClass();
            c.removeAttribute(a);
        }
        else if (actionObject instanceof Axiom)
        {
            Axiom a = (Axiom) actionObject;
            OntologyClass c = a.getOntologyClass();
            c.removeAxiom(a);
        }
        else if (actionObject instanceof Relationship)
        {
            Relationship r = (Relationship) actionObject;
            Term t = r.getSource();
            t.removeRelationship(r);
        }
        else if (actionObject instanceof DomainEntry)
        {
            DomainEntry de = (DomainEntry) actionObject;
            Domain domain = de.getDomain();
            domain.removeEntry(de);
        }
    }

    protected void createPopupMenu()
    {
        // Sets the Popup Menu
        popMenu = new JPopupMenu(ApplicationUtilities.getResourceString("ontology"));
        addMenuItem(popMenu, actions.getAction("addClass"));
        addMenuItem(popMenu, actions.getAction("addTerm"));
        addMenuItem(popMenu, actions.getAction("addAttribute"));
        addMenuItem(popMenu, actions.getAction("addAxiom"));
        addMenuItem(popMenu, actions.getAction("addRelationship"));
        popMenu.addSeparator();
        addMenuItem(popMenu, actions.getAction("addDomainEntry"));
        popMenu.addSeparator();
        addMenuItem(popMenu, actions.getAction("delete"));
        addMenuItem(popMenu, actions.getAction("rename"));
    }

    protected JMenuItem addMenuItem(JPopupMenu menu, Action action)
    {
        JMenuItem menuItem = menu.add(action);
        menuItem.setAccelerator((KeyStroke) action.getValue(Action.ACCELERATOR_KEY));
        if (action.getValue("helpID") != null)
            CSH.setHelpIDString(menuItem, (String) action.getValue("helpID"));
        return menuItem;
    }

    protected void showPopupForObject(DefaultMutableTreeNode node, MouseEvent e)
    {
        Set<?> actionNames = actions.getActions();
        for (Iterator<?> i = actionNames.iterator(); i.hasNext();)
            actions.getAction((String) i.next()).setEnabled(false);

        actionObject = null;
        if (node == null)
            return;
        Object object = node.getUserObject();
        if (object instanceof String)
        {
            String nodeString = (String) object;
            if (nodeString.equals(ApplicationUtilities.getResourceString("ontology.terms")) ||
                nodeString.equals(ApplicationUtilities.getResourceString("ontology.subterms")))
            {
                actions.getAction("addTerm").setEnabled(true);
            }
            else if (nodeString.equals(ApplicationUtilities.getResourceString("ontology.classes")) ||
                nodeString.equals(ApplicationUtilities
                    .getResourceString("ontology.class.subclasses")))
            {
                actions.getAction("addClass").setEnabled(true);
            }
            else if (nodeString.equals(ApplicationUtilities
                .getResourceString("ontology.attributes")))
            {
                actions.getAction("addAttribute").setEnabled(true);
            }
            else if (nodeString.equals(ApplicationUtilities.getResourceString("ontology.axioms")))
            {
                actions.getAction("addAxiom").setEnabled(true);
            }
            else if (nodeString.equals(ApplicationUtilities
                .getResourceString("ontology.relationships")))
            {
                actions.getAction("addRelationship").setEnabled(true);
            }
            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();
            if (parentNode != null && !parentNode.isRoot())
                actionObject = parentNode.getUserObject();
        }
        else
        {
            if (object instanceof Term)
            {
                actions.getAction("delete").setEnabled(true);
                actions.getAction("rename").setEnabled(true);
            }
            else if (object instanceof OntologyClass)
            {
                actions.getAction("delete").setEnabled(true);
                actions.getAction("rename").setEnabled(true);
            }
            else if (object instanceof Attribute)
            {
                actions.getAction("delete").setEnabled(true);
                actions.getAction("rename").setEnabled(true);
            }
            else if (object instanceof Axiom)
            {
                actions.getAction("delete").setEnabled(true);
                actions.getAction("rename").setEnabled(true);
            }
            else if (object instanceof Relationship)
            {
                actions.getAction("delete").setEnabled(true);
                actions.getAction("rename").setEnabled(true);
            }
            else if (object instanceof Domain)
            {
                // if(((Domain)object).getEntriesCount()==0)
                actions.getAction("rename").setEnabled(true);
                // if(((Domain)object).getName().equals(ApplicationUtilities.getResourceString("ontology.domain.choice")))
                actions.getAction("addDomainEntry").setEnabled(true);
            }
            else if (object instanceof DomainEntry)
            {
                actions.getAction("delete").setEnabled(true);
            }
            actionObject = object;
        }
        popMenu.show(e.getComponent(), e.getX(), e.getY());
    }

    protected void addClassToTree(OntologyClass superClass, OntologyClass ontologyClass)
    {
        try
        {
            OntologyTreeModel treeModel = (OntologyTreeModel) ontologyTree.getModel();
            DefaultMutableTreeNode classesNode;
            if (superClass == null) // add it to the root
                classesNode = treeModel.findChildNodeWithUserObject(ApplicationUtilities
                    .getResourceString("ontology.classes"));
            else
            {
                DefaultMutableTreeNode parentClassNode = treeModel
                    .findNodeWithUserObject(superClass);
                classesNode = treeModel.findChildNodeWithUserObject(parentClassNode,
                    ApplicationUtilities.getResourceString("ontology.class.subclasses"));
            }
            OntologyClassGui ontologyClassGui = new OntologyClassGui(ontologyClass);
            treeModel.insertNodeInto(ontologyClassGui.getTreeBranch(), classesNode,
                classesNode.getChildCount());
        }
        catch (Exception e)
        {
            return;
        }
    }

    protected void deleteClassFromTree(OntologyClass superClass, OntologyClass ontologyClass)
    {
        try
        {
            OntologyTreeModel treeModel = (OntologyTreeModel) ontologyTree.getModel();
            DefaultMutableTreeNode classNode = treeModel.findNodeWithUserObject(ontologyClass);
            treeModel.removeNodeFromParent(classNode);
        }
        catch (Exception e)
        {
            return;
        }
    }

    protected void addTermToTree(Term parent, Term term)
    {
        addTermToTree(parent, term, -1);
    }

    protected void addTermToTree(Term parent, Term term, int position)
    {
        try
        {
            OntologyTreeModel treeModel = (OntologyTreeModel) ontologyTree.getModel();
            DefaultMutableTreeNode termsNode;
            if (parent == null) // add it to the root
                termsNode = treeModel.findChildNodeWithUserObject(ApplicationUtilities
                    .getResourceString("ontology.terms"));
            else
            {
                DefaultMutableTreeNode parentTermNode = treeModel.findNodeWithUserObject(parent);
                termsNode = treeModel.findChildNodeWithUserObject(parentTermNode,
                    ApplicationUtilities.getResourceString("ontology.subterms"));
            }
            TermGui termGui = new TermGui(term);
            treeModel.insertNodeInto(termGui.getTreeBranch(), termsNode,
                position == -1 ? termsNode.getChildCount() : position);
        }
        catch (Exception e)
        {
            return;
        }
    }

    protected void deleteTermFromTree(Term parent, Term term)
    {
        try
        {
            OntologyTreeModel treeModel = (OntologyTreeModel) ontologyTree.getModel();
            DefaultMutableTreeNode termNode = treeModel.findNodeWithUserObject(term);
            treeModel.removeNodeFromParent(termNode);
        }
        catch (Exception e)
        {
            return;
        }
    }

    protected void addAttributeToTree(OntologyClass ontologyClass, Attribute attribute)
    {
        try
        {
            OntologyTreeModel treeModel = (OntologyTreeModel) ontologyTree.getModel();
            DefaultMutableTreeNode parentNode = treeModel.findNodeWithUserObject(ontologyClass);
            DefaultMutableTreeNode attributesNode = treeModel.findChildNodeWithUserObject(
                parentNode, ApplicationUtilities.getResourceString("ontology.attributes"));
            treeModel.insertNodeInto(OntologyObjectGuiFactory.getOntologyObjectGui(attribute).getTreeBranch(), attributesNode,
                attributesNode.getChildCount());
        }
        catch (Exception e)
        {
            return;
        }
    }

    protected void deleteAttributeFromTree(OntologyClass ontologyClass, Attribute attribute)
    {
        try
        {
            OntologyTreeModel treeModel = (OntologyTreeModel) ontologyTree.getModel();
            DefaultMutableTreeNode attributeNode = treeModel.findNodeWithUserObject(attribute);
            treeModel.removeNodeFromParent(attributeNode);
        }
        catch (Exception e)
        {
            return;
        }
    }

    protected void addAxiomToTree(OntologyClass ontologyClass, Axiom axiom)
    {
        try
        {
            OntologyTreeModel treeModel = (OntologyTreeModel) ontologyTree.getModel();
            DefaultMutableTreeNode parentNode = treeModel.findNodeWithUserObject(ontologyClass);
            DefaultMutableTreeNode axiomsNode = treeModel.findChildNodeWithUserObject(parentNode,
                ApplicationUtilities.getResourceString("ontology.axioms"));
            treeModel.insertNodeInto(OntologyObjectGuiFactory.getOntologyObjectGui(axiom).getTreeBranch(), axiomsNode, axiomsNode.getChildCount());
        }
        catch (Exception e)
        {
            return;
        }
    }

    protected void deleteAxiomFromTree(OntologyClass ontologyClass, Axiom axiom)
    {
        try
        {
            OntologyTreeModel treeModel = (OntologyTreeModel) ontologyTree.getModel();
            DefaultMutableTreeNode axiomNode = treeModel.findNodeWithUserObject(axiom);
            treeModel.removeNodeFromParent(axiomNode);
        }
        catch (Exception e)
        {
            return;
        }
    }

    protected void addRelationshipToTree(Term term, Relationship relationship)
    {
        try
        {
            OntologyTreeModel treeModel = (OntologyTreeModel) ontologyTree.getModel();
            DefaultMutableTreeNode parentNode = treeModel.findNodeWithUserObject(term);
            DefaultMutableTreeNode relationshipsNode = treeModel.findChildNodeWithUserObject(
                parentNode, ApplicationUtilities.getResourceString("ontology.relationships"));
            treeModel.insertNodeInto(OntologyObjectGuiFactory.getOntologyObjectGui(relationship).getTreeBranch(), relationshipsNode,
                relationshipsNode.getChildCount());
        }
        catch (Exception e)
        {
            return;
        }
    }

    protected void deleteRelationshipFromTree(Term term, Relationship relationship)
    {
        try
        {
            OntologyTreeModel treeModel = (OntologyTreeModel) ontologyTree.getModel();
            DefaultMutableTreeNode relationshipNode = treeModel
                .findNodeWithUserObject(relationship);
            treeModel.removeNodeFromParent(relationshipNode);
        }
        catch (Exception e)
        {
            return;
        }
    }

    protected void addDomainEntryToTree(Domain domain, DomainEntry entry)
    {
        try
        {
            OntologyTreeModel treeModel = (OntologyTreeModel) ontologyTree.getModel();
            DefaultMutableTreeNode domainNode = treeModel.findNodeWithUserObject(domain);
            treeModel.insertNodeInto(OntologyObjectGuiFactory.getOntologyObjectGui(entry).getTreeBranch(), domainNode, domainNode.getChildCount());
        }
        catch (Exception e)
        {
            return;
        }
    }

    protected void deleteDomainEntryFromTree(Domain domain, DomainEntry entry)
    {
        try
        {
            OntologyTreeModel treeModel = (OntologyTreeModel) ontologyTree.getModel();
            DefaultMutableTreeNode entryNode = treeModel.findNodeWithUserObject(entry);
            treeModel.removeNodeFromParent(entryNode);
        }
        catch (Exception e)
        {
            return;
        }
    }

    protected void updateTree(Object object)
    {
        OntologyTreeModel treeModel = (OntologyTreeModel) ontologyTree.getModel();
        for (int i = 0; i < ontologyTree.getRowCount(); i++)
        {
            TreePath path = ontologyTree.getPathForRow(i);
            if (((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject().equals(
                object))
                treeModel.valueForPathChanged(path, object);
        }
    }

    protected void refreshTree()
    {
        OntologyTreeModel treeModel = (OntologyTreeModel) ontologyTree.getModel();
        treeModel.updateTree();
    }

    protected static OntologyGui ontology;

    public static OntologyGui createOntologyDialog()
    {
        final TextField txtOntologyName = new TextField(10);
        final TextField txtOntologyTitle = new TextField(15);
        final TextField txtOntologySite = new TextField(15);

        final JDialog dialog = new JDialog((JFrame) null,
            ApplicationUtilities.getResourceString("ontology.dialog.windowTitle"), true);
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        dialog.setSize(new Dimension(ApplicationUtilities.getIntProperty("ontology.dialog.width"),
            ApplicationUtilities.getIntProperty("ontology.dialog.height")));
        dialog.setLocationRelativeTo(null);
        dialog.setResizable(false);

        JPanel south = new JPanel();
        south.setLayout(new FlowLayout());
        final JButton okButton;
        south.add(okButton = new JButton(ApplicationUtilities
            .getResourceString("ontology.dialog.button.ok")));
        dialog.getRootPane().setDefaultButton(okButton);
        okButton.setEnabled(txtOntologyTitle.getText().trim().length() > 0 &&
            txtOntologyName.getText().trim().length() > 0);
        okButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                ontology = new OntologyGui(txtOntologyName.getText(), txtOntologyTitle.getText());
                try
                {
                    ontology.setSiteURL(NetworkUtilities.makeURL(txtOntologySite.getText()));
                    dialog.dispose();
                }
                catch (MalformedURLException ex)
                {
                    JOptionPane.showMessageDialog(dialog,
                        ApplicationUtilities.getResourceString("error") + ": " + ex.getMessage(),
                        ApplicationUtilities.getResourceString("error"), JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        JButton cancelButton;
        south.add(cancelButton = new JButton(ApplicationUtilities
            .getResourceString("ontology.dialog.button.cancel")));
        cancelButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                ontology = null;
                dialog.dispose();
            }
        });
        panel.add(BorderLayout.SOUTH, south);

        JPanel center = new JPanel(new GridBagLayout());
        panel.add(BorderLayout.CENTER, center);
        center.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        {// Title
            JLabel title = new JLabel(ApplicationUtilities.getResourceString("ontology"),
                ApplicationUtilities.getImage("ontology.gif"), JLabel.LEFT);
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
                new MultilineLabel(ApplicationUtilities
                    .getResourceString("ontology.dialog.explanation")), gbcl);
        }

        {// Name
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = 2;
            gbcl.insets = new Insets(0, 0, 5, 5);
            gbcl.anchor = GridBagConstraints.EAST;
            JLabel name = new JLabel(ApplicationUtilities.getResourceString("ontology.name") + ":");
            name.setFont(new Font(name.getFont().getName(), Font.BOLD, name.getFont().getSize()));
            center.add(name, gbcl);

            gbcl.gridx = 1;
            gbcl.anchor = GridBagConstraints.WEST;
            center.add(txtOntologyName, gbcl);
            txtOntologyName.addKeyListener(new KeyAdapter()
            {
                public void keyPressed(KeyEvent event)
                {
                    SwingUtilities.invokeLater(new Runnable()
                    {
                        public void run()
                        {
                            if (!txtOntologyTitle.getText().trim().equals("") &&
                                !txtOntologyName.getText().trim().equals(""))
                                okButton.setEnabled(true);
                            else
                                okButton.setEnabled(false);
                        }
                    });
                }
            });
        }

        {// Title
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = 3;
            gbcl.insets = new Insets(0, 0, 5, 5);
            gbcl.anchor = GridBagConstraints.EAST;
            JLabel title = new JLabel(ApplicationUtilities.getResourceString("ontology.title") +
                ":");
            title
                .setFont(new Font(title.getFont().getName(), Font.BOLD, title.getFont().getSize()));
            center.add(title, gbcl);

            gbcl.gridx = 1;
            gbcl.weightx = 1;
            gbcl.fill = GridBagConstraints.HORIZONTAL;
            center.add(txtOntologyTitle, gbcl);
            txtOntologyTitle.addKeyListener(new KeyAdapter()
            {
                public void keyTyped(KeyEvent event)
                {
                    SwingUtilities.invokeLater(new Runnable()
                    {
                        public void run()
                        {
                            if (!txtOntologyTitle.getText().trim().equals("") &&
                                !txtOntologyName.getText().trim().equals(""))
                                okButton.setEnabled(true);
                            else
                                okButton.setEnabled(false);
                        }
                    });
                }
            });
        }

        {// Site URL
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = 4;
            gbcl.insets = new Insets(0, 0, 0, 5);
            gbcl.anchor = GridBagConstraints.EAST;
            JLabel name = new JLabel(ApplicationUtilities.getResourceString("ontology.site") + ":");
            center.add(name, gbcl);

            gbcl.gridx = 1;
            gbcl.weightx = 1;
            gbcl.fill = GridBagConstraints.HORIZONTAL;
            center.add(txtOntologySite, gbcl);
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
                        txtOntologyName.requestFocus();
                    }
                });
            }

            public void windowClosing(WindowEvent e)
            {
                ontology = null;
                dialog.dispose();
            }
        });
        dialog.setContentPane(panel);

        dialog.setVisible(true);
        return ontology;
    }
    /**
     * Open an ontology from a file
     * 
     * @param file the {@link File} to read from
     * @return an {@link OntologyGui}
     * @throws IOException
     */
    public static Ontology open(File file) throws IOException
    {
        if (!file.exists())
            throw new IOException(StringUtilities.getReplacedString(
                ApplicationUtilities.getResourceString("error.ontology.file"), new String[]
                {
                    file.getAbsolutePath()
                }));
        try
        {
            String ext = FileUtilities.getFileExtension(file);
            if (ext != null && ext.equalsIgnoreCase("xml"))
                return Ontology.openFromXML(file);
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Ontology model = (Ontology) ois.readObject();
            ois.close();
            model.setListeners(new ArrayList<OntologyModelListener>());
            OntologyGui ontology = new OntologyGui(model);
            ontology.ontologyCore.setFile(file);
            ontology.ontologyCore.setDirty(false);
            return ontology.ontologyCore;
        }
        catch (ClassNotFoundException e)
        {
            throw new IOException(e.getMessage());
        }
    }
    
    public HyperTree getHyperTree(boolean showClasses, boolean showRelations, boolean showProperties)
    {
        return new HyperTree(getHyperTreeNode(showClasses, showRelations, showProperties));
    }

//    public JGraph getGraph()
//    {
//        return getGraph();
//    }

    /**
     * Match two ontologies according to an algorithm
     * 
     * @param ontology the {@link OntologyGui}
     * @param algorithm the {@link Algorithm}
     * @return MatchInformation
     */
    public MatchInformation match(Ontology ontology, Algorithm algorithm)
    {
        return algorithm.match(this.ontologyCore, ontology);
    }

    /**
     * Normalize the model
     */
    public void normalize()
    {
        ontologyCore.normalize();
    }

    /**
     * Generate an ontology from a URL
     * 
     * @param url the {@link OntologyGui}
     * @return an {@link OntologyGui}
     * @throws IOException
     */
    public static Ontology generateOntology(URL url) throws IOException
    {
        OntologyGenerateHelper ontologyGenerateHelper = Ontology.generateOntology(url);
        Ontology ontology = ontologyGenerateHelper.getOntology();
        Document document = ontologyGenerateHelper.getDocument();
        OntologyClass formClass = ontologyGenerateHelper.getFormClass();
        OntologyClass textInputClass = ontologyGenerateHelper.getTextInputClass();
        OntologyClass passwordInputClass = ontologyGenerateHelper.getPasswordInputClass();
        OntologyClass fileInputClass = ontologyGenerateHelper.getFileInputClass();
        OntologyClass hiddenInputClass = ontologyGenerateHelper.getHiddenInputClass();
        OntologyClass checkboxInputClass = ontologyGenerateHelper.getCheckboxInputClass();
        OntologyClass radioInputClass = ontologyGenerateHelper.getRadioInputClass();
        OntologyClass selectInputClass = ontologyGenerateHelper.getSelectInputClass();
        OntologyClass textareaInputClass = ontologyGenerateHelper.getTextareaInputClass();
        OntologyClass buttonInputClass = ontologyGenerateHelper.getButtonInputClass();
        OntologyClass submitInputClass = ontologyGenerateHelper.getSubmitInputClass();
        OntologyClass resetInputClass = ontologyGenerateHelper.getResetInputClass();
        OntologyClass imageInputClass = ontologyGenerateHelper.getImageInputClass();
        Term pageTerm = ontologyGenerateHelper.getPageTerm();
            
        JTree elementsTree = HTMLUtilitiesGui.getFORMElementsHierarchy(document, url);
        ArrayList<?> f = HTMLUtilitiesGui.extractFormsFromTree((DefaultMutableTreeNode) elementsTree
            .getModel().getRoot());

        Term prevFormTerm = null;
        for (Iterator<?> j = f.iterator(); j.hasNext();)
        {
            FORMElementGui form = (FORMElementGui) j.next();
            Term formTerm = new Term(formClass, form.getName());
            if (prevFormTerm != null)
            {
                prevFormTerm.setSucceed(formTerm);
                formTerm.setPrecede(prevFormTerm);
            }
            prevFormTerm = formTerm;
            formTerm.setAttributeValue("method", form.getMethod());
            formTerm.setAttributeValue("action", form.getAction());
            pageTerm.addTerm(formTerm);
            Term prevInputTerm = null;
            for (int k = 0; k < form.getInputsCount(); k++)
            {
                INPUTElementGui input = form.getInput(k);
                Term inputTerm = null;
                if (input.getInputType().equals(INPUTElement.TEXT))
                {
                    TextINPUTElementGui textInput = (TextINPUTElementGui) input;
                    inputTerm = new Term(textInputClass, textInput.getLabel(), textInput.getValue());
                    inputTerm.setAttributeValue("name", textInput.getName());
                    inputTerm.setAttributeValue("defaultValue", textInput.getDefaultValue());
                    if (textInput.getSize() != -1)
                        inputTerm.setAttributeValue("size", new Integer(textInput.getSize()));
                    if (textInput.getMaxLength() != -1)
                        inputTerm.setAttributeValue("maxLength",
                            new Integer(textInput.getMaxLength()));
                    inputTerm.setAttributeValue("readOnly", new Boolean(textInput.isReadOnly()));
                }
                else if (input.getInputType().equals(INPUTElement.PASSWORD))
                {
                    PasswordINPUTElementGui passwordInput = (PasswordINPUTElementGui) input;
                    inputTerm = new Term(passwordInputClass, passwordInput.getLabel(),
                        passwordInput.getValue());
                    inputTerm.setAttributeValue("name", passwordInput.getName());
                    inputTerm.setAttributeValue("defaultValue", passwordInput.getDefaultValue());
                    if (passwordInput.getSize() != -1)
                        inputTerm.setAttributeValue("size", new Integer(passwordInput.getSize()));
                    if (passwordInput.getMaxLength() != -1)
                        inputTerm.setAttributeValue("maxLength",
                            new Integer(passwordInput.getMaxLength()));
                    inputTerm
                        .setAttributeValue("readOnly", new Boolean(passwordInput.isReadOnly()));
                }
                else if (input.getInputType().equals(INPUTElement.FILE))
                {
                    FileINPUTElementGui fileInput = (FileINPUTElementGui) input;
                    inputTerm = new Term(fileInputClass, fileInput.getLabel(), fileInput.getValue());
                    inputTerm.setAttributeValue("name", fileInput.getName());
                    if (fileInput.getSize() != -1)
                        inputTerm.setAttributeValue("size", new Integer(fileInput.getSize()));
                    if (fileInput.getMaxLength() != -1)
                        inputTerm.setAttributeValue("maxLength",
                            new Integer(fileInput.getMaxLength()));
                    inputTerm.setAttributeValue("readOnly", new Boolean(fileInput.isReadOnly()));
                }
                else if (input.getInputType().equals(INPUTElement.HIDDEN))
                {
                    HiddenINPUTElementGui hiddenInput = (HiddenINPUTElementGui) input;
                    inputTerm = new Term(hiddenInputClass, input.getName(), hiddenInput.getValue());
                    inputTerm.setAttributeValue("name", hiddenInput.getName());
                }
                else if (input.getInputType().equals(INPUTElement.CHECKBOX))
                {
                    CheckboxINPUTElementGui checkboxInput = (CheckboxINPUTElementGui) input;
                    inputTerm = new Term(checkboxInputClass, checkboxInput.getLabel(),
                        checkboxInput.getValue());
                    inputTerm.setAttributeValue("name", checkboxInput.getName());
                    Domain checkboxDomain = new Domain(
                        ApplicationUtilities.getResourceString("ontology.domain.choice"), "choice");
                    for (int o = 0; o < checkboxInput.getOptionsCount(); o++)
                    {
                        CheckboxINPUTElementOptionGui option = checkboxInput.getOption(o);
                        Term optionTerm = new Term(option.getLabel(), option.getValue());
                        optionTerm.addAttribute(new Attribute("checked", new Boolean(option
                            .isChecked())));
                        optionTerm.addAttribute(new Attribute("defaultChecked", new Boolean(option
                            .isDefaultChecked())));
                        checkboxDomain.addEntry(new DomainEntry(optionTerm));
                    }
                    inputTerm.setDomain(checkboxDomain);
                }
                else if (input.getInputType().equals(INPUTElement.RADIO))
                {
                    RadioINPUTElementGui radioInput = (RadioINPUTElementGui) input;
                    inputTerm = new Term(radioInputClass, radioInput.getLabel(),
                        radioInput.getValue());
                    inputTerm.setAttributeValue("name", radioInput.getName());
                    Domain radioDomain = new Domain(
                        ApplicationUtilities.getResourceString("ontology.domain.choice"), "choice");
                    for (int o = 0; o < radioInput.getOptionsCount(); o++)
                    {
                        RadioINPUTElementOptionGui option = radioInput.getOption(o);
                        Term optionTerm = new Term(option.getLabel(), option.getValue());
                        optionTerm.addAttribute(new Attribute("checked", new Boolean(option
                            .isChecked())));
                        optionTerm.addAttribute(new Attribute("defaultChecked", new Boolean(option
                            .isDefaultChecked())));
                        radioDomain.addEntry(new DomainEntry(optionTerm));
                    }
                    inputTerm.setDomain(radioDomain);
                }
                else if (input.getInputType().equals(INPUTElement.SELECT))
                {
                    SELECTElementGui selectInput = (SELECTElementGui) input;
                    inputTerm = new Term(selectInputClass, selectInput.getLabel(),
                        selectInput.getValue());
                    inputTerm.setAttributeValue("name", selectInput.getName());
                    Domain selectDomain = new Domain(
                        ApplicationUtilities.getResourceString("ontology.domain.choice"), "choice");
                    for (int o = 0; o < selectInput.getOptionsCount(); o++)
                    {
                        OPTIONElementGui option = selectInput.getOption(o);
                        Term optionTerm = new Term(option.getLabel(), option.getValue());
                        optionTerm.addAttribute(new Attribute("selected", new Boolean(option
                            .isSelected())));
                        optionTerm.addAttribute(new Attribute("defaultSelected", new Boolean(option
                            .isDefaultSelected())));
                        selectDomain.addEntry(new DomainEntry(optionTerm));
                    }
                    inputTerm.setDomain(selectDomain);
                }
                else if (input.getInputType().equals(INPUTElement.TEXTAREA))
                {
                    TEXTAREAElementGui textareaInput = (TEXTAREAElementGui) input;
                    inputTerm = new Term(textareaInputClass, textareaInput.getLabel(),
                        textareaInput.getValue());
                    inputTerm.setAttributeValue("name", textareaInput.getName());
                    inputTerm.setAttributeValue("defaultValue", textareaInput.getDefaultValue());
                    if (textareaInput.getRows() != -1)
                        inputTerm.setAttributeValue("rows", new Integer(textareaInput.getRows()));
                    if (textareaInput.getCols() != -1)
                        inputTerm.setAttributeValue("cols", new Integer(textareaInput.getCols()));
                    inputTerm
                        .setAttributeValue("readOnly", new Boolean(textareaInput.isReadOnly()));
                }
                else if (input.getInputType().equals(INPUTElement.BUTTON))
                {
                    ButtonINPUTElementGui buttonInput = (ButtonINPUTElementGui) input;
                    inputTerm = new Term(buttonInputClass, buttonInput.getValue());
                    inputTerm.setAttributeValue("name", buttonInput.getName());
                }
                else if (input.getInputType().equals(INPUTElement.SUBMIT))
                {
                    SubmitINPUTElementGui submitInput = (SubmitINPUTElementGui) input;
                    inputTerm = new Term(submitInputClass, submitInput.getValue());
                    inputTerm.setAttributeValue("name", submitInput.getName());
                }
                else if (input.getInputType().equals(INPUTElement.RESET))
                {
                    ResetINPUTElementGui resetInput = (ResetINPUTElementGui) input;
                    inputTerm = new Term(resetInputClass, resetInput.getValue());
                    inputTerm.setAttributeValue("name", resetInput.getName());
                }
                else if (input.getInputType().equals(INPUTElement.IMAGE))
                {
                    ImageINPUTElementGui imageInput = (ImageINPUTElementGui) input;
                    inputTerm = new Term(imageInputClass, imageInput.getAlt());
                    inputTerm.setAttributeValue("name", imageInput.getName());
                    inputTerm.setAttributeValue("src", imageInput.getSrc());
                }
                if (inputTerm != null)
                {
                    inputTerm.setAttributeValue("disabled", new Boolean(input.isDisabled()));
                    Hashtable<?, ?> events = input.getEvents();
                    for (Enumeration<?> e = events.keys(); e.hasMoreElements();)
                    {
                        String event = (String) e.nextElement();
                        String script = (String) events.get(event);
                        inputTerm.addAxiom(new Axiom(event, script));
                    }
                    if (prevInputTerm != null)
                    {
                        prevInputTerm.setSucceed(inputTerm);
                        inputTerm.setPrecede(prevInputTerm);
                    }
                    prevInputTerm = inputTerm;
                    formTerm.addTerm(inputTerm);
                }
            }
        }

        return ontology;
    }

    public String getXMLRepresentationAsString() throws IOException
    {
        org.jdom.Element ontologyElement = ontologyCore.getXMLRepresentation();
        DocType ontologyDocType = new DocType("ontology");
        org.jdom.Document ontologyDocument = new org.jdom.Document(ontologyElement, ontologyDocType);

        StringOutputStream xmlRepresentation = new StringOutputStream();
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(xmlRepresentation));
        XMLOutputter fmt = new XMLOutputter("    ", true);
        fmt.output(ontologyDocument, out);
        out.close();
        return xmlRepresentation.toString();
    }
    
    public NodeHyperTree getHyperTreeNode(boolean showClasses, boolean showRelations,
        boolean showProperties)
    {
        NodeHyperTree root = new NodeHyperTree(this, NodeHyperTree.CLASS);

        if (showClasses && !ontologyCore.isLight())
        {
            NodeHyperTree classesNode = new NodeHyperTree(
                PropertiesHandler.getResourceString("ontology.classes"), NodeHyperTree.CLASS);
            root.add(classesNode);
            for (Iterator<Object> i = ontologyCore.getClasses(false).iterator(); i.hasNext();)
            {
                classesNode.add(((OntologyClassGui) i.next()).getHyperTreeNode(showProperties));
            }
        }

        NodeHyperTree termsNode = new NodeHyperTree(
            PropertiesHandler.getResourceString("ontology.terms"), NodeHyperTree.TERM);
        root.add(termsNode);
        for (Iterator<Term> i = ontologyCore.getTerms(false).iterator(); i.hasNext();)
        {
            TermGui termGui = new TermGui((Term) i.next());
            termsNode.add(termGui.getHyperTreeNode(showRelations, showClasses,
                showProperties));
        }

        return root;
    }

    public JGraph getGraph()
    {
        JGraph graph = new JGraph(new DefaultGraphModel());
        graph.setEditable(false);
        ArrayList<DefaultGraphCell> cells = new ArrayList<DefaultGraphCell>();
        // ConnectionSet for the Insert method
        ConnectionSet cs = new ConnectionSet();
        // Hashtable for Attributes (Vertex to Map)
        Hashtable<DefaultGraphCell, Map<?, ?>> attributes = new Hashtable<DefaultGraphCell, Map<?, ?>>();

        DefaultGraphCell vertex = new DefaultGraphCell(ontologyCore.getName());
        cells.add(vertex);
        Map<?, ?> map = GraphUtilities.createDefaultAttributes();
        GraphConstants.setIcon(map, ApplicationUtilities.getImage("ontology.gif"));
        attributes.put(vertex, map);

        if (!ontologyCore.getTerms(false).isEmpty())
        {
            DefaultPort toChildPort = new OrderedDefaultPort("toChild");
            vertex.add(toChildPort);
            for (Iterator<Term> i = ontologyCore.getTerms(false).iterator(); i.hasNext();)
            {
                TermGui termGui = new TermGui((Term) i.next());
                termGui.buildGraphHierarchy(toChildPort, cells, attributes, cs);
            }
            if (GraphUtilities.getShowPrecedenceLinks())
            {
                for (Iterator<Term> i = ontologyCore.getTerms(false).iterator(); i.hasNext();)
                {
                    TermGui termGui = new TermGui((Term) i.next());
                    termGui.buildPrecedenceRelationships(cells, attributes, cs);
                }
            }
        }

        // Insert the cells (View stores attributes)
        graph.getModel().insert(cells.toArray(), cs, null, attributes);
        GraphUtilities.alignHierarchy(graph, SwingConstants.LEFT, 10, 10);
        return graph;
    }

    public DefaultGraphCell addToGraph(ArrayList<DefaultGraphCell> cells,
        Hashtable<DefaultGraphCell, Map<?, ?>> attributes, ConnectionSet cs)
    {
        DefaultGraphCell vertex = new DefaultGraphCell(ontologyCore.getName());
        cells.add(vertex);
        Map<?, ?> map = GraphUtilities.createDefaultAttributes();
        GraphConstants.setIcon(map, ApplicationUtilities.getImage("ontology.gif"));
        attributes.put(vertex, map);

        if (!ontologyCore.getTerms(false).isEmpty())
        {
            DefaultPort toChildPort = new DefaultPort("toChild");
            vertex.add(toChildPort);
            for (Iterator<Term> i = ontologyCore.getTerms(false).iterator(); i.hasNext();)
            {
                TermGui termGui = new TermGui((Term) i.next());
                termGui.buildGraphHierarchy(toChildPort, cells, attributes, cs);
            }
            if (GraphUtilities.getShowPrecedenceLinks())
            {
                for (Iterator<Term> i = ontologyCore.getTerms(false).iterator(); i.hasNext();)
                {
                    TermGui termGui = new TermGui((Term) i.next());
                    termGui.buildPrecedenceRelationships(cells, attributes, cs);
                }
            }
        }

        return vertex;
    }
    
    public JTree getClassesHierarchy()
    {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(
            PropertiesHandler.getResourceString("ontology.classes"));
        for (Iterator<Object> i = ontologyCore.getClasses(false).iterator(); i.hasNext();)
        {
            OntologyClass c = (OntologyClass) i.next();
            DefaultMutableTreeNode classNode = new DefaultMutableTreeNode(c);
            getClassesHierarchyRec(c, classNode);
        }
        JTree tree = new JTree(root);
        tree.setCellRenderer(new OntologyTreeRenderer());
        return tree;
    }
    
    public void getClassesHierarchyRec(OntologyClass c, DefaultMutableTreeNode root)
    {
        for (int i = 0; i < c.getSubClassesCount(); i++)
        {
            OntologyClass sc = c.getSubClass(i);
            if (!(sc instanceof Term))
            {
                DefaultMutableTreeNode subNode = new DefaultMutableTreeNode(sc);
                root.add(subNode);
                getClassesHierarchyRec(sc, subNode);
            }
        }
    }
    
    public JTree getTermsHierarchy()
    {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(
            PropertiesHandler.getResourceString("ontology.terms"));
        for (Iterator<Term> i = ontologyCore.getTerms(false).iterator(); i.hasNext();)
        {
            Term t = (Term) i.next();
            DefaultMutableTreeNode termNode = new DefaultMutableTreeNode(t);
            root.add(termNode);
            getTermsHierarchyRec(t, termNode);
        }
        JTree tree = new JTree(root);
        tree.setCellRenderer(new OntologyTreeRenderer());
        return tree;
    }
    
    protected void getTermsHierarchyRec(Term t, DefaultMutableTreeNode root)
    {
        for (int i = 0; i < t.getTermsCount(); i++)
        {
            Term st = t.getTerm(i);
            DefaultMutableTreeNode subNode = new DefaultMutableTreeNode(st);
            root.add(subNode);
            getTermsHierarchyRec(st, subNode);
        }
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
                PropertiesHandler.getResourceString("ontology.name"), ontologyCore.getName()
            },
            {
                PropertiesHandler.getResourceString("ontology.title"), ontologyCore.getTitle()
            },
            {
                PropertiesHandler.getResourceString("ontology.site"),
                ontologyCore.getSiteURL() != null ? ontologyCore.getSiteURL().toExternalForm() : null
            },
            {
                PropertiesHandler.getResourceString("ontology.terms"), new Integer(ontologyCore.getTerms(false).size())
            }
        };
        JTable properties = new JTable(new PropertiesTableModel(columnNames, 4, data)
        {
            private static final long serialVersionUID = 1L;

            public boolean isCellEditable(int row, int col)
            {
                return col == 1 && (row == 0 || row == 1 || row == 2);
            }
        });
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
                    if (!ontologyCore.getName().equals(label))
                        setName(label);
                    break;
                case 1:
                    if (!ontologyCore.getTitle().equals(label))
                        setTitle(label);
                    break;
                case 2:
                    if (!ontologyCore.getSiteURL().toExternalForm().equals(label))
                        ontologyCore.setSiteURL(label);
                }
            }
        });
        return properties;
    }
    
    public DefaultMutableTreeNode getTreeBranch()
    {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(this);

        if (!ontologyCore.isLight())
        {
            DefaultMutableTreeNode classesNode = new DefaultMutableTreeNode(
                PropertiesHandler.getResourceString("ontology.classes"));
            root.add(classesNode);
            for (Iterator<Object> i = ontologyCore.getClasses(false).iterator(); i.hasNext();)
            {
                classesNode.add(OntologyObjectGuiFactory.getOntologyObjectGui((OntologyClass) i.next()).getTreeBranch());
            }
        }

        DefaultMutableTreeNode termsNode = new DefaultMutableTreeNode(
            PropertiesHandler.getResourceString("ontology.terms"));
        root.add(termsNode);
        for (Iterator<Term> i = ontologyCore.getTerms(false).iterator(); i.hasNext();)
        {
            TermGui termGui = new TermGui((Term) i.next());
            termsNode.add(termGui.getTreeBranch());
        }

        return root;
    }
    
    public void setOntology(Ontology ontology)
    {
        ontologyCore = ontology;
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
		OntologyGui other = (OntologyGui) obj;
		if (ontologyCore == null)
		{
			if (other.ontologyCore != null)
				return false;
		}
		else if (!ontologyCore.equals(other.ontologyCore))
			return false;
		return true;
	}

}