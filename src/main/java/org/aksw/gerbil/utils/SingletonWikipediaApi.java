package org.aksw.gerbil.utils;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.aksw.gerbil.config.GerbilConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.acubelab.batframework.utils.WikipediaApiInterface;

public class SingletonWikipediaApi extends WikipediaApiInterface {

    private static final Logger LOGGER = LoggerFactory.getLogger(SingletonWikipediaApi.class);

    private static final String TITLE_CACHE_FILE_PROPERTY_NAME = "org.aksw.gerbil.utils.SingletonWikipediaApi.TitleCacheFile";
    private static final String REDIRECT_CACHE_FILE_PROPERTY_NAME = "org.aksw.gerbil.utils.SingletonWikipediaApi.RedirectCacheFile";

    public static synchronized SingletonWikipediaApi getInstance() {
        if (instance == null) {
            String titleCacheFile = GerbilConfiguration.getInstance().getString(TITLE_CACHE_FILE_PROPERTY_NAME);
            String redirectCacheFile = GerbilConfiguration.getInstance().getString(REDIRECT_CACHE_FILE_PROPERTY_NAME);
            try {
                try {
                    instance = new SingletonWikipediaApi(titleCacheFile, redirectCacheFile);
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
     * Overrides the method of the super class because as a singleton this class needs a synchronized version of this
     * method.
     */
    public synchronized void flush() throws FileNotFoundException, IOException {
        super.flush();
    }
}
