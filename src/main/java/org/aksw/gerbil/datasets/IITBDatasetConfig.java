package org.aksw.gerbil.datasets;

import java.io.IOException;

import it.acubelab.batframework.datasetPlugins.IITBDataset;
import it.acubelab.batframework.problems.TopicDataset;
import it.acubelab.batframework.utils.WikipediaApiInterface;

import org.aksw.gerbil.GerbilConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;

public class IITBDatasetConfig extends AbstractDatasetConfiguration {

    private static final String IITB_CRAWL_FOLDER_PROPERTY_NAME = "org.aksw.gerbil.datasets.IITBDatasetConfig.CrawledDocs";
    private static final String IITB_ANNOTATIONS_FILE_PROPERTY_NAME = "org.aksw.gerbil.datasets.IITBDatasetConfig.Annotations";

    private WikipediaApiInterface wikiAPI;

    public IITBDatasetConfig(WikipediaApiInterface wikiAPI) {
        super("IITB", true, ExperimentType.D2W);
        this.wikiAPI = wikiAPI;
    }

    @Override
    protected TopicDataset loadDataset() throws Exception {
        String crawlFolder = GerbilConfiguration.getInstance().getString(IITB_CRAWL_FOLDER_PROPERTY_NAME);
        if (crawlFolder == null) {
            throw new IOException("Couldn't load needed Property \"" + IITB_CRAWL_FOLDER_PROPERTY_NAME + "\".");
        }
        String annotationsFile = GerbilConfiguration.getInstance().getString(IITB_ANNOTATIONS_FILE_PROPERTY_NAME);
        if (annotationsFile == null) {
            throw new IOException("Couldn't load needed Property \"" + IITB_ANNOTATIONS_FILE_PROPERTY_NAME + "\".");
        }
        return new IITBDataset(crawlFolder, annotationsFile, wikiAPI);
    }

}
