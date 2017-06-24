/**
 * This file is part of General Entity Annotator Benchmark.
 *
 * General Entity Annotator Benchmark is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * General Entity Annotator Benchmark is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with General Entity Annotator Benchmark.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.aksw.gerbil.semantic.sameas.impl.cache;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.aksw.gerbil.semantic.sameas.SameAsRetriever;
import org.aksw.gerbil.semantic.sameas.SameAsRetrieverDecorator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class InMemoryCachingSameAsRetriever extends CacheLoader<String, Set<String>>
        implements SameAsRetrieverDecorator {

    private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryCachingSameAsRetriever.class);

    private static final int DEFAULT_CACHE_SIZE = 10000;
    private static final Set<String> NULL_SENTINEL = new HashSet<String>(0);

    protected SameAsRetriever decoratedRetriever;
    private LoadingCache<String, Set<String>> cache;

    public InMemoryCachingSameAsRetriever(SameAsRetriever decoratedRetriever) {
        this(decoratedRetriever, DEFAULT_CACHE_SIZE);
    }

    public InMemoryCachingSameAsRetriever(SameAsRetriever decoratedRetriever, int cacheSize) {
        this.decoratedRetriever = decoratedRetriever;
        cache = CacheBuilder.newBuilder().maximumSize(cacheSize).build(this);
    }

    @Override
    public Set<String> retrieveSameURIs(String uri) {
        try {
            Set<String> result = cache.get(uri);
            if (result == NULL_SENTINEL) {
                return null;
            } else {
                return result;
            }
        } catch (ExecutionException e) {
            LOGGER.error("Couldn't retrieve sameAs links. Returning null.", e);
            return null;
        }
    }

    @Override
    public Set<String> retrieveSameURIs(String domain, String uri) {
        return retrieveSameURIs(uri);
    }

    @Override
    public SameAsRetriever getDecorated() {
        return decoratedRetriever;
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

    @Override
    public Set<String> load(String key) throws Exception {
        Set<String> result = decoratedRetriever.retrieveSameURIs(key);
        if (result == null) {
            return NULL_SENTINEL;
        } else {
            return result;
        }
    }

}
