package ac.technion.iem.ontobuilder.gui.utils.thesaurus;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.help.CSH;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ToolTipManager;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import ac.technion.iem.ontobuilder.core.resources.OntoBuilderResources;
import ac.technion.iem.ontobuilder.core.thesaurus.Thesaurus;
import ac.technion.iem.ontobuilder.core.thesaurus.ThesaurusException;
import ac.technion.iem.ontobuilder.core.thesaurus.ThesaurusWord;
import ac.technion.iem.ontobuilder.core.thesaurus.event.ThesaurusModelEvent;
import ac.technion.iem.ontobuilder.core.thesaurus.event.ThesaurusModelListener;
import ac.technion.iem.ontobuilder.core.thesaurus.event.ThesaurusSelectionEvent;
import ac.technion.iem.ontobuilder.core.thesaurus.event.ThesaurusSelectionListener;
import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.application.action.Actions;
import ac.technion.iem.ontobuilder.gui.elements.PopupListener;
import ac.technion.iem.ontobuilder.gui.elements.PopupTrigger;

/**
 * <p>Title: ThesaurusGui</p>
 * <p>Description: Implements the methods of the Thesaurus used by the GUI</p>
 * Extends {@link JPanel} 
 */
public class ThesaurusGui extends JPanel
{
    private static final long serialVersionUID = -6535717369370003541L;

    protected JPopupMenu popMenu;
    protected ThesaurusWord currentWord;
    protected ThesaurusWord currentParentWord;

    protected Actions actions;
    protected Thesaurus model;
    protected ArrayList<ThesaurusSelectionListener> selectionListeners;
    protected ArrayList<ThesaurusModelListener> modelListeners;

    protected JTree thesaurusTree;
    protected ThesaurusTreeCellEditor thesaurusCellEditor;

    /**
     * Constructs a Thesaurus
     * 
     * @param file the file with the ThesaurusModel
     * @throws ThesaurusException
     */
    public ThesaurusGui(File file) throws ThesaurusException
    {
        super(new BorderLayout());
        model = new Thesaurus(file);
        init();
    }

    /**
     * Constructs a Thesaurus
     * 
     * @param file the name of the file with the ThesaurusModel
     * @throws ThesaurusException
     */
    public ThesaurusGui(String file) throws ThesaurusException
    {
        super(new BorderLayout());
        model = new Thesaurus(file);
        init();
    }

    /**
     * Initialize the Thesaurus
     */
    protected void init()
    {
        selectionListeners = new ArrayList<ThesaurusSelectionListener>();
        modelListeners = new ArrayList<ThesaurusModelListener>();
        initializeActions();
        createPopupMenu();

        // Initialize the model
        model.addThesaurusModelListener(new ThesaurusModelListener()
        {
            public void wordChanged(ThesaurusModelEvent e)
            {
                fireWordChangedEvent(e);
            }

            public void wordRenamed(ThesaurusModelEvent e)
            {
                renameWordInTree(e.getWord(), e.getOldName());
                fireWordRenamedEvent(e);
            }

            public void wordAdded(ThesaurusModelEvent e)
            {
                addWordToTree(e.getWord());
                fireWordAddedEvent(e);
            }

            public void wordDeleted(ThesaurusModelEvent e)
            {
                deleteWordFromTree(e.getWord());
                fireWordDeletedEvent(e);
            }

            public void synonymAdded(ThesaurusModelEvent e)
            {
                addSynonymToTree(e.getWord(), e.getSynonym());
                fireSynonymAddedEvent(e);
            }

            public void synonymDeleted(ThesaurusModelEvent e)
            {
                deleteSynonymFromTree(e.getWord(), e.getSynonym());
                fireSynonymDeletedEvent(e);
            }

            public void homonymAdded(ThesaurusModelEvent e)
            {
                addHomonymToTree(e.getWord(), e.getHomonym());
                fireHomonymAddedEvent(e);
            }

            public void homonymDeleted(ThesaurusModelEvent e)
            {
                deleteHomonymFromTree(e.getWord(), e.getHomonym());
                fireHomonymDeletedEvent(e);
            }

            public void update(ThesaurusModelEvent e)
            {
                updateTree();
                fireUpdateEvent(e);
            }
        });

        // Initialize the view
        thesaurusTree = new JTree(new ThesaurusTreeModel(model));
        add(BorderLayout.CENTER, thesaurusTree);
        thesaurusTree.getSelectionModel()
            .setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        ToolTipManager.sharedInstance().registerComponent(thesaurusTree);
        thesaurusTree.setCellRenderer(new ThesaurusTreeRenderer());
        thesaurusTree.putClientProperty("JTree.lineStyle", "Angled");
        PopupTrigger pt = new PopupTrigger();
        pt.addPopupListener(new PopupListener()
        {
            public void popup(MouseEvent e)
            {
                JTree thesaurusTree = (JTree) e.getSource();
                TreePath path = thesaurusTree.getPathForLocation(e.getX(), e.getY());
                if (path == null)
                    return;
                thesaurusTree.setSelectionPath(path);
                showPopupForObject((DefaultMutableTreeNode) path.getLastPathComponent(), e);
            }
        });
        thesaurusTree.addMouseListener(pt);
        thesaurusTree.addTreeSelectionListener(new TreeSelectionListener()
        {
            public void valueChanged(TreeSelectionEvent e)
            {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) ((JTree) e.getSource())
                    .getLastSelectedPathComponent();
                if (node != null)
                {
                    Object object = node.getUserObject();
                    fireThesaurusSelectionEvent(object);
                }
            }
        });
        thesaurusTree.setEditable(true);
        thesaurusCellEditor = new ThesaurusTreeCellEditor(thesaurusTree);
        thesaurusTree.setCellEditor(new DefaultTreeCellEditor(thesaurusTree,
            (DefaultTreeCellRenderer) thesaurusTree.getCellRenderer(), thesaurusCellEditor));
        thesaurusTree.getCellEditor().addCellEditorListener(new CellEditorListener()
        {
            public void editingStopped(ChangeEvent e)
            {
                ThesaurusTreeCellEditor editor = (ThesaurusTreeCellEditor) e.getSource();
                ThesaurusWord word = editor.getWordBeingEdited();
                if (word != null)
                    renameWord(word.getWord(), editor.getChangedValue());
            }

            public void editingCanceled(ChangeEvent e)
            {
            }
        });

    }

    public void addThesaurusSelectionListener(ThesaurusSelectionListener l)
    {
        selectionListeners.add(l);
    }

    public void removeThesaurusSelectionListener(ThesaurusSelectionListener l)
    {
        selectionListeners.remove(l);
    }

    protected void fireThesaurusSelectionEvent(Object object)
    {
        for (Iterator<ThesaurusSelectionListener> i = selectionListeners.iterator(); i.hasNext();)
        {
            ThesaurusSelectionListener l = i.next();
            l.valueChanged(new ThesaurusSelectionEvent(this, object));
        }
    }

    public void addThesaurusModelListener(ThesaurusModelListener l)
    {
        modelListeners.add(l);
    }

    public void removeThesaurusModelListener(ThesaurusModelListener l)
    {
        modelListeners.remove(l);
    }

    protected void fireWordChangedEvent(ThesaurusModelEvent e)
    {
        for (Iterator<ThesaurusModelListener> i = modelListeners.iterator(); i.hasNext();)
        {
            ThesaurusModelListener l = i.next();
            l.wordChanged(new ThesaurusModelEvent(this, e.getWord()));
        }
    }

    protected void fireWordRenamedEvent(ThesaurusModelEvent e)
    {
        for (Iterator<ThesaurusModelListener> i = modelListeners.iterator(); i.hasNext();)
        {
            ThesaurusModelListener l = i.next();
            l.wordRenamed(new ThesaurusModelEvent(this, e.getWord(), e.getOldName()));
        }
    }

    protected void fireWordAddedEvent(ThesaurusModelEvent e)
    {
        for (Iterator<ThesaurusModelListener> i = modelListeners.iterator(); i.hasNext();)
        {
            ThesaurusModelListener l = i.next();
            l.wordAdded(new ThesaurusModelEvent(this, e.getWord(), e.getSynonym(), e.getHomonym()));
        }
    }

    protected void fireWordDeletedEvent(ThesaurusModelEvent e)
    {
        for (Iterator<ThesaurusModelListener> i = modelListeners.iterator(); i.hasNext();)
        {
            ThesaurusModelListener l = i.next();
            l.wordDeleted(new ThesaurusModelEvent(this, e.getWord(), e.getSynonym(), e.getHomonym()));
        }
    }

    protected void fireSynonymAddedEvent(ThesaurusModelEvent e)
    {
        for (Iterator<ThesaurusModelListener> i = modelListeners.iterator(); i.hasNext();)
        {
            ThesaurusModelListener l = i.next();
            l.synonymAdded(new ThesaurusModelEvent(this, e.getWord(), e.getSynonym(), e
                .getHomonym()));
        }
    }

    protected void fireSynonymDeletedEvent(ThesaurusModelEvent e)
    {
        for (Iterator<ThesaurusModelListener> i = modelListeners.iterator(); i.hasNext();)
        {
            ThesaurusModelListener l = i.next();
            l.synonymDeleted(new ThesaurusModelEvent(this, e.getWord(), e.getSynonym(), e
                .getHomonym()));
        }
    }

    protected void fireHomonymAddedEvent(ThesaurusModelEvent e)
    {
        for (Iterator<ThesaurusModelListener> i = modelListeners.iterator(); i.hasNext();)
        {
            ThesaurusModelListener l = i.next();
            l.homonymAdded(new ThesaurusModelEvent(this, e.getWord(), e.getSynonym(), e
                .getHomonym()));
        }
    }

    protected void fireHomonymDeletedEvent(ThesaurusModelEvent e)
    {
        for (Iterator<ThesaurusModelListener> i = modelListeners.iterator(); i.hasNext();)
        {
            ThesaurusModelListener l = i.next();
            l.homonymDeleted(new ThesaurusModelEvent(this, e.getWord(), e.getSynonym(), e
                .getHomonym()));
        }
    }

    protected void fireUpdateEvent(ThesaurusModelEvent e)
    {
        for (Iterator<ThesaurusModelListener> i = modelListeners.iterator(); i.hasNext();)
        {
            ThesaurusModelListener l = i.next();
            l.update(new ThesaurusModelEvent(this, e.getWord(), e.getSynonym(), e.getHomonym()));
        }
    }

    /**
     * Initializes all the relevant actions (add word, remove word, add synonym, remove synonym add
     * homonym, remove homonym, rename word)
     */
    protected void initializeActions()
    {
        actions = new Actions();

        // Add word
        Action action = new AbstractAction(
            ApplicationUtilities.getResourceString("action.addWord"),
            ApplicationUtilities.getImage("addword.gif"))
        {
            private static final long serialVersionUID = -1686408951546296617L;

            public void actionPerformed(ActionEvent e)
            {
                commandAddWord();
            }
        };
        action.putValue(Action.LONG_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.addWord.longDescription"));
        action.putValue(Action.SHORT_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.addWord.shortDescription"));
        action.putValue(
            Action.MNEMONIC_KEY,
            new Integer(KeyStroke.getKeyStroke(
                ApplicationUtilities.getResourceString("action.addWord.mnemonic")).getKeyCode()));
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ApplicationUtilities
            .getResourceString("action.addWord.accelerator")));
        actions.addAction("addWord", action);

        // Remove word
        action = new AbstractAction(ApplicationUtilities.getResourceString("action.removeWord"),
            ApplicationUtilities.getImage("removeword.gif"))
        {
            private static final long serialVersionUID = 1053323566463085237L;

            public void actionPerformed(ActionEvent e)
            {
                commandRemoveWord();
            }
        };
        action.putValue(Action.LONG_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.removeWord.longDescription"));
        action.putValue(Action.SHORT_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.removeWord.shortDescription"));
        action
            .putValue(
                Action.MNEMONIC_KEY,
                new Integer(KeyStroke.getKeyStroke(
                    ApplicationUtilities.getResourceString("action.removeWord.mnemonic"))
                    .getKeyCode()));
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ApplicationUtilities
            .getResourceString("action.removeWord.accelerator")));
        actions.addAction("removeWord", action);

        // Add synonym
        action = new AbstractAction(ApplicationUtilities.getResourceString("action.addSynonym"),
            ApplicationUtilities.getImage("addsynonym.gif"))
        {
            private static final long serialVersionUID = 2053401815527915359L;

            public void actionPerformed(ActionEvent e)
            {
                commandAddSynonym();
            }
        };
        action.putValue(Action.LONG_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.addSynonym.longDescription"));
        action.putValue(Action.SHORT_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.addSynonym.shortDescription"));
        action
            .putValue(
                Action.MNEMONIC_KEY,
                new Integer(KeyStroke.getKeyStroke(
                    ApplicationUtilities.getResourceString("action.addSynonym.mnemonic"))
                    .getKeyCode()));
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ApplicationUtilities
            .getResourceString("action.addSynonym.accelerator")));
        actions.addAction("addSynonym", action);

        // Remove synonym
        action = new AbstractAction(ApplicationUtilities.getResourceString("action.removeSynonym"),
            ApplicationUtilities.getImage("removesynonym.gif"))
        {
            private static final long serialVersionUID = 716498071013860178L;

            public void actionPerformed(ActionEvent e)
            {
                commandRemoveSynonym();
            }
        };
        action.putValue(Action.LONG_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.removeSynonym.longDescription"));
        action.putValue(Action.SHORT_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.removeSynonym.shortDescription"));
        action.putValue(
            Action.MNEMONIC_KEY,
            new Integer(KeyStroke.getKeyStroke(
                ApplicationUtilities.getResourceString("action.removeSynonym.mnemonic"))
                .getKeyCode()));
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ApplicationUtilities
            .getResourceString("action.removeSynonym.accelerator")));
        actions.addAction("removeSynonym", action);

        // Add homonym
        action = new AbstractAction(ApplicationUtilities.getResourceString("action.addHomonym"),
            ApplicationUtilities.getImage("addhomonym.gif"))
        {
            private static final long serialVersionUID = -8164217283475144886L;

            public void actionPerformed(ActionEvent e)
            {
                commandAddHomonym();
            }
        };
        action.putValue(Action.LONG_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.addHomonym.longDescription"));
        action.putValue(Action.SHORT_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.addHomonym.shortDescription"));
        action
            .putValue(
                Action.MNEMONIC_KEY,
                new Integer(KeyStroke.getKeyStroke(
                    ApplicationUtilities.getResourceString("action.addHomonym.mnemonic"))
                    .getKeyCode()));
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ApplicationUtilities
            .getResourceString("action.addHomonym.accelerator")));
        actions.addAction("addHomonym", action);

        // Remove homonym
        action = new AbstractAction(ApplicationUtilities.getResourceString("action.removeHomonym"),
            ApplicationUtilities.getImage("removehomonym.gif"))
        {
            private static final long serialVersionUID = -3634980176397459346L;

            public void actionPerformed(ActionEvent e)
            {
                commandRemoveHomonym();
            }
        };
        action.putValue(Action.LONG_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.removeHomonym.longDescription"));
        action.putValue(Action.SHORT_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.removeHomonym.shortDescription"));
        action.putValue(
            Action.MNEMONIC_KEY,
            new Integer(KeyStroke.getKeyStroke(
                ApplicationUtilities.getResourceString("action.removeHomonym.mnemonic"))
                .getKeyCode()));
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ApplicationUtilities
            .getResourceString("action.removeHomonym.accelerator")));
        actions.addAction("removeHomonym", action);

        // Rename word
        action = new AbstractAction(ApplicationUtilities.getResourceString("action.renameWord"),
            ApplicationUtilities.getImage("renameword.gif"))
        {
            private static final long serialVersionUID = 6322275609472643566L;

            public void actionPerformed(ActionEvent e)
            {
                commandRenameWord();
            }
        };
        action.putValue(Action.LONG_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.renameWord.longDescription"));
        action.putValue(Action.SHORT_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.renameWord.shortDescription"));
        action
            .putValue(
                Action.MNEMONIC_KEY,
                new Integer(KeyStroke.getKeyStroke(
                    ApplicationUtilities.getResourceString("action.renameWord.mnemonic"))
                    .getKeyCode()));
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ApplicationUtilities
            .getResourceString("action.renameWord.accelerator")));
        actions.addAction("renameWord", action);
    }

    /**
     * Creates a popup menu
     */
    protected void createPopupMenu()
    {
        // Sets the Popup Menu
        popMenu = new JPopupMenu(ApplicationUtilities.getResourceString("thesaurus"));
        addMenuItem(popMenu, actions.getAction("addWord"));
        addMenuItem(popMenu, actions.getAction("removeWord"));
        popMenu.addSeparator();
        addMenuItem(popMenu, actions.getAction("addSynonym"));
        addMenuItem(popMenu, actions.getAction("removeSynonym"));
        popMenu.addSeparator();
        addMenuItem(popMenu, actions.getAction("addHomonym"));
        addMenuItem(popMenu, actions.getAction("removeHomonym"));
        popMenu.addSeparator();
        addMenuItem(popMenu, actions.getAction("renameWord"));
    }

    protected JMenuItem addMenuItem(JPopupMenu menu, Action action)
    {
        JMenuItem menuItem = menu.add(action);
        menuItem.setAccelerator((KeyStroke) action.getValue(Action.ACCELERATOR_KEY));
        if (action.getValue("helpID") != null)
            CSH.setHelpIDString(menuItem, (String) action.getValue("helpID"));
        return menuItem;
    }

    /**
     * Shows a popup for an object
     */
    protected void showPopupForObject(DefaultMutableTreeNode node, MouseEvent e)
    {
        Set<?> actionNames = actions.getActions();
        for (Iterator<?> i = actionNames.iterator(); i.hasNext();)
            actions.getAction((String) i.next()).setEnabled(false);

        if (node == null)
            return;
        Object object = node.getUserObject();
        if (object instanceof String)
        {
            String s = (String) object;
            if (s.equals(ApplicationUtilities.getResourceString("thesaurus")))
            {
                actions.getAction("addWord").setEnabled(true);
                currentWord = null;
                currentParentWord = null;
            }
            else
            {
                if (s.equals(ApplicationUtilities.getResourceString("thesaurus.synonyms")))
                    actions.getAction("addSynonym").setEnabled(true);
                else if (s.equals(ApplicationUtilities.getResourceString("thesaurus.homonyms")))
                    actions.getAction("addHomonym").setEnabled(true);
                currentWord = (ThesaurusWord) ((DefaultMutableTreeNode) node.getParent())
                    .getUserObject();
                currentParentWord = null;
            }
        }
        else
        // is a ThesaurusWord
        {
            actions.getAction("renameWord").setEnabled(true);
            Object parent = ((DefaultMutableTreeNode) node.getParent()).getUserObject();
            currentWord = (ThesaurusWord) object;
            if (parent instanceof String)
            {
                String s = (String) parent;
                if (s.equals(ApplicationUtilities.getResourceString("thesaurus")))
                {
                    actions.getAction("removeWord").setEnabled(true);
                    currentParentWord = null;
                }
                else
                {
                    if (s.equals(ApplicationUtilities.getResourceString("thesaurus.synonyms")))
                        actions.getAction("removeSynonym").setEnabled(true);
                    else if (s.equals(ApplicationUtilities.getResourceString("thesaurus.homonyms")))
                        actions.getAction("removeHomonym").setEnabled(true);
                    currentParentWord = (ThesaurusWord) ((DefaultMutableTreeNode) node.getParent()
                        .getParent()).getUserObject();
                }
            }
        }
        popMenu.show(e.getComponent(), e.getX(), e.getY());
    }

    /**
     * Renames a word
     * 
     * @param word the word to rename
     * @param newName the word to rename with
     */
    public void renameWord(String word, String newName)
    {
        model.renameWord(word, newName);
    }

    /**
     * Adds a word
     * 
     * @param word the new word
     */
    public void addWord(String word)
    {
        model.addWord(word);
    }

    /**
     * Deletes a word
     * 
     * @param word the word to delete
     */
    public void deleteWord(String word)
    {
        model.deleteWord(word);
    }

    /**
     * Returns the number of words
     * 
     * @return the word count
     */
    public int getWordCount()
    {
        return model.getWordCount();
    }

    /**
     * Adds a synonym
     * 
     * @param theWord the word to add a synonym to
     * @param theSynonym the synonym of the word
     */
    public void addSynonym(String theWord, String theSynonym)
    {
        model.addSynonym(theWord, theSynonym);
    }

    /**
     * Deletes a synonym
     * 
     * @param theWord the word to delete the synonym from
     * @param theSynonym the synonym to delete
     */
    public void deleteSynonym(String theWord, String theSynonym)
    {
        model.deleteSynonym(theWord, theSynonym);
    }

    /**
     * Adds a homonym
     * 
     * @param theWord the word to add a homonym to
     * @param theHomonym the homonym of the word
     */
    public void addHomonym(String theWord, String theHomonym)
    {
        model.addHomonym(theWord, theHomonym);
    }

    /**
     * Deletes a homonym
     * 
     * @param theWord the word to delete the homonym from
     * @param theHomonym the homonym to delete
     */
    public void deleteHomonym(String theWord, String theHomonym)
    {
        model.deleteHomonym(theWord, theHomonym);
    }

    /**
     * Get all the synonyms of a word
     * 
     * @param word the word to get synonyms of
     * @return a vector of all the synonyms
     */
    public Vector<String> getSynonyms(String word)
    {
        return model.getSynonyms(word);
    }

    /**
     * Get all the homonyms of a word
     * 
     * @param word the word to get homonyms of
     * @return a vector of all the v
     */
    public Vector<String> getHomonyms(String word)
    {
        return model.getHomonyms(word);
    }

    /**
     * Check if a string is a synonym of a word
     * 
     * @param word the word to check in
     * @param synonym the synonym to check
     * @return true if it is a synonym
     */
    public boolean isSynonym(String word, String synonym)
    {
        return model.isSynonym(word, synonym);
    }

    /**
     * Check if a string is a homonym of a word
     * 
     * @param word the word to check in
     * @param homonym the homonym to check
     * @return true if it is a homonym
     */
    public boolean isHomonym(String word, String homonym)
    {
        return model.isHomonym(word, homonym);
    }

    /**
     * Save the current Thesaurus into a file
     * 
     * @param file the name of the file
     * @throws ThesaurusException
     */
    public void saveThesaurus(String file) throws ThesaurusException
    {
        model.saveThesaurus(file);
    }

    /**
     * Action to perform upon an AddWord command
     */
    protected void commandAddWord()
    {
        String word = JOptionPane.showInputDialog(ApplicationUtilities
            .getResourceString("thesaurus.addWord"));
        if (word != null && word.length() > 0)
            addWord(word);
    }

    /**
     * Action to perform upon an AddSynonym command
     */
    protected void commandAddSynonym()
    {
        if (currentWord == null)
            return;
        String synonym = JOptionPane.showInputDialog(ApplicationUtilities
            .getResourceString("thesaurus.addSynonym"));
        if (synonym != null && synonym.length() > 0)
            addSynonym(currentWord.getWord(), synonym);
    }

    /**
     * Action to perform upon an AddHomonym command
     */
    protected void commandAddHomonym()
    {
        if (currentWord == null)
            return;
        String homonym = JOptionPane.showInputDialog(ApplicationUtilities
            .getResourceString("thesaurus.addHomonym"));
        if (homonym != null && homonym.length() > 0)
            addHomonym(currentWord.getWord(), homonym);
    }

    /**
     * Action to perform upon an RemoveWord command
     */
    protected void commandRemoveWord()
    {
        if (currentWord == null)
            return;
        deleteWord(currentWord.getWord());
    }

    /**
     * Action to perform upon an RemoveSynonym command
     */
    protected void commandRemoveSynonym()
    {
        if (currentWord == null || currentParentWord == null)
            return;
        deleteSynonym(currentParentWord.getWord(), currentWord.getWord());
    }

    /**
     * Action to perform upon an RemoveHomonym command
     */
    protected void commandRemoveHomonym()
    {
        if (currentWord == null || currentParentWord == null)
            return;
        deleteHomonym(currentParentWord.getWord(), currentWord.getWord());
    }

    /**
     * Action to perform upon an RenameWord command
     */
    protected void commandRenameWord()
    {
        thesaurusTree.startEditingAtPath(thesaurusTree.getSelectionModel().getSelectionPath());
        thesaurusCellEditor.getEditorComponent().requestFocus();
    }

    /**
     * Renames a word in the ThesaurusTreeModel
     * 
     * @param word the new ThesaurusWord
     * @param oldName the word to replace
     */
    protected void renameWordInTree(ThesaurusWord word, String oldName)
    {
        ThesaurusTreeModel treeModel = (ThesaurusTreeModel) thesaurusTree.getModel();
        for (int i = 0; i < thesaurusTree.getRowCount(); i++)
        {
            TreePath path = thesaurusTree.getPathForRow(i);
            if (((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject().equals(word))
                treeModel.valueForPathChanged(path, word);
        }
    }

    /**
     * Adds a word to the ThesaurusTreeModel
     * 
     * @param word the new ThesaurusWord
     */
    protected void addWordToTree(ThesaurusWord word)
    {
        DefaultTreeModel treeModel = (DefaultTreeModel) thesaurusTree.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
        DefaultMutableTreeNode wordNode = new DefaultMutableTreeNode(word);
        wordNode.add(new DefaultMutableTreeNode(ApplicationUtilities
            .getResourceString("thesaurus.synonyms")));
        wordNode.add(new DefaultMutableTreeNode(ApplicationUtilities
            .getResourceString("thesaurus.homonyms")));
        treeModel.insertNodeInto(wordNode, root, root.getChildCount());
    }

    /**
     * Deletes a word from the ThesaurusTreeModel
     * 
     * @param word the ThesaurusWord to delete
     */
    protected void deleteWordFromTree(ThesaurusWord word)
    {
        ThesaurusTreeModel treeModel = (ThesaurusTreeModel) thesaurusTree.getModel();
        Vector<?> nodes = treeModel.findNodesWithUserObject(word);
        for (Iterator<?> i = nodes.iterator(); i.hasNext();)
            treeModel.removeNodeFromParent((DefaultMutableTreeNode) i.next());
    }

    /**
     * Adds a synonym to the ThesaurusTreeModel
     * 
     * @param word to add the synonym to
     * @param synonym the synonym to add
     */
    protected void addSynonymToTree(ThesaurusWord word, ThesaurusWord synonym)
    {
        ThesaurusTreeModel treeModel = (ThesaurusTreeModel) thesaurusTree.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
        DefaultMutableTreeNode wordNode = treeModel.findChildNodeWithUserObject(root, word);
        DefaultMutableTreeNode synonymNode = treeModel.findChildNodeWithUserObject(root, synonym);
        DefaultMutableTreeNode wordNodeSynonyms = (DefaultMutableTreeNode) wordNode.getFirstChild();
        DefaultMutableTreeNode synonymNodeSynonyms = (DefaultMutableTreeNode) synonymNode
            .getFirstChild();
        treeModel.insertNodeInto(new DefaultMutableTreeNode(synonymNode.getUserObject()),
            wordNodeSynonyms, wordNodeSynonyms.getChildCount());
        treeModel.insertNodeInto(new DefaultMutableTreeNode(wordNode.getUserObject()),
            synonymNodeSynonyms, synonymNodeSynonyms.getChildCount());
    }

    /**
     * Deletes a synonym from the ThesaurusTreeModel
     * 
     * @param word to delete the synonym from
     * @param synonym the synonym to delete
     */
    protected void deleteSynonymFromTree(ThesaurusWord word, ThesaurusWord synonym)
    {
        ThesaurusTreeModel treeModel = (ThesaurusTreeModel) thesaurusTree.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
        DefaultMutableTreeNode wordNode = treeModel.findChildNodeWithUserObject(root, word);
        DefaultMutableTreeNode synonymNode = treeModel.findChildNodeWithUserObject(root, synonym);
        DefaultMutableTreeNode wordNodeSynonyms = (DefaultMutableTreeNode) wordNode.getFirstChild();
        DefaultMutableTreeNode synonymNodeSynonyms = (DefaultMutableTreeNode) synonymNode
            .getFirstChild();
        DefaultMutableTreeNode wordNodeSynonymsSynonym = treeModel.findChildNodeWithUserObject(
            wordNodeSynonyms, synonymNode.getUserObject());
        DefaultMutableTreeNode synonymNodeSynonymsWord = treeModel.findChildNodeWithUserObject(
            synonymNodeSynonyms, wordNode.getUserObject());
        treeModel.removeNodeFromParent(wordNodeSynonymsSynonym);
        treeModel.removeNodeFromParent(synonymNodeSynonymsWord);
    }

    /**
     * Adds a homonym to the ThesaurusTreeModel
     * 
     * @param word to add the homonym to
     * @param homonym the homonym to add
     */
    protected void addHomonymToTree(ThesaurusWord word, ThesaurusWord homonym)
    {
        ThesaurusTreeModel treeModel = (ThesaurusTreeModel) thesaurusTree.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
        DefaultMutableTreeNode wordNode = treeModel.findChildNodeWithUserObject(root, word);
        DefaultMutableTreeNode homonymNode = treeModel.findChildNodeWithUserObject(root, homonym);
        DefaultMutableTreeNode wordNodeHomonyms = (DefaultMutableTreeNode) wordNode.getLastChild();
        DefaultMutableTreeNode homonymNodeHomonyms = (DefaultMutableTreeNode) homonymNode
            .getLastChild();
        treeModel.insertNodeInto(new DefaultMutableTreeNode(homonymNode.getUserObject()),
            wordNodeHomonyms, wordNodeHomonyms.getChildCount());
        treeModel.insertNodeInto(new DefaultMutableTreeNode(wordNode.getUserObject()),
            homonymNodeHomonyms, homonymNodeHomonyms.getChildCount());
    }

    /**
     * Deletes a homonym from the ThesaurusTreeModel
     * 
     * @param word to delete the homonym from
     * @param homonym the homonym to delete
     */
    protected void deleteHomonymFromTree(ThesaurusWord word, ThesaurusWord homonym)
    {
        ThesaurusTreeModel treeModel = (ThesaurusTreeModel) thesaurusTree.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
        DefaultMutableTreeNode wordNode = treeModel.findChildNodeWithUserObject(root, word);
        DefaultMutableTreeNode homonymNode = treeModel.findChildNodeWithUserObject(root, homonym);
        DefaultMutableTreeNode wordNodeHomonyms = (DefaultMutableTreeNode) wordNode.getLastChild();
        DefaultMutableTreeNode homonymNodeHomonyms = (DefaultMutableTreeNode) homonymNode
            .getLastChild();
        DefaultMutableTreeNode wordNodeHomonymsHomonym = treeModel.findChildNodeWithUserObject(
            wordNodeHomonyms, homonymNode.getUserObject());
        DefaultMutableTreeNode homonymNodeHomonymsWord = treeModel.findChildNodeWithUserObject(
            homonymNodeHomonyms, wordNode.getUserObject());
        treeModel.removeNodeFromParent(wordNodeHomonymsHomonym);
        treeModel.removeNodeFromParent(homonymNodeHomonymsWord);
    }

    /**
     * Update the tree
     */
    protected void updateTree()
    {
        ThesaurusTreeModel treeModel = (ThesaurusTreeModel) thesaurusTree.getModel();
        treeModel.updateTree();
    }

    public static void main(String args[])
    {
        // Initialize the properties
        try
        {
            ApplicationUtilities
                .initializeProperties(OntoBuilderResources.Config.APPLICATION_PROPERTIES);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return;
        }

        // Initialize the resource bundle
        try
        {
            ApplicationUtilities.initializeResources(
            	OntoBuilderResources.Config.RESOURCES_PROPERTIES,
                java.util.Locale.getDefault());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return;
        }

        try
        {
            ThesaurusGui thesaurus;
            File thesaurusFile = new File(OntoBuilderResources.Config.THESAURUS_XML);
            thesaurus = new ThesaurusGui(thesaurusFile);

            System.out.println("'" + args[0] + "' and '" + args[1] + "' are " +
                (thesaurus.isSynonym(args[0], args[1]) ? "" : "not ") + "synonyms");
            System.out.println("'" + args[0] + "' and '" + args[1] + "' are " +
                (thesaurus.isHomonym(args[0], args[1]) ? "" : "not ") + "homonyms");
        }
        catch (ThesaurusException e)
        {
            e.printStackTrace();
        }
    }
    
    public Thesaurus getThesaurus()
    {
        return model;
    }
}