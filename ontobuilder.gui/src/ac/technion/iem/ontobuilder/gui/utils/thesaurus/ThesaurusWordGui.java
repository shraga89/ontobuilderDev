package ac.technion.iem.ontobuilder.gui.utils.thesaurus;

import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import ac.technion.iem.ontobuilder.core.thesaurus.ThesaurusWord;
import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.application.ObjectWithProperties;
import ac.technion.iem.ontobuilder.gui.application.PropertiesCellEditor;
import ac.technion.iem.ontobuilder.gui.application.PropertiesTableModel;

/**
 * <p>
 * Title: ThesaurusWordGui
 * </p>
  * <p>Description: Implements the methods of the ThesaurusWord used by the GUI</p>
  * implements {@link ObjectWithProperties}
 */
public class ThesaurusWordGui implements ObjectWithProperties
{
    private ThesaurusWord thesaurusWord;
    
    public ThesaurusWordGui(ThesaurusWord thesaurusWord)
    {
        this.thesaurusWord = thesaurusWord;
    }

    public JTable getProperties()
    {
        String columnNames[] =
        {
            ApplicationUtilities.getResourceString("properties.attribute"),
            ApplicationUtilities.getResourceString("properties.value")
        };
        Object data[][] =
        {
            {
                ApplicationUtilities.getResourceString("thesaurus.word"), thesaurusWord.getWord()
            }
        };
        JTable properties = new JTable(new PropertiesTableModel(columnNames, 1, data)
        {
            private static final long serialVersionUID = 8070655135515222542L;

            public boolean isCellEditable(int row, int col)
            {
                return col == 1 && row == 0;
            }
        });
        TableColumn valueColumn = properties.getColumn(columnNames[1]);
        valueColumn.setCellEditor(new PropertiesCellEditor());
        properties.getModel().addTableModelListener(new TableModelListener()
        {
            public void tableChanged(TableModelEvent e)
            {
                int row = e.getFirstRow();
                int column = e.getColumn();
                Object data = ((TableModel) e.getSource()).getValueAt(row, column);
                String label = (String) data;
                if (!thesaurusWord.getWord().equals(label))
                    thesaurusWord.setWord(label);
            }
        });
        return properties;
    }
}