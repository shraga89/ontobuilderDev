package ac.technion.iem.ontobuilder.core.thesaurus.event;

import java.util.EventListener;

/**
 * <p>Title: ThesaurusModelListener</p>
 * Extends {@link EventListener} 
 */
public interface ThesaurusModelListener extends EventListener
{
    public void update(ThesaurusModelEvent e);

    public void wordChanged(ThesaurusModelEvent e);

    public void wordAdded(ThesaurusModelEvent e);

    public void wordRenamed(ThesaurusModelEvent e);

    public void wordDeleted(ThesaurusModelEvent e);

    public void synonymAdded(ThesaurusModelEvent e);

    public void synonymDeleted(ThesaurusModelEvent e);

    public void homonymAdded(ThesaurusModelEvent e);

    public void homonymDeleted(ThesaurusModelEvent e);
}
