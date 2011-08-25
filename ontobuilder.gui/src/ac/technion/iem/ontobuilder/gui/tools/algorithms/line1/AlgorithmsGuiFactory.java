package ac.technion.iem.ontobuilder.gui.tools.algorithms.line1;

import ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.Algorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.SimilarityFloodingAlgorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.precedence.NewPrecedenceAlgorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.precedence.PrecedenceAlgorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.term.CombinedAlgorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.term.GraphAlgorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.term.TermAlgorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.term.TermValueAlgorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.term.ValueAlgorithm;

/**
 * <p>Title: AlgorithmsGuiFactory</p>
 * <p>Description: Creates an algorithm Gui according to the input algorithm type</p>
 */
public class AlgorithmsGuiFactory
{
    public static AbstractAlgorithmGui getAlgorithmGui(Algorithm algorithm)
    {
        if (algorithm instanceof CombinedAlgorithm)
        {
            return new CombinedAlgorithmGui(algorithm);
        }
        else if(algorithm instanceof GraphAlgorithm)
        {
            return new GraphAlgorithmGui(algorithm);
        }
        else if(algorithm instanceof NewPrecedenceAlgorithm)
        {
            return new NewPrecedenceAlgorithmGui(algorithm);
        }
        else if(algorithm instanceof PrecedenceAlgorithm)
        {
            return new PrecedenceAlgorithmGui(algorithm);
        }
        else if(algorithm instanceof SimilarityFloodingAlgorithm)
        {
            return new SimilarityFloodingAlgorithmGui(algorithm);
        }
        else if(algorithm instanceof TermAlgorithm)
        {
            return new TermAlgorithmGui(algorithm);
        }
        else if(algorithm instanceof TermValueAlgorithm)
        {
            return new TermValueAlgorithmGui(algorithm);
        }
        else if(algorithm instanceof ValueAlgorithm)
        {
            return new ValueAlgorithmGui(algorithm);
        }
        else return null;
    }
}
