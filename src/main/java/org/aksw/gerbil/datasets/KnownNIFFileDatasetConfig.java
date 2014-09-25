package org.aksw.gerbil.datasets;

import java.io.IOException;

import it.acubelab.batframework.problems.TopicDataset;

import org.aksw.gerbil.GerbilConfiguration;
import org.aksw.gerbil.bat.datasets.FileBasedNIFDataset;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.apache.jena.riot.Lang;

public class KnownNIFFileDatasetConfig extends AbstractDatasetConfiguration {

    private static final String NIF_DATASET_FILE_PROPERTY_NAME = "org.aksw.gerbil.datasets.KnownNIFFileDatasetConfig.";

    public static enum NIFDatasets {
        KORE50("KORE50"),
        N3_NEWS_100("N³-News-100"),
        N3_REUTERS_128("N³-Reuters-128"),
        N3_RSS_500("N³-RSS-500"),
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

    public KnownNIFFileDatasetConfig(NIFDatasets dataset) {
        super(dataset.getDatasetName(), true, ExperimentType.Sa2W);
        this.dataset = dataset;
    }

    @Override
    protected TopicDataset loadDataset() throws Exception {
        String propertyKey = NIF_DATASET_FILE_PROPERTY_NAME + dataset.name();
        String nifFile = GerbilConfiguration.getInstance().getString(propertyKey);
        if (nifFile == null) {
            throw new IOException("Couldn't load needed Property \"" + propertyKey + "\".");
        }
        return new FileBasedNIFDataset(nifFile, getDatasetName(), Lang.TTL);
    }
}
