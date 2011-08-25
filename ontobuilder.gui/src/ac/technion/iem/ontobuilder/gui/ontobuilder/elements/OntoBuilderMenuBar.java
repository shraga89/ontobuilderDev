package ac.technion.iem.ontobuilder.gui.ontobuilder.elements;

import javax.swing.Box;
import javax.swing.JMenu;

import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.elements.MenuBar;
import ac.technion.iem.ontobuilder.gui.ontobuilder.main.OntoBuilder;
import ac.technion.iem.ontobuilder.gui.tools.exactmapping.ToolMetadata;
import ac.technion.iem.ontobuilder.gui.tools.exactmapping.ToolsUtilities;
import ac.technion.iem.ontobuilder.io.exports.ExportUtilities;
import ac.technion.iem.ontobuilder.io.exports.ExporterMetadata;
import ac.technion.iem.ontobuilder.io.exports.ExportersTypeEnum;
import ac.technion.iem.ontobuilder.io.imports.ImportUtilities;
import ac.technion.iem.ontobuilder.io.imports.ImporterMetadata;

/**
 * <p>Title: OntoBuilderMenuBar</p>
 * Extends a {@link MenuBar}
 */
public class OntoBuilderMenuBar extends MenuBar
{
    private static final long serialVersionUID = 5235359169599722940L;

    /**
     * Constructs a OntoBuilderMenuBar
     * 
     * @param ontoBuilder the {@link OntoBuilder} application
     */
    public OntoBuilderMenuBar(OntoBuilder ontoBuilder)
    {
        super(ontoBuilder);
    }

    /**
     * Initialize the OntoBuilderMenuBar
     */
    protected void init()
    {
        // File menu
        JMenu menuFile = new JMenu(ApplicationUtilities.getResourceString("menu.file"));
        menuFile
            .setMnemonic(ApplicationUtilities.getResourceString("menu.file.mnemonic").charAt(0));
        add(menuFile);

        addMenuItem(menuFile, application.getAction("open"));

        // _______
        menuFile.addSeparator();

        // New Ontology
        addMenuItem(menuFile, application.getAction("newontology"));

        // Open Ontology
        addMenuItem(menuFile, application.getAction("openontology"));

        // Save Ontology
        addMenuItem(menuFile, application.getAction("saveontology"));

        // Save Light Ontology
        addMenuItem(menuFile, application.getAction("savelightontology"));

        // Save As Ontology
        addMenuItem(menuFile, application.getAction("saveasontology"));

        // Close Ontology
        addMenuItem(menuFile, application.getAction("closeontology"));

        // _______
        menuFile.addSeparator();

        // // Print
        // addMenuItem(menuFile,application.getAction("print"));

        // Import
        JMenu importMenu = new JMenu(ApplicationUtilities.getResourceString("menu.import"));
        importMenu.setMnemonic(ApplicationUtilities.getResourceString("menu.import.mnemonic")
            .charAt(0));
        importMenu.setIcon(ApplicationUtilities.getImage("import.gif"));
        // Add importer actions
        ImporterMetadata[] importers = ImportUtilities.getAllImporterMetadata();

        if (importers.length > 0)
        {
            for (int i = 0; i < importers.length; i++)
            {
                ImporterMetadata importer = importers[i];
                addMenuItem(importMenu, application.getAction("import." + importer.getType()), true);
            }
        }
        else
        {
            importMenu.setEnabled(false);
        }

        addMenuItem(menuFile, importMenu);

        // Export
        JMenu exportMenu = new JMenu(ApplicationUtilities.getResourceString("menu.export"));
        exportMenu.setMnemonic(ApplicationUtilities.getResourceString("menu.export.mnemonic")
            .charAt(0));
        exportMenu.setIcon(ApplicationUtilities.getImage("export.gif"));
        // Add ontology exporters actions
        ExporterMetadata[] exporters = ExportUtilities.getAllExporterMetadata();
        if (exporters.length > 0)
        {
            for (int i = 0; i < exporters.length; i++)
            {
                ExporterMetadata exporter = exporters[i];
                if (exporter.getExClass().equalsIgnoreCase(ExportersTypeEnum.ONTOLOGY.getName()))
                {
                    addMenuItem(exportMenu, application.getAction("export." + exporter.getType()),
                        true);
                }
            }
        }
        else
        {
            exportMenu.setEnabled(false);
        }

        addMenuItem(menuFile, exportMenu);

        if (application.isApplication())
        {
            // _______
            menuFile.addSeparator();

            // Exit
            addMenuItem(menuFile, application.getAction("exit"));
        }

        // Edit menu
        JMenu menuEdit = new JMenu(ApplicationUtilities.getResourceString("menu.edit"));
        menuEdit
            .setMnemonic(ApplicationUtilities.getResourceString("menu.edit.mnemonic").charAt(0));
        add(menuEdit);

        // Cut
        addMenuItem(menuEdit, application.getAction("cut"));

        // Copy
        addMenuItem(menuEdit, application.getAction("copy"));

        // Paste
        addMenuItem(menuEdit, application.getAction("paste"));

        // Ontology menu
        JMenu menuOntology = new JMenu(ApplicationUtilities.getResourceString("menu.ontology"));
        menuOntology.setMnemonic(ApplicationUtilities.getResourceString("menu.ontology.mnemonic")
            .charAt(0));
        add(menuOntology);

        // Hyperbolic Ontology
        addMenuItem(menuOntology, application.getAction("hyperbolicontology"));

        // Graph Ontology
        addMenuItem(menuOntology, application.getAction("graphontology"));

        menuOntology.addSeparator();

        // Normalize Ontology
        addMenuItem(menuOntology, application.getAction("normalizeontology"));

        menuOntology.addSeparator();

        // Biztalk
        addMenuItem(menuOntology, application.getAction("biztalkMapper"));

        menuOntology.addSeparator();

        // Ontology Wizard
        addMenuItem(menuOntology, application.getAction("ontowizard"));

        // Ontology Merge Wizard
        addMenuItem(menuOntology, application.getAction("mergeontologies"));

        // Meta Top K
        addMenuItem(menuOntology, application.getAction("metaTopK"));

        // Tools menu
        JMenu menuTools = new JMenu(ApplicationUtilities.getResourceString("menu.tools"));
        menuTools.setMnemonic(ApplicationUtilities.getResourceString("menu.tools.mnemonic").charAt(
            0));
        add(menuTools);

        // OntoParser
        addMenuItem(menuTools, application.getAction("ontoparser"));
        menuTools.addSeparator();

        // Tools
        JMenu toolMeue = new JMenu(ApplicationUtilities.getResourceString("menu.tool"));
        // importMenu.setMnemonic(ApplicationUtilities.getResourceString("menu.tool.mnemonic").charAt(0));
        toolMeue.setIcon(ApplicationUtilities.getImage("tools.gif"));
        // Add tools
        ToolMetadata[] tools = ToolsUtilities.getAllToolMetadata();

        if (tools.length > 0)
        {
            for (int i = 0; i < tools.length; i++)
            {
                addCheckBoxMenuItem(toolMeue, tools[i].getName(),
                    application.getAction("tool." + tools[i].getName()), tools[i].getIcon());
            }
        }
        else
        {
            toolMeue.setEnabled(false);
        }

        addMenuItem(menuTools, toolMeue);
        menuTools.addSeparator();

        // Site Map
        addMenuItem(menuTools, application.getAction("sitemap"));

        menuTools.addSeparator();

        // Options
        addMenuItem(menuTools, application.getAction("options"));

        add(Box.createHorizontalGlue());

        // Help menu
        JMenu menuHelp = new JMenu(ApplicationUtilities.getResourceString("menu.help"));
        menuHelp
            .setMnemonic(ApplicationUtilities.getResourceString("menu.help.mnemonic").charAt(0));
        add(menuHelp);

        // Help
        menuHelpHelp = addMenuItem(menuHelp, application.getAction("help"));

        // Context Sensitive Help
        menuHelpCSHelp = addMenuItem(menuHelp, application.getAction("cshelp"));

        menuHelp.addSeparator();

        // About
        addMenuItem(menuHelp, application.getAction("about"));

    }
}