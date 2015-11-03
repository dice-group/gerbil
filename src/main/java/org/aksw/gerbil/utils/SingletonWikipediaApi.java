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
package org.aksw.gerbil.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.aksw.gerbil.config.GerbilConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unipi.di.acube.batframework.utils.WikipediaApiInterface;

@Deprecated
public class SingletonWikipediaApi extends WikipediaApiInterface {

    private static final Logger LOGGER = LoggerFactory.getLogger(SingletonWikipediaApi.class);

    private static final String TITLE_CACHE_FILE_PROPERTY_NAME = "org.aksw.gerbil.utils.SingletonWikipediaApi.TitleCacheFile";
    private static final String REDIRECT_CACHE_FILE_PROPERTY_NAME = "org.aksw.gerbil.utils.SingletonWikipediaApi.RedirectCacheFile";

    public static synchronized SingletonWikipediaApi getInstance() {
        if (instance == null) {
            String titleCacheFileName = GerbilConfiguration.getInstance().getString(TITLE_CACHE_FILE_PROPERTY_NAME);
            String redirectCacheFileName = GerbilConfiguration.getInstance().getString(
                    REDIRECT_CACHE_FILE_PROPERTY_NAME);
            try {
                boolean fileCheck = true;
                File titleCacheFile = new File(titleCacheFileName);
                if (titleCacheFile.exists() && titleCacheFile.isDirectory()) {
                    LOGGER.error("The cache file \"" + titleCacheFile.getAbsolutePath() + "\" is a directory.");
                    fileCheck = false;
                }
                File parent = titleCacheFile.getParentFile();
                if ((parent != null) && (!parent.exists())) {
                    if (!parent.mkdirs()) {
                        LOGGER.error("Couldn't create folder for cache file \"" + parent.getAbsolutePath() + "\".");
                        fileCheck = false;
                    }
                }
                File redirectCacheFile = new File(titleCacheFileName);
                if (redirectCacheFile.exists() && redirectCacheFile.isDirectory()) {
                    LOGGER.error("The cache file \"" + redirectCacheFile.getAbsolutePath() + "\" is a directory.");
                    fileCheck = false;
                }
                parent = redirectCacheFile.getParentFile();
                if ((parent != null) && (!parent.exists())) {
                    if (!parent.mkdirs()) {
                        LOGGER.error("Couldn't create folder for cache file \"" + parent.getAbsolutePath() + "\".");
                        fileCheck = false;
                    }
                }

                try {
                    if (fileCheck) {
                        instance = new SingletonWikipediaApi(titleCacheFileName, redirectCacheFileName);
                    } else {
                        instance = new SingletonWikipediaApi(null, null);
                    }
                } catch (IOException e) {
                    LOGGER.error(
                            "Got an IO Exception while trying to initialize the SingletonWikipediaApi from cache. Trying it again witout caching...",
                            e);
                    instance = new SingletonWikipediaApi(null, null);
                }
            } catch (Exception e) {
                LOGGER.error("Couldn't create SingletonWikipediaApi. Returning null", e);
            }
        }
        return instance;
    }

    private static SingletonWikipediaApi instance = null;

    private SingletonWikipediaApi(String bidiTitle2widCacheFileName, String wid2redirectCacheFileName)
            throws FileNotFoundException, IOException, ClassNotFoundException {
        super(bidiTitle2widCacheFileName, wid2redirectCacheFileName);
    }

    /**
     * Overrides the method of the super class because as a singleton this class
     * needs a synchronized version of this method.
     */
    public synchronized void flush() throws FileNotFoundException, IOException {
        super.flush();
    }
}
