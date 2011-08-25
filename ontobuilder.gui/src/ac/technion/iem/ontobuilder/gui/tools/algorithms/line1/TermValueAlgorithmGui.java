package ac.technion.iem.ontobuilder.gui.tools.algorithms.line1;

import java.util.HashMap;

import javax.swing.JTable;

import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.application.PropertiesTableModel;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.Algorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.term.TermValueAlgorithm;

/**
 * <p>
 * Title: TermValueAlgorithmGui
 * </p>
  * <p>Description: Implements the methods of the TermValueAlgorithm used by the GUI</p>
 */
public class TermValueAlgorithmGui extends AbstractAlgorithmGui
{
    private TermValueAlgorithm _termValueAlgorithm;
    
    public TermValueAlgorithmGui(Algorithm abstractAlgorithm)
    {
        _termValueAlgorithm = (TermValueAlgorithm)abstractAlgorithm;
    }
    
    public void updateProperties(HashMap<?, ?> properties)
    {
        double termWeight = new Double(properties.get(
            ApplicationUtilities.getResourceString("algorithm.combined.termWeight")).toString())
            .doubleValue();
        _termValueAlgorithm.setTermWeight(termWeight);
        
        double valueWeight = new Double(properties.get(
            ApplicationUtilities.getResourceString("algorithm.combined.valueWeight")).toString())
            .doubleValue();
        _termValueAlgorithm.setValueWeight(valueWeight);
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
                ApplicationUtilities.getResourceString("algorithm.combined.termWeight"),
                new Double(_termValueAlgorithm.getTermWeight())
            },
            {
                ApplicationUtilities.getResourceString("algorithm.combined.valueWeight"),
                new Double(_termValueAlgorithm.getValueWeight())
            }
        };
        JTable properties = new JTable(new PropertiesTableModel(columnNames, 2, data));
        return properties;
    }
}
