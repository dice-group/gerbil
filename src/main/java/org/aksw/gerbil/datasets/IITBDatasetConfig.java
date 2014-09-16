package org.aksw.gerbil.datasets;

import it.acubelab.batframework.datasetPlugins.IITBDataset;
import it.acubelab.batframework.problems.TopicDataset;
import it.acubelab.batframework.utils.WikipediaApiInterface;

import org.aksw.gerbil.GerbilProperties;
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
        return new IITBDataset(GerbilProperties.getPropertyValue(IITB_CRAWL_FOLDER_PROPERTY_NAME),
                GerbilProperties.getPropertyValue(IITB_ANNOTATIONS_FILE_PROPERTY_NAME), wikiAPI);
    }

}
