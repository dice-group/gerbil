/**
 * The MIT License
 * Copyright (c) 2014 Agile Knowledge Engineering and Semantic Web (AKSW) (usbeck@informatik.uni-leipzig.de)
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
package org.aksw.gerbil.datasets;

import it.unipi.di.acube.batframework.datasetPlugins.IITBDataset;
import it.unipi.di.acube.batframework.utils.WikipediaApiInterface;

import java.io.IOException;

import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.dataset.Dataset;
import org.aksw.gerbil.dataset.impl.bat.BatFrameworkDatasetWrapper;
import org.aksw.gerbil.datatypes.ExperimentType;

public class IITBDatasetConfig extends AbstractDatasetConfiguration {

    public static final String DATASET_NAME = "IITB"
	    + BatFrameworkDatasetWrapper.DATASET_NAME_SUFFIX;

    private static final String IITB_CRAWL_FOLDER_PROPERTY_NAME = "org.aksw.gerbil.datasets.IITBDatasetConfig.CrawledDocs";
    private static final String IITB_ANNOTATIONS_FILE_PROPERTY_NAME = "org.aksw.gerbil.datasets.IITBDatasetConfig.Annotations";

    private WikipediaApiInterface wikiAPI;

    @SuppressWarnings("deprecation")
    public IITBDatasetConfig(WikipediaApiInterface wikiAPI) {
	super(DATASET_NAME, true, ExperimentType.Sa2KB);
	this.wikiAPI = wikiAPI;
    }

    @Override
    protected Dataset loadDataset() throws Exception {
	String crawlFolder = GerbilConfiguration.getInstance().getString(
		IITB_CRAWL_FOLDER_PROPERTY_NAME);
	if (crawlFolder == null) {
	    throw new IOException("Couldn't load needed Property \""
		    + IITB_CRAWL_FOLDER_PROPERTY_NAME + "\".");
	}
	String annotationsFile = GerbilConfiguration.getInstance().getString(
		IITB_ANNOTATIONS_FILE_PROPERTY_NAME);
	if (annotationsFile == null) {
	    throw new IOException("Couldn't load needed Property \""
		    + IITB_ANNOTATIONS_FILE_PROPERTY_NAME + "\".");
	}
	return BatFrameworkDatasetWrapper.create(new IITBDataset(crawlFolder,
		annotationsFile, wikiAPI), wikiAPI);
    }

}
