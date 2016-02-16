package org.aksw.gerbil.dataset.check.impl;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class InMemoryCachingEntityCheckerManager extends EntityCheckerManagerImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryCachingEntityCheckerManager.class);

    private static final int DEFAULT_CACHE_SIZE = 10000;
    private static final int DEFAULT_CACHE_LIFETIME = 2 * 24 * 60 * 60 * 1000;

    private LoadingCache<String, Boolean> cache;

    public InMemoryCachingEntityCheckerManager() {
        this(DEFAULT_CACHE_SIZE, DEFAULT_CACHE_LIFETIME);
    }

    public InMemoryCachingEntityCheckerManager(int cacheSize, long cacheLifeTime) {
        cache = CacheBuilder.newBuilder().maximumSize(cacheSize).expireAfterWrite(cacheLifeTime, TimeUnit.MILLISECONDS)
                .build(new CacheLoader<String, Boolean>() {
                    @Override
                    public Boolean load(String key) throws Exception {
                        return performCheck(key);
                    }
                });
    }

    @Override
    public boolean checkUri(String uri) {
        try {
            return cache.get(uri);
        } catch (ExecutionException e) {
            LOGGER.error("Exception while trying to check URI. Returning true.");
            return true;
        }
    }

    protected boolean performCheck(String uri) {
        return super.checkUri(uri);
    }

}
