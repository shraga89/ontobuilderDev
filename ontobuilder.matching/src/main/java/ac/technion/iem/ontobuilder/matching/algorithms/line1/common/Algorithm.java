package ac.technion.iem.ontobuilder.matching.algorithms.line1.common;

import org.jdom2.Element;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.thesaurus.Thesaurus;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.term.TermPreprocessor;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;

/**
 * <p>Title: interface Algorithm</p>
 */
public interface Algorithm
{
    String MATCH_THRESHOLD_PROPERTY = "matchThreshold";

    MatchInformation match(Ontology targetOntology, Ontology candidateOntology);

    void setMode(int mode);

    int getMode();

    String getPluginName();

    void setPluginName(String pluginName);

    String getName();

    String getDescription();

    void configure(Element element);

    void setThreshold(double threshold);

    double getThreshold();

    boolean usesThesaurus();

    void setThesaurus(Thesaurus thesaurus);

    Thesaurus getThesaurus();

    void setTermPreprocessor(TermPreprocessor termPreprocessor);

    TermPreprocessor getTermPreprocessor();

    boolean implementsEffectiveness();

    double getEffectiveness();

    Algorithm makeCopy();
}
