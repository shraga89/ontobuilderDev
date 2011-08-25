package ac.technion.iem.ontobuilder.gui.tools.algorithms.line1;

import java.awt.Component;
import java.util.HashMap;

import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.application.PropertiesTableModel;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.Algorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.term.TermAlgorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.term.TermAlgorithmFlagsEnum;

public class TermAlgorithmGui extends AbstractAlgorithmGui
{
    private TermAlgorithm _termAlgorithm;
    private int mode;
    
    public TermAlgorithmGui(Algorithm abstractAlgorithm)
    {
        _termAlgorithm = (TermAlgorithm)abstractAlgorithm;
        mode = abstractAlgorithm.getMode();
    }
    
    protected TermAlgorithmGui()
    {
        
    }
    
    public JTable getProperties()
    {
        String columnNames[] =
        {
            ApplicationUtilities.getResourceString("properties.attribute"),
            ApplicationUtilities.getResourceString("properties.value")
        };
        Object data[][] =
        {
            {
                ApplicationUtilities.getResourceString("algorithm.term.symmetric"),
                new Boolean((mode & TermAlgorithmFlagsEnum.SYMMETRIC_FLAG.getValue()) > 0)
            },
            {
                ApplicationUtilities.getResourceString("algorithm.term.useThesaurus"),
                new Boolean((mode & TermAlgorithmFlagsEnum.USE_THESAURUS_FLAG.getValue()) > 0)
            },
            {
                ApplicationUtilities.getResourceString("algorithm.term.useSoundex"),
                new Boolean((mode & TermAlgorithmFlagsEnum.USE_SOUNDEX_FLAG.getValue()) > 0)
            },
            {
                ApplicationUtilities.getResourceString("algorithm.term.wordLabelWeight"),
                new Double(_termAlgorithm.getWordLabelWeight())
            },
            {
                ApplicationUtilities.getResourceString("algorithm.term.stringLabelWeight"),
                new Double(_termAlgorithm.getStringLabelWeight())
            },
            {
                ApplicationUtilities.getResourceString("algorithm.term.wordNameWeight"),
                new Double(_termAlgorithm.getWordNameWeight())
            },
            {
                ApplicationUtilities.getResourceString("algorithm.term.stringNameWeight"),
                new Double(_termAlgorithm.getStringNameWeight())
            },
            {
                ApplicationUtilities.getResourceString("algorithm.term.maxCommonSubStringWeight"),
                new Double(_termAlgorithm.getMaxCommonSubStringWeight())
            },
            {
                ApplicationUtilities.getResourceString("algorithm.term.nGramWeight"),
                new Double(_termAlgorithm.getNGramWeight())
            },
            {
                ApplicationUtilities.getResourceString("algorithm.term.nGram"), new Integer(_termAlgorithm.getNGram())
            }
        };
        JTable properties = new JTable(new PropertiesTableModel(columnNames, 10, data));
        TableColumn valueColumn = properties.getColumn(ApplicationUtilities
            .getResourceString("properties.value"));
        valueColumn.setCellRenderer(new PropertiesCellRenderer());
        return properties;
    }

    public void updateProperties(HashMap<?, ?> properties)
    {
        boolean symmetric = new Boolean(properties.get(
            ApplicationUtilities.getResourceString("algorithm.term.symmetric")).toString())
            .booleanValue();
        boolean useThesaurus = new Boolean(properties.get(
            ApplicationUtilities.getResourceString("algorithm.term.useThesaurus")).toString())
            .booleanValue();
        boolean useSoundex = new Boolean(properties.get(
            ApplicationUtilities.getResourceString("algorithm.term.useSoundex")).toString())
            .booleanValue();
        if (symmetric)
            mode += TermAlgorithmFlagsEnum.SYMMETRIC_FLAG.getValue();
        if (useThesaurus)
            mode += TermAlgorithmFlagsEnum.USE_THESAURUS_FLAG.getValue();
        if (useSoundex)
            mode += TermAlgorithmFlagsEnum.USE_SOUNDEX_FLAG.getValue();
        double wordLabelWeight = new Double(properties.get(
            ApplicationUtilities.getResourceString("algorithm.term.wordLabelWeight")).toString())
            .doubleValue();
        _termAlgorithm.setWordLabelWeight(wordLabelWeight);
        double stringLabelWeight = new Double(properties.get(
            ApplicationUtilities.getResourceString("algorithm.term.stringLabelWeight")).toString())
            .doubleValue();
        _termAlgorithm.setStringLabelWeight(stringLabelWeight);
        double wordNameWeight = new Double(properties.get(
            ApplicationUtilities.getResourceString("algorithm.term.wordNameWeight")).toString())
            .doubleValue();
        _termAlgorithm.setWordNameWeight(wordNameWeight);
        double stringNameWeight = new Double(properties.get(
            ApplicationUtilities.getResourceString("algorithm.term.stringNameWeight")).toString())
            .doubleValue();
        _termAlgorithm.setStringNameWeight(stringNameWeight);
        double maxCommonSubStringWeight = new Double(properties.get(
            ApplicationUtilities.getResourceString("algorithm.term.maxCommonSubStringWeight"))
            .toString()).doubleValue();
        _termAlgorithm.setMaxCommonSubStringWeight(maxCommonSubStringWeight);
        double nGramWeight = new Double(properties.get(
            ApplicationUtilities.getResourceString("algorithm.term.nGramWeight")).toString())
            .doubleValue();
        _termAlgorithm.setNGramWeight(nGramWeight);
        int nGram = new Integer(properties.get(
            ApplicationUtilities.getResourceString("algorithm.term.nGram")).toString()).intValue();
        _termAlgorithm.setNGram(nGram);
    }
    
    protected class PropertiesCellRenderer extends DefaultTableCellRenderer
    {
        private static final long serialVersionUID = -8136182017228024130L;

        public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column)
        {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
                row, column);
            if (row >= 0 && row <= 2)
            {
                JCheckBox check = new JCheckBox();
                check.setHorizontalAlignment(SwingConstants.CENTER);
                if (isSelected)
                {
                    check.setForeground(table.getSelectionForeground());
                    check.setBackground(table.getSelectionBackground());
                }
                else
                {
                    check.setForeground(table.getForeground());
                    check.setBackground(table.getBackground());
                }
                check.setSelected(((value instanceof Boolean ? (Boolean) value : new Boolean(
                    (String) value))).booleanValue());
                return check;
            }
            else
                return c;
        }
    }
}
