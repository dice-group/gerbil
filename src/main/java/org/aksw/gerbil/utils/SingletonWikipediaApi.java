/**
 * The MIT License (MIT)
 *
 * Copyright (C) 2014 Agile Knowledge Engineering and Semantic Web (AKSW) (usbeck@informatik.uni-leipzig.de)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.aksw.gerbil.utils;

import it.acubelab.batframework.utils.WikipediaApiInterface;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.aksw.gerbil.config.GerbilConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
