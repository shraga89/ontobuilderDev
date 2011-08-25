package ac.technion.iem.ontobuilder.gui.tools.exactmapping;

import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.OntologyUtilities;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.meta.match.MatchedAttributePair;
import ac.technion.iem.ontobuilder.matching.utils.SchemaTranslator;

/**
 * <p>Title: ExactTabelModel</p>
 * Extends a {@link AbstractTableModel}
 */
public class ExactTabelModel extends AbstractTableModel
{

    private static final long serialVersionUID = 1L;

    // used in order to display messages
    private ExactMappingTool parentComponent = null;

    /**
     * Constructs an ExactTabelModel
     * 
     * @param parent an {@link ExactMappingTool}
     */

    public ExactTabelModel(ExactMappingTool parent)
    {
        parentComponent = parent;
    }

    /**
     * Check if the table is populated
     * 
     * @return <code>true</code> if it is populated
     */
    public boolean isTablePopulated()
    {
        return data.length > 0;
    }

    /**
     * Update the current <code>ExactTabelModel</code> from the previous mapping
     * 
     * @param st a {@link SchemaTranslator}
     */
    public void updateFromPrevMapping(SchemaTranslator st)
    {
        MatchedAttributePair[] matches = st.getMatchedPairs();
        for (int i = 0; i < matches.length; i++)
        {
            MatchedAttributePair match = matches[i];
            for (int j = 0; j < data.length; j++)
            {
                ExactTableRowModel row = data[j];
                if ((OntologyUtilities.oneIdRemoval(row.getCandTerm().toString()).equals(
                    OntologyUtilities.oneIdRemoval(match.getAttribute1())) || OntologyUtilities
                    .oneIdRemoval(row.getCandTerm().toStringVs2()).equals(
                        OntologyUtilities.oneIdRemoval(match.getAttribute1()))) &&
                    (OntologyUtilities.oneIdRemoval(row.getTargetTerm().toString()).equals(
                        OntologyUtilities.oneIdRemoval(match.getAttribute2())) || OntologyUtilities
                        .oneIdRemoval(row.getTargetTerm().toStringVs2()).equals(
                            OntologyUtilities.oneIdRemoval(match.getAttribute2()))))
                {
                    row.setSelected(Boolean.TRUE);
                    break;
                }

            }
        }
        fireTableDataChanged();
    }

    /**
     * Sets table data and refreshes its view
     * 
     * @param cand the candidate {@link Ontology}
     * @param target the target {@link Ontology}
     * @param checkType the check type
     */
    public void setTableData(Ontology cand, Ontology target, int checkType)
    {
        candTermsCnt = cand.getTerms(true).size();
        targetTermsCnt = target.getTerms(true).size();
        ExactTableRowModel[] tempData = new ExactTableRowModel[candTermsCnt * targetTermsCnt];
        Term candTerm, targetTerm;
        int counter = 0;
        for (int i = 0; i < candTermsCnt; i++)
        {
            candTerm = (Term) cand.getTerms(true).get(i);
            if (shouldInclude(candTerm, checkType))
                for (int j = 0; j < targetTermsCnt; j++)
                {
                    targetTerm = (Term) target.getTerms(true).get(j);
                    if (shouldInclude(targetTerm, checkType))
                    {
                        tempData[counter] = new ExactTableRowModel(candTerm, targetTerm,
                            Boolean.FALSE);
                        counter++;
                    }
                }
        }
        // System.out.println("out of " + candTermsCnt + " cands and " + targetTermsCnt +
        // " terms, used " +counter);
        data = new ExactTableRowModel[counter];
        for (int i = 0; i < counter; i++)
            data[i] = tempData[i];
        fireTableDataChanged(); // signal the JTabel of the model that data was changed
    }

    /**
     * Checks if a cell is editable
     * 
     * @param row the row id
     * @param col the col id
     * @return <code>true</code> if it is editable
     */
    public boolean isCellEditable(int row, int col)
    {
        if (col == 2)
            return true;
        else
            return false;
    }

    /**
     * When selecting a value - checks if the target is already used
     * 
     * @param value the value to set
     * @param row the row id
     * @param col the col id
     */
    public void setValueAt(Object value, int row, int col)
    {
        if (col == 2)
        {
            Term cand = data[row].getCandTerm();
            Term targ = data[row].getTargetTerm();
            boolean crash = false;
            if (((Boolean) value) == Boolean.TRUE)
            {
                if (parentComponent.isGuardActive())
                {
                    for (int i = 0; i < data.length; i++)
                    {
                        if (data[i].getTargetTerm().toString().equals(targ.toString()) &&
                            data[i].getSelected().booleanValue())
                        {
                            JOptionPane.showMessageDialog(parentComponent,
                                "Target already used with candidate:\n" + data[i].getCandTerm(),
                                "Term Selection Error", JOptionPane.ERROR_MESSAGE);
                            // crash = true;
                        }
                        if (data[i].getCandTerm() == cand && data[i].getSelected().booleanValue())
                        {
                            JOptionPane.showMessageDialog(parentComponent,
                                "Candidate already has a target:\n" + data[i].getTargetTerm(),
                                "Term Selection Error", JOptionPane.ERROR_MESSAGE);
                            crash = true;
                        }

                    }
                }
            }
            if (!crash)
            {
                data[row].setSelected((Boolean) value);
                fireTableDataChanged();
            }
        }
    }

    /**
     * Get the value at a specific location
     * 
     * @param row the row id
     * @param col the col id
     * @return the value
     */
    public Object getValueAt(int row, int col)
    {
        ExactTableRowModel tableRow = data[row];
        Object toReturn = null;
        switch (col)
        {
        case (0):
            toReturn = tableRow.getCandTerm().toString();
            break;
        case (1):
            toReturn = tableRow.getTargetTerm().toString();

            break;
        case (2):
            toReturn = tableRow.getSelected();
            break;
        }
        return toReturn;
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
     * Get the column count
     * 
     * @return column count
     */
    public int getColumnCount()
    {
        return columnNames.length;
    }

    /**
     * Get the row count
     * 
     * @return row count
     */
    public int getRowCount()
    {
        return data.length;
    }

    /**
     * Get the row
     * 
     * @param index row index
     * @return an {@link ExactTableRowModel}
     */
    public ExactTableRowModel getRow(int index)
    {
        return data[index];
    }

    /**
     * Get the column class
     */
    public Class<?> getColumnClass(int columnIndex)
    {
        return types[columnIndex];
    }

    /**
     * Get the pairs count
     * 
     * @return the number of pairs
     */
    public int getSelectPairsCount()
    {
        int cnt = 0;
        for (int i = 0; i < data.length; i++)
        {
            if (data[i].getSelected().equals(Boolean.TRUE))
                cnt++;
        }
        return cnt;
    }

    /**
     * Get the selected pairs indexes
     * 
     * @return the indexes
     */
    public int[] getSelectedPairsIndexes()
    {
        int[] indexes = new int[getSelectPairsCount()];
        int j = 0;
        for (int i = 0; i < data.length; i++)
        {
            if (data[i].getSelected().equals(Boolean.TRUE))
                indexes[j++] = i;
        }
        return indexes;
    }

    protected ExactTableRowModel[] data = new ExactTableRowModel[0];

    /** columnNames[i] holds the name of the i'th column. */
    protected static String[] columnNames =
    {
        "Candidate Term", "Target Term", "Selected Match Pairs"
    };
    Class<?>[] types = new Class[]
    {
        Term.class, Term.class, java.lang.Boolean.class
    };
    int candTermsCnt;
    int targetTermsCnt;

    /**
     * Check if the term to check is included
     * 
     * @param termToCheck the {@link Term} to check
     * @param checkType the check type
     * @return <code>true</code> if the term to check is included
     */
    private boolean shouldInclude(Term termToCheck, int checkType)
    {
        String termType;
        boolean result = true;
        if (checkType > 0)
        {
            termType = termToCheck.toString().split(":")[0];
            if (((checkType >= 1) && (termType.equals("form") || termType.equals("page"))) ||
                ((checkType == 2) && (termType.equals("hidden") || termType.equals("group"))) ||
                ((checkType == 3) && (termType.equals("hidden"))) ||
                ((checkType == 4) && (termType.equals("hidden") || termType.equals("image"))))
                result = false;
        }
        return result;
    }
}
