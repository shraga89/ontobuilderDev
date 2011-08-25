package ac.technion.iem.ontobuilder.gui.match;

import javax.swing.table.DefaultTableModel;

/**
 * <p>Title: Match</p>
 * Extends {@link DefaultTableModel}
 */
public class MatchTableModel extends DefaultTableModel
{
    private static final long serialVersionUID = -4900105414832436175L;

    public MatchTableModel(Object columnNames[], Object data[][])
    {
        super(data, columnNames);
    }

    public boolean isCellEditable(int row, int col)
    {
        return false;
    }

    public void refreshData(Object[][] data, int cnt)
    {
        int rows = getRowCount();
        for (int i = 0; i < rows; i++)
        {
            removeRow(0);
        }
        for (int i = 0; i < cnt; i++)
            insertRow(i, data[i]);
        fireTableDataChanged();
    }
}
