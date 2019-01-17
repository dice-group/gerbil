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
package org.aksw.gerbil.dataset.check.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.Semaphore;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrotsearch.hppc.ObjectLongOpenHashMap;

/**
 * <p>
 * This class is an extension of the {@link EntityCheckerManagerImpl} that
 * caches the results using a persistent file-based cache. Note that the cache
 * stores the time stamp at which a URI is added to the cache. Cache entries
 * have a maximal lifetime. If it is exceeded, the entries might be deleted from
 * the cache.
 * </p>
 * <p>
 * Internally the result of the checking is stored using the lowest bit of the
 * timestamp.
 * </p>
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public class FileBasedCachingEntityCheckerManager extends EntityCheckerManagerImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileBasedCachingEntityCheckerManager.class);

    private static final int MAX_CONCURRENT_READERS = 1000;
    private static final int EXISTS_FLAG_MASK = 0x1;
    private static final int ENTITY_EXISTS_BIT = 0x1;
    private static final int ENTITY_DOES_NOT_EXIST_BIT = 0x0;
    private static final int ERASE_EXISTS_FLAG_MASK = ~EXISTS_FLAG_MASK;

    public static FileBasedCachingEntityCheckerManager create(long cacheEntryLifetime, File cacheFile) {
        File tempCacheFile = new File(cacheFile.getAbsolutePath() + "_temp");

        ObjectLongOpenHashMap<String> cache = null;
        // try to read the cache file
        cache = readCacheFile(cacheFile);
        // if this doesn't work, try to read the temp file
        if (cache == null) {
            LOGGER.warn("Couldn't read the cache file. Trying the temporary file...");
            cache = readCacheFile(tempCacheFile);
            // if this worked, rename the temp file to the real file
            if (cache != null) {
                try {
                    if (!tempCacheFile.renameTo(cacheFile)) {
                        LOGGER.warn("Reading from the temporary cache file worked, but I couldn't rename it.");
                    }
                } catch (Exception e) {
                    LOGGER.warn("Reading from the temporary cache file worked, but I couldn't rename it.", e);
                }
            }
        }
        // if the reading didn't worked, create new cache objects
        if (cache == null) {
            LOGGER.warn("Couldn't read cache from files. Creating new empty cache.");
            cache = new ObjectLongOpenHashMap<String>();
        }
        return new FileBasedCachingEntityCheckerManager(cache, cacheEntryLifetime, cacheFile, tempCacheFile);
    }

    public static ObjectLongOpenHashMap<String> readCacheFile(File cacheFile) {
        if (!cacheFile.exists() || cacheFile.isDirectory()) {
            return null;
        }
        try(ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(cacheFile)))) {
            // first, read the number of URIs
            int count = ois.readInt();
            String uri;
            ObjectLongOpenHashMap<String> cache = new ObjectLongOpenHashMap<String>(2 * count);
            for (int i = 0; i < count; ++i) {
                uri = (String) ois.readObject();
                cache.put(uri, ois.readLong());
            }
            return cache;
        } catch (Exception e) {
            LOGGER.error("Exception while reading cache file.", e);
        }
        return null;
    }

    protected ObjectLongOpenHashMap<String> cache;
    private long cacheEntryLifetime;
    private int cacheChanges = 0;
    private int forceStorageAfterChanges = 10000;
    private Semaphore cacheReadMutex = new Semaphore(MAX_CONCURRENT_READERS);
    private Semaphore cacheWriteMutex = new Semaphore(1);
    protected File cacheFile;
    protected File tempCacheFile;

    protected FileBasedCachingEntityCheckerManager(ObjectLongOpenHashMap<String> cache, long cacheEntryLifetime,
            File cacheFile, File tempCacheFile) {
        this.cache = cache;
        this.cacheEntryLifetime = cacheEntryLifetime;
        this.cacheFile = cacheFile;
        this.tempCacheFile = tempCacheFile;
    }

    @Override
    public boolean checkUri(String uri) {
        try {
            cacheReadMutex.acquire();
        } catch (InterruptedException e) {
            LOGGER.error("Exception while waiting for read mutex. Returning true.", e);
            return true;
        }
        boolean uriIsCached = cache.containsKey(uri);
        long timestamp, resultBit = ENTITY_EXISTS_BIT;
        if (uriIsCached) {
            timestamp = cache.get(uri);
            if ((System.currentTimeMillis() - timestamp) < cacheEntryLifetime) {
                resultBit = timestamp & EXISTS_FLAG_MASK;
            } else {
                uriIsCached = false;
            }
        }
        // If the URI is not in the cache, or it has been cached but the result
        // is null and the request should be retried
        if (!uriIsCached) {
            cacheReadMutex.release();
            resultBit = super.checkUri(uri) ? ENTITY_EXISTS_BIT : ENTITY_DOES_NOT_EXIST_BIT;
            // Set the new timestamp inside the cache
            try {
                cacheWriteMutex.acquire();
                // now we need all others
                cacheReadMutex.acquire(MAX_CONCURRENT_READERS);
            } catch (InterruptedException e) {
                LOGGER.error("Exception while waiting for read mutex. Returning.", e);
                return resultBit != ENTITY_DOES_NOT_EXIST_BIT;
            }
            timestamp = (System.currentTimeMillis() & ERASE_EXISTS_FLAG_MASK) | resultBit;
            cache.put(uri, timestamp);
            ++cacheChanges;
            if ((forceStorageAfterChanges > 0) && (cacheChanges >= forceStorageAfterChanges)) {
                LOGGER.info("Storing the cache has been forced...");
                try {
                    performCacheStorage();
                } catch (IOException e) {
                    LOGGER.error("Exception while writing cache to file. Aborting.", e);
                }
            }
            // The last one will be released at the end
            cacheReadMutex.release(MAX_CONCURRENT_READERS - 1);
            cacheWriteMutex.release();
        }
        cacheReadMutex.release();
        return resultBit != ENTITY_DOES_NOT_EXIST_BIT;
    }

    protected boolean performCheck(String uri) {
        return super.checkUri(uri);
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

    /**
     * Writes the cache to the {@link #tempCacheFile}. After that the
     * {@link #cacheFile} is deleted and the {@link #tempCacheFile} is renamed.
     * <b>NOTE</b> that this method should only be called if the
     * {@link #cacheWriteMutex} has been acquired.
     * 
     * @throws IOException
     */
    private void performCacheStorage() throws IOException {
        eraseOldEntries();
        FileOutputStream fout = null;
        ObjectOutputStream oout = null;
        try {
            fout = new FileOutputStream(tempCacheFile);
            oout = new ObjectOutputStream(fout);
            // first, serialize the number of URIs
            oout.writeInt(cache.assigned);
            // go over the mapping and serialize all existing pairs
            for (int i = 0; i < cache.allocated.length; ++i) {
                if (cache.allocated[i]) {
                    oout.writeObject(((Object[]) cache.keys)[i]);
                    oout.writeLong(cache.values[i]);
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

    private void eraseOldEntries() {
        // TODO Add the erasing of old entries

        // long currentTime = System.currentTimeMillis();
        // for (int i = 0; i < cache.allocated.length; ++i) {
        // if (cache.allocated[i]) {
        // // If this entry is to old
        // if ((currentTime - cache.values[i]) > cacheEntryLifetime) {
        // ((Object[]) cache.keys)[i] = null;
        // cache.values[i] = 0L;
        // --cache.assigned;
        // cache.allocated[i] = false;
        // }
        // }
        // }
    }

}
