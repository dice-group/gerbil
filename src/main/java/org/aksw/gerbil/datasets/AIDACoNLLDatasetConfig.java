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
package org.aksw.gerbil.datasets;

import it.unipi.di.acube.batframework.datasetPlugins.ConllAidaDataset;
import it.unipi.di.acube.batframework.datasetPlugins.ConllAidaTestADataset;
import it.unipi.di.acube.batframework.datasetPlugins.ConllAidaTestBDataset;
import it.unipi.di.acube.batframework.datasetPlugins.ConllAidaTrainingDataset;
import it.unipi.di.acube.batframework.problems.TopicDataset;
import it.unipi.di.acube.batframework.utils.WikipediaApiInterface;
import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;

import java.io.IOException;

public class AIDACoNLLDatasetConfig extends AbstractDatasetConfiguration {

    public static final String DATASET_NAME_START = "AIDA/CoNLL";

    private static final String DATASET_FILE_PROPERTY_NAME = "org.aksw.gerbil.datasets.AIDACoNLLDatasetConfig.DatasetFile";

    public static enum AIDACoNLLChunk {
        TRAINING, TEST_A, TEST_B, COMPLETE
    }

    private AIDACoNLLChunk chunk;
    private WikipediaApiInterface wikiApi;

    public AIDACoNLLDatasetConfig(AIDACoNLLChunk chunk, WikipediaApiInterface wikiApi) {
        super(DATASET_NAME_START, true, ExperimentType.Sa2KB);
        this.chunk = chunk;
        this.wikiApi = wikiApi;
        // Set the correct name
        switch (chunk) {
        case TRAINING: {
            setName(getName() + "-Training");
            break;
        }
        case TEST_A: {
            setName(getName() + "-Test A");
            break;
        }
        case TEST_B: {
            setName(getName() + "-Test B");
            break;
        }
        case COMPLETE: {
            setName(getName() + "-Complete");
            break;
        }
        }
    }

    @Override
    protected TopicDataset loadDataset() throws Exception {
        String file = GerbilConfiguration.getInstance().getString(DATASET_FILE_PROPERTY_NAME);
        if (file == null) {
            throw new IOException("Couldn't load needed Property \"" + DATASET_FILE_PROPERTY_NAME + "\".");
        }
        switch (chunk) {
        case TRAINING: {
            return new ConllAidaTrainingDataset(file, wikiApi);
        }
        case TEST_A: {
            return new ConllAidaTestADataset(file, wikiApi);
        }
        case TEST_B: {
            return new ConllAidaTestBDataset(file, wikiApi);
        }
        case COMPLETE: {
            return new ConllAidaDataset(file, wikiApi);
        }
        }
        return null;
    }
}
