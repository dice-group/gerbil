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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Semaphore;

import org.aksw.gerbil.semantic.sameas.SameAsRetriever;
import org.aksw.gerbil.semantic.sameas.impl.AbstractSameAsRetrieverDecorator;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrotsearch.hppc.IntArrayList;
import com.carrotsearch.hppc.IntOpenHashSet;
import com.carrotsearch.hppc.ObjectIntOpenHashMap;

public class FileBasedCachingSameAsRetriever extends AbstractSameAsRetrieverDecorator {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileBasedCachingSameAsRetriever.class);

    private static final int MAX_CONCURRENT_READERS = 1000;

    protected static final int ENTITY_NOT_FOUND = -1;

    @SuppressWarnings("unchecked")
    public static FileBasedCachingSameAsRetriever create(SameAsRetriever decoratedRetriever,
            boolean requestEntitiesNotFound, File cacheFile) {
        File tempCacheFile = new File(cacheFile.getAbsolutePath() + "_temp");

        Object objects[] = null;
        // try to read the cache file
        objects = readCacheFile(cacheFile);
        // if this doesn't work, try to read the temp file
        if (objects == null) {
            LOGGER.warn("Couldn't read the cache file. Trying the temporary file...");
            objects = readCacheFile(tempCacheFile);
            // if this worked, rename the temp file to the real file
            if (objects != null) {
                try {
                    if (!tempCacheFile.renameTo(cacheFile)) {
                        LOGGER.warn("Reading from the temporary cache file worked, but I couldn't rename it.");
                    }
                } catch (Exception e) {
                    LOGGER.warn("Reading from the temporary cache file worked, but I couldn't rename it.", e);
                }
            }
        }
        ObjectIntOpenHashMap<String> uriSetIdMapping;
        List<Set<String>> sets;
        // if the reading didn't worked, create new cache objects
        if (objects == null) {
            LOGGER.warn("Couldn't read cache from files. Creating new empty cache.");
            uriSetIdMapping = new ObjectIntOpenHashMap<String>();
            sets = new ArrayList<Set<String>>();
        } else {
            uriSetIdMapping = (ObjectIntOpenHashMap<String>) objects[0];
            sets = (List<Set<String>>) objects[1];
        }
        return new FileBasedCachingSameAsRetriever(decoratedRetriever, uriSetIdMapping, sets, requestEntitiesNotFound,
                cacheFile, tempCacheFile);
    }

    protected ObjectIntOpenHashMap<String> uriSetIdMapping;
    protected List<Set<String>> sets;
    private int cacheChanges = 0;
    private int forceStorageAfterChanges = 1000;
    private Semaphore cacheReadMutex = new Semaphore(MAX_CONCURRENT_READERS);
    private Semaphore cacheWriteMutex = new Semaphore(1);
    private boolean requestEntitiesNotFound;
    protected File cacheFile;
    protected File tempCacheFile;

    protected FileBasedCachingSameAsRetriever(SameAsRetriever decoratedRetriever,
            ObjectIntOpenHashMap<String> uriSetIdMapping, List<Set<String>> sets, boolean requestEntitiesNotFound,
            File cacheFile, File tempCacheFile) {
        super(decoratedRetriever);
        this.uriSetIdMapping = uriSetIdMapping;
        this.sets = sets;
        this.requestEntitiesNotFound = requestEntitiesNotFound;
        this.cacheFile = cacheFile;
        this.tempCacheFile = tempCacheFile;
    }

    @Override
    public Set<String> retrieveSameURIs(String uri) {
        // if the cache contains the uri, return the set or a set
        // containing only the uri (use the read mutex!!!)
        Set<String> result = null;
        try {
            cacheReadMutex.acquire();
        } catch (InterruptedException e) {
            LOGGER.error("Exception while waiting for read mutex. Returning null.", e);
            return null;
        }
        boolean uriIsCached = uriSetIdMapping.containsKey(uri);
        if (uriIsCached) {
            int setId = uriSetIdMapping.get(uri);
            if (setId != ENTITY_NOT_FOUND) {
                result = sets.get(setId);
            }
        }
        // If the URI is not in the cache, or it has been cached but the result
        // is null and the request should be retried
        if (!uriIsCached || (uriIsCached && (result == null) && requestEntitiesNotFound)) {
            cacheReadMutex.release();
            result = decoratedRetriever.retrieveSameURIs(uri);
            try {
                cacheWriteMutex.acquire();
                // now we need all others
                cacheReadMutex.acquire(MAX_CONCURRENT_READERS);
            } catch (InterruptedException e) {
                LOGGER.error("Exception while waiting for read mutex. Returning null.", e);
                return null;
            }
            // Check again that nobody already added the uri
            if (uriSetIdMapping.containsKey(uri)) {
                // use the cached result
                int setId = uriSetIdMapping.get(uri);
                if (setId != ENTITY_NOT_FOUND) {
                    result = sets.get(setId);
                } else {
                    result = null;
                }
            } else {
                if (result != null) {
                    mergeSetIntoCache(result);
                } else {
                    uriSetIdMapping.put(uri, ENTITY_NOT_FOUND);
                }
                ++cacheChanges;
                if ((forceStorageAfterChanges > 0) && (cacheChanges >= forceStorageAfterChanges)) {
                    LOGGER.info("Storing the cache has been forced...");
                    try {
                        performCacheStorage();
                    } catch (IOException e) {
                        LOGGER.error("Exception while writing cache to file. Aborting.", e);
                    }
                }
            }
            // The last one will be released at the end
            cacheReadMutex.release(MAX_CONCURRENT_READERS - 1);
            cacheWriteMutex.release();
        }
        cacheReadMutex.release();
        return result;
    }

    protected void mergeSetIntoCache(Set<String> result) {
        // In most cases we shouldn't need this objects
        IntOpenHashSet alreadyExistingSets = null;
        int setId;
        for (String uri : result) {
            if (uriSetIdMapping.containsKey(uri)) {
                setId = uriSetIdMapping.get(uri);
                if (setId != ENTITY_NOT_FOUND) {
                    if (alreadyExistingSets == null) {
                        alreadyExistingSets = new IntOpenHashSet();
                    }
                    alreadyExistingSets.add(setId);
                }
            }
        }
        // if a joining is needed
        if (alreadyExistingSets != null) {
            for (int i = 0; i < alreadyExistingSets.allocated.length; i++) {
                if (alreadyExistingSets.allocated[i] && (alreadyExistingSets.keys[i] != ENTITY_NOT_FOUND)) {
                    result.addAll(sets.get(alreadyExistingSets.keys[i]));
                    sets.set(alreadyExistingSets.keys[i], null);
                }
            }
        }
        setId = sets.size();
        sets.add(result);
        for (String uri : result) {
            uriSetIdMapping.put(uri, setId);
        }
    }

    public void storeCache() {
        try {
            cacheWriteMutex.acquire();
        } catch (InterruptedException e) {
            LOGGER.error("Exception while waiting for write mutex for storing the cache. Aborting.", e);
            return;
        }
        try {
            performCacheStorage();
        } catch (IOException e) {
            LOGGER.error("Exception while writing cache to file. Aborting.", e);
        }
        cacheWriteMutex.release();
    }

    private void performCacheStorage() throws IOException {
        checkSetMapping();
        FileOutputStream fout = null;
        ObjectOutputStream oout = null;
        try {
            fout = new FileOutputStream(tempCacheFile);
            oout = new ObjectOutputStream(fout);
            // first, serialize the number of URIs
            oout.writeInt(uriSetIdMapping.assigned);
            // go over the mapping and serialize all existing pairs
            for (int i = 0; i < uriSetIdMapping.allocated.length; ++i) {
                if (uriSetIdMapping.allocated[i]) {
                    oout.writeObject(((Object[]) uriSetIdMapping.keys)[i]);
                    oout.writeInt(uriSetIdMapping.values[i]);
                }
            }
            // write the number of sets
            oout.writeInt(sets.size());
            // write the single sets
            for (Set<String> set : sets) {
                oout.writeInt(set.size());
                for (String uri : set) {
                    oout.writeObject(uri);
                }
            }
        } finally {
            IOUtils.closeQuietly(oout);
            IOUtils.closeQuietly(fout);
        }
        if (cacheFile.exists() && !cacheFile.delete()) {
            LOGGER.error("Cache file couldn't be deleted. Aborting.");
            return;
        }
        if (!tempCacheFile.renameTo(cacheFile)) {
            LOGGER.error("Temporary cache file couldn't be renamed. Aborting.");
            return;
        }
        cacheChanges = 0;
    }

    private void checkSetMapping() {
        IntArrayList missingSets = null;
        for (int i = 0; i < sets.size(); ++i) {
            if (sets.get(i) == null) {
                if (missingSets == null) {
                    missingSets = new IntArrayList();
                }
                missingSets.add(i);
            }
        }
        if (missingSets != null) {
            LOGGER.info("The cache contains sets that have been merged. Renumbering the existing sets.");
            // very simple approach: go through the missing sets starting with
            // the highest id, remove the null from the list of sets and reduce
            // the ids of all uris referencing a set with a higher id than the
            // deleted one
            int setId;
            for (int i = (missingSets.elementsCount - 1); i >= 0; --i) {
                setId = missingSets.buffer[i];
                sets.remove(setId);
                for (int j = 0; j < uriSetIdMapping.allocated.length; ++j) {
                    if (uriSetIdMapping.allocated[j]) {
                        if (uriSetIdMapping.values[j] == setId) {
                            LOGGER.error("Found a uri pointing to a non existing set!");
                        }
                        if (uriSetIdMapping.values[j] > setId) {
                            --uriSetIdMapping.values[j];
                        }
                    }
                }
            }
        }
    }

    public static Object[] readCacheFile(File cacheFile) {
        if (!cacheFile.exists() || cacheFile.isDirectory()) {
            return null;
        }
        ObjectInputStream oin = null;
        try {
            oin = new ObjectInputStream(new BufferedInputStream(new FileInputStream(cacheFile)));
            // first, read the number of URIs
            int count = oin.readInt();
            String uri;
            ObjectIntOpenHashMap<String> uriSetIdMapping = new ObjectIntOpenHashMap<String>(count);
            for (int i = 0; i < count; ++i) {
                uri = (String) oin.readObject();
                uriSetIdMapping.put(uri, oin.readInt());
            }
            count = oin.readInt();
            List<Set<String>> sets = new ArrayList<Set<String>>(count);
            Set<String> set;
            int setSize;
            for (int i = 0; i < count; ++i) {
                setSize = oin.readInt();
                set = new HashSet<String>(setSize);
                for (int j = 0; j < setSize; ++j) {
                    set.add((String) oin.readObject());
                }
                sets.add(set);
            }
            return new Object[] { uriSetIdMapping, sets };
        } catch (Exception e) {
            LOGGER.error("Exception while reading cache file.", e);
        } finally {
            IOUtils.closeQuietly(oin);
        }
        return null;
    }

    @Override
    public Set<String> retrieveSameURIs(String domain, String uri) {
        return retrieveSameURIs(uri);
    }
}
