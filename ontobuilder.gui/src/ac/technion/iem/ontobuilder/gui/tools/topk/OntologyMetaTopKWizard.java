package ac.technion.iem.ontobuilder.gui.tools.topk;

import java.awt.BorderLayout;
import java.awt.Color;
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
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileFilter;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.utils.properties.ApplicationParameters;
import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.application.PropertiesTableModel;
import ac.technion.iem.ontobuilder.gui.elements.MultilineLabel;
import ac.technion.iem.ontobuilder.gui.elements.TextField;
import ac.technion.iem.ontobuilder.gui.elements.ToolBar;
import ac.technion.iem.ontobuilder.gui.match.MatchInformationGui;
import ac.technion.iem.ontobuilder.gui.match.MatchTableModel;
import ac.technion.iem.ontobuilder.gui.ontobuilder.elements.ThresholdDialog;
import ac.technion.iem.ontobuilder.gui.tools.algorithms.line1.AlgorithmsGuiFactory;
import ac.technion.iem.ontobuilder.gui.tools.mergewizard.MatchGraphStatus;
import ac.technion.iem.ontobuilder.gui.tools.mergewizard.WizardStatus;
import ac.technion.iem.ontobuilder.gui.utils.files.common.FilePreviewer;
import ac.technion.iem.ontobuilder.gui.utils.files.common.FileUtilities;
import ac.technion.iem.ontobuilder.gui.utils.files.common.GeneralFileFilter;
import ac.technion.iem.ontobuilder.gui.utils.files.common.GeneralFilePreviewer;
import ac.technion.iem.ontobuilder.gui.utils.files.common.GeneralFileView;
import ac.technion.iem.ontobuilder.gui.utils.files.image.ImageFileFilter;
import ac.technion.iem.ontobuilder.gui.utils.files.image.ImageFileViewer;
import ac.technion.iem.ontobuilder.gui.utils.files.image.ImageUtilities;
import ac.technion.iem.ontobuilder.gui.utils.files.image.JPGImageFileFilter;
import ac.technion.iem.ontobuilder.gui.utils.files.image.PNGImageFileFilter;
import ac.technion.iem.ontobuilder.gui.utils.files.xml.XMLUtilities;
import ac.technion.iem.ontobuilder.gui.utils.graphs.GraphUtilities;
import ac.technion.iem.ontobuilder.io.exports.ExportUtilities;
import ac.technion.iem.ontobuilder.io.exports.Exporter;
import ac.technion.iem.ontobuilder.io.exports.ExporterMetadata;
import ac.technion.iem.ontobuilder.io.exports.ExportersTypeEnum;
import ac.technion.iem.ontobuilder.io.matchimport.NativeMatchImporter;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.common.AbstractAlgorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.common.Algorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.meta.AbstractMetaAlgorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.meta.MetaAlgorithmInitiationException;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.meta.MetaAlgorithmNamesEnum;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.meta.MetaAlgorithmRunningException;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.meta.MetaAlgorithmUtilities;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.meta.MetaAlgorithmsFactory;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.meta.MetaAlgorithmsFactoryException;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.misc.CrossThresholdAlgorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.misc.MatrixDirectAlgorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.misc.MatrixDirectWithBoundingAlgorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.misc.SMThersholdAlgorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.utils.TopKPlot;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.wrapper.SchemaMatchingsException;
import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.match.MatchOntologyHandler;
import ac.technion.iem.ontobuilder.matching.meta.aggregators.AbstractGlobalAggregator;
import ac.technion.iem.ontobuilder.matching.meta.aggregators.AbstractLocalAggregator;
import ac.technion.iem.ontobuilder.matching.meta.aggregators.AverageGlobalAggregator;
import ac.technion.iem.ontobuilder.matching.meta.aggregators.AverageLocalAggregator;
import ac.technion.iem.ontobuilder.matching.meta.aggregators.SumGlobalAggregator;
import ac.technion.iem.ontobuilder.matching.meta.aggregators.SumLocalAggregator;
import ac.technion.iem.ontobuilder.matching.meta.match.MatchMatrix;
import ac.technion.iem.ontobuilder.matching.utils.SchemaMatchingAlgorithmsRunner;

import com.jgraph.JGraph;
import com.jrefinery.chart.ChartFactory;
import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.JFreeChartPanel;
import com.jrefinery.data.SeriesException;
import com.jrefinery.data.XYDataPair;
import com.jrefinery.data.XYSeries;
import com.jrefinery.data.XYSeriesCollection;


/**
 * <p>Title: OntologyMetaTopKWizard</p>
 * <p>Description: This class implements the Meta top k Wizard</p>
 * @author haggai
 */
public class OntologyMetaTopKWizard
{
    private JFrame parent;
    private Vector<Ontology> ontologies;
    private Ontology target;
    private Ontology candidate;
    private AbstractAlgorithm algorithm;
    private Vector<AbstractAlgorithm> availableAlgs;
    private Vector<AbstractAlgorithm> selectedAlgs;
    private boolean normalize = false;
    private JTable properties;
    private MatchInformation exactMapping;
    private Vector<AbstractLocalAggregator> availableLocalFAggs;
    private Vector<AbstractGlobalAggregator> availableGlobalFAggs;
    private Vector<AbstractLocalAggregator> availableLocalHAggs;
    private Vector<AbstractGlobalAggregator> availableGlobalHAggs;
    private Vector<Object> availableMetaAlgsNames;
    private String metaAlgorithmName;
    private AbstractMetaAlgorithm metaAlg;
    private AbstractGlobalAggregator globalF;
    private AbstractGlobalAggregator globalH;
    private AbstractLocalAggregator localf;
    private AbstractLocalAggregator localh;
    private int k;
    private Vector<MatchInformation> topKMatchings;
    private HashMap<String, MatchInformation> matchInfos;

    /**
     * Constructs a OntologyMetaTopKWizard
     *
     * @param parent the parent {@link JFrame}
     * @param ontologies a list of ontologies
     * @param target the target {@link Ontology}
     * @param algorithms match algorithms
     */
    public OntologyMetaTopKWizard(JFrame parent, Vector<Ontology> ontologies, Ontology target,
        Vector<AbstractAlgorithm> algorithms)
    {
        this.parent = parent;
        this.ontologies = ontologies;
        this.target = target;
        this.availableAlgs = algorithms;
        this.selectedAlgs = new Vector<AbstractAlgorithm>();
        availableLocalFAggs = new Vector<AbstractLocalAggregator>(
            MetaAlgorithmUtilities.loadAllLocalAggregators());
        availableGlobalFAggs = new Vector<AbstractGlobalAggregator>(
            MetaAlgorithmUtilities.loadAllGlobalAggregators());
        availableLocalHAggs = new Vector<AbstractLocalAggregator>(
            MetaAlgorithmUtilities.loadAllLocalAggregators());
        availableGlobalHAggs = new Vector<AbstractGlobalAggregator>(
            MetaAlgorithmUtilities.loadAllGlobalAggregators());
        availableMetaAlgsNames = new Vector<Object>(
            MetaAlgorithmUtilities.getMetaAlgorithmNames());
        metaAlgorithmName = (String) availableMetaAlgsNames.get(0);
    }

    /**
     * Set to normalize before match
     * 
     * @param b <code>true</code> to normalize
     */
    public void setNormalizeBeforeMatch(boolean b)
    {
        normalize = b;
    }

    /**
     * Check if has to normalize before match
     * 
     * @return <code>true</code> if has to normalize
     */
    public boolean hasToNormalizeBeforeMatch()
    {
        return normalize;
    }

    /**
     * Start an ontology merge
     * 
     * @return an merged {@link Ontology}
     */
    public Ontology startOntologyMerge()
    {
        if (start().getNextAction() == WizardStatus.CANCEL_ACTION)
            return null;
        if (normalize)
        {
            target.normalize();
            candidate.normalize();
        }

        if (ApplicationParameters.result)
        {
            // System.out.println("************************************************************************************************************************************************");
            // System.out.println("Running algorithm " + algorithm.getName() + " with threshold of "
            // + algorithm.getThreshold()*100 + "% \n\n\n");
        }

        if (properties != null)
        {
            HashMap<Object, Object> dataMap = ((PropertiesTableModel) properties.getModel())
                .getDataMap();
            
            AlgorithmsGuiFactory.getAlgorithmGui(algorithm).updateProperties(dataMap);
        }

        matchInfos = new HashMap<String, MatchInformation>();
        Iterator<AbstractAlgorithm> algItr = selectedAlgs.iterator();
        MatchInformation matchInformation;
        // run matchers to generate MatchInformation for each.
        while (algItr.hasNext())
        {
            algorithm = (AbstractAlgorithm) algItr.next();
            matchInformation = MatchOntologyHandler.match(target, candidate, algorithm);
            if (matchInformation == null)
                return null;
            else
                matchInfos.put(algorithm.getName(), matchInformation);
            System.out.println("generating match info object for matcher: " + algorithm.getName());
        }

        // run meta-algorithm
        topKMatchings = runMetaAlgorithm();
        System.out.println("Meta algorithm run...");

        // TODO: generate the tabs structure

        // TODO
        if (showMatchInformation().getNextAction() == WizardStatus.CANCEL_ACTION)
            return null;
        // Ontology mergedOntology=OntologyUtilities.createOntology(ontologyName,matchInformation);

        if (ApplicationParameters.result)
            System.out
                .println("************************************************************************************************************************************************\n\n\n\n");

        return null;// mergedOntology;
    }

    /**
     * Run the meta algorithm
     * 
     * @return a vector of {@link MatchInformation}
     */
    private Vector<MatchInformation> runMetaAlgorithm()
    {
        if (metaAlgorithmName
            .equals(MetaAlgorithmNamesEnum.THERSHOLD_ALGORITHM.getName()))
            return runTA();
        else if (metaAlgorithmName
            .equals(MetaAlgorithmNamesEnum.MATRIX_DIRECT_ALGORITHM.getName()))
            return runMD();
        else if (metaAlgorithmName
            .equals(MetaAlgorithmNamesEnum.MATRIX_DIRECT_WITH_BOUNDING_ALGORITHM.getName()))
            return runMDB();
        else
            return runCrossThreshold();
    }

    /**
     * Run the MATRIX_DIRECT_ALGORITHM
     * 
     * @return a vector of {@link MatchInformation}
     */
    private Vector<MatchInformation> runMD()
    {
        Object[] params = new Object[4];
        int numSelectedMatchers = selectedAlgs.size();
        Algorithm[] matchAlgorithms = new Algorithm[numSelectedMatchers];
        Iterator<AbstractAlgorithm> it = selectedAlgs.iterator();
        int i = 0;
        while (it.hasNext())
        {
            matchAlgorithms[i++] = (AbstractAlgorithm) it.next();
        }
        params[0] = new Integer(k);
        params[1] = globalF;
        params[2] = localf;
        params[3] = new MatchMatrix();
        MatrixDirectAlgorithm mda;
        try
        {
            mda = (MatrixDirectAlgorithm) MetaAlgorithmsFactory.getInstance().buildMetaAlgorithm(
                MetaAlgorithmNamesEnum.MATRIX_DIRECT_ALGORITHM, params);
        }
        catch (MetaAlgorithmsFactoryException e)
        {
            e.printStackTrace();
            return null;
        }
        try
        {
            mda.init(candidate, target, matchAlgorithms.length, matchAlgorithms,
                new SchemaMatchingAlgorithmsRunner());
        }
        catch (MetaAlgorithmInitiationException e)
        {
            e.printStackTrace();
            return null;
        }
        mda.useStatistics();
        mda.normalizeMatrixes();
        try
        {
            mda.runAlgorithm();
        }
        catch (MetaAlgorithmRunningException e)
        {
            e.printStackTrace();
            return null;
        }
        metaAlg = mda;
        return mda.getAllKBestMappings();
    }

    /**
     * Run the MATRIX_DIRECT_WITH_BOUNDING_ALGORITHM
     * 
     * @return a vector of {@link MatchInformation}
     */
    private Vector<MatchInformation> runMDB()
    {
        Object[] params = new Object[6];
        int numSelectedMatchers = selectedAlgs.size();
        Algorithm[] matchAlgorithms = new Algorithm[numSelectedMatchers];
        Iterator<AbstractAlgorithm> it = selectedAlgs.iterator();
        int i = 0;
        while (it.hasNext())
        {
            matchAlgorithms[i++] = (AbstractAlgorithm) it.next();
        }
        params[0] = new Integer(k);
        params[1] = globalF;// F
        params[2] = localf;// f
        params[3] = globalH;// H
        params[4] = localh;// h
        params[5] = new MatchMatrix();
        MatrixDirectWithBoundingAlgorithm mdb;
        try
        {
            mdb = (MatrixDirectWithBoundingAlgorithm) MetaAlgorithmsFactory.getInstance()
                .buildMetaAlgorithm(MetaAlgorithmNamesEnum.MATRIX_DIRECT_WITH_BOUNDING_ALGORITHM,
                    params);
        }
        catch (MetaAlgorithmsFactoryException e)
        {
            e.printStackTrace();
            return null;
        }
        try
        {
            mdb.init(candidate, target, matchAlgorithms.length, matchAlgorithms,
                new SchemaMatchingAlgorithmsRunner());
        }
        catch (MetaAlgorithmInitiationException e)
        {
            e.printStackTrace();
            return null;
        }
        mdb.useStatistics();
        mdb.normalizeMatrixes();
        try
        {
            mdb.runAlgorithm();
        }
        catch (MetaAlgorithmRunningException e)
        {
            e.printStackTrace();
            return null;
        }
        metaAlg = mdb;
        return mdb.getAllKBestMappings();
    }

    /**
     * Run the THERSHOLD_ALGORITHM
     * 
     * @return a vector of {@link MatchInformation}
     */
    private Vector<MatchInformation> runTA()
    {
        SMThersholdAlgorithm ta = null;
        Object wait = new Object();
        Object[] params = new Object[3];
        int numSelectedMatchers = selectedAlgs.size();
        Algorithm[] matchAlgorithms = new Algorithm[numSelectedMatchers];
        Iterator<AbstractAlgorithm> it = selectedAlgs.iterator();
        int i = 0;
        while (it.hasNext())
        {
            matchAlgorithms[i++] = (AbstractAlgorithm) it.next();
        }
        params[0] = new Integer(k);
        params[1] = globalF;// F
        params[2] = localf;// f
        try
        {
            ta = (SMThersholdAlgorithm) MetaAlgorithmsFactory.getInstance().buildMetaAlgorithm(
                MetaAlgorithmNamesEnum.THERSHOLD_ALGORITHM, params);
        }
        catch (MetaAlgorithmsFactoryException e)
        {
            e.printStackTrace();
            return null;
        }
        try
        {
            ta.init(candidate, target, matchAlgorithms.length, matchAlgorithms,
                new SchemaMatchingAlgorithmsRunner());
        }
        catch (MetaAlgorithmInitiationException e)
        {
            e.printStackTrace();
            return null;
        }
        try
        {
            ta.setThreshold(0);
        }
        catch (MetaAlgorithmInitiationException e)
        {
            e.printStackTrace();
            return null;
        }
        ta.normalizeMatrixes();
        ta.useStatistics();
        try
        {
            ta.runAlgorithm();
        }
        catch (MetaAlgorithmRunningException e)
        {
            e.printStackTrace();
            return null;
        }

        synchronized (wait)
        {
            while (!ta.isAlgorithmRunFinished())
            {
                try
                {
                    wait.wait(1000);
                }
                catch (InterruptedException e)
                {
                }
            }
        }
        metaAlg = ta;
        return ta.getAllKBestMappings();
    }

    /**
     * Run the HYBRID_ALGORITHM
     * 
     * @return a vector of {@link MatchInformation}
     */
    private Vector<MatchInformation> runCrossThreshold()
    {
        Object wait = new Object();
        Object[] params = new Object[6];
        int numSelectedMatchers = selectedAlgs.size();
        Algorithm[] matchAlgorithms = new Algorithm[numSelectedMatchers];
        Iterator<AbstractAlgorithm> it = selectedAlgs.iterator();
        int i = 0;
        while (it.hasNext())
        {
            matchAlgorithms[i++] = (AbstractAlgorithm) it.next();
        }
        params[0] = new Integer(k);
        params[1] = globalF;// F
        params[2] = localf;// f
        params[3] = globalH;// H
        params[4] = localh;// h
        params[5] = new MatchMatrix();
        CrossThresholdAlgorithm hybrid;
        try
        {
            hybrid = (CrossThresholdAlgorithm) MetaAlgorithmsFactory.getInstance()
                .buildMetaAlgorithm(MetaAlgorithmNamesEnum.HYBRID_ALGORITHM, params);
        }
        catch (MetaAlgorithmsFactoryException e)
        {
            e.printStackTrace();
            return null;
        }
        try
        {
            hybrid.init(candidate, target, matchAlgorithms.length, matchAlgorithms,
                new SchemaMatchingAlgorithmsRunner());
        }
        catch (MetaAlgorithmInitiationException e)
        {
            e.printStackTrace();
            return null;
        }
        hybrid.useStatistics();
        if (exactMapping != null)
            hybrid.setExactMapping(exactMapping);
        hybrid.normalizeMatrixes();
        try
        {
            hybrid.runAlgorithm();
        }
        catch (MetaAlgorithmRunningException e)
        {
            e.printStackTrace();
            return null;
        }

        synchronized (wait)
        {
            while (!hybrid.isAlgorithmRunFinished())
            {
                try
                {
                    wait.wait(1000);
                }
                catch (InterruptedException e)
                {
                }
            }
        }
        metaAlg = hybrid;
        return hybrid.getAllKBestMappings();
    }

    /**
     * Start the ontology meta top K wizard
     * 
     * @return a {@link WizardStatus}
     */
    public WizardStatus start()
    {
        final WizardStatus status = new WizardStatus();
        final JList targetList = new JList(ontologies);
        targetList.setSelectedValue(target, true);
        final JList candidateList = new JList(ontologies);
        if (candidate != null)
            candidateList.setSelectedValue(candidate, true);
        final TextField txtOntologyName = new TextField(15);

        final JDialog startDialog = new JDialog(parent,
            ApplicationUtilities.getResourceString("metaTopK.windowTitle"), true);
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        startDialog.setSize(new Dimension(ApplicationUtilities
            .getIntProperty("mergewizard.start.width"), ApplicationUtilities
            .getIntProperty("mergewizard.start.height")));
        startDialog.setLocationRelativeTo(parent);
        startDialog.setResizable(false);

        JPanel south = new JPanel();
        south.setLayout(new FlowLayout(FlowLayout.RIGHT));
        south.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.black),
            BorderFactory.createEmptyBorder(0, 10, 10, 10)));
        final JButton nextButton;
        south.add(nextButton = new JButton(ApplicationUtilities
            .getResourceString("mergewizard.button.next")));
        startDialog.getRootPane().setDefaultButton(nextButton);
        nextButton.setEnabled(false);
        nextButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                status.setNextAction(WizardStatus.NEXT_ACTION);
                txtOntologyName.getText();
                startDialog.dispose();
            }
        });
        JButton cancelButton;
        south.add(cancelButton = new JButton(ApplicationUtilities
            .getResourceString("mergewizard.button.cancel")));
        cancelButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                status.setNextAction(WizardStatus.CANCEL_ACTION);
                startDialog.dispose();
            }
        });
        panel.add(BorderLayout.SOUTH, south);

        JLabel west = new JLabel(ApplicationUtilities.getImage("metabanner.gif"));
        west.setBorder(BorderFactory.createLineBorder(Color.black));
        panel.add(BorderLayout.WEST, west);

        txtOntologyName.addKeyListener(new KeyAdapter()
        {
            public void keyTyped(KeyEvent event)
            {
                SwingUtilities.invokeLater(new Runnable()
                {
                    public void run()
                    {
                        if (!txtOntologyName.getText().trim().equals("") && target != null &&
                            candidate != null)
                            nextButton.setEnabled(true);
                        else
                            nextButton.setEnabled(false);
                    }
                });
            }
        });

        targetList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        targetList.addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent e)
            {
                target = (Ontology) targetList.getSelectedValue();
                txtOntologyName.setText((target != null ? target.getName() : "") +
                    (candidate != null ? "-" + candidate.getName() : ""));
                nextButton.setEnabled(!txtOntologyName.getText().trim().equals("") &&
                    target != null && candidate != null);
            }
        });
        targetList.setCellRenderer(new DefaultListCellRenderer()
        {
            private static final long serialVersionUID = 1L;

            public Component getListCellRendererComponent(JList list, Object value, int index,
                boolean isSelected, boolean cellHasFocus)
            {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setIcon(ApplicationUtilities.getImage("ontology.gif"));
                return this;
            }
        });

        candidateList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        candidateList.addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent e)
            {
                candidate = (Ontology) candidateList.getSelectedValue();
                txtOntologyName.setText((target != null ? target.getName() + "-" : "") +
                    (candidate != null ? candidate.getName() : ""));
                nextButton.setEnabled(!txtOntologyName.getText().trim().equals("") &&
                    target != null && candidate != null);
            }
        });
        candidateList.setCellRenderer(new DefaultListCellRenderer()
        {
            private static final long serialVersionUID = -6274546166339049548L;

            public Component getListCellRendererComponent(JList list, Object value, int index,
                boolean isSelected, boolean cellHasFocus)
            {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setIcon(ApplicationUtilities.getImage("ontology.gif"));
                return this;
            }
        });

        JPanel normalizationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JCheckBox chkNormalize = new JCheckBox(
            ApplicationUtilities.getResourceString("mergewizard.start.normalize"));
        normalizationPanel.add(chkNormalize);
        chkNormalize.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent e)
            {
                setNormalizeBeforeMatch(e.getStateChange() == ItemEvent.SELECTED);
            }
        });

        JPanel center = new JPanel(new GridBagLayout());
        panel.add(BorderLayout.CENTER, center);
        center.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));

        {// Title
            JLabel title = new JLabel(ApplicationUtilities.getResourceString("metaTopK.title"),
                JLabel.LEFT);
            title.setFont(new Font(title.getFont().getFontName(), Font.BOLD, title.getFont()
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
            gbcl.insets = new Insets(0, 0, 10, 0);
            gbcl.anchor = GridBagConstraints.WEST;
            center.add(
                new MultilineLabel(ApplicationUtilities.getResourceString("metaTopK.explanation")),
                gbcl);
        }

        {// Ontology Name
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = 2;
            gbcl.insets = new Insets(0, 0, 10, 5);
            gbcl.anchor = GridBagConstraints.EAST;
            JLabel ontologyLabel = new JLabel(
                ApplicationUtilities.getResourceString("mergewizard.start.ontologyName") + ":");
            ontologyLabel.setFont(new Font(ontologyLabel.getFont().getName(), Font.BOLD,
                ontologyLabel.getFont().getSize()));
            center.add(ontologyLabel, gbcl);

            gbcl.gridx = 1;
            gbcl.weightx = 1;
            gbcl.insets = new Insets(0, 0, 10, 15);
            gbcl.anchor = GridBagConstraints.WEST;
            gbcl.fill = GridBagConstraints.HORIZONTAL;
            center.add(txtOntologyName, gbcl);
        }

        {// Targets
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = 3;
            gbcl.gridwidth = 2;
            gbcl.insets = new Insets(0, 5, 0, 0);
            gbcl.anchor = GridBagConstraints.WEST;
            JLabel targets = new JLabel(
                ApplicationUtilities.getResourceString("mergewizard.target") + ":");
            targets.setFont(new Font(targets.getFont().getName(), Font.BOLD, targets.getFont()
                .getSize()));
            center.add(targets, gbcl);

            gbcl.gridy = 4;
            gbcl.insets = new Insets(0, 5, 5, 15);
            gbcl.weightx = 1.0;
            gbcl.weighty = 1.0;
            gbcl.fill = GridBagConstraints.BOTH;
            center.add(new JScrollPane(targetList), gbcl);
        }

        {// Candidates
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = 5;
            gbcl.gridwidth = 2;
            gbcl.insets = new Insets(0, 5, 0, 0);
            gbcl.anchor = GridBagConstraints.WEST;
            JLabel candidates = new JLabel(
                ApplicationUtilities.getResourceString("mergewizard.candidate") + ":");
            candidates.setFont(new Font(candidates.getFont().getName(), Font.BOLD, candidates
                .getFont().getSize()));
            center.add(candidates, gbcl);

            gbcl.gridy = 6;
            gbcl.insets = new Insets(0, 5, 0, 15);
            gbcl.weightx = 1.0;
            gbcl.weighty = 1.0;
            gbcl.fill = GridBagConstraints.BOTH;
            center.add(new JScrollPane(candidateList), gbcl);
        }

        {// Normalization
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = 7;
            gbcl.gridwidth = 2;
            gbcl.insets = new Insets(0, 5, 0, 10);
            gbcl.anchor = GridBagConstraints.WEST;
            gbcl.fill = GridBagConstraints.HORIZONTAL;
            gbcl.weightx = 1.0;
            center.add(normalizationPanel, gbcl);
        }

        startDialog.addWindowListener(new WindowAdapter()
        {
            public void windowOpened(WindowEvent e)
            {
                SwingUtilities.invokeLater(new Runnable()
                {
                    public void run()
                    {
                        candidateList.requestFocus();
                    }
                });
            }

            public void windowClosing(WindowEvent e)
            {
                status.setNextAction(WizardStatus.CANCEL_ACTION);
                startDialog.dispose();
            }
        });
        startDialog.setContentPane(panel);

        startDialog.setVisible(true);
        ;
        switch (status.getNextAction())
        {
        case WizardStatus.NEXT_ACTION:
            return useExactMapping();
        default:
            return status;
        }
    }

    /**
     * Use exact mapping
     * 
     * @return a {@link WizardStatus}
     */
    public WizardStatus useExactMapping()
    {
        final WizardStatus status = new WizardStatus();

        final JDialog dialog = new JDialog(parent,
            ApplicationUtilities.getResourceString("mergewizard.windowTitle"), true);
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        dialog.setSize(new Dimension(
            ApplicationUtilities.getIntProperty("mergewizard.start.width"), ApplicationUtilities
                .getIntProperty("mergewizard.start.height")));
        dialog.setLocationRelativeTo(parent);
        dialog.setResizable(false);

        JLabel west = new JLabel(ApplicationUtilities.getImage("metabanner.gif"));
        west.setBorder(BorderFactory.createLineBorder(Color.black));
        panel.add(BorderLayout.WEST, west);

        JPanel center = new JPanel(new AbsoluteLayout());
        panel.add(BorderLayout.CENTER, center);
        center.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));

        {// Title
            JLabel title = new JLabel(
                ApplicationUtilities.getResourceString("mergewizard.exactMapping.title"),
                JLabel.LEFT);
            title.setFont(new Font(title.getFont().getFontName(), Font.BOLD, title.getFont()
                .getSize() + 6));
            center.add(title, new AbsoluteConstraints(10, 10, -1, -1));
        }

        {// Explanation
            center.add(
                new MultilineLabel(ApplicationUtilities
                    .getResourceString("mergewizard.exactMapping.explanation")),
                new AbsoluteConstraints(10, 40, 400, -1));
        }

        // Exact Mapping
        final JButton open = new JButton(
            ApplicationUtilities.getResourceString("mergewizard.exactMapping.openButton"));

        center.add(open, new AbsoluteConstraints(10, 150, -1, -1));

        final JTextField name = new JTextField(200);
        name.setText("");
        exactMapping = null;
        center.add(name, new AbsoluteConstraints(10, 180, 400, 30));

        open.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
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
                    name.setText(file.getAbsolutePath());
                }
            }
        });

        JPanel south = new JPanel();
        south.setLayout(new FlowLayout(FlowLayout.RIGHT));
        south.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.black),
            BorderFactory.createEmptyBorder(0, 10, 10, 10)));
        JButton backButton;
        south.add(backButton = new JButton(ApplicationUtilities
            .getResourceString("mergewizard.button.back")));
        backButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                status.setNextAction(WizardStatus.BACK_ACTION);
                dialog.dispose();
            }
        });
        final JButton nextButton;
        south.add(nextButton = new JButton(ApplicationUtilities
            .getResourceString("mergewizard.button.next")));
        dialog.getRootPane().setDefaultButton(nextButton);
        nextButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {

                try
                {
                    if (name.getText() != null && name.getText().length() > 0)
                    {
                    	NativeMatchImporter imp = new NativeMatchImporter();
                        exactMapping = imp.importMatch(new MatchInformation(candidate,target), new File(name.getText()));
                    }
                    status.setNextAction(WizardStatus.NEXT_ACTION);
                }
                catch (Exception e1)
                {
                    JOptionPane.showMessageDialog(null, "Faild to read exact maping file.",
                        ApplicationUtilities.getResourceString("error"), JOptionPane.ERROR_MESSAGE);
                }
                dialog.dispose();
            }
        });
        JButton cancelButton;
        south.add(cancelButton = new JButton(ApplicationUtilities
            .getResourceString("mergewizard.button.cancel")));
        cancelButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                status.setNextAction(WizardStatus.CANCEL_ACTION);
                dialog.dispose();
            }
        });
        panel.add(BorderLayout.SOUTH, south);

        dialog.addWindowListener(new WindowAdapter()
        {
            public void windowOpened(WindowEvent e)
            {
                SwingUtilities.invokeLater(new Runnable()
                {
                    public void run()
                    {
                    }
                });
            }

            public void windowClosing(WindowEvent e)
            {
                status.setNextAction(WizardStatus.CANCEL_ACTION);
                dialog.dispose();
            }
        });
        dialog.setContentPane(panel);

        dialog.setVisible(true);
        switch (status.getNextAction())
        {
        case WizardStatus.NEXT_ACTION:
            return selectAlgorithms();
        case WizardStatus.BACK_ACTION:
            return start();
        default:
            return status;
        }

    }

    /**
     * Select algorithms to use
     * 
     * @return a {@link WizardStatus}
     */
    public WizardStatus selectAlgorithms()
    {
        final WizardStatus status = new WizardStatus();
        algorithm = (AbstractAlgorithm) availableAlgs.get(0);
        final MultilineLabel txtExplanation = new MultilineLabel(algorithm.getDescription());
        final JPanel propertiesPanel = new JPanel(new BorderLayout());
        properties = AlgorithmsGuiFactory.getAlgorithmGui(algorithm).getProperties();
        properties.getModel().addTableModelListener(new TableModelListener()
        {
            public void tableChanged(TableModelEvent event)
            {
                AlgorithmsGuiFactory.getAlgorithmGui(algorithm).updateProperties(((PropertiesTableModel) properties.getModel())
                    .getDataMap());
            }

        });
        propertiesPanel.add(BorderLayout.CENTER, new JScrollPane(properties));
        propertiesPanel.setBorder(BorderFactory.createLineBorder(Color.black));

        final JDialog dialog = new JDialog(parent,
            ApplicationUtilities.getResourceString("mergewizard.windowTitle"), true);
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        dialog.setSize(new Dimension(ApplicationUtilities
            .getIntProperty("metatopkwizard.algorithm.width"), ApplicationUtilities
            .getIntProperty("metatopkwizard.algorithm.height")));
        dialog.setLocationRelativeTo(parent);
        dialog.setResizable(false);

        JPanel south = new JPanel();
        south.setLayout(new FlowLayout(FlowLayout.RIGHT));
        south.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.black),
            BorderFactory.createEmptyBorder(0, 10, 10, 10)));
        JButton backButton;
        south.add(backButton = new JButton(ApplicationUtilities
            .getResourceString("mergewizard.button.back")));
        backButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                status.setNextAction(WizardStatus.BACK_ACTION);
                dialog.dispose();
            }
        });
        final JButton nextButton;
        south.add(nextButton = new JButton(ApplicationUtilities
            .getResourceString("mergewizard.button.next")));
        dialog.getRootPane().setDefaultButton(nextButton);
        nextButton.setEnabled(false);
        nextButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                status.setNextAction(WizardStatus.NEXT_ACTION);
                dialog.dispose();
            }
        });
        JButton cancelButton;
        south.add(cancelButton = new JButton(ApplicationUtilities
            .getResourceString("mergewizard.button.cancel")));
        cancelButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                status.setNextAction(WizardStatus.CANCEL_ACTION);
                dialog.dispose();
            }
        });
        panel.add(BorderLayout.SOUTH, south);

        JLabel west = new JLabel(ApplicationUtilities.getImage("metabanner.gif"));
        west.setBorder(BorderFactory.createLineBorder(Color.black));
        panel.add(BorderLayout.WEST, west);

        JPanel center = new JPanel(new GridBagLayout());
        panel.add(BorderLayout.CENTER, center);
        center.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));

        final JList algorithmsList = new JList(availableAlgs);
        algorithmsList.setSelectedIndex(0);
        algorithmsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        algorithmsList.addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent e)
            {
                algorithm = (AbstractAlgorithm) algorithmsList.getSelectedValue();
                properties = AlgorithmsGuiFactory.getAlgorithmGui(algorithm).getProperties();
                properties.getModel().addTableModelListener(new TableModelListener()
                {

                    public void tableChanged(TableModelEvent event)
                    {
                        AlgorithmsGuiFactory.getAlgorithmGui(algorithm).updateProperties(((PropertiesTableModel) properties.getModel())
                            .getDataMap());
                    }

                });
                txtExplanation.setText(algorithm != null ? algorithm.getDescription() : "");
                propertiesPanel.removeAll();
                if (algorithm != null)
                {
                    propertiesPanel.add(BorderLayout.CENTER, new JScrollPane(properties));
                    propertiesPanel.revalidate();
                }
            }
        });

        final JList algorithmsList2 = new JList(selectedAlgs);
        algorithmsList2.setSelectedIndex(0);
        algorithmsList2.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        {// Title
            JLabel title = new JLabel(
                ApplicationUtilities.getResourceString("metatopkwizard.algorithm.title"),
                JLabel.LEFT);
            title.setFont(new Font(title.getFont().getFontName(), Font.BOLD, title.getFont()
                .getSize() + 6));
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.weightx = 1;
            gbcl.fill = GridBagConstraints.HORIZONTAL;
            gbcl.insets = new Insets(0, 0, 10, 0);
            gbcl.anchor = GridBagConstraints.NORTHWEST;
            center.add(title, gbcl);
        }

        {// Explanation
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = 1;
            gbcl.weightx = 1;
            gbcl.fill = GridBagConstraints.HORIZONTAL;
            gbcl.insets = new Insets(0, 0, 10, 0);
            gbcl.anchor = GridBagConstraints.WEST;
            center.add(
                new MultilineLabel(ApplicationUtilities
                    .getResourceString("metatopkwizard.algorithm.explanation")), gbcl);
        }

        {// Algorithms
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = 2;
            gbcl.insets = new Insets(0, 10, 0, 0);
            gbcl.anchor = GridBagConstraints.WEST;
            JLabel algorithmsLabel = new JLabel(
                ApplicationUtilities.getResourceString("metatopkwizard.algorithms.available") + ":");
            center.add(algorithmsLabel, gbcl);

            gbcl.gridy = 3;
            gbcl.insets = new Insets(0, 10, 10, 15);
            gbcl.weightx = 1.0;
            gbcl.weighty = 1.0;
            gbcl.fill = GridBagConstraints.BOTH;
            center.add(new JScrollPane(algorithmsList), gbcl);
        }

        {
            GridBagConstraints gbcl = new GridBagConstraints();
            JPanel algButtonsPanel = new JPanel();
            JButton select = new JButton(ApplicationUtilities.getImage("down.gif"));
            select.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    algorithm = (AbstractAlgorithm) algorithmsList.getSelectedValue();
                    if (!selectedAlgs.contains(algorithm))
                    {
                        selectedAlgs.add(algorithm);
                        algorithmsList2.setListData(selectedAlgs);
                        if (properties != null)
                        {
                            HashMap<Object, Object> dataMap = ((PropertiesTableModel) properties
                                .getModel()).getDataMap();
                            AlgorithmsGuiFactory.getAlgorithmGui(algorithm).updateProperties(dataMap);
                        }
                        nextButton.setEnabled(true);
                    }

                }
            });
            algButtonsPanel.add(select);
            JButton remove = new JButton(ApplicationUtilities.getImage("up.gif"));
            remove.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    Object[] values = algorithmsList2.getSelectedValues();
                    for (int i = 0; i < values.length; i++)
                    {
                        if (selectedAlgs.contains(values[i]))
                        {
                            selectedAlgs.remove(values[i]);
                            if (selectedAlgs.isEmpty())
                                nextButton.setEnabled(false);
                        }
                    }
                    if (values.length > 0)
                        algorithmsList2.setListData(selectedAlgs);
                }
            });
            algButtonsPanel.add(remove);
            gbcl.gridy = 4;
            gbcl.insets = new Insets(0, 10, 0, 0);
            gbcl.anchor = GridBagConstraints.CENTER;
            center.add(algButtonsPanel, gbcl);
        }

        {
            GridBagConstraints gbcl = new GridBagConstraints();
            // Algorithms 2
            gbcl.gridy = 5;
            gbcl.insets = new Insets(0, 10, 0, 0);
            gbcl.anchor = GridBagConstraints.WEST;
            JLabel algorithmsLabel2 = new JLabel(
                ApplicationUtilities.getResourceString("metatopkwizard.algorithms.selected") + ":");
            center.add(algorithmsLabel2, gbcl);

            gbcl.gridy = 6;
            gbcl.insets = new Insets(0, 10, 10, 15);
            gbcl.weightx = 1.0;
            gbcl.weighty = 1.0;
            gbcl.fill = GridBagConstraints.BOTH;
            center.add(new JScrollPane(algorithmsList2), gbcl);
        }

        {// Properties
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = 7;
            gbcl.insets = new Insets(0, 10, 0, 0);
            gbcl.anchor = GridBagConstraints.WEST;
            JLabel propertiesLabel = new JLabel(
                ApplicationUtilities.getResourceString("metatopkwizard.algorithms.properties") +
                    ":");
            center.add(propertiesLabel, gbcl);

            gbcl.gridy = 8;
            gbcl.insets = new Insets(0, 10, 10, 15);
            gbcl.weightx = 1.0;
            gbcl.weighty = 1.0;
            gbcl.fill = GridBagConstraints.BOTH;
            center.add(propertiesPanel, gbcl);
        }

        {// Description
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = 9;
            gbcl.insets = new Insets(0, 10, 0, 0);
            gbcl.anchor = GridBagConstraints.WEST;
            JLabel description = new JLabel(
                ApplicationUtilities.getResourceString("metatopkwizard.algorithms.description") +
                    ":");
            center.add(description, gbcl);

            gbcl.gridy = 10;
            gbcl.insets = new Insets(0, 10, 0, 15);
            gbcl.weightx = 1.0;
            gbcl.weighty = 1.0;
            gbcl.fill = GridBagConstraints.BOTH;
            center.add(new JScrollPane(txtExplanation), gbcl);
        }

        dialog.addWindowListener(new WindowAdapter()
        {
            public void windowOpened(WindowEvent e)
            {
                SwingUtilities.invokeLater(new Runnable()
                {
                    public void run()
                    {
                        algorithmsList.requestFocus();
                    }
                });
            }

            public void windowClosing(WindowEvent e)
            {
                status.setNextAction(WizardStatus.CANCEL_ACTION);
                dialog.dispose();
            }
        });
        dialog.setContentPane(panel);

        dialog.setVisible(true);
        switch (status.getNextAction())
        {
        case WizardStatus.BACK_ACTION:
            return useExactMapping();
        case WizardStatus.NEXT_ACTION:
            return selectAggregators();
        case WizardStatus.CANCEL_ACTION:
        default:
            return status;
        }
    }

    /**
     * Select the meta algorithms
     * 
     * @return a {@link WizardStatus}
     */
    public WizardStatus selectMetaAlgorithms()
    {
        final WizardStatus status = new WizardStatus();
        algorithm = (AbstractAlgorithm) availableAlgs.get(0);
        final MultilineLabel txtExplanation = new MultilineLabel(metaAlgorithmName);

        final JDialog dialog = new JDialog(parent,
            ApplicationUtilities.getResourceString("mergewizard.windowTitle"), true);
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        dialog.setSize(new Dimension(ApplicationUtilities
            .getIntProperty("metatopkwizard.algorithm.width"), ApplicationUtilities
            .getIntProperty("metatopkwizard.algorithm.height")));
        dialog.setLocationRelativeTo(parent);
        dialog.setResizable(false);

        JPanel south = new JPanel();
        south.setLayout(new FlowLayout(FlowLayout.RIGHT));
        south.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.black),
            BorderFactory.createEmptyBorder(0, 10, 10, 10)));
        JButton backButton;
        south.add(backButton = new JButton(ApplicationUtilities
            .getResourceString("mergewizard.button.back")));
        backButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                status.setNextAction(WizardStatus.BACK_ACTION);
                dialog.dispose();
            }
        });
        final JButton nextButton;
        south.add(nextButton = new JButton(ApplicationUtilities
            .getResourceString("mergewizard.button.next")));
        dialog.getRootPane().setDefaultButton(nextButton);
        nextButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                status.setNextAction(WizardStatus.NEXT_ACTION);
                dialog.dispose();
            }
        });
        JButton cancelButton;
        south.add(cancelButton = new JButton(ApplicationUtilities
            .getResourceString("mergewizard.button.cancel")));
        cancelButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                status.setNextAction(WizardStatus.CANCEL_ACTION);
                dialog.dispose();
            }
        });
        panel.add(BorderLayout.SOUTH, south);

        JLabel west = new JLabel(ApplicationUtilities.getImage("metabanner.gif"));
        west.setBorder(BorderFactory.createLineBorder(Color.black));
        panel.add(BorderLayout.WEST, west);

        JPanel center = new JPanel(new GridBagLayout());
        panel.add(BorderLayout.CENTER, center);
        center.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));

        final JList algorithmsList = new JList(availableMetaAlgsNames);
        algorithmsList.setSelectedIndex(0);
        algorithmsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        algorithmsList.addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent e)
            {
                metaAlgorithmName = (String) algorithmsList.getSelectedValue();
                txtExplanation.setText(metaAlgorithmName);
            }
        });

        final JList algorithmsList2 = new JList(selectedAlgs);
        algorithmsList2.setSelectedIndex(0);
        algorithmsList2.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        JTextField kParam = new JTextField("1");
        kParam.setPreferredSize(new Dimension(30, 20));

        {// Title
            JLabel title = new JLabel(
                ApplicationUtilities.getResourceString("metatopkwizard.metaalgorithm.title"),
                JLabel.LEFT);
            title.setFont(new Font(title.getFont().getFontName(), Font.BOLD, title.getFont()
                .getSize() + 6));
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.weightx = 1;
            gbcl.fill = GridBagConstraints.HORIZONTAL;
            gbcl.insets = new Insets(0, 0, 0, 0);
            gbcl.anchor = GridBagConstraints.NORTHWEST;
            center.add(title, gbcl);
        }

        {// Explanation
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = 1;
            gbcl.weightx = 1;
            gbcl.fill = GridBagConstraints.HORIZONTAL;
            gbcl.insets = new Insets(0, 0, 10, 0);
            gbcl.anchor = GridBagConstraints.WEST;
            center.add(
                new MultilineLabel(ApplicationUtilities
                    .getResourceString("metatopkwizard.metaalgorithm.explanation")), gbcl);
        }

        {// Algorithms
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = 2;
            gbcl.insets = new Insets(0, 10, 0, 0);
            gbcl.anchor = GridBagConstraints.WEST;
            JLabel algorithmsLabel = new JLabel(
                ApplicationUtilities.getResourceString("metatopkwizard.metaalgorithm.available") +
                    ":");
            center.add(algorithmsLabel, gbcl);

            gbcl.gridy = 3;
            gbcl.insets = new Insets(0, 10, 10, 15);
            gbcl.weightx = 1.0;
            gbcl.weighty = 0.1;
            gbcl.fill = GridBagConstraints.BOTH;
            center.add(new JScrollPane(algorithmsList), gbcl);
        }

        {// K
            GridBagConstraints gbcl = new GridBagConstraints();
            JPanel kPanel = new JPanel();
            JLabel algorithmsLabel = new JLabel(
                ApplicationUtilities.getResourceString("metatopkwizard.metaalgorithm.k") + ":");
            gbcl.anchor = GridBagConstraints.WEST;
            kPanel.add(algorithmsLabel, gbcl);
            gbcl.fill = GridBagConstraints.EAST;
            kPanel.add(kParam, gbcl);

            gbcl.gridy = 4;
            gbcl.insets = new Insets(0, 10, 10, 15);
            gbcl.weightx = 1.0;
            gbcl.weighty = 0.1;
            gbcl.anchor = GridBagConstraints.WEST;
            center.add(kPanel, gbcl);
        }

        {// Description
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = 5;
            gbcl.insets = new Insets(0, 10, 0, 0);
            gbcl.anchor = GridBagConstraints.WEST;
            JLabel description = new JLabel(
                ApplicationUtilities.getResourceString("metatopkwizard.metaalgorithm.description") +
                    ":");
            center.add(description, gbcl);

            gbcl.gridy = 6;
            gbcl.insets = new Insets(0, 10, 0, 15);
            gbcl.weightx = 1.0;
            gbcl.weighty = 1.0;
            gbcl.fill = GridBagConstraints.BOTH;
            center.add(new JScrollPane(txtExplanation), gbcl);
        }

        dialog.addWindowListener(new WindowAdapter()
        {
            public void windowOpened(WindowEvent e)
            {
                SwingUtilities.invokeLater(new Runnable()
                {
                    public void run()
                    {
                        algorithmsList.requestFocus();
                    }
                });
            }

            public void windowClosing(WindowEvent e)
            {
                status.setNextAction(WizardStatus.CANCEL_ACTION);
                dialog.dispose();
            }
        });
        dialog.setContentPane(panel);

        dialog.setVisible(true);
        switch (status.getNextAction())
        {
        case WizardStatus.BACK_ACTION:
            return selectAggregators();
        case WizardStatus.NEXT_ACTION:
            k = Integer.parseInt(kParam.getText());
        case WizardStatus.CANCEL_ACTION:
        default:
            return status;
        }
    }

    /**
     * Select aggregators
     * 
     * @return a {@link WizardStatus}
     */
    public WizardStatus selectAggregators()
    {
        final WizardStatus status = new WizardStatus();
        algorithm = (AbstractAlgorithm) availableAlgs.get(0);
        final MultilineLabel txtExplanation = new MultilineLabel("");
        final JPanel propertiesPanel = new JPanel(new BorderLayout());
        properties = AlgorithmsGuiFactory.getAlgorithmGui(algorithm).getProperties();
        propertiesPanel.add(BorderLayout.CENTER, new JScrollPane(properties));
        propertiesPanel.setBorder(BorderFactory.createLineBorder(Color.black));

        final JDialog dialog = new JDialog(parent,
            ApplicationUtilities.getResourceString("mergewizard.windowTitle"), true);
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        dialog.setSize(new Dimension(ApplicationUtilities
            .getIntProperty("metatopkwizard.algorithm.width"), ApplicationUtilities
            .getIntProperty("metatopkwizard.algorithm.height")));
        dialog.setLocationRelativeTo(parent);
        dialog.setResizable(false);

        JPanel south = new JPanel();
        south.setLayout(new FlowLayout(FlowLayout.RIGHT));
        south.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.black),
            BorderFactory.createEmptyBorder(0, 10, 10, 10)));
        JButton backButton;
        south.add(backButton = new JButton(ApplicationUtilities
            .getResourceString("mergewizard.button.back")));
        backButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                status.setNextAction(WizardStatus.BACK_ACTION);
                dialog.dispose();
            }
        });
        final JButton nextButton;
        south.add(nextButton = new JButton(ApplicationUtilities
            .getResourceString("mergewizard.button.next")));
        dialog.getRootPane().setDefaultButton(nextButton);
        nextButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                status.setNextAction(WizardStatus.NEXT_ACTION);
                dialog.dispose();
            }
        });
        JButton cancelButton;
        south.add(cancelButton = new JButton(ApplicationUtilities
            .getResourceString("mergewizard.button.cancel")));
        cancelButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                status.setNextAction(WizardStatus.CANCEL_ACTION);
                dialog.dispose();
            }
        });
        panel.add(BorderLayout.SOUTH, south);

        JLabel west = new JLabel(ApplicationUtilities.getImage("metabanner.gif"));
        west.setBorder(BorderFactory.createLineBorder(Color.black));
        panel.add(BorderLayout.WEST, west);

        JPanel center = new JPanel(new GridBagLayout());
        panel.add(BorderLayout.CENTER, center);
        center.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));

        Vector<AbstractLocalAggregator> fi = new Vector<AbstractLocalAggregator>();
        fi.add(availableLocalFAggs.get(0));
        Vector<AbstractGlobalAggregator> Fi = new Vector<AbstractGlobalAggregator>();
        Fi.add(availableGlobalFAggs.get(0));
        final JList aggregatorList1 = new JList(availableLocalFAggs);
        final JList aggregatorList2 = new JList(availableGlobalFAggs);
        final JList aggregatorList3 = new JList(fi);
        final JList aggregatorList4 = new JList(Fi);

        globalF = new SumGlobalAggregator();
        localf = new SumLocalAggregator();
        globalH = new SumGlobalAggregator();
        localh = new SumLocalAggregator();

        aggregatorList1.setSelectedIndex(0);
        aggregatorList1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        aggregatorList2.setSelectedIndex(0);
        aggregatorList2.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // aggregatorList3.setSelectedIndex(0);
        aggregatorList3.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // aggregatorList4.setSelectedIndex(0);
        aggregatorList4.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        aggregatorList1.addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent e)
            {
                // AbstractLocalAggregator aggregator;
                localf = (AbstractLocalAggregator) aggregatorList1.getSelectedValue();
                if (localf == null)
                    return;
                txtExplanation.setText("Local " + localf.toString() + " f aggregator");
                if (localf.getAggregatorType().indexOf("Average") != -1)
                {
                    ThresholdDialog td = new ThresholdDialog(dialog,
                        ((AverageLocalAggregator) localf).getThreshold());
                    td.setVisible(true);
                    ((AverageLocalAggregator) localf).setThreshold(td.getThreshold());
                }

                AbstractLocalAggregator hl;
                double th = -1;
                Vector<AbstractLocalAggregator> aggrs = new Vector<AbstractLocalAggregator>();
                Iterator<AbstractLocalAggregator> it = availableLocalHAggs.iterator();
                while (it.hasNext())
                {
                    hl = it.next();

                    if (DominantsMap.isDominated(hl.getOrderKey(), localf.getOrderKey()))
                    {
                        aggrs.add(hl);
                        if (localf.getOrderKey().equals("AVG") && hl.getOrderKey().equals("AVG"))
                        {
                            th = ((AverageLocalAggregator) localf).getThreshold();
                            ((AverageLocalAggregator) hl).setThreshold(th);
                        }
                    }
                }
                aggregatorList3.setListData(aggrs);
                aggregatorList3.setSelectedIndex(0);

            }
        });

        aggregatorList2.addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent e)
            {
                // AbstractGlobalAggregator aggregator;
                globalF = (AbstractGlobalAggregator) aggregatorList2.getSelectedValue();
                if (globalF == null)
                    return;
                txtExplanation.setText("Global " + globalF.toString() + " F aggregator");
                if (globalF.getAggregatorType().indexOf("Average") != -1)
                {
                    ThresholdDialog td = new ThresholdDialog(dialog,
                        ((AverageGlobalAggregator) globalF).getThreshold());
                    td.setVisible(true);
                    ((AverageGlobalAggregator) globalF).setThreshold(td.getThreshold());

                }

                AbstractGlobalAggregator hg;
                double th = -1;
                Vector<AbstractGlobalAggregator> aggrs = new Vector<AbstractGlobalAggregator>();
                Iterator<AbstractGlobalAggregator> it = availableGlobalHAggs.iterator();
                while (it.hasNext())
                {
                    hg = it.next();
                    if (DominantsMap.isDominated(hg.getOrderKey(), globalF.getOrderKey()))
                    {
                        aggrs.add(hg);
                        if (globalF.getOrderKey().equals("AVG") && hg.getOrderKey().equals("AVG"))
                        {
                            th = ((AverageGlobalAggregator) globalF).getThreshold();
                            ((AverageGlobalAggregator) hg).setThreshold(th);
                        }
                    }
                }
                aggregatorList4.setListData(aggrs);
                aggregatorList4.setSelectedIndex(0);

            }
        });

        aggregatorList3.addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent e)
            {
                // AbstractLocalAggregator aggregator;
                localh = (AbstractLocalAggregator) aggregatorList3.getSelectedValue();
                if (localh == null)
                    return;
                txtExplanation.setText("Local " + localh.toString() + " h aggregator");
                if (localh.getAggregatorType().indexOf("Average") != -1)
                {
                    double cachedThreshold = ((AverageLocalAggregator) localh).getThreshold();
                    ThresholdDialog td = new ThresholdDialog(dialog,
                        ((AverageLocalAggregator) localh).getThreshold());
                    td.setVisible(true);
                    ((AverageLocalAggregator) localh).setThreshold(Math.max(cachedThreshold,
                        td.getThreshold()));

                }

            }
        });

        aggregatorList4.addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent e)
            {
                // AbstractGlobalAggregator aggregator;
                globalH = (AbstractGlobalAggregator) aggregatorList4.getSelectedValue();
                if (globalH == null)
                    return;
                txtExplanation.setText("Global " + globalH.toString() + " H aggregator");
                if (globalH.getAggregatorType().indexOf("Average") != -1)
                {
                    double cachedThreshold = ((AverageGlobalAggregator) globalH).getThreshold();
                    ThresholdDialog td = new ThresholdDialog(dialog,
                        ((AverageGlobalAggregator) globalH).getThreshold());
                    td.setVisible(true);
                    ((AverageGlobalAggregator) globalH).setThreshold(Math.max(cachedThreshold,
                        td.getThreshold()));
                }

            }
        });

        {// Title
            JLabel title = new JLabel(
                ApplicationUtilities.getResourceString("metatopkwizard.aggregators.title"),
                JLabel.LEFT);
            title.setFont(new Font(title.getFont().getFontName(), Font.BOLD, title.getFont()
                .getSize() + 6));
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.weightx = 1;
            gbcl.fill = GridBagConstraints.HORIZONTAL;
            gbcl.insets = new Insets(0, 0, 10, 0);
            gbcl.anchor = GridBagConstraints.NORTHWEST;
            center.add(title, gbcl);
        }

        {// Explanation
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = 1;
            gbcl.weightx = 1;
            gbcl.fill = GridBagConstraints.HORIZONTAL;
            gbcl.insets = new Insets(0, 0, 10, 0);
            gbcl.anchor = GridBagConstraints.WEST;
            center.add(
                new MultilineLabel(ApplicationUtilities
                    .getResourceString("metatopkwizard.aggregators.explanation")), gbcl);
        }

        {// Algorithms
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = 2;
            gbcl.insets = new Insets(0, 10, 0, 0);
            gbcl.anchor = GridBagConstraints.WEST;
            JLabel algorithmsLabel = new JLabel(
                ApplicationUtilities.getResourceString("metatopkwizard.aggregators.availablefl") +
                    ":");
            center.add(algorithmsLabel, gbcl);

            gbcl.gridy = 3;
            gbcl.insets = new Insets(0, 10, 10, 15);
            gbcl.weightx = 1.0;
            gbcl.weighty = 1.0;
            gbcl.fill = GridBagConstraints.BOTH;
            center.add(new JScrollPane(aggregatorList1), gbcl);
        }

        {
            GridBagConstraints gbcl = new GridBagConstraints();
            // Algorithms 2
            gbcl.gridy = 5;
            gbcl.insets = new Insets(0, 10, 0, 0);
            gbcl.anchor = GridBagConstraints.WEST;
            JLabel algorithmsLabel2 = new JLabel(
                ApplicationUtilities.getResourceString("metatopkwizard.aggregators.availablefg") +
                    ":");
            center.add(algorithmsLabel2, gbcl);

            gbcl.gridy = 6;
            gbcl.insets = new Insets(0, 10, 10, 15);
            gbcl.weightx = 1.0;
            gbcl.weighty = 1.0;
            gbcl.fill = GridBagConstraints.BOTH;
            center.add(new JScrollPane(aggregatorList2), gbcl);
        }

        {
            GridBagConstraints gbcl = new GridBagConstraints();
            // Algorithms 3
            gbcl.gridy = 7;
            gbcl.insets = new Insets(0, 10, 0, 0);
            gbcl.anchor = GridBagConstraints.WEST;
            JLabel algorithmsLabel3 = new JLabel(
                ApplicationUtilities.getResourceString("metatopkwizard.aggregators.availablehl") +
                    ":");
            center.add(algorithmsLabel3, gbcl);

            gbcl.gridy = 8;
            gbcl.insets = new Insets(0, 10, 10, 15);
            gbcl.weightx = 1.0;
            gbcl.weighty = 1.0;
            gbcl.fill = GridBagConstraints.BOTH;
            center.add(new JScrollPane(aggregatorList3), gbcl);
        }

        {
            GridBagConstraints gbcl = new GridBagConstraints();
            // Algorithms 4
            gbcl.gridy = 9;
            gbcl.insets = new Insets(0, 10, 0, 0);
            gbcl.anchor = GridBagConstraints.WEST;
            JLabel algorithmsLabel4 = new JLabel(
                ApplicationUtilities.getResourceString("metatopkwizard.aggregators.availablehg") +
                    ":");
            center.add(algorithmsLabel4, gbcl);

            gbcl.gridy = 10;
            gbcl.insets = new Insets(0, 10, 10, 15);
            gbcl.weightx = 1.0;
            gbcl.weighty = 1.0;
            gbcl.fill = GridBagConstraints.BOTH;
            center.add(new JScrollPane(aggregatorList4), gbcl);
        }

        {// Description
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = 11;
            gbcl.insets = new Insets(0, 10, 0, 0);
            gbcl.anchor = GridBagConstraints.WEST;
            JLabel description = new JLabel(
                ApplicationUtilities.getResourceString("metatopkwizard.aggregators.description") +
                    ":");
            center.add(description, gbcl);

            gbcl.gridy = 12;
            gbcl.insets = new Insets(0, 10, 0, 15);
            gbcl.weightx = 1.0;
            gbcl.weighty = 1.0;
            gbcl.fill = GridBagConstraints.BOTH;
            center.add(new JScrollPane(txtExplanation), gbcl);
        }

        dialog.addWindowListener(new WindowAdapter()
        {
            public void windowOpened(WindowEvent e)
            {
                SwingUtilities.invokeLater(new Runnable()
                {
                    public void run()
                    {
                        aggregatorList2.requestFocus();
                    }
                });
            }

            public void windowClosing(WindowEvent e)
            {
                status.setNextAction(WizardStatus.CANCEL_ACTION);
                dialog.dispose();
            }
        });
        dialog.setContentPane(panel);

        dialog.setVisible(true);
        switch (status.getNextAction())
        {
        case WizardStatus.BACK_ACTION:
            return selectAlgorithms();
        case WizardStatus.NEXT_ACTION:
            return selectMetaAlgorithms();
        case WizardStatus.CANCEL_ACTION:
        default:
            return status;
        }
    }

    /**
     * Show match information
     * 
     * @return a {@link WizardStatus}
     */
    public WizardStatus showMatchInformation()
    {
        // if(ApplicationParameters.result)
        // System.out.println("Algorithm Results:\n\n" + matchInformation.getReport() + "\n\n\n");

        final WizardStatus status = new WizardStatus();
        final JDialog dialog = new JDialog(parent,
            ApplicationUtilities.getResourceString("mergewizard.windowTitle"), true);
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        dialog.setSize(new Dimension(ApplicationUtilities
            .getIntProperty("mergewizard.matchInformation.width"), ApplicationUtilities
            .getIntProperty("mergewizard.matchInformation.height")));
        dialog.setLocationRelativeTo(parent);
        // dialog.setResizable(false);

        JPanel south = new JPanel();
        south.setLayout(new FlowLayout(FlowLayout.RIGHT));
        south.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.black),
            BorderFactory.createEmptyBorder(0, 10, 10, 10)));
        // JButton saveButton;
        // south.add(saveButton=new
        // JButton(ApplicationUtilities.getResourceString("mergewizard.button.save")));
        // saveButton.setToolTipText(ApplicationUtilities.getResourceString("mergewizard.button.save.tooltip"));
        // saveButton.setMnemonic(KeyStroke.getKeyStroke(ApplicationUtilities.getResourceString("mergewizard.button.save.mnemonic")).getKeyCode());
        // CSH.setHelpIDString(saveButton,ApplicationUtilities.getResourceString("mergewizard.button.save.helpID"));
        // saveButton.addActionListener(new ActionListener()
        // {
        // public void actionPerformed(ActionEvent e)
        // {
        // // Filters
        // ArrayList filters=new ArrayList();
        // filters.add(XMLUtilities.xmlFileFilter);
        // filters.add(TextUtilities.textFileFilter);
        // FileUtilities.configureFileDialogFilters(filters);
        // // Viewers
        // FileUtilities.fileViewer.removeAllViewers();
        // FileUtilities.fileViewer.addViewer(XMLUtilities.xmlFileViewer);
        // FileUtilities.fileViewer.addViewer(TextUtilities.textFileViewer);
        // File file=FileUtilities.saveFileDialog(null,new File(ontologyName + "-match.txt"));
        // if(file!=null)
        // {
        // try
        // {
        // String extFile=file.getAbsolutePath();
        // String ext=FileUtilities.getFileExtension(file);
        // short format=MatchInformation.TEXT_FORMAT;
        // javax.swing.filechooser.FileFilter filter=FileUtilities.fileChooser.getFileFilter();
        // if(filter instanceof TextFileFilter)
        // {
        // format=MatchInformation.TEXT_FORMAT;
        // if(ext==null || !ext.equalsIgnoreCase("txt"))
        // extFile+=".txt";
        // }
        // else if(filter instanceof XMLFileFilter)
        // {
        // format=MatchInformation.XML_FORMAT;;
        // if(ext==null || !ext.equalsIgnoreCase("xml"))
        // extFile+=".xml";
        // }
        // //TODO: matchInformation.save(new File(extFile),format);
        // }
        // catch(Exception ex)
        // {
        // JOptionPane.showMessageDialog(null,ApplicationUtilities.getResourceString("error") + ": "
        // +
        // ex.getMessage(),ApplicationUtilities.getResourceString("error"),JOptionPane.ERROR_MESSAGE);
        // }
        // }
        // }
        // }
        // );
        JButton cancelButton;
        south.add(cancelButton = new JButton(ApplicationUtilities
            .getResourceString("mergewizard.button.cancel")));
        cancelButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                status.setNextAction(WizardStatus.CANCEL_ACTION);
                dialog.dispose();
            }
        });
        JButton finishButton;
        south.add(finishButton = new JButton(ApplicationUtilities
            .getResourceString("mergewizard.button.finish")));
        finishButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                status.setNextAction(WizardStatus.FINISH_ACTION);
                dialog.dispose();
            }
        });
        panel.add(BorderLayout.SOUTH, south);

        JLabel west = new JLabel(ApplicationUtilities.getImage("metabanner.gif"));
        west.setBorder(BorderFactory.createLineBorder(Color.black));
        panel.add(BorderLayout.WEST, west);

        JPanel center = new JPanel(new GridBagLayout());
        panel.add(BorderLayout.CENTER, center);
        center.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        {// Title
            JLabel title = new JLabel(
                ApplicationUtilities.getResourceString("mergewizard.matchInformation.title"),
                JLabel.LEFT);
            title.setFont(new Font(title.getFont().getFontName(), Font.BOLD, title.getFont()
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
            gbcl.weightx = 1;
            gbcl.gridwidth = 2;
            gbcl.fill = GridBagConstraints.HORIZONTAL;
            gbcl.insets = new Insets(0, 0, 10, 0);
            gbcl.anchor = GridBagConstraints.WEST;
            center.add(
                new MultilineLabel(ApplicationUtilities
                    .getResourceString("mergewizard.matchInformation.explanation")), gbcl);
        }

        {// Target Ontology
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = 2;
            gbcl.insets = new Insets(0, 5, 0, 5);
            gbcl.anchor = GridBagConstraints.EAST;
            JLabel targetOntologyLabel = new JLabel(
                ApplicationUtilities.getResourceString("mergewizard.matchInformation.target") + ":");
            center.add(targetOntologyLabel, gbcl);

            gbcl.gridx = 1;
            gbcl.weightx = 1;
            gbcl.anchor = GridBagConstraints.WEST;
            gbcl.fill = GridBagConstraints.HORIZONTAL;
            center.add(new JLabel(target.getName()), gbcl);
        }

        {// Candidate Ontology
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = 3;
            gbcl.anchor = GridBagConstraints.EAST;
            gbcl.insets = new Insets(0, 5, 0, 5);
            JLabel candidateOntologyLabel = new JLabel(
                ApplicationUtilities.getResourceString("mergewizard.matchInformation.candidate") +
                    ":");
            center.add(candidateOntologyLabel, gbcl);

            gbcl.gridx = 1;
            gbcl.weightx = 1;
            gbcl.anchor = GridBagConstraints.WEST;
            gbcl.fill = GridBagConstraints.HORIZONTAL;
            center.add(new JLabel(candidate.getName()), gbcl);
        }

        {// Information
            JTabbedPane informationPanel = new JTabbedPane();

            informationPanel.addTab(metaAlgorithmName,
                generateTabbedPaneForMetaMatcher(topKMatchings));

            Iterator<String> matchersInfo = matchInfos.keySet().iterator();
            String algName;
            while (matchersInfo.hasNext())
            {
                algName = matchersInfo.next();
                informationPanel.addTab(algName,
                    generateTabbedPaneForMatcher((MatchInformation) matchInfos.get(algName)));
            }

            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = 9;
            gbcl.weightx = 1.0;
            gbcl.weighty = 1.0;
            gbcl.gridwidth = 2;
            gbcl.fill = GridBagConstraints.BOTH;
            gbcl.insets = new Insets(0, 5, 10, 5);
            center.add(informationPanel, gbcl);
        }

        dialog.addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                dialog.dispose();
                status.setNextAction(WizardStatus.CANCEL_ACTION);
            }
        });
        dialog.setContentPane(panel);

        dialog.setVisible(true);
        return status;
    }

    /**
     * Generate a tabbed pane for the meta matcher
     * 
     * @param matches a vector of {@link MatchInformation}
     * @return a {@link JTabbedPane}
     */
    private JTabbedPane generateTabbedPaneForMetaMatcher(final Vector<MatchInformation> matches)
    {
    	MatchInformation match = matches.get(0);
        MatchMatrix mmatrix = (MatchMatrix) metaAlg.getMatchMatrix(0);
        if (mmatrix == null)
        {
            mmatrix = (MatchMatrix) metaAlg.getCombinedMatrix();
        }
        final MatchInformation matchInformation = match.clone();
//        matchInformation.setTargetOntologyTermsTotal(mmatrix.getCandidateTerms().size());
//        matchInformation.setTargetOntology(target);
//        matchInformation.setCandidateOntologyTermsTotal(mmatrix.getTargetTerms().size());
//        matchInformation.setCandidateOntology(candidate);
        matchInformation.setMetaAlgorithm(metaAlg);
//        matchInformation.setOriginalCandidateTerms(mmatrix.getTargetTerms());
//        matchInformation.setOriginalTargetTerms(mmatrix.getCandidateTerms());
        matchInformation.setMatrix(mmatrix);
        final MatchInformationGui matchInformationGui = new MatchInformationGui(matchInformation);
        // Information
        JTabbedPane informationPanel = new JTabbedPane();

        JPanel statisticsPanel = new JPanel();

        statisticsPanel.setLayout(new BoxLayout(statisticsPanel, BoxLayout.Y_AXIS));
        statisticsPanel.add(new JScrollPane(matchInformationGui.getStatistics(exactMapping)));

        if (exactMapping != null)
        {
            XYSeries xy = new XYSeries(metaAlgorithmName);
            TopKPlot kplot = metaAlg.getStatistics().getMappingsPlot();
            for (int i = 0; i < k; i++)
            {
                XYDataPair pair = new XYDataPair(new Integer(i + 1), new Integer(
                    (int) kplot.getYPlot(i)));
                try
                {
                    xy.add(pair);
                }
                catch (SeriesException e1)
                {

                }
            }

            XYSeriesCollection dataset = new XYSeriesCollection();
            dataset.addSeries(xy);

            // create the chart...
            JFreeChart chart = ChartFactory.createXYChart("Statistics", // chart title

                "Top-K Generated Mappings", // domain axis label
                "Total Iterations", // range axis label
                dataset, // data
                true // include legend
                );

            chart.setBackgroundPaint(Color.white);
            JFreeChartPanel chartPanel = new JFreeChartPanel(chart);
            chartPanel.setBackground(statisticsPanel.getBackground());
            chartPanel.setForeground(statisticsPanel.getForeground());
            statisticsPanel.add(chartPanel);

            if (exactMapping != null)
            {
                int numMatches = matches.size();
                XYSeries pAtK = new XYSeries("Precision@K");
                XYSeries rAtK = new XYSeries("Recall@K");
                for (int i = 0; i < numMatches; i++)
                {
                	MatchInformation mi = matches.get(i);
                    try
                    {
                        pAtK.add(new XYDataPair(new Integer(i + 1), new Double(
                            mi.getPrecision(exactMapping))));
                        rAtK.add(new XYDataPair(new Integer(i + 1), new Double(
                        		mi.getRecall(exactMapping))));
                    }
                    catch (SeriesException e1)
                    {

                    }
                }

                dataset = new XYSeriesCollection();
                dataset.addSeries(pAtK);
                dataset.addSeries(rAtK);
                chart = ChartFactory.createXYChart("", // chart title

                    "Top-K Generated Mappings", // domain axis label
                    "%", // range axis label
                    dataset, // data
                    true // include legend
                    );

                chart.setBackgroundPaint(Color.white);
                chartPanel = new JFreeChartPanel(chart);
                chartPanel.setBackground(statisticsPanel.getBackground());
                chartPanel.setForeground(statisticsPanel.getForeground());
                statisticsPanel.add(chartPanel);
            }
        }

        informationPanel.addTab(
            ApplicationUtilities.getResourceString("mergewizard.matchInformation.statistics"),
            statisticsPanel);

        // Buttons
        final JButton removeMatchButton = new JButton(
            ApplicationUtilities.getResourceString("mergewizard.button.removeMatch"));
        new JButton(ApplicationUtilities.getResourceString("mergewizard.button.match"));
        new JLabel("", null, JLabel.CENTER);
        new JCheckBox(
            ApplicationUtilities.getResourceString("mergewizard.checkbox.updateThesaurus"));

        // Match Table
        final JTable matchTable = matchInformationGui.getMatchTable();
        matchTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        matchTable.setRowSelectionAllowed(true);
        matchTable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent e)
            {
                if (e.getValueIsAdjusting())
                    return;
                ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                if (lsm.isSelectionEmpty())
                    removeMatchButton.setEnabled(false);
                else
                {
                    lsm.getMinSelectionIndex();
                    removeMatchButton.setEnabled(true);
                }
            }
        });

        // // Mismatch Lists
        // final JList targetMismatchesList=new
        // JList(matchInformation.getMismatchesTargetOntology().toArray());
        // final JList candidateMismatchesList=new
        // JList(matchInformation.getMismatchesCandidateOntology().toArray());
        //
        // targetMismatchesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // targetMismatchesList.getSelectionModel().addListSelectionListener(new
        // ListSelectionListener()
        // {
        // public void valueChanged(ListSelectionEvent e)
        // {
        // ListSelectionModel model=(ListSelectionModel)e.getSource();
        // targetMismatchIndex=model.getMinSelectionIndex();
        // matchButton.setEnabled(targetMismatchIndex!=-1 && candidateMismatchIndex!=-1);
        // Object targetObject=targetMismatchesList.getSelectedValue();
        // Object candidateObject=candidateMismatchesList.getSelectedValue();
        // mismatchMatchLabel.setText((targetObject!=null?targetObject:"") + (targetObject!=null &&
        // candidateObject!=null?" <--> ":"") + (candidateObject!=null?candidateObject:""));
        // }
        // }
        // );
        // candidateMismatchesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // candidateMismatchesList.getSelectionModel().addListSelectionListener(new
        // ListSelectionListener()
        // {
        // public void valueChanged(ListSelectionEvent e)
        // {
        // ListSelectionModel model=(ListSelectionModel)e.getSource();
        // candidateMismatchIndex=model.getMinSelectionIndex();
        // matchButton.setEnabled(targetMismatchIndex!=-1 && candidateMismatchIndex!=-1);
        // Object targetObject=targetMismatchesList.getSelectedValue();
        // Object candidateObject=candidateMismatchesList.getSelectedValue();
        // mismatchMatchLabel.setText((targetObject!=null?targetObject:"") + (targetObject!=null &&
        // candidateObject!=null?" <--> ":"") + (candidateObject!=null?candidateObject:""));
        // }
        // }
        // );
        //
        // // Target Mismatch Table
        // ArrayList mismatchesTargetOntology=matchInformation.getMismatchesTargetOntology();
        // Object
        // targetMismatchColumns[]={matchInformation.getTargetOntology().getName(),ApplicationUtilities.getResourceString("mergewizard.matchInformation.include")};
        // Object targetMismatchData[][]=new Object[mismatchesTargetOntology.size()][2];
        // for(int i=0;i<mismatchesTargetOntology.size();i++)
        // {
        // Mismatch mismatch=(Mismatch)mismatchesTargetOntology.get(i);
        // targetMismatchData[i][0]=mismatch;
        // targetMismatchData[i][1]=new Boolean(mismatch.isSelected());
        // }
        // final JTable mismatchesTargetTable=new JTable(new
        // DefaultTableModel(targetMismatchData,targetMismatchColumns)
        // {
        // public boolean isCellEditable(int row,int col)
        // {
        // return col==1;
        // }
        //
        // public Class getColumnClass(int col)
        // {
        // if(col==1) return Boolean.class;
        // return Object.class;
        // }
        //
        // public void setValueAt(Object aValue, int rowIndex, int columnIndex)
        // {
        // Mismatch m=(Mismatch)getValueAt(rowIndex,0);
        // m.setSelected(((Boolean)aValue).booleanValue());
        // super.setValueAt(aValue,rowIndex,columnIndex);
        // }
        // }
        // );
        //
        // // Candidate Mismatch Table
        // ArrayList mismatchesCandidateOntology=matchInformation.getMismatchesCandidateOntology();
        // Object
        // candidateMismatchColumns[]={matchInformation.getCandidateOntology().getName(),ApplicationUtilities.getResourceString("mergewizard.matchInformation.include")};
        // Object candidateMismatchData[][]=new Object[mismatchesCandidateOntology.size()][2];
        // for(int i=0;i<mismatchesCandidateOntology.size();i++)
        // {
        // Mismatch mismatch=(Mismatch)mismatchesCandidateOntology.get(i);
        // candidateMismatchData[i][0]=mismatch;
        // candidateMismatchData[i][1]=new Boolean(mismatch.isSelected());
        // }
        // final JTable mismatchesCandidateTable=new JTable(new
        // DefaultTableModel(candidateMismatchData,candidateMismatchColumns)
        // {
        // public boolean isCellEditable(int row,int col)
        // {
        // return col==1;
        // }
        //
        // public Class getColumnClass(int col)
        // {
        // if(col==1) return Boolean.class;
        // return Object.class;
        // }
        //
        // public void setValueAt(Object aValue, int rowIndex, int columnIndex)
        // {
        // Mismatch m=(Mismatch)getValueAt(rowIndex,0);
        // m.setSelected(((Boolean)aValue).booleanValue());
        // super.setValueAt(aValue,rowIndex,columnIndex);
        // }
        // }
        // );
        //
        // // Matches
        // JPanel matchesPanel=new JPanel(new BorderLayout());
        // JPanel matchesButtonPanel=new JPanel(new FlowLayout(FlowLayout.RIGHT));
        // removeMatchButton.setEnabled(false);
        // removeMatchButton.setToolTipText(ApplicationUtilities.getResourceString("mergewizard.button.removeMatch.tooltip"));
        // removeMatchButton.setMnemonic(KeyStroke.getKeyStroke(ApplicationUtilities.getResourceString("mergewizard.button.removeMatch.mnemonic")).getKeyCode());
        // CSH.setHelpIDString(removeMatchButton,ApplicationUtilities.getResourceString("mergewizard.button.removeMatch.helpID"));
        // removeMatchButton.addActionListener(new ActionListener()
        // {
        // public void actionPerformed(ActionEvent e)
        // {
        // int targetMismatches=matchInformation.getMismatchesTargetOntology().size();
        // int candidateMismatches=matchInformation.getMismatchesCandidateOntology().size();
        //
        // DefaultTableModel model=(DefaultTableModel)matchTable.getModel();
        // matchInformation.removeMatch(matchIndex);
        // model.removeRow(matchIndex);
        // // Get the new mismatches and update the tables
        // if(matchInformation.getMismatchesTargetOntology().size()>targetMismatches) // the
        // mismatches were updated
        // {
        // Mismatch
        // mismatchTarget=(Mismatch)matchInformation.getMismatchesTargetOntology().get(matchInformation.getMismatchesTargetOntology().size()-1);
        // DefaultTableModel targetModel=(DefaultTableModel)mismatchesTargetTable.getModel();
        // targetModel.addRow(new Object[] {mismatchTarget,new
        // Boolean(mismatchTarget.isSelected())});
        // targetMismatchesList.setListData(matchInformation.getMismatchesTargetOntology().toArray());
        // }
        // if(matchInformation.getMismatchesCandidateOntology().size()>candidateMismatches) // the
        // mismatches were updated
        // {
        // Mismatch
        // mismatchCandidate=(Mismatch)matchInformation.getMismatchesCandidateOntology().get(matchInformation.getMismatchesCandidateOntology().size()-1);
        // DefaultTableModel candidateModel=(DefaultTableModel)mismatchesCandidateTable.getModel();
        // candidateModel.addRow(new Object[] {mismatchCandidate,new
        // Boolean(mismatchCandidate.isSelected())});
        // candidateMismatchesList.setListData(matchInformation.getMismatchesCandidateOntology().toArray());
        // }
        // }
        // }
        // );
        // matchesButtonPanel.add(removeMatchButton);
        // matchesPanel.add(BorderLayout.SOUTH,matchesButtonPanel);
        // matchesPanel.add(BorderLayout.CENTER,new JScrollPane(matchTable));
        // informationPanel.addTab(ApplicationUtilities.getResourceString("mergewizard.matchInformation.matches"),matchesPanel);
        //
        // // Mismatches
        // JPanel mismatchesPanel=new JPanel(new BorderLayout());
        //
        // JPanel mismatchesInformationPanel=new JPanel(new GridBagLayout());
        // mismatchesInformationPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        // GridBagConstraints gbc=new GridBagConstraints();
        // gbc.insets=new Insets(10,10,5,10);
        // gbc.weightx=1;
        // mismatchesInformationPanel.add(new
        // JLabel(matchInformation.getTargetOntology().getName()),gbc);
        // gbc.gridx=1;
        // mismatchesInformationPanel.add(new
        // JLabel(matchInformation.getCandidateOntology().getName()),gbc);
        // gbc.weightx=0;
        // gbc.weighty=1;
        // gbc.insets=new Insets(0,10,20,10);
        // gbc.fill=GridBagConstraints.BOTH;
        // gbc.gridx=0;
        // gbc.gridy=1;
        // mismatchesInformationPanel.add(new JScrollPane(targetMismatchesList),gbc);
        // gbc.gridx=1;
        // mismatchesInformationPanel.add(new JScrollPane(candidateMismatchesList),gbc);
        // gbc.gridx=0;
        // gbc.gridy=2;
        // gbc.gridwidth=2;
        // gbc.weightx=1;
        // gbc.weighty=0;
        // gbc.insets=new Insets(10,20,20,20);
        // gbc.fill=GridBagConstraints.HORIZONTAL;
        // mismatchesInformationPanel.add(mismatchMatchLabel,gbc);
        // gbc.insets=new Insets(0,20,10,20);
        // gbc.gridy=3;
        // updateThesaurusCheckBox.setToolTipText(ApplicationUtilities.getResourceString("mergewizard.checkbox.updateThesaurus.tooltip"));
        // updateThesaurusCheckBox.setMnemonic(KeyStroke.getKeyStroke(ApplicationUtilities.getResourceString("mergewizard.checkbox.updateThesaurus.mnemonic")).getKeyCode());
        // CSH.setHelpIDString(updateThesaurusCheckBox,ApplicationUtilities.getResourceString("mergewizard.checkbox.updateThesaurus.helpID"));
        // mismatchesInformationPanel.add(updateThesaurusCheckBox,gbc);
        // mismatchesPanel.add(BorderLayout.CENTER,mismatchesInformationPanel);
        //
        // JPanel mismatchesButtonPanel=new JPanel(new FlowLayout(FlowLayout.RIGHT));
        // matchButton.setToolTipText(ApplicationUtilities.getResourceString("mergewizard.button.match.tooltip"));
        // matchButton.setEnabled(false);
        // matchButton.setMnemonic(KeyStroke.getKeyStroke(ApplicationUtilities.getResourceString("mergewizard.button.match.mnemonic")).getKeyCode());
        // CSH.setHelpIDString(matchButton,ApplicationUtilities.getResourceString("mergewizard.button.match.helpID"));
        // matchButton.addActionListener(new ActionListener()
        // {
        // public void actionPerformed(ActionEvent e)
        // {
        // Mismatch
        // targetMismatch=(Mismatch)matchInformation.getMismatchesTargetOntology().get(targetMismatchIndex);
        // Mismatch
        // candidateMismatch=(Mismatch)matchInformation.getMismatchesCandidateOntology().get(candidateMismatchIndex);
        // matchInformation.addMatch(targetMismatch.getTerm(),candidateMismatch.getTerm(),1);
        // DefaultTableModel matchModel=(DefaultTableModel)matchTable.getModel();
        // Term targetTerm=targetMismatch.getTerm();
        // Term candidateTerm=candidateMismatch.getTerm();
        // matchModel.addRow(new Object[] {targetTerm.getName() + " (" +
        // targetTerm.getAttributeValue("name") + ")",candidateTerm.getName() + " (" +
        // candidateTerm.getAttributeValue("name") + ")","100%"});
        //
        // DefaultTableModel
        // targetMismatchModel=(DefaultTableModel)mismatchesTargetTable.getModel();
        // targetMismatchModel.removeRow(targetMismatchIndex);
        //
        // DefaultTableModel
        // candidateMismatchModel=(DefaultTableModel)mismatchesCandidateTable.getModel();
        // candidateMismatchModel.removeRow(candidateMismatchIndex);
        //
        // targetMismatchesList.setListData(matchInformation.getMismatchesTargetOntology().toArray());
        // candidateMismatchesList.setListData(matchInformation.getMismatchesCandidateOntology().toArray());
        // }
        // }
        // );
        // mismatchesButtonPanel.add(matchButton);
        // mismatchesPanel.add(BorderLayout.SOUTH,mismatchesButtonPanel);
        // informationPanel.addTab(ApplicationUtilities.getResourceString("mergewizard.matchInformation.mismatches"),mismatchesPanel);
        //
        // // Mismatches Target
        // JPanel mismatchesTargetPanel=new JPanel(new BorderLayout());
        // JPanel mismatchesTargetButtonPanel=new JPanel(new FlowLayout(FlowLayout.RIGHT));
        // final JButton targetCheckAllButton=new
        // JButton(ApplicationUtilities.getResourceString("mergewizard.button.checkAll"));
        // targetCheckAllButton.setToolTipText(ApplicationUtilities.getResourceString("mergewizard.button.checkAll.tooltip"));
        // targetCheckAllButton.setMnemonic(KeyStroke.getKeyStroke(ApplicationUtilities.getResourceString("mergewizard.button.checkAll.mnemonic")).getKeyCode());
        // CSH.setHelpIDString(targetCheckAllButton,ApplicationUtilities.getResourceString("mergewizard.button.checkAll.helpID"));
        // targetCheckAllButton.addActionListener(new ActionListener()
        // {
        // public void actionPerformed(ActionEvent e)
        // {
        // DefaultTableModel model=(DefaultTableModel)mismatchesTargetTable.getModel();
        // for(int i=0;i<model.getRowCount();i++)
        // model.setValueAt(new Boolean(true),i,1);
        // }
        // }
        // );
        // mismatchesTargetButtonPanel.add(targetCheckAllButton);
        // final JButton targetUncheckAllButton=new
        // JButton(ApplicationUtilities.getResourceString("mergewizard.button.uncheckAll"));
        // targetUncheckAllButton.setToolTipText(ApplicationUtilities.getResourceString("mergewizard.button.uncheckAll.tooltip"));
        // targetUncheckAllButton.setMnemonic(KeyStroke.getKeyStroke(ApplicationUtilities.getResourceString("mergewizard.button.uncheckAll.mnemonic")).getKeyCode());
        // CSH.setHelpIDString(targetUncheckAllButton,ApplicationUtilities.getResourceString("mergewizard.button.uncheckAll.helpID"));
        // targetUncheckAllButton.addActionListener(new ActionListener()
        // {
        // public void actionPerformed(ActionEvent e)
        // {
        // DefaultTableModel model=(DefaultTableModel)mismatchesTargetTable.getModel();
        // for(int i=0;i<model.getRowCount();i++)
        // model.setValueAt(new Boolean(false),i,1);
        // }
        // }
        // );
        // mismatchesTargetButtonPanel.add(targetUncheckAllButton);
        // mismatchesTargetPanel.add(BorderLayout.SOUTH,mismatchesTargetButtonPanel);
        // mismatchesTargetPanel.add(BorderLayout.CENTER,new JScrollPane(mismatchesTargetTable));
        // informationPanel.addTab(ApplicationUtilities.getResourceString("mergewizard.matchInformation.mismatchesTarget"),mismatchesTargetPanel);
        //
        //
        // // Mismatches Candidate
        // JPanel mismatchesCandidatePanel=new JPanel(new BorderLayout());
        // JPanel mismatchesCandidateButtonPanel=new JPanel(new FlowLayout(FlowLayout.RIGHT));
        // final JButton candidateCheckAllButton=new
        // JButton(ApplicationUtilities.getResourceString("mergewizard.button.checkAll"));
        // candidateCheckAllButton.setToolTipText(ApplicationUtilities.getResourceString("mergewizard.button.checkAll.tooltip"));
        // candidateCheckAllButton.setMnemonic(KeyStroke.getKeyStroke(ApplicationUtilities.getResourceString("mergewizard.button.checkAll.mnemonic")).getKeyCode());
        // CSH.setHelpIDString(candidateCheckAllButton,ApplicationUtilities.getResourceString("mergewizard.button.checkAll.helpID"));
        // candidateCheckAllButton.addActionListener(new ActionListener()
        // {
        // public void actionPerformed(ActionEvent e)
        // {
        // DefaultTableModel model=(DefaultTableModel)mismatchesCandidateTable.getModel();
        // for(int i=0;i<model.getRowCount();i++)
        // model.setValueAt(new Boolean(true),i,1);
        // }
        // }
        // );
        // mismatchesCandidateButtonPanel.add(candidateCheckAllButton);
        // final JButton candidateUncheckAllButton=new
        // JButton(ApplicationUtilities.getResourceString("mergewizard.button.uncheckAll"));
        // candidateUncheckAllButton.setToolTipText(ApplicationUtilities.getResourceString("mergewizard.button.uncheckAll.tooltip"));
        // candidateUncheckAllButton.setMnemonic(KeyStroke.getKeyStroke(ApplicationUtilities.getResourceString("mergewizard.button.uncheckAll.mnemonic")).getKeyCode());
        // CSH.setHelpIDString(candidateUncheckAllButton,ApplicationUtilities.getResourceString("mergewizard.button.uncheckAll.helpID"));
        // candidateUncheckAllButton.addActionListener(new ActionListener()
        // {
        // public void actionPerformed(ActionEvent e)
        // {
        // DefaultTableModel model=(DefaultTableModel)mismatchesCandidateTable.getModel();
        // for(int i=0;i<model.getRowCount();i++)
        // model.setValueAt(new Boolean(false),i,1);
        // }
        // }
        // );
        // mismatchesCandidateButtonPanel.add(candidateUncheckAllButton);
        // mismatchesCandidatePanel.add(BorderLayout.SOUTH,mismatchesCandidateButtonPanel);
        // mismatchesCandidatePanel.add(BorderLayout.CENTER,new
        // JScrollPane(mismatchesCandidateTable));
        // informationPanel.addTab(ApplicationUtilities.getResourceString("mergewizard.matchInformation.mismatchesCandidate"),mismatchesCandidatePanel);

        // Graph
        final JPanel graphPanel = new JPanel(new BorderLayout());
        final JGraph graph = matchInformationGui.getGraph(exactMapping);
        MatchGraphStatus mgsTemp = null;
        try
        {
            mgsTemp = new MatchGraphStatus(matchInformation);
        }
        catch (SchemaMatchingsException e)
        {
            // should not occur
        }
        final MatchGraphStatus mgs = mgsTemp;
        mgs.setScale(1);
        // mgs.setOne2ManyBestGraph(graph);
        mgs.setDisplayedGraph(graph);

        graphPanel.add(BorderLayout.CENTER, new JScrollPane(graph));
        informationPanel.addTab(
            ApplicationUtilities.getResourceString("mergewizard.matchInformation.graph"),
            graphPanel);
        // Graph Toolbox
        ToolBar graphToolbar = new ToolBar(null);
        // Zoom In
        Action action = new AbstractAction(ApplicationUtilities.getResourceString("action.zoomIn"),
            ApplicationUtilities.getImage("zoomin.gif"))
        {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e)
            {
                if (mgs.getDisplayedGraph() == null)
                    return;
                double scale = mgs.getScale();
                mgs.getDisplayedGraph().setScale(scale * 1.5);
                mgs.setScale(scale * 1.5);
            }
        };
        action.putValue(Action.LONG_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.zoomIn.longDescription"));
        action.putValue(Action.SHORT_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.zoomIn.shortDescription"));
        action.putValue(
            Action.MNEMONIC_KEY,
            new Integer(KeyStroke.getKeyStroke(
                ApplicationUtilities.getResourceString("action.zoomIn.mnemonic")).getKeyCode()));
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ApplicationUtilities
            .getResourceString("action.zoomIn.accelerator")));
        graphToolbar.addButton(action);

        // Zoom Out
        action = new AbstractAction(ApplicationUtilities.getResourceString("action.zoomOut"),
            ApplicationUtilities.getImage("zoomout.gif"))
        {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e)
            {
                if (mgs.getDisplayedGraph() == null)
                    return;
                double scale = mgs.getScale();
                mgs.getDisplayedGraph().setScale(scale / 1.5);
                mgs.setScale(scale / 1.5);
            }
        };
        action.putValue(Action.LONG_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.zoomOut.longDescription"));
        action.putValue(Action.SHORT_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.zoomOut.shortDescription"));
        action.putValue(
            Action.MNEMONIC_KEY,
            new Integer(KeyStroke.getKeyStroke(
                ApplicationUtilities.getResourceString("action.zoomOut.mnemonic")).getKeyCode()));
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ApplicationUtilities
            .getResourceString("action.zoomOut.accelerator")));
        graphToolbar.addButton(action);

        graphToolbar.addSeparator();

        // Save image
        action = new AbstractAction(ApplicationUtilities.getResourceString("action.saveImage"),
            ApplicationUtilities.getImage("saveimage.gif"))
        {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e)
            {
                if (graph == null)
                    return;
                // Filters
                ArrayList<FileFilter> filters = new ArrayList<FileFilter>();
                filters.add(JPGImageFileFilter.buildImageFileFilter());
                filters.add(PNGImageFileFilter.buildImageFileFilter());
                FileUtilities.configureFileDialogFilters(filters);
                // Viewers
                FileUtilities.fileViewer.removeAllViewers();
                FileUtilities.fileViewer.addViewer(ImageFileViewer.buildImageFileViewer());
                File file = FileUtilities.saveFileDialog(null, null);
                if (file != null)
                {
                    try
                    {
                        File theFile;
                        String extFile = file.getAbsolutePath();
                        String ext = FileUtilities.getFileExtension(file);
                        String format = "JPG";
                        if (ext == null ||
                            (!ext.equalsIgnoreCase("png") && !ext.equalsIgnoreCase("jpg")))
                        {
                            javax.swing.filechooser.FileFilter filter = FileUtilities.fileChooser
                                .getFileFilter();
                            if (filter instanceof JPGImageFileFilter)
                            {
                                extFile += ".jpg";
                                format = "JPG";
                            }
                            else if (filter instanceof ImageFileFilter)
                            {
                                extFile += ".png";
                                format = "PNG";
                            }
                        }
                        theFile = new File(extFile);
                        ImageUtilities.saveImageToFile(
                            GraphUtilities.toImage(mgs.getDisplayedGraph()), format, theFile);
                    }
                    catch (IOException ex)
                    {
                        JOptionPane.showMessageDialog(
                            null,
                            ApplicationUtilities.getResourceString("error") + ": " +
                                ex.getMessage(), ApplicationUtilities.getResourceString("error"),
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        };
        action.putValue(Action.LONG_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.saveImage.longDescription"));
        action.putValue(Action.SHORT_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.saveImage.shortDescription"));
        action.putValue(
            Action.MNEMONIC_KEY,
            new Integer(KeyStroke.getKeyStroke(
                ApplicationUtilities.getResourceString("action.saveImage.mnemonic")).getKeyCode()));
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ApplicationUtilities
            .getResourceString("action.saveImage.accelerator")));
        graphToolbar.addButton(action);

        graphPanel.add(BorderLayout.NORTH, graphToolbar);

        // Top K
        // Toolbox
        ToolBar topKToolbar = new ToolBar(null);

        // Best 1:1 mapping
        action = new AbstractAction(
            ApplicationUtilities.getResourceString("action.bestOneToOneMatching"),
            ApplicationUtilities.getImage("topk.gif"))
        {
            /** TODO: Doc. */
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e)
            {

                try
                {

                	//matchInformation = topKMatchings.get(0);
                    MatchMatrix matchs = (metaAlg.getMatchMatrix(0) != null 
                    		? metaAlg.getMatchMatrix(0) : metaAlg.getCombinedMatrix());
                    // //////////
                    matchInformation.setMatrix(matchs);
                    JGraph graph = matchInformationGui.getGraph(exactMapping);
                    graph.setScale(mgs.getScale());
                    mgs.setDisplayedGraph(graph);
                    JScrollPane graphPane = (JScrollPane) graphPanel.getComponent(0);
                    graphPane.setViewportView(graph);
                    // topkLabel.setText("Best One to One Matching");
                    if (exactMapping != null)
                    {
                        NumberFormat.getInstance();
                        matchInformation.getRecall(exactMapping);
                        matchInformation.getPrecision(exactMapping);

                    }
                    else
                    {
                        // recallLabel.setText("N/A");
                        // precisionLabel.setText("N/A");
                    }
                    ((MatchTableModel) matchTable.getModel()).refreshData(
                        matchInformationGui.getTableModelData(), matchInformation.getNumMatches());
                }
                catch (Exception sme)
                {
                    // ignore
                }
            }

        };
        action.putValue(Action.LONG_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.bestOneToOneMatching.longDescription"));
        action.putValue(Action.SHORT_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.bestOneToOneMatching.shortDescription"));
        action.putValue(
            Action.MNEMONIC_KEY,
            new Integer(KeyStroke.getKeyStroke(
                ApplicationUtilities.getResourceString("action.bestOneToOneMatching.mnemonic"))
                .getKeyCode()));
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ApplicationUtilities
            .getResourceString("action.bestOneToOneMatching.accelerator")));
        topKToolbar.addButton(action);

        // Previous match
        action = new AbstractAction(
            ApplicationUtilities.getResourceString("action.previousBestMatching"),
            ApplicationUtilities.getImage("back.gif"))
        {
            /** TODO: Doc. */
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e)
            {

                try
                {
                    if (mgs.getCurrentK() == 1)
                        return;// do nothing
                    int index = mgs.getCurrentK();
                    mgs.setCurrentK(mgs.getCurrentK() - 1);
                    MatchInformation mi = topKMatchings.get(index).clone();// mgs.previous();
                    mi.setMatrix(metaAlg
                            .getMatchMatrix(0) != null ? metaAlg.getMatchMatrix(0) : metaAlg
                                    .getCombinedMatrix());
                    ArrayList<Match> matches = mi.getCopyOfMatches();
                    matchInformation.setMatches(matches);
                    JGraph graph = matchInformationGui.getGraph(exactMapping);
                    graph.setScale(mgs.getScale());
                    mgs.setDisplayedGraph(graph);
                    JScrollPane graphPane = (JScrollPane) graphPanel.getComponent(0);
                    graphPane.setViewportView(graph);
                    if (mgs.getTopkIndex() > 1)
                    {
                        // topkLabel.setText(mgs.getTopkIndex()+" Best Matching");
                    }
                    else
                    {
                        // topkLabel.setText("Best One to One Matching");
                    }

                    if (exactMapping != null)
                    {
                        NumberFormat.getInstance();
                        matchInformation.getRecall(exactMapping);
                        matchInformation.getPrecision(exactMapping);

                    }
                    else
                    {
                        // recallLabel.setText("N/A");
                        // precisionLabel.setText("N/A");
                    }

                    ((MatchTableModel) matchTable.getModel()).refreshData(
                        matchInformationGui.getTableModelData(), matches.size());

                }
                catch (Exception sme)
                {
                    // ignore
                }
            }

        };
        action.putValue(Action.LONG_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.previousBestMatching.longDescription"));
        action.putValue(Action.SHORT_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.previousBestMatching.shortDescription"));
        action.putValue(
            Action.MNEMONIC_KEY,
            new Integer(KeyStroke.getKeyStroke(
                ApplicationUtilities.getResourceString("action.previousBestMatching.mnemonic"))
                .getKeyCode()));
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ApplicationUtilities
            .getResourceString("action.previousBestMatching.accelerator")));
        topKToolbar.addButton(action);

        // Next Match
        action = new AbstractAction(
            ApplicationUtilities.getResourceString("action.nextBestMatching"),
            ApplicationUtilities.getImage("forward.gif"))
        {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e)
            {

                try
                {
                    if (mgs.getCurrentK() == k)
                    {
                        return;
                    }
                    int index = mgs.getCurrentK();
                    mgs.setCurrentK(mgs.getCurrentK() + 1);
                    MatchInformation mi = topKMatchings.get(index).clone();
                    mi.setMatrix(metaAlg
                            .getMatchMatrix(0) != null ? metaAlg.getMatchMatrix(0) : metaAlg
                                    .getCombinedMatrix());
                    ArrayList<Match> matches = mi.getCopyOfMatches();
                    matchInformation.setMatches(matches);
                    JGraph graph = matchInformationGui.getGraph(exactMapping);
                    graph.setScale(mgs.getScale());
                    mgs.setDisplayedGraph(graph);
                    JScrollPane graphPane = (JScrollPane) graphPanel.getComponent(0);
                    graphPane.setViewportView(graph);
                    if (mgs.getTopkIndex() > 1)
                    {
                        // topkLabel.setText(mgs.getTopkIndex()+" Best Matching");
                    }
                    else
                    {
                        // topkLabel.setText("Best One to One Matching");
                    }

                    if (exactMapping != null)
                    {
                        NumberFormat.getInstance();
                        matchInformation.getRecall(exactMapping);
                        matchInformation.getPrecision(exactMapping);

                    }
                    else
                    {
                        // recallLabel.setText("N/A");
                        // precisionLabel.setText("N/A");
                    }

                    ((MatchTableModel) matchTable.getModel()).refreshData(
                        matchInformationGui.getTableModelData(), matches.size());

                }
                catch (Exception sme)
                {
                    // ignore
                }
            }

        };
        action.putValue(Action.LONG_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.nextBestMatching.longDescription"));
        action.putValue(Action.SHORT_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.nextBestMatching.shortDescription"));
        action.putValue(
            Action.MNEMONIC_KEY,
            new Integer(KeyStroke.getKeyStroke(
                ApplicationUtilities.getResourceString("action.nextBestMatching.mnemonic"))
                .getKeyCode()));
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ApplicationUtilities
            .getResourceString("action.nextBestMatching.accelerator")));
        topKToolbar.addButton(action);

        // Save Match
        action = new AbstractAction(
            ApplicationUtilities.getResourceString("action.saveTopKMatching"),
            ApplicationUtilities.getImage("saveontology.gif"))
        {
            /** TODO: Doc. */
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e)
            {

                try
                {

                	MatchInformation st = mgs.getSt();
                    if (st == null)
                    {// 1:N matching
                     // JOptionPane.showMessageDialog(null,ApplicationUtilities.getResourceString("error")
                     // + ":" +
                     // "No Top K matching generated",ApplicationUtilities.getResourceString("error"),JOptionPane.ERROR_MESSAGE);
                     // return;
                        st = mgs.getMatchInformation().clone();
                    }
                    // // Filters
                    // ArrayList filters=new ArrayList();
                    // filters.add(XMLUtilities.xmlFileFilter);
                    // FileUtilities.configureFileDialogFilters(filters);
                    // // Viewers
                    // FileUtilities.fileViewer.removeAllViewers();
                    // FileUtilities.fileViewer.addViewer(XMLUtilities.xmlFileViewer);
                    // File file=FileUtilities.saveFileDialog(null,new
                    // File(matchInformation.getTargetOntology().getName()+"-"+matchInformation.getCandidateOntology().getName()
                    // +"-"+mgs.getTopkIndex()+"-th_top_match.xml"));

                    // /new
                    ExporterMetadata[] metadata = ExportUtilities.getAllExporterMetadata();
                    // Filters
                    ArrayList<FileFilter> filters = new ArrayList<FileFilter>();
                    for (int i = 0; i < metadata.length; i++)
                    {
                        if (metadata[i].getExClass().equalsIgnoreCase(ExportersTypeEnum.MATCHING.getName()))
                        {
                            filters.add(new GeneralFileFilter(metadata[i].getExtension(),
                                "Save matching as " + metadata[i].getType() + " file"));
                        }
                    }
                    FileUtilities.configureFileDialogFilters(filters);
                    // Viewers
                    FileUtilities.fileViewer.removeAllViewers();
                    for (int i = 0; i < metadata.length; i++)
                    {
                        if (metadata[i].getExClass().equalsIgnoreCase(ExportersTypeEnum.MATCHING.getName()))
                        {
                            FileUtilities.fileViewer.addViewer(new GeneralFileView(metadata[i]
                                .getExtension(), metadata[i].getIcon()));
                        }
                    }
                    // Previewers
                    FilePreviewer filePrefviewer = new FilePreviewer();
                    for (int i = 0; i < metadata.length; i++)
                    {
                        if (metadata[i].getExClass().equalsIgnoreCase(ExportersTypeEnum.MATCHING.getName()))
                        {
                            filePrefviewer.addPreviewer(new GeneralFilePreviewer(metadata[i]
                                .getExtension()));
                        }
                    }
                    FileUtilities.configureFileDialogPreviewer(filePrefviewer);

                    final File file = FileUtilities.saveFileDialog(null, null);
                    // /

                    if (file != null)
                    {
                        try
                        {
                            Exporter exporter = ExportUtilities.getExporterPlugin(FileUtilities
                                .getFileExtension(file));
                            HashMap<String, Object> params = new HashMap<String, Object>();
                            params.put("Matching", st);
                            params.put("index", new Integer(mgs.getTopkIndex()));
                            params.put("candName", matchInformation.getCandidateOntology()
                                .getName());
                            params
                                .put("targetName", matchInformation.getTargetOntology().getName());
                            params.put("filepath", file.getAbsolutePath());
                            exporter.export(params, file);
                        }
                        catch (Exception ex)
                        {
                            JOptionPane.showMessageDialog(
                                null,
                                ApplicationUtilities.getResourceString("error") + ": " +
                                    ex.getMessage(),
                                ApplicationUtilities.getResourceString("error"),
                                JOptionPane.ERROR_MESSAGE);
                        }
                    }

                }
                catch (Exception ex)
                {
                    // ignore
                }
            }

        };
        action.putValue(Action.LONG_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.saveTopKMatching.longDescription"));
        action.putValue(Action.SHORT_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.saveTopKMatching.shortDescription"));
        action.putValue(
            Action.MNEMONIC_KEY,
            new Integer(KeyStroke.getKeyStroke(
                ApplicationUtilities.getResourceString("action.saveTopKMatching.mnemonic"))
                .getKeyCode()));
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ApplicationUtilities
            .getResourceString("action.saveTopKMatching.accelerator")));
        topKToolbar.addButton(action);

        topKToolbar.addSeparator();

        graphPanel.add(BorderLayout.SOUTH, topKToolbar);

        return informationPanel;
    }

    /**
     * Generate a tabbed pane for the a matcher
     * 
     * @param matchInformation a {@link MatchInformation}
     * @return a {@link JTabbedPane}
     */
    private JTabbedPane generateTabbedPaneForMatcher(final MatchInformation matchInformation)
    {
        final MatchInformationGui matchInformationGui = new MatchInformationGui(matchInformation);
        
        // Information
        JTabbedPane informationPanel = new JTabbedPane();
        JPanel statisticsPanel = new JPanel();

        statisticsPanel.setLayout(new BoxLayout(statisticsPanel, BoxLayout.Y_AXIS));

        // if (exactMapping != null){
        // //DefaultPieDataset pie=new DefaultPieDataset();
        // SchemaTranslator st = new SchemaTranslator(matchInformation, true, true);
        // double precision = SchemaMatchingsUtilities.calculatePrecision(exactMapping, st);
        // double recall = SchemaMatchingsUtilities.calculateRecall(exactMapping, st);
        // NumberFormat nf = NumberFormat.getNumberInstance();
        // nf = NumberFormat.getInstance();
        // nf.setMinimumFractionDigits(2);
        // //
        // pie.setValue(" "+ApplicationUtilities.getResourceString("mergewizard.matchInformation.mismatches"),new
        // Double(nf.format(1-precision)));
        // //
        // pie.setValue(ApplicationUtilities.getResourceString("mergewizard.matchInformation.matches"),new
        // Double(nf.format(precision)));
        // //
        // chart=ChartFactory.createPieChart(ApplicationUtilities.getResourceString("mergewizard.matchInformation.statistics"),pie,true);
        // // chart.setBackgroundPaint(Color.white);
        // // PiePlot plot = (PiePlot)chart.getPlot();
        // // plot.setOutlinePaint(Color.white);
        // // plot.setSectionLabelType(PiePlot.PERCENT_LABELS);
        //
        // String[] seriesNames = new String[]{"Precision", "Recall"};
        // String[] catNames = new String[]{""};
        // Number[][] values = new Number[][]{{new Double(precision)},{new Double(recall)}};
        // DefaultCategoryDataset dataset = new DefaultCategoryDataset(seriesNames,catNames,values);
        //
        // chart = ChartFactory.createVerticalBarChart(
        // "Statistics", // chart title
        // "", // domain axis label
        // "%", // range axis label
        // dataset, // data
        // true // include legend
        // );
        // chart.setBackgroundPaint(Color.white);
        // BarPlot plot = (BarPlot)chart.getPlot();
        // plot.setOutlinePaint(Color.white);
        // }

        informationPanel.addTab(
            ApplicationUtilities.getResourceString("mergewizard.matchInformation.statistics"),
            statisticsPanel);

        // Buttons
        final JButton removeMatchButton = new JButton(
            ApplicationUtilities.getResourceString("mergewizard.button.removeMatch"));
        new JButton(ApplicationUtilities.getResourceString("mergewizard.button.match"));
        new JLabel("", null, JLabel.CENTER);
        new JCheckBox(
            ApplicationUtilities.getResourceString("mergewizard.checkbox.updateThesaurus"));

        // Match Table
        final JTable matchTable = matchInformationGui.getMatchTable();
        matchTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        matchTable.setRowSelectionAllowed(true);
        matchTable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent e)
            {
                if (e.getValueIsAdjusting())
                    return;
                ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                if (lsm.isSelectionEmpty())
                    removeMatchButton.setEnabled(false);
                else
                {
                    lsm.getMinSelectionIndex();
                    removeMatchButton.setEnabled(true);
                }
            }
        });

        // Graph
        final JPanel graphPanel = new JPanel(new BorderLayout());

        MatchGraphStatus mgsTemp = null;
        try
        {
            mgsTemp = new MatchGraphStatus(matchInformation);
        }
        catch (SchemaMatchingsException e)
        {
            // should not occur
        }
        final MatchGraphStatus mgs = mgsTemp;
        MatchInformation st = null;
        try
        {
            st = mgs.best();
        }
        catch (SchemaMatchingsException e1)
        {
            return null;
        }
        // TODO: 6/5/2007 here there may some bug - fix it
        ArrayList<Match> matches = st.getCopyOfMatches();
        // //////////
        matchInformation.setMatches(matches);
        statisticsPanel.add(new JScrollPane(matchInformationGui.getStatistics(exactMapping)));
        final JGraph graph = matchInformationGui.getGraph(exactMapping);
        mgs.setScale(1);
        // mgs.setOne2ManyBestGraph(graph);
        mgs.setDisplayedGraph(graph);

        graphPanel.add(BorderLayout.CENTER, new JScrollPane(graph));
        informationPanel.addTab(
            ApplicationUtilities.getResourceString("mergewizard.matchInformation.graph"),
            graphPanel);
        // Graph Toolbox
        ToolBar graphToolbar = new ToolBar(null);
        // Zoom In
        Action action = new AbstractAction(ApplicationUtilities.getResourceString("action.zoomIn"),
            ApplicationUtilities.getImage("zoomin.gif"))
        {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e)
            {
                if (mgs.getDisplayedGraph() == null)
                    return;
                double scale = mgs.getScale();
                mgs.getDisplayedGraph().setScale(scale * 1.5);
                mgs.setScale(scale * 1.5);
            }
        };
        action.putValue(Action.LONG_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.zoomIn.longDescription"));
        action.putValue(Action.SHORT_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.zoomIn.shortDescription"));
        action.putValue(
            Action.MNEMONIC_KEY,
            new Integer(KeyStroke.getKeyStroke(
                ApplicationUtilities.getResourceString("action.zoomIn.mnemonic")).getKeyCode()));
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ApplicationUtilities
            .getResourceString("action.zoomIn.accelerator")));
        graphToolbar.addButton(action);

        // Zoom Out
        action = new AbstractAction(ApplicationUtilities.getResourceString("action.zoomOut"),
            ApplicationUtilities.getImage("zoomout.gif"))
        {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e)
            {
                if (mgs.getDisplayedGraph() == null)
                    return;
                double scale = mgs.getScale();
                mgs.getDisplayedGraph().setScale(scale / 1.5);
                mgs.setScale(scale / 1.5);
            }
        };
        action.putValue(Action.LONG_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.zoomOut.longDescription"));
        action.putValue(Action.SHORT_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.zoomOut.shortDescription"));
        action.putValue(
            Action.MNEMONIC_KEY,
            new Integer(KeyStroke.getKeyStroke(
                ApplicationUtilities.getResourceString("action.zoomOut.mnemonic")).getKeyCode()));
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ApplicationUtilities
            .getResourceString("action.zoomOut.accelerator")));
        graphToolbar.addButton(action);

        graphToolbar.addSeparator();

        // Save image
        action = new AbstractAction(ApplicationUtilities.getResourceString("action.saveImage"),
            ApplicationUtilities.getImage("saveimage.gif"))
        {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e)
            {
                if (graph == null)
                    return;
                // Filters
                ArrayList<FileFilter> filters = new ArrayList<FileFilter>();
                filters.add(JPGImageFileFilter.buildImageFileFilter());
                filters.add(PNGImageFileFilter.buildImageFileFilter());
                FileUtilities.configureFileDialogFilters(filters);
                // Viewers
                FileUtilities.fileViewer.removeAllViewers();
                FileUtilities.fileViewer.addViewer(ImageFileViewer.buildImageFileViewer());
                File file = FileUtilities.saveFileDialog(null, null);
                if (file != null)
                {
                    try
                    {
                        File theFile;
                        String extFile = file.getAbsolutePath();
                        String ext = FileUtilities.getFileExtension(file);
                        String format = "JPG";
                        if (ext == null ||
                            (!ext.equalsIgnoreCase("png") && !ext.equalsIgnoreCase("jpg")))
                        {
                            javax.swing.filechooser.FileFilter filter = FileUtilities.fileChooser
                                .getFileFilter();
                            if (filter instanceof JPGImageFileFilter)
                            {
                                extFile += ".jpg";
                                format = "JPG";
                            }
                            else if (filter instanceof ImageFileFilter)
                            {
                                extFile += ".png";
                                format = "PNG";
                            }
                        }
                        theFile = new File(extFile);
                        ImageUtilities.saveImageToFile(
                            GraphUtilities.toImage(mgs.getDisplayedGraph()), format, theFile);
                    }
                    catch (IOException ex)
                    {
                        JOptionPane.showMessageDialog(
                            null,
                            ApplicationUtilities.getResourceString("error") + ": " +
                                ex.getMessage(), ApplicationUtilities.getResourceString("error"),
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        };
        action.putValue(Action.LONG_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.saveImage.longDescription"));
        action.putValue(Action.SHORT_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.saveImage.shortDescription"));
        action.putValue(
            Action.MNEMONIC_KEY,
            new Integer(KeyStroke.getKeyStroke(
                ApplicationUtilities.getResourceString("action.saveImage.mnemonic")).getKeyCode()));
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ApplicationUtilities
            .getResourceString("action.saveImage.accelerator")));
        graphToolbar.addButton(action);

        graphPanel.add(BorderLayout.NORTH, graphToolbar);

        // Top K
        // Toolbox
        ToolBar topKToolbar = new ToolBar(null);

        // Best 1:1 mapping
        action = new AbstractAction(
            ApplicationUtilities.getResourceString("action.bestOneToOneMatching"),
            ApplicationUtilities.getImage("topk.gif"))
        {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e)
            {

                try
                {

                	MatchInformation st = mgs.best();
                    ArrayList<Match> matches = st.getCopyOfMatches();
                    // //////////
                    matchInformation.setMatches(matches);
                    JGraph graph = matchInformationGui.getGraph(exactMapping);
                    graph.setScale(mgs.getScale());
                    mgs.setDisplayedGraph(graph);
                    JScrollPane graphPane = (JScrollPane) graphPanel.getComponent(0);
                    graphPane.setViewportView(graph);
                    // topkLabel.setText("Best One to One Matching");
                    if (exactMapping != null)
                    {
                        NumberFormat.getInstance();
                        matchInformation.getRecall(exactMapping);
                        matchInformation.getPrecision(exactMapping);

                    }
                    else
                    {
                        // recallLabel.setText("N/A");
                        // precisionLabel.setText("N/A");
                    }
                    ((MatchTableModel) matchTable.getModel()).refreshData(
                        matchInformationGui.getTableModelData(), matches.size());
                }
                catch (SchemaMatchingsException sme)
                {
                    // ignore
                }
            }

        };
        action.putValue(Action.LONG_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.bestOneToOneMatching.longDescription"));
        action.putValue(Action.SHORT_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.bestOneToOneMatching.shortDescription"));
        action.putValue(
            Action.MNEMONIC_KEY,
            new Integer(KeyStroke.getKeyStroke(
                ApplicationUtilities.getResourceString("action.bestOneToOneMatching.mnemonic"))
                .getKeyCode()));
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ApplicationUtilities
            .getResourceString("action.bestOneToOneMatching.accelerator")));
        topKToolbar.addButton(action);

        // Previous match
        action = new AbstractAction(
            ApplicationUtilities.getResourceString("action.previousBestMatching"),
            ApplicationUtilities.getImage("back.gif"))
        {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    if (mgs.getTopkIndex() == 1)
                        return;// do nothing
                    MatchInformation st = mgs.previous();
                    ArrayList<Match> matches = st.getCopyOfMatches();

                    matchInformation.setMatches(matches);
                    JGraph graph = matchInformationGui.getGraph(exactMapping);
                    graph.setScale(mgs.getScale());
                    mgs.setDisplayedGraph(graph);
                    JScrollPane graphPane = (JScrollPane) graphPanel.getComponent(0);
                    graphPane.setViewportView(graph);
                    if (mgs.getTopkIndex() > 1)
                    {
                        // topkLabel.setText(mgs.getTopkIndex()+" Best Matching");
                    }
                    else
                    {
                        // topkLabel.setText("Best One to One Matching");
                    }

                    if (exactMapping != null)
                    {
                        NumberFormat.getInstance();
                        matchInformation.getRecall(exactMapping);
                        matchInformation.getPrecision(exactMapping);

                    }
                    else
                    {
                        // recallLabel.setText("N/A");
                        // precisionLabel.setText("N/A");
                    }

                    ((MatchTableModel) matchTable.getModel()).refreshData(
                        matchInformationGui.getTableModelData(), matches.size());

                }
                catch (SchemaMatchingsException sme)
                {
                    // ignore
                }
            }

        };
        action.putValue(Action.LONG_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.previousBestMatching.longDescription"));
        action.putValue(Action.SHORT_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.previousBestMatching.shortDescription"));
        action.putValue(
            Action.MNEMONIC_KEY,
            new Integer(KeyStroke.getKeyStroke(
                ApplicationUtilities.getResourceString("action.previousBestMatching.mnemonic"))
                .getKeyCode()));
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ApplicationUtilities
            .getResourceString("action.previousBestMatching.accelerator")));
        topKToolbar.addButton(action);

        // Next Match
        action = new AbstractAction(
            ApplicationUtilities.getResourceString("action.nextBestMatching"),
            ApplicationUtilities.getImage("forward.gif"))
        {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e)
            {

                try
                {
                	MatchInformation st = mgs.next();
                    ArrayList<Match> matches = st.getCopyOfMatches();
                    matchInformation.setMatches(matches);
                    JGraph graph = matchInformationGui.getGraph(exactMapping);
                    graph.setScale(mgs.getScale());
                    mgs.setDisplayedGraph(graph);
                    JScrollPane graphPane = (JScrollPane) graphPanel.getComponent(0);
                    graphPane.setViewportView(graph);
                    if (mgs.getTopkIndex() > 1)
                    {
                        // topkLabel.setText(mgs.getTopkIndex()+" Best Matching");
                    }
                    else
                    {
                        // topkLabel.setText("Best One to One Matching");
                    }

                    if (exactMapping != null)
                    {
                        NumberFormat.getInstance();
                        matchInformation.getRecall(exactMapping);
                        matchInformation.getPrecision(exactMapping);

                    }
                    else
                    {
                        // recallLabel.setText("N/A");
                        // precisionLabel.setText("N/A");
                    }

                    ((MatchTableModel) matchTable.getModel()).refreshData(
                        matchInformationGui.getTableModelData(), matches.size());

                }
                catch (SchemaMatchingsException sme)
                {
                    // ignore
                }
            }

        };
        action.putValue(Action.LONG_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.nextBestMatching.longDescription"));
        action.putValue(Action.SHORT_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.nextBestMatching.shortDescription"));
        action.putValue(
            Action.MNEMONIC_KEY,
            new Integer(KeyStroke.getKeyStroke(
                ApplicationUtilities.getResourceString("action.nextBestMatching.mnemonic"))
                .getKeyCode()));
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ApplicationUtilities
            .getResourceString("action.nextBestMatching.accelerator")));
        topKToolbar.addButton(action);

        // Save Match
        action = new AbstractAction(
            ApplicationUtilities.getResourceString("action.saveTopKMatching"),
            ApplicationUtilities.getImage("saveontology.gif"))
        {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e)
            {

                try
                {
                	MatchInformation st = mgs.getSt();
                    if (st == null)
                    {// 1:N matching
                     // JOptionPane.showMessageDialog(null,ApplicationUtilities.getResourceString("error")
                     // + ":" +
                     // "No Top K matching generated",ApplicationUtilities.getResourceString("error"),JOptionPane.ERROR_MESSAGE);
                     // return;
                        st = mgs.getMatchInformation().clone();
                    }
                    // // Filters
                    // ArrayList filters=new ArrayList();
                    // filters.add(XMLUtilities.xmlFileFilter);
                    // FileUtilities.configureFileDialogFilters(filters);
                    // // Viewers
                    // FileUtilities.fileViewer.removeAllViewers();
                    // FileUtilities.fileViewer.addViewer(XMLUtilities.xmlFileViewer);
                    // File file=FileUtilities.saveFileDialog(null,new
                    // File(matchInformation.getTargetOntology().getName()+"-"+matchInformation.getCandidateOntology().getName()
                    // +"-"+mgs.getTopkIndex()+"-th_top_match.xml"));

                    // /new
                    ExporterMetadata[] metadata = ExportUtilities.getAllExporterMetadata();
                    // Filters
                    ArrayList<FileFilter> filters = new ArrayList<FileFilter>();
                    for (int i = 0; i < metadata.length; i++)
                    {
                        if (metadata[i].getExClass().equalsIgnoreCase(ExportersTypeEnum.MATCHING.getName()))
                        {
                            filters.add(new GeneralFileFilter(metadata[i].getExtension(),
                                "Save matching as " + metadata[i].getType() + " file"));
                        }
                    }
                    FileUtilities.configureFileDialogFilters(filters);
                    // Viewers
                    FileUtilities.fileViewer.removeAllViewers();
                    for (int i = 0; i < metadata.length; i++)
                    {
                        if (metadata[i].getExClass().equalsIgnoreCase(ExportersTypeEnum.MATCHING.getName()))
                        {
                            FileUtilities.fileViewer.addViewer(new GeneralFileView(metadata[i]
                                .getExtension(), metadata[i].getIcon()));
                        }
                    }
                    // Previewers
                    FilePreviewer filePrefviewer = new FilePreviewer();
                    for (int i = 0; i < metadata.length; i++)
                    {
                        if (metadata[i].getExClass().equalsIgnoreCase(ExportersTypeEnum.MATCHING.getName()))
                        {
                            filePrefviewer.addPreviewer(new GeneralFilePreviewer(metadata[i]
                                .getExtension()));
                        }
                    }
                    FileUtilities.configureFileDialogPreviewer(filePrefviewer);

                    final File file = FileUtilities.saveFileDialog(null, null);
                    // /

                    if (file != null)
                    {
                        try
                        {
                            Exporter exporter = ExportUtilities.getExporterPlugin(FileUtilities
                                .getFileExtension(file));
                            HashMap<String, Object> params = new HashMap<String, Object>();
                            params.put("Matching", st);
                            params.put("index", new Integer(mgs.getTopkIndex()));
                            params.put("candName", matchInformation.getCandidateOntology()
                                .getName());
                            params
                                .put("targetName", matchInformation.getTargetOntology().getName());
                            params.put("filepath", file.getAbsolutePath());
                            exporter.export(params, file);
                        }
                        catch (Exception ex)
                        {
                            JOptionPane.showMessageDialog(
                                null,
                                ApplicationUtilities.getResourceString("error") + ": " +
                                    ex.getMessage(),
                                ApplicationUtilities.getResourceString("error"),
                                JOptionPane.ERROR_MESSAGE);
                        }
                    }

                }
                catch (Exception ex)
                {
                    // ignore
                }
            }

        };
        action.putValue(Action.LONG_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.saveTopKMatching.longDescription"));
        action.putValue(Action.SHORT_DESCRIPTION,
            ApplicationUtilities.getResourceString("action.saveTopKMatching.shortDescription"));
        action.putValue(
            Action.MNEMONIC_KEY,
            new Integer(KeyStroke.getKeyStroke(
                ApplicationUtilities.getResourceString("action.saveTopKMatching.mnemonic"))
                .getKeyCode()));
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ApplicationUtilities
            .getResourceString("action.saveTopKMatching.accelerator")));
        topKToolbar.addButton(action);

        topKToolbar.addSeparator();

        graphPanel.add(BorderLayout.SOUTH, topKToolbar);

        return informationPanel;
    }

}