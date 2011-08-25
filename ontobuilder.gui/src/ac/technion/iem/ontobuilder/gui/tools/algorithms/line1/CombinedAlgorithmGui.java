package ac.technion.iem.ontobuilder.gui.tools.algorithms.line1;

import java.util.HashMap;

import javax.swing.JTable;

import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.application.PropertiesTableModel;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.Algorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.term.CombinedAlgorithm;

/**
 * <p>
 * Title: CombinedAlgorithmGui
 * </p>
  * <p>Description: Implements the methods of the CombinedAlgorithm used by the GUI</p>
 */
public class CombinedAlgorithmGui extends TermValueAlgorithmGui
{
    private CombinedAlgorithm _combinedAlgorithm;
    
    public CombinedAlgorithmGui(Algorithm abstractAlgorithm)
    {
        super(abstractAlgorithm);
        _combinedAlgorithm = (CombinedAlgorithm)abstractAlgorithm;
    }
    
    public void updateProperties(HashMap<?, ?> properties)
    {
        super.updateProperties(properties);
        
        double graphWeight = new Double(properties.get(
            ApplicationUtilities.getResourceString("algorithm.combined.graphWeight")).toString())
            .doubleValue();
        _combinedAlgorithm.setGraphWeight(graphWeight);
        
        double precedenceWeight = new Double(properties.get(
            ApplicationUtilities.getResourceString("algorithm.combined.precedenceWeight"))
            .toString()).doubleValue();
        _combinedAlgorithm.setPrecedenceWeight(precedenceWeight);
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
                new Double(_combinedAlgorithm.getTermWeight())
            },
            {
                ApplicationUtilities.getResourceString("algorithm.combined.valueWeight"),
                new Double(_combinedAlgorithm.getValueWeight())
            },
            {
                ApplicationUtilities.getResourceString("algorithm.combined.graphWeight"),
                new Double(_combinedAlgorithm.getGraphWeight())
            },
            {
                ApplicationUtilities.getResourceString("algorithm.combined.precedenceWeight"),
                new Double(_combinedAlgorithm.getPrecedenceWeight())
            }
        };
        JTable properties = new JTable(new PropertiesTableModel(columnNames, 4, data));
        return properties;
    }
}
