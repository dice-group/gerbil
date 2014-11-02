package org.aksw.gerbil.datasets;

import it.acubelab.batframework.problems.TopicDataset;
import it.acubelab.batframework.systemPlugins.DBPediaApi;
import it.acubelab.batframework.utils.WikipediaApiInterface;

import org.aksw.gerbil.bat.datasets.FileBasedNIFDataset;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.apache.jena.riot.Lang;

public class NIFFileDatasetConfig extends AbstractDatasetConfiguration {

    private String nifFile;
    private WikipediaApiInterface wikiApi;
    private DBPediaApi dbpediaApi;

    public NIFFileDatasetConfig(WikipediaApiInterface wikiApi, DBPediaApi dbpediaApi, String name, String nifFile) {
        super(name, true, ExperimentType.Sa2W);
        this.nifFile = nifFile;
        this.wikiApi = wikiApi;
        this.dbpediaApi = dbpediaApi;
    }

    @Override
    protected TopicDataset loadDataset() throws Exception {
        return new FileBasedNIFDataset(wikiApi, dbpediaApi, nifFile, getName(), Lang.TTL);
    }
}
