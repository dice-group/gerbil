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
package org.aksw.gerbil.datasets;

import it.acubelab.batframework.datasetPlugins.MeijDataset;
import it.acubelab.batframework.problems.TopicDataset;

import java.io.IOException;

import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;

public class MeijDatasetConfig extends AbstractDatasetConfiguration {

    public static final String DATASET_NAME = "Meij";

    private static final String MEIJ_TWEETS_FILE_PROPERTY_NAME = "org.aksw.gerbil.datasets.MeijDatasetConfig.tweetsFile";
    private static final String MEIJ_TAGS_FILE_PROPERTY_NAME = "org.aksw.gerbil.datasets.MeijDatasetConfig.tagsFile";
    private static final String MEIJ_RANK_FILE_PROPERTY_NAME = "org.aksw.gerbil.datasets.MeijDatasetConfig.rankFile";

    public MeijDatasetConfig() {
        super(DATASET_NAME, true, ExperimentType.Rc2W);
    }

    @Override
    protected TopicDataset loadDataset() throws Exception {
        String tweetsFile = GerbilConfiguration.getInstance().getString(MEIJ_TWEETS_FILE_PROPERTY_NAME);
        if (tweetsFile == null) {
            throw new IOException("Couldn't load needed Property \"" + MEIJ_TWEETS_FILE_PROPERTY_NAME + "\".");
        }
        String tagsFile = GerbilConfiguration.getInstance().getString(MEIJ_TAGS_FILE_PROPERTY_NAME);
        if (tagsFile == null) {
            throw new IOException("Couldn't load needed Property \"" + MEIJ_TAGS_FILE_PROPERTY_NAME + "\".");
        }
        String rankFile = GerbilConfiguration.getInstance().getString(MEIJ_RANK_FILE_PROPERTY_NAME);
        if (rankFile == null) {
            throw new IOException("Couldn't load needed Property \"" + MEIJ_RANK_FILE_PROPERTY_NAME + "\".");
        }
        return new MeijDataset(tweetsFile, tagsFile, rankFile);
    }

}
