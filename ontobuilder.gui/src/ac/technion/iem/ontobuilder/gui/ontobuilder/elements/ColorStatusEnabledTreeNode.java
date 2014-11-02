/**
 * 
 */
package ac.technion.iem.ontobuilder.gui.ontobuilder.elements;

import java.awt.Color;

import ac.technion.iem.ontobuilder.gui.ontology.OntologyGui.TERM_MATCH_STATUS;


/**
 * @author Tomer Sagi
 *
 */
public class ColorStatusEnabledTreeNode extends javax.swing.tree.DefaultMutableTreeNode {

	/**
	 * Version of mutable tree node with colors
	 */
	private static final long serialVersionUID = -2540377525346378678L;
	
	private Color bgColor = null;
	private TERM_MATCH_STATUS s = TERM_MATCH_STATUS.UNDECIDED; 

	
	public ColorStatusEnabledTreeNode(Object userObject) {
		super(userObject);
	}

	/**
	 * @return the bgColor
	 */
	public Color getBgColor() {
		return bgColor;
	}

	/**
	 * @param bgColor the bgColor to set
	 */
	public void setBgColor(Color bgColor) {
		this.bgColor = bgColor;
	}

	/**
	 * @return the match status
	 */
	public TERM_MATCH_STATUS getStatus() {
		return s;
	}

	/**
	 * @param s the match status to set
	 */
	public void setStatus(TERM_MATCH_STATUS s) {
		this.s = s;
	}
	

}
