package ac.technion.iem.ontobuilder.gui.ontobuilder.elements;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Component;
import java.awt.Label;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.gui.elements.TextPane;
import ac.technion.iem.ontobuilder.gui.match.MIPanelMatchTableModel;
import ac.technion.iem.ontobuilder.gui.ontobuilder.main.OntoBuilder;
import ac.technion.iem.ontobuilder.gui.ontology.OntologyGui;
import ac.technion.iem.ontobuilder.gui.ontology.TermGui;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.term.TermAlgorithm;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;

/**
 * <p>Title: Match Information Panel</p>
 * Extends a {@link JPanel} and provides Match edit capabilities.  
 */
public class MIPanel extends JPanel
{
    private static final long serialVersionUID = 1L;
    private JScrollPane miPane;
    private MatchInformation mi = null;
    private MatchInformation suggestions = null;
    private MIPanelMatchTableModel tm;
	private OntologyGui candGui = null;
	private OntologyGui targGui = null;
	private Term targetTerm = null;
	private TextPane ttt; 
    /**
     * Constructs a default OntologyPanel
     * 
     * @param ontoBuilder the {@link OntoBuilder} application
     */
    public MIPanel()
    {
    	JTable table = new JTable(30,4);
    	table.setAutoCreateRowSorter(true);
        miPane = new JScrollPane(table);
		JPanel controlPane = new JPanel();
		JSplitPane mainPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, miPane, controlPane);
		mainPane.setDividerLocation(0.8);
		setLayout(new BorderLayout());
		add(BorderLayout.CENTER, mainPane);
		Label tt = new Label("Target Term:");
		ttt = new TextPane("");
		Button suggest = new Button("Show Suggestions");
		controlPane.add(suggest);
		controlPane.add(tt);
		controlPane.add(ttt);
    }


	/**
	 * @return the matchInformation
	 */
	public MatchInformation getMi() {
		return mi;
	}

	public void setMi(OntologyGui candidate, OntologyGui target) {
		this.candGui = candidate;
		this.targGui = target;
		this.mi = new MatchInformation(candidate.getOntology(),target.getOntology());
		//TODO make algorithm choice interactive / property based
		this.suggestions = targGui.match(candGui.getOntology(), new TermAlgorithm());
		tm = new MIPanelMatchTableModel(mi,suggestions);
		JTable t = (JTable) ((JViewport) miPane.getComponent(0)).getComponent(0);
		t.setModel(tm);
		t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		t.removeColumn(t.getColumn("termIDs"));
		t.validate();
		miPane.validate();
		TableModelListener changeListener = new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				// TODO  this should update the MI with the user changes
				
			}
		};
		ListSelectionListener selectionListener = new ListSelectionListener() {
			
			
			/**
			 * Sets focus in the candidate ontology gui on the selected term
			 * @Override
			 */
			public void valueChanged(ListSelectionEvent e) { 
				if (e.getValueIsAdjusting()) return;
				ListSelectionModel lsm = (ListSelectionModel)e.getSource();
                if (!lsm.isSelectionEmpty()) {
                    int selectedRow = lsm.getMinSelectionIndex();
                    System.out.println("Row " + selectedRow
                                       + " is now selected.");
                    String termID = (String)tm.getValueAt(selectedRow,0);
                    candGui.setSelectionToTerm(Long.parseLong(termID));
    				//TODO show arc from target term to this term
                }
				
				
			
			}
		};
		t.getSelectionModel().addListSelectionListener(selectionListener ); 
		tm.addTableModelListener(changeListener);
	}

	/**
	 * Resets match information object by clearing all match data recorded.  
	 */

	public void resetMI() {
		if (mi==null)
		{
			return;
		}
		mi.clearMatches();
		tm.clear();
	}
	
	public void setTargetTerm(Term t)
	{
		this.targetTerm = t;
		this.ttt.setText(t.getName());
		tm.setTerm(t);
		miPane.validate();
	}

}