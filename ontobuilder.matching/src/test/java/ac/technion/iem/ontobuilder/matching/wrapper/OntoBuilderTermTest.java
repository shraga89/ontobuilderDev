package ac.technion.iem.ontobuilder.matching.wrapper;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.OntologyClass;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import junit.framework.TestCase;

public class OntoBuilderWrapperTest extends TestCase {

    public void testMatchOntologies() throws OntoBuilderWrapperException {
        OntoBuilderWrapper obw = new OntoBuilderWrapper(true);
        Ontology o1 = new Ontology("Test1", "Title Test 1");
        Ontology o2 = new Ontology("Test 2", "Title Test 2");
        o1.setLight(true);
        o2.setLight(true);
        o1.addTerm(new Term("airplane", "Skipper"));
        o2.addTerm(new Term( "aircraft", "B-52"));
        MatchInformation res = obw.matchOntologies(o1, o2, "Term Match");
        assertNotNull(res);
    }
}