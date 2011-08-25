package ac.technion.iem.ontobuilder.gui.utils.files.common;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;

/**
 * <p>Title: FilePreviewer</p>
 * Extends {@link JPanel}
 */
public class FilePreviewer extends JPanel
{
    private static final long serialVersionUID = -4041249958072294888L;
    
    protected ArrayList<FilePreview> previewers;
    protected String noPreview;

    public FilePreviewer()
    {
        previewers = new ArrayList<FilePreview>();
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(ApplicationUtilities.getIntProperty("file.preview.width"), 0));
        noPreview = ApplicationUtilities.getResourceString("file.nopreview");
        add(BorderLayout.CENTER, new JLabel(noPreview, SwingConstants.CENTER));
    }

    public void addPreviewer(FilePreview previewer)
    {
        previewers.add(previewer);
    }

    public void removePreviewer(FilePreview previewer)
    {
        previewers.remove(previewer);
    }

    public void configure(File f)
    {
        removeAll();
        for (Iterator<FilePreview> i = previewers.iterator(); i.hasNext();)
        {
            FilePreview previewer = (FilePreview) i.next();
            if (previewer.isFileSupported(f))
            {
                add(BorderLayout.CENTER, new JScrollPane(previewer.getComponent(f)));
                // add(BorderLayout.CENTER,new JLabel(f.getName(),JLabel.CENTER));
                revalidate();
                return;
            }
        }
        add(BorderLayout.CENTER, new JLabel(noPreview, SwingConstants.CENTER));
        revalidate();
    }
}