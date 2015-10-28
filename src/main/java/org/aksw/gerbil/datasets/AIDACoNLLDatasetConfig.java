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

import it.unipi.di.acube.batframework.datasetPlugins.ConllAidaDataset;
import it.unipi.di.acube.batframework.datasetPlugins.ConllAidaTestADataset;
import it.unipi.di.acube.batframework.datasetPlugins.ConllAidaTestBDataset;
import it.unipi.di.acube.batframework.datasetPlugins.ConllAidaTrainingDataset;
import it.unipi.di.acube.batframework.problems.TopicDataset;
import it.unipi.di.acube.batframework.utils.WikipediaApiInterface;

import java.io.IOException;

import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.dataset.Dataset;
import org.aksw.gerbil.dataset.impl.bat.BatFrameworkDatasetWrapper;
import org.aksw.gerbil.datatypes.ExperimentType;

public class AIDACoNLLDatasetConfig extends AbstractDatasetConfiguration {

    public static final String DATASET_NAME_START = "AIDA/CoNLL";

    private static final String DATASET_FILE_PROPERTY_NAME = "org.aksw.gerbil.datasets.AIDACoNLLDatasetConfig.DatasetFile";

    public static enum AIDACoNLLChunk {
	TRAINING, TEST_A, TEST_B, COMPLETE
    }

    private AIDACoNLLChunk chunk;
    private WikipediaApiInterface wikiApi;

    @SuppressWarnings("deprecation")
    public AIDACoNLLDatasetConfig(AIDACoNLLChunk chunk,
	    WikipediaApiInterface wikiApi) {
	super(DATASET_NAME_START, true, ExperimentType.Sa2KB);
	this.chunk = chunk;
	this.wikiApi = wikiApi;
	// Set the correct name
	switch (chunk) {
	case TRAINING: {
	    setName(getName() + "-Training"
		    + BatFrameworkDatasetWrapper.DATASET_NAME_SUFFIX);
	    break;
	}
	case TEST_A: {
	    setName(getName() + "-Test A"
		    + BatFrameworkDatasetWrapper.DATASET_NAME_SUFFIX);
	    break;
	}
	case TEST_B: {
	    setName(getName() + "-Test B"
		    + BatFrameworkDatasetWrapper.DATASET_NAME_SUFFIX);
	    break;
	}
	case COMPLETE: {
	    setName(getName() + "-Complete"
		    + BatFrameworkDatasetWrapper.DATASET_NAME_SUFFIX);
	    break;
	}
	}
    }

    @Override
    protected Dataset loadDataset() throws Exception {
	String file = GerbilConfiguration.getInstance().getString(
		DATASET_FILE_PROPERTY_NAME);
	if (file == null) {
	    throw new IOException("Couldn't load needed Property \""
		    + DATASET_FILE_PROPERTY_NAME + "\".");
	}
	TopicDataset dataset = null;
	switch (chunk) {
	case TRAINING: {
	    dataset = new ConllAidaTrainingDataset(file, wikiApi);
	    break;
	}
	case TEST_A: {
	    dataset = new ConllAidaTestADataset2(file, wikiApi);
	    break;
	}
	case TEST_B: {
	    dataset = new ConllAidaTestBDataset(file, wikiApi);
	    break;
	}
	case COMPLETE: {
	    dataset = new ConllAidaDataset(file, wikiApi);
	    break;
	}
	default: {
	    return null;
	}
	}
	return BatFrameworkDatasetWrapper.create(dataset, wikiApi);
    }
}
