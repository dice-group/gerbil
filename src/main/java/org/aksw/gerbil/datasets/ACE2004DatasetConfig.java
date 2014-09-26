package org.aksw.gerbil.datasets;

import it.acubelab.batframework.datasetPlugins.ACE2004Dataset;
import it.acubelab.batframework.problems.TopicDataset;
import it.acubelab.batframework.utils.WikipediaApiInterface;

import java.io.IOException;

import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;

public class ACE2004DatasetConfig extends AbstractDatasetConfiguration {

    public static final String DATASET_NAME = "ACE2004";

    private static final String ACE2004_TEXTS_FOLDER_PROPERTY_NAME = "org.aksw.gerbil.datasets.ACE2004DatasetConfig.TextsFolder";
    private static final String ACE2004_ANNOTATIONS_FOLDER_PROPERTY_NAME = "org.aksw.gerbil.datasets.ACE2004DatasetConfig.AnnotationsFolder";

    private WikipediaApiInterface wikiAPI;

    public ACE2004DatasetConfig(WikipediaApiInterface wikiAPI) {
        super(DATASET_NAME, true, ExperimentType.D2W);
        this.wikiAPI = wikiAPI;
    }

    @Override
    protected TopicDataset loadDataset() throws Exception {
        String textsFolder = GerbilConfiguration.getInstance().getString(ACE2004_TEXTS_FOLDER_PROPERTY_NAME);
        if (textsFolder == null) {
            throw new IOException("Couldn't load needed Property \"" + ACE2004_TEXTS_FOLDER_PROPERTY_NAME + "\".");
        }
        String annotationsFolder = GerbilConfiguration.getInstance()
                .getString(ACE2004_ANNOTATIONS_FOLDER_PROPERTY_NAME);
        if (annotationsFolder == null) {
            throw new IOException("Couldn't load needed Property \"" + ACE2004_ANNOTATIONS_FOLDER_PROPERTY_NAME + "\".");
        }
        return new ACE2004Dataset(textsFolder, annotationsFolder, wikiAPI);
    }
}
