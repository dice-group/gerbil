package org.aksw.gerbil.datasets;

import it.acubelab.batframework.problems.TopicDataset;
import it.acubelab.batframework.utils.WikipediaApiInterface;

import org.aksw.gerbil.bat.datasets.FileBasedNIFDataset;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.apache.jena.riot.Lang;

public class NIFFileDatasetConfig extends AbstractDatasetConfiguration {

    private String file;
    private WikipediaApiInterface wikiApi;

    public NIFFileDatasetConfig(WikipediaApiInterface wikiApi, String name, String file, boolean couldBeCached,
            ExperimentType... applicableForExperiment) {
        super(name, couldBeCached, applicableForExperiment);
        this.wikiApi = wikiApi;
    }

    @Override
    protected TopicDataset loadDataset() throws Exception {
        FileBasedNIFDataset dataset = new FileBasedNIFDataset(wikiApi, file, getName(), Lang.TTL);
        dataset.init();
        return dataset;
    }
}
