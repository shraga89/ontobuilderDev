package ac.technion.iem.ontobuilder.gui.utils.dom;

import javax.swing.JTable;

import org.w3c.dom.NamedNodeMap;

import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.application.ObjectWithProperties;
import ac.technion.iem.ontobuilder.gui.application.PropertiesTableModel;
import ac.technion.iem.ontobuilder.io.utils.dom.DOMNode;

/**
 * <p>
 * Title: DomNodeGui
 * </p>
  * <p>Description: Implements the methods of the DomNode used by the GUI</p>
 */
public class DOMNodeGui implements ObjectWithProperties
{
    private DOMNode _domNode;
    
    public DOMNodeGui(DOMNode domNode)
    {
        _domNode = domNode;
    }
    
    public JTable getProperties()
    {
        String columnNames[] =
        {
            ApplicationUtilities.getResourceString("properties.attribute"),
            ApplicationUtilities.getResourceString("properties.value")
        };
        NamedNodeMap attributes = _domNode.getNode().getAttributes();
        if (attributes == null)
            return null;
        Object data[][] = new Object[attributes.getLength()][2];
        for (int i = 0; i < attributes.getLength(); i++)
        {
            data[i][0] = attributes.item(i).getNodeName();
            data[i][1] = attributes.item(i).getNodeValue();
        }
        JTable propertiesTable = new JTable(new PropertiesTableModel(columnNames, 2, data));
        return propertiesTable;
    }
}
