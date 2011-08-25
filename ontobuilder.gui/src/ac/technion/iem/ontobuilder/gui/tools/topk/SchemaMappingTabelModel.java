package ac.technion.iem.ontobuilder.gui.tools.topk;

import java.util.Collections;
import java.util.LinkedList;

import javax.swing.table.AbstractTableModel;

import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.utils.MatchedPairsComperator;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.wrapper.SchemaMatchingsWrapper;
import ac.technion.iem.ontobuilder.matching.meta.match.MatchedAttributePair;

/**
 * <p>Title: SchemaMappingTabelModel</p>
 * Extends {@link AbstractTableModel}
 */
public class SchemaMappingTabelModel extends AbstractTableModel
{

    private static final long serialVersionUID = 1149516121795214112L;

    /**
     * Sets table data and refreshes its view
     * 
     * @param data a list of matched attribute pairs
     */
    public void setTableData(LinkedList<?> data, SchemaMatchingsWrapper smw)
    {
        Collections.sort(data, new MatchedPairsComperator());
        records = (MatchedAttributePair[]) data.toArray();
        this.smw = smw;
        fireTableDataChanged(); // signal the JTabel of the model that data was changed
    }

    /**
     * Get the value at a specific row and column
     */
    public Object getValueAt(int row, int col)
    {
        MatchedAttributePair pair = records[row];
        Object toReturn = null;
        switch (col)
        {
        case (0):
            // Term candidateTerm =
            // smw.getMatchMatrix().getTermByName(pair.getAttribute1(),smw.getMatchMatrix().getCandidateTerms());
            toReturn = pair.getAttribute1();
            break;
        case (1):
            // Term targetTerm =
            // smw.getMatchMatrix().getTermByName(pair.getAttribute2(),smw.getMatchMatrix().getTargetTerms());
            toReturn = pair.getAttribute2();
            break;
        case (2):
            toReturn = "" + pair.getMatchedPairWeight();
            break;
        }
        return toReturn;
    }

    /**
     * Empties a table from its data
     */
    public void emptyTable()
    {
        records = new MatchedAttributePair[0];
        fireTableDataChanged(); // signal the JTabel of the model that data was changed
    }

    /**
     * Get the column name
     * 
     * @param col to return
     * @return column name
     */
    public String getColumnName(int col)
    {
        return columnNames[col];
    }

    /**
     * Get the number of columns
     * 
     * @return column count
     */
    public int getColumnCount()
    {
        return columnNames.length;
    }

    /**
     * Get the number of rows
     * 
     * @return row count
     */
    public int getRowCount()
    {
        return records.length;
    }

    /**
     * Check if a cell is editable
     * 
     * @param row
     * @param col
     * @return always false, table is Read-Only
     */
    public boolean isCellEditable(int row, int col)
    {
        return false; // Read-Only table.
    }

    /**
     * @return Object at table row need to convert to specific object
     */
    public Object getRecordAtRow(int row)
    {
        return records[row];
    }

    /** holds table records */
    protected MatchedAttributePair[] records = new MatchedAttributePair[0];

    /** columnNames[i] holds the name of the i'th column. */
    protected static String[] columnNames =
    {
        "Candidate Term", "Target Term", "Effectiveness"
    };
    private SchemaMatchingsWrapper smw;

    public SchemaMatchingsWrapper getSchemaMatchingsWrapper()
    {
        return this.smw;
    }
}
