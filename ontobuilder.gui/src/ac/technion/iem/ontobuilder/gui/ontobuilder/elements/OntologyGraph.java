package ac.technion.iem.ontobuilder.gui.ontobuilder.elements;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileFilter;

import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.application.action.Actions;
import ac.technion.iem.ontobuilder.gui.elements.ToolBar;
import ac.technion.iem.ontobuilder.gui.utils.files.common.FileUtilities;
import ac.technion.iem.ontobuilder.gui.utils.files.image.ImageFileFilter;
import ac.technion.iem.ontobuilder.gui.utils.files.image.ImageFileViewer;
import ac.technion.iem.ontobuilder.gui.utils.files.image.ImageUtilities;
import ac.technion.iem.ontobuilder.gui.utils.files.image.JPGImageFileFilter;
import ac.technion.iem.ontobuilder.gui.utils.files.image.PNGImageFileFilter;
import ac.technion.iem.ontobuilder.gui.utils.graphs.GraphUtilities;

import com.jgraph.JGraph;

/**
 * <p>Title: OntologyGraph</p>
 * Extends a {@link JDialog}
 */
public class OntologyGraph extends JDialog
{
    private static final long serialVersionUID = 1L;

    protected JGraph graph;
    protected Actions actions;

    /**
     * Constructs an OntologyGraph
     * 
     * @param parent the parent {@link JFrame}
     * @param graph the {@link JGraph}
     */
    public OntologyGraph(JFrame parent, JGraph graph)
    {
        super(parent, false);
        this.graph = graph;
        initializeActions();

        setSize(new Dimension(ApplicationUtilities.getIntProperty("ontology.graph.width"),
            ApplicationUtilities.getIntProperty("ontology.graph.height")));
        setLocationRelativeTo(parent);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setContentPane(mainPanel);

        // Buttons
        JPanel buttonsPanel = new JPanel(new FlowLayout());

        // Close
        JButton closeButton;
        buttonsPanel.add(closeButton = new JButton(ApplicationUtilities
            .getResourceString("ontology.graph.button.close")));
        closeButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                dispose();
            }
        });
        getRootPane().setDefaultButton(closeButton);

        mainPanel.add(BorderLayout.SOUTH, buttonsPanel);

        // JPanel centerPanel=new JPanel(new BorderLayout());

        mainPanel.add(BorderLayout.CENTER, new JScrollPane(graph));

        JPanel northPanel = new JPanel(new BorderLayout());
        mainPanel.add(BorderLayout.NORTH, northPanel);

        // Toolbox
        ToolBar toolbar = new ToolBar(null);
        // Zoom In
        toolbar.addButton(actions.getAction("zoomIn"));
        // Zoom Out
        toolbar.addButton(actions.getAction("zoomOut"));
        toolbar.addSeparator();
        // Save Image
        toolbar.addButton(actions.getAction("saveImage"));
        northPanel.add(BorderLayout.CENTER, toolbar);

        // Title
        JLabel title = new JLabel(ApplicationUtilities.getResourceString("ontology.graph.title"),
            ApplicationUtilities.getImage("graph.gif"), SwingConstants.LEFT);
        title.setFont(new Font(getFont().getName(), Font.BOLD, getFont().getSize() + 4));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        northPanel.add(BorderLayout.NORTH, title);

        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                dispose();
            }
        });
    }

    /**
     * Initialize the actions
     */
    public void initializeActions()
    {
        actions = new Actions();

        // Zoom In
        Action action = new AbstractAction(ApplicationUtilities.getResourceString("action.zoomIn"),
            ApplicationUtilities.getImage("zoomin.gif"))
        {

            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e)
            {
                commandZoomIn();
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
        actions.addAction("zoomIn", action);

        // Zoom Out
        action = new AbstractAction(ApplicationUtilities.getResourceString("action.zoomOut"),
            ApplicationUtilities.getImage("zoomout.gif"))
        {

            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e)
            {
                commandZoomOut();
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
        actions.addAction("zoomOut", action);

        // Save image
        action = new AbstractAction(ApplicationUtilities.getResourceString("action.saveImage"),
            ApplicationUtilities.getImage("saveimage.gif"))
        {

            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e)
            {
                commandSaveImage();
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
        actions.addAction("saveImage", action);
    }

    /**
     * Zoom into a command
     */
    public void commandZoomIn()
    {
        if (graph == null)
            return;
        double scale = graph.getScale();
        graph.setScale(scale * 1.5);
    }

    /**
     * Zoom out of a command
     */
    public void commandZoomOut()
    {
        if (graph == null)
            return;
        double scale = graph.getScale();
        graph.setScale(scale / 1.5);
    }

    /**
     * Execute the save image command
     */
    public void commandSaveImage()
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
                if (ext == null || (!ext.equalsIgnoreCase("png") && !ext.equalsIgnoreCase("jpg")))
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
                ImageUtilities.saveImageToFile(GraphUtilities.toImage(graph), format, theFile);
            }
            catch (IOException ex)
            {
                JOptionPane.showMessageDialog(null,
                    ApplicationUtilities.getResourceString("error") + ": " + ex.getMessage(),
                    ApplicationUtilities.getResourceString("error"), JOptionPane.ERROR_MESSAGE);
            }
        }

    }
}