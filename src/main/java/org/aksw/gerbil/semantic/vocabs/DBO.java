package org.aksw.gerbil.semantic.vocabs;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

/**
 * The DBpedia ontology vocabulary (http://dbpedia.org/ontology/). <b>Note</b>
 * that this class contains only the resources, that are needed by GERBIL.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public class DBO {

    protected static final String URI = "http://dbpedia.org/ontology/";

    /**
     * returns the URI for this schema
     * 
     * @return the URI for this schema
     */
    public static String getURI() {
        return URI;
    }

    protected static final Resource resource(String local) {
        return ResourceFactory.createResource(URI + local);
    }

    protected static final Property property(String local) {
        return ResourceFactory.createProperty(URI, local);
    }

    public static final Property wikiPageID = property("wikiPageID");
    public static final Property wikiPageRedirects = property("wikiPageRedirects");

}
