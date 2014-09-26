package org.aksw.gerbil.datasets;

import java.io.IOException;

import it.acubelab.batframework.datasetPlugins.ConllAidaDataset;
import it.acubelab.batframework.datasetPlugins.ConllAidaTestADataset;
import it.acubelab.batframework.datasetPlugins.ConllAidaTestBDataset;
import it.acubelab.batframework.datasetPlugins.ConllAidaTrainingDataset;
import it.acubelab.batframework.problems.TopicDataset;
import it.acubelab.batframework.utils.WikipediaApiInterface;

import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;

public class AIDACoNLLDatasetConfig extends AbstractDatasetConfiguration {
    
    public static final String DATASET_NAME_START = "AIDA/CoNLL";

    private static final String DATASET_FILE_PROPERTY_NAME = "org.aksw.gerbil.datasets.AIDACoNLLDatasetConfig.DatasetFile";

    public static enum AIDACoNLLChunk {
        TRAINING,
        TEST_A,
        TEST_B,
        COMPLETE
    }

    private AIDACoNLLChunk chunk;
    private WikipediaApiInterface wikiApi;

    public AIDACoNLLDatasetConfig(AIDACoNLLChunk chunk, WikipediaApiInterface wikiApi) {
        super(DATASET_NAME_START, true, ExperimentType.Sa2W);
        this.chunk = chunk;
        this.wikiApi = wikiApi;
        // Set the correct name
        switch (chunk) {
        case TRAINING: {
            this.datasetName = this.datasetName + "-Training";
            break;
        }
        case TEST_A: {
            this.datasetName = this.datasetName + "-Test A";
            break;
        }
        case TEST_B: {
            this.datasetName = this.datasetName + "-Test B";
            break;
        }
        case COMPLETE: {
            this.datasetName = this.datasetName + "-Complete";
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
