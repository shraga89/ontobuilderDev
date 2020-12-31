package ac.technion.iem.ontobuilder.core.thesaurus.event;

import java.util.EventListener;

/**
 * <p>Title: ThesaurusSelectionListener</p>
 * Extends {@link EventListener} 
 */
public interface ThesaurusSelectionListener extends EventListener
{
    public void valueChanged(ThesaurusSelectionEvent e);
}
