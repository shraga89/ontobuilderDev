package ac.technion.iem.ontobuilder.matching.algorithms.line2.meta;

import java.util.Vector;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.common.Algorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.tkm.TKM;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;


/**
 * <p>Title: MetaAlgorithm</p>
 * Extends {@link MetaAlgorithmNames}
 */
public interface MetaAlgorithm
{

    public void runAlgorithm() throws MetaAlgorithmRunningException;

    public void nullify();

    public void init(Ontology o1, Ontology o2, int numOfMatchingAlgorithms,
        Algorithm[] algorithms, TKM tmkClass) throws MetaAlgorithmInitiationException;

    public String getAlgorithmName();

    public void setAlgorithmName(String algorithmName);

    public Vector<MatchInformation> getAllKBestMappings();

    public MatchInformation getKthBestMapping(int k);

    public int getNumOfSchemaMatchers();

    public void reset();

}