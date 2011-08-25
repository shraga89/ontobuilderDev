package ac.technion.iem.ontobuilder.gui.tools.algorithms.line1;

import java.util.HashMap;

import javax.swing.JTable;

import ac.technion.iem.ontobuilder.gui.application.ObjectWithProperties;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.Algorithm;

/**
 * <p>
 * Title: AbstractAlgorithmGui
 * </p>
  * <p>Description: Implements the methods of the AbstractAlgorithm used by the GUI</p>
 */
public abstract class AbstractAlgorithmGui implements ObjectWithProperties
{
    public AbstractAlgorithmGui(Algorithm abstractAlgorithm)
    {
    }
    
    public AbstractAlgorithmGui()
    {
    }
    
    abstract public void updateProperties(HashMap<?, ?> properties);
    
    abstract public JTable getProperties();
}
