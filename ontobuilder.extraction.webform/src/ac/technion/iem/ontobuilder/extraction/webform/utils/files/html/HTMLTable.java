package ac.technion.iem.ontobuilder.extraction.webform.utils.files.html;

import java.util.ArrayList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import ac.technion.iem.ontobuilder.core.utils.dom.DOMUtilities;

/**
 * <p>Title: HTMLTable</p>
 */
public class HTMLTable
{
    private ArrayList<ArrayList<Node>> rows;

    public HTMLTable()
    {
        rows = new ArrayList<ArrayList<Node>>();
    }

    public void addCell(int r, int c, Node node)
    {
        while (r >= rows.size())
            rows.add(new ArrayList<Node>());
        ArrayList<Node> columns = rows.get(r);
        while (c >= columns.size())
            columns.add(null);
        for (; c < columns.size() && columns.get(c) != null; c++)
            ;
        if (c == columns.size())
            columns.add(null);
        columns.set(c, node);
    }

    public Node getCell(int r, int c)
    {
        if (r >= rows.size())
            throw new IllegalArgumentException(r +
                " is greater than the number of rows in the table (" + rows.size() + ")");
        ArrayList<Node> columns = rows.get(r);
        if (c >= columns.size())
            throw new IllegalArgumentException(c +
                " is greater than the number of columns in row " + r + " (" + columns.size() + ")");
        return columns.get(c);
    }

    public static HTMLTable buildTable(Element tableElement)
    {
        HTMLTable table = new HTMLTable();
        ArrayList<Element> trElements = DOMUtilities.findChildElementsWithTag(tableElement, "tr");
        for (int row = 0; row < trElements.size(); row++)
        {
            Element trElement = (Element) trElements.get(row);
            ArrayList<Element> tdElements = DOMUtilities.findChildElementsWithTag(trElement, "td");
            for (int col = 0; col < tdElements.size(); col++)
            {
                int rowspan = 1;
                int colspan = 1;
                Element tdElement = (Element) tdElements.get(col);
                String rowspanStr = tdElement.getAttribute("rowspan");
                if (rowspanStr.length() > 0)
                    try
                    {
                        rowspan = Integer.parseInt(rowspanStr);
                    }
                    catch (NumberFormatException e)
                    {
                        rowspan = 1;
                    }
                String colspanStr = tdElement.getAttribute("colspan");
                if (colspanStr.length() > 0)
                    try
                    {
                        colspan = Integer.parseInt(colspanStr);
                    }
                    catch (NumberFormatException e)
                    {
                        colspan = 1;
                    }
                for (; rowspan > 0; rowspan--)
                {
                    int colspanAux = colspan;
                    for (; colspanAux > 0; colspanAux--)
                        table.addCell(row + rowspan - 1, col + colspanAux - 1, tdElement);
                }
            }
        }
        return table;
    }

    public CellPosition getNodeLocation(Node node)
    {
        for (int r = 0; r < rows.size(); r++)
        {
            ArrayList<Node> columns = rows.get(r);
            for (int c = 0; c < columns.size(); c++)
            {
                Node cell = columns.get(c);
                if (cell != null && cell.equals(node))
                    return new CellPosition(c, r);
            }
        }
        return null;
    }

    public Node getLeftCell(Node node)
    {
    	CellPosition loc = getNodeLocation(node);
        if (loc == null)
            return null;
        if (loc.x == 0)
            return null; // The node is the first cell in the row
        ArrayList<Node> columns = rows.get(loc.y);
        return columns.get(loc.x - 1);
    }

    public Node getRightCell(Node node)
    {
    	CellPosition loc = getNodeLocation(node);
        if (loc == null)
            return null;
        ArrayList<Node> columns = rows.get(loc.y);
        if (loc.x == columns.size() - 1)
            return null; // The node is the last cell in the row
        return columns.get(loc.x + 1);
    }

    public Node getTopCell(Node node)
    {
    	CellPosition loc = getNodeLocation(node);
        if (loc == null)
            return null;
        if (loc.y == 0)
            return null; // The node is in the first row
        ArrayList<Node> columns = rows.get(loc.y - 1);
        if (loc.x >= columns.size())
            return null; // There is no cell above
        return columns.get(loc.x);
    }

    public Node getNonEmptyTopCell(Node node)
    {
    	CellPosition loc = getNodeLocation(node);
        if (loc == null)
            return null;
        if (loc.y == 0)
            return null; // The node is in the first row
        for (int i = loc.y - 1; i >= 0; i--)
        {
            ArrayList<Node> columns = rows.get(i);
            if (loc.x >= columns.size())
                continue; // There is no cell above
            Node n = columns.get(loc.x);
            if (DOMUtilities.getTextValue(n).trim().length() > 0)
                return n;
        }
        return null;
    }

    public Node getBottomCell(Node node)
    {
    	CellPosition loc = getNodeLocation(node);
        if (loc == null)
            return null;
        if (loc.y == rows.size() - 1)
            return null; // The node is in the last row
        ArrayList<Node> columns = rows.get(loc.y + 1);
        if (loc.y >= columns.size())
            return null; // There is no cell below
        return columns.get(loc.y);
    }

    public void print()
    {
        System.out.println("******************************");
        for (int r = 0; r < rows.size(); r++)
        {
            ArrayList<Node> columns = rows.get(r);
            for (int c = 0; c < columns.size(); c++)
            {
                Node node = columns.get(c);
                System.out.println("(" + r + "," + c + ") " + DOMUtilities.getTextValue(node));
            }
        }
        System.out.println("******************************");
    }
}