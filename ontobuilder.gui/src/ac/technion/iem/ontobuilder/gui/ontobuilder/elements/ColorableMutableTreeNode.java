/**
 * 
 */
package ac.technion.iem.ontobuilder.gui.ontobuilder.elements;

import java.awt.Color;


/**
 * @author Tomer
 *
 */
public class ColorableMutableTreeNode extends javax.swing.tree.DefaultMutableTreeNode {

	/**
	 * Version of mutable tree node with colors
	 */
	private static final long serialVersionUID = -2540377525346378678L;
	
	private Color bgColor = null;

	
	public ColorableMutableTreeNode(Object userObject) {
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
	

}
