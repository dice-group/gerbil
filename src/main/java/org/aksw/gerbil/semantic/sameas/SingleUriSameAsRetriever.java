package org.aksw.gerbil.semantic.sameas;

import java.util.Set;

/**
 * This interface defines methods implemented by classes that can retrieve URIs
 * pointing at the same entity as the given URI.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public interface SingleUriSameAsRetriever {

    /**
     * Returns a Set containing the URIs having a sameAs link to this URI or
     * null if no such URIs could be found.
     * 
     * @param uri
     *            the URI for which sameAs links should be discovered
     * @return a Set of URIs or null if no URIs could be found
     */
    public Set<String> retrieveSameURIs(String uri);

    /**
     * Returns a Set containing the URIs having a sameAs link to this URI or
     * null if no such URIs could be found.
     * 
     * @param uri
     *            the URI for which sameAs links should be discovered
     * @param domain
     *            the domain of the URI
     * @return a Set of URIs or null if no URIs could be found
     */
    public Set<String> retrieveSameURIs(String domain, String uri);
}
