package ac.technion.iem.ontobuilder.io.matchimport;

import com.hp.hpl.jena.rdf.model.*;

/** VCARD vocabulary class for namespace http://knowledgeweb.semanticweb.org/heterogeneity/alignment#
 */
public class ALIGN {

    protected static final String uri ="http://knowledgeweb.semanticweb.org/heterogeneity/alignment";

    /** returns the URI for this schema
     * @return the URI for this schema
     */
    public static String getURI() {
          return uri;
    }

    private static Model m = ModelFactory.createDefaultModel();
    
    public static final Resource ALIGNMENT = m.createResource(uri + "Alignment" );
    public static final Property O1 = m.createProperty(uri + "onto1" );
    public static final Property O2 = m.createProperty(uri + "onto2" );
    public static final Property MAP = m.createProperty(uri + "map" );
    public static final Property CELL = m.createProperty(uri + "cell" );
    public static final Property E1 = m.createProperty(uri + "entity1" );
    public static final Property E2 = m.createProperty(uri + "entity2" );
    public static final Property MEASURE = m.createProperty(uri + "measure" );
    }
