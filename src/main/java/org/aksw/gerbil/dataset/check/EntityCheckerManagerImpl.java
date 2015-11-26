package org.aksw.gerbil.dataset.check;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.aksw.gerbil.transfer.nif.Meaning;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrotsearch.hppc.ObjectObjectOpenHashMap;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * <p>
 * Standard implementation of the {@link EntityCheckerManager} interface.
 * Internally it uses a cache for storing the results of the
 * {@link EntityChecker}.
 * </p>
 * TODO The current implementation is no thread safe if
 * {@link #registerEntityChecker(String, EntityChecker)} is called while another
 * thread already is inside the {@link #checkMeanings(List)} method.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public class EntityCheckerManagerImpl extends CacheLoader<String, Boolean> implements EntityCheckerManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntityCheckerManagerImpl.class);

    private static final int DEFAULT_CACHE_SIZE = 10000;
    private static final int DEFAULT_CACHE_LIFETIME = 2 * 24 * 60 * 60 * 1000;

    private static final String SYNTETIC_URI_NAME_SPACE = "http://aksw.org/unknown_entity/";

    private ObjectObjectOpenHashMap<String, EntityChecker> registeredCheckers = new ObjectObjectOpenHashMap<String, EntityChecker>();
    private LoadingCache<String, Boolean> cache;

    public EntityCheckerManagerImpl() {
        this(DEFAULT_CACHE_SIZE, DEFAULT_CACHE_LIFETIME);
    }

    public EntityCheckerManagerImpl(int cacheSize, long cacheLifeTime) {
        cache = CacheBuilder.newBuilder().maximumSize(cacheSize).expireAfterWrite(cacheLifeTime, TimeUnit.MILLISECONDS)
                .build(this);
    }

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
            try {
                // If the URI does not exist
                if ((uri != null) && (!cache.get(uri))) {
                    if (wrongUris == null) {
                        wrongUris = new ArrayList<String>(3);
                        newUris = new ArrayList<String>(3);
                    }
                    wrongUris.add(uri);
                    newUris.add(generateNewUri(uri));
                }
            } catch (ExecutionException e) {
                LOGGER.info("Couldn't check the existing .", e);
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
        newUri.append(SYNTETIC_URI_NAME_SPACE);
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

    @Override
    public Boolean load(String uri) throws Exception {
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
