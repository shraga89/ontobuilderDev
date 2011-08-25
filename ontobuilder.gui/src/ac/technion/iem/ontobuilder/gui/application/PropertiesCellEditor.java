package ac.technion.iem.ontobuilder.gui.application;

import javax.swing.DefaultCellEditor;

import ac.technion.iem.ontobuilder.gui.elements.TextField;

/**
 * <p>Title: PropertiesCellEditor</p>
 * Extends {@link DefaultCellEditor}
 */
public class PropertiesCellEditor extends DefaultCellEditor
{
    private static final long serialVersionUID = 1L;

    public PropertiesCellEditor()
    {
        super(new TextField());
    }
}