package ac.technion.iem.ontobuilder.gui.ontobuilder.elements;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SortOrder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.table.TableColumnExt;

import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.core.resources.OntoBuilderResources;
import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.elements.TextPane;
import ac.technion.iem.ontobuilder.gui.match.MIPanelMatchTableModel;
import ac.technion.iem.ontobuilder.gui.ontobuilder.main.OntoBuilder;
import ac.technion.iem.ontobuilder.gui.ontology.OntologyGui;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.common.MatchingAlgorithmsNamesEnum;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.simple.Max2LM;
import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.wrapper.OntoBuilderWrapper;
import ac.technion.iem.ontobuilder.matching.wrapper.OntoBuilderWrapperException;

/**
 * <p>Title: Match Information Panel</p>
 * Extends a {@link JPanel} and provides Match edit capabilities.  
 * Singleton design pattern
 */
public final class MIPanel extends JPanel
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
	private static MIPanel instance = null;
	private boolean binSugg = true;//TODO make this a property
	public static enum SUGG_BEHAVIOR{
		NONE("No Suggestions"),ALWAYSSHOW("Always Show Suggestions"),UPONREQUEST("Show Suggestions UnLimited"),LIMITED("Show Suggestions Limited");
	private final String label;
	SUGG_BEHAVIOR(String lbl)
	{
		label = lbl;
	}
	
	public String toString()
	{
		return label;
	}
	};
	public static String SUGG_BEHAVIOR_PROPERTY = "suggestionBehavior";
	private SUGG_BEHAVIOR suggB = SUGG_BEHAVIOR.UPONREQUEST;
	
	private JButton suggestB;
	private JButton noMatchB;
	private TextPane suggestCounter;
	private TextPane limitField;
	private JXTable table;
	private TableColumnExt suggColumn; 
	private int suggLimit = 30; //TODO make this a property
	private Logger userActionLog = Logger.getLogger(MIPanel.class);
	private JLabel outOf;
	
	/**
	 * If true then the MIPanel shows binary suggestions. 
	 * @return the binSugg
	 */
	public boolean isBinSuggestions() {
		return binSugg;
	}

	/**
	 * If true then the MIPanel will show binary suggestions. 
	 * @param binSugg the binSugg to set
	 */
	public void setBinSugg(boolean binSugg) {
		this.binSugg = binSugg;
	}
	
    /**
	 * @return the suggLimit
	 */
	public int getSuggLimit() {
		return suggLimit;
	}

	/**
	 * @param suggLimit the suggLimit to set
	 */
	public void setSuggLimit(int suggLimit) {
		this.suggLimit = suggLimit;
	}

	/**
     * Constructs a default OntologyPanel, private due to singleton
     * 
     * @param ontoBuilder the {@link OntoBuilder} application
     */
    private MIPanel()
    {
    	//Logging
    	PropertyConfigurator.configure(OntoBuilderResources.Config.LOG4J_PROPERTIES);
    	
    	//Layout
    	table = new JXTable(30,5);
    	table.setAutoCreateRowSorter(true);
    	table.setSortOrderCycle(SortOrder.DESCENDING, SortOrder.ASCENDING, SortOrder.UNSORTED);
    	suggColumn = table.getColumnExt(4);
        miPane = new JScrollPane(table);
		JPanel controlPane = new JPanel();
		controlPane.setLayout(new GridLayout(3,3));
		JSplitPane mainPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, miPane, controlPane);
		mainPane.setDividerLocation(0.2);
		setLayout(new BorderLayout());
		add(BorderLayout.CENTER, mainPane);
		JLabel tt = new JLabel("Target Term:");
		Font font = tt.getFont();
		tt.setFont(new Font(font.getFontName(), Font.BOLD, font.getSize()));
		ttt = new TextPane("");
		ttt.setBackground(Color.WHITE);
		tt.setVerticalTextPosition(JLabel.TOP);
		suggestCounter = new TextPane("0");
		suggestCounter.setBackground(Color.white);
		suggestCounter.setAlignmentX(0.0f);
		limitField = new TextPane(Integer.toString(suggLimit));
		limitField.setAlignmentX(0.0f);
		outOf = new JLabel(" of ");
		outOf.setVerticalTextPosition(JLabel.TOP);
		outOf.setVerticalAlignment(JLabel.TOP);
		ImageIcon icon = ApplicationUtilities.getImage("lifesaver.gif");
		suggestB = new JButton(icon);
		suggestB.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int used = Integer.parseInt(suggestCounter.getText());
				if (table.getColumnCount()<4 && used <= suggLimit)
				{
					suggColumn.setVisible(true);
					used++;
					suggestCounter.setText(Integer.toString(used));
					userActionLog.info("|showSuggestion|" + targetTerm.getId() + "|" + targetTerm.toString() + "|used:|" + used + "|of:|" + suggLimit);
				}
			}
		});
		
		noMatchB = new JButton("No Match!");
		noMatchB.setBackground(Color.red);
		noMatchB.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				userActionLog.info("|noMatch|" + targetTerm.getId() + "|" + targetTerm.toString());
				for (int i=0;i<tm.getRowCount();i++)
				{ 
					double val = Double.parseDouble((String)tm.getValueAt(i, 3));
					if (val > 0.0)
					{
						tm.setValueAtNoFire("0.0", i, 3);
						mi.updateMatch(targetTerm, mi.getCandidateOntology().getTermByID(Long.parseLong((String)tm.getValueAt(i, 0))), 0.0);
					}
				}
				targGui.colorTerm(targetTerm, Color.red.brighter());
			}
		});
		controlPane.add(tt);
		controlPane.add(ttt);
		controlPane.add(noMatchB);
		controlPane.add(suggestB);
		controlPane.add(new JLabel(""));
		controlPane.add(new JLabel(""));
		controlPane.add(suggestCounter);
		controlPane.add(outOf);
		controlPane.add(limitField);
		
		if (suggB.equals(SUGG_BEHAVIOR.NONE))
		{
			suggestB.setVisible(false);
			suggestCounter.setVisible(false);
			outOf.setVisible(false);
			limitField.setVisible(false);
		}
    }

    /**
     * Use instead of constructor (singleton)
     * @return
     */
    public static MIPanel getMIPanel()
    {
    	if (instance==null)
    		instance = new MIPanel();
    	return instance;
    }

	/**
	 * @return the matchInformation
	 */
	public MatchInformation getMi() {
		return mi;
	}

	public void setMi(OntologyGui candidate, OntologyGui target) {
		
		this.suggB = SUGG_BEHAVIOR.ALWAYSSHOW;
		this.candGui = candidate;
		this.setTargGui(target);
		this.mi = new MatchInformation(candidate.getOntology(),target.getOntology());
		//TODO make algorithm choice interactive / property based
		OntoBuilderWrapper obw = new OntoBuilderWrapper();
		try {
			MatchInformation flmRes = obw.matchOntologies(candidate.getOntology(), target.getOntology(), MatchingAlgorithmsNamesEnum.TERM.getName());
			
			if (binSugg)
			{
				Max2LM mxDelta = new Max2LM(0.1d);
				suggestions = mxDelta.match(flmRes);
				for (Match m : suggestions.getCopyOfMatches())
					suggestions.updateMatch(m.getTargetTerm(),m.getCandidateTerm(), 1.0d);
			} else
			{
				suggestions = flmRes;
			}
		} catch (OntoBuilderWrapperException e) {
			e.printStackTrace();
		}
		tm = new MIPanelMatchTableModel(mi,suggestions);
		table.setModel(tm);
		suggColumn = table.getColumnExt(4);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getColumnExt(0).setVisible(false);
		table.getColumnModel().getColumn(2).setCellRenderer(new PercentageRenderer(new DecimalFormatRenderer()) );
		table.getColumnModel().getColumn(3).setCellRenderer(new PercentageRenderer(new DecimalFormatRenderer()));

		if (!this.suggB.equals(SUGG_BEHAVIOR.ALWAYSSHOW))
			 suggColumn.setVisible(false);
		table.validate();
		miPane.validate();
		TableModelListener changeListener = new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {

				Long termID = Long.parseLong((String)tm.getValueAt(e.getFirstRow(), 0));
				Term candTerm = mi.getMatrix().getTermByID(termID,true);
				Double val = Double.parseDouble((String)tm.getValueAt(e.getFirstRow(), 3));
				mi.updateMatch(targetTerm, candTerm,val);
				userActionLog.info("|matched|" + targetTerm.getId() + "|" + targetTerm.toString() + "|" + termID+ "|" + candTerm.getProvenance() + "|" + val);
				//drawArcs(targetTerm,true);
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
                    String termID = (String)tm.getValueAt(table.convertRowIndexToModel(selectedRow),0);
                    candGui.setSelectionToTerm(Long.parseLong(termID));
    				//TODO show arc from target term to this term
                }
				
				
			
			}
		};
		table.getSelectionModel().addListSelectionListener(selectionListener ); 
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
		if ((suggB.equals(SUGG_BEHAVIOR.UPONREQUEST) || suggB.equals(SUGG_BEHAVIOR.LIMITED)) && table.getColumnCount()>3)
			suggColumn.setVisible(false);
		miPane.validate();
		userActionLog.info("|setTargetTerm|" + targetTerm.getId() + "|" + targetTerm.getProvenance());
		//drawArcs(t,true);
		
	}
	
	/**
	 * Draws arcs according to current match information from supplied term
	 * @param t Term to draw arcs from
	 * @param isTargetTerm is the term a target term?
	 */
	private void drawArcs(Term t, boolean isTargetTerm) 
	{
		OntologyPanelSBS op = OntologyPanelSBS.getInstance();
		op.clearArcs();
		List<Match> matches = mi.getMatchesForTerm(t, !isTargetTerm);
		if (matches==null)
			return;
		for (Match m : matches)
		{
			Term toTerm = (isTargetTerm ? m.getCandidateTerm() : m.getTargetTerm());
			OntologyPanelSBS.getInstance().draw_line(t, toTerm, Double.toString(m.getEffectiveness()));
		}
	}

	/**
	 * @return the suggB
	 */
	public SUGG_BEHAVIOR getSuggB() {
		return suggB;
	}

	/**
	 * @param string the suggB to set
	 */
	public void setSuggB(String label) {
		for (SUGG_BEHAVIOR sb :SUGG_BEHAVIOR.values())
			if (sb.toString().equals(label))
			{
				this.suggB = sb;
				break;
			}
		switch (this.suggB)
		{
		case ALWAYSSHOW:
			suggColumn.setVisible(true);
			suggestB.setVisible(false);
			this.limitField.setVisible(false);
			this.suggestCounter.setVisible(false);
			this.outOf.setVisible(false);
			break;
		case UPONREQUEST:
			suggColumn.setVisible(false);
			suggestB.setVisible(true);
			this.limitField.setVisible(false);
			this.suggestCounter.setVisible(true);
			this.outOf.setVisible(false);
			break;
		case LIMITED:
			suggColumn.setVisible(false);
			suggestB.setVisible(true);
			this.limitField.setVisible(true);
			this.suggestCounter.setVisible(true);
			this.outOf.setVisible(true);
			break;
		case NONE:
			suggColumn.setVisible(false);
			suggestB.setVisible(false);
			this.limitField.setVisible(false);
			this.suggestCounter.setVisible(false);
			this.outOf.setVisible(false);
			break;
		}
	}

	/**
	 * @return the targGui
	 */
	public OntologyGui getTargGui() {
		return targGui;
	}

	/**
	 * @param targGui the targGui to set
	 */
	public void setTargGui(OntologyGui targGui) {
		this.targGui = targGui;
	}

	static class DecimalFormatRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = -1792145160785538894L;
		private static final DecimalFormat formatter = new DecimalFormat( "#.00" );
		
		public Component getTableCellRendererComponent(JTable table, Object value, 
				boolean isSelected, boolean hasFocus, int row, int column) {
				value = formatter.format(Double.parseDouble((String)value));
		return super.getTableCellRendererComponent(
		table, value, isSelected, hasFocus, row, column );
		}
	}
	
	static public class PercentageRenderer implements TableCellRenderer
	{
	  private TableCellRenderer delegate;
	  private NumberFormat formatter;

	  public PercentageRenderer(TableCellRenderer defaultRenderer)
	  {
	    this.delegate = defaultRenderer;
	    formatter = NumberFormat.getPercentInstance();
	  }

	  public Component getTableCellRendererComponent(JTable table, Object value, 
	                           boolean isSelected, boolean hasFocus, int row, int column) 
	  {
	    Component c = delegate.getTableCellRendererComponent(table, value, isSelected, 
	                                                                hasFocus, row, column);
	    String s = formatter.format(Double.parseDouble((String)value));
	    if (c instanceof JLabel)
	      ((JLabel)c).setText(s);
	    return c;
	  }
	} 
	
	/**
	 * Set selection in term table to supplied
	 * candidate term if exists
	 * @param term
	 */
	public void selectCandidateTerm(Term term) {
		long termID = term.getId();
		int row = table.convertRowIndexToView(this.tm.findTerm(termID));
		if (row != -1)
			this.table.changeSelection(row, 0, false, false); 
		
	}
}