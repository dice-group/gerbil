package org.aksw.gerbil.dataset.check.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.aksw.gerbil.dataset.check.EntityChecker;
import org.aksw.gerbil.dataset.check.EntityCheckerManager;
import org.aksw.gerbil.transfer.nif.Meaning;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrotsearch.hppc.ObjectObjectOpenHashMap;

/**
 * <p>
 * Standard implementation of the {@link EntityCheckerManager} interface.
 * Internally it uses a cache for storing the results of the
 * {@link EntityChecker}.
 * </p>
 * TODO The current implementation is not thread safe if
 * {@link #registerEntityChecker(String, EntityChecker)} is called while another
 * thread already is inside the {@link #checkMeanings(List)} method.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public class EntityCheckerManagerImpl implements EntityCheckerManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntityCheckerManagerImpl.class);

    private static final String SYNTHETIC_URI_NAME_SPACE = "http://aksw.org/unknown_entity/";

    private ObjectObjectOpenHashMap<String, EntityChecker> registeredCheckers = new ObjectObjectOpenHashMap<String, EntityChecker>();

    @Override
    public void registerEntityChecker(String namespace, EntityChecker checker) {
        registeredCheckers.put(namespace, checker);
    }

    @Override
    public void checkMeanings(List<? extends Meaning> meanings) {
        for (Meaning meaning : meanings) {
            checkMeaning(meaning);
        }
    }

    public void checkMeaning(Meaning meaning) {
        Set<String> uris = meaning.getUris();
        List<String> wrongUris = null;
        List<String> newUris = null;
        for (String uri : uris) {
            // If the URI does not exist
            if ((uri != null) && (!checkUri(uri))) {
                if (wrongUris == null) {
                    wrongUris = new ArrayList<String>(3);
                    newUris = new ArrayList<String>(3);
                }
                wrongUris.add(uri);
                newUris.add(generateNewUri(uri));
            }
        }
        if (wrongUris != null) {
            LOGGER.info("Couldn't find an entity with the URIs={}.", wrongUris);
            uris.removeAll(wrongUris);
            uris.addAll(newUris);
        }
    }

    protected String generateNewUri(String uri) {
        StringBuilder newUri = new StringBuilder();
        newUri.append(SYNTHETIC_URI_NAME_SPACE);
        char chars[] = uri.toCharArray();
        for (int i = 0; i < chars.length; ++i) {
            switch (chars[i]) {
            case '.': // falls through
            case ':':
            case '/': {
                newUri.append('_');
                break;
            }
            default: {
                newUri.append(chars[i]);
            }
            }
        }
        return newUri.toString();
    }

    public boolean checkUri(String uri) {
        String namespace;
        int matchingId = -1;
        for (int i = 0; (i < registeredCheckers.allocated.length) && (matchingId < 0); ++i) {
            if (registeredCheckers.allocated[i]) {
                namespace = (String) ((Object[]) registeredCheckers.keys)[i];
                if (uri.startsWith(namespace)) {
                    matchingId = i;
                }
            }
        }
        // If there is a checker available for this URI
        if (matchingId >= 0) {
            EntityChecker checker = (EntityChecker) ((Object[]) registeredCheckers.values)[matchingId];
            // Return whether this URI does exist
            return checker.entityExists(uri);
        } else {
            return true;
        }
    }

}
