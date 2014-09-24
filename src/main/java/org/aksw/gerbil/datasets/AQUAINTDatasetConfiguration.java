package org.aksw.gerbil.datasets;

import java.io.IOException;

import it.acubelab.batframework.datasetPlugins.AQUAINTDataset;
import it.acubelab.batframework.problems.TopicDataset;
import it.acubelab.batframework.utils.WikipediaApiInterface;

import org.aksw.gerbil.GerbilConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;

public class AQUAINTDatasetConfiguration extends AbstractDatasetConfiguration {

    private static final String TEXTS_PATH_PROPERTY_NAME = "org.aksw.gerbil.datasets.AQUAINTDatasetConfiguration.TextsPath";
    private static final String ANNOTATIONS_PATH_PROPERTY_NAME = "org.aksw.gerbil.datasets.AQUAINTDatasetConfiguration.AnnotationsPath";

    private WikipediaApiInterface wikiApi;

    public AQUAINTDatasetConfiguration(WikipediaApiInterface wikiApi) {
        super("AQUAINT", true, ExperimentType.Sa2W);
        this.wikiApi = wikiApi;
    }

    @Override
    protected TopicDataset loadDataset() throws Exception {
        String textsPath = GerbilConfiguration.getInstance().getString(TEXTS_PATH_PROPERTY_NAME);
        if (textsPath == null) {
            throw new IOException("Couldn't load needed Property \"" + TEXTS_PATH_PROPERTY_NAME + "\".");
        }
        String annotationsPath = GerbilConfiguration.getInstance().getString(ANNOTATIONS_PATH_PROPERTY_NAME);
        if (annotationsPath == null) {
            throw new IOException("Couldn't load needed Property \"" + ANNOTATIONS_PATH_PROPERTY_NAME + "\".");
        }
        return new AQUAINTDataset(textsPath, annotationsPath, wikiApi);
    }

}
