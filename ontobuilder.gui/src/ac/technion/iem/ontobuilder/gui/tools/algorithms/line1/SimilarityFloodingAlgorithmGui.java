package ac.technion.iem.ontobuilder.gui.tools.algorithms.line1;

import java.util.HashMap;

import javax.swing.JTable;

import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.application.PropertiesTableModel;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.Algorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.SimilarityFloodingAlgorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.SimilarityFloodingAlgorithmFixpointFormulasTypes;

/**
 * <p>
 * Title: SimilarityFloodingAlgorithmGui
 * </p>
  * <p>Description: Implements the methods of the SimilarityFloodingAlgorithm used by the GUI</p>
 */
public class SimilarityFloodingAlgorithmGui extends AbstractAlgorithmGui
{
    @SuppressWarnings("unused")
    private SimilarityFloodingAlgorithm _similarityFloodingAlgorithm;
    
    public SimilarityFloodingAlgorithmGui(Algorithm abstractAlgorithm)
    {
        _similarityFloodingAlgorithm = (SimilarityFloodingAlgorithm)abstractAlgorithm;
    }
    
    // value of minimal residual vector length that will necessitate another algorithm iteration
    protected double epsilon = 0.05;

    // selected type of fixpoint formula
    protected SimilarityFloodingAlgorithmFixpointFormulasTypes fixpointType = SimilarityFloodingAlgorithmFixpointFormulasTypes.FIX_BASIC; 
    /**
     * Constructs a default SimilarityFloodingAlgorithmGui
     */
    public SimilarityFloodingAlgorithmGui()
    {
    }

    // creates the JTable for user to view and change values.
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
                "Epsilon Value", new Double(epsilon)
            },
            {
                "FixPoint Type", new Double(fixpointType.getValue())
            }
        };
        JTable properties = new JTable(new PropertiesTableModel(columnNames, 2, data));
        properties.setEditingRow(0);
        properties.setEditingRow(1);
        return properties;
    }

    public void updateProperties(HashMap<?, ?> properties)
    {
    }
}
