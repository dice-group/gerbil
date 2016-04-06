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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Semaphore;

import org.aksw.gerbil.semantic.sameas.SameAsRetriever;
import org.aksw.gerbil.semantic.sameas.impl.cache.FileBasedCachingSameAsRetriever;
import org.junit.Assert;
import org.junit.Test;

import com.carrotsearch.hppc.BitSet;
import com.carrotsearch.hppc.ObjectIntOpenHashMap;

public class FileBasedCachingSameAsRetrieverTest extends FileBasedCachingSameAsRetriever {

    private static final int MODULO_NR = 1024;
    private static final int MAX_MUL = 10;
    private static final int MAX_NR = MAX_MUL * MODULO_NR;
    private static final int MAX_NR_SAME_AS_FOUND = 3;
    private static final int NUMBER_OF_WORKERS = 4;
    private static final int NUMBER_OF_REQUESTS_PER_WORKER = 5000;

    private Semaphore cacheUsageFinished = new Semaphore(0);

    public FileBasedCachingSameAsRetrieverTest() throws IOException {
        super(new RandomNumberReturningSameAsRetriever(), new ObjectIntOpenHashMap<String>(),
                new ArrayList<Set<String>>(), false, File.createTempFile("cache_test_", ".cache"),
                File.createTempFile("cache_test_", ".cache_temp"));
    }

    @Test
    public void test() throws InterruptedException {
        ///// test the cache itself

        for (int i = 0; i < NUMBER_OF_WORKERS; ++i) {
            (new Thread(new CacheUser(this))).start();
        }

        cacheUsageFinished.acquire(NUMBER_OF_WORKERS);

        storeCache();

        int setId;
        Set<String> uris;
        BitSet checkedSets = new BitSet(sets.size());
        for (int i = 0; i < uriSetIdMapping.allocated.length; ++i) {
            if (uriSetIdMapping.allocated[i]) {
                setId = uriSetIdMapping.values[i];
                if (setId != ENTITY_NOT_FOUND) {
                    // make sure that the set can exist
                    Assert.assertTrue(setId < sets.size());
                    uris = sets.get(setId);
                    // make sure that the URI pointing to this set is part of it
                    Assert.assertTrue(uris.contains(((Object[]) uriSetIdMapping.keys)[i]));
                    // If this set does not have been checked before, check all
                    // the URIs inside
                    if (!checkedSets.get(setId)) {
                        for (String s : uris) {
                            Assert.assertTrue(uriSetIdMapping.containsKey(s));
                            Assert.assertEquals(setId, uriSetIdMapping.get(s));
                        }
                        checkedSets.set(setId);
                    }
                }
            }
        }

        ///// test the read/write operation
        // read the cache with another cache object
        FileBasedCachingSameAsRetriever otherCache = FileBasedCachingSameAsRetriever.create(decoratedRetriever, false,
                cacheFile);

        Assert.assertEquals(sets.size(), otherCache.sets.size());
        Assert.assertEquals(uriSetIdMapping.assigned, otherCache.uriSetIdMapping.assigned);
        checkedSets = new BitSet(sets.size());
        int otherSetId;
        String uri;
        Set<String> otherUris;
        for (int i = 0; i < uriSetIdMapping.allocated.length; ++i) {
            if (uriSetIdMapping.allocated[i]) {
                setId = uriSetIdMapping.values[i];
                uri = (String) ((Object[]) uriSetIdMapping.keys)[i];
                Assert.assertTrue(otherCache.uriSetIdMapping.containsKey(uri));
                otherSetId = otherCache.uriSetIdMapping.get(uri);
                if (setId != ENTITY_NOT_FOUND) {
                    Assert.assertNotEquals(ENTITY_NOT_FOUND, otherSetId);
                    uris = sets.get(setId);
                    otherUris = otherCache.sets.get(otherSetId);
                    Assert.assertEquals(uris.size(), otherUris.size());

                    for (String s : uris) {
                        Assert.assertTrue(otherUris.contains(s));
                        Assert.assertTrue(otherCache.uriSetIdMapping.containsKey(s));
                        Assert.assertEquals(otherSetId, otherCache.uriSetIdMapping.get(s));
                    }
                } else {
                    Assert.assertEquals(ENTITY_NOT_FOUND, otherSetId);
                }
            }
        }
    }

    public static class CacheUser implements Runnable {

        private Random random = new Random();
        private FileBasedCachingSameAsRetrieverTest cache;

        public CacheUser(FileBasedCachingSameAsRetrieverTest cache) {
            this.cache = cache;
        }

        @Override
        public void run() {
            Set<String> uris;
            int uriNr, modValue;
            for (int i = 0; i < NUMBER_OF_REQUESTS_PER_WORKER; ++i) {
                uriNr = random.nextInt(MAX_NR);
                modValue = uriNr % MODULO_NR;
                uris = cache.retrieveSameURIs(Integer.toString(uriNr));
                if (uris != null) {
                    for (String s : uris) {
                        Assert.assertEquals(modValue, Integer.parseInt(s) % MODULO_NR);
                    }
                }
            }
            cache.cacheUsageFinished.release();
        }

    }

    public static class RandomNumberReturningSameAsRetriever implements SameAsRetriever {

        private Random random = new Random();

        @Override
        public Set<String> retrieveSameURIs(String uri) {
            int uriValue = Integer.parseInt(uri);
            int sameAsFound = random.nextInt(MAX_NR_SAME_AS_FOUND);
            if (sameAsFound == 0) {
                return null;
            }
            Set<String> result = new HashSet<String>();
            result.add(uri);
            int diff, newNr;
            for (int i = 0; i < sameAsFound; ++i) {
                diff = (random.nextInt(MAX_MUL - 1) + 1) * MODULO_NR;
                newNr = uriValue + diff;
                if (uriValue > MAX_NR) {
                    newNr = uriValue - diff;
                    if (newNr >= 0) {
                        result.add(Integer.toString(newNr));
                    }
                } else {
                    result.add(Integer.toString(newNr));
                }
            }
            return result;
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
        public Set<String> retrieveSameURIs(String domain, String uri) {
            return retrieveSameURIs(uri);
        }
    }

}
