package ac.technion.iem.ontobuilder.gui.tools.algorithms.line1;

import java.util.HashMap;

import javax.swing.JTable;

import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.application.PropertiesTableModel;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.Algorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.term.GraphAlgorithm;

/**
 * <p>
 * Title: CombinedAlgorGraphAlgorithmGuiithmGui
 * </p>
  * <p>Description: Implements the methods of the GraphAlgorithm used by the GUI</p>
 */
public class GraphAlgorithmGui extends TermValueAlgorithmGui
{
    private GraphAlgorithm _graphAlgorithm;
    
    public GraphAlgorithmGui(Algorithm abstractAlgorithm)
    {
        super(abstractAlgorithm);
        _graphAlgorithm = (GraphAlgorithm)abstractAlgorithm;
    }
    
    public void updateProperties(HashMap<?, ?> properties)
    {
        super.updateProperties(properties);
        
        double graphWeight = new Double(properties.get(
            ApplicationUtilities.getResourceString("algorithm.combined.graphWeight")).toString())
            .doubleValue();
        _graphAlgorithm.setGraphWeight(graphWeight);
        double siblingsWeight = new Double(properties.get(
            ApplicationUtilities.getResourceString("algorithm.graph.siblingsWeight")).toString())
            .doubleValue();
        _graphAlgorithm.setSiblingsWeight(siblingsWeight);
        double parentsWeight = new Double(properties.get(
            ApplicationUtilities.getResourceString("algorithm.graph.parentsWeight")).toString())
            .doubleValue();
        _graphAlgorithm.setParentsWeight(parentsWeight);
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
                new Double(_graphAlgorithm.getTermWeight())
            },
            {
                ApplicationUtilities.getResourceString("algorithm.combined.valueWeight"),
                new Double(_graphAlgorithm.getValueWeight())
            },
            {
                ApplicationUtilities.getResourceString("algorithm.combined.graphWeight"),
                new Double(_graphAlgorithm.getGraphWeight())
            },
            {
                ApplicationUtilities.getResourceString("algorithm.graph.parentsWeight"),
                new Double(_graphAlgorithm.getParentsWeight())
            },
            {
                ApplicationUtilities.getResourceString("algorithm.graph.siblingsWeight"),
                new Double(_graphAlgorithm.getSiblingsWeight())
            }
        };
        JTable properties = new JTable(new PropertiesTableModel(columnNames, 5, data));
        return properties;
    }
}
