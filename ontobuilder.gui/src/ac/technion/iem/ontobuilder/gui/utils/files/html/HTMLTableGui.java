package ac.technion.iem.ontobuilder.gui.utils.files.html;

import java.awt.Point;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.CellPosition;
import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.HTMLTable;

/**
 * <p>Title: HTMLTable</p>
 */
public class HTMLTableGui
{
	protected HTMLTable htmlTable;

    public HTMLTableGui(HTMLTable htmlTable)
    {
    	this.htmlTable = htmlTable;
    }
    
    public HTMLTableGui()
    {
    	this(new HTMLTable());
    }

    public void addCell(int r, int c, Node node)
    {
    	this.htmlTable.addCell(r, c, node);
    }

    public Node getCell(int r, int c)
    {
        return this.htmlTable.getCell(r, c);
    }

    public static HTMLTableGui buildTable(Element tableElement)
    {
        HTMLTableGui table = new HTMLTableGui(HTMLTable.buildTable(tableElement));
        return table;
    }

    public Point getNodeLocation(Node node)
    {
        CellPosition loc = this.htmlTable.getNodeLocation(node);
        if (loc != null) return new Point(loc.x,loc.y);
        return null;
    }

    public Node getLeftCell(Node node)
    {
    	return this.htmlTable.getLeftCell(node);
    }

    public Node getRightCell(Node node)
    {
    	return this.htmlTable.getRightCell(node);
    }

    public Node getTopCell(Node node)
    {
    	return this.htmlTable.getTopCell(node);
    }

    public Node getNonEmptyTopCell(Node node)
    {
    	return this.htmlTable.getNonEmptyTopCell(node);
    }

    public Node getBottomCell(Node node)
    {
    	return this.htmlTable.getBottomCell(node);
    }

    public void print()
    {
    	this.htmlTable.print();
    }
}