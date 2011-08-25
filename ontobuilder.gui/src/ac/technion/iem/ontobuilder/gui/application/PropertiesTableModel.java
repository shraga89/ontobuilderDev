package ac.technion.iem.ontobuilder.gui.application;

import java.util.HashMap;

import javax.swing.table.AbstractTableModel;

/**
 * <p>Title: PropertiesTableModel</p>
 * <p>Description:  A table model for properties editing</p>
 * Extends {@link AbstractTableModel}
 */
public class PropertiesTableModel extends AbstractTableModel
{
    private static final long serialVersionUID = 1L;

    String columnNames[];
    HashMap<Object, Object> dataMap;
    Object data[][];

    /**
     * Constructs a PropertiesTableModel
     * 
     * @param columnNames the columns names
     * @param propsCnt the properties count
     * @param data array of the data objects
     */
    public PropertiesTableModel(String columnNames[], int propsCnt, Object data[][])
    {
        this.columnNames = columnNames;
        dataMap = new HashMap<Object, Object>();
        this.data = data;
        for (int i = 0; i < propsCnt; i++)
        {
            dataMap.put(data[i][0], data[i][1]);
        }
    }

    /**
     * Get the column count
     */
    public int getColumnCount()
    {
        return columnNames.length;
    }

    /**
     * Get the row count
     */
    public int getRowCount()
    {
        return data.length;
    }

    /**
     * Get the column name
     * 
     * @param index of the column to get a the name of
     */
    public String getColumnName(int col)
    {
        return columnNames[col];
    }

    /**
     * Get the value at a current cell according to its row and column location
     * 
     * @param row the row index
     * @param col the column index
     */
    public Object getValueAt(int row, int col)
    {
        return data[row][col];
    }

    /**
     * Get a data object by its key
     * 
     * @param key the key
     * @return the object
     */
    public Object getValueByKey(String key)
    {
        return dataMap.get(key);
    }

    /**
     * Checks whether a certain cell is editable
     * 
     * @param row the row index
     * @param col the column index
     */
    public boolean isCellEditable(int row, int col)
    {
        return true;
    }

    /**
     * Set the value in a specific cell
     * 
     * @param the value to set
     * @param row the row index
     * @param col the column index
     */
    public void setValueAt(Object value, int row, int col)
    {
        if (col == 1)
        {
            data[row][col] = value;
            dataMap.put(data[row][0], data[row][1]);
            fireTableCellUpdated(row, col);
        }
    }

    /**
     * Get the map with all the data objects
     * 
     * @return the map
     */
    public HashMap<Object, Object> getDataMap()
    {
        return dataMap;
    }
}