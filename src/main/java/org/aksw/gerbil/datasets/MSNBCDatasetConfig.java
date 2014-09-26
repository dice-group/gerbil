package org.aksw.gerbil.datasets;

import it.acubelab.batframework.datasetPlugins.MSNBCDataset;
import it.acubelab.batframework.problems.TopicDataset;
import it.acubelab.batframework.utils.WikipediaApiInterface;

import java.io.IOException;

import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;

public class MSNBCDatasetConfig extends AbstractDatasetConfiguration {

    public static final String DATASET_NAME = "MSNBC";

    private static final String MSNBC_TEXTS_FOLDER_PROPERTY_NAME = "org.aksw.gerbil.datasets.MSNBCDatasetConfig.TextsFolder";
    private static final String MSNBC_ANNOTATIONS_FOLDER_PROPERTY_NAME = "org.aksw.gerbil.datasets.MSNBCDatasetConfig.AnnotationsFolder";

    private WikipediaApiInterface wikiAPI;

    public MSNBCDatasetConfig(WikipediaApiInterface wikiAPI) {
        super(DATASET_NAME, true, ExperimentType.D2W);
        this.wikiAPI = wikiAPI;
    }

    @Override
    protected TopicDataset loadDataset() throws Exception {
        String textsFolder = GerbilConfiguration.getInstance().getString(MSNBC_TEXTS_FOLDER_PROPERTY_NAME);
        if (textsFolder == null) {
            throw new IOException("Couldn't load needed Property \"" + MSNBC_TEXTS_FOLDER_PROPERTY_NAME + "\".");
        }
        String annotationsFolder = GerbilConfiguration.getInstance()
                .getString(MSNBC_ANNOTATIONS_FOLDER_PROPERTY_NAME);
        if (annotationsFolder == null) {
            throw new IOException("Couldn't load needed Property \"" + MSNBC_ANNOTATIONS_FOLDER_PROPERTY_NAME + "\".");
        }
        return new MSNBCDataset(textsFolder, annotationsFolder, wikiAPI);
    }
}
