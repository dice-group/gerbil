package org.aksw.gerbil.semantic.sameas;

import java.util.HashSet;
import java.util.Set;

/**
 * This {@link SameAsRetriever} is used to fix common problems with URIs, e.g.,
 * if a URI has the domain <code>en.dbpedia.org</code> (which is not existing) a
 * URI with the correct domain (<code>dbpedia.org</code>) is added.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public class ErrorFixingSameAsRetriever implements SameAsRetriever {

    private static final String WRONG_EN_DBPEDIA_DOMAIN = "en.dbpedia.org";
    private static final String DBPEDIA_DOMAIN = "dbpedia.org";

    @Override
    public Set<String> retrieveSameURIs(String uri) {
        Set<String> uris = null;
        if (uri.contains(WRONG_EN_DBPEDIA_DOMAIN)) {
            uris = new HashSet<String>();
            uris.add(uri);
            uris.add(uri.replace(WRONG_EN_DBPEDIA_DOMAIN, DBPEDIA_DOMAIN));
        }
        return uris;
    }

    @Override
    public void addSameURIs(Set<String> uris) {
        Set<String> temp = new HashSet<String>();
        Set<String> result;
        for (String uri : uris) {
            result = retrieveSameURIs(uri);
            if (result != null) {
                temp.addAll(retrieveSameURIs(uri));
            }
        }
        uris.addAll(temp);
    }

}
