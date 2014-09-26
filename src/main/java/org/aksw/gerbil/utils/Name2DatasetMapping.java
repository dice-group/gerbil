package org.aksw.gerbil.utils;

import it.acubelab.batframework.systemPlugins.DBPediaApi;
import it.acubelab.batframework.utils.WikipediaApiInterface;

import org.aksw.gerbil.annotators.AnnotatorConfiguration;
import org.aksw.gerbil.annotators.BabelfyAnnotatorConfig;
import org.aksw.gerbil.annotators.NIFWebserviceAnnotatorConfiguration;
import org.aksw.gerbil.annotators.SpotlightAnnotatorConfig;
import org.aksw.gerbil.annotators.TagMeAnnotatorConfig;
import org.aksw.gerbil.annotators.WikipediaMinerAnnotatorConfig;
import org.aksw.gerbil.datasets.ACE2004DatasetConfig;
import org.aksw.gerbil.datasets.AIDACoNLLDatasetConfig;
import org.aksw.gerbil.datasets.DatasetConfiguration;
import org.aksw.gerbil.datasets.AIDACoNLLDatasetConfig.AIDACoNLLChunk;
import org.aksw.gerbil.datatypes.ExperimentType;

public class Name2DatasetMapping {

    public static DatasetConfiguration getAnnotatorConfig(String name) {
        switch (name) {
        case ACE2004DatasetConfig.DATASET_NAME:
            return new ACE2004DatasetConfig(SingletonWikipediaApi.getInstance());
            // case NIFWebserviceAnnotatorConfiguration.DATASET_NAME:
            // return new NIFWebserviceAnnotatorConfiguration(null, name, false, ExperimentType.Sa2W);
            // case SpotlightAnnotatorConfig.DATASET_NAME:
            // return new SpotlightAnnotatorConfig(SingletonWikipediaApi.getInstance(), new DBPediaApi());
            // case TagMeAnnotatorConfig.DATASET_NAME:
            // return new TagMeAnnotatorConfig();
            // case WikipediaMinerAnnotatorConfig.DATASET_NAME:
            // return new WikipediaMinerAnnotatorConfig();
        }

        if (name.startsWith(AIDACoNLLDatasetConfig.DATASET_NAME_START)) {
            AIDACoNLLChunk chunk = null;
            switch (name) {
            case AIDACoNLLDatasetConfig.DATASET_NAME_START + "-Training": {
                chunk = AIDACoNLLChunk.TRAINING;
                break;
            }
            case AIDACoNLLDatasetConfig.DATASET_NAME_START + "-Test A": {
                chunk = AIDACoNLLChunk.TEST_A;
                break;
            }
            case AIDACoNLLDatasetConfig.DATASET_NAME_START + "-Test B": {
                chunk = AIDACoNLLChunk.TEST_B;
                break;
            }
            case AIDACoNLLDatasetConfig.DATASET_NAME_START + "-Complete": {
                chunk = AIDACoNLLChunk.COMPLETE;
                break;
            }
            default:
                return null;
            }
            return new AIDACoNLLDatasetConfig(chunk, SingletonWikipediaApi.getInstance());
        }
        return null;
    }
}
