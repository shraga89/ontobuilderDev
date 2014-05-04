package ac.technion.iem.ontobuilder.gui.match;

import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;

import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;

/**
 * <p>Title: Match</p>
 * Extends {@link DefaultTableModel}
 */
public class MIPanelMatchTableModel extends DefaultTableModel
{
	static Logger l = Logger.getLogger(MIPanelMatchTableModel.class);
    private static final long serialVersionUID = -4900105414832436175L;
	private MatchInformation mi;
	private MatchInformation smi;
	
    public MIPanelMatchTableModel(Object columnNames[], Object data[][])
    {
        super(data, columnNames);
    }

    /**
     * Constructor - creates the table and fills the data
     * @param myMi
     * @param suggestionMI
     */
    public MIPanelMatchTableModel(MatchInformation myMi,MatchInformation suggestionMi) {
    	super();
    	this.mi = myMi;
    	this.smi = suggestionMi;
    	this.addColumn("termIDs");
    	this.addColumn("Candidate Terms");
    	this.addColumn("Data Type");
    	this.addColumn("Your Confidence Score");
    	if (suggestionMi!=null)
    		this.addColumn("Suggestion's Confidence");
    	
    	for (Term t : mi.getOriginalCandidateTerms())
    		{
    			String[] row;
    			if (suggestionMi!=null)
    				row = new String[] {Long.toString(t.getId()), t.getName(),t.getDomain().getName(),"0.0","0.0"};
    			else
    				row = new String[] {Long.toString(t.getId()),t.getName(),t.getDomain().getName(),"0.0"};
    			this.addRow(row);
    		}
    	fireTableStructureChanged();
	}

	public boolean isCellEditable(int row, int col)
    {
        return (col==3);
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

	public void clear() {
		for (int i=0;i<this.getRowCount();i++)
		{
			this.setValueAt("0.0", i, 3);
			if (this.getColumnCount()==5)
				this.setValueAt("0.0", i, 4);
		}
		
	}

	/**
	 * Updates the match table model to show 
	 * candidate term values for the given
	 * target term.
	 * @param t Target Term for which values are shown.
	 */
	public void setTerm(Term t) {

		for (int i = 1; i <this.getRowCount() ; i++)
		{
			Long termID = Long.parseLong((String)this.getValueAt(i, 0));
			this.setValueAt(Double.toString(mi.getMatrix().getMatchConfidenceByID(termID, t.getId())), i, 3);
			if (smi!=null)
				this.setValueAt(Double.toString(smi.getMatrix().getMatchConfidenceByID(termID, t.getId())), i, 4);
		}
		
		
	}
}
