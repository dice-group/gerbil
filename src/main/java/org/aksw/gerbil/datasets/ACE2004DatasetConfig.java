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

import it.unipi.di.acube.batframework.datasetPlugins.ACE2004Dataset;
import it.unipi.di.acube.batframework.utils.WikipediaApiInterface;

import java.io.IOException;

import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.dataset.Dataset;
import org.aksw.gerbil.dataset.impl.bat.BatFrameworkDatasetWrapper;
import org.aksw.gerbil.datatypes.ExperimentType;

public class ACE2004DatasetConfig extends AbstractDatasetConfiguration {

    public static final String DATASET_NAME = "ACE2004"
	    + BatFrameworkDatasetWrapper.DATASET_NAME_SUFFIX;

    private static final String ACE2004_TEXTS_FOLDER_PROPERTY_NAME = "org.aksw.gerbil.datasets.ACE2004DatasetConfig.TextsFolder";
    private static final String ACE2004_ANNOTATIONS_FOLDER_PROPERTY_NAME = "org.aksw.gerbil.datasets.ACE2004DatasetConfig.AnnotationsFolder";

    private WikipediaApiInterface wikiAPI;

    @SuppressWarnings("deprecation")
    public ACE2004DatasetConfig(WikipediaApiInterface wikiAPI) {
	super(DATASET_NAME, true, ExperimentType.Sa2KB);
	this.wikiAPI = wikiAPI;
    }

    @Override
    protected Dataset loadDataset() throws Exception {
	String textsFolder = GerbilConfiguration.getInstance().getString(
		ACE2004_TEXTS_FOLDER_PROPERTY_NAME);
	if (textsFolder == null) {
	    throw new IOException("Couldn't load needed Property \""
		    + ACE2004_TEXTS_FOLDER_PROPERTY_NAME + "\".");
	}
	String annotationsFolder = GerbilConfiguration.getInstance().getString(
		ACE2004_ANNOTATIONS_FOLDER_PROPERTY_NAME);
	if (annotationsFolder == null) {
	    throw new IOException("Couldn't load needed Property \""
		    + ACE2004_ANNOTATIONS_FOLDER_PROPERTY_NAME + "\".");
	}
	return BatFrameworkDatasetWrapper.create(new ACE2004Dataset(
		textsFolder, annotationsFolder, wikiAPI), wikiAPI);
    }
}
