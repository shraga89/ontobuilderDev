package ac.technion.iem.ontobuilder.gui.utils.files.ontology;

import java.io.File;

import javax.swing.Icon;
import javax.swing.filechooser.FileView;

import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.utils.files.common.FileUtilities;

/**
 * <p>Title: OntologyFileViewer</p>
 * Extends {@link FileView}
 */
public class OntologyFileViewer extends FileView
{
    /**
     * Get the icon
     * 
     * @param the file to get the icon from
     * @return the icon
     */
    public Icon getIcon(File f)
    {
        String extension = FileUtilities.getFileExtension(f);
        if (extension != null && (extension.equals("xml") || extension.equals("ont")))
            return ApplicationUtilities.getImage("ontologyfile.gif");
        return null;
    }
}