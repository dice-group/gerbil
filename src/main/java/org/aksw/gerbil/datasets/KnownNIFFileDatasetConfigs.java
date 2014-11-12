package org.aksw.gerbil.datasets;

import it.acubelab.batframework.systemPlugins.DBPediaApi;
import it.acubelab.batframework.utils.WikipediaApiInterface;

import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.web.config.AdapterList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KnownNIFFileDatasetConfigs extends AdapterList<DatasetConfiguration> {

    private static final Logger LOGGER = LoggerFactory.getLogger(KnownNIFFileDatasetConfigs.class);

    private static final String NIF_DATASET_FILE_PROPERTY_NAME = "org.aksw.gerbil.datasets.KnownNIFFileDatasetConfig.";

    public static KnownNIFFileDatasetConfigs create(WikipediaApiInterface wikiApi, DBPediaApi dbpediaApi) {
        NIFDatasets datasets[] = NIFDatasets.values();
        List<DatasetConfiguration> configurations = new ArrayList<DatasetConfiguration>(datasets.length);
        DatasetConfiguration config;
        for (int i = 0; i < datasets.length; i++) {
            config = getDatasetConfig(wikiApi, dbpediaApi, datasets[i]);
            if (config != null) {
                configurations.add(config);
            }
        }
        return new KnownNIFFileDatasetConfigs(configurations);
    }

    public static DatasetConfiguration getDatasetConfig(WikipediaApiInterface wikiApi, DBPediaApi dbpediaApi,
            NIFDatasets dataset) {
        String propertyKey = NIF_DATASET_FILE_PROPERTY_NAME + dataset.name();
        String nifFile = GerbilConfiguration.getInstance().getString(propertyKey);
        if (nifFile == null) {
            LOGGER.error("Couldn't load needed Property \"" + propertyKey + "\". The dataset \"" + dataset.name()
                    + "\" won't be available.");
            return null;
        } else {
            return new NIFFileDatasetConfig(wikiApi, dataset.getDatasetName(), nifFile, true, ExperimentType.Sa2W);
        }
    }

    protected KnownNIFFileDatasetConfigs(List<DatasetConfiguration> configurations) {
        super(configurations);
    }

    public static enum NIFDatasets {
        KORE50("KORE50"), N3_NEWS_100("N3-News-100"), N3_REUTERS_128("N3-Reuters-128"), N3_RSS_500("N3-RSS-500"), DBPEDIA_SPOTLIGHT(
                "DBpediaSpotlight");

        private String name;

        private NIFDatasets(String name) {
            this.name = name;
        }

        public String getDatasetName() {
            return name;
        }
    }
}
