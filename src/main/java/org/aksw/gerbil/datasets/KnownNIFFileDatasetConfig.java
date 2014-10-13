package org.aksw.gerbil.datasets;

import java.io.IOException;

import it.acubelab.batframework.problems.TopicDataset;
import it.acubelab.batframework.systemPlugins.DBPediaApi;
import it.acubelab.batframework.utils.WikipediaApiInterface;

import org.aksw.gerbil.bat.datasets.FileBasedNIFDataset;
import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.apache.jena.riot.Lang;

public class KnownNIFFileDatasetConfig extends AbstractDatasetConfiguration {

    private static final String NIF_DATASET_FILE_PROPERTY_NAME = "org.aksw.gerbil.datasets.KnownNIFFileDatasetConfig.";

    public static enum NIFDatasets {
        KORE50("KORE50"),
        N3_NEWS_100("N3-News-100"),
        N3_REUTERS_128("N3-Reuters-128"),
        N3_RSS_500("N3-RSS-500"),
        DBPEDIA_SPOTLIGHT("DBpediaSpotlight");

        private String name;

        private NIFDatasets(String name) {
            this.name = name;
        }

        public String getDatasetName() {
            return name;
        }
    }

    private NIFDatasets dataset;
    private WikipediaApiInterface wikiApi;
    private DBPediaApi dbpediaApi;

    public KnownNIFFileDatasetConfig(WikipediaApiInterface wikiApi, DBPediaApi dbpediaApi, NIFDatasets dataset) {
        super(dataset.getDatasetName(), true, ExperimentType.Sa2W);
        this.dataset = dataset;
        this.wikiApi = wikiApi;
        this.dbpediaApi = dbpediaApi;
    }

    @Override
    protected TopicDataset loadDataset() throws Exception {
        String propertyKey = NIF_DATASET_FILE_PROPERTY_NAME + dataset.name();
        String nifFile = GerbilConfiguration.getInstance().getString(propertyKey);
        if (nifFile == null) {
            throw new IOException("Couldn't load needed Property \"" + propertyKey + "\".");
        }
        return new FileBasedNIFDataset(wikiApi, dbpediaApi, nifFile, getDatasetName(), Lang.TTL);
    }
}
