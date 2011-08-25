package ac.technion.iem.ontobuilder.gui.utils.thesaurus;

import javax.swing.JPanel;

import ac.technion.iem.ontobuilder.core.thesaurus.Thesaurus;
import ac.technion.iem.ontobuilder.gui.ontobuilder.main.OntoBuilder;

import java.awt.BorderLayout;

/**
 * <p>Title: ThesaurusPanel</p>
 * Extends a {@link JPanel}
 */
public class ThesaurusPanel extends JPanel
{
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a ThesaurusPanel
     * 
     * @param ontoBuilder the {@link OntoBuilder} application
     */
    public ThesaurusPanel(OntoBuilder ontoBuilder)
    {
        setLayout(new BorderLayout());
    }

    /**
     * Show the Thesaurus
     * 
     * @param thesaurus the {@link Thesaurus} to show
     */
    public void showThesaurus(ThesaurusGui thesaurus)
    {
        removeAll();
        if (thesaurus == null)
            return;
        add(BorderLayout.CENTER, thesaurus);
    }
}
