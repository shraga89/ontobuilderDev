package ac.technion.iem.ontobuilder.gui.ontology;

import java.util.EventObject;

/**
 * <p>Title: OntologySelectionEvent</p>
 * Extends {@link EventObject}
 */
public class OntologySelectionEvent extends EventObject
{
    private static final long serialVersionUID = 1L;

    protected Object selectedObject;

    /**
     * Constructs a OntologySelectionEvent
     *
     * @param source the source
     * @param selectedObject the selected object
     */
    public OntologySelectionEvent(Object source, Object selectedObject)
    {
        super(source);
        this.selectedObject = selectedObject;
    }

    /**
     * Get the selected object
     *
     * @return the object
     */
    public Object getSelectedObject()
    {
        return selectedObject;
    }
}
