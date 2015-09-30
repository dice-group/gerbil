/**
 * This file is part of General Entity Annotator Benchmark.
 *
 * General Entity Annotator Benchmark is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * General Entity Annotator Benchmark is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with General Entity Annotator Benchmark.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.aksw.gerbil.datasets;

import it.acubelab.batframework.problems.TopicDataset;
import it.acubelab.batframework.utils.WikipediaApiInterface;

import java.io.IOException;

import org.aksw.gerbil.bat.datasets.FileBasedNIFDataset;
import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.apache.jena.riot.Lang;

public class KnownNIFFileDatasetConfig extends AbstractDatasetConfiguration {

    private static final String NIF_DATASET_FILE_PROPERTY_NAME = "org.aksw.gerbil.datasets.KnownNIFFileDatasetConfig.";

    public static enum NIFDatasets {
        KORE50("KORE50"),
        // N3_NEWS_100("N3-News-100"), Removed since this is a german dataset
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

    public KnownNIFFileDatasetConfig(WikipediaApiInterface wikiApi, NIFDatasets dataset) {
        super(dataset.getDatasetName(), true, ExperimentType.Sa2KB);
        this.dataset = dataset;
        this.wikiApi = wikiApi;
    }

    @Override
    protected TopicDataset loadDataset() throws Exception {
        String propertyKey = NIF_DATASET_FILE_PROPERTY_NAME + dataset.name();
        String nifFile = GerbilConfiguration.getInstance().getString(propertyKey);
        if (nifFile == null) {
            throw new IOException("Couldn't load needed Property \"" + propertyKey + "\".");
        }
        FileBasedNIFDataset dataset = new FileBasedNIFDataset(wikiApi, nifFile, getName(), Lang.TTL);
        dataset.init();
        return dataset;
    }
}
