package org.aksw.gerbil.dataset.check;

import java.util.List;

import org.aksw.gerbil.transfer.nif.Meaning;

/**
 * This is an interface for a class that manages the entity checking using an
 * internal mapping of URI name spaces to known entity checkers.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public interface EntityCheckerManager {

    /**
     * Registers the given {@link EntityChecker} for being used for URIs of the
     * given name space. Note that only one single EntityChecker per name space
     * is allowed. If a second EntityChecker is registered, the first one will
     * be overwritten.
     * 
     * @param namespace
     *            URI name space for which the given checker should be used.
     * @param checker
     *            the {@link EntityChecker} that should be registered for the
     *            given URI name space.
     */
    public void registerEntityChecker(String namespace, EntityChecker checker);

    /**
     * Checks the given list of {@link Meaning}s for their existence based on
     * the available {@link EntityChecker}. Only URIs are checked which have a
     * name space for which an {@link EntityChecker} is available. If a URI has
     * been identified as not existing, it is replaced by a generated URI.
     * 
     * @param meanings
     *            a List of {@link Meaning}s that should be checked for their
     *            existence.
     */
    public void checkMeanings(List<? extends Meaning> meanings);
}
