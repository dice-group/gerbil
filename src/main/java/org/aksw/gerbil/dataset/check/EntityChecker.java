package org.aksw.gerbil.dataset.check;

/**
 * Interface for an {@link EntityChecker} that checks whether an entity with the
 * given URI exists.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public interface EntityChecker {

    /**
     * Returns true if there is an entity with the given URI. Else, false is
     * returned.
     * 
     * @param uri
     *            the URI of the entity that should be checked
     * @return true if there is an entity with the given URI. Else, false is
     *         returned.
     */
    public boolean entityExists(String uri);
}
