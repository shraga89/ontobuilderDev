package ac.technion.iem.ontobuilder.gui.utils;

import java.util.ArrayList;

import javax.swing.JTable;
import javax.swing.table.TableModel;

import ac.technion.iem.ontobuilder.core.utils.StringUtilities;

/**
 * <p>
 * Title: StringUtilitiesGui
 * </p>
  * <p>Description: Implements the methods of the StringUtilities used by the GUI</p>
  * extends {@link StringUtilities}
 */
public class StringUtilitiesGui extends StringUtilities
{

	public static String getJTableStringRepresentation(JTable table)
	{
		StringBuffer bf = new StringBuffer("");

		TableModel model = table.getModel();
		ArrayList<Integer> columnsWidths = new ArrayList<Integer>();
		for (int i = 0; i < model.getColumnCount(); i++)
		{
			int width = model.getColumnName(i).length();
			for (int j = 0; j < model.getRowCount(); j++)
			{
				Object o = model.getValueAt(j, i);
				if (o == null)
					continue;
				width = Math.max(width, o.toString().length());
			}
			columnsWidths.add(new Integer(width));
		}
		for (int i = 0; i < model.getColumnCount(); i++)
		{
			String columnName = model.getColumnName(i);
			int width = ((Integer) columnsWidths.get(i)).intValue();
			bf.append(columnName).append(buildString(' ', width - columnName.length() + 1));
		}
		bf.append("\n");
		for (int i = 0; i < columnsWidths.size(); i++)
		{
			int width = ((Integer) columnsWidths.get(i)).intValue();
			bf.append(buildString('-', width)).append(" ");
		}
		bf.append("\n");

		for (int i = 0; i < model.getRowCount(); i++)
		{
			for (int j = 0; j < model.getColumnCount(); j++)
			{
				int width = ((Integer) columnsWidths.get(j)).intValue();
				Object o = model.getValueAt(i, j);
				if (o == null)
					continue;
				String cell = o.toString();
				bf.append(cell).append(buildString(' ', width - cell.length() + 1));
			}
			bf.append("\n");
		}

		return bf.toString();
	}
	
}
