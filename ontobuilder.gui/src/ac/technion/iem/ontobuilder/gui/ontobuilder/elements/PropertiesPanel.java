package ac.technion.iem.ontobuilder.gui.ontobuilder.elements;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * <p>Title: OntologyPanel</p>
 * Extends a {@link JPanel}
 */
public class PropertiesPanel extends JPanel
{
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a default PropertiesPanel
     */
    public PropertiesPanel()
    {
        setLayout(new BorderLayout());
    }

    public void showProperties(JTable properties)
    {
        removeAll();
        if (properties != null)
        {
            properties.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer()
            {
                private static final long serialVersionUID = 1L;

                public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column)
                {
                    super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
                        column);
                    setFont(new Font(getFont().getName(), Font.BOLD, getFont().getSize()));
                    return this;
                }
            });
            add(BorderLayout.NORTH, properties.getTableHeader());
            add(BorderLayout.CENTER, properties);
        }
        else
            add(BorderLayout.CENTER, new JPanel());
        revalidate();
    }
}