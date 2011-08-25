package ac.technion.iem.ontobuilder.gui.tools.exactmapping;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableColumnModel;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.elements.AbsoluteConstraints;
import ac.technion.iem.ontobuilder.gui.elements.AbsoluteLayout;
import ac.technion.iem.ontobuilder.gui.ontology.OntologyGui;
import ac.technion.iem.ontobuilder.gui.tools.utils.ExceptionsHandler;
import ac.technion.iem.ontobuilder.gui.tools.utils.FileChoosingUtilities;
import ac.technion.iem.ontobuilder.gui.utils.files.common.FileUtilities;
import ac.technion.iem.ontobuilder.gui.utils.files.xml.XMLUtilities;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.wrapper.SchemaMatchingsException;
import ac.technion.iem.ontobuilder.matching.meta.match.MatchedAttributePair;
import ac.technion.iem.ontobuilder.matching.utils.SchemaMatchingsUtilities;
import ac.technion.iem.ontobuilder.matching.utils.SchemaTranslator;


/**
 * <p>Title: ExactMappingTool</p>
 * Extends a {@link JPanel}
 * @author haggai
 */
public class ExactMappingTool extends JPanel
{
    private static final long serialVersionUID = -4636689169117870001L;
    
    static private int checkType = 0;
    private String lastDir = System.getProperty("user.dir");

    /** Creates new form JFrame */
    public ExactMappingTool()
    {
        initComponents();
        setVisible(true);
    }

    /**
     * Initialize the components
     */
    private void initComponents()
    {
        jPanel1 = new javax.swing.JPanel();

        // Head panel - where user picks ontologies
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();

        // inner panel in 12 - for target ontology selection
        jLabel21 = new javax.swing.JLabel();
        targetOntology = new javax.swing.JComboBox();
        candOntology = new javax.swing.JComboBox();
        CandButton = new javax.swing.JButton();
        loadButton = new javax.swing.JButton();
        TargetButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        dataTable = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        viewTermsButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();

        jPanel1.setLayout(new AbsoluteLayout());

        jPanel1.setBorder(new javax.swing.border.CompoundBorder());
        jPanel2.setLayout(new AbsoluteLayout());

        jPanel2.setBorder(new javax.swing.border.TitledBorder(null, "Pick Ontologies",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 12),
            Color.BLACK));
        jLabel2.setFont(new java.awt.Font("Arial", 1, 12));
        jLabel2.setIcon(ApplicationUtilities.getImage("ontology.gif"));
        jLabel2.setText("Candidate Ontology:");
        jPanel2.add(jLabel2, new AbsoluteConstraints(15, 30, 170, -1));

        jLabel21.setFont(new java.awt.Font("Arial", 1, 12));
        jLabel21.setIcon(ApplicationUtilities.getImage("ontology.gif"));
        jLabel21.setText("Target Ontology:");
        jPanel2.add(jLabel21, new AbsoluteConstraints(15, 60, 170, -1));

        jPanel2.add(targetOntology, new AbsoluteConstraints(180, 60, 380, 20));

        jPanel2.add(candOntology, new AbsoluteConstraints(180, 30, 380, 20));

        CandButton.setToolTipText("Select candidate ontology");
        CandButton.setIcon(ApplicationUtilities.getImage("openontology.gif"));
        CandButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                pickCandXMLFileButtonPressed();
            }
        });
        jPanel2.add(CandButton, new AbsoluteConstraints(570, 30, 50, 20));

        TargetButton.setToolTipText("Select target ontology");
        TargetButton.setIcon(ApplicationUtilities.getImage("openontology.gif"));
        TargetButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                pickTargetXMLFileButtonPressed();
            }
        });
        jPanel2.add(TargetButton, new AbsoluteConstraints(570, 60, 50, 20));

        jPanel1.add(jPanel2, new AbsoluteConstraints(30, 90, 750, 100));

        jLabel1.setFont(new java.awt.Font("Arial", 1, 28));
        jLabel1.setIcon(ApplicationUtilities.getImage(""));
        jLabel1.setText("Exact Mapping Editor");
        jPanel1.add(jLabel1, new AbsoluteConstraints(240, 20, 400, 80));

        jScrollPane1.setBorder(new javax.swing.border.TitledBorder(null, "Make Exact Mapping",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 12),
            Color.BLACK));

        dataTable.setRowHeight(16);
        dataTable.setRowSelectionAllowed(false);
        dataTable.setFont(new java.awt.Font("Arial", 1, 12));
        dataTable.setModel(new ExactTabelModel(this));
        dataTable.getTableHeader().setFont(new java.awt.Font("Arial", 1, 12));
        dataTable.getTableHeader().setReorderingAllowed(false);

        // set the checkbox column width
        TableColumnModel columnsModel = dataTable.getColumnModel();
        columnsModel.getColumn(2).setMaxWidth(40);

        jScrollPane1.setViewportView(dataTable);

        jPanel1.add(jScrollPane1, new AbsoluteConstraints(30, 210, 750, 250));

        jPanel3.setLayout(new AbsoluteLayout());

        jPanel3.setBorder(new javax.swing.border.TitledBorder(null, "Options",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 12),
            Color.BLACK));
        // Load maping
        loadButton.setIcon(ApplicationUtilities.getImage("open.gif"));
        loadButton.setToolTipText("Load existing mapping");
        loadButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {

                if (!((ExactTabelModel) dataTable.getModel()).isTablePopulated())
                {
                    JOptionPane.showMessageDialog(null,
                        "Table is not populated, please select first ontology pair to edit!",
                        ApplicationUtilities.getResourceString("error"), JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Filters
                ArrayList<FileFilter> filters = new ArrayList<FileFilter>();
                filters.add(XMLUtilities.xmlFileFilter);
                FileUtilities.configureFileDialogFilters(filters);
                // Viewers
                FileUtilities.fileViewer.removeAllViewers();
                FileUtilities.fileViewer.addViewer(XMLUtilities.xmlFileViewer);
                File file = FileUtilities.openFileDialog(null);
                if (file != null)
                {
                    try
                    {
                        ((ExactTabelModel) dataTable.getModel())
                            .updateFromPrevMapping(SchemaMatchingsUtilities
                                .readXMLBestMatchingFile(file.getAbsolutePath()));
                    }
                    catch (Exception e)
                    {
                        JOptionPane.showMessageDialog(null,
                            "Error occurred when tried to load the mapping",
                            ApplicationUtilities.getResourceString("error"),
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                else
                {
                    JOptionPane.showMessageDialog(null,
                        "Error occurred when tried to load the mapping",
                        ApplicationUtilities.getResourceString("error"), JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        });
        jPanel3.add(loadButton, new AbsoluteConstraints(70, 20, 30, 30));

        viewTermsButton.setIcon(ApplicationUtilities.getImage("ontowizard.gif"));
        viewTermsButton.setToolTipText("Populate editor table");
        viewTermsButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                viewButtonPressed();
            }
        });
        jPanel3.add(viewTermsButton, new AbsoluteConstraints(100, 20, 30, 30));

        saveButton.setIcon(ApplicationUtilities.getImage("savematch.gif"));
        saveButton.setIcon(ApplicationUtilities.getImage("saveontology.gif"));
        saveButton.setToolTipText("Save mapping");
        saveButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                saveButtonPressed();
            }
        });
        jPanel3.add(saveButton, new AbsoluteConstraints(130, 20, 30, 30));

        isOnlyExactMapping = new JCheckBox();
        isOnlyExactMapping.setSelected(false);

        jPanel3.add(isOnlyExactMapping, new AbsoluteConstraints(190, 20, 20, 30));
        JLabel exactMappingLabel = new JLabel("Use 1:1 mapping guard");
        exactMappingLabel.setFont(new java.awt.Font("Arial", 1, 12));
        jPanel3.add(exactMappingLabel, new AbsoluteConstraints(210, 20, -1, 30));

        jPanel1.add(jPanel3, new AbsoluteConstraints(30, 470, 750, 60));

        add(jPanel1, java.awt.BorderLayout.CENTER);

        setLocation(getSize().width / 2, getSize().height / 2);
    }

    public void pickCandXMLFileButtonPressed()
    {
        FileChoosingUtilities.openFileChoser(this, "Pick up Candidate Ontology XML file", lastDir);
        if (FileChoosingUtilities.isFileChosed())
        {
            candidateXMLFiles.put(FileChoosingUtilities.getChosenFile().getName(),
                FileChoosingUtilities.getChosenFile());
            tempCandidateXMLFiles.addFirst(FileChoosingUtilities.getChosenFile().getName());
            refreshCandFilesListView();
            lastDir = FileChoosingUtilities.getChosenFile().getParent();
        }

    }

    public void pickTargetXMLFileButtonPressed()
    {
        FileChoosingUtilities.openFileChoser(this, "Pick up Target Ontology XML file", lastDir);
        if (FileChoosingUtilities.isFileChosed())
        {
            targetXMLFiles.put(FileChoosingUtilities.getChosenFile().getName(),
                FileChoosingUtilities.getChosenFile());
            tempTargetXMLFiles.addFirst(FileChoosingUtilities.getChosenFile().getName());
            refreshTargetFilesListView();
            lastDir = FileChoosingUtilities.getChosenFile().getParent();
        }
    }

    /**
     * Check if the guard is active
     *
     * @return <code>true</code> if is it active
     */
    public boolean isGuardActive()
    {
        return isOnlyExactMapping.isSelected();
    }

    /**
     * View the button pressed
     */
    public void viewButtonPressed()
    {
        try
        {
            if (candOntology.getSelectedItem() == null || targetOntology.getSelectedItem() == null)
            {
                eh.displayErrorMessage(false, this, ApplicationUtilities
                    .getResourceString("tools.exact.view.error.missingontology"), "Error");
                return;
            }
            if (!lastReadCandOntology.equals(candOntology.getSelectedItem()))
            {
                candOnto = Ontology.openFromXML((File) candidateXMLFiles.get(candOntology
                    .getSelectedItem()));
                OntologyGui ontologyGui = new OntologyGui(candOnto);
                if (!ontologyGui.isLightweight())
                {
                    candOnto.normalize();
                }
                // candOnto =
                // ob.readOntologyXMLFile(((File)candidateXMLFiles.get(candOntology.getSelectedItem())).getPath(),true);
                lastReadCandOntology = (String) candOntology.getSelectedItem();
            }
            if (!lastReadTargetOntology.equals(targetOntology.getSelectedItem()))
            {
                targetOnto = Ontology.openFromXML((File) targetXMLFiles.get(targetOntology
                    .getSelectedItem()));
                // targetOnto =
                // ob.readOntologyXMLFile(((File)targetXMLFiles.get(targetOntology.getSelectedItem())).getPath(),true);
                lastReadTargetOntology = (String) targetOntology.getSelectedItem();
            }
            ((ExactTabelModel) dataTable.getModel()).setTableData(candOnto, targetOnto, checkType);
        }
        catch (Exception e)
        {
            eh.displayErrorMessage(false, this, e.getMessage(), "Error");
        }
    }

    /**
     * Save the button pressed
     */
    public void saveButtonPressed()
    {
        try
        {
            if (candOntology.getSelectedItem() == null || targetOntology.getSelectedItem() == null)
            {
                eh.displayErrorMessage(false, this, ApplicationUtilities
                    .getResourceString("tools.exact.save.error.missingontology"), "Error");
                return;
            }
            ExactTabelModel model = (ExactTabelModel) dataTable.getModel();
            int[] selectedIndexes = model.getSelectedPairsIndexes();
            MatchedAttributePair[] matchPairs = new MatchedAttributePair[selectedIndexes.length];
            for (int i = 0; i < selectedIndexes.length; i++)
            {
                matchPairs[i] = new MatchedAttributePair((String) model.getValueAt(
                    selectedIndexes[i], 0), (String) model.getValueAt(selectedIndexes[i], 1), 1.0);
            }
            validate(matchPairs);
            SchemaTranslator st = new SchemaTranslator(matchPairs);
            // Filters
            ArrayList<FileFilter> filters = new ArrayList<FileFilter>();
            filters.add(XMLUtilities.xmlFileFilter);
            FileUtilities.configureFileDialogFilters(filters);
            // Viewers
            FileUtilities.fileViewer.removeAllViewers();
            FileUtilities.fileViewer.addViewer(XMLUtilities.xmlFileViewer);
            File file = FileUtilities.saveFileDialog(null, null);

            if (file != null)
            {

                try
                {
                    String filePath = (file.getName() != null && !file.getName().equals("") ? file
                        .getAbsolutePath() : file.getParentFile().getAbsolutePath() +
                        File.separator + lastReadCandOntology + "_" + lastReadTargetOntology +
                        "_EXACT.xml");
                    st.saveMatchToXML(0, lastReadCandOntology, lastReadTargetOntology, filePath);
                    JOptionPane.showMessageDialog(this, "saved to:" + filePath + "\n" +
                        "Total Saved: " + selectedIndexes.length + " matches",
                        "Save Exact Mapping", JOptionPane.INFORMATION_MESSAGE);
                }
                catch (SchemaMatchingsException ex)
                {
                    JOptionPane.showMessageDialog(null,
                        ApplicationUtilities.getResourceString("error") + ": " + ex.getMessage(),
                        ApplicationUtilities.getResourceString("error"), JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

        }
        catch (Throwable e)
        {
            eh.displayErrorMessage(false, this, e.getMessage(), "Error");
        }
    }

    /**
     * Validate the MatchedAttributePair array
     *
     * @param matchPairs a {@link MatchedAttributePair} array
     * @throws Exception when the exact mapping is not 1:1
     */
    private void validate(MatchedAttributePair[] matchPairs) throws Exception
    {
        for (int i = 0; i < matchPairs.length; i++)
        {
            String current = matchPairs[i].getAttribute1();
            for (int j = 0; j < matchPairs.length; j++)
            {
                if (i == j)
                    continue;
                if (isGuardActive() && current.equals(matchPairs[j].getAttribute1()))
                    throw new Exception("Exact mapping supposed to be 1:1\n");
            }
        }
    }

    /**
     * Refresh the candidate files list view
     */
    public void refreshCandFilesListView()
    {
        candOntology.removeAllItems(); // remove old values
        Iterator<String> it = tempCandidateXMLFiles.iterator();
        while (it.hasNext())
        {
            candOntology.addItem(it.next());
        }
    }

    /**
     * Refresh the target files list view
     */
    public void refreshTargetFilesListView()
    {
        targetOntology.removeAllItems(); // remove old values
        Iterator<String> it = tempTargetXMLFiles.iterator();
        while (it.hasNext())
        {
            targetOntology.addItem(it.next());
        }
    }

    /**
     * args can be: -filter1 : will sift 'form' and 'page' terms -filter2 : will also sift 'group'
     * and 'hidden' terms
     */
    public static void main(String args[])
    {

        if (args.length > 0)
        {
            if (args[0].equals("-filter1"))
            {
                checkType = 1;
                print("Ignoring terms: form, page");
            }
            if (args[0].equals("-filter2"))
            {
                checkType = 2;
                print("Ignoring terms: form, page, hidden, group");
            }
            if (args[0].equals("-filter3"))
            {
                checkType = 3;
                print("Ignoring terms: form, page, hidden");
            }
            if (args[0].equals("-filter4"))
            {
                checkType = 4;
                print("Ignoring terms: form, page, hidden, image");
            }
        }
    }

    private static void print(String s)
    {
        System.out.println(s);
    }

    // Variables declaration - do not modify
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel1;

    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable dataTable;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JComboBox candOntology;
    private javax.swing.JButton viewTermsButton;
    private javax.swing.JComboBox targetOntology;
    private javax.swing.JButton saveButton;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JButton CandButton;
    private javax.swing.JButton TargetButton;
    private JButton loadButton;
    private JCheckBox isOnlyExactMapping;
    private Hashtable<String, File> candidateXMLFiles = new Hashtable<String, File>();
    private Hashtable<String, File> targetXMLFiles = new Hashtable<String, File>();
    private LinkedList<String> tempCandidateXMLFiles = new LinkedList<String>();
    private LinkedList<String> tempTargetXMLFiles = new LinkedList<String>();
    // private OntoBuilderWrapper ob = new OntoBuilderWrapper();
    private ExceptionsHandler eh = new ExceptionsHandler();
    private String lastReadCandOntology = "";
    private String lastReadTargetOntology = "";
    Ontology candOnto;
    Ontology targetOnto;
    // End of variables declaration

}
